package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.QuickNumberRange;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import org.jetbrains.annotations.Nullable;

public class AbsoluteMotionCondition extends SkillCondition implements IEntityCondition {

    // Params
    @Nullable QuickNumberRange acceptedX = null;
    @Nullable QuickNumberRange acceptedY = null;
    @Nullable QuickNumberRange acceptedZ = null;

    public AbsoluteMotionCondition(MythicLineConfig mlc) {
        super(mlc.getLine());
        String expected = mlc.getString(new String[]{"velocity", "v", "vector"}, this.conditionVar);

        // Split by commas
        String[] vec = expected.replace("\"", "").replace("<&cm>", ",").replace(" ", "<&sp>").replace("<&sp>", ",").split(",");

        //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a77 Received\u00a7e " + expected);

        // He he ha ha
        if (vec.length >= 1) { acceptedX = QuickNumberRange.FromString(vec[0]); }
        if (vec.length >= 2) { acceptedY = QuickNumberRange.FromString(vec[1]); }
        if (vec.length >= 3) { acceptedZ = QuickNumberRange.FromString(vec[2]); }

        //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a77 X\u00a7e " + acceptedX);
        //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a77 Y\u00a7e " + acceptedY);
        //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a77 Z\u00a7e " + acceptedZ);
    }

    public boolean check(AbstractEntity entity) {
        AbstractVector v = entity.getVelocity();

        //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a77 Comparing X\u00a7e " + acceptedX + "\u00a77 to" + v.getX());
        //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a77 Comparing Y\u00a7e " + acceptedY + "\u00a77 to" + v.getY());
        //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a77 Comparing Z\u00a7e " + acceptedZ + "\u00a77 to" + v.getZ());

        // Check against X Y Z of expected range
        if (acceptedX != null) { if (!acceptedX.InRange(v.getX())) {
            //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a7c X");
            return false; } }
        if (acceptedY != null) { if (!acceptedY.InRange(v.getY())) {
            //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a7c Y");
            return false; } }
        if (acceptedZ != null) { if (!acceptedZ.InRange(v.getZ())) {
            //AMC//OotilityCeption.Log("\u00a78MMC\u00a73 AMC\u00a7c Z");
            return false; } }
        return true;
     }
}
