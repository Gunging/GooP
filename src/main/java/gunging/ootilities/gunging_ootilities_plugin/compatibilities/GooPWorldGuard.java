package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GooPWorldGuard {

    public static StateFlag MORTAL_IMMORTALITY;
    OotilityCeption oots = new OotilityCeption();

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
    }

    public BlockVector3 BV3at(Location loc) {
        return BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
    }

    public Boolean PlayerInCustomFlag(Player player, Integer customFlagID){
        // WHAT this should not even load wtf!
        if (!Gunging_Ootilities_Plugin.foundWorldGuard) {
            oots.CPLog("\u00a7cAttempting to use World Guard while it should be disabled wth.");
            return false;
        }

        RegionContainer tContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery tQuery = tContainer.createQuery();
        ApplicableRegionSet tSet = tQuery.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

        // Supposing it Loaded
        if (tSet != null){

            // Lets see what regions apply to da player
            try {

                // Hope I get dat flag identified
                LocalPlayer lPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

                StateFlag tFlag = null;

                // Which flag, again?
                switch (customFlagID){
                    case 0:
                        tFlag = MORTAL_IMMORTALITY;
                        break;
                    default:
                        oots.CPLog("\u00a7cCustom WorldGuard Flag ID \u00a7e" + customFlagID + "\u00a7c not found. Contact Plugin Developer.");
                        return false;
                }

                // Test for flag
                if (tSet.testState(null, new StateFlag[] { tFlag })) {

                    //Well, guess player is immortal
                    return true;
                } else {

                    // Nope
                    return false;
                }

            } catch (Exception e){

                // I guess not
                return false;
            }

        } else {

            // If regions didnt load, I guess such player cannot be on a region with such flag.
            return false;
        }
    }
}
