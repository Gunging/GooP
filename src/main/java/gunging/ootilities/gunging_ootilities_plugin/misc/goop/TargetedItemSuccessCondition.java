package gunging.ootilities.gunging_ootilities_plugin.misc.goop;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TargetedItemSuccessCondition {

    /**
     * @param iSource ItemStack to process
     *
     * @return <code>null</code> for failures. Anything else for success.
     */
    boolean isSuccess(@NotNull TargetedItem iSource);
}
