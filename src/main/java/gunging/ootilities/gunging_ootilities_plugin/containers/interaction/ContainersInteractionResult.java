package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Apart from saving the items into the file (which happens in {@link ContainersInteractionHandler})
 *
 * Sometimes, commands must be run, and the inventory rebuilt. This has the information
 * on which commands to run and which slots to send updated information for.
 */
public class ContainersInteractionResult {

    /**
     * @param slot Slot interaction
     */
    public ContainersInteractionResult(@Nullable CIInteractingSlot slot, @NotNull CIRClickType which) {
        if (slot instanceof CIContainerInteracting) { addInteraction((CIContainerInteracting) slot, which); }
    }

    /**
     * @return Commands that will run the tick immediately after
     *         the consummation of this action. This is because
     *         java still thinks that the item being moved is in
     *         its previous position from what is intuitive.
     */
    @NotNull public ArrayList<String> getCommands() { return commands; }
    @NotNull final ArrayList<String> commands = new ArrayList<>();
    /**
     * @param commands Command to set
     */
    public void setCommands(@NotNull ArrayList<String> commands) { clearCommands(); addCommands(commands); }
    /**
     * @param command Command to add.
     */
    public void addCommand(@NotNull String command) { commands.add(command); }
    /**
     * @param commands Commands to add.
     */
    public void addCommands(@NotNull ArrayList<String> commands) { this.commands.addAll(commands); }
    /**
     * @param command Command to add.
     * @param providedSlot Slot number being affected
     * @param key Slot key of the affected slot
     */
    public void addCommand(@NotNull String command, int providedSlot, @NotNull String key) { commands.add(command.replace("%provided-slot%", key + providedSlot)); }
    /**
     * @param commands Commands to add.
     * @param providedSlot Slot number being affected
     * @param key Slot key of the affected slot
     */
    public void addCommands(@NotNull ArrayList<String> commands, int providedSlot, @NotNull String key) { for (String cmm : commands) { if (cmm == null) { continue; } addCommand(cmm, providedSlot, key); } }
    /**
     * @param commands Commands to add.
     * @param providedSlot Slot number being affected
     * @param fromPlayerInven If the slot was clicked in the normal inventory, otherwise assumed to be in the observed container.
     */
    public void addCommands(@NotNull ArrayList<String> commands, int providedSlot, boolean fromPlayerInven) { for (String cmm : commands) { if (cmm == null) { continue; } addCommand(cmm, providedSlot, fromPlayerInven ? "" : "c"); } }
    /**
     * @param interacting Display slot interaction
     * @param which On Click, On Store, or On Take, commands?
     *              <b>0 = On Click</b>, <b>1 = On Store</b>, <b>2 = On Take</b>
     */
    public void addCommands(@NotNull CIContainerInteracting interacting, @NotNull CIRClickType which) {
        switch (which) {
            case CLICK: addCommands(interacting.getContainerSlot().getCommandsOnClick(), interacting.getContainerSlot().getSlotNumber(), interacting.getTemplate().isPlayer()); break;
            case STORE: addCommands(interacting.getContainerSlot().getCommandsOnStore(), interacting.getContainerSlot().getSlotNumber(), interacting.getTemplate().isPlayer()); break;
            case TAKE: addCommands(interacting.getContainerSlot().getCommandsOnTake(), interacting.getContainerSlot().getSlotNumber(), interacting.getTemplate().isPlayer()); break;
            case SWAP:
                addCommands(interacting.getContainerSlot().getCommandsOnTake(), interacting.getContainerSlot().getSlotNumber(), interacting.getTemplate().isPlayer());
                addCommands(interacting.getContainerSlot().getCommandsOnStore(), interacting.getContainerSlot().getSlotNumber(), interacting.getTemplate().isPlayer());
                break;
        }
    }
    /**
     * Clear the currently registered commands.
     */
    public void clearCommands() { commands.clear(); }

