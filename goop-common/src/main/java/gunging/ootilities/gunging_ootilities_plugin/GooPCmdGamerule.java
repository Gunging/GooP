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
 * The {@code /goop gamerule} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdGamerule {

    private GooPCmdGamerule() { }

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
        //   0      1       2      3    args.Length
        // /goop gamerule <rule> value
        //   -      0       1      2    args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.gamerule");
        }

        // Got permission?
        if (permission) {

            if (args.length == 3) {
                Boolean val;

                // Get that user intended value
                if (OotilityCeption.BoolTryParse(args[2])){

                    val = OotilityCeption.BoolParse(args[2]);

                    // Split Logga
                    Player chosenDev = null;
                    Integer intarg = 0;

                    // If has hyphen
                    if (args[1].contains("-")) {

                        // Split by hyphen
                        String[] devSplit = args[1].split("-");

                        // Repalce
                        args[1] = devSplit[0];

                        // If exists
                        if (devSplit.length > 1) {

                            // Get Dev Name
                            if (args[1].startsWith("dev")) {

                                // Get Player. Must be online
                                chosenDev = (Player) OotilityCeption.GetPlayer(devSplit[1], false);
                            }
                        }
                    }

                    Gunging_Ootilities_Plugin.theMain.reloadConfig();

                    switch (args[1].toLowerCase()){
                        case "sf":
                        case "sendfeedback":
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73sendSuccessFeedback\u00a77 updated to \u00a7b" + val));
                            if (!Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73sendSuccessFeedback\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.sendGooPSuccessFeedback = val;
                            if (Gunging_Ootilities_Plugin.saveGamerulesConfig) Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("SendSuccessFeedback", val);
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73sendFailFeedback\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.sendGooPFailFeedback = val;
                            if (Gunging_Ootilities_Plugin.saveGamerulesConfig) Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("SendFailFeedback", val);
                            break;
                        case "ssf":
                        case "sendsuccessfeedback":
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73sendSuccessFeedback\u00a77 updated to \u00a7b" + val));
                            if (!Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73sendSuccessFeedback\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.sendGooPSuccessFeedback = val;
                            if (Gunging_Ootilities_Plugin.saveGamerulesConfig) Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("SendSuccessFeedback", val);
                            break;
                        case "sff":
                        case "sendfailfeedback":
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73sendFailFeedback\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.sendGooPFailFeedback = val;
                            if (Gunging_Ootilities_Plugin.saveGamerulesConfig) Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("SendFailFeedback", val);
                            break;
                        case "blockerrorfeedback":
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73blockErrorFeedback\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.blockImportantErrorFeedback = val;
                            if (Gunging_Ootilities_Plugin.saveGamerulesConfig) Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("BlockErrorFeedback", val);
                            break;
                        case "griefbreaksbedrock":
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a7bgriefBreaksBedrock\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.griefBreaksBedrock = val;
                            if (Gunging_Ootilities_Plugin.saveGamerulesConfig) Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("GriefBedrockCommand", val);
                            break;
                        case "anvilrename":
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a7banvilRename\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.anvilRenameEnabled = val;
                            if (Gunging_Ootilities_Plugin.saveGamerulesConfig) Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("AnvilRename", val);
                            break;
                        case "savegamerulechanges":
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "Gamerule \u00a73saveGameruleChanges\u00a77 updated to \u00a7b" + val));
                            Gunging_Ootilities_Plugin.saveGamerulesConfig = val;

                            // Always updated, regardless of itself
                            Gunging_Ootilities_Plugin.theMain.UpdateConfigBool("SaveGameruleChanges", val);
                            break;
                        case "dev":
                            // No rememberance, thus no save regard
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "\u00a7eToggled Developer Logging to: \u00a7b" + val));
                            Gunging_Ootilities_Plugin.devLogging = val;

                            // Update Player
                            if (chosenDev != null) { Gunging_Ootilities_Plugin.devPlayer = chosenDev; }
                            break;
                        default:
                            // I have no memory of that shit
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "'\u00a73" + args[1] + "\u00a77' is not a valid gamerule! do \u00a7e/goop gamerule\u00a77 for the list of gamerules."));
                            break;
                    }

                    // Maybe this!?
                    if (Gunging_Ootilities_Plugin.saveGamerulesConfig) { Gunging_Ootilities_Plugin.theMain.saveConfig(); }

                } else {

                    // Vro thats not a bool
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Gamerule", "The accepted values are \u00a7btrue \u00a77and \u00a7bfalse\u00a77. '\u00a73" + args[2] + "\u00a77' is neither of them"));
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73GooP Gamerule, \u00a77Some 'Gamerules' that affect this plugin.");
                logReturn.add("\u00a73Usage: \u00a7e/goop gamerule <rule> <value>");
                logReturn.add("\u00a73 - \u00a7e<rule> \u00a77Rule name. These are them:");
                logReturn.add("\u00a73" + Gunging_Ootilities_Plugin.sendGooPSuccessFeedback + "\u00a7e: \u00a7bsendSuccessFeedback \u00a77Messages when a command succeeds.");
                logReturn.add("\u00a73" + Gunging_Ootilities_Plugin.sendGooPFailFeedback + "\u00a7e: \u00a7bsendFailFeedback \u00a77Messages when a command fails.");
                logReturn.add("\u00a73" + Gunging_Ootilities_Plugin.blockImportantErrorFeedback + "\u00a7e: \u00a7bblockErrorFeedback \u00a77Toggles error messages.");
                logReturn.add("\u00a73" + Gunging_Ootilities_Plugin.saveGamerulesConfig + "\u00a7e: \u00a7bsaveGameruleChanges \u00a77Update the \u00a7econfig.yml\u00a77 whenever a gamerule changes?");
                logReturn.add("\u00a73" + Gunging_Ootilities_Plugin.griefBreaksBedrock + "\u00a7e: \u00a7bgriefBreaksBedrock \u00a77Should \u00a7e/goop grief\u00a77 break unbreakable blocks? (Bedrock, End Portal Frames...) \u00a78They wont drop as items as there is no pickaxe power that makes them do so in vanilla.");
                logReturn.add("\u00a73" + Gunging_Ootilities_Plugin.anvilRenameEnabled + "\u00a7e: \u00a7banvilRename \u00a77Should GooP take over anvil renaming (may be incompatible with anvil renaming plguins)");
                logReturn.add("\u00a73 - \u00a7e<value> \u00a77Value of the rule.");
                logReturn.add("\u00a73You would usually use these for debugging mostly.");

            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Gamerule", "Incorrect usage. For info: \u00a7e/goop gamerule"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop gamerule <rule> <value>");
                }
            }

        // No perms
        } else {
            // Tell him lmao
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to change gamerules!"));
        }
        return null;
    }
}
