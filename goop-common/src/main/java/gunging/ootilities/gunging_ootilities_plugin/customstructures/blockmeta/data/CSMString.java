package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;
import org.jetbrains.annotations.NotNull;

public class CSMString extends CSMeta {

    /**
     * @return The value of this flag
     */
    @NotNull public String getValue() { return value; }
    @NotNull String value;
    /**
     * @param value The value of this flag
     */
    public void setValue(@NotNull String value) { this.value = value; }

    /**
     * @param value Value of this flag.
     */
    public CSMString(@NotNull String value) { this.value = value; }

    @Override public String toString() { return getValue(); }
}
