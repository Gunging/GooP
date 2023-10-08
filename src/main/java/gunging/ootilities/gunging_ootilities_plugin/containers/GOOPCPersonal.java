package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.PersonalContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Personal;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerOpeningReason;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import gunging.ootilities.gunging_ootilities_plugin.misc.OptimizedTimeFormat;
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
 * A wrapper to manage the loading and unloading of personal containers.
 */
public class GOOPCPersonal extends GOOPCDeployed {

    //region Constructor
    /**
     * A wrapper to manage the loading and unloading of Personal-Type containers.
     *
     * @param template Template followed by this container
     */
    public GOOPCPersonal(@NotNull GOOPCTemplate template) { super(template); }

    /**
     * @return Each Owner is given a numeric ID until next reboot, this
     *         tells you which of these temporal IDs belong to which owner.
     */
    @NotNull public HashMap<UUID, Long> getLinkOwnerToOID() { return linkOwnerToOID; }
    @NotNull final HashMap<UUID, Long> linkOwnerToOID = new HashMap<>();
    /**
     * @param associated Owner UUID you do have
     * @return The associated OID (if one is assigned to that owner)
     */
    @Nullable public Long getOID(@Nullable UUID associated) { if (associated == null) { return null; } return linkOwnerToOID.get(associated); }

    /**
     * @return Each Owner is given a numeric ID until next reboot, this
     *         tells you which of these IDs belong to which owner.
     */
    @NotNull public HashMap<Long, UUID> getReverseOwnerToOID() { return reverseOwnerToOID; }
    @NotNull final HashMap<Long, UUID> reverseOwnerToOID = new HashMap<>();
    /**
     * @param associated The OID you do have
     * @return The associated UUID (if one is assigned to that OID)
     */
    @Nullable public UUID getFromOID(@Nullable Long associated) { if (associated == null) { return null; } return reverseOwnerToOID.get(associated); }

    /**
     * @return The ID of the next owner to be loaded this reboot.
     */
    public long getTemporalOIDsCount() { return temporalOIDsCount; }
    long temporalOIDsCount = 0;

    /**
     * @return The saved inventories of all loaded owners.
     */
    @NotNull public HashMap<UUID, HashMap<Integer, ItemStack>> getPerOwnerInventories() { return perOwnerInventories; }
    @NotNull final HashMap<UUID, HashMap<Integer, ItemStack>> perOwnerInventories = new HashMap<>();

    /**
     * Be mindful when you call this method, if this owner does not
     * exist it will be registered and initialized as empty.
     *
     * @param owner The player who owns this inventory.
     *
     * @return The map of items itself.
     */
    @NotNull public HashMap<Integer, ItemStack> getOwnerInventory(@NotNull UUID owner) {
        HashMap<Integer, ItemStack> ret = perOwnerInventories.get(owner);
        if (ret != null) { return ret; }
        registerInventoryFor(owner);
        ret = perOwnerInventories.get(owner);
        if (ret != null) { return ret; }

        // This should never actually happen
        Gunging_Ootilities_Plugin.theOots.CPLog("Could not register inventory for \u00a73" + owner.toString() + "\u00a77 in\u00a7e " + getTemplate().getInternalName());
        ret = new HashMap<>();
        return ret;
    }
    //endregion

    //region Inventory Storages
    /**
     * @return The inventories currently loaded, per owner.
     */
    @NotNull public HashMap<UUID, PersonalContainerInventory> getOpenedInstances() { return openedInstances; }
    @NotNull final HashMap<UUID, PersonalContainerInventory> openedInstances = new HashMap<>();
    /**
     * @param owner The Owner UUID
     *
     * @return The instance managing this owner UUID
     */
    @Nullable public PersonalContainerInventory getOpenedInstance(@Nullable UUID owner) { if (owner == null) { return null;} return getOpenedInstances().get(owner); }

