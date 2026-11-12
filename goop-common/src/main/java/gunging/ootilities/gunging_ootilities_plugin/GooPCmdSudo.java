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
 * The {@code /goop sudo} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdSudo {

    private GooPCmdSudo() { }

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
        //   0    1      2        3...      args.Length
        // /goop delay <entity> <command...>
        //   -    0      1        2...      args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.sudo");
        }

        // GOt permission?
        if (permission) {

            // Chek for args length
            if (args.length >= 3) {

                // CHeck failure
                boolean failure = false;
                boolean asOP = args[0].toLowerCase().endsWith("p");
                RefSimulator<String> logAddtion = new RefSimulator<>("");

                // Get Playes / Entity
                CommandSender asConsole = Bukkit.getServer().getConsoleSender(); boolean isConsole = false;
                Entity asEntity = OotilityCeption.getEntityByUniqueId(args[1]);
                ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], logAddtion);

                // not console?
                if (!args[1].equals("console")) {
                    //DBG//OotilityCeption.Log("\u00a73sudop\u00a77 Sudoing entity? \u00a7e" + (asEntity != null));
                    //DBG//OotilityCeption.Log("\u00a73sudop\u00a77 Sudoing players? \u00a7e" + targets.size());

                    // Does the player exist?
                    if (targets.size() < 1 && asEntity == null) {
                        // Failure
                        failure = true;

                        // Notify the error
                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Sudo", "Target must be an online player!"));
                    }

                // Was Console
                } else {

                    // As console IG
                    isConsole = true;
                    //DBG//OotilityCeption.Log("\u00a73sudop\u00a77 Sudoing console");
                }

                // If player was found
                if (!failure) {

                    // Build Command
                    StringBuilder lLine;

                    // Gather initial value
                    lLine = new StringBuilder(args[2]);
                    for (int i = 3; i < args.length; i++) {

                        // Add, separating with spaces
                        lLine.append(" ").append(args[i]);
                    }

                    // Append chained
                    if (chained) { lLine.append(" oS= ").append(commandChain.getFlaredCommand()); }
                    String cmddd = lLine.toString();

                    if (!isConsole) {

                        // Run
                        for (Player target : targets) {
                            //DBG//OotilityCeption.Log("\u00a73sudop\u00a77 Running as \u00a7e" + target.getName() + "\u00a73 " + cmddd);

                            // OP Perms?
                            boolean wasOP = target.isOp();
                            if (asOP) { target.setOp(true); }

                            // Attempt to run command
                            try {

                                // Run as player
                                OotilityCeption.SendAndParseConsoleCommand(target, cmddd, target, null, null, null, null);

                            // You cannot escape me
                            } catch (Throwable ignored) { }

                            // Revert
                            if (asOP && !wasOP) { target.setOp(false); }
                        }

                        // Run
                        if (asEntity != null) {
                            //DBG//OotilityCeption.Log("\u00a73sudop\u00a77 Running as \u00a7e" + asEntity.getName() + "\u00a73 " + cmddd);

                            // As Entity
                            OotilityCeption.SendAndParseConsoleCommand(asEntity.getUniqueId(), cmddd, asEntity, null, null, null, null);
                        }

                    // Lest go
                    } else {
                        //DBG//OotilityCeption.Log("\u00a73sudop\u00a77 Running as \u00a7econsole\u00a73 " + cmddd);

                        // As Entity
                        OotilityCeption.SendAndParseConsoleCommand(cmddd, asConsole, null, null, null);

                    }
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Sudo, \u00a77Execute a command as someone");
                logReturn.add("\u00a73Usage: \u00a7e/goop sudo <entity> <command>");
                logReturn.add("\u00a73 - \u00a7e<entity> \u00a77UUID, vanilla selector, player name");
                logReturn.add("\u00a73 - \u00a7e<command> \u00a77Command to execute");
                logReturn.add("\u00a7c> \u00a77May use command \u00a7csudop\u00a77 to run with OP perms.");
                logReturn.add("\u00a78Be aware that this turns players OP for the sync duration of the command, and they will remain OPd if the command crashes the server while executing. Async commands may not finish executing by the time a player is reverted to normal.");

            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Sudo", "Incorrect usage. For info: \u00a7e/goop sudo"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop sudo <players / entity uuid> <command>");
                }
            }

            // No perms
        } else {

            // Clarify it lack of perms
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oNo permission to hyjack people's command sending abilities."));
        }
        return null;
    }
}
