package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Vex;

import java.io.File;

public class VexChargingMechanic extends SkillMechanic implements ITargetedEntitySkill {

    boolean valueToSet;

    //NEWEN//public VexChargingMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
        //NEWEN//super(manager, file, skill, mlc);
        /*OLDEN*/public VexChargingMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        /*OLDEN*/super(manager, skill, mlc);
        GooPMythicMobs.newenOlden = true;

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
