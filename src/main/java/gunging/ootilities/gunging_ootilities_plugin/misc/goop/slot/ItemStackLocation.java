package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.misc.NBTFilter;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Contains the information on *where* an item is stored.
 * <br><br>
 * For something that has the information to find an item, and
 * thus generate one of these, see {@link ItemStackSlot}.
 */
public abstract class ItemStackLocation {

    //region Constructor
    /**
     * @return The slot this item is located at, in this inventory.
     */
    public int getSlot() { return slot; }
    int slot;

    /**
     * @return If this slot is within a shulker box, the parent is
     *         its position in an inventory somewhere.
     */
    @Nullable public ItemStackLocation getParent() { return parent; }
    @Nullable ItemStackLocation parent;

    /**
     * @return The type of location where this slot is at.
     */
    @NotNull public SearchLocation getLocation() { return location; }
    @NotNull SearchLocation location;

    /**
     * The general interface of ItemStackLocation.
     *
     * @param location Search location of this slot.
     * @param parent Parent of the slot.
     * @param slotNumber Slot of this inventory where this at.
     */
    public ItemStackLocation(@NotNull SearchLocation location, @Nullable ItemStackLocation parent, int slotNumber) {
        this.location = location;
        this.parent = parent;
        this.slot = slotNumber;
    }

    /**
     * @param shulkerNumber Slot within the shulker box you target.
     *
     * @return A slot from a shulker box, assuming this is a shulker box.
     */
    @NotNull public abstract ISLShulker getShulker(int shulkerNumber);
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
    //endregion

    /**
     * @return The ItemStack contained within this slot?
     */
    @Nullable public abstract ItemStack getItem();
    /**
     * @return The amount of items in this slot, the count.
     */
    public int getAmount() { return getItem() == null ? 0 : getItem().getAmount(); }

    /**
     * This will edit the item in the inventory.
     * <br><br>
     * If need be manual, this will also save the item changes into
     * the files, and call updates to the edited inventory.
     *
     * @param item The new item to put at this location.
     */
    public abstract void setItem(@Nullable ItemStack item);

    /**
     * @param amount New amount of this item to find here.
     */
    public void setAmount(int amount) {
        // Get item
        ItemStack item = getItem();
        if (item == null) { return; }

        // Edit amount
        item.setAmount(amount);

        // Replace with new quantity
        setItem(item);
    }

    //region Semi-Static
    /**
     * Used to generate dummy ItemStackLocation to execute
     * semi-static methods rather than being functional.
     *
     * @see #getAllMatching(Player, NBTFilter, RefSimulator, RefSimulator)
     * @see #getAllShulker(Player)
     */
    @NotNull public ItemStackLocation(@NotNull SearchLocation location, @Nullable ItemStackLocation parent) {
        this.location = location;
        this.parent = parent;
        this.slot = 0; }

    /**
     * Go through the entire location for this player, and find all items
     * that match this filter, and also count their amounts for ease of access.
     *
     * @param player Player whose inventory is to be scourged through.
     * @param filter Filter of items to match.
     * @param count Will export the cumulative amount of items found.
     *
     * @see #ItemStackLocation(SearchLocation, ItemStackLocation)
     */
    @NotNull public abstract ArrayList<? extends ItemStackLocation> getAllMatching(@NotNull Player player, @NotNull NBTFilter filter, @Nullable RefSimulator<Integer> count, @Nullable RefSimulator<String> logger);

    /**
     * Find all slots that contain a shulker box.
     *
     * @param player Player whose inventory is to be scourged through.
     *
     * @see #ItemStackLocation(SearchLocation, ItemStackLocation)
     */
    @NotNull public abstract ArrayList<? extends ItemStackLocation> getAllShulker(@NotNull Player player);
    //endregion
}
