package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;

public class CSMBoolean extends CSMeta {

    /**
     * @return The value of this flag
     */
    public boolean getValue() { return value; }
    boolean value;
    /**
     * @param value The value of this flag
     */
    public void setValue(boolean value) { this.value = value; }

    /**
     * @param value Value of this flag.
     */
    public CSMBoolean(boolean value) { this.value = value; }

    @Override public String toString() { return String.valueOf(getValue()); }
}
