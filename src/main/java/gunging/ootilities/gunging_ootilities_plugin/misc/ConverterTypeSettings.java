package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypeNames;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class ConverterTypeSettings {

    // What does this target?
    ConverterTypeNames target;

    @NotNull
    public ConverterTypeNames getType() {
        return target;
    }

    // Loaded
    static HashMap<ConverterTypeNames, ConverterTypeSettings> loadedSettings = new HashMap<>();

    // What does it apply?
    ListPlaceholder randomTierOnCraft = null;
    ListPlaceholder randomTierOnPickup = null;

    // Tier settings
    HashMap<String, ConverterPerTier> perTierSettingsOnCraft = new HashMap<>(), perTierSettingsOnPickup = new HashMap<>();

    // The loaded ones
    public static void Reset() {
        loadedSettings.clear();
    }

    /**
     * Creates and loads the settings that will be applied
     */
    public ConverterTypeSettings(@NotNull ConverterTypeNames to) {

        // Bruh
        target = to;

        // Set
        loadedSettings.put(target, this);
    }

    /**
     * Returns the settings if any are loaded.
     */
    @Nullable
    public static ConverterTypeSettings PertainingTo(@Nullable ConverterTypeNames ctn) {
        if (ctn == null) { return null; }

        // Whatever find
        return loadedSettings.get(ctn);
    }

    /**
     * A Placeholder List of tiers to apply.
     */
    public void SetTierRates(@NotNull ArrayList<String> tierList, boolean asPickup) {

        if (asPickup) {

            // Creates list
            randomTierOnPickup = new ListPlaceholder("GooP_Converter_" + target.name(), tierList);
        } else {

            // Creates list
            randomTierOnCraft = new ListPlaceholder("GooP_Converter_" + target.name(), tierList);
        }
    }
    public void SetPerTierStats(@Nullable String targetTier, @NotNull ConverterPerTier ptr, boolean asPickup) {

        if (asPickup) {

            if (targetTier == null) { nullTierPickup = ptr.interestingTierName; }

            // just put i guess
            perTierSettingsOnPickup.put(targetTier, ptr);

        } else {

            if (targetTier == null) { nullTier = ptr.interestingTierName; }

            // just put i guess
            perTierSettingsOnCraft.put(targetTier, ptr);
        }
    }

    @Nullable String nullTier = null;
    @Nullable String nullTierPickup = null;
    @Nullable public String GetNullTier(boolean asPickup) { return asPickup ? nullTierPickup : nullTier; }

    public boolean hasRandomTier(boolean asPickup) {

        // Welp
        if (asPickup) {

            // Creates list
            return randomTierOnPickup != null;
        } else {

            // Creates list
            return randomTierOnCraft != null;
        }
    }

    /**
     * WILL PRODUCE A NULL POINTER EXCEPTION IF IT HAS NO RANDOM TIER CONFIGURED
     */
    public String getRandomTier(boolean asPickup) {

        if (asPickup) {

            return randomTierOnPickup.RandomListItem();
        } else {

            return randomTierOnCraft.RandomListItem();
        }
    }

    @Nullable
    public ConverterPerTier getPerTierSettings(@Nullable String tier, boolean asPickup) {

        if (asPickup) {

            return perTierSettingsOnPickup.get(tier);
        } else {

            return perTierSettingsOnCraft.get(tier);
        }
    }

    /**
     * Applies things that should be applied before an item is crafted.
     *
     * Specifically, non tier-specific settings, because it must be
     * crafted to finally decide what it will look like, and this
     * step happens before deciding the tier.
     */
    @NotNull
    public ItemStack ApplyTo(@NotNull ItemStack iSource, @Nullable Player parseAS, boolean asPickup) {

        // AH
        ConverterPerTier cpt2 = getPerTierSettings(null, asPickup);

        // Contained?
        if (cpt2 != null) {
            //RLD//OotilityCeption.Log("\u00a78CONVERTER \u00a7bPREVIEW\u00a77 Does have Null-Tier Settings");

            // Apply
            iSource = cpt2.ApplyTo(iSource);
        }

        // Return result
        return iSource;
    }

    /**
     * Applies such changes that should happen AFTER the item
     * is crafted. Doesnt matter when picking up.
     *
     * Specifically, tier-specific settings, because it must be
     * crafted to finally decide what it will look like.
     */
    @NotNull
    public ItemStack ApplyPostTo(@NotNull ItemStack iSource, @Nullable Player parseAS, boolean asPickup) {
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
                }
            }
        }


        // Return result
        return iSource;
    }
}
