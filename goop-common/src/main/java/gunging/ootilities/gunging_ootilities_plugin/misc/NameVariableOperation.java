package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class NameVariableOperation {

    /**
     * 0: Replace
     * 1: Variable (Replace)
     * 2: Placeholder Refresh
     */
    public int asOperation;
    String iReplace;
    ArrayList<NameVariable> iVariables;

    /**
     * Targets the whole original string rather than only variables of it.
     */
    public NameVariableOperation(@NotNull String overwrite) {

        asOperation = 0;
        iReplace = overwrite;
    }

    /**
     * Will change the values of these variables within the target string if they exist.
     */
    public NameVariableOperation(@NotNull ArrayList<NameVariable> specs) {
        iVariables = specs;
        asOperation = 1;
    }

    /**
     * Will do nothing to the original name, but refresh PAPI placeholders.
     */
    public NameVariableOperation() {
        asOperation = 2;
    }

    /**
     * Will change the values of this variable within the target string if it exists.
     */
    public NameVariableOperation(@NotNull NameVariable spec) {
        ArrayList<NameVariable> specs = new ArrayList<>();
        specs.add(spec);
        iVariables = specs;
        asOperation = 1;
    }

    public boolean isReplace() { return asOperation == 0; }
    public boolean isRevariable() { return asOperation == 1; }
    public boolean isReplaceholder() { return asOperation == 2; }

    @Nullable
    public String getReplace() { return iReplace; }

    @Nullable
    public ArrayList<NameVariable> getVariables() { return iVariables; }

    @Nullable
    public NameVariable getVariable(@NotNull String identifier) {

        // No null
        if (getVariables() == null) { return null; }

        for (NameVariable nv : getVariables()) {

            if (nv.getIdentifier() != null) {

                if (nv.getIdentifier().equals(identifier)) {

                    return nv;
                }
            }
        }

        // Nothing found
        return null;
    }
}
