package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSString;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMString;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@SuppressWarnings("deprecation")
public class CSMSSign extends CSMSString {

    /**
     * @return The line number used by this sign.
     */
    @Range(from = 0, to = 3) public int getLineNumber() { return lineNumber; }
    @Range(from = 0, to = 3) final int lineNumber;

    /**
     * This flag will have its corresponding {@link CSMString}
     * as the code the respective line of the sign must have.
     */
    public CSMSSign(@Range(from = 0, to = 3) int lineNumber) { super("SIGN" + lineNumber); this.lineNumber = lineNumber; }

    @Override
    @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMString meta, @NotNull Orientations inRelativeTo) {
        BlockState input = block.getState();

        // Block data bisected??
        if (!(input instanceof Sign)) { return block; }

        // Write Line
        ((Sign) input).setLine(getLineNumber(), meta.getValue());

        // Apply
        input.update();

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMString fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockState input = block.getState();

        // Block data bisected??
        if (!(input instanceof Sign)) { return null; }

        // The value of the line at such line number
        return new CSMString(((Sign) input).getLine(getLineNumber()));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMString meta, @NotNull Orientations inRelativeTo) {
        BlockState input = block.getState();

        // Block data bisected??
        if (!(input instanceof Sign)) { return false; }

        // If the sign contains the expected text (ignore case).
        return ((Sign) input).getLine(getLineNumber()).toLowerCase().contains(meta.getValue().toLowerCase());
    }
}
