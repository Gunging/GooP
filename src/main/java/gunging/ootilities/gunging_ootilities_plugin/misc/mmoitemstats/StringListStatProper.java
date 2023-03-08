package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringListStat;
import org.bukkit.Material;

public class StringListStatProper extends StringListStat implements GemStoneStat {
    public StringListStatProper(String id, Material mat, String name, String[] lore, String[] types, Material... materials) { super(id, mat, name, lore, types, materials); }
}
