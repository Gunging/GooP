package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMCMMO;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOLib;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MMPHMCMMOStat extends MMPHMMOStatPlaceholder {
    public MMPHMCMMOStat(@NotNull MMPHMMOStatTarget whom) { super(whom); }

    @NotNull
    final static HashMap<MMPHMMOStatTarget, MetaPlaceholder> inst = new HashMap<>();
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

        // A value to return
        Double result = GooPMCMMO.MCMMODoubleStat(asPlayer, statName);

        // Adjust
        if (result == null) { return "00"; }

        // Return
        return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));
    }
}
