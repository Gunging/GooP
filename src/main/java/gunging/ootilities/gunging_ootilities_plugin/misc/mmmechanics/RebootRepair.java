package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.events.GooPGriefEvent;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.jetbrains.annotations.Nullable;

public class RebootRepair extends SkillMechanic implements INoTargetSkill {

    @Nullable PlaceholderString rebootKey;

    public RebootRepair(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public RebootRepair(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
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
