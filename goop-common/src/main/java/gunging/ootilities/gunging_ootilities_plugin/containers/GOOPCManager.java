package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionAttributes;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GCT_PlayerTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ISLObservedContainer;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.chunks.ChunkMap;
import gunging.ootilities.gunging_ootilities_plugin.misc.chunks.ChunkMapEntry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.nio.cs.DoubleByte;

import javax.swing.*;
import java.nio.charset.CharsetEncoder;
import java.util.*;

/**
 * Manages the loading and editing of container templates.
 */
public class GOOPCManager {

    //region Manager Init
//    /**
//     * Loads the Containers Engine
//     */
//    public static void enable() { if (!Gunging_Ootilities_Plugin.theMain.HasLoaded()) { if (Gunging_Ootilities_Plugin.theMain.IsLoading()) { premiumCOEnabled = true;  } } }
//
//    /**
//     * @return If the premium key was detected.
//     */
//    public static boolean isPremiumEnabled() { return premiumCOEnabled; }
//    protected static boolean premiumCOEnabled = false;
//
//    /**
//     * @param config Configuration
//     */
//    public static void enableLiteVersions(@NotNull FileConfiguration config) {
//
//        // Yeah
//        enableLiteEquipment(config.getString("Containers.LIT"));
//        enableEntitiesInSlash(config.getString("MythicMobs.EIS"));
//        enableLocationsInSlash(config.getString("MythicMobs.LIS"));
//    }
//
//    /**
//     * Loads the Containers Engine
//     */
//    static void enableLiteEquipment(@Nullable String str) {
//        //PRM//OotilityCeption.Log("\u00a78LITE\u00a7b INPUT\u00a77 " + str);
//        if (str == null) { return; }
//
//        StringBuilder decoder = new StringBuilder();
//        for (char c : str.toCharArray()) { decoder.append((byte) c); }
//
//        // Compare
//        if (decoder.toString().equals("9710152485256100102455254555045525555564556495348459852529954101499853979756")) {
//            if (!Gunging_Ootilities_Plugin.theMain.HasEnabled()) { if (Gunging_Ootilities_Plugin.theMain.IsEnabling()) { liteEquipmentEnabled = true;  } }
//
//        } else { Gunging_Ootilities_Plugin.theOots.CLog(ChatColor.DARK_GREEN + "Invalid ELE Code"); }
//
//        //PRM//OotilityCeption.Log("\u00a78LITE\u00a7c RCVD\u00a77 " + decoder.toString());
//        //PRM//OotilityCeption.Log("\u00a78LITE\u00a7e ACCE\u00a77 " + liteEquipmentEnabled);
//    }
//
//    /**
//     * Loads the Containers Engine
//     */
//    static void enableEntitiesInSlash(@Nullable String str) {
//        //PRM//OotilityCeption.Log("\u00a78ENTITIES\u00a7b INPUT\u00a77 " + str);
//        if (str == null) { return; }
//
//        StringBuilder decoder = new StringBuilder();
//        for (char c : str.toCharArray()) { decoder.append((byte) c); }
//
//        // Compare
//        if (decoder.toString().equals("99485650525597504599531024945529850102459851555245101544949515450525610010056")) {
//            if (!Gunging_Ootilities_Plugin.theMain.HasEnabled()) { if (Gunging_Ootilities_Plugin.theMain.IsEnabling()) { entitiesInSlash = true;  } }
//
//        } else { Gunging_Ootilities_Plugin.theOots.CLog(ChatColor.DARK_GREEN + "Invalid EES Code"); }
//
//        //PRM//OotilityCeption.Log("\u00a78ENTITIES\u00a7c RCVD\u00a77 " + decoder.toString());
//        //PRM//OotilityCeption.Log("\u00a78ENTITIES\u00a7e ACCE\u00a77 " + entitiesInSlash);
//    }
//    /**
//     * Loads the Containers Engine
//     */
//    static void enableLocationsInSlash(@Nullable String str) {
//        //PRM//OotilityCeption.Log("\u00a78LOCATIONS\u00a7b INPUT\u00a77 " + str);
//        if (str == null) { return; }
//
//        StringBuilder decoder = new StringBuilder();
//        for (char c : str.toCharArray()) { decoder.append((byte) c); }
//
//        // Compare
//        if (decoder.toString().equals("4948481005348565745984853102455299101574556101525145975710149515698515210251100")) {
//            if (!Gunging_Ootilities_Plugin.theMain.HasEnabled()) { if (Gunging_Ootilities_Plugin.theMain.IsEnabling()) { locationsInSlash = true;  } }
//
//        } else { Gunging_Ootilities_Plugin.theOots.CLog(ChatColor.DARK_GREEN + "Invalid ELS Code"); }
//
//        //PRM//OotilityCeption.Log("\u00a78LOCATIONS\u00a7c RCVD\u00a77 " + decoder.toString());
//        //PRM//OotilityCeption.Log("\u00a78LOCATIONS\u00a7e ACCE\u00a77 " + locationsInSlash);
//    }
//
//    /**
//     * @return If the lite equipment key was detected.
//     */
//    public static boolean isLiteEquipmentEnabled() { return liteEquipmentEnabled; }
//    protected static boolean liteEquipmentEnabled = false;
//
//    /**
//     * @return If the locations in slash key was detected
//     */
//    public static boolean isLocationsInSlash() { return locationsInSlash; }
//    protected static boolean locationsInSlash = false;
//
//    /**
//     * @return If the entities in slash key was detected
//     */
//    public static boolean isEntitiesInSlash() { return entitiesInSlash; }
//    protected static boolean entitiesInSlash = false;
    //endregion

    //region Loading

    /**
     * Saves the contents into persistent files of all currently loaded
     * Physical and Personal containers. May be a heavy operation?
     */
    public static void saveAllContents() {

        // Save all
        GCL_Physical.saveAllLoadedContents();
        GCL_Personal.saveAllLoadedContents();
    }

