package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;
import org.bukkit.Note;
import org.jetbrains.annotations.NotNull;

public class CSMNote extends CSMeta {

    /**
     * @return The value of this flag
     */
    @NotNull public Note getValue() { return value; }
    @NotNull Note value;
    /**
     * @param value The value of this flag
     */
    public void setValue(@NotNull Note value) { this.value = value;}

    /**
     * @param value Value of this flag.
     */
    public CSMNote(@NotNull Note value) { this.value = value; }

    @Override public String toString() { return getValue().toString(); }
}