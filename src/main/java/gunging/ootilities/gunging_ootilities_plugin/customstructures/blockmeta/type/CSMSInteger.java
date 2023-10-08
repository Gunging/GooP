package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CSMSInteger extends CSMetaSource<CSMInteger> {

    public CSMSInteger(@NotNull String internalName) { super(internalName); }

    @Override
    public @Nullable CSMInteger fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // No boolean no service
        if (!OotilityCeption.IntTryParse(serialized)) { return null; }

        // Yes
        return new CSMInteger(OotilityCeption.ParseInt(serialized));
    }

    @Override
    public @NotNull String toString(@NotNull CSMInteger serializable) {

        // Um yeah just that
        return String.valueOf(serializable.getValue());
    }
}