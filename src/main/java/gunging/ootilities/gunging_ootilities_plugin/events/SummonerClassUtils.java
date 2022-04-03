package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.GTL_SummonerClass;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation.GTranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SummonerClassUtils extends BukkitRunnable implements Listener {

    /**
     * Links a summoner's UUID to all the minions it owns.
     * <p></p>
     * When an owner dies or leaves the server, all its minions despawn.
     */
    static HashMap<UUID, ArrayList<SummonerClassMinion>> minionship = new HashMap<>();

    /**
     * Links a minion's UUID to the minion it is itself.
     */
    static HashMap<UUID, SummonerClassMinion> selfship = new HashMap<>();

    /**
     * Enables a Minion. Minions not yet enabled will not teleport to their
     * owner nor be removed when the owner dies/leaves.
     * <p></p>
     * Attempting to enable a minion whose owner is dead/offline will
     * result in the minion getting destroyed.
     * <p>Will fail if the minion is already enabled.</p>
     */
    public static void EnableMinion(@NotNull SummonerClassMinion min) {

        // If minion is invalid, this is cancelled
        if (!min.isMinionValid()) { return; }

        // Cancel if already enabled
        if (min.isEnabled()) { return; }

        // Owner not ok?
        if (!min.isOwnerValid()) {

            // Lmao kill minion
            min.Kill();

        // Alr so owner exists
        } else {

            // Just add to minionship ig
            min.SCM_Enable();
            AddEnabledMinionTo(min);
        }
    }

    /**
     * Will add such a minion to such owner, restricting to maximum count.
     */
    public static void AddEnabledMinionTo(@Nullable SummonerClassMinion minion) {

        // Cancels if anything is not right
        if (minion == null) { return; }
        if (!minion.isOwnerValid() || !minion.isMinionValid()) { return; }
        if (!minion.isEnabled()) { return; }

        // All right proceed.
        ArrayList<SummonerClassMinion> minions = GetMinionsOf(minion.getOwner());

        // Just add. Max minion count is not currently supported.
        minions.add(minion);

        // Register
        minionship.put(minion.getOwner().getUniqueId(), minions);
        selfship.put(minion.getMinion().getUniqueId(), minion);

        // Check minion count >;) brUH
        CheckMinionCount(minion.getOwner().getUniqueId());
    }

    /**
     * A list of all entities registered as a minion of this guy.
     */
    @NotNull
    public static ArrayList<SummonerClassMinion> GetMinionsOf(@NotNull Entity owner) {

        // Find minions
        ArrayList<SummonerClassMinion> minions = minionship.get(owner.getUniqueId());
        if (minions == null) { minions = new ArrayList<>(); }

        // Return thay
        return minions;
    }

    /**
     * Disables a Minion from the leash ticks and such.
     * <p></p>
     * <b>Will not kill the entity. It is also automatically called when they are killed.</b>
     */
    public static void DisableMinion(@NotNull SummonerClassMinion min) {

        // Just remove from minionship ig
        min.SCM_Disable();

        // Clear selflink
        selfship.remove(min.getMinion().getUniqueId());

        // Remove from minionship
        minionship.get(min.getOwner().getUniqueId()).remove(min);

        // Call event
        GooPMinionDisableEvent event = new GooPMinionDisableEvent(min);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Removes all minions
     */
    public static void RemoveAllMinions() {

        // For every registered owner
        for (UUID own : minionship.keySet()) {

            // Remove all
            RemoveAllMinionsOf(own);
        }
    }

    /**
     * Removes all the minions associated to this UUID
     */
    public static void RemoveAllMinionsOf(@Nullable UUID owner) {

        // If owner ever had any minions
        if (minionship.get(owner) == null) { return; }

        // For lup through all
        for (int o = 0; o < minionship.get(owner).size();) {

            // Destroy lma0
            minionship.get(owner).get(o).Kill();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnPlayerLogout(PlayerQuitEvent event) {

        // Kill all their minions
        RemoveAllMinionsOf(event.getPlayer().getUniqueId());
    }

    /** @noinspection ConstantConditions*/
    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerDeath(PlayerDeathEvent event) {

        // Cancel event
        if (event instanceof Cancellable) { if (((Cancellable) event).isCancelled()) { return; } }

        // Kill their minions
        RemoveAllMinionsOf(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnMinionDeath(EntityDeathEvent event) {

        // Kancel
        boolean isCancelled = false;
        if (Gunging_Ootilities_Plugin.asPaperSpigot) { isCancelled = event.isCancelled(); }

        // Did they die?
        if (!isCancelled) {

            // Is it a minion?
            if (event.getEntity() instanceof Player) { return; }

            // Remove minions of it
            RemoveAllMinionsOf(event.getEntity().getUniqueId());

            // Get minion
            SummonerClassMinion minion = GetMinion(event.getEntity());
            if (minion == null) { return; }

            // Message
            if (minion.getOwner() instanceof Player) {

                // Message
                String message = GTranslationManager.getSummonerTranslation(GTL_SummonerClass.MINION_DEATH).replace("%minion_uuid%", String.valueOf(minion.getMinion().getUniqueId())).replace("%minion_name%", (minion.getMinion().getCustomName() != null ? minion.getMinion().getCustomName() : minion.getMinion().getName()));

                // Set to space to skip
                if (message.length() > 0 && !message.equals(" ")) {

                    message = OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(message, minion.getOwner(), (Player) minion.getOwner(), null, null));
                    minion.getOwner().sendMessage(message); }
            }

            // They've died lmao, unregister them I guess.
            DisableMinion(minion);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void OnMinionPvP(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) { return; }
        if (!(event.getEntity() instanceof LivingEntity)) { return; }

        // The receiver isn't a minion? Not our business
        SummonerClassMinion minion = GetMinion(event.getEntity());
        if (minion == null) { return; }

        if (!minion.isPreventPlayerDamage()) { return; }

        //region Get True Damager
        Entity trueDamager = event.getDamager();
        if (event.getDamager() instanceof Projectile) {

            // If shooter is not null
            Projectile arrow = (Projectile) trueDamager;
            if (arrow.getShooter() instanceof Entity) {

                // Real damager is the one who fired this
                trueDamager = (Entity) arrow.getShooter();
            }
        }
        if (event.getDamager() instanceof Firework) {

            // If shooter is not null
            Firework arrow = (Firework) event.getDamager();
            if (XBow_Rockets.fireworkSources.containsKey(arrow.getUniqueId())) {

                // Real damager is the one who fired this
                trueDamager = XBow_Rockets.fireworkSources.get(arrow.getUniqueId());
            }
        }
        //endregion

        // The damager isn't a player? Not our business.
        if (!(trueDamager instanceof Player)) { return; }

        // Players cannot deal damage to minions with PvP Block
        event.setCancelled(true);
    }

    @Override
    public void run() {

        // Run SYNC bruh alv
        LeashEffect();
    }

    /**
     * Teleports all minions to their owners if they exceed their leash ranges.
     * <p></p>
     * Runs every 10 seconds, also cleans up the arrays, removing dead minions and such.
     */
    public static void LeashEffect() {

        // Extract UUIDS
        ArrayList<UUID> owners = new ArrayList<>(minionship.keySet());

        // For every minion AHH
        for (UUID owner : owners) {

            // Get array
            ArrayList<SummonerClassMinion> cleaned = CheckMinionCount(owner);

            // Get owner
            Entity summoner = null;
            Location sLoc = null;

            // Apply leash ranges
            for (int m = 0; m < cleaned.size(); m++) {

                // Get at
                SummonerClassMinion minion = cleaned.get(m);

                // Actually get owner
                if (summoner == null) {

                    // Acquire summoner specifications
                    summoner = minion.getOwner();
                    sLoc = summoner.getLocation();
                }

                // Get self
                Entity min = minion.getMinion();

                // Get Locs
                Location mLoc = min.getLocation();

                // Same world?
                if (sLoc.getWorld() == mLoc.getWorld()) {

                    // Measure distance
                    double dist = sLoc.distance(mLoc);

                    // Greater than leash range?
                    if (dist > minion.getLeashRange()) {

                        // Greater than kill range?
                        if (Gunging_Ootilities_Plugin.summonKillDistance != null && dist > Gunging_Ootilities_Plugin.summonKillDistance) {

                            // Destroy
                            minion.Kill();

                            // Repeat Index
                            m--;

                        } else {

                            // Teleport
                            min.teleport(sLoc);
                        }
                    }

                // Different world, teleport.
                } else {

                    // Kill range defined?
                    if (Gunging_Ootilities_Plugin.summonKillDistance != null) {

                        // Destroy
                        minion.Kill();

                        // Repeat Index
                        m--;

                    } else {

                        // Teleport
                        min.teleport(sLoc);
                    }
                }
            }
        }
    }

    /**
     * Checks and constrains minion count of this owner.
     */
    @NotNull public static ArrayList<SummonerClassMinion> CheckMinionCount(@NotNull UUID owner) {

        // Get array
        ArrayList<SummonerClassMinion> subs = minionship.get(owner);
        ArrayList<SummonerClassMinion> cleaned = new ArrayList<>();

        // Return if null
        if (subs == null) { return cleaned; }
        if (subs.size() <= 0) { return cleaned; }

        // Get owner
        double maxSummons = GetMaxMinions(subs.get(0).getOwner());
        HashMap<String, Integer> kindCount = new HashMap<>();

        // Clean up the minions
        for (int m = subs.size() - 1; m >= 0; m--) {

            // Get observed
            SummonerClassMinion minion = subs.get(m);
            boolean limitFailure = false;

            // Kind check
            if (minion.hasKind()) {

                // Check Kind
                Integer currentKindCount = kindCount.get(minion.getKind());
                if (currentKindCount == null) { currentKindCount = 0; }
                currentKindCount++;

                // Check Limit
                if (minion.hasLimit()) {

                    // Check Limit
                    if (currentKindCount > minion.getLimit()) {

                        // Cancel this
                        limitFailure = true;
                    }
                }

                // If it did not surpass the limit, this minion is actually counted thus
                if (!limitFailure) {

                    // Well its added
                    kindCount.put(minion.getKind(), currentKindCount);
                }
            }

            // Summon weight must fit this minion's weight
            boolean weightFailure = (maxSummons - minion.getWeight()) < 0;

            // Owner valid? If not then kill (They should have been killed already tho but anyway)
            if (!weightFailure && !limitFailure && minion.isOwnerValid() && minion.isMinionValid()) {

                // Add and count
                maxSummons -= minion.getWeight();
                cleaned.add(minion);

            // If still enabled
            } else if (minion.isEnabled()) {

                // Message
                if (minion.getWeight() > 0 && (minion.getOwner() instanceof Player)) {

                    // Message
                    String message = GTranslationManager.getSummonerTranslation(GTL_SummonerClass.MINION_EXCEED_CAP).replace("%minion_uuid%", String.valueOf(minion.getMinion().getUniqueId())).replace("%minion_name%", (minion.getMinion().getCustomName() != null ? minion.getMinion().getCustomName() : minion.getMinion().getName()));

                    // Set to space to skip
                    if (message.length() > 0 && !message.equals(" ")) {
                        message = OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(message, minion.getOwner(), (Player) minion.getOwner(), null, null));
                        minion.getOwner().sendMessage(message); }
                }
                // Kill and forget about it
                if (minion.isMinionValid()) { minion.Kill(); } else { DisableMinion(minion); }
            }
        }

        // Save
        minionship.put(owner, cleaned);
        return cleaned;
    }

    /**
     * Tells you how many minions this guy can support.
     * <p> Monsters will return a value of 32767
     * </p> Players will return their max number of minions (by default, 1).
     */
    public static double GetMaxMinions(@NotNull Entity summoner) {

        // Summons
        double maxSummons = 32767;

        // Is it player?
        if (summoner instanceof Player) {

            // Is MMOItems enabled?
            if (Gunging_Ootilities_Plugin.foundMMOItems) {

                // Get Cumulative Minions
                maxSummons = GooPMMOItems.CummaltiveEquipmentDoubleStatValue((Player) summoner, GooPMMOItems.MINIONS, 1.0);
            }
        }

        // Return thay
        return maxSummons;
    }

    /**
     * Gets the number of minions associated to this ID.
     */
    public static double GetCurrentMinionCount(@NotNull UUID owner) {

        // Get
        ArrayList<SummonerClassMinion> subs = minionship.get(owner);

        // Return if null
        if (subs == null) { return 0; }

        // Wells thats all.
        double currentCount = 0D;
        for (SummonerClassMinion sub : subs) { currentCount += sub.getWeight(); }

        // YEs
        return currentCount;
    }

    /**
     * Gets the owner of this minion if:
     * <P>* This is actually a minion
     * </P>* This minion is valid and the owner exists
     */
    @Nullable
    public static Entity GetOwner(@Nullable Entity owner) {

        // Bruh
        if (owner == null) { return null; }

        // Return thay
        return GetOwner(owner.getUniqueId());
    }

    /**
     * Gets the owner of a minion of such UUID if:
     * <P>* This is actually a minion
     * </P>* This minion is valid and the owner exists
     */
    @Nullable
    public static Entity GetOwner(@Nullable UUID ownerUUID) {

        // Bruh
        if (ownerUUID == null) { return null; }

        // Get minion?
        SummonerClassMinion min = GetMinion(ownerUUID);

        if (min != null) {

            // Valid?
            if (min.isOwnerValid() && min.isMinionValid()) {

                // Ret
                return min.getOwner();
            }
        }

        // Nope
        return null;
    }

    /**
     * Finds the <code>SummonerClassMinion</code> instance of this entity, if there is one.
     */
    @Nullable
    public static SummonerClassMinion GetMinion(@Nullable Entity potentialMinion) {

        // None if null
        if (potentialMinion == null) { return null; }

        // Gets from selfship lma0
        return GetMinion(potentialMinion.getUniqueId());
    }

    /**
     * Finds the <code>SummonerClassMinion</code> instance of this uuid, if there is one.
     */
    @Nullable
    public static SummonerClassMinion GetMinion(@Nullable UUID potentialMinion) {

        // None if null
        if (potentialMinion == null) { return null; }

        // Gets from selfship lma0
        return SummonerClassUtils.selfship.get(potentialMinion);
    }
}
