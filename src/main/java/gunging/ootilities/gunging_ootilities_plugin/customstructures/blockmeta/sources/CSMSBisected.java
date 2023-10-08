package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMBoolean;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSBoolean;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSBisected extends CSMSBoolean {

    /**
     * This flag will have its corresponding {@link CSMBoolean} as
     * <code>true</code> if the block is in the bottom half.
     */
    public CSMSBisected() { super("HALF"); }

    @Override
    @NotNull public Block apply(@NotNull Block block, @NotNull CSMBoolean meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Bisected)) { return block; }

        // Set the block data as the bisected half I guess
        ((Bisected) input).setHalf(meta.getValue() ? Bisected.Half.BOTTOM : Bisected.Half.TOP);

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable public CSMBoolean fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Bisected)) { return null; }

        // Interestingly, if its open, bisected won't matter.
        if (OotilityCeption.IsTrapdoor(input.getMaterial())) {

            // Only depends on trapdoor facing direction.
            if (((Openable) input).isOpen()) { return null; } }

        // Return data, where 'true' means its the bottom half.
        return new CSMBoolean((((Bisected) input).getHalf().equals(Bisected.Half.BOTTOM)));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMBoolean meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof Bisected)) { return false; }

        // All right, well true means bottom
        return (meta.getValue() && ((Bisected) input).getHalf() == Bisected.Half.BOTTOM)
                || (!meta.getValue() && ((Bisected) input).getHalf() == Bisected.Half.TOP);
    }
}
