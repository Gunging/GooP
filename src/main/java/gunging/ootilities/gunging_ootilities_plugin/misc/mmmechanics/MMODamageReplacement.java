package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.mechanics.DamageMechanic;

public class MMODamageReplacement extends DamageMechanic {

    public MMODamageReplacement(SkillExecutor manager, String line, MythicLineConfig config) {
        super(manager, line, config);

        String typesString = config.getString(new String[]{"type", "t"}, null);
        if (typesString == null || MythicBukkit.isVolatile()) { this.element = typesString; }
    }
}
