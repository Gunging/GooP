package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.conditions.ISkillMetaCondition;
import io.lumine.xikage.mythicmobs.util.annotations.MythicCondition;
import io.lumine.xikage.mythicmobs.util.annotations.MythicField;
import io.lumine.xikage.mythicmobs.utils.numbers.RangedDouble;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public class DistanceFromOriginCondition extends CustomMMCondition implements ISkillMetaCondition {
    protected RangedDouble distance;

    boolean matchSelf;
    boolean matchTarget;
    boolean matchTrigger;

    public DistanceFromOriginCondition(MythicLineConfig mlc) {
        super(mlc);
        String d = mlc.getString(new String[]{"distance", "d"}, this.conditionVar);
        matchSelf = mlc.getBoolean(new String[]{"self", "s", "caster", "c"}, false);
        matchTrigger = mlc.getBoolean(new String[]{"trigger", "trig"}, false);
        matchTarget = mlc.getBoolean(new String[]{"target", "targ", "t"}, !matchSelf && !matchTrigger);
        this.distance = new RangedDouble(d, true);
        //MM//OotilityCeption.Log("\u00a7aRegistered \u00a77DO Condition: \u00a7bS:" + matchSelf + "\u00a77, \u00a7bT:" + matchTarget + "\u00a77, \u00a7bTrig: " + matchTrigger);
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {

        AbstractLocation origin = skillMetadata.getOrigin();

        if (matchTrigger && skillMetadata.getTrigger() != null) {

            double diffSq = (float)origin.distanceSquared(skillMetadata.getTrigger().getLocation());
            //DO//OotilityCeption.Log("\u00a7aDO \u00a77DO Trigger Result: \u00a7e" + this.distance.equals(diffSq));
            return this.distance.equals(diffSq);

        } else if (matchSelf && skillMetadata.getCaster() != null) {

            double diffSq = (float)origin.distanceSquared(skillMetadata.getCaster().getLocation());
            //DO//OotilityCeption.Log("\u00a7aDO \u00a77DO Self Result: \u00a7e" + this.distance.equals(diffSq));
            return this.distance.equals(diffSq);

        } else if (matchTarget) {
            if (skillMetadata.getEntityTargets() != null) {
                for (AbstractEntity target : skillMetadata.getEntityTargets()) {
                    if (target == null) { continue; }

                    double diffSq = (float)origin.distanceSquared(target.getLocation());
                    if (this.distance.equals(diffSq)) { return true; }
                }
            }

            if (skillMetadata.getLocationTargets() != null) {

                for (AbstractLocation target : skillMetadata.getLocationTargets()) {
                    if (target == null) { continue; }

                    double diffSq = (float)origin.distanceSquared(target);
                    if (this.distance.equals(diffSq)) { return true; }
                }
            }

            //DO//OotilityCeption.Log("\u00a7aDO \u00a77Failed DO: \u00a7cno target matched");
            return false;
        }

        //DO//OotilityCeption.Log("\u00a7aDO \u00a77Failed DO: \u00a7cNo matches");
        return false;
    }
}
