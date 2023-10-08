package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;
import org.bukkit.block.data.type.Slab;
import org.jetbrains.annotations.NotNull;

public class CSMSlab extends CSMeta {

    /**
     * @return The value of this flag
     */
    @NotNull public Slab.Type getValue() { return value; }
    @NotNull Slab.Type value;

    /**
     * @param value The value of this flag
     */
    public void setValue(@NotNull Slab.Type value) { this.value = value; }

    /**
     * @param value Value of this flag.
     */
    public CSMSlab(@NotNull Slab.Type value) { this.value = value; }

    @Override public String toString() { return getValue().toString(); }
}