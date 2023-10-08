package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;

public class CSMInteger extends CSMeta {

    /**
     * @return The value of this flag
     */
    public int getValue() { return value; }
    int value;
    /**
     * @param value The value of this flag
     */
    public void setValue(int value) { this.value = value; }

    /**
     * @param value Value of this flag.
     */
    public CSMInteger(int value) { this.value = value; }

    @Override public String toString() { return String.valueOf(getValue()); }
}