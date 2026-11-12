package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMInteger;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSInteger;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSLevelled extends CSMSInteger {

    /**
     * This flag will have its corresponding {@link CSMInteger} as
     * the amount of levelling it must be leveled by.
     */
    public CSMSLevelled() { super("LEVEL"); }

    @Override
    @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMInteger meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Levelled)) { return block; }

        // Make sure it is valid
        if (((Levelled) input).getMaximumLevel() > meta.getValue()) { return block; }

        // Set the block to the expected age
        ((Levelled) input).setLevel(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMInteger fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Levelled)) { return null; }

        // Return data, age data
        return new CSMInteger((((Levelled) input).getLevel()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMInteger meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof Levelled)) { return false; }

        // All right, same age?
        return ((Levelled) input).getLevel() == meta.getValue();
    }
}