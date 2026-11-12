package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.containers.interaction.CIInteractingSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CIStackingWay {

    /**
     * @return Place to put this item
     */
    @NotNull public  CIInteractingSlot getLocation() { return location; }
    @NotNull CIInteractingSlot location;

    /**
     * @return Item to put (with correct amount)
     */
    @NotNull public ItemStack getAmountedItem() { return amountedItem; }
    @NotNull ItemStack amountedItem;

    /**
     * A stacking operation to perform
     *
     * @param location Place to put this item
     * @param amountedItem Item to put (with correct amount)
     */
    public CIStackingWay(@NotNull CIInteractingSlot location, @NotNull ItemStack amountedItem) {
        this.location = location;
        this.amountedItem = amountedItem;
    }
}
