package gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.NBTFilter;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * A slot found in a common player enderchest.
 */
public class ISLEnderchest extends ItemStackLocation {

    /**
     * @return The inventory where this item is located in
     */
    @NotNull public OfflinePlayer getOwner() { return owner; }
    @NotNull OfflinePlayer owner;

    /**
     * The only search locations for which this constructor is suitable are: <br>
     *     {@link SearchLocation#ENDERCHEST}            <br>
     *
     * @param owner The player who owns this slot.
     * @param slotNumber Slot of this enderchest where the item is at.
     */
    public ISLEnderchest(@NotNull OfflinePlayer owner, int slotNumber) {
        super(SearchLocation.ENDERCHEST, null, slotNumber);
        this.owner = owner;
    }

    @Override
    public @NotNull ISLShulker getShulker(int shulkerNumber) { return new ISLShulker(SearchLocation.SHULKER_ENDERCHEST, this, shulkerNumber); }

    @Override
    public @Nullable ItemStack getItem() {
        if (getSlot() < 0 || getSlot() >= 27) { return null; }

        // Get player
        Player player = getOwner().getPlayer();
        if (player == null) { return null; }

        // Get enderchest
        Inventory enderchest = player.getEnderChest();
        return enderchest.getItem(getSlot());
    }

    @Override
    public void setItem(@Nullable ItemStack item) {
        if (getSlot() < 0 || getSlot() >= 27) { return; }

        // Get player
        Player player = getOwner().getPlayer();
        if (player == null) { return; }

        // Get enderchest
        Inventory enderchest = player.getEnderChest();
        enderchest.setItem(getSlot(), item);
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
    public ISLEnderchest() { super(SearchLocation.ENDERCHEST, null); owner = null; }
    @Override @NotNull public ArrayList<ISLEnderchest> getAllMatching(@NotNull Player player, @NotNull NBTFilter filter, @Nullable RefSimulator<Integer> count, @Nullable RefSimulator<String> logger) {

        // Count
        int totalAmount = 0;

        // Locations
        ArrayList<ISLEnderchest> ret = new ArrayList<>();

        // Inspect whole inventory
        for (int slot = 0; slot < 27; slot++) {

            // Temporary Item Stack for evaluation
            ISSEnderchest itemSlot = new ISSEnderchest(slot, null);
            itemSlot.setElaborator(player);
            ISLEnderchest location = itemSlot.getItem(player);

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
    @Override @NotNull public ArrayList<ISLEnderchest> getAllShulker(@NotNull Player player) {

        // Locations
        ArrayList<ISLEnderchest> ret = new ArrayList<>();

        // Inspect whole inventory
        for (int slot = 0; slot < 27; slot++) {

            // Temporary Item Stack for evaluation
            ISSEnderchest itemSlot = new ISSEnderchest(slot, null);
            itemSlot.setElaborator(player);
            ISLEnderchest location = itemSlot.getItem(player);

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
