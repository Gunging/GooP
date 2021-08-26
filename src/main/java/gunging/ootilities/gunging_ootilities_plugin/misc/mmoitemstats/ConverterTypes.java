package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.misc.ConverterPerTier;
import gunging.ootilities.gunging_ootilities_plugin.misc.ConverterTypeSettings;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterTypes {

    public static ArrayList<ConverterTypeNames> convertingTypes = new ArrayList<>();

    public static void ConverterReload(OotilityCeption oots) {

        if (Gunging_Ootilities_Plugin.theMain.mmoitemsConverterPair != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.mmoitemsConverterPair;
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Compile Converter Types
            List<String> conv = ofgStorage.getStringList("Convert_Into_MMOItems");
            ArrayList<String> trueConversion = new ArrayList<>();
            for (String con : conv) { trueConversion.add(con.toUpperCase().replace(" ", "_").replace("-", "_")); }

            // Reload
            ReloadCTypes(trueConversion);

            // For each value
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()) {

                // Get Mask Name
                String tName = val.getKey();

                // Get all involved
                ArrayList<ConverterTypeNames> nmes = FromString(tName);

                // If at least one existed, grab the sektion of it
                ConfigurationSection typeConfig = ofgStorage.getConfigurationSection(tName);

                // Attempt to get bothe
                if (typeConfig != null) {

                    ArrayList<ConverterTypeSettings> set = new ArrayList<>();

                    // For all valid
                    for (ConverterTypeNames nme : nmes) {

                        // Kreate new
                        ConverterTypeSettings setti = new ConverterTypeSettings(nme);

                        // Get OnKraft and OnPickup
                        ConfigurationSection onCraft = typeConfig.getConfigurationSection("OnCraft");
                        ConfigurationSection onPickup = typeConfig.getConfigurationSection("OnPickup");

                        // Stuff on craft
                        if (onCraft != null) {

                            //region Random Tier
                            // Get Absolute List
                            List<String> rawList = onCraft.getStringList("Tier");

                            // If long enough
                            if (rawList.size() > 0) {

                                // Add
                                setti.SetTierRates(new ArrayList<>(rawList), false);

                                //region Per Tier Modifiers
                                // Attempt to get sections
                                HashMap<String, ConfigurationSection> perTierMods = new HashMap<>();

                                // Get
                                for (String tire : rawList) {
                                    // Split
                                    int spaceSplit = tire.indexOf(" ");
                                    if (spaceSplit < 1) { spaceSplit = tire.length(); }
                                    String trueTier = tire.substring(0, spaceSplit);

                                    //Get Config section?
                                    ConfigurationSection sSection = onCraft.getConfigurationSection(trueTier);

                                    // Add if valid
                                    if (sSection != null) {

                                        // Put
                                        perTierMods.put(trueTier, sSection);
                                    }
                                }

                                // Edit thay contents
                                for (String t : perTierMods.keySet()) {

                                    // Get sektion
                                    ConfigurationSection sec = perTierMods.get(t);

                                    // Create per tier
                                    ConverterPerTier cpt = new ConverterPerTier(t);

                                    // Put information
                                    for (String stat : sec.getValues(false).keySet()) {


                                        // Get as String List
                                        ArrayList<String> statdataa = new ArrayList<>(sec.getStringList(stat));

                                        // If existed
                                        if (statdataa.size() > 1) {

                                            // Add as scuh
                                            cpt.AddStatData(stat, statdataa);

                                        } else {

                                            // Gather value
                                            String statdatum = sec.getString(stat);
                                            if (statdatum != null) {

                                                // Add I gues
                                                cpt.AddStatData(stat, statdatum);

                                                // It wasn't a simple string
                                            }
                                        }
                                    }

                                    // Append CPT
                                    setti.SetPerTierStats(t, cpt, false);
                                }
                                //endregion
                            }
                            //endregion

                            //region Set Name
                            // Get Absolute List
                            String rawString = onCraft.getString("Name");

                            // If long enough
                            if (rawString != null) {

                                // Add
                                setti.SetName(rawString, false);
                            }
                            //endregion
                        }

                        if (onPickup != null) {

                            //region Random Tier
                            // Get Absolute List
                            List<String> rawList = onPickup.getStringList("Tier");

                            // If long enough
                            if (rawList.size() > 0) {

                                // Add
                                setti.SetTierRates(new ArrayList<>(rawList), true);

                                //region Per Tier Modifiers
                                // Attempt to get sections
                                HashMap<String, ConfigurationSection> perTierMods = new HashMap<>();

                                // Get
                                for (String tire : rawList) {

                                    //Get Config section?
                                    ConfigurationSection sSection = onPickup.getConfigurationSection(tire);

                                    // Add if valid
                                    if (sSection != null) {

                                        // Put
                                        perTierMods.put(tire, sSection);
                                    }
                                }

                                // Edit thay contents
                                for (String t : perTierMods.keySet()) {

                                    // Get sektion
                                    ConfigurationSection sec = perTierMods.get(t);

                                    // Create per tier
                                    ConverterPerTier cpt = new ConverterPerTier(t);

                                    // Put information
                                    for (String stat : sec.getValues(false).keySet()) {

                                        // Gather value
                                        String statdatum = sec.getString(stat);
                                        if (statdatum != null) {

                                            // Add I gues
                                            cpt.AddStatData(stat, statdatum);

                                        // It wasn't a simple string
                                        } else {

                                            // Get as String List
                                            ArrayList<String> statdataa = new ArrayList<>(sec.getStringList(stat));

                                            // If existed
                                            if (statdataa.size() > 0) {

                                                // Add as scuh
                                                cpt.AddStatData(stat, statdataa);
                                            }
                                        }
                                    }

                                    // Append CPT
                                    setti.SetPerTierStats(t, cpt, true);
                                }
                                //endregion
                            }
                            //endregion

                            //region Rename
                            // Get Absolute List
                            String rawString = onPickup.getString("Name");

                            // If long enough
                            if (rawString != null) {

                                // Add
                                setti.SetName(rawString, false);
                            }
                            //endregion
                        }
                    }
                }
            }
        }
    }

    public static void ReloadCTypes(ArrayList<String> names) {

        ArrayList<String> realTypes = GooPMMOItems.GetMMOItem_TypeNames();
        boolean whipEnabled = realTypes.contains("WHIP");

        convertingTypes.clear();
        ConverterTypeSettings.Reset();

        // Macro-Overrides
        if (names.contains("ALL")) {

            convertingTypes.add(ConverterTypeNames.ARMOUR);
            convertingTypes.add(ConverterTypeNames.SHIELD);
            convertingTypes.add(ConverterTypeNames.SWORD);
            convertingTypes.add(ConverterTypeNames.AXE);
            convertingTypes.add(ConverterTypeNames.BOW);
            convertingTypes.add(ConverterTypeNames.CROSSBOW);
            convertingTypes.add(ConverterTypeNames.TRIDENT);
            if (whipEnabled) { convertingTypes.add(ConverterTypeNames.WHIP); }
            convertingTypes.add(ConverterTypeNames.TOOL);

        } else {

            if (names.contains("WEAPONS")) {
                names.remove("WEAPONS");
                if (!names.contains("SWORD")) { names.add("SWORD"); }
                if (!names.contains("AXE")) { names.add("AXE"); }
                if (!names.contains("BOW")) { names.add("BOW"); }
                if (!names.contains("CROSSBOW")) { names.add("CROSSBOW"); }
                if (!names.contains("TRIDENT")) { names.add("TRIDENT"); }
            }
        }

        for (String str : names) {

            if (str.equals("ARMOR")) { str = "ARMOUR"; }

            // Is it a supported plugin command?
            try {
                // Yes, it seems to be
                ConverterTypeNames cmd = ConverterTypeNames.valueOf(str);

                if (cmd != ConverterTypeNames.WHIP || whipEnabled) { convertingTypes.add(cmd); }

            // Not recognized
            } catch (IllegalArgumentException ex) {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                    Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("Converting Type '\u00a73" + str + "\u00a77' not recognized. Ignored"));
                }
            }
        }
    }

    /**
     * Gets the Converter Type Names associated to a string.
     */
    @NotNull
    public static ArrayList<ConverterTypeNames> FromString(@Nullable String str) {

        // If has whip, thay allow
        ArrayList<String> realTypes = GooPMMOItems.GetMMOItem_TypeNames();
        boolean whipEnabled = realTypes.contains("WHIP");

        // That ret
        ArrayList<ConverterTypeNames> ret = new ArrayList<>();

        // Is it str?
        if (str == null) { return ret; }

        // Upper
        str = str.toUpperCase();

        // Macro-Overrides
        if (str.equals("ALL")) {

            // Add all
            ret.add(ConverterTypeNames.ARMOUR);
            ret.add(ConverterTypeNames.SHIELD);
            ret.add(ConverterTypeNames.SWORD);
            ret.add(ConverterTypeNames.AXE);
            ret.add(ConverterTypeNames.BOW);
            ret.add(ConverterTypeNames.CROSSBOW);
            ret.add(ConverterTypeNames.TRIDENT);
            if (whipEnabled) { ret.add(ConverterTypeNames.WHIP); }
            ret.add(ConverterTypeNames.TOOL);

        // Other Makro override
        } else if (str.equals("WEAPONS")) {

            // Add weapons
            ret.add(ConverterTypeNames.SWORD);
            ret.add(ConverterTypeNames.AXE);
            ret.add(ConverterTypeNames.BOW);
            ret.add(ConverterTypeNames.CROSSBOW);
            ret.add(ConverterTypeNames.TRIDENT);

        // Add Each
        } else {

            if (str.equals("ARMOR")) { str = "ARMOUR"; }

            // Is it a supported plugin command?
            try {
                // Yes, it seems to be
                ConverterTypeNames cmd = ConverterTypeNames.valueOf(str);

                if (cmd != ConverterTypeNames.WHIP || whipEnabled) { convertingTypes.add(cmd); }

                // Not recognized
            } catch (IllegalArgumentException ex) {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback && !str.equals("CONVERT_INTO_MMOITEMS")) {

                    Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("Converting Type '\u00a73" + str + "\u00a77' not recognized. Ignored"));
                }
            }
        }

        // Ya thats fine
        return ret;
    }

    public static boolean IsConvertable(@NotNull Material type) {
        return IsConvertable(type, null);
    }
    public static boolean IsConvertable(@NotNull Material type, @Nullable RefSimulator<ConverterTypeNames> appliesto) {

        // Evaluate each type
        for (ConverterTypeNames conv : convertingTypes) {
            switch (conv) {
                case ARMOUR:
                    if (OotilityCeption.IsArmor(type)) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.ARMOUR); } return true; }
                    break;
                case SHIELD:
                    if (type == Material.SHIELD) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.SHIELD); } return true; }
                    break;
                case WHIP:
                    if ((type == Material.LEAD) ||
                            (type == Material.CARROT_ON_A_STICK) ||
                            (type == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.WARPED_FUNGUS_ON_A_STICK))) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.WHIP); } return true; }
                    break;
                case TOOL:
                    if (OotilityCeption.IsTool(type)) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.TOOL); }return true; }
                    break;
                case SWORD:
                    if (OotilityCeption.IsSword(type)) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.SWORD); }return true; }
                    break;
                case AXE:
                    if (OotilityCeption.IsAxe(type)) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.AXE); }return true; }
                    break;
                case BOW:
                    if (type == Material.BOW) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.BOW); }return true; }
                    break;
                case CROSSBOW:
                    if (type == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CROSSBOW)) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.CROSSBOW); }return true; }
                    break;
                case TRIDENT:
                    if (type == Material.TRIDENT) { if (appliesto != null) { appliesto.setValue(ConverterTypeNames.TRIDENT); }return true; }
                    break;
            }
        }

        return false;
    }
}
