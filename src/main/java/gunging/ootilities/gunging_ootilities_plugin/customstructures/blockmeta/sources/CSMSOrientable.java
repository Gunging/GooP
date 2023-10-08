package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.Axis;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSOrientable extends CSMSRelativeOrientation {

    /**
     * This flag will have its corresponding {@link CSMRelativeOrientation}
     * for the axis this is oriented along or whatever yeah.
     */
    public CSMSOrientable() { super("ORIENTABLE"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMRelativeOrientation meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Orientable)) { return block; }

        // All right what block face we trying to get
        Axis axis = allowedOrient((Orientable) input, meta, inRelativeTo);

        // No orientable axis? snooze
        if (axis == null) { return block; }

        // Set that
        ((Orientable) input).setAxis(axis);

        // Set
        block.setBlockData(input);

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMRelativeOrientation fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Block data bisected??
        if (!(input instanceof Orientable)) { return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMRelativeOrientation(orient(((Orientable) input).getAxis(), inRelativeTo));
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMRelativeOrientation meta, @NotNull Orientations inRelativeTo) {
        BlockData input = block.getBlockData();

        // Cannot hve this data, no match.
        if (!(input instanceof Orientable)) { return false; }

        // All right what block face we trying to get
        Axis axis = allowedOrient((Orientable) input, meta, inRelativeTo);

        // Invalid -> no match
        if (axis == null) { return false; }

        // Axis must match
        return axis == ((Orientable) input).getAxis();
    }

    /**
     * @param input Input block, just to check the allowed axes with {@link Orientable#getAxes()}
     *
     * @param meta The set of Relative Orientations to choose from.
     *
     * @param inRelativeTo To correctly convert relative orientations to and from axes.
     *
     * @return The axis in which the meta expects orientation, while being compatible with this orientable object.
     *         <code>null</code> if incompatible, of course.
     */
    @Nullable public static Axis allowedOrient(@NotNull Orientable input, @NotNull CSMRelativeOrientation meta, @NotNull Orientations inRelativeTo) {

        // All right what block face we trying to get
        Axis axis = Axis.Z;

        // Ideally there is only one orientation registered
        for (RelativeOrientations orientation : meta.getValue()) {

            // Ax
            Axis ax = orient(orientation, inRelativeTo);

            // If allowed, that one.
            if (input.getAxes().contains(ax)) { axis = ax; } }

        // No change if not supported
        if (!input.getAxes().contains(axis)) { return null; }

        // Yes
        return axis;
    }

    /**
     * @param facing The direction being faced by the individual block, absolute with the world.
     *
     * @param inRelativeTo The direction being faced by the core, absolute with the world.
     *
     * @return The direction faced by the individual block, relative to the core.
     */
    @NotNull public static RelativeOrientations orient(@NotNull Axis facing, @NotNull Orientations inRelativeTo) {

        // Orientate
        switch (inRelativeTo) {
            case NorthForward:
            case SouthForward:
                if (facing == Axis.X) { return RelativeOrientations.RIGHT; } else
                if (facing == Axis.Y) { return RelativeOrientations.UP; } else
                if (facing == Axis.Z) { return RelativeOrientations.FORWARD; }

                break;
            case EastForward:
            case WestForward:
                if (facing == Axis.X) { return RelativeOrientations.FORWARD; } else
                if (facing == Axis.Y) { return RelativeOrientations.UP; } else
                if (facing == Axis.Z) { return RelativeOrientations.RIGHT; }

                break;
        }

        // Yeah
        return RelativeOrientations.FORWARD;
    }

    /**
     * @param facing The direction being faced by the individual block, relative to the core.
     *
     * @param inRelativeTo The direction being faced by the core, absolute with the world.
     *
     * @return The direction faced by the individual block, absolute with the world.
     */
    @NotNull public static Axis orient(@NotNull RelativeOrientations facing, @NotNull Orientations inRelativeTo) {

        // Orientate
        switch (inRelativeTo) {
            case NorthForward:
            case SouthForward:
                if (facing == RelativeOrientations.RIGHT) { return Axis.X; } else
                if (facing == RelativeOrientations.LEFT) { return Axis.X; } else
                if (facing == RelativeOrientations.UP) { return Axis.Y; } else
                if (facing == RelativeOrientations.DOWN) { return Axis.Y; } else
                if (facing == RelativeOrientations.FORWARD) { return Axis.Z; } else
                if (facing == RelativeOrientations.BACK) { return Axis.Z; } else

                    break;
            case EastForward:
            case WestForward:
                if (facing == RelativeOrientations.RIGHT) { return Axis.Z; } else
                if (facing == RelativeOrientations.LEFT) { return Axis.Z; } else
                if (facing == RelativeOrientations.UP) { return Axis.Y; } else
                if (facing == RelativeOrientations.DOWN) { return Axis.Y; } else
                if (facing == RelativeOrientations.FORWARD) { return Axis.X; } else
                if (facing == RelativeOrientations.BACK) { return Axis.X; } else

                break;
        }

        // Yeah
        return Axis.Z;
    }
}