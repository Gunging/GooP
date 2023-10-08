package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSDirectional extends CSMSRelativeOrientation {

    /**
     * This flag will have its corresponding {@link CSMRelativeOrientation}
     * for the direction this is facing or whatever yeah.
     */
    public CSMSDirectional() { super("DIRECTIONAL"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMRelativeOrientation meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Directional)) { return block; }

        // All right what block face we trying to get
        BlockFace direction = BlockFace.SOUTH;

        // Ideally there is only one orientation registered
        for (RelativeOrientations orientation : meta.getValue()) { direction = transform(orientation, inRelativeTo); }

        // Set that
        ((Directional) input).setFacing(direction);

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMRelativeOrientation fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Directional)) { return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMRelativeOrientation(transform(((Directional) input).getFacing(), inRelativeTo));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMRelativeOrientation meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof Directional)) { return false; }

        // Orient
        RelativeOrientations contained = transform(((Directional) input).getFacing(), inRelativeTo);

        // All must be contained
        for (RelativeOrientations expectedOrientation : meta.getValue()) {

            // All orientations expected by the meta
            if (expectedOrientation == null) { continue; }

            // Not contained by the block? Fail
            if (contained != expectedOrientation) { return false; } }

        // Valid if the meta indeed expected it (it may not be expected, and thus this will fail).
        return meta.getValue().contains(contained);
    }
}
