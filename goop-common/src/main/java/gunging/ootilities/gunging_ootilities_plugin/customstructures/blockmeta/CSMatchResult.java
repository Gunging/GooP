package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSBlock;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Some data regarding the matching block pattern
 */
public class CSMatchResult {

    /**
     * @return The blocks in the world that constitute this structure
     */
    @NotNull public ArrayList<Block> getWorldBlocks() { return worldBlocks; }
    @NotNull ArrayList<Block> worldBlocks;

    /**
     * @return The block that the player interacted with to trigger this structure
     */
    @NotNull public Block getInteractedBlock() { return interactedBlock; }
    @NotNull Block interactedBlock;

    /**
     * @return The custom-structure block that encodes for the place of the {@link #getInteractedBlock()}
     */
    @NotNull public CSBlock getCsInteractedBlock() { return csInteractedBlock; }
    @NotNull CSBlock csInteractedBlock;

    /**
     * @return The core block of the structure, at the 0 0 0 indices
     */
    @NotNull public Block getCoreBlock() { return coreBlock; }
    @NotNull Block coreBlock;

    /**
     * @return The orientation that the structure matched
     */
    @NotNull public Orientations getOrientation() { return orientation; }
    @NotNull Orientations orientation;

    /**
     * @param worldBlocks The blocks in the world that constitute this structure
     * @param interactedBlock The block that the player interacted with to trigger this structure
     * @param csInteractedBlock The custom-structure block that encodes for the place of the {@link #getInteractedBlock()}
     * @param coreBlock The core block of the structure, at the 0 0 0 indices
     * @param orientation The orientation that the structure matched
     */
    public CSMatchResult(@NotNull ArrayList<Block> worldBlocks, @NotNull Block interactedBlock, @NotNull CSBlock csInteractedBlock, @NotNull Block coreBlock, @NotNull Orientations orientation) {
        this.worldBlocks = worldBlocks;
        this.interactedBlock = interactedBlock;
        this.csInteractedBlock = csInteractedBlock;
        this.coreBlock = coreBlock;
        this.orientation = orientation;
    }
}
