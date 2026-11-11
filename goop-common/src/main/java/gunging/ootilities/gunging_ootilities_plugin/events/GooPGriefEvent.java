package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.RebootBrokenBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GooPGriefEvent extends Event implements Cancellable {

    @NotNull static HashMap<String, ArrayList<RebootBrokenBlock>> rebootBroken = new HashMap<>();

    /**
     * Reboots (repairs) all blocks that were registered to be reboot broken
     */
    public static void reboot() {

        // Reboot them all
        for (Map.Entry<String, ArrayList<RebootBrokenBlock>> rkk : rebootBroken.entrySet()) {

            // For each fo them
            for (RebootBrokenBlock rbb : rkk.getValue()) {

                // Reboot state
                rbb.getOriginal().update(true);
            }

            // Clear
            rkk.getValue().clear();
        }

        // Clear array
        rebootBroken.clear();
    }
    /**
     * Reboots (repairs) all blocks that were registered to be reboot broken under this key
     *
     * @param key Blocks to reboot
     */
    public static void reboot(@NotNull String key) {
        ArrayList<RebootBrokenBlock> ready = rebootBroken.get(key);
        if (ready == null) { return; }

        // For each fo them
        for (RebootBrokenBlock rbb : ready) {

            // Reboot state
            rbb.getOriginal().update(true);
        }

        // Clear
        ready.clear();
        rebootBroken.remove(key);
    }

    /**
     * Will store a copy of this block snapshot in the reboot break arrays, but
     * <b>will not actually break the block</b>. It is assumed you break it however
     * you want <i>after</i> calling this method.
     *
     * @param location Where to break
     *
     * @param key Key to use
     */
    public static void rebootBreak(@NotNull Block location, @org.jetbrains.annotations.Nullable String key) {

        // Store state
        BlockState stasis = location.getState();
        String trueKey = key;

        // Defaults to the world
        if (key == null) { trueKey = location.getWorld().getName(); }

        // Create block
        RebootBrokenBlock rbb = new RebootBrokenBlock(stasis, trueKey);

        // Register
        registerRebootBrokenBlock(rbb);
    }
    /**
     * @param rbb Block to add to the arrays
     */
    public static void registerRebootBrokenBlock(@NotNull RebootBrokenBlock rbb) {
        ArrayList<RebootBrokenBlock> ready = rebootBroken.get(rbb.getRebootKey());
        if (ready == null) { ready = new ArrayList<>(); }

        // If its already there this is ignored, you can only reboot to the first state.
        for (RebootBrokenBlock bkk : ready) { if (OotilityCeption.LocationEquals(bkk.getOriginal().getLocation(), rbb.getOriginal().getLocation())) { return; } }

        ready.add(rbb);
        rebootBroken.put(rbb.getRebootKey(), ready);
    }

    //region Event Standard
    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    //endregion

    //region Cancellable Standard
    boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    //endregion

    Player player;
    ArrayList<Block> blocks;
    ItemStack tool;
    @Nullable String rebootKey;

    @Deprecated
    public GooPGriefEvent(@NotNull Player breaker, @NotNull ArrayList<Block> affected, ItemStack toolUsed) { this(breaker, affected, toolUsed, null); }
    public GooPGriefEvent(@NotNull Player breaker, @NotNull ArrayList<Block> affected, ItemStack toolUsed, @Nullable String rebootKey) {
        player = breaker;
        blocks = affected;
        tool = toolUsed;
        this.rebootKey = rebootKey;
    }

    public Player getPlayer() { return player; }
    public ArrayList<Block> getBlocks() { return blocks; }
    public ItemStack getTool() { return tool; }
    @Nullable public String getRebootKey() { return rebootKey; }
}
