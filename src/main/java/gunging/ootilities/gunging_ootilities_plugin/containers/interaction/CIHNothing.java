package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CIHNothing extends ContainersClickHandler {

    /**
     * For the use of {@link InventoryAction#NOTHING}
     */
    public CIHNothing() { super(InventoryAction.NOTHING); }

    @Override
    public boolean isMultiSlot() { return false; }

    /**
     * Pickup all action is very simple, but it has two paths:
     * <p>
     * #1 The player is picking up an item they have stored
     * <p>
     * #2 The player is picking up a default/display item
     */
    @Override
    @Nullable
    public ContainersInteractionResult handleContainersOperation(@Nullable GOOPCDeployed deployed, @Nullable ContainerInventory observed, @Nullable GOOPCPlayer rpg, @NotNull Player player, @NotNull InventoryClickEvent event) {

        //region Standard Checking

        // Only proceed if allowed
        event.setCancelled(true);

        // Preview mode? No more actions.
        if (GOOPCManager.isUsage_Preview(event.getView())) {
            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7e -\u00a77 Preview Mode");
            return null; }

        // What is the meaning of this?
        CIInteractionSpecs specs = new CIInteractionSpecs(player, event, deployed, observed, rpg);
        boolean affectsRPGInven = specs.affectsRPGInventory(affectsBottom(event));
        boolean affectsContainer = specs.affectsContainer(affectsTop(event));

        // Should not happen
        if ((!affectsRPGInven && !affectsContainer) || (event.getClickedInventory() == null)) { return null; }

        // Get interacting slot
        CIInteractingSlot interactingSlot = ContainersClickHandler.eventTarget(specs.getClickedDeployed(), specs.getClickedSlot(), event.getClickedInventory(), event.getView(), observed);
        if (!(interactingSlot instanceof CIContainerInteracting)) { return null; }

        // Identifying
        RefSimulator<CIInteractingFailReason> interactionFail = new RefSimulator<>(null);
        GOOPCSlot slot = ((CIContainerInteracting) interactingSlot).getContainerSlot();

        // Interact
        ContainersClickHandler.canInteract(interactingSlot, player, null, interactionFail);

        // If its display, it shall run commands on click
        if (interactionFail.getValue() == CIInteractingFailReason.DISPLAY_ITEM) {

            /*
             * If it has commands on click, it will execute them, by
             * building an operation result. The action is cancelled
             * nevertheless.
             */
            event.setCancelled(true);

            // No more business if there is no Commands On Click
            if (!slot.hasCommandsOnClick()) {

                //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 No Commands On Click");
                return null; }

            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7e *\u00a77 Running On-Click Commands");

            // All the commands on click are included, and that's it.
            return new ContainersInteractionResult(interactingSlot, CIRClickType.CLICK);
        }

        // Nothing else is supported for this inventoryAction
        return null;
    }
}
