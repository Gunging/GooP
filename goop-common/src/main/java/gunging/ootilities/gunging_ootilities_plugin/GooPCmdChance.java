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
 * The {@code /goop chance} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdChance {

    private GooPCmdChance() { }

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
        //   0    1             2               3...      args.Length
        // /goop chance <percent/fraction> <command...>
        //   -    0             1               2...      args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.chance");
        }

        // GOt permission?
        if (permission) {

            // Chek for args length
            if (args.length >= 3) {

                // CHeck failure
                boolean failure = false;

                // CHeck for failure
                double chance = 0;
                String unparsedChance = args[1];
                boolean percentage = unparsedChance.endsWith("%");
                if (percentage) { unparsedChance = unparsedChance.substring(0, unparsedChance.length() -1); }
                if (OotilityCeption.DoubleTryParse(unparsedChance)) {

                    // Parse
                    chance = Double.parseDouble(unparsedChance);

                    if (!percentage) { chance *= 100; }

                // Otherwise failure
                } else {

                    // Fail
                    failure = true;

                    // Notify
                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("Chance", "Expected a number (even followed by a % symbol) instead of \u00a73" + args[1])); }

                }

                // If player was found
                if (!failure) {

                    // Chance 5 Success
                    if (OotilityCeption.GetRandomInt(0, 10000) < (chance * 100)) {

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

                        // Mention
                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("Chance", "Roll of \u00a7b" + chance + "%\u00a77 chance \u00a7asucceeded\u00a77!")); }

                        // Just run I guess
                        OotilityCeption.SendConsoleCommand(cmddd, null, null, null);

                    } else {

                        // Mention
                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Chance", "Roll of \u00a7b" + chance + "%\u00a77 chance \u00a7cfailed\u00a77!")); }

                    }
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Chance, \u00a77Random chance to run a command");
                logReturn.add("\u00a73Usage: \u00a7e/goop chance <chance> <command>");
                logReturn.add("\u00a73 - \u00a7e<chance> \u00a77Chance to run following command");
                logReturn.add("\u00a73 - \u00a7e<command> \u00a77Command to execute");

            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Chance", "Incorrect usage. For info: \u00a7e/goop chance"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop chance <chance> <command>");
                }
            }

        // No perms
        } else {

            // Clarify it lack of perms
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oWhat are the odds of this?!"));
        }
        return null;
    }
}
