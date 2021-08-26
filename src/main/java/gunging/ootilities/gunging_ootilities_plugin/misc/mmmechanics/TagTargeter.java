package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.ScoreRequirements;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class TagTargeter extends IEntitySelector {

    ArrayList<String> sReq = new ArrayList<>();

    public TagTargeter(MythicLineConfig var1) {
        super(var1);

        PlaceholderString sreQ = var1.getPlaceholderString(new String[]{"tags", "tag", "t"}, (String)null);

        //MM//OotilityCeption.Log("\u00a7b> > > > > > > > > > Loading \u00a7dGooPTag");
        // F
        if (sreQ != null) {
            //MM//OotilityCeption.Log("Tags Got: \u00a73" + sreQ.get());

            // Yes
             sReq.addAll(Arrays.asList((OotilityCeption.UnwrapFromCurlyBrackets(sreQ.get()).split(","))));
        }
    }

    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {

        // A Set of Entities to Return
        HashSet<AbstractEntity> ret = new HashSet<>();

        // If the tag requirements of this targeter are not null
        if (sReq != null) {

            // Attempt to parse each?
            ArrayList<String> trueSREQ = new ArrayList<>();
            for (String rq : sReq) {

                // Just make sure
                if (rq != null) {

                    // Parse
                    String p = PlaceholderString.of(rq).get(skillMetadata, skillMetadata.getCaster().getEntity());

                    // Build
                    trueSREQ.add(p);

                    // Log
                    //MM//OotilityCeption.Log("\u00a7d >> \u00a77" + p);
                }
            }

            // Test each online player
            for (Player min : Bukkit.getOnlinePlayers()) {
                //MM//OotilityCeption.Log("\u00a7e>>> \u00a77" + min.getName());

                // fail?
                boolean failure = false;

                // Get the scoreboard tags of the player
                ArrayList<String> pTags = new ArrayList<>(min.getScoreboardTags());

                // Make sure the player has all the tags specified
                for (String tg : trueSREQ) {
                    //MM//OotilityCeption.Log("\u00a7b >> \u00a77" + tg);

                    // If the player does not have the tag
                    if (!pTags.contains(tg)) {
                        //MM//OotilityCeption.Log("\u00a7c -- \u00a77Missing");

                        // Failure
                        failure = true;
                        break;
                    }
                }

                // No failure = Player had all tags
                if (!failure) {
                    //MM//OotilityCeption.Log("\u00a7a  > \u00a77Accepted");

                    // Adapt and Add
                    ret.add(BukkitAdapter.adapt((Entity) min));
                }
            }
        }

        //MM//OotilityCeption.Log("\u00a7a*\u00a77" + ret.size());
        return ret;
    }
}
