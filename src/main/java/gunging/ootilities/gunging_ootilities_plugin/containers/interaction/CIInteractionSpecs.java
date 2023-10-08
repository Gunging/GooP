package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCDeployed;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CIInteractionSpecs {

    /**
     * @param player    The player who is clicking
     * @param event     Event that fired this Interaction
     * @param deployed  The deployed GooP container of the top inventory
     * @param observed  The particular instance of the top inventory GooP Container
     * @param rpg       The active RPG Inventory of the player who is clicking
     */
    public CIInteractionSpecs(@NotNull Player player,
                              @NotNull InventoryClickEvent event,
                              @Nullable GOOPCDeployed deployed,
                              @Nullable ContainerInventory observed,
                              @Nullable GOOPCPlayer rpg) {
        this.player = player;
        this.deployedContainer = deployed;
        this.observedContainer = observed;
        this.deployedRPG = rpg;
        this.event = event;
    }

    /**
     * @return The deployed GooP container of the top inventory
     */
    @Nullable public GOOPCDeployed getDeployedContainer() { return deployedContainer; }
    @Nullable GOOPCDeployed deployedContainer;

    /**
     * @return The particular instance of the top inventory GooP Container
     */
    @Nullable public ContainerInventory getObservedContainer() { return observedContainer; }
    @Nullable ContainerInventory observedContainer;

    /**
     * @return The active RPG Inventory of the player who is clicking
     */
    @Nullable public GOOPCPlayer getDeployedRPG() { return deployedRPG; }
    @Nullable GOOPCPlayer deployedRPG;

    /**
     * @return The player who is clicking
     */
    @NotNull public Player getPlayer() { return player; }
    @NotNull Player player;

    /**
     * @return Event that fired this Interaction
     */
    @NotNull public InventoryClickEvent getEvent() { return event; }
    @NotNull InventoryClickEvent event;

    /**
     * @return If this click took place in the top inventory GooP Container
     */
    public boolean isClickedTopInventory() { return (getEvent().getRawSlot() < getEvent().getView().getTopInventory().getSize()) && (getEvent().getView().getType() != InventoryType.CRAFTING); }

    /**
     * @return If this click took place in the bottom inventory GooP Container
     */
    public boolean isClickedBottomInventory() { return !isClickedTopInventory(); }

    /**
     * @return If there is no GooP Container in the top inventory
     */
    public boolean isNullContainer() { return getDeployedContainer() == null || getObservedContainer() == null; }

    /**
     * @return if there is no RPG Inventory in the bottom inventory
     */
    public boolean isNullRPGInventory() { return getDeployedRPG() == null; }

    /**
     * @return If there is a GooP Container in the top inventory
     */
    public boolean hasContainer() { return !isNullContainer(); }

    /**
     * @return if there is an RPG Inventory in the bottom inventory
     */
    public boolean hasRPGInventory() { return !isNullRPGInventory(); }

    /**
     * @param affectsTop If this interaction affects the top inventory
     *
     * @return If this will be affecting the GooP Container
     */
    public boolean affectsContainer(boolean affectsTop) { return hasContainer() && (isClickedTopInventory() || affectsTop); }

    /**
     * @param affectsBottom If this interaction affects the bottom inventory
     *
     * @return If this will be affecting the RPG Inventory
     */
    public boolean affectsRPGInventory(boolean affectsBottom) { return hasRPGInventory() && (isClickedBottomInventory() || affectsBottom || event.getView().getType() == InventoryType.CRAFTING); }

    /**
     * @return Usually just {@link InventoryClickEvent#getSlot()} but when clicking the four
     *         crafting slots from the player crafting menu, it will return the correct slots
     *         80-84 that code for the template slots.
     */
    public int getClickedSlot() {

        // Crafting and top inventory (crafting slots)
        if (getEvent().getView().getType() == InventoryType.CRAFTING && getEvent().getRawSlot() < getEvent().getView().getTopInventory().getSize()) {

            // Return correct
            switch (getEvent().getSlot()){
                default: return 84;
                case 1: return 80;
                case 2: return 81;
                case 3: return 82;
                case 4: return 83;
            }
        }

        // Just the usual
        return event.getSlot();

    }

    /**
     * @return Which Deployed GooP Container is being clicked
     */
    @Nullable public GOOPCDeployed getClickedDeployed() { return isClickedTopInventory() ? getDeployedContainer() : getDeployedRPG(); }
}
