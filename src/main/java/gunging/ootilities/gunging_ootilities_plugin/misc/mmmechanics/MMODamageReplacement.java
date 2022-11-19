package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.mechanics.DamageMechanic;

import java.io.File;

public class MMODamageReplacement extends DamageMechanic {

    //NEWEN//public MMODamageReplacement(SkillExecutor manager, File file, String line, MythicLineConfig config) {
        //NEWEN//super(manager, file, line, config);
        /*OLDEN*/public MMODamageReplacement(SkillExecutor manager, String line, MythicLineConfig config) {
            /*OLDEN*/super(manager, line, config);
        GooPMythicMobs.newenOlden = true;

        String typesString = config.getString(new String[]{"type", "t"}, null);
        if (typesString == null || MythicBukkit.isVolatile()) { this.element = typesString; }
    }
}
