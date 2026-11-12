package gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation;

import org.jetbrains.annotations.NotNull;

/**
 * Binds a translation to an enum
 *
 * @param <KEY> Enum type yeah
 */
public class GTranslation<KEY> {

    /**
     * @return The enum key
     */
    @NotNull KEY getKey() { return key; }
    @NotNull final KEY key;

    /**
     * @return The translation currently loaded
     */
    @NotNull public String getTranslation() { return translation; }
    @NotNull String translation;
    /**
     * @param translation Load this translation
     */
    public void setTranslation(@NotNull String translation) { this.translation = translation; }

    /**
     * Link a translation to an enum
     *
     * @param key Enum constant key yes
     * @param translation String value
     */
    public GTranslation(@NotNull KEY key, @NotNull String translation) {
        this.key = key;
        this.translation = translation;
    }
}
