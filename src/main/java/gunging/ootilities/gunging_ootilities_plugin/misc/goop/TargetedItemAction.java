package gunging.ootilities.gunging_ootilities_plugin.misc.goop;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Which of the many GooP Methods will be called?
 */
@FunctionalInterface
public interface TargetedItemAction {

    /**
     * @param iSource ItemStack to process
     *
     * @return <code>null</code> for failures. Anything else for success.
     */
    @Nullable ItemStack Process(@NotNull TargetedItem iSource);
}