    /**
     * @return Who has opened the container of whom.
     */
    public @NotNull HashMap<UUID, UUID> getPlayerLookingAt() { return playerLookingAt; }
    @NotNull final HashMap<UUID, UUID> playerLookingAt = new HashMap<>();
    /**
     * @param player Player who might have one of these Personal Containers open
     *
     * @return The UUID of the owner of the personal container this player is looking at.
     */
    @Nullable public UUID getLookingAt(@Nullable UUID player) { if (player == null) { return null; } return playerLookingAt.get(player); }
    /**
     * @param player The player who has the inventory open (not necessarily their own personal container)
     *
     * @param owner The owner of the inventory being opened (not necessarily the player opening it)
     */
    public void setLookingAt(@NotNull UUID player, @NotNull UUID owner) { playerLookingAt.put(player, owner); }

    @Override public boolean isLooking(@Nullable UUID player) { if (player == null) { return false; } return playerLookingAt.containsKey(player); }
    /**
     * @param owner Target Personal Container
     *
     * @return If the player who the PAPI placeholders in that container will parse for
     */
    @Nullable public Player getPrincipalObserver(@Nullable UUID owner) {
        PersonalContainerInventory opened = getOpenedInstance(owner);
        if (opened == null) { return null; }

        // Yeah
        return opened.getPrincipalObserver();
    }

    @Override public @NotNull HashMap<Long, PersonalContainerInventory> getOpenedByID() {

        // Yeah
        HashMap<Long, PersonalContainerInventory> ret = new HashMap<>();

        // For every entry
        for (Map.Entry<UUID, PersonalContainerInventory> oid : getOpenedInstances().entrySet()) {

            // Lookup IDs
            ret.put(getOID(oid.getKey()), oid.getValue());
        }

        // Yeah
        return ret;
    }
    @Override @Nullable public PersonalContainerInventory getOpenedInstance(@Nullable Long oid) { return getOpenedInstance(getFromOID(oid)); }
    @Override @Nullable public PersonalContainerInventory getObservedBy(@Nullable UUID player) { return getOpenedInstance(getLookingAt(player)); }
    @Override @Nullable public Player getPrincipalObserver(@Nullable Long id) { return getPrincipalObserver(getFromOID(id)); }
    //endregion

    //region Loading and Unloading
    /**
     * Registers this players in two places:
     * <br>     #1 Makes sure they have Inventory Contents associated to them.
     * <br>     #2 Makes sure they have a registered temporal OID.
     *
     * @param player Player to register.
     */
    public void registerInventoryFor(@NotNull UUID player) {

        // If not in the Inventory Hash
        if (!perOwnerInventories.containsKey(player)) {

            // Create HashMap, load contents from file or empty.
            GCL_Personal.load(this, player);
        }

        // If not in the ID link hash
        if (!linkOwnerToOID.containsKey(player)) {

            // Assign a new ID
            temporalOIDsCount++;

            // Register Bidirectionally
            linkOwnerToOID.put(player, temporalOIDsCount);
            reverseOwnerToOID.put(temporalOIDsCount, player);
        }
    }

    /**
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param inventories The inventories stored in the files (most likely).
     */
    public void loadOwnerContents(@NotNull HashMap<UUID, HashMap<Integer, ItemStack>> inventories) {

        // Load all of those
        for (Map.Entry<UUID, HashMap<Integer, ItemStack>> pair : inventories.entrySet()) { loadOwnerContents(pair.getKey(), pair.getValue()); }
    }
    /**
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param owner Owner of the inventory
     *
     * @param savedInventory Inventory contents (overwrites previous)
     */
    public void loadOwnerContents(@NotNull UUID owner, @NotNull HashMap<Integer, ItemStack> savedInventory) {

        // It must be set in the arrays
        perOwnerInventories.put(owner, savedInventory);

        // It must be set in the opened container inventory
        PersonalContainerInventory opened = getOpenedInstance(owner);

        // If existing, reload
        if (opened != null) { opened.loadStorageContents(savedInventory); }
    }
    /**
     * Puts an item in someone's loaded storage.
     * <br><br>
     * This <b>does not</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param owner The player who owns this inventory
     * @param slot Slot at which this item is found.
     */
    public void loadOwnerItem(@Nullable UUID owner, int slot, @Nullable ItemStack item) {
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76LDE\u00a77 Loading at \u00a73#" + slot + "\u00a77 " + OotilityCeption.GetItemName(item));
        if (owner == null) { return; }
        if (slot >= getTemplate().getTotalSlotCount() || slot < 0) {
            //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76LDE\u00a7c -\u00a77 Overflow Slot ");
            return; }

        // It must be set in the arrays
        getOwnerInventory(owner).put(slot, item);

        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76LDE\u00a7a *\u00a77 Put " + OotilityCeption.GetItemName(getOwnerInventory(owner).get(slot)));

        // It must be set in the opened container inventory
        PersonalContainerInventory opened = getOpenedInstance(owner);

        // If existing, reload
        if (opened != null) {
            //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 Found opened instance for this owner");
            opened.setStorageItem(slot, item); }

        //CLI//else { OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 No opened instance for this owner"); }
    }

