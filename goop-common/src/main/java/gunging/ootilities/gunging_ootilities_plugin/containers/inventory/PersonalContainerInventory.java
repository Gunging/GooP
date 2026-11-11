package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCPersonal;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * ContainerInventories are multi-layered inventories.
 * <br>
 * Apart from the layers of storage, this keeps owner information.
 */
public class PersonalContainerInventory extends ContainerInventory {

    /**
     * @return The UUID of the entity who owns this container.
     */
    @NotNull public UUID getOwnerUUID() { return ownerUUID; }
    @NotNull final UUID ownerUUID;

    /**
     * @return The OID of the entity who owns this container.
     */
    public long getOwnerOID() { return ownerOID; }
    final long ownerOID;

    /**
     * @return The Personal Container managing this PersonalContainerInventory
     */
    @NotNull public GOOPCPersonal getPersonalContainer() { return personalContainer; }
    @NotNull final GOOPCPersonal personalContainer;

    /**
     * @return Same as {@link #getOwnerOID()} but in String form.
     */
    @Override
    @Nullable public String getInstanceID() { return String.valueOf(getOwnerOID()); }

    /**
     * A multi-layered inventory.
     *
     * @param template The template obeyed by this container inventory
     * @param container The container instance belonging to this.
     * @param reason Reason of the creation of this inventory, alters behaviour.
     * @param owner The player who owns this container.
     */
    public PersonalContainerInventory(@NotNull GOOPCTemplate template, @NotNull GOOPCPersonal container, @NotNull ContainerReasonProcess reason, @NotNull Player owner) { this(template, container, reason, owner.getUniqueId(), owner); }

    /**
     * A multi-layered inventory.
     *
     * @param template The template obeyed by this container inventory
     * @param container The container instance belonging to this.
     * @param reason Reason of the creation of this inventory, alters behaviour.
     * @param owner The player who owns this container.
     */
    public PersonalContainerInventory(@NotNull GOOPCTemplate template, @NotNull GOOPCPersonal container, @NotNull ContainerReasonProcess reason, @NotNull UUID owner, @NotNull Player opener) {
        super(template, reason, opener);
        personalContainer = container;
        this.ownerUUID = owner;
        this.ownerOID = personalContainer.getLinkOwnerToOID().get(ownerUUID);
    }

    @Override public void sanitizeStorage(@NotNull Player receiver) {

        // For all slots
        for (GOOPCSlot cSlot : getTemplate().getSlotsContent().values()) {

            // Skip null and storage slots
            if (cSlot == null || cSlot.isForStorage()) { continue; }

            // Well, its not supposed to do that...
            getPersonalContainer().dropItemFromInventoryAndSave(getOwnerUUID(), cSlot.getSlotNumber(), receiver, receiver.getLocation(), false);
        }
    }

    @Override public void setAndSaveStorageItem(int slot, @Nullable ItemStack item) {
        //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76SVE\u00a77 Setting and Saving at \u00a73#" + slot + "\u00a77: " + OotilityCeption.GetItemName(item) + "\u00a78 (PER)");

        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76SVE\u00a77 Overflow Slot");
            return; }

        // Save
        getPersonalContainer().setAndSaveOwnerItem(getOwnerUUID(), slot, item, false);
    }
}
