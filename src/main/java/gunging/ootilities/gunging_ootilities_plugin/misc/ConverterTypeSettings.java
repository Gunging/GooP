package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypeNames;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConvertingReason;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.StringListData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Per converting action, how will each kind of items be affected?
 */
public class ConverterTypeSettings {

    //region Manager
    /**
     * Clear the loaded converter settings yeah
     */
    public static void reset() { liveConverters.clear(); }

    /**
     * @return The converters available to convert items yeah
     */
    @NotNull public static HashMap<ConverterTypeNames, ConverterTypeSettings> getLiveConverters() { return liveConverters; }
    @NotNull final static HashMap<ConverterTypeNames, ConverterTypeSettings> liveConverters = new HashMap<>();
    /**
     * @param kindOfItems The kind of items that you are converting
     *
     * @return If any is loaded, the converter to convert this item.
     */
    @Nullable public ConverterTypeSettings getConverterFor(@NotNull ConverterTypeNames kindOfItems) { return liveConverters.get(kindOfItems); }

    /**
     * @param kindOfItems Kind of items you are converting
     *
     * @return The settings applied to this kind of items, if any are loaded.
     */
    @Nullable public static ConverterTypeSettings PertainingTo(@Nullable ConverterTypeNames kindOfItems) { if (kindOfItems == null) { return null; } return liveConverters.get(kindOfItems); }
    //endregion

    //region Constructor
    /**
     * @return The kind of items this converter will apply to
     */
    @NotNull public ConverterTypeNames getTargetItemKinds() { return targetItemKinds; }
    @NotNull final ConverterTypeNames targetItemKinds;

    /**
     * Creates and loads the settings that will be applied
     *
     * @param targetItemKinds Kind of items that this settings will apply to
     */
    public ConverterTypeSettings(@NotNull ConverterTypeNames targetItemKinds) {
        this.targetItemKinds = targetItemKinds;

        // Set live
        liveConverters.put(this.targetItemKinds, this);
    }
    //endregion

    //region Per-Action Converting
    /**
     * @return Per-Converting-Action, the random list of tiers that will be applied to items.
     */
    @NotNull public HashMap<ConvertingReason, ListPlaceholder> getRandomTiering() { return randomTiering; }
    @NotNull final HashMap<ConvertingReason, ListPlaceholder> randomTiering = new HashMap<>();
    /**
     * @param reason Reason by which you are converting
     *
     * @return If there is any random tiering to be applied, those random tiers.
     */
    @Nullable public ListPlaceholder getRandomTiering(@NotNull ConvertingReason reason) { return randomTiering.get(reason); }
    /**
     * @param reason Action to which bind these random tiers
     *
     * @param tierList List of tiers to apply
     */
    public void setRandomTiering(@NotNull ArrayList<String> tierList, @NotNull ConvertingReason reason) { randomTiering.put(reason, new ListPlaceholder("GooP_Converter_" + targetItemKinds.name(), tierList)); }
    /**
     * @param reason The action by which this item is being converted
     *
     * @return If any random tiering is applied
     */
    public boolean hasRandomTier(@NotNull ConvertingReason reason) { return getRandomTiering(reason) != null; }
    /**
     * @param reason The action by which this is being converted.
     *
     * @return A random of any of the configured tiers, if any were configured,
     *         or <code>null</code> if none were configured.
     */
    @Nullable public String getRandomTier(@NotNull ConvertingReason reason) {
        ListPlaceholder lph = getRandomTiering(reason);
        if (lph == null) { return null; }
        String chosen = lph.RandomListItem();
        return "none".equals(chosen) ? null : chosen; }

