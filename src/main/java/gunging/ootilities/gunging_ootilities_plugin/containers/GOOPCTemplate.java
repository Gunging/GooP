package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.containers.options.*;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * It stores the layout of a container, slot per slot.
 */
public class GOOPCTemplate {

    //region Utility Template Methods
    /**
     * Will create a valid HashMap of Integers and Container Slots give the slots.
     *
     * @param slots If any have a repeated index, the second and subsequent will be ignored.
     * @param height Number of rows you expect the container to have (Between 1 and 6). Slots that overflow are ignored.
     *
     * @return A continuous HashMap with ContainerSlots and NULL values from ZERO to (HEIGHT * 9 - 1)
     */
    public static HashMap<Integer, GOOPCSlot> buildValidSlotsContent(@NotNull ArrayList<GOOPCSlot> slots, int height) {
        // Creates a HashMap, and fills the whole thing
        HashMap<Integer, GOOPCSlot> ret = new HashMap<>();

        //SRZ//OotilityCeption. Log("\u00a78GCT\u00a73 DZC\u00a77 Refining \u00a7ex" + slots.size() + "\u00a77 to be of height \u00a73" + height);

        // Fill
        for (int i = 0; i < height * 9; i++) {

            // Put null entry
            ret.put(i, null);
        }
        //SRZ//OotilityCeption. Log("\u00a78GCT\u00a73 DZC\u00a77 Filled base with \u00a73" + ret.size() + "\u00a77 null entries.");

        // Edit with slots
        for (GOOPCSlot slot : slots) {

            // If Slot non-null
            if (slot != null) {

                // Add it if contained
                if (ret.containsKey(slot.getSlotNumber())) {

                    //SRZ//OotilityCeption. Log("\u00a78GCT\u00a73 DZC\u00a77 Put at \u00a7e#" + slot.getSlotNumber() + "\u00a77 slot\u00a7f " + GOOPCSlot.serialize(slot));

                    // If still null, set
                    ret.putIfAbsent(slot.getSlotNumber(), slot);
                }
                //SRZ// else { OotilityCeption. Log("\u00a78GCT\u00a73 DZC\u00a77 Ignored at \u00a7e#" + slot.getSlotNumber() + "\u00a77 slot\u00a7f " + GOOPCSlot.serialize(slot)); }
            }
        }

        // Return result
        return ret;
    }

    /**
     * Ah yes don't you love brute forcing it instead of writing a proper mathematical 'IsDivisibleByNine()' method?
     *
     * @param number The number that may be an acceptable total slots number
     *
     * @return If this is a slots number supported in minecraft inventories.
     */
    public static boolean isValidMultipleOfNine(int number) {
        return (number == 9 ||
                number == 18 ||
                number == 27 ||
                number == 36 ||
                number == 45 ||
                number == 54);
    }

    /**
     * @return The deployed manager for this template.
     */
    @NotNull public GOOPCDeployed getDeployed() { return deployed; }
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NotNull GOOPCDeployed deployed;
    /**
     * Link the deployed container to this template.
     *
     * @param deployed The deployed container manager thing.
     */
    protected void setDeployed(@NotNull GOOPCDeployed deployed) { this.deployed = deployed; }
    //endregion

    //region Container Edge Engine
    /**
     * Lower bound of CustomModelData for Edge Formations
     */
    @NotNull public static final String EDGE_ENCRYPTION_CODE = "Container Edge";

    /**
     * The legendary edge material that turns into Container Edges
     */
    @NotNull public static final ItemStack CONTAINER_EDGE_MATERIAL = OotilityCeption.NameEncrypt(new ItemStack(Material.GRAY_STAINED_GLASS), EDGE_ENCRYPTION_CODE);

    /**
     * Lower bound of CustomModelData for Edge Formations
     */
    public static final int EDGE_FORMATIONS_CMD_START = 2985;

    /**
     * @return If each edge receives a specific Custom Model Data
     *         depending on the edges adjacent and diagonal to it.
     */
    public boolean isEdgeFormations() { return edgeFormations; }
    boolean edgeFormations;
    /**
     * @param edgeFormations If each edge receives a specific Custom Model Data
     *         depending on the edges adjacent and diagonal to it.
     */
    public void setEdgeFormations(boolean edgeFormations) { this.edgeFormations = edgeFormations; }

