package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.GooPGriefEvent;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class RebootRepair extends SkillMechanic implements INoTargetSkill {

    @Nullable PlaceholderString rebootKey;

    //NEWEN//public RebootRepair(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
        //NEWEN//super(manager, file, skill, mlc);

        /*OLDEN*/public RebootRepair(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        /*OLDEN*/super(manager, skill, mlc);

        GooPMythicMobs.newenOlden = true;
        rebootKey = mlc.getPlaceholderString(new String[]{"rebootKey", "rk"}, null);
    }

    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {

        // Reboot all
        if (rebootKey == null) {
            GooPGriefEvent.reboot();

        // Reboot specific
        } else {
            GooPGriefEvent.reboot(rebootKey.get(skillMetadata));
        }

        return SkillResult.SUCCESS;
    }
}
