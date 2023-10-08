package gunging.ootilities.gunging_ootilities_plugin.containers.player;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.containers.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.interaction.CIRClickType;
import gunging.ootilities.gunging_ootilities_plugin.containers.interaction.ContainersInteractionResult;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Player;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Templates;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerOpeningReason;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerSlotEdges;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISLInventory;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Attempts to translate operations between player inventory
 * and GooP Containers Listeners
 */
public class GOOPCPlayer extends GOOPCDeployed {

    /**
     * Manage the loading and unloading of Player-Type containers.
     *
     * @param template         Template followed by this container
     */
    public GOOPCPlayer(@NotNull GOOPCTemplate template) { super(template); }

    //region Load & Unload
    /**
     * @return Is this the container that manages players with no active container?
     */
    public boolean isDefaulted() { return defaulted; }
    boolean defaulted;
    /**
     * @param defaulted  Is this the container that manages players with no active container?
     */
    public void setDefaulted(boolean defaulted) { this.defaulted = defaulted; }
    
    @NotNull final ArrayList<UUID> activeFor = new ArrayList<>();
    /**
     * @return List of players who use this template
     */
    @NotNull public ArrayList<UUID> getActiveFor() { return activeFor; }
    /**
     * @param player Player who you want to know if they is using this
     *
     * @return If this player has this container active for them
     */
    public boolean isActiveFor(@Nullable UUID player) { return player != null && activeFor.contains(player); }
    /**
     * <b>Only call if you really know what you are doing.</b>
     *
     * @param player Player to add to the list of actives
     *
     * @see #setActiveFor(UUID)
     */
    public void addActiveFor(@NotNull UUID player) { activeFor.add(player); }
    /**
     * <b>You probably want to save after calling.</b>
     *
     * @param player Player to remove from the list of actives
     *
     * @see gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Player#saveActives(GOOPCPlayer)
     */
    public void removeActiveFor(@Nullable UUID player) { activeFor.remove(player); GCL_Player.clearInventoryFor(player); }
    /**
     * This method will remove the player from being active from
     * its previous inventory container where they were active,
     * and save those changes..
     *
     * @param player Player for which this is now the active player inventory container
     */
    public void setActiveFor(@NotNull UUID player) {

        // Remove previous
        GOOPCPlayer rpg = GCL_Player.getInventoryFor(player);
        if (rpg != null) { rpg.removeActiveFor(player); GCL_Player.saveActives(rpg);}

        // Add to this
        addActiveFor(player);
        GCL_Player.saveActives(this);
    }
    /**
     * @param player Player to add to the list of actives
     */
    public void loadActiveFor(@NotNull ArrayList<UUID> player) { activeFor.clear(); activeFor.addAll(player); }

