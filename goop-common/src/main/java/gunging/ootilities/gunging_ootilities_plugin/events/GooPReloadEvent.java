package gunging.ootilities.gunging_ootilities_plugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GooPReloadEvent extends Event {

    //region Event Standard
    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    //endregion

    /**
     * Yes
     */
    public GooPReloadEvent() {};
}
