package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CSMRelativeOrientation extends CSMeta {

    /**
     * @return The value of this flag
     */
    @NotNull public ArrayList<RelativeOrientations> getValue() { return value; }
    @NotNull final ArrayList<RelativeOrientations> value;
    
    /**
     * @param value The value of this flag
     */
    public void setValue(@NotNull RelativeOrientations value) { this.value.clear(); this.value.add(value); }

    /**
     * @param value Value of this flag.
     */
    public CSMRelativeOrientation(@NotNull RelativeOrientations value) { this.value = new ArrayList<>(); this.value.add(value); }

    /**
     * @param value Value of this flag.
     */
    public CSMRelativeOrientation(@NotNull ArrayList<RelativeOrientations> value) { this.value = value; }

    @Override public String toString() { return getValue().toString(); }
}
