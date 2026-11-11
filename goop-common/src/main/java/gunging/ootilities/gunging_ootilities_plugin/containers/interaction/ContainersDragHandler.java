package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

public abstract class ContainersDragHandler extends ContainersInteractionHandler<InventoryDragEvent> {

    //region Constructor
    /**
     * @return The action that triggered this call
     */
    @NotNull public DragType getAction() { return action; }
    @NotNull DragType action;

    /**
     * @param action To handle this action yeah.
     */
    public ContainersDragHandler(@NotNull DragType action) { this.action = action; }
    //endregion

    /**
     * Its a drag, duh.
     *
     * @return <code>true</code>
     */
    @Override public boolean isMultiSlot() { return true; }
}
