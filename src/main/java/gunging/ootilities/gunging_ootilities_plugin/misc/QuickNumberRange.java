package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public class QuickNumberRange {
    public boolean isAny() { return minimumInclusive == null && maximumInclusive == null; }
    Double minimumInclusive;
    Double maximumInclusive;

    public Double getMinimumInclusive() { return minimumInclusive; }
    public Double GetMinimumInclusive() { return getMinimumInclusive(); }
    public Double getMaximumInclusive() { return maximumInclusive; }
    public Double GetMaximumInclusive() { return getMaximumInclusive(); }
    public boolean HasMin() { return hasMin(); }
    public boolean HasMax() { return hasMax(); }
    public boolean hasMin() { return minimumInclusive != null; }
    public boolean hasMax() { return maximumInclusive != null; }

    /**
     * <p>
     *     Supported Formats:
     * </p>
     *     [m]..[M] (Vanilla range, the way used in command selectors)
     * <p>
     *
     * </p>
     *      Obviously, [m] is the minimum, and [M] the maximum; And supports them not being specified.
     * @return NULL if incorrect format
     */
    @Nullable public static QuickNumberRange FromString(@Nullable String fromString) {
        //OotilityCeption. Log("Getting Range From \u00a79" + fromString);
        if (fromString == null) { return null; }

        if (fromString.equals("..")) { return new QuickNumberRange(null, null); }

        // Number itself? So its basically an EXACTLY this value, I'll allow it.
        if (OotilityCeption.DoubleTryParse(fromString)) {

            // Parse
            Double prsed = Double.parseDouble(fromString);

            // Reurn
            //OotilityCeption. Log("Exact Range Passed: \u00a7a" + prsed);

            // Return
            return new QuickNumberRange(prsed, prsed);
        }

        // Split
        if (fromString.contains("..")) {

            // Split
            String[] sblit = fromString.split("\\.\\.");

            // Must be length two
            if (sblit.length == 2 || sblit.length == 1) {
                //OotilityCeption. Log("\u00a7aSplit Success");

                // Failure?
                boolean failure = false;
                Double min = null;
                Double max = null;

                String fist = "", scnd = "";
                if (sblit.length == 2) {

                    // In order
                    fist = sblit[0];
                    scnd = sblit[1];

                } else {

                    // Unrestricted Bounds
                    if(fromString.startsWith("..")) {

                        // Its second
                        scnd = sblit[0];

                    } else {

                        // Its first
                        fist = sblit[0];
                    }
                }

                // Try to parse both
                if (fist.length() > 0) {
                    //OotilityCeption. Log("Parsing \u00a73" + fist);

                    // If has length, is it numeric ofrmat?
                    if (OotilityCeption.DoubleTryParse(fist)) {

                        // Parse
                        min = Double.parseDouble(fist);
                        //OotilityCeption. Log("Min as \u00a7a" + min);

                    // Did not parse
                    } else {
                        //OotilityCeption. Log("\u00a7cFailed");

                        // Numer format excpetopn
                        failure = true;
                    }
                }

                // Try to parse both
                if (scnd.length() > 0) {
                    //OotilityCeption. Log("Parsing \u00a73" + scnd);

                    // If has length, is it numeric ofrmat?
                    if (OotilityCeption.DoubleTryParse(scnd)) {

                        // Parse
                        max = Double.parseDouble(scnd);
                        //OotilityCeption.Log("Max as \u00a7a" + max);

                        // Did not parse
                    } else {
                        //OotilityCeption. Log("\u00a7cFailed");

                        // Numer format excpetopn
                        failure = true;
                    }
                }

                // Success?
                if (!failure) {

                    // Return
                    return new QuickNumberRange(min, max);
                }
            }
        }

        //OotilityCeption. Log("\u00a7cFailure");
        // Something went wrong
        return null;
    }

    public QuickNumberRange(Double min, Double max) {
        minimumInclusive = min;
        maximumInclusive = max;
    }

    /**
     * Will return TRUE if <code>test</code> is 'in range.' Accounting for no min/max boundaries
     */
    public boolean InRange(double test) {

        // If has minimum
        if (hasMin()) {

            // Fail if below minimum
            if (test < getMinimumInclusive()) { return false; }
        }

        // If has maximum
        if (hasMax()) {

            // Fail if above maximum
            if (test > getMaximumInclusive()) { return false; }
        }

        // Success
        return true;
    }

    /**
     * This '<i>Quick Number Range</i>' is actually just a number,
     * because the user specified the same as the min and max.
     */
    public boolean isSimple() {

        // If either bound is missing, this is not simple.
        if (!hasMax() || !hasMin()) { return false; }

        // They match-yo?
        return getMaximumInclusive().equals(getMinimumInclusive());
    }

    @Override
    public String toString() {

        // Simple?
        if (isSimple()) { return String.valueOf(getMaximumInclusive()); }

        // Get
        String minn = ""; String maxn = "";
        if (hasMin()) { minn = String.valueOf(getMinimumInclusive()); }
        if (hasMax()) { maxn = String.valueOf(getMaximumInclusive()); }

        // Build
        return minn + ".." + maxn;
    }

    public String qrToString() {

        // Simple?
        if (isSimple()) { return String.valueOf(getMaximumInclusive()); }

        // Get
        String minn = "-∞"; String maxn = "∞";
        if (hasMin()) { minn = String.valueOf(getMinimumInclusive()); }
        if (hasMax()) { maxn = String.valueOf(getMaximumInclusive()); }

        // Build
        return minn + "—" + maxn;
    }
}
