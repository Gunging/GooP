package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.GungingOotilities;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.AppliccableMask;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypeNames;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypes;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class OnApplyCommand implements Listener {

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
        } else {

            //DBG//OotilityCeption.Log("\u00a7cGemStone had no OnApply");
        }

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
                                AppliccableMask targetMask = AppliccableMask.GetMask(maskName);
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

                                        } else {

                                            //DBG//OotilityCeption.Log("\u00a76 +\u00a77 Created New");
                                        }
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
            if (ConverterTypes.IsConvertable(event.getItem().getItemStack().getType(), convName)) {

                // If it is not a MMOItem
                if (!GooPMMOItems.IsMMOItem(event.getItem().getItemStack())) {

                    // Prepare result
                    ItemStack result = GooPMMOItems.ConvertVanillaToMMOItem(event.getItem().getItemStack());

                    // Random Tier?
                    ConverterTypeSettings set = ConverterTypeSettings.PertainingTo(convName.getValue());

                    // If existed, apply
                    if (set != null) {
                        result = set.ApplyTo(result, (Player) event.getEntity(), true);
                        result = set.ApplyPostTo(result, (Player) event.getEntity(), true);
                    }

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

    static HashMap<UUID, ItemStack> craftPrep = new HashMap<>();
    static HashMap<UUID, ItemStack> craftPrepResult = new HashMap<>();
    @EventHandler
    public void OnCraftBrep(PrepareItemCraftEvent event) {

        // Only players ffs
        if (!(event.getView().getPlayer() instanceof Player)) { return; }
        Player player = (Player) event.getView().getPlayer();

        if (event.getInventory().getResult() != null) {

            // Extract Name
            RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);

            // If the item picked up is marked as convertible
            if (ConverterTypes.IsConvertable(event.getInventory().getResult().getType(), convName)) {

                // If it is not a MMOItem
                if (!GooPMMOItems.IsMMOItem(event.getInventory().getResult())) {

                    // Store
                    craftPrep.put(player.getUniqueId(), event.getInventory().getResult().clone());

                    // Prepare result; Apply the pre craft I suppose
                    ItemStack result = GooPMMOItems.ConvertVanillaToMMOItem(event.getInventory().getResult());
                    ConverterTypeSettings set = ConverterTypeSettings.PertainingTo(convName.getValue());
                    if (set != null) { set.ApplyTo(result, player, false); }

                    // Store
                    craftPrepResult.put(player.getUniqueId(), result.clone());

                    // Well, override it I suppose. Drop another entity with the qualifications
                    event.getInventory().setResult(result);
                }
            }
        }
    }

    static HashMap<UUID, HashMap<Integer, ? extends ItemStack> > craftHeld = new HashMap<>();
    static HashMap<UUID, ItemStack> craftSearch = new HashMap<>();
    static HashMap<UUID, ItemStack> craftPrepd = new HashMap<>();
    static HashMap<UUID, ConverterTypeSettings> craftSet = new HashMap<>();
    static ArrayList<Player> crafters = new ArrayList<>();
    static boolean messagesRunning = false;
    @EventHandler
    public void OnCraft(CraftItemEvent event) {

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

            // Extract Name
            RefSimulator<ConverterTypeNames> convName = new RefSimulator<>(null);

            // If the item picked up is marked as convertible
            if (ConverterTypes.IsConvertable(originalResult.getType(), convName)) {

                // If it is not a MMOItem
                if (!GooPMMOItems.IsMMOItem(originalResult)) {

                    // Convert
                    ItemStack convertedResult = GooPMMOItems.ConvertVanillaToMMOItem(originalResult);

                    // Capture those already crafted
                    HashMap<Integer, ? extends ItemStack> heldItems = player.getInventory().all(postConvertedResult);

                    // Get Conversion Setting to Apply
                    ConverterTypeSettings set = ConverterTypeSettings.PertainingTo(convName.getValue());

                    // Store
                    crafters.add(player);
                    craftHeld.put(player.getUniqueId(), heldItems);
                    craftSearch.put(player.getUniqueId(), postConvertedResult.clone());
                    craftPrepd.put(player.getUniqueId(), convertedResult.clone());
                    craftSet.put(player.getUniqueId(), set);

                    // Apply 2 result itself
                    if (event.getInventory().getResult() != null) {

                        // Convert
                        ItemStack cursorResult = convertedResult.clone();

                        // Transform Result
                        if (set != null) {

                            // Apply Pre
                            cursorResult = set.ApplyTo(cursorResult, player, false);

                            // Apply Post
                            cursorResult = set.ApplyPostTo(cursorResult, player, false);
                        }

                        // Set in inventory a reroll
                       event.getInventory().setResult(cursorResult);
                    }

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
                                    HashMap<Integer, ? extends ItemStack> oldHeld = craftHeld.get(p.getUniqueId());

                                    // Get the ones that are new
                                    HashMap<Integer, ? extends ItemStack> trulyHeld = p.getInventory().all(craftSearch.get(p.getUniqueId()));

                                    // Prepare result
                                    ItemStack asMMOItem = craftPrepd.get(p.getUniqueId());

                                    // Random Tier?
                                    ConverterTypeSettings set = craftSet.get(p.getUniqueId());

                                    // Remove Originals
                                    for (Integer i : oldHeld.keySet()) { trulyHeld.remove(i); }

                                    // For each difference
                                    for (Integer i : trulyHeld.keySet()) {
                                        //DBG//OotilityCeption.Log("Converting At Slot: \u00a7b" + i);

                                        // Clonium
                                        ItemStack curr = asMMOItem.clone();

                                        // Transform Result
                                        if (set != null) {

                                            // Apply Pre
                                            curr = set.ApplyTo(curr, p, false);

                                            // Apply Post
                                            curr = set.ApplyPostTo(curr, p, false);
                                        }

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
            }
        }
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
