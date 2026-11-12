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
 * The {@code /goop compare} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdCompare {

    private GooPCmdCompare() { }

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
        //   0     1        2           3        4        5...                                 args.Length
        // /goop compare <player> <objective> <score> {Something} GC_<operator> {Anything}
        //   -     0        1           2        3        4...                                 args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.compare");
        }

        // GOt permission?
        if (permission) {

            // Chek for args length
            if (args.length >= 7) {

                // CHeck for failure
                boolean failure = false;

                // Gets that player boi
                ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], null);
                Entity asNonplayer = null;
                if (targets.size() < 1) {

                    // Perhaps its an entity
                    asNonplayer = OotilityCeption.getEntityByUniqueId(args[1]);

                    if (asNonplayer == null || asNonplayer instanceof Player) {

                        // Failure
                        failure = true;

                        // Notify the error
                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Compare", "Target must be an online player!"));
                    }
                }

                // Some scoreboards to test
                String objectiveName = args[2];
                Objective targetObjective = OotilityCeption.GetObjective(objectiveName);
                // Does the scoreboard objective exist?
                if (targetObjective == null && !objectiveName.equals("read")) {
                    // Failure
                    failure = true;

                    // Mention
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Compare", "Scoreboard Objective '\u00a73" + args[2] + "\u00a77' does not exist."));
                }

                // Parse score form
                boolean asDifference = false,
                        asSum = false,
                        asProduct = false,
                        asDivision = false,
                        asPower = false,
                        asRoot = false,
                        asCosine = false,
                        asSine = false,
                        asTangent = false,
                        asArcCosine = false,
                        asArcSine = false,
                        asArcTangent = false;
                PlusMinusPercent asNumber = PlusMinusPercent.GetPMP(args[3], null);
                if (asNumber == null) {

                    switch (args[3].toLowerCase()) {
                        case "difference": asDifference = true; break;
                        case "sum": asSum = true; break;
                        case "product": asProduct = true; break;
                        case "division": asDivision = true; break;
                        case "root": asRoot = true; break;
                        case "power": asPower = true; break;
                        case "cos": asCosine = true; break;
                        case "sin": asSine = true; break;
                        case "tan": asTangent = true; break;
                        case "arccos": asArcCosine = true; break;
                        case "arcsin": asArcSine = true; break;
                        case "arctan": asArcTangent = true; break;
                        default:

                            // Failure, not numeric nor keyword
                            failure = true;

                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Compare", "Expected integer number for score or one of the math operation keywords instead of \u00a73" + args[3]));
                            break;
                    }
                }

                // Parse comparables
                StringBuilder somethingBLDR = new StringBuilder(), anythingBLDR = new StringBuilder();
                String operator = null, something = null, anything = null;
                boolean cmprd = false;
                for (int c = 4; c < args.length; c++) {

                    // Get observed
                    String obs = args[c];

                    // Does it equal the OS sequence?
                    if (obs.startsWith("GC_")) {

                        // It is now chained
                        cmprd = true;

                        // Just store operator for now
                        operator = obs;

                    } else {

                        // Chaining or true?
                        if (!cmprd) {

                            // Build Something
                            somethingBLDR.append(" ").append(obs);

                        // Its chained, shall now build
                        } else {

                            // Append to Anything
                            anythingBLDR.append(" ").append(obs);
                        }
                    }
                }

                boolean greaterThan = false,
                        lessThan = false,
                        equals = false,
                        forceString = false,
                        negate = false,
                        ignoreCaps = false;
                int finalMultiplier = 100;
                // Success?
                if (cmprd) {

                    // Build both
                    anything = anythingBLDR.toString().substring(1);
                    something = somethingBLDR.toString().substring(1);
                    operator = operator.substring("GC_".length());

                    // Parse operator
                    if (operator.contains(">")) { greaterThan = true; }
                    if (operator.contains("<")) { lessThan = true; }
                    if (operator.contains("=")) { equals = true; }
                    if (operator.contains("!")) { negate = true; }
                    if (operator.contains("S")) { forceString = true; }
                    if (operator.contains("s")) { forceString = true; }
                    if (operator.contains("c")) { ignoreCaps = true; }
                    if (operator.contains("C")) { ignoreCaps = true; }
                    if (operator.contains("e")) { finalMultiplier = 1; }
                    if (operator.contains("E")) { finalMultiplier = 1; }

                    // Force string with guaranteed parsed non-integer
                    if (forceString && asNumber == null && !failure) {

                        // Fail
                        failure = true;

                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Compare", "Forcing a string operation (\u00a7bS\u00a77) with your operator (\u00a73" + operator + "\u00a77) is incompatible with math operations like \u00a73" + args[3]));
                    }

                    // Operator unsuccessful
                    if (!greaterThan && !lessThan && !equals) {

                        // Fail
                        failure = true;

                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Compare", "Your operator \u00a73" + operator + "\u00a77 doesnt make sense. Try \u00a7bGC_=\u00a77 or \u00a7bGC_>\u00a77 for beginners. Full explanation in \u00a7e/goop compare"));
                    }

                    // Force string with guaranteed parsed non-integer
                    if (forceString && (greaterThan || lessThan)) {

                        // Fail
                        failure = true;

                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Compare", "Forcing a string operation (\u00a7bS\u00a77) with your operator (\u00a73" + operator + "\u00a77) is incompatible with math comparators \u00a7b>\u00a77 and \u00a7b<\u00a77."));
                    }

                } else {

                    // Must be comparing something-yo!
                    failure = true;

                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Compare", "You must insert a comparison operation there! Try \u00a7bGC_=\u00a77 or \u00a7bGC_>=\u00a77 for beginners. Full explanation in \u00a7e/goop compare"));
                }

                if (!failure) {

                    if (asNonplayer != null) {

                        // Parse both things
                        String pSomething = OotilityCeption.ParseConsoleCommand(something, asNonplayer, null, (ItemStack) null);
                        String pAnything = OotilityCeption.ParseConsoleCommand(anything, asNonplayer, null, (ItemStack) null);

                        // Doubley
                        Double pS = OotilityCeption.MathExpressionResult(pSomething, new ArrayList<>()), pA = OotilityCeption.MathExpressionResult(pAnything, new ArrayList<>());

                        // Attempt as numeric if both parse
                        boolean isNumeric = !forceString && (pS != null) && (pA != null);

                        // Proceed to compare
                        boolean resultant = false;
                        if (isNumeric) {

                            // Evaluate Operations
                            if (equals && (pS.equals(pA))) {
                                resultant = true;
                            }
                            if (greaterThan && (pS > pA)) {
                                resultant = true;
                            }
                            if (lessThan && (pS < pA)) {
                                resultant = true;
                            }

                        } else {

                            // Caps differentation
                            if (ignoreCaps) {

                                // Compare stringally
                                if (equals && (pSomething.equalsIgnoreCase(pAnything))) {
                                    resultant = true;
                                }
                            } else {

                                // Compare stringally
                                if (equals && (pSomething.equals(pAnything))) {
                                    resultant = true;
                                }
                            }
                        }

                        // Invert result if appropiate
                        if (negate) {
                            resultant = !resultant;
                        }

                        // Success?
                        if (resultant) {

                            int scoreSet = -32767;

                            // Get result
                            if (asNumber != null) {

                                // They specified a specific number, lest go
                                scoreSet = OotilityCeption.RoundToInt(asNumber.apply((targetObjective == null ? 0.0D : OotilityCeption.GetEntryScore(targetObjective, asNonplayer.getUniqueId().toString()) + 0.0D)));

                            // Perform operation
                            } else if (isNumeric) {

                                // Perform operations
                                try {

                                    // As dictated
                                    if (asSum) {
                                        scoreSet = OotilityCeption.RoundToInt((pS + pA) * finalMultiplier);
                                    } else if (asDifference) {
                                        scoreSet = OotilityCeption.RoundToInt((pS - pA) * finalMultiplier);
                                    } else if (asProduct) {
                                        scoreSet = OotilityCeption.RoundToInt((pS * pA) * finalMultiplier);
                                    } else if (asDivision) {
                                        scoreSet = OotilityCeption.RoundToInt((pS / pA) * finalMultiplier);
                                    } else if (asPower) {
                                        scoreSet = OotilityCeption.RoundToInt((Math.pow(pS, pA)) * finalMultiplier);
                                    } else if (asRoot) {
                                        scoreSet = OotilityCeption.RoundToInt((Math.pow(pS, 1.0D / pA)) * finalMultiplier);
                                    } else if (asCosine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.cos(pS * Math.PI / 180.0) * finalMultiplier);
                                    } else if (asSine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.sin(pS * Math.PI / 180.0) * finalMultiplier);
                                    } else if (asTangent) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.tan(pS * Math.PI / 180.0) * finalMultiplier);
                                    } else if (asArcCosine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.acos(pS) * finalMultiplier * 180.0 / Math.PI);
                                    } else if (asArcSine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.asin(pS) * finalMultiplier * 180.0 / Math.PI);
                                    } else if (asArcTangent) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.atan(pS) * finalMultiplier * 180.0 / Math.PI);
                                    }

                                } catch (Exception ignored) {
                                    scoreSet = -32767;
                                }
                            }

                            // Set score lma0
                            if (targetObjective != null) {

                                // Perform scoreboard operation
                                OotilityCeption.SetEntryScore(targetObjective, asNonplayer.getUniqueId().toString(), scoreSet);

                                // Notify the success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback)
                                    logReturn.add(OotilityCeption.LogFormat("Compare", "Comparison returned true! Setting score \u00a73" + args[2] + "\u00a77 of \u00a73" + asNonplayer.getName() + "\u00a77 to \u00a7e" + scoreSet + "\u00a77."));

                            } else {

                                // Notify the success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback)
                                    logReturn.add(OotilityCeption.LogFormat("Compare", "Comparison returned true! Result is \u00a7e" + scoreSet + "\u00a77."));

                            }

                            // Update @v
                            commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(scoreSet)));

                            // Run Chain
                            commandChain.chain(chained, asNonplayer.getUniqueId(), sender);

                        } else {

                            // lol found no
                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback)
                                logReturn.add(OotilityCeption.LogFormat("Compare", "{Something} (\u00a73" + pSomething + "\u00a77) did not match {Anything} (\u00a73" + pAnything + "\u00a77)."));
                        }
                    }

                    // For every
                    for (Player target : targets) {

                        // Parse both things
                        String pSomething = OotilityCeption.ParseConsoleCommand(something, target, target, null);
                        String pAnything = OotilityCeption.ParseConsoleCommand(anything, target, target, null);

                        // Doubley
                        Double pS = OotilityCeption.MathExpressionResult(pSomething, new ArrayList<>()), pA = OotilityCeption.MathExpressionResult(pAnything, new ArrayList<>());

                        // Attempt as numeric if both parse
                        boolean isNumeric = !forceString && (pS != null) && (pA != null);

                        // Proceed to compare
                        boolean resultant = false;
                        if (isNumeric) {

                            // Evaluate Operations
                            if (equals && (pS.equals(pA))) {
                                resultant = true;
                            }
                            if (greaterThan && (pS > pA)) {
                                resultant = true;
                            }
                            if (lessThan && (pS < pA)) {
                                resultant = true;
                            }

                        } else {

                            // Caps differentation
                            if (ignoreCaps) {

                                // Compare stringally
                                if (equals && (pSomething.equalsIgnoreCase(pAnything))) {
                                    resultant = true;
                                }
                            } else {

                                // Compare stringally
                                if (equals && (pSomething.equals(pAnything))) {
                                    resultant = true;
                                }
                            }
                        }

                        // Invert result if appropiate
                        if (negate) {
                            resultant = !resultant;
                        }

                        // Success?
                        if (resultant) {

                            // Get result
                            int scoreSet = -32767;
                            if (asNumber != null) {

                                // They specified a specific number, lest go
                                scoreSet = OotilityCeption.RoundToInt(asNumber.apply((targetObjective == null ? 0.0D : OotilityCeption.GetPlayerScore(targetObjective, target) + 0.0D)));

                                // Perform operation
                            } else if (isNumeric) {

                                // Perform operations
                                try {

                                    // As dictated
                                    if (asSum) {
                                        scoreSet = OotilityCeption.RoundToInt((pS + pA) * finalMultiplier);
                                    } else if (asDifference) {
                                        scoreSet = OotilityCeption.RoundToInt((pS - pA) * finalMultiplier);
                                    } else if (asProduct) {
                                        scoreSet = OotilityCeption.RoundToInt((pS * pA) * finalMultiplier);
                                    } else if (asDivision) {
                                        scoreSet = OotilityCeption.RoundToInt((pS / pA) * finalMultiplier);
                                    } else if (asPower) {
                                        scoreSet = OotilityCeption.RoundToInt((Math.pow(pS, pA)) * finalMultiplier);
                                    } else if (asRoot) {
                                        scoreSet = OotilityCeption.RoundToInt((Math.pow(pS, 1.0D / pA)) * finalMultiplier);
                                    } else if (asCosine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.cos(pS * Math.PI / 180.0) * finalMultiplier);
                                    } else if (asSine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.sin(pS * Math.PI / 180.0) * finalMultiplier);
                                    } else if (asTangent) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.tan(pS * Math.PI / 180.0) * finalMultiplier);
                                    } else if (asArcCosine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.acos(pS) * finalMultiplier * 180.0 / Math.PI);
                                    } else if (asArcSine) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.asin(pS) * finalMultiplier * 180.0 / Math.PI);
                                    } else if (asArcTangent) {
                                        scoreSet = OotilityCeption.RoundToInt(Math.atan(pS) * finalMultiplier * 180.0 / Math.PI);
                                    }

                                } catch (Exception ignored) {
                                    scoreSet = -32767;
                                }
                            }

                            if (targetObjective != null) {

                                // Set score lma0
                                OotilityCeption.SetPlayerScore(targetObjective, target, scoreSet);

                                // Notify the success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback)
                                    logReturn.add(OotilityCeption.LogFormat("Compare", "Comparison returned true! Setting score \u00a73" + args[2] + "\u00a77 of player \u00a73" + target.getName() + "\u00a77 to \u00a7e" + scoreSet + "\u00a77."));

                            } else {

                                // Notify the success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback)
                                    logReturn.add(OotilityCeption.LogFormat("Compare", "Comparison returned true for player \u00a73" + target.getName() + "\u00a77! Result is \u00a7e" + scoreSet + "\u00a77."));

                            }

                            // Update @v
                            commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(scoreSet)));

                            // Run Chain
                            commandChain.chain(chained, target, sender);

                        } else {

                            if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, target.getPlayer(), target.getPlayer(), null, null))); }

                            // lol found no
                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback)
                                logReturn.add(OotilityCeption.LogFormat("Compare", "{Something} (\u00a73" + pSomething + "\u00a77) did not match {Anything} (\u00a73" + pAnything + "\u00a77)."));
                        }
                    }
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Compare, \u00a77Parses placeholders and changes a player's score.");
                logReturn.add("\u00a73Usage: \u00a7e/goop compare <player> <objective> <score> {Something} GC_<operator> {Anything}");
                logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player to parse placeholders, and change score.");
                logReturn.add("\u00a73 - \u00a7e<objective> \u00a77Scoreboard objective to store results IF the values match.");
                logReturn.add("\u00a73 --> \u00a7bread \u00a77Ignore scoreboard, just perform the comparison operation");
                logReturn.add("\u00a73 - \u00a7e<score> \u00a77Score value that will be given \u00a7nif this succeeds.");
                logReturn.add("\u00a73 -> If both {Something} and {Anything} are numeric, you can use these keywords:");
                logReturn.add("\u00a73 --> \u00a7bsum \u00a77The result of \u00a7e{Something}\u00a73+\u00a7e{Anything}");
                logReturn.add("\u00a73 --> \u00a7bdifference \u00a77The result of \u00a7e{Something}\u00a73-\u00a7e{Anything}");
                logReturn.add("\u00a73 --> \u00a7bproduct \u00a77The result of \u00a7e{Something}\u00a73*\u00a7e{Anything}");
                logReturn.add("\u00a73 --> \u00a7bdivision \u00a77The result of \u00a7e{Something}\u00a73/\u00a7e{Anything}");
                logReturn.add("\u00a73 --> \u00a7bpower \u00a77The result of \u00a7e{Something}\u00a73^\u00a7e{Anything}");
                logReturn.add("\u00a73 --> \u00a7broot \u00a77The \u00a7e{Anything}th \u00a73root of \u00a7e{Something}\u00a77.");
                logReturn.add("\u00a73 --> \u00a7bcos \u00a77Will ignore {Anything} and set to the Cosine of \u00a7e{Something}\u00a77 (degrees).");
                logReturn.add("\u00a73 --> \u00a7bsin \u00a77Will ignore {Anything} and set to the Sine of \u00a7e{Something}\u00a77 (degrees).");
                logReturn.add("\u00a73 --> \u00a7btan \u00a77Will ignore {Anything} and set to the Tangent of \u00a7e{Something}\u00a77 (degrees).");
                logReturn.add("\u00a73 --> \u00a7barcCos \u00a77Will ignore {Anything} and set to the ArcCosine of \u00a7e{Something}\u00a77 (degrees).");
                logReturn.add("\u00a73 --> \u00a7barcSin \u00a77Will ignore {Anything} and set to the ArcSine of \u00a7e{Something}\u00a77 (degrees).");
                logReturn.add("\u00a73 --> \u00a7barcTan \u00a77Will ignore {Anything} and set to the ArcTangent of \u00a7e{Something}\u00a77 (degrees).");
                logReturn.add("\u00a77 ----- \u00a7cValues will be multiplied by 100, basically to store two decimal places.");
                logReturn.add("\u00a77 ----- *Math Errors (\u00a7fdiv by 0, etc\u00a77) will cause score to be set to -32767, but the command will 'succeed'");
                logReturn.add("\u00a73 - \u00a7e{Something} \u00a77Any length and order of placeholders and strings (even with spaces).");
                logReturn.add("\u00a73 - \u00a7e<operator> \u00a77The way to compare. Must be preceded with \u00a7eGC_");
                logReturn.add("\u00a73 --> \u00a7b= \u00a77If these are equal. Will parse numeric placeholders automatically.");
                logReturn.add("\u00a73 --> \u00a7b> \u00a77If {Something} exceeds {Anything}. Will fail if even one isnt numerical.");
                logReturn.add("\u00a73 --> \u00a7b< \u00a77If {Something} is less than {Anything}. Will fail if even one isnt numerical.");
                logReturn.add("\u00a73 --> \u00a7bS \u00a77Will not parse numerically, and strictly check as strings (\u00a7f0.00 \u00a77wont equal \u00a7f0\u00a77, thus).");
                logReturn.add("\u00a73 --> \u00a7bC \u00a77Ignore capitalization (\u00a7fno\u00a77 =\u00a7fNo\u00a77).");
                logReturn.add("\u00a73 --> \u00a7bE \u00a77Exact Result (\u00a7fDont multiply by 100 after operation\u00a77).");
                logReturn.add("\u00a73 --> \u00a7b! \u00a77Will run the comparison pretending its not there, then invert the result.");
                logReturn.add("\u00a77 ----- \u00a73Any combination and order is acceptable: \u00a7bGC_><\u00a73 for example, will fail if they are equal.");
                logReturn.add("\u00a77 ----- \u00a73Using \u00a7bGC_<=>\u00a73 can only fail if you input non-numerical stuff.");
                logReturn.add("\u00a73 - \u00a7e{Anything} \u00a77Any length and order of placeholders and strings (even with spaces).");

            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Compare", "Incorrect usage. For info: \u00a7e/goop compare"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop compare <player> <objective> <score> {Something} GC_<operator> {Anything}");
                }
            }

            // No perms
        } else {

            // Clarify it lack of perms
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to compare data!"));
        }
        return null;
    }
}
