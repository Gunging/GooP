package gunging.ootilities.gunging_ootilities_plugin;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.*;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionAttributes;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionPotionEffects;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.containers.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ISSObservedContainer;
import gunging.ootilities.gunging_ootilities_plugin.containers.restriction.*;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.*;
import gunging.ootilities.gunging_ootilities_plugin.events.GooPGriefEvent;
import gunging.ootilities.gunging_ootilities_plugin.events.GooP_FontUtils;
import gunging.ootilities.gunging_ootilities_plugin.events.ScoreboardLinks;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.TargetedItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISSEnderchest;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISSInventory;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables.GOOPUCKTPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables.GOOPUCKTServer;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables.GOOPUCKTUnique;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.unlockables.GooPUnlockableTarget;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

/**
 * The {@code /goop testinventory} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdTestInventory {

    private GooPCmdTestInventory() { }

    /** @return null to continue with GooP's usual feedback output, or the value onCommand should return */
    @SuppressWarnings({"ConstantConditions", "unused"})
    static Boolean run(@NotNull GooPCommandContext context) {
        CommandSender sender = context.sender;
        Command command = context.command;
        String label = context.label;
        String[] args = context.args;
        boolean senderIsPlayer = context.senderIsPlayer;
        Location senderLocation = context.senderLocation;
        String failMessage = context.failMessage;
        UUID successibilityFlare = context.successibilityFlare;
        List<String> logReturn = context.logReturn;
        boolean supp = context.supp;
        GooP_Commands cmd = context.cmd;
        StringBuilder successSlots = context.successSlots;
        int argsMinLength, argsMaxLength;
        String subcommand, subcategory, usage;
        boolean permission = context.permission;
        ArrayList<String> trueArgs = context.trueArgs;
        boolean chained = context.chained;
        StringBuilder chanedArgs = context.chanedArgs;
        SuccessibleChain commandChain = context.commandChain;
        String chainedNoLocation = context.chainedNoLocation;
        RefSimulator<List<String>> logReturnUrn = context.logReturnUrn;
        String senderUUIDIG = context.senderUUIDIG;
        OotilityCeption oots = context.oots;
        GungingOotilities self = context.self;
        //   0      1              2       3    4 5 6     [7]       7 [8]     8 [9]  9 [10]     args.Length
        // /goop testinventory <player> <slot> {n b t} [amount] [objective] [score] [comp]  [Shulker Box Name Filter]
        //   -      0              1       2    3 4 5     [6]       6 [7]     7 [8]  8 [9]      args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.testinventory");
        }

        // Got perms?
        if (permission) {

            // How many args this man got?
            if (args.length >= 6) {
                Entity asDroppedItem = OotilityCeption.getEntityByUniqueId(args[1]);
                if (!(asDroppedItem instanceof Item)) { asDroppedItem = null; }

                // Gets that player boi
                ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], null);
                RefSimulator<String> logAddition = new RefSimulator<>("");

                // Some scoreboards to test
                Objective targetObjective = null;
                PlusMinusPercent amountPMP = null;
                PlusMinusPercent score = null;

                String objectiveStr = null;
                String amountPmPText = null;
                int nameFilterBegin = 32767;
                int scorelessNameFilterBegin = 32767;

                QuickNumberRange itemAmountTest = null;
                boolean toCompletion = false;

                /*
                 * If any of the optional terms is used
                 */
                if (args.length >= 7) {

                    // Attempt to parse sixth argument as an amount range to accept
                    itemAmountTest = QuickNumberRange.FromString(args[6]);
                    int iAD = 0;

                    // Item Amount Test was specified
                    if (itemAmountTest != null) { iAD = 1; }

                    // If there is only one optional argument
                    if (args.length == (7 + iAD)) {

                        // It must be comp
                        toCompletion = args[6 + iAD].equals("comp");

                        // Name filter??? (of one word)
                        scorelessNameFilterBegin = 6 + iAD + (toCompletion ? 1 : 0);

                    // There is two optional arguments
                    } else if (args.length == (8 + iAD)) {

                        // They must be an objective and a scoreo
                        objectiveStr = args[6 + iAD];
                        amountPmPText = args[7 + iAD];

                        // Name filter??? (two words)
                        scorelessNameFilterBegin = 6 + iAD;

                    // There is three optional arguments
                    } else if (args.length >= (9 + iAD)) {

                        // They must be an objective and a score
                        objectiveStr = args[6 + iAD];
                        amountPmPText = args[7 + iAD];
                        toCompletion = args[8 + iAD].equals("comp");

                        // Was the first one, comp?
                        if (toCompletion) {

                            // Begin index is 9 + iAD
                            nameFilterBegin = 9 + iAD;

                            // Name filter must begin after comp keyword
                            scorelessNameFilterBegin = 9 + iAD;

                        // Not comp, must be the start of the name filter
                        } else {

                            // Name filter is 8 + iAD
                            nameFilterBegin = 8 + iAD;

                            // Except the last three word could have been it
                            scorelessNameFilterBegin = 6 + iAD;
                        }
                    }
                }

                // Some bool to know if failure
                boolean failure = false;

                // Does the player exist?
                if (targets.size() == 0 && asDroppedItem == null) {

                    // Failure
                    failure = true;

                    // Add a trash null entry
                    targets.add(null);

                    // Notify the error
                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", "Target must be an online player!"));
                }

                //Switch for sintax
                boolean allowNull = false;
                if (OotilityCeption.IsInvalidItemNBTtestString(args[3], args[4], args[5], logAddition)) {
                    // Ew
                    failure = true;

                    // Log it
                    if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Test Inventory", logAddition.GetValue()));}
                } else { allowNull = (args[3].equals("v") && (args[4].toLowerCase()).equals("air"));  }

                // Can it parse the score?
                boolean scoreExpectedScoreboard = false;
                boolean uiAmount = false, asSlot = false;
                if (amountPmPText != null) {

                    // Will it set the objective to the count of items in the inventory?
                    asSlot = amountPmPText.toLowerCase().contains("slot");
                    uiAmount = amountPmPText.toLowerCase().contains("amount") || asSlot;    // Cling onto UIAmount system, the slot.
                    if (uiAmount || asSlot) {

                        // Build Amount PMP base
                        amountPMP = PlusMinusPercent.GetPMP(amountPmPText.toLowerCase().replace("amount", "1"), null);

                        // If it didnt parse, default to just set
                        if (amountPMP == null) { amountPMP = new PlusMinusPercent(1D, false, false); }

                        // This guy tryna have an objective, man
                        scoreExpectedScoreboard = true;
                    }

                    // Get score increment as PMP
                    score = PlusMinusPercent.GetPMP(amountPmPText, logAddition);
                    if (score != null) { scoreExpectedScoreboard = true; }

                    // Does the scoreboard objective exist?
                    targetObjective = OotilityCeption.GetObjective(objectiveStr);
                }

                // Could not find the objective?
                if (targetObjective == null && amountPmPText != null) {

                    /*
                     * So the player may specify a shulker box name filter without
                     * an objective/score pair, in which case the score text will not
                     * be a PMP nor 'amount' and will start immediately after comp.
                     */
                    failure = failure || scoreExpectedScoreboard;

                    // Mention
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback && failure) logReturn.add(OotilityCeption.LogFormat("Test Inventory", "Scoreboard Objective '\u00a73" + objectiveStr + "\u00a77' does not exist."));
                }

                // Name
                String fltr = null;
                if (args.length > scorelessNameFilterBegin) {

                    // Identify where the name filter truly begins
                    int trueNameFilter = scorelessNameFilterBegin;

                    // If expected scoreboard, use the alternative
                    if (scoreExpectedScoreboard) { trueNameFilter = nameFilterBegin; }

                    //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Args Length:\u00a79 " + args.length);
                    //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Scoreless F:\u00a73 " + scorelessNameFilterBegin);
                    //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 True Name F:\u00a7b " + trueNameFilter);

                    // If still can build the name
                    if (args.length > trueNameFilter) {

                        // Stringbuild the remaining args
                        StringBuilder tot = new StringBuilder();
                        for (int ar = trueNameFilter; ar < args.length; ar++) {
                            if (!(tot.length() == 0)) { tot.append(" "); }
                            tot.append(args[ar]); }

                        // Build
                        fltr = tot.toString();
                        //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Searching in boxes of name '" + fltr + "'");
                    }
                }

                // Everthing went all right?
                if (!failure) {

                    // Compare dropped item
                    if (asDroppedItem != null) {
                        int kount = 0;
                        Integer slotS = null;

                        // Get Item
                        ItemStack targetItem = OotilityCeption.FromDroppedItem(asDroppedItem);
                        //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Found Item " + OotilityCeption.GetItemName(targetItem));

                        // If it is not null
                        if (!OotilityCeption.IsAirNullAllowed(targetItem)) {

                            // Found something, does it match thoi?
                            if (OotilityCeption.MatchesItemNBTtestString(targetItem, args[3], args[4], args[5], logAddition)) {
                                //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                // COunt
                                kount += targetItem.getAmount();
                                slotS = 0;
                                if (chained) { OotilityCeption.Slot4Success(successSlots, new ISSInventory(0, null), OotilityCeption.comma); }
                            }

                        // Item Stack is null or air, are we searching for that?
                        } else if (allowNull) {
                            //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                            // Count
                            kount++;
                            slotS = 0;
                            if (chained) { OotilityCeption.Slot4Success(successSlots, new ISSInventory(0, null), OotilityCeption.comma); }
                        }


                        boolean success;
                        String logrt;

                        // If found
                        if (kount > 0) {

                            logrt = "Item successfully detected, counted a total of \u00a7e" + kount + "\u00a77. ";

                            // Does it proc?
                            if (itemAmountTest != null) {

                                if (itemAmountTest.InRange(kount)) {

                                    logrt += "Furthermore, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. ";
                                    success = true;

                                } else {

                                    logrt += "However, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7cdoes not\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to fail thus. ";
                                    success = false;
                                }

                                // No amount to say anything, true by default
                            } else { success = true; }

                            // Still succeeded?
                            if (success) {

                                // Some objective operation will be undergone
                                if (targetObjective != null) {

                                    // Provided an amount
                                    if (asSlot) {

                                        if (slotS == null) { slotS = -32767; }

                                        // Iamount pmp
                                        double negativity = amountPMP.getValue();
                                        amountPMP.OverrideValue(slotS * negativity);

                                        // Done
                                        OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), amountPMP);
                                        logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a73" + OotilityCeption.GetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString()) + "\u00a77. ";

                                        // Provided a score
                                    } else if (uiAmount) {

                                        // Iamount pmp
                                        double negativity = amountPMP.getValue();
                                        amountPMP.OverrideValue(kount * negativity);

                                        // Done
                                        OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), amountPMP);
                                        logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a73" + OotilityCeption.GetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString()) + "\u00a77. ";

                                        // Provided a score
                                    } else if (score != null) {

                                        //Behold
                                        OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), score);
                                        logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a73" + OotilityCeption.GetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString()) + "\u00a77. ";
                                    }
                                }

                                // Run Chain
                                if (chained) {
                                    commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@t", successSlots.toString()));
                                    commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(kount)));
                                }
                            }

                            // Nothing was found
                        } else {

                            logrt = "Nothing in \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77's slots \u00a7e" + args[2] + "\u00a77 matched \u00a7e" + args[3] + " " + args[4] + " " + args[5] + "\u00a77. ";
                            success = uiAmount && !asSlot;

                            // Does it proc?
                            if (itemAmountTest != null) {

                                if (itemAmountTest.InRange(0)) {

                                    logrt += "However, the amount of stuff found (\u00a7b0\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to succeed thus.";
                                    commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(0)));
                                    success = true;

                                } else {

                                    logrt += "Furthermore, the amount of stuff found (\u00a7b0\u00a77) \u00a7cdoes not\u00a77 even fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. ";
                                    success = false;
                                }
                            }

                            // Difference between amount and no amount
                            if (uiAmount && success) {

                                // Is there a target objective?
                                if (success && targetObjective != null) {

                                    // Iamount pmp
                                    amountPMP.OverrideValue(0.0D);

                                    // Well nothing means 0 this time
                                    OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), amountPMP);

                                    // Log
                                    logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a730\u00a77. ";
                                }
                            }
                        }

                        // Well
                        if (success) {

                            // Run Chain
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                            commandChain.chain(chained, asDroppedItem.getUniqueId(), sender);

                        } else {

                            // N00b
                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                        }
                    }

                    // For every target
                    for (Player target : targets) {
                        failure = false;

                        //Lets get that inven slot
                        RefSimulator<String> slotFailure = new RefSimulator<>("");
                        ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[2], target, slotFailure);

                        // So, does the slot make no sense?
                        if (slott.size() == 0) {
                            // Failure
                            failure = true;

                            // If no slots were wrong
                            if (slotFailure.getValue() == null && Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Test Inventory", "The slots specified didn't make sense in this context. As if trying to access shulker boxes where there are none, or using container placeholder names in non-goop inventories.")); }
                        }

                        // Log
                        if (slotFailure.getValue() != null) { logReturn.add(OotilityCeption.LogFormat("Test Inventory", slotFailure.getValue())); }

                        // Nice sintax!
                        if (!failure) {

                            // Count matches
                            int kount = 0;
                            Integer slotS = null;

                            // For every slot
                            for (ItemStackSlot tSlot : slott) {

                                // Time to get that item stack
                                ItemStackLocation tISource = OotilityCeption.getItemFromPlayer(target, tSlot, fltr);
                                //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Looking at \u00a7e" + tSlot.toString() + "\u00a77: \u00a78(\u00a76" + tSlot.getLocation().toString() + "\u00a78;\u00a79" + tISource.getClass().getSimpleName() + "\u00a78)");

                                // If slot made sense
                                if (tISource != null) {

                                    // Get Item
                                    ItemStack targetItem = tISource.getItem();
                                    //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Found Item " + OotilityCeption.GetItemName(targetItem));

                                    // If it is not null
                                    if (!OotilityCeption.IsAirNullAllowed(targetItem)) {

                                        // Found something, does it match thoi?
                                        if (OotilityCeption.MatchesItemNBTtestString(targetItem, args[3], args[4], args[5], logAddition)) {
                                            //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                            // COunt
                                            kount += targetItem.getAmount();
                                            slotS = tSlot.getSlot();
                                            if (chained) { OotilityCeption.Slot4Success(successSlots, tSlot, OotilityCeption.comma); }

                                            // Is this the whole damn thing? No? Then break it
                                            if ((asSlot || !uiAmount) && !toCompletion) {
                                                //TSI//OotilityCeption.Log("\u00a7b\u00a7oCompletion Reached, breaking. ");
                                                break; }

                                        }

                                    // Item Stack is null or air, are we searching for that?
                                    } else if (allowNull) {
                                        //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                        // Count
                                        kount++;
                                        if (chained) { OotilityCeption.Slot4Success(successSlots, tSlot, OotilityCeption.comma); }

                                        // Is this the whole damn thing? No? Then break it
                                        slotS = tSlot.getSlot();
                                        if ((asSlot || !uiAmount) && !toCompletion) {
                                            //TSI//OotilityCeption.Log("\u00a7b\u00a7oCompletion Reached, breaking. ");
                                            break; }

                                    }

                                // Null Item Stack Location? I guess thats air?
                                } else if (allowNull) {

                                    //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                    // Count
                                    kount++;
                                    slotS = tSlot.getSlot();
                                    if (chained) { OotilityCeption.Slot4Success(successSlots, tSlot, OotilityCeption.comma); }

                                    // Is this the whole damn thing? No? Then break it
                                    if ((asSlot || !uiAmount) && !toCompletion) {
                                        //TSI//OotilityCeption.Log("\u00a7b\u00a7oCompletion Reached, breaking. ");
                                        break; }
                                }
                            }

                            boolean success;
                            String logrt;

                            // If found
                            if (kount > 0) {

                                logrt = "Item \u00a7e" + args[3] + " " + args[4] + " " + args[5] + "\u00a77 successfully detected in \u00a73" + target.getName()  + "\u00a77's slots \u00a7b" + args[2] +  "\u00a77, counted a total of \u00a7e" + kount + "\u00a77. ";

                                // Does it proc?
                                if (itemAmountTest != null) {

                                    if (itemAmountTest.InRange(kount)) {

                                        logrt += "Furthermore, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77.";
                                        success = true;

                                    } else {

                                        logrt += "However, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7cdoes not\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to fail thus. ";
                                        success = false;
                                    }

                                // No amount to say anything, true by default
                                } else { success = true; }

                                // Still succeeded?
                                if (success) {

                                    // Some objective operation will be undergone
                                    if (targetObjective != null) {

                                        // Provided an amount
                                        if (asSlot) {

                                            if (slotS == null) { slotS = -32767; }

                                            // Iamount pmp
                                            double negativity = amountPMP.getValue();
                                            amountPMP.OverrideValue(slotS * negativity);

                                            // Done
                                            OotilityCeption.SetPlayerScore(targetObjective, target, amountPMP);
                                            logrt += "Their score '\u00a73" + targetObjective.getName() + "\u00a77 is now \u00a73" + OotilityCeption.GetPlayerScore(targetObjective, target) + "\u00a77. ";

                                        // Provided a score
                                        } else if (uiAmount) {

                                            // Iamount pmp
                                            double negativity = amountPMP.getValue();
                                            amountPMP.OverrideValue(kount * negativity);

                                            // Done
                                            OotilityCeption.SetPlayerScore(targetObjective, target, amountPMP);
                                            logrt += "Their score '\u00a73" + targetObjective.getName() + "\u00a77 is now \u00a73" + OotilityCeption.GetPlayerScore(targetObjective, target) + "\u00a77. ";

                                        // Provided a score
                                        } else if (score != null) {

                                            //Behold
                                            OotilityCeption.SetPlayerScore(targetObjective, target, score);
                                            logrt += "Their score '\u00a73" + targetObjective.getName() + "\u00a77 is now \u00a73" + OotilityCeption.GetPlayerScore(targetObjective, target) + "\u00a77. ";
                                        }
                                    }

                                    // Run Chain
                                    if (chained) {
                                        commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@t", successSlots.toString()));
                                        commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(kount)));
                                    }
                                }

                            // Nothing was found
                            } else {

                                logrt = "Nothing in \u00a73" + target.getName() + "\u00a77's slots \u00a7b" + args[2] + "\u00a77 matched \u00a7e" + args[3] + " " + args[4] + " " + args[5] + "\u00a77. ";
                                success = uiAmount && !asSlot;

                                // Does it proc?
                                if (itemAmountTest != null) {

                                    if (itemAmountTest.InRange(0)) {

                                        logrt += "However, the amount of stuff found (\u00a7b0\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to succeed thus.";
                                        commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", "0"));
                                        success = true;

                                    } else {

                                        logrt += "Furthermore, the amount of stuff found (\u00a7b0\u00a77) \u00a7cdoes not\u00a77 even fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. ";
                                        success = false;
                                    }
                                }

                                // Difference between amount and no amount
                                if (uiAmount && success) {

                                    // Is there a target objective?
                                    if (success && targetObjective != null) {

                                        // Iamount pmp
                                        amountPMP.OverrideValue(0.0D);

                                        // Well nothing means 0 this time
                                        OotilityCeption.SetPlayerScore(targetObjective, target, amountPMP);

                                        // Log
                                        logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + target.getName() + "\u00a77 is now \u00a730\u00a77. ";
                                    }
                                }
                            }


                            // Well
                            if (success) {

                                // Run Chain
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                                commandChain.chain(chained, target, sender);

                            } else {

                                // N00b
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                                if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, target.getPlayer(), target.getPlayer(), null, null))); }
                            }
                        }
                    }
                }

            // If they were calling the help form of the command
            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Test Inventory, \u00a77Makes sure the player has an item in his inven.");
                logReturn.add("\u00a73Usage: \u00a7e/goop testinventory <player> <slot> {nbt} [amount] [objective] [score] [comp] [shulker...]");
                logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player target of the command.");
                logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slots in players inventory.");
                logReturn.add("\u00a73 - \u00a7e{nbt} \u00a77These are the formats that match your plugins:");
                logReturn.add("\u00a73 --> \u00a7ee <enchantment name> <level> \u00a77Tests for an enchantment.");
                logReturn.add("\u00a73 --> \u00a7ev <material> * \u00a77Tests for a vanilla item.");
                if (Gunging_Ootilities_Plugin.foundMMOItems) { logReturn.add("\u00a73 --> \u00a7em <mmoitem type> <mmoitem id> \u00a77Tests for it being a precise mmoitem."); }

                logReturn.add("\u00a73 - \u00a7e[amount] \u00a77Range of amount by which this command succeeds.");
                logReturn.add("\u00a73 - \u00a7e[scoreboard] \u00a77Scoreboard objective name that will be modified IF the item matches perfectly.");
                logReturn.add("\u00a73 - \u00a7e[value] \u00a77Score value that will be given to the player.");
                logReturn.add("\u00a73 --> \u00a7eamount \u00a77keyword will set the score to the amount of that item the player has.");
                logReturn.add("\u00a73 - \u00a7e[comp] \u00a77To count the items in all the slots.");
                logReturn.add("\u00a73 - \u00a7e[shulker....] \u00a77Name of shulker boxes to inspect (All if missing).");
                logReturn.add("\u00a73 --> \u00a77Will only affect Shulker Box Slots.");

            // Incorrect number of argumenst. These beople
            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Test Inventory", "Incorrect usage. For info: \u00a7e/goop testinventory"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop testinventory <player> <slot> {nbt} <scoreboard> <value>");
                }
            }

        // No permission
        } else {

            // Tell him lmao
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to check inventories!"));
        }
        return null;
    }
}
