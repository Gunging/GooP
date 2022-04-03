package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

import java.util.function.BiFunction;

public abstract class MMPlaceholder implements BiFunction<PlaceholderMeta, String, String> {

    @Override public abstract String apply(PlaceholderMeta metadata, String arg);
}
