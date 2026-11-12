package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class ContainersInteractionHandler<T extends InventoryInteractEvent> {

    //region As Manager
    /**
     * @param action Action taking place.
     *
     * @return If there is any handler to handle this, that handler.
     */
    @Nullable
    public static ContainersClickHandler getHandlerFor(@NotNull InventoryAction action) { return handlerHashMap.get(action); }
    @NotNull static HashMap<InventoryAction, ContainersClickHandler> handlerHashMap = new HashMap<>();

    /**
     * @param action Action taking place.
     *
     * @return If there is any handler to handle this, that handler.
     */
    @Nullable public static ContainersDragHandler getHandlerFor(@NotNull DragType action) { return handlerDragMap.get(action); }
    @NotNull static HashMap<DragType, ContainersDragHandler> handlerDragMap = new HashMap<>();

    /**
     * Will load the static references of all handlers.
     */
    public static void registerHandlers() {

        // Pickup methodes
        handlerHashMap.put(InventoryAction.SWAP_WITH_CURSOR, new CIHSwap());
        handlerHashMap.put(InventoryAction.PICKUP_ALL, new CIHPickupAll());
        handlerHashMap.put(InventoryAction.PICKUP_HALF, new CIHPickupHalf());
        handlerHashMap.put(InventoryAction.PLACE_ALL, new CIHPlaceAll());
        handlerHashMap.put(InventoryAction.PLACE_SOME, new CIHPlaceSome());
        handlerHashMap.put(InventoryAction.PLACE_ONE, new CIHPlaceOne());
        handlerHashMap.put(InventoryAction.DROP_ALL_CURSOR, new CIHDropAllCursor());
        handlerHashMap.put(InventoryAction.DROP_ONE_CURSOR, new CIHDropOneCursor());
        handlerHashMap.put(InventoryAction.DROP_ONE_SLOT, new CIHDropOneSlot());
        handlerHashMap.put(InventoryAction.DROP_ALL_SLOT, new CIHDropAllSlot());
        handlerHashMap.put(InventoryAction.NOTHING, new CIHNothing());
        handlerHashMap.put(InventoryAction.MOVE_TO_OTHER_INVENTORY, new CIHMoveToOtherInventory());
        handlerHashMap.put(InventoryAction.COLLECT_TO_CURSOR, new CIHCollectToCursor());

        handlerDragMap.put(DragType.EVEN, new CIHEvenDrag());
        handlerDragMap.put(DragType.SINGLE, new CIHSingleDrag());
    }
    //endregion


    //region Multi-Slot Region
    /**
     * @return If this action can affect multiple slots
     */
    public abstract boolean isMultiSlot();

    /**
     * Assuming that the action is taking place in the bottom inventory,
     * will the top inventory of the InventoryView be affected?
     *
     * @param event Event that is happening
     *
     * @return If the top inventory will be affected by the action.
     */
    public boolean affectsTop(@NotNull T event) { return isMultiSlot(); }

    /**
     * Assuming that the action is taking place in the bottom inventory,
     * will the top inventory of the InventoryView be affected?
     *
     * @param event Event that is happening
     *
     * @return If the bottom inventory will be affected by the action.
     */
    public boolean affectsBottom(@NotNull T event) { return isMultiSlot(); }
    //endregion

    //region Handler
    /**
     * Saves this change into the file, and basically tells which commands must
     * be ran, and slots of the inventory updated / rebuilt for the observers.
     *
     * @param deployed Identified container
     * @param player Player who is doing the transaction
     * @param event All the event information
     */
    @Nullable public abstract ContainersInteractionResult handleContainersOperation(@Nullable GOOPCDeployed deployed, @Nullable ContainerInventory observed, @Nullable GOOPCPlayer rpg, @NotNull Player player, @NotNull T event);
    //endregion
}
