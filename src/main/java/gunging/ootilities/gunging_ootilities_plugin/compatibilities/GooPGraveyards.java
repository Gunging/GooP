package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import io.lumine.xikage.graveyards.Graveyard;
import io.lumine.xikage.graveyards.Graveyards;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GooPGraveyards {

    public GooPGraveyards() { }

    public void CompatibilityCheck(){
        Graveyard reckon = Graveyard.get("No");
        reckon.getDiscoverRange();
    }

    public static void SimulateGraveyardsRespawn(Player gPlayer) {

        // Get the nearest graveyard
        Graveyard gGrave = Graveyard.getNearestGraveyard(gPlayer);

        // Found any?
        if (gGrave != null) {

            // Resistance to Death, according to Graveyards
            if (gGrave.getImmunityTicks() > 0) { gPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, gGrave.getImmunityTicks(), 100)); }

            // Can there be a mythic skill being ran?
            if (Gunging_Ootilities_Plugin.foundMythicMobs) {

                // Is there actually a Mythic Skill in it?
                if (gGrave.getMythicSkill().isPresent()) {

                    // Well then run it I suppose
                    GooPMythicMobs.GraveyardsRespawnSkill(gGrave.getMythicSkill().get(), gPlayer.getLocation(), gPlayer);
                }
            }

            // Vro just tp
            gPlayer.teleport(gGrave.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }
}
