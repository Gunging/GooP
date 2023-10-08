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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * When a player clicks a slot in the container GUI... raw
 * from the InventoryClickEvent, this handler will translate
 * it into a GooP Containers Action.
 */
public abstract class ContainersClickHandler extends ContainersInteractionHandler<InventoryClickEvent> {

    //region Constructor
    /**
     * @return The action that triggered this call
     */
    @NotNull public InventoryAction getAction() { return action; }
    @NotNull InventoryAction action;

    /**
     * @param action To handle this action yeah.
     */
    public ContainersClickHandler(@NotNull InventoryAction action) { this.action = action; }
    //endregion

    /**
     * Assumes you have checked that its not preview mode with {@link GOOPCManager#isUsage_Preview(InventoryView)}
     *
     * @param interactingSlot  Slot being interacted with
     * @param player Player performing the interaction
     * @param item Item performing the interaction
     *
     * @return If this slot can perform its functions
     */
    @Contract("null,_,_,_ -> false")
    public static boolean canInteract(@Nullable CIInteractingSlot interactingSlot, @Nullable Player player, @Nullable ItemStack item, @Nullable RefSimulator<CIInteractingFailReason> fail) {

        // If the slot is null, action is unsupported
        if (interactingSlot == null) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.UNKNOWN_SLOT); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Unknown Slot");
            return false;
        }

        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 " + interactingSlot.getView().getType().toString() + " Inventory Slot \u00a7b#" + interactingSlot.getSlotNumber());

        // That's all the restrictions given to normal slots
        if (!(interactingSlot instanceof CIContainerInteracting)) { return true; }
        CIContainerInteracting containerSlot = (CIContainerInteracting) interactingSlot;

        // Identify slot
        GOOPCSlot slot = containerSlot.getContainerSlot();

        //EVN//OotilityCeption.Log("\u00a78CIH\u00a7a EV\u00a77 Template Slot \u00a7b#" + slot.getSlotNumber());

        // If the player is picking up an edge, the event is cancelled as usual
        if (slot.isForEdge()) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.EDGE_ITEM); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Edge Cancellation");
            return false;
        }

        /*
         * Does the player meet restrictions to use this slot?
         */
        boolean meetsRestrictions = player == null || slot.matchesRestrictions(player);

        // Restrictions unmet? No commands
        if (!meetsRestrictions) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.RESTRICTIONS_UNMET); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Restrictions Unmet");
            return false;
        }

        // If it's display, it shall run commands on click
        if (slot.isForDisplay()) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.DISPLAY_ITEM); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Display Item");
            return false;
        }

        // Is it default from tags?
        boolean tagsDefault = (GOOPCManager.isDefaultItem(interactingSlot.getInventory().getItem(interactingSlot.getSlotNumber())));

        // Not for storage? I don't know what you are.
        if (slot.isForResult()) {

            // Is it the default item? You can't take that
            if (tagsDefault || (containerSlot.getObserved() != null && OotilityCeption.IsAirNullAllowed(containerSlot.getObserved().getLayerEdited().get(slot.getSlotNumber())))) {

                // Fail reason
                if (fail != null) { fail.setValue(CIInteractingFailReason.DEFAULT_ITEM); }

                //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Cannot Drop Default (Tags?\u00a79 " + tagsDefault + "\u00a77) (Edited Layer? " + (containerSlot.getObserved() == null ? "\u00a7cnull obs" : OotilityCeption.GetItemName(containerSlot.getObserved().getLayerEdited().get(slot.getSlotNumber()))) + "\u00a77)");
                return false; }

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.RESULT_SLOT_TYPE); }

            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Result Slot");
            return false; }

        // Not for storage? I don't know what you are.
        if (!slot.isForStorage()) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.UNKNOWN_SLOT_TYPE); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a74 !\u00a77 Unknown Storage Type");
            return false; }

        // Cannot save container bags within container bags :sorrow:
        if (OotilityCeption.ContainsContainerBag(item)) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.CONTAINERCEPTION); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Containerception");
            return false;
        }

        // Not trying to get into book overflow agony
        if (OotilityCeption.ContainsWrittenBook(item)) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.COUNTER_OVERFLOW); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Counter Written Book");
            return false;
        }

        // Storage
        if (!slot.canStore(item)) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.CANNOT_STORE); }

            //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a74 !\u00a77 Cannot store this item");
            return false; }

        // Is it the default item? You can't take that
        if (tagsDefault || (containerSlot.getObserved() != null && containerSlot.getObserved().isDefault(slot.getSlotNumber()))) {

            // Fail reason
            if (fail != null) { fail.setValue(CIInteractingFailReason.DEFAULT_ITEM); }

            //CLI// OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7c -\u00a77 Cannot Drop Default (Tags?\u00a79 " + tagsDefault + "\u00a77) (Storage Layer? " + (containerSlot.getObserved() == null ? "\u00a7cnull obs" : OotilityCeption.GetItemName(containerSlot.getObserved().getLayerStorage().get(slot.getSlotNumber()))) + "\u00a77)");
            return false; }

        // Allow
        //CLI//OotilityCeption.Log("\u00a78CONTAINERS \u00a7eDAS\u00a7a !\u00a77 Success");
        return true;
    }

    /**
     * @param deployed Forces this slot to become of type {@link CIContainerInteracting}, by providing
     *                 which container is interacting.
     *
     * @param eventSlot Slot number as per the vanilla inventory ordering, or the container template.
     *
     * @param clickedInventory Inventory that allows {@link Inventory#getItem(int)} where int is the
     *                         event slot, for slots not of type {@link CIContainerInteracting}.
     *
     * @param view Inventory view for better information.
     *
     * @param observed Container Inventory to retrieve items when the interaction is {@link CIContainerInteracting}.
     *
     * @return Built Interacting slot.
     *
     * @see #eventTarget(GOOPCDeployed, int, Inventory, InventoryView, ContainerInventory)
     */
    @Nullable public static CIInteractingSlot eventTarget(@Nullable GOOPCDeployed deployed, int eventSlot, @NotNull Inventory clickedInventory, @NotNull InventoryView view, @Nullable ContainerInventory observed) {

        // Identify slot being interacted with
        GOOPCSlot slot = deployed != null ? deployed.getTemplate().getSlotAt( (deployed instanceof GOOPCPlayer) ? GOOPCPlayer.eventToContinuous(eventSlot) : eventSlot) : null;

        // Now you know the target
        return knownTarget(deployed, slot, eventSlot, clickedInventory, view, observed);
    }

    /**
     * @param deployed Forces this slot to become of type {@link CIContainerInteracting}, by providing
     *                 which container is interacting.
     *
     * @param knownSlot Required for it to completely be {@link CIContainerInteracting}, if null but a
     *                  deployed container was specified, this method will return null.
     *
     * @param eventSlot Slot number as per the vanilla inventory ordering, or the container template.
     *
     * @param clickedInventory Inventory that allows {@link Inventory#getItem(int)} where int is the
     *                         event slot, for slots not of type {@link CIContainerInteracting}.
     *
     * @param view Inventory view for better information.
     *
     * @param observed Container Inventory to retrieve items when the interaction is {@link CIContainerInteracting}.
     *
     * @return Built Interacting slot.
     */
    @Contract("!null,null,_,_,_,_->null;!null,!null,_,_,_,_->!null;null,_,_,_,_,_->!null")
    @Nullable public static CIInteractingSlot knownTarget(@Nullable GOOPCDeployed deployed, @Nullable GOOPCSlot knownSlot, int eventSlot, @NotNull Inventory clickedInventory, @NotNull InventoryView view, @Nullable ContainerInventory observed) {
        //KT//OotilityCeption.Log("\u00a78CCH\u00a72 KT\u00a77 Deployed\u00a73 " + (deployed == null ?  "\u00a7cnull" : deployed.getTemplate().getInternalName()));
        //KT//OotilityCeption.Log("\u00a78CCH\u00a72 KT\u00a77 Tmp Slot\u00a73 " + (knownSlot == null ? "\u00a7cnull" : "#" + knownSlot.getSlotNumber()));
        //KT//OotilityCeption.Log("\u00a78CCH\u00a72 KT\u00a77 Observed\u00a73 " + (observed == null ? "\u00a7cnull" : observed.getInstanceID()));

        // Invalid index
        //if (eventSlot < 0 || eventSlot >= clickedInventory.getSize()) { return null; }

        // No container no service
        if (deployed == null) {
            //KT//OotilityCeption.Log("\u00a78CCH\u00a72 KT\u00a7a //\u00a77 No deployed, not container. ");

            // Just that
            return new CIInteractingSlot(eventSlot, clickedInventory, view);

        // All right lets try the container
        } else {

            if (knownSlot != null) {
                //KT//OotilityCeption.Log("\u00a78CCH\u00a72 KT\u00a7a //\u00a77 Known slot, container. ");

                // Build slot
                return new CIContainerInteracting(eventSlot, clickedInventory, view, deployed.getTemplate(), knownSlot, observed);
            }

            // Invalid slot
            //KT//OotilityCeption.Log("\u00a78CCH\u00a72 KT\u00a7c //\u00a77 Invalid Slot");
            return null;
        }
    }
}
