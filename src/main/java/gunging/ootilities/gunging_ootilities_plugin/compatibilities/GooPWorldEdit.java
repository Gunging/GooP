package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GooPWorldEdit {

    static WorldEditPlugin theWorldEdit = null;
    public static WorldEditPlugin GetPlugin() { return theWorldEdit; }
    public static WorldEditPlugin getPlugin() { return GetPlugin(); }
    public static boolean pluginExisted() { return theWorldEdit != null; }

    public GooPWorldEdit() { }

    public void CompatibilityCheck() { theWorldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"); }

    public static boolean HasSelection(Player whom) {

        // Get Selection
        Location reg = GetMaximumOfSelecton(whom);

        // Success if Exists
        return (reg != null);
    }

    public static Location GetMaximumOfSelecton(Player whom) {

        // Get Selection
        CuboidRegion reg = GetPlayerSelection(whom);

        // Did it exist?
        if (reg != null) {

            // Find World
            World selecW = GetSelectionWorld(whom);

            // World Exists?
            if (selecW != null) {

                // Git Max
                BlockVector3 selecc = reg.getMaximumPoint();

                // Get Max IG
                return new Location(selecW, selecc.getX(), selecc.getY(), selecc.getZ());
            }
        }

        // Neop
        return null;
    }
    public static Location GetMinimumOfSelecton(Player whom) {

        // Get Selection
        CuboidRegion reg = GetPlayerSelection(whom);

        // Did it exist?
        if (reg != null) {

            // Find World
            World selecW = GetSelectionWorld(whom);

            // World Exists?
            if (selecW != null) {

                // Git Max
                BlockVector3 selecc = reg.getMinimumPoint();

                // Get Max IG
                return new Location(selecW, selecc.getX(), selecc.getY(), selecc.getZ());
            }
        }

        // Neop
        return null;
    }

    /**
     * Can only get cuboid regions
     * @param whom Player who has selected
     * @return Their selection if they have a complete selection
     */
    public static CuboidRegion GetPlayerSelection(Player whom) {


        try {

            return (CuboidRegion) (getPlugin().getSession(whom).getSelection(getPlugin().getSession(whom).getSelectionWorld()));
        } catch (Exception ignored) {

            // Fail
            return null;
        }
    }

    /**
     * Can only get cuboid regions
     * @param whom Player who has selected
     * @return Their selection if they have a complete selection
     */
    public static World GetSelectionWorld(Player whom) {

        try {

            // Try to get world ig
            return Bukkit.getWorld(getPlugin().getSession(whom).getSelectionWorld().getName());

        } catch (Exception ignored) {

            // Fail
            return null;
        }
    }
}
