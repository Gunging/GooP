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

public class CRP_PlayerDisplay implements ContainerReasonProcess {

    /**
     * @return The generic instance of this CRP
     */
    @NotNull
    public static CRP_PlayerDisplay getInstance() { return instance; }
    @NotNull static final CRP_PlayerDisplay instance = new CRP_PlayerDisplay();

    @Override public boolean isLoreParser() { return false; }

    @Override public int getInventorySize(@NotNull GOOPCTemplate template) { return 54; }

    @Override
    public @NotNull String getTitle(@NotNull GOOPCTemplate template, @Nullable String instanceID) {
        return OotilityCeption.ParseColour(template.getEncodedTitle(instanceID, template.getContainerType())) + "\u00a75\u00a7l Editing DISPLAY" + GOOPCManager.MODE_EDIT_DISPLAY; }

    @Override
    public @NotNull HashMap<Integer, ItemStack> getDefaultLayer(@NotNull GOOPCTemplate template) {

        // The inventory to return.
        HashMap<Integer, ItemStack> ret = GOOPCPlayer.clonePlayerEditionContents();

        /*
         * Include the default items already present.
         */
        HashMap<Integer, GOOPCSlot> slotsContent = template.getSlotsContent();

        // Map values
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(103));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(102));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(101));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(100));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(-106));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(80));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(81));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(82));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(83));
        CRP_PlayerCommands.assimilate(ret, slotsContent.get(84));

        // That's the default inventory
        return ret;
    }
}