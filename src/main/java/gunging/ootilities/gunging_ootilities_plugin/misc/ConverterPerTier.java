package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPMMOItemsItemStats;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StringListStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ConverterPerTier {

    String tier;
    public ConverterPerTier(String toTier) { tier = toTier; }

    ArrayList<ItemStat> addedStats = new ArrayList<>();
    HashMap<ItemStat, PlusMinusPercent> numericStatData = new HashMap<>();
    HashMap<ItemStat, StringData> stringStatData = new HashMap<>();
    HashMap<ItemStat, StatData> stringListStatData = new HashMap<>();

    public void AddStatData(@NotNull String statName, @NotNull String data) {

        // Get
        ItemStat actual = GooPMMOItems.Stat(statName);

        // Set
        if (actual != null) { AddStatData(actual, data); } else {
            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + statName + " \u00a77for Tier-specific Conversion: \u00a7cStat not found");
        }
    }

    public void AddStatData(@NotNull String statName, @NotNull ArrayList<String> data) {

        // Get
        ItemStat actual = GooPMMOItems.Stat(statName);

        // Set
        if (actual != null) { AddStatData(actual, data); } else {
            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + statName + " \u00a77for Tier-specific Conversion: \u00a7cStat not found");
        }
    }

    public void AddStatData(@NotNull ItemStat statt, @NotNull ArrayList<String> data) {

        // Must be string list
        if (statt instanceof StringListStat || IsGemSocketsStat(statt)) {

            // Store
            addedStats.add(statt);
            stringListStatData.put(statt, new StringListData(data));

        } else {

            // Registr
            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + statt.getName() + " \u00a77for Tier-specific Conversion: \u00a7cYou provided a string list, but this is not a StringListStat");
        }
    }
    public void AddStatData(@NotNull ItemStat statt, @NotNull String data) {

        // Treat as numeric or String
        if (statt instanceof DoubleStat) {

            // If Valid
            PlusMinusPercent pmp = PlusMinusPercent.GetPMP(data, null);

            // Valid?
            if (pmp != null) {

                // As Double
                addedStats.add(statt);
                numericStatData.put(statt, pmp);

            // Log
            } else {
                Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + statt.getName() + " \u00a77for Tier-specific Conversion: \u00a7cExpected PlusMinusPercent operation instead of \u00a76" + data);
            }

        } else if (statt instanceof StringStat) {

            // As String
            addedStats.add(statt);
            stringStatData.put(statt, new StringData(data));

        // Not string list is it?
        } else if (statt instanceof StringListStat || IsGemSocketsStat(statt)) {

            // List
            ArrayList<String> sdat = new ArrayList<>();
            sdat.add(data);

            // Send as list of one element
            AddStatData(statt, sdat);

        } else {

            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + statt.getName() + " \u00a77for Tier-specific Conversion: \u00a7cOnly Numeric, String, and StringList stats are supported. This is neither.");
        }
    }

    // Is eet?
    boolean IsGemSocketsStat(ItemStat stat) {
        return stat.equals(GooPMMOItems.Stat(GooPMMOItemsItemStats.GEM_SOCKETS));
    }

    @NotNull
    public ItemStack ApplyTo(@NotNull ItemStack iSource) {

        // Should be a MMOItem but
        if (!GooPMMOItems.IsMMOItem(iSource)) { iSource = GooPMMOItems.ConvertVanillaToMMOItem(iSource); }

        // Add All I guess
        NBTItem asNBT = NBTItem.get(iSource);

        // For each sat
        for (ItemStat stt : addedStats) {

            // Numeric?
            PlusMinusPercent pmp = numericStatData.get(stt);
            if (pmp != null) {

                // Get Value
                Double base = GooPMMOItems.GetDoubleStatValue(asNBT, stt);
                if (base == null) { base = 0.0D; }

                // Create double data
                double finalBase = pmp.apply(base);
                DoubleData dat = new DoubleData(finalBase);

                // Set
                MMOItem statAdded = GooPMMOItems.SetStatData(asNBT, stt, dat, false);
                if (statAdded != null) { asNBT = statAdded.newBuilder().buildNBT(); }

            } else {

                // String?
                StringData str = stringStatData.get(stt);
                if (str != null) {

                    // Set
                    MMOItem statAdded = GooPMMOItems.SetStatData(asNBT, stt, str, false);
                    if (statAdded != null) { asNBT = statAdded.newBuilder().buildNBT(); }

                } else {

                    // Get String List Data
                    StatData sdat = stringListStatData.get(stt);

                    // Valid?
                    if (sdat instanceof StringListStat) {

                        // Then add
                        MMOItem statAdded = GooPMMOItems.SetStatData(asNBT, stt, sdat, false);
                        if (statAdded != null) { asNBT = statAdded.newBuilder().buildNBT(); }
                    }
                }
            }
        }

        // Build
        return GooPMMOItems.Build(asNBT);
    }
}
