package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.PhysicalContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Physical;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerOpeningReason;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.GPCProtection;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import gunging.ootilities.gunging_ootilities_plugin.misc.OptimizedTimeFormat;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.chunks.ChunkMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A wrapper to manage the loading and unloading of physical containers.
 */
public class GOOPCPhysical extends GOOPCDeployed {

    //region Constructor
    /**
     * A wrapper to manage the loading and unloading of Physical Containers
     *
     * @param template Template followed by this container
     */
    public GOOPCPhysical(@NotNull GOOPCTemplate template) { super(template); }

    /**
     * @return Each Location is given a numeric ID until next reboot, this
     *         tells you which of these temporal IDs belong to which location.
     */
    @NotNull public ChunkMap<Long> getLinkLocationToLID() { return linkLocationToLID; }
    @NotNull final ChunkMap<Long> linkLocationToLID = new ChunkMap<>();
    /**
     * @param associated Location you do have
     * @return The associated LID (if one is assigned to that location)
     */
    @Nullable public Long getLID(@Nullable Location associated) { if (associated == null) { return null; } return linkLocationToLID.get(associated); }

    /**
     * @return Each Owner is given a numeric ID until next reboot, this
     *         tells you which of these IDs belong to which owner.
     */
    @NotNull public HashMap<Long, Location> getReverseLocationToLID() { return reverseLocationToLID; }
    @NotNull final HashMap<Long, Location> reverseLocationToLID = new HashMap<>();
    /**
     * @param associated The OID you do have
     * @return The associated UUID (if one is assigned to that OID)
     */
    @Nullable public Location getFromLID(@Nullable Long associated) { if (associated == null) { return null; } return reverseLocationToLID.get(associated); }

    /**
     * @return The ID of the next location to be loaded this reboot.
     */
    public long getTemporalOIDsCount() { return temporalOIDsCount; }
    long temporalOIDsCount = 0;

    /**
     * @return The saved inventories of all loaded locations.
     */
    @NotNull public ChunkMap<GPCContent> getPerLocationInventories() { return perLocationInventories; }
    @NotNull final ChunkMap<GPCContent> perLocationInventories = new ChunkMap<>();

    /**
     * Be mindful when you call this method, if this location does not
     * exist it will be registered and initialized as empty.
     *
     * @param location The location where searching content.
     *
     * @return The protection information pertaining to this location
     *
     * @see #getLocationInventoryTry(Location)
     */
    @NotNull public GPCContent getLocationInventory(@NotNull Location location) {
        GPCContent ret = perLocationInventories.get(location);
        if (ret != null) { return ret; }
        registerInventoryFor(location);
        ret = perLocationInventories.get(location);
        if (ret != null) { return ret; }

        // This should never actually happen
        Gunging_Ootilities_Plugin.theOots.CPLog("Could not register inventory for \u00a73" + OotilityCeption.BlockLocation2String(location) + "\u00a77 in\u00a7e " + getTemplate().getInternalName());
        ret = new GPCContent(this, location, -1, GPCProtection.UNREGISTERED, null);
        return ret;
    }

    /**
     * @param location The location where searching content.
     *
     * @return The protection information pertaining to this location,
     *         if there is one registered already.
     *
     * @see #getLocationInventory(Location)
     */
    @Nullable public GPCContent getLocationInventoryTry(@Nullable Location location) { return perLocationInventories.get(location); }
    //endregion

    //region Inventory Storages
    /**
     * @return The inventories currently loaded.
     */
    @NotNull public ChunkMap<PhysicalContainerInventory> getOpenedInstances() { return openedInstances; }
    @NotNull final ChunkMap<PhysicalContainerInventory> openedInstances = new ChunkMap<>();

