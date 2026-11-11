package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCPersonal;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Information to identify a personal container.
 */
public class ISSPersonalContainer extends ItemStackSlot {

    /**
     * @return Alias under which we identify the slots of the container.
     */
    @Nullable public String getSlotsAlias() { return slotsAlias; }
    @Nullable String slotsAlias;

    /**
     * @return The target personal container.
     */
    @NotNull public GOOPCPersonal getPersonal() { return personal; }
    @NotNull GOOPCPersonal personal;

    /**
     * Valid for {@link SearchLocation#PERSONAL_CONTAINER}
     *
     * @param slot     The actual index of the slot. May be NULL for 'any'
     * @param range    The upper range slot of this query. Null will be set to equal 'slot'
     * @param personal The target Personal Container
     * @param slotsAlias alias of the slots.
     */
    public ISSPersonalContainer(@Nullable Integer slot, @Nullable Integer range, @NotNull GOOPCPersonal personal, @Nullable String slotsAlias) {
        super(SearchLocation.PERSONAL_CONTAINER, slot, range, null);
        this.personal = personal;
        this.slotsAlias = slotsAlias;
    }

    @Override public int getMaximum() {

        // Yeah
        return getPersonal().getTemplate().getTotalSlotCount();
    }

    @Override public @NotNull SearchLocation getShulkerLocation() { return SearchLocation.SHULKER_PERSONAL_CONTAINER; }

    @Override @NotNull
    public String getPrefix() { return "|" + getPersonal().getTemplate().getInternalName() + "|"; }

    @Override
    public @NotNull String getRangeToString() {
        if (getSlotsAlias() == null) { return super.getRangeToString(); }
        return "<" + getSlotsAlias() + ">";
    }

    @Override @NotNull public ArrayList<ISSPersonalContainer> elaborate() {

        // Value to return
        ArrayList<ISSPersonalContainer> ret = new ArrayList<>();
        //SLOT//OotilityCeption.Log("\u00a78Slot \u00a7ePersonal\u00a77 Elaborating '\u00a73" + getSlotsAlias() + "\u00a77', Slots \u00a7b" + getSlot() + "-" + getRange());

        // Include alias slots
        for (Integer slot : getPersonal().getTemplate().getAliasSlots(getSlotsAlias())) {
            //SLOT//OotilityCeption.Log("\u00a78Slot \u00a7ePersonal\u00a77 Alias \u00a7b#" + slot);


            // Exclude edge slots
            if (getPersonal().getTemplate().isEdgeSlot(slot)) { continue; }

            // Add a slot of that number
            ret.add(new ISSPersonalContainer(slot, null, getPersonal(), getSlotsAlias()));
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
            //SLOT//OotilityCeption.Log("\u00a78Slot \u00a7ePersonal\u00a77 Range \u00a7b#" + slot);

            // Exclude edge slots
            if (getPersonal().getTemplate().isEdgeSlot(slot)) { continue; }

            // Add a slot of that number
            ret.add(new ISSPersonalContainer(slot, null, getPersonal(), getSlotsAlias()));
        }

        // That's it
        return ret;
    }

    /**
     * Will get the item at this player's personal container of this kind.
     *
     * @param player Player that owns the personal container where
     *               this item will be found.
     *
     * @return The item found at this slot ({@link #getSlot()}) of the container.
     */
    @Override public @NotNull ISLPersonalContainer getItem(@NotNull OfflinePlayer player) {

        // Yes
        return new ISLPersonalContainer(getPersonal(), player.getUniqueId(), getSlot());
    }
}
