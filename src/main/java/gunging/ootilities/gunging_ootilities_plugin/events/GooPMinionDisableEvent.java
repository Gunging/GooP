package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class GooPMinionDisableEvent extends GooPMinionEvent {
    /**
     * Event called after the minion has been disabled and removed
     * from the minion system completely.
     *
     * @param info All the minion information
     */
    public GooPMinionDisableEvent(@NotNull SummonerClassMinion info) { super(info); }
}
