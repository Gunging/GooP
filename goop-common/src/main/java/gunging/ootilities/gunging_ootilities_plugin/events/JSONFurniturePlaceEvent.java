package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JSONFurniturePlaceEvent extends BlockEvent {

    @Nullable public Player getPlayer() { return player; }
    @Nullable Player player;

    @NotNull public ItemStack getItem() { return item; }
    @NotNull ItemStack item;

    @NotNull public Orientations getForward() { return forward; }
    @NotNull Orientations forward;

    public JSONFurniturePlaceEvent(@NotNull Block theBlock, @Nullable Player player, @NotNull ItemStack item, @NotNull Orientations forward) {
        super(theBlock);
        this.player = player;
        this.item = item;
        this.forward = forward;
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
}
