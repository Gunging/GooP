package gunging.ootilities.gunging_ootilities_plugin.containers.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCPersonal;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Personal;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import net.Indyuce.mmoitems.inventory.EquippedItem;
import net.Indyuce.mmoitems.inventory.InventoryResolver;
import net.Indyuce.mmoitems.inventory.InventorySupplier;
import net.Indyuce.mmoitems.inventory.InventoryWatcher;
import net.Indyuce.mmoitems.inventory.ItemUpdate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Lets items stored in the equipment slots of Personal Containers count as equipped for
 * MMOItems: their stats, abilities and set bonuses apply as if worn (accessory bags, relic slots).
 * <p>
 * MMOItems 6.10 replaced the old {@code PlayerInventory#getInventory(Player)} list with
 * per-player {@link InventoryWatcher}s that report what changed. MMOItems asks every watcher to
 * re-scan through {@link InventoryWatcher#watchAll} whenever the player's inventory is resolved,
 * which GooP triggers with {@link GooPMMOItems#UpdatePlayerEquipment} after a container edit.
 */
public class ContainerToMIInventory implements InventorySupplier {

    @Override
    public InventoryWatcher supply(InventoryResolver resolver) { return new ContainerWatcher(resolver.getPlayerData().getPlayer()); }

    /**
     * The MMOItems slot container items equip into. OTHER accepts every item type, matching
     * GooP 1.x where any MMOItem in an equipment slot applied its stats.
     */
    @NotNull static EquipmentSlot containerSlot() { return EquipmentSlot.OTHER; }

    /** Tracks, for one player, what is equipped from each container slot */
    static class ContainerWatcher extends InventoryWatcher {

        @NotNull final Player player;

        /** Equipped item per container slot, keyed by container name and slot index */
        @NotNull final Map<String, EquippedItem> equipped = new HashMap<>();

        ContainerWatcher(@NotNull Player player) { this.player = player; }

        @Override
        public void watchAll(Consumer<ItemUpdate> callback) {
            Set<String> seenSlots = new HashSet<>();

            for (GOOPCPersonal container : GCL_Personal.getLoaded()) {
                if (container.getTemplate().getEquipmentSlots().size() <= 0) { continue; }

                // Make sure this player's contents are loaded
                container.registerInventoryFor(player.getUniqueId());

                Set<String> duplicateIDs = new HashSet<>();
                String name = container.getTemplate().getInternalName();
                int containerId = name.hashCode();

                for (Map.Entry<Integer, ItemStack> slot : container.indexedItemsForEquipment(player).entrySet()) {
                    ItemStack item = slot.getValue();

                    // Duplicate MMOItems are ignored when the template forbids them
                    if (!OotilityCeption.IsAirNullAllowed(item) && !container.getTemplate().isAllowDuplicateEquipment()) {
                        String id = GooPMMOItems.GetMMOItemID(item, null);
                        if (id != null && !duplicateIDs.add(id)) { item = null; }
                    }

                    String key = name + "#" + slot.getKey();
                    seenSlots.add(key);
                    callIfNotNull(update(key, item, slot.getKey(), containerId), callback);
                }
            }

            // Slots that disappeared (container unloaded, slot no longer equipment) unequip
            for (String key : new HashSet<>(equipped.keySet())) {
                if (seenSlots.contains(key)) { continue; }
                callIfNotNull(update(key, null, 0, 0), callback);
            }
        }

        /** @return The change at this slot, or null when nothing changed */
        @Nullable ItemUpdate update(@NotNull String key, @Nullable ItemStack item, int slotIndex, int containerId) {
            EquippedItem previous = equipped.get(key);
            ItemUpdate update = checkForUpdate(item, previous, containerSlot(), slotIndex, containerId);
            if (update == null) { return null; }

            if (update.getNew() == null) { equipped.remove(key); } else { equipped.put(key, update.getNew()); }
            return Objects.equals(update.getOld(), update.getNew()) ? null : update;
        }
    }
}
