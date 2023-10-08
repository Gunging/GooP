package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Information to identify an observed container's item.
 */
public class ISSObservedContainer extends ItemStackSlot {

    /**
     * @return Alias under which we identify the slots of the container.
     */
    @Nullable public String getSlotsAlias() { return slotsAlias; }
    @Nullable String slotsAlias;

    /**
     * Valid for {@link SearchLocation#OBSERVED_CONTAINER}
     *
     * @param slot     The actual index of the slot. May be NULL for 'any'
     * @param range    The upper range slot of this query. Null will be set to equal 'slot'
     * @param slotsAlias The alias of the target slots, for future search.
     */
    public ISSObservedContainer(@Nullable Integer slot, @Nullable Integer range, @Nullable String slotsAlias) {
        super(SearchLocation.OBSERVED_CONTAINER, slot, range, null);
        this.slotsAlias = slotsAlias;
    }

    @Override public int getMaximum() {

        // No observer, no observed container
        if (getElaborator() == null) { return 0; }

        // Get observed?
        GOOPCDeployed observed = GOOPCManager.getObservedContainer(getElaborator().getUniqueId());
        if (observed == null) { return 0; }

        // Yeah
        return observed.getTemplate().getTotalSlotCount();
    }

    @Override public @NotNull SearchLocation getShulkerLocation() { return SearchLocation.SHULKER_OBSERVED_CONTAINER; }

    @Override @NotNull
    public String getPrefix() { return "c"; }

    @Override
    public @NotNull String getRangeToString() {
        if (getSlotsAlias() == null) { return super.getRangeToString(); }
        return "<" + getSlotsAlias() + ">";
    }

    @Override @NotNull public ArrayList<ISSObservedContainer> elaborate() {

        // Value to return
        ArrayList<ISSObservedContainer> ret = new ArrayList<>();
        //SLOT// OotilityCeption.Log("\u00a78Slot \u00a76Observed\u00a77 Elaborating '\u00a73" + getSlotsAlias() + "\u00a77', Slots \u00a7b" + getSlot() + "-" + getRange());

        // No observer, no observed container
        if (getElaborator() == null) { return ret; }

        // Get observed?
        GOOPCDeployed observed = GOOPCManager.getObservedContainer(getElaborator().getUniqueId());
        if (observed == null) { return ret; }

        // Include alias slots
        for (Integer slot : observed.getTemplate().getAliasSlots(getSlotsAlias())) {
            //SLOT//OotilityCeption.Log("\u00a78Slot \u00a76Observed\u00a77 Alias \u00a7b#" + slot);

            // Exclude edge slots
            if (observed.getTemplate().isEdgeSlot(slot)) { continue; }

            // Add a slot of that number
            ret.add(new ISSObservedContainer(slot, null, getSlotsAlias()));
        }

        /*
         * Using an alias disables range, so that's it.
         *
         * Specially since null bounds are passed in the constructor, resulting
         * in ANY ranges making the use of aliases basically target all slots.
         */
        if (getSlotsAlias() != null) { return ret; }

        // Include contained range of slots
        for (Integer slot : super.elaboratedRange()) {
            //SLOT//OotilityCeption.Log("\u00a78Slot \u00a76Observed\u00a77 Range \u00a7b#" + slot);

            // Exclude edge slots
            if (observed.getTemplate().isEdgeSlot(slot)) { continue; }

            // Add a slot of that number
            ret.add(new ISSObservedContainer(slot, null, getSlotsAlias()));
        }

        // That's it
        return ret;
    }

    /**
     * Will get the item at the container this player is observing.
     *
     * @param player Player that is observing any container.
     *
     * @return The item found at this slot ({@link #getSlot()}) of the container.
     */
    @Override public @Nullable ISLObservedContainer getItem(@NotNull OfflinePlayer player) {

        // Get observed?
        GOOPCDeployed observed = GOOPCManager.getObservedContainer(player.getUniqueId());
        if (observed == null) { return null; }

        // Yes
        return new ISLObservedContainer(observed, player.getUniqueId(), getSlot());
    }
}
