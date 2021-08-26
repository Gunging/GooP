package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
     * Will add such a minion to such owner, restricting to maximum count (whenever that is implemented).
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

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerDeath(PlayerDeathEvent event) {

        // Did they die?
        if (!event.isCancelled()) {

            // Kill their minions
            RemoveAllMinionsOf(event.getEntity().getUniqueId());
        }
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

            // They've died lmao, unregister them I guess.
            DisableMinion(minion);
        }
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
            ArrayList<SummonerClassMinion> subs = minionship.get(owner);
            ArrayList<SummonerClassMinion> cleaned = new ArrayList<>();

            // Get owner
            Entity summoner = null;
            double maxSummons = 32767;

            // Clean up the minions
            for (int m = subs.size() - 1; m >= 0; m--) {
                // Get observed
                SummonerClassMinion minion = subs.get(m);

                // Actually get owner
                if (summoner == null) {

                    // Set
                    summoner = minion.getOwner();

                    // Is it player?
                    if (summoner instanceof Player) {

                        // Is MMOItems enabled?
                        if (Gunging_Ootilities_Plugin.foundMMOItems) {

                            // Get Cummulative Minions
                            maxSummons = GooPMMOItems.CummaltiveEquipmentDoubleStatValue((Player) summoner, GooPMMOItems.MINIONS, 1.0);
                        }
                    }
                }

                // Owner valid? If not then kill (They should have been killed already tho but anyway)
                if (minion.isMinionValid() && minion.isOwnerValid() && maxSummons > 0 && minion.isEnabled()) {

                    // Add and count
                    maxSummons -= minion.getWeight();
                    cleaned.add(minion);

                // If still enabled
                } else if (minion.isEnabled()) {

                    // Kill and forget about it
                    if (minion.isMinionValid()) { minion.Kill(); } else { DisableMinion(minion); }
                    selfship.remove(minion.getMinion().getUniqueId());
                }
            }

            // Alr now they've been cleaned
            minionship.put(owner, cleaned);

            // Apply leash ranges
            for (int m = 0; m < cleaned.size(); m++) {

                // Get at
                SummonerClassMinion minion = cleaned.get(m);

                // Get self
                Entity min = minion.getMinion();

                // Get Locs
                Location sLoc = summoner.getLocation(), mLoc = min.getLocation();

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
    public static void CheckMinionCount(@NotNull UUID owner) {

        // Get array
        ArrayList<SummonerClassMinion> subs = minionship.get(owner);

        // Return if null
        if (subs == null) { return; }
        if (subs.size() <= 0) { return; }

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
                Integer i = kindCount.get(minion.getKind());
                if (i == null) { i = 0; } i++; int ip = i;

                // Check Limit
                if (minion.hasLimit()) {

                    // Check Limit
                    if (ip > minion.getLimit()) {

                        // Cancel this
                        limitFailure = true;
                    }
                }

                // If it did not surpass the limit, this minion is actually counted thus
                if (!limitFailure) {

                    // Well its added
                    kindCount.put(minion.getKind(), ip);
                }
            }

            // Owner valid? If not then kill (They should have been killed already tho but anyway)
            if (minion.isMinionValid() && !limitFailure && minion.isOwnerValid() && maxSummons > 0 && minion.isEnabled()) {

                // Add and count
                maxSummons -= minion.getWeight();

            // If still enabled
            } else if (minion.isEnabled()) {

                // Kill and forget about it
                if (minion.isMinionValid()) { minion.Kill(); } else { DisableMinion(minion); }
                selfship.remove(minion.getMinion().getUniqueId());
            }
        }
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

                // Get Cummulative Minions
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
