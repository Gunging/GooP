package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.NBTFilter;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ISLShulker extends ItemStackLocation {

    /**
     * @return This location will return null items unless they are
     *         in a shulker box, and a box named like this, at that,
     */
    @Nullable public String getShulkerBoxNameFilter() { return shulkerBoxNameFilter; }
    @Nullable String shulkerBoxNameFilter;
    /**
     * @param shulkerBoxNameFilter The name of shulker boxes that will be
     *                             accepted ~ if it is not matched, {@link #getItem()}
     *                             will return null.
     */
    public void setShulkerBoxNameFilter(@Nullable String shulkerBoxNameFilter) { this.shulkerBoxNameFilter = shulkerBoxNameFilter; }

    /**
     * The only search locations for which this constructor is suitable are: <br>
     *     {@link SearchLocation#SHULKER_PERSONAL_CONTAINER}            <br>
     *     {@link SearchLocation#SHULKER_OBSERVED_CONTAINER}            <br>
     *     {@link SearchLocation#SHULKER_INVENTORY}            <br>
     *     {@link SearchLocation#SHULKER_ENDERCHEST}            <br>
     *
     * @param shulkerSlot Slot of this inventory where this at.
     */
    public ISLShulker(@NotNull SearchLocation type, @NotNull ItemStackLocation parent, int shulkerSlot) {
        super(type, parent, shulkerSlot);
    }

    @SuppressWarnings("ConstantConditions")
    @Override @NotNull public ItemStackLocation getParent() { return parent; }

    /**
     *
     * @param shulkerNumber Slot within the shulker box you target.
     *
     * @return Itself, you cannot have shulker boxes within shulker boxes.
     */
    @Override public @NotNull ISLShulker getShulker(int shulkerNumber) { return this; }

    @Override public @Nullable ItemStack getItem() {

        if (getSlot() < 0 || getSlot() >= 27) { return null; }

        // Find the parent's item
        ItemStack shulker = getParent().getItem();
        if (shulker == null || !OotilityCeption.IsShulkerBox(shulker.getType())) { return null; }

        // If the shulker box name filter exists
        if (shulkerBoxNameFilter != null) {

            // Do the names of the item and filter match
            if (!OotilityCeption.ParseColour(OotilityCeption.GetItemName(shulker)).equals(OotilityCeption.ParseColour(shulkerBoxNameFilter))) {

                // No item accepted.
                return null;
            }
        }

        // Get Shulker inventory
        BlockStateMeta shulkerMeta = (BlockStateMeta) shulker.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) shulkerMeta.getBlockState();

        // Retrieve Item
        return shulkerBox.getInventory().getItem(getSlot());

    }

    @Override
    public void setItem(@Nullable ItemStack item) {
        if (getSlot() < 0 || getSlot() >= 27) { return; }

        // Find the parent's item
        ItemStack shulker = getParent().getItem();
        if (shulker == null || !OotilityCeption.IsShulkerBox(shulker.getType())) { return; }

        // Get Shulker inventory
        BlockStateMeta shulkerMeta = (BlockStateMeta) shulker.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) shulkerMeta.getBlockState();

        // Set Item
        shulkerBox.getInventory().setItem(getSlot(), item);
        shulkerBox.update();
        shulkerMeta.setBlockState(shulkerBox);
        shulker.setItemMeta(shulkerMeta);

        // Set in parent
        getParent().setItem(shulker);
    }

    @Override public @NotNull ArrayList<ISLShulker> getAllMatching(@NotNull Player player, @NotNull NBTFilter filter, @Nullable RefSimulator<Integer> count, @Nullable RefSimulator<String> logger) {

        // Count
        int totalAmount = 0;

        // Locations
        ArrayList<ISLShulker> ret = new ArrayList<>();

        /*
         * First find all parent slots that have shulker boxes
         */
        ArrayList<? extends ItemStackLocation> shulker = getParent().getAllShulker(player);

        /*
         * Then, go through each of them, make sure they match the name
         */
        for (ItemStackLocation parentLocation : shulker) {

            // Inspect whole inventory
            for (int slot = 0; slot < 27; slot++) {

                // Find location
                ISLShulker location = parentLocation.getShulker(slot);
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

        return ret;
    }
    @Override public @NotNull ArrayList<? extends ItemStackLocation> getAllShulker(@NotNull Player player) {

        /*
         * Shulker boxes cannot have shulker boxes within,
         * this array will always be empty anyway.
         */
        return new ArrayList<>();
    }
}