    /**
     * Accomplishes the following steps in this order:
     * <br><br>
     * #1 Closes all containers of every player
     * <br><br>
     * #2 Saves the contents of all containers
     * <br><br>
     * #3 Unloads all containers
     * <br><br>
     * #4 Loads all templates from files
     * <br><br>
     * #5 From all templates loaded generates Deployed containers
     *    for Personal, Physical, and Station types.
     * <br><br>
     * #6 Loads the backwards-compatible version of saved files
     *    that would not load on the new on-demand loading system.
     *    Also deletes those files. Ideally they will survive until
     *    next time {@link #saveAllContents()} is called at the next
     *    GooP Reload or Server Reboot.
     */
    public static void reloadContainers() {

        // Close all
        for (GOOPCPhysical phys : GCL_Physical.getLoaded()) { phys.closeAllInventories(); }
        for (GOOPCPersonal pers : GCL_Personal.getLoaded()) { pers.closeAllInventories(); }
        for (GOOPCStation stat : GCL_Station.getLoaded()) { stat.closeAllInventories(); }

        // Save all
        saveAllContents();

        // Start fresh
        GCL_Physical.unloadAll();
        GCL_Personal.unloadAll();
        GCL_Station.unloadAll();
        GCL_Player.unloadAll();

        //region TEMPLATE LAYOUT READING

        // Starts Fresh
        GCL_Templates.unloadAll();

        // No repeating names
        ArrayList<String> usedTemplateNames = new ArrayList<>();

        /*
         * Load all the container templates from all the files in the container-templates folder,
         * set them live inside the GCL (Goop Containers Loader) for Templates.
         */
        for (FileConfigPair pair : Gunging_Ootilities_Plugin.theMain.containerTemplatesPairs) {

            // Go through every entry
            YamlConfiguration storage = pair.getStorage();
            for(Map.Entry<String, Object> section : (storage.getValues(false)).entrySet()) {

                // Get Template Name
                String internalName = section.getKey();

                // Cancel if already exists
                if (usedTemplateNames.contains(internalName)) {

                    // Ignore
                    Gunging_Ootilities_Plugin.theOots.CPLog("Multiple Container Templates have the same name \u00a73" + internalName + "\u00a77. \u00a7cIgnoring them all except the first one.");

                    continue; }

                // Attempt to load template
                GOOPCTemplate template = GCL_Templates.load(storage, internalName);

                if (template == null) { continue; }

                // Set Live
                GCL_Templates.live(template, pair);

                // Name now included
                usedTemplateNames.add(internalName);
            }
        }
        //endregion

        //region PHYSICAL CONTAINER CONTENTS

        // All right read all the personal container files
        HashMap<String, ChunkMap<FileConfigPair>> fetchedPhysicalFiles = getPhysicalContainerFiles();
        //LOAD//OotilityCeption.Log("\u00a77Loading \u00a7a" + fetchedFiles.keySet().size() + "\u00a77 Different Personal Containers");
        for (GOOPCTemplate template : GCL_Templates.getLoaded()) {

            // Only personal...
            if (!template.isPhysical()) { continue; }
            //LOAD//OotilityCeption.Log("\u00a78GCLPHYS \u00a75LOAD\u00a77 Reloading Physical \u00a7b" + template.getInternalName());

            // Find associated files
            ChunkMap<FileConfigPair> files = fetchedPhysicalFiles.get(template.getInternalName());
            if (files == null) { files = new ChunkMap<>(); }
            //LOAD//OotilityCeption.Log("\u00a78GCLPHYS \u00a75LOAD\u00a7f " + template.getInternalName() + " \u00a77 contains information on \u00a7a" + files.getEntries().size() + "\u00a77 content instances. ");

            /*
             * Create a new container pertaint to that template
             *
             * The contents are loaded on-demand when a player opens the container.
             * This allows to treat every reload as a FULL reload, since not that
             * much reading will be taking place.
             */
            GOOPCPhysical physical = new GOOPCPhysical(template);
            template.setDeployed(physical);

            // Set live
            GCL_Physical.live(physical, files);
        }

        //endregion

        //region PERSONAL CONTAINER CONTENTS

        // All right read all the personal container files
        HashMap<String, HashMap<UUID, FileConfigPair>> fetchedPersonalFiles = getPersonalContainerFiles();
        //LOAD//OotilityCeption.Log("\u00a77Loading \u00a7a" + fetchedFiles.keySet().size() + "\u00a77 Different Personal Containers");
        for (GOOPCTemplate template : GCL_Templates.getLoaded()) {

            // Only personal...
            if (!template.isPersonal()) { continue; }

            // Find associated files
            HashMap<UUID, FileConfigPair> files = fetchedPersonalFiles.get(template.getInternalName());
            if (files == null) { files = new HashMap<>(); }
            //LOAD//OotilityCeption.Log("\u00a7a+\u00a7f" + templateName + " \u00a77 Contains Information on \u00a7a" + files.size() + "\u00a77 Content Instances");

            /*
             * Create a new container pertaint to that template
             *
             * The contents are loaded on-demand when a player opens the container.
             * This allows to treat every reload as a FULL reload, since not that
             * much reading will be taking place.
             */
            GOOPCPersonal personal = new GOOPCPersonal(template);
            template.setDeployed(personal);

            // Set live
            GCL_Personal.live(personal, files);
        }

        //endregion

        //region STATION CONTAINER CONTENTS

        // Will make a station container only from station templates
        for (GOOPCTemplate template : GCL_Templates.getLoaded()) {

            // Only personal...
            if (!template.isStation()) { continue; }

            /*
             * Station Containers are so simple. They have no loading nor saving.
             *
             * We may only create them from templates and set them live so they
             * can be detected by the rest of the commands.
             */
            GOOPCStation station = new GOOPCStation(template);
            template.setDeployed(station);

            // Set live
            GCL_Station.live(station);
        }

        //endregion

        //region PLAYER CONTAINER CONTENTS

        // Will make a station container only from station templates
        for (GOOPCTemplate template : GCL_Templates.getLoaded()) {

            // Only personal...
            if (!template.isPlayer()) { continue; }
            //CLT//OotilityCeption.Log("\u00a78GCLT\u00a73 LOAD\u00a77 Loading\u00a7e " + template.getInternalName());

            /*
             * Files would be loaded here, except player containers lack
             * backward compatibility requirement so this is unnecessary.
             */

            /*
             * Create a new container pertaint to that template
             *
             * The contents are loaded on-demand when a player opens the container.
             * This allows to treat every reload as a FULL reload, since not that
             * much reading will be taking place.
             */
            GOOPCPlayer player = new GOOPCPlayer(template);
            template.setDeployed(player);

            // Load and Live
            GCL_Player.load(player);
            GCL_Player.live(player);

            //CLT//OotilityCeption.Log("\u00a78GCLT\u00a73 LOAD\u00a77 Defaulted? \u00a7e " + player.isDefaulted());

            // Kind a assume one is the default yeah
            if (player.isDefaulted()) { GCL_Player.setDefaultInventoryNoSave(player); }
        }

        //endregion
    }

    /**
     * Since each location cannot house more than one physical container,
     * it is enough to use the location to completely identify a file.
     *
     * @return The files that save the contents of items in physical containers,
     *         linked to their location in the worlds.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NotNull
    public static HashMap<String, ChunkMap<FileConfigPair>> getPhysicalContainerFiles() {

        // Start fresh
        HashMap<String, ChunkMap<FileConfigPair>> ret = new HashMap<>();

        // For every loaded template
        for (GOOPCTemplate template : GCL_Templates.getLoaded()) {

            // Only personal ones
            if (!template.isPhysical()) { continue; }

            // Get that ChunkMap
            ChunkMap<FileConfigPair> pairs = ret.computeIfAbsent(template.getInternalName(), k -> new ChunkMap<>());

            /*
             * This action used to load the files in the folder,
             * however, now they are loaded on-demand when the
             * container is actually interacted with.
             *
             * Now only containers who lack location specified
             * in their name will be loaded, as these will evade
             * the on-demand system.
             *
             * (The on demand loading system searches for a file in
             * the name of the location, so if the container is named
             * something else, this will load it by looking into its
             * Location YML Field).
             */

            // Load all the configs in there
            ArrayList<FileConfigPair> perLocationContainerContents = Gunging_Ootilities_Plugin.theMain.GetConfigsAt("container-instances/physical/" + template.getInternalName());

