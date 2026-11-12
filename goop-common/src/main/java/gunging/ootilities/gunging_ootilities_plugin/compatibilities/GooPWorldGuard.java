package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GooPWorldGuard {

    public static StateFlag MORTAL_IMMORTALITY;
    public static StateFlag HOV_ALLOW_SET_BACK;
    public static LocationFlag HOV_RESPAWN_LOCATION;

    public static void LoadNRegisterFlags() {

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // create a flag with the name "my-custom-flag", defaulting to true
            StateFlag flag = new StateFlag("goop-mortal-immortality", false);
            registry.register(flag);
            MORTAL_IMMORTALITY = flag; // only set our field if there was no error
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("goop-mortal-immortality");
            if (existing instanceof StateFlag) {
                MORTAL_IMMORTALITY = (StateFlag) existing;
            } else {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
            }
        }

        if (Gunging_Ootilities_Plugin.theMain.getServer().getPluginManager().getPlugin("AAA_HeartOfAvalon") != null) {

            try {

                StateFlag flag = new StateFlag("goop-allow-back-set", true);
                registry.register(flag);
                HOV_ALLOW_SET_BACK = flag;

            } catch (FlagConflictException e) {

                Flag<?> existing = registry.get("goop-allow-back-set");
                if (existing instanceof StateFlag) { HOV_ALLOW_SET_BACK = (StateFlag) existing; }
            }

            try {

                // Setup Flag
                LocationFlag flag = new LocationFlag("goop-revive-location");
                registry.register(flag);
                HOV_RESPAWN_LOCATION = flag;

            } catch (FlagConflictException e) {

                // Admit old
                Flag<?> existing = registry.get("goop-revive-location");
                if (existing instanceof LocationFlag) { HOV_RESPAWN_LOCATION = (LocationFlag) existing; }
            }
        }
    }

    public BlockVector3 BV3at(Location loc) { return BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()); }

    @Deprecated
    @NotNull public Boolean PlayerInCustomFlag(@Nullable Player player, @Nullable Integer customFlagID) { return inStateFlag(player, customFlagID); }

    /**
     * @param player Player who exists
     *
     * @param customFlagID Flag to test
     *
     * @return If the region they are in has this flag enabled
     */
    public static boolean inStateFlag(@Nullable Player player, @Nullable Integer customFlagID) {
        if (player == null || customFlagID == null) { return false; }

        // WHAT this should not even load wtf!
        if (!Gunging_Ootilities_Plugin.foundWorldGuard) {
            Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cAttempting to use World Guard while it should be disabled wth.");
            return false;
        }

        RegionContainer tContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery tQuery = tContainer.createQuery();
        ApplicableRegionSet tSet = tQuery.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

        // Supposing it Loaded
        if (tSet != null) {

            // Lets see what regions apply to da player
            try {

                // Hope I get dat flag identified
                StateFlag tFlag;

                // Which flag, again?
                switch (customFlagID){
                    case 0:
                        tFlag = MORTAL_IMMORTALITY;
                        break;
                    case 1:
                        tFlag = HOV_ALLOW_SET_BACK;
                        break;
                    default:
                        Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cCustom WorldGuard Flag ID \u00a7e" + customFlagID + "\u00a7c not found. Contact Plugin Developer.");
                        return false;
                }

                // Test for flag
                return tSet.testState(null, tFlag);

            } catch (Exception e){

                // I guess not
                return false;
            }

        } else {

            // If regions didn't load, I guess such player cannot be on a region with such flag.
            return false;
        }
    }

    /**
     * @param player Player who exists
     *
     * @param customFlagID Flag to test
     *
     * @return If the region they are in has a location specified for this flag
     */
    @Nullable public static Location inLocationFlag(@Nullable Player player, @Nullable Integer customFlagID) {
        if (player == null || customFlagID == null) { return null; }

        // WHAT this should not even load wtf!
        if (!Gunging_Ootilities_Plugin.foundWorldGuard) {
            Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cAttempting to use World Guard while it should be disabled wth.");
            return null;
        }

        RegionContainer tContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery tQuery = tContainer.createQuery();
        ApplicableRegionSet tSet = tQuery.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

        // Supposing it Loaded
        if (tSet != null) {

            // Lets see what regions apply to da player
            try {

                // Hope I get dat flag identified
                LocationFlag tFlag;

                // Which flag, again?
                switch (customFlagID){
                    case 0:
                        tFlag = HOV_RESPAWN_LOCATION;
                        break;
                    default:
                        Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cCustom WorldGuard Flag ID \u00a7e" + customFlagID + "\u00a7c not found. Contact Plugin Developer.");
                        return null;
                }

                // Test for flag
                com.sk89q.worldedit.util.Location result = tSet.queryValue(null, tFlag);
                if (result == null) { return null; }

                // That's it
                return new Location(player.getWorld(), result.getX(), result.getY(), result.getZ(), result.getYaw(), result.getPitch());

            } catch (Exception e){

                // I guess not
                return null;
            }

        } else {

            // If regions didn't load, I guess such player cannot be on a region with such flag.
            return null;
        }
    }
}
