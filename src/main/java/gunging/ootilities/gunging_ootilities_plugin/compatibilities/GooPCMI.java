package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CommandsHandler;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

public class GooPCMI {

    /**
     * @return The CMI Instance (apparently)
     */
    @NotNull public static CMI getInstance() { return CMI.getInstance(); }

    /**
     * @param warpName Name of the CMI warp
     *
     * @return The location it takes you to if it exists
     */
    @Nullable public static Location getWarp(@Nullable String warpName) {

        CommandsHandler inst = getInstance().getCommandManager();

        try {

            Class inste = inst.getClass();
            OotilityCeption.Log("\u00a78GOOPCMI\u00a7e WARP\u00a7c class " + inste.getName() + " " + inste.getSimpleName());

            for (Field f : inste.getDeclaredFields()) { OotilityCeption.Log("\u00a78GOOPCMI\u00a7e WARP\u00a7a +\u00a77 " + f.getName() + " " + f.getType().getSimpleName()); }

            Field mngr = inste.getDeclaredField("plugin");
            OotilityCeption.Log("\u00a78GOOPCMI\u00a7e WARP\u00a7c field");

            mngr.setAccessible(true);
            OotilityCeption.Log("\u00a78GOOPCMI\u00a7e WARP\u00a7c access");

            CMI instance = (CMI) mngr.get(inst);
            OotilityCeption.Log("\u00a78GOOPCMI\u00a7e WARP\u00a77 INS" + (instance == null ? "null" : instance.getClass().getSimpleName()));

            if (instance != null) OotilityCeption.Log("\u00a78GOOPCMI\u00a7e WARP\u00a77 WMG" + (instance.getWarpManager() == null ? "null" : instance.getWarpManager().getClass().getSimpleName()));

            if (instance != null) OotilityCeption.Log("\u00a78GOOPCMI\u00a7e WARP\u00a77 #" + instance.getWarpManager().getWarps().size());

            for (Map.Entry<String, CmiWarp> map : getInstance().getWarpManager().getWarps().entrySet()) {

                OotilityCeption.Log("\u00a78GOOPCMI\u00a76 WARP\u00a7a +\u00a77" + map.getKey());
            }
        } catch (Exception ignored) {

            OotilityCeption.Log("\u00a78GOOPCMI\u00a76 WARP\u00a7d " + ignored.getMessage());
            ignored.printStackTrace();

        }

        OotilityCeption.Log("\u00a78GOOPCMI\u00a76 WARP\u00a77 CMI" + (getInstance() == null ? "null" : getInstance().getClass().getSimpleName()));

        OotilityCeption.Log("\u00a78GOOPCMI\u00a76 WARP\u00a77 WMG" + (getInstance().getWarpManager() == null ? "null" : getInstance().getWarpManager().getClass().getSimpleName()));

        OotilityCeption.Log("\u00a78GOOPCMI\u00a76 WARP\u00a77 #" + getInstance().getWarpManager().getWarps().size());

        for (Map.Entry<String, CmiWarp> map : getInstance().getWarpManager().getWarps().entrySet()) {

            OotilityCeption.Log("\u00a78GOOPCMI\u00a76 WARP\u00a7a +\u00a77" + map.getKey());
        }

        // Sleeper warp
        CmiWarp found = getInstance().getWarpManager().getWarp(warpName);
        if (found == null) {
            OotilityCeption.Log("\u00a78GOOPCMI\u00a76 WARP\u00a77 Warp not found");
            return null; }

        // Locate
        return found.getLoc().getBukkitLoc();
    }
}
