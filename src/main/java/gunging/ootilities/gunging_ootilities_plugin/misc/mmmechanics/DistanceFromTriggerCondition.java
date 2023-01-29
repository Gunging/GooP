package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.misc.QuickNumberRange;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;

public class DistanceFromTriggerCondition extends CustomMMCondition implements ISkillMetaCondition {
    protected QuickNumberRange distance;

    boolean matchSelf;
    boolean matchTarget;

    public DistanceFromTriggerCondition(MythicLineConfig mlc) {
        super(mlc);
        String d = mlc.getString(new String[]{"distance", "d"}, this.conditionVar);
        matchSelf = mlc.getBoolean(new String[]{"self", "s", "caster", "c"}, false);
        matchTarget = mlc.getBoolean(new String[]{"target", "targ", "t"}, !matchSelf);

        distance = QuickNumberRange.FromString(d);
        if (distance == null) { distance = GooPMythicMobs.rangedDoubleToQNR(d); }
        if (distance == null) { distance = new QuickNumberRange(0D, 0D); }
        //MM//OotilityCeption.Log("\u00a7aRegistered \u00a77DO Condition: \u00a7bS:" + matchSelf + "\u00a77, \u00a7bT:" + matchTarget + "\u00a77, \u00a7bTrig: " + matchTrigger);
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {
        if (skillMetadata.getTrigger() == null) { return false; }
        AbstractLocation origin = skillMetadata.getTrigger().getLocation();

        if (matchSelf && skillMetadata.getCaster() != null) {

            double diffSq = (float)origin.distanceSquared(skillMetadata.getCaster().getLocation());
            //DO//OotilityCeption.Log("\u00a7aDO \u00a77DO Self Result: \u00a7e" + this.distance.InRange(diffSq));
            return this.distance.InRange(diffSq);

        } else if (matchTarget) {
            if (skillMetadata.getEntityTargets() != null) {
                for (AbstractEntity target : skillMetadata.getEntityTargets()) {
                    if (target == null) { continue; }

                    double diffSq = (float)origin.distanceSquared(target.getLocation());
                    if (this.distance.InRange(diffSq)) { return true; }
                }
            }

            if (skillMetadata.getLocationTargets() != null) {

                for (AbstractLocation target : skillMetadata.getLocationTargets()) {
                    if (target == null) { continue; }

                    double diffSq = (float)origin.distanceSquared(target);
                    if (this.distance.InRange(diffSq)) { return true; }
                }
            }

            //DO//OotilityCeption.Log("\u00a7aDO \u00a77Failed DO: \u00a7cno target matched");
            return false;
        }

        //DO//OotilityCeption.Log("\u00a7aDO \u00a77Failed DO: \u00a7cNo matches");
        return false;
    }
}
