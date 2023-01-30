package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Vex;

public class VexChargingMechanic extends SkillMechanic implements ITargetedEntitySkill {

    boolean valueToSet;

    public VexChargingMechanic(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public VexChargingMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        valueToSet = mlc.getBoolean(new String[] { "charging", "c"}, true);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {

        // Must be vex
        if (!(targetProbably.getBukkitEntity() instanceof Vex)) { return SkillResult.INVALID_TARGET; }

        // Okay
        Vex vex = (Vex) targetProbably.getBukkitEntity();

        // Set
        vex.setCharging(valueToSet);

        // Yes
        return SkillResult.SUCCESS;
    }
}