    /**
     * @param slot Slot of the current, target edge.
     *
     * @return If there is another edge above this edge.
     */
    boolean mainlandNorth(int slot) {
        // Gets the slot above
        int tSlot = slot - 9;

        // If it makes sense
        if (tSlot >= 0) {

            // Is there any item at all in that slot?
            GOOPCSlot targetSlot = slotsContent.get(tSlot);

            // Null means AIR. Thus no Edge.
            if (targetSlot == null) return false;

            // WIll be true if the target slot is an edge
            return targetSlot.isForEdge();

        } else {

            // Nothing above means it is on the top edge so actually YES it has mainland.
            return true;
        }
    }
    /**
     * @param slot Slot of the current, target edge.
     *
     * @return If there is another edge below this edge.
     */
    boolean mainlandSouth(int slot) {
        // Gets the slot above
        int tSlot = slot + 9;

        // If it makes sense
        if (tSlot < slotsContent.size()) {

            // Is there any item at all in that slot?
            GOOPCSlot targetSlot = slotsContent.get(tSlot);

            // Null means AIR. Thus no Edge.
            if (targetSlot == null) return false;

            // WIll be true if the target slot is an edge
            return targetSlot.isForEdge();

        } else {

            // Nothing below means it is on the bottom edge so actually YES it has mainland.
            return true;
        }
    }
    /**
     * @param slot Slot of the current, target edge.
     *
     * @return If there is another edge next to this edge.
     */
    boolean mainlandEast(int slot) {
        // Does it happen to not be one of the very last?
        if (!isValidMultipleOfNine(slot + 1)) {

            // Gets immediate next slot
            GOOPCSlot targetSlot = slotsContent.get(slot + 1);

            // Null means AIR. Thus no Edge.
            if (targetSlot == null) return false;

            // WIll be true if the target slot is an edge
            return targetSlot.isForEdge();

        } else {

            // Being of the very last means it has mainland.
            return true;
        }
    }
    /**
     * @param slot Slot of the current, target edge.
     *
     * @return If there is another edge next to this edge.
     */
    boolean mainlandWest(int slot) {
        // Does it happen to not be one of the very first?
        if (!isValidMultipleOfNine(slot + 9)) {

            // Gets immediate previous slot
            GOOPCSlot targetSlot = slotsContent.get(slot - 1);

            // Null means AIR. Thus no Edge.
            if (targetSlot == null) return false;

            // WIll be true if the target slot is an edge
            return targetSlot.isForEdge();

        } else {

            // Being of the very first means it has mainland.
            return true;
        }
    }
    /**
     * @param northM Is there another edge above?
     * @param eastM Is there another edge to the right?
     * @param southM Is there another edge below?
     * @param westM Is there another edge to the left?
     *
     * @return The kind of edge if it has these surrounding edge types.
     */
    @NotNull ContainerSlotEdges formEdge(boolean northM, boolean eastM, boolean southM, boolean westM) {

        // Well lets begin in the same order as the array
        if (westM) {

            // Does it also have North?
            if (northM) {

                // Well does it have east?
                if (eastM) {

                    // Would it also have south?
                    if (southM) {

                        // Then it has West, North, East AND South so it is a full mainland.
                        return ContainerSlotEdges.MAINLAND;

                        // So everything except SOUTH
                    } else {

                        // Then it is has Shore Southwards
                        return ContainerSlotEdges.WM_SOUTHSHORE;
                    }

                    // Could it be a West-South-North?
                } else if (southM) {

                    // Indeed, so it only has shore Eastward
                    return ContainerSlotEdges.WM_EASTSHORE;

                    // Only North and West
                } else {

                    // It is a corner that starts in west and turns northward
                    return ContainerSlotEdges.WM_NORTHCORNER;
                }

                // I guess no north, what about East?
            } else if (eastM) {

                // Well does it happen to also have south?
                if (southM) {

                    // Then it only missing NORTH
                    return ContainerSlotEdges.WM_NORTHSHORE;

                    // Nope, only East and West
                } else {

                    // Its a bridge thus
                    return ContainerSlotEdges.WM_BRIDGE;
                }

                // What about south?
            } else if (southM) {

                // Both West and South
                return ContainerSlotEdges.WM_SOUTHCORNER;

                // Neither of those
            } else {

                // Only West Shore
                return ContainerSlotEdges.WM_PENNINSULA;
            }

            // No West well what about North?
        } else if (northM) {

            // Does it have East Mainland?
            if (eastM) {

                // Does it also happen to ahve south?
                if (southM) {

                    // Then it has everything but West.
                    return ContainerSlotEdges.NM_WESTSHORE;

                    // No, only North and East
                } else {

                    // Thus its an EastCorner
                    return ContainerSlotEdges.NM_EASTCORNER;
                }

                // Could it be a North-South?
            } else if (southM) {

                // Then it is a bridge
                return ContainerSlotEdges.NM_BRIDGE;

                // Nothing else but North
            } else {

                // Then its a peninsula.
                return ContainerSlotEdges.NM_PENNINSULA;
            }

            // Missing North Mainland Too
        } else if (eastM) {

            // Does it have south?
            if (southM) {

                // A South corner thus
                return ContainerSlotEdges.EM_SOUTHCORNER;

                // Nope, only East thus
            } else {

                // Peninsula
                return ContainerSlotEdges.EM_PENNINSULA;
            }

            // ALso no East Mainland
        } else if (southM) {

            // Then it only has south
            return ContainerSlotEdges.SM_PENNINSULA;

            // No South Mainland Either
        } else {

            // No mainland at all. It is an Island
            return ContainerSlotEdges.ISLAND;
        }
    }