    /**
     * @param player Player whose inventory to apply this onto.
     *
     *               Will drop all items in conflicting slots to the ground.
     *
     * @param onlyDynamic 'Dynamic' is the crafting slots. They tend to get cleared A LOT for a plethora of reasons.
     *                    Thus, call this method with that true to only regenerate the dynamic slots.
     */
    public void applyTo(@NotNull Player player, boolean onlyDynamic) {
        int bound = onlyDynamic ? -7 : 36;

        // Well for every item in the player's inventory yea
        for (int slot = -12; slot < bound; slot++) {

            // Temporary Item Stack for evaluation
            ISLInventory location = OotilityCeption.getItemFromPlayer(player, slot, true);
            if (location == null) {
                //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a7c Invalid Location #" + slot);
                continue; }

            // Item Expected
            GOOPCSlot display = getTemplate().getSlotAt(location.getSlot());
            if (display == null) {
                //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a7c Unknown Template Slot #" + location.getSlot());
                continue; }

            // Apply
            applyToLocation(player, location, display, slot != -8);
        }
    }
    /**
     * @param player Player whose inventory to apply this onto.
     *
     * @param location Inventory slot to apply
     *
     * @param allowDropping Should drop all items in conflicting slot to the ground?
     *
     * @param display Slot template to follow
     */
    public void applyToLocation(@NotNull Player player, @NotNull ISLInventory location, @NotNull GOOPCSlot display, boolean allowDropping) {

        // Item Found
        ItemStack item = location.getItem();

        // Default items do not count
        if (GOOPCManager.isDefaultItem(item)) {
            //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a78 Item was a DEFAULT item, nulling. ");
            item = null; }

        //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a77 Applying slot\u00a7e #" + location.getSlot());
        //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a7e +\u00a77 Current Item:\u00a7f " + OotilityCeption.GetItemName(item));
        //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a7e +\u00a77 Display Item:\u00a7f " + OotilityCeption.GetItemName(display.getContent()));

        // If for display or edge
        boolean dropItem = display.isForEdge() || display.isForDisplay();

        // Restrictions of slot and player?
        if (display.isForStorage()) {

            // If it cannot be stored in this slot, drop it.
            if (!dropItem && !display.canStore(item)) {
                //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a77 Dropping due\u00a76 Storage");
                dropItem = true; }

            // If the player does not meet restrictions, drop it
            if (!dropItem && !display.matchesRestrictions(player)) {
                //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a77 Dropping due\u00a76 Restrictions");
                dropItem = true; }

            // Did it not get dropped?
            if (!dropItem && !OotilityCeption.IsAirNullAllowed(item)) {
                //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a77 Not dropped, running commands on store. ");

                // Run commands on store
                ContainersInteractionResult result = new ContainersInteractionResult(null, CIRClickType.STORE);
                result.addCommands(display.getCommandsOnStore(), display.getSlotNumber(), true);

                // Run commands
                for (String cmd : result.getCommands()) {
                    //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a77 Sending Command \u00a73" + cmd);

                    // Run commands, parse placeholders
                    OotilityCeption.SendConsoleCommand(GOOPCManager.parseAsContainers(cmd, null), player, player, null, null);
                }
            }
        }

        // Drop it
        if (dropItem && !OotilityCeption.IsAirNullAllowed(item)) {
            //APY//OotilityCeption.Log("\u00a78GPC\u00a73 APPLY\u00a77 Dropping Item");

            // Make a copy
            ItemStack droppable = new ItemStack(item);

            // Set to air
            location.setItem(null);
            item = null;

            // Drop it in world
            if (allowDropping) {

                // Some slots (Crafting result slot!) must not, in general, drop their contents.
                Item dropped = player.getWorld().dropItem(player.getLocation(), droppable);
                dropped.setPickupDelay(0);
            }
        }

        // Set whatever remains
        location.setItem(item);
    }
    //endregion

    //region Observed Container
    /**
     * Not Supported
     *
     * @return false
     */
    @Override public boolean isLooking(@Nullable UUID player) { return false; }

    /**
     * Not Supported
     *
     * @return empty map
     */
    @Override public @NotNull HashMap<Long, ContainerInventory> getOpenedByID() { return new HashMap<>(); }

    /**
     * Not Supported
     *
     * @return null
     */
    @Override @Nullable public ContainerInventory getOpenedInstance(@Nullable Long oid) { return null; }

    /**
     * Not Supported
     *
     * @return null
     */
    @Override @Nullable public ContainerInventory getObservedBy(@Nullable UUID player) { return null; }

    /**
     * Not Supported
     *
     * @return null
     */
    @Override @Nullable public Player getPrincipalObserver(@Nullable Long id) { return null; }
    //endregion

    //region Inspector
    /**
     * Not Supported. Empty method.
     */
    @Override public void updateSeen(long id, @NotNull UUID user, @NotNull String message) { }

    /**
     * Not Supported
     *
     * @return empty list
     */
    @Override public @NotNull ArrayList<String> getSerializableSeens(@Nullable Long id) { return new ArrayList<>(); }

    /**
     * Not Supported
     *
     * @return empty list
     */
    @Override public @NotNull HashMap<UUID, NameVariable> getSeens(@Nullable Long id) { return new HashMap<>(); }
    //endregion

    //region Usage
    /**
     * Not Supported. Empty method.
     */
    @Override public void openForPlayer(@NotNull Player opener, @NotNull ContainerOpeningReason reason) { }

    /**
     * Not Supported. Empty method.
     */
    @Override public void openForPlayer(@NotNull Player opener, long id, @NotNull ContainerOpeningReason reason) { }

    /**
     * Not Supported. Empty method.
     */
    @Override public void closeForPlayer(@NotNull Player opener) { }

    /**
     * Not Supported.
     * @return null
     */
    @Override public @Nullable ItemStack getObservedItem(@Nullable UUID observer, @Nullable Integer slot) { return null; }
    //endregion

