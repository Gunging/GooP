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
 * The {@code /goop tp} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdTp {

    private GooPCmdTp() { }

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
        // /goop tp [who] <where> [invis]
        //   -    0      1        2...      args[n]

        // Check 5 Permission
        if (senderIsPlayer) {

            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.tp");
        }

        // GOt permission?
        if (permission) {

            // Chek for args length
            if (args.length == 2 || args.length == 3 || args.length == 5 || args.length == 6) {

                // CHeck for failure
                boolean failure = false;

                // Identify args
                String whoms = "";
                String pW = null, pX = null, pY = null, pZ = null;
                String pYaw = null, pPitch = null;
                Player pAt;
                Location targetLocation = null;

                //   0    1    [2]   [3]  2  [4]3  [5]4  [6]5   args.Length
                // /goop tp [player] <world>  <x>   <y>   <z>
                //   -    0    [1]   [2]  1  [3]2  [4]3  [5]4   args[n]
                if (args.length >= 5) {

                    // Starting from last
                    for (int a = args.length - 1; a > 0; a--) {

                        // In expected order
                        if (pZ == null) { pZ = args[a]; }
                        else if (pY == null) { pY = args[a];}
                        else if (pX == null) { pX = args[a];}
                        else if (pW == null) { pW = args[a];}
                        else break;
                    }

                    // Valid location?
                    RefSimulator<String> logAddition = new RefSimulator<>("");
                    Player p = null; if (senderIsPlayer) { p = (Player) sender; }
                    targetLocation = OotilityCeption.ValidLocation(p, senderLocation, pW, pX, pY, pZ, logAddition);

                    // Ret
                    if (targetLocation == null) { failure = true; }

                    // Add Log
                    if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Tp", logAddition.GetValue())); }
                }

                // Not parsing locaton, last arg must be a player
                if (args.length < 5) {

                    // Just get ig
                    pAt = (Player) OotilityCeption.GetPlayer(args[args.length - 1], false);

                    // Success?
                    if (pAt == null) {

                        // No rip
                        failure = true;

                        // Notify
                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Tp", "Could not find location of player \u00a73" + args[args.length-1]));}
                    }

                    // Thus thats their location
                    targetLocation = pAt.getLocation();
                }

                // So we must have a location or a target, right?
                if (!failure) {

                    // Is it implied?
                    if (args.length == 2 || args.length == 5) {

                        // Must be a player
                        if (sender instanceof  Player) {

                            // Gets the name
                            whoms = ((Player) sender).getName();


                        // Cant implicitly tp the console
                        } else {

                            // Hey!
                            failure = true;

                            // Log
                            logReturn.add(OotilityCeption.LogFormat("Tp", "You must specify who to teleport when called from the console!"));
                        }

                    // No, its explicit
                    } else {

                        // Will be 1
                        whoms = args[1];
                    }
                }

                // Gets that player boi
                ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, whoms, null);
                if (targets.size() < 1 && !failure) {
                    // Failure
                    failure = true;

                    // Notify
                    logReturn.add(OotilityCeption.LogFormat("Tp", "Player not found"));
                }

                // If player was found
                if (!failure) {

                    // Each target
                    for (Player target : targets) {

                        // Teleport lma0
                        target.teleport(targetLocation);

                        // Mention
                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("Tp", "Teleported \u00a73" + target.getName() + "\u00a77 to \u00a7e" + OotilityCeption.BlockLocation2String(targetLocation))); }

                        // Run Chain
                        commandChain.chain(chained, target, sender);
                    }
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Teleport, \u00a77Straight up teleports someone somewhere");
                logReturn.add("\u00a73Usage: \u00a7e/goop tp [who] <where>");
                logReturn.add("\u00a73 - \u00a7e[who] \u00a77Players to teleport; you if missing.");
                logReturn.add("\u00a73 - \u00a7e<where> \u00a77Location to teleport them all.");
                logReturn.add("\u00a7b ---> \u00a77May be another player's name.");
                logReturn.add("\u00a7b ---> \u00a77May in the format \u00a7ew x y z");
                logReturn.add("\u00a78Because vanilla command does not support world ffs.");
            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Teleport", "Incorrect usage. For info: \u00a7e/goop tp"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop tp [who] <where>");
                }
            }

            // No perms
        } else {

            // Clarify it lack of perms
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oCant zip zoom, no permission."));
        }
        return null;
    }
}
