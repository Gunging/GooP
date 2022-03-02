package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCStation;
import gunging.ootilities.gunging_ootilities_plugin.containers.compatibilities.ContainerTemplateMapping;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GooPMMOLib {

    public static boolean CompatibilityCheck() {
        ProvidedUIFilter poof = new ProvidedUIFilter(null, "", "");

        return true;
    }

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

    public static boolean a(@NotNull GOOPCStation station) {

        // Already has a mapping? must be moving the result slot
        ContainerTemplateMapping currentMapping = (ContainerTemplateMapping) station.getCustomVanillaMapping();

        if (station.getTemplate().getResultSlot() != null) {
            if (currentMapping != null) {

                try {

                    // Refresh it
                    currentMapping.refreshTemplateVars();

                    return true;

                } catch (IllegalArgumentException ignored) {

                    // CANCEL
                    currentMapping.discontinue();
                }

            // Yeah
            } else {

                try {
                    ContainerTemplateMapping mapping = new ContainerTemplateMapping(station.getTemplate());
                    VanillaInventoryMapping.registerCustomMapping(mapping);
                    station.setCustomVanillaMapping(mapping);

                    return true;

                } catch (IllegalArgumentException ignored) { }
            }
        }

        if (currentMapping != null) { currentMapping.discontinue(); }

        station.setCustomVanillaMapping(null);
        return false;
    }

    public static void b(@NotNull GOOPCStation station) {
        if (!(station.getCustomVanillaMapping() instanceof ContainerTemplateMapping)) { return; }
        ContainerTemplateMapping mapping = (ContainerTemplateMapping) station.getCustomVanillaMapping();
        mapping.discontinue();
        station.setCustomVanillaMapping(null);
    }
}