    /**
     * Unloads this inventory if there are no viewers of it
     *
     * @param opened Inventory to unload.
     */
    public void shouldUnloadOpened(@NotNull PersonalContainerInventory opened) {
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a7cCLS\u00a79 *\u00a77 Perhaps unloading for owner \u00a7b" + opened.getOwnerUUID().toString() + "\u00a78 (Observers:\u00a75 " + opened.getObservers().size() + "\u00a78)");

        // Unload if it has no viewers
        if (opened.getObservers().size() == 0) {
            //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a7cCLS\u00a7c *\u00a77 Unloading...");

            unloadOpened(opened); }
    }
    /**
     * Unloads this inventory.
     *
     * @param opened Inventory to unload.
     */
    public void unloadOpened(@NotNull PersonalContainerInventory opened) {

        // Does it have any viewers?
        if (opened.getObservers().size() > 0) { closeInventory(opened, true); return; }

        // Unload that one belonging to this UUID
        openedInstances.remove(opened.getOwnerUUID());
    }

    /**
     * Previous seens get cleared.
     *
     * @param seensOverride The inspector history of this container.
     */
    public void loadSeens(@NotNull HashMap<UUID, HashMap<UUID, NameVariable>> seensOverride) { lastSeen.clear(); lastSeen.putAll(seensOverride); }
    /**
     * Previous seens for this owner get cleared.
     *
     * @param seensOverride The inspector history for this owner for this container.
     */
    public void loadSeens(@NotNull UUID owner, @NotNull HashMap<UUID, NameVariable> seensOverride) { lastSeen.put(owner, seensOverride); }

