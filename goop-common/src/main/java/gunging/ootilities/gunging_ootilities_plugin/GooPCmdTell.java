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
 * The {@code /goop tell} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdTell {

    private GooPCmdTell() { }

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
        //   0    1      2         3...     args.Length
        // /goop tell <player> <message>
        //   -    0      1         2...     args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.tell");
        }

        // GOt permission?
        if (permission) {

            // Chek for args length
            if (args.length >= 3) {

                // CHeck for failure
                boolean failure = false;

                // Gets that player boi
                ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], null);
                if (targets.size() < 1) {
                    // Failure
                    failure = true;

                    // Notify
                    logReturn.add(OotilityCeption.LogFormat("Tell", "Player not found"));
                }

                // If player was found
                if (!failure) {

                    // Build message
                    StringBuilder messageBuilder = new StringBuilder();
                    for (int a = 2; a < args.length; a++) { messageBuilder.append(" ").append(args[a]); }
                    String message = messageBuilder.toString().substring(1);

                    // Parse font codes
                    message = GooP_FontUtils.ParseFontLinks(message);

                    // Each target
                    for (Player target : targets) {

                        // Parse message
                        String mInstance = OotilityCeption.ParseConsoleCommand(message, target, target, null);

                        // Parse again, with colour codes this time
                        mInstance = OotilityCeption.ParseColour(mInstance);

                        // Send
                        target.sendMessage(mInstance);
                        // Mention
                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("Tell", "Told \u00a73" + target.getName() + "\u00a77: \u00a7f" + mInstance)); }

                        // Run Chain
                        commandChain.chain(chained, target, sender);
                    }
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Tell, \u00a77Sends a message to a player, parses colour codes and placeholders.");
                logReturn.add("\u00a73Usage: \u00a7e/goop tell <player> <message...>");
                logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player that will be displayed such a message.");
                logReturn.add("\u00a73 - \u00a7e<message...> \u00a77Message to display");
                logReturn.add("\u00a78In more technical language, parses placeholders, and then colour codes.");
            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Tell", "Incorrect usage. For info: \u00a7e/goop tell"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop tell <player> <message...>");
                }
            }

        // No perms
        } else {

            // Clarify it lack of perms
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oCan you not tell whats happening?"));
        }
        return null;
    }
}