    //region Edition
    public static final int EDITION_SLOT_HEAD = 11;
    public static final int EDITION_SLOT_CHEST = 20;
    public static final int EDITION_SLOT_LEGS = 29;
    public static final int EDITION_SLOT_BOOTS = 38;

    public static final int EDITION_SLOT_CRAFT_RESULT = 17;
    public static final int EDITION_SLOT_CRAFT_UL = 14;
    public static final int EDITION_SLOT_CRAFT_UR = 15;
    public static final int EDITION_SLOT_CRAFT_BL = 23;
    public static final int EDITION_SLOT_CRAFT_BR = 24;

    public static final int EDITION_SLOT_OFFHAND = 41;

    /**
     * @param cont Continuous byt more as in, the usual inventory slot name ~ 100-103 for armor or such,
     *             but this time transformed into format of the /goop containers config view
     *
     * @return Format of the /goop containers config view
     */
    public static int continuousToEdition(int cont) {
        switch (cont) {
            case 100: return EDITION_SLOT_BOOTS;
            case 101: return EDITION_SLOT_LEGS;
            case 102: return EDITION_SLOT_CHEST;
            case 103: return EDITION_SLOT_HEAD;
            case -106: return EDITION_SLOT_OFFHAND;

            case 80: return EDITION_SLOT_CRAFT_UL;
            case 81: return EDITION_SLOT_CRAFT_UR;
            case 82: return EDITION_SLOT_CRAFT_BL;
            case 83: return EDITION_SLOT_CRAFT_BR;
            case 84: return EDITION_SLOT_CRAFT_RESULT;
            default: return -1;
        }
    }

    /**
     * **Using {@link InventoryClickEvent#getSlot()}**, not the {@link InventoryClickEvent#getRawSlot()},
     * will convert into the correct armor slots because for some reason they are 36 through 39 instead of
     * the usual 100 through 103 or whatever.
     *
     * @param cont {@link InventoryClickEvent#getSlot()}
     *
     *
     * @return Format of the {@link GOOPCTemplate#getSlotsContent()}
     */
    public static int eventToContinuous(int cont) {
        switch (cont) {
            case 36: return 100;
            case 37: return 101;
            case 38: return 102;
            case 39: return 103;
            case 40: return -106;
            default: return cont;
        }
    }

