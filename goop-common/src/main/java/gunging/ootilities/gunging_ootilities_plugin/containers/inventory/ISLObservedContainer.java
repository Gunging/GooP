package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.containers.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.NBTFilter;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISLEnderchest;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISLShulker;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISSEnderchest;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * An item found inside a container.
 */
public class ISLObservedContainer extends ItemStackLocation {

    /**
     * @return The deployed container being observed.
     */
    @NotNull public GOOPCDeployed getDeployed() { return deployed; }
    @NotNull final GOOPCDeployed deployed;

    /**
     * @return The player who has this opened.
     */
    @NotNull public UUID getObserver() { return observer; }
    @NotNull final UUID observer;

    /**
     * The only search locations for which this constructor is suitable are: <br>
     *     {@link SearchLocation#OBSERVED_CONTAINER}            <br>
     *
     * @param deployed Observed container template.
     * @param observer Player observing this observed container.
     * @param slotNumber Slot of this inventory where this at.
     */
    public ISLObservedContainer(@NotNull GOOPCDeployed deployed, @NotNull UUID observer, int slotNumber) {
        super(SearchLocation.OBSERVED_CONTAINER, null, slotNumber);
        this.deployed = deployed;
        this.observer = observer;
    }

    @Override public @NotNull ISLShulker getShulker(int shulkerNumber) { return new ISLShulker(SearchLocation.SHULKER_OBSERVED_CONTAINER, this, shulkerNumber); }

    @Override public @Nullable ItemStack getItem() {

        // Get looking at
        return getDeployed().getObservedItem(getObserver(), getSlot());
    }

    @Override public void setItem(@Nullable ItemStack item) {
        ContainerInventory inventory = getDeployed().getObservedBy(getObserver());
        if (inventory == null) { return; }

        // Is it storage slot?
        if (getDeployed().getTemplate().isStorageSlot(getSlot())) {

            // Treating as personal container
            if (getDeployed() instanceof GOOPCPersonal) {

                // Reference
                GOOPCPersonal personal = (GOOPCPersonal) getDeployed();

                // Get Owner
                UUID personalOwner = personal.getLookingAt(getObserver());
                if (personalOwner == null) { return; }

                // Set item
                personal.setAndSaveOwnerItem(personalOwner, getSlot(), item, true);

                // Maybe update?
                if (Gunging_Ootilities_Plugin.foundMMOItems && getDeployed().getTemplate().getEquipmentSlots().contains(getSlot())) {

                    // Of the owner of the container yeah
                    GooPMMOItems.UpdatePlayerEquipment(personalOwner); }

            // Treating as physical container
            } else if (getDeployed() instanceof GOOPCPhysical) {

                // Reference
                GOOPCPhysical physical = (GOOPCPhysical) getDeployed();

                // Get Location
                Location physicalLocation = physical.getPlayerLookingAt().get(getObserver());
                if (physicalLocation == null) { return; }

                // Save item
                physical.setAndSaveOwnerItem(physicalLocation, getSlot(), item, true);

            // Treating as station container
            } else if (getDeployed() instanceof GOOPCStation) {

                // Just update storage layer, no need to save in the files
                inventory.setStorageItem(getSlot(), item);

                // Send inventory update
                inventory.updateSlot(getSlot());
            }

        // Just for display, no fancy operations needed
        } else {

            // Set in edited layer
            inventory.setEditedItem(getSlot(), item);

            // Send inventory update
            inventory.updateSlot(getSlot());
        }
    }

    /**
     * Used to generate dummy ISLEnderchest to run semi-static
     * methods, rather than being functional. Please do not use
     * any other methods with this constructor:
     *
     * @see #getAllMatching(Player, NBTFilter, RefSimulator, RefSimulator)
     * @see #getAllShulker(Player)
     */
    @SuppressWarnings("ConstantConditions")
    public ISLObservedContainer() { super(SearchLocation.OBSERVED_CONTAINER, null); deployed = null; observer = null; }
    @Override @NotNull public ArrayList<ISLObservedContainer> getAllMatching(@NotNull Player player, @NotNull NBTFilter filter, @Nullable RefSimulator<Integer> count, @Nullable RefSimulator<String> logger) {

        // Count
        int totalAmount = 0;

        // Locations
        ArrayList<ISLObservedContainer> ret = new ArrayList<>();

        // Get observed
        GOOPCDeployed observed = GOOPCManager.getObservedContainer(player.getUniqueId());
        if (observed == null) { if (count != null) { count.setValue(0); } return ret; }

        // Get Size of Container
        int size = observed.getTemplate().getTotalSlotCount();

        // Inspect whole inventory
        for (int slot = 0; slot < size; slot++) {

            // If for storage
            if (observed.getTemplate().isStorageSlot(slot)) {

                // Temporary Item Stack for evaluation
                ISSObservedContainer itemSlot = new ISSObservedContainer(slot, null, null);
                itemSlot.setElaborator(player);
                ISLObservedContainer location = itemSlot.getItem(player);
                if (location == null) { continue; }

                // Get item from location
                ItemStack item = location.getItem();

                // If the item matches
                if (!OotilityCeption.MatchesItemNBTtestString(item, filter, logger)) { continue; }

                // Include amount
                if (OotilityCeption.IsAirNullAllowed(item)) { totalAmount++; } else { totalAmount += item.getAmount(); }

                // Include location
                ret.add(location);
            }
        }

        // Store
        if (count != null) { count.setValue(totalAmount); }

        // Yeah
        return ret;
    }
    @Override @NotNull public ArrayList<ISLObservedContainer> getAllShulker(@NotNull Player player) {

        // Locations
        ArrayList<ISLObservedContainer> ret = new ArrayList<>();

        // Get observed
        GOOPCDeployed observed = GOOPCManager.getObservedContainer(player.getUniqueId());
        if (observed == null) { return ret; }

        // Get Size of Container
        int size = observed.getTemplate().getTotalSlotCount();

        // Inspect whole inventory
        for (int slot = 0; slot < size; slot++) {

            // If for storage
            if (observed.getTemplate().isStorageSlot(slot)) {

                // Temporary Item Stack for evaluation
                ISSObservedContainer itemSlot = new ISSObservedContainer(slot, null, null);
                itemSlot.setElaborator(player);
                ISLObservedContainer location = itemSlot.getItem(player);
                if (location == null) { continue; }

                // Get item from location
                ItemStack item = location.getItem();
                if (item == null) { continue; }

                // If the item matches
                if (!OotilityCeption.IsShulkerBox(item.getType())) { continue; }

                // Include location
                ret.add(location);
            }
        }

        // Yeah
        return ret;
    }
}
