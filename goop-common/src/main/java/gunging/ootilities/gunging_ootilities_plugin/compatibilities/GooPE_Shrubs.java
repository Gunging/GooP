package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.mmoitem_shrubs.MMOItem_Shrub;
import gunging.ootilities.mmoitem_shrubs.MMOItem_Shrub_Manager;
import gunging.ootilities.mmoitem_shrubs.MMOItem_Shrubs;
import org.bukkit.Location;

import java.util.ArrayList;

public class GooPE_Shrubs {

    public static ArrayList<String> loadedShrubTypes = new ArrayList<>();

    public static void ReloadShrubTypes() {

        // Reload Types
        MMOItem_Shrubs.theMain.Reload(false);

        ReloadShrubNames();
    }

    public static ArrayList<String> getLoadedShrubTypes() { return loadedShrubTypes; }

    public static void ReloadShrubNames() {
        loadedShrubTypes = new ArrayList<>();
        for (MMOItem_Shrub sh : MMOItem_Shrub_Manager.loadedShrubTemplates) {

            // Add its name
            loadedShrubTypes.add(sh.name);
        }
    }

    public static Boolean IsShrubTypeLoaded(String type, RefSimulator<String> logger) {
        if (loadedShrubTypes.size() < 1) { ReloadShrubNames(); }

        if (loadedShrubTypes.contains(type)) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Shrub type '\u00a73" + type + "\u00a77' indeed loaded.");
            return true;

        } else {
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "There is no loaded Shrub Type of name \u00a73" + type);
            return false;
        }
    }

    public static void CreateShrubInstanceAt(String type, Location loc, RefSimulator<String> logger) {

        if (IsShrubTypeLoaded(type, logger)) {

            // No shrub exists there, right?
            if (MMOItem_Shrub_Manager.IsBlockAShrub(loc)) {

                // Cant create cuz block is already a shrub
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "That block is already a shrub!");

            } else {

                // Create shrub at location with no source mmoitem
                MMOItem_Shrub_Manager.CreateNewShrubInstanceAt(MMOItem_Shrub_Manager.loadedShrubLink.get(type), loc, null, null);

                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "New shrub created!");
            }
        }
    }
}