    @NotNull public static HashMap<Integer, ItemStack> clonePlayerEditionContents() {

        // Create map
        HashMap<Integer, ItemStack> ret = new HashMap<>();

        // Deep clone
        for (Map.Entry<Integer, ItemStack> entry : getPlayerEditionContents().entrySet()) {
            //CRP//OotilityCeption.Log("\u00a78GPCPlayer \u00a7bVIEW\u00a77 Cloned at\u00a73 " + entry.getKey() + "\u00a77 ~ " + OotilityCeption.GetItemName(entry.getValue()));

            // Skip
            if (entry.getKey() == null || entry.getValue() == null) { continue; }

            // Clone that stuff
            ret.put(entry.getKey(), new ItemStack(entry.getValue()));
        }

        // Yes
        return ret;
    }
    @NotNull static HashMap<Integer, ItemStack> playerEditionContents = new HashMap<>();
    @NotNull public static HashMap<Integer, ItemStack> getPlayerEditionContents() {
        if (playerEditionContents.size() > 5) { return playerEditionContents; }

        // Fill of barrier
        for (int i = 0; i < 54; i++) {

            // Choose display stack
            ItemStack display;
            boolean showcase = false;

            switch (i) {

                // Barrier slots
                default:
                    // Modify the 'default Item'
                    display = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

                    // Sets to the correct CustomModelData
                    if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14) {

                        // Set Custom Model Data
                        ItemMeta meta = display.getItemMeta();
                        meta.setCustomModelData(GOOPCTemplate.EDGE_FORMATIONS_CMD_START + ContainerSlotEdges.MAINLAND.ordinal());
                        display.setItemMeta(meta);
                    }

                    // Encrypt it again I guess
                    display = OotilityCeption.NameEncrypt(display, GOOPCTemplate.EDGE_ENCRYPTION_CODE);
                    break;

                // Hint slots
                case (EDITION_SLOT_HEAD - 1): showcase = true; display = OotilityCeption.RenameItem(new ItemStack(Material.CHAINMAIL_HELMET), "\u00a77▣ \u00a7eHead Armor slot: ", null); break;
                case (EDITION_SLOT_CHEST - 1): showcase = true; display = OotilityCeption.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "\u00a77▣ \u00a7eChest Armor slot: ", null); break;
                case (EDITION_SLOT_LEGS - 1): showcase = true; display = OotilityCeption.RenameItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), "\u00a77▣ \u00a7eLegs Armor slot: ", null); break;
                case (EDITION_SLOT_BOOTS - 1): showcase = true; display = OotilityCeption.RenameItem(new ItemStack(Material.CHAINMAIL_BOOTS), "\u00a77▣ \u00a7eBoots Armor slot: ", null); break;
                case (EDITION_SLOT_CRAFT_UL - 1): showcase = true; display = OotilityCeption.RenameItem(new ItemStack(Material.CRAFTING_TABLE), "\u00a73▣ \u00a7eCrafting slots: ", null); break;
                case (EDITION_SLOT_OFFHAND - 1): showcase = true; display = OotilityCeption.RenameItem(new ItemStack(Material.SHIELD), "\u00a7b▣ \u00a7eOffhand slot: ", null); break;

                // Air slots
                case EDITION_SLOT_HEAD:
                case EDITION_SLOT_CHEST:
                case EDITION_SLOT_LEGS:
                case EDITION_SLOT_BOOTS:
                case EDITION_SLOT_CRAFT_UL:
                case EDITION_SLOT_CRAFT_UR:
                case EDITION_SLOT_CRAFT_BL:
                case EDITION_SLOT_CRAFT_BR:
                case EDITION_SLOT_CRAFT_RESULT:
                case EDITION_SLOT_OFFHAND:

                    // Air item
                    display = new ItemStack(Material.AIR); break;
            }

            if (showcase && display != null && display.hasItemMeta()) {

                ItemMeta meta = display.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                display.setItemMeta(meta);
            }

            // All right build it
            playerEditionContents.put(i, display);
        }

        // Yes
        return playerEditionContents;
    }

    /**
     * Displays a debugging view of the Container Template to this player
     *
     * @param template Template to preview / debug
     * @param player Player who will see it
     */
    public static void previewContainerContents(@NotNull GCT_PlayerTemplate template, @NotNull Player player) {

        // Generate preview
        ContainerInventory generated = new ContainerInventory(template, GOOPCManager.getReasonProcessOf(ContainerOpeningReason.EDITION_PLAYER_COMMANDS), player);

        // That's what we needed
        Inventory inven = generated.getInventory();

        // Update
        generated.updateInventory();

        // Open for player
        player.openInventory(inven);

        // Rename every item for its slots
        editionItemSet(inven, template, 103);
        editionItemSet(inven, template, 102);
        editionItemSet(inven, template, 101);
        editionItemSet(inven, template, 100);
        editionItemSet(inven, template, -106);
        editionItemSet(inven, template, 80);
        editionItemSet(inven, template, 81);
        editionItemSet(inven, template, 82);
        editionItemSet(inven, template, 83);
        editionItemSet(inven, template, 84);
    }
    static void editionItemSet(@NotNull Inventory inven, @NotNull GCT_PlayerTemplate template, int i) {
        GOOPCSlot slot = template.getSlotAt(i);

        /*
         * If the slot does not exist... just a glass pane with the index
         */
        if (slot == null) {

            // Just the number I guess... This should never really happen
            inven.setItem(i, OotilityCeption.RenameItem(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE), "\u00a73[ \u00a7e" + i + "\u00a73 ]", null));
            return;
        }

        // Set config view item
        inven.setItem(GOOPCPlayer.continuousToEdition(slot.getSlotNumber()), slot.toConfigView());
    }

    /**
     * Marks a player to currently be in PHASE 1 - DISPLAY of a container. <br>
     * Opens the edition inventory for that player.
     *
     * @param editor Player editing the container's contents.
     * @param template Template being edited.
     */
    public static void editionBegin(@NotNull Player editor, @NotNull GCT_PlayerTemplate template) {

        /*
         * Assumes that it is called continuous to {@link GOOPCManager#editionFinalize(Player, GOOPCTemplate, Inventory)
         */

        // Build ye default inventory
        ContainerInventory inven = new ContainerInventory(template, GOOPCManager.getReasonProcessOf(ContainerOpeningReason.EDITION_PLAYER_DISPLAY), editor);
        Inventory actual = inven.getInventory();

        // Place the contents
        inven.updateInventory();

        // Open it
        editor.openInventory(actual);
    }

    /**
     * The editor must already be in PHASE 1 - DISPLAY, moves them forward to PHASE 2 - STORAGE <br>
     * Opens the storage edition for the player, as well.
     *
     * @param editor Player editing the container's contents.
     * @param template Template being edited.
     * @param displayEditionResult Inventory resultant from display edit operation.
     */
    public static void editionAdvance(@NotNull Player editor, @NotNull GCT_PlayerTemplate template, @NotNull Inventory displayEditionResult) {

        // Store reference
        GOOPCManager.getDisplayEditionResults().put(editor.getUniqueId(), displayEditionResult);

        // Evaluate if closing next tick
        (new BukkitRunnable() {
            public void run() {

                // Build ye default inventory
                ContainerInventory inven = new ContainerInventory(template, GOOPCManager.getReasonProcessOf(ContainerOpeningReason.EDITION_PLAYER_STORAGE), editor);
                Inventory actual = inven.getInventory();

                /*
                 * All edge materials will now become the true edge.
                 *
                 * This will be copied from the just-closed result inventory
                 * into the newly generated edition one.
                 */
                for (int i = 0; i < actual.getSize(); i++) {

                    // Get from recently-closed display edition inventory
                    ItemStack item = displayEditionResult.getItem(i);

                    // Is it an actual item?
                    if (OotilityCeption.IsAirNullAllowed(item)) {
                        actual.setItem(i, null); continue;}

                    // Is it the edge material?
                    if (!OotilityCeption.IsEncrypted(item, GOOPCTemplate.EDGE_ENCRYPTION_CODE)) {
                        actual.setItem(i, item); continue; }

                    // Modify the 'default Item'
                    item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

                    // Sets to the correct CustomModelData
                    if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14) {

                        // Set Custom Model Data
                        ItemMeta meta = item.getItemMeta();
                        meta.setCustomModelData(GOOPCTemplate.EDGE_FORMATIONS_CMD_START + ContainerSlotEdges.MAINLAND.ordinal());
                        item.setItemMeta(meta);
                    }

                    // Encrypt it again I guess
                    item = OotilityCeption.NameEncrypt(item, GOOPCTemplate.EDGE_ENCRYPTION_CODE);

                    // Replace the item in the newly generated inventory
                    actual.setItem(i, item);
                }

                // Now it has all default slots, open it
                editor.openInventory(actual);

            }

        }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
    }

    /**
     * The player has finished PHASE 2 - STORAGE. Now its time to compare it to PHASE 1 - DISPLAY
     * in order to build, apply, and save the new content setup.
     *
     * @param editor Player editing the container's contents.
     * @param template Template being edited.
     * @param storageEditionResult Inventory resultant from storage edit operation.
     */
    public static void editionFinalize(@NotNull Player editor, @NotNull GCT_PlayerTemplate template, @NotNull Inventory storageEditionResult) {

        // Get Display Resultant
        Inventory displayEditionResult = GOOPCManager.getDisplayEditionResults().get(editor.getUniqueId());

        // These are fresh from being edited anyway, should be ready to add these new special slots
        HashMap<Integer, GOOPCSlot> refinedContents = template.getSlotsContent();

        // Examine thay inventories contents, add them to the existing refined contents
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult, 103);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,102);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,101);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,100);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,-106);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,80);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,81);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,82);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,83);
        editionBuild(refinedContents, template, storageEditionResult, displayEditionResult,84);

        // Update the special slots
        template.loadUniqueSlots(new ArrayList<>(refinedContents.values()));

        // Save changes
        template.processContentEdges();
        GCL_Templates.saveTemplate(template);

        // Basically cancel lol
        GOOPCManager.unregisterEdition(editor.getUniqueId());
    }
    static void editionBuild(@NotNull HashMap<Integer, GOOPCSlot> refinedContents, @NotNull GCT_PlayerTemplate template, @NotNull Inventory storageEditionResult, @NotNull Inventory displayEditionResult, int s) {

        // Identify Items
        ItemStack storage = storageEditionResult.getItem(GOOPCPlayer.continuousToEdition(s));
        ItemStack display = displayEditionResult.getItem(GOOPCPlayer.continuousToEdition(s));

        // Identify slots (to copy commands and such)
        refinedContents.put(s, GOOPCSlot.fromComparison(display, storage, template.getSlotAt(s), s));
    }
    //endregion
}
