package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface SlashResult<T> {

    /**
     * @param data All the information of the mythic skill known
     *
     * @param slashedLocation Location of the slash point
     *
     * @param funnies List of audience, apparently
     */
    @Nullable T process(@NotNull SkillMetadata data, @NotNull AbstractLocation slashedLocation, Object... funnies);
}
