package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMSlab;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSSlab extends CSMetaSource<CSMSlab> {

    /**
     * This flag will have its corresponding {@link CSMSlab}
     * as the type of slab it encodes for.
     */
    public CSMSSlab() { super("SLAB"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMSlab meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Slab)) { return block; }

        // Set the block data as the bisected half I guess
        ((Slab) input).setType(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMSlab fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Slab)) { return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMSlab((((Slab) input).getType()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMSlab meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof Slab)) { return false; }

        // All right, well the types must match
        return meta.getValue() == ((Slab) input).getType();
    }

    @Override
    public @Nullable CSMSlab fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // No boolean no service
        try {

            // Parse
            Slab.Type slabType = Slab.Type.valueOf(serialized.toUpperCase());

            // Yes
            return new CSMSlab(slabType);

            // Not an orientation I sleep
        } catch (IllegalArgumentException ignored) { return null; }
    }

    @Override
    public @NotNull String toString(@NotNull CSMSlab serializable) {

        // Um yeah just that
        return String.valueOf(serializable.getValue());
    }
}
