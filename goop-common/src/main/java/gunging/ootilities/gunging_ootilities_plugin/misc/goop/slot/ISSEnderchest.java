package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * With the information to find an item in someone's enderchest.
 */
public class ISSEnderchest extends ItemStackSlot {

    /**
     * Valid for {@link SearchLocation#ENDERCHEST}
     *
     * @param slot     The actual index of the slot. May be NULL for 'any'
     * @param range    The upper range slot of this query. Null will be set to equal 'slot'
     */
    public ISSEnderchest(@Nullable Integer slot, @Nullable Integer range) { super(SearchLocation.ENDERCHEST, slot, range, null); }

    @Override public int getMaximum() { return 27; }

    @Override public @NotNull SearchLocation getShulkerLocation() { return SearchLocation.SHULKER_ENDERCHEST; }

    @Override @NotNull
    public String getPrefix() { return "e"; }

    @Override @NotNull public ArrayList<ISSEnderchest> elaborate() {

        // Value to return
        ArrayList<ISSEnderchest> ret = new ArrayList<>();

        // Include contained range of slots
        for (Integer slot : super.elaboratedRange()) {

            // Add a slot of that number
            ret.add(new ISSEnderchest(slot, null));
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
    @Override public @NotNull ISLEnderchest getItem(@NotNull OfflinePlayer player) {

        // Yes
        return new ISLEnderchest(player, getSlot());
    }
}
