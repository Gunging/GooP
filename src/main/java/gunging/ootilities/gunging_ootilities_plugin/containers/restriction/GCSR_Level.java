package gunging.ootilities.gunging_ootilities_plugin.containers.restriction;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.containers.SlotRestriction;
import gunging.ootilities.gunging_ootilities_plugin.misc.QuickNumberRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Requires a player to be of certain level
 */
public class GCSR_Level extends SlotRestriction {

    @NotNull
    final QuickNumberRange levelRange;

    public GCSR_Level(@NotNull QuickNumberRange levelRange) {
        super();
        this.levelRange = levelRange;
    }

    @Override
    public boolean isUnlockedFor(@NotNull Player player) {

        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a77 Player has level? \u00a77" + serialize());

        // No MMOItems no service
        if (!Gunging_Ootilities_Plugin.foundMMOItems) { return false; }

        // If the class is contained-yo
        return levelRange.InRange(GooPMMOItems.GetPlayerLevel(player));
    }


    @NotNull
    @Override
    public ArrayList<String> appendLore(@NotNull ArrayList<String> item) {

        item.add(OotilityCeption.ParseColour("\u00a73>\u00a77 Levels:\u00a7b " + levelRange.qrToString()));

        // Return thay
        return item;
    }

    @NotNull
    @Override
    public String serialize() { return levelRange.toString(); }

    @Nullable public static GCSR_Level deserialize(@NotNull String serialized) {

        // Create array
        QuickNumberRange qnr = QuickNumberRange.FromString(serialized);

        if (qnr == null) { return null; }

        // Parse error
        return new GCSR_Level(qnr);
    }
}
