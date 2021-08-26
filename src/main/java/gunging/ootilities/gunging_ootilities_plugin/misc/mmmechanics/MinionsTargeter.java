package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashSet;

public class MinionsTargeter extends IEntitySelector {

    PlaceholderString kindTargetting;

    public MinionsTargeter(MythicLineConfig mlc) {
        super(mlc);
        kindTargetting = mlc.getPlaceholderString(new String[] { "kind", "k" }, null);
    }

    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {

        // Get Caster
        LivingEntity caster = (LivingEntity) BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        // A Set of Entities to Return
        HashSet<AbstractEntity> ret = new HashSet<>();

        // Get Entities
        ArrayList<SummonerClassMinion> minions = SummonerClassUtils.GetMinionsOf(caster);

        // Kind filter
        String kindFilter = null;
        if (kindTargetting != null) { kindFilter = kindTargetting.get(skillMetadata, skillMetadata.getCaster().getEntity()); }

        // FOreach
        for (SummonerClassMinion min : minions) {

            // Succ
            boolean kindSuccess = false;

            // Check king ig
            if (kindFilter != null) {

                // Does the minion have kind?
                if (min.hasKind()) {

                    // Kind matching
                    kindSuccess = min.getKind().equals(kindFilter);
                }

            // A kind was not specified
            } else  { kindSuccess = true; }

            // Add if suceed
            if (kindSuccess) {

                // Adapt and Add
                ret.add(BukkitAdapter.adapt(min.getMinion()));
            }
        }

        return ret;
    }
}