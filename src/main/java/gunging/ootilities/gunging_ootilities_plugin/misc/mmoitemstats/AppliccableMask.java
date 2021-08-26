package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppliccableMask {
    static HashMap<String, AppliccableMask> aMasks = new HashMap<>();

    ArrayList<String> appliccableTypes;
    public String name;

    public static void ReloadMasks(OotilityCeption oots) {

        // Starts Fresh
        aMasks = new HashMap<>();

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
                for (String str : maskedTypes) {
                    String mod = str.toUpperCase().replace(" ", "_").replace("-", " ");
                    trueTypes.add(mod);
                }

                if (!aMasks.containsKey(tName)) {

                    // Create and add
                    //CANPLACE//OotilityCeption. Log("\u00a7c>\u00a74>\u00a7c> \u00a77Created mask \u00a7c" + tName);
                    aMasks.put(tName, new AppliccableMask(tName, trueTypes));

                } else {

                    // Log
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                        oots.CLog(oots.LogFormat("OnApply Masks","Error when loading mask '\u00a73" + tName + "\u00a77': There is already a mask defined with that name!"));
                    }
                }
            }
        }
    }
    public AppliccableMask(String nmae, ArrayList<String> types) {
        name = nmae;
        appliccableTypes = types;
    }
    public static AppliccableMask GetMask(String name) {
        //CANPLACE//OotilityCeption. Log("\u00a74: \u00a77Mask Contained: \u00a7c" + aMasks.containsKey(name));
        return aMasks.getOrDefault(name, null);
    }
    public boolean AppliesTo(String targetType) {
        if (targetType == null) { return false; }
        return appliccableTypes.contains(targetType.toUpperCase().replace(" ", "_").replace("-","_"));
    }

    public static ArrayList<String> GetLoadedMaskNames() { return new ArrayList<>(aMasks.keySet()); }
}
