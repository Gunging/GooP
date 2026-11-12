package gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GOOPUCKTServer implements GooPUnlockableTarget {
    @NotNull public static final UUID zeroUUID = UUID.fromString("a4f6f6b1-63d4-475e-bb0c-347d0423a379");

    @Override public @NotNull UUID getUniqueId() { return zeroUUID; }

    @Override public @NotNull String getName() {return "\u00a73Server";}
}
