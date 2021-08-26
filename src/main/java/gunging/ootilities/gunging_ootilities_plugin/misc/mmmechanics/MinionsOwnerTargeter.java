package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;

public class MinionsOwnerTargeter extends IEntitySelector {

    public MinionsOwnerTargeter(MythicLineConfig var1) {
        super(var1);
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