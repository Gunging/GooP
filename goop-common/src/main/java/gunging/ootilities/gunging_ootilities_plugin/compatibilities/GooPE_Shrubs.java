package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Bridge to the MMOItem Shrubs addon.
 * <p>
 * Shrubs is a separately distributed, closed addon with no published API artifact, so it cannot
 * be a compile-time dependency of an open build. The addon announces itself by calling
 * {@link Gunging_Ootilities_Plugin#EnableMMOItemShrubs()}; after that, the few calls GooP makes
 * into it go through reflection on its public static members. If the addon changes shape, the
 * calls fail softly and report that the shrub action is unavailable.
 */
public class GooPE_Shrubs {

    private static final String PKG = "gunging.ootilities.mmoitem_shrubs.";

    public static ArrayList<String> loadedShrubTypes = new ArrayList<>();

    public static void ReloadShrubTypes() {
        try {
            Class<?> main = Class.forName(PKG + "MMOItem_Shrubs");
            Object theMain = main.getField("theMain").get(null);
            main.getMethod("Reload", boolean.class).invoke(theMain, false);
        } catch (ReflectiveOperationException | LinkageError e) {
            OotilityCeption.Log("§cCould not reload MMOItem Shrubs: §7" + e);
        }
        ReloadShrubNames();
    }

    public static ArrayList<String> getLoadedShrubTypes() { return loadedShrubTypes; }

    public static void ReloadShrubNames() {
        loadedShrubTypes = new ArrayList<>();
        Object templates = managerStatic("loadedShrubTemplates");
        if (!(templates instanceof Collection)) { return; }
        for (Object shrub : (Collection<?>) templates) {
            try {
                Field name = shrub.getClass().getField("name");
                loadedShrubTypes.add(String.valueOf(name.get(shrub)));
            } catch (ReflectiveOperationException ignored) { }
        }
    }

    public static Boolean IsShrubTypeLoaded(String type, RefSimulator<String> logger) {
        if (loadedShrubTypes.size() < 1) { ReloadShrubNames(); }

        if (loadedShrubTypes.contains(type)) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Shrub type '§3" + type + "§7' indeed loaded.");
            return true;
        } else {
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "There is no loaded Shrub Type of name §3" + type);
            return false;
        }
    }

    public static void CreateShrubInstanceAt(String type, Location loc, RefSimulator<String> logger) {
        if (!IsShrubTypeLoaded(type, logger)) { return; }
        try {
            Class<?> manager = Class.forName(PKG + "MMOItem_Shrub_Manager");
            boolean isShrub = (boolean) manager.getMethod("IsBlockAShrub", Location.class).invoke(null, loc);
            if (isShrub) {
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "That block is already a shrub!");
                return;
            }

            Object link = managerStatic("loadedShrubLink");
            Object template = link instanceof Map ? ((Map<?, ?>) link).get(type) : null;
            if (template == null) {
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Shrub type §3" + type + "§7 has no template.");
                return;
            }

            for (Method m : manager.getMethods()) {
                if (m.getName().equals("CreateNewShrubInstanceAt") && m.getParameterCount() == 4) {
                    m.invoke(null, template, loc, null, null);
                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "New shrub created!");
                    return;
                }
            }
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "This MMOItem Shrubs version cannot create shrubs from GooP.");

        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Could not create the shrub: §3" + e);
        }
    }

    /** @return A public static field of the Shrubs manager class, or null if unavailable */
    @Nullable private static Object managerStatic(@NotNull String field) {
        try {
            return Class.forName(PKG + "MMOItem_Shrub_Manager").getField(field).get(null);
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }
}