    /**
     * @return Who has opened a container where
     */
    public @NotNull HashMap<UUID, Location> getPlayerLookingAt() { return playerLookingAt; }
    @NotNull final HashMap<UUID, Location> playerLookingAt = new HashMap<>();
    /**
     * @param player Player who might have one of these Physical Containers open
     *
     * @return The Location of the physical container this player is looking at.
     */
    @Nullable Location getLookingAt(@Nullable UUID player) { if (player == null) { return null; } return playerLookingAt.get(player); }
    /**
     * @param player The player who has the inventory open
     *
     * @param location The location of the inventory being opened
     */
    public void setLookingAt(@NotNull UUID player, @NotNull Location location) { playerLookingAt.put(player, location); }
    /**
     * @param player Player who might be looking at any Physical Container of this template
     *
     * @return If the player is indeed looking at a personal container of this template.
     */
    @Override public boolean isLooking(@Nullable UUID player) { if (player == null) { return false; } return playerLookingAt.containsKey(player); }
    /**
     * @param location Target Physical Container
     *
     * @return If the player who the PAPI placeholders in that container will parse for
     */
    @Nullable Player getPrincipalObserver(@Nullable Location location) {
        PhysicalContainerInventory opened = getOpenedInstance(location);
        if (opened == null) { return null; }

        // Yeah
        return opened.getPrincipalObserver();
    }
    @Override public @NotNull HashMap<Long, PhysicalContainerInventory> getOpenedByID() {

        // Yeah
        HashMap<Long, PhysicalContainerInventory> ret = new HashMap<>();

        // For every entry
        for (Map.Entry<Long, Location> lid : getReverseLocationToLID().entrySet()) {

            // Find opened?
            PhysicalContainerInventory opened = getOpenedInstance(lid.getValue());

            // Skip Closed
            if (opened == null) { continue; }

            // Lookup IDs and put the opened inven
            ret.put(lid.getKey(), opened);
        }

        // Yeah
        return ret;
    }

    /**
     * @param location Location that may have an opened container
     *
     * @return The opened container inventory of that location
     */
    @Contract("null -> null")
    @Nullable PhysicalContainerInventory getOpenedInstance(@Nullable Location location) { if (location == null) { return null; } return getOpenedInstances().get(location); }
    @Override @Nullable public PhysicalContainerInventory getOpenedInstance(@Nullable Long lid) { return getOpenedInstance(getFromLID(lid)); }
    @Override @Nullable public PhysicalContainerInventory getObservedBy(@Nullable UUID player) { return getOpenedInstance(getLookingAt(player)); }
    @Override @Nullable public Player getPrincipalObserver(@Nullable Long id) { return getPrincipalObserver(getFromLID(id)); }
    //endregion

    //region Loading and Unloading
    /**
     * Registers this location in two places:
     * <br>     #1 Makes sure they have Protections Information associated to them.
     * <br>     #2 Makes sure they have a registered temporal LID.
     *
     * @param location Player to register.
     */
    public void registerInventoryFor(@NotNull Location location) {

        // If not in the Inventory Hash
        if (!perLocationInventories.contains(location)) {

            // Create HashMap, load contents from file or empty.
            GCL_Physical.load(this, location);
        }

        // If not in the ID link hash
        if (!linkLocationToLID.contains(location)) {

            // Assign a new ID
            temporalOIDsCount++;

            // Register Bidirectionally
            linkLocationToLID.put(location, temporalOIDsCount);
            reverseLocationToLID.put(temporalOIDsCount, location);
        }
    }

    /**
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param inventories The inventories stored in the files (most likely).
     */
    public void loadLocationContents(@NotNull HashMap<Location, GPCContent> inventories) {

        // For every UUID
        for (Map.Entry<Location, GPCContent> pair : inventories.entrySet()) { loadLocationContents(pair.getKey(), pair.getValue()); }
    }
    /**
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param location Owner of the inventory
     *
     * @param savedInventory Inventory contents (overwrites previous)
     */
    public void loadLocationContents(@NotNull Location location, @NotNull GPCContent savedInventory) {

        // It must be set in the arrays
        perLocationInventories.put(location, savedInventory);

        // It must be set in the opened container inventory
        PhysicalContainerInventory opened = getOpenedInstance(location);

        // If existing, reload
        if (opened != null) { opened.loadStorageContents(savedInventory.getStorageContents()); }
    }
    /**
     * Puts an item in someone's loaded storage.
     * <br><br>
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param location The player who owns this inventory
     * @param slot Slot at which this item is found.
     */
    public void loadLocationItem(@Nullable Location location, @Nullable Integer slot, @Nullable ItemStack item) {
        if (location == null) { return; }
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return; }

        // Get loaded GPC
        GPCContent contents = getLocationInventory(location);

        // Update the item in the contents
        contents.setItem(slot, item);

        // It must be set in the opened container inventory
        PhysicalContainerInventory opened = getOpenedInstance(location);

