package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.ConditionAction;
import org.jetbrains.annotations.NotNull;

public class CustomMMCondition extends SkillCondition {

    // All right
    public CustomMMCondition(@NotNull MythicLineConfig mlc) {
        super(mlc.getLine());

        // Whats the action?
        String action = mlc.getString(new String[] {"action", "a"}, "REQUIRED");
        if (ConditionAction.isAction(action)) { ACTION = ConditionAction.valueOf(action.toUpperCase()); }
    }
}
