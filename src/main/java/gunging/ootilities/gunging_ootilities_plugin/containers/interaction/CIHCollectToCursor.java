package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.containers.restriction.RestrictedBehaviour;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CIHCollectToCursor extends ContainersClickHandler {

    /**
     * For the use of {@link InventoryAction#COLLECT_TO_CURSOR}
     */
    public CIHCollectToCursor() { super(InventoryAction.COLLECT_TO_CURSOR); }

    @Override public boolean isMultiSlot() { return true; }

    /**
     * Pickup all action is very simple, but it has two paths:
     *
     *  #1 The player is picking up an item they have stored
     *
     *  #2 The player is picking up a default/display item
     */
    @Override @Nullable
    public ContainersInteractionResult handleContainersOperation(@Nullable GOOPCDeployed deployed, @Nullable ContainerInventory observed, @Nullable GOOPCPlayer rpg, @NotNull Player player, @NotNull InventoryClickEvent event) {

        // Only proceed if allowed
        event.setCancelled(true);

        // Preview mode? No more actions.
        if (GOOPCManager.isUsage_Preview(event.getView())) {
            //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a7e -\u00a77 Preview Mode");
            return null;
        }

        // Easy reference
        ItemStack collectionItem = GOOPCManager.cloneItem(event.getCursor());
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Current?\u00a73 " + OotilityCeption.GetItemName(event.getCurrentItem(), true));
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Cursor?\u00a73 " + OotilityCeption.GetItemName(event.getCursor(), true));

        // Is it the default item? You can't take that
        if (OotilityCeption.IsAirNullAllowed(collectionItem)) {
            //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a7c -\u00a77 Aborting Operation ~ target item is AIR");
            return null;
        }

        // What is the meaning of this?
        CIInteractionSpecs specs = new CIInteractionSpecs(player, event, deployed, observed, rpg);
        boolean affectsRPGInven = specs.affectsRPGInventory(affectsBottom(event));
        boolean affectsContainer = specs.affectsContainer(affectsTop(event));

        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Event Slot \u00a7b#" + event.getSlot() + " \u00a78(Raw\u00a79 #" + event.getRawSlot() + "\u00a78)\u00a77, Used:\u00a79 " + specs.getClickedSlot());
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Affects Container?\u00a73 " + affectsContainer);
        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Affects RPGInvent?\u00a73 " + affectsRPGInven);

        // Should not happen
        if ((!affectsRPGInven && !affectsContainer) || (event.getClickedInventory() == null)) { return null; }

        // Take the item out all right
        ContainersInteractionResult result = new ContainersInteractionResult(null, CIRClickType.TAKE);

        // Choose which slots this can be sent to
        ArrayList<CIInteractingSlot> targetSlots = new ArrayList<>();

        //region Choose which slots qualify for collecting items to cursor
        // The target slots are all those for the player inventory
        if (rpg != null) {
            //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e Adding the slots of the RPG Template. ");

            // Reference base inventory slots (no armor or such)
            for (int i = 0; i < 36; i++) {

                // Skip the one source slot
                if (specs.isClickedBottomInventory()) { if (i == specs.getClickedSlot()) {
                    //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Skipping used slot\u00a76 #" + specs.getClickedSlot());
                    continue; } }

                // Slot target the bottom RPG inventory
                targetSlots.add(knownTarget(rpg, rpg.getTemplate().getSlotAt(i), i, event.getView().getBottomInventory(), event.getView(), null));
            }

        // Target vanilla inventory
        } else {
            //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e Adding the slots of the vanilla player inventory. ");

            // Reference base inventory slots (no armor or such) in the normal left to right order
            for (int i = 0; i < 36; i++) {

                // Skip the one source slot
                if (specs.isClickedBottomInventory()) { if (i == specs.getClickedSlot()) {
                    //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Skipping used slot\u00a76 #" + specs.getClickedSlot());
                    continue; } }

                // Slot target the bottom vanilla inventory
                targetSlots.add(knownTarget(null, null, i, event.getView().getBottomInventory(), event.getView(), null));
            }
        }
        
        // For the crafting view, the player's inventory is enough.
        if (event.getView().getType() != InventoryType.CRAFTING) {
            
            if (observed != null) {
                //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e Adding the slots of the top GooPContainer ");

                // All the slots in that template being seen
                for (int i = 0; i < observed.getTemplate().getTotalSlotCount(); i++) {

                    // Skip the one source slot
                    if (specs.isClickedTopInventory()) { if (i == specs.getClickedSlot()) {
                        //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Skipping used slot\u00a76 #" + specs.getClickedSlot());
                        continue; } }

                    // Slot target the bottom vanilla inventory
                    targetSlots.add(knownTarget(deployed, observed.getTemplate().getSlotAt(i), i, event.getView().getTopInventory(), event.getView(), observed));
                }

            } else {

                //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a7e Adding the slots of the top non-container inventory ");

                // Examine top inventory for stacking success
                for (int i = 0; i < event.getView().getTopInventory().getSize(); i++) {

                    // Collecting to the craft result is not allowed
                    if (event.getView().getType() == InventoryType.WORKBENCH && i == 0) { continue; }

                    // Skip the one source slot
                    if (specs.isClickedTopInventory()) { if (i == specs.getClickedSlot()) {
                        //CLI//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Skipping used slot\u00a76 #" + specs.getClickedSlot());
                        continue; } }

                    // Slot target the bottom vanilla inventory
                    targetSlots.add(knownTarget(null,  null, i, event.getView().getTopInventory(), event.getView(), null));
                }
            }
        }
        //endregion

        // Time to collect-to-cursor as much as possible
        int currentCount = collectionItem.getAmount();
        int maxAmount = collectionItem.getMaxStackSize();
        //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 Collecting to fill\u00a73 " + currentCount + "\u00a77 up to\u00a79 " + maxAmount);

        // Collect (skipping full stacks, then taking from full stacks)
        currentCount = collect(result, targetSlots, player, collectionItem, currentCount, maxAmount, true);
        currentCount = collect(result, targetSlots, player, collectionItem, currentCount, maxAmount, false);

        // Put the remainder in the inventory
        ItemStack finalStored = OotilityCeption.asQuantity(collectionItem, currentCount);
        result.setCursorUpdate(finalStored);

        // Finally
        return result;
    }

    public int collect(@NotNull ContainersInteractionResult result, @NotNull ArrayList<CIInteractingSlot> targetSlots, @NotNull Player player, @NotNull ItemStack collectionItem, int currentCount, int maxAmount, boolean skipFullStacks) {
        int currentCounter = currentCount;
        
        // Go through each interacting slot and attempt to collect items to cursor
        for (CIInteractingSlot interSlot : targetSlots) {
            //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 Trying slot\u00a73 " + interSlot.toString());

            // If you have picked it all up, finish.
            if (currentCounter >= maxAmount) { break; }

            // Item in there
            ItemStack observedInterim = interSlot.getCurrentItem();

            // In this case, the event is allowed to proceed, if the target item is identical to the one being collected
            if (OotilityCeption.IsAirNullAllowed(observedInterim) || !observedInterim.isSimilar(collectionItem) || (observedInterim.getAmount() == maxAmount && skipFullStacks)) {

                //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 This slot does not have similar item. Skip. ");
                continue; }

            //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 Found " + OotilityCeption.GetItemName(observedInterim, true));

            // Restrict containers interaction slots
            if (interSlot instanceof CIContainerInteracting) {

                // Identifying
                RefSimulator<CIInteractingFailReason> interFail = new RefSimulator<>(null);
                GOOPCSlot slotInterim = ((CIContainerInteracting) interSlot).getContainerSlot();

                // Interact
                boolean couldInteract = ContainersClickHandler.canInteract(interSlot, player, null, interFail);

                // Any other forms of failed interaction cause no result.
                if (!couldInteract) {

                    /*
                     * However, CIHPickupAll allows to pickup the item if the fail reason is not meeting
                     * restrictions, it is a storage slot, and its restricted behaviour is take. So...
                     */
                    if (interFail.getValue() != CIInteractingFailReason.RESTRICTIONS_UNMET || !slotInterim.isForStorage() || slotInterim.getRestrictedBehaviour() != RestrictedBehaviour.TAKE) {

                        //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 Cannot take item " + OotilityCeption.GetItemName(interSlot.getCurrentItem()) +  " from here:\u00a7c " + interFail.getValue().toString());
                        continue;
                    }
                }
            }

            // Pickup some
            if (interSlot instanceof CIContainerInteracting) { result.addInteraction((CIContainerInteracting) interSlot, CIRClickType.TAKE); }

            // Now it takes to collect a few items
            int maxCollections = maxAmount - currentCounter;
            //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 Can collect up to " + maxCollections);
            int pickedUp;

            // If can collect it all
            if (maxCollections >= observedInterim.getAmount()) {

                // Take them all
                pickedUp = observedInterim.getAmount();
                currentCounter += pickedUp;
                interSlot.setItem(null);
                //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 Collected \u00a7aall\u00a77 items in this slot\u00a7e -\u00a76 " + pickedUp + "\u00a78 (Current Count\u00a73" + currentCount + "\u00a78)");

                // Can only pick up some
            } else {

                // Pickup as much as you can
                pickedUp = maxCollections;
                currentCounter += pickedUp;
                interSlot.setItem(OotilityCeption.asQuantity(observedInterim, observedInterim.getAmount() - pickedUp));
                //CLI//OotilityCeption.Log("\u00a78CIH \u00a7eCTC\u00a77 Collected \u00a7bsome\u00a77 items from this slot:\u00a76 " + pickedUp + "\u00a78 (Current Count\u00a73" + currentCount + "\u00a78)");
            }
        }
        
        // Edited from
        return currentCounter;
    }
}
