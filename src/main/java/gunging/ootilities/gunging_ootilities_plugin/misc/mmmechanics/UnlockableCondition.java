package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.misc.GooPUnlockables;
import gunging.ootilities.gunging_ootilities_plugin.misc.QuickNumberRange;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables.GOOPUCKTServer;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import org.jetbrains.annotations.NotNull;

public class UnlockableCondition extends CustomMMCondition implements IEntityCondition {
    @NotNull final PlaceholderString uck, x;
    QuickNumberRange secondsRem;
    boolean asServer;

    public UnlockableCondition(MythicLineConfig mlc) {
        super(mlc);
        uck = mlc.getPlaceholderString(new String[]{"unlockable", "u"}, "nne");
        x = mlc.getPlaceholderString(new String[]{"value", "v", "x"}, "true");
        asServer = mlc.getBoolean(new String[]{"server", "srv", "s"}, false);
        String d  = mlc.getString(new String[]{"remainingSeconds", "remainingTime", "rs", "rt", "time", "seconds"}, "..");

        secondsRem = QuickNumberRange.FromString(d);
        if (secondsRem == null) { secondsRem = GooPMythicMobs.rangedDoubleToQNR(d); }
        if (secondsRem == null) { secondsRem = new QuickNumberRange(0D, 0D); }

        //MM//OotilityCeption.Log("\u00a7aRegistered \u00a77ASMIN Condition: \u00a7bOP:" + requireOP);
        //MM//OotilityCeption.Log("\u00a7bRGM:" + requiredGamemodes.size());
        //MM//OotilityCeption.Log("\u00a7bBGM:" + blockedGamemodes.size());
        //MM//OotilityCeption.Log("\u00a7bRNames:" + requiredNames.size());
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {

        // Get unlockable
        GooPUnlockables uckk = GooPUnlockables.From(asServer ? GOOPUCKTServer.zeroUUID : abstractEntity.getUniqueId(), uck.get());
        uckk.CheckTimer();

        // Evaluate
        String ex = x.get(abstractEntity);
        boolean requireFalse = ex.equalsIgnoreCase("false");

        // Yes
        if (requireFalse && !uckk.IsUnlocked()) { return neg(true); }

        // Time success required, but it only makes sense if it is already unlocked
        if (!secondsRem.isAny() && uckk.IsUnlocked()) { if (!secondsRem.InRange(uckk.RemainingSeconds())) { return neg(false); }}

        boolean anyTrue = ex.equalsIgnoreCase("true");
        if (anyTrue && uckk.IsUnlocked()) { return neg(true); }

        // Parse Range ig
        QuickNumberRange qnr = QuickNumberRange.FromString(ex);
        if (qnr != null) {

            // Get Unlock
            return neg(qnr.InRange(uckk.GetUnlock()));
        }

        // UUuuuh false i guess?
        return neg(false);
    }
}
