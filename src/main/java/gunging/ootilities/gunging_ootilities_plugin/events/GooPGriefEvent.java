package gunging.ootilities.gunging_ootilities_plugin.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GooPGriefEvent extends Event implements Cancellable {

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
    public GooPGriefEvent(@NotNull Player breaker, @NotNull ArrayList<Block> affected, ItemStack toolUsed) {
        player = breaker;
        blocks = affected;
        tool = toolUsed;
    }

    public Player getPlayer() { return player; }
    public ArrayList<Block> getBlocks() { return blocks; }
    public ItemStack getTool() { return tool; }
}
