package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import org.bukkit.Material;

public class DoubleStatProper extends DoubleStat implements GemStoneStat {
    public DoubleStatProper(String id, Material mat, String name, String[] lore) { super(id, mat, name, lore); }
    public DoubleStatProper(String id, Material mat, String name, String[] lore, String[] types, Material... materials) { super(id, mat, name, lore, types, materials); }
    public DoubleStatProper(String id, Material mat, String name, String[] lore, String[] types, boolean moreIsBetter, Material... materials) { super(id, mat, name, lore, types, moreIsBetter, materials); }
}
