package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOLib;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MMPHMMOStat extends MMPHMMOStatPlaceholder {
    public MMPHMMOStat(@NotNull MMPHMMOStatTarget whom) { super(whom); }

    @NotNull final static HashMap<MMPHMMOStatTarget, MetaPlaceholder> inst = new HashMap<>();
    public static MetaPlaceholder getInst(@NotNull MMPHMMOStatTarget which) {

        // Find
        MetaPlaceholder mph = inst.get(which);
        if (mph != null) { return mph; }

        // Create
        mph = Placeholder.meta(new MMPHMMOStat(which));
        inst.put(which, mph);
        return mph; }

    @Override
    public @NotNull String getStat(@NotNull PlaceholderMeta meta, @NotNull String statName, @Nullable ItemStackSlot slot, @NotNull Player asPlayer) {

        // String stat support
        if (slot != null) {

            // Change Team
            ItemStackLocation location = slot.getItem(asPlayer);

            // Sleep
            if (location == null) { return  "00.0"; }

            // Find item
            ItemStack item = location.getItem();

            // Sleeper
            if (OotilityCeption.IsAirNullAllowed(item)) { return  "00.0"; }

            // Okay now we can do operations with the single item
            String result = null;

            // Uuuuh yeah
            if (Gunging_Ootilities_Plugin.foundMMOItems || Gunging_Ootilities_Plugin.foundMMOCore) {

                // Fetch from MMO
                result = GooPMMOItems.GetStringStatValue(item, GooPMMOItems.Stat(statName), asPlayer, true); }

            // Adjust
            if (result == null) { return "00.00"; }

            // Yeah
            return result;
        }

        // A value to return
        Double result = 0D;

        // Fetch from MMO
        if (Gunging_Ootilities_Plugin.foundMMOItems || Gunging_Ootilities_Plugin.foundMMOCore) {

            // Fetch from MMO
            result = GooPMMOLib.CDoubleStat(asPlayer, statName); }

        // Adjust
        if (result == null) { return "00"; }

        // Return
        return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));
    }
}
