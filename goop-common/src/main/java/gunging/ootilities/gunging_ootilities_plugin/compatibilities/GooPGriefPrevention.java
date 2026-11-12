package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class GooPGriefPrevention {

    @Nullable static DataStore claimsMarket = null;
    public static boolean claimsMarketExisted() { return claimsMarket != null; }

    public GooPGriefPrevention() { int p = GriefPrevention.TREE_RADIUS; }

    static void CheckMarket() {
        if (claimsMarket == null) { if (GriefPrevention.instance != null) { claimsMarket = GriefPrevention.instance.dataStore; } }
    }

    /**
     * Checks if that location is protected from changes made by <code>whom</code>
     */
    public static boolean IsProtectedAgainst(@NotNull Location place, @NotNull Player whom) {

        try {

            CheckMarket();

            if (claimsMarket == null) { return false; }

            // Get data
            PlayerData pd = claimsMarket.getPlayerData(whom.getUniqueId());

            Claim toClaim = claimsMarket.getClaimAt(place, false, pd.lastClaim);

            // No claim? Not protected
            if (toClaim == null) { return false; }
            pd.lastClaim = toClaim;

            // If this method returns null, apparently this guy is allowed to build so the result is the opposite.
            return toClaim.allowBuild(whom, Material.AIR) != null;

        } catch (Throwable ignored) { return false; }
    }

    /**
     * Checks if that location is protected from changes made by <code>whom</code>
     */
    public static boolean IsMemberOfClaimAt(@NotNull Location place, @NotNull Player whom) {

        CheckMarket(); if (claimsMarket == null) { return false; }

        // Get data
        PlayerData pd = claimsMarket.getPlayerData(whom.getUniqueId());

        Claim toClaim = claimsMarket.getClaimAt(place, false, pd.lastClaim);

        // No claim? Not a member either
        if (toClaim == null) { return false; }
        pd.lastClaim = toClaim;

        // If this method returns null, that means this player is allowed to build so they must be memberz
        return toClaim.allowBuild(whom, Material.AIR) == null;
    }
}
