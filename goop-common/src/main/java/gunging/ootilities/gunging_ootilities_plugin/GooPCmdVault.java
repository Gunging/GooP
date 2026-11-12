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
 * The {@code /goop vault} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdVault {

    private GooPCmdVault() { }

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
        //   0      1        2      args.Length
        // /goop vault {action}
        //   -      0        1      args[n]

        // FFS Check for MMOItems
        if (Gunging_Ootilities_Plugin.foundVault) {

            // Check 5 Permission
            if (senderIsPlayer) {
                // Solid check for permission
                permission = sender.hasPermission("gunging_ootilities_plugin.vault");
            }

            // Got permission?
            if (permission) {
                if (args.length >= 2) {
                    // Some bool to know if failure
                    boolean failure = false;
                    ArrayList<OfflinePlayer> targets = null;

                    switch (args[1].toLowerCase()) {
                        //region charge
                        case "charge":
                            //   0      1     2       3       4         args.Length
                            // /goop vault charge <player> <amount>
                            //   -      0     1       2       3         args[n]

                            // Git Vault
                            if (args.length == 4) {

                                // Gets that player boi
                                targets = OotilityCeption.GetPlayers(senderLocation, args[2], true, null);

                                // Does the player exist?
                                if (targets.size() < 1) {
                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Target must be a player!"));
                                }

                                // Parse amount?
                                Double charge = null;
                                if (OotilityCeption.DoubleTryParse(args[3])) { charge = Double.parseDouble(args[3]); }
                                if (charge < 0) { charge = null; }
                                if (charge == null) {

                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Expected positive numeric value instead of \u00a73" + args[3]));
                                }

                                // Valid Sintax
                                if (!failure) {

                                    // Execute for each player
                                    for (OfflinePlayer target : targets) {

                                        // Get this' eco
                                        double balance = GooPVault.GetPlayerBalance(target);

                                        // Enough?
                                        if (balance >= charge) {

                                            // Charge I guess
                                            GooPVault.Withdraw(target, charge);

                                            // Run Chain
                                            commandChain.chain(chained, target.getPlayer(), sender);

                                            // Say that
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Charged \u00a7e" + charge + " to player \u00a73" + target.getName() + "\u00a77, they now have \u00a7e" + GooPVault.GetPlayerBalance(target)));

                                        } else {

                                            // Say that
                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Player \u00a73" + target.getName() + "\u00a77 doesnt have enough money!"));

                                            // Notify
                                            if (failMessage != null && target.isOnline()) { target.getPlayer().sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage.replace("%charge%", String.valueOf(charge)), target.getPlayer(), target.getPlayer(), null, null))); }
                                        }
                                    }
                                }

                            // Incorrect number of args
                            } else {

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                    logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Incorrect usage. For info: \u00a7e/goop vault"));
                                    logReturn.add("\u00a73Usage: \u00a7e/goop vault charge <player> <amount>");
                                }
                            }

                            break;
                        //endregion
                        //region balancecheck
                        case "checkbalance":
                            //   0      1     2             3       4         5         6       args.Length
                            // /goop vault checkbalance <player> <range> [objective] [±][score][%]
                            //   -      0     1             2       3         4         5       args[n]
                            argsMinLength = 4;
                            argsMaxLength = 6;
                            usage = "/goop vault checkbalance <player> <range> [objective] [±][score][%]";
                            subcommand = "Check Balance";
                            subcategory = "Vault - Check Balance";

                            // Help form?
                            if (args.length == 2)  {

                                logReturn.add("\u00a7e______________________________________________");
                                logReturn.add("\u00a73Vault - \u00a7b" + subcommand + ",\u00a77 Check the balance of players.");
                                logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                                logReturn.add("\u00a73 - \u00a7e<range> \u00a77Balance range by which command succeeds.");
                                logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Scoreboard to output the result.");
                                logReturn.add("\u00a73 - \u00a7e[±][score][%] \u00a77Scoreboard operation if command succeeds.");
                                logReturn.add("\u00a73      * \u00a7bamount\u00a77 keyword to set the score to the player's balance.");

                                // Correct number of args?
                            } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                // Gets that player boi
                                targets = OotilityCeption.GetPlayers(senderLocation, args[2], true, null);

                                // Does the player exist?
                                if (targets.size() < 1) {
                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target must be a player!"));
                                }

                                // Parse amount?
                                QuickNumberRange charge = null;
                                charge = QuickNumberRange.FromString(args[3]);
                                if (charge == null) {

                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected a number or numeric range instead of \u00a73" + args[3] + "\u00a77. Ranges are specified with two numbers separated by \u00a7bb\u00a77. Example \u00a7e-4\u00a77 and \u00a7e32.4\u00a77: \u00a7b-4..32.5\u00a77. They are inclusive, and you may not specify either of the bounds (\u00a7b10..\u00a77 will match anything equal or greater than 10)."));
                                }

                                Objective objective = null;
                                PlusMinusPercent scoreOps = null;
                                boolean asAmount = false;
                                RefSimulator<String> logAddtion = new RefSimulator<>("");
                                if (args.length >= 6) {

                                    // Get
                                    objective = OotilityCeption.GetObjective(args[4]);
                                    scoreOps = PlusMinusPercent.GetPMP(args[5], logAddtion);

                                    // Log
                                    if (objective == null) {

                                        // Fail
                                        failure = true;

                                        // Notify the error
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Objective \u00a73" + args[4] + "\u00a77 not found."));
                                    }

                                    // Aequal?
                                    if (args[5].toLowerCase().equals("amount")) {

                                        asAmount = true;

                                    // PMP
                                    } else if (scoreOps == null) {

                                        // Fail
                                        failure = true;

                                        // Notify the error
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected numeric value, PMP, or \u00a7bamount\u00a77 keyword instead of \u00a7e" + args[5] + "\u00a77."));
                                    }
                                }

                                // Valid Sintax
                                if (!failure) {

                                    // Execute for each player
                                    for (OfflinePlayer target : targets) {

                                        // Get this' eco
                                        double balance = GooPVault.GetPlayerBalance(target);

                                        // Enough?
                                        if (charge.InRange(balance)) {

                                            // Edit score
                                            if (objective != null) {

                                                // Amount?
                                                if (asAmount) { scoreOps = new PlusMinusPercent(balance, false, false); }

                                                // Operate
                                                OotilityCeption.SetEntryScore(objective, target.getName().toString(), scoreOps);
                                            }

                                            // Run Chain
                                            commandChain.chain(chained, target.getPlayer(), sender);

                                            // Say that
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Player \u00a73" + target.getName() + "\u00a77 has \u00a7e" + balance + "\u00a77. \u00a7aSuccessfuly\u00a77 in range \u00a73" + args[3]));

                                        } else {

                                            // Failure
                                            if (failMessage != null && target.isOnline()) { target.getPlayer().sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage.replace("%charge%", charge.qrToString()), target.getPlayer(), target.getPlayer(), null, null))); }

                                            // Say that
                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Player \u00a73" + target.getName() + "\u00a77 has \u00a7e" + balance + "\u00a77. \u00a7cNot within\u00a77 range \u00a73" + args[3]));
                                        }
                                    }
                                }

                            // Incorrect number of args
                            } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                // Notify Error
                                if (args.length >= argsMinLength) {
                                    logReturn.add(OotilityCeption.LogFormat("Vault - " + subcommand, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop vault checkbalance"));

                                } else {

                                    logReturn.add(OotilityCeption.LogFormat("Vault - " + subcommand, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop vault checkbalance"));
                                }

                                // Notify Usage
                                logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            }

                            break;
                        //endregion
                        //region operation
                        case "operation":
                            //   0      1     2       3       4         args.Length
                            // /goop vault operation <player> <value>
                            //   -      0     1       2       3         args[n]

                            // Git Vault
                            if (args.length == 4) {

                                // Gets that player boi
                                targets = OotilityCeption.GetPlayers(senderLocation, args[2], true, null);

                                // Does the player exist?
                                if (targets.size() < 1) {
                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Operation", "Target must be a player!"));
                                }

                                // Parse amount?
                                RefSimulator<String> logAddition = new RefSimulator<>("");
                                PlusMinusPercent charge = PlusMinusPercent.GetPMP(args[3], logAddition);
                                if (logAddition.getValue() != null) {

                                    // Notify
                                    logReturn.add(OotilityCeption.LogFormat("Vault - Operation", logAddition.getValue()));
                                }
                                if (charge == null) {

                                    // Failure
                                    failure = true;
                                }

                                // Valid Sintax
                                if (!failure) {

                                    // Execute for each player
                                    for (OfflinePlayer target : targets) {

                                        // Get this' eco
                                        double balance = GooPVault.GetPlayerBalance(target);

                                        // Charge I guess
                                        GooPVault.SetPlayerBalance(target, charge);

                                        // Say that
                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Operation", "Player \u00a73" + target.getName() + "\u00a77 now has a balance of \u00a7e" + GooPVault.GetPlayerBalance(target)));


                                        // Run Chain
                                        commandChain.chain(chained, target.getPlayer(), sender);
                                    }
                                }

                                // Incorrect number of args
                            } else {

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                    logReturn.add(OotilityCeption.LogFormat("Vault - Operation", "Incorrect usage. For info: \u00a7e/goop vault"));
                                    logReturn.add("\u00a73Usage: \u00a7e/goop vault operation <player> [±]<amount>[%]");
                                }
                            }

                            break;
                        //endregion
                        default:
                            // I have no memory of that shit
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Vault", "'\u00a73" + args[1] + "\u00a77' is not a valid Vault action! do \u00a7e/goop vault\u00a77 for the list of actions."));
                            break;
                    }

                } else if (args.length == 1) {
                    logReturn.add("\u00a7e______________________________________________");
                    logReturn.add("\u00a73GooP-Vault, \u00a77Related to the third party plugin.");
                    logReturn.add("\u00a73Usage: \u00a7e/goop vault {action}");
                    logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                    logReturn.add("\u00a73 --> \u00a7echarge <player> <amount>");
                    logReturn.add("\u00a73      * \u00a77If the player has enoigh eco, charges this amount.");
                    logReturn.add("\u00a73      * \u00a77If the target doesnt have enough, fails and doesn't do anything.");
                    logReturn.add("\u00a73 --> \u00a7eoperation <player> [±]<amount>[%]");
                    logReturn.add("\u00a73      * \u00a77Performs a PMP Operation on the player's balance.");

                } else {
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                        logReturn.add(OotilityCeption.LogFormat("Vault", "Incorrect usage. For info: \u00a7e/goop vault"));
                        logReturn.add("\u00a73Usage: \u00a7e/goop vault {action}");
                    }
                }

            // No perms
            } else {

                // Tell him lmao
                logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to use vault-related commands!"));
            }

        // Vault not installed. Returning
        } else {

            // Tell him lmao
            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("\u00a77These commands are to be used with the third party plugin \u00a7e\u00a7lVault \u00a77which you dont have installed."));
        }
        return null;
    }
}