    /**
     * Closes the inventories of everyone who have this Personal Container open.
     */
    public void closeAllInventories() {

        // Go through all those opened
        for (PersonalContainerInventory opened : openedInstances.values()) {

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
    public void closeInventory(@Nullable PersonalContainerInventory opened, boolean forceCloseAll) {
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
    @NotNull final HashMap<UUID, HashMap<UUID, NameVariable>> lastSeen = new HashMap<>();
    /**
     * This method will append the timestamp of when it was called in order
     * to get an idea of when it happened, this is automatic and need not
     * be included in the message.
     *
     * @param owner Whose personal container?
     *
     * @param user The one who has accessed this container
     *
     * @param message Message about their action accessing it.
     */
    public void updateSeen(@NotNull UUID owner, @NotNull UUID user, @NotNull String message) {

        // Get thisse
        HashMap<UUID, NameVariable> tSeen = lastSeen.get(owner);
        if (tSeen == null) { tSeen = new HashMap<>(); }

        // Just joins two links
        tSeen.put(user, new NameVariable(OptimizedTimeFormat.GetCurrentTime(), message));
        lastSeen.put(owner, tSeen);

        // Save
        GCL_Personal.saveContents(this, owner);
    }
    /**
     * @param owner Whose personal container?
     *
     * @return The people who have accessed this in the past, and when, ready
     *         to be writen into the files.
     */
    @NotNull public ArrayList<String> getSerializableSeens(@Nullable UUID owner) {
        if (owner == null) { return new ArrayList<>(); }

        // Get thisse
        HashMap<UUID, NameVariable> tSeen = lastSeen.get(owner);
        if (tSeen == null) { tSeen = new HashMap<>(); }

        ArrayList<String> ret = new ArrayList<>();
        for (UUID seen : tSeen.keySet()) {

            // Add string
            ret.add(seen.toString() + "#Ñ#" + tSeen.get(seen).Serialize());
        }

        // Return thay
        return ret;
    }
    /**
     * @param owner Whose personal container?
     *
     * @return The people who have accessed this in the past, and when.
     */
    public @NotNull HashMap<UUID, NameVariable> getSeens(@Nullable UUID owner) {
        if (owner == null) { return new HashMap<>(); }

        // Get those
        HashMap<UUID, NameVariable> selected = lastSeen.get(owner);
        if (selected == null) { selected = new HashMap<>(); }

        // Put
        return selected;
    }

    /*
    /**
     * bleh bleh (taking to sarahhhh) but something about
     *
     * player must be online to know if the slot is restricted
     * so if the player is offline it kind of thinks the slot is unlocked yea
     *
     * @param uuid uuid of player who you're testing
     * @param s slot number
     * @return If we can assume this slot is locked
     *
    public boolean isUnlockedGuess(@NotNull UUID uuid, int s) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);

        Player b = p.getPlayer();

        if (p.hasPlayedBefore() && b != null) {

            return getParentTemplate().meetsRestrictions(b, s);

        } else {

            return true;
        }
    } */

    @Override public @NotNull HashMap<UUID, NameVariable> getSeens(@Nullable Long id) { return getSeens(getFromOID(id)); }
    @Override public @NotNull ArrayList<String> getSerializableSeens(@Nullable Long id) { return getSerializableSeens(getFromOID(id)); }
    @Override public void updateSeen(long id, @NotNull UUID user, @NotNull String message) {
        UUID owner = getFromOID(id);
        if (owner == null) { return; }

        // Update it
        updateSeen(owner, user, message);
    }
    //endregion

    //region Actual Functionality
    /**
     * This method returns no information on the slot the items were in.
     *
     * @param owner Owner of the Personal Container
     *
     * @return The items they have equipped (the items actually saved within
     *         equipment slots, as recognized in the files).
     *
     * @see #indexedItemsForEquipment(Player)
     */
    @NotNull public ArrayList<ItemStack> itemsForEquipment(@NotNull Player owner) {

        // Return the values of the indexed variant
        return new ArrayList<>(indexedItemsForEquipment(owner).values());
    }
    /**
     * @param owner Owner of the Personal Container
     *
     * @return The items they have equipped (the items actually saved within
     *         equipment slots, as recognized in the files) and where they are.
     *
     * @see #itemsForEquipment(Player)
     */
    @NotNull public HashMap<Integer, ItemStack> indexedItemsForEquipment(@NotNull Player owner) {

        // Something to return
        HashMap<Integer, ItemStack> ret = new HashMap<>();

        // Get Eq Slots
        ArrayList<Integer> in = getTemplate().getEquipmentSlots();
        if (in.size() == 0) { return ret; }

        // Ay
        HashMap<Integer, ItemStack> inven = perOwnerInventories.get(owner.getUniqueId());
        if (inven == null) { return ret; }

        // Get thine inventory
        for (Integer i : in) {

            // Find slot
            GOOPCSlot slot = getTemplate().getSlotAt(i);

            // Skip snoozer slots
            if (slot == null) { continue; }

            // Clear slot due to restrictions
            if (!slot.matchesRestrictions(owner)) {

                // Get item
                ItemStack item = getOwnerItem(owner.getUniqueId(), i);

                // If there is nothing, no need to be checking this stuff
                if (OotilityCeption.IsAirNullAllowed(item)) { continue; }

                // Switch restriction
                switch (slot.getRestrictedBehaviour()) {
                    case DROP:

                        // Drop
                        for (ItemStack drop : owner.getInventory().addItem(item).values()) {

                            // Not air right
                            if (OotilityCeption.IsAirNullAllowed(drop)) { continue; }

                            // Drop to the world
                            owner.getWorld().dropItem(owner.getLocation(), drop); }

                        // Notice there is no break; It is expected that DROP will also call this method:
                    case DESTROY:

                        // Clear item at this slot. May update observed inventories.
                        setAndSaveOwnerItem(owner.getUniqueId(), i, null,true);
                        break;

                    // Take and lock wont equip the item though
                    default: break;
                }

            // Passed restrictions
            } else {

                // Return thine Items
                ItemStack found = inven.get(i);

                // Found
                if (OotilityCeption.IsAirNullAllowed(found)) { continue; }

                // Include as equipped item yes
                ret.put(i, found);
            }
        }

        return ret;
    }

    /**
     * Opens this player's own personal container for themselves.
     *
     * @param opener Player who is opening the container, and owns it.
     *
     * @param reason Reason under which they are opening it.
     */
    @Override public void openForPlayer(@NotNull Player opener, @NotNull ContainerOpeningReason reason) {  openForPlayer(opener, opener.getUniqueId(), reason);  }
    /**
     * Opens someone's personal container to some player.
     *
     * @param opener Player who is opening the container.
     *
     * @param owner Owner of the target personal container.
     *
     * @param reason Reason of the opener to open the container.
     */
    public void openForPlayer(@NotNull Player opener, @NotNull UUID owner, @NotNull ContainerOpeningReason reason) {
        //OPN//OotilityCeption.Log("\u00a7b--------------------------------------------------");
        //OPN//OotilityCeption.Log("\u00a78PERSONAL \u00a73OPN\u00a77 Opening for\u00a7a " + opener.getName());

        // Register the UUID ffs
        registerInventoryFor(owner);

        // Make sure they don't got nothing open
        GOOPCManager.closeAllContainersFor(opener);

        // Any existing inventory for this owner?
        PersonalContainerInventory opened = getOpenedInstance(owner);
        if (opened != null) {
            //OPN//OotilityCeption.Log("\u00a78PERSONAL \u00a73OPN\u00a77 Found previously opened instance, redirecting...");

            // Add observer
            opened.addObserver(opener);

            // Open inventory
            opener.openInventory(opened.getInventory());

            // Register
            setLookingAt(opener.getUniqueId(), owner);

            // Yes
            return;
        }
        //OPN//OotilityCeption.Log("\u00a78PERSONAL \u00a73OPN\u00a77 Creating opened instance...");

        // Create with the opener as principal but correct owner ID
        opened = new PersonalContainerInventory(getTemplate(), this, GOOPCManager.getReasonProcessOf(reason), owner, opener);

        //OPN//OotilityCeption.Log("\u00a78PERSONAL \u00a73OPN\u00a77 Loading Owner Contents...");
        // Load the contents
        opened.loadStorageContents(getOwnerInventory(owner));

        // Register it being opened
        openedInstances.put(owner, opened);

        // Perform Restricted Behaviour ops if it is the owner
        if (opener.getUniqueId().equals(owner)) {

            // Sanitize
            opened.sanitizeStorage(opener);

            // Any restriction deactivated any equipment piece?
            boolean refreshEquipment = false;

            // Evaluate every slot for Restriction
            for (int s = 0; s < getTemplate().getTotalSlotCount(); s++) {

                // Find slot
                GOOPCSlot slot = getTemplate().getSlotAt(s);

                // Skip snooze
                if (slot == null) { continue; }

                // Restrictions not met? Restrict Behaviour kicks in.
                if (!slot.matchesRestrictions(opener)) {

                    // Will have to refresh equipment yeah
                    refreshEquipment = true;

                    // Find the item in that slot
                    ItemStack item = getOwnerItem(owner, s);

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
                            setAndSaveOwnerItem(owner, s, null, false);
                            break;
                    }
                }
            }

            // Update Equipment if any changes were made due to restrictions kicking in.
            if (refreshEquipment && Gunging_Ootilities_Plugin.foundMMOItems) { GooPMMOItems.UpdatePlayerEquipment(opener); }
        }

        //OPN//OotilityCeption.Log("\u00a78PERSONAL \u00a73OPN\u00a77 Opening...");

        // Update inventory
        opened.updateInventory();

        // Open inventory
        opener.openInventory(opened.getInventory());

        // Register
        setLookingAt(opener.getUniqueId(), owner);
    }

    @Override public void closeForPlayer(@NotNull Player opener) {
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a7cCLS\u00a77 Closing for \u00a73 " + opener.getName());

        // Unregistered from Looking At
        UUID openedID = getLookingAt(opener.getUniqueId());
        if (openedID == null) {
            //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a7cCLS\u00a7c *\u00a77 Not registered having this opened");
            return; }
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a7cCLS\u00a7e *\u00a77 Removed from opening registry");
        getPlayerLookingAt().remove(opener.getUniqueId());

        // Unregister from Observed Container
        PersonalContainerInventory opened = getOpenedInstance(openedID);
        if (opened == null) {
            //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a7cCLS\u00a7c *\u00a77 No open instance for owner");
            return; }
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a7cCLS\u00a7e *\u00a77 Removed from opened instance observer list");

        // Remove with demotion
        opened.removeObserver(opener, true);

        // Unregister opened
        shouldUnloadOpened(opened);
    }

    /**
     * @param owner The player who owns this inventory
     * @param slot Slot at which this item is found.
     *
     * @return The map of items itself.
     */
    @Nullable public ItemStack getOwnerItem(@Nullable UUID owner, @Nullable Integer slot) {
        if (owner == null || slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }
        return getOwnerInventory(owner).get(slot);
    }
    /**
     * @param owner Target opened container.
     * @param slot Slot where at
     *
     * @return The item in the edited layer in the opened container yeah.
     */
    @Nullable public ItemStack getEdited(@NotNull UUID owner, @Nullable Integer slot) {
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Make sure it is registered
        registerInventoryFor(owner);
        //Gunging_Ootilities_Plugin.theOots.C Log("Set Item " + OotilityCeption.GetItemName(item) + "\u00a77 at slot \u00a7e" + slot + "\u00a7 in inventory of " + owner.toString());

        // Find the Personal Container Inventory (if existing)
        PersonalContainerInventory opened = getOpenedInstance(owner);
        if (opened == null) { return null; }

        // Well, get the edited layer there
        return opened.getLayerEdited().get(slot);
    }
    /**
     * @param owner Target opened container.
     * @param slot Slot where at
     *
     * @return The item in the default layer in the opened container yeah.
     */
    @Nullable public ItemStack getDefault(@NotNull UUID owner, @Nullable Integer slot) {
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Make sure it is registered
        registerInventoryFor(owner);

        // Find the Personal Container Inventory (if existing)
        PersonalContainerInventory opened = getOpenedInstance(owner);
        if (opened == null) { return null; }

        // Well, get the edited layer there
        return opened.getLayerDefault().get(slot);
    }
    /**
     * @param observer Target opened container.
     * @param slot Slot where at
     *
     * @return The item actually displayed to the user.
     */
    @Contract("null,_ -> null; _, null -> null")
    @Override
    @Nullable public ItemStack getObservedItem(@Nullable UUID observer, @Nullable Integer slot) {
        if (observer == null) { return null; }
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Well, get the edited layer there
        return getObservedItemByOwner(getLookingAt(observer), slot);
    }
    /**
     * @param owner Target opened container.
     * @param slot Slot where at
     *
     * @return The item actually displayed to the user.
     */
    @Contract("null,_ -> null; _, null -> null")
    @Nullable public ItemStack getObservedItemByOwner(@Nullable UUID owner, @Nullable Integer slot) {
        if (owner == null) { return null; }
        if (slot == null || slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return null; }

        // Make sure it is registered
        registerInventoryFor(owner);

        // Find the Personal Container Inventory (if existing)
        PersonalContainerInventory opened = getOpenedInstance(owner);
        if (opened == null) {

            // Not for storage no service
            if (!getTemplate().isStorageSlot(slot)) { return null; }

            // Get personal contents, naturally.
            return getOwnerItem(owner, slot);
        }

        // Well, get the edited layer there
        return opened.getItem(slot);
    }

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
        UUID targetOwner = getFromOID(oid);
        if (targetOwner == null) { targetOwner = opener.getUniqueId(); }

        // Open
        openForPlayer(opener, targetOwner, reason);
    }
    //endregion

    //region Saving
    /**
     * Saves all the inventories associated to this Personal Container into the files.
     */
    public void saveOwnerItems() { for (UUID owner : perOwnerInventories.keySet()) { saveOwnerItems(owner); } }
    /**
     * Saves all the items of this player into the files.
     *
     * @param owner The player whose items to save.
     */
    public void saveOwnerItems(@NotNull UUID owner) {  GCL_Personal.saveContents(this, owner); }
    /**
     * Sets the item in the Owner's Contents, saves it into the files, and sets
     * the item in the storage layers of PersonalContainerInventories.
     * <br><br>
     * This <b>does not necessarily</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     *
     * @param owner Owner of the Contents
     * @param slot Slot among the Contents
     * @param item Item to include in the Contents
     * @param procUpdate If the changes should be updated onto the actual observed inventory
     */
    public void setAndSaveOwnerItem(@NotNull UUID owner, int slot, @Nullable ItemStack item, boolean procUpdate) {
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 Saving at \u00a73#" + slot + "\u00a77 " + OotilityCeption.GetItemName(item));
        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) {
            //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a7c-\u00a77 Overflow Slot");
            return; }

        // Make sure it is registered
        registerInventoryFor(owner);
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 Registered Owner Inventory");

        // Put it into the Contents
        loadOwnerItem(owner, slot, item);
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 Loaded Owner Item");

        // Find inventory and send update
        if (procUpdate) {
            //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 Searching opened to update");

            // Put it into the Personal Container Inventory (if existing)
            PersonalContainerInventory opened = getOpenedInstance(owner);
            if (opened != null) {
                //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 Found opened instance for this owner");
                opened.updateSlot(slot);
            }

            //CLI//else { OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 No opened instance for this owner"); }
        }

        // Save Inventory
        saveOwnerItems(owner);
        //CLI//OotilityCeption.Log("\u00a78PERSONAL \u00a76SVE\u00a77 Saved Contents");
    }

