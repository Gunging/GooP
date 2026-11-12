package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CIHPlaceAll extends CIHPlaceSome {

    /**
     * For the use of {@link InventoryAction#PLACE_ALL}
     */
    public CIHPlaceAll() { super(InventoryAction.PLACE_ALL); }

    @Override public int getAmountToTransfer(@NotNull ItemStack cursor, int currentAmount) {

        // How many items fit?
        int numberOfItemsThatCanBePlaced = cursor.getMaxStackSize() - currentAmount;

        // How many are we depositing?
        int numberOfItemsToPlace = cursor.getAmount();

        // If we can deposit them, deposit them.
        if (numberOfItemsToPlace <= numberOfItemsThatCanBePlaced && numberOfItemsThatCanBePlaced > 0) {
            return numberOfItemsToPlace;

        // Otherwise, we don't do anything
        } else { return 0; }
    }
}