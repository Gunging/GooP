package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Player;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISLInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CIContainerInteracting extends CIInteractingSlot {

    @Override public String toString() {
        return (getTemplate().isPlayer() ? "Player #" : "Container #") + getContainerSlot().getSlotNumber() + " (" + getTemplate().getInternalName() + ")";
    }

    /**
     * @return Container Slot being interacted with
     */
    public @NotNull GOOPCSlot getContainerSlot() { return containerSlot; }
    @NotNull GOOPCSlot containerSlot;

    /**
     * @return Observed container inventory, if applicable
     */
    public @Nullable ContainerInventory getObserved() { return getTemplate().isPlayer() ? null : observed; }
    @Nullable ContainerInventory observed;

    /**
     * @return Template the slot belongs to.
     */
    public @NotNull GOOPCTemplate getTemplate() { return template; }
    @NotNull GOOPCTemplate template;

    @Override
    public void setItem(@Nullable ItemStack item) {
        //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cSET\u00a77 Storing " + OotilityCeption.GetItemName(item, true));

        /*
         * Put the item where it belongs, and update cursor
         */
        if (getTemplate().isPlayer()) {
            //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cSET\u00a77 As RPG");

            if (!(getInventory().getHolder() instanceof Player)) {

                Gunging_Ootilities_Plugin.theOots.CLog("\u00a7cInvalid Inventory Set Holder\u00a7e CIInteractingContainerSlot");
                return;
            }

            // Identify
            Player player = (Player) getInventory().getHolder();
            ItemStack displayingItem = item;

            // Defaulted
            GOOPCPlayer r = GCL_Player.getInventoryFor(player);
            if (r != null && OotilityCeption.IsAirNullAllowed(displayingItem)) {
                GOOPCSlot c = r.getTemplate().getSlotAt(getContainerSlot().getSlotNumber());
                if (c != null) {
                    displayingItem = c.getContent();
                    //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cSET\u00a77 Defaulted to " + OotilityCeption.GetItemName(displayingItem, true));
                } }

            //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cSET\u00a77 Storing " + OotilityCeption.GetItemName(displayingItem, true));

            // Set item in inventory
            OotilityCeption.setItemFromPlayerInventory(player, getContainerSlot().getSlotNumber(), displayingItem);

        } else if (observed != null) {
            //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cSET\u00a77 At observed");

            // Set to air, the item just got dropped
            observed.setAndSaveStorageItem(getContainerSlot().getSlotNumber(), item);

        // Wut
        } else {

            Gunging_Ootilities_Plugin.theOots.CLog("\u00a7cInvalid Containers Set Interaction\u00a7e CIInteractingContainerSlot");
        }
    }

    @Override
    public boolean hasPreferenceFor(@NotNull ItemStack item) {

        // If the slot has no masks ~ I sleep
        if (!getContainerSlot().hasKindMasks() && !getContainerSlot().hasTypeMask()) { return false; }

        // Must be able to store it ofc
        return getContainerSlot().canStore(item);
    }

    @Override
    public @Nullable ItemStack getCurrentItem() {
        //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cGET\u00a77 Getting Current Item at #" + getSlotNumber());

        /*
         * Put the item where it belongs, and update cursor
         */
        if (getTemplate().isPlayer()) {
            //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cGET\u00a77 As RPG");

            if (!(getInventory().getHolder() instanceof Player)) {

                Gunging_Ootilities_Plugin.theOots.CLog("\u00a7cInvalid Inventory Get Holder\u00a7e CIInteractingContainerSlot");
                return null;
            }

            // Identify
            Player player = (Player) getInventory().getHolder();

            // Set item in inventory
            return OotilityCeption.getItemFromPlayerInventory(player, getContainerSlot().getSlotNumber());

        } else if (observed != null) {
            //CLI// OotilityCeption.Log("\u00a78CIICS \u00a7cGET\u00a77 At observed");

            // Set to air, the item just got dropped
            return observed.getLayerStorage().get(getContainerSlot().getSlotNumber());

        // Wut
        } else {

            Gunging_Ootilities_Plugin.theOots.CLog("\u00a7cInvalid Containers Get Interaction\u00a7e CIInteractingContainerSlot");
            return null;
        }
    }

    /**
     * @param rawSlotNumber Slot number of this inventory where this item is found.
     * @param inventory     Inventory where the slot is found
     * @param view          Type of Inventory View this interaction happened in.
     * @param containerSlot Container Slot being interacted with
     * @param template      Template the slot belongs to.
     */
    public CIContainerInteracting(int rawSlotNumber, @NotNull Inventory inventory, @NotNull InventoryView view, @NotNull GOOPCTemplate template, @NotNull GOOPCSlot containerSlot, @Nullable ContainerInventory observed) {
        super(rawSlotNumber, inventory, view);
        this.observed = observed;
        this.containerSlot = containerSlot;
        this.template = template;
    }
}
