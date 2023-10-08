package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import org.jetbrains.annotations.NotNull;

public class CRP_PlayerStorage extends CRP_EditionStorage {
    /**
     * @return The generic instance of this CRP
     */
    @NotNull public static CRP_PlayerStorage getInstance() { return instance; }
    @NotNull static final CRP_PlayerStorage instance = new CRP_PlayerStorage();

    @Override public int getInventorySize(@NotNull GOOPCTemplate template) { return 54; }
}
