package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.bukkit.Material;

public class StringStatProper extends StringStat implements GemStoneStat { public StringStatProper(String id, Material mat, String name, String[] lore, String[] types, Material... materials) { super(id, mat, name, lore, types, materials); }}
