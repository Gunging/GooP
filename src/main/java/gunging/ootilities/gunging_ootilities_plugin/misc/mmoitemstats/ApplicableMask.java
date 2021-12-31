package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ApplicableMask {
    @NotNull final static HashMap<String, ApplicableMask> loadedMasks = new HashMap<>();

    /**
     * @return The MMOItem Types to match yeah.
     */
    @NotNull public ArrayList<String> getApplicableTypes() { return applicableTypes; }
    @NotNull final ArrayList<String> applicableTypes;

    /**
     * @return The name of this applicable mask
     */
    @NotNull public String getName() { return name; }
    @NotNull String name;

    /**
     * @param nmae Name to load this / set live.
     *
     * @param types The matched item IDs
     */
    public ApplicableMask(@NotNull String nmae, @NotNull ArrayList<String> types) { name = nmae;applicableTypes = types; }

    /**
     * Reload the masks yeah
     */
    public static void reloadMasks() {

        // Starts Fresh
        loadedMasks.clear();

        // If there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.applicableMasksPair != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.applicableMasksPair;
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Log da shit
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()) {

                // Get Mask Name
                String tName = val.getKey();

                // Get Absolute List
                List<String> maskedTypes = ofgStorage.getStringList(tName);
                ArrayList<String> trueTypes = new ArrayList<>();

                // Pass on real values
                for (String str : maskedTypes) { String mod = str.toUpperCase().replace(" ", "_").replace("-", " "); trueTypes.add(mod); }

                if (!loadedMasks.containsKey(tName)) {

                    // Create and add
                    //CANPLACE//OotilityCeption. Log("\u00a7c>\u00a74>\u00a7c> \u00a77Created mask \u00a7c" + tName);
                    loadedMasks.put(tName, new ApplicableMask(tName, trueTypes));

                } else {

                    // Log
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                        Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("OnApply Masks","Error when loading mask '\u00a73" + tName + "\u00a77': There is already a mask defined with that name!"));
                    }
                }
            }
        }
    }

    /**
     * This will also create a new mask if the name is a valid
     * MMOItem Type or a comma-separated-list thereof.
     *
     * @param name Name of mask you are trying to fetch.
     *
     * @return The mask of this name, loaded, if any found.
     */
    @Nullable public static ApplicableMask getMask(@Nullable String name) {
        if (name == null) { return null; }

        // Any existing?
        ApplicableMask loadedMask = loadedMasks.get(name);
        if (loadedMask != null) { return loadedMask; }
        if (!Gunging_Ootilities_Plugin.foundMMOItems) { return null; }

        //CLI// OotilityCeption.Log("\u00a78MASK \u00a79GET\u00a77 Parsing A. Mask: \u00a7e " + name);

        // Attempt to read
        ArrayList<String> potentialTypes = new ArrayList<>();
        ArrayList<String> trueTypes = new ArrayList<>();
        if (name.contains(",")) { potentialTypes.addAll(Arrays.asList(name.split(","))); } else { potentialTypes.add(name); }

        // All valid right
        for (String potentialType : potentialTypes) {
            String pType = potentialType.replace(" ", "_").replace("-","_");

            //CLI// OotilityCeption.Log("\u00a78MASK \u00a79GET\u00a77 Trying MMOItems Type \u00a7e " + pType);

            // Any invalid makes snooze
            if (GooPMMOItems.GetMMOItemTypeFromString(pType) == null) {

                //CLI// OotilityCeption.Log("\u00a78MASK \u00a79GET\u00a77 Its a \u00a7cFailure");
                return null; }

            //CLI// OotilityCeption.Log("\u00a78MASK \u00a79GET\u00a77 That works yeah");
            trueTypes.add(pType);
        }

        // All types matched it seems...
        return new ApplicableMask(name, trueTypes);
    }

    /**
     * @param miType MMOItem Type
     *
     * @return If this mask allows such MMOItem type
     */
    public boolean AppliesTo(@Nullable String miType) {
        if (miType == null) { return false; }
        return applicableTypes.contains(miType.toUpperCase().replace(" ", "_").replace("-","_"));
    }

    /**
     * @return Names of live loaded masks
     */
    public static ArrayList<String> getLoadedMaskNames() { return new ArrayList<>(loadedMasks.keySet()); }
}
