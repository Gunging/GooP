package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.ListenedEntityEvent;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListenedEntity extends BukkitRunnable {

    public static ArrayList<ListenedEntity> observedEntities = new ArrayList<>();
    static HashMap<Entity, ListenedEntity> sources = new HashMap<>();
    public static boolean running = false;

    Entity listenedEntity;
    public Entity getEntity() { return listenedEntity; }
    public Entity GetEntity() { return listenedEntity; }

    ArrayList<ListenedEntityReasons> reasons = new ArrayList<>();
    public ArrayList<ListenedEntityReasons> getReasons() { return reasons; }
    public ArrayList<ListenedEntityReasons> GetReasons() { return reasons; }
    public boolean HasReason(ListenedEntityReasons reason) { return reasons.contains(reason); }

    ArrayList<String> objectives = new ArrayList<>();
    public ArrayList<String> getObjectives() { return objectives; }
    public ArrayList<String> GetObjectives() { return objectives; }
    public boolean HasObjective(String objective) { return objectives.contains(objective); }

    public ListenedEntity() { running = true; }
    public ListenedEntity(Entity lEntity) {
        listenedEntity = lEntity;
    }

    public ListenedEntity addReason(ListenedEntityReasons reason) {
        if (!HasReason(reason)) { reasons.add(reason); }
        return this;
    }

    public ListenedEntity addObjective(String objective) {
        if (!HasObjective(objective)) { objectives.add(objective); }
        return this;
    }

    public static ListenedEntity addReasonTo(Entity target, ListenedEntityReasons reason) { return Get(target).addReason(reason); }
    public static ListenedEntity Get(Entity whose) { return sources.get(whose); }

    public void Enable() {
        if (sources.containsKey(listenedEntity)) {

            return;
        } else {

            sources.put(listenedEntity, this);
            observedEntities.add(this);

            if (!running) {
                BukkitTask task = new ListenedEntity().runTaskTimer(Gunging_Ootilities_Plugin.theMain.getPlugin(), 4, 2);
            }
        }
    }

    @Override
    public void run() {

        if (observedEntities.size() == 0) {

            // Cancel when there is none of these running
            running = false;
            this.cancel();

        // Well make sure all of them are doing what they're supposed 2
        } else {

            // For every tracked item
            for (int i = 0; i < observedEntities.size(); i++) {

                if (i < observedEntities.size() && i >= 0) {
                    ListenedEntity lEntity = observedEntities.get(i);

                    for (int v = 0; v < lEntity.reasons.size(); v++) {

                        if (v < lEntity.reasons.size() && v >= 0) {
                            ListenedEntityReasons reason = lEntity.reasons.get(v);

                            // No entity, no listening
                            if (lEntity.getEntity() != null) {
                                switch (reason) {
                                    case UponDissapeareance:
                                        // The immediate tick after rocket disappearing basically
                                        if (!lEntity.getEntity().isValid() || lEntity.getEntity().isDead()) {

                                            // Call Event
                                            ListenedEntityEvent evnt = new ListenedEntityEvent(lEntity, ListenedEntityReasons.UponDissapeareance, lEntity.getEntity().getLocation());
                                            Bukkit.getPluginManager().callEvent(evnt);

                                            // Remove & Repeat index
                                            lEntity.reasons.remove(reason);
                                            i--;
                                        }
                                        break;
                                    case UponLanding:
                                        // The immediate tick after hitting the ground
                                        if (lEntity.getEntity().isOnGround()) {

                                            // Call Event
                                            ListenedEntityEvent evnt = new ListenedEntityEvent(lEntity, ListenedEntityReasons.UponLanding, lEntity.getEntity().getLocation());
                                            Bukkit.getPluginManager().callEvent(evnt);

                                            // Remove & Repeat index
                                            lEntity.reasons.remove(reason);
                                            i--;
                                        }
                                        break;
                                }

                            } else {

                                // Clear reasons of null entities
                                lEntity.reasons = new ArrayList<>();
                            }
                        }
                    }

                    // If it has no more reasons left
                    if (lEntity.reasons.size() == 0) {

                        // Remove & Repeat index
                        observedEntities.remove(lEntity);
                        i--;
                    }
                }
            }
        }
    }
}
