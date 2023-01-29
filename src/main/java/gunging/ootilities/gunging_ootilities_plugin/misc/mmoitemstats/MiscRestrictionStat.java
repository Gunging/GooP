package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.QuickNumberRange;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MiscRestrictionStat extends DoubleStat implements ItemRestriction {

    @NotNull QuickNumberRange rangeOfSuccess;
    @NotNull ArrayList<String> px;

    public MiscRestrictionStat(String s, Material material, String s1, String[] strings, String[] strings1, @NotNull QuickNumberRange rangeOfSuccess, @NotNull ArrayList<String> px) {
        super(s, material, s1, strings, strings1);
        this.rangeOfSuccess = rangeOfSuccess;
        this.px = px;
    }

    @Override
    public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean loud) {
        if (!nbtItem.hasTag(getNBTPath())) { return true; }

        // Identify the value in the item
        double current = nbtItem.getDouble(getNBTPath());
        Player player = rpgPlayer.getPlayer();
        ItemStack itm = nbtItem.getItem();

        //RST//OotilityCeption.Log("\u00a78RST\u00a73 CHK\u00a77 Value in item:\u00a7b " + current + "\u00a77; Range of Success:\u00a7a " + rangeOfSuccess.toString());

        // Parse range of success
        for (String p : px) {
            if (p == null) { continue; }

            // Parse
            Double d = SilentNumbers.DoubleParse(OotilityCeption.ParseConsoleCommand(p, player, player, null, itm));

            // Not null?? add
            if (d != null) {
                //RST//OotilityCeption.Log("\u00a78RST\u00a73 CHK\u00a7a +\u00a77 " + p + "\u00a77:\u00a7b " + d);
                current += d; }

            //RST// else { OotilityCeption.Log("\u00a78RST\u00a73 CHK\u00a7c -\u00a77 " + p + "\u00a77; Could not parse. "); }
        }

        //RST//OotilityCeption.Log("\u00a78RST\u00a73 CHK\u00a77 Total: \u00a7b " + current + "\u00a77; \u00a7e" + rangeOfSuccess.InRange(current));

        // Can use if in range of success
        return rangeOfSuccess.InRange(current);
    }
}
