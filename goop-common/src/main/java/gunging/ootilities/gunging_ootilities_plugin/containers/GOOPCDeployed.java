package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerOpeningReason;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * A management system for a Template:
 * <br> #1 Opens containers
 * <br> #2 Saves changes in their storage contents
 * <br> #3 Manages the storage contents themselves
 */
public abstract class GOOPCDeployed {

    //region Constructor
    /**
     * @return Template followed by this container
     */
    @NotNull
    public GOOPCTemplate getTemplate() { return template; }
    @NotNull GOOPCTemplate template;
    /**
     * Reloading GooP will re-parse the template from the files, so this is necessary
     * to update this GOOPCDeployed with already good information.
     *
     * Reloading must close the container inventories for correct unloading, thus,
     * this method does not need to update the reloaded template into the Opened
     * ContainerInventories.
     *
     * @param newParent Parent that was reloaded.
     */
    public void setTemplate(@NotNull GOOPCTemplate newParent) { template = newParent; internalTemporalID = newParent.getInternalID(); }

    /**
     * @return The ID used by this Container until next reboot.
     */
    public long getInternalTemporalID() { return internalTemporalID; }
    long internalTemporalID;

    /**
     * A wrapper to manage the loading and unloading of Personal-Type containers.
     *
     * @param template Template followed by this container
     */
    public GOOPCDeployed(@NotNull GOOPCTemplate template) {

        // Until-Server-Restart ID
        internalTemporalID = template.getInternalID();

        // The ultimate basic
        this.template = template;
    }
    //endregion

    //region Observed Container
    /**
     * @return The inventories currently loaded, per sub-instance (OID for personal, LID for physical).
     */
    @NotNull public abstract HashMap<Long, ? extends ContainerInventory> getOpenedByID();
    /**
     * @param id Sub-instance number (OID for personal, LID for physical).
     *
     * @return The inventory loaded that corresponds to this temporal ID
     */
    @Nullable public abstract ContainerInventory getOpenedInstance(@Nullable Long id);
    /**
     * @param player Player who might have one of these containers open
     *
     * @return The Container Inventory that player has opened
     */
    @Nullable public abstract ContainerInventory getObservedBy(@Nullable UUID player);
    /**
     * @param id Sub-instance number (OID for personal, LID for physical).
     *
     * @return The principal observer of the target Personal Container
     *         Inventory; Strong preference given to the owner.
     */
    @Nullable public abstract Player getPrincipalObserver(@Nullable Long id);
    /**
     * @param player Player who might be looking at any container of this template
     *
     * @return If the player is indeed looking at a container of this template.
     */
    public abstract boolean isLooking(@Nullable UUID player);
    //endregion

    //region Inspector
    /**
     * This method will append the timestamp of when it was called in order
     * to get an idea of when it happened, this is automatic and need not
     * be included in the message.
     *
     * @param id Which container instance?
     *
     * @param user The one who has accessed this container
     *
     * @param message Message about their action accessing it.
     */
    public abstract void updateSeen(long id, @NotNull UUID user, @NotNull String message);

    /**
     * @param id Which container instance?
     *
     * @return The people who have accessed this in the past, and when, ready
     *         to be writen into the files.
     */
    @NotNull abstract public ArrayList<String> getSerializableSeens(@Nullable Long id);
    /**
     * @param id Which container instance?
     *
     * @return The people who have accessed this in the past, and when.
     */
    @NotNull abstract public HashMap<UUID, NameVariable> getSeens(@Nullable Long id);
    //endregion

    //region Usage
    /**
     * Opens this player's own personal container for themselves.
     *
     * @param opener Player who is opening the container.
     *
     * @param reason Reason under which they are opening it.
     */
    public abstract void openForPlayer(@NotNull Player opener, @NotNull ContainerOpeningReason reason);
    /**
     * Opens this player's own personal container for themselves.
     *
     * @param opener Player who is opening the container.
     *
     * @param id Target container
     *
     * @param reason Reason under which they are opening it.
     */
    public abstract void openForPlayer(@NotNull Player opener, long id, @NotNull ContainerOpeningReason reason);

    /**
     * Unregisters this player from having this container opened.
     * Does not actually close the inventory.
     *
     * @param opener A player that potentially has this station open.
     */
    public abstract void closeForPlayer(@NotNull Player opener);
    /**
     * @param observer Player who may be observing this kind of container
     *
     * @param slot Slot target
     *
     * @return The item observed at that slot
     */
    @Contract("null,_ -> null; _, null -> null")
    @Nullable public abstract ItemStack getObservedItem(@Nullable UUID observer, @Nullable Integer slot);
    //endregion
}
