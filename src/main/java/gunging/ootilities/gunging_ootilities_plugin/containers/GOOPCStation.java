package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerOpeningReason;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * A wrapper to manage the loading and unloading of station containers.
 */
public class GOOPCStation extends GOOPCDeployed {

    /**
     * A wrapper to manage the loading and unloading of Personal-Type containers.
     *
     * @param template         Template followed by this container
     */
    public GOOPCStation(@NotNull GOOPCTemplate template) { super(template); }

    //region Observed Container
    /**
     * @return The inventories currently loaded, per owner.
     */
    @NotNull public HashMap<Long, ContainerInventory> getOpenedInstances() { return openedInstances; }
    @NotNull final HashMap<Long, ContainerInventory> openedInstances = new HashMap<>();

    /**
     * @return Who has opened the container of whom.
     */
    public @NotNull HashMap<UUID, Long> getPlayerLookingAt() { return playerLookingAt; }
    @NotNull final HashMap<UUID, Long> playerLookingAt = new HashMap<>();
    /**
     * @param player Player who might have one of these Personal Containers open
     *
     * @return The UUID of the owner of the personal container this player is looking at.
     */
    @Nullable Long getLookingAt(@Nullable UUID player) { if (player == null) { return null; } return playerLookingAt.get(player); }
    /**
     * @param player The player who has the inventory open (not necessarily their own personal container)
     *
     * @param id The owner of the inventory being opened (not necessarily the player opening it)
     */
    public void setLookingAt(@NotNull UUID player, long id) { playerLookingAt.put(player, id); }
    /**
     * @param player Player who might be looking at any Personal Container of this template
     *
     * @return If the player is indeed looking at a personal container of this template.
     */
    @Override public boolean isLooking(@Nullable UUID player) { if (player == null) { return false; } return playerLookingAt.containsKey(player); }

    @Override public @NotNull HashMap<Long, ContainerInventory> getOpenedByID() { return getOpenedInstances(); }
    @Override @Nullable public ContainerInventory getOpenedInstance(@Nullable Long oid) { return getOpenedInstances().get(oid); }
    @Override @Nullable public ContainerInventory getObservedBy(@Nullable UUID player) { return getOpenedInstance(getLookingAt(player)); }
    @Override @Nullable public Player getPrincipalObserver(@Nullable Long id) {
        ContainerInventory opened = getOpenedInstance(id);
        if (opened == null) { return null; }

        // Yeah
        return opened.getPrincipalObserver();
    }
    //endregion

    //region Inspector
    @Override public void updateSeen(long id, @NotNull UUID user, @NotNull String message) { }
    @Override public @NotNull ArrayList<String> getSerializableSeens(@Nullable Long id) { return new ArrayList<>(); }
    @Override public @NotNull HashMap<UUID, NameVariable> getSeens(@Nullable Long id) { return new HashMap<>(); }
    //endregion

    //region Unloading
    /**
     * Unloads this inventory if there are no viewers of it
     *
     * @param opened Inventory to unload.
     */
    public void shouldUnloadOpened(@NotNull ContainerInventory opened, long arrayPos) {

        // Unload if it has no viewers
        if (opened.getObservers().size() == 0) { unloadOpened(opened, arrayPos); }
    }
    /**
     * Unloads this inventory.
     *
     * @param opened Inventory to unload.
     */
    public void unloadOpened(@NotNull ContainerInventory opened, long arrayPos) {

        // Does it have any viewers?
        if (opened.getObservers().size() > 0) { closeInventory(opened, true); return; }

        // Unload that one belonging to this UUID
        openedInstances.remove(arrayPos);

        // Drop those items
        dropAllItems(opened);
    }

    /**
     * Drop all the storage items from this opened inventory to the ground.
     * <br><br>
     * <b>This does not save no changes in any file</b>.<br>
     * This does not proc inventory update.
     *
     * @param opened The inventory
     */
    public static void dropAllItems(@NotNull ContainerInventory opened) {

        // Drop items to the principal player
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack toDrop : opened.getLayerStorage().values()) { if (OotilityCeption.IsAirNullAllowed(toDrop)) { continue; } items.add(toDrop); }

        // Drop them
        for (ItemStack drop : opened.getPrincipalObserver().getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
            opened.getPrincipalObserver().getWorld().dropItem(opened.getPrincipalObserver().getLocation(), drop); }

