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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CIHSwap extends ContainersClickHandler {

    /**
     * For the use of {@link InventoryAction#SWAP_WITH_CURSOR}
     */
    public CIHSwap() { super(InventoryAction.SWAP_WITH_CURSOR); }

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
        boolean couldInteract = ContainersClickHandler.canInteract(interactingSlot, player, event.getCursor(), interactionFail);

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

        /*
         * Default item treatment for result slots, only works for STATION containers
         */
        if (interactionFail.getValue() == CIInteractingFailReason.RESULT_SLOT_TYPE && affectsContainer) {

            /*
             * Allow taking of the item, MythicLib will handle the rest.
             */
            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePCK\u00a73 +\u00a77 Delegating Handling to MythicLib");
            event.setCancelled(false);
            return null;
        }

        boolean isDefault = interactionFail.getValue() == CIInteractingFailReason.DEFAULT_ITEM;

        // Any other forms of failed interaction cause no result.
        if (!couldInteract) {

            /*
             * Storing items on default items is indeed allowed
             */
            if (!(isDefault && slot.isForStorage()))
                return null;
        }

        /*
         * So the item being taken out
         *  + Its not the default
         *  + Its from a storage slot
         *  + The restrictions have been checked
         *  + Slot masks (not applicable here).
         *
         * So it can be taken out yeah
         */
        ContainersInteractionResult result = new ContainersInteractionResult(interactingSlot, isDefault ? CIRClickType.STORE : CIRClickType.SWAP);

        // Stacks of the cursor to replace
        ItemStack finalStored = GOOPCManager.cloneItem(event.getCursor());
        ItemStack finalPickup = isDefault ? null : GOOPCManager.cloneItem(event.getCurrentItem());

        // Update places
        event.setCursor(finalPickup);
        interactingSlot.setItem(finalStored);

        //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eSWP\u00a7a +\u00a77 Storing " + OotilityCeption.GetItemName(finalStored));
        //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eSWP\u00a7a +\u00a77 Picking Up " + OotilityCeption.GetItemName(finalPickup));

        // Finally
        return result;
    }
}