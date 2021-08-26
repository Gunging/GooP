package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.misc.GooPUnlockables;
import gunging.ootilities.gunging_ootilities_plugin.misc.QuickNumberRange;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import org.jetbrains.annotations.NotNull;

public class UnlockableCondition extends CustomMMCondition implements IEntityCondition {
    @NotNull final PlaceholderString uck, x;

    public UnlockableCondition(MythicLineConfig mlc) {
        super(mlc);
        uck = mlc.getPlaceholderString(new String[]{"unlockable", "u"}, "nne");
        x = mlc.getPlaceholderString(new String[]{"value", "v", "x"}, "true");

        //MM//OotilityCeption.Log("\u00a7aRegistered \u00a77ASMIN Condition: \u00a7bOP:" + requireOP);
        //MM//OotilityCeption.Log("\u00a7bRGM:" + requiredGamemodes.size());
        //MM//OotilityCeption.Log("\u00a7bBGM:" + blockedGamemodes.size());
        //MM//OotilityCeption.Log("\u00a7bRNames:" + requiredNames.size());
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {

        // Get unlockable
        GooPUnlockables uckk = GooPUnlockables.From(abstractEntity.getUniqueId(), uck.get());
        uckk.CheckTimer();

        // Evaluate
        String ex = x.get(abstractEntity);
        boolean requireFalse = ex.equalsIgnoreCase("false");

        // Yes
        if (requireFalse && !uckk.IsUnlocked()) { return true; }

        boolean anyTrue = ex.equalsIgnoreCase("true");
        if (anyTrue && uckk.IsUnlocked()) { return true; }

        // Parse Range ig
        QuickNumberRange qnr = QuickNumberRange.FromString(ex);
        if (qnr != null) {

            // Get Unlock
            return qnr.InRange(uckk.GetUnlock());
        }

        // UUuuuh false i guess?
        return false;
    }
}
