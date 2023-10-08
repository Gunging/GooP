package gunging.ootilities.gunging_ootilities_plugin.containers.loader;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCPersonal;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Micromanages the loading of Personal Containers
 */
public class GCL_Personal {

    /**
     * Load the contents of every owner of a Personal Container (overwrites previous)
     * <br><br>
     * Unlike templates, the load function here actually loads the contents
     * <br><br>
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param personal Container Template Manager that will load this owner
     *
     * @param owner Owner to be loaded
     */
    public static void load(@NotNull GOOPCPersonal personal, @NotNull UUID owner) {
        GOOPCTemplate template = personal.getTemplate();
        String error = "Error when loading personal container \u00a73" + template.getInternalName() + "\u00a77 for player \u00a6e " + owner.toString() + "\u00a77: \u00a7c";
        //String warning = "Warning when loading personal container \u00a73" + template.getInternalName() + "\u00a77 for player \u00a6e " + owner.toString() + "\u00a77: \u00a76";

        // Get the pair
        FileConfigPair csPair = getStorage(template.getInternalName(), owner);

        // Load the YML from it
        YamlConfiguration storage = csPair.getStorage();

        // Get the content ItemStacks and their Indices
        @SuppressWarnings("unchecked")
        ArrayList<ItemStack> rawStoredContents =  (ArrayList<ItemStack>) storage.get(YML_PERSONAL_CONTENT);
        ArrayList<String> rawSeens = new ArrayList<>(storage.getStringList(YML_PERSONAL_SEEN));
        String storedIndices = storage.getString(YML_PERSONAL_INDICES, null);

        //LOAD//OotilityCeption.Log("\u00a7a+\u00a78+\u00a77 UUID: \u00a7a" + owner);
        //LOAD//String contentsLog = rawStoredContents == null ? "null" : String.valueOf(rawStoredContents.size());
        //LOAD//OotilityCeption.Log("\u00a7a+\u00a78++\u00a77 Contents: \u00a7a" + contentsLog);
        //LOAD//OotilityCeption.Log("\u00a7a+\u00a78++\u00a77 Indexes: \u00a7a" + storedIndices);
        //LOAD//String seensLog = rawSeens == null ? "null" : String.valueOf(rawSeens.size());
        //LOAD//OotilityCeption.Log("\u00a7a+\u00a78++\u00a77 Seens: \u00a7a" + seensLog);

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
                    UUID openerUUID = OotilityCeption.UUIDFromString(opener);
                    NameVariable nv = NameVariable.Deserialize(message);

                    // Success
                    if (openerUUID != null && nv != null) { ownerSeens.put(openerUUID, nv); }
                } } }

        // Load the inventory
        HashMap<Integer, ItemStack> ownerStorage = new HashMap<>();
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
                            ownerStorage.put(index, iSource);
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
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog(error + "Items are present but with no index."); }
            }
        }

        // Set the damn inventory
        personal.loadOwnerContents(owner, ownerStorage);
        personal.loadSeens(owner, ownerSeens);
    }

    /**
     * Load a Personal Container
     *
     * @param personal Container to register. Unregisters any previous ones of the same template Name / ID
     */
    public static void live(@NotNull GOOPCPersonal personal, @NotNull HashMap<UUID, FileConfigPair> storage) {

        // Unloads any previous one of the same qualifications
        unload(personal.getTemplate().getInternalID());
        unload(personal.getTemplate().getInternalName());

        // Register in the arrays
        byInternalID.put(personal.getTemplate().getInternalID(), personal);
        byInternalName.put(personal.getTemplate().getInternalName(), personal);
        filesByName.put(personal.getTemplate().getInternalName(), storage);
        loaded.add(personal);
    }

     /**
     * Unload a personal container by the ID of its template.
     *
     * @param byID Internal ID of the Template of the Personal Container to unload.
     */
    public static void unload(@Nullable Long byID) {
        if (byID == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCPersonal personal = loaded.get(i);

            // Is it the one? No? Skip
            if (personal.getTemplate().getInternalID() != byID) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(personal.getTemplate().getInternalName());
            byInternalID.remove(personal.getTemplate().getInternalID());
            filesByName.remove(personal.getTemplate().getInternalName());

            // That's all
            break;
        }
    }
    /**
     * Unload a personal container by the name of its template.
     *
     * @param byName Internal Name of the Template of the Personal Container to unload.
     */
    public static void unload(@Nullable String byName) {
        if (byName == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCPersonal personal = loaded.get(i);

            // Is it the one? No? Skip
            if (!personal.getTemplate().getInternalName().equals(byName)) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(personal.getTemplate().getInternalName());
            byInternalID.remove(personal.getTemplate().getInternalID());
            filesByName.remove(personal.getTemplate().getInternalName());

            // That's all
            break;
        }
    }
    /**
     * Clears all arrays
     */
    public static void unloadAll() { loaded.clear(); byInternalName.clear(); byInternalID.clear(); filesByName.clear(); }

    /**
     * Unloads this container from memory, and erases its file.
     * <br><br>
     * This will straight-up delete this storage from the files
     * and unload it from memory.
     *
     * @param personal From which container deleting.
     * @param owner Storage being unloaded
     */
    public static void unloadDelete(@NotNull GOOPCPersonal personal, @NotNull UUID owner) {
        Long oid = personal.getOID(owner);

        // Clear links
        personal.getLinkOwnerToOID().put(owner, null);
        personal.getReverseOwnerToOID().put(oid, null);
        personal.getOpenedInstances().put(owner, null);
        personal.getPerOwnerInventories().put(owner, null);

        // Clear and delete files
        HashMap<UUID, FileConfigPair> byName = getStorage(personal.getTemplate().getInternalName());
        FileConfigPair pair = byName.get(owner);
        byName.put(owner, null);
        getFilesByName().put(personal.getTemplate().getInternalName(), byName);

        // Delete
        if (pair == null) { return; }
        //noinspection ResultOfMethodCallIgnored
        pair.getFile().delete();
    }

    /**
     * @return The Personal Containers currently loaded (one for every loaded template)
     */
    @NotNull public static ArrayList<GOOPCPersonal> getLoaded() { return loaded; }
    @NotNull final static ArrayList<GOOPCPersonal> loaded = new ArrayList<>();

    /**
     * @return The Personal Containers currently loaded (by its template internal ID)
     */
    @NotNull public static HashMap<Long, GOOPCPersonal> getByInternalID() { return byInternalID; }
    /**
     * @param id ID Currently assigned at this template
     *
     * @return The Personal Containers currently loaded (by its template internal ID)
     */
    @Nullable public static GOOPCPersonal getByInternalID(@Nullable Long id) { if (id == null) { return null; } return getByInternalID().get(id); }
    @NotNull final static HashMap<Long, GOOPCPersonal> byInternalID = new HashMap<>();
    /**
     * @param internalID ID that you want to see if its loaded
     *
     * @return If there is a Personal Containers loaded by this ID (its template internal ID)
     */
    public static boolean isPersonalContainer(long internalID) { return getByInternalID(internalID) != null; }

    /**
     * @return The Personal Containers currently loaded (by its template internal name)
     */
    @NotNull public static HashMap<String, GOOPCPersonal> getByInternalName() { return byInternalName; }
    /**
     * @param internalName Name of this template
     *
     * @return The Personal Containers currently loaded (by its template internal name)
     */
    @Nullable public static GOOPCPersonal getByInternalName(@Nullable String internalName) { if (internalName == null) { return null; } return getByInternalName().get(internalName); }
    @NotNull final static HashMap<String, GOOPCPersonal> byInternalName = new HashMap<>();
    /**
     * @param internalName Name that you want to see if its loaded
     *
     * @return If there is a Personal Containers loaded by this name (its template internal name)
     */
    public static boolean isPersonalContainer(@Nullable String internalName) { return getByInternalName(internalName) != null; }

    /**
     * @return Where are the contents of this Personal Container stored?
     */
    @NotNull public static HashMap<String, HashMap<UUID, FileConfigPair>> getFilesByName() { return filesByName; }
    /**
     * @param internalName Name of the target template
     *
     * @return Where are the contents of this Personal Container stored (by owner ID)?
     */
    @NotNull public static HashMap<UUID, FileConfigPair> getStorage(@Nullable String internalName) {
        if (internalName == null) { return new HashMap<>(); }
        HashMap<UUID, FileConfigPair> ret = getFilesByName().get(internalName);
        return ret != null ? ret : new HashMap<>();
    }
    /**
     * This method will create a file for this owner if there is none.  <br>
     * It does not check that the internal name belongs to a template.  <br>
     * <b>Be mindful of when you call this.</b>
     *
     * @param internalName Name of the target template
     *
     * @param owner UUID to which the contents are saved
     *
     * @return Where are the contents of this Personal Container stored?
     */
    @NotNull public static FileConfigPair getStorage(@NotNull String internalName, @NotNull UUID owner) {
        FileConfigPair pair = getStorage(internalName).get(owner);
        if (pair != null) { return pair; }

        // Create File
        pair = Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-instances/personal/" + internalName, owner + ".yml", false, true);

        // Load File
        HashMap<UUID, FileConfigPair> storage = getStorage(internalName);
        storage.put(owner, pair); filesByName.put(internalName, storage);

        // Yes
        return pair;
    }
    @NotNull final static HashMap<String, HashMap<UUID, FileConfigPair>> filesByName = new HashMap<>();

    /**
     * Saves all the attributes of all loaded templates
     */
    public static void saveAllLoadedContents() { for (GOOPCPersonal personal : getLoaded()) { saveAllContents(personal); } }
    /**
     * Saves all the contents of all owners of this Personal Container
     *
     * @param templateID ID of the template corresponding to the Personal Container to save (in its folder directory, a file for each owner)
     */
    public static void saveAllContents(@Nullable Long templateID) { saveAllContents(getByInternalID(templateID)); }
    /**
     * Saves all the contents of all owners of this Personal Container
     *
     * @param templateName Name of the template corresponding to the Personal Container to save (in its folder directory, a file for each owner)
     */
    public static void saveAllContents(@Nullable String templateName) { saveAllContents(getByInternalName(templateName)); }
    /**
     * Saves all the contents of all owners of this Personal Container
     *
     * @param personal Personal Container to save (in its folder directory, a file for each owner)
     */
    public static void saveAllContents(@Nullable GOOPCPersonal personal) {
        if (personal == null) { return; }

        // Get owners
        HashMap<UUID, FileConfigPair> loadedOwners = getStorage(personal.getTemplate().getInternalName());

        // Save all of their files
        for (UUID owner : loadedOwners.keySet()) { saveContents(personal, owner); }
    }
    /**
     * Saves the contents of this owners of this Personal Container
     *
     * @param personal Personal Container to save (in its folder directory, a separate file from all other owners)
     */
    public static void saveContents(@NotNull GOOPCPersonal personal, @NotNull UUID owner) {

        // Find its file
        FileConfigPair directory = getStorage(personal.getTemplate().getInternalName(), owner);
        directory = Gunging_Ootilities_Plugin.theMain.GetLatest(directory);
        //CLI//OotilityCeption.Log("\u00a78GCL_PERSONAL \u00a7bSVE\u00a77 Saving at file\u00a7b " + directory.getFile().getPath() );

        // Modify Storage
        YamlConfiguration storage = directory.getStorage();

        //region Serialize Content
        HashMap<Integer, ItemStack> contents = personal.getOwnerInventory(owner);
        ArrayList<ItemStack> serializedContents = new ArrayList<>();
        StringBuilder serializedIndices = new StringBuilder();
        for (Integer idx : contents.keySet()) {

            // Get Slot
            ItemStack iSource = contents.get(idx);

            /*
             * Do not save air?
             */
            if (OotilityCeption.IsAirNullAllowed(iSource)) { continue; }
            if (iSource.getAmount() <= 0) { continue; }

            // Serialize
            serializedContents.add(iSource);
            serializedIndices.append(" ").append(idx); }
        //endregion
        //CLI//OotilityCeption.Log("\u00a78GCL_PERSONAL \u00a7bSVE\u00a77 Built\u00a7e x" + serializedContents.size() + " items\u00a77 out of \u00a76 x" + contents.size() + " contents");

        // Store thay, if has contents
        if (serializedContents.size() > 0) {
            storage.set(YML_PERSONAL_CONTENT, serializedContents);
            storage.set(YML_PERSONAL_INDICES, serializedIndices.substring(1));
            //CLI//OotilityCeption.Log("\u00a78GCL_PERSONAL \u00a7bSVE\u00a77 Saving\u00a7e " + serializedContents.size() + " items\u00a77 with indices\u00a66 " + serializedIndices.substring(1));
        } else {
            storage.set(YML_PERSONAL_CONTENT, null);
            storage.set(YML_PERSONAL_INDICES, null); }

        // Seens
        ArrayList<String> seens = personal.getSerializableSeens(owner);
        storage.set(YML_PERSONAL_SEEN, seens.size() > 0 ? seens : null);
        //CLI//OotilityCeption.Log("\u00a78GCL_PERSONAL \u00a7bSVE\u00a77 Saving file...");

        // Actually Save
        Gunging_Ootilities_Plugin.theMain.SaveFile(directory);
    }

    //region YML Configuration Names
    static final String YML_PERSONAL_SEEN = "Seen";
    static final String YML_PERSONAL_CONTENT = "Contents";
    static final String YML_PERSONAL_INDICES = "ContentIndices";
    //endregion
}
