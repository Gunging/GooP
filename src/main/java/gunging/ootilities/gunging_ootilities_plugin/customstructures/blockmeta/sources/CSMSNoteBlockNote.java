package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMNote;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CSMSNoteBlockNote extends CSMetaSource<CSMNote> {

    /**
     * This flag will have its corresponding
     * {@link CSMNote} as the note it encodes for.
     */
    public CSMSNoteBlockNote() { super("NOTE"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMNote meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof NoteBlock)) { return block; }

        // Set the block data as the bisected half I guess
        ((NoteBlock) input).setNote(meta.getValue());

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMNote fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof NoteBlock)) { return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMNote((((NoteBlock) input).getNote()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMNote meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof NoteBlock)) { return false; }

        // All right, well the types must match
        return meta.getValue().equals(((NoteBlock) input).getNote());
    }

    @Override
    public @Nullable CSMNote fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // No boolean no service
        try {

            // Parse
            byte bit = Byte.parseByte(serialized);

            // Yes
            return new CSMNote(new Note(bit));

            // Not an orientation I sleep
        } catch (IllegalArgumentException ignored) { return null; }
    }

    @Override
    public @NotNull String toString(@NotNull CSMNote serializable) {

        // Um yeah just that
        return String.valueOf(serializable.getValue().getId());
    }
}