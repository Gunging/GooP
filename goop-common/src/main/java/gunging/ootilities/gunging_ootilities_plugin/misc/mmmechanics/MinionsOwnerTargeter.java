package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;

public class MinionsOwnerTargeter extends IEntitySelector {

    public MinionsOwnerTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
    }

    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {

        // Get Caster
        LivingEntity caster = (LivingEntity) BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        // A Set of Entities to Return
        HashSet<AbstractEntity> ret = new HashSet<>();

        // Get As Minion
        SummonerClassMinion scm = SummonerClassUtils.GetMinion(caster);

        // Found?
        if (scm != null) {

            // Adapt and Add
            ret.add(BukkitAdapter.adapt(scm.getOwner()));
        }

        return ret;
    }
}