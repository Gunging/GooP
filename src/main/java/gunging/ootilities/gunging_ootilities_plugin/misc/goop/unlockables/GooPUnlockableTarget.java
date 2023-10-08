package gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Something that can participate in the GooP Unlockables system
 */
public interface GooPUnlockableTarget {

    /**
     * @return The UUID associated to this GooP Unlockable Target
     */
    @NotNull UUID getUniqueId();

    /**
     * @return Display name used to provide feedback if GooP Unlockable commands fail.
     */
    @NotNull String getName();
}
