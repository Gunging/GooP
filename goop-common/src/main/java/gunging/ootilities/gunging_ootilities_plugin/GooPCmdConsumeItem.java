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
 * The {@code /goop consumeitem} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdConsumeItem {

    private GooPCmdConsumeItem() { }

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
        //   0      1            2     3 4 5      6     args.Length
        // /goop consumeitem <player> {n b t} <amount> [I,E,C,P,S,Z,H,L] [Name Filter]
        //   -      0            1     2 3 4      5     args[n]

        // Check 5 Permission
        if (senderIsPlayer) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.consumeitem");
        }

        // Got permission, then?
        if (permission) {

            // How many args this man got?
            if (args.length >= 6) {

                // Gets that player boi
                ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], null);
                RefSimulator<String> logAddition = new RefSimulator<>("");
                boolean failure = false;

                // Does the player exist?
                if (targets.size() == 0) {
                    // Failure
                    failure = true;

                    // Notify the error
                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Consume Item", "Target must be an online player!"));
                }

                // Can it parse tha mount?
                if (!(OotilityCeption.IntTryParse(args[5]) || args[5].equalsIgnoreCase("All"))) {

                    // Failure
                    failure = true;

                    // The thing is not an integer nor an accepted keyword
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Consume Item", "Expected an integer number or the keyword '\u00a7eall\u00a77' instead of '\u00a73" + args[5] + "\u00a77.'"));
                }

                //Switch for sintax
                if (OotilityCeption.IsInvalidItemNBTtestString(args[2], args[3], args[4], logAddition)) {
                    // Ew
                    failure = true;

                    // Log it
                    if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Consume Item", logAddition.GetValue()));}
                }

                if (!failure) {

                    // Alr lets keep a count of the items found
                    Integer tRAmount = null; boolean iAll = args[5].equalsIgnoreCase("All");
                    if (!iAll) { tRAmount = OotilityCeption.ParseInt(args[5]); }

                    // Build [I,E,C,P,S,Z,H,L]
                    ArrayList<SearchLocation> sweep = new ArrayList<>();
                    if (args.length >= 7) {
                        String arg = args[6].toUpperCase();
                        if (arg.contains("I")) { sweep.add(SearchLocation.INVENTORY); }
                        if (arg.contains("E")) { sweep.add(SearchLocation.ENDERCHEST); }
                        if (arg.contains("C")) { sweep.add(SearchLocation.OBSERVED_CONTAINER); }
                        if (arg.contains("P")) { sweep.add(SearchLocation.PERSONAL_CONTAINER); }
                        if (arg.contains("S")) { sweep.add(SearchLocation.SHULKER_INVENTORY); }
                        if (arg.contains("Z")) { sweep.add(SearchLocation.SHULKER_ENDERCHEST); }
                        if (arg.contains("H")) { sweep.add(SearchLocation.SHULKER_OBSERVED_CONTAINER); }
                        if (arg.contains("L")) { sweep.add(SearchLocation.SHULKER_PERSONAL_CONTAINER); }

                        if (sweep.size() == 0) { sweep.add(SearchLocation.INVENTORY); }

                    } else { sweep.add(SearchLocation.INVENTORY); }

                    // Make Array
                    SearchLocation[] sweeptot = new SearchLocation[sweep.size()];
                    for (int s = 0; s < sweeptot.length; s++) { sweeptot[s] = sweep.get(s); }

                    // Name
                    String fltr = null;
                    if (args.length >= 8) {

                        // Stringbuild the remaining args
                        StringBuilder tot = new StringBuilder();
                        for (int ar = 7; ar < args.length; ar++) { tot.append(args[ar]); }

                        // Build
                        fltr = tot.toString();
                    }

                    // For every target
                    for (Player target : targets) {

                        // New Ref Simulator
                        RefSimulator<Integer> totalFound = new RefSimulator<>(0);
                        Integer tAmount = tRAmount;
                        int oAmount = 0;

                        NBTFilter filter = new NBTFilter(args[2], args[3], args[4], null);

                        // Get Inven Items until enough have been found
                        ArrayList<ItemStackLocation> items = OotilityCeption.getMatchingItems(target, filter, totalFound, fltr, logAddition, sweeptot);

                        // If tAmount null, it serendipitously equals the found value
                        if (iAll) { tAmount = totalFound.getValue(); }

                        // Did it find enough?
                        if (totalFound.getValue() >= tAmount) {

                            // Examine all of them
                            for (ItemStackLocation item : items) {

                                boolean fatalFailureBruh = OotilityCeption.IsAirNullAllowed(item.getItem());

                                if (!fatalFailureBruh) {

                                    int currentAmount = item.getAmount();

                                    // Totally clearing?
                                    if (iAll) {
                                        oAmount += currentAmount;

                                        item.setAmount(0);

                                    // Otherwise (Not totally clearing)
                                    } else {

                                        // If the Amount can absorve the whole cleareance
                                        if (currentAmount >= tAmount) {

                                            // Absorb
                                            item.setAmount(currentAmount - tAmount);

                                            // Finish
                                            oAmount = tRAmount;
                                            break;

                                        // If theres more amount still, set to zero and decrease tAmout
                                        } else {

                                            // Decrease
                                            tAmount -= currentAmount;

                                            // Consume
                                            item.setAmount(0);
                                        }
                                    }

                                // There seems to be a fatal failure
                                } else {

                                    // HUH
                                    logReturn.add(OotilityCeption.LogFormat("Consume Item", "\u00a7cThis shouldnt be happening: Item Not Found - Contact Dev"));
                                }
                            }

                            // Success
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Consume Item", "Successfully removed \u00a73" + oAmount + "\u00a77 items from " + target.getName() + "'s inventory."));

                            // Run Chain
                            commandChain.chain(chained, target, sender);

                        // If there weren't enough
                        } else {

                            // I guess not
                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Consume Item", "Player '\u00a73" + target.getName() + "\u00a77' didn't have enough of that in their inventory."));

                            if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage.replace("%amount%", String.valueOf(tAmount)), target.getPlayer(), target.getPlayer(), null, null))); }
                        }
                    }
                }

                // If they were calling the help form of the command
            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Consume Item, \u00a77Removes an amount of a specific item from the player.");
                logReturn.add("\u00a73Usage: \u00a7e/goop consumeitem <player> {nbt} <amount> [I,E,C,P,S,Z,H,L] [Shulker Box Name Filter]");
                logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player whose inventory will be affected.");
                logReturn.add("\u00a73 - \u00a7e{nbt} \u00a77These are the formats that match your plugins:");
                logReturn.add("\u00a73 --> \u00a7ee <enchantment name> <level> \u00a77Tests for an enchantment.");
                if (Gunging_Ootilities_Plugin.foundMMOItems) { logReturn.add("\u00a73 --> \u00a7em <mmoitem type> <mmoitem id> \u00a77Tests for it being a precise mmoitem."); }
                logReturn.add("\u00a73 - \u00a7e<amount> \u00a77Number of items that will be removed.");
                logReturn.add("\u00a73 --> \u00a7eall \u00a77will remove all items that match from the player's inven.");
                logReturn.add("\u00a73 - \u00a7e[I,E,C,P,S,Z,H,L] \u00a77Inventories from which to consume items");
                logReturn.add("\u00a73 - \u00a7e[Shulker Box Name Filter] \u00a77Name of shulker boxes to open and check");
                logReturn.add("\u00a7cWill fail if the player doesnt have enough items to remove.");

                // Incorrect number of arguments. These people
            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Consume Item", "Incorrect usage. For info: \u00a7e/goop consumeitem"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop consumeitem <player> {nbt} <amount>");
                }
            }

        // No permission
        } else {

            // Tell him lmao
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to consume items!"));
        }
        return null;
    }
}
