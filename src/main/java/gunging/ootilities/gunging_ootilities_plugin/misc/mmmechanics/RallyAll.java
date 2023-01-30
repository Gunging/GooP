package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
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

    public RallyAll(CustomMechanic manager, @NotNull String skill, @NotNull MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public RallyAll(SkillExecutor manager, @NotNull String skill, @NotNull MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        setAsyncSafe(false);
        this.overwriteTarget = mlc.getBoolean(new String[]{"overwritetarget", "ot"}, true);
        this.rallyToTrigger = mlc.getBoolean(new String[]{"rallytotrigger", "rt", "rtt"}, false);
    }

    @Override
    public SkillResult castAtEntity(@NotNull SkillMetadata data, @Nullable AbstractEntity target) {
        if (target == null) { return SkillResult.SUCCESS; }

        // Respect current target for MythicMobs if specified
        if (!overwriteTarget) {

            // Is it a MythicMob? Cancel if it has target
            ActiveMob amx = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
            if (amx != null && amx.hasTarget()) { return SkillResult.SUCCESS; } }

        // Get as living entity
        Entity ent = target.getBukkitEntity();
        if (!(ent instanceof LivingEntity)) { return SkillResult.SUCCESS; }

        // Choose caster if rallyToTrigger is false
        Entity cas = rallyToTrigger ? (data.getTrigger() != null ? data.getTrigger().getBukkitEntity() : null) : data.getCaster().getEntity().getBukkitEntity();
        if (!(cas instanceof LivingEntity)) { return SkillResult.SUCCESS; }

        // The target of the target is now the caster
        MythicBukkit.inst().getVolatileCodeHandler().getAIHandler().setTarget((LivingEntity) ent, (LivingEntity) cas);

        // That's it
        return SkillResult.SUCCESS;
    }
}