    /**
     * @return If the GOOPCListener class shall not update the slot
     */
    public boolean isPreventEquipmentUpdate() { return preventEquipmentUpdate; }
    /**
     * MAKE SURE TO KNOW WHAT YOU'RE DOING!
     * <p></p>
     * This is only used by {@link CIHDropAllSlot} or {@link CIHDropOneSlot} because
     * they allow the drop event to run to completion, while the other interaction
     * handlers usually override the entire pickup event.
     *
     * @param prevent If the GOOPCListener class shall not update the slot
     */
    public void setPreventEquipmentUpdate(boolean prevent) { preventEquipmentUpdate = prevent; }
    boolean preventEquipmentUpdate;

    /**
     * @return The slots that will suffer an inventory update
     *         being sent to the player, basically all the slots
     *         that had any changes made to the item they hold.
     */
    @NotNull public ArrayList<Integer> getSlotsUpdate() { return slotsUpdate; }
    @NotNull final ArrayList<Integer> slotsUpdate = new ArrayList<>();
    /**
     * @param slot Slot to add.
     */
    public void addSlotUpdate(@NotNull Integer slot) { slotsUpdate.add(slot); }
    /**
     * @param slots Slots to add.
     */
    public void addSlotsUpdate(@NotNull ArrayList<Integer> slots) { this.slotsUpdate.addAll(slots); }

    /**
     * @return Slots to check that, if they are AIR, they should instead have
     *         a default item per the RPG Inventory of the player.
     */
    @NotNull public ArrayList<CIContainerInteracting> getPlayerSlotsUpdate() { return playerSlotsUpdate; }
    @NotNull final ArrayList<CIContainerInteracting> playerSlotsUpdate = new ArrayList<>();
    /**
     * @param slot Slot to add.
     */
    public void addPlayerSlotUpdate(@NotNull CIContainerInteracting slot) { playerSlotsUpdate.add(slot); }
    /**
     * @param slots Slots to add.
     */
    public void addPlayerSlotsUpdate(@NotNull ArrayList<CIContainerInteracting> slots) { this.playerSlotsUpdate.addAll(slots); }

    /**
     * @param interacting Slot interaction
     * @param which On Click, On Store, or On Take, commands?
     *              <b>0 = On Click</b>, <b>1 = On Store</b>, <b>2 = On Take</b>
     */
    public void addInteraction(@NotNull CIContainerInteracting interacting, @NotNull CIRClickType which) {

        // Slot Update
        if (!interacting.getTemplate().isPlayer()) {

            // Apparently only if its not in the player's inventory
            addSlotUpdate(interacting.getContainerSlot().getSlotNumber());


        // Check default item if the item is being taken
        } else if (which == CIRClickType.TAKE) {

            // Well now it does check it!
            addPlayerSlotUpdate(interacting);
        }

        // Commands of interaction
        addCommands(interacting, which);
    }

    /**
     /**
     * @return Cursor replacement if {@link #isCursorUpdate()} is true
     */
    @Nullable public ItemStack getEditedCursor() { return editedCursor; }
    @Nullable ItemStack editedCursor;

    /**
     * @return If the cursor must be forcibly updated next frame
     */
    public boolean isCursorUpdate() { return cursorUpdate; }
    boolean cursorUpdate = false;

    /**
     * @return If the cursor was updated this frame instead
     */
    public boolean isCursorUpdateResolved() { return cursorUpdateResolution; }
    /**
     * @param cursorUpdateResolution If the cursor was updated this frame instead
     */
    public void setCursorUpdateResolved(boolean cursorUpdateResolution) { this.cursorUpdateResolution = cursorUpdateResolution; }
    boolean cursorUpdateResolution = false;

    /**
     * @param editedCursor ItemStack to replace cursor soon
     */
    public void setCursorUpdate(@Nullable ItemStack editedCursor) { cursorUpdate = true; this.editedCursor = editedCursor; }
}
