package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class CompactCodedValue {

    String code, val;

    /**
     * The code that stands for the value
     */
    @NotNull
    public String getID() { return code; }

    /**
     * The code that stands for the value
     */
    @NotNull
    public String GetID() { return getID(); }

    /**
     * The value encoded by the code
     */
    @NotNull
    public String getValue() { return val; }

    /**
     * The value encoded by the code
     */
    @NotNull
    public String GetValue() { return getValue(); }

    /**
     * Creates a Compact Variable characterized by a code and a value it encodes for.
     */
    public CompactCodedValue(@NotNull String id, @NotNull String value){
        code = id;
        val = value;
    }

    /**
     * Gets all those compact variables encoded in string.
     * <p> </p>
     * Format:
     * <p>[ID]=[VALUE];[ID1]=[VALUE1];[ID2]=[VALUE2]</p>
     * <p></p>
     * Use <&sc> or <$sc> as placeholder for semicolons.
     * @return A list with all valid ID=VALUE pairs contained in the string.
     */
    @NotNull
    public static ArrayList<CompactCodedValue> ListFromString(@Nullable String source) {

        // Return Value
        ArrayList<CompactCodedValue> ret = new ArrayList<>();

        // Early Quit
        if (source == null) { return ret;}
        if (!source.contains("=")) { return ret;}

        // Split
        ArrayList<String> encodeds = new ArrayList<>();
        if (source.contains(";")) { encodeds.addAll(Arrays.asList(source.split(";"))); }
        else { encodeds.add(source); }

        // Foreach
        for (String encode : encodeds) {

            // Parse Attempt
            CompactCodedValue encoded = FromString(encode);

            // Add if valid
            if (encoded != null) { ret.add(encoded); }
        }

        // Return thay
        return ret;
    }

    /**
     * Unparses a variable-id pair from a string
     * <p></p>
     * Format:
     * <p>[ID]=[VALUE]</p>
     * <p></p>
     * All <&sc> and <$sc> are replaced by semicolons.
     * @return Will return null if its not in a valid format (has no =)
     */
    @Nullable
    public static CompactCodedValue FromString(@Nullable String source) {

        // Early quit
        if (source == null) { return null;}
        if (source.length() < 3) { return null; }

        // Equals Find
        int eqlasLoc = source.indexOf("=");
        if (eqlasLoc < 1) { return null;}

        // Get ID and Value
        String id = source.substring(0, eqlasLoc);
        String val = source.substring(eqlasLoc + 1);

        // Parse
        id = id.replace("<&sc>", ";").replace("<$sc>", ";");
        val = val.replace("<&sc>", ";").replace("<$sc>", ";");

        // Bake and return
        return new CompactCodedValue(id, val);
    }
}
