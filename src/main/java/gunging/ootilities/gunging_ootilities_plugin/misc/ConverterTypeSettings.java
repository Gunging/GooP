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
    String nameOnCraft = null, nameOnPickup = null;

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
    public static ConverterTypeSettings PertainingTo(@NotNull ConverterTypeNames ctn) {

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
    public void SetPerTierStats(@NotNull String targetTier, @NotNull ConverterPerTier ptr, boolean asPickup) {

        if (asPickup) {

            // just put i guess
            perTierSettingsOnPickup.put(targetTier, ptr);
        } else {

            // just put i guess
            perTierSettingsOnCraft.put(targetTier, ptr);
        }
    }

    /**
     * A Placeholder List of tiers to apply.
     */
    public void SetName(@NotNull String name, boolean asPickup) {

        if (asPickup) {

            // Creates list
            nameOnPickup = name;
        } else {

            // Creates list
            nameOnCraft = name;
        }
    }

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
    public boolean hasName(boolean asPickup) {

        // Welp
        if (asPickup) {

            // Creates list
            return nameOnPickup != null;
        } else {

            // Creates list
            return nameOnCraft != null;
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
    public ConverterPerTier getPerTierSettings(@NotNull String tier, boolean asPickup) {

        if (asPickup) {

            return perTierSettingsOnPickup.get(tier);
        } else {

            return perTierSettingsOnCraft.get(tier);
        }
    }

    public String getName(boolean asPickup) {

        if (asPickup) {

            return nameOnPickup;
        } else {

            return nameOnCraft;
        }
    }

    /**
     * Applies things that should be applied before an item is crafted.
     */
    @NotNull
    public ItemStack ApplyTo(@NotNull ItemStack iSource, Player parseAS, boolean asPickup) {

        // AH
        ItemStack iResult = iSource;

        // Roll for tier
        if (hasName(asPickup)) {

            // Set Name
            iSource = OotilityCeption.RenameItem(iSource, getName(asPickup), parseAS, null);
            if(iSource == null) { iSource = iResult; }
        }

        // Return result
        return iSource;
    }

    /**
     * Applies such changes that should happen AFTER the item is crafted. Doesnt matter when picking up.
     */
    @NotNull
    public ItemStack ApplyPostTo(@NotNull ItemStack iSource, Player parseAS, boolean asPickup) {

        // AH
        ItemStack iResult = iSource;

        // Roll for tier
        if (hasRandomTier(asPickup)) {

            // Get Random Tier
            String rTier = getRandomTier(asPickup);

            // If exists
            if (GooPMMOItems.TierExists(rTier)) {

                // Apply
                iSource = GooPMMOItems.SetTier(iSource, rTier, null,null);
                if (iSource == null) { iSource = iResult; }

                // Has it per tier shit?
                ConverterPerTier cpt = getPerTierSettings(rTier, asPickup);

                // Contained?
                if (cpt != null) {

                    // Apply
                    iSource = cpt.ApplyTo(iSource);
                }
            }
        }

        // Return result
        return iSource;
    }
}
