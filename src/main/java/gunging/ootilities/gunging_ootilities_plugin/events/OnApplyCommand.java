package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.GungingOotilities;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ApplicableMask;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypeNames;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypes;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConvertingReason;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import net.Indyuce.mmoitems.api.interaction.GemStone;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.data.*;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.NameData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class OnApplyCommand implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void OnVillagerTalk(PlayerInteractEntityEvent event) {

        /*
         * Villager? :flushed:
         */
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Merchant)) { return; }

        // Cast it
        Merchant merchant = (Merchant) entity;
        for (int m = 0; m < merchant.getRecipes().size(); m++) {
            MerchantRecipe recipe = merchant.getRecipe(m);

            ArrayList<ItemStack> bakedIngredients = new ArrayList<>();
            for (ItemStack ingredient : recipe.getIngredients()) {

                ItemStack converted = convertOrNull(ingredient, event.getPlayer(), ConvertingReason.TRADED, null);
                if (converted == null) { bakedIngredients.add(ingredient); continue; }

                //TRD//OotilityCeption.Log("\u00a78RELOAD\u00a79 IE\u00a77 Replacing Recipe ingredient\u00a79 #" + m);
                bakedIngredients.add(converted);
            }

            RefSimulator<ConverterPerTier> cptRet = new RefSimulator<>(null);
            ItemStack bakedResult = convertOrNull(recipe.getResult(), event.getPlayer(), ConvertingReason.TRADED, cptRet);
            if (bakedResult != null) {
                //TRD// OotilityCeption.Log("\u00a78RELOAD\u00a79 IE\u00a77 Replacing Recipe at\u00a79 #" + m);

                // If the result is enchanted... we must  remove this recipe entirely!
                MerchantRecipe newRecipe = new MerchantRecipe(bakedResult, recipe.getUses(), recipe.getMaxUses(), recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(), recipe.getDemand(), recipe.getSpecialPrice(), recipe.shouldIgnoreDiscounts());

                // Valid tier thing?
                if (cptRet.getValue() != null) {
                    //TRD// OotilityCeption.Log("\u00a78RELOAD\u00a79 IE\u00a77 Evaluating CPT for tier \u00a79" + cptRet.getValue().getTier());

                    // Adjust ingredient prices
                    RefSimulator<ItemStack> first = new RefSimulator<>(null); if (bakedIngredients.size() > 0) { first.setValue(bakedIngredients.get(0)); }
                    RefSimulator<ItemStack> second = new RefSimulator<>(null); if (bakedIngredients.size() > 1) { second.setValue(bakedIngredients.get(1)); }

                    //TRD// OotilityCeption.Log("\u00a78RELOAD\u00a79 IE\u00a77 Input #1: " + OotilityCeption.GetItemName(first.getValue(), true));
                    //TRD// OotilityCeption.Log("\u00a78RELOAD\u00a79 IE\u00a77 Input #2: " + OotilityCeption.GetItemName(second.getValue(), true));

                    cptRet.getValue().adjustPrice(first, second);

                    //TRD// OotilityCeption.Log("\u00a78RELOAD\u00a79 IE\u00a77 Result #1: " + OotilityCeption.GetItemName(first.getValue(), true));
                    //TRD// OotilityCeption.Log("\u00a78RELOAD\u00a79 IE\u00a77 Result #2: " + OotilityCeption.GetItemName(second.getValue(), true));

                    // Take away first two
                    bakedIngredients.remove(0);
                    bakedIngredients.remove(0);
                    bakedIngredients.add(0, second.getValue());
                    bakedIngredients.add(0, first.getValue());
                }

                // Yes
                newRecipe.setIngredients(bakedIngredients);

                // Reset recipe and re-evaluate
                merchant.setRecipe(m, newRecipe);
                continue;
            }

            // Set ingredients
            recipe.setIngredients(bakedIngredients);
        }
    }

    @Nullable ItemStack convertOrNull(@Nullable ItemStack item, @Nullable Player p, @NotNull ConvertingReason reason, @Nullable RefSimulator<ConverterPerTier> cptRet) {

        if (OotilityCeption.IsAirNullAllowed(item)) { return null; }

        // If the item picked up is marked as convertible
        RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);
        if (!ConverterTypes.IsConvertable(item, convName)) { return null; }

        // If it is not a MMOItem
        if (GooPMMOItems.IsMMOItem(item)) { return null; }

        // Yes, result
        return FullConvert(item, ConverterTypeSettings.PertainingTo(convName.getValue()), p, reason, cptRet);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnLootGen(LootGenerateEvent event) {

        // If it wasn't cancelled and the picker upper was a player
        if (event.isCancelled()) { return; }

        //LGN//OotilityCeption.Log("\u00a78CONVERTER\u00a76 LG\u00a77 Generating Loot!");
        //LGN//OotilityCeption.Log("\u00a78CONVERTER\u00a76 LG\u00a77 Entity:\u00a7e " + (event.getEntity() == null ? "null" : event.getEntity().getType().toString()));
        //LGN//OotilityCeption.Log("\u00a78CONVERTER\u00a76 LG\u00a77 Killer:\u00a7e " + (event.getLootContext().getKiller() == null ? "null" : event.getLootContext().getKiller().getType().toString()));
        //LGN//OotilityCeption.Log("\u00a78CONVERTER\u00a76 LG\u00a77 Holder Entity:\u00a7e " + (event.getInventoryHolder() instanceof Entity ? ((Entity) event.getInventoryHolder()).getType().toString() : null));
        //LGN//OotilityCeption.Log("\u00a78CONVERTER\u00a76 LG\u00a77 Holder Block:\u00a7e " + (event.getInventoryHolder() instanceof BlockState ? ((BlockState) event.getInventoryHolder()).getType().toString() : null));
        //LGN//OotilityCeption.Log("\u00a78CONVERTER\u00a76 LG\u00a77 Location:\u00a7e " + OotilityCeption.BlockLocation2String(event.getLootContext().getLocation()));
        //LGN//OotilityCeption.Log("\u00a78CONVERTER\u00a76 LG\u00a77 Looted:\u00a7e " + (event.getLootContext().getLootedEntity() == null ? "null" : event.getLootContext().getLootedEntity().getType().toString()));

        if (event.getLootContext().getKiller() != null) { return; }
        if (event.getEntity() != null) { return; }

        Player parser = null;

        // Players in world...
        Location loc = event.getLootContext().getLocation();
        double bestDistance = 30;
        for (Player player : loc.getWorld().getNearbyPlayers(loc, 30)) {
            double currentDistance = player.getLocation().distance(loc);

            // Are they closer?
            if (currentDistance < bestDistance) {

                // Accept
                bestDistance = currentDistance;
                parser = player;
            }
        }

        ArrayList<ItemStack> convertedItems = new ArrayList<>();

        // For every item
        for (ItemStack item : event.getLoot()) {

            ItemStack converted = convertOrNull(item, parser, ConvertingReason.LOOT_GEN, null);
            if (converted == null) { convertedItems.add(item); } else { convertedItems.add(converted); }
        }

        // Set items
        event.setLoot(convertedItems);
    }

    @NotNull ItemStack DisplayConvert(@NotNull ItemStack stacc, @Nullable ConverterTypeSettings set, @Nullable Player p, @NotNull ConvertingReason asPickup) {
        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 Converting for Display");

        // Convert
        ItemStack result = GooPMMOItems.ConvertVanillaToMMOItem(
                stacc,
                ConverterTypes.typePrefix,
                ConverterTypes.GenerateConverterID(stacc.getType(), set == null ? null : set.getNullTierName(asPickup)));

        // Obtain internals
        RefSimulator<String> miID = new RefSimulator<>(null), miType = new RefSimulator<>(null);
        GooPMMOItems.GetMMOItemInternals(result, miType, miID);
        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 Produced\u00a7c " + miType.getValue() + " " + miID.getValue());

        // Counter VANILLA conversion
        if (!GooPMMOItems.VANILLA_MIID.equals(miID.getValue())) {

            // Is there one of such name already?
            Type mType = MMOItems.plugin.getTypes().get(miType.getValue());
            if (mType != null) {

                // Find loaded MMOItem
                MMOItem browse = MMOItems.plugin.getMMOItem(mType, miID.getValue());

                // Found the appropriate MMOItem?
                if (browse != null) {

                    //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 True Existed. \u00a7aDisplaying");

                    // Build for display
                    return browse.newBuilder().build(true);
                }
            }
        }

        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 True nonexisting, \u00a7bApplying");

        // Transform Result
        if (set != null) {

            // Apply Pre
            result = set.applyDisplayTo(result, p, asPickup);
        }

        return result;
    }

    /**
     *
     * @param netheritizedResult Basically the item stack but funny converted into nitherite while keeping its stats constant.
     *
     * @param original Original item being smith before becoming nitherite
     *
     * @param player Player, I, guess
     *
     * @return The item but upgraded into nitherite.
     */
    @NotNull ItemStack FullSmith(@NotNull ItemStack netheritizedResult, @Nullable ConverterTypeSettings set, @NotNull ItemStack original, @NotNull Player player) {
        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 Smithing for Full");


        // Is there one of such name already?
        Type mType = MMOItems.plugin.getTypes().get(GooPMMOItems.GetMMOItemType(netheritizedResult, "no"));
        if (mType != null) {

            RefSimulator<String> tierScry = new RefSimulator<>(null);
            GooPMMOItems.SetTier(netheritizedResult, null, tierScry, null);
            String actualTier = tierScry.GetValue();
            if ("none".equals(actualTier)) { actualTier = null; }

            String mID = ConverterTypes.GenerateConverterID(netheritizedResult.getType(), actualTier);

            //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 Identified\u00a7c " + mType.getId() + " " + mID);


            // Counter VANILLA conversion
            if (!GooPMMOItems.VANILLA_MIID.equals(mID)) {

                // Find loaded MMOItem
                MMOItem browse = MMOItems.plugin.getMMOItem(mType, mID);

                // Found the appropriate MMOItem?
                if (browse != null) {
                    //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 True Existed. \u00a7aBuilding");
                    ReadMMOItem mmo = ConverterTypes.smithGemstones ? new LiveMMOItem(NBTItem.get(original)) : new VolatileMMOItem(NBTItem.get(original));

                    // Transfer stuff (Upgrades first because they are used when recalculating StatHistories)
                    if (ConverterTypes.smithUpgrades) {

                        // Get enchants data
                        UpgradeData upgr2 = (UpgradeData) browse.getData(ItemStats.UPGRADE);
                        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a7aUPGRADE\u00a77 Reading level (\u00a7e" + (upgr2 != null) + "\u00a77)");

                        if (upgr2 != null) {
                            //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a7aUPGRADE\u00a77 Transferring level \u00a7e" + mmo.getUpgradeLevel());

                            // Set level pog-yoo
                            upgr2.setLevel(mmo.getUpgradeLevel());
                        }
                    }

                    // Transfer stuff (Upgrades first because they are used when recalculating StatHistories)
                    if (ConverterTypes.smithGemstones) {

                        // Get enchants data
                        ArrayList<MMOItem> gemstonesTherein = mmo.extractGemstones();
                        ArrayList<ItemStack> remainingStones = new ArrayList<>();
                        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a7bGEMS\u00a77 Transferring stones \u00a73x" + gemstonesTherein.size());

                        for (MMOItem m : gemstonesTherein) {
                            //CNV// MMOItems.log("\u00a76 +\u00a77 Fitting \u00a7e" + m.getType().toString() + " " + m.getId());

                            // What sockets are available?
                            GemSocketsData genGemstones = (GemSocketsData) browse.getData(ItemStats.GEM_SOCKETS);

                            // Abort lol
                            if (genGemstones == null || (genGemstones.getEmptySlots().size() == 0)) {
                                //GEM// MMOItems.log("\u00a7c !!\u00a77 Dropping: No more empty slots in target ");

                                // Just keep as 'remaining'
                                remainingStones.add(m.newBuilder().build());
                                continue; }

                            // Ok proceed
                            GemStone asGem = new GemStone(player, m.newBuilder().buildNBT());

                            // Put
                            GemStone.ApplyResult res = asGem.applyOntoItem(browse, browse.getType(), "", false, true);

                            // None?
                            if (res.getType() == GemStone.ResultType.SUCCESS && (res.getResultAsMMOItem() != null)) {

                                // Success that's nice
                                browse = res.getResultAsMMOItem();
                                //GEM// MMOItems.log("\u00a7a W\u00a77 Socketed! ");

                                // Didn't fit L
                            } else {
                                //GEM// MMOItems.log("\u00a7e !!\u00a77 Dropping: Does not fit socket ");
                                remainingStones.add(m.newBuilder().build()); } }

                        // Give the gems back
                        for (ItemStack drop : player.getInventory().addItem(
                                remainingStones.toArray(new ItemStack[0])).values()) {

                            // Not air right
                            if (SilentNumbers.isAir(drop)) { continue; }

                            // Drop to the world
                            player.getWorld().dropItem(player.getLocation(), drop); }
                    }

                    // Transfer stuff
                    if (ConverterTypes.smithEnchants) {
                        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a7dENCHANTS\u00a77 Separating Enchants");

                        // Make sure to separate player enchantments
                        Enchants.separateEnchantments(mmo);

                        // Get enchants data
                        StatHistory originalHist = StatHistory.from(mmo, ItemStats.ENCHANTS);
                        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a7dENCHANTS\u00a77 Transferring enchants \u00a75x" + originalHist.getExternalData());

                        // Get enchantment history of new
                        StatHistory hist = StatHistory.from(browse, ItemStats.ENCHANTS);

                        for (StatData ench : originalHist.getExternalData()) {
                            if (!(ench instanceof EnchantListData)) { continue; }

                            // Merge as ExSH
                            hist.registerExternalData(ench);
                        }

                        // The law requires me to merge this, too
                        if (!((Mergeable) originalHist.getOriginalData()).isClear()) { hist.registerExternalData(originalHist.getOriginalData()); }

                        // Recalculate with upgrade level
                        browse.setData(ItemStats.ENCHANTS, hist.recalculate(browse.getUpgradeLevel()));
                    }

                    // Build for display
                    return browse.newBuilder().build();
                }
            }
        }
        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 True nonexisting, \u00a7bCombinating");

        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73NE\u00a77 Nitherite Upgrade");

        // Ok this people are upgrading this sh to nitherite, add power!
        RefSimulator<Double>
                vDamageRef = new RefSimulator<>(0.0),
                vSpeedRef = new RefSimulator<>(0.0),
                vArmorRef = new RefSimulator<>(0.0),
                vArmorTRef = new RefSimulator<>(0.0),
                mKResRef = new RefSimulator<>(0.0);
        RefSimulator<String> tNameRef = new RefSimulator<>("MISCELLANEOUS");

        // Get those values
        OotilityCeption.GatherDefaultVanillaAttributes(original.getType(), netheritizedResult.getType(), tNameRef, vDamageRef, vSpeedRef, vArmorRef, vArmorTRef, mKResRef);

        // Get Live
        LiveMMOItem mmo = new LiveMMOItem(netheritizedResult);

        // Any bonuses?
        if (vDamageRef.GetValue()   != 0) {
            DoubleData val = (DoubleData) mmo.getData(ItemStats.ATTACK_DAMAGE);
            double current = val != null ? val.getValue() : 0;
            mmo.setData(ItemStats.ATTACK_DAMAGE,        new DoubleData(current + vDamageRef.GetValue()));
        }
        if (vSpeedRef.GetValue()    != 0) {
            DoubleData val = (DoubleData) mmo.getData(ItemStats.ATTACK_SPEED);
            double current = val != null ? val.getValue() : 0;
            mmo.setData(ItemStats.ATTACK_SPEED,        new DoubleData(current + vSpeedRef.GetValue()));
        }
        ItemStat armorStat = Gunging_Ootilities_Plugin.useMMOLibDefenseConvert ? ItemStats.DEFENSE : ItemStats.ARMOR;
        if (vArmorRef.GetValue()    != 0) {
            DoubleData val = (DoubleData) mmo.getData(armorStat);
            double current = val != null ? val.getValue() : 0;
            mmo.setData(armorStat,        new DoubleData(current + vArmorRef.GetValue()));
        }
        if (vArmorTRef.GetValue()   != 0) {
            DoubleData val = (DoubleData) mmo.getData(ItemStats.ARMOR_TOUGHNESS);
            double current = val != null ? val.getValue() : 0;
            mmo.setData(ItemStats.ARMOR_TOUGHNESS,        new DoubleData(current + vArmorTRef.GetValue()));
        }
        if (mKResRef.GetValue()     != 0) {
            DoubleData val = (DoubleData) mmo.getData(ItemStats.KNOCKBACK_RESISTANCE);
            double current = val != null ? val.getValue() : 0;
            mmo.setData(ItemStats.KNOCKBACK_RESISTANCE,        new DoubleData(current + mKResRef.GetValue()));
        }
        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73VL\u00a77 Damage:\u00a7a " + vDamageRef.getValue());
        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73VL\u00a77 Speed:\u00a7a " + vSpeedRef.getValue());
        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73VL\u00a77 Armor:\u00a7a " + vArmorRef.getValue() + "\u00a78 (\u00a72" + armorStat.getName() + "\u00a78/" + Gunging_Ootilities_Plugin.useMMOLibDefenseConvert + ")");
        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73VL\u00a77 Tough:\u00a7a " + vArmorTRef.getValue());
        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73VL\u00a77 MKRes:\u00a7a " + mKResRef.getValue());

        // Replace name ALV
        tNameRef.SetValue(OotilityCeption.GetItemName(new ItemStack(netheritizedResult.getType())));
        NameData oldNameData = (NameData) mmo.getData(ItemStats.NAME);
        NameData newData = oldNameData == null ? new NameData(tNameRef.getValue()) : oldNameData;
        if (oldNameData != null) { newData.setString(tNameRef.getValue()); }
        mmo.setData(ItemStats.NAME, newData);

        // Build that shit
        return mmo.newBuilder().build();
    }
    @NotNull ItemStack FullConvert(@NotNull ItemStack stacc, @Nullable ConverterTypeSettings set, @Nullable Player player, @NotNull ConvertingReason asPickup) {
        return FullConvert(stacc, set, player, asPickup, null);
    }
    @NotNull ItemStack FullConvert(@NotNull ItemStack stacc, @Nullable ConverterTypeSettings set, @Nullable Player player, @NotNull ConvertingReason asPickup, @Nullable RefSimulator<ConverterPerTier> cptRet) {
        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 Converting for Full");

        String preChosenTier = null;
        if (set != null) {

            // Roll for tier
            if (set.hasRandomTier(asPickup)) {

                // Get Random Tier
                preChosenTier = set.getRandomTier(asPickup);
            }
        }

        // Convert
        ItemStack result = GooPMMOItems.ConvertVanillaToMMOItem(
                stacc,
                ConverterTypes.typePrefix,
                ConverterTypes.GenerateConverterID(stacc.getType(), preChosenTier));


        // Obtain internals
        RefSimulator<String> miID = new RefSimulator<>(null), miType = new RefSimulator<>(null);
        GooPMMOItems.GetMMOItemInternals(result, miType, miID);
        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 Produced\u00a7c " + miType.getValue() + " " + miID.getValue());

        if (!GooPMMOItems.VANILLA_MIID.equals(miID.getValue())) {

            // Is there one of such name already?
            Type mType = MMOItems.plugin.getTypes().get(miType.getValue());
            if (mType != null) {

                // Find loaded MMOItem
                MMOItem browse = MMOItems.plugin.getMMOItem(mType, miID.getValue());

                // Found the appropriate MMOitem?
                if (browse != null) {
                    //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 True Existed. \u00a7aBuilding");

                    // Build for display
                    return browse.newBuilder().build();
                }
            }
        }
        //CNV//OotilityCeption.Log("\u00a78CONVERTER \u00a73CONVERT\u00a77 True nonexisting, \u00a7bApplying");

        // Transform Result
        if (set != null) {

            // Apply Pre
            result = set.applyDisplayTo(result, player, asPickup);

            // Apply Post
            result = set.applyTo(result, player, asPickup, cptRet);
        }

        return result;
    }

    @EventHandler
    public void GemstoneApply(ApplyGemStoneEvent event) {

        // Get command
        String command = OnApplyCommand.gemStoneOnApplies.get(event.getPlayer().getUniqueId());

        // Registered?
        if (command != null) {
            //DBG//OotilityCeption.Log("Retrieved Gem OnApply: \u00a7e" + command);

            // UUUh Retrieve Provided
            Integer providedSlot = OnApplyCommand.gemStoneProvidedSlots.get(event.getPlayer().getUniqueId());

            // Apply GemStones then modify item
            (new BukkitRunnable() {
                public void run() {

                    // Run
                    OnApplyCommand.ExecuteOnApply(command, event.getPlayer(), providedSlot, OnApplyCommand.gemStoneTarget.get(event.getPlayer().getUniqueId()));
                }

            }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);

            // No OnAPply
        }

        //DBG// else { OotilityCeption.Log("\u00a7cGemStone had no OnApply"); }

        // Clear
        OnApplyCommand.gemStoneOnApplies.remove(event.getPlayer().getUniqueId());
    }

    public static HashMap<UUID, Integer> gemStoneProvidedSlots = new HashMap<>();
    public static HashMap<UUID, String> gemStoneOnApplies = new HashMap<>();
    public static HashMap<UUID, ItemStack> gemStoneTarget = new HashMap<>();
    @EventHandler(priority = EventPriority.LOW)
    public void OnApply(InventoryClickEvent event) {

        // Inminent cancellation
        if (!(event.getWhoClicked() instanceof Player)) { return; }
        if (event.getClickedInventory() == null) { return; }

        //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Hat Equip Test");
        //HAT//OotilityCeption. Log("    §8>§aϿ§8<§7 Inventory Type: \u00a7f" + event.getClickedInventory().getType().toString());
        //HAT//OotilityCeption. Log("    §8>§eϿ§8<§7 Action: \u00a7f" + event.getAction().toString());
        //HAT//OotilityCeption. Log("    §8>§cϿ§8<§7 Slot Type: \u00a7f" + event.getView().getSlotType(event.getSlot()).toString());
        //HAT//OotilityCeption. Log("    §8>§bϿ§8<§7 Inventory View Type: \u00a7f" + event.getView().getType().toString());
        //HAT//OotilityCeption. Log("    §8>§0Ͽ§8<§7 Inventory Slot: \u00a7f" + event.getSlot());
        if (!event.getView().getType().equals(InventoryType.CRAFTING)) { return; }
        if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) { return; }

        // Normalize into non-nulls
        ItemStack current = new ItemStack(Material.AIR);
        if (event.getCurrentItem() != null) { current = event.getCurrentItem(); }
        ItemStack cursor = new ItemStack(Material.AIR);
        if (event.getCursor() != null) { cursor = event.getCursor(); }
        //HAT//OotilityCeption. Log("    §8>§dϿ§8<§7 Current Item: \u00a7f" + OotilityCeption.GetItemName(current));
        //HAT//OotilityCeption. Log("    §8>§fϿ§8<§7 Cursor Item: \u00a7f" + OotilityCeption.GetItemName(cursor));

        // Get Player who did
        Player player = (Player)event.getWhoClicked();

        // Get the action
        if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {

            // If its not air, and the inventory is the normal kind I guess
            if (!OotilityCeption.IsAir((current.getType()))) {
                //DBG//OotilityCeption.Log("Apply Attempt: \u00a7e" + OotilityCeption.GetItemName(cursor) + "\u00a77 on \u00a7b" + OotilityCeption.GetItemName(current));

                // If the item was an MMOItem
                if (GooPMMOItems.IsMMOItem(cursor)) {
                    //DBG//OotilityCeption.Log("\u00a7aAppliant is MMOItem");

                    // If it can be used
                    if (GooPMMOItems.MeetsRequirements(player, cursor, true)) {
                        //DBG//OotilityCeption.Log("\u00a7aApplicant meets RPG Reqs");

                        String hasApplyFunction = GooPMMOItems.GetStringStatValue(cursor, GooPMMOItems.APPLICABLE_COMMAND, null, false);

                        // If it can do any of these actions
                        if (hasApplyFunction != null) {
                            //DBG//OotilityCeption.Log("\u00a7aFound Apply Function: \u00a73" + hasApplyFunction);

                            // Boolean for success
                            boolean success = false;
                            NBTItem item = NBTItem.get(cursor);

                            // If if item has an apply mask
                            String maskName = GooPMMOItems.GetStringStatValue(cursor, GooPMMOItems.APPLICABLE_MASK, null, false);
                            if (maskName != null) {
                                //DBG//OotilityCeption.Log("\u00a77Had Mask: \u00a7b" + maskName);

                                // If exists
                                ApplicableMask targetMask = ApplicableMask.getMask(maskName);
                                if (targetMask != null) {
                                    //DBG//OotilityCeption.Log("\u00a7bFound Mask");

                                    // If MMOItem
                                    if (GooPMMOItems.IsMMOItem(current)) {

                                        // What type is it
                                        NBTItem itm = NBTItem.get(current);
                                        String type = itm.getString("MMOITEMS_ITEM_TYPE");

                                        // DOes it match?
                                        success = targetMask.AppliesTo(type);
                                        //DBG//OotilityCeption.Log("\u00a77Mask Match? \u00a7c" + success);
                                    }
                                }

                            // Is it a gemstone tho
                            } else if (GooPMMOItems.IsGemstone(item, player)) {
                                //DBG//OotilityCeption.Log("\u00a7bIs Gemstone");

                                // Add
                                gemStoneProvidedSlots.put(event.getWhoClicked().getUniqueId(), event.getSlot());
                                gemStoneOnApplies.put(event.getWhoClicked().getUniqueId(), hasApplyFunction);
                                gemStoneTarget.put(event.getWhoClicked().getUniqueId(), current);

                            // So it didn't attempt to have a Mask, and it wasn't a GemStone
                            } else {
                                //DBG//OotilityCeption.Log("\u00a7bNo Mask Restriction");

                                // Will not be restricted - success
                                success = true;
                            }

                            // Success so far, test for APPLIANT TIMES
                            if (success) {

                                // Is it capped?
                                Double onApplyLimti = GooPMMOItems.GetDoubleStatValue(cursor, GooPMMOItems.APPLICABLE_LIMIT, null, false);

                                // If limited
                                if (onApplyLimti != null) {
                                    //DBG//OotilityCeption.Log("Found Limt \u00a7e" + onApplyLimti);

                                    // Get Appliant Class
                                    String onApplyClass = GooPMMOItems.GetStringStatValue(cursor, GooPMMOItems.APPLICABLE_CLASS, null, false);
                                    if (onApplyClass == null) {

                                        // Get Internals
                                        RefSimulator<String> mType = new RefSimulator<>(""), mID = new RefSimulator<>("");
                                        GooPMMOItems.GetMMOItemInternals(cursor, mType, mID);

                                        // Build
                                        onApplyClass = mType + " " + mID;
                                    }
                                    //DBG//OotilityCeption.Log("Found Class \u00a76" + onApplyClass);

                                    // Convert current I guess
                                    if (!GooPMMOItems.IsMMOItem(current)) {
                                        current = GooPMMOItems.ConvertVanillaToMMOItem(current);
                                        //DBG//OotilityCeption.Log("\u00a76 +\u00a77 Converted");
                                        }

                                    if (GooPMMOItems.IsMMOItem(current)) {

                                        //region Get Existing
                                        ArrayList<String> onApplied = new ArrayList<>();
                                        ArrayList<String> fiApplied = new ArrayList<>();

                                        VolatileMMOItem asV = new VolatileMMOItem(NBTItem.get(current));

                                        if (asV.hasData(GooPMMOItems.APPLICABLE_TIMES)) {

                                            onApplied = new ArrayList<>(((StringListData) asV.getData(GooPMMOItems.APPLICABLE_TIMES)).getList());
                                            StatHistory.from(asV, GooPMMOItems.APPLICABLE_TIMES);

                                        }
                                        //DBG// else { OotilityCeption.Log("\u00a76 +\u00a77 Created New"); }
                                        //endregion

                                        // New item
                                        boolean asNew = true;

                                        // Cook
                                        for (String str : onApplied) {
                                            // Get obs
                                            //DBG//OotilityCeption.Log("\u00a7e>>>\u00a77 " + str);

                                            // Does it start with the key?
                                            if (str.startsWith(onApplyClass)) {

                                                // Noep
                                                asNew = false;

                                                // Get number
                                                int lastSpace = str.lastIndexOf(" ");
                                                String asNumber = str.substring(lastSpace + 1);
                                                //DBG//OotilityCeption.Log("\u00a76   >\u00a77 Yes, Stripped \u00a78" + asNumber);

                                                // It MUST parse right
                                                if (OotilityCeption.IntTryParse(asNumber)) {
                                                    //DBG//OotilityCeption.Log("\u00a76   >\u00a77 Number Working");

                                                    // Calculate
                                                    int times = OotilityCeption.ParseInt(asNumber);

                                                    // Fail if it has been exceeded
                                                    if (times >= onApplyLimti) {
                                                        //DBG//OotilityCeption.Log("\u00a76   >\u00a77 Hit Limit");

                                                        // Fail
                                                        success = false;

                                                        // Add
                                                        fiApplied.add(str);

                                                    } else {

                                                        // Cook
                                                        times++;

                                                        // Build new
                                                        String newOnApplyClass = onApplyClass + " " + times;
                                                        //DBG//OotilityCeption.Log("\u00a76   >\u00a77 Increase into \u00a78" + newOnApplyClass);

                                                        // Add
                                                        fiApplied.add(newOnApplyClass);
                                                    }
                                                }
                                                
                                            // Not the string we was searching for
                                            } else {

                                                // Add
                                                fiApplied.add(str);
                                            }
                                        }

                                        // Hey it was nEw
                                        if (asNew) {
                                            //DBG//OotilityCeption.Log("\u00a76 +\u00a77 Initialized \u00a7e" + onApplyClass);

                                            // Add
                                            fiApplied.add(onApplyClass + " 1");

                                            // Fail if it has been exceeded
                                            if (1 > onApplyLimti) {
                                                //DBG//OotilityCeption.Log("\u00a76   >\u00a77 Hit Limit");

                                                // Fail
                                                success = false;
                                            }
                                        }

                                        // Build Item
                                        LiveMMOItem asNewNBT = new LiveMMOItem(NBTItem.get(current));

                                        // Get History
                                        StatHistory hist = StatHistory.from(asNewNBT, GooPMMOItems.APPLICABLE_TIMES);
                                        hist.setOriginalData(GooPMMOItems.APPLICABLE_TIMES.getClearStatData());
                                        hist.clearExternalData();
                                        hist.clearGemstones();
                                        hist.clearModifiersBonus();

                                        hist.registerExternalData(new StringListData(fiApplied));
                                        asNewNBT.setData(GooPMMOItems.APPLICABLE_TIMES, hist.recalculate(asNewNBT.getUpgradeLevel()));

                                        //DBG//OotilityCeption.Log("\u00a76 +\u00a77 Finished");

                                        // Get
                                        ItemStack asNewItm = asNewNBT.newBuilder().build();

                                        // Set
                                        event.setCurrentItem(asNewItm);
                                    }

                                }
                            }

                            // There was a success
                            if (success) {

                                // Execute
                                ExecuteOnApply(hasApplyFunction, (Player) event.getWhoClicked(), event.getSlot(), current);

                                // No More
                                event.setCancelled(true);

                                // Decrease usage if consumable
                                if (GooPMMOItems.IsConsumable(item, player) && !OotilityCeption.If(GooPMMOItems.GetBooleanStatValue(item, GooPMMOItems.APPLICABLE_CONSUME, null, false))) {
                                    //DBG//OotilityCeption.Log("\u00a73Consumed \u00a7b1");

                                    // Decrease uses I guess
                                    event.getCursor().setAmount(cursor.getAmount() - 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        // \/ \/ \/ \/ \/ \/ HATTABLE STAT STUFF \/ \/ \/ \/ \/ \/

        // Get Action?
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§a Quick Equip");

            // If its not air, and the inventory is the normal kind I guess
            if (!OotilityCeption.IsAir((current.getType()))) {
                //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Not Air");

                // If the item was an MMOItem
                if (GooPMMOItems.IsMMOItem(current)) {
                    //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Is MMOItem");

                    // If it can be used
                    if (GooPMMOItems.MeetsRequirements(player, current, true)) {
                        //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Requirements Met");

                        Boolean hattable = GooPMMOItems.GetBooleanStatValue(current, GooPMMOItems.HAT, null, false);
                        // IF it is hattable and player doesnt have stuff in helmet
                        if (hattable != null) {
                            //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Had Hattable");

                            boolean playerHasHelm = false;
                            if (player.getInventory().getHelmet() == null) {
                                //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Null Helmet");
                                playerHasHelm = false;
                            } else if (!OotilityCeption.IsAir(player.getInventory().getHelmet().getType())) {
                                //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Had Helmet");
                                playerHasHelm = true;
                            }

                            // If hattable
                            if (hattable && !playerHasHelm) {
                                //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§6 Appropiate to Equip");

                                // Create copy itemstack
                                ItemStack newHelmet = new ItemStack(current);

                                // I guess we destroy that itemstack
                                event.setCancelled(true);
                                event.getCurrentItem().setAmount(0);

                                // We force player into helmet
                                player.getInventory().setHelmet(newHelmet);

                                // Update inven
                                player.updateInventory();
                            }
                        }
                    }
                }
            }
        }

        // Ok what
        if (event.getAction() == InventoryAction.PLACE_ALL) {
            //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§a Manual Equip");

            // If its not air, and the inventory is the normal kind I guess
            if (!OotilityCeption.IsAir((cursor.getType()))) {
                //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Not Air");

                // If the item was an MMOItem
                if (GooPMMOItems.IsMMOItem(cursor)) {
                    //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Is MMOItem");

                    // If it can be used
                    if (GooPMMOItems.MeetsRequirements(player, cursor, true)) {
                        //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Requirements Met");

                        // If the slot was the helmet
                        if (event.getSlot() == 39) {
                            //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Correct Slot (helmet)");

                            Boolean hattable = GooPMMOItems.GetBooleanStatValue(cursor, GooPMMOItems.HAT, null, false);

                            // IF it is hattable and player doesnt have stuff in helmet
                            if (hattable != null) {
                                //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Was hattable");

                                boolean playerHasHelm = false;
                                if (player.getInventory().getHelmet() == null) {
                                    //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Null Helmet");
                                    playerHasHelm = false;
                                } else if (!OotilityCeption.IsAir(player.getInventory().getHelmet().getType())) {
                                    //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§7 Had Helmet");
                                    playerHasHelm = true;
                                }

                                // If hattable
                                if (hattable && !playerHasHelm) {
                                    //HAT//OotilityCeption. Log(" §8>§3Ͽ§8<§6 Appropiate to Equip");

                                    // Create copy itemstack
                                    ItemStack newHelmet = new ItemStack(cursor);

                                    // I guess we destroy that itemstack
                                    event.setCancelled(true);
                                    event.getCursor().setAmount(0);

                                    // We force player into helmet
                                    player.getInventory().setHelmet(newHelmet);

                                    // Update inven
                                    player.updateInventory();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void ExecuteOnApply(String cmd, Player player, Integer providedSlot, ItemStack asItem) {

        // Bake Slot if exists
        cmd = cmd.replace("%provided-slot%", String.valueOf(providedSlot));

        // Provide slot to mythicmobs
        if (cmd.toLowerCase().contains("runskillas")) {

            // Prepare placeholder
            GungingOotilities.providedSlot.put(player.getUniqueId(), providedSlot);
        }

        // Run command
        OotilityCeption.SendConsoleCommand(cmd, player, player, null, asItem);
    }

    @EventHandler
    public void OnItemPickup(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) { return; }

        // If it wasn't cancelled and the picker upper was a player
        if (!event.isCancelled()) {

            // Extract Name
            RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);

            // If the item picked up is marked as convertible
            if (ConverterTypes.IsConvertable(event.getItem().getItemStack(), convName)) {

                // If it is not a MMOItem
                if (!GooPMMOItems.IsMMOItem(event.getItem().getItemStack())) {

                    // Prepare result
                    ItemStack result = FullConvert(event.getItem().getItemStack(), ConverterTypeSettings.PertainingTo(convName.getValue()), (Player) event.getEntity(), ConvertingReason.PICKUP);

                    // Well, override it I suppose. Drop another entity with the qualifications
                    Item e = event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), result);
                    e.setVelocity(new Vector(0, 0, 0));
                    e.setPickupDelay(0);

                    // Cancel this event so player doesnt pick up original
                    event.setCancelled(true);

                    // Remove original
                    event.getItem().remove();
                }
            }
        }
    }

    @EventHandler public void OnSmithPrep(PrepareSmithingEvent event) {

        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73PR\u00a77 Prep Begin -----------------------------------------------");

        // Only players ffs
        if (!(event.getView().getPlayer() instanceof Player)) { return; }
        Player player = (Player) event.getView().getPlayer();
        ItemStack eventResultItem = event.getInventory().getResult();

        if (eventResultItem != null) {

            //SMH//OotilityCeption.Log("\u00a78Smith \u00a73PR\u00a77 Result Exist");

            // Extract Name
            RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);

            ItemStack item = event.getInventory().getInputEquipment();
            ItemStack ingot = event.getInventory().getInputMineral();

            // Diamond Upgrading Reqs
            boolean nitheriteIngot = ingot != null && ingot.getType() == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_INGOT, Material.COMMAND_BLOCK);
            boolean diamondPiece = item != null && OotilityCeption.IsDiamond(item.getType());
            String baseID = GooPMMOItems.GetMMOItemID(item, "no");
            boolean mIDMatch = (GooPMMOItems.VANILLA_MIID.equals(baseID) || baseID.startsWith("GENERIC"));
            //SMH//OotilityCeption.Log("\u00a78Smith \u00a73NE\u00a77 Diamond:\u00a7b " + diamondPiece + "\u00a77, Nitherite:\u00a75 " + nitheriteIngot + "\u00a77, MIID:\u00a73 " + mIDMatch);
            if (nitheriteIngot && diamondPiece && mIDMatch) {

                // Store
                craftPrep.put(player.getUniqueId(), eventResultItem.clone());

                ConverterTypeSettings set = null;
                if (ConverterTypes.IsConvertable(eventResultItem, convName)) { set = ConverterTypeSettings.PertainingTo(convName.getValue()); }

                // Build that shit
                ItemStack result = FullSmith(eventResultItem, set, item, player);

                // Store
                craftPrepResult.put(player.getUniqueId(), result.clone());

                //SMH//OotilityCeption.Log("\u00a78Smith \u00a73NENENE\u00a77 Showing...");

                // Well, override it I suppose. Drop another entity with the qualifications
                event.getInventory().setResult(result);

                // Run
                (new BukkitRunnable() {
                    public void run() {

                        // Well, override it I suppose. Drop another entity with the qualifications
                        event.getInventory().setResult(result);

                        //SMH//OotilityCeption.Log("\u00a78Smith \u00a73NENENE\u00a77 Shown...");

                    }

                }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);

            } else

            // If the item picked up is marked as convertible
            if (ConverterTypes.IsConvertable(eventResultItem, convName)) {

                //SMH//OotilityCeption.Log("\u00a78Smith \u00a7cSNOOZE\u00a77 Snoozing...");

                // If it is not a MMOItem
                if (!GooPMMOItems.IsMMOItem(eventResultItem)) {

                    // Store
                    craftPrep.put(player.getUniqueId(), eventResultItem.clone());

                    // Prepare result; Apply the pre craft I suppose
                    ItemStack result = DisplayConvert(eventResultItem, ConverterTypeSettings.PertainingTo(convName.getValue()), player, ConvertingReason.CRAFT);

                    // Store
                    craftPrepResult.put(player.getUniqueId(), result.clone());

                    //SMH//OotilityCeption.Log("\u00a78Smith \u00a7cSNOOZE\u00a77 Overriding...");

                    // Well, override it I suppose. Drop another entity with the qualifications
                    event.getInventory().setResult(result);
                }
            }
        }
    }
    @EventHandler public void OnSmith(SmithItemEvent event) {

        //SMH//OotilityCeption.Log("\u00a78Smith \u00a76EV\u00a77 Event Begin");

        // Only players ffs
        if (!(event.getView().getPlayer() instanceof Player)) { return; }
        Player player = (Player) event.getView().getPlayer();

        // Get
        ItemStack originalResult = craftPrep.get(player.getUniqueId());
        craftPrep.remove(player.getUniqueId());

        // Get predicted result
        ItemStack postConvertedResult = craftPrepResult.get(player.getUniqueId());
        craftPrepResult.remove(player.getUniqueId());
        if (event.isCancelled()) { return; }

        if (originalResult != null) {

            //SMH//OotilityCeption.Log("\u00a78Smith \u00a76EV\u00a77 Found Result");

            // Extract Name
            RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);

            // If the item picked up is marked as convertible
            if (ConverterTypes.IsConvertable(originalResult, convName)) {

                ItemStack item = event.getInventory().getInputEquipment();
                ItemStack ingot = event.getInventory().getInputMineral();

                //SMH//OotilityCeption.Log("\u00a78Smith \u00a76EV\u00a77 Convertible Result");

                // Diamond Upgrading Reqs
                boolean nitheriteIngot = ingot != null && ingot.getType() == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_INGOT, Material.COMMAND_BLOCK);
                boolean diamondPiece = item != null && OotilityCeption.IsDiamond(item.getType());
                String baseID = GooPMMOItems.GetMMOItemID(item, "no");
                if (nitheriteIngot && diamondPiece && (GooPMMOItems.VANILLA_MIID.equals(baseID) || baseID.startsWith("GENERIC"))) {

                    //SMH//OotilityCeption.Log("\u00a78Smith \u00a76NE\u00a77 Netherite Upgrade");

                    // The calculations were already performed.
                    event.getInventory().setResult(postConvertedResult);

                } else

                    // If it is not a MMOItem
                if (!GooPMMOItems.IsMMOItem(originalResult)) {

                    // Capture those already crafted
                    HashMap<Integer, ItemStack> heldItems = ScourgeForConverted(player.getInventory(), postConvertedResult);

                    // Get Conversion Setting to Apply
                    ConverterTypeSettings set = ConverterTypeSettings.PertainingTo(convName.getValue());

                    // Store
                    crafters.add(player);
                    craftHeld.put(player.getUniqueId(), heldItems);
                    craftSearch.put(player.getUniqueId(), postConvertedResult.clone());
                    craftPrepd.put(player.getUniqueId(), originalResult.clone());
                    craftSet.put(player.getUniqueId(), set);

                    // Apply 2 result itself
                    if (event.getInventory().getResult() != null) {

                        // Convert
                        ItemStack cursorResult = FullConvert(originalResult.clone(), set, player, ConvertingReason.CRAFT);

                        // Set in inventory a reroll
                        event.getInventory().setResult(cursorResult);
                    }

                    // No need to spamm the method vro
                    BeginScourger();
                }
            }
        }
    }

    void BeginScourger() {

        // No need to spamm the method vro
        if (!messagesRunning) {

            // No need to spamm vro
            messagesRunning = true;

            // Run
            (new BukkitRunnable() {
                public void run() {

                    // For each vro
                    for (Player p : crafters) {

                        // Get The ones they already held
                        HashMap<Integer, ItemStack> oldHeld = craftHeld.get(p.getUniqueId());

                        ItemStack sought = craftSearch.get(p.getUniqueId());

                        // Get the ones that are new
                        HashMap<Integer, ItemStack> trulyHeld = ScourgeForConverted(p.getInventory(), sought);

                        // Prepare result
                        ItemStack asMMOItem = craftPrepd.get(p.getUniqueId());

                        // Random Tier?
                        ConverterTypeSettings set = craftSet.get(p.getUniqueId());

                        //DBG//OotilityCeption.Log("\u00a78CONVERTER\u00a7e RESULT\u00a77 Original Slots:");
                        //DBG//for (Integer i : trulyHeld.keySet()) { OotilityCeption.Log("\u00a78CONVERTER\u00a7e RESULT\u00a77 +\u00a7b " + i); }

                        // Remove Originals
                        for (Integer i : oldHeld.keySet()) {
                            //DBG//OotilityCeption.Log("\u00a78CONVERTER\u00a7e RESULT\u00a77 Countering Search At Slot: \u00a7b" + i);
                            trulyHeld.remove(i); }

                        // For each difference
                        for (Integer i : trulyHeld.keySet()) {
                            //DBG//OotilityCeption.Log("\u00a78CONVERTER\u00a7e RESULT\u00a77 Converting At Slot: \u00a7b" + i);

                            // Clonium
                            ItemStack curr = FullConvert(asMMOItem.clone(), set, p, ConvertingReason.CRAFT);

                            // Set in inventory a reroll
                            p.getInventory().setItem(i, curr);
                        }
                    }

                    // No longer running
                    messagesRunning = false;

                    // Clear both arrays
                    crafters.clear();
                    craftHeld.clear();
                    craftSearch.clear();
                    craftPrepd.clear();
                    craftSet.clear();
                }

            }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
        }
    }

    static HashMap<UUID, ItemStack> craftPrep = new HashMap<>();
    static HashMap<UUID, ItemStack> craftPrepResult = new HashMap<>();
    @EventHandler public void OnCraftPrep(PrepareItemCraftEvent event) {

        // Only players ffs
        if (!(event.getView().getPlayer() instanceof Player)) { return; }
        if (event.getInventory().getResult() == null) { return; }
        Player player = (Player) event.getView().getPlayer();

        //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a77 Prep Begin ------------------------------> " + OotilityCeption.GetItemName(event.getInventory().getResult()));

        // Extract Name
        RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);

        // If the item picked up is marked as convertible
        if (!ConverterTypes.IsConvertable(event.getInventory().getResult(), convName)) {
            //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a7c Non-convertible result.");
            return; }
        //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a77 Convertible result...");

        // If it is not a MMOItem
        if (GooPMMOItems.IsMMOItem(event.getInventory().getResult())) {
            //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a7c Already custom crafted.");
            return; }
        //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a77 Vanilla crafting detected... preparing for craft.");

        // Prepare result; Apply the pre craft I suppose
        ItemStack result = DisplayConvert(event.getInventory().getResult(), ConverterTypeSettings.PertainingTo(convName.getValue()), player, ConvertingReason.CRAFT);

        // Store
        craftPrepResult.put(player.getUniqueId(), result.clone());

        // Well, override it I suppose. Drop another entity with the qualifications
        event.getInventory().setResult(result);
    }

    static HashMap<UUID, HashMap<Integer, ItemStack> > craftHeld = new HashMap<>();
    static HashMap<UUID, ItemStack> craftSearch = new HashMap<>();
    static HashMap<UUID, ItemStack> craftPrepd = new HashMap<>();
    static HashMap<UUID, ConverterTypeSettings> craftSet = new HashMap<>();
    static ArrayList<Player> crafters = new ArrayList<>();
    static boolean messagesRunning = false;
    @EventHandler public void OnCraft(CraftItemEvent event) {

        // Only players ffs
        if (!(event.getView().getPlayer() instanceof Player)) { return; }
        Player player = (Player) event.getView().getPlayer();

        // Get predicted result
        ItemStack postConvertedResult = craftPrepResult.get(player.getUniqueId());
        craftPrepResult.remove(player.getUniqueId());

        if (event.isCancelled()) { return; }
        if (postConvertedResult == null) { return; }

        //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a77 Craft Begin ----------> " + OotilityCeption.GetItemName(event.getRecipe().getResult()) + "\u00a77 to " + OotilityCeption.GetItemName(postConvertedResult));

        // Extract Name
        RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);

        // If the item picked up is marked as convertible
        if (!ConverterTypes.IsConvertable(event.getRecipe().getResult(), convName)) {
            //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a7c For some reason, not convertible anymore..? ");
            return; }
        //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a77 Convertible item identified. ");

        // If it is not a MMOItem
        if (GooPMMOItems.IsMMOItem(event.getRecipe().getResult())) {
            //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a7c For some reason, custom crafted..? ");
            return; }
        //CFT//OotilityCeption.Log("\u00a78Craft \u00a73PR\u00a77 Vanilla crafted item identified! Performing operation... ");

        // Capture those already crafted
        HashMap<Integer, ItemStack> heldItems = ScourgeForConverted(player.getInventory(), postConvertedResult);

        // Get Conversion Setting to Apply
        ConverterTypeSettings set = ConverterTypeSettings.PertainingTo(convName.getValue());

        // Store
        crafters.add(player);
        craftHeld.put(player.getUniqueId(), heldItems);
        craftSearch.put(player.getUniqueId(), postConvertedResult.clone());
        craftPrepd.put(player.getUniqueId(), event.getRecipe().getResult().clone());
        craftSet.put(player.getUniqueId(), set);

        // Apply 2 result itself
        if (event.getInventory().getResult() != null) {

            // Convert
            ItemStack cursorResult = FullConvert(event.getRecipe().getResult().clone(), set, player, ConvertingReason.CRAFT);

            // Set in inventory a reroll
            event.getInventory().setResult(cursorResult);
        }

        BeginScourger();
    }

    HashMap<Integer, ItemStack> ScourgeForConverted(@NotNull Inventory inven, @Nullable ItemStack sought) {

        // Create
        HashMap<Integer, ItemStack> ret = new HashMap<>();
        if (sought == null) { return ret; }

        //DBG//OotilityCeption.Log("\u00a78CONVERTER\u00a7e RESULT\u00a77 Comparing to " + OotilityCeption.GetItemName(sought));
        for (int i = 0; i < inven.getSize(); i++) {

            // Find item
            ItemStack found = inven.getItem(i);

            // Easy Find
            String mID = GooPMMOItems.GetMMOItemID(found, "no");

            if (mID.equals(GooPMMOItems.VANILLA_MIID)) {

                //DBG//OotilityCeption.Log("\u00a78CONVERTER\u00a7e RESULT\u00a77 + " + OotilityCeption.GetItemName(found) + " \u00a78(VANILLA MID \u00a73true\u00a78)");

                ret.put(i, found);
                continue;
            }

            if (mID.startsWith("GENERIC_")) {

                //DBG//OotilityCeption.Log("\u00a78CONVERTER\u00a7e RESULT\u00a77 + " + OotilityCeption.GetItemName(found) + " \u00a78(GENERIC MID \u00a73true\u00a78)");

                ret.put(i, found);
            }
        }

        return ret;
    }

    static HashMap<UUID, Player> gp_Players = new HashMap<>();
    static HashMap<UUID, String> gp_Skills = new HashMap<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDropItem(PlayerDropItemEvent event) {

        // If it hasnt been cancelled. All DropItem functionality requires MythicMobs as well.
        if (!event.isCancelled() && Gunging_Ootilities_Plugin.foundMythicMobs) {
            //DBG//OotilityCeption.Log("Dropped Item");

            // Get Item
            ItemStack source = event.getItemDrop().getItemStack();

            // Does it have ground pound stat?
            String sData = GooPMMOItems.GetStringStatValue(source, GooPMMOItems.GROUND_POUND_STAT, event.getPlayer(), false);

            // Did it have it?
            if (sData != null) {
                //DBG//OotilityCeption.Log("\u00a7aFound Skill: \u00a77" + sData);

                // Get Item
                Entity eSource = event.getItemDrop();

                // Create Listened Entity
                ListenedEntity lEntity = new ListenedEntity(eSource);

                // Add Reason and Listener
                lEntity.addObjective("GroundPStat");
                lEntity.addReason(ListenedEntityReasons.UponLanding);

                // Store info
                gp_Players.put(eSource.getUniqueId(), event.getPlayer());
                gp_Skills.put(eSource.getUniqueId(), sData);

                // Enable BRUH
                lEntity.Enable();
            }
        }
    }

    @EventHandler
    public void OnDropLanding(ListenedEntityEvent event) {

        // Must be a landing event
        if (Gunging_Ootilities_Plugin.foundMythicMobs && event.getEventReason().equals(ListenedEntityReasons.UponLanding)) {
            //DBG//OotilityCeption.Log("Dropped Item Landed");

            // Does it have the Objective?
            if (event.getObjectives().contains("GroundPStat")) {
                //DBG//OotilityCeption.Log("Was Ground Pound");

                // Get ID
                UUID tUUID = event.getEntity().getUniqueId();

                // Run
                Player gpPlayer = gp_Players.get(tUUID);
                String gpSkill = gp_Skills.get(tUUID);

                // Both shall exist
                if (gpPlayer != null && gpSkill != null) {
                    //DBG//OotilityCeption.Log("Found Player and Skill Information");

                    // MM Skill Exist?
                    if (GooPMythicMobs.SkillExists(gpSkill)) {
                        //DBG//OotilityCeption.Log("\u00a72Running Skill \u00a77" + gpSkill + "\u00a72 as \u00a73" + gpPlayer.getName());

                        // Run it
                        GooPMythicMobs.ExecuteMythicSkillAs(gpSkill, gpPlayer, event.getEntity(), event.getLocation());
                    }
                }

                // Remove
                gp_Players.remove(tUUID);
                gp_Skills.remove(tUUID);
            }
        }
    }
}