    /**
     * Actually builds the Edge Slots Contents
     */
    public void processContentEdges() {
        // Something regarding walls
        ArrayList<GOOPCSlot> cEdges = new ArrayList<>();
        resultSlot = null;

        // Iterates for every slot:
        for (GOOPCSlot slot : slotsContent.values()) {
            if (slot == null) { continue; }

            // Edge Slot
            if (slot.isForEdge()) { cEdges.add(slot); }

            // Result slot
            if (slot.isForResult()) { resultSlot = slot; }
        }

        //EDGE//OotilityCeption. Log(" \u00a7a~\u00a72~\u00a7a~ \u00a77All edges accounted for.");
        // Bruh I guess it identifies the edges?
        for (GOOPCSlot edge : cEdges) {
            //EDGE//OotilityCeption. Log(" \u00a7a>\u00a78> \u00a77Looking at \u00a7f#" + edge.getSlotNumber());

            // Type of Edge
            ContainerSlotEdges type = ContainerSlotEdges.MAINLAND;

            // Gets the type of edge it will have
            if (isEdgeFormations()) {
                //EDGE//OotilityCeption. Log(" \u00a7a>\u00a78> \u00a77Processing advanced edges...");
                type = formEdge(mainlandNorth(edge.slotNumber), mainlandEast(edge.slotNumber), mainlandSouth(edge.slotNumber), mainlandWest(edge.slotNumber));
            }

            // Assigns it
            //EDGE//OotilityCeption. Log(" \u00a7a>\u00a78> \u00a72Identified as \u00a7a" + type.toString());
            edge.setEdgeFormation(type);
        }
    }
    //endregion

    //region Result Slot
    /**
     * @return The custom mythiclib recipe string, or null if its for the workbench.
     */
    @Nullable public String getCustomMythicLibRecipe() { return customMythicLibRecipe; }
    @Nullable String customMythicLibRecipe;
    /**
     * @param recipe The custom mythiclib recipe string, or null if its for the workbench.
     */
    public void setCustomMythicLibRecipe(@Nullable String recipe) { customMythicLibRecipe = recipe; }

    /**
     * See {@link #processContentEdges()} for the method that finds this
     * result slot, and ignores the rest of the result slots.
     *
     * @return The result slot if this is treated as a crafting station.
     */
    @Nullable public GOOPCSlot getResultSlot() { return resultSlot; }
    @Nullable GOOPCSlot resultSlot;

