package gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A player participant of the GooP Unlockables system
 */
public class GOOPUCKTPlayer implements GooPUnlockableTarget {

    /**
     * Player represented by this Unlockable Target
     */
    public @NotNull Player getPlayer() { return player; }
    @NotNull Player player;

    /**
     * @param player Player represented by this Unlockable Target
     */
    public GOOPUCKTPlayer(@NotNull Player player) {this.player = player;}

    @Override @NotNull public UUID getUniqueId() {return player.getUniqueId(); }

    @Override @NotNull public String getName() {return "Player \u00a73" + player.getName();}
}
