package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ISLPersonalContainer;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Player;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Has the information to find an item in a player's inventory.
 */
public class ISSInventory extends ItemStackSlot {
    /**
     * Valid for {@link SearchLocation#INVENTORY}
     *
     * @param slot     The actual index of the slot. May be NULL for 'any'
     * @param range    The upper range slot of this query. Null will be set to equal 'slot'
     */
    public ISSInventory(@Nullable Integer slot, @Nullable Integer range) { super(SearchLocation.INVENTORY, slot, range, null); }

    @Override public int getMaximum() { return 36; }

    @Override public @NotNull SearchLocation getShulkerLocation() { return SearchLocation.SHULKER_INVENTORY; }

    @Override @NotNull public String getPrefix() { return ""; }

    /**
     * @return Just the range, {@link #getRangeToString()}
     */
    @Override public String toString() { return getRangeToString(); }

    @Override @NotNull public ArrayList<ISSInventory> elaborate() {

        // Value to return
        ArrayList<ISSInventory> ret = new ArrayList<>();

        GOOPCPlayer r = GCL_Player.getInventoryFor(getElaborator());

        // Include contained range of slots
        for (Integer s : super.elaboratedRange()) {
            if (r != null && r.getTemplate().isEdgeSlot(s)) { continue; }

            // Add a slot of that number
            ret.add(new ISSInventory(s, null));
        }

        /*
         * The any keyword makes super.elaborate() to add a slot
         * from 0 to the max range of slots... basically.
         *
         * However, normal inventory 'any' keyword must also include
         * armor slots, offhand, and even the cursor. Here it is:
         */
        if (isAny()) {

            // Include contained range of slots
            for (int s = 100; s <= 103; s++) {
                if (r != null && r.getTemplate().isEdgeSlot(s)) { continue; }

                // Add a slot of that number
                ret.add(new ISSInventory(s, null));
            }
            for (int s = -107; s <= -106; s++) {
                if (r != null && r.getTemplate().isEdgeSlot(s)) { continue; }

                // Add a slot of that number
                ret.add(new ISSInventory(s, null));
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
    @Override public @NotNull ISLInventory getItem(@NotNull OfflinePlayer player) {

        // Yes
        return new ISLInventory(player, getSlot());
    }
}
