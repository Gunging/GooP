package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.conditions.ConditionAction;
import org.jetbrains.annotations.NotNull;

public class CustomMMCondition extends SkillCondition {

    boolean negate;

    public CustomMMCondition(@NotNull MythicLineConfig mlc) {
        super(mlc.getLine());

        // Whats the action?
        String action = mlc.getString(new String[] {"action", "a"}, "REQUIRED");

        // Negate
        negate = mlc.getBoolean(new String[] {"negate"}, false);

        // Adapt i gues
        action = action.replace("<&sp>", " ");
        if (action.startsWith("\"")) { action = action.substring(1); }
        if (action.endsWith("\"")) { action = action.substring(0, action.length() - 1); }

        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a73 >--------------------------------<");
        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a77 Parsing MM Line with GooP Condition: ");
        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a7b " + mlc.getLine());
        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a7e ORIGINAL\u00a77 Action:\u00a7f " + ACTION.toString());
        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a7e ORIGINAL\u00a77 Action Var:\u00a7f " + (actionVar == null ? null : actionVar.get()));
        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 RAW\u00a77 " + action);

        this.ACTION = ConditionAction.REQUIRED;
        this.actionVar = null;

        String[] split = action.split(" ");
        if (split.length >= 1) {

            /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Split into \u00a7f" + split.length);

            if (ConditionAction.isAction(split[0])) {
                /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 First action \u00a7e" + split[0]);

                try { this.ACTION = ConditionAction.valueOf(split[0].toUpperCase()); } catch (IllegalArgumentException ignored) { this.ACTION = ConditionAction.get(split[0].toUpperCase()); }
                if (split.length > 1) {
                    /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Action Var Identified \u00a7a" + split[1]);
                    this.actionVar = PlaceholderString.of(split[1]);
                }

            } else {
                /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 First condition \u00a73" + split[0]);
                this.conditionVar = split[0];

                if (split.length > 1 && ConditionAction.isAction(split[1])) {
                    /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Second action \u00a7e" + split[1]);
                    try { this.ACTION = ConditionAction.valueOf(split[1].toUpperCase()); } catch (IllegalArgumentException ignored) { this.ACTION = ConditionAction.get(split[1].toUpperCase()); }

                    if (split.length > 2) {
                        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 SP\u00a77 Action Var Identified \u00a7a" + split[2]);
                        this.actionVar = PlaceholderString.of(split[2]);
                    }
                }
            }
        }


        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a76 CC\u00a77 " + ACTION.toString());
        /*CND*/if (actionVar != null) OotilityCeption.Log("\u00a78ACT\u00a76 CC\u00a77 " + actionVar.get());
        /*CND*/OotilityCeption.Log("\u00a78ACT\u00a73 >--------------------------------<");
    }

    /**
     * @param value Result of this Mythic Condition
     *
     * @return Inverted if necessary
     */
    boolean neg(boolean value) { return value == !isNegate(); }

    /**
     * @return If the result of this CustomMMCondition should be inverted
     */
    public boolean isNegate() { return negate; }
}
