package gunging.ootilities.gunging_ootilities_plugin.containers.loader;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOLib;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicCrucible;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCStation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Micromanages the loading of Station Containers
 */
public class GCL_Station {

    /**
     * Load a Station Container
     *
     * @param station Container to register. Unregisters any previous ones of the same template Name / ID
     */
    public static void live(@NotNull GOOPCStation station) {

        // Unloads any previous one of the same qualifications
        unload(station.getTemplate().getInternalID());
        unload(station.getTemplate().getInternalName());

        // Register in the arrays
        byInternalID.put(station.getTemplate().getInternalID(), station);
        byInternalName.put(station.getTemplate().getInternalName(), station);
        loaded.add(station);

        // Attempt to register the VanillaInventoryMapping
        if (Gunging_Ootilities_Plugin.foundMythicLib) { GooPMMOLib.a(station); }
        if (Gunging_Ootilities_Plugin.foundMythicCrucible) { GooPMythicCrucible.a(station); }
    }
    /**
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
            GOOPCStation station = loaded.get(i);

            // Is it the one? No? Skip
            if (station.getTemplate().getInternalID() != byID) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(station.getTemplate().getInternalName());
            byInternalID.remove(station.getTemplate().getInternalID());

            // Attempt to unregister the VanillaInventoryMapping
            if (Gunging_Ootilities_Plugin.foundMythicLib) { GooPMMOLib.b(station); }
            if (Gunging_Ootilities_Plugin.foundMythicCrucible) { GooPMythicCrucible.b(station); }

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
            GOOPCStation station = loaded.get(i);

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
    public static void unloadAll() {

        // Attempt to unregister the VanillaInventoryMapping
        if (Gunging_Ootilities_Plugin.foundMythicLib) { for (GOOPCStation loaded : getLoaded()) { GooPMMOLib.b(loaded); } }
        if (Gunging_Ootilities_Plugin.foundMythicCrucible) { for (GOOPCStation loaded : getLoaded()) { GooPMythicCrucible.b(loaded); } }

        // Clear arrays
        loaded.clear(); byInternalName.clear(); byInternalID.clear(); }

    /**
     * @return The Station Containers currently loaded (one for every loaded template)
     */
    @NotNull public static ArrayList<GOOPCStation> getLoaded() { return loaded; }
    @NotNull final static ArrayList<GOOPCStation> loaded = new ArrayList<>();

    /**
     * @return The Station Containers currently loaded (by its template internal ID)
     */
    @NotNull public static HashMap<Long, GOOPCStation> getByInternalID() { return byInternalID; }
    /**
     * @param id ID Currently assigned at this template
     *
     * @return The Station Containers currently loaded (by its template internal ID)
     */
    @Nullable public static GOOPCStation getByInternalID(@Nullable Long id) { if (id == null) { return null; } return getByInternalID().get(id); }
    @NotNull final static HashMap<Long, GOOPCStation> byInternalID = new HashMap<>();
    /**
     * @param internalID ID that you want to see if its loaded
     *
     * @return If there is a Station Containers loaded by this ID (its template internal ID)
     */
    public static boolean isStationContainer(long internalID) { return getByInternalID(internalID) != null; }

    /**
     * @return The Station Containers currently loaded (by its template internal name)
     */
    @NotNull public static HashMap<String, GOOPCStation> getByInternalName() { return byInternalName; }
    /**
     * @param internalName Name of this template
     *
     * @return The Station Containers currently loaded (by its template internal name)
     */
    @Nullable public static GOOPCStation getByInternalName(@Nullable String internalName) { if (internalName == null) { return null; } return getByInternalName().get(internalName); }
    @NotNull final static HashMap<String, GOOPCStation> byInternalName = new HashMap<>();
    /**
     * @param internalName Name that you want to see if its loaded
     *
     * @return If there is a Station Containers loaded by this name (its template internal name)
     */
    public static boolean isStationContainer(@Nullable String internalName) { return getByInternalName(internalName) != null; }
}
