package gunging.ootilities.gunging_ootilities_plugin.misc.chunks;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Divides locations into 64x64 chunks, meant for arrays of locations
 * that may span long distances and have thousands of entries.
 *
 * Using it at this level, will divide search time by 64.
 *
 * @author Gunging
 */
public class CKS_Minor {

    Long x64Side, x64Forward, x64Vertical;
    @NotNull static ArrayList<CKS_Minor> loadeds = new ArrayList<>();

    public CKS_Minor(Location convertible) {

        x64Side = Math.round(Math.floor(convertible.getBlockX() / 64.0D));
        x64Vertical = Math.round(Math.floor(convertible.getBlockY() / 64.0D));
        x64Forward = Math.round(Math.floor(convertible.getBlockZ() / 64.0D));
    }

    public boolean Matches(long x, long y, long z) {

        // X Matches?
        if (x == x64Side) {

            // Z Matches?
            if (z == x64Forward) {

                // Y Matches?
                return y == x64Vertical;
            }
        }

        // Noep
        return false;
    }

    /**
     * @param loc Location in world
     *
     * @return Chunk associated to this location
     */
    @NotNull public static CKS_Minor GetFrom(@NotNull Location loc) {

        // Return Thay
        return GetFrom(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc);
    }

    /**
     * @param locX World X Location
     * @param locY World Y Location
     * @param locZ World Z Location
     * @param newIfApplicable Can only return <code>null</code> if this is <code>null</code>.
     *
     * @return Minor chunk associated to this location
     */
    @Nullable public static CKS_Minor GetFrom(int locX, int locY, int locZ, @Nullable Location newIfApplicable) {

        long discX = Math.round(Math.floor(locX / 64.0D));
        long discY = Math.round(Math.floor(locY / 64.0D));
        long discZ = Math.round(Math.floor(locZ / 64.0D));

        // For every one contained
        for (CKS_Minor lChunk : loadeds) {

            // If it matches, return thay
            if (lChunk.Matches(discX, discY, discZ)) {

                // Thats the one
                return lChunk;
            }
        }

        // Will create if a location was provided
        if (newIfApplicable != null) {

            // No one matched? Must be new
            CKS_Minor newChunk = new CKS_Minor(newIfApplicable);

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
