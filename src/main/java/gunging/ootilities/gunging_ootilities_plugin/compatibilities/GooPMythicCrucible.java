package gunging.ootilities.gunging_ootilities_plugin.compatibilities;


import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCStation;
import gunging.ootilities.gunging_ootilities_plugin.containers.compatibilities.ContainerTemplateMappingMythic;
import io.lumine.mythiccrucible.items.recipes.crafting.recipes.vmp.VanillaInventoryMapping;
import io.lumine.mythiccrucible.items.recipes.crafting.uimanager.ProvidedUIFilter;
import org.jetbrains.annotations.NotNull;

public class GooPMythicCrucible {
    public static boolean CompatibilityCheck() {ProvidedUIFilter poof = new ProvidedUIFilter(null, ""); return true; }

    public static boolean a(@NotNull GOOPCStation station) {

        // Already has a mapping? must be moving the result slot
        ContainerTemplateMappingMythic currentMapping = (ContainerTemplateMappingMythic) station.getCustomVanillaMapping();

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
                    ContainerTemplateMappingMythic mapping = new ContainerTemplateMappingMythic(station.getTemplate());
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
        if (!(station.getCustomVanillaMapping() instanceof ContainerTemplateMappingMythic)) { return; }
        ContainerTemplateMappingMythic mapping = (ContainerTemplateMappingMythic) station.getCustomVanillaMapping();
        mapping.discontinue();
        station.setCustomVanillaMapping(null);
    }
}