        // If existing, reload
        if (opened != null) { opened.setStorageItem(slot, item); }
    }

    /**
     * Unloads this inventory if there are no viewers of it
     *
     * @param opened Inventory to unload.
     */
    public void shouldUnloadOpened(@NotNull PhysicalContainerInventory opened) {

        // Unload if it has no viewers
        if (opened.getObservers().size() == 0) { unloadOpened(opened); }
    }
    /**
     * Unloads this inventory.
     *
     * @param opened Inventory to unload.
     */
    public void unloadOpened(@NotNull PhysicalContainerInventory opened) {

        // Does it have any viewers?
        if (opened.getObservers().size() > 0) { closeInventory(opened, true); return; }

        // Unload that one belonging to this Location
        openedInstances.remove(opened.getLocation());
    }

    /**
     * Previous seens get cleared.
     *
     * @param seensOverride The inspector history of this container.
     */
    public void loadSeens(@NotNull ChunkMap<HashMap<UUID, NameVariable>> seensOverride) {

        perLocationInventories.forEachExisting((location,protection) -> {

            HashMap<UUID, NameVariable> seens = seensOverride.get(protection.getLocation());
            if (seens == null) { seens = new HashMap<>(); }

            // Load it ig
            loadSeens(protection, seens);
        });
    }
    /**
     * Previous seens for this owner get cleared.
     *
     * @param seensOverride The inspector history for this owner for this container.
     */
    public void loadSeens(@NotNull GPCContent location, @NotNull HashMap<UUID, NameVariable> seensOverride) {

        // Delegate to the GPCContent itself
        location.loadSeens(seensOverride);
    }

    /**
     * Closes the inventories of everyone who have this Personal Container open.
     */
    public void closeAllInventories() {

        // Close all of those
        openedInstances.forEachExisting((location,opened) -> closeInventory(opened, true));
    }
    /**
     * Closes the inventories of everyone who have this Personal Container Inventory open.
     *
     * @param opened Personal Container Inventory that must be closed completely.
     *
     * @param forceCloseAll If all players are to be force-closed (as oppose of someone closing by themselves).
     */
    public void closeInventory(@Nullable PhysicalContainerInventory opened, boolean forceCloseAll) {
        if (opened == null) { return; }

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
                if (isLooking(viewer.getUniqueId())) {

                    // Remove reference from PersonalContainer
                    playerLookingAt.remove(viewer.getUniqueId());

                    // Remove reference from PersonalContainerInventory
                    opened.removeObserver(viewer, false);

                    // Close for that player
                    viewer.closeInventory();

                    // Unregister if it makes sense
                    shouldUnloadOpened(opened);

                    // Repeat Index
                    i--;
                }
            }
        }

        //todo what if forceCloseAll = false?

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

    //region Inspector
    /**
     * This method will append the timestamp of when it was called in order
     * to get an idea of when it happened, this is automatic and need not
     * be included in the message.
     *
     * @param location Which physical container
     *
     * @param user The one who has accessed this container
     *
     * @param message Message about their action accessing it.
     */
    public void updateSeen(@NotNull Location location, @NotNull UUID user, @NotNull String message) {

        // Find
        GPCContent contents = getLocationInventory(location);

        // Get the seens
        contents.putSeen(user, new NameVariable(OptimizedTimeFormat.GetCurrentTime(), message));

        // Save
        GCL_Physical.saveContents(this, contents);
    }
    /**
     * @param location Which physical container
     *
     * @return The people who have accessed this in the past,
     *         and when, ready to be writen into the files.
     */
    @NotNull public ArrayList<String> getSerializableSeens(@Nullable Location location) {
        if (location == null) { return new ArrayList<>(); }

        // Find
        GPCContent contents = getLocationInventory(location);

        // Return thay
        return contents.getSerializableSeens();
    }
    /**
     * @param location Which physical container
     *
     * @return The people who have accessed this in the past, and when.
     */
    public @NotNull HashMap<UUID, NameVariable> getSeens(@Nullable Location location) {
        if (location == null) { return new HashMap<>(); }
        // Find
        GPCContent contents = getLocationInventory(location);

        // Put
        return contents.getSeens();
    }

    @Override public @NotNull HashMap<UUID, NameVariable> getSeens(@Nullable Long id) { return getSeens(getFromLID(id)); }

    @Override public @NotNull ArrayList<String> getSerializableSeens(@Nullable Long id) { return getSerializableSeens(getFromLID(id)); }
    @Override public void updateSeen(long id, @NotNull UUID user, @NotNull String message) {
        Location location = getFromLID(id);
        if (location == null) { return; }

        // Update it
        updateSeen(location, user, message);
    }
    //endregion

    //region Actual Functionality
    /**
     * Opens the physical container at the target location for the specified player. <br><br>
     *
     * Will create a new empty one in there if it did not exist before. However,
     * if you know you are creating one right now, and may want to edit its
     * inherent structure or protect it with {@link GPCProtection#PRIVATE} on
     * the go, use the create method before calling this.
     *
     * @param opener Player who is opening the container.
     *
     * @param location Location of the target physical container.
     *
     * @param reason Reason of the opener to open the container.
     *
     * @see #createPhysicalInstanceAt(Location, UUID, RefSimulator)
     */
    public void openForPlayer(@NotNull Player opener, @NotNull Location location, @NotNull ContainerOpeningReason reason) {

        // Register the UUID ffs
        registerInventoryFor(location);

        // Make sure they don't got nothing open
        GOOPCManager.closeAllContainersFor(opener);

        // Any existing inventory for this owner?
        PhysicalContainerInventory opened = getOpenedInstance(location);
        if (opened != null) {

            // Add observer
            opened.addObserver(opener);

            // Open inventory
            opener.openInventory(opened.getInventory());

            // Register
            setLookingAt(opener.getUniqueId(), location);

            // Yes
            return;
        }

        // Acquire the protection information
        GPCContent contents = getLocationInventory(location);

        // Create with the opener as principal but correct owner ID
        opened = new PhysicalContainerInventory(getTemplate(), this, GOOPCManager.getReasonProcessOf(reason), contents, opener);

        // Load the contents
        opened.loadStorageContents(contents.getStorageContents());

        // Register it being opened
        openedInstances.put(location, opened);

        // Perform Restricted Behaviour ops if it is the owner
        if (contents.isAdmin(opener.getUniqueId())) {

            // Sanitize
            opened.sanitizeStorage(opener);

            // Evaluate every slot for Restriction
            for (int s = 0; s < getTemplate().getTotalSlotCount(); s++) {

                // Find slot
                GOOPCSlot slot = getTemplate().getSlotAt(s);

                // Skip snooze
                if (slot == null) { continue; }

                // Restrictions not met? Restrict Behaviour kicks in.
                if (!slot.matchesRestrictions(opener)) {

                    // Find the item in that slot
                    ItemStack item = getLocationItem(location, s);

                    // Skip if there is no item to begin with
                    if (OotilityCeption.IsAirNullAllowed(item)) { continue; }

                    // Switch restriction
                    switch (slot.getRestrictedBehaviour()) {
                        case DROP:

                            // Drop
                            for (ItemStack drop : opener.getInventory().addItem(item).values()) {

                                // Drop to the world
                                opener.getWorld().dropItem(opener.getLocation(), drop);
                            }

                            // Notice the lack of break; statement - DROP restriction type also saves changes


                        case DESTROY:

                            // Clear item at this slot. Update happens later
                            setAndSaveOwnerItem(location, s, null, false);
                            break;
                    }
                }
            }
        }

        // Update inventory
        opened.updateInventory();

        // Open inventory
        opener.openInventory(opened.getInventory());

        // Register
        setLookingAt(opener.getUniqueId(), location);
    }

    /**
     * Creates a new Physical Instance at given location. <br>
     * Ignored if a Physical Instance already exists in that block.
     * <br><br>
     * Will search for an unclaimed inherent structure at this location
     * using {@link GCL_Physical#getUnclaimedInherentStructure(Location)}
     *
     * @param location The location in the world it must occupy
     * @param creatorUUID The UUID of the player who will control the protection
     * @param logger Retrieves fail/success/error messages to be handled elsewhere.
     */
    public void createPhysicalInstanceAt(@NotNull Location location, @NotNull UUID creatorUUID, @Nullable RefSimulator<String> logger) {

        // Is that already a physical container?
        if (GCL_Physical.isPhysicalContainerCore(location)) {

            // Set
            OotilityCeption.Log4Success(logger, true, "\u00a7cThere already is a container in that block.");

            // Snooze
            return;
        }

        // This now exists at that location
        GPCContent content = getLocationInventory(location);

        // Set its owner
        content.editProtection(GPCProtection.PRIVATE, creatorUUID);

        // Structure
        content.loadInherentBlocks(GCL_Physical.getUnclaimedInherentStructure(location));

        // Save
        GCL_Physical.saveContents(this, content);
    }

    /**
     * @param location The player who owns this inventory
     * @param slot Slot at which this item is found.
     *
     * @return The map of items itself.
     */
    @Nullable public ItemStack getLocationItem(@Nullable Location location, @Nullable Integer slot) {
        if (location == null) { return null; }
        return getLocationInventory(location).getItem(slot);
    }
    /**
     * @param location Target opened container.
     * @param slot Slot where at
     *
     * @return The item in the edited layer in the opened container yeah.
     */
    @Nullable public ItemStack getEdited(@NotNull Location location, @Nullable Integer slot) {
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Make sure it is registered
        registerInventoryFor(location);
        //Gunging_Ootilities_Plugin.theOots.C Log("Set Item " + OotilityCeption.GetItemName(item) + "\u00a77 at slot \u00a7e" + slot + "\u00a7 in inventory of " + owner.toString());

        // Find the Personal Container Inventory (if existing)
        PhysicalContainerInventory opened = getOpenedInstance(location);
        if (opened == null) { return null; }

        // Well, get the edited layer there
        return opened.getLayerEdited().get(slot);
    }
    /**
     * @param location Target opened container.
     * @param slot Slot where at
     *
     * @return The item in the default layer in the opened container yeah.
     */
    @Nullable public ItemStack getDefault(@NotNull Location location, @Nullable Integer slot) {
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Make sure it is registered
        registerInventoryFor(location);

        // Find the Personal Container Inventory (if existing)
        PhysicalContainerInventory opened = getOpenedInstance(location);
        if (opened == null) { return null; }

        // Well, get the edited layer there
        return opened.getLayerDefault().get(slot);
    }
    /**
     * @param location Target opened container.
     * @param slot Slot where at
     *
     * @return The item actually displayed to the user.
     */
    @Contract("null, _ -> null;_ , null -> null")
    @Nullable public ItemStack getObservedItem(@Nullable Location location, @Nullable Integer slot) {
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Find the Personal Container Inventory (if existing)
        PhysicalContainerInventory opened = getOpenedInstance(location);
        if (opened == null) {

            // Not for storage no service
            if (!getTemplate().isStorageSlot(slot)) { return null; }

            // Get personal contents, naturally.
            return getLocationItem(location, slot);
        }

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

    @Override public void closeForPlayer(@NotNull Player opener) {

        // Unregistered from Looking At
        Location openedID = getLookingAt(opener.getUniqueId());
        if (openedID == null) { return; }
        getPlayerLookingAt().remove(opener.getUniqueId());

        // Unregister from Observed Container
        PhysicalContainerInventory opened = getOpenedInstance(openedID);
        if (opened == null) { return; }

        // Remove with demotion
        opened.removeObserver(opener, true);

        // Unregister opened
        shouldUnloadOpened(opened);
    }

    /**
     * Avoid using this, as the location chosen is where the player stands.
     *
     * @param opener Player who is opening the container.
     * @param reason Reason under which they are opening it.
     */
    @Override  public void openForPlayer(@NotNull Player opener, @NotNull ContainerOpeningReason reason) { openForPlayer(opener, opener.getLocation().toBlockLocation(), reason); }
    /**
     * Opens someone's personal container to some player.
     * <br><br>
     * <b>
     *     If this OID is not linked to any loaded container, this
     *     method will default to opening this player's own container,
     *     in which case the Player will be assigned an OID that is
     *     <i>not</i> necessarily the one you specified.
     * </b>
     *
     * @param opener Player who is opening the container.
     *
     * @param oid OID of the Owner of the target personal container.
     *            If this OID is not registered, the container will use the
     *            player as the owner and open their container.
     *
     * @param reason Reason of the opener to open the container.
     */
    @Override public void openForPlayer(@NotNull Player opener, long oid, @NotNull ContainerOpeningReason reason) {
        Location targetOwner = getFromLID(oid);
        if (targetOwner == null) { targetOwner = opener.getLocation().toBlockLocation(); }

        // Open
        openForPlayer(opener, targetOwner, reason);
    }

    /**
     * Drop all the storage items from this opened inventory to the ground.
     * <br><br>
     * <b>This does not save no changes in any file</b>.<br>
     * This does not proc inventory update.
     *
     * @param opened Location of the opened inventory
     */
    public void dropAllItems(@NotNull GPCContent opened) {

        // Drop items to the principal player
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack toDrop : opened.getStorageContents().values()) {
            if (OotilityCeption.IsAirNullAllowed(toDrop)) { continue; } items.add(toDrop); }

        // Drop them
        for (ItemStack drop : items) { opened.getLocation().getWorld().dropItem(opened.getLocation(), drop); }

        // Clear inventory
        opened.loadStorageContents(new HashMap<>());
    }
    //endregion

    //region Saving
    /**
     * Saves all the inventories associated to this Physical Container into the files.
     */
    public void saveLocationItems() { perLocationInventories.forEachExisting((location,protection) -> saveLocationItems(protection)); }
    /**
     * Saves all the items of this player into the files.
     *
     * @param contents The contents state (contents, seens, members...) to save
     */
    public void saveLocationItems(@NotNull GPCContent contents) {  GCL_Physical.saveContents(this, contents); }
    /**
     * Sets the item in the Location's Contents, saves it into the files, and sets
     * the item in the storage layers of PhysicalContainerInventories.
     * <br><br>
     * This <b>does not necessarily</b> proc inventory update.
     *
     * @param location Location of the Contents
     * @param slot Slot among the Contents
     * @param item Item to include in the Contents
     * @param procUpdate If the changes should be updated onto the actual observed inventory
     */
    public void setAndSaveOwnerItem(@NotNull Location location, int slot, @Nullable ItemStack item, boolean procUpdate) {
        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return; }

        // Make sure it is registered
        registerInventoryFor(location);

        //Gunging_Ootilities_Plugin.theOots.C Log("Set Item " + OotilityCeption.GetItemName(item) + "\u00a77 at slot \u00a7e" + slot + "\u00a7 in inventory of " + owner.toString());

        // Make sure it is not null
        ItemStack carbonCopy = new ItemStack(Material.AIR);
        if (item != null) { carbonCopy = new ItemStack(item); }

        // Get loaded GPC
        GPCContent contents = getLocationInventory(location);

        // Update the item in the contents
        contents.setItem(slot, item);

        // Put it into the Personal Container Inventory (if existing)
        PhysicalContainerInventory opened = getOpenedInstance(location);
        if (opened != null) { opened.setStorageItem(slot, carbonCopy); if (procUpdate) { opened.updateSlot(slot); } }

        // Save Inventory
        saveLocationItems(contents);
    }
    /**
     * Sets the item in the Owner's Observed Inventory edited layer.
     * <br><br>
     * This <b>does not necessarily</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param location Location of the Contents
     * @param slot Slot among the Contents
     * @param item Item to include in the Contents
     * @param procUpdate If the changes should be updated onto the actual observed inventory
     */
    public void setInEdited(@NotNull Location location, int slot, @Nullable ItemStack item, boolean procUpdate) {
        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return; }

        // Make sure it is registered
        registerInventoryFor(location);
        //Gunging_Ootilities_Plugin.theOots.C Log("Set Item " + OotilityCeption.GetItemName(item) + "\u00a77 at slot \u00a7e" + slot + "\u00a7 in inventory of " + owner.toString());

        // Make sure it is not null
        ItemStack carbonCopy = new ItemStack(Material.AIR);
        if (item != null) { carbonCopy = new ItemStack(item); }

        // Put it into the Personal Container Inventory (if existing)
        PhysicalContainerInventory opened = getOpenedInstance(location);
        if (opened != null) { opened.setEditedItem(slot, carbonCopy); if (procUpdate) { opened.updateSlot(slot); } }
    }
    /**
     * Deletes the item at this slot, and drops it somewhere.
     * <br><br>
     * This <b>does not necessarily</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param location Target container contents
     * @param slot Target slot in the contents
     * @param receiver Player who will receive the item into their inventory.
     *                 The item will be dropped at their location if their
     *                 inventory is full.
     * @param backupDrop Place to drop the item if the receiver player is null.
     * @param procUpdate If the changes should be updated onto the actual observed inventory
     */
    public void dropItemFromInventoryAndSave(@NotNull Location location, int slot, @Nullable Player receiver, @Nullable Location backupDrop, boolean procUpdate) {
        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return; }

        // Register
        registerInventoryFor(location);

        // Get Target ItemStack
        ItemStack dropped = getLocationItem(location, slot);

        // If the inventory has it
        if (!OotilityCeption.IsAirNullAllowed(dropped)) {

            // Drop at player
            if (receiver != null) {

                // Drop
                for (ItemStack drop : receiver.getInventory().addItem(new ItemStack[] { dropped }).values()) {

                    // Drop to the world
                    receiver.getWorld().dropItem(receiver.getLocation(), drop); }

                // Drop at location
            } else if (backupDrop != null) {

                // Yeah, ok
                backupDrop.getWorld().dropItem(backupDrop, dropped);
            }

            // Well, its not supposed to do that...
            setAndSaveOwnerItem(location, slot, null, procUpdate);
        }
    }
    //endregion
}