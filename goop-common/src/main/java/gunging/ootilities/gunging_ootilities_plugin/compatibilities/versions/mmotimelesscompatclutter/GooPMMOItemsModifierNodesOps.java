package gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.mmotimelesscompatclutter;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.template.ModifierNode;
import net.Indyuce.mmoitems.api.item.template.NameModifier;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.NameData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GooPMMOItemsModifierNodesOps {
    public static ArrayList<String> getGlobalModifierNames() {

        ArrayList<String> ret = new ArrayList<>();

        for (ModifierNode mod : MMOItems.plugin.getTemplates().getModifierNodes()) { ret.add(mod.getId()); }

        return ret;
    }
    @Nullable
    public static ItemStack ModifierOperation(@Nullable String rawModifier, @Nullable ItemStack mmo, boolean useGlobl, boolean chances, @Nullable RefSimulator<String> logger) {
        if (rawModifier == null || mmo == null) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Unspecified modifier or item. ");
            return null; }

        // Convert into MMOItem
        if (!GooPMMOItems.IsMMOItem(mmo)) {

            // Maybe global ones yeah
            if (useGlobl) {
                mmo = GooPMMOItems.ConvertVanillaToMMOItem(mmo);

            } else {
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "This " + OotilityCeption.GetItemName(mmo) + " is not an MMOItem, so it has no modifiers, enable \u00a7buse-global\u00a77 to use this command with this.");
                return null;
            }
        }

        MMOItemTemplate template = MMOItems.plugin.getTemplates().getTemplate(NBTItem.get(mmo));

        // Sleap
        if (template == null && !useGlobl) {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "This " + OotilityCeption.GetItemName(mmo) + " is an MMOItem but the template is not loaded, was it deleted? GooP cannot find its modifiers, enable \u00a7buse-global\u00a77 to use this command with this.");
            return null;
        }

        boolean dummyTemplate = (template == null);
        if (template == null) { template = new MMOItemTemplate(Type.TOOL, "HX_MÑP"); }

        boolean random = rawModifier.equalsIgnoreCase("random");
        boolean clear = rawModifier.equalsIgnoreCase("none");

        boolean modifierLocal = false;
        modifierLocal = template.hasModifier(rawModifier);
        boolean modifierExists = modifierLocal || (useGlobl && MMOItems.plugin.getTemplates().hasModifier(rawModifier));

        // Ay exist?
        if (!random && !clear && !modifierExists) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "There is no modifier of name \u00a73" + rawModifier + "\u00a77. ");
            return null; }

        NBTItem nbt = NBTItem.get(mmo);
        LiveMMOItem live = new LiveMMOItem(nbt);

        // Perform operation
        if (random) {

            // Shuffle
            List<ModifierNode> modifiers = new ArrayList<>(template.getModifiers().values());

            // Add all globally loaded ones
            if (useGlobl) { modifiers.addAll(MMOItems.plugin.getTemplates().getModifierNodes()); }

            // Ay exist?
            if (modifiers.isEmpty()) {
                if (dummyTemplate) {
                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "MMOItem \u00a7e" + template.getType().getId() + " " + template.getId() + "\u00a77 has\u00a7c no modifiers\u00a77, cant pick a random one thus. ");

                } else {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "There are\u00a7c no global modifiers\u00a77, cant pick a random one thus. ");
                }
                return null;
            }

            // Get Modifier
            ModifierNode modifier = null;
            if (chances) {

                // Select based on chances
                int breaker = 0;
                while (modifier == null && breaker < 400) { breaker++; Collections.shuffle(modifiers); for (ModifierNode mod : modifiers) { if (mod.rollChance()) { modifier = mod; break; } } }
                if (breaker >= 400) {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "This " + OotilityCeption.GetItemName(mmo) + "\u00a77 could\u00a7c not roll\u00a77 for any modifier after \u00a7b" + breaker + "\u00a77 iterations, chances must be too low. ");
                    return null;
                }

            } else {

                // Get one from the list, same chance all of them
                modifier = modifiers.get(OotilityCeption.GetRandomInt(0, modifiers.size() - 1));
            }

            UUID modUUID = UUID.randomUUID();

            /*CURRENT-MMOITEMS*/
            MMOItemBuilder freshBuilder = template.newBuilder();
            //YE-OLDEN-MMO//MMOItemBuilder freshBuilder = template.newBuilder(null);


            for (ItemStat stat : modifier.getItemData().keySet()) {

                // Randomize yeah
                StatData statData = modifier.getItemData().get(stat).randomize(freshBuilder);

                // Is this mergeable?
                if (stat.getClearStatData() instanceof Mergeable) {

                    // Apply onto Stat History
                    StatHistory hist = StatHistory.from(live, stat);

                    // Apply
                    hist.registerModifierBonus(modUUID, statData);

                } else {

                    // Set, there is no more.
                    live.setData(stat, statData);
                }
            }

            // Rename
            if (modifier.hasNameModifier()) {

                // Get name data
                StatHistory hist = StatHistory.from(live, ItemStats.NAME);

                // Create new Name Data
                NameModifier namemod = modifier.getNameModifier();
                NameData modName = new NameData("");

                // Include modifier information
                if (namemod.getType() == NameModifier.ModifierType.PREFIX) { modName.addPrefix(namemod.getFormat()); }
                if (namemod.getType() == NameModifier.ModifierType.SUFFIX) { modName.addSuffix(namemod.getFormat()); }

                // Register onto SH
                hist.registerModifierBonus(modUUID, modName);
            }

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Applied \u00a7b" + modifier.getId() + "\u00a77 to " + OotilityCeption.GetItemName(mmo) + "\u00a77. ");

        } else if (clear) {

            // Well I guess go through all stat histories
            for (StatHistory hist : live.getStatHistories()) {

                // Clear modifiers
                hist.clearModifiersBonus();
            }

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Cleared modifiers of " + OotilityCeption.GetItemName(mmo));

        } else {

            // Get Modifier
            ModifierNode modifier = modifierLocal ? template.getModifier(rawModifier) : MMOItems.plugin.getTemplates().getModifier(rawModifier);

            UUID modUUID = UUID.randomUUID();
            /*CURRENT-MMOITEMS*/MMOItemBuilder freshBuilder = template.newBuilder();
            //YE-OLDEN-MMO//MMOItemBuilder freshBuilder = template.newBuilder(null);

            for (ItemStat stat : modifier.getItemData().keySet()) {

                // Randomize yeah
                StatData statData = modifier.getItemData().get(stat).randomize(freshBuilder);

                // Is this mergeable?
                if (stat.getClearStatData() instanceof Mergeable) {


                    // Apply onto Stat History
                    StatHistory hist = StatHistory.from(live, stat);

                    // Apply
                    hist.registerModifierBonus(modUUID, statData);

                } else {

                    // Set, there is no more.
                    live.setData(stat, statData);
                }
            }

            // Rename
            if (modifier.hasNameModifier()) {

                // Get name data
                StatHistory hist = StatHistory.from(live, ItemStats.NAME);

                // Create new Name Data
                NameModifier namemod = modifier.getNameModifier();
                NameData modName = new NameData("");

                // Include modifier information
                if (namemod.getType() == NameModifier.ModifierType.PREFIX) { modName.addPrefix(namemod.getFormat()); }
                if (namemod.getType() == NameModifier.ModifierType.SUFFIX) { modName.addSuffix(namemod.getFormat()); }

                // Register onto SH
                hist.registerModifierBonus(modUUID, modName);
            }

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Applied \u00a7b" + modifier.getId() + "\u00a77 to " + OotilityCeption.GetItemName(mmo));
        }

        // Yeah that's it
        return live.newBuilder().build();
    }
}
