package gunging.ootilities.gunging_ootilities_plugin.misc.chunks;


import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Executes an operation on every entry of a chunk map, including null entries
 */
@FunctionalInterface
public interface ChunkForEach<T> {

    /**
     * @param entry Value within the map
     */
    void process(@NotNull Location key, @Nullable T entry);
}