    /**
     * @return Per-Converting-Action, the tier displayed before the tier is decided.
     */
    @NotNull public HashMap<ConvertingReason, ConverterPerTier> getNullTier() { return nullTier; }
    @NotNull final HashMap<ConvertingReason, ConverterPerTier> nullTier = new HashMap<>();
    /**
     * @param reason Reason by which you are converting
     *
     * @return Per-Converting-Action, the tier displayed before the tier is decided.
     */
    @Nullable public ConverterPerTier getNullTier(@NotNull ConvertingReason reason) { return nullTier.get(reason); }
    /**
     * @param reason The reason by which you are converting the item
     *
     * @return The name of the tier uuuuh or something like that Im not entirely sure.
     */
    @Nullable public String getNullTierName(@NotNull ConvertingReason reason) { ConverterPerTier cpt = nullTier.get(reason); return cpt == null ? null : cpt.interestingTierName;}
    /**
     * @return Per-Converting-Action, the different bonuses applied to each tier
     */
    @NotNull public HashMap<ConvertingReason, HashMap<String, ConverterPerTier>> getPerTierSettings() { return perTierSettings; }
    @NotNull final HashMap<ConvertingReason, HashMap<String, ConverterPerTier>> perTierSettings = new HashMap<>();
    /**
     * @param reason Reason by which you are converting
     *
     * @return If this tier action has settings to it, those settings.
     */
    @NotNull public HashMap<String, ConverterPerTier> getPerTierSettings(@NotNull ConvertingReason reason) { HashMap<String, ConverterPerTier> ret = perTierSettings.get(reason); if (ret == null) { ret = new HashMap<>(); } perTierSettings.put(reason, ret); return ret; }
    @Nullable public ConverterPerTier getPerTierSettings(@Nullable String tier, @NotNull ConvertingReason asPickup) {
        if (tier == null) { return getNullTier(asPickup); }
        return perTierSettings.get(asPickup).get(tier); }
    /**
     * @param tier Tier these settings will apply to
     * @param settings Settings to apply
     * @param reason Action by which these settings will apply
     */
    public void setPerTierSettings(@Nullable String tier, @NotNull ConverterPerTier settings, @NotNull ConvertingReason reason) {

        // Store null tier reference I guess
        if (tier == null) { nullTier.put(reason, settings); }

        // Find settings
        HashMap<String, ConverterPerTier> tierSettings = getPerTierSettings(reason);

        // Put
        tierSettings.put(tier, settings);
        perTierSettings.put(reason, tierSettings);
    }
    //endregion

    /**
     * Applies things that should be applied before an item is crafted.
     *
     * Specifically, non tier-specific settings, because it must be
     * crafted to finally decide what it will look like, and this
     * step happens before deciding the tier.
     *
     * <b>It is applied before {@link #applyTo(ItemStack, Player, ConvertingReason, RefSimulator)}</b>
     */
    @NotNull public ItemStack applyDisplayTo(@NotNull ItemStack iSource, @Nullable Player parseAS, @NotNull ConvertingReason asPickup) {

        // AH
        ConverterPerTier cpt2 = getPerTierSettings(null, asPickup);

        // Contained?
        if (cpt2 != null) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bPREVIEW\u00a77 Does have Null-Tier Settings");

            // Apply
            iSource = cpt2.ApplyTo(iSource);
        }

        // You say its a MMOItem RIGHT!??
        if (Gunging_Ootilities_Plugin.foundMMOItems && parseAS != null) { iSource = GooPMMOItems.internallyParsePlaceholdes(iSource, parseAS); }

        // Return result
        return iSource;
    }

    /**
     * Applies such changes that should happen AFTER the item is crafted.
     *
     * Specifically, tier-specific settings, because it must be
     * crafted to finally decide what it will look like.
     *
     * <b>It is applied after {@link #applyDisplayTo(ItemStack, Player, ConvertingReason)}</b>
     */
    @NotNull
    public ItemStack applyTo(@NotNull ItemStack iSource, @Nullable Player parseAS, @NotNull ConvertingReason asPickup, @Nullable RefSimulator<ConverterPerTier> cptRet) {
        //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bAPPLY\u00a77 Post applying onto " + OotilityCeption.GetItemName(iSource));

        // AH
        ItemStack iResult = iSource;

        // Roll for tier
        if (hasRandomTier(asPickup)) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bAPPLY\u00a77 Random tiering detected");

            // Get Random Tier
            String preChosenTier = getRandomTier(asPickup);

            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bAPPLY\u00a77 Random Chance ~ " + preChosenTier);

            // If exists
            if (GooPMMOItems.TierExists(preChosenTier)) {
                //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bAPPLY\u00a77 Choosing " + preChosenTier);

                // Apply
                iSource = GooPMMOItems.SetTier(iSource, preChosenTier, null,null);
                if (iSource == null) { iSource = iResult; }

                // Has it per tier shit?
                ConverterPerTier cpt = getPerTierSettings(preChosenTier, asPickup);

                // Contained?
                if (cpt != null) {
                    //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bAPPLY\u00a77 Does have Per-Tier Settings");

                    // Apply
                    iSource = cpt.ApplyTo(iSource);

                    if (cptRet != null) { cptRet.setValue(cpt); }
                }
            }
        }

        // You say its a MMOItem RIGHT!??
        if (Gunging_Ootilities_Plugin.foundMMOItems && parseAS != null) { iSource = GooPMMOItems.internallyParsePlaceholdes(iSource, parseAS); }

        // Return result
        return iSource;
    }
}
