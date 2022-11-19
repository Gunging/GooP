package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.earth2me.essentials.Essentials;
import org.bukkit.Location;

import javax.annotation.Nullable;

public class GooPEssentials {

    static Essentials huh;
    public GooPEssentials(Essentials ess) { huh = ess; }

    /**
     * @param warpName Name of the CMI warp
     *
     * @return The location it takes you to if it exists
     */
    @Nullable public static Location getWarp(@Nullable String warpName) {
        if (huh == null) { return null; }

        // What
        if (warpName == null) { return null; }

        // Anyway
        try { return huh.getWarps().getWarp(warpName); } catch (Throwable ignored) { return null; }
    }
}
