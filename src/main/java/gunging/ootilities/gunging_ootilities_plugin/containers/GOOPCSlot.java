package gunging.ootilities.gunging_ootilities_plugin.containers;

import com.destroystokyo.paper.profile.ProfileProperty;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerSlotEdges;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerSlotTypes;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.KindRestriction;
import gunging.ootilities.gunging_ootilities_plugin.containers.restriction.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.PlusMinusPercent;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ApplicableMask;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Stores the information of an individual slot of a container.
 * That way, a {@link GOOPCTemplate} is just a list of these.
 */
@SuppressWarnings("unused")
public class GOOPCSlot implements Cloneable {

    @NotNull public static GOOPCSlot fromComparison(@Nullable ItemStack display, @Nullable ItemStack storage, @Nullable GOOPCSlot original, int s) {
        GOOPCSlot result;

        // Ok, is it an edge?
        if (OotilityCeption.IsEncrypted(display, GOOPCTemplate.EDGE_ENCRYPTION_CODE)) {

            // Add as edge
            result = new GOOPCSlot(s, ContainerSlotTypes.EDGE, OotilityCeption.RenameItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "", null));

            // No then lets see if its for storage
        } else {

            // Non-null
            if (OotilityCeption.IsAirNullAllowed(storage)) { storage = new ItemStack(Material.AIR); }
            if (OotilityCeption.IsAirNullAllowed(display)) { display = new ItemStack(Material.AIR); }

            // Was there any change between storage and display
            boolean isForDisplay = storage.isSimilar(display);

            // Add as straight up ItemStack or as MMOItem?
            boolean asMMOItem = false;
            if (Gunging_Ootilities_Plugin.foundMMOItems) { asMMOItem = GooPMMOItems.IsMMOItem(display); }

            // Decide item storage capabilities
            ContainerSlotTypes csType = ContainerSlotTypes.DISPLAY;
            if (!isForDisplay) { csType = ContainerSlotTypes.STORAGE; }

            // Build ContainerSlot
            if (asMMOItem) {

                // Get Internals
                RefSimulator<String> mType = new RefSimulator<>(""), mID = new RefSimulator<>("");
                GooPMMOItems.GetMMOItemInternals(display, mType, mID);

                // Build
                result = new GOOPCSlot(s, csType, mType.getValue(), mID.getValue());

                // As ItemStack it is
            } else {

                /*
                 *  Build, using the one in the display container as the
                 *  true default/display item that will be in the container.
                 */
                result = new GOOPCSlot(s, csType, display);
            }
        }

