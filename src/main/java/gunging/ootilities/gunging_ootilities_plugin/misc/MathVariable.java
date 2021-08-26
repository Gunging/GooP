package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MathVariable {

    String identifier;
    double value;

    public boolean isVariable() { return getIdentifier() != null; }

    /**
     * If null, this is basically a simple string.
     */
    @Nullable
    public String getIdentifier() { return identifier; }
    public double getValue() { return value; }

    public void setIdentifier(String newIdentifier) { identifier = newIdentifier; }
    public void setValue(double newValue) { value = newValue; }

    // Just kreate
    public MathVariable(@Nullable String name, double val) {
        identifier = name;
        value = val;
    }

    @Override
    public String toString() {

        if (isVariable()) {

            return ChatColor.RED + getIdentifier() + "\u00a77=" + getValue();

        } else {

            return String.valueOf(getValue());
        }
    }
}
