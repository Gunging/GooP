package gunging.ootilities.gunging_ootilities_plugin.containers.loader;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCPhysical;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.GPCContent;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.GPCProtection;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSBlock;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import gunging.ootilities.gunging_ootilities_plugin.misc.chunks.ChunkMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Micromanages the loading of Physical Containers
 */
public class GCL_Physical {

    /**
     * Load the contents of this Physical Container at this location.
     * <br><br>
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> save into the files.
     *
     * @param physical Deployed container that will load this location
     *
     * @param location Location to load into the files
     */
    public static void load(@NotNull GOOPCPhysical physical, @NotNull Location location) {
        GOOPCTemplate template = physical.getTemplate();
        String error = "Error when loading physical container \u00a73" + template.getInternalName() + "\u00a77 at location \u00a6e " + toStringLocation(location) + "\u00a77: \u00a7c";
        String warning = "Warning when loading physical container \u00a73" + template.getInternalName() + "\u00a77 at location \u00a6e " + toStringLocation(location) + "\u00a77: \u00a76";
        //String warning = "Warning when loading personal container \u00a73" + template.getInternalName() + "\u00a77 for player \u00a6e " + owner.toString() + "\u00a77: \u00a76";

        // Get the pair
        FileConfigPair csPair = getStorage(template.getInternalName(), location);

        // Load the YML from it
        YamlConfiguration storage = csPair.getStorage();

        // Load the ID, very important apparently
        long locationID = storage.getLong(YML_PHYSICAL_ID, -1);
        if (locationID == -1) {
            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(warning + "No Internal ID, generating new. "); }

            // Create a brand new ID
            while (physical.getFromLID(locationID) != null) {
                locationID = new Random().nextLong();

                // No negatives
                if (locationID < 0) { locationID *= -1; }
            } }

        // Protection against duplicate loaded - Change Team (the one being loaded will choose a new ID)
        Location duplicate = physical.getFromLID(locationID);
        if (duplicate != null) {
            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(error + "Duplicate Internal ID (\u00a7f" + locationID + "\u00a7c), loading at \u00a76" + toStringLocation(location) +  "\u00a77 but shares internal ID with the existing one at \u00a7d" + toStringLocation(duplicate) + "\u00a7c, generating new ID for \u00a76the new one being loaded\u00a7c. "); }