    /**
     * @return If drag events are allowed in this container
     */
    public boolean isAllowDragEvents() { return allowDragEvents; }
    boolean allowDragEvents = true;
    /**
     * @param allow If drag events are allowed in this container
     */
    public void setAllowDragEvents(boolean allow) { allowDragEvents = allow; }

    /**
     * @return If provided slot includes inventory slots when extended by drag
     */
    public boolean isProvidedDragExtendsToInventory() { return allowDragEvents; }
    boolean providedDragExtendsToInventory = false;
    /**
     * @param extend If provided slot includes inventory slots when extended by drag
     */
    public void setProvidedDragExtendsToInventory(boolean extend) { providedDragExtendsToInventory = extend; }
    //endregion

    //region Constructor
    /**
     * @return Container Template ID
     */
    public long getInternalID() { return internalID; }
    long internalID = -1;
    public void setInternalID(long internalID) { this.internalID = internalID; }
    /**
     * @return Container Template Name
     */
    @NotNull public String getInternalName() { return internalName; }
    @NotNull String internalName;
    /**
     * @return Container Type
     */
    @NotNull public ContainerTypes getContainerType() { return containerType; }
    ContainerTypes containerType;
    /**
     * @return If the container is physical
     */
    public boolean isPhysical() { return getContainerType() == ContainerTypes.PHYSICAL; }
    /**
     * @return If the container is personal
     */
    public boolean isPersonal() { return getContainerType() == ContainerTypes.PERSONAL; }
    /**
     * @return If the container is station
     */
    public boolean isStation() { return getContainerType() == ContainerTypes.STATION; }
    /**
     * @return If the container is player inventory override
     */
    public boolean isPlayer() { return getContainerType() == ContainerTypes.PLAYER; }

    /**
     * @return The number of slots in this container
     */
    public int getTotalSlotCount() { return getHeight() * 9; }
    /**
     * @return The number of rows in this container
     */
    public int getHeight() { return height; }
    int height;
    /**
     * Change the height of the container. Note that this will delete slots content
     * that overflow the container under the new size.
     *
     * @param rowsNumber A number of the following: 1, 2, 3, 4, 5, or 6
     */
    public void setHeight(int rowsNumber) {

        // This should really ALWAYS be true
        if (rowsNumber >= 1 && rowsNumber <= 6) {

            // New content
            HashMap<Integer, GOOPCSlot> rebuiltRows = buildValidSlotsContent(new ArrayList<>(slotsContent.values()), rowsNumber);

            // Store New Contents I Guess
            height = (int) Math.round(rebuiltRows.size() / 9.0);
            slotsContent = rebuiltRows;
            processContentEdges();
        }
    }

    /**
     * @return The slot pattern itself
     */
    @NotNull public HashMap<Integer, GOOPCSlot> getSlotsContent() { return slotsContent; }
    @NotNull HashMap<Integer, GOOPCSlot> slotsContent;
    @Nullable public GOOPCSlot getSlotAt(@Nullable Integer i) { return slotsContent.get(i); }
    public void loadSlotsContent(@NotNull HashMap<Integer, GOOPCSlot> contents) {
        slotsContent.clear();
        for (int i = 0; i < getTotalSlotCount(); i++) { slotsContent.put(i, contents.get(i)); }
    }

    /**
     * This is the player-friendly title, it may have color codes too.
     *
     * @return The title to display to the players.
     */
    @NotNull public String getTitle() { return title; }
    @NotNull String title;
    /**
     * Causes the encoded title to regenerate next time the method
     * to get it is called, too.
     *
     * @param newTitle The new title to display to players.
     */
    public void setTitle(@NotNull String newTitle) { title = newTitle; encodedTitle = null; }

    /**
     * Create a container template
     *
     * @param internalName The internal name of this container.
     * @param slotContents Starting slot configuration.
     * @param storageType Type of container.
     * @param displayTitle Friendly title displayed to players.
     * @param edgeFormations Advanced CustomModelData for Texturing of Edges?
     *
     * @throws IllegalArgumentException If the size of the array is not a multiple of 9, is less than 9, or is greater than 54.
     */
    public GOOPCTemplate(@NotNull String internalName, @NotNull HashMap<Integer, GOOPCSlot> slotContents, @NotNull ContainerTypes storageType, @NotNull String displayTitle, boolean edgeFormations) throws IllegalArgumentException {
        //Gunging_Ootilities_Plugin.theOots.C Log("Creating GooP Container with \u00a7e" + contents.size() + "\u00a77 slots, and of name " + title);

        // If it is a multiple of 9 within 5 and 60 (inclusive btw [the way I worded this is going to annoy someone someday])
        if (isValidMultipleOfNine(slotContents.size())) {

            // Store height
            height = (int) Math.round(slotContents.size() / 9.0);
            //Gunging_Ootilities_Plugin.theOots.C Log("Determined height to be \u00a7e" + height + "\u00a77 (\u00a73" + getTotalSlotCount() + "\u00a77)");

            // Set contents
            slotsContent = slotContents;
            this.edgeFormations = edgeFormations;

            // Set title
            this.title = displayTitle;
            this.internalName = internalName;
            containerType = storageType;

            // Processes Content Edges
            //EDGE//OotilityCeption. Log(" \u00a7a~\u00a72~\u00a7a~ \u00a77Processing Edges of \u00a7f" + template + " \u00a7a~\u00a72~\u00a7a~");
            processContentEdges();

        } else {

            // Throw exception
            throw new IllegalArgumentException("[GooP Containers] Container Template attempted to be created with " + slotContents.size() + " slots.");
        }
    }

    /**
     * @return Container Template ID hidden among section signs.
     */
    @NotNull public String getEncodedID() {
        if (encodedID != null) { return encodedID; }

        // Convert to string
        String stringID = String.valueOf(internalID);
        StringBuilder ret = new StringBuilder();

        // Add Section Signs between its characters
        for (char c : stringID.toCharArray()) { ret.append("\u00a7").append(c); }

        // Save it and return it
        encodedID = ret.toString();
        return encodedID;
    }
    String encodedID = null;
    /**
     * @return Title wrapped by identifier variables.
     */
    @NotNull public String getEncodedTitle() {
        // If exists, return it.
        if (encodedTitle != null) { return encodedTitle; }

        // Otherwise, bake it, store it, and return it.
        encodedTitle = GOOPCManager.TITLE_ID_OPEN + getEncodedID() + GOOPCManager.TITLE_ID_CLOSE + getTitle();
        return encodedTitle;
    }
    String encodedTitle = null;
    /**
     * @param instanceID For personal containers, OID; For physical containers, LID; For station containers, null.
     *
     * @param type Type of encoding.
     *
     * @return Title succeeded by identifier variables.
     */
    @NotNull public String getEncodedTitle(@Nullable String instanceID, @NotNull ContainerTypes type) {

        // Is it a station container?
        if (instanceID == null) {
            return getEncodedTitle();

        // Which kinda container, then?
        } else {

            // Use special notation
            switch (type) {
                case PHYSICAL:
                    return getEncodedTitle() + GOOPCManager.PHYS_ID_OPEN + OotilityCeption.IntermitentEncode(instanceID, false) + GOOPCManager.PHYS_ID_CLOSE;

                case PERSONAL:
                    return getEncodedTitle() + GOOPCManager.PERS_ID_OPEN + OotilityCeption.IntermitentEncode(instanceID, false) + GOOPCManager.PERS_ID_CLOSE;

                default:
                    return getEncodedTitle();
            }
        }
    }
    //endregion

    //region Equipment
    /**
     * @return The slots that count as equipment.
     */
    @NotNull public ArrayList<Integer> getEquipmentSlots() { return equipmentSlots; }
    @NotNull final ArrayList<Integer> equipmentSlots = new ArrayList<>();
    public void refreshEquipmentSlots() {

        // Add I guess
        equipmentSlots.clear();
        for (GOOPCSlot c : slotsContent.values()) {

            // Add if for equipment
            if (c != null) { if (c.isForEquipment()) { equipmentSlots.add(c.getSlotNumber()); } }
        }
    }

    /**
     * @return If multiple items will be counted twice
     */
    public boolean isAllowDuplicateEquipment() { return allowDuplicateEquipment; }
    boolean allowDuplicateEquipment = true;
    /**
     * @param allow If multiple items will be counted twice
     */
    public void setAllowDuplicateEquipment(boolean allow) { allowDuplicateEquipment = allow; }
    //endregion

    //region Aliases
    /**
     * @return What a slot alias represents
     */
    @NotNull public HashMap<String, ArrayList<Integer>> getAliasMeanings() { return aliasMeanings; }
    @NotNull final HashMap<String, ArrayList<Integer>> aliasMeanings = new HashMap<>();
    /**
     * Rebuilds the aliases map
     */
    public void reloadAliases() {

        // New thing
        aliasMeanings.clear();

        // For every content
        for (GOOPCSlot cSlot : slotsContent.values()) {

            // If exists
            if (cSlot != null) {

                // Does it have an optional identifier?
                if (cSlot.hasAlias()) {

                    // Verify that its entry is not null
                    if (!aliasMeanings.containsKey(cSlot.getAlias())) {
                        aliasMeanings.put(cSlot.getAlias(), new ArrayList<>()); }

                    // Add a reference to oneself
                    aliasMeanings.get(cSlot.getAlias()).add(cSlot.getSlotNumber());
                }
            }
        }
    }

    /**
     * @param alias Alias name
     * @return The slots associated with this alias
     */
    @NotNull public ArrayList<Integer> getAliasSlots(@Nullable String alias) {

        // If contained
        if (aliasMeanings.containsKey(alias)) {

            // Make sure non-null
            aliasMeanings.computeIfAbsent(alias, k -> new ArrayList<>());

            // Return thay
            return aliasMeanings.get(alias);

        } else {

            // Empty list but not null
            return new ArrayList<>();
        }
    }
    //endregion

    //region Slot Commands
    /**
     * @return The commands that run when the player closes the container.
     */
    public @NotNull ArrayList<String> getCommandsOnClose() { return commandsOnClose; }
    @NotNull ArrayList<String> commandsOnClose = new ArrayList<>();
    /**
     * @param command The command that runs when the player closes the container.
     */
    public void setCommandsOnClose(@Nullable String command) {

        // Clear current commands
        commandsOnClose.clear();
        if (command == null || command.isEmpty()) { return; }

        commandsOnClose.add(command);
    }
    /**
     * @param commands The commands that run when the player closes the container.
     */
    public void setCommandsOnClose(@Nullable ArrayList<String> commands) {

        // Clear current commands
        commandsOnClose.clear();
        addCommandsOnClose(commands);
    }
    /**
     * @param command The command that runs when the player closes the container.
     */
    public void addCommandsOnClose(@Nullable String command) {

        // Ignore
        if (command == null || command.isEmpty()) { return; }

        commandsOnClose.add(command);
    }
    /**
     * @param commands The commands that run when the player closes the container.
     */
    public void addCommandsOnClose(@Nullable ArrayList<String> commands) {

        // Ignore
        if (commands == null || commands.size() == 0) { return; }
        for (String cmd : commands) { if (cmd == null || cmd.isEmpty()) { continue; } commandsOnClose.add(cmd); }
    }
    /**
     * @return If the container runs a command when the inventory is closed.
     */
    public boolean hasCommandsOnClose() { return commandsOnClose.size() > 0; }
    /**
     * Run the Command on Close
     *
     * @param closer Player who closed their inventories. For PAPI parsing.
     */
    public void executeCommandsOnClose(@NotNull OfflinePlayer closer) {

        // Send every command
        for (String commandOnClose : getCommandsOnClose()) {
            if (commandOnClose == null) { continue; }

            // Run it lmao
            OotilityCeption.SendConsoleCommand(commandOnClose, closer.getPlayer(), closer, null, null);
        }
    }

    /**
     * @return The command that runs when the player opens the container.
     */
    @NotNull public ArrayList<String> getCommandsOnOpen() { return commandsOnOpen; }
    @NotNull ArrayList<String> commandsOnOpen = new ArrayList<>();
    /**
     * @param command The command that runs when the player opens the container.
     */
    public void setCommandsOnOpen(@Nullable String command) {

        // Clear current commands
        commandsOnOpen.clear();
        if (command == null || command.isEmpty()) { return; }

        commandsOnOpen.add(command);
    }
    /**
     * @param commands The commands that run when the player opens the container.
     */
    public void setCommandsOnOpen(@Nullable ArrayList<String> commands) {

        // Clear current commands
        commandsOnOpen.clear();
        addCommandsOnOpen(commands);
    }
    /**
     * @param command The command that runs when the player opens the container.
     */
    public void addCommandsOnOpen(@Nullable String command) {

        // Ignore
        if (command == null || command.isEmpty()) { return; }

        commandsOnOpen.add(command);
    }
    /**
     * @param commands The commands that run when the player opens the container.
     */
    public void addCommandsOnOpen(@Nullable ArrayList<String> commands) {

        // Ignore
        if (commands == null) { return; }
        for (String cmd : commands) { if (cmd == null || cmd.isEmpty()) { continue; } commandsOnOpen.add(cmd); }
    }
    /**
     * @return If the container runs a command when the inventory is opened.
     */
    public boolean hasCommandsOnOpen() { return commandsOnOpen.size() > 0; }
    /**
     * Run the Command on open
     *
     * @param opener Player who closed their inventories. For PAPI parsing.
     */
    public void executeCommandsOnOpen(@NotNull OfflinePlayer opener) {

        for (String commandOnOpen : getCommandsOnOpen()) {
            if (commandOnOpen == null) { continue; }

            // Run it lmao
            OotilityCeption.SendConsoleCommand(commandOnOpen, opener.getPlayer(), opener, null, null);
        }
    }
    //endregion

    //region Slot Types
    /**
     * @param slot Slot you inquire about
     *
     * @return If this slot allows players to put items into itself
     */
    public boolean isStorageSlot(@Nullable Integer slot) {
        GOOPCSlot containerSlot = getSlotAt(slot);
        if (containerSlot == null) { return false; }

        // Is it even registered?
        return containerSlot.isForStorage();
    }
    /**
     * @param slot Slot you inquire about
     *
     * @return If this slot displays a default item
     */
    public boolean isDisplaySlot(@Nullable Integer slot) {
        GOOPCSlot containerSlot = getSlotAt(slot);
        if (containerSlot == null) { return false; }

        // Is it even registered?
        return containerSlot.isForDisplay();
    }
    /**
     * @param slot Slot you inquire about
     *
     * @return If this slot is occupied by a container edge
     */
    public boolean isEdgeSlot(@Nullable Integer slot) {
        GOOPCSlot containerSlot = getSlotAt(slot);
        if (containerSlot == null) { return false; }

        // Is it even registered?
        return containerSlot.isForEdge();
    }

    /**
     * @return <code>false</code> iff and only if the provided item does not meet the
     *         restrictions the slot contains.
     *         <br><br>
     *         Will return true for all other scenarios:
     *         <br>+ Slot having no requirements
     *         <br>+ Slot not existing.
     */
    public boolean isPlaceable(@Nullable ItemStack iSource, @Nullable Integer slot) {
        if (slot == null) { return true; }

        // Slot exists?
        GOOPCSlot csSlot = slotsContent.get(slot);

        // If non-null
        if (csSlot != null) { return csSlot.canStore(iSource); } else { return true; }
    }
    /**
     * @return <code>false</code> iff and only if the provided
     *         player does not meet the slot restrictions.
     *         <br><br>
     *         Will return true for all other scenarios:
     *         <br>+ Slot having no restrictions.
     *         <br>+ Slot not existing.
     */
    public boolean meetsRestrictions(@NotNull Player p, @Nullable Integer slot) {
        if (slot == null) { return true; }

        // Slot exists?
        GOOPCSlot csSlot = slotsContent.get(slot);

        // If non-null
        if (csSlot != null) { return csSlot.matchesRestrictions(p); } else { return true; }
    }
    //endregion
}
