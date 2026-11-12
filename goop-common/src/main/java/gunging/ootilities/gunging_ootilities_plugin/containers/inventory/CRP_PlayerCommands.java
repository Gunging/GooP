package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GCT_PlayerTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * The very chad /goop containers config commands slots; but when used with a Player Container Template
 */
public class CRP_PlayerCommands extends CRP_EditionCommands {

    /**
     * @return The generic instance of this CRP
     */
    @NotNull public static CRP_PlayerCommands getInstance() { return instance; }
    @NotNull static final CRP_PlayerCommands instance = new CRP_PlayerCommands();

    @Override public int getInventorySize(@NotNull GOOPCTemplate template) { return 54; }

    @Override public boolean isLoreParser() { return false; }

    @Override
    public @NotNull String getTitle(@NotNull GOOPCTemplate template, @Nullable String instanceID) {
        return OotilityCeption.ParseColour(template.getEncodedTitle(instanceID, template.getContainerType())) + GOOPCManager.MODE_EDIT_COMMAND; }

    @Override
    public @NotNull HashMap<Integer, ItemStack> getDefaultLayer(@NotNull GOOPCTemplate template) {

        // The inventory to return.
        HashMap<Integer, ItemStack> ret = GOOPCPlayer.clonePlayerEditionContents();
        //CRP//OotilityCeption.Log("\u00a78CRP \u00a7bPC\u00a77 Base edition contents\u00a73 x" + ret.size());

        /*
         * Include the default items already present.
         */
        HashMap<Integer, GOOPCSlot> slotsContent = template.getSlotsContent();

        // Map values
        assimilate(ret, slotsContent.get(103));
        assimilate(ret, slotsContent.get(102));
        assimilate(ret, slotsContent.get(101));
        assimilate(ret, slotsContent.get(100));
        assimilate(ret, slotsContent.get(-106));
        assimilate(ret, slotsContent.get(80));
        assimilate(ret, slotsContent.get(81));
        assimilate(ret, slotsContent.get(82));
        assimilate(ret, slotsContent.get(83));
        assimilate(ret, slotsContent.get(84));

        // That's the default inventory
        return ret;
    }

    static void assimilate(@NotNull HashMap<Integer, ItemStack> map, @Nullable GOOPCSlot slot_head) {
        if (slot_head == null) { return; }

        // Put in the appropriate slot
        map.put(GOOPCPlayer.continuousToEdition(slot_head.getSlotNumber()), slot_head.getContentNonNull());
    }
}
