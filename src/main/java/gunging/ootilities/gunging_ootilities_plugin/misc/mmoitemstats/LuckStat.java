package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import net.Indyuce.mmoitems.stat.type.AttributeStat;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public class LuckStat extends AttributeStat {
    public LuckStat() { super("GOOP_LUCK", Material.EXPERIENCE_BOTTLE, "Luck", new String[]{"Affects loot tables and other stuff,", "like fishing, I guess."}, Attribute.GENERIC_LUCK); }
}