            // Create a brand new ID
            while (getByInternalID(locationID) != null) {
                locationID = new Random().nextLong();

                // No negatives
                if (locationID < 0) { locationID *= -1; }
            } }

        // Load its protections
        GPCProtection protection = getProtectionFromString(storage.getString(YML_PHYSICAL_PROTECTION, null));
        UUID owner = getUUIDFromString(storage.getString(YML_PHYSICAL_OWNER, null));
        ArrayList<UUID> admins = getUUIDList(storage.getStringList(YML_PHYSICAL_ADMINS));
        ArrayList<UUID> members = getUUIDList(storage.getStringList(YML_PHYSICAL_MEMBERS));
        ArrayList<CSBlock> inherentStructure = getStructure(storage.getStringList(YML_PHYSICAL_STRUCTURE), warning);

        // Get the content ItemStacks and their Indices
        @SuppressWarnings("unchecked")
        ArrayList<ItemStack> rawStoredContents =  (ArrayList<ItemStack>) storage.get(YML_PHYSICAL_CONTENT);
        ArrayList<String> rawSeens = new ArrayList<>(storage.getStringList(YML_PHYSICAL_SEENS));
        String storedIndices = storage.getString(YML_PHYSICAL_INDICES, null);

        // Compile seens
        HashMap<UUID, NameVariable> ownerSeens = new HashMap<>();
        for (String rawSeen : rawSeens) {

            // Gib $Ñ$
            if (rawSeen.contains("#Ñ#")) {

                // Split
                String[] rawSplit = rawSeen.split("#Ñ#");

                // Appropriate length
                if (rawSplit.length >= 2) {

                    // Get UUID
                    String opener = rawSplit[0];
                    String message = rawSplit[1];

                    // Get UUID
                    UUID openerUUID = getUUIDFromString(opener);
                    NameVariable nv = NameVariable.Deserialize(message);

                    // Success
                    if (openerUUID != null && nv != null) { ownerSeens.put(openerUUID, nv); }
                } } }

        // Load the inventory
        HashMap<Integer, ItemStack> locationStorage = new HashMap<>();
        if (rawStoredContents != null) {

            // If there was stored index information
            if (storedIndices != null) {

                // Split indices into actual indices
                String[] indexSplit = storedIndices.split(" ");

                // Know current looked at index
                int currIndex = 0;

                // Examine every index
                for (int i = 0; i < indexSplit.length; i++) {

                    // Attempt to parse
                    if (OotilityCeption.IntTryParse(indexSplit[i])) {

                        // Parse and add
                        int index = OotilityCeption.ParseInt(indexSplit[i]);

                        if (currIndex < rawStoredContents.size()) {

                            // Get ItemStack
                            ItemStack iSource = rawStoredContents.get(currIndex);

                            // Add
                            locationStorage.put(index, iSource);
                            //LOAD//OotilityCeption.Log("Loaded " + OotilityCeption.GetItemName(iSource) + "\u00a77 at index \u00a7e" + index);

                            // Increase currIndex
                            currIndex++;
                        }

                    } else {

                        // Error bruh
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(error + "Could not parse integer from index \u00a7e" + indexSplit[i]); }
                    }
                }

            // So apparently the items were present but we don't know in what slots they are supposed to go
            } else if (rawStoredContents.size() > 0) {

                // BRUH
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(error + "Items are present but with no index. "); }
            }
        }

        // Adjust protection
        if (protection == GPCProtection.UNREGISTERED) { owner = null; admins.clear(); members.clear(); }

        /*
         * The GPCContent stores the seen information, contents,
         * protection, and so on for this physical container at
         * this location.
         */
        GPCContent loadedContent = new GPCContent(physical, location, locationID, protection, owner);

        // Load those contents
        loadedContent.loadStorageContents(locationStorage);

        // Load protection
        if (protection != GPCProtection.UNREGISTERED) { loadedContent.loadAdmins(admins); loadedContent.loadMembers(members); }

        // Load Inherent Structure
        loadedContent.loadInherentBlocks(inherentStructure);

        // Set the damn inventory
        physical.loadLocationContents(location, loadedContent);
        physical.loadSeens(loadedContent, ownerSeens);
    }

    /**
     * Unloads this container from memory, and erases its file.
     * <br><br>
     * This will straight-up delete this storage, to drop its items
     * as well (a more natural deletion), call {@link GPCContent#destroy()};
     * method which does close the inventories of anyone who has it
     * open, something which is important to avoid dupes, and this
     * method does not do it.
     *
     * @param content Location being unloaded
     */
    public static void unloadDelete(@NotNull GPCContent content) {
        GOOPCPhysical physical = content.getContainer();
        Long lid = physical.getLID(content.getLocation());
        Location location = content.getLocation();

        // Clear links
        physical.getLinkLocationToLID().put(location, null);
        physical.getReverseLocationToLID().put(lid, null);
        physical.getPerLocationInventories().put(location, null);
        physical.getOpenedInstances().put(location, null);

        // Clear inherent
        for (CSBlock csBlock : content.getInherentBlocks()) {

            // Skip snooze
            if (csBlock == null) { continue; }

            // Obtain relative location
            Location csLocation = csBlock.getRelativeTo(location);

            // Yeah...
            ArrayList<GPCContent> inherent = getInherentStructureOverlap().get(csLocation);
            ArrayList<GPCContent> dormant = getDormantInherentStructures().get(csLocation);

            // Remove this content from the inherent one
            if (inherent != null) {
                for (int i = 0; i < inherent.size(); i++) {
                    GPCContent csContent = inherent.get(i);
                    if (csContent == null) { continue; }

                    // Same location?
                    if (csContent.getLocation().equals(location)) {

                        // remove
                        inherent.remove(i);

                        // Repeat Index
                        i--;
                    }
                }

                // Refresh removed
                inherentStructureOverlap.put(location, inherent);
            }

            // Remove this content from the dormant one
            if (dormant != null) {
                for (int i = 0; i < dormant.size(); i++) {
                    GPCContent csContent = dormant.get(i);
                    if (csContent == null) { continue; }

                    // Same location?
                    if (csContent.getLocation().equals(location)) {

                        // remove
                        dormant.remove(i);

                        // Repeat Index
                        i--;
                    }
                }

                // Refresh removed
                dormantInherentStructures.put(location, dormant);
            }
        }

        // Clear and delete files
        ChunkMap<FileConfigPair> byName = getStorage(content.getContainer().getTemplate().getInternalName());
        FileConfigPair pair = byName.get(location);
        byName.put(location, null);
        getFilesByName().put(content.getContainer().getTemplate().getInternalName(), byName);

        // Delete
        if (pair == null) { return; }
        //noinspection ResultOfMethodCallIgnored
        pair.getFile().delete();
    }

    /**
     * Load a Physical Container
     *
     * @param physical Container to register. Unregisters any previous ones of the same template Name / ID
     */
    public static void live(@NotNull GOOPCPhysical physical, @NotNull ChunkMap<FileConfigPair> storage) {

        // Unloads any previous one of the same qualifications
        unload(physical.getTemplate().getInternalID());
        unload(physical.getTemplate().getInternalName());

        // Register in the arrays
        byInternalID.put(physical.getTemplate().getInternalID(), physical);
        byInternalName.put(physical.getTemplate().getInternalName(), physical);
        filesByName.put(physical.getTemplate().getInternalName(), storage);
        loaded.add(physical);

        //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a75LOAD\u00a77 For \u00a7b" + physical.getTemplate().getInternalName());
        //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a75LOAD\u00a77 Files: \u00a7b" + storage.getEntries().size());

        /*
         * Must go through every FileConfigPair and map
         * the inherent structures to their core blocks
         */
        loadDormantInherentStructures(physical);
    }
    /**
     * Unload a Physical Container by the ID of its template.
     *
     * @param byID Internal ID of the Template of the Physical Container to unload.
     */
    public static void unload(@Nullable Long byID) {
        if (byID == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCPhysical physical = loaded.get(i);

            // Is it the one? No? Skip
            if (physical.getTemplate().getInternalID() != byID) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(physical.getTemplate().getInternalName());
            byInternalID.remove(physical.getTemplate().getInternalID());
            filesByName.remove(physical.getTemplate().getInternalName());

            // That's all
            break;
        }
    }
    /**
     * Unload a Physical Container by the name of its template.
     *
     * @param byName Internal Name of the Template of the Physical Container to unload.
     */
    public static void unload(@Nullable String byName) {
        if (byName == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCPhysical physical = loaded.get(i);

            // Is it the one? No? Skip
            if (!physical.getTemplate().getInternalName().equals(byName)) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(physical.getTemplate().getInternalName());
            byInternalID.remove(physical.getTemplate().getInternalID());
            filesByName.remove(physical.getTemplate().getInternalName());

            // That's all
            break;
        }
    }
    /**
     * Clears all arrays
     */
    public static void unloadAll() {
        loaded.clear();
        byInternalName.clear();
        byInternalID.clear();
        filesByName.clear();
        dormantInherentStructures.clear();
        unclaimedInherentStructures.clear();
        inherentStructureOverlap.clear();
    }

    /**
     * @return The Physical Containers currently loaded (one for every loaded template)
     */
    @NotNull
    public static ArrayList<GOOPCPhysical> getLoaded() { return loaded; }
    @NotNull final static ArrayList<GOOPCPhysical> loaded = new ArrayList<>();

    /**
     * @return The Physical Containers currently loaded (by its template internal ID)
     */
    @NotNull public static HashMap<Long, GOOPCPhysical> getByInternalID() { return byInternalID; }
    /**
     * @param id ID Currently assigned at this template
     *
     * @return The Physical Containers currently loaded (by its template internal ID)
     */
    @Nullable
    public static GOOPCPhysical getByInternalID(@Nullable Long id) { if (id == null) { return null; } return getByInternalID().get(id); }
    @NotNull final static HashMap<Long, GOOPCPhysical> byInternalID = new HashMap<>();
    /**
     * @param internalID ID that you want to see if its loaded
     *
     * @return If there is a Physical Containers loaded by this ID (its template internal ID)
     */
    public static boolean isPhysicalContainer(long internalID) { return getByInternalID(internalID) != null; }

    /**
     * @return The Physical Containers currently loaded (by its template internal name)
     */
    @NotNull public static HashMap<String, GOOPCPhysical> getByInternalName() { return byInternalName; }
    /**
     * @param internalName Name of this template
     *
     * @return The Physical Containers currently loaded (by its template internal name)
     */
    @Nullable public static GOOPCPhysical getByInternalName(@Nullable String internalName) { if (internalName == null) { return null; } return getByInternalName().get(internalName); }
    @NotNull final static HashMap<String, GOOPCPhysical> byInternalName = new HashMap<>();
    /**
     * @param internalName Name that you want to see if its loaded
     *
     * @return If there is a Physical Containers loaded by this name (its template internal name)
     */
    public static boolean isPhysicalContainer(@Nullable String internalName) { return getByInternalName(internalName) != null; }

    /**
     * @return Where are the contents of this Physical Container stored?
     */
    @NotNull public static HashMap<String, ChunkMap<FileConfigPair>> getFilesByName() { return filesByName; }
    /**
     * @param internalName Name of the target template
     *
     * @return Where are the contents of this Physical Container stored (by location)?
     */
    @NotNull public static ChunkMap<FileConfigPair> getStorage(@Nullable String internalName) {
        if (internalName == null) { return new ChunkMap<>(); }
        ChunkMap<FileConfigPair> ret = getFilesByName().get(internalName);
        if (ret == null) { ret = new ChunkMap<>(); filesByName.put(internalName, ret); }
        return ret;
    }
    /**
     * This method will create a file for this location if there is none.  <br>
     * It does not check that the internal name belongs to a template.  <br>
     * <b>Be mindful of when you call this.</b>
     *
     * @param internalName Name of the target template
     *
     * @param location Location to which the contents are saved
     *
     * @return Where are the contents of this Physical Container stored?
     */
    @NotNull public static FileConfigPair getStorage(@NotNull String internalName, @NotNull Location location) {
        FileConfigPair pair = getStorage(internalName).get(location);
        if (pair != null) { return pair; }

        // Create File
        pair = Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-instances/physical/" + internalName, toStringLocation(location) + ".yml", false, true);

        // Load File
        ChunkMap<FileConfigPair> storage = getStorage(internalName);
        storage.put(location, pair); filesByName.put(internalName, storage);

        // Yes
        return pair;
    }
    @NotNull final static HashMap<String, ChunkMap<FileConfigPair>> filesByName = new HashMap<>();

    /**
     * Saves all the attributes of all loaded templates
     */
    public static void saveAllLoadedContents() { for (GOOPCPhysical physical : getLoaded()) { saveAllContents(physical); } }
    /**
     * Saves all the contents of all locations of this Physical Container
     *
     * @param templateID ID of the template corresponding to the Physical Container to save (in its folder directory, a file for each location)
     */
    public static void saveAllContents(@Nullable Long templateID) { saveAllContents(getByInternalID(templateID)); }
    /**
     * Saves all the contents of all locations of this Physical Container
     *
     * @param templateName Name of the template corresponding to the Physical Container to save (in its folder directory, a file for each location)
     */
    public static void saveAllContents(@Nullable String templateName) { saveAllContents(getByInternalName(templateName)); }
    /**
     * Saves all the contents of all locations of this Physical Container
     *
     * @param physical Physical Container to save (in its folder directory, a file for each location)
     */
    public static void saveAllContents(@Nullable GOOPCPhysical physical) {
        if (physical == null) { return; }

        // Get contents
        ChunkMap<GPCContent> loadedContents = physical.getPerLocationInventories();

        // Save them
        loadedContents.forEachExisting((location,content) -> saveContents(physical, content));
    }

    /**
     * Saves the contents of this locations of this Physical Container
     *
     * @param physical Physical Container to save (in its folder directory, a separate file from all other locations)
     */
    public static void saveContents(@NotNull GOOPCPhysical physical, @NotNull GPCContent contents) {

        // Find its file
        FileConfigPair directory = getStorage(physical.getTemplate().getInternalName(), contents.getLocation());
        directory = Gunging_Ootilities_Plugin.theMain.GetLatest(directory);

        // Modify Storage
        YamlConfiguration storage = directory.getStorage();

        // Important Information
        storage.set(YML_PHYSICAL_ID, physical.getInternalTemporalID());
        storage.set(YML_PHYSICAL_TEMPLATE, physical.getTemplate().getInternalName());
        storage.set(YML_PHYSICAL_LOCATION, toStringLocation(contents.getLocation()));

        //region Serialize Content
        HashMap<Integer, ItemStack> items = contents.getStorageContents();
        ArrayList<ItemStack> serializedContents = new ArrayList<>();
        StringBuilder serializedIndices = new StringBuilder();
        for (Map.Entry<Integer, ItemStack> item : items.entrySet()) {

            // Get Slot
            Integer iIndex = item.getKey();
            ItemStack iSource = item.getValue();

            /*
             * Do not save air?
             */
            if (OotilityCeption.IsAirNullAllowed(iSource)) { continue; }
            if (iSource.getAmount() <= 0) { continue; }

            // Serialize
            serializedContents.add(iSource);
            serializedIndices.append(" ").append(iIndex); }
        //endregion

        // Store thay, if has contents
        if (serializedContents.size() > 0) {
            storage.set(YML_PHYSICAL_CONTENT, serializedContents);
            storage.set(YML_PHYSICAL_INDICES, serializedIndices.substring(1));
        } else {
            storage.set(YML_PHYSICAL_CONTENT, null);
            storage.set(YML_PHYSICAL_INDICES, null); }

        // Save Protection
        storage.set(YML_PHYSICAL_PROTECTION, contents.getProtectionType().toString());
        storage.set(YML_PHYSICAL_OWNER, contents.getContainerOwner() == null ? null : contents.getContainerOwner().toString());
        storage.set(YML_PHYSICAL_ADMINS, contents.getContainerAdmins().isEmpty() ? null : toStringUUIDs(contents.getContainerAdmins()));
        storage.set(YML_PHYSICAL_MEMBERS, contents.getContainerMembers().isEmpty() ? null : toStringUUIDs(contents.getContainerMembers()));
        storage.set(YML_PHYSICAL_SEENS, contents.getSerializableSeens());

        // Save Inherence
        storage.set(YML_PHYSICAL_STRUCTURE, toStringStructure(contents.getInherentBlocks()));

        // Actually Save
        Gunging_Ootilities_Plugin.theMain.SaveFile(directory);
    }

    //region YML Configuration Names
    static final String YML_PHYSICAL_ID = "Id";
    static final String YML_PHYSICAL_TEMPLATE = "Parent";
    static final String YML_PHYSICAL_LOCATION = "Location";

    static final String YML_PHYSICAL_CONTENT = "Contents";
    static final String YML_PHYSICAL_INDICES = "ContentIndices";

    static final String YML_PHYSICAL_PROTECTION = "Protection";
    static final String YML_PHYSICAL_MEMBERS = "Members";
    static final String YML_PHYSICAL_ADMINS = "Admins";
    static final String YML_PHYSICAL_OWNER = "Owner";
    static final String YML_PHYSICAL_SEENS = "Seen";

    static final String YML_PHYSICAL_STRUCTURE = "InherentStructure";

    /**
     * Intended to work with file names, but in general, parses locations from space-separated keys
     *
     * @return A location if exists, or NULL if anything doesnt make sense
     *
     * @see #toStringLocation(Location)
     */
    @Contract("null -> null") @Nullable public static Location validLocation(@Nullable String rawLocation) {
        if (rawLocation == null) { return null; }

        // Remove file extension
        if (rawLocation.contains(".")) { rawLocation = rawLocation.substring(0, rawLocation.indexOf('.')); }

        // Attempt to split into [w x y z] format
        String[] locSplit = rawLocation.split(" ");
        if (locSplit.length < 4) { return null; }

        // Attempt to parse
        return OotilityCeption.ValidLocation(locSplit[0], locSplit[1], locSplit[2], locSplit[3], null);
    }

    /**
     * @param loc Location to convert to [w x y z] format
     *
     * @return Space-separated string from this location.
     *
     * @see #validLocation(String)
     */
    @NotNull public static String toStringLocation(@NotNull Location loc) { return loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ(); }

    /**
     * @param unparsed {@link GPCProtection} that is in string form; Caps insensitive.
     *
     * @return Either it parsed, or {@link GPCProtection#UNREGISTERED} as the default.
     */
    @NotNull public static GPCProtection getProtectionFromString(@Nullable String unparsed) {
        if (unparsed == null) { return GPCProtection.UNREGISTERED; }

        // Unregistered is the default value
        try { return GPCProtection.valueOf(unparsed.toUpperCase()); } catch (IllegalArgumentException ignored) { return GPCProtection.UNREGISTERED; }
    }

    /**
     * @param unparsed List of strings that should be UUIDs
     *
     * @return A list of UUIDs, ignoring entries that are invalid.
     */
    @NotNull public static ArrayList<UUID> getUUIDList(@NotNull List<String> unparsed) {
        ArrayList<UUID> ret = new ArrayList<>();
        for (String str : unparsed) {

            // Decode
            UUID decoded = getUUIDFromString(str);

            // Skip
            if (decoded == null) { continue; }

            // Include
            ret.add(decoded);
        }

        // Yeah
        return ret;
    }

    /**
     * @param toSave List of UUIDs that can be emtpy.
     *
     * @return The UUIDs as strings.
     */
    @NotNull public static ArrayList<String> toStringUUIDs(@Nullable ArrayList<UUID> toSave) {
        ArrayList<String> ret = new ArrayList<>();
        if (toSave == null) { return ret; }

        // Encode
        for (UUID uuid : toSave) { if (uuid == null) { continue; } ret.add(uuid.toString()); }

        // Yes
        return ret;
    }

    /**
     * @param unparsed String that could be a UUID
     *
     * @return This string as a UUID, or <code>null</code> if incorrect format.
     */
    @Contract("null -> null") @Nullable public static UUID getUUIDFromString(@Nullable String unparsed) {
        if (unparsed == null) { return null; }

        // Correct Format?
        if (unparsed.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {

            // Return thay
            return UUID.fromString(unparsed);
        }

        // No
        return null;
    }

    /**
     * @param unparsed A list of blocks that are probably custom structure blocks.
     *
     * @param warn Warning format.
     *
     * @return The list of blocks that were in 'valid' format. Physical containers
     *         now are forced to have their core block registered, and that is done
     *         in {@link GPCContent#loadStorageContents(HashMap)}, when this inherence
     *         are actually loaded into the container.
     */
    @NotNull public static ArrayList<CSBlock> getStructure(@NotNull List<String> unparsed, @NotNull String warn) {

        // The array of blocks
        ArrayList<CSBlock> ret = new ArrayList<>();

        // For each string
        for (String encoded : unparsed) {

            // Split by spaces
            String[] split = encoded.split(" ");

            // What will it be?
            Material srcMat = Material.STONE;
            Integer srcS = null, srcV = null, srcF = null;

            // Number of args makes sense?
            if (split.length == 4) {

                // Get Side Offset
                if (OotilityCeption.IntTryParse(split[1])) {

                    // Worked
                    srcS = OotilityCeption.ParseInt(split[1]);

                    // Nah fam
                } else {

                    // Log it
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(warn + "Inherent structure side offset \u00a7e" + split[1] + "\u00a7c is not an integer number. "); }
                }

                // Get Vertical Offset
                if (OotilityCeption.IntTryParse(split[2])) {

                    // Worked
                    srcV = OotilityCeption.ParseInt(split[2]);

                    // Nah fam
                } else {

                    // Log it
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(warn + "Inherent structure vertical offset \u00a7e" + split[2] + "\u00a7c is not an integer number. "); }
                }

                // Get Forward Offset
                if (OotilityCeption.IntTryParse(split[3])) {

                    // Worked
                    srcF = OotilityCeption.ParseInt(split[3]);

                    // Nah fam
                } else {

                    // Log it
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(warn + "Inherent structure forward offset \u00a7e" + split[3] + "\u00a7c is not an integer number. "); }
                }

            // Not <mat> <sOff> <vOff> <fOff> syntax
            } else {

                // Warn
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(warn + "Expected 0\u00a7e<mat> <sOff> <vOff> <fOff>\u00a7c' instead of '\u00a76" + encoded + "\u00a7c'. "); }
            }

            // Good job with that syntax. Create a Structure Block and add it
            if (srcF != null && srcS != null && srcV != null) {

                // Create and add
                ret.add(new CSBlock(srcMat, srcS, srcV, srcF));
            }
        }

        // The values of the map
        return ret;
    }

    /**
     * @param toSave List of CustomStructureBlocks that can be emtpy.
     *
     * @return The CustomStructureBlocks as strings.
     */
    @NotNull public static ArrayList<String> toStringStructure(@Nullable ArrayList<CSBlock> toSave) {
        ArrayList<String> ret = new ArrayList<>();
        if (toSave == null) { return ret; }

        // Encode
        for (CSBlock block : toSave) { if (block == null) { continue; } ret.add(block.getBlockType().toString() + " " + block.getSideOffset() + " " + block.getVerticalOffset() + " " + block.getForwardOffset()); }

        // Yes
        return ret;
    }
    //endregion

    //region Inherent Structure Engine
    /**
     * @param location Location of the core block
     *
     * @return Any unclaimed inherent structure at this core block.
     */
    @NotNull public static ArrayList<CSBlock> getUnclaimedInherentStructure(@Nullable Location location) { ArrayList<CSBlock> ret = unclaimedInherentStructures.get(location); return ret == null ? new ArrayList<>() : ret; }
    @NotNull static ChunkMap<ArrayList<CSBlock>> unclaimedInherentStructures = new ChunkMap<>();
    /**
     * When GooP CustomStructures detects a container access command, it registers itself here,
     * and if a container is created at the target location, it will know it is its structure.
     *
     * @param location Location of the core block
     *
     * @param structure Structure composition
     */
    public static void registerUnclaimedInherentStructure(@NotNull Location location, @NotNull ArrayList<CSBlock> structure) { unclaimedInherentStructures.put(location, structure); }

    /**
     * <b>Are you sure you don't want to be calling {@link #getContainersAt(Location)} instead?
     * This will not load any unloaded physical containers at the location you desire, as they
     * are loaded on-demand when calling that other method!</b>
     * <br><br>
     * At a given location, due to the multi-block nature of inherent structures,
     * more than one physical container may occupy the same blocks. This chunk map
     * tracks all the containers directly allocated to their world positions.
     * <br><br>
     * This only occurs with multi-block inherent structures. The core of a physical
     * container, the block the items are actually saved at, can only host one physical
     * container.
     * <br><br>
     * The inherent structure of another physical container may still overlap a core
     * block of another container's inherent structure, the core blocks just cannot
     * overlap.
     *
     * @return All the containers that call this block part of their inherent structure.
     */
    @NotNull public static ChunkMap<ArrayList<GPCContent>> getInherentStructureOverlap() {
        return inherentStructureOverlap;
    }
    @NotNull static ChunkMap<ArrayList<GPCContent>> inherentStructureOverlap = new ChunkMap<>();

    /**
     * Its important to note that these GPCContents are <b>invalid</b> and are only
     * guaranteed to carry valid location and container template information.
     *
     * @return The Physical Containers that are in the files having this location as part
     *         of their inherent structure, but are not yet loaded.
     */
    @NotNull public static ChunkMap<ArrayList<GPCContent>> getDormantInherentStructures() { return dormantInherentStructures; }
    @NotNull static ChunkMap<ArrayList<GPCContent>> dormantInherentStructures = new ChunkMap<>();

    /**
     * Adds the core blocks of this physical container to {@link #getDormantInherentStructures()}
     *
     * @param physical The physical container that was just set live and has the
     *                 information necessary for this operation.
     */
    public static void loadDormantInherentStructures(@NotNull GOOPCPhysical physical) {
        //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a79DIH\u00a77 Loading dormant of \u00a73" + physical.getTemplate().getInternalName());

        /*
         * Go through each container found in the FileConfigPair ChunkMap:
         *
         *  #1 Find the inherent structure
         *  #2 Scan each inherent structure block
         *  #3 Link the core block reference to each inherent block
         *  #4 Save
         *
         * It doesn't matter if the structure does not link the core block to... the core block,
         * because this is literally the first block searched for; but I guess it doesnt really
         * hurt in the end, its just redundant.
         */
        ChunkMap<FileConfigPair> storages = getFilesByName().get(physical.getTemplate().getInternalName());
        //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a79DIH\u00a77 Found \u00a7bx" + storages.getEntries().size() + "\u00a77 storages...");

        storages.forEachExisting((location,pair) -> {
            //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a79DIH\u00a77 Loading dormant of \u00a7b" + toStringLocation(location));

            // Get the storage of it
            YamlConfiguration storage = pair.getStorage();

            // Obtain the inherent structure blocks
            ArrayList<CSBlock> inherentStructure = getStructure(storage.getStringList(YML_PHYSICAL_STRUCTURE), "");

            // Through each of them
            for (CSBlock csBlock : inherentStructure) {

                // Obtain true location
                Location csLocation = csBlock.getRelativeTo(location);

                // Get that array
                ArrayList<GPCContent> inherentOverlap = dormantInherentStructures.get(csLocation);
                if (inherentOverlap == null) { inherentOverlap = new ArrayList<>(); }

                /*
                 * These only carry container and location information.
                 */
                GPCContent voidContent = new GPCContent(physical, location, -2, GPCProtection.UNREGISTERED, null);

                // Add the core block location reference
                inherentOverlap.add(voidContent);

                // All of those core blocks are now inherent to this location
                dormantInherentStructures.put(csLocation, inherentOverlap);

                //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a79DIH\u00a77 Registered \u00a73" + toStringLocation(csLocation) + " to \u00a7b" + toStringLocation(voidContent.getLocation()));
            }
        });
    }

    /**
     *     todo Calling this will load from the files this GPCContent, of whatever
     *          containers intersect this block with their inherent structures.
     *
     * @param location The location that might house a physical container.
     *
     *
     * @return All the containers that consider this location part of their inherent structure.
     */
    @NotNull public static ArrayList<GPCContent> getContainersAt(@NotNull Location location) {

        // Attempt to find
        ArrayList<GPCContent> ret = getInherentStructureOverlap().get(location);
        //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a73INH\u00a77 Requesting at \u00a7a " + toStringLocation(location));

        // Already loaded
        if (ret != null) {
            //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a73INH\u00a77 Found \u00a7bx" + ret.size());
            return ret; }

        //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a73INH\u00a77 Checking dormant...");

        /*
         * Very well, lets see what the dormant inherent structures
         * want to say about this location...
         *
         * Supposing it does find something, all those containers will
         * be loaded, and then returned as the result of this method.
         */
        ArrayList<GPCContent> inherentCores = getDormantInherentStructures().get(location);
        if (inherentCores == null) {
            //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a73INH\u00a7c No dormant registered.");
            return new ArrayList<>(); }

        // Load all of them
        for (GPCContent core : inherentCores) {
            if (core == null) { continue; }

            // Identify source
            Location coreLocation = core.getLocation();
            GOOPCPhysical physical = core.getContainer();

            //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a73INH\u00a77 Loading dormant based at \u00a7a " + toStringLocation(coreLocation) + "\u00a77 -\u00a7e " + physical.getTemplate().getInternalName());

            /*
             * The load method also puts the inherent structure of this
             * into the InherentStructureOverlap list.
             *
             * It actually takes place all the way deep into
             * GPCContent#loadInherentBlocks(ArrayList<CustomStructureBlock>)
             */
            load(physical, coreLocation);
        }

        // Attempt to find
        ret = getInherentStructureOverlap().get(location);

        // Already loaded
        if (ret != null) {
            //INH//OotilityCeption.Log("\u00a78GCLPHYS \u00a73INH\u00a77 Loaded a total of \u00a7bx" + ret.size());
            return ret; }

        // This should not happen
        Gunging_Ootilities_Plugin.theOots.CPLog("Incomplete inherent structure loading in\u00a7e GCL_Physical.java");
        return new ArrayList<>();
    }

    /**
     * @param location The location that might house a physical container.
     *
     * @return If the size of {@link #getContainersAt(Location)} is greater than zero.
     */
    public static boolean isPhysicalContainer(@Nullable Location location) { if (location == null) { return false; } return getContainersAt(location).size() > 0; }

    /**
     * @param location The location that might house a physical container.
     *
     * @return If there is one, the Physical Container that saves items to this location.
     *
     * @see #getInherentStructureOverlap() Complete description found here.
     */
    @Nullable public static GPCContent getContainerAt(@Nullable Location location) {
        if (location == null) { return null; }

        // Attempt to find
        ArrayList<GPCContent> containers = getContainersAt(location);

        // For each of those
        for (GPCContent container : containers) {

            // Test core location to be the requested.
            if (location.equals(container.getLocation())) { return container; }
        }

        // No container core was found
        return null;
    }

    /**
     * @param location The location that might house a physical container.
     *
     * @return If {@link #getContainerAt(Location)} returns non-null.
     */
    public static boolean isPhysicalContainerCore(@Nullable Location location) { return getContainerAt(location) != null; }
    //endregion
}
