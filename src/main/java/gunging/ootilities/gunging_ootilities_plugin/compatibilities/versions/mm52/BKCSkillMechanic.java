package gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.mm52;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;

/**
 * Lack of backwards compatibility constructor for MM 5.2.X makes
 * this necessary for easier building of MM 5.1.X GooP Versions.
 */
public abstract class BKCSkillMechanic extends SkillMechanic {

    public BKCSkillMechanic(CustomMechanic exec, String line, MythicLineConfig mlc) {

        // Choose the correct MythicMobs version

        /*NEWEN*/super(exec.getManager(), exec.getFile(), line, mlc);
        //OLDEN//super(exec.getManager(), line, mlc);
        GooPMythicMobs.newenOlden = true;
    }
}
