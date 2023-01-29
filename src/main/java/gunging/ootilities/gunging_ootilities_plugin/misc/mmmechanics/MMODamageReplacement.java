package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.mm52.BKCDamageMechanic;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;

public class MMODamageReplacement extends BKCDamageMechanic {

    public MMODamageReplacement(CustomMechanic manager, String line, MythicLineConfig config) {
        super(manager, line, config);

        String typesString = config.getString(new String[]{"type", "t"}, null);
        if (typesString == null || MythicBukkit.isVolatile()) { this.element = typesString; }
    }
}
