package gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GOOPUCKTUnique implements GooPUnlockableTarget {

    /**
     * Player represented by this Unlockable Target
     */
    public @NotNull UUID getUUID() { return player; }
    @NotNull UUID player;

    /**
     * @param player Player represented by this Unlockable Target
     */
    public GOOPUCKTUnique(@NotNull UUID player) {this.player = player;}

    @Override @NotNull public UUID getUniqueId() {return player; }

    @Override @NotNull public String getName() {return "UUID \u00a73" + player;}
}
