package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class NameVariable {

    String identifier;
    String value;

    public boolean isVariable() { return getIdentifier() != null; }

    /**
     * If null, this is basically a simple string.
     */
    @Nullable public String getIdentifier() { return identifier; }
    @NotNull public String getValue() { return value; }

    public void setIdentifier(String newIdentifier) { identifier = newIdentifier; }
    public void setValue(String newValue) { value = newValue; }

    // Just kreate
    public NameVariable(@Nullable String name, @NotNull String val) {
        identifier = name;
        value = val;
    }

    @Override
    public String toString() {

        if (isVariable()) {

            return ChatColor.RED + getIdentifier() + "\u00a77." + getValue();

        } else {

            return getValue();
        }
    }

    public String Serialize() {
        if (isVariable()) {
            return getIdentifier() + "@Ñ@" + getValue();
        } else return getValue();
    }

    @Nullable
    public static NameVariable Deserialize(String str) {

        if (str.contains("@Ñ@")) {
            String[] part = str.split("@Ñ@");
            if (part.length >= 2) {

                return new NameVariable(part[0], part[1]);
            }
        }

        return new NameVariable(null, str);
    }
}
