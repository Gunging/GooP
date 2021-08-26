package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOCore;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import org.bukkit.entity.Player;

public class GooPMMOLib {

    // For the dual-cummulativity of some stats
    public static Double CDoubleStat(Player target, String arg) {

        // A value to return
        Double result = null;

        // MMOCore takes precedence (since it totals everything)
        if (Gunging_Ootilities_Plugin.foundMMOCore) {

            // Value from MMOCore
            result = GooPMMOCore.CummulativeDoubleStat(target, arg);

            // If MMOCore is not present, will use only MMOItems
        } else if (Gunging_Ootilities_Plugin.foundMMOItems) {

            // Value from MMOItems Alone
            result = GooPMMOItems.CummaltiveEquipmentDoubleStatValue(target, arg);
        } else {

            // Nothing
            result = 0.0;
        }

        // Final
        return result;
    }

}
