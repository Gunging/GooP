package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class GooPMinionEvent extends EntityEvent {

    /**
     * @return Entity being summoned
     */
    @NotNull
    public Entity getMinion() { return getEntity(); }

    /**
     * @return Entity summoning the minion.
     */
    @NotNull public Entity getSummoner() { return info.getOwner(); }

    /**
     * @return All the information of the minion being summoned
     */
    @NotNull public SummonerClassMinion getInfo() { return info; }
    @NotNull SummonerClassMinion info;

    /**
     * Event that has to do with a {@link SummonerClassMinion}
     *
     * @param info All the minion information
     */
    public GooPMinionEvent(@NotNull SummonerClassMinion info) {
        super(info.getMinion());
        this.info = info;
    }

    //region Event Standard
    private static final HandlerList handlers = new HandlerList();
    @NotNull @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
    //endregion
}
