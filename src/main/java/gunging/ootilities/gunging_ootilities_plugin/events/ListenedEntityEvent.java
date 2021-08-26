package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.misc.ListenedEntity;
import gunging.ootilities.gunging_ootilities_plugin.misc.ListenedEntityReasons;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import java.util.ArrayList;

public class ListenedEntityEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    // The thing itself
    ListenedEntity lstndEntity;
    ListenedEntityReasons reasonOfThis;
    Location eventLocation;

    public ListenedEntityEvent(ListenedEntity lEntity, ListenedEntityReasons reason, Location loc) {
        super(lEntity.getEntity());
        reasonOfThis = reason;
        lstndEntity = lEntity;
        eventLocation = loc;
    }

    public ArrayList<ListenedEntityReasons> getRemainingReasons() {
        return lstndEntity.getReasons();
    }
    public ArrayList<String> getObjectives() { return lstndEntity.getObjectives(); }
    public ListenedEntityReasons getEventReason() { return reasonOfThis; }
    public ListenedEntity getListenedEntity() { return lstndEntity; }
    public Location getLocation() { return eventLocation; }

    @Override
    public Entity getEntity() {
        return (Entity) lstndEntity.getEntity();
    }
}
