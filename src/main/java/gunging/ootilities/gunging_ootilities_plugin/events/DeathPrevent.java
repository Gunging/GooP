package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPGraveyards;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPWorldGuard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathPrevent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDeathPrevent(PlayerDeathEvent event) {
        Player murdered = event.getEntity();

        // Testing for: WorldGuard Region Flag --- Is WorldGuard even enabled?
        if (Gunging_Ootilities_Plugin.foundWorldGuard) {

            // Are they in such flag?
            if (GooPWorldGuard.inStateFlag(murdered, 0)) {

                // Notify of success
                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("Mortal Immortality", "Player \u00a73" + murdered.getName() + "\u00a77 was prevented from dying."));
                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback && murdered.isOp()) Gunging_Ootilities_Plugin.theOots.PLog(murdered, OotilityCeption.LogFormat("Mortal Immortality", "You was prevented from dying."));

                // Kill Denied
                murdered.setHealth(1);

                // Graveyards Trigger
                if (Gunging_Ootilities_Plugin.foundGraveyards) {

                    // Simulate Respawn
                    GooPGraveyards.SimulateGraveyardsRespawn(murdered);
                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback && murdered.isOp()) Gunging_Ootilities_Plugin.theOots.PLog(murdered, OotilityCeption.LogFormat("Mortal Immortality", "Graveyards Activated"));
                }

            }

        }
    }
}
