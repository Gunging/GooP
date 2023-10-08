package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * When the container's storage is being edited.
 */
public class CRP_EditionDisplay  implements ContainerReasonProcess {

    /**
     * @return The generic instance of this CRP
     */
    @NotNull
    public static CRP_EditionDisplay getInstance() { return instance; }
    @NotNull static final CRP_EditionDisplay instance = new CRP_EditionDisplay();
    @Override public int getInventorySize(@NotNull GOOPCTemplate template) { return template.getTotalSlotCount(); }

    @Override public boolean isLoreParser() { return false; }

    @Override
    public @NotNull String getTitle(@NotNull GOOPCTemplate template, @Nullable String instanceID) {
        return OotilityCeption.ParseColour(template.getEncodedTitle(instanceID, template.getContainerType())) + "\u00a75\u00a7l Editing DISPLAY" + GOOPCManager.MODE_EDIT_DISPLAY; }

    @Override
    public @NotNull HashMap<Integer, ItemStack> getDefaultLayer(@NotNull GOOPCTemplate template) {

        // The inventory to return.
        HashMap<Integer, ItemStack> ret = new HashMap<>();

        /*
         * Just return the slots content yeah
         */
        HashMap<Integer, GOOPCSlot> slotsContent = template.getSlotsContent();
        for (Map.Entry<Integer, GOOPCSlot> entry : slotsContent.entrySet()) {
            GOOPCSlot slot = entry.getValue();
            if (slot == null) { continue; }

            // Aye
            int displayingSlot = slot.getSlotNumber();
            if (template.isPlayer()) {

                // Funny mix-up
                if (displayingSlot >= 9) {

                    // Move up by one row
                    displayingSlot -= 9;
                } else {

                    // Move down by 3 rows
                    displayingSlot += 27;
                }
            }

            // Set to edge material
            if (slot.isForEdge()) { ret.put(displayingSlot, GOOPCTemplate.CONTAINER_EDGE_MATERIAL.clone());

            // Set it to the default value
            } else { ret.put(displayingSlot, slot.getContentNonNull()); }
        }

        // That's the default inventory
        return ret;
    }
}
