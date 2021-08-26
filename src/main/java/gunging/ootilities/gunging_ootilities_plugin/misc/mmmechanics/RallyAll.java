package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Rallies all targets towards the caster or trigger of the skill.
 *
 * @author Gunging
 */
public class RallyAll extends SkillMechanic implements ITargetedEntitySkill {
    protected boolean overwriteTarget;
    protected boolean rallyToTrigger;

    public RallyAll(@NotNull CustomMechanic skill, @NotNull MythicLineConfig mlc) {
        super(skill.getConfigLine(), mlc);
        setAsyncSafe(false);
        this.overwriteTarget = mlc.getBoolean(new String[]{"overwritetarget", "ot"}, true);
        this.rallyToTrigger = mlc.getBoolean(new String[]{"rallytotrigger", "rt", "rtt"}, false);
    }

    public boolean castAtEntity(@NotNull SkillMetadata data, @Nullable AbstractEntity target) {
        if (target == null) { return true; }

        // Respect current target for MythicMobs if specified
        if (!overwriteTarget) {

            // Is it a MythicMob? Cancel if it has target
            ActiveMob amx = MythicMobs.inst().getMobManager().getMythicMobInstance(target);
            if (amx != null && amx.hasTarget()) { return true; } }

        // Get as living entity
        Entity ent = target.getBukkitEntity();
        if (!(ent instanceof LivingEntity)) { return true; }

        // Choose caster if rallyToTrigger is false
        Entity cas = rallyToTrigger ? (data.getTrigger() != null ? data.getTrigger().getBukkitEntity() : null) : data.getCaster().getEntity().getBukkitEntity();
        if (!(cas instanceof LivingEntity)) { return true; }

        // The target of the target is now the caster
        MythicMobs.inst().getVolatileCodeHandler().getAIHandler().setTarget((LivingEntity) ent, (LivingEntity) cas);

        // That's it
        return true;
    }
}