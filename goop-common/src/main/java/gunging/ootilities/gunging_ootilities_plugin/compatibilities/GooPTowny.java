package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.SummonMinionMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Keeps Towny from deleting GooP minions in towns that disable mob spawning.
 * <p>
 * Towny's events are bound by class name at runtime instead of through a compile-time
 * dependency: Towny is only published to its own repository, which an open build of GooP
 * should not need to reach, and only two of its events matter here.
 */
public class GooPTowny implements Listener {

    private static final String MOB_SPAWN_REMOVAL = "com.palmergames.bukkit.towny.event.mobs.MobSpawnRemovalEvent";
    private static final String MOB_REMOVAL = "com.palmergames.bukkit.towny.event.MobRemovalEvent";

    /** Throws if Towny (or the events this bridge needs) is not installed */
    public GooPTowny() throws ClassNotFoundException {
        Class.forName(MOB_SPAWN_REMOVAL);
        Class.forName(MOB_REMOVAL);
    }

    /** Listens to Towny's mob removal events on behalf of GooP */
    @SuppressWarnings("unchecked")
    public void register(@NotNull Plugin plugin) throws ClassNotFoundException {
        Bukkit.getPluginManager().registerEvent((Class<? extends Event>) Class.forName(MOB_SPAWN_REMOVAL), this, EventPriority.NORMAL,
                (listener, event) -> onTownyDeleteSpawningSummon(event), plugin);
        Bukkit.getPluginManager().registerEvent((Class<? extends Event>) Class.forName(MOB_REMOVAL), this, EventPriority.NORMAL,
                (listener, event) -> onTownyDeleteSummon(event), plugin);
    }

    /** A minion is spawning right where the summon mechanic is placing it: let it spawn */
    void onTownyDeleteSpawningSummon(@NotNull Event event) {
        if (!(event instanceof Cancellable)) { return; }
        if (!Gunging_Ootilities_Plugin.foundMythicMobs) { return; }

        Location spawn = SummonMinionMechanic.minionSpawn;
        Entity entity = entityOf(event);
        if (spawn == null || entity == null || spawn.getWorld() == null) { return; }
        if (!spawn.getWorld().equals(entity.getWorld())) { return; }
        if (spawn.distanceSquared(entity.getLocation()) > 25) { return; }

        ((Cancellable) event).setCancelled(true);
    }

    /** Minions owned by players are never removed by Towny */
    void onTownyDeleteSummon(@NotNull Event event) {
        if (!(event instanceof Cancellable)) { return; }
        Entity entity = entityOf(event);
        if (entity == null) { return; }

        SummonerClassMinion mn = SummonerClassUtils.GetMinion(entity);
        if (mn == null) { return; }
        if (mn.getOwner() instanceof Player) { ((Cancellable) event).setCancelled(true); }
    }

    /** Both Towny events expose the entity through {@code getEntity()} */
    @Nullable private static Entity entityOf(@NotNull Event event) {
        try {
            Object e = event.getClass().getMethod("getEntity").invoke(event);
            return e instanceof Entity ? (Entity) e : null;
        } catch (ReflectiveOperationException | LinkageError ex) {
            return null;
        }
    }
}
