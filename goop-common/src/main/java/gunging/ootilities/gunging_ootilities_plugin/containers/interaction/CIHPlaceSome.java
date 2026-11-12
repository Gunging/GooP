package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CIHPlaceSome extends ContainersClickHandler {

    /**
     * For the use of {@link InventoryAction#PLACE_SOME}
     */
    public CIHPlaceSome() { super(InventoryAction.PLACE_SOME); }

    /**
     * For the use of {@link InventoryAction#PLACE_ALL} and {@link InventoryAction#PLACE_ONE}
     */
    public CIHPlaceSome(@NotNull InventoryAction override) { super(override); }

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

        // Should never happen
        ItemStack cursorItem = event.getCursor();
        if (OotilityCeption.IsAirNullAllowed(cursorItem)) { return null; }

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
        boolean couldInteract = ContainersClickHandler.canInteract(interactingSlot, player, cursorItem, interactionFail);

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
         * So the item being put out
         *  + Slot currently empty
         *  + Its from a storage slot
         *  + The restrictions have been checked
         *  + Slot masks checked
         *
         * So it can be taken out yeah
         */
        ContainersInteractionResult result = new ContainersInteractionResult(interactingSlot, CIRClickType.STORE);

        // Current amount?
        int currentAmount = isDefault ? 0 : GOOPCManager.amountOfItem(event.getCurrentItem());
        interactingSlot.setItem(actuallyPlace(result, cursorItem, currentAmount));

        // Finally
        return result;
    }

    public int getAmountToTransfer(@NotNull ItemStack cursor, int currentAmount) {

        /*
         * Will transfer enough to get the "Current Item" to max stack,
         * which is the max transfers (the difference between the max stack
         * and the current item amount) or the most it can transfer ~ the
         * amount currently in the cursor.
         *
         * If PLACE_SOME is being called, we are guaranteed to not be placing
         * all, so the former must be the case...!!
         */

        // How many items fit?
        int numberOfItemsThatCanBePlaced = cursor.getMaxStackSize() - currentAmount;

        // If we can deposit them, deposit them. Otherwise, we don't do anything
        return Math.max(numberOfItemsThatCanBePlaced, 0);
    }

    @NotNull public ItemStack actuallyPlace(@NotNull ContainersInteractionResult result, @NotNull ItemStack cursor, int currentAmountInitial) {

        //EVN// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePLC\u00a7a +\u00a77 Initial \u00a7bCursor\u00a77 " + OotilityCeption.GetItemName(cursor, true));
        //EVN// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePLC\u00a7a +\u00a77 Initial \u00a7eCurrent\u00a77 Amount " + currentAmountInitial);

        /*
         * Perform calculations:
         *
         *   + Get amount already in there
         *   + Get amount in cursor
         *
         * If currently the default item is in there, the current count is ZERO!
         * Though this means the attribute to make them unstackable is not being
         * applied correctly grrr...
         */
        int cursorAmountInitial = GOOPCManager.amountOfItem(cursor);
        int amountToTransfer = getAmountToTransfer(cursor, currentAmountInitial);
        //EVN// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePLC\u00a7a +\u00a77 Transference amount " + amountToTransfer);

        // Transfer one from cursor to current
        cursorAmountInitial -= amountToTransfer;
        currentAmountInitial += amountToTransfer;

        // Stacks of the cursor to replace
        ItemStack finalStored = GOOPCManager.cloneItem(cursor);
        ItemStack finalPickup = finalStored.clone();

        finalStored.setAmount(currentAmountInitial);
        finalPickup.setAmount(cursorAmountInitial);

        // Update places
        result.setCursorUpdate(finalPickup);

        //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePLC\u00a7a +\u00a77 Storing " + OotilityCeption.GetItemName(finalStored, true));
        //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePLC\u00a7a +\u00a77 Picking Up " + OotilityCeption.GetItemName(finalPickup, true));
        return finalStored;
    }
}
