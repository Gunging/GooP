package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Map;

public class OptiFineGlint {
    // Internal Overall Storage
    public static ArrayList<OptiFineGlint> loadedGlints = new ArrayList<OptiFineGlint>();

    // Reload Config
    public static void ReloadGlints(OotilityCeption oots) {
        // Starts Fresh
        loadedGlints = new ArrayList<OptiFineGlint>();

        // If there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.optiFineGlintPair != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.optiFineGlintPair;

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Log da shit
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()){

                // Get Glint Name
                String tName = val.getKey();

                // Get Enchantment Value
                Enchantment tEnchant = null;
                if (ofgStorage.contains(tName + ".Enchantment")) { tEnchant = oots.GetEnchantmentByName(ofgStorage.getString(tName + ".Enchantment")); }

                // Get Enchantment Level
                Integer tLevel = null; Boolean igLevel = true;
                if (ofgStorage.contains(tName + ".Level")) {
                    // Something is in there so we're not ignoring level
                    igLevel = false;

                    // Does it parse bruh
                    if (oots.IntTryParse(ofgStorage.getString(tName + ".Level"))) {

                        // Set
                        tLevel = ofgStorage.getInt(tName + ".Level");
                    }
                }

                // Get Lore Line
                String tLore = null;
                if (ofgStorage.contains(tName + ".LoreLine")) { tLore = ofgStorage.getString(tName + ".LoreLine"); }

                //oots.CPLog("Loaded \u00a7e" + tName + " \u00a77 for enchantment \u00a73" + tEnchant + " " + tLevel);

                // Create and add glint
                loadedGlints.add(new OptiFineGlint(tName, tEnchant, tLevel, tLore, igLevel));
            }


