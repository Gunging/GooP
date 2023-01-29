package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.mm52.BKCSkillMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MinionMechanic extends BKCSkillMechanic implements ITargetedEntitySkill {
    PlaceholderDouble leashRange;
    String mmSkill;
    PlaceholderDouble weight;
    PlaceholderString kindOverride;
    boolean pvpBlock;


    public MinionMechanic(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);

        leashRange = mlc.getPlaceholderDouble(new String[] { "leashrange", "lr" }, 20.0);
        mmSkill = mlc.getString(new String[] { "skill", "s" });
        weight = mlc.getPlaceholderDouble(new String[] { "weight", "w", "minionweight", "mw" }, 1D);
        kindOverride = mlc.getPlaceholderString(new String[] { "kind", "k" }, "generic");
        pvpBlock = mlc.getBoolean(new String[] { "pvpblock", "pvpb", "preventPlayerDamage", "ppd" }, false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {
        // Gather all necessary values - caster and target
        Entity target = BukkitAdapter.adapt(targetProbably);

        // Target not player right
        if (target instanceof Player) { return SkillResult.ERROR; }

        // All right proceed
        Entity caster = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        String k = kindOverride.get(skillMetadata);
        double w  = weight.get(skillMetadata);

        // Is it minion?
        if (!SummonerClassMinion.isMinion(target)) {

            // Create entity
            SummonerClassMinion newMinion = new SummonerClassMinion(caster, target);

            // Set the options
            newMinion.setLeashRange(leashRange.get(skillMetadata, skillMetadata.getCaster().getEntity()));
            newMinion.setSkillOnRemove(mmSkill);
            newMinion.setKind(k);
            newMinion.setWeight(w);
            newMinion.setPreventPlayerDamage(pvpBlock);

            // Enable
            newMinion.Enable();
            return SkillResult.SUCCESS;
        }

        return SkillResult.ERROR;
    }
}