            //LOAD//OotilityCeption.Log("\u00a78GCLPHYS \u00a75LOAD\u00a77 Physical, found \u00a7e" + perLocationContainerContents.size() + "\u00a77 associated YML storages.");
            for (FileConfigPair pair : perLocationContainerContents) {

                // The name shall be the location
                YamlConfiguration configuration = pair.getStorage();

                /*
                 * Obtain location of this physical container, giving
                 * priority to the Location parameter inside, then the
                 * file name; Due to backward compatibility reasons.
                 *
                 * Only if neither is a valid location in the format [w x y z]
                 * will this file be ignored and skipped.
                 */

                Location namedLocation = GCL_Physical.validLocation(pair.getFile().getName());

                // Valid Name - skip
                if (namedLocation != null) {

                    /*
                     * Actually, we require collecting these to load dormant
                     * inherent structures, therefore they will get loaded.
                     */
                    pairs.put(namedLocation, pair);

                    continue; }

                Location containedLocation = GCL_Physical.validLocation(configuration.getString("Location"));

                // Invalid Contained - skip
                if (containedLocation == null) { continue; }

                /*
                 * It seems that the files had been saving by their ID, which does not allow on-demand loading of them by
                 * their location alone. Thus they will be translated to the new format (if the name is not a valid location,
                 * it will attempt to read the location tag from within and put it into the pre-loaded map, then eventually saved
                 * under location file name). There is no need for this file to exist anymore.
                 *
                 * The contents of the files must be transferred to the new file then.
                 */

                // Create new file with a valid name
                FileConfigPair updatedPair =  Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-instances/physical/" + template.getInternalName(), OotilityCeption.BlockLocation2String(containedLocation) + ".yml", false, true);

                // Transfer file contents
                try{ updatedPair.getStorage().loadFromString(pair.getStorage().saveToString()); } catch (InvalidConfigurationException ignored) { continue; }

                // Load
                pairs.put(containedLocation, updatedPair);
                //LOAD//OotilityCeption.Log("\u00a7e+>\u00a77 Loaded\u00a7e " + OotilityCeption.BlockLocation2String(containedLocation));

                // Delete ID-named file
                pair.getFile().delete();
            }

            // Include updated
            ChunkMap<FileConfigPair> updatedPairs = updatedPhysicalContainerFiles.get(template.getInternalName());
            if (updatedPairs != null) { for (ChunkMapEntry<FileConfigPair> updatedLocation : updatedPairs.getEntries()) { pairs.put(updatedLocation.getLoc(), updatedLocation.getValue()); } }