        // Failed to parse
        } else {

            // Empty but not null
            loadedGlints = new ArrayList<OptiFineGlint>();
        }
    }

    // Check for Contain
    public static OptiFineGlint IsGlintLoaded(String glintName, RefSimulator<String> logger) {

        // Check every loaded Glint
        for (OptiFineGlint glnt : loadedGlints) {

            // Is it the one we searching four?
            if (glnt.getGlintName().equals(glintName)) {

                // Enchantment and Level may suck ngl
                Boolean enchSuccess, levelSuccess;

                // Success if non-null
                enchSuccess = (glnt.getEnchant() != null);

                // Success if non-null or level disabled
                levelSuccess = (glnt.ignoresLevel() || glnt.getLevel() != null);

                // Check for short bounds
                if (levelSuccess && !glnt.ignoresLevel()) {

                    // Will become false if out of bounds
                    levelSuccess = (glnt.getLevel() < 32767 && glnt.getLevel() > -32768);
                }

                // Log Any error
                if (!enchSuccess && levelSuccess) {

                    // Invalid Enchantment
                    OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The \u00a7eenchantment \u00a77associated with glint '\u00a73 " + glintName + "\u00a77' is \u00a7cinvalid\u00a77.");

                    // Nope
                    return null;
                }

                // Log Any error
                if (!levelSuccess && enchSuccess) {

                    // Invalid Level
                    OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The \u00a7eenchantment level \u00a77associated with glint '\u00a73 " + glintName + "\u00a77' is \u00a7cinvalid\u00a77.");

                    // Nope
                    return null;
                }

                // Log Double Error
                if (!levelSuccess && !enchSuccess) {

                    // Invalid Everything lol
                    OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The \u00a7eenchantment and \u00a7eenchantment levels \u00a77associated with glint '\u00a73 " + glintName + "\u00a77' are \u00a7cinvalid\u00a77.");

                    // Nope
                    return null;
                }

                // If both succeeded
                if (levelSuccess && enchSuccess) {

                    // Found enchantment
                    return glnt;
                }
            }
        }

        // Log Return
        OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "No optiFine glint of name '\u00a73 " + glintName + "\u00a77' is currently loaded.");

        // Nope
        return null;
    }

    // Apply Glint
    public static ItemStack ApplyGlint(ItemStack iSource, OptiFineGlint tGlint, RefSimulator<String> logger) {

        OotilityCeption oots = new OotilityCeption();
        // Make sure the glint fucking exists
        if (tGlint != null) {

            // Make sure its non-null
            if (iSource != null) {

                // Make sure its not air
                if (!OotilityCeption.IsAir(iSource.getType())) {

                    // Product yeet
                    ItemStack iProduct = iSource;

                    // Make sure the item meets enchantment-level requirements
                    if (tGlint.getLevel() != null) {

                        // Level Exists. Is the level serendipitously the correct already?
                        if (iSource.getEnchantmentLevel(tGlint.getEnchant()) == tGlint.getLevel()) {

                            // Yes. Wow.

                        // No. Lets add the next useless enchant ;D
                        } else {

                            // Add the next useless enchant to the item
                            iProduct = OotilityCeption.AddUselessEnchantmentOfLevel(iSource, tGlint.getLevel());
                        }
                    }

                    // The level stuff is now setup. Add the enchantment if it wasn't serendipitously added yet.
                    if (!OotilityCeption.TestForEnchantment(iProduct, tGlint.getEnchant(), null, null)) {

                        // Enchantment at least power
                        iProduct.addUnsafeEnchantment(tGlint.getEnchant(), 1);
                    }

                    // Add Lore, if defined
                    if (tGlint.getLoreLine() != null) {

                        // Jus' Append it at the end
                        iProduct = OotilityCeption.AppendLoreLine(iProduct, tGlint.getLoreLine(), null, null);
                    }

                    // Log I suppose
                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully applied \u00a73" + tGlint.getGlintName() + "\u00a77 to " + OotilityCeption.GetItemName(iProduct));

                    // Finish
                    return iProduct;

                // Item Stack is AIR
                } else {

                    //Log
                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "There is no item in there!");
                }

            // Item Stack is Null
            } else {

                //Log
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "There is no item in that slot!");
            }

        // Glint Doesnt Exist
        } else {

            //Log
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "That glint doesnt exist!");
        }

        // Null - ew
        return null;
    }

    // Define Glint
    public static OptiFineGlint DefineGlint(String glintName, Enchantment enchant, Integer enchantLevel, String loreLine, RefSimulator<String> logger) {

        // Define in YML if there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.optiFineGlintPair != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.optiFineGlintPair;

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Set Enchantment
            ofgStorage.set(glintName + ".Enchantment", enchant.getKey().getKey());

            // Set Level
            if (enchantLevel != null) { ofgStorage.set(glintName + ".Level", enchantLevel); }

            // Set lore Line
            if (loreLine != null) { ofgStorage.set(glintName + ".LoreLine", loreLine); }

            // Save
            Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);

            // Log
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfuly defined new glint: \u00a73" + glintName);

        } else {

            // Log
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "\u00a7cCan't save new glint due to \u00a7eopti-fine-glints.yml\u00a7c parsing errors. \u00a77It will be useable until next reload though.");
        }

        // Load and Return
        OptiFineGlint ret = new OptiFineGlint(glintName, enchant, enchantLevel, loreLine, enchantLevel == null);
        loadedGlints.add(ret);

        return ret;
    }

    // The Basics Stuff
    String eName;
    Enchantment tEnchant;
    Integer tLevel;
    String tLoreLine;   // Lore line WITH color codes
    Boolean noLevel;
    Boolean noLore;

    public OptiFineGlint(String glintName, Enchantment enchant, Integer enchantLevel, String loreLine, Boolean ignoreLevel) {
        eName = glintName;
        tEnchant = enchant;
        tLevel = enchantLevel;
        tLoreLine = loreLine;
        noLevel = ignoreLevel;
        noLore = (loreLine == null);
    }

    public String getGlintName() { return eName; }
    public Enchantment getEnchant() { return tEnchant; }
    public Integer getLevel() { if (noLevel) { return null; } else { return tLevel; } }
    public String getLoreLine() { if (noLore) { return null; } else { return tLoreLine; } }
    public Boolean ignoresLevel() { return noLevel; }
}
