package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The very chad /goop containers config commands slots
 */
public class CRP_EditionCommands implements ContainerReasonProcess {

    /**
     * @return The generic instance of this CRP
     */
    @NotNull
    public static CRP_EditionCommands getInstance() { return instance; }
    @NotNull static final CRP_EditionCommands instance = new CRP_EditionCommands();

    @Override public boolean isLoreParser() { return false; }

    @Override public int getInventorySize(@NotNull GOOPCTemplate template) { return template.getTotalSlotCount(); }

    @Override
    public @NotNull String getTitle(@NotNull GOOPCTemplate template, @Nullable String instanceID) {
        return OotilityCeption.ParseColour(template.getEncodedTitle(instanceID, template.getContainerType())) + GOOPCManager.MODE_EDIT_COMMAND; }

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

            // Set it to the default value
            ret.put(slot.getSlotNumber(), slot.getContentNonNull());
        }

        // That's the default inventory
        return ret;
    }
}
