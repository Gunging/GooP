package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Allows to find an item inside a shulker box, inside somewhere...
 */
public class ISSShulker extends ItemStackSlot {

    /**
     * Valid for {@link SearchLocation#SHULKER_INVENTORY},
     * {@link SearchLocation#SHULKER_ENDERCHEST},
     * {@link SearchLocation#SHULKER_OBSERVED_CONTAINER},
     * {@link SearchLocation#SHULKER_PERSONAL_CONTAINER}
     *
     * @param location The what kind of inventory is this shulker box found
     * @param slot     The actual index of the slot. May be NULL for 'any'
     * @param range    The upper range slot of this query. Null will be set to equal 'slot'
     * @param parent   If within a shulker box, the slot of thay shulker box. May be NULL
     */
    public ISSShulker(@NotNull SearchLocation location, @Nullable Integer slot, @Nullable Integer range, @NotNull ItemStackSlot parent) { super(location, slot, range, parent); }

    @Override public int getMaximum() { return 27; }

    @Override public @NotNull SearchLocation getShulkerLocation() { return getLocation(); }

    /** @noinspection ConstantConditions*/
    @Override @NotNull public ItemStackSlot getParent() { return super.getParent(); }

    @Override @NotNull
    public String getPrefix() { return getParent().getPrefix() + getParent().getRangeToString() + "."; }

    @Override @NotNull public ArrayList<ISSShulker> elaborate() {

        // Value to return
        ArrayList<ISSShulker> ret = new ArrayList<>();

        // Elaborate parent
        for (ItemStackSlot parentElaborate : getParent().elaborate()) {

            // Include contained range of slots
            for (Integer slot : super.elaboratedRange()) {

                // Add a slot of that number
                ret.add(new ISSShulker(getLocation(), slot, null, parentElaborate));
            }
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
    @Override public @Nullable ISLShulker getItem(@NotNull OfflinePlayer player) {

        // Find the one from the parent
        ItemStackLocation parentLocation = getParent().getItem(player);
        if (parentLocation == null) { return null; }

        // Suppose *that* was a shulker, well, this slot of thay shulker.
        return parentLocation.getShulker(getSlot());
    }
}
