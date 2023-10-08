package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.containers.restriction.RestrictedBehaviour;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CIHMoveToOtherInventory extends ContainersClickHandler {

    /**
     * For the use of {@link InventoryAction#MOVE_TO_OTHER_INVENTORY}
     */
    public CIHMoveToOtherInventory() { super(InventoryAction.MOVE_TO_OTHER_INVENTORY); }

    @Override public boolean isMultiSlot() { return true; }

    /**
     * Pickup all action is very simple, but it has two paths:
     *
     *  #1 The player is picking up an item they have stored
     *
     *  #2 The player is picking up a default/display item
     */
    @Override @Nullable public ContainersInteractionResult handleContainersOperation(@Nullable GOOPCDeployed deployed, @Nullable ContainerInventory observed, @Nullable GOOPCPlayer rpg, @NotNull Player player, @NotNull InventoryClickEvent event) {
        //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS-HFI\u00a77 Handling from Inventory");

        // Only proceed if allowed
        event.setCancelled(true);

        // Preview mode? No more actions.
        if (GOOPCManager.isUsage_Preview(event.getView())) {
            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7e -\u00a77 Preview Mode");
            return null; }

        // Easy reference
        ItemStack movingItem = GOOPCManager.cloneItem(event.getCurrentItem());

        // Is it the default item? You can't take that
        if (OotilityCeption.IsAirNullAllowed(movingItem)) {
            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eMVI\u00a7c -\u00a77 Aborting Operation ~ target item is AIR");
            return null; }

        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Slot \u00a7b#" + event.getSlot() + " \u00a78(Raw\u00a79 #" + event.getRawSlot() + "\u00a78) -\u00a75 " + event.getView().getType().toString());
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Affects Container?\u00a73 " + affectsContainer);
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Affects RPGInvent?\u00a73 " + affectsRPGInven);
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Current?\u00a73 " + OotilityCeption.GetItemName(event.getCurrentItem()));
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Cursor?\u00a73 " + OotilityCeption.GetItemName(event.getCursor()));

        // What is the meaning of this?
        CIInteractionSpecs specs = new CIInteractionSpecs(player, event, deployed, observed, rpg);
        boolean affectsRPGInven = specs.affectsRPGInventory(affectsBottom(event));
        boolean affectsContainer = specs.affectsContainer(affectsTop(event));

        // Should not happen
        if ((!affectsRPGInven && !affectsContainer) || (event.getClickedInventory() == null)) { return null; }

        // Get interacting slot
        CIInteractingSlot interactingSlot = ContainersClickHandler.eventTarget(specs.getClickedDeployed(), specs.getClickedSlot(), event.getClickedInventory(), event.getView(), observed);
        if (interactingSlot == null) {
            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c Failed to build source interacting slot. ");
            return null; }

        // Identifying
        RefSimulator<CIInteractingFailReason> interactionFail = new RefSimulator<>(null);
        GOOPCSlot sourceSlot;

        // Non-container slots are clear already, otherwise default items and such must be taken care of
        if (interactingSlot instanceof CIContainerInteracting) {
            sourceSlot = ((CIContainerInteracting) interactingSlot).getContainerSlot();

            // Interact
            boolean couldInteract = ContainersClickHandler.canInteract(interactingSlot, player, null, interactionFail);

            // If its display, it shall run commands on click
            if (interactionFail.getValue() == CIInteractingFailReason.DISPLAY_ITEM) {

                /*
                 * If it has commands on click, it will execute them, by
                 * building an operation result. The action is cancelled
                 * nevertheless.
                 */
                event.setCancelled(true);

                // No more business if there is no Commands On Click
                if (!sourceSlot.hasCommandsOnClick()) {

                    //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 No Commands On Click");
                    return null; }

                //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7e *\u00a77 Running On-Click Commands");

                // All the commands on click are included, and that's it.
                return new ContainersInteractionResult(interactingSlot, CIRClickType.CLICK);
            }

            // Default item treatment for result slots, only works for STATION containers
            if (interactionFail.getValue() == CIInteractingFailReason.RESULT_SLOT_TYPE && affectsContainer) {

                /*
                 * Allow taking of the item, MythicLib will handle the rest.
                 */
                //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePCK\u00a73 +\u00a77 Delegating Handling to MythicLib");
                event.setCancelled(false);
                return null;
            }

            // Any other forms of failed interaction cause no result.
            if (!couldInteract) {

                /*
                 * However, CIHPickupAll allows to pickup the item if the fail reason is not meeting
                 * restrictions, it is a storage slot, and its restricted behaviour is take. So...
                 */
                if (interactionFail.getValue() != CIInteractingFailReason.RESTRICTIONS_UNMET || !sourceSlot.isForStorage() || sourceSlot.getRestrictedBehaviour() != RestrictedBehaviour.TAKE)
                    return null;
            }
        }

        // Choose which slots this can be sent to
        ArrayList<CIInteractingSlot> targetSlots = new ArrayList<>();

        // From the container to the inventory
        if (specs.isClickedTopInventory()) {

            // The target slots are all those for the player inventory
            if (rpg != null) {
                //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e From top container to bottom RPG inventory. ");

                // Crafting with the workbench must be allowed and is kind of unsupported
                if (event.getView().getType() == InventoryType.WORKBENCH && event.getRawSlot() == 0) {

                    // No need to cancel
                    event.setCancelled(false);

                    // Do whatever you want
                    return null;
                }

                // Reference base inventory slots (no armor or such)
                for (int i = 0; i < 36; i++) {

                    // Slot target the bottom RPG inventory
                    targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(i), i, event.getView().getBottomInventory(), event.getView(), null));
                }

            // Target vanilla inventory
            } else {
                //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e From top container to bottom vanilla inventory. ");

                // Reference base inventory slots (apparently fits hotbar from right to left)
                for (int i = 8; i >= 0; i--) {

                    // Slot target the bottom vanilla inventory
                    targetSlots.add(knownTarget(null, null, i, event.getView().getBottomInventory(), event.getView(), null));
                }

                // Reference base inventory slots (no armor or such) in the normal left to right order
                for (int i = 9; i < 36; i++) {

                    // Slot target the bottom vanilla inventory
                    targetSlots.add(knownTarget(null, null, i, event.getView().getBottomInventory(), event.getView(), null));
                }
            }

        // Clicking the bottom inventory
        } else {

            if (event.getView().getType() == InventoryType.CRAFTING) {

                if (rpg != null) {
                    //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e Move around RPG Inventory");

                    boolean inHotbar = event.getSlot() >= 0 && event.getSlot() < 9;

                    // Armor first
                    if (event.getSlot() != 36) targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(100), 36, event.getView().getBottomInventory(), event.getView(), null));
                    if (event.getSlot() != 37) targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(101), 37, event.getView().getBottomInventory(), event.getView(), null));
                    if (event.getSlot() != 38) targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(102), 38, event.getView().getBottomInventory(), event.getView(), null));
                    if (event.getSlot() != 39) targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(103), 39, event.getView().getBottomInventory(), event.getView(), null));

                    // Reference base inventory slots (no armor or such)
                    for (int i = 0; i < 36; i++) {
                        if (event.getSlot() == i) { continue; }

                        // No hotbar slots if its in hotbar
                        if (inHotbar) { if (i < 9) { continue; } }

                        // Slot target the bottom RPG inventory
                        targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(i), i, event.getView().getBottomInventory(), event.getView(), null));
                    }

                    // Snooze
                    if (event.getSlot() != 40) targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(-106), 40, event.getView().getBottomInventory(), event.getView(), null));

                } else {

                    Gunging_Ootilities_Plugin.theOots.CLog("Containers has no business over this event,\u00a7c why is it involved? ");
                }

            // Normal chest view, use normal mode
            } else {

                if (observed != null) {
                    //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e From bottom inventory to top container. ");

                    // All the slots in that template being seen
                    for (int i = 0; i < observed.getTemplate().getTotalSlotCount(); i++) {

                        // Slot target the bottom vanilla inventory
                        targetSlots.add(knownTarget(deployed, observed.getTemplate().getSlotAt(i), i, event.getView().getTopInventory(), event.getView(), observed));
                    }

                } else {

                    //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e From bottom inventory to non-container top inventory. ");
                    int remainder = movingItem.getAmount();

                    // Examine top inventory for stacking success
                    for (int i = 0; i < event.getView().getTopInventory().getSize(); i++) {

                        // Get that
                        ItemStack target = event.getView().getTopInventory().getItem(i);
                        if (OotilityCeption.IsAirNullAllowed(target)) { remainder = 0; break; }
                        if (target.isSimilar(movingItem)) { remainder -= (movingItem.getType().getMaxStackSize() - target.getAmount()); }
                        if (remainder <= 0) { break; }
                    }

                    // Nothing happens
                    if (remainder == movingItem.getAmount()) { return null; }

                    // Allow event to proceed as normal
                    ContainersInteractionResult result = new ContainersInteractionResult(interactingSlot, CIRClickType.TAKE);
                    event.setCancelled(false);
                    return result;
                }
            }
        }

        // Calculate necessary actions to accommodate in the player's inventory
        int initialRemainder = movingItem.getAmount();
        RefSimulator<Integer> remainder = new RefSimulator<>(initialRemainder);
        ArrayList<CIStackingWay> targetSlot = distribute(targetSlots, movingItem, player, remainder);
        //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePCK\u00a73 +\u00a77 Remainder of operation: " + remainder.getValue());


        // Failure?
        if (targetSlot.size() == 0) {

            // Nothing to do
            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eMVI\u00a7c -\u00a77 Operation incomplete, no stacking operations. ");
            return null;
        }

        /*
         * So the item being taken out
         *  + Is not the default
         *  + Is from a storage slot
         *  + The restrictions have been checked
         *  + Slot masks (not applicable here).
         *
         * So it can be taken out yeah
         */
        ContainersInteractionResult result = new ContainersInteractionResult(interactingSlot, CIRClickType.TAKE);

        // Execute actions
        for (CIStackingWay sw : targetSlot) {

            // Perform stacking operation
            sw.getLocation().setItem(sw.getAmountedItem());
            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7ePCK\u00a7a +\u00a77 Storing @" + sw.getLocation().getSlotNumber() + " " + OotilityCeption.GetItemName(sw.getAmountedItem(), true));

            // Add commands or whatever if its a container slot
            if (!(sw.getLocation() instanceof CIContainerInteracting)) { continue; }

            // Get slot information
            GOOPCSlot swSlot = ((CIContainerInteracting) sw.getLocation()).getContainerSlot();

            // Reference slot update and store commands
            if (!((CIContainerInteracting) sw.getLocation()).getTemplate().isPlayer()) { result.addSlotUpdate(swSlot.getSlotNumber()); }
            result.addCommands((CIContainerInteracting) sw.getLocation(), CIRClickType.STORE);
        }

        // Put the remainder in the inventory
        ItemStack finalStored = OotilityCeption.asQuantity(movingItem, remainder.getValue());
        interactingSlot.setItem(finalStored);

        // Finally
        return result;
    }

    /**
     * Distributes the item among the bunch of slots that the item can fit onto
     *
     * @param inventories Slots onto which send the item
     * @param item Item Stack to distribute
     *
     * @return Each individual Stacking Operation to make
     */
    @NotNull ArrayList<CIStackingWay> distribute(@NotNull ArrayList<CIInteractingSlot> inventories, @NotNull ItemStack item, @NotNull Player player, @NotNull RefSimulator<Integer> remainder) {
        ArrayList<CIStackingWay> ret = new ArrayList<>();
        int kount = item.getAmount();

        /*
         * Startup scourge: Remove those that cannot be interacted with
         */
        for (int s = 0; s < inventories.size(); s++) {

            // Get observed
            CIInteractingSlot interactingSlot = inventories.get(s);
            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Validating Slot \u00a7e#" + interactingSlot.getSlotNumber() + "\u00a77 of\u00a7b " + ((interactingSlot instanceof CIContainerInteracting) ? ((CIContainerInteracting) interactingSlot).getTemplate().getInternalName() : "\u00a7cvanilla"));

            // Cant interact cant consider
            RefSimulator<CIInteractingFailReason> reason = new RefSimulator<>(null);
            boolean isDefault = false;
            if (!canInteract(interactingSlot, player, item, reason)) {

                // Is it default item? You are allowed to stack items on top of them :flushed:
                isDefault = reason.getValue() == CIInteractingFailReason.DEFAULT_ITEM;
                if (!(isDefault && interactingSlot instanceof CIContainerInteracting && ((CIContainerInteracting) interactingSlot).getContainerSlot().isForStorage())) {
                    //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7c -\u00a77 Removed - cannot interact");

                    // Remove
                    inventories.remove(s);
                    s--;

                    // No more action needed
                    continue;
                }
            }

            // Slots that are occupied well, obviously won't work!
            ItemStack obs = interactingSlot.getCurrentItem();
            if (!isDefault && !OotilityCeption.IsAirNullAllowed(obs) && !item.isSimilar(obs)) {
                //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7c -\u00a77 Removed - occupied");

                // Remove
                inventories.remove(s);
                s--;

                //ACT//continue;
            }

            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7a +\u00a77 Accepted");
        }

        //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Distributing Amount:\u00a73 " + kount);

        /*
         * First scourge: Slots where this item preferably fits
         */
        for (int s = 0; s < inventories.size(); s++) {

            // Slot must be preferred
            CIInteractingSlot interactingSlot = inventories.get(s);
            if (!interactingSlot.hasPreferenceFor(item)) { continue; }
            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Preference Slot \u00a7e#" + interactingSlot.getSlotNumber() + "\u00a77 of\u00a7b " + ((interactingSlot instanceof CIContainerInteracting) ? ((CIContainerInteracting) interactingSlot).getTemplate().getInternalName() : "\u00a7cvanilla"));

            // Compare to current item
            ItemStack obs = interactingSlot.getCurrentItem();
            if (OotilityCeption.IsAirNullAllowed(obs) || GOOPCManager.isDefaultItem(obs)) {
                //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 (empty Slot, operation should end after this)");
                obs = OotilityCeption.asQuantity(item, 0);

            }  else if (!item.isSimilar(obs)) {
                //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a75 -\u00a77 Ignored, not stackable");
                continue; }

            // Dekrease kount
            int absorbed = item.getType().getMaxStackSize() - obs.getAmount();
            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Absorbing " + item.getType().getMaxStackSize() + " - " + obs.getAmount() + " = \u00a7e" + absorbed);

            // Will absorb?
            if (absorbed > 0) {
                //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7b +\u00a77 Applied \u00a7b+");

                // If greater than kount, we dne
                if (absorbed > kount) {

                    // No more kount
                    ret.add(new CIStackingWay(interactingSlot, OotilityCeption.asQuantity(item, kount + obs.getAmount())));
                    kount = 0;
                    break;

                } else {

                    // Subtract absorption
                    ret.add(new CIStackingWay(interactingSlot, OotilityCeption.asQuantity(item, absorbed + obs.getAmount())));
                    kount -= absorbed;
                    if (kount == 0) { break; }
                }
            }

            // Remove
            inventories.remove(s);
            s--;
        }

        //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Distributing Amount:\u00a73 " + kount);

        // Operation complete
        if (kount <= 0) {

            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7a Closing Success");
            remainder.setValue(0); return ret; }

        /*
         * Second scourge: Find all stacking items and put the item atop
         */
        for (int s = 0; s < inventories.size(); s++) {

            // Skip Air
            CIInteractingSlot interactingSlot = inventories.get(s);
            ItemStack obs = interactingSlot.getCurrentItem();
            if (OotilityCeption.IsAirNullAllowed(obs)) { continue; }

            // Must be able to stack
            if (!item.isSimilar(obs)) { continue; }
            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Stackable Slot \u00a7e#" + interactingSlot.getSlotNumber() + "\u00a77 of\u00a7b " + ((interactingSlot instanceof CIContainerInteracting) ? ((CIContainerInteracting) interactingSlot).getTemplate().getInternalName() : "\u00a7cvanilla"));


            // Dekrease kount
            int absorbed = item.getType().getMaxStackSize() - obs.getAmount();

            // Will absorb?
            if (absorbed > 0) {
                //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7b +\u00a77 Applied \u00a7b+");

                // If greater than kount, we dne
                if (absorbed > kount) {

                    // No more kount
                    ret.add(new CIStackingWay(interactingSlot, OotilityCeption.asQuantity(item, kount + obs.getAmount())));
                    kount = 0;
                    break;

                } else {

                    // Subtract absorption
                    ret.add(new CIStackingWay(interactingSlot, OotilityCeption.asQuantity(item, absorbed + obs.getAmount())));
                    kount -= absorbed;
                    if (kount == 0) { break; }
                }
            }

            // Remove
            inventories.remove(s);
            s--;
        }

        //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Distributing Amount:\u00a73 " + kount);

        // Operation complete
        if (kount <= 0) {

            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7a Closing Success");
            remainder.setValue(0); return ret; }

        /*
         * Last scourge: Any empty slot to store the remainder
         */
        if (inventories.size() > 0) {

            // Only need an empty slot
            CIInteractingSlot interactingSlot = inventories.get(0);

            // Then this is all we needed
            ret.add(new CIStackingWay(interactingSlot, OotilityCeption.asQuantity(item, kount)));
            kount = 0;

            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Empty Slot \u00a7e#" + interactingSlot.getSlotNumber() + "\u00a77 of\u00a7b " + ((interactingSlot instanceof CIContainerInteracting) ? ((CIContainerInteracting) interactingSlot).getTemplate().getInternalName() : "\u00a7cvanilla"));
            //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a7b +\u00a77 Applied \u00a7b+");
        }

        //ACT//OotilityCeption.Log("\u00a78CIH\u00a7c MTO\u00a77 Remaining Amount:\u00a73 " + kount);

        remainder.setValue(kount);
        return ret;
    }
}
