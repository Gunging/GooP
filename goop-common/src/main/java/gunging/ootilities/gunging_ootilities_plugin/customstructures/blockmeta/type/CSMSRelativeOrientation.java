package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CSMSRelativeOrientation extends CSMetaSource<CSMRelativeOrientation> {

    public CSMSRelativeOrientation(@NotNull String internalName) { super(internalName); }

    @Override
    public @Nullable CSMRelativeOrientation fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // Split by spaces
        ArrayList<String> split = new ArrayList<>();
        if (serialized.contains(" ")) { split.addAll(Arrays.asList(serialized.split(" "))); } else { split.add(serialized); }

        // Build arraylist
        ArrayList<RelativeOrientations> orientations = new ArrayList<>();

        // Yes
        for (String orientation : split) {

            // No boolean no service
            try {

                // Parse
                RelativeOrientations parsed = RelativeOrientations.valueOf(orientation.toUpperCase());

                // Yes
                orientations.add(parsed);

                // Not an orientation I sleep
            } catch (IllegalArgumentException ignored) { return null; }
        }

        // Yeah
        return new CSMRelativeOrientation(orientations);
    }

    @Override
    public @NotNull String toString(@NotNull CSMRelativeOrientation serializable) {

        // Build string
        StringBuilder builder = new StringBuilder();
        boolean separated = false;

        // Add those
        for (RelativeOrientations orientation : serializable.getValue()) {
            if (orientation == null) { continue; }

            // Space
            if (separated) { builder.append(" "); } else { separated = true; }

            // Add
            builder.append(orientation.toString());
        }

        // Um yeah just that
        return builder.toString();
    }

    /**
     * @param facing The direction being faced by the individual block, absolute with the world.
     *
     * @param inRelativeTo The direction being faced by the core, absolute with the world.
     *
     * @return The direction faced by the individual block, relative to the core.
     */
    @NotNull public static RelativeOrientations transform(@NotNull BlockFace facing, @NotNull Orientations inRelativeTo) {

        // Orientate
        switch (inRelativeTo) {
            case NorthForward:
                if (facing == BlockFace.NORTH) { return RelativeOrientations.FORWARD; } else
                if (facing == BlockFace.SOUTH) { return RelativeOrientations.BACK; } else
                if (facing == BlockFace.EAST) { return RelativeOrientations.RIGHT; } else
                if (facing == BlockFace.WEST) { return RelativeOrientations.LEFT; } else
                if (facing == BlockFace.UP) { return RelativeOrientations.UP; } else
                if (facing == BlockFace.DOWN) { return RelativeOrientations.DOWN; }
                break;
            case SouthForward:
                if (facing == BlockFace.NORTH) { return RelativeOrientations.BACK; } else
                if (facing == BlockFace.SOUTH) { return RelativeOrientations.FORWARD; } else
                if (facing == BlockFace.EAST) { return RelativeOrientations.LEFT; } else
                if (facing == BlockFace.WEST) { return RelativeOrientations.RIGHT; } else
                if (facing == BlockFace.UP) { return RelativeOrientations.UP; } else
                if (facing == BlockFace.DOWN) { return RelativeOrientations.DOWN; }
                break;
            case EastForward:
                if (facing == BlockFace.NORTH) { return RelativeOrientations.LEFT; } else
                if (facing == BlockFace.SOUTH) { return RelativeOrientations.RIGHT; } else
                if (facing == BlockFace.EAST) { return RelativeOrientations.FORWARD; } else
                if (facing == BlockFace.WEST) { return RelativeOrientations.BACK; } else
                if (facing == BlockFace.UP) { return RelativeOrientations.UP; } else
                if (facing == BlockFace.DOWN) { return RelativeOrientations.DOWN; }
                break;
            case WestForward:
                if (facing == BlockFace.NORTH) { return RelativeOrientations.RIGHT; } else
                if (facing == BlockFace.SOUTH) { return RelativeOrientations.LEFT; } else
                if (facing == BlockFace.EAST) { return RelativeOrientations.BACK; } else
                if (facing == BlockFace.WEST) { return RelativeOrientations.FORWARD; } else
                if (facing == BlockFace.UP) { return RelativeOrientations.UP; } else
                if (facing == BlockFace.DOWN) { return RelativeOrientations.DOWN; }
                break;
        }

        // Yes
        return RelativeOrientations.FORWARD;
    }

    /**
     * @param facing The direction being faced by the individual block, relative to the core.
     *               
     * @param inRelativeTo The direction being faced by the core, absolute with the world.
     *                     
     * @return The direction faced by the individual block, absolute with the world.
     */
    @NotNull public static BlockFace transform(@NotNull RelativeOrientations facing, @NotNull Orientations inRelativeTo) {

        // Orientate
        switch (inRelativeTo) {
            case NorthForward:
                if (facing == RelativeOrientations.FORWARD) { return BlockFace.NORTH; } else
                if (facing == RelativeOrientations.BACK) { return BlockFace.SOUTH; } else
                if (facing == RelativeOrientations.RIGHT) { return BlockFace.EAST; } else
                if (facing == RelativeOrientations.LEFT) { return BlockFace.WEST; } else
                if (facing == RelativeOrientations.UP) { return BlockFace.UP; } else
                if (facing == RelativeOrientations.DOWN) { return BlockFace.DOWN; }
                break;
            case SouthForward:
                if (facing == RelativeOrientations.BACK) { return BlockFace.NORTH; } else
                if (facing == RelativeOrientations.FORWARD) { return BlockFace.SOUTH; } else
                if (facing == RelativeOrientations.LEFT) { return BlockFace.EAST; } else
                if (facing == RelativeOrientations.RIGHT) { return BlockFace.WEST; } else
                if (facing == RelativeOrientations.UP) { return BlockFace.UP; } else
                if (facing == RelativeOrientations.DOWN) { return BlockFace.DOWN; }
                break;
            case EastForward:
                if (facing == RelativeOrientations.LEFT) { return BlockFace.NORTH; } else
                if (facing == RelativeOrientations.RIGHT) { return BlockFace.SOUTH; } else
                if (facing == RelativeOrientations.FORWARD) { return BlockFace.EAST; } else
                if (facing == RelativeOrientations.BACK) { return BlockFace.WEST; } else
                if (facing == RelativeOrientations.UP) { return BlockFace.UP; } else
                if (facing == RelativeOrientations.DOWN) { return BlockFace.DOWN; }
                break;
            case WestForward:
                if (facing == RelativeOrientations.RIGHT) { return BlockFace.NORTH; } else
                if (facing == RelativeOrientations.LEFT) { return BlockFace.SOUTH; } else
                if (facing == RelativeOrientations.BACK) { return BlockFace.EAST; } else
                if (facing == RelativeOrientations.FORWARD) { return BlockFace.WEST; } else
                if (facing == RelativeOrientations.UP) { return BlockFace.UP; } else
                if (facing == RelativeOrientations.DOWN) { return BlockFace.DOWN; }
                break;
        }

        // Never will happen
        return BlockFace.SOUTH;
    }

    @NotNull public static Orientations betweenAbsolutes(@NotNull BlockFace face) {

        switch (face) {

            case EAST_NORTH_EAST:
            case EAST_SOUTH_EAST:
            case EAST: return Orientations.EastForward;

            case NORTH_EAST:
            case NORTH_WEST:
            case NORTH_NORTH_EAST:
            case NORTH_NORTH_WEST:
            case NORTH: return Orientations.NorthForward;

            case WEST_NORTH_WEST:
            case WEST_SOUTH_WEST:
            case WEST: return Orientations.WestForward;

            default: return Orientations.SouthForward;
        }
    }
}
