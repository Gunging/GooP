package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.event.executors.TownyActionEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GooPTowny {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public GooPTowny() { new TownyActionEventExecutor(); }

    /**
     * Returns TRUE if the player cannot destroy such material in this location.
     */
    public static boolean IsProtectedAgainst(@NotNull Location loc, @NotNull Player whom, @NotNull Material material) {

        // Yea thats just it vro
        return !TownyActionEventExecutor.canDestroy(whom, loc, material);
    }
}
