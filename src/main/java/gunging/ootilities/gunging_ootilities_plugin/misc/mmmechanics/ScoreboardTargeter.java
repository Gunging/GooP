package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.ScoreRequirements;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashSet;

public class ScoreboardTargeter extends IEntitySelector {

    PlaceholderString sreQ;

    public ScoreboardTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        sreQ = mlc.getPlaceholderString(new String[]{"scores", "score", "sc", "s"}, (String)null);
        //DBG//OotilityCeption.Log("\u00a7b> > > > > > > > > > Loading \u00a73GooPScore");
    }

    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {

        // A Set of Entities to Return
        HashSet<AbstractEntity> ret = new HashSet<>();

        // Build sreq
        ArrayList<ScoreRequirements> sReq = ScoreRequirements.FromCompactString(sreQ.get(skillMetadata, skillMetadata.getCaster().getEntity()));

        // FOreach
        for (Player min : Bukkit.getOnlinePlayers()) {
            //DBG//OotilityCeption.Log("\u00a7e>>> \u00a77" + min.getName());

            // fail?
            boolean failure = false;

            // Meets?
            for (ScoreRequirements sR : sReq) {
                //DBG//OotilityCeption.Log(" \u00a73>>> \u00a77" + sR.toString());

                // Was it a valid objective?
                if (sR.validObjective()) {
                    //DBG//OotilityCeption.Log("\u00a7b  >> \u00a77" + sR.getObjectiveName());

                    // Well get player's score
                    int pScore = OotilityCeption.GetPlayerScore(sR.getObjective(), min);
                    //DBG//OotilityCeption.Log("\u00a7b  >> \u00a77" + pScore);

                    // Compare it
                    failure = !sR.InRange(pScore + 0.0D) || failure;
                }
            }

            // No Fail
            if (!failure) {
                //DBG//OotilityCeption.Log("\u00a7a  > \u00a77Accepted");

                // Adapt and Add
                ret.add(BukkitAdapter.adapt((Entity) min));
            }
        }

        //DBG//OotilityCeption.Log("\u00a7a*\u00a77" + ret.size());
        return ret;
    }
}
