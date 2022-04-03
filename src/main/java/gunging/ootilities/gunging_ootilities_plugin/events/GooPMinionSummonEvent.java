package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class GooPMinionSummonEvent extends GooPMinionEvent implements Cancellable {

    /**
     * This event is called prior to summoning the minion, such that cancelling will
     * completely abort the minion being summoned, and changing information of the
     * minion will affect the minion as soon as it is summoned.
     *
     * @param info All the minion information
     */
    public GooPMinionSummonEvent(@NotNull SummonerClassMinion info) { super(info); }

    //region Cancellable Standard
    boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    //endregion
}
