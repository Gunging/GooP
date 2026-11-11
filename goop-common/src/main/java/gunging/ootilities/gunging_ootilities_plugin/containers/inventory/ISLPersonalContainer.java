package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCPersonal;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Personal;
import gunging.ootilities.gunging_ootilities_plugin.misc.NBTFilter;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISLShulker;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * An item found inside a personal container.
 */
public class ISLPersonalContainer extends ItemStackLocation {

    /**
     * @return The target personal container.
     */
    @NotNull
    public GOOPCPersonal getPersonal() { return personal; }
    @NotNull final GOOPCPersonal personal;

    /**
     * @return The owner of the contents of the personal container.
     */
    @NotNull public UUID getOwner() { return owner; }
    @NotNull final UUID owner;

    /**
     * The only search locations for which this constructor is suitable are: <br>
     *     {@link SearchLocation#PERSONAL_CONTAINER}            <br>
     *
     * @param personal The container this slot is looking into.
     * @param owner The owner of this personal container.
     * @param slotNumber Slot of this inventory where this at.
     */
    public ISLPersonalContainer(@NotNull GOOPCPersonal personal, @NotNull UUID owner, int slotNumber) {
        super(SearchLocation.PERSONAL_CONTAINER, null, slotNumber);
        this.personal = personal;
        this.owner = owner;
    }

    @Override public @NotNull ISLShulker getShulker(int shulkerNumber) { return new ISLShulker(SearchLocation.SHULKER_PERSONAL_CONTAINER, this, shulkerNumber); }

    @Override public @Nullable ItemStack getItem() { return getPersonal().getObservedItemByOwner(getOwner(), getSlot()); }

    @Override public void setItem(@Nullable ItemStack item) {

        // Storage or Edited?
        if (getPersonal().getTemplate().isStorageSlot(getSlot())) {

            // Set item in storage. Save.
            getPersonal().setAndSaveOwnerItem(getOwner(), getSlot(), item, true);

            /*
             * As of currently, only storage slots can be for equipment.
             *
             * Therefore, this check only makes sense here, could it even
             * be practical for display slots to be equipment slots?
             */
            if (Gunging_Ootilities_Plugin.foundMMOItems && getPersonal().getTemplate().getEquipmentSlots().contains(getSlot())) {

                // Of the owner of the container yeah
                GooPMMOItems.UpdatePlayerEquipment(getOwner()); }

        // Not for storage ~ Use Edited Layer
        } else {

            // Set item in edited layer.
            getPersonal().setInEdited(getOwner(), getSlot(), item, true);
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
    public ISLPersonalContainer() { super(SearchLocation.PERSONAL_CONTAINER, null); personal = null; owner = null; }
    @Override @NotNull public ArrayList<ISLPersonalContainer> getAllMatching(@NotNull Player player, @NotNull NBTFilter filter, @Nullable RefSimulator<Integer> count, @Nullable RefSimulator<String> logger) {

        // Count
        int totalAmount = 0;

        // Locations
        ArrayList<ISLPersonalContainer> ret = new ArrayList<>();

        // For every container loaded wth
        for (GOOPCPersonal personal : GCL_Personal.getLoaded()) {

            // Get Size of Container
            int size = personal.getTemplate().getTotalSlotCount();

            // Inspect whole inventory
            for (int slot = 0; slot < size; slot++) {

                // If for storage
                if (personal.getTemplate().isStorageSlot(slot)) {

                    // Temporary Item Stack for evaluation
                    ISSPersonalContainer itemSlot = new ISSPersonalContainer(slot, null, personal, null);
                    itemSlot.setElaborator(player);
                    ISLPersonalContainer location = itemSlot.getItem(player);

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
        }

        // Store
        if (count != null) { count.setValue(totalAmount); }

        // Yeah
        return ret;
    }
    @Override @NotNull public ArrayList<ISLPersonalContainer> getAllShulker(@NotNull Player player) {

        // Locations
        ArrayList<ISLPersonalContainer> ret = new ArrayList<>();

        // For every container loaded wth
        for (GOOPCPersonal personal : GCL_Personal.getLoaded()) {

            // Get Size of Container
            int size = personal.getTemplate().getTotalSlotCount();

            // Inspect whole inventory
            for (int slot = 0; slot < size; slot++) {

                // If for storage
                if (personal.getTemplate().isStorageSlot(slot)) {

                    // Temporary Item Stack for evaluation
                    ISSPersonalContainer itemSlot = new ISSPersonalContainer(slot, null, personal, null);
                    itemSlot.setElaborator(player);
                    ISLPersonalContainer location = itemSlot.getItem(player);

                    // Get item from location
                    ItemStack item = location.getItem();
                    if (item == null) { continue; }

                    // If the item matches
                    if (!OotilityCeption.IsShulkerBox(item.getType())) { continue; }

                    // Include location
                    ret.add(location);
                }
            }
        }

        // Yeah
        return ret;
    }
}
