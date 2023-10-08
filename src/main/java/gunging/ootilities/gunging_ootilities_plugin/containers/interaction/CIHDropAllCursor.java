package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.flying_protector.logic.GooPTrue;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CIHDropAllCursor extends ContainersClickHandler {

    /**
     * For the use of {@link InventoryAction#PICKUP_ALL}
     */
    public CIHDropAllCursor() { super(InventoryAction.DROP_ALL_CURSOR); }

    @Override public boolean isMultiSlot() { return false; }

    /**
     * Pickup all action is very simple, but it has two paths:
     *
     *  #1 The player is picking up an item they have stored
     *
     *  #2 The player is picking up a default/display item
     */
    @Override @Nullable
    public ContainersInteractionResult handleContainersOperation(@Nullable GOOPCDeployed deployed, @Nullable ContainerInventory observed, @Nullable GOOPCPlayer rpg, @NotNull Player player, @NotNull InventoryClickEvent event) {

        // Containers has no business with cursor operations of this kind
        event.setCancelled(false);
        return null;
    }
}
