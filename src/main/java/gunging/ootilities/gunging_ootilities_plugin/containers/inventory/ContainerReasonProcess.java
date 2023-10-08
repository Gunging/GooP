package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Depending on the reason why a container was created,
 * some things may work differently. This is managed
 * by these process directives.
 */
public interface ContainerReasonProcess {

    /**
     * @return If PAPI placeholders in item lore will be parsed.
     */
    boolean isLoreParser();

    /**
     * @param template Template being used to generate a Container Inventory
     *
     * @return The size of the inventory that will be generated
     */
    int getInventorySize(@NotNull GOOPCTemplate template);

    /**
     * @param template Template to be used
     * @param instanceID ID Of Physical Instance (if PHYSICAL) or OID (if PERSONAL)
     *
     * @return If PAPI placeholders in item lore will be parsed.
     */
    @NotNull String getTitle(@NotNull GOOPCTemplate template, @Nullable String instanceID);

    /**
     * @param template Template to produce defaults from
     *
     * @return The layer of default items to be used.
     */
    @NotNull HashMap<Integer, ItemStack> getDefaultLayer(@NotNull GOOPCTemplate template);
}
