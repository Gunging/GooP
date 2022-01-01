package gunging.ootilities.gunging_ootilities_plugin.misc;

import com.sun.org.apache.xpath.internal.operations.Plus;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPMMOItemsItemStats;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypes;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.*;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class ConverterPerTier {

    @Nullable public String getTier() { return tier; }
    @Nullable String tier;
    public ConverterPerTier(@Nullable String toTier) { tier = toTier; }

    ArrayList<ItemStat> addedStats = new ArrayList<>();
    HashMap<ItemStat, PlusMinusPercent> numericStatData = new HashMap<>();
    HashMap<ItemStat, BooleanData> booleanStatData = new HashMap<>();
    HashMap<ItemStat, StringData> stringStatData = new HashMap<>();
    HashMap<ItemStat, StringListData> stringListStatData = new HashMap<>();

    /**
     *  Tier is decided elsewhere, such that it is extraneous
     *  to specify a tier here... mostly...
     *
     *  Only used for the tierless setting, when displaying.
     */
    @Nullable public String interestingTierName = null;

    public void AddStatData(@Nullable String statName, @NotNull String data) {

        // Get
        ItemStat actualStat = GooPMMOItems.Stat(statName);

        // Set
        if (actualStat != null) {

            if (actualStat == ItemStats.TIER) { interestingTierName = data; }

            // Delegate to parser
            AddStatData(actualStat, data);

        // Invalid stat name
        } else {

            /*
             * Special keys??
             */
            if (ConverterTypes.PRICE_MULTIPLIER.equals(statName)) {
                if (!setPrice(data)) { Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register price change for villager trades when using the MMOItems Converter for Tier '\u00a7e" + tier + "\u00a77' Conversion: \u00a7cYou must specify a PMP (+40%) or an NBT Filter (m CONSUMABLE MANGO)"); }
                return;
            }

            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + statName + " \u00a77for Tier-specific Conversion: \u00a7cStat not found");
        }
    }

    //region Funny villager trades price scaling
    @Nullable NBTFilter priceNBTFilter = null;
    @Nullable PlusMinusPercent priceMultiplier = null;

    public boolean setPrice(@NotNull String price) {
        priceMultiplier = null;
        priceNBTFilter = null;

        // Attempt to parse
        priceMultiplier = PlusMinusPercent.GetPMP(price, null);
        if (priceMultiplier != null) { return true; }

        if (!price.contains(" ")) { return false; }
        String[] mmSplit = price.split(" ");
        if (mmSplit.length != 3 && mmSplit.length != 4) { return false; }

        if (mmSplit.length == 4) { priceMultiplier = PlusMinusPercent.GetPMP(mmSplit[3], null); }

        // Attempt as NBT Filter
        if (OotilityCeption.IsInvalidItemNBTtestString(mmSplit[0], mmSplit[1], mmSplit[2], null, null)) { return false; }
        priceNBTFilter = new NBTFilter(mmSplit[0], mmSplit[1], mmSplit[2], null);
        return true;
    }

    /**
     * Will multiply the emerald amounts times the price multiplier.
     *
     * Then:
     *      If an NBT Filter was specified, it will apply it.
     *
     *      If the thing remains as a thing that can be crafted into
     *      blocks, it will craft into blocks and divide price by nine,
     *      in an attempt to fit higher prices.
     *
     *      Up to two stacks of blocks can be generated, assuming the
     *      second slot was empty.
     *
     * @param first First item of the trade
     * @param second Second item of the trade
     */
    public void adjustPrice(@NotNull RefSimulator<ItemStack> first, @NotNull RefSimulator<ItemStack> second) {
        ItemStack firstItem = first.getValue();
        boolean firstTakeover = OotilityCeption.IsAirNullAllowed(firstItem) || firstItem.getType() == Material.EMERALD;
        if (Gunging_Ootilities_Plugin.foundMMOItems) { firstTakeover = firstTakeover && !GooPMMOItems.IsMMOItem(firstItem); }
        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a77 Allow #1 Takeover?\u00a7f " + firstTakeover);

        ItemStack secondItem = second.getValue();
        boolean secondTakeover = OotilityCeption.IsAirNullAllowed(secondItem) || secondItem.getType() == Material.EMERALD;
        if (Gunging_Ootilities_Plugin.foundMMOItems) { secondTakeover = secondTakeover && !GooPMMOItems.IsMMOItem(secondItem); }
        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a77 Allow #2 Takeover?\u00a7f " + secondTakeover);

        // Very well
        int totalEmeraldCount = 0;
        if (firstTakeover) { totalEmeraldCount += firstItem.getAmount(); }
        if (secondTakeover) { totalEmeraldCount += secondItem.getAmount(); }
        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a77 Original Price:\u00a7f " + totalEmeraldCount);

        // Transform price if appropriate
        if (priceMultiplier != null) { totalEmeraldCount = OotilityCeption.RoundToInt(priceMultiplier.apply((double) totalEmeraldCount)); }
        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a77 Adjusted Price:\u00a7e " + totalEmeraldCount + "\u00a78 (\u00a77" + (priceMultiplier != null ? priceMultiplier.toString() : "null") + "\u00a78)");

        // Item to change to?
        ItemStack finalCurrency = priceNBTFilter == null ? new ItemStack(Material.EMERALD) : OotilityCeption.ItemFromNBTTestString(priceNBTFilter.filterKey, priceNBTFilter.dataPrime, priceNBTFilter.dataDime, null, null);
        if (finalCurrency == null) { finalCurrency = new ItemStack(Material.EMERALD); }
        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a77 Adjusted Currency:\u00a7f " + OotilityCeption.GetItemName(finalCurrency));

        // Convert to blocks, in the process divide count by nine... of course it it would save inventory space.
        if (totalEmeraldCount > 64 && (!Gunging_Ootilities_Plugin.foundMMOItems || !GooPMMOItems.IsMMOItem(finalCurrency))) {

            // Attempt to convert to blocks
            Material blockMaterial = OotilityCeption.asBlock(finalCurrency.getType());
            if (blockMaterial != null) {

                finalCurrency.setType(blockMaterial);
                totalEmeraldCount = OotilityCeption.RoundToInt(totalEmeraldCount / 9.000D); } }

        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a76_______________________ ");
        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a77 Approved Price:\u00a76 " + totalEmeraldCount);
        //TRD// OotilityCeption.Log("\u00a78ADJUST\u00a7a TK\u00a77 Approved Currency:\u00a7f " + OotilityCeption.GetItemName(finalCurrency));

        /*
         *      Very well, now that we know the final item the villager will
         *      charge, and how many, its only left to distribute it among
         *      the slots we are allowed to take over.
         */
        if (firstTakeover) {

            // If it exceeds the max stack, we can set a fraction of it
            if (totalEmeraldCount > finalCurrency.getType().getMaxStackSize()) {

                // Set max and subtract
                first.setValue(OotilityCeption.asQuantity(finalCurrency, finalCurrency.getType().getMaxStackSize()));
                totalEmeraldCount -= finalCurrency.getType().getMaxStackSize();

            // Max stack fits, we can just set them all
            } else {

                // Set max and subtract
                first.setValue(OotilityCeption.asQuantity(finalCurrency, totalEmeraldCount));
                totalEmeraldCount = 0;
            }
        }
        if (secondTakeover) {

            // If it exceeds the max stack, we can set a fraction of it
            if (totalEmeraldCount > finalCurrency.getType().getMaxStackSize()) {

                // Set max and subtract
                second.setValue(OotilityCeption.asQuantity(finalCurrency, finalCurrency.getType().getMaxStackSize()));
                totalEmeraldCount -= finalCurrency.getType().getMaxStackSize();

            // Max stack fits, we can just set them all
            } else {

                // Set max and subtract
                second.setValue(OotilityCeption.asQuantity(finalCurrency, totalEmeraldCount));
            }
        }

        // Any remainder? We can do nothing for you, sorry.
    }
    //endregion

    public void AddStatData(@NotNull String statName, @NotNull ArrayList<String> data) {

        // Get
        ItemStat actualStat = GooPMMOItems.Stat(statName);

        // Set
        if (actualStat != null) {

            // Delegate to parser
            AddStatData(actualStat, data);

        // Invalid stat name
        } else {
            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + statName + " \u00a77for Tier-specific Conversion: \u00a7cStat not found");
        }
    }

    public void AddStatData(@NotNull ItemStat itemStat, @NotNull ArrayList<String> data) {
        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73ST\u00a77 Registering\u00a7a list\u00a77 stat\u00a7b " + itemStat.getName() + " \u00a77x" + data.size());

        // Must be string list
        if (itemStat instanceof StringListStat || IsGemSocketsStat(itemStat)) {

            // Store
            addedStats.add(itemStat);
            stringListStatData.put(itemStat, new StringListData(data));

        } else {

            // Register
            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + itemStat.getName() + " \u00a77for the MMOItems Converter: \u00a7cYou provided a string list, but \u00a7e" + itemStat.getName() + "\u00a7c is not a StringListStat");
        }
    }
    public void AddStatData(@NotNull ItemStat itemStat, @NotNull String data) {

        // Treat as numeric or String
        if (itemStat instanceof DoubleStat) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73ST\u00a77 Registering\u00a7a double\u00a77 stat\u00a7b " + itemStat.getName() + " \u00a77" + data);

            // If Valid
            PlusMinusPercent pmp = PlusMinusPercent.GetPMP(data, null);

            // Valid?
            if (pmp != null) {

                // As Double
                addedStats.add(itemStat);
                numericStatData.put(itemStat, pmp);

            // Log
            } else {
                Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + itemStat.getName() + " \u00a77for Tier-specific Conversion: \u00a7cExpected PlusMinusPercent operation instead of \u00a76" + data);
            }

        } else if (itemStat instanceof BooleanStat) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73ST\u00a77 Registering\u00a7a bool\u00a77 stat\u00a7b " + itemStat.getName() + " \u00a77" + data);

            // As String
            addedStats.add(itemStat);
            booleanStatData.put(itemStat, new BooleanData("true".equalsIgnoreCase(data)));

        } else if (itemStat instanceof StringStat) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73ST\u00a77 Registering\u00a7a string\u00a77 stat\u00a7b " + itemStat.getName() + " \u00a77" + data);

            // As String
            addedStats.add(itemStat);
            stringStatData.put(itemStat, new StringData(data));

        // Not string list is it?
        } else if (itemStat instanceof StringListStat || IsGemSocketsStat(itemStat)) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a73ST\u00a77 Registering\u00a7a list\u00a77 stat\u00a7b " + itemStat.getName() + " \u00a77" + data);

            // List
            ArrayList<String> sdat = new ArrayList<>();
            sdat.add(data);

            // Send as list of one element
            AddStatData(itemStat, sdat);

        } else {

            Gunging_Ootilities_Plugin.theOots.CPLog("Failed to register stat \u00a73" + itemStat.getName() + " \u00a77for Tier-specific Conversion: \u00a7cOnly Numeric, String, and StringList stats are supported. This is neither.");
        }
    }

    // Is eet?
    boolean IsGemSocketsStat(ItemStat stat) {
        return stat.equals(GooPMMOItems.Stat(GooPMMOItemsItemStats.GEM_SOCKETS));
    }

    @NotNull
    public ItemStack ApplyTo(@NotNull ItemStack iSource) {
        //RLD// OotilityCeption.Log("\u00a78CONVERTER \u00a7bTIER\u00a77 Applying onto " + OotilityCeption.GetItemName(iSource));

        // Should be a MMOItem but
        if (!GooPMMOItems.IsMMOItem(iSource)) { iSource = GooPMMOItems.ConvertVanillaToMMOItem(iSource); }

        // Add All I guess
        LiveMMOItem mmo = new LiveMMOItem(iSource);

        // For each sat
        for (ItemStat stt : addedStats) {

            // Numeric?
            PlusMinusPercent pmp = numericStatData.get(stt);
            if (pmp != null) {

                // Get Value
                Double base = GooPMMOItems.GetDoubleStatValue(mmo, stt);
                if (base == null) { base = 0.0D; }

                // Create double data
                double finalBase = pmp.apply(base);
                DoubleData dat = new DoubleData(finalBase);

                // Get History
                StatHistory hit = StatHistory.from(mmo, stt);
                hit.setOriginalData(dat);

                // That's it
                mmo.setData(stt, hit.recalculate(mmo.getUpgradeLevel()));

                //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bTIER\u00a77 Double Data\u00a7b " + finalBase + "\u00a77 onto " + stt.getName());

            } else if (stt instanceof BooleanStat) {

                // String?
                BooleanData str = booleanStatData.get(stt);
                if (str != null) {

                    // That's it
                    mmo.setData(stt, str);

                    //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bTIER\u00a77 Boolean Data\u00a7b " + str.isEnabled() + "\u00a77 onto " + stt.getName());
                }

            } else {

                // String?
                StringData str = stringStatData.get(stt);
                if (str != null) {
                    //RLD// OotilityCeption.Log("\u00a78CONVERTER \u00a7bTIER\u00a77 String Data\u00a7b " + str.toString() + "\u00a77 onto " + stt.getName());

                    // That's it
                    mmo.setData(stt, str);

                } else {

                    // Get String List Data
                    StringListData stringListData = stringListStatData.get(stt);

                    // Valid?
                    if (stringListData != null) {
                        //RLD// OotilityCeption.Log("\u00a78CONVERTER \u00a7bTIER\u00a77 String List Data\u00a7b " + ((StringListData) stringListData).getList().size() + "\u00a77 onto " + stt.getName());

                        // Convert to gemstones
                        StatData trueList = stringListData;
                        if (stt.getClearStatData() instanceof GemSocketsData) {

                            trueList = new GemSocketsData(stringListData.getList());
                        }

                        // Get History
                        StatHistory hit = StatHistory.from(mmo, stt);
                        hit.setOriginalData(trueList);

                        // That's it
                        mmo.setData(stt, hit.recalculate(mmo.getUpgradeLevel()));
                    }
                }
            }
        }

        // Build
        return mmo.newBuilder().build();
    }
}
