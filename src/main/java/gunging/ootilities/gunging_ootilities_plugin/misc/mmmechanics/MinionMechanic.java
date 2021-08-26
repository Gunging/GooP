package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.util.annotations.MythicMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@MythicMechanic(
        author = "gunging",
        name = "GooPMinion",
        description = "Labels the targets as minions of the caster"
)
public class MinionMechanic extends SkillMechanic implements ITargetedEntitySkill {
    PlaceholderDouble leashRange;
    String mmSkill;
    PlaceholderDouble weight;
    PlaceholderString kindOverride;


    public MinionMechanic(CustomMechanic skill, MythicLineConfig mlc) {
        super(skill.getConfigLine(), mlc);
        leashRange = mlc.getPlaceholderDouble(new String[] { "leashrange", "lr" }, 20.0);
        mmSkill = mlc.getString(new String[] { "skill", "s" });
        weight = mlc.getPlaceholderDouble(new String[] { "weight", "w", "minionweight", "mw" }, 1D);
        kindOverride = mlc.getPlaceholderString(new String[] { "kind", "k" }, "generic");
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {
        // Gather all necessary values - caster and target
        Entity target = BukkitAdapter.adapt(targetProbably);

        // Target not player right
        if (target instanceof Player) { return false; }

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

            // Enable
            newMinion.Enable();
            return true;
        }

        return false;
    }
}
