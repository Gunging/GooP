package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Basically stores the times a player punches a block.
 *
 */
public class BlockHitRememberances {
    public Player playerReference;
    public Block blockReference;
    public OptimizedTimeFormat originTimeReference;
    ArrayList<OptimizedTimeFormat> punchesTimestamp = new ArrayList<>();
    int registeredClicks;

    public int GetTotalPunches() { return registeredClicks; }
    static HashMap<Player, HashMap<Block, BlockHitRememberances>> global = new HashMap<>();
    public static BlockHitRememberances GetFrom(Player p, Block b) {

        // Does the player have a place?
        global.computeIfAbsent(p, k -> new HashMap<>());

        // Get Child
        HashMap<Block, BlockHitRememberances> perPlayer = global.get(p);

        // Return contained
        return perPlayer.get(b);
    }

    public static BlockHitRememberances GetOrCreate(Player player, Block block) {

        // Try to get existing
        BlockHitRememberances bhr = GetFrom(player, block);

        // Return if existed
        if (bhr != null) { return bhr; }

        // Agh I guess create new
        bhr = new BlockHitRememberances(player, block);

        // Load
        HashMap<Block, BlockHitRememberances> perPlayer = global.get(player);
        perPlayer.put(block, bhr);

        // Return
        return bhr;
    }

    public BlockHitRememberances(Player player, Block block) {
        // Basic
        playerReference = player;
        blockReference = block;

        // Origin
        originTimeReference = OptimizedTimeFormat.Current();
    }

    public void ResetIfAged(int secondsAge) {

        // If a lot of time has happened
        if (OotilityCeption.SecondsElapsedSince(originTimeReference, OptimizedTimeFormat.Current()) >= secondsAge) {

            // Find currentmost punch
            for (int p = 0; p < punchesTimestamp.size(); p++) {

                // Is it within time?
                if (OotilityCeption.SecondsElapsedSince(punchesTimestamp.get(p), OptimizedTimeFormat.Current()) < secondsAge) {

                    // This is now the origin
                    originTimeReference = punchesTimestamp.get(p);

                    // Break
                    break;

                } else {

                    // Remove
                    punchesTimestamp.remove(p);

                    // Reset index
                    p--;

                    // Substract a punch
                    registeredClicks--;
                }

                // If it reached this far, that means all previous punches are beyond the timestamp.
                originTimeReference = OptimizedTimeFormat.Current();
            }
        }
    }

    public void Punch() { registeredClicks++; punchesTimestamp.add(OptimizedTimeFormat.Current()); }

    /**
     * Gets the total Punches Per Second
     */
    public double GetPPS() {
        return (registeredClicks + 0.0D) / OotilityCeption.SecondsElapsedSince(originTimeReference, OptimizedTimeFormat.Current());
    }

    public boolean PPSExceed(double punchesPerSecond) { return GetPPS() >= punchesPerSecond;}
    public boolean MinimumPunches(int punches) { return registeredClicks >= punches; }
}
