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
 * The {@code /goop stasis} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdStasis {

    private GooPCmdStasis() { }

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
        //   0      1       2         3     args.Length
        // /goop stasis <entity> <duration>
        //   -      0       1         2     args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.stasis");
        }

        // GOt permission?
        if (permission) {

            // Chek for args length
            if (args.length == 3) {

                // CHeck for failure
                boolean failure = false;

                // Duration is an integer
                if (!OotilityCeption.IntTryParse(args[2])) {
                    // Failure
                    failure = true;

                    // Notify the error
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Stasis", "Duration (in ticks) must be an integer number."));
                }

                if (!failure) {

                    // Gets that player boi
                    ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], null);
                    Entity targetNonPlayer = OotilityCeption.getEntityByUniqueId(args[1]);
                    if (targetNonPlayer instanceof Player) { targetNonPlayer = null; }

                    // Treat as player
                    for (Player target : targets) {
                        // Slowness
                        target.addPotionEffect(new PotionEffect(GooP_MinecraftVersions.GetVersionPotionEffect(GooPVersionPotionEffects.SLOW), OotilityCeption.ParseInt(args[2]), 254, true));
                        // Jump Prevention
                        target.addPotionEffect(new PotionEffect(GooP_MinecraftVersions.GetVersionPotionEffect(GooPVersionPotionEffects.JUMP), OotilityCeption.ParseInt(args[2]), 129, true));
                        // Confusion
                        target.addPotionEffect(new PotionEffect(GooP_MinecraftVersions.GetVersionPotionEffect(GooPVersionPotionEffects.CONFUSION), OotilityCeption.ParseInt(args[2]), 4, true));

                        // Mention
                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Stasis", "Successfully suspended \u00a73" + target.getName() + "\u00a77's flow of time."));

                        // Run Chain
                        commandChain.chain(chained, target, sender);
                    }

                    // Entity found
                    if (targetNonPlayer != null) {

                        // Well NoAI set to true bruh
                        LivingEntity slTarget = (LivingEntity) targetNonPlayer;
                        slTarget.setAI(false);

                        // Soon enough, cancel
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Gunging_Ootilities_Plugin.getPlugin(Gunging_Ootilities_Plugin.class) , () -> slTarget.setAI(true), (OotilityCeption.ParseInt(args[2])));

                        // Mention
                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Stasis", "Successfully suspended \u00a73" + slTarget.getName() + "\u00a77's flow of time."));

                        // Run Chain
                        commandChain.chain(chained, targetNonPlayer.getUniqueId(), sender);
                    }

                    // Entity not found
                    if (targets.size() == 0 && targetNonPlayer == null) {

                        // Not found
                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Stasis", "Entity not found."));
                    }
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Stasis, \u00a77Kinda like freezes that entity in time.");
                logReturn.add("\u00a73Usage: \u00a7e/goop stasis <entity> <duration>");
                logReturn.add("\u00a73 - \u00a7e<entity> \u00a77Entity that will be affected.");
                logReturn.add("\u00a73 --> \u00a73Players \u00a77are slowed, blocked from jumping, and confused.");
                logReturn.add("\u00a73 --> \u00a73Mobs \u00a77are cleared from their artificial intelligence.");
                logReturn.add("\u00a73 - \u00a7e<duration> \u00a77Duration of stasis in ticks.");
                logReturn.add("\u00a73Equivalent of vanilla \u00a7e/data set entity @s NoAi value set true\u00a73 but logless.");
            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Stasis", "Incorrect usage. For info: \u00a7e/goop stasis"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop stasis <entity> <duration>");
                }
            }

        // No perms
        } else {

            // Clarify it lack of perms
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to suspend creatures!"));
        }
        return null;
    }
}
