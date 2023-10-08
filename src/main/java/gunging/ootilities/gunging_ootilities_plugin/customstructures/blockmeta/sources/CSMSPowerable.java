package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMBoolean;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSBoolean;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSPowerable extends CSMSBoolean {

    /**
     * This flag will have its corresponding {@link CSMBoolean} as
     * <code>true</code> if the thing is in 'powered' state.
     */
    public CSMSPowerable() { super("POWERED"); }

    @Override
    @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMBoolean meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Powerable)) { return block; }

        // Set the block data as the bisected half I guess
        ((Powerable) input).setPowered(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMBoolean fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Powerable)) { return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMBoolean((((Powerable) input).isPowered()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMBoolean meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof Powerable)) { return false; }

        // All right, well true means bottom
        return (meta.getValue() && ((Powerable) input).isPowered()) || (!meta.getValue() && !((Powerable) input).isPowered());
    }
}
