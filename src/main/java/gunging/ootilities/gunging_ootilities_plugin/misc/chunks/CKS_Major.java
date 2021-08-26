package gunging.ootilities.gunging_ootilities_plugin.misc.chunks;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Divides locations in huge areas of 2048x2048, which, assuming
 * that your array has thousands of entries spanning very long
 * distances, will divide search time by 2048.
 *
 * @author Gunging
 */
public class CKS_Major {

    Long x2048Side, x2048Forward;
    static ArrayList<CKS_Major> loadeds = new ArrayList<>();

    public CKS_Major(@NotNull Location convertible) {

        x2048Side = Math.round(Math.floor(convertible.getBlockX() / 2048.0D));
        x2048Forward = Math.round(Math.floor(convertible.getBlockZ() / 2048.0D));
    }

    public boolean Matches(long x, long z) {

        // X Matches?
        if (x == x2048Side) {

            // Z Matches?
            return z == x2048Forward;
        }

        // Noep
        return false;
    }

    /**
     * @param loc Location in world
     *
     * @return Chunk associated to this location
     */
    @NotNull public static CKS_Major GetFrom(@NotNull Location loc) {

        // Return Thay
        return GetFrom(loc.getBlockX(), loc.getBlockZ(), loc);
    }

    /**
     * @param locX World X Location
     * @param locZ World Z Location
     * @param newIfApplicable Can only return <code>null</code> if this is <code>null</code>.
     *
     * @return Minor chunk associated to this location
     */
    @Nullable public static CKS_Major GetFrom(Integer locX, Integer locZ, @Nullable Location newIfApplicable) {

        long discX = Math.round(Math.floor(locX / 2048.0D));
        long discZ = Math.round(Math.floor(locZ / 2048.0D));

        // For every one contained
        for (CKS_Major lChunk : loadeds) {

            // If it matches, return thay
            if (lChunk.Matches(discX, discZ)) {

                // Thats the one
                return lChunk;
            }
        }

        // Will create if a location was provided
        if (newIfApplicable != null) {

            // No one matched? Must be new
            CKS_Major newChunk = new CKS_Major(newIfApplicable);

            // Add and return
            loadeds.add(newChunk);

            // Return
            return newChunk;

        } else {

            // Null Return
            return null;
        }
    }
}
