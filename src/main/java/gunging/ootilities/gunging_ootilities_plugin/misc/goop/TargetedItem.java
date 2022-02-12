package gunging.ootilities.gunging_ootilities_plugin.misc.goop;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TargetedItem {
    
    // Generic targeted item
    @NotNull TargetedItemType type;
    @Nullable final ItemStack original;
    @Nullable public ItemStack getOriginal() { return original; }
    @NotNull public ItemStack getValidOriginal() { Validate.isTrue(!OotilityCeption.IsAirNullAllowed(original), "This item is null or air, consider using #getOriginal() instead."); return original; }

    @Nullable protected ItemStack result;
    @Nullable public ItemStack getResult() { return result; }

    @NotNull public Entity getEntity() {

        // Find entity
        switch (type) {
            case PLAYER: return player_player;
            case DROPPED: return dropped_item;
        }

        // Cursed
        return null;
    }

    @Nullable public Player getPlayer() {

        // Find entity
        switch (type) {
            case PLAYER: return player_player;
            case DROPPED: return null;
        }

        // Cursed
        return null;
    }

    /**
     * Overwrites the contents of this place yes.
     */
    public void ApplyChanges() {

        switch (type) {
            case PLAYER:
                player_loc.setItem(getResult());
                break;
            case DROPPED:
                OotilityCeption.SetDroppedItemItemStack(dropped_item, getResult());
                break;
        }
    }

    // Targeted item utils
    @NotNull RefSimulator<String> logAddition = new RefSimulator<>(null);
    @NotNull public RefSimulator<String> getLogAddition() { return logAddition; }
    public void addToLogAddition(@NotNull String str) { String curr = logAddition.getValue(); if (curr == null) { curr = ""; } logAddition.setValue(curr + str); }
    @NotNull RefSimulator<String> ref_str_a = new RefSimulator<>(null);
    @NotNull public RefSimulator<String> getRef_str_a() { return ref_str_a; }
    @NotNull RefSimulator<String> ref_str_b = new RefSimulator<>(null);
    @NotNull public RefSimulator<String> getRef_str_b() { return ref_str_b; }
    @NotNull RefSimulator<String> ref_str_c = new RefSimulator<>(null);
    @NotNull public RefSimulator<String> getRef_str_c() { return ref_str_c; }

    @NotNull RefSimulator<Integer> ref_int_a = new RefSimulator<>(null);
    @NotNull public RefSimulator<Integer> getRef_int_a() { return ref_int_a; }
    @NotNull RefSimulator<Integer> ref_int_b = new RefSimulator<>(null);
    @NotNull public RefSimulator<Integer> getRef_int_b() { return ref_int_b; }
    @NotNull RefSimulator<Integer> ref_int_c = new RefSimulator<>(null);
    @NotNull public RefSimulator<Integer> getRef_int_c() { return ref_int_c; }

    @NotNull RefSimulator<Double> ref_dob_a = new RefSimulator<>(null);
    @NotNull public RefSimulator<Double> getRef_dob_a() { return ref_dob_a; }
    @NotNull RefSimulator<Double> ref_dob_b = new RefSimulator<>(null);
    @NotNull public RefSimulator<Double> getRef_dob_b() { return ref_dob_b; }
    @NotNull RefSimulator<Double> ref_dob_c = new RefSimulator<>(null);
    @NotNull public RefSimulator<Double> getRef_dob_c() { return ref_dob_c; }

    @NotNull RefSimulator<Boolean> ref_bol_a = new RefSimulator<>(null);
    @NotNull public RefSimulator<Boolean> getRef_bol_a() { return ref_bol_a; }
    @NotNull RefSimulator<Boolean> ref_bol_b = new RefSimulator<>(null);
    @NotNull public RefSimulator<Boolean> getRef_bol_b() { return ref_bol_b; }
    @NotNull RefSimulator<Boolean> ref_bol_c = new RefSimulator<>(null);
    @NotNull public RefSimulator<Boolean> getRef_bol_c() { return ref_bol_c; }
    
    /**
     * 
     * @param player Player associated to this slot
     *               
     * @param slot Slot this item is located at.
     */
    public TargetedItem(@NotNull Player player, @NotNull ItemStackSlot slot, @NotNull ItemStackLocation slot_loc) {
        type = TargetedItemType.PLAYER;
        player_player = player;
        player_loc = slot_loc;
        player_slot = slot;

        // Get Original Item
        original = player_loc.getItem();
    }
    ItemStackSlot player_slot;
    ItemStackLocation player_loc;
    Player player_player;

    /**
     * @param item Dropped Item represented
     */
    public TargetedItem(@NotNull Item item) {
        type = TargetedItemType.DROPPED;
        dropped_item = item;
        
        // Get Original Item
        original = OotilityCeption.FromDroppedItem(item);
    }
    Item dropped_item;

    /**
     * @return If this TargetedItem is invalid
     */
    public boolean isAir() { return OotilityCeption.IsAirNullAllowed(original); }
}
