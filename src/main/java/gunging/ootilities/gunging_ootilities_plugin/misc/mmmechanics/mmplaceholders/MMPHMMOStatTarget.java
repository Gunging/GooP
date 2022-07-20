package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;

/**
 * Whose MMOStats are we reading
 */
public enum MMPHMMOStatTarget {

    /**
     * The MMOStats of interest belong to the caster of the skill
     */
    CASTER,

    /**
     * The MMOStats of interest belong to the trigger of the skill
     */
    TRIGGER,

    /**
     * The MMOStats of interest belong to the {@link SummonerClassUtils}
     * owner of the caster of the skill
     */
    OWNER
}
