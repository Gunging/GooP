package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CIHPlaceOne extends CIHPlaceSome {

    /**
     * For the use of {@link InventoryAction#PLACE_ONE}
     */
    public CIHPlaceOne() { super(InventoryAction.PLACE_ONE); }

    @Override public int getAmountToTransfer(@NotNull ItemStack cursor, int currentAmount) {

        // How many items fit?
        int numberOfItemsThatCanBePlaced = cursor.getMaxStackSize() - currentAmount;

        // How many are we depositing?
        int numberOfItemsToPlace = 1;

        // If we can deposit them, deposit them. Otherwise, we don't do anything
        if (numberOfItemsToPlace <= numberOfItemsThatCanBePlaced) { return numberOfItemsToPlace; } else { return 0; }
    }
}
