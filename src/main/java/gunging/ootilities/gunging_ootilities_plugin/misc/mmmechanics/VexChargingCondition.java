package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.Vex;

public class VexChargingCondition extends CustomMMCondition implements IEntityCondition {

    boolean expected;

    public VexChargingCondition(MythicLineConfig mlc) {
        super(mlc);
        expected = mlc.getBoolean(new String[]{"charging", "c"}, true);
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {

        // Invalid target
        if (!(abstractEntity.getBukkitEntity() instanceof Vex)) { return false; }

        // Cast
        Vex vex = (Vex) abstractEntity.getBukkitEntity();

        // Return if the value matches
        return vex.isCharging() == expected;
    }
}
