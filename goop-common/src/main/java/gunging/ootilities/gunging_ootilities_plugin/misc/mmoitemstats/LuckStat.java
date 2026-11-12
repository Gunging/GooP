package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public class LuckStat extends DoubleStat {
    public LuckStat() { super("GOOP_LUCK", Material.EXPERIENCE_BOTTLE, "Luck", new String[]{"Affects loot tables and other stuff,", "like fishing, I guess."}); }
}
