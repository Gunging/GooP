package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMInteger;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSInteger;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSAgeable extends CSMSInteger {

    /**
     * This flag will have its corresponding {@link CSMInteger} as
     * the amount of age it must have to match
     */
    public CSMSAgeable() { super("AGE"); }

    @Override
    @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMInteger meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Ageable)) { return block; }

        // Make sure it is valid
        if (((Ageable) input).getMaximumAge() > meta.getValue()) { return block; }

        // Set the block to the expected age
        ((Ageable) input).setAge(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMInteger fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Ageable)) { return null; }

        // Return data, age data
        return new CSMInteger((((Ageable) input).getAge()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMInteger meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof Ageable)) { return false; }

        // All right, same age?
        return ((Ageable) input).getAge() == meta.getValue();
    }
}