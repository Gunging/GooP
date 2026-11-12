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
 * The {@code /goop permission} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdPermission {

    private GooPCmdPermission() { }

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
        //   0      1          2        3       4           5   args.Length
        // /goop permission <player> <perm> [objective] [score]
        //   -      0          1        2       3           4   args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.permission");
        }

        // GOt permission?
        if (permission) {

            // Correct number of args?
            argsMinLength = 3;
            argsMaxLength = 5;
            usage = "/goop permission <player> <perm> [objective] [score]";
            subcategory = "Permission";

            // Help form?
            if (args.length == 1)  {

                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Permission, \u00a77Succeeds if the player has permission");
                logReturn.add("\u00a73Usage: \u00a7e" + usage);
                logReturn.add("\u00a73 - \u00a7e<player> \u00a77UUID, vanilla selector, player name.");
                logReturn.add("\u00a73 - \u00a7e<perm> \u00a77Permission to check.");
                logReturn.add("\u00a73      * \u00a77Negate with \u00a7b!\u00a77 prefix.");
                logReturn.add("\u00a73      * \u00a77Separate two permissions with a comma as 'OR'");
                logReturn.add("\u00a73      * \u00a77to succeed with either permission.");
                logReturn.add("\u00a73      * \u00a77Use && as 'AND' for permissions.");
                logReturn.add("\u00a73      * \u00a77Separate two permissions with \u00a7b&&\u00a77 as 'AND'");
                logReturn.add("\u00a73      * \u00a77to rquire both permissions to succeed.");
                logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Will store the score result.");
                logReturn.add("\u00a73 - \u00a7e[score] \u00a77Score result if succeeds.");
                logReturn.add("\u00a78 Can combine OR and AND, ex: perm1,perm2&&perm3,!perm4&&perm5");

                // Correct number of args?
            } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                // CHeck failure
                boolean failure = false;
                RefSimulator<String> logAddtion = new RefSimulator<>("");

                // Get Playes / Entity
                CommandSender asConsole = Bukkit.getServer().getConsoleSender(); boolean isConsole = false;
                Entity asEntity = OotilityCeption.getEntityByUniqueId(args[1]);
                ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], logAddtion);

                // Yes
                ArrayList<ArrayList<String>> permissionREQ = GCSR_Permission.Parse(args[2]);

                // Does the player exist?
                if (targets.size() < 1 && asEntity == null) {
                    // Failure
                    failure = true;

                    // Notify the error
                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target must be an online player!"));
                }

                Objective objective = null;
                PlusMinusPercent scoreOps = null;
                if (args.length == 5) {

                    // Get
                    objective = OotilityCeption.GetObjective(args[3]);
                    scoreOps = PlusMinusPercent.GetPMP(args[4], logAddtion);

                    // Log
                    if (objective == null) {

                        // Fail
                        failure = true;

                        // Notify the error
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Objective \u00a73" + args[3] + "\u00a77 not found."));
                    }

                    // PMP
                    if (scoreOps == null) {

                        // Fail
                        failure = true;

                        // Notify the error
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected numeric value or PMP instead of \u00a7e" + args[4] + "\u00a77."));
                    }
                }

                // If player was found
                if (!failure) {

                    // Parse GCSRPs
                    ArrayList<GCSR_Permission> criteria = new ArrayList<>();
                    for (ArrayList<String> permcomp : permissionREQ) { criteria.add(new GCSR_Permission(permcomp)); }

                    if (asEntity != null) {

                        // Its an AND right
                        boolean faux = false;
                        for (GCSR_Permission criterion : criteria) { if (!criterion.isUnlockedForOP()) { faux = true; break; } }

                        // Mention
                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "Non-Players \u00a7aalways\u00a77 have permission.")); }

                        // If not failure
                        if (!faux) {
                            if (objective != null) {
                                // Operate
                                OotilityCeption.SetEntryScore(objective, asEntity.getUniqueId().toString(), scoreOps);
                            }

                            // Run Chain
                            commandChain.chain(chained, asEntity.getUniqueId(), sender);
                        }
                    }

                    // Each target
                    for (Player target : targets) {

                        // Its an AND right
                        boolean faux = false; String fail = "";
                        for (GCSR_Permission criterion : criteria) { if (!criterion.isUnlockedFor(target)) { faux = true; fail = criterion.toString(); break; } }

                        // Has perm?
                        if (!faux) {

                            if (objective != null) {
                                // Operate
                                OotilityCeption.SetPlayerScore(objective, target, scoreOps);
                            }

                            // Mention
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "Player \u00a73" + target.getName() + "\u00a7a had\u00a77 the specified permissions.")); }

                            // Run Chain
                            commandChain.chain(chained, target, sender);

                        // Fail = No Perms
                        } else {

                            if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, target.getPlayer(), target.getPlayer(), null, null))); }

                            // Mention
                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "Player \u00a73" + target.getName() + "\u00a7c failed\u00a77 at meeting " + fail + " permission.")); }
                        }
                    }
                }

            // Incorrect number of args
            } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                // Notify Error
                if (args.length >= argsMinLength) {
                    logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop permission"));

                } else {

                    logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop permission"));
                }

                // Notify Usage
                logReturn.add("\u00a73Usage: \u00a7e" + usage);
            }

            // No perms
        } else {

            // Clarify it lack of perms
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou have no permission to check permissio... holdon... this command failed unspecifiedly."));
        }
        return null;
    }
}