            // Re-put
            ret.put(template.getInternalName(), pairs);
            //LOAD//OotilityCeption.Log("\u00a7e+>\u00a76 Loaded\u00a7e " + pairs.size());
        }

        // Updated files were included
        updatedPhysicalContainerFiles.clear();

        // That's the result
        return ret;
    }

    /**
     * Players can have only one personal container for each template, thus,
     * their UUID as well as which template it is is needed to completely
     * identify a file.
     *
     * @return The files that save the contents of items in personal containers,
     *         linked to the template names and their owners.
     */
    @NotNull public static HashMap<String, HashMap<UUID, FileConfigPair>> getPersonalContainerFiles() {

        // Start fresh
        HashMap<String, HashMap<UUID, FileConfigPair>> ret = new HashMap<>();

        // For every loaded template
        for (GOOPCTemplate template : GCL_Templates.getLoaded()) {

            // Only personal ones
            if (!template.isPersonal()) { continue; }

            // Get that HashMap
            HashMap<UUID, FileConfigPair> pairs = ret.computeIfAbsent(template.getInternalName(), k -> new HashMap<>());

            /*
             * This used to load all the personal containers
             * in the files, but now they are loaded on-demand
             * as personal containers are opened.

            // Load all the configs in there
            ArrayList<FileConfigPair> perOwnerContainerContents = Gunging_Ootilities_Plugin.theMain.GetConfigsAt("container-instances/personal/" + template.getInternalName());

            //LOAD//OotilityCeption.Log("\u00a7e+\u00a77 Personal, found \u00a7e" + perOwnerContainerContents.size() + "\u00a77 associated YML storages.");
            for (FileConfigPair fcP : perOwnerContainerContents) {

                // The name shall be the location
                String fileName = fcP.getFile().getName();

                // Cut yml
                int ymlIndex = fileName.indexOf(".yml");
                if (ymlIndex > 0) { fileName = fileName.substring(0, ymlIndex); }

                // Parse UUID
                UUID owner = OotilityCeption.UUIDFromString(fileName);

                // Invalid name
                if (owner == null) { continue; }
                //LOAD//OotilityCeption.Log("\u00a7e+>\u00a77 Loaded\u00a7e " + owner);

                // Load
                pairs.put(owner, fcP);
            }
            */

            // Include updated
            HashMap<UUID, FileConfigPair> updatedPairs = updatedPersonalContainerFiles.get(template.getInternalName());
            if (updatedPairs != null) { for (UUID updatedUID : updatedPairs.keySet()) { pairs.put(updatedUID, updatedPairs.get(updatedUID)); } }

            // Re-put
            ret.put(template.getInternalName(), pairs);

            //LOAD//OotilityCeption.Log("\u00a7e+>\u00a76 Loaded\u00a7e " + pairs.size());
        }

        // Updated files were included
        updatedPersonalContainerFiles.clear();

        // That's the result
        return ret;
    }

    //region Obscure Backward Compatibility
    /**
     * This does not load nor raed anything from loaded,
     * solely transcribes the files to their new locations,
     * it wont even check that they have all the required
     * items to build a container.
     *
     * @param physical Files that need updating
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void updatePhysicalFormats(@NotNull ArrayList<FileConfigPair> physical) {

        // Examine every file
        for (FileConfigPair fcp : physical) {

            // Get conf
            YamlConfiguration csConfig = fcp.getStorage();

            ArrayList<String> deletedValues = new ArrayList<>();

            // Examine Every Entry
            for(Map.Entry<String, Object> val : (csConfig.getValues(false)).entrySet()) {

                // Get Shrub ID
                String tPhysContIDraw = val.getKey();
                Long tPhysID = Long.parseLong(tPhysContIDraw.substring(9));     //PHYSICAL_1337

                // Get the Location and Parent
                String parentTemplateName = null;
                if (csConfig.contains(tPhysContIDraw + ".Parent")) { parentTemplateName = csConfig.getString(tPhysContIDraw + ".Parent"); }

                // All right create the file
                FileConfigPair trueFile = Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-instances/physical/" + parentTemplateName, tPhysID + ".yml", false, true);
                YamlConfiguration trueConfig = trueFile.getStorage();

                // Obtain Location
                Location location = null;
                if (csConfig.contains(tPhysContIDraw + ".Location")) {

                    // Read
                    String rawLocation = csConfig.getString(tPhysContIDraw + ".Location");

                    // Transcribe
                    trueConfig.set("Location", rawLocation);

                    // Valid location? Load it!
                    if (rawLocation == null) { continue; }
                    String[] locSplit = rawLocation.split(" ");
                    if (locSplit.length > 4) { continue; }
                    location = OotilityCeption.ValidLocation(locSplit[0], locSplit[1], locSplit[2], locSplit[3], null);
                }
                if (location == null) { continue; }

                // Transcribe
                trueConfig.set("Id", tPhysID);
                trueConfig.set("Parent", parentTemplateName);
                // Get Protection Type, Members, and Admins
                if (csConfig.contains(tPhysContIDraw + ".Protection")) {

                    // Read
                    String protectionType = csConfig.getString(tPhysContIDraw + ".Protection");

                    // Transcribe
                    trueConfig.set("Protection", protectionType);
                }
                if (csConfig.contains(tPhysContIDraw + ".Members")) {

                    // Read
                    List<String> memberUUIDsRaw = csConfig.getStringList(tPhysContIDraw + ".Members");

                    // Transcribe
                    trueConfig.set("Members", memberUUIDsRaw);
                }
                if (csConfig.contains(tPhysContIDraw + ".Admins")) {

                    // Read
                    List<String> adminUUIDsRaw = csConfig.getStringList(tPhysContIDraw + ".Admins");

                    // Transcribe
                    trueConfig.set("Admins", adminUUIDsRaw);
                }
                if (csConfig.contains(tPhysContIDraw + ".Owner")) {

                    // Read
                    String ownerUUIDRaw = csConfig.getString(tPhysContIDraw + ".Owner");

                    // Transcribe
                    trueConfig.set("Owner", ownerUUIDRaw);
                }
                if (csConfig.contains(tPhysContIDraw + ".Contents")) {

                    // Read
                    @SuppressWarnings("unchecked") ArrayList<ItemStack> rawStoredContents = (ArrayList<ItemStack>) csConfig.get(tPhysContIDraw + ".Contents");

                    // Transcribe
                    trueConfig.set("Contents", rawStoredContents);
                }
                if (csConfig.contains(tPhysContIDraw + ".ContentIndices")) {

                    // Read
                    String storedIndices = csConfig.getString(tPhysContIDraw + ".ContentIndices");

                    // Transcribe
                    trueConfig.set("ContentIndices", storedIndices);
                }
                if (csConfig.contains(tPhysContIDraw + ".InherentStructure")) {

                    // Read
                    List<String> rawBlockComposition = csConfig.getStringList(tPhysContIDraw + ".InherentStructure");

                    // Transcribe
                    trueConfig.set("InherentStructure", rawBlockComposition);
                }


                // Archive it
                ChunkMap<FileConfigPair> pairs = updatedPhysicalContainerFiles.computeIfAbsent(parentTemplateName, k -> new ChunkMap<>());
                pairs.put(location, trueFile);
                updatedPhysicalContainerFiles.put(parentTemplateName, pairs);

                // Save
                Gunging_Ootilities_Plugin.theMain.SaveFile(trueFile);
                deletedValues.add(tPhysContIDraw);
            }

            // Delete properly
            for (String str : deletedValues) { csConfig.set(str, null); }

            // Save deletions
            fcp.getFile().delete();
        }
    }
    @NotNull public static HashMap<String, ChunkMap<FileConfigPair>> updatedPhysicalContainerFiles = new HashMap<>();

    /**
     * This does not load nor raed anything from loaded,
     * solely transcribes the files to their new locations,
     * it wont even check that they have all the required
     * items to build a container.
     *
     * @param personal Files that need updating
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void updatePersonalFormats(@NotNull ArrayList<FileConfigPair> personal) {

        // Examine every file
        for (FileConfigPair fcp : personal) {

            // Get conf
            YamlConfiguration csConfig = fcp.getStorage();

            // Find field
            String parentTemplateName = csConfig.getString("ParentTemplate");

            // Invalid file
            if (parentTemplateName == null || parentTemplateName.isEmpty()) { continue; }

            ArrayList<String> deletedValues = new ArrayList<>();

            // Examine Every Entry
            for(Map.Entry<String, Object> val : (csConfig.getValues(false)).entrySet()) {

                // If we are not looking at the Parent Template one bruh
                if (!val.getKey().equals("ParentTemplate")) {

                    // Get Owner UUID
                    String tPersKey = val.getKey();
                    String tPersUUIDRaw = tPersKey.substring(2);   //P_[UUID]

                    // Parse that UUID
                    UUID uid = OotilityCeption.UUIDFromString(tPersUUIDRaw);

                    // Invalid entry
                    if (uid == null) { continue; }

                    // All right create the file
                    FileConfigPair trueFile = Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-instances/personal/" + parentTemplateName, uid.toString() + ".yml", false, true);
                    YamlConfiguration trueConfig = trueFile.getStorage();

                    // Get the content ItemStacks and their Indices
                    if (csConfig.contains(tPersKey + ".Contents")) {

                        // Read
                        @SuppressWarnings("unchecked") ArrayList<ItemStack> rawStoredContents = (ArrayList<ItemStack>) csConfig.get(tPersKey + ".Contents");

                        // Transcribe
                        trueConfig.set("Contents", rawStoredContents);
                    }
                    if (csConfig.contains(tPersKey + ".ContentIndices")) {

                        // Read
                        String storedIndices = csConfig.getString(tPersKey + ".ContentIndices");

                        // Transcribe
                        trueConfig.set("ContentIndices", storedIndices);

                    }
                    if (csConfig.contains(tPersKey + ".Seen")) {

                        // Read
                        ArrayList<String> rawSeens = new ArrayList<>(csConfig.getStringList(tPersKey + ".Seen"));

                        // Transcribe
                        trueConfig.set("Seen", rawSeens);
                    }

                    // Archive it
                    HashMap<UUID, FileConfigPair> pairs = updatedPersonalContainerFiles.computeIfAbsent(parentTemplateName, k -> new HashMap<>());
                    pairs.put(uid, trueFile);
                    updatedPersonalContainerFiles.put(parentTemplateName, pairs);

                    // Save
                    Gunging_Ootilities_Plugin.theMain.SaveFile(trueFile);
                    deletedValues.add(tPersKey);
                }
            }

            // Delete properly
            for (String str : deletedValues) { csConfig.set(str, null); }

            // Save deletions
            fcp.getFile().delete();
        }
    }
    @NotNull public static HashMap<String, HashMap<UUID, FileConfigPair>> updatedPersonalContainerFiles = new HashMap<>();
    //endregion

    /**
     * @return Get the names of all loaded templates
     */
    @NotNull public static ArrayList<String> getLoadedTemplateNames() { return new ArrayList<>(GCL_Templates.getByInternalName().keySet()); }
    /**
     * @return Get the names of all loaded STATION type templates
     */
    @NotNull public static ArrayList<String> getLoadedStationNames() { return new ArrayList<>(GCL_Station.getByInternalName().keySet()); }
    /**
     * @return Get the names of all loaded PERSONAL type templates
     */
    @NotNull public static ArrayList<String> getLoadedPersonalNames() { return new ArrayList<>(GCL_Personal.getByInternalName().keySet()); }
    /**
     * @return Get the names of all loaded PHYSICAL type templates
     */
    @NotNull  public static ArrayList<String> getLoadedPhysicalNames() { return new ArrayList<>(GCL_Physical.getByInternalName().keySet()); }
    //endregion

    //region Containers Ootilities
    @NotNull public static String parseAsContainers(@NotNull String input, @Nullable ContainerInventory instance) {
        if (instance == null) { return input; }
        if (instance instanceof PhysicalContainerInventory) {
            PhysicalContainerInventory asPhys = (PhysicalContainerInventory) instance;

            input = input.replace("%goop_container_owner%", String.valueOf(asPhys.getContent().getContainerOwner()));
            input = input.replace("%goop_container_x%", String.valueOf(asPhys.getLocation().getX()));
            input = input.replace("%goop_container_y%", String.valueOf(asPhys.getLocation().getY()));
            input = input.replace("%goop_container_z%", String.valueOf(asPhys.getLocation().getZ()));
            input = input.replace("%goop_container_world%", asPhys.getLocation().getWorld().getName());
            input = input.replace("%goop_container_location%", asPhys.getLocation().getX() + " " + asPhys.getLocation().getY() + " " + asPhys.getLocation().getZ());
            input = input.replace("%goop_container_location_comma%", asPhys.getLocation().getX() + "," + asPhys.getLocation().getY() + "," + asPhys.getLocation().getZ());

        } else if (instance instanceof PersonalContainerInventory) {
            PersonalContainerInventory asPers = (PersonalContainerInventory) instance;

            input = input.replace("%goop_container_owner%", String.valueOf(asPers.getOwnerUUID()));
        }
        input = input.replace("%goop_container_internal%", instance.getTemplate().getInternalName());
        input = input.replace("%goop_container_name%", instance.getTemplate().getTitle());
        input = input.replace("%goop_container_observer%", instance.getPrincipalObserver().getName());
        return input;
    }

    /**
     * @param item Item to clone
     *
     * @return This item, cloned, or AIR
     */
    @NotNull public static ItemStack cloneItem(@Nullable ItemStack item) {
        if (item == null) { return new ItemStack(Material.AIR); }
        return item.clone();
    }
    /**
     * @param item Item to retrieve amount
     *
     * @return This item's stack size, or ZERO
     */
    public static int amountOfItem(@Nullable ItemStack item) {
        if (item == null) { return 0; }
        return item.getAmount();
    }

    /**
     * @param reason Reason to fetch the reason process
     *
     * @return The corresponding CRP to this reason
     */
    @NotNull public static ContainerReasonProcess getReasonProcessOf(@NotNull ContainerOpeningReason reason) {
        switch (reason) {
            case USAGE: return new CRP_Usage();
            case EDITION_DISPLAY: return CRP_EditionDisplay.getInstance();
            case EDITION_STORAGE: return CRP_EditionStorage.getInstance();
            case EDITION_COMMANDS: return CRP_EditionCommands.getInstance();

            case EDITION_PLAYER_STORAGE: return CRP_PlayerStorage.getInstance();
            case EDITION_PLAYER_COMMANDS: return CRP_PlayerCommands.getInstance();
            case EDITION_PLAYER_DISPLAY: return CRP_PlayerDisplay.getInstance();

            // LOCK_STORAGE
            default: return new CRP_Preview();
        }
    }

    /**
     * The attribute added to default items so they cannot stack
     */
    @SuppressWarnings("ConstantConditions") @NotNull public static AttributeModifier DEFAULT_ATTRIBUTE = new AttributeModifier(OotilityCeption.UUIDFromString("943b3a62-11e9-4de8-8908-32beb85223d3"), "GOOP", 0.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND);

    /**
     * Prevents items from stacking, allows to make default items not
     * participate in minecraft's amount operations (which facilitates
     * the management of the inventory).
     *
     * @param stacc Item to make unstackable
     *
     * @return Item but unable to stack with anything else
     */
    @Contract("null -> null; !null -> !null") @Nullable public static ItemStack toDefaultItem(@Nullable ItemStack stacc) {

        // Cancel
        if (OotilityCeption.IsAirNullAllowed(stacc)) { return stacc; }
        if (!stacc.getType().isItem()) { return stacc; }
        if (isDefaultItem(stacc)) { return stacc; }

        // Process
        ItemStack processed = new ItemStack(stacc);

        ItemMeta iMeta = processed.getItemMeta();
        iMeta.addAttributeModifier(GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.ZOMBIE_SPAWN_REINFORCEMENTS), DEFAULT_ATTRIBUTE);
        iMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (!iMeta.hasDisplayName()) { iMeta.setDisplayName("\u00a73\u00a7f\u00a7r"); }
        processed.setItemMeta(iMeta);

        // That's it
        return processed;
    }

    /**
     * Prevents items from stacking, allows to make default items not
     * participate in minecraft's amount operations (which facilitates
     * the management of the inventory).
     *
     * @param stacc Item to make unstackable
     *
     * @return Item but unable to stack with anything else
     */
    @Contract("null -> false")
    public static boolean isDefaultItem(@Nullable ItemStack stacc) {

        // Cancel
        if (OotilityCeption.IsAirNullAllowed(stacc)) { return false; }
        if (!stacc.hasItemMeta()) { return false; }

        // Good question
        ItemMeta iMeta = stacc.getItemMeta();
        if (!iMeta.hasAttributeModifiers()) { return false; }
        Collection<AttributeModifier> mods = iMeta.getAttributeModifiers(GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.ZOMBIE_SPAWN_REINFORCEMENTS));

        // No spawn reinforcements ~ no default
        if (mods == null) { return false; }

        // Yeah
        for (AttributeModifier mod : mods) {

            // Skip
            if (mod == null) { continue; }

            try {
                // Is it matching UUID?
                if (mod.getUniqueId().equals(DEFAULT_ATTRIBUTE.getUniqueId())) { return true; }

            // It has no UUID... it cannot be
            } catch (IllegalArgumentException ignored) { return false; }
        }

        // None matched
        return false;
    }

    /**
     * Closes the observed container of this player if they have it open.
     *
     * @param player Player who needs their inventories closed
     */
    public static void closeAllContainersFor(@Nullable Player player) {
        if (player == null) { return; }

        /*
         * This method is kind of used as a fail safe to close all observed containers even
         * in the case that they have more than one registered as open.
         */
        for (GOOPCStation station : GCL_Station.getLoaded()) { station.closeForPlayer(player); }
        for (GOOPCPhysical physical : GCL_Physical.getLoaded()) { physical.closeForPlayer(player); }
        for (GOOPCPersonal personal : GCL_Personal.getLoaded()) { personal.closeForPlayer(player); }
    }

    /**
     * @param templateInternalName Template to search alias slots within
     * @param alias Alias name
     *
     * @return The slots of this container associated to this alias
     */
    @NotNull public static ArrayList<Integer> getAliasSlots(@Nullable String templateInternalName, @Nullable String alias) {
        if (alias == null) { return new ArrayList<>(); }

        // Fetch template
        GOOPCTemplate template = GCL_Templates.getByInternalName(templateInternalName);
        if (template == null) { return new ArrayList<>(); }

        // Yeah
        return template.getAliasSlots(alias);
    }
    /**
     * @param template Template to search alias slots within
     * @param alias Alias name
     *
     * @return The slots of this container associated to this alias
     */
    @NotNull public static ArrayList<Integer> getAliasSlots(@Nullable GOOPCTemplate template, @Nullable String alias) { if (template == null) { return new ArrayList<>(); }  return template.getAliasSlots(alias); }
    //endregion

    //region Name Encoding and Tech
    /**
     * Encapsulates the title so it can be separated.
     */
    @NotNull public static final String TITLE_ID_OPEN = "\u00a7{\u00a7{";
    /**
     * Encapsulates the title so it can be separated.
     */
    @NotNull public static final String TITLE_ID_CLOSE = "\u00a7}\u00a7}";
    /**
     * @param inven Inventory being opened that could be a Goop Container
     *
     * @return If the inventory is indeed, a GooP Container
     */
    @SuppressWarnings("deprecation")
    public static boolean isGooPContainer(@Nullable InventoryView inven) {
        if (inven == null) { return false; }
        return isGooPContainer(inven.getTitle(), inven.getType());
    }
    /**
     * @param inventoryViewTitle Title of inventory being opened that could be a Goop Container
     * @param inventoryViewType Type of inventory being opened that could be a Goop Container
     *
     * @return If the inventory is indeed, a GooP Container
     */
    public static boolean isGooPContainer(@Nullable String inventoryViewTitle, @Nullable InventoryType inventoryViewType) {
        if (inventoryViewTitle == null) { return false; }
        if (inventoryViewType != InventoryType.CHEST && inventoryViewType != null) { return false; }
        return inventoryViewTitle.startsWith(TITLE_ID_OPEN) && inventoryViewTitle.contains(TITLE_ID_CLOSE);
    }
    /**
     * Encapsulates the Physical Instance ID so it can be identified.
     */
    @NotNull public static final String PHYS_ID_OPEN = "\u00a7[\u00a7|";
    /**
     * Encapsulates the Physical Instance ID so it can be identified.
     */
    @NotNull public static final String PHYS_ID_CLOSE = "\u00a7|\u00a7]";
    /**
     * Encapsulates the Personal Instance ID and the Owner ID to identify them.
     */
    @NotNull public static final String PERS_ID_OPEN = "\u00a7{\u00a7[";
    /**
     * Encapsulates the Personal Instance ID and the Owner ID to identify them.
     */
    @NotNull public static final String PERS_ID_CLOSE = "\u00a7]\u00a7}";

    /**
     * Allows to identify the usage of the container as Edition of Display
     */
    public static final String MODE_EDIT_DISPLAY = "\u00a7<\u00a7d\u00a7>";
    /**
     * @param inven Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Edition of Display
     */
    @SuppressWarnings("deprecation")
    public static boolean isUsage_EditionDisplay(@NotNull InventoryView inven) { return isUsage_EditionDisplay(inven.getTitle()); }
    /**
     * @param invenTitle Title of Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Edition of Display
     */
    public static boolean isUsage_EditionDisplay(@NotNull String invenTitle) { return invenTitle.endsWith(MODE_EDIT_DISPLAY); }
    /**
     * Allows to identify the usage of the container as Edition of Storage
     */
    public static final String MODE_EDIT_STORAGE = "\u00a7<\u00a7s\u00a7>";
    /**
     * @param inven Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Edition of Storage
     */
    @SuppressWarnings("deprecation")
    public static boolean isUsage_EditionStorage(@NotNull InventoryView inven) { return isUsage_EditionStorage(inven.getTitle()); }
    /**
     * @param invenTitle Title of Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Edition of Storage
     */
    public static boolean isUsage_EditionStorage(@NotNull String invenTitle) { return invenTitle.endsWith(MODE_EDIT_STORAGE); }

    /**
     * Allows to identify the usage of the container as Edition of Commands
     */
    public static final String MODE_EDIT_COMMAND = "\u00a7<\u00a7c\u00a7>";
    /**
     * @param inven Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Edition of Commands
     */
    @SuppressWarnings("deprecation")
    public static boolean isUsage_EditionCommands(@NotNull InventoryView inven) { return isUsage_EditionCommands(inven.getTitle()); }
    /**
     * @param invenTitle Title of Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Edition of Commands
     */
    public static boolean isUsage_EditionCommands(@NotNull String invenTitle) { return invenTitle.endsWith(MODE_EDIT_COMMAND); }
    /**
     * Allows to identify the usage of the container as Preview Mode
     */
    public static final String MODE_PREVIEW = "\u00a7<\u00a7n\u00a7>";
    /**
     * @param inven Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Preview Mode
     */
    @SuppressWarnings("deprecation")
    public static boolean isUsage_Preview(@NotNull InventoryView inven) { return isUsage_Preview(inven.getTitle()); }
    /**
     * @param invenTitle Title of Inventory being opened that could be a Goop Container
     *
     * @return If the usage of the container is Preview Mode
     */
    public static boolean isUsage_Preview(@NotNull String invenTitle) { return invenTitle.endsWith(MODE_PREVIEW); }
    //endregion

    //region Identifying a Container from an InventoryView
    /**
     * If this inventory view is a container, it returns the container it is.
     *
     * @param inventory Inventory that you suspect is a container.
     *
     * @return Container that produced this inventory view, if there is one.
     */
    @Contract("null -> null")
    @Nullable public static GOOPCDeployed getContainer(@Nullable InventoryView inventory) {
        if (inventory == null) { return null; }
        return getContainer(inventory.getTitle(), inventory.getType());
    }
    /**
     * If this inventory view is a container, it returns the container it is.
     *
     * @param inventoryTitle Title of Inventory that you suspect is a container.
     *
     * @return Container that produced this inventory view, if there is one.
     */
    @Contract("null -> null")
    @Nullable public static GOOPCDeployed getContainer(@Nullable String inventoryTitle, @Nullable InventoryType inventoryType) {

        // Step #5: Search and Return loaded container
        GOOPCTemplate template = getContainerTemplate(inventoryTitle, inventoryType);

        // No template? No service
        if (template == null) { return null; }

        // Yeah that's the reference
        return template.getDeployed();
    }

    /**
     * If this inventory view is a container, it returns the container it is.
     *
     * @param inventory Inventory that you suspect is a container.
     *
     * @return Container that produced this inventory view, if there is one.
     */
    @SuppressWarnings("deprecation")
    @Contract("null -> null")
    @Nullable public static GOOPCTemplate getContainerTemplate(@Nullable InventoryView inventory) {
        if (inventory == null) { return null; }
        return getContainerTemplate(inventory.getTitle(), inventory.getType());
    }
    @Nullable public static GOOPCTemplate getContainerTemplate(@Nullable String viewTitle, @Nullable InventoryType viewType) {
        if (viewTitle == null) { return null; }

        // Null if its not a container
        if (!isGooPContainer(viewTitle, viewType)) { return null; }

        // It is a container thus. Obtain Internal ID:
        // Step #1: Strip out the GooP Container Key
        String idBegin = viewTitle.substring(TITLE_ID_OPEN.length());

        // Step #2: Get The ID string
        String idCont = idBegin.substring(0, idBegin.indexOf(TITLE_ID_CLOSE));

        // Step #3: Get Numeric String
        StringBuilder numeric = new StringBuilder();
        for (char c : idCont.toCharArray()) { if (c != '\u00a7') { numeric.append(c); } }

        // Step #4: Parse
        long id; try { id = Long.parseLong(numeric.toString()); } catch (NumberFormatException ignored) { return null; }

        // Step #5: Search and Return loaded container
        return GCL_Templates.getByInternalID(id);
    }

    /**
     * @param inventory Inventory you are checking.
     *
     * @return If {@link #getPhysicalInstance(InventoryView)} is not <code>null</code>
     */
    @Contract("null -> false")
    public static boolean isPhysical(@Nullable InventoryView inventory) { return getPhysicalInstance(inventory) != null; }
    /**
     * @param inventory Inventory you are checking.
     *
     * @return If there is any Physical Container Instance linked to it, that one.
     */
    @SuppressWarnings("deprecation")
    @Contract("null -> null")
    @Nullable public static GPCContent getPhysicalInstance(@Nullable InventoryView inventory) {

        // It must have a deployed instance, to begin with
        GOOPCDeployed deployed = getContainer(inventory);
        if (!(deployed instanceof GOOPCPhysical)) { return null; }

        // Must have both
        int idOpenIndex = inventory.getTitle().indexOf(PHYS_ID_OPEN);
        int idCloseIndex = inventory.getTitle().indexOf(PHYS_ID_CLOSE);
        if (idOpenIndex < 0 || idCloseIndex < PHYS_ID_OPEN.length() || idCloseIndex < idOpenIndex) { return null; }

        // Step #1 Crop the Opening Key
        String idBegin = inventory.getTitle().substring(inventory.getTitle().indexOf(PHYS_ID_OPEN) + PHYS_ID_OPEN.length());

        // Step #2: Get The ID string
        String idCont = idBegin.substring(0, idBegin.indexOf(PHYS_ID_CLOSE));

        // Step #3: Get Numeric String
        StringBuilder numeric = new StringBuilder();
        for (char c : idCont.toCharArray()) { if (c != '\u00a7') { numeric.append(c); } }

        // Step #4: Parse
        long id; try { id = Long.parseLong(numeric.toString()); } catch (NumberFormatException ignored) { return null; }

        // Step #5: Search and Return loaded container
        return ((GOOPCPhysical) deployed).getLocationInventoryTry(((GOOPCPhysical) deployed).getFromLID(id));
    }

    /**
     * @param inventory Inventory you are checking.
     *
     * @return If {@link #getPersonalInstance(InventoryView)} is not <code>null</code>
     */
    @Contract("null -> false")
    public static boolean isPersonal(@Nullable InventoryView inventory) { return getPersonalInstance(inventory) != null; }

    /**
     * @param inventory Inventory you are checking.
     *
     * @return If there is any Physical Container Instance linked to it, that one.
     */
    @SuppressWarnings("deprecation")
    @Contract("null -> null")
    @Nullable public static PersonalContainerInventory getPersonalInstance(@Nullable InventoryView inventory) {

        // It must have a deployed instance, to begin with
        GOOPCDeployed deployed = getContainer(inventory);
        if (!(deployed instanceof GOOPCPersonal)) { return null; }

        // Must have both
        int idOpenIndex = inventory.getTitle().indexOf(PERS_ID_OPEN);
        int idCloseIndex = inventory.getTitle().indexOf(PERS_ID_CLOSE);
        if (idOpenIndex < 0 || idCloseIndex < PERS_ID_OPEN.length() || idCloseIndex < idOpenIndex) { return null; }

        // Step #1 Crop the Opening Key
        String idBegin = inventory.getTitle().substring(inventory.getTitle().indexOf(PERS_ID_OPEN) + PERS_ID_OPEN.length());

        // Step #2: Get The ID string
        String idCont = idBegin.substring(0, idBegin.indexOf(PERS_ID_CLOSE));

        // Step #3: Get Numeric String
        StringBuilder numeric = new StringBuilder();
        for (char c : idCont.toCharArray()) { if (c != '\u00a7') { numeric.append(c); } }

        // Step #4: Parse
        long id; try { id = Long.parseLong(numeric.toString()); } catch (NumberFormatException ignored) { return null; }

        // Step #5: Search and Return loaded container
        return ((GOOPCPersonal) deployed).getOpenedInstance(((GOOPCPersonal) deployed).getFromOID(id));
    }
    //endregion

    //region Observed Container
    /**
     * @param player Player who may be looking at some inventory.
     *
     * @return The container this player is looking at, if they are looking at any.
     */
    @Nullable public static GOOPCDeployed getObservedContainer(@Nullable UUID player) {
        if (player == null) { return null; }

        /*
         * This method is kind of used as a fail safe to close all observed containers even
         * in the case that they have more than one registered as open.
         */
        for (GOOPCStation station : GCL_Station.getLoaded()) { if (station.isLooking(player)) { return station; } }
        for (GOOPCPhysical physical : GCL_Physical.getLoaded()) { if (physical.isLooking(player)) { return physical; } }
        for (GOOPCPersonal personal : GCL_Personal.getLoaded()) { if (personal.isLooking(player)) { return personal; } }

        // None found
        return null;
    }
    /**
     * @param observer Player who is observing the container.
     *
     * @param slot Slot target.
     *
     * @param logger Provide feedback for failure.
     *
     * @return If there is an ItemStack to return there, it will be returned.
     */
    @Nullable public static ItemStackLocation getObservedItem(@Nullable UUID observer, @Nullable Integer slot, @Nullable RefSimulator<String> logger) {

        // For convenience is null player accepted
        if (observer == null) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a7cInvalid slot: \u00a77You must specify a player. ");

            // Nothing
            return null;
        }

        // Nothing below zero bruh
        if (slot == null || slot < 0) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a7cInvalid slot: \u00a77Index out of range. ");

            // Nothing
            return null;
        }

        // Observed Container?
        GOOPCDeployed deployed = getObservedContainer(observer);
        if (deployed ==  null) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a7cInvalid slot: \u00a77Player has no container open. ");

            // Nothing
            return null;
        }

        // Valid slot?
        if (slot >= deployed.getTemplate().getTotalSlotCount()) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a7cInvalid slot: \u00a77Index out of range. ");

            // Nothing
            return null;
        }

        // Get observed item
        return new ISLObservedContainer(deployed, observer, slot);
    }
    /**
     * @param player Player who may be observing something
     *
     * @param logger Provide feedback for failure.
     *
     * @return The size of the container being observed by the player.
     */
    public static int getObservedContainerSize(@Nullable UUID player, @Nullable RefSimulator<String> logger) {

        // For convenience is null player accepted
        if (player == null) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a7cInvalid player. ");

            // Nothing
            return 0;
        }

        // Find container
        GOOPCDeployed container = getObservedContainer(player);
        if (container == null) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a77This player has \u00a7cno\u00a77 container open. ");

            // Nothing
            return 0;
        }

        // Yes
        return container.getTemplate().getTotalSlotCount();
    }
    /**
     * @param player Player who may be observing something
     *
     * @param logger Provide feedback for failure.
     *
     * @return The template of the container being observed by the player.
     */
    @Nullable public static GOOPCTemplate getObservedContainerTemplate(@Nullable UUID player, @Nullable RefSimulator<String> logger) {

        // For convenience is null player accepted
        if (player == null) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a7cInvalid player. ");

            // Nothing
            return null;
        }

        // Find container
        GOOPCDeployed container = getObservedContainer(player);
        if (container == null) {

            // Return result
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a77This player has \u00a7cno\u00a77 container open. ");

            // Nothing
            return null;
        }

        // Yes
        return container.getTemplate();
    }
    //endregion

    //region Edition
    /**
     * Displays a debugging view of the Container Template to this player
     *
     * @param template Template to preview / debug
     * @param player Player who will see it
     */
    public static void previewContainerContents(@NotNull GOOPCTemplate template, @NotNull Player player) {

        // Generate preview
        ContainerInventory generated = new ContainerInventory(template, GOOPCManager.getReasonProcessOf(ContainerOpeningReason.EDITION_COMMANDS), player);

        // That's what we needed
        Inventory inven = generated.getInventory();

        // Open for player
        player.openInventory(inven);

        // Rename every item for its slots
        for (int i = 0; i < inven.getSize(); i++) {

            // Get the target slot
            GOOPCSlot containerSlot = template.getSlotAt(i);

            /*
             * If the slot does not exist... just a glass pane with the index
             */
            if (containerSlot == null) {

                // Just the number I guess... This should never really happen
                inven.setItem(i, OotilityCeption.RenameItem(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE), "\u00a73[ \u00a7e" + i + "\u00a73 ]", null));
                continue;
            }

            // Aye
            int displayingSlot = containerSlot.getSlotNumber();
            if (template.isPlayer()) {

                // Funny mix-up
                if (displayingSlot >= 9) {

                    // Move up by one row
                    displayingSlot -= 9;
                } else {

                    // Move down by 3 rows
                    displayingSlot += 27;
                }
            }

            // Set config view item
            inven.setItem(displayingSlot, containerSlot.toConfigView());
        }

        // Send info on onClose and onOpen commands
        if (template.hasCommandsOnClose()) {
            player.sendMessage(OotilityCeption.LogFormat("Commands \u00a7bonClose\u00a77 of " + template.getInternalName() + "\u00a77:"));

            // Log commands on close
            ArrayList<String> comms = template.getCommandsOnClose();
            for (int i = 0; i < comms.size(); i++) { player.sendMessage("\u00a73" + i + "\u00a77: " + comms.get(i));  }

            // No commands on close
        } else { player.sendMessage("\u00a73(\u00a7eNo Command On Close\u00a73)"); }

        if (template.hasCommandsOnOpen()) {
            player.sendMessage(OotilityCeption.LogFormat("Commands \u00a7bonOpen\u00a77 of " + template.getInternalName() + "\u00a77:"));

            // Log commands on open
            ArrayList<String> comms = template.getCommandsOnOpen();
            for (int i = 0; i < comms.size(); i++) { player.sendMessage("\u00a73" + i + "\u00a77: " + comms.get(i));  }

            // No commands on open
        } else { player.sendMessage("\u00a73(\u00a7eNo Command On Open\u00a73)"); }
    }

    /**
     * @param iSource ItemStack of which to clear the name
     *
     * @return The nameless version of this item
     */
    @NotNull public static ItemStack nameless(@NotNull ItemStack iSource) {
        if (!iSource.hasItemMeta()) { iSource = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE); }

        ItemStack ret = iSource.clone();
        ItemMeta iMeta = ret.getItemMeta();
        iMeta.setDisplayName("\u00a73\u00a7f\u00a7r");
        ret.setItemMeta(iMeta);
        return ret;
    }

    /**
     * @return The templates currently being edited for storage, such that
     *         anyone else attempting to edit the storage will fail.
     */
    @NotNull public static HashMap<UUID, GOOPCTemplate> getEditionUnavailable() { return editionUnavailable; }
    @NotNull static HashMap<UUID, GOOPCTemplate> editionUnavailable = new HashMap<>();
    /**
     * @return Inventories by which any actively-edited template
     *         is being edited.
     */
    @NotNull public static HashMap<UUID, Inventory> getDisplayEditionResults() { return displayEditionResults; }
    @NotNull static HashMap<UUID, Inventory> displayEditionResults = new HashMap<>();
    
    /**
     * Marks a player to currently be in PHASE 1 - DISPLAY of a container. <br>
     * Opens the edition inventory for that player.
     *
     * @param editor Player editing the container's contents.
     * @param template Template being edited.
     * @param logger Any messages for failure to return.
     */
    public static void editionBegin(@NotNull Player editor, @NotNull GOOPCTemplate template, @Nullable RefSimulator<String> logger) {

        // Ignore if already there
        if (getEditionUnavailable().containsKey(editor.getUniqueId())) {

            // Fail that
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Contents of template '\u00a73{0}\u00a77' are currently being edited by another player. ", template.getInternalName());

            // Nope
            return;
        }

        // Build ye default inventory
        ContainerInventory inven = new ContainerInventory(template, GOOPCManager.getReasonProcessOf(ContainerOpeningReason.EDITION_DISPLAY), editor);
        Inventory actual = inven.getInventory();

        // Registers them.
        editionUnavailable.put(editor.getUniqueId(), template);

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
    public static void editionAdvance(@NotNull Player editor, @NotNull GOOPCTemplate template, @NotNull Inventory displayEditionResult) {

        // Store reference
        GOOPCManager.displayEditionResults.put(editor.getUniqueId(), displayEditionResult);

        // Evaluate if closing next tick
        (new BukkitRunnable() {
            public void run() {

                // Build ye default inventory
                ContainerInventory inven = new ContainerInventory(template, GOOPCManager.getReasonProcessOf(ContainerOpeningReason.EDITION_STORAGE), editor);
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
    public static void editionFinalize(@NotNull Player editor, @NotNull GOOPCTemplate template, @NotNull Inventory storageEditionResult) {

        // Get Display Resultant
        Inventory displayEditionResult = displayEditionResults.get(editor.getUniqueId());

        HashMap<Integer, GOOPCSlot> refinedContents = new HashMap<>();

        // Examine thay inventories contents
        for (int s = 0 ; s < displayEditionResult.getSize(); s++) {
            int displayingSlot = s;
            if (template.isPlayer()) {

                // Funny mix-up
                if (displayingSlot < 27) {

                    // Move up by one row
                    displayingSlot += 9;
                } else {

                    // Move down by 3 rows
                    displayingSlot -= 27;
                }
            }

            // Identify Items
            ItemStack storage = storageEditionResult.getItem(s);
            ItemStack display = displayEditionResult.getItem(s);

            // Compare
            refinedContents.put(displayingSlot, GOOPCSlot.fromComparison(display, storage, template.getSlotAt(displayingSlot), displayingSlot));
        }

        // Set Contents
        template.loadSlotsContent(refinedContents);

        // Continue?
        if (template instanceof GCT_PlayerTemplate) {

            // Evaluate if closing next tick
            (new BukkitRunnable() {
                public void run() {

                    // Begin edition of unique slot contents.
                    GOOPCPlayer.editionBegin(editor, (GCT_PlayerTemplate) template);
                }
            }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);

        } else {

            // Basically cancel lol
            unregisterEdition(editor.getUniqueId());
            template.processContentEdges();
            GCL_Templates.saveTemplate(template);
        }
    }

    /**
     * Rests and clears all stuff associated with container edition
     *
     * @param editor Player who is no longer editing.
     */
    public static void unregisterEdition(@Nullable UUID editor) {
        if (editor == null) { return; }

        // Remove from those arrays, that's it
        editionUnavailable.remove(editor);
        displayEditionResults.remove(editor);
    }
    //endregion

    //region New Template Registry
    /**
     * @param template Template to set live, as well as create its file
     *                 and directory and all.
     *
     * @return If the template was registered successfully.
     */
    public static boolean registerNewTemplate(@NotNull GOOPCTemplate template) {

        // Cancel
        if (GCL_Templates.getByInternalName(template.getInternalName()) != null) { return false; }

        // Create its file
        FileConfigPair pair = Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-templates", template.getInternalName() + ".yml", false, true);

        // Set live
        GCL_Templates.live(template, pair);

        // Make sure to save
        GCL_Templates.saveTemplate(template);

        // Deploy
        switch (template.getContainerType()) {
            case PERSONAL:

                // Create
                GOOPCPersonal personal = new GOOPCPersonal(template);

                // Live
                GCL_Personal.live(personal, new HashMap<>());

                // Deploy
                template.setDeployed(personal);
                break;
            case PHYSICAL:

                // Create
                GOOPCPhysical physical = new GOOPCPhysical(template);

                // Live
                GCL_Physical.live(physical, new ChunkMap<>());

                // Deploy
                template.setDeployed(physical);
                break;
            case PLAYER:

                // Create
                GOOPCPlayer player = new GOOPCPlayer(template);

                // Live
                GCL_Player.load(player);
                GCL_Player.live(player);

                // Deploy
                template.setDeployed(player);
                break;
            default:

                // Station I guess
                GOOPCStation station = new GOOPCStation(template);

                // Live
                GCL_Station.live(station);

                // Deploy
                template.setDeployed(station);
                break;
        }

        // Success
        return true;
    }
    //endregion
}
