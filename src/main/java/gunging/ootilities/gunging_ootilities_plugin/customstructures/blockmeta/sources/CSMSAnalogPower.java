package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMInteger;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSInteger;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSAnalogPower extends CSMSInteger {

    /**
     * This flag will have its corresponding {@link CSMInteger} as
     * the amount of power it must be powered by.
     */
    public CSMSAnalogPower() { super("ANALOG"); }

    @Override
    @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMInteger meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof AnaloguePowerable)) { return block; }

        // Make sure it is valid
        if (((AnaloguePowerable) input).getMaximumPower() > meta.getValue()) { return block; }

        // Set the block to the expected age
        ((AnaloguePowerable) input).setPower(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMInteger fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof AnaloguePowerable)) { return null; }

        // Return data, age data
        return new CSMInteger((((AnaloguePowerable) input).getPower()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMInteger meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof AnaloguePowerable)) { return false; }

        // All right, same age?
        return ((AnaloguePowerable) input).getPower() == meta.getValue();
    }
}