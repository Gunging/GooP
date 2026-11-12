package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Saves GooP's YAML files off the main thread without corrupting them.
 * <p>
 * GooP 1.x queued files in an unsynchronized list and called {@code YamlConfiguration#save}
 * from an async task while the main thread kept editing the same configuration, and swallowed
 * every exception. That raced (IndexOutOfBoundsException in the queue) and could write a
 * half-edited or truncated file without any message.
 * <p>
 * Now: repeated saves of one file within half a second collapse into one; the YAML text is
 * captured on the main thread, where the configuration is edited; a single writer thread
 * writes it to a temporary file and moves it over the old one, so files are written in order
 * and a crash mid-write leaves the previous version intact. Failures are logged.
 */
public final class YamlSaveQueue {

    @NotNull private final Plugin plugin;
    @NotNull private final Map<String, FileConfigPair> pending = new LinkedHashMap<>();
    @NotNull private final ExecutorService writer;
    private boolean flushScheduled;
    private boolean closed;

    public YamlSaveQueue(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.writer = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "GooP-YAML-writer");
            t.setDaemon(true);
            return t;
        });
    }

    /** Queue this file to be saved shortly. Later queues of the same file replace earlier ones. */
    public synchronized void queue(@NotNull FileConfigPair pair) {
        pending.put(pair.getFile().getPath(), pair);
        // While disabling, tasks cannot be scheduled; close() writes whatever is pending
        if (flushScheduled || closed || !plugin.isEnabled()) { return; }
        flushScheduled = true;
        Bukkit.getScheduler().runTaskLater(plugin, this::flushToWriter, 10L);
    }

    /** Main thread: snapshot every pending file and hand the text to the writer thread */
    private void flushToWriter() {
        Map<File, String> snapshots = snapshotPending();
        if (snapshots.isEmpty()) { return; }
        try {
            writer.execute(() -> writeAll(snapshots));
        } catch (java.util.concurrent.RejectedExecutionException shuttingDown) {
            writeAll(snapshots);
        }
    }

    /**
     * Saves everything still queued right now, on this thread, and stops the writer.
     * Call from {@code onDisable}.
     */
    public void close() {
        synchronized (this) { closed = true; }
        writer.shutdown();
        try {
            if (!writer.awaitTermination(30, TimeUnit.SECONDS)) {
                plugin.getLogger().warning("Timed out waiting for queued file saves to finish.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        writeAll(snapshotPending());
    }

    @NotNull private Map<File, String> snapshotPending() {
        Map<File, String> snapshots = new LinkedHashMap<>();
        synchronized (this) {
            flushScheduled = false;
            for (FileConfigPair pair : pending.values()) {
                snapshots.put(pair.getFile(), pair.getStorage().saveToString());
            }
            pending.clear();
        }
        return snapshots;
    }

    private void writeAll(@NotNull Map<File, String> snapshots) {
        for (Map.Entry<File, String> e : snapshots.entrySet()) {
            try {
                writeAtomically(e.getKey(), e.getValue());
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save " + e.getKey().getPath(), ex);
            }
        }
    }

    /** Writes next to the target first, then replaces it, so a crash never leaves a partial file */
    static void writeAtomically(@NotNull File file, @NotNull String content) throws IOException {
        Path target = file.toPath();
        Path parent = target.toAbsolutePath().getParent();
        if (parent != null) { Files.createDirectories(parent); }

        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        Files.write(tmp, content.getBytes(StandardCharsets.UTF_8));
        try {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException notAtomic) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
