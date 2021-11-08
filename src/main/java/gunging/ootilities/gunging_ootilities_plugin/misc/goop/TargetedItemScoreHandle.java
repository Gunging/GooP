package gunging.ootilities.gunging_ootilities_plugin.misc.goop;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TargetedItemScoreHandle {

    /**
     * @param iSource ItemStack to process
     *
     * @return <code>null</code> for failures. Anything else for success.
     */
    void handleScores(@NotNull TargetedItem iSource, @NotNull SuccessibleInformation sInfo);
}
