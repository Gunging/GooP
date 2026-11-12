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
 * The {@code /goop unlockables} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdUnlockables {

    private GooPCmdUnlockables() { }

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
        // Delegate onto
        onCommand_GooPUnlock(self, sender, command, label, args, senderLocation, chained, commandChain, logReturnUrn, failMessage);

        // Extract
        if (logReturnUrn.getValue() != null) {

            // For
            for (String ret : logReturnUrn.getValue()) {

                // Add
                logReturn.add(ret);
            }
        }
        return null;
    }

    static void onCommand_GooPUnlock(@NotNull GungingOotilities self, @NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args, @Nullable Location senderLocation, boolean chained, @Nullable SuccessibleChain commandChain, @NotNull RefSimulator<List<String>> logReturnUrn, @Nullable String failMessage) {
        OotilityCeption oots = self.oots;
        // Has permission?
        boolean permission = true;

        // What will be said to the caster (caster = sender of command)
        List<String> logReturn = new ArrayList<>();

        // Check 5 Permission
        if (sender instanceof Player) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.unlockables");
        }

        if (permission) {

            if (args.length >= 2) {

                // Help Parameters
                int argsMinLength, argsMaxLength;
                String subcommand, usage, subcategory;

                //   0    1      2      3      4    5+       args.Length
                // /goop unlockables {a} <player> <goal> {a}
                //   -    0      1      2       3    4+       args[n]

                // Failure
                boolean failure = false;

                // Gets the players
                ArrayList<GooPUnlockableTarget> targets = new ArrayList<>();
                if (args.length > 2) {
                    for (Player p : OotilityCeption.GetPlayers(senderLocation, args[2], null)) { targets.add(new GOOPUCKTPlayer(p)); }
                    if ("server".equalsIgnoreCase(args[2])) { targets.add(new GOOPUCKTServer()); }
                    String[] split = args[2].contains(";") ? args[2].split(";") : new String[] { args[2] };
                    for (String s : split) { UUID u = OotilityCeption.UUIDFromString(s); if (u != null) { targets.add(new GOOPUCKTUnique(u)); } }}

                // Switch
                switch (args[1].toLowerCase()) {
                    //region Unlock
                    case "unlock":
                        //   0        1        2       3       4     5        6        7         args.Length
                        // /goop unlockables unlock <player> <goal> [x] [reset timer] [time]
                        //   -        0        1       2       3     4        5        6         args[n]

                        // Correct number of args?
                        argsMinLength = 4;
                        argsMaxLength = 7;
                        usage = "/goop unlockables unlock <player> <goal> [x] [reset timer] [time]";
                        subcommand = "Unlock";
                        subcategory = "Unlockables - Unlock";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Unlockables - \u00a7b" + subcommand + ",\u00a77 Unlocks an unlockable :B");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who will unlock this goal.");
                            logReturn.add("\u00a73      * \u00a7bserver \u00a77Global unlockable for the entire server");
                            logReturn.add("\u00a73 - \u00a7e<goal> \u00a77Name of the goal.");
                            logReturn.add("\u00a73 - \u00a7e[x] \u00a77Value of the goal, \u00a7btrue\u00a77 by default.");
                            logReturn.add("\u00a73      * \u00a7btoggle\u00a77 Always succeeds, will lock the unlockable if it was unlocked. ");
                            logReturn.add("\u00a73      * \u00a77Can be any number except 0, instead.");
                            logReturn.add("\u00a73 - \u00a7e[reset timer] \u00a77If this new timer will take prevalence.");
                            logReturn.add("\u00a73      * \u00a7bfalse \u00a77The old timer will remain, but if this");
                            logReturn.add("\u00a73          \u00a77goal was locked, the new timer will be set.");
                            logReturn.add("\u00a73 - \u00a7e[time] \u00a77Time this goal will remain unlocked.");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Does the player exist?
                            if (targets.size() < 1) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target must be an online player!"));
                            }

                            // Get
                            PlusMinusPercent ex = null;
                            boolean astoggle = false;
                            if (args.length >= 5) {
                                String exArg = args[4];
                                astoggle = "toggle".equals(exArg);

                                if (!astoggle) {

                                    // Correct
                                    switch (exArg.toLowerCase()) {
                                        case "true":
                                            exArg = "1";
                                            break;
                                        case "false":
                                            exArg = "0";
                                            break;
                                    }

                                    // Ref Sim
                                    RefSimulator<String> logAddition = new RefSimulator<>("");

                                    // Try parse
                                    ex = PlusMinusPercent.GetPMP(exArg, logAddition);

                                    // Log
                                    if (logAddition != null && logAddition.getValue() != null && logAddition.getValue().length() > 1) { logReturn.add(OotilityCeption.LogFormat(subcategory, logAddition.getValue())); }

                                    if (ex == null) {

                                        // Failure
                                        failure = true;
                                    }
                                }
                            }

                            // Timer
                            OptimizedTimeFormat otf = null;
                            if (args.length >= 7) {

                                // Identify time measure
                                String magnitude = args[6].substring(0, args[6].length() - 1);
                                String units = args[6].substring(args[6].length() - 1);

                                // Parses?
                                if (OotilityCeption.IntTryParse(magnitude)) {

                                    // Kreate
                                    otf = OptimizedTimeFormat.Current();
                                    Integer mag = OotilityCeption.ParseInt(magnitude);
                                    if (mag < 0) { mag = -mag; }

                                    // Units?
                                    switch (units) {
                                        case "s":
                                            otf.AddSeconds(mag);
                                            break;
                                        case "m":
                                            otf.AddMinutes(mag);
                                            break;
                                        case "h":
                                            otf.AddHours(mag);
                                            break;
                                        case "d":
                                            otf.AddDays(mag);
                                            break;
                                        case "y":
                                            otf.AddYears(mag);
                                            break;
                                        default:

                                            // Fail
                                            failure = true;

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Time units '" + units + "\u00a77' not recognized. Must use \u00a7bs m h d \u00a77or\u00a7b y \u00a77(Second, Minute, Hour, Day, Year)"));

                                            break;
                                    }

                                } else {

                                    // Fail
                                    failure = true;

                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected an integer for time value instead of \u00a73" + magnitude));
                                }
                            }

                            boolean reset = false;
                            if (args.length >= 6) {

                                // Parse
                                if (OotilityCeption.BoolTryParse(args[5])) {

                                    reset = Boolean.parseBoolean(args[5]);

                                } else {

                                    // Fail
                                    failure = true;

                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected \u00a7btrue\u00a77 or \u00a7bfalse\u00a77 instead of \u00a73" + args[5]));

                                }
                            }

                            // No more failure
                            if (!failure) {

                                // For every player
                                for (GooPUnlockableTarget target : targets) {

                                    // Chaining succ
                                    failure = false;
                                    boolean chainingSuccess = false;

                                    // Unlock
                                    GooPUnlockables uck = GooPUnlockables.From(target.getUniqueId(), args[3]);
                                    uck.CheckTimer();
                                    String logMod = "";

                                    // No new timer was specified
                                    if (otf == null) {

                                        // What to do with the old timer?
                                        if (reset) {

                                            // If unlocked
                                            if (uck.IsUnlocked()) {

                                                // Timed to begin with
                                                if (uck.isTimed()) {

                                                    // Remove it
                                                    uck.SetTimed(null);

                                                    // Run Chain
                                                    chainingSuccess = true;

                                                    logMod = "\u00a77 Removed old goal timer.";
                                                }

                                            // Not unlocked
                                            } else {

                                                // Failure
                                                failure = true;

                                                logMod = "\u00a77 Failed to remove goal timer: \u00a7eGoal is not unlocked!";
                                            }
                                        }

                                    // A new timer was specified
                                    } else {

                                        // Not forcing reset but, its not unlocked in the first place... or forcing a timer reset sure.
                                        if (reset || !uck.IsUnlocked()) {

                                            // Replace it
                                            uck.SetTimed(otf);

                                            // Run Chain
                                            chainingSuccess = true;

                                            // Already unlocked?
                                            if (uck.isTimed()) {

                                                logMod = "\u00a77 Set goal to automatically lock \u00a73" + args[6] + "\u00a77 from now.";

                                            } else {


                                                logMod = "\u00a77 Replaced old goal timer with new one.";
                                            }

                                        }
                                    }

                                    // If toggling, the success conditions are changed
                                    if (astoggle) {

                                        // If its not yet unlocked
                                        if (uck.IsUnlocked()) {

                                            // Unlock
                                            uck.Lock();

                                            // Ntify
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 has \u00a72successfuly\u00a77 locked goal \u00a7e" + args[3] + "\u00a77." + logMod));

                                        } else {

                                            // Unlock
                                            uck.Unlock();

                                            // Ntify
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 has \u00a7asuccessfuly\u00a77 unlocked goal \u00a7e" + args[3] + "\u00a77." + logMod));
                                        }

                                        // Run Chain
                                        chainingSuccess = true;

                                        // Save
                                        uck.Save();

                                    // If not toggling, work normally
                                    } else {

                                        // If its not yet unlocked
                                        if (!failure && (!uck.IsUnlocked() || ex != null)) {

                                            // Raw Unlock
                                            if (ex == null) {

                                                // Unlock
                                                uck.Unlock();

                                                // Ntify
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 has \u00a7asuccessfuly\u00a77 unlocked goal \u00a7e" + args[3] + "\u00a77." + logMod));

                                                // Run Chain
                                                chainingSuccess = true;

                                                // Save
                                                uck.Save();

                                                // Unlock opps
                                            } else {

                                                // Get
                                                double u = uck.GetUnlock();

                                                // Oppe
                                                uck.SetUnlock(ex.apply(u));

                                                // Must be diff
                                                if (u != uck.GetUnlock()) {

                                                    // Ntify
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 has \u00a7asuccessfuly\u00a77 unlocked goal \u00a7e" + args[3] + "\u00a77 at \u00a7b" + uck.GetUnlock() + "\u00a77." + logMod));

                                                    // Run Chain
                                                    chainingSuccess = true;

                                                    // Save
                                                    uck.Save();

                                                } else {

                                                    // L
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 already had goal \u00a7e" + args[3] + "\u00a77 unlocked." + logMod));
                                                }
                                            }

                                        } else {

                                            // L
                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 alraedy had goal \u00a7e" + args[3] + "\u00a77 unlocked." + logMod));
                                        }
                                    }

                                    // Run Chain
                                    if (chainingSuccess) { commandChain.chain(chained, (target instanceof GOOPUCKTPlayer) ? ((GOOPUCKTPlayer) target).getPlayer() : null, sender); }

                                    else if (!chainingSuccess) {

                                        if (failMessage != null && target instanceof GOOPUCKTPlayer) { ((GOOPUCKTPlayer) target).getPlayer().sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, ((GOOPUCKTPlayer) target).getPlayer(), ((GOOPUCKTPlayer) target).getPlayer(), null, null))); }
                                    }
                                }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop unlockables unlock"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop unlockables unlock"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }

                        break;
                    //endregion
                    //region Lock
                    case "lock":
                        //   0    1        2       3      4      args.Length
                        // /goop unlockables lock <player> <goal>
                        //   -    0        1       2      3      args[n]

                        // Correct number of args?
                        argsMinLength = 4;
                        argsMaxLength = 4;
                        usage = "/goop unlockables lock <player> <goal>";
                        subcommand = "Lock";
                        subcategory = "Unlockables - Lock";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Unlockables - \u00a7b" + subcommand + ",\u00a77 Locks an unlockable.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who will unlock this goal.");
                            logReturn.add("\u00a73      * \u00a7bserver \u00a77Global unlockable for the entire server");
                            logReturn.add("\u00a73 - \u00a7e<goal> \u00a77Name of the goal to lock");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Does the player exist?
                            if (targets.size() < 1) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target must be an online player!"));
                            }

                            // No more failure
                            if (!failure) {

                                // For every player
                                for (GooPUnlockableTarget target : targets) {

                                    // Unlock
                                    GooPUnlockables uck = GooPUnlockables.From(target.getUniqueId(), args[3]);
                                    uck.CheckTimer();

                                    // If its not yet unlocked
                                    if (uck.IsUnlocked()) {

                                        // Unlock
                                        uck.Lock();

                                        // Ntify
                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 now has goal \u00a7e" + args[3] + "\u00a77 as \u00a7clocked\u00a77."));

                                        // Run Chain
                                        commandChain.chain(chained, (target instanceof GOOPUCKTPlayer) ? ((GOOPUCKTPlayer) target).getPlayer() : null, sender);

                                        // Save
                                        uck.Save();

                                    } else {

                                        if (failMessage != null && target instanceof GOOPUCKTPlayer) { ((GOOPUCKTPlayer) target).getPlayer().sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, ((GOOPUCKTPlayer) target).getPlayer(), ((GOOPUCKTPlayer) target).getPlayer(), null, null))); }

                                        // L
                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 alraedy had goal \u00a7e" + args[3] + "\u00a77 locked."));
                                    }
                                }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop unlockables lock"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop unlockables lock"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Lockstate
                    case "lockstate":
                        //   0          1        2          3      4        5      [5]   6     [6]   7      args.Length
                        // /goop unlockables lockstate <player> <goal> [range] [objective] [±][score][%]
                        //   -          0        1          2      3        4      [4]   5     [5]   6      args[n]

                        // Correct number of args?
                        argsMinLength = 4;
                        argsMaxLength = 7;
                        usage = "/goop unlockables lockstate <player> <goal> [range] [objective] [±][score][%]";
                        subcommand = "LockState";
                        subcategory = "Unlockables - LockState";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Unlockables - \u00a7b" + subcommand + ",\u00a77 Read the lock state of a goal.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who will unlock this goal.");
                            logReturn.add("\u00a73      * \u00a7bserver \u00a77Global unlockable for the entire server");
                            logReturn.add("\u00a73 - \u00a7e<goal> \u00a77Name of the goal to lock");
                            logReturn.add("\u00a73 - \u00a7e[range] \u00a77Range by which this command succeeds");
                            logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Objective to write the score onto");
                            logReturn.add("\u00a73 - \u00a7e[±][score][%] \u00a77Score operation to perform");
                            logReturn.add("\u00a73      * \u00a7bread \u00a77To read the unlockable value.");
                            logReturn.add("\u00a73      * \u00a7bread# \u00a77Read but multiply by # (a number) in the score.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Does the player exist?
                            if (targets.size() < 1) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Unlock - LockState", "Target must be an online player!"));
                            }

                            // Get
                            QuickNumberRange tLock = null;
                            boolean anySuccess = false;
                            int lockArgInsert = 0;
                            if (args.length >= 5) {
                                anySuccess = args[4].toLowerCase().equals("true");

                                if (OotilityCeption.BoolTryParse(args[4])) {

                                    // Parse
                                    Boolean tBool = Boolean.parseBoolean(args[4]);

                                    // Modify if tru
                                    if (tBool) { tLock = new QuickNumberRange(1.0, 1.0); }
                                    else { tLock = new QuickNumberRange(0.0, 0.0); }
                                    
                                    // A lock range was indeed shown
                                    lockArgInsert++;

                                // Neither, cancel
                                } else {

                                    // Parse Heab
                                    tLock = QuickNumberRange.FromString(args[4]);

                                    // Valid? Accept
                                    if (tLock != null) { lockArgInsert++; }
                                }
                            }

                            // Obj
                            Objective targetObjective = null;
                            PlusMinusPercent targetScore = null;
                            boolean readMode = false;
                            double readOps = 1;
                            if (args.length >= (5 + lockArgInsert)) {

                                // Ref
                                RefSimulator<String> retAddition = new RefSimulator<>("");
                                
                                String objectiveName = args[4 + lockArgInsert];
                                String scoreOperation = "read";
                                if (args.length >= 6 + lockArgInsert) { scoreOperation = args[5 + lockArgInsert]; }

                                // Parse
                                targetObjective = OotilityCeption.GetObjective(objectiveName);
                                
                                if (scoreOperation.startsWith("read")) {
                                    readMode = true; 
                                
                                    // Parse Operation Value
                                    String ops = scoreOperation.substring("read".length());
                                    if (OotilityCeption.DoubleTryParse(ops)) { readOps = Double.parseDouble(ops); }
                                    
                                // Not 'read' keyword, parse as PMP
                                } else { targetScore = PlusMinusPercent.GetPMP(scoreOperation, retAddition); }

                                // Objective not found (RIP)
                                if (targetObjective == null) {

                                    // Fail
                                    failure = true;

                                    // Note
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Could not find objective of name \u00a73" + objectiveName));
                                }

                                // Got anything to say?
                                if (retAddition != null && retAddition.getValue() != null && retAddition.getValue().length() > 1) { logReturn.add(OotilityCeption.LogFormat(subcategory, retAddition.getValue())); }

                                // No score operation, but its also not read mode
                                if (targetScore == null && !readMode) {

                                    // Not intended
                                    failure = true;
                                }
                            }

                            // No more failure
                            if (!failure) {

                                // For every player
                                for (GooPUnlockableTarget target : targets) {

                                    // Unlock
                                    GooPUnlockables uck = GooPUnlockables.From(target.getUniqueId(), args[3]);
                                    uck.CheckTimer();

                                    // Get Score
                                    double fax = uck.GetUnlock();

                                    /*
                                     * Range success only considered when specifiyng one
                                     */
                                    boolean rangeSuccess = true;
                                    if (tLock != null) { rangeSuccess = tLock.InRange(fax); }
                                    else if (anySuccess) { rangeSuccess = uck.IsUnlocked(); }

                                    // Range success?
                                    if (rangeSuccess) {

                                        // Run Chain
                                        commandChain.chain(chained, (target instanceof GOOPUCKTPlayer) ? ((GOOPUCKTPlayer) target).getPlayer() : null, sender);

                                        // Score check
                                        if (targetObjective != null && target instanceof GOOPUCKTPlayer) {

                                            // Declassify score
                                            if (readMode) { targetScore = new PlusMinusPercent(uck.GetUnlock() * readOps, false, false); }

                                            // Set
                                            OotilityCeption.SetPlayerScore(targetObjective, ((GOOPUCKTPlayer) target).getPlayer(), targetScore);

                                            // Ntify
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a7a had \u00a77goal in the correct lock state. Set their score \u00a73" + targetObjective.getName() + "\u00a77 to \u00a7b" + OotilityCeption.GetPlayerScore(targetObjective, ((GOOPUCKTPlayer) target).getPlayer())));

                                        } else {

                                            // Ntify
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a7a had \u00a77goal in the correct lock state."));
                                        }

                                    // Score doesnt match
                                    } else {

                                        if (failMessage != null && target instanceof GOOPUCKTPlayer) { ((GOOPUCKTPlayer) target).getPlayer().sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, ((GOOPUCKTPlayer) target).getPlayer(), ((GOOPUCKTPlayer) target).getPlayer(), null, null))); }

                                        // Ntify
                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, target.getName() + "\u00a77 did \u00a7cnot\u00a77 have the goal in the correct unlock state (\u00a7e" + uck.GetUnlock() + "\u00a77)."));
                                    }
                                }
                            }
                            
                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop unlockables lockstate"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop unlockables lockstate"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Check
                    case "check":
                        //   0      1           2      3     args.Length
                        // /goop unlockables check <player>
                        //   -      0           1      2     args[n]

                        // Correct number of args?
                        argsMinLength = 3;
                        argsMaxLength = 3;
                        usage = "/goop unlockables check <player> <goal>";
                        subcommand = "Check";
                        subcategory = "Unlockables - Check";
                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Unlockables - \u00a7b" + subcommand + ",\u00a77 Print the unlockable states of a player.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player to check unlocks.");
                            logReturn.add("\u00a73      * \u00a7bserver \u00a77Check active global unlockables");
                            logReturn.add("\u00a78Meant for admin use, it accomplishes nothing except letting you read a player's unlockable values.");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Player is offline yo
                            OfflinePlayer asOffline = OotilityCeption.GetPlayer(args[2], true);

                            // Does the player exist?
                            if (targets.size() < 1 && asOffline == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target must be an online player!"));
                            }

                            // No more failure
                            if (!failure) {

                                // Strs
                                String col, bol = " \u00a7c", suc = " \u00a7a";

                                // The offline one yes
                                if (asOffline != null) {
                                    logReturn.add("\u00a7b+++\u00a77 For player \u00a7e" + asOffline.getName());
                                    for (GooPUnlockables uck : GooPUnlockables.getRegisteredTo(asOffline.getUniqueId())) {
                                        if (uck == null) { continue; }
                                        uck.CheckTimer();
                                        if (uck.IsUnlocked()) { col = suc; } else { col = bol; }

                                        String timeRemaining = "";
                                        if (uck.isTimed()) {
                                            timeRemaining = "\u00a73 " + OotilityCeption.NicestTimeValueFrom((double) OotilityCeption.SecondsElapsedSince(OptimizedTimeFormat.Current(), uck.GetTimed())); }

                                        logReturn.add("\u00a7a> \u00a77 " + uck.getGoalname() + col + uck.IsUnlocked() + timeRemaining); } }

                                // For every player
                                for (GooPUnlockableTarget target : targets) {
                                    if (asOffline != null && target.getUniqueId().equals(asOffline.getUniqueId())) { continue; }

                                    logReturn.add("\u00a7b+++\u00a77 For player \u00a7e" + target.getName());
                                    for (GooPUnlockables uck : GooPUnlockables.getRegisteredTo(target.getUniqueId())) {
                                        if (uck == null) { continue; }
                                        uck.CheckTimer();
                                        if (uck.IsUnlocked()) { col = suc; } else { col = bol; }

                                        String timeRemaining = "";
                                        if (uck.isTimed()) {
                                            timeRemaining = "\u00a73 " + OotilityCeption.NicestTimeValueFrom((double) OotilityCeption.SecondsElapsedSince(OptimizedTimeFormat.Current(), uck.GetTimed())); }

                                        logReturn.add("\u00a7a> \u00a77 " + uck.getGoalname() + col + uck.IsUnlocked() + timeRemaining);
                                    }
                                }
                            }

                            // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop unlockables check"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop unlockables check"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion

                    default:
                        // I have no memory of that shit
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Unlockables", "'\u00a73" + args[1] + "\u00a77' is not a valid Unlockables action! do \u00a7e/goop unlockables\u00a77 for the list of actions."));
                        break;
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Unlockables, \u00a77Yet another way of 'unlocking' stuff.");
                logReturn.add("\u00a73Usage: \u00a7e/goop unlockables {action}");
                logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                logReturn.add("\u00a73 --> \u00a7eunlock <player> <goal> [x] [reset timer] [time]");
                logReturn.add("\u00a73      * \u00a77Target goal is now unlocked by target player");
                logReturn.add("\u00a73      * \u00a77You may specify a non-zero value for X to store");
                logReturn.add("\u00a73 --> \u00a7elock <player> <goal>");
                logReturn.add("\u00a73      * \u00a77Target goal, to target player, is now locked");
                logReturn.add("\u00a73 --> \u00a7elockstate <player> <goal> <x> [objective] [score]");
                logReturn.add("\u00a73      * \u00a77Checks that the goal is locked or unlocked.");
                logReturn.add("\u00a73      * \u00a77You may specify a range, \u00a7b0..10\u00a77 for x.");
                logReturn.add("\u00a73      * \u00a78Note that '\u00a7clocked\u00a78' means that \u00a7cx = 0.0");
                logReturn.add("\u00a73 --> \u00a7echeck <player>");
                logReturn.add("\u00a73      * \u00a77For debugging, check the goals of all the players.");
                logReturn.add("\u00a73      * \u00a77This just prints their values so you can read them.");
            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Unlock", "Incorrect usage. For info: \u00a7e/goop unlockables"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop unlockables {action} <player> <goal>");
                }
            }

        } else {

            // Tell him lmao
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission unlock stuff this way!"));
        }

        //Set Log Return Urn Value
        logReturnUrn.SetValue(logReturn);
    }
}
