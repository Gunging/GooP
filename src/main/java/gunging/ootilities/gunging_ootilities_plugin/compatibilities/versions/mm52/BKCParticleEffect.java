package gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.mm52;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.mechanics.ParticleEffect;

public class BKCParticleEffect extends ParticleEffect {

    public BKCParticleEffect(CustomMechanic exec, String line, MythicLineConfig mlc) {

        // Choose the correct MythicMobs version

        /*NEWEN*/super(exec.getManager(), exec.getFile(), line, mlc);
        //OLDEN//super(exec.getManager(), line, mlc);
        GooPMythicMobs.newenOlden = true;
    }
}
