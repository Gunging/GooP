package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;

import java.util.ArrayList;

public class PlusMinusPercent {

    Boolean relative = false;
    Double operationValue = 0.0;
    Boolean multiplyPercent = false;

    public PlusMinusPercent(Double value, Boolean relativity, Boolean multiplicative) {
        relative = relativity;
        operationValue = value;
        multiplyPercent = multiplicative;
    }

    public Double apply(Double source) {

        // Titration brUH
        double oV = operationValue;

        // Is it a percent?
        if (multiplyPercent) {

            // Relative is on base 100%; so +50% means it multiplies by 1.5; while -50% is *0.5
            if (relative) { oV += 1; }

            return source * oV;

        // Its not a percent, its scalar
        } else {

            // If it is relative to the source
            if (relative) {

                // Just shift by the source
                return  source + oV;

            // Otherwise its a straight up set
            } else {

                // Thets the set
                return oV;
            }
        }
    }

    public Double getValue() { return operationValue; }
    public Double GetValue() { return operationValue; }

    public void OverrideValue(double newOperationValue) { operationValue = newOperationValue; }
    public void OverrideRelativity(boolean newRelativity) { relative = newRelativity; }
    public void OverrideMultiplicativity(boolean newMultiplicativity) { multiplyPercent = newMultiplicativity; }

    public Boolean GetRelative() { return relative; }
    public Boolean getRelative() { return relative; }

    public Boolean IsPercent() { return multiplyPercent; }
    public Boolean isPercent() { return multiplyPercent; }
    public Boolean GetMultiplyInstead() { return multiplyPercent; }
    public Boolean getMultiplyInstead() { return multiplyPercent; }

    /**
     * Will return a PMP from the given string, or NULL if it doesnt parse.
     */
    public static PlusMinusPercent GetPMP(String arg, RefSimulator<String> logger) {
        OotilityCeption oots = new OotilityCeption();

        Boolean relativity = false;
        Boolean multiplicativity = false;
        Double positivity = 1.0;
        Double value = 0.0;

        String unsignedArg = arg;

        if (arg.startsWith("-")) {
            relativity = true;
            positivity = -1.0;
            unsignedArg = arg.substring(1);

        } else if (arg.startsWith("+")) {
            relativity = true;
            unsignedArg = arg.substring(1);

        } else if (arg.startsWith("n")) {
            positivity = -1.0;
            unsignedArg = arg.substring(1);
        }

        if (arg.endsWith("%")) {
            multiplicativity = true;
            positivity *= 0.01;
            unsignedArg = unsignedArg.substring(0, unsignedArg.length() - 1);
        }

        if (OotilityCeption.DoubleTryParse(unsignedArg)) {
            value = Double.parseDouble(unsignedArg) * positivity;

        } else {
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Cant parse a numeric value from '\u00a73" + unsignedArg + "\u00a77' (The numeric part of \u00a7e" + arg + "\u00a77: Without ±,n, or %)");
            return null;
        }

        if (logger != null) { logger.SetValue(null); }
        return new PlusMinusPercent(value, relativity, multiplicativity);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");

        // Relative?
        if (getRelative() && !getMultiplyInstead()) {

            // n or no prefix?
            if (getValue() < 0) { str.append('-'); } else { str.append('+'); }

        } else {

            // n or no prefix?
            if (getValue() < 0) { str.append('n'); }
        }

        // Appent absolute value
        double val = getValue();
        if (getMultiplyInstead()) { val *= 100; }

        // Append thay
        str.append(OotilityCeption.ReadableRounding(val, 2));

        // Append percent
        if (getMultiplyInstead()) { str.append('%'); }

        // Build
        return str.toString();
    }
}
