package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Physical;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.GPCProtection;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSBlock;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import gunging.ootilities.gunging_ootilities_plugin.misc.chunks.ChunkMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Among other things than just the items loaded at this location,
 * this wrapper stores protection information and transaction history.
 * <br><br>
 * What this <b>does not do</b> is care about the opened container. It
 * is the job of the GOOPCPhysical to connect these two. If any changes
 * are made, the GOOPCPhysical will update the changes here, and in any
 * opened containers, and when generating a new opened container, the
 * GOOPCPhysical will only read these values to be up-to-date.
 * <br>
 * Therefore, this is only a wrapper for the information at that location,
 * and has no input on the observed container.
 */
public class GPCContent {

    //region Constructor
    /**
     * @return The location of this physical container
     */
    @NotNull public GOOPCPhysical getContainer() { return container; }
    @NotNull final GOOPCPhysical container;

    /**
     * @return The location of this physical container
     */
    @NotNull public Location getLocation() { return location; }
    @NotNull final Location location;

    /**
     * @return The access history of this physical container.
     */
    @NotNull public HashMap<UUID, NameVariable> getSeens() { return seens; }
    @NotNull final HashMap<UUID, NameVariable> seens = new HashMap<>();
    /**
     * @param seensOverride Delete the previous seen information and load these
     */
    public void loadSeens(@NotNull HashMap<UUID, NameVariable> seensOverride) { seens.clear(); seens.putAll(seensOverride); }
    /**
     * @param user User accessing this container
     *
     * @param message Message attached to the accessing of the container.
     */
    public void putSeen(@NotNull UUID user, @NotNull NameVariable message) { seens.put(user, message); }
    /**
     * @return The people who have accessed this in the past,
     *         and when, ready to be writen into the files.
     */
    @NotNull public ArrayList<String> getSerializableSeens() {

        // Get the seens
        HashMap<UUID, NameVariable> seens = getSeens();

        ArrayList<String> ret = new ArrayList<>();
        for (UUID seen : seens.keySet()) {

            // Add string
            ret.add(seen.toString() + "#Ñ#" + seens.get(seen).Serialize()); }

        // Return thay
        return ret;
    }

    /**
     * @return The LID of this physical container
     */
    public long getLID() { return lid; }
    final long lid;

    /**
     * @return The type of protection this container follows
     */
    @NotNull public GPCProtection getProtectionType() { return protectionType; }
    @NotNull GPCProtection protectionType;

    /**
     * Edits the protection of this container.
     *
     * @param type New type of protection
     * @param owner New owner
     */
    public void editProtection(@NotNull GPCProtection type, @Nullable UUID owner) {

        // UNRESTRICTED iff owner is null
        if (owner == null) { type = GPCProtection.UNREGISTERED; }
        else if (type == GPCProtection.UNREGISTERED) { owner = null; }

        // Accept Owner
        containerOwner = owner;
        if (owner != null) {
            containerAdmins.add(owner);
            containerMembers.add(owner); }
        protectionType = type;
    }

    /**
     * @return The ID of the highest admin of this container
     */
    @Nullable public UUID getContainerOwner() { return containerOwner; }
    @Nullable UUID containerOwner;

    /**
     * @param location The location of this container
     * @param protectionType Protection type, yes
     * @param containerOwner Owner of the protection. If null, protection type will be set to
     *                       {@link GPCProtection#UNREGISTERED}
     */
    public GPCContent(@NotNull GOOPCPhysical container, @NotNull Location location, long lid, @NotNull GPCProtection protectionType, @Nullable UUID containerOwner) {
        this.container = container;
        this.lid = lid;
        this.location = location;
        this.protectionType = protectionType;
        this.containerOwner = containerOwner;
    }
    //endregion

    //region Loading
    /**
     * @return The items stored at this location
     */
    @NotNull public HashMap<Integer, ItemStack> getStorageContents() { return storageContents; }
    @NotNull final HashMap<Integer, ItemStack> storageContents = new HashMap<>();
    /**
     * This method
     *
     * @param contents Contents to overwrite the current contents with.
     */
    public void loadStorageContents(@NotNull HashMap<Integer, ItemStack> contents) {
        storageContents.clear();
        storageContents.putAll(contents);
    }

    /**
     * @return All the people who have permission to add members to this container
     *         (including the owner).
     */
    @NotNull public ArrayList<UUID> getContainerAdmins() { return containerAdmins; }
    @NotNull final ArrayList<UUID> containerAdmins = new ArrayList<>();
    /**
     * @param newAdmins Admin list to replace current admins (adds owner automatically if missing)
     */
    public void loadAdmins(@NotNull ArrayList<UUID> newAdmins) {
        containerAdmins.clear();
        if (containerOwner != null && !newAdmins.contains(containerOwner)) { newAdmins.add(containerOwner); }
        for (UUID admin : newAdmins) { if (admin == null) { continue; } containerAdmins.add(admin); }
    }

