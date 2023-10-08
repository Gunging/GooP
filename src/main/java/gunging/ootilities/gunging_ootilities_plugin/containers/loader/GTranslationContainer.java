package gunging.ootilities.gunging_ootilities_plugin.containers.loader;

import gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation.GTranslation;
import org.jetbrains.annotations.NotNull;

public class GTranslationContainer extends GTranslation<GTL_Containers> {

    /**
     * Link a translation to an enum
     *
     * @param key Enum constant to link
     * @param translation String value
     */
    public GTranslationContainer(@NotNull GTL_Containers key, @NotNull String translation) { super(key, translation); }
}
