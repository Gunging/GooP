package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;
import org.bukkit.Instrument;
import org.jetbrains.annotations.NotNull;

public class CSMInstrument  extends CSMeta {

    /**
     * @return The value of this flag
     */
    @NotNull
    public Instrument getValue() { return value; }

    @NotNull Instrument value;

    /**
     * @param value The value of this flag
     */
    public void setValue(@NotNull Instrument value) { this.value = value;}

    /**
     * @param value Value of this flag.
     */
    public CSMInstrument(@NotNull Instrument value) { this.value = value; }

    @Override public String toString() { return getValue().toString(); }
}
