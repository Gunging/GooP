package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CIInteractingSlot {

    @Override public String toString() { return "Vanilla #" + getSlotNumber(); }

    /**
     * @return Slot number of this inventory where this item is found.
     */
    public int getSlotNumber() { return slotNumber; }
    int slotNumber;

    /**
     * @return Inventory where the slot is found
     */
    public @NotNull Inventory getInventory() { return inventory; }
    @NotNull Inventory inventory;

    /**
     * @return Type of Inventory View this interaction happened in
     */
    public @NotNull InventoryView getView() { return view; }
    @NotNull InventoryView view;

    /**
     * Sets this item in the storage layer, or straight-up inventory.
     * Set null or air to make the default item show up!
     *
     * @param item Item to put in this slot
     */
    public void setItem(@Nullable ItemStack item) { getInventory().setItem(getSlotNumber(), item); }

    /**
     * @return The item currently in this slot
     */
    @Nullable public ItemStack getCurrentItem() { return getInventory().getItem(getSlotNumber()); }

    /**
     * @param item Item attempting to stack
     *
     * @return If this slot particularly is meant to hold this item, like
     *         shields are sent to the offhand when Shift+Click, or coal is
     *         sent to the fuel slot of a furnace.
     */
    public boolean hasPreferenceFor(@NotNull ItemStack item) {

        // So, its armor eh?
        switch (getSlotNumber()) {
            case 103: return OotilityCeption.IsHelmet(item.getType());
            case 102: return OotilityCeption.IsChestplate(item.getType());
            case 101: return OotilityCeption.IsLeggings(item.getType());
            case 100: return OotilityCeption.IsBoots(item.getType());
            case -106: return Material.SHIELD == item.getType();
        }

        // No special preference
        return false;
    }

    /**
     * @param rawSlotNumber Slot number of this inventory where this item is found.
     * @param inventory Inventory where the slot is found
     * @param view Type of Inventory View this interaction happened in
     */
    public CIInteractingSlot(int rawSlotNumber, @NotNull Inventory inventory, @NotNull InventoryView view) {
        this.slotNumber = rawSlotNumber;
        this.inventory = inventory;
        this.view = view;
    }
}
