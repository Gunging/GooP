package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

public class GooPMinionCondition extends CustomMMCondition implements IEntityComparisonCondition, IEntityCondition {

    boolean self;

    public GooPMinionCondition(MythicLineConfig mlc) {
        super(mlc);

        self = mlc.getBoolean(new String[]{"onlyMine", "om", "self", "s"}, false);
    }

    @Override
    public boolean check(AbstractEntity caster, AbstractEntity target) {
        //MM//OotilityCeption.Log("\u00a76AC\u00a78 IEntityComparisonCondition Parsed Act -\u00a73 " + ACTION);

        // Both must exist
        if (target == null) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c No entity");
            return neg(false); }

        // Get UUID
        SummonerClassMinion scm = SummonerClassUtils.GetMinion(target.getUniqueId());

        //MM//OotilityCeption.Log("\u00a76AC\u00a77 Checking:\u00a7a " + target.getName());

        // Is it a minion?
        if (scm == null) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c Not minion");
            return neg(false); }

        // Alr, require self?
        if (self && caster != null) {

            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Maybe Passed: \u00a7e" + (scm.getOwner().getUniqueId().equals(caster.getUniqueId())));

            // Owner UUID matches? Then yes
            return neg(scm.getOwner().getUniqueId().equals(caster.getUniqueId()));
        }

        //MM//OotilityCeption.Log("\u00a76AC\u00a77 Passed:\u00a7a Player valid \u00a78(\u00a7d" + (caster != null) + "\u00a78)");
        return neg(scm.isEnabled() && scm.isMinionValid());
        
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        //MM//OotilityCeption.Log("\u00a76AC\u00a78 IEntityCondition Parsed Act -\u00a73 " + ACTION);

        // Both must exist
        if (abstractEntity == null) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c No entity");
            return neg(false); }

        // Get UUID
        SummonerClassMinion scm = SummonerClassUtils.GetMinion(abstractEntity.getUniqueId());

        //MM//OotilityCeption.Log("\u00a76AC\u00a77 Checking:\u00a7a " + abstractEntity.getName());

        // Is it a minion?
        if (scm == null) {
            
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c Not minion");
            return neg(false); }

        //MM//OotilityCeption.Log("\u00a76AC\u00a77 Passed:\u00a7a Is minion");
        return neg(true);
    }
}
