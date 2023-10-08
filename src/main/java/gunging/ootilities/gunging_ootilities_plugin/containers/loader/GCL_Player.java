package gunging.ootilities.gunging_ootilities_plugin.containers.loader;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISLInventory;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Does basically nothing except provide symmetry for the other GCL classes.
 */
public class GCL_Player implements Listener {

    /**
     * Load the list of players who are bound to this template
     */
    public static void load(@NotNull GOOPCPlayer player) {
        GOOPCTemplate template = player.getTemplate();
        //String error = "Error when loading personal container \u00a73" + template.getInternalName() + "\u00a77 for player \u00a6e " + owner.toString() + "\u00a77: \u00a7c";
        //String warning = "Warning when loading personal container \u00a73" + template.getInternalName() + "\u00a77 for player \u00a6e " + owner.toString() + "\u00a77: \u00a76";

        // Get the pair
        FileConfigPair csPair = getStorage(template.getInternalName());

        // Load the YML from it
        YamlConfiguration storage = csPair.getStorage();

        // Get the content ItemStacks and their Indices
        ArrayList<String> rawUUIDs = new ArrayList<>(storage.getStringList(YML_PLAYER_LIST));

        // Compile active for players
        ArrayList<UUID> uuids = new ArrayList<>();
        for (String rawSeen : rawUUIDs) {
            UUID parsed = OotilityCeption.UUIDFromString(rawSeen);
            if (parsed == null)  { continue; }

            // Include
            uuids.add(parsed);
        }

        // Set the damn inventory
        player.loadActiveFor(uuids);
    }
    /**
     * Load a Station Container
     *
     * @param inven Container to register. Unregisters any previous ones of the same template Name / ID
     */
    public static void live(@NotNull GOOPCPlayer inven) {

        // Unloads any previous one of the same qualifications
        unload(inven.getTemplate().getInternalID());
        unload(inven.getTemplate().getInternalName());

        // Register in the arrays
        byInternalID.put(inven.getTemplate().getInternalID(), inven);
        byInternalName.put(inven.getTemplate().getInternalName(), inven);
        loaded.add(inven);
    }

    /**
     * Unload a station container by the ID of its template.
     *
     * @param byID Internal ID of the Template of the Station Container to unload.
     */
    public static void unload(@Nullable Long byID) {
        if (byID == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCPlayer station = loaded.get(i);

            // Is it the one? No? Skip
            if (station.getTemplate().getInternalID() != byID) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(station.getTemplate().getInternalName());
            byInternalID.remove(station.getTemplate().getInternalID());

            // That's all
            break;
        }
    }
    /**
     * Unload a station container by the name of its template.
     *
     * @param byName Internal Name of the Template of the Station Container to unload.
     */
    public static void unload(@Nullable String byName) {
        if (byName == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCPlayer station = loaded.get(i);

            // Is it the one? No? Skip
            if (!station.getTemplate().getInternalName().equals(byName)) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(station.getTemplate().getInternalName());
            byInternalID.remove(station.getTemplate().getInternalID());

            // That's all
            break;
        }
    }
    /**
     * Clears all arrays
     */
    public static void unloadAll() { loaded.clear(); byInternalName.clear(); byInternalID.clear(); loadedPerPlayer.clear(); }

    /**
     * Saves all the attributes of all loaded templates
     */
    public static void saveAllLoaded() { for (GOOPCPlayer player : getLoaded()) { saveActives(player); } }
    /**
     * Saves the players this container is active for
     *
     * @param player Player Container to save
     */
    public static void saveActives(@NotNull GOOPCPlayer player) {

        // Find its file
        FileConfigPair directory = getStorage(player.getTemplate().getInternalName());
        directory = Gunging_Ootilities_Plugin.theMain.GetLatest(directory);

        // Modify Storage
        YamlConfiguration storage = directory.getStorage();

        // Active Players
        ArrayList<UUID> actives = player.getActiveFor();
        ArrayList<String> serializedActives = new ArrayList<>();
        for (UUID uid : actives) { if (uid != null) { serializedActives.add(uid.toString()); }}
        storage.set(YML_PLAYER_LIST, serializedActives.size() > 0 ? serializedActives : null);

        // Is it the default one :O ?
        storage.set(YML_PLAYER_DEFAULT, player.isDefaulted());

        // Actually Save
        Gunging_Ootilities_Plugin.theMain.SaveFile(directory);
    }

