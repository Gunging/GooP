package gunging.ootilities.gunging_ootilities_plugin.containers.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCPersonal;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Personal;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Equips the items stored in storage slots to the player.
 */
public class ContainerToMIInventory implements PlayerInventory {

    @Override
    public List<EquippedItem> getInventory(@NotNull Player player) {
        //EQ//OotilityCeption.Log("Equipping player\u00a7b" + player.getName());

        // Test for unlocked
        //if (!GOOPCManager.isPremiumEnabled() && !player.isOp() && !GOOPCManager.isLiteEquipmentEnabled()) { return new ArrayList<>(); }

        ArrayList<EquippedItem> ret = new ArrayList<>();
        ArrayList<String> duplicateIDs = new ArrayList<>();

        // Get personal containers?
        for (GOOPCPersonal p : GCL_Personal.getLoaded()) {
            //EQ//OotilityCeption.Log("Container \u00a7b" + p.getTemplate().getInternalName());

            // Skip non-container
            if (p.getTemplate().getEquipmentSlots().size() <= 0) { continue; }
            
            // Load the player
            p.registerInventoryFor(player.getUniqueId());

            // Well
            for (Map.Entry<Integer, ItemStack> itm : p.indexedItemsForEquipment(player).entrySet()) {
                if (OotilityCeption.IsAirNullAllowed(itm.getValue())) { continue; }

                // It must be a MMOItem
                String id = GooPMMOItems.GetMMOItemID(itm.getValue(), null);

                // Invalid MMOItems ID? Equip this item.
                if (id == null) { ret.add(new ContainersEquippedItem(p, player, itm.getKey(), itm.getValue())); continue; }

                // If disallowed duplicates and already equipped, skip this.
                if (!p.getTemplate().isAllowDuplicateEquipment() && duplicateIDs.contains(id)) { continue; }

                // Allow MMOItem
                ret.add(new ContainersEquippedItem(p, player, itm.getKey(), itm.getValue()));

                // Consider this item equipped
                duplicateIDs.add(id);
            }
        }

        // I guess
        return ret;
    }

    @Nullable static io.lumine.mythic.lib.api.player.EquipmentSlot anyEqSlot = null;

    @NotNull static io.lumine.mythic.lib.api.player.EquipmentSlot getAnyEqSlot() {

        // Already reflected? Done
        if (anyEqSlot != null) { return anyEqSlot; }

        // ANY or OTHER
        try { anyEqSlot = io.lumine.mythic.lib.api.player.EquipmentSlot.valueOf("ANY"); }
        catch (IllegalArgumentException ignored) { anyEqSlot = io.lumine.mythic.lib.api.player.EquipmentSlot.valueOf("OTHER"); }

        // Yeah
        return anyEqSlot;
    }
}

/**
 * He made it abstract :skull: so
 */
class ContainersEquippedItem extends EquippedItem {

    @NotNull GOOPCPersonal container;
    @NotNull Player player;
    int index;

    public ContainersEquippedItem(@NotNull GOOPCPersonal container, @NotNull Player player, int index, @NotNull ItemStack itemStack) {
        /*CURRENT-MMOITEMS*/super(itemStack, ContainerToMIInventory.getAnyEqSlot());
        //YE-OLDEN-MMO//super(itemStack, net.Indyuce.mmoitems.api.Type.EquipmentSlot.ANY);
        this.container = container;
        this.player = player;
        this.index = index;
        GooPMythicMobs.newenOlden = true;
    }

    /*NEWEN*/@Override
    /*NEWEN*/public void setItem(@Nullable ItemStack itemStack) {

        // Set and save owner item (real)
    /*NEWEN*/container.setAndSaveOwnerItem(player.getUniqueId(), index, itemStack, false);
    /*NEWEN*/}
}
