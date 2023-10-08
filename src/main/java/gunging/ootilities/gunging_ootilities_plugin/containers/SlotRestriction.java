package gunging.ootilities.gunging_ootilities_plugin.containers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Something that would prevent a STORAGE slot from being interacted with,
 * and even dropping / destroying the items it already had in it!
 */
public abstract class SlotRestriction {

    /**
     * If this method returns false, the slot will decide behaviour in
     * accordance to its RestrictedBehaviour.
     *
     * @param player Player attempting to interact with the slot
     *
     * @return If the slot should allow them to put items into it
     */
    public abstract boolean isUnlockedFor(@NotNull Player player);

    /**
     * @return When saving in the file
     */
    @NotNull public abstract String serialize();

    /**
     * @param item ItemStack to describe
     * @return The item with new lore lines to describe the restriction
     */
    @NotNull public abstract ArrayList<String> appendLore(@NotNull ArrayList<String> item);
}
