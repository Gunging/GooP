package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.ConditionAction;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import org.jetbrains.annotations.NotNull;

public class CustomMMCondition extends SkillCondition {

    // All right
    public CustomMMCondition(@NotNull MythicLineConfig mlc) {
        super(mlc.getLine());

        // Whats the action?
        String action = mlc.getString(new String[] {"action", "a"}, "REQUIRED");

        // Adapt i gues
        action = action.replace("<&sp>", " ");
        if (action.startsWith("\"")) { action = action.substring(1); }
        if (action.endsWith("\"")) { action = action.substring(0, action.length() - 1); }

        //CND//OotilityCeption.Log("\u00a78ACT\u00a76 RAW\u00a77 " + action);

        this.ACTION = ConditionAction.REQUIRED;
        this.actionVar = null;

        String[] split = action.split(" ");
        if (split.length >= 1) {

            //CND//OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Split into \u00a7f" + split.length);

            if (ConditionAction.isAction(split[0])) {
                //CND//OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 First action \u00a7e" + split[0]);

                this.ACTION = ConditionAction.valueOf(split[0].toUpperCase());
                if (split.length > 1) {
                    //CND//OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Action Var Identified \u00a7a" + split[1]);
                    this.actionVar = PlaceholderString.of(split[1]);
                }

            } else {
                //CND//OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 First condition \u00a73" + split[0]);
                this.conditionVar = split[0];

                if (split.length > 1 && ConditionAction.isAction(split[1])) {
                    //CND//OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Second action \u00a7e" + split[1]);
                    this.ACTION = ConditionAction.valueOf(split[1].toUpperCase());

                    if (split.length > 2) {
                        //CND//OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Action Var Identified \u00a7a" + split[2]);
                        this.actionVar = PlaceholderString.of(split[2]);
                    }
                }
            }
        }


        //CND//OotilityCeption.Log("\u00a78ACT\u00a76 CC\u00a77 " + ACTION.toString());
        //CND//if (actionVar != null) OotilityCeption.Log("\u00a78ACT\u00a76 CC\u00a77 " + actionVar.get());
    }
}
