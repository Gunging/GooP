package gunging.ootilities.gunging_ootilities_plugin.misc.chunks;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;


/**
 * Executes an operation on every entry of a chunk map, excluding null entries
 */
@FunctionalInterface
public interface ChunkForEachNotNull<T> {

    /**
     * @param entry Value within the map
     */
    void process(@NotNull Location key, @NotNull T entry);
}