        // Acquire all nonstandard original values
        if (original != null) {

            // Clone
            original = original.clone();

            // Keep result type
            if (original.isForResult() && result.isForDisplay()) { result.setSlotType(ContainerSlotTypes.RESULT); }

            /*
             * This operation is meant to modify the
             * default item as well as the storage
             * type, so those values are overridden.
             *
             * I do not remember why I'm also changing
             * the slot number but whatever.
             */
            original.setContent(GOOPCManager.cloneItem(result.getContent(false)));
            original.setSlotNumber(result.getSlotNumber());
            original.setSlotType(result.getSlotType());

            // Include
            return original;

        } else {

            // Include
            return result;
        }
    }

    @NotNull public ItemStack toConfigView() {

        // Get item
        ItemStack displayItem = getContentNonNull().clone();

        /*
         * Fetch the lore of the item so more can be added.
         * Cannot display shit if it has no item meta.
         */
        if (displayItem.getItemMeta() == null) { displayItem = GOOPCManager.nameless(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)); }

        ItemMeta iMeta = displayItem.getItemMeta();
        ArrayList<String> viewLore = new ArrayList<>();
        ArrayList<String> initialLore = new ArrayList<>(); if (iMeta.getLore() != null) { initialLore = new ArrayList<>(iMeta.getLore()); }

        /*
         * Build Name:
         *
         * [#] <ALIAS> <name>
         *
         */
        String alias = hasAlias() ? "\u00a7e<\u00a77" + getAlias() + "\u00a7e> \u00a7f" : "";
        String name = "\u00a73[ \u00a7e" + getSlotNumber() + "\u00a73 ] \u00a7f" + alias + OotilityCeption.GetItemName(displayItem);

        /*
         * GOOPCSlot Constructor: Type
         * The rest of the constructor parameters are displayed in the slot name.
         */
        viewLore.add(OotilityCeption.ParseColour("\u00a77Type: \u00a73" + getSlotType()));

        /*
         * Equipment
         */
        if (isForEquipment()) { viewLore.add("");
            viewLore.add(OotilityCeption.ParseColour("\u00a77Equipment Slot:\u00a7e true"));
        }

        /*
         * Commands
         */
        if (hasCommandsOnClick()) {   viewLore.add("");
            viewLore.add(OotilityCeption.ParseColour("\u00a77Commands\u00a73 OnClick\u00a77:"));

            // Log commands on close
            for (int j = 0; j < getCommandsOnClick().size(); j++) { viewLore.addAll(OotilityCeption.chop("\u00a73" + j + "\u00a77: " + getCommandsOnClick().get(j), 60, "\u00a77")); }
        }
        if (hasCommandsOnStore()) {   viewLore.add("");
            viewLore.add(OotilityCeption.ParseColour("\u00a77Commands\u00a73 OnStore\u00a77:"));

            // Log commands on close
            for (int j = 0; j < getCommandsOnStore().size(); j++) { viewLore.addAll(OotilityCeption.chop("\u00a73" + j + "\u00a77: " + getCommandsOnStore().get(j), 60, "\u00a77")); }
        }
        if (hasCommandsOnTake()) {    viewLore.add("");
            viewLore.add(OotilityCeption.ParseColour("\u00a77Commands\u00a73 OnTake\u00a77:"));

            // Log commands on close
            for (int j = 0; j < getCommandsOnTake().size(); j++) { viewLore.addAll(OotilityCeption.chop("\u00a73" + j + "\u00a77: " + getCommandsOnTake().get(j), 60, "\u00a77")); }
        }

        /*
         * Masks
         */
        if (hasTypeMask() || maskToString() != null) { viewLore.add(""); }
        if (getTypeWhitelist() != null) { viewLore.add(OotilityCeption.ParseColour("\u00a77Type Mask:  \u00a7b" + getTypeWhitelist().getName())); }
        if (maskToString() != null) { for (String line : OotilityCeption.chop("\u00a77ID Mask:  \u00a7b" + maskToString(), 60, "\u00a7b")) { viewLore.add(OotilityCeption.ParseColour(line)); } }

        /*
         * Restrictions
         */
        if (getRestrictions().size() > 0) { viewLore.add("");
            viewLore.add(OotilityCeption.ParseColour("\u00a77Restriction Behaviour: \u00a73" + getRestrictedBehaviour().toString()));

            for (SlotRestriction sr : getRestrictions()) { viewLore = sr.appendLore(viewLore); }
        }

        /*
         * Apply lore changes
         */
        viewLore.add("");
        viewLore.addAll(initialLore);

        // Rename and set
        iMeta.setLore(viewLore);
        iMeta.setDisplayName(name);
        displayItem.setItemMeta(iMeta);

        // Yes
        return displayItem;
    }

    @Override public GOOPCSlot clone() {
        try { super.clone(); } catch (CloneNotSupportedException ignored) { }

        // Construct similar slot
        GOOPCSlot clone = new GOOPCSlot(getSlotNumber(), getSlotType(), getContentNonNull(false).clone());

        // Services
        clone.setEdgeFormation(getEdgeFormation());

        clone.setAlias(getAlias());

        clone.setCommandsOnClick(getCommandsOnClick());
        clone.setCommandsOnStore(getCommandsOnStore());
        clone.setCommandsOnTake(getCommandsOnTake());

        clone.setTypeMask(getTypeWhitelist());
        clone.setKindWhitelist(getKindWhitelist());
        clone.setKindBlacklist(getKindBlacklist());
        clone.setIDWhitelist(getIdWhitelist());
        clone.setIDBlacklist(getIdBlacklist());

        clone.setRestrictions(getRestrictions());
        clone.setRestrictedBehaviour(getRestrictedBehaviour());

        clone.setForEquipment(isForEquipment());

        // Yeah
        return clone;
    }

    /**
     * @param item Item trying to be put here, even air.
     *
     * @return If this item fits in this Container Slot
     */
    public boolean canStore(@Nullable ItemStack item) {
        //KTI//OotilityCeption.Log("Storing " + OotilityCeption.GetItemName(item));

        // Can always store AIR
        if (OotilityCeption.IsAirNullAllowed(item)) {
            //KTI//OotilityCeption.Log("Aerial Bypass");
            //KTI//OotilityCeption. Log(" \u00a73-\u00a7b-\u00a73> \u00a77Aerial Bypass");
            return true; }

        // If MMOItems is enabled, fetch the Type and ID in question
        String mType = null, mID = null;
        if (Gunging_Ootilities_Plugin.foundMMOItems) {

            RefSimulator<String> miType = new RefSimulator<>(null), miID = new RefSimulator<>(null);
            GooPMMOItems.GetMMOItemInternals(item, miType, miID);
            mType = miType.getValue();
            mID = miID.getValue(); }

        /*
         * Test it against item restrictions:
         *
         *      #1 Of MMOItem Type
         *      #2 Of Item Kind
         *      #3 Of MMOItem ID
         */
        @SuppressWarnings("ConstantConditions")
        boolean typeSuccess = !hasTypeMask() || getTypeWhitelist().AppliesTo(mType);
        boolean kindSuccess = !hasKindMasks() || allowedKind(item, getKindWhitelist(), getKindBlacklist());
        boolean idSuccess = !hasIDMasks() || allowedID(mID, getIdWhitelist(), getIdBlacklist());

        /*
         * If it has ID Masks, and has no kind masks, and the KIND SUCCESS was marked
         * as success due to having no kind masks... then this is not that much of a
         * success, the ID Success will decide alone if it succeeded.
         *
         * The converse also applies
         */
        if (hasIDMasks() && !hasKindMasks() && kindSuccess) { kindSuccess = false; }
        if (hasKindMasks() && !hasIDMasks() && idSuccess) { idSuccess = false; }

        // It must succeed on all three.
        return typeSuccess && (kindSuccess || idSuccess);
    }

    //region #### Constructor ####
    /**
     * @return The position of this slot in the template.
     */
    public int getSlotNumber() { return slotNumber; }
    int slotNumber;
    /**
     * @param slotNumber The position of this slot in the template.
     */
    public void setSlotNumber(int slotNumber) { this.slotNumber = slotNumber; }

    /**
     * @return Item storage behaviour of this slot.
     */
    @NotNull public ContainerSlotTypes getSlotType() { return slotType; }
    @NotNull ContainerSlotTypes slotType;
    /**
     * Changes the way this slot accepts items.
     *
     * @param slotType Item storage capability you trying to specify
     */
    public void setSlotType(@NotNull ContainerSlotTypes slotType) { this.slotType = slotType; }
    /**
     * @return If this slot is of type STORAGE
     */
    public boolean isForStorage() { return slotType == ContainerSlotTypes.STORAGE;  }
    /**
     * @return If this slot is of type EDGE
     */
    public boolean isForEdge() { return slotType == ContainerSlotTypes.EDGE; }
    /**
     * @return If this slot is of type RESULT
     */
    public boolean isForResult() { return slotType == ContainerSlotTypes.RESULT; }
    /**
     * @return If this slot is of type DISPLAY
     */
    public boolean isForDisplay() { return slotType == ContainerSlotTypes.DISPLAY; }

    /**
     * @return The item displayed by default by this slot.
     */
    @Nullable public ItemStack getContent() { return getContent(true); }
    /**
     * @return The item displayed by default by this slot.
     */
    @Nullable public ItemStack getContent(boolean forceDefault) { if (content == null) { return null; }return forceDefault ? GOOPCManager.toDefaultItem(content) : content; }
    /**
     * @return The item displayed by default by this slot,
     *         or AIR if its currently null.
     */
    @NotNull public ItemStack getContentNonNull() { return getContentNonNull(true); }
    /**
     * @return The item displayed by default by this slot,
     *         or AIR if its currently null.
     */
    @NotNull public ItemStack getContentNonNull(boolean forceDefault) {if (content == null) { return new ItemStack(Material.AIR); } return (forceDefault ? GOOPCManager.toDefaultItem(content) : content); }
    @Nullable ItemStack content;
    /**
     * Set the item displayed by default by this slot.
     * 
     * @param content The item that will now be displayed
     */
    public void setContent(@Nullable ItemStack content) { this.content = content; sanitizeContent(); }
    /**
     * @return If this slot's {@link #getContent()} contains AIR or something like that.
     */
    public boolean isEmpty() { return (getContent(false) == null); }
    /**
     * It is assumed that AIR slots have a null content,
     * this just makes sure that is true.
     */
    void sanitizeContent() { if (OotilityCeption.IsAirNullAllowed(content)) { content = null; } }

    /**
     * Create a GOOPCSlot that updates itself by fetching MMOItems from
     * the templates, rather than having a statically-built item.
     *
     * @param number Slot number that this slot will occupy
     * @param slotType Storage behaviour of this slot
     *
     * @param mmoitemType MMOItem Type to bind
     * @param mmoitemID MMOItem ID to bind
     *
     * @throws ClassNotFoundException If MMOItems plugin is not present or it did not load.
     *                                Make sure to check {@link Gunging_Ootilities_Plugin#foundMMOItems}
     *                                before using this as a constructor.
     */
    @SuppressWarnings("JavaDoc") public GOOPCSlot(int number, @NotNull ContainerSlotTypes slotType, @NotNull String mmoitemType, @NotNull String mmoitemID) { this(number, slotType, GooPMMOItems.GetMMOItemOrDefault(mmoitemType, mmoitemID)); }
    /**
     * Constructor for the wrapper that stores information on the
     * customization of one particular slot in a container template.
     *
     * @param number Slot number that this slot will occupy
     * @param slotType Storage behaviour of this slot
     * @param content Item that will be displayed by default.
     */
    public GOOPCSlot(int number, @NotNull ContainerSlotTypes slotType, @Nullable ItemStack content) {
        this.slotType = slotType;
        this.slotNumber = number;

        // Edge overrides content
        if (isForEdge()) { content = OotilityCeption.RenameItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "\u00a7r ", null); }
        this.content = content;
        sanitizeContent();
    }
    //endregion

    //region #### Services ####
    //region Container Edges
    /**
     * @return If this slot is a {@link ContainerSlotTypes#EDGE}, then this will
     *         be the kind of edge it must be displayed.
     */
    @NotNull public ContainerSlotEdges getEdgeFormation() { return edgeFormation; }
    @NotNull ContainerSlotEdges edgeFormation = ContainerSlotEdges.MAINLAND;
    /**
     * Processes the Custom Model Data of the item within,
     * but will be ignored if the slot is not of the correct
     * type.
     *
     * @param edge The edge formation that will be displayed by this slot.
     */
    public void setEdgeFormation(@NotNull ContainerSlotEdges edge) {

        // If its an edge
        if (isForEdge()) {

            // Store it cuz why not
            edgeFormation = edge;

            // Modify the 'default Item'
            @SuppressWarnings("ConstantConditions")
            ItemMeta iMeta = getContent(false).getItemMeta();

            // Sets to the correct CustomModelData
            iMeta.setCustomModelData(GOOPCTemplate.EDGE_FORMATIONS_CMD_START + edgeFormation.ordinal());

            // Log
            //EDGE//OotilityCeption. Log(("Edges Set CMD of \u00a73#" + slotNumber + "\u00a77 to \u00a7e" + dMeta.getCustomModelData());

            // Set meta
            getContent(false).setItemMeta(iMeta);
        }
    }
    //endregion

    //region Alias
    /**
     * @return Another way to target this slot through the [slot] argument,
     *         apart from its slot number.
     */
    public @Nullable String getAlias() { return alias; }
    @Nullable String alias;
    /**
     * Changes the alias of this item
     *
     * @param alias Another way to target this slot through the [slot] argument,
     *              apart from its slot number.
     */
    public void setAlias(@Nullable String alias) { this.alias = alias; }
    /**
     * @return If there is a name by which this slot will be targeted,
     *         apart from its slot number.
     */
    public boolean hasAlias() { return (alias != null); }
    //endregion

    //region Commands of Slot
    /**
     * @return The commands run when clicking this slot.
     */
    @NotNull public ArrayList<String> getCommandsOnClick() { return commandsOnClick; }
    @NotNull final ArrayList<String> commandsOnClick = new ArrayList<>();
    /**
     * @return If this slot runs commands clicked.
     */
    public boolean hasCommandsOnClick() { return  commandsOnClick.size() > 0;}
    /**
     * Run the commands that run when this slot is clicked.
     *
     * @param opener Player performing this operation. For PAPI parsing.
     * @param item ItemStack removed, for 'PAPI parsing'
     */
    public void executeCommandsOnClick(@NotNull OfflinePlayer opener, @Nullable ItemStack item) {

        for (String commandOnClick : getCommandsOnClick()) {
            if (commandOnClick == null) { continue; }

            // Run it lmao
            OotilityCeption.SendConsoleCommand(commandOnClick, opener.getPlayer(), opener, null, item);
        }
    }
    /**
     * @param command The command that runs when the player clicks this slot..
     */
    public void setCommandsOnClick(@Nullable String command) {

        // Clear current commands
        commandsOnClick.clear();
        if (command == null || command.isEmpty()) { return; }

        commandsOnClick.add(command);
    }
    /**
     * @param commands The commands that run when the player clicks this slot..
     */
    public void setCommandsOnClick(@Nullable ArrayList<String> commands) {

        // Clear current commands
        commandsOnClick.clear();
        if (commands == null || commands.size() == 0) { return; }

        commandsOnClick.addAll(commands);
    }
    /**
     * @param command The command that runs when the player clicks this slot..
     */
    public void addCommandsOnClick(@Nullable String command) {

        // Ignore
        if (command == null || command.isEmpty()) { return; }

        commandsOnClick.add(command);
    }
    /**
     * @param commands The commands that run when the player clicks this slot..
     */
    public void addCommandsOnClick(@Nullable ArrayList<String> commands) {

        // Ignore
        if (commands == null || commands.size() == 0) { return; }

        commandsOnClick.addAll(commands);
    }

    /**
     * @return The commands run when storing items in this slot.
     */
    @NotNull public ArrayList<String> getCommandsOnStore() { return commandsOnStore; }
    @NotNull final ArrayList<String> commandsOnStore = new ArrayList<>();
    /**
     * @return If this slot runs commands when storing items in it.
     */
    public boolean hasCommandsOnStore() { return  commandsOnStore.size() > 0;}
    /**
     * Run the commands that run when an item is stored into this slot.
     *
     * @param opener Player performing this operation. For PAPI parsing.
     * @param item ItemStack removed, for 'PAPI parsing'
     */
    public void executeCommandsOnStore(@NotNull OfflinePlayer opener, @Nullable ItemStack item) {

        for (String commandOnStore : getCommandsOnStore()) {
            if (commandOnStore == null) { continue; }

            // Run it lmao
            OotilityCeption.SendConsoleCommand(commandOnStore, opener.getPlayer(), opener, null, item);
        }
    }
    /**
     * @param command The command that runs when the player stores items into this slot.
     */
    public void setCommandsOnStore(@Nullable String command) {

        // Clear current commands
        commandsOnStore.clear();
        if (command == null || command.isEmpty()) { return; }

        commandsOnStore.add(command);
    }
    /**
     * @param commands The commands that run when the player stores items into this slot.
     */
    public void setCommandsOnStore(@Nullable ArrayList<String> commands) {

        // Clear current commands
        commandsOnStore.clear();
        if (commands == null || commands.size() == 0) { return; }

        commandsOnStore.addAll(commands);
    }
    /**
     * @param command The command that runs when the player stores items into this slot.
     */
    public void addCommandsOnStore(@Nullable String command) {

        // Ignore
        if (command == null || command.isEmpty()) { return; }

        commandsOnStore.add(command);
    }
    /**
     * @param commands The commands that run when the player stores items into this slot.
     */
    public void addCommandsOnStore(@Nullable ArrayList<String> commands) {

        // Ignore
        if (commands == null || commands.size() == 0) { return; }

        commandsOnStore.addAll(commands);
    }

    /**
     * @return The commands run when removing items from this slot.
     */
    @NotNull public ArrayList<String> getCommandsOnTake() { return commandsOnTake; }
    @NotNull final ArrayList<String> commandsOnTake = new ArrayList<>();
    /**
     * @return If this slot runs commands when removing items from it.
     */
    public boolean hasCommandsOnTake() { return  commandsOnTake.size() > 0;}
    /**
     * Run the commands that run when an item is taken from the slot.
     *
     * @param opener Player performing this operation. For PAPI parsing.
     * @param item ItemStack removed, for 'PAPI parsing'
     */
    public void executeCommandsOnTake(@NotNull OfflinePlayer opener, @Nullable ItemStack item) {

        for (String commandOnTake : getCommandsOnTake()) {
            if (commandOnTake == null) { continue; }

            // Run it lmao
            OotilityCeption.SendConsoleCommand(commandOnTake, opener.getPlayer(), opener, null, item);
        }
    }
    /**
     * @param command The command that runs when the player takes items from this slot.
     */
    public void setCommandsOnTake(@Nullable String command) {

        // Clear current commands
        commandsOnTake.clear();
        if (command == null || command.isEmpty()) { return; }

        commandsOnTake.add(command);
    }
    /**
     * @param commands The commands that run when the player takes items from this slot.
     */
    public void setCommandsOnTake(@Nullable ArrayList<String> commands) {

        // Clear current commands
        commandsOnTake.clear();
        if (commands == null || commands.size() == 0) { return; }

        commandsOnTake.addAll(commands);
    }
    /**
     * @param command The command that runs when the player takes items from this slot.
     */
    public void addCommandsOnTake(@Nullable String command) {

        // Ignore
        if (command == null || command.isEmpty()) { return; }

        commandsOnTake.add(command);
    }
    /**
     * @param commands The commands that run when the player takes items from this slot.
     */
    public void addCommandsOnTake(@Nullable ArrayList<String> commands) {

        // Ignore
        if (commands == null || commands.size() == 0) { return; }

        commandsOnTake.addAll(commands);
    }
    //endregion

    //region Item Whitelists and Blacklists
    /**
     * @return The Type Mask that restricts to only certain MMOItem types.
     */
    @Nullable public ApplicableMask getTypeWhitelist() { return typeWhitelist; }
    @Nullable ApplicableMask typeWhitelist = null;
    /**
     * Edit the Type Mask of items that can be stored in these slots.
     *
     * @param mask The mask that whitelists or blacklists item types.
     */
    public void setTypeMask(@Nullable ApplicableMask mask) { typeWhitelist = mask; }
    /**
     * @return If the MMOItem type of items is to be checked before storing.
     */
    public boolean hasTypeMask() { return Gunging_Ootilities_Plugin.foundMMOItems && typeWhitelist != null; }

    /**
     * @return What 'kind' of items are whitelisted by this slot?
     */
    @NotNull public ArrayList<KindRestriction> getKindWhitelist() { return kindWhitelist; }
    @NotNull final ArrayList<KindRestriction> kindWhitelist = new ArrayList<>();
    /**
     * @param restrictions What 'kind' of items are whitelisted by this slot?
     */
    public void setKindWhitelist(@NotNull ArrayList<KindRestriction> restrictions) { kindWhitelist.clear(); for (KindRestriction restriction : restrictions) { addToWhitelist(restriction);} }
    /**
     * Add a kind to be whitelisted.
     *
     * @param kind Kind to be added to the whitelist.
     */
    public void addToWhitelist(@NotNull KindRestriction kind) { kindWhitelist.add(kind); }

    /**
     * @return What 'kind' of items are blacklisted by this slot?
     */
    @NotNull public ArrayList<KindRestriction> getKindBlacklist() { return kindBlacklist; }
    @NotNull final ArrayList<KindRestriction> kindBlacklist = new ArrayList<>();
    /**
     * @param restrictions What 'kind' of items are blacklisted by this slot?
     */
    public void setKindBlacklist(@NotNull ArrayList<KindRestriction> restrictions) { kindBlacklist.clear(); for (KindRestriction restriction : restrictions) { addToBlacklist(restriction);} }
    /**
     * Add a kind to be blacklisted.
     *
     * @param kind Kind to be added to the blacklist.
     */
    public void addToBlacklist(@NotNull KindRestriction kind) { kindBlacklist.add(kind); }

    /**
     * @return If the kind of item is to be checked before storing it.
     */
    public boolean hasKindMasks() { return kindWhitelist.size() != 0 || kindBlacklist.size() != 0; }
    /**
     * @param mat Item that is to be stored in this container
     *
     * @param whitelist Kinds of which one must be matched
     * @param blacklist Kinds that must not be matched
     *
     * @return If the item matches any of the expected kinds but none of the blocked kinds
     */
    public static boolean allowedKind(@NotNull ItemStack mat, @NotNull ArrayList<KindRestriction> whitelist, @NotNull ArrayList<KindRestriction> blacklist) {
        //KTI//OotilityCeption.Log("\u00a7b---\u00a77 Kinds");

        // Automatic success if there is no blacklist nor whitelist
        if (whitelist.size() == 0 && blacklist.size() == 0) {
            //KTI//OotilityCeption.Log("\u00a73---\u00a77 No lists, \u00a7aSuccess");
            return true; }

        // If it is any of the entries in the blacklist, it will be blocked
        for (KindRestriction k : blacklist) { if (kindRestrictionMatch(mat, k)) {
            //KTI//OotilityCeption.Log("\u00a73---\u00a77 Blacklist Matched, \u00a7cFail");
            return false; } }

        // First it must succeed by matching one entry of the whitelist
        boolean success = whitelist.size() == 0;
        for (KindRestriction k : whitelist) { if (kindRestrictionMatch(mat, k)) { success = true; break;} }

        // If no whitelist entry matched, rejection.
        //KTI//OotilityCeption.Log("\u00a73---\u00a77 Whitelist matched? \u00a7a" + success);
        return success;
    }
    /**
     * @param iSource Item being tested to be of this kind
     *
     * @param restriction Kind the item should be for this to succeed
     *
     * @return If this item is of this kind.
     */
    public static boolean kindRestrictionMatch(@NotNull ItemStack iSource, @NotNull KindRestriction restriction) {

        boolean success = false;
        Material mat = iSource.getType();

        // Which?
        switch (restriction) {
            case AXE: success = OotilityCeption.IsAxe(mat); break;
            case ARMOR: success = OotilityCeption.IsArmor(mat); break;
            case BOOTS: success = OotilityCeption.IsBoots(mat); break;
            case BUTTON: success = OotilityCeption.IsButton(mat); break;
            case CHESTPLATE: success = OotilityCeption.IsChestplate(mat); break;
            case CROSSBOW: success = OotilityCeption.IsCrossbow(mat); break;
            case FISHING_ROD: success = OotilityCeption.IsFishingRod(mat); break;
            case HELMET:
                success = OotilityCeption.IsHelmet(mat);

                // Maybe a hat stat
                if (!success && Gunging_Ootilities_Plugin.foundMMOItems) {

                    // If it is a hat, its a helmet
                    success = OotilityCeption.If(GooPMMOItems.GetBooleanStatValue(iSource, GooPMMOItems.HAT, null, true)); }
                break;
            case LEAVES: success = OotilityCeption.IsLeaves(mat); break;
            case LEGGINGS: success = OotilityCeption.IsLeggings(mat); break;
            case MELEE_WEAPON: success = OotilityCeption.IsMeleeWeapon(mat); break;
            case DOOR: success = OotilityCeption.IsDoor(mat); break;
            case PRESSURE_PLATE: success = OotilityCeption.IsPressurePlate(mat); break;
            case RANGED_WEAPON: success = OotilityCeption.IsRangedWeapon(mat); break;
            case TOOL: success = OotilityCeption.IsTool(mat); break;
            case SHULKER: success = OotilityCeption.IsShulkerBox(mat); break;
            case SWORD: success = OotilityCeption.IsSword(mat); break;
            case HEAD: success = OotilityCeption.IsHead(mat); break;
        }

        return success;
    }

    /**
     * @return What MMOItem ID of items are whitelisted by this slot?
     */
    @NotNull public ArrayList<String> getIdWhitelist() { return idWhitelist; }
    @NotNull ArrayList<String> idWhitelist = new ArrayList<>();
    /**
     * @param restrictions What 'kind' of items are whitelisted by this slot?
     */
    public void setIDWhitelist(@NotNull ArrayList<String> restrictions) { idWhitelist.clear(); for (String restriction : restrictions) { addToWhitelist(restriction);} }
    /**
     * Add an MMOItem ID to be whitelisted.
     *
     * @param mmoitemID MMOItem ID to be added to the whitelist.
     */
    public void addToWhitelist(@NotNull String mmoitemID) {

        try {

            // Attempt to add as kind restriction
            KindRestriction kind = KindRestriction.valueOf(mmoitemID);
            addToWhitelist(kind);

            // Add as MMOItem ID
        } catch (IllegalArgumentException ignored) { idWhitelist.add(mmoitemID); }
    }

    /**
     * @return What MMOItem ID of items are blacklisted by this slot?
     */
    @NotNull public ArrayList<String> getIdBlacklist() { return idBlacklist; }
    @NotNull ArrayList<String> idBlacklist = new ArrayList<>();
    /**
     * @param restrictions What 'kind' of items are blacklisted by this slot?
     */
    public void setIDBlacklist(@NotNull ArrayList<String> restrictions) { idBlacklist.clear(); for (String restriction : restrictions) { addToBlacklist(restriction);} }
    /**
     * Add an MMOItem ID to be blacklisted.
     *
     * @param mmoitemID MMOItem ID to be added to the blacklist.
     */
    public void addToBlacklist(@NotNull String mmoitemID) {

        try {

            // Attempt to add as kind restriction
            KindRestriction kind = KindRestriction.valueOf(mmoitemID);
            addToBlacklist(kind);

            // Add as MMOItem ID
        } catch (IllegalArgumentException ignored) { idBlacklist.add(mmoitemID); }
    }

    /**
     * @return If the MMOItem ID of the item is to be checked before storing it.
     */
    boolean hasIDMasks() { return Gunging_Ootilities_Plugin.foundMMOItems && (idWhitelist.size() != 0 || idBlacklist.size() != 0); }
    /**
     * @param id MMOItem ID to test
     *
     * @param whitelist IDs of which one must be matched
     * @param blacklist IDs that must not be matched
     *
     * @return If the ID matches any of the expected but none of the blocked
     */
    public static boolean allowedID(@Nullable String id, @NotNull ArrayList<String> whitelist, @NotNull ArrayList<String> blacklist) {
        //KTI//OotilityCeption.Log("\u00a7b---\u00a77 MMOItem IDs");
        if (id == null) { id = ""; }

        // Automatic success if there is no blacklist nor whitelist
        if (whitelist.size() == 0 && blacklist.size() == 0) {
            //KTI//OotilityCeption.Log("\u00a73---\u00a77 No lists, \u00a7aSuccess");
            return true; }

        // If it is any of the entries in the blacklist, it will be blocked
        for (String k : blacklist) { if (OotilityCeption.wildcardMatches(id, k, true)) {
            //KTI//OotilityCeption.Log("\u00a73---\u00a77 Blacklist Matched, \u00a7cFail");
            return false; } }

        // First it must succeed by matching one entry of the whitelist
        boolean success = whitelist.size() == 0;
        for (String k : whitelist) { if (OotilityCeption.wildcardMatches(id, k, true)) { success = true; break;} }

        // If no whitelist entry matched, rejection.
        //KTI//OotilityCeption.Log("\u00a73---\u00a77 Whitelist matched? \u00a7a" + success);
        return success;
    }
    //endregion

    //region Player Attribute Restrictions
    /**
     * @return The qualifications a player must meet to be able to use this slot.
     */
    @NotNull public ArrayList<SlotRestriction> getRestrictions() { return restrictions; }
    @NotNull final ArrayList<SlotRestriction> restrictions = new ArrayList<>();
    /**
     * @param restrictions What 'kind' of items are whitelisted by this slot?
     */
    public void setRestrictions(@NotNull ArrayList<SlotRestriction> restrictions) { this.restrictions.clear(); for (SlotRestriction restriction : restrictions) { addRestriction(restriction);} }
    /**
     * @param restriction A player must match these attributes to be able to use this slot.
     */
    public void addRestriction(@NotNull SlotRestriction restriction) { restrictions.add(restriction); }
    /**
     * Clear the restrictions of player attributes imposed on this slot.
     */
    public void clearRestrictions() { restrictions.clear(); }

    /**
     * Suppose a player suddenly stops having the qualifications
     * required to use this slot (we can't assume they are all
     * permanent!)
     *
     * @return What happens to the items already stored in this slot
     *         once a player loses the ability to take them out?
     */
    @NotNull public RestrictedBehaviour getRestrictedBehaviour() { return restrictedBehaviour; }
    @NotNull RestrictedBehaviour restrictedBehaviour = RestrictedBehaviour.TAKE;
    /**
     * Edit the behaviour of items locked inside the slot.
     *
     * @param behaviour What will happen to items already stored in this slot
     *                  once their owner loses the ability to take them out?
     */
    public void setRestrictedBehaviour(@NotNull RestrictedBehaviour behaviour) { restrictedBehaviour = behaviour; }
    
    /**
     * @param player Player that is attempting to interact with these slots
     *               
     * @return If the player has the qualifications to interact
     */
    public boolean matchesRestrictions(@NotNull Player player) {

        // For every restriction
        for (SlotRestriction restriction : getRestrictions()) {
            if (restriction == null) { continue; }

            //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eC\u00a77 Checking \u00a7b" + sr.getClass().getSimpleName());

            // Does it not match? FAIL
            if (!restriction.isUnlockedFor(player)) {
                //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eV\u00a7c Not Met");
                return false; } }

        // Matched all
        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eV\u00a7a All Met");
        return true;
    }
    //endregion

    //region Equipment
    /**
     * @return If MMOItems put into this slots will give their
     *         stats to the player owner of this container.
     */
    public boolean isForEquipment() { return forEquipment; }
    boolean forEquipment;
    /**
     * Changes the equipment ability of this slot.
     *
     * @param equipment If MMOItems put into this slots will give their
     *                  stats to the player owner of this container.
     */
    public void setForEquipment(boolean equipment) { forEquipment = Gunging_Ootilities_Plugin.foundMMOItems && equipment; }
    //endregion
    //endregion

    //region #### Persistence ####
    /**
     * @param refinedContents The entire slot contents of a template.
     *
     * @return All of those but {@link #serialize(GOOPCSlot)} has been called.
     */
    @NotNull public static ArrayList<String> serializeContents(@NotNull HashMap<Integer, GOOPCSlot> refinedContents) {
        // A value to return
        ArrayList<String> ret = new ArrayList<>();

        //SRZ//OotilityCeption.Log("\u00a78SLOT\u00a7e SC\u00a77 Saving Contents.... \u00a76x" + refinedContents.size());

        // Add all!
        for (Map.Entry<Integer, GOOPCSlot> entry : refinedContents.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) { continue; }

            // Add to ret
            ret.add(serialize(entry.getValue()));
        }

        // Was there nothing? An air sample, for display.
        if (ret.size() == 0) { ret.add("0||DISPLAY||v air 0||1"); }

        //SRZ//for (String str : ret) { OotilityCeption.Log("\u00a78SLOT\u00a7e SC\u00a77 \u00a76+\u00a77 " + str); }

        return ret;
    }
    @NotNull public static String serialize(@NotNull GOOPCSlot slot) {

        // Begin
        StringBuilder serialized = new StringBuilder();

        // Start with link
        if (slot.hasAlias()) { serialized.append(slot.getAlias()).append(" "); }

        // Add number
        serialized.append(slot.getSlotNumber()).append("||");

        // Set TYpe
        serialized.append(slot.getSlotType().toString()).append("||");

        // Remember Item
        boolean asMI = false;
        if (Gunging_Ootilities_Plugin.foundMMOItems) {

            // Store as MMOItem
            if (GooPMMOItems.IsMMOItem(slot.getContent(false))) {

                // Get Type and ID Info
                RefSimulator<String> mType = new RefSimulator<>(""), mID = new RefSimulator<>("");
                GooPMMOItems.GetMMOItemInternals(slot.getContent(false), mType, mID);

                // Serialize as MMOItem
                serialized.append("m ").append(mType.getValue()).append(" ").append(mID.getValue()).append("||");
                asMI = true;

            // Store as vanilla
            } else {
                String type = slot.getContentNonNull(false).getType().toString();

                // Serialize as Vanilla
                serialized.append("v ").append(type).append(" 0").append("||");
            }

        // Store as vanilla
        } else {
            String type = slot.getContentNonNull(false).getType().toString();

            // Serialize as Vanilla
            serialized.append("v ").append(type).append(" 0").append("||");
        }

        // Store count
        int count = slot.getContentNonNull(false).getAmount();

        // Serialize as Vanilla
        serialized.append(count).append("||");

        // Add intangible stuff
        if (slot.hasCommandsOnTake()) {
            for (String command : slot.getCommandsOnTake()) {

                //Store it
                serialized.append("COT").append(command).append("||");
            }
        }

        // Add intangible stuff
        if (slot.hasCommandsOnStore()) {
            for (String command : slot.getCommandsOnStore()) {

                //Store it
                serialized.append("COS").append(command).append("||");
            }
        }

        // Add intangible stuff
        if (slot.hasCommandsOnClick()) {
            for (String command : slot.getCommandsOnClick()) {

                //Store it
                serialized.append("COC").append(command).append("||");
            }
        }

        // Mask of Type
        if (slot.hasTypeMask()) {

            //Store it
            //noinspection ConstantConditions
            serialized.append("AMT").append(slot.getTypeWhitelist().getName()).append("||");
        }

        // Mask of ID
        if (slot.hasKindMasks() || slot.hasIDMasks()) {

            //Store it
            serialized.append("AMI");

            // For every one
            for (String str : slot.getIdWhitelist()) { serialized.append(str).append(","); }

            // For every one
            for (String str : slot.getIdBlacklist()) { serialized.append("!").append(str).append(","); }

            // For every one
            for (KindRestriction str : slot.getKindWhitelist()) { serialized.append(str.toString()).append(","); }

            // For every one
            for (KindRestriction str : slot.getKindBlacklist()) { serialized.append("!").append(str.toString()).append(","); }

            serialized.append("||");
        }

        // Equipment
        if (slot.isForEquipment()) {

            //Store it
            serialized.append("EQP").append(slot.isForEquipment()).append("||");
        }

        // Restricted Behaviour
        if (slot.getRestrictedBehaviour() != RestrictedBehaviour.TAKE) {

            //Store it
            serialized.append("RCB").append(slot.getRestrictedBehaviour().toString()).append("||");
        }

        // Equipment
        for (SlotRestriction sr : slot.getRestrictions()) {

            // Class
            String subRestriction = "----";
            if (sr instanceof GCSR_Level) { subRestriction = "LEVL"; } else
            if (sr instanceof GCSR_Unlockable) { subRestriction = "UCKB"; } else
            if (sr instanceof GCSR_Permission) { subRestriction = "PERM"; } else
            if (sr instanceof GCSR_Class) { subRestriction = "CLSS"; }

            //Store it
            serialized.append("RCC").append(subRestriction).append(sr.serialize()).append("||");
        }

        // If null, finished.
        if (slot.getContent(false) == null) { return serialized.toString(); }

        // If MMOItem, finished. No Meta will be saved
        if (asMI) { return serialized.toString(); }

        // Stuff that assumes meta exists, so escape if not
        if (!slot.getContentNonNull(false).hasItemMeta()) { return serialized.toString(); }

        // Is it a head or something?
        if (slot.getContentNonNull(false).getType() == Material.PLAYER_HEAD) {

            // Git Meta
            SkullMeta sMeta = (SkullMeta) slot.getContentNonNull(false).getItemMeta();

            // Git Property Map
            @SuppressWarnings("ConstantConditions") Set<ProfileProperty> propertyMap = sMeta.getPlayerProfile().getProperties();

            // Get texture?
            String texture = "";
            for (ProfileProperty prop : propertyMap) {

                //TXR//OotilityCeption. Log("\u00a7e" + prop.getName() + "\u00a77:");
                //TXR//OotilityCeption. Log("\u00a73Signature: \u00a77" + prop.getSignature());
                //TXR//OotilityCeption. Log("\u00a77Value: \u00a7f" + prop.getValue());

                // Is it texture?
                if (prop.getName().equals("textures")) { texture = prop.getValue(); }
            }

            //Store it
            serialized.append("TXR").append(texture).append("||");
        }

        // Is the item named anything?
        if (slot.getContentNonNull(false).getItemMeta().hasDisplayName()) {

            //Store it
            serialized.append("NME").append(OotilityCeption.GetItemName(slot.getContent(false))).append("||");
        }

        // Has the item custom model data? Well, is it at least MC 1.14?
        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) {

            // Has the item custom model data?
            if (slot.getContentNonNull(false).getItemMeta().hasCustomModelData()) {

                //Store it
                serialized.append("CMD").append(slot.getContentNonNull(false).getItemMeta().getCustomModelData()).append("||");
            }
        }

        if (slot.getContentNonNull(false).getItemMeta().getItemFlags().size() > 0) {

            StringBuilder flagsEncode = new StringBuilder();
            boolean first = true;
            for (ItemFlag iFlag : slot.getContentNonNull(false).getItemMeta().getItemFlags()) {

                // Append comma
                if (first) { first = false; } else { flagsEncode.append(","); }

                // Append
                flagsEncode.append(iFlag.toString());
            }

            // Good size
            if (flagsEncode.length() > 0) {

                //Store it
                serialized.append("HFG").append(flagsEncode.toString()).append("||");
            }
        }

        // Does it have enchantments?
        HashMap<Enchantment, Integer> enchs = new HashMap<>(slot.getContentNonNull(false).getEnchantments());
        for (Enchantment ench : enchs.keySet()) {

            //Store it with its level
            serialized.append("NCH").append(ench.getKey().getKey()).append(" ").append(enchs.get(ench)).append("||");
        }

        // Does it have lore?
        if (slot.getContentNonNull(false).getItemMeta().hasLore()) {

            // Get it ALL
            @SuppressWarnings("deprecation") 
            List<String> iLore = slot.getContentNonNull(false).getItemMeta().getLore();
            if (iLore == null) { iLore = new ArrayList<>(); }

            // Add it all
            for (int nIndex = 0; nIndex < iLore.size(); nIndex++) {

                // Make the number format ###
                StringBuilder bakedIndex = new StringBuilder();
                if (nIndex < 100) { bakedIndex.append("0"); }
                if (nIndex < 10) { bakedIndex.append("0"); }
                bakedIndex.append(nIndex);

                //Store it with its level
                serialized.append("LRE").append(bakedIndex.toString()).append(iLore.get(nIndex)).append("||");
            }
        }

        //Return built
        return serialized.toString();
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Nullable public static GOOPCSlot deserialize(@Nullable String serialized) {
        if (serialized == null) { return null; }
        // Format:
        // {id:slot} {type} {filter} {amount} <modifiers>
        // GAUGE 0||DISPLAY||v red_stained_glass_pane 0||1||

        // Step #1: Split by bars (converting them into Ñ because REGEX gets confused, apparently)
        String[] barSplit = serialized.split("\\|\\|");

        // Must be at least of length four eh
        if (barSplit.length < 4) { return null; }

        //region Step #2: Get Optional ID and Slot Number
        String sOptID = null; int sSlot;
        if (barSplit[0].contains(" ")) {

            // Split
            String[] sOptSlot = barSplit[0].split(" ");

            // Get Numeric value first, if parses, and if the format is correct
            if (OotilityCeption.IntTryParse(sOptSlot[1]) && sOptSlot.length == 2) {

                // Valid numeric value. Accepted
                sSlot = Integer.parseInt(sOptSlot[1]);

            // No valid numeric. Slot is NULL
            } else {

                // That's a quick fail-yo!
                return null;
            }

            // Still here? Well accepted the Optional ID
            sOptID = sOptSlot[0];

        // No spaces - only a slot
        } else if (OotilityCeption.IntTryParse(barSplit[0])) {

            // Valid numeric value. Accepted
            sSlot = Integer.parseInt(barSplit[0]);

        // No valid numeric. Slot is NULL
        } else {

            // That's a quick fail-yo!
            return null;
        }
        //endregion

        // Step #3: Getting the Slot Type
        ContainerSlotTypes sType = ContainerSlotTypes.DISPLAY;
        try { sType = ContainerSlotTypes.valueOf(barSplit[1].toUpperCase()); } catch (IllegalArgumentException ignored) { }
        if (sType == ContainerSlotTypes.RESULT && !(Gunging_Ootilities_Plugin.foundMythicLib || Gunging_Ootilities_Plugin.foundMythicCrucible)) { sType = ContainerSlotTypes.DISPLAY; }

        // Step #4: Validate Filter
        String[] nbtSplit = barSplit[2].split(" ");
        if (nbtSplit.length == 3) {

            // Check if its a valid NBT Test String (With amount :o fancy)
            if (OotilityCeption.IsInvalidItemNBTtestString(nbtSplit[0], nbtSplit[1], nbtSplit[2], barSplit[3], null)) {

                // If its invalid, well then this operation is a failure.
                return null;
            }

        // If the length of nbtSplit was not exactly 3
        } else {

            //If not theres something wrong with that format
            return null;
        }

        // Make final
        GOOPCSlot ret = new GOOPCSlot(sSlot, sType, OotilityCeption.ItemFromNBTTestString(nbtSplit[0], nbtSplit[1], nbtSplit[2], barSplit[3], null));

        // Optional Identifier
        ret.setAlias(sOptID);

        // No more information? We're done.
        if (barSplit.length <= 4) { return ret; }

        // First round: Head
        for (int b = 4; b < barSplit.length; b++) {

            // If long enough
            if (barSplit[b].length() > 3) {

                // Get the Actual Argument
                String argument = barSplit[b].substring(3);
                String identifier = barSplit[b].substring(0, 3);

                //noinspection SwitchStatementWithTooFewBranches
                switch (identifier.toUpperCase()) {
                    case "TXR": //TXR...... (Head Texture String)
                        // Is this even a player head tho lol?
                        if (ret.getContentNonNull(false).getType() == Material.PLAYER_HEAD) {

                            // Get Head
                            ItemStack asHead = OotilityCeption.GetSkullFrom(argument);

                            // Get meta I guess
                            if (!OotilityCeption.IsAirNullAllowed(asHead)) {

                                // If has item meta idk
                                if (asHead.hasItemMeta()) {

                                    // Get Meta
                                    SkullMeta sMeta = (SkullMeta) asHead.getItemMeta();

                                    // Get item
                                    ItemStack iSource = ret.getContentNonNull(false);

                                    // Set Meta
                                    iSource.setItemMeta(sMeta);

                                    // Put it into hither
                                    ret.setContent(iSource);
                                }
                            }
                        }
                        break;
                    default: break;
                }
            }
        }

        // Shall Continue Parsing, whats the type?
        for (int b = 4; b < barSplit.length; b++) {

            // If long enough
            if (barSplit[b].length() > 3) {

                // Get the Actual Argument
                String argument = barSplit[b].substring(3);
                String identifier = barSplit[b].substring(0, 3);

                //CPL//OotilityCeption. Log("#" + sSlot + " \u00a7c:\u00a74:\u00a7c: \u00a77Parsing Identifier: \u00a7c" + identifier + "\u00a77 with argument \u00a7c" + argument);

                switch (identifier.toUpperCase()) {
                    case "COC": //COCgoop nbt
                        ret.addCommandsOnClick(argument);
                        break;
                    case "COS": //COSgoop nbt
                        ret.addCommandsOnStore(argument);
                        break;
                    case "COT": //COTgoop nbt
                        ret.addCommandsOnTake(argument);
                        break;
                    case "HFG": //COTHIDE_ATTRIBUTES,HIDE_UNBREAKABLE

                        // Split
                        ArrayList<String> flagsEncode = new ArrayList<>();

                        // Commas?
                        if (argument.contains(",")) { flagsEncode.addAll(Arrays.asList(argument.split(","))); } else { flagsEncode.add(argument); }

                        // Attempt to parse and add
                        for (String flag : flagsEncode) {

                            try {
                                // Funny flag
                                ItemFlag iFlag = ItemFlag.valueOf(flag);

                                // Include it
                                ret.getContentNonNull(false).addItemFlags(iFlag);

                            } catch (IllegalArgumentException ignored) {}
                        }
                        break;
                    case "NME": //NMECaladbold, Arc of Rainbows
                        ret.setContent(OotilityCeption.RenameItem(ret.getContent(false), argument, null));
                        break;
                    case "CMD": //CMD2
                        PlusMinusPercent cModelData = PlusMinusPercent.GetPMP(argument, null);
                        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0 && cModelData != null) { ret.setContent(OotilityCeption.SetCMD(ret.getContent(false), cModelData, null, null)); }
                        break;
                    case "NCH": //NCHfrost_walker 5
                        if (argument.contains(" ")) {
                            String[] enchantSplit = argument.split(" ");
                            if (!OotilityCeption.IsInvalidItemNBTtestString("e", enchantSplit[0], enchantSplit[1], null)) {
                                ret.setContent(OotilityCeption.EnchantmentOperation(ret.getContent(false), OotilityCeption.GetEnchantmentByName(enchantSplit[0]), PlusMinusPercent.GetPMP(enchantSplit[1], null), null, null));
                            }
                        }
                        break;
                    case "LRE": //LRE000A relic from an age past...
                        if (argument.length() > 3) {
                            String index = argument.substring(0, 3);
                            String lore = argument.substring(3);
                            if (OotilityCeption.IntTryParse(index)) {

                                // Parse the Index
                                int idx = Integer.parseInt(index);

                                // Modify Item
                                ItemStack modified = ret.getContent(false);

                                // Get Lore
                                ArrayList<String> iLore = OotilityCeption.GetLore(modified);

                                int loreSize = iLore.size();

                                // While there is less lore than can be handled without generating an Index Out Of Range thing
                                while (loreSize < idx) {

                                    // Append a lore line at bottom
                                    modified = OotilityCeption.AppendLoreLine(modified, "&r", null, null);

                                    // Get new lore line length
                                    loreSize = OotilityCeption.GetLore(modified).size();
                                }

                                // Put
                                ret.setContent(OotilityCeption.AppendLoreLine(modified, lore, idx, null));
                            }
                        }
                        break;
                    case "RCC": //RCCCLSSMage∪Rogue∪Paladin
                        if (argument.length() > 4) {
                            String index = argument.substring(0, 4);
                            String lore = argument.substring(4);

                            SlotRestriction sr = null;
                            switch (index) {
                                case "CLSS":
                                    sr = GCSR_Class.deserialize(lore);
                                    break;
                                case "LEVL":
                                    sr = GCSR_Level.deserialize(lore);
                                    break;
                                case "UCKB":
                                    sr = GCSR_Unlockable.deserialize(lore);
                                    break;
                                case "PERM":
                                    sr = GCSR_Permission.deserialize(lore);
                                    break;
                            }

                            // Add if success
                            if (sr != null) { ret.addRestriction(sr); }
                        }
                        break;
                    case "RCB": //RCBDESTROY

                        // Parse
                        RestrictedBehaviour rb = null;
                        try { rb = RestrictedBehaviour.valueOf(argument.toUpperCase().replace(" ", "_").replace("-","_")); } catch (IllegalArgumentException ignored) {}

                        // Apply
                        if (rb != null) { ret.setRestrictedBehaviour(rb); }
                        break;
                    case "AMT": //AMTWeapons
                        if (Gunging_Ootilities_Plugin.foundMMOItems) { ret.setTypeMask(ApplicableMask.getMask(argument)); }
                        break;
                    case "AMI": //AMISK_SWORD
                        ret.loadKindIDRestrictions(argument);
                        break;
                    case "EQP": //EQPtrue
                        boolean eqp = false;
                        // Deserialize Equipment Reqs
                        if (OotilityCeption.BoolTryParse(argument)) { eqp = Boolean.parseBoolean(argument); }
                        ret.setForEquipment(eqp);
                        break;
                    default: break;
                }
            }
        }

        // Shall be baked now
        return ret;
    }
    /**
     * Starts fresh and sets the mask entirely from this.
     *
     * @param argument The whole, compressed mask (in a comma separated list)
     */
    public void loadKindIDRestrictions(@Nullable String argument) {
        //CLI// OotilityCeption.Log("\u00a78SLOT \u00a7bSNZ\u00a77 Editing ID Mask by\u00a7e " + argument + "\u00a77 of slot \u00a73#" + getSlotNumber());

        // Start Fresh
        getKindBlacklist().clear();
        getKindWhitelist().clear();
        getIdBlacklist().clear();
        getIdWhitelist().clear();

        if (argument == null) { return; }

        // Split argument by kommas
        if (argument.contains(",")) {

            // Just go through each
            for (String str : argument.split(",")) { includeInKinds(str); }

            // No
        } else {

            // Include as-is
            includeInKinds(argument); }
    }
    /**
     * Actually parses this kind or ID, somehow identifying which it is?
     *
     * @param part Either a kind or an ID restriction
     */
    public void includeInKinds(@Nullable String part) {
        //CLI// OotilityCeption.Log("\u00a78SLOT \u00a7bSNZ\u00a77 Incliding kind by\u00a7e " + part + "\u00a77 of slot \u00a73#" + getSlotNumber());

        // No
        if (part == null || part.isEmpty()) { return; }

        // Caps
        part = part.toUpperCase().replace(" ", "_").replace("-", "_");

        // Counter?
        boolean counter = false; String p = part;
        if (part.startsWith("!")) { counter = true; p = part.substring(1); }

        // Is it a kind restriction
        try {
            KindRestriction asKind = KindRestriction.valueOf(part);

            // Success by Kind
            if (counter) { addToBlacklist(asKind); } else { addToWhitelist(asKind); }
            return;

        } catch (IllegalArgumentException ignored) {}

        // Did not parse as kind so we load it as an ID Mask
        if (!Gunging_Ootilities_Plugin.foundMMOItems) { return; }
        if (counter) { addToBlacklist(p); } else { addToWhitelist(p); }
    }

    /**
     * @return Basically just puts the ID and Kinds mask as a comma-separated list.
     *         Apparently, for the sole use of /goop containers config view
     */
    @Nullable public String maskToString() {

        // No
        if (!hasKindMasks() && !hasIDMasks()) { return null; }

        StringBuilder serialized = new StringBuilder();

        // For every one
        for (String str : getIdWhitelist()) { serialized.append(str).append(","); }

        // For every one
        for (String str : getIdBlacklist()) { serialized.append("!").append(str).append(","); }

        // For every one
        for (KindRestriction str : getKindWhitelist()) { serialized.append(str.toString()).append(","); }

        // For every one
        for (KindRestriction str : getKindBlacklist()) { serialized.append("!").append(str.toString()).append(","); }

        // Delete final comma
        serialized.deleteCharAt(serialized.length() - 1);

        // Build
        return serialized.toString();
    }
    //endregion
}