    /**
     * Sets the item in the Owner's Observed Inventory edited layer.
     * <br><br>
     * This <b>does not necessarily</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param owner Owner of the Contents
     * @param slot Slot among the Contents
     * @param item Item to include in the Contents
     * @param procUpdate If the changes should be updated onto the actual observed inventory
     */
    public void setInEdited(@NotNull UUID owner, int slot, @Nullable ItemStack item, boolean procUpdate) {
        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return; }

        // Make sure it is registered
        registerInventoryFor(owner);
        //Gunging_Ootilities_Plugin.theOots.C Log("Set Item " + OotilityCeption.GetItemName(item) + "\u00a77 at slot \u00a7e" + slot + "\u00a7 in inventory of " + owner.toString());

        // Make sure it is not null
        ItemStack carbonCopy = new ItemStack(Material.AIR);
        if (item != null) { carbonCopy = new ItemStack(item); }

        // Put it into the Personal Container Inventory (if existing)
        PersonalContainerInventory opened = getOpenedInstance(owner);
        if (opened != null) { opened.setEditedItem(slot, carbonCopy); if (procUpdate) { opened.updateSlot(slot); } }
    }
    /**
     * Deletes the item at this slot, and drops it somewhere.
     * <br><br>
     * This <b>does not necessarily</b> proc inventory update.
     * This <b>does not</b> proc MMOItem equipment updates.
     * This <b>does not</b> save into the files.
     *
     * @param owner Target container contents
     * @param slot Target slot in the contents
     * @param receiver Player who will receive the item into their inventory.
     *                 The item will be dropped at their location if their
     *                 inventory is full.
     * @param backupDrop Place to drop the item if the receiver player is null.
     * @param procUpdate If the changes should be updated onto the actual observed inventory
     */
    public void dropItemFromInventoryAndSave(@NotNull UUID owner, int slot, @Nullable Player receiver, @Nullable Location backupDrop, boolean procUpdate) {
        if (slot < 0 || slot >= getTemplate().getTotalSlotCount()) { return; }

        // Register
        registerInventoryFor(owner);

        // Get Target ItemStack
        ItemStack dropped = getOwnerItem(owner, slot);

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
            setAndSaveOwnerItem(owner, slot, null, procUpdate);
        }
    }
    //endregion
}