        // Clear inventory
        opened.getLayerStorage().clear();
    }

    /**
     * Closes the inventories of everyone who have this Personal Container open.
     */
    public void closeAllInventories() {

        // Go through all those opened
        for (ContainerInventory opened : openedInstances.values()) {

            // Inventory
            closeInventory(opened, true);
        }
    }
    /**
     * Closes the inventories of everyone who have this Personal Container Inventory open.
     *
     * @param opened Personal Container Inventory that must be closed completely.
     *
     * @param forceCloseAll If all players are to be force-closed (as oppose of someone closing by themselves).
     */
    public void closeInventory(@NotNull ContainerInventory opened, boolean forceCloseAll) {
        Long arrayPos = getLookingAt(opened.getPrincipalObserver().getUniqueId());

        // Close for everyone
        if (forceCloseAll) {

            /*
             * Will go through every observer and close their inventory.
             */
            for (int i = 0; i < opened.getObservers().size(); i++) {

                // Who about to get their inventory closed
                Player viewer = opened.getObservers().get(i);

                /*
                 * Since this method is called to unregister containers
                 * from the OnInventoryClose event, we must check this
                 * to make sure no infinite loops happen.
                 *
                 * #1 The event removes the player closing the container
                 *    from playerLookingAt before running this method.
                 *
                 * #2 This method removes the player closing the container
                 *    from playerLookingAt before running closeInventory()
                 *
                 * #3 The event has no business running this method if the
                 *    player has already been unregistered.
                 */
                if (arrayPos != null) {

                    // Remove reference from PersonalContainer
                    playerLookingAt.remove(viewer.getUniqueId());

                    // Remove reference from PersonalContainerInventory
                    opened.removeObserver(viewer, false);

                    // Close for that player
                    viewer.closeInventory();

                    // Unregister if it makes sense
                    shouldUnloadOpened(opened, arrayPos);

                    // Repeat Index
                    i--;
                }
            }
        }

        /*
        // Evaluate if closing next tick
        (new BukkitRunnable() {
            public void run() {

                // If it has no viewers. Delete
                if (containerInventory.getViewers().size() < 1) {

                    // Get target
                    UUID target = null;

                    // Identify its position in the arrays
                    for (UUID seer : openedInstances.keySet()) {
                        HashMap<UUID, Inventory> seedInfo = openedInstances.get(seer);

                        // If not closed already
                        if (seedInfo != null) {

                            // Find the inventory
                            for (Inventory seed : seedInfo.values()) {

                                if (containerInventory.equals(seed)) {

                                    // Get source
                                    target = seer;
                                    break;
                                }
                            }
                        }
                    }

                    // Found the correct?
                    if (target != null) {

                        // Destroy it
                        openedInstances.put(target, null);
                    }
                }
            }

        }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
        */
    }
    //endregion

    //region Usage
    long nextOpeningID = 0;

    @Override
    public void openForPlayer(@NotNull Player opener, @NotNull ContainerOpeningReason reason) {

        // Open generic
        openForPlayer(opener, -1, reason);
    }

    @Override public void openForPlayer(@NotNull Player opener, long id, @NotNull ContainerOpeningReason reason) { openForPlayer(opener, opener, id, reason); }

    /**
     * @param opener Player who will open the inventory itself
     *
     * @param owner Player who will parse placeholders of the inventory
     *
     * @param id Yeah sure, like you know this number x)
     *
     *           Supposing there was a currently-opened station of this ID, it will open exactly it.
     *
     * @param reason Reason the container is being opened.
     */
    public void openForPlayer(@NotNull Player opener, @NotNull Player owner, long id, @NotNull ContainerOpeningReason reason) {

        /*
         * Station containers have no true ID, as they are quite volatile.
         *  // Personal containers have OID, linked to owner UUID
         *  // Physical containers have LID, linked to world location
         *
         * As such, if the ID matches nothing (or more often, is negative to just
         * make a new container and open it for the player), the ID is just rerolled
         * and a new container is opened.
         */
        if (id >= 0) {

            // Make sure they don't got nothing open
            GOOPCManager.closeAllContainersFor(opener);

            // Any existing inventory for this owner?
            ContainerInventory opened = getOpenedInstance(id);
            if (opened != null) {

                // Add observer
                opened.addObserver(opener);

                // Open inventory
                opener.openInventory(opened.getInventory());

                // Register
                setLookingAt(opener.getUniqueId(), id);

                // Yes
                return;
            }
        }

        // The ID did not exist, create a new one
        id = nextOpeningID;

        // Create with the opener as principal but correct owner ID
        ContainerInventory opened = new ContainerInventory(getTemplate(), GOOPCManager.getReasonProcessOf(reason), owner);
        opened.removeObserver(owner, false);
        opened.addObserver(opener);

        // Register it being opened
        openedInstances.put(id, opened);

        // Update inventory
        opened.updateInventory();

        // Open inventory
        opener.openInventory(opened.getInventory());

        // Register
        setLookingAt(opener.getUniqueId(), id);

        // Next ID, please
        nextOpeningID++;
    }

    @Override
    public void closeForPlayer(@NotNull Player opener) {

        // Unregistered from Looking At
        Long openedID = getLookingAt(opener.getUniqueId());
        if (openedID == null) { return; }
        getPlayerLookingAt().remove(opener.getUniqueId());

        // Unregister from Observed Container
        ContainerInventory opened = getOpenedInstance(openedID);
        if (opened == null) { return; }

        // Remove with demotion
        opened.removeObserver(opener, true);

        // Unregister opened
        shouldUnloadOpened(opened, openedID);
    }


    /**
     * @param temporal Target opened container.
     * @param slot Slot where at
     *
     * @return The item actually displayed to the user.
     */
    @Contract("null, _ -> null;_ , null -> null")
    @Nullable public ItemStack getObservedItem(@Nullable Long temporal, @Nullable Integer slot) {
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Find the Personal Container Inventory (if existing)
        ContainerInventory opened = getOpenedInstance(temporal);
        if (opened == null) { return null; }

        // Well, get the edited layer there
        return opened.getItem(slot);
    }
    /**
     * @param observer Player who may be observing this kind of container
     *
     * @param slot Slot target
     *
     * @return The item being observed by this player
     */
    @Contract("null, _ -> null;_ , null -> null")
    @Override @Nullable public ItemStack getObservedItem(@Nullable UUID observer, @Nullable Integer slot) {

        // Whatever item is being shown to the player
        return getObservedItem(getLookingAt(observer), slot);
    }
    //endregion

    //region MythicLibStation
    /**
     * @return The associated Vanilla Inventory Mapping, only used
     *         with MythicLib installed.
     */
    @Nullable public Object getCustomVanillaMapping() { return customVanillaMapping; }
    @Nullable Object customVanillaMapping;
    /**
     * @param map MUST be of type io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping
     */
    public void setCustomVanillaMapping(@Nullable Object map) { customVanillaMapping = map; }
    //endregion
}
