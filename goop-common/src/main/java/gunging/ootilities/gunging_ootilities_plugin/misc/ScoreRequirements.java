package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ScoreRequirements extends QuickNumberRange {

    @NotNull String objectiveName;
    @Nullable Objective objectiveS;

    @NotNull public String getObjectiveName() { return objectiveName; }
    @Nullable public Objective getObjective() { return objectiveS; }
    public boolean validObjective() { return objectiveS != null; }

    public ScoreRequirements(Double min, Double max, @NotNull String objective) {
        super(min, max);
        objectiveName = objective;
        objectiveS = OotilityCeption.GetObjective(objective);
    }

    public ScoreRequirements(@NotNull QuickNumberRange range, @NotNull String objective) {
        super(range.getMinimumInclusive(), range.getMaximumInclusive());
        objectiveName = objective;
        objectiveS = OotilityCeption.GetObjective(objective);
    }

    /**
     * <p>
     *     A list in the format {[score]=[min]..[max], ...}
     *
     *     Not necessarily within {}s, will crop them out if they are there tho.
     * </p>
     * @return An array with all the score requirements. Empty if not in format.
     */
    @NotNull
    public static ArrayList<ScoreRequirements> FromCompactString(String source) {
        // The returnin
        ArrayList<ScoreRequirements> ret = new ArrayList<>();

        // No null
        if (source == null) { return ret; }

        // Crop {}s
        source = OotilityCeption.UnwrapFromCurlyBrackets(source);

        // Split by commas
        String[] splitSR = source.split(",");

        // Foreach
        for (String str : splitSR) {

            // Parse and add
            ScoreRequirements sR = FromString(str);
            if (sR != null) { ret.add(sR); }
        }

        // Result
        return ret;
    }

    /**
     * <p>
     *     In the format [score]=[min]..[max]
     * </p>
     * @return The SINGLE score requirement in there
     */
    @Nullable
    public static ScoreRequirements FromString(String source) {

        // Bruh
        if (source == null) { return null; }

        // Split equals
        if (source.contains("=")) {

            // Split
            String[] agrs = source.split("=");

            // Exactly two-yo
            if (agrs.length == 2) {

                // Parse range lma000
                QuickNumberRange qnr = QuickNumberRange.FromString(agrs[1]);

                // Success?
                if (qnr != null) {

                    // JUST GO IN THEREEEE
                    return new ScoreRequirements(qnr, agrs[0]);
                }
            }
        }

        // Ew format error
        return null;
    }

    @Override
    public String toString() {

        // Get Objective Name
        if (validObjective()) {

            return ChatColor.GREEN + objectiveName + " \u00a77: \u00a7e" + qrToString();

        // Objective actually does not exist bruh
        } else {

            return ChatColor.RED + objectiveName + " \u00a77: \u00a7e" + qrToString();
        }
    }
}
