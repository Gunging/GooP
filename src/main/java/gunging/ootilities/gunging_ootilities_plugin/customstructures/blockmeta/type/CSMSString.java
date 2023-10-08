package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMString;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CSMSString extends CSMetaSource<CSMString> {

    public CSMSString(@NotNull String internalName) { super(internalName); }

    @Override
    public @Nullable CSMString fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // Yes
        return new CSMString(serialized);
    }

    @Override
    public @NotNull String toString(@NotNull CSMString serializable) {

        // Um yeah just that
        return serializable.getValue();
    }
}
