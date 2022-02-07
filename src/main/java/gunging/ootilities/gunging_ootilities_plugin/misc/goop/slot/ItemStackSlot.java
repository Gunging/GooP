package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Has the information to find an item, given a player.
 */
public abstract class ItemStackSlot {

    //region Constructor
    /**
     * @return The slot number this ItemStackSlot is targeting
     */
    public int getSlot() { return slot; }
    int slot;

    /**
     * If the slot is not a range, this will just equal {@link #getSlot()}
     *
     * @return Is this a compound slot with a range? If so,
     *         what is the upper range it targets?
     */
    public int getRange() { return range; }
    int range;

    /**
     * @return If this slot is all slots.
     */
    public boolean isAny() { return any; }
    boolean any;

    /**
     * @return The kind of inventory this slot is located in.
     */
    @NotNull public SearchLocation getLocation() { return location; }
    @NotNull SearchLocation location;

    /**
     * @return Suppose this slot is within a shulker, well,
     *         where is this shulker located?
     */
    @Nullable public ItemStackSlot getParent() { return parent; }
    @Nullable ItemStackSlot parent;

    /**
     * @return The player who is elaborating this ItemStackSlots,
     *         useful for some implementations.
     */
    @Nullable public OfflinePlayer getElaborator() { return elaborator; }
    @Nullable OfflinePlayer elaborator;
    /**
     * @param elaborator The player who will be used to elaborate.
     */
    public void setElaborator(@Nullable OfflinePlayer elaborator) { this.elaborator = elaborator; }

    /**
     * The information on where to find an item, without actually going
     * to get such item yet. Will provide a player later to actually find.
     *
     * @param location The 'general' kind of inventory the slot is in.
     * @param slot The actual index of the slot. May be NULL for 'any'
     * @param range The upper range slot of this query. Null will be set to equal 'slot'
     * @param parent If within a shulker box, the slot of thay shulker box. May be NULL
     */
    public ItemStackSlot(@NotNull SearchLocation location, @Nullable Integer slot, @Nullable Integer range, @Nullable ItemStackSlot parent) {
        this.location = location;
        this.parent = parent;

        // Identify 'any' slot query
        if (slot == null) { slot = -1; range = -1; any = true; } else if (range == null) { range = slot; }
        if (slot > range) { int lowest = range; range = slot; slot = lowest; }

        // Identify slot ranges
        this.slot = slot;
        this.range = range;
    }
    //endregion

    //region Informational
    /**
     * @return Is this a player inventory location?
     */
    public boolean isInInventory() { return getLocation() == SearchLocation.INVENTORY || getLocation() == SearchLocation.SHULKER_INVENTORY; }
    /**
     * @return Is this an enderchest inventory?
     */
    public boolean isInEnderchest() { return getLocation() == SearchLocation.ENDERCHEST || getLocation() == SearchLocation.SHULKER_ENDERCHEST; }
    /**
     * @return Is this a personal container inventory?
     */
    public boolean isInPersonalContainer() { return getLocation() == SearchLocation.PERSONAL_CONTAINER || getLocation() == SearchLocation.SHULKER_PERSONAL_CONTAINER; }
    /**
     * @return Is this an observed container inventory?
     */
    public boolean isInObservedContainer() { return getLocation() == SearchLocation.OBSERVED_CONTAINER || getLocation() == SearchLocation.SHULKER_OBSERVED_CONTAINER; }
    /**
     * @return Is this a shulker box inventory?
     */
    public boolean isInShulker() { return getLocation() == SearchLocation.SHULKER_PERSONAL_CONTAINER || getLocation() == SearchLocation.SHULKER_OBSERVED_CONTAINER || getLocation() == SearchLocation.SHULKER_INVENTORY || getLocation() == SearchLocation.SHULKER_ENDERCHEST; }
    /**
     * @return If a shulker were to be made here, its location.
     */
    @NotNull public abstract SearchLocation getShulkerLocation();

    /**
     * @param slot Some slot number
     *
     * @return If this slot is included within the scope of this {@link ItemStackSlot}
     */
    public boolean inRange(int slot) { return isAny() || (slot >= getSlot()) && (slot <= getRange()); }
    /**
     * @return The maximum value of slots this search location supports.
     */
    public abstract int getMaximum();

    /**
     * @return The prefix that qualifies this search location.
     */
    @NotNull public abstract String getPrefix();
    /**
     * @return Reverts the range back to string form.
     */
    @NotNull public String getRangeToString() {

        // Any case
        if (isAny()) { return "*"; }

        // Special slots
        switch (getSlot()) {
            case -7: return "mainhand";
            case -106: return "offhand";
            case -107: return "cursor";
        }

        // Single case
        if (getSlot() == getRange()) { return String.valueOf(getSlot()); }

        // Range Case
        return getSlot() + "-" + getRange();
    }
    /**
     * @return The {@link #getPrefix()} plus the {@link #getRangeToString()}
     */
    @Override public String toString() { return getPrefix() + getRangeToString(); }
    //endregion

    /**
     * @return The list of single-slot ItemStackSlots that
     *         are contained within this ranged slot.
     */
    @NotNull public abstract ArrayList<? extends ItemStackSlot> elaborate();
    /**
     * @return The list of integer that are contained by
     *         the range of this ItemStackSlot.
     */
    @NotNull public ArrayList<Integer> elaboratedRange() {

        // Yeah build this list
        ArrayList<Integer> ret = new ArrayList<>();

        // Is it the 'any' slot?
        if (isAny()) {

            // Add one for each slot, from 0 to the maximum.
            for (int i = 0; i < getMaximum(); i++) {
                //SLOT//OotilityCeption.Log("\u00a78Slot \u00a73ELB\u00a77 Included \u00a7e#" + i);
                ret.add(i); }

            // Not 'any' - single slot?
        } else if (getSlot() == getRange()) {

            // Only itself
            ret.add(getSlot());
            //SLOT//OotilityCeption.Log("\u00a78Slot \u00a73ELB\u00a77 Included \u00a7a#" + getSlot());

        // Then its a range
        } else {

            // All between, and inclusive.
            for (int i = getSlot(); i <= getRange(); i++) {
                //SLOT//OotilityCeption.Log("\u00a78Slot \u00a73ELB\u00a77 Included \u00a7c#" + i);
                ret.add(i); }
        }

        // Those are the slots the range stands for
        return ret;
    }

    /**
     * This will ignore any ranges, and use the {@link #getSlot()} as the
     * target slot.
     *
     * @param player Player to actually find this item
     *
     * @return The item found, if one can be found.
     */
    @Nullable public abstract ItemStackLocation getItem(@NotNull OfflinePlayer player);
}
