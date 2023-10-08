package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMInstrument;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.Instrument;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSNoteBlockInstrument  extends CSMetaSource<CSMInstrument> {

    /**
     * This flag will have its corresponding
     * {@link CSMInstrument} as the note it encodes for.
     */
    public CSMSNoteBlockInstrument() { super("INSTRUMENT"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMInstrument meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof NoteBlock)) { return block; }

        // Set the block data as the bisected half I guess
        ((NoteBlock) input).setInstrument(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMInstrument fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof NoteBlock)) { return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMInstrument((((NoteBlock) input).getInstrument()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMInstrument meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof NoteBlock)) { return false; }

        // All right, well the types must match
        return meta.getValue().equals(((NoteBlock) input).getInstrument());
    }

    @Override
    public @Nullable CSMInstrument fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // No boolean no service
        try {

            // Parse
            Instrument instrument = Instrument.valueOf(serialized);

            // Yes
            return new CSMInstrument(instrument);

            // Not an orientation I sleep
        } catch (IllegalArgumentException ignored) { return null; }
    }

    @Override
    public @NotNull String toString(@NotNull CSMInstrument serializable) {

        // Um yeah just that
        return String.valueOf(serializable.getValue());
    }
}