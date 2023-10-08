package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ContainerInventories are multi-layered inventories.
 * <br>
 * Apart from the layers of storage, this keeps location information.
 */
public class PhysicalContainerInventory extends ContainerInventory {

    /**
     * @return The protection information of this container
     */
    @NotNull public GPCContent getContent() { return content; }
    @NotNull final GPCContent content;

    /**
     * @return The LID of the location that bases this container.
     */
    public long getLocationLID() { return locationLID; }
    final long locationLID;

    /**
     * @return The LID of the location that bases this container.
     */
    @NotNull public Location getLocation() { return location; }
    @NotNull final Location location;

    /**
     * @return The Physical Container managing this PhysicalContainerInventory
     */
    @NotNull public GOOPCPhysical getPhysicalContainer() { return physicalContainer; }
    @NotNull final GOOPCPhysical physicalContainer;

    /**
     * @return Same as {@link #getLocationLID()} but in String form.
     */
    @Override @Nullable public String getInstanceID() { return String.valueOf(getLocationLID()); }

    /**
     * A multi-layered inventory.
     *
     * @param template   The template obeyed by this container inventory
     * @param container  The container instance belonging to this.
     * @param reason     Reason of the creation of this inventory, alters behaviour.
     * @param content The protection information
     * @param opener     Player opening this inventory
     */
    public PhysicalContainerInventory(@NotNull GOOPCTemplate template, @NotNull GOOPCPhysical container, @NotNull ContainerReasonProcess reason, @NotNull GPCContent content, @NotNull Player opener) {
        super(template, reason, opener);
        physicalContainer = container;
        this.content = content;
        this.location = content.getLocation();
        this.locationLID = content.getLID();
    }

    @Override public void sanitizeStorage(@NotNull Player receiver) {

        // For all slots
        for (GOOPCSlot cSlot : getTemplate().getSlotsContent().values()) {

            // Skip null and storage slots
            if (cSlot == null || cSlot.isForStorage()) { continue; }

            // Well, its not supposed to do that...
            getPhysicalContainer().dropItemFromInventoryAndSave(getContent().getLocation(), cSlot.getSlotNumber(), receiver, receiver.getLocation(), false);
        }
    }

    @Override public void setAndSaveStorageItem(int slot, @Nullable ItemStack item) {
        //CLI// OotilityCeption.Log("\u00a78CONINVEN \u00a76SVE\u00a77 Setting and Saving at \u00a73#" + slot + "\u00a77: " + OotilityCeption.GetItemName(item) + "\u00a78 (PHY)");
        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76SVE\u00a77 Overflow Slot");
            return; }

        // Save in physical
        getPhysicalContainer().setAndSaveOwnerItem(getLocation(), slot, item, false);
    }
}
