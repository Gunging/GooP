package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.NBTFilter;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * A slot found in a common player inventory.
 */
public class ISLInventory extends ItemStackLocation {

    /**
     * @return The inventory where this item is located in
     */
    @NotNull public OfflinePlayer getOwner() { return owner; }
    @NotNull OfflinePlayer owner;

    /**
     * The only search locations for which this constructor is suitable are: <br>
     *     {@link SearchLocation#INVENTORY}            <br>
     *
     * @param owner The player who owns this slot.
     * @param slotNumber Slot of this inventory where this at.
     */
    public ISLInventory(@NotNull OfflinePlayer owner, int slotNumber) {
        super(SearchLocation.INVENTORY, null, slotNumber);
        this.owner = owner;
    }

    @Override public @NotNull ISLShulker getShulker(int shulkerNumber) { return new ISLShulker(SearchLocation.SHULKER_INVENTORY, this, shulkerNumber); }

    @Override public @Nullable ItemStack getItem() { return OotilityCeption.getItemFromPlayerInventory(getOwner().getPlayer(), getSlot()); }

    @Override public void setItem(@Nullable ItemStack item) { OotilityCeption.setItemFromPlayerInventory(getOwner().getPlayer(), getSlot(), item); }

    /**
     * Used to generate dummy ISLInventory to run semi-static
     * methods, rather than being functional. Please do not use
     * any other methods with this constructor:
     *
     * @see #getAllMatching(Player, NBTFilter, RefSimulator, RefSimulator)
     * @see #getAllShulker(Player)
     */
    @SuppressWarnings("ConstantConditions")
    public ISLInventory() { super(SearchLocation.INVENTORY, null); owner = null; }
    @Override @NotNull public ArrayList<ISLInventory> getAllMatching(@NotNull Player player, @NotNull NBTFilter filter, @Nullable RefSimulator<Integer> count, @Nullable RefSimulator<String> logger) {

        // Count
        int totalAmount = 0;

        // Locations
        ArrayList<ISLInventory> ret = new ArrayList<>();

        // Inspect whole inventory
        for (int slot = -7; slot < 36; slot++) {

            // Temporary Item Stack for evaluation
            ISLInventory location = OotilityCeption.getItemFromPlayer(player, slot, true);
            if (location == null) { continue; }

            // If found
            ItemStack item = location.getItem();

            // If the item matches
            if (!OotilityCeption.MatchesItemNBTtestString(item, filter, logger)) { continue; }

            // Include amount
            if (OotilityCeption.IsAirNullAllowed(item)) { totalAmount++; } else { totalAmount += item.getAmount(); }

            // Include location
            ret.add(location);
        }

        // Store
        if (count != null) { count.setValue(totalAmount); }

        // Yeah
        return ret;
    }
    @Override @NotNull public ArrayList<ISLInventory> getAllShulker(@NotNull Player player) {

        // Locations
        ArrayList<ISLInventory> ret = new ArrayList<>();

        // Inspect whole inventory
        for (int slot = -7; slot < 36; slot++) {

            // Temporary Item Stack for evaluation
            ISLInventory location = OotilityCeption.getItemFromPlayer(player, slot, true);
            if (location == null) { continue; }

            // If found
            ItemStack item = location.getItem();
            if (item == null) { continue; }

            // If the item matches
            if (!OotilityCeption.IsShulkerBox(item.getType())) { continue; }

            // Include location
            ret.add(location);
        }

        // Yeah
        return ret;
    }
}