    //region Who is managed by which inventory?
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnPlayerJoin(PlayerJoinEvent event) {

        // Load their inventory
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnPlayerLeave(PlayerQuitEvent event) {

        // Unload their inventory
        clearInventoryFor(event.getPlayer());
    }

    /**
     * @return The inventory for all players with no inventory forcibly active.
     */
    @Nullable public static GOOPCPlayer getDefaultInventory() { return defaultInventory; }
    @Nullable static GOOPCPlayer defaultInventory;
    /**
     * @param base All the players without an inventory manually set active
     *             will have this now.
     */
    public static void setDefaultInventory(@Nullable GOOPCPlayer base) {

        // Un-default previous
        GOOPCPlayer previous = defaultInventory;
        if (previous != null) {
            previous.setDefaulted(false);
            saveActives(previous); }

        // Default new
        defaultInventory = base;
        if (base == null) { return; }
        base.setDefaulted(true);
        saveActives(base);

        // Clear
        loadedPerPlayer.clear();
    }
    /**
     * Will not save any changes in the default container...
     * <b>Call only if you know what you are doing.</b>
     *
     * @param base All the players without an inventory manually set active
     *             will have this now.
     */
    public static void setDefaultInventoryNoSave(@Nullable GOOPCPlayer base) {

        // Un-default previous
        GOOPCPlayer previous = defaultInventory;
        if (previous != null) {
            previous.setDefaulted(false); }

        // Default new
        defaultInventory = base;
        if (base == null) { return; }
        base.setDefaulted(true);
    }
    /**
     * Will not check for premium being enabled.
     *
     * @param observer Player to know which inventory is overriding for them
     *
     * @return The inventory overriding this players inventory, which <b>not necessarily is</b>
     *         the one currently actually applied. For example, in CREATIVE or SPECTATOR mode,
     *         these functions are disabled.
     */
    @Nullable public static GOOPCPlayer getOverridingInventoryFor(@Nullable UUID observer) {
        if (observer == null) { return null; }

        // Search among active
        for (GOOPCPlayer player : getLoaded()) {if (player.isActiveFor(observer)) { return player; } }

        // None was
        return null;
    }
    /**
     * Will check for premium being enabled.
     *
     * @param observer Player that you are checking.
     *
     * @return Which player inventory container manages this player?
     *
     * @see #getInventoryFor(UUID) This method does not check for premium being enabled
     */
    @Nullable public static GOOPCPlayer getInventoryFor(@Nullable OfflinePlayer observer) {
        if (observer == null) { return null; }

        // Creative or Spectator return null
        if (observer.isOnline() && observer.getPlayer() != null &&
                (observer.getPlayer().getGameMode() == GameMode.CREATIVE
                || observer.getPlayer().getGameMode() == GameMode.SPECTATOR)) { return null; }

        // Non op and un-purchased is disabled
        //if (!GOOPCManager.isPremiumEnabled() && !observer.isOp()) { return null; }

        // Yes
        return getInventoryFor(observer.getUniqueId());
    }
    /**
     * @param observer Player to remove from cached overridden inventories
     */
    public static void clearInventoryFor(@Nullable OfflinePlayer observer) {
        if (observer == null) { return; }
        clearInventoryFor(observer.getUniqueId());
    }
    /**
     * @param observer Player to remove from cached overridden inventories
     */
    public static void clearInventoryFor(@Nullable UUID observer) { loadedPerPlayer.remove(observer); }
    /**
     * Will not check for premium being enabled.
     *
     * @param observer Player that you are checking.
     *
     * @return Which player inventory container manages this player?
     *
     * @see #getInventoryFor(OfflinePlayer) This method does check for premium being enabled
     */
    @Nullable public static GOOPCPlayer getInventoryFor(@Nullable UUID observer) {
        if (observer == null) { return null; }

        // Perhaps its loaded idk
        GOOPCPlayer fromLoaded = loadedPerPlayer.get(observer);
        if (fromLoaded != null) { return fromLoaded; }

        // Search among active
        for (GOOPCPlayer player : getLoaded()) {

            // Active? That's the one
            if (player.isActiveFor(observer)) {

                fromLoaded = player;
                break;
            }
        }

        // Default if null
        if (fromLoaded == null) { fromLoaded = defaultInventory; }

        // Remember and Return
        loadedPerPlayer.put(observer, fromLoaded);
        return fromLoaded;
    }
    @NotNull static final HashMap<UUID, GOOPCPlayer> loadedPerPlayer = new HashMap<>();

    /**
     * @param player Player to whom check that they are using the GOOPCPlayer
     *               assigned to them.
     *
     * @param onlyDynamic 'Dynamic' is the crafting slots. They tend to get cleared A LOT for a plethora of reasons.
     *                    Thus, call this method with that true to only regenerate the dynamic slots.
     *
     * @see GOOPCPlayer#applyTo(Player, boolean)
     */
    public static void corroborateInventory(@NotNull Player player, boolean onlyDynamic) {

        // Get the inventory they are supposed to be using
        GOOPCPlayer rpg = GCL_Player.getInventoryFor(player);

        // Remove any default items
        if (rpg == null) {

            // Remove the RPG Inventory stuff
            cleanseInventory(player, onlyDynamic);

        // Apply
        } else {

            // To the player
            rpg.applyTo(player, onlyDynamic);
        }
    }
    public static void cleanseInventory(@NotNull Player player, boolean onlyDynamic) {

        // Get the inventory they are supposed to be using
        int bound = onlyDynamic ? -7 : 36;

        // Well for every item in the player's inventory yea
        for (int slot = -12; slot < bound; slot++) {
            //CRR//OotilityCeption.Log("\u00a78GPC\u00a79 CORR\u00a77 Seeking slot\u00a76 #" + slot);

            // Temporary Item Stack for evaluation
            ISLInventory location = OotilityCeption.getItemFromPlayer(player, slot, true);
            if (location == null) {
                //CRR//OotilityCeption.Log("\u00a78GPC\u00a79 CORR\u00a7c Invalid Location");
                continue;
            }

            // Item Found
            ItemStack item = location.getItem();

            // Default items do not count
            if (GOOPCManager.isDefaultItem(item)) {
                //CRR//OotilityCeption.Log("\u00a78GPC\u00a79 CORR\u00a78 Item was a DEFAULT item, nulling. ");
                location.setItem(null);
            }
        }
    }
    //endregion

    /**
     * @return The Station Containers currently loaded (one for every loaded template)
     */
    @NotNull public static ArrayList<GOOPCPlayer> getLoaded() { return loaded; }
    @NotNull final static ArrayList<GOOPCPlayer> loaded = new ArrayList<>();

    /**
     * @return The Station Containers currently loaded (by its template internal ID)
     */
    @NotNull public static HashMap<Long, GOOPCPlayer> getByInternalID() { return byInternalID; }
    /**
     * @param id ID Currently assigned at this template
     *
     * @return The Station Containers currently loaded (by its template internal ID)
     */
    @Nullable public static GOOPCPlayer getByInternalID(@Nullable Long id) { if (id == null) { return null; } return getByInternalID().get(id); }
    @NotNull final static HashMap<Long, GOOPCPlayer> byInternalID = new HashMap<>();
    /**
     * @param internalID ID that you want to see if its loaded
     *
     * @return If there is a Station Containers loaded by this ID (its template internal ID)
     */
    public static boolean isPlayerContainer(long internalID) { return getByInternalID(internalID) != null; }

    /**
     * The file where this containers saves the players it is active for.
     *
     * @param internalName Name of the target template
     *
     * @return Where are the contents of this Personal Container stored?
     */
    @NotNull public static FileConfigPair getStorage(@NotNull String internalName) {
        FileConfigPair pair = filesByName.get(internalName);
        if (pair != null) { return pair; }

        // Create File
        pair = Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-instances/player", internalName + ".yml", false, true);

        // Load File
        filesByName.put(internalName, pair);

        // Yes
        return pair;
    }
    @NotNull final static HashMap<String, FileConfigPair> filesByName = new HashMap<>();

    /**
     * @return The Station Containers currently loaded (by its template internal name)
     */
    @NotNull public static HashMap<String, GOOPCPlayer> getByInternalName() { return byInternalName; }
    /**
     * @param internalName Name of this template
     *
     * @return The Station Containers currently loaded (by its template internal name)
     */
    @Nullable public static GOOPCPlayer getByInternalName(@Nullable String internalName) { if (internalName == null) { return null; } return getByInternalName().get(internalName); }
    @NotNull final static HashMap<String, GOOPCPlayer> byInternalName = new HashMap<>();
    /**
     * @param internalName Name that you want to see if its loaded
     *
     * @return If there is a Station Containers loaded by this name (its template internal name)
     */
    public static boolean isPlayerContainer(@Nullable String internalName) { return getByInternalName(internalName) != null; }

    //region YML Configuration Names
    static final String YML_PLAYER_LIST = "Active";
    static final String YML_PLAYER_DEFAULT = "Defaulted";
    //endregion
}
