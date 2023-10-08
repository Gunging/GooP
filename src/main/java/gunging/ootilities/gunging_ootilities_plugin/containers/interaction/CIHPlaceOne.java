package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CIHPlaceOne extends CIHPlaceSome {

    /**
     * For the use of {@link InventoryAction#PLACE_ONE}
     */
    public CIHPlaceOne() { super(InventoryAction.PLACE_ONE); }

    @Override public int getAmountToTransfer(@NotNull ItemStack cursor, int currentAmount) { return 1; }
}
