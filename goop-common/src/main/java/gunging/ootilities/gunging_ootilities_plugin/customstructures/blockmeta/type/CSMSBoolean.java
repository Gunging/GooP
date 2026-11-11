package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CSMSBoolean extends CSMetaSource<CSMBoolean> {

    public CSMSBoolean(@NotNull String internalName) { super(internalName); }

    @Override
    public @Nullable CSMBoolean fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // No boolean no service
        if (!OotilityCeption.BoolTryParse(serialized)) { return null; }

        // Yes
        return new CSMBoolean(OotilityCeption.BoolParse(serialized));
    }

    @Override
    public @NotNull String toString(@NotNull CSMBoolean serializable) {

        // Um yeah just that
        return String.valueOf(serializable.getValue());
    }
}
