package gunging.ootilities.gunging_ootilities_plugin.misc.chunks;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An entry of a Chunk Map, with information on its location and whatever else.
 *
 * @param <T> Whatever entry type
 */
public class ChunkMapEntry<T> {

    @Nullable T value;
    @Nullable public T getValue() { return value; }
    public void setValue(@Nullable T value) { this.value = value; }

    @NotNull final Location loc;
    @NotNull public Location getLoc() { return loc; }

    @NotNull final CKS_Minor minor;
    @NotNull public CKS_Minor getMinor() { return minor; }

    @NotNull final CKS_Major major;
    @NotNull public CKS_Major getMajor() { return major; }

    public ChunkMapEntry(@Nullable T value, @NotNull Location loc, @NotNull CKS_Minor minor, @NotNull CKS_Major major) {
        this.value = value;
        this.loc = loc;
        this.minor = minor;
        this.major = major;
    }
}