    /**
     * @return All the people who can take items out of this container
     *         (including the admins and owner).
     */
    @NotNull public ArrayList<UUID> getContainerMembers() { return containerMembers; }
    @NotNull final ArrayList<UUID> containerMembers = new ArrayList<>();
    /**
     * @param newMembers Member list to replace current members (adds admins and owner automatically if missing)
     */
    public void loadMembers(@NotNull ArrayList<UUID> newMembers) {
        containerMembers.clear();
        for (UUID member : containerAdmins) { if (!newMembers.contains(member)) { newMembers.add(member); } }
        for (UUID member : newMembers) { if (member == null) { continue; } containerMembers.add(member); }
    }
    /**
     * @return The blocks this container occupies in the world.
     *         With custom structures, containers may be large vaults.
     */
    @NotNull public ArrayList<CSBlock> getInherentBlocks() { return inherentBlocks; }
    @NotNull final ArrayList<CSBlock> inherentBlocks = new ArrayList<>();
    /**
     * The inherent structure is now forced to include the core block, as such,
     * this will check that there is a core block and add it if missing.
     * <br><br>
     * This also registers the inherent blocks associated to this location
     * in {@link GCL_Physical#getInherentStructureOverlap()}; There is tho
     * not a good way of removing previous blocks that are linked to this
     * core, such that a reload is required for that to happen. However,
     * nowhere in the plugin is it supported to modify Inherent Structures
     * without reloading afterwards anyway so I wont care about that today.
     *
     * @param blocksOverride Blocks to replace the current inherent structure
     */
    public void loadInherentBlocks(@NotNull ArrayList<CSBlock> blocksOverride) {

        // Force structure to have the core
        boolean hasCore = false;
        for (CSBlock csBlock : blocksOverride) { if (csBlock.isCore()) { hasCore = true; break; }}
        if (!hasCore) { blocksOverride.add(new CSBlock(Material.STONE, 0, 0, 0)); }

        // Update structure
        inherentBlocks.clear();
        inherentBlocks.addAll(blocksOverride);


        // Fetch the Inherent Overlap
        ChunkMap<ArrayList<GPCContent>> inherentOverlap = GCL_Physical.getInherentStructureOverlap();

        /*
         * Go through each Custom Structure Block, then
         * obtain its true location relative to this, in
         * order to add it to the Inherent Overlap.
         */
        for (CSBlock csBlock : getInherentBlocks()) {
            if (csBlock == null) { continue; }

            // Get true location
            Location csLocation = csBlock.getRelativeTo(getLocation());

            // Get references
            ArrayList<GPCContent> overlap = inherentOverlap.get(csLocation);
            if (overlap == null) { overlap = new ArrayList<>(); }

            // Add oneself
            boolean contained = false; for (GPCContent content : overlap) { if (content == null) { continue; } if (this.getLocation().equals(content.getLocation())) { contained = true; break; } }
            if (!contained) { overlap.add(this); }

            // Yes
            inherentOverlap.put(csLocation, overlap);
        }
    }
    //endregion

    //region Administrative
    /**
     * @param potentialOwner Player who you don't know if hey are the owner of this
     * @return If they own the protection on this container.
     */
    public boolean isOwner(@Nullable UUID potentialOwner) { if (potentialOwner == null) { return false; } return potentialOwner.equals(getContainerOwner()); }
    /**
     * @param potentialAdmin Player who you don't know if hey are an admin of this
     * @return If they can edit the protection of this container.
     */
    public boolean isAdmin(@Nullable UUID potentialAdmin) { if (potentialAdmin == null) { return false; } return getContainerAdmins().contains(potentialAdmin); }
    /**
     * @param potentialMember Player who you don't know if hey are a member of this
     * @return If they can store items in this container.
     */
    public boolean isMember(@Nullable UUID potentialMember) { if (potentialMember == null) { return false; } return getContainerMembers().contains(potentialMember); }

    /**
     * @return If the protection type of this is {@link GPCProtection#UNREGISTERED}
     */
    public boolean isUnprotected() { return getProtectionType() == GPCProtection.UNREGISTERED; }
    /**
     * @return If the protection type of this is {@link GPCProtection#PRIVATE}
     */
    public boolean isPrivate() { return getProtectionType() == GPCProtection.PRIVATE; }
    /**
     * @return If the protection type of this is {@link GPCProtection#PUBLIC}
     */
    public boolean isPublic() { return getProtectionType() == GPCProtection.PUBLIC; }
    /**
     * @return If the protection type of this is {@link GPCProtection#DISPLAY}
     */
    public boolean isDisplay() { return getProtectionType() == GPCProtection.DISPLAY; }

    /**
     * @param player Player mining this block, or <code>null</code> if an
     *               environmental cause.
     *
     * @return If this player can destroy the container.
     */
    public boolean canDestroy(@Nullable Player player) {

        // Any source can destroy unprotected containers
        if (isUnprotected()) { return true; }

        // Environment cannot destroy protected containers
        if (player == null) { return false; }

        // Op players can destroy
        if (player.isOp()) { return true; }

        /*
         * This thing is protected, only the owner can
         */
        return isOwner(player.getUniqueId());
    }

    /**
     * This destroys the container no matter what. Make sure
     * that you checked the source with {@link #canDestroy(UUID)}
     * first.
     */
    public void destroy() {

        /*
         * Destroying a physical container involves the following:
         *
         *   #0 Close the inventory from everyone who has it open
         *
         *   #1 Spill its items at its core location
         *
         *   #2 Unload from the files and engines
         *
         *   #3 Delete its file
         */
        getContainer().closeInventory(getContainer().getOpenedInstance(getLocation()), true);
        getContainer().dropAllItems(this);
        GCL_Physical.unloadDelete(this);
    }
    //endregion

    //region Storage
    public void setItem(@Nullable Integer slot, @Nullable ItemStack item) {

        // Out of Range Cancellation
        if (slot == null || item == null || slot < 0 || slot >= getContainer().getTemplate().getTotalSlotCount()) { return; }

        // Well, set the content!
        storageContents.put(slot, item);
    }

    /**
     * @param slot Slot target, method will return null if invalid.
     * @return The item saved into the files contained in this slot
     */
    @Nullable public ItemStack getItem(@Nullable Integer slot) {

        // Out of Range Cancellation
        if (slot == null || slot < 0 || slot >= getContainer().getTemplate().getTotalSlotCount()) { return null; }

        // Well, set the content!
        return storageContents.get(slot);
    }
    //endregion
}
