package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMFaceAttachable;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSFaceAttachable extends CSMetaSource<CSMFaceAttachable> {

    /**
     * This flag will have its corresponding {@link CSMFaceAttachable}
     * as the type of slab it encodes for.
     */
    public CSMSFaceAttachable() { super("ATTACH"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMFaceAttachable meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof FaceAttachable)) { return block; }

        // Set the block data as the bisected half I guess
        ((FaceAttachable) input).setAttachedFace(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMFaceAttachable fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof FaceAttachable)) { return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMFaceAttachable((((FaceAttachable) input).getAttachedFace()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMFaceAttachable meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof FaceAttachable)) { return false; }

        // All right, well the types must match
        return meta.getValue() == ((FaceAttachable) input).getAttachedFace();
    }

    @Override
    public @Nullable CSMFaceAttachable fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // No boolean no service
        try {

            // Parse
            FaceAttachable.AttachedFace slabType = FaceAttachable.AttachedFace.valueOf(serialized.toUpperCase());

            // Yes
            return new CSMFaceAttachable(slabType);

            // Not an orientation I sleep
        } catch (IllegalArgumentException ignored) { return null; }
    }

    @Override
    public @NotNull String toString(@NotNull CSMFaceAttachable serializable) {

        // Um yeah just that
        return String.valueOf(serializable.getValue());
    }
}