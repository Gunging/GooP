package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * When the container's storage is being edited.
 */
public class CRP_EditionStorage implements ContainerReasonProcess {

    /**
     * @return The generic instance of this CRP
     */
    @NotNull public static CRP_EditionStorage getInstance() { return instance; }
    @NotNull static final CRP_EditionStorage instance = new CRP_EditionStorage();

    @Override public boolean isLoreParser() { return false; }

    @Override public int getInventorySize(@NotNull GOOPCTemplate template) { return template.getTotalSlotCount(); }

    @Override
    public @NotNull String getTitle(@NotNull GOOPCTemplate template, @Nullable String instanceID) {
        return OotilityCeption.ParseColour(template.getEncodedTitle(instanceID, template.getContainerType())) + "\u00a79\u00a7l Editing STORAGE" + GOOPCManager.MODE_EDIT_STORAGE; }

    @Override
    public @NotNull HashMap<Integer, ItemStack> getDefaultLayer(@NotNull GOOPCTemplate template) {

        /*
         * It seems that, when it comes to editing storage, the items from
         * the freshly-closed Editing Display inventory are just copied over,
         * except if they are container edge materials, in which case they are
         * transformed into actual container edges. Therefore, this really just
         * gets overwritten in that step, and we only need an inventory view with
         * the appropriate size and title.
         *
         */

        return new HashMap<>();
    }
}
