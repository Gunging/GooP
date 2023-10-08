package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CSMSMultipleFacing extends CSMSRelativeOrientation {

    /**
     * This flag will have its corresponding {@link CSMRelativeOrientation}
     * for the directions this is facing or whatever yeah.
     */
    public CSMSMultipleFacing() { super("FACING"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMRelativeOrientation meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof MultipleFacing)) { return block; }

        // All right what block face we trying to get
        ArrayList<BlockFace> expectedDirections = new ArrayList<>();

        // Ideally there is only one orientation registered
        for (RelativeOrientations orientation : meta.getValue()) { expectedDirections.add(transform(orientation, inRelativeTo)); }

        // Every allowed face
        for (BlockFace allowedFace : ((MultipleFacing) input).getAllowedFaces()) {

            // Set that face if the list contains it
            ((MultipleFacing) input).setFace(allowedFace, expectedDirections.contains(allowedFace));
        }

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMRelativeOrientation fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof MultipleFacing)) { return null; }

        // Build array
        ArrayList<RelativeOrientations> orientations = new ArrayList<>();

        // For each block facing direction, transform it.
        for (BlockFace facing : ((MultipleFacing) input).getFaces()) {

            // Add it transformed
            orientations.add(transform(facing, inRelativeTo));
        }

        // Return data, where 'true' means its the bottom half.
        return new CSMRelativeOrientation(orientations);
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMRelativeOrientation meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof MultipleFacing)) { return false; }

        // Build array
        ArrayList<RelativeOrientations> contained = new ArrayList<>();

        // For each block facing direction, transform it.
        for (BlockFace facing : ((MultipleFacing) input).getFaces()) {

            // Add it transformed
            contained.add(transform(facing, inRelativeTo));
        }

        // All must be contained
        for (RelativeOrientations expectedOrientation : meta.getValue()) {

            // All orientations expected by the meta
            if (expectedOrientation == null) { continue; }

            // Not contained by the block? Fail
            if (!contained.contains(expectedOrientation)) { return false; } }

        // All must be contained
        for (RelativeOrientations observedOrientation : contained) {

            // All orientations observed in the block
            if (observedOrientation == null) { continue; }

            // Not expected by the meta? Fail
            if (!meta.getValue().contains(observedOrientation)) { return false; } }

        // All the observed were expected, and all the expected were observed.
        return true;
    }
}
