package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CIHPlaceAll extends CIHPlaceSome {

    /**
     * For the use of {@link InventoryAction#PLACE_ALL}
     */
    public CIHPlaceAll() { super(InventoryAction.PLACE_ALL); }

    @Override public int getAmountToTransfer(@NotNull ItemStack cursor, int currentAmount) { return cursor.getAmount(); }
}