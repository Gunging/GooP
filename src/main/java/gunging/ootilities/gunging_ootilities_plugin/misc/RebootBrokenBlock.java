package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class RebootBrokenBlock {

    /**
     * @return Block as it was when reboot broken
     */
    @NotNull public BlockState getOriginal() { return original; }
    @NotNull BlockState original;

    /**
     * @return Reboot key
     */
    @NotNull public String getRebootKey() { return rebootKey; }
    @NotNull String rebootKey;

    /**
     * @param original Block as it was when reboot broken
     * @param rebootKey Reboot key
     */
    public RebootBrokenBlock(@NotNull BlockState original, @NotNull String rebootKey) {
        this.original = original;
        this.rebootKey = rebootKey;
    }
}
