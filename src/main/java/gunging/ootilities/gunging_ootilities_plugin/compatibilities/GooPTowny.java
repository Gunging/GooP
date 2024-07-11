package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.palmergames.bukkit.towny.event.MobRemovalEvent;
import com.palmergames.bukkit.towny.event.executors.TownyActionEventExecutor;
import com.palmergames.bukkit.towny.event.mobs.MobSpawnRemovalEvent;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.SummonMinionMechanic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class GooPTowny implements Listener {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public GooPTowny() { new TownyActionEventExecutor(); }

    /**
     * Returns TRUE if the player cannot destroy such material in this location.
     */
    public static boolean IsProtectedAgainst(@NotNull Location loc, @NotNull Player whom, @NotNull Material material) {

        // Yea thats just it vro
        return !TownyActionEventExecutor.canDestroy(whom, loc, material);
    }

    @EventHandler
    public void onTownyDeleetSummon(@NotNull MobSpawnRemovalEvent event) {
        if (!Gunging_Ootilities_Plugin.foundMythicMobs) { return; }
        //SPW//OotilityCeption.Log("\u00a78GPT\u00a7c MSR\u00a77 Removal of " + event.getEntity().getName());

        // Spawning minion?
        if (SummonMinionMechanic.minionSpawn == null) {
            //SPW//OotilityCeption.Log("\u00a78GPT\u00a7c MSR\u00a77 No spawn loc");
            return; }

        // Same world?
        if (!SummonMinionMechanic.minionSpawn.getWorld().getName().equals(event.getEntity().getLocation().getWorld().getName())) {
            //SPW//OotilityCeption.Log("\u00a78GPT\u00a7c MSR\u00a77 Other world");
            return; }

        // Must be close
        if (SummonMinionMechanic.minionSpawn.distance(event.getEntity().getLocation()) > 5) {
            //SPW//OotilityCeption.Log("\u00a78GPT\u00a7c MSR\u00a77 Too far " + SummonMinionMechanic.minionSpawn.distance(event.getEntity().getLocation()));
            return; }

        // This is a minion spawning, please do not delete.
        event.setCancelled(true);
        //SPW//OotilityCeption.Log("\u00a78GPT\u00a7c MSR\u00a7a Minion Protection");
    }

    @EventHandler
    public void onTownyDeleetSummon(@NotNull MobRemovalEvent event) {

        // Attempt to find minion
        SummonerClassMinion mn = SummonerClassUtils.GetMinion(event.getEntity());

        // Sleep
        if (mn == null) { return; }

        // Owner is a player? Do not delete this minion
        if (mn.getOwner() instanceof Player) { event.setCancelled(true); }
    }
}
