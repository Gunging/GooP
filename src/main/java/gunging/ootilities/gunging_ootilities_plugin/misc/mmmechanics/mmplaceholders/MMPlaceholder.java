package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public abstract class MMPlaceholder implements BiFunction<PlaceholderMeta, String, String> {

    @Override public abstract String apply(PlaceholderMeta metadata, String arg);
}
