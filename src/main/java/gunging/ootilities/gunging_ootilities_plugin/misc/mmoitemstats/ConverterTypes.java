package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.misc.ConverterPerTier;
import gunging.ootilities.gunging_ootilities_plugin.misc.ConverterTypeSettings;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterTypes {

    public static ArrayList<ConverterTypeNames> convertingTypes = new ArrayList<>();

    @Nullable public static String typePrefix = null;
    public static boolean preciseIDConversion = false;
    public static boolean blockMythicMobs = false;

    @NotNull public static String GenerateConverterID(@NotNull Material mat, @Nullable String tier) {

        if (!preciseIDConversion) { return GooPMMOItems.VANILLA_MIID; }

        String tierPost = "";
        if (tier != null) { tierPost = "_" + tier.toUpperCase(); }

        String material = "";
        if (OotilityCeption.IsNetherite(mat)) { material = "_" + "NETHERITE"; }
        else if (OotilityCeption.IsDiamond(mat)) { material = "_" + "DIAMOND"; }
        else if (OotilityCeption.IsGold(mat)) { material = "_" + "GOLDEN"; }
        else if (OotilityCeption.IsIron(mat)) { material = "_" + "IRON"; }
        else if (OotilityCeption.IsLeather(mat)) { material = "_" + "LEATHER"; }
        else if (OotilityCeption.IsChainmail(mat)) { material = "_" + "CHAINMAIL"; }
        else if (OotilityCeption.IsStone(mat)) { material = "_" + "STONE"; }
        else if (OotilityCeption.IsWooden(mat)) { material = "_" + "WOODEN"; }

        String equipment = "";
        if (OotilityCeption.IsBoots(mat)) { equipment = "_" + "BOOTS"; }
        else if (OotilityCeption.IsLeggings(mat)) { equipment = "_" + "LEGGINGS"; }
        else if (OotilityCeption.IsChestplate(mat)) { equipment = "_" + "CHESTPLATE"; }
        else if (OotilityCeption.IsHelmet(mat)) { equipment = "_" + "HELMET"; }
        else if (mat == Material.BOW) { equipment = "_" + "BOW"; }
        else if (mat == Material.CROSSBOW) { equipment = "_" + "GUN"; }
        else if (OotilityCeption.IsHoe(mat)) { equipment = "_" + "HOE"; }
        else if (OotilityCeption.IsShovel(mat)) { equipment = "_" + "SHOVEL"; }
        else if (OotilityCeption.IsPickaxe(mat)) { equipment = "_" + "PICKAXE"; }
        else if (OotilityCeption.IsAxe(mat)) { equipment = "_" + "AXE"; }
        else if (OotilityCeption.IsSword(mat)) { equipment = "_" + "SWORD"; }

        return "GENERIC" + equipment + material + tierPost;
    }

    public static void ConverterReload() {
        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73RLD\u00a77 Reloading");

        /*
         *  Is there any settings for this?
         */
        if (Gunging_Ootilities_Plugin.theMain.mmoitemsConverterPair != null) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73RLD\u00a77 File Existed");

            // Obtain file information
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.mmoitemsConverterPair;
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            typePrefix = ofgStorage.getString("MMOItems_Type_Prefix", null);
            preciseIDConversion = ofgStorage.getBoolean("Differentiate_Items", false);
            blockMythicMobs = !(ofgStorage.getBoolean("Allow_MythicItems", true));

            // Compile Converter Types
            List<String> conv = ofgStorage.getStringList("Convert_Into_MMOItems");
            ArrayList<String> trueConversion = new ArrayList<>();
            for (String con : conv) { trueConversion.add(con.toUpperCase().replace(" ", "_").replace("-", "_")); }

            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73RLD\u00a77 Found \u00a76" + conv.size() + "\u00a77 converters.");

            // Reload loaded types
            ReloadCTypes(trueConversion);

            // For each value in the file
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()) {

                // Shortcut for value name
                String configSectionName = val.getKey();

                // Get all involved
                ArrayList<ConverterTypeNames> converterNames = FromString(configSectionName);

                //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73ST-" + configSectionName + "\u00a77 Loading settings for \u00a76" + converterNames.size() + "\u00a77 converters.");

                // If at least one existed, grab the section of it
                ConfigurationSection typeConfig = ofgStorage.getConfigurationSection(configSectionName);

                // Attempt to get both crafting and pickup information.
                if (typeConfig != null) {

                    // For all valid
                    for (ConverterTypeNames converterName : converterNames) {
                        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73ST\u00a77 Settings for \u00a76" + converterName.toString());

                        // Create new settings wrapper
                        ConverterTypeSettings converterSettings = new ConverterTypeSettings(converterName);

                        // Stuff on craft
                        ConfigurationSection onCraft = typeConfig.getConfigurationSection("OnCraft");
                        if (onCraft != null) { converterSettings.SetPerTierStats(null, ParseConverterSettings(onCraft, converterSettings, null, false), false); }

                        // Stuff on pickup.
                        ConfigurationSection onPickup = typeConfig.getConfigurationSection("OnPickup");
                        if (onPickup != null) { converterSettings.SetPerTierStats(null, ParseConverterSettings(onPickup, converterSettings, null, true), true); }
                    }
                }

            }
        }

        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73RLD\u00a77 Final:");
        //RLD//for (ConverterTypeNames ct : convertingTypes) OotilityCeption.Log("\u00a78CONVERTER \u00a73RLD\u00a77 + \u00a7e" + ct.toString());

    }

    static @NotNull ConverterPerTier ParseConverterSettings(@NotNull ConfigurationSection section, @NotNull ConverterTypeSettings converterSettings, @Nullable String tierName, boolean asPickup) {
        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bSETTINGS\u00a77 Parsing Settings (Pickup? \u00a73" + asPickup + "\u00a77)");

        /*
         * Only the main configuration can specify a tier list
         */
        if (tierName == null) {

            // Get Absolute List
            List<String> rawTierList = section.getStringList("Tier");

            // If there were actually any entries in the tier list.
            if (rawTierList.size() > 0) {
                //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bSETTINGS\u00a77 Tier List\u00a7e x" + rawTierList.size());

                // Add the tier rates
                converterSettings.SetTierRates(new ArrayList<>(rawTierList), asPickup);

                // Get tier settings
                for (String rawTier : rawTierList) {
                    //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bSETTINGS\u00a77 Tier\u00a7e " + rawTier);

                    // Remove whatever number these people have in that
                    int spaceSplit = rawTier.indexOf(" ");
                    if (spaceSplit < 1) { spaceSplit = rawTier.length(); }
                    String trueTier = rawTier.substring(0, spaceSplit);

                    // Get the section of this tier configuration.
                    ConfigurationSection sSection = section.getConfigurationSection(trueTier);

                    // Cancel
                    if (sSection == null) { continue; }

                    // Apply settings
                    converterSettings.SetPerTierStats(trueTier, ParseConverterSettings(sSection, converterSettings, trueTier, asPickup), asPickup);
                }
            }
        }

        // Create Per Tier
        ConverterPerTier cpt = new ConverterPerTier(tierName);
        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bSETTINGS\u00a73 " + tierName + "-MOD\u00a77 Reading...");

        // Put information
        for (String stat : section.getValues(false).keySet()) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bCRAFT\u00a73 " + tierName + "-MOD\u00a77 Section\u00a7f " + stat);

            // Skip tier
            if (tierName == null && stat.equals("Tier")) { continue; }

            // Get as String List
            ArrayList<String> readingAsList = new ArrayList<>(section.getStringList(stat));

            // If existed
            if (readingAsList.size() > 1) {
                //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bCRAFT\u00a73 " + tierName + "-MOD\u00a77 Seems it was a string list\u00a7a x" + readingAsList.size());

                // Add as such
                cpt.AddStatData(stat, readingAsList);

            } else if (!section.isConfigurationSection(stat)) {

                // Gather value
                String readingAsString = section.getString(stat);
                if (readingAsString != null) {
                    //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bCRAFT\u00a73 " + tierName + "-MOD\u00a77 Accepted\u00a7a " + readingAsString);

                    // Add as such
                    cpt.AddStatData(stat, readingAsString);
                }

            }

            //RLD// else { OotilityCeption.Log("\u00a78CONVERTER \u00a7bCRAFT\u00a73 " + tierName + "-MOD\u00a77 It is a section,\u00a7c REJECTED "); }
        }

        // Nice
        return cpt;
    }

    public static void ReloadCTypes(ArrayList<String> names) {
        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73RLD\u00a77 Reloading \u00a76" + names.size() + "\u00a77 converters.");

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

                if (cmd != ConverterTypeNames.WHIP || whipEnabled) {
                    //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7aADD\u00a77 Added \u00a76" + cmd.toString());
                    convertingTypes.add(cmd);
                }

            // Not recognized
            } catch (IllegalArgumentException ex) {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                    Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("Converting Type '\u00a73" + str + "\u00a77' not recognized. Ignored"));
                }
            }
        }


        //RLD//for (ConverterTypeNames ct : convertingTypes) OotilityCeption.Log("\u00a78CONVERTER \u00a73RLD\u00a77 + Loaded \u00a7a" + ct.toString());
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
                ret.add(cmd);

                // Not recognized
            } catch (IllegalArgumentException ex) {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                    if (!str.equals("MMOITEMS_TYPE_PREFIX") &&
                        !str.equals("DIFFERENTIATE_ITEMS") &&
                        !str.equals("ALLOW_MYTHICITEMS") &&
                        !str.equals("CONVERT_INTO_MMOITEMS")) {

                        Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("Converting Type '\u00a73" + str + "\u00a77' not recognized. Ignored")); }
                }
            }
        }

        // Ya thats fine
        return ret;
    }

    public static boolean IsConvertable(@NotNull ItemStack stack) {
        return IsConvertable(stack, null);
    }
    public static boolean IsConvertable(@NotNull ItemStack stack, @Nullable RefSimulator<ConverterTypeNames> appliesto) {

        // Block MM
        if (Gunging_Ootilities_Plugin.foundMythicMobs && blockMythicMobs) { if (GooPMythicMobs.isMythicItem(stack)) { return false; } }

        // Usual working
        Material type = stack.getType();

        // Evaluate each type
        for (ConverterTypeNames conv : convertingTypes) {
            switch (conv) {
                case ARMOUR:
                    if (OotilityCeption.IsArmor(type)) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.ARMOUR); }
                        
                        return true;
                    }
                    break;
                case SHIELD:
                    if (type == Material.SHIELD) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.SHIELD); }
                        
                        return true;
                    }
                    break;
                case WHIP:
                    if ((type == Material.LEAD) ||
                            (type == Material.CARROT_ON_A_STICK) ||
                            (type == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.WARPED_FUNGUS_ON_A_STICK))) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.WHIP); }
                        
                        return true;
                    }
                    break;
                case TOOL:
                    if (OotilityCeption.IsTool(type)) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.TOOL); }
                        
                        return true;
                    }
                    break;
                case SWORD:
                    if (OotilityCeption.IsSword(type)) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.SWORD); }
                        
                        return true;
                    }
                    break;
                case AXE:
                    if (OotilityCeption.IsAxe(type)) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.AXE); }
                        
                        return true;
                    }
                    break;
                case BOW:
                    if (type == Material.BOW) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.BOW); }
                        
                        return true;
                    }
                    break;
                case CROSSBOW:
                    if (type == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CROSSBOW)) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.CROSSBOW); }
                        
                        return true;
                    }
                    break;
                case TRIDENT:
                    if (type == Material.TRIDENT) {
                        if (appliesto != null) { appliesto.setValue(ConverterTypeNames.TRIDENT); }
                        
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
}
