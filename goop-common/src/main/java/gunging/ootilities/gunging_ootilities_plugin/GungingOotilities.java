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

public class GungingOotilities implements CommandExecutor {
    OotilityCeption oots = new OotilityCeption();

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args) {
        boolean senderIsPlayer = (sender instanceof Player);

        // Strip location data
        Location senderLocation = null;
        String failMessage = null;
        UUID successibilityFlare = null;
        if (args.length >= 1) {
            RefSimulator<String> fmFind = new RefSimulator<>(null);
            args[0] = OotilityCeption.ProcessFailMessageOfCommand(args[0], fmFind);
            String locData = args[0];
            failMessage = fmFind.getValue();

            if (locData.startsWith("$") && locData.length() > 3) {

                // Does it end in $
                int flareIDX = locData.indexOf("$", 1);
                if (flareIDX > 4) {

                    // Then krop
                    String sucFlare = locData.substring(1, flareIDX);

                    // Valid location?
                    successibilityFlare = OotilityCeption.UUIDFromString(sucFlare);

                    // Strip
                    args[0] = locData.substring(flareIDX + 1);
                    locData = args[0];
                }
            }

            if (locData.startsWith("<") && locData.length() > 3) {

                // Does it end in >
                int senderRelativeLocationIDX = locData.indexOf(">");
                if (senderRelativeLocationIDX > 4) {

                    // Then krop
                    String rLoverride = locData.substring(1, senderRelativeLocationIDX);

                    // Valid location?
                    senderLocation = OotilityCeption.ValidLocation(rLoverride, null);

                    // Strip
                    args[0] = locData.substring(senderRelativeLocationIDX + 1);
                }
            }
        }

        // Defaults to sender
        if ((senderLocation == null)) {

            if (sender instanceof Entity) {

                // Thay loc
                senderLocation = ((Entity) sender).getLocation();

            } else if (sender instanceof BlockCommandSender) {

                // Thay loc
                senderLocation = ((BlockCommandSender) sender).getBlock().getLocation();
            }
        }

        // What will be said to the caster (caster = sender of command)
        List<String> logReturn = new ArrayList<>();

        // Will it be a supported plugin command?
        boolean supp = false;

        // If there are any arguments
        GooP_Commands cmd = null;
        if (args.length > 0) {

            // Is it a supported plugin command?
            try {
                // Yes, it seems to be
                cmd = GooP_Commands.valueOf(args[0].toLowerCase());

                // Well it is supported then
                supp = true;

            // Not recognized
            } catch (IllegalArgumentException ex) {

                // Yes it is! Lets go
                if (args[0].toLowerCase().equals("sudop")) {
                    cmd = GooP_Commands.sudo;
                    supp = true;
                } else

                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Command '\u00a73" + args[0] + "\u00a77' not recognized. For available commands: \u00a7e/goop \u00a77OR \u00a7e/gungingootilities")); }

                /* Daybroken Trigger
                if (args.length == 8) {

                    //   0     1    2    3    4   5   6     7    8   args.Length
                    // /goop hack for yunque fall in your head mbn9
                    //   -     0    1    2    3   4   5     6    7   args[n]
                    //logReturn.add(((args[2] + args[5]).length()) + " | " + args[4].equals("in") + " | " + args[1].startsWith("fo") + " | " + (args[7] + args[5] + args[3]).length() + ", " + (args[0] + args[2] + args[4]).length() + ", " + args[4].length() + " : " + (args[6] + args[6] + args[4]).length() + " - " + ((Player)sender).getClientViewDistance());

                    if ((args[2] + args[5]).length() == 10) {
                        if (args[4].equals("in")) {
                            if (args[1].startsWith("fo")) {
                                if (Math.pow((args[7] + args[5] + args[3]).length() - (args[0] + args[2] + args[4]).length() + (args[4].length() - 1), (args[6] + args[6] + args[4]).length()) == 1) {
                                    if (senderIsPlayer) {
                                        if (((Player)sender).getClientViewDistance() == 2) {
                                            // Imbending Boom Abbroaches
                                            Gunging_Ootilities_Plugin.daybroken = true;
                                            logReturn.add("\u00a7cImbending Boom Abbroaches");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } */
            }

        // Otherwise, its the analogous of /help
        } else {
            logReturn.add("\u00a7e______________________________________________");
            logReturn.add("\u00a73Help, \u00a77Available Commands:");
            for (GooP_Commands o : GooP_Commands.values()) { logReturn.add("\u00a73 - \u00a7e" + o.toString()); }
        }

        // Slots of success
        StringBuilder successSlots = new StringBuilder();

        // Help Parameters
        int argsMinLength, argsMaxLength;
        String subcommand, subcategory, usage;

        if (supp) {

            // Check for permission
            boolean permission = true;

            // Split the heck off the chain of commands
            ArrayList<String> trueArgs = new ArrayList<>(); boolean chained = false; StringBuilder chanedArgs = new StringBuilder();
            for (String obs : args) {

                // Does it equal the OS sequence?
                if (!chained && OotilityCeption.IsChainedKey(obs)) {

                    // It is now chained
                    chained = true;
                    //CHN//OotilityCeption. Log("\u00a7eChained Command Arg");

                } else {

                    // Chaining or true?
                    if (!chained) {

                        // Add obs to the truth
                        trueArgs.add(obs);
                        //CHN//OotilityCeption. Log("Examined arg: \u00a7b" + obs);

                    // Its chained, shall now build
                    } else {

                        // Append after a space
                        chanedArgs.append(" ").append(obs);
                        //CHN//OotilityCeption. Log("Chained arg: \u00a7f" + obs);
                    }
                }
            }

            // Chain command execc
            SuccessibleChain commandChain = new SuccessibleChain("", successibilityFlare);
            String chainedNoLocation = null;
            RefSimulator<List<String>> logReturnUrn = new RefSimulator<>(null);
            if (chained) {
                String chainedCommand = null;

                // Replace args
                args = new String[trueArgs.size()];
                for (int s = 0; s < args.length; s++) {

                    // Parsed
                    args[s] = trueArgs.get(s);

                    //CHN//OotilityCeption. Log("\u00a78GOOP\u00a7b OS=\u00a77 Baking true arg \u00a73#" + s + "\u00a77: \u00a7b" + args[s]);
                }

                // Yes. Chain
                if (args.length > 1) {
                    // Build chain command (Cut the first space)
                    chainedNoLocation = chanedArgs.toString().substring(1);
                    chainedCommand = OotilityCeption.ProcessGooPRelativityOfCommand(chainedNoLocation, senderLocation);
                    //CHN//OotilityCeption. Log("\u00a7eDetermined chained command: \u00a7f" + chainedCommand);

                } else { chained = false; }

                // Save chain
                commandChain = new SuccessibleChain(chainedCommand, successibilityFlare);
            }

            String senderUUIDIG = senderIsPlayer ? ((Player) sender).getUniqueId().toString() : null;
            for (int s = 0; s < args.length; s++) {
                //CHN//OotilityCeption. Log("\u00a78GOOP\u00a7b OS=\u00a77 Cooking Arg \u00a73#" + s + "\u00a77: \u00a7b" + args[s]);
                String rawTrueArg = args[s];

                // Parse funny <caster.name> placeholder sweet
                if (senderIsPlayer) {
                    boolean playerParseAllowed = true;

                    if (s > 3) {
                        if (
                                (args[0].toLowerCase().equals("customstructures") &&
                                        args[1].toLowerCase().equals("edit")) ||

                                        (args[0].toLowerCase().equals("containers") &&
                                                args[1].toLowerCase().equals("config"))) {

                            playerParseAllowed = false;
                        }
                    }

                    if (playerParseAllowed) {
                        rawTrueArg = rawTrueArg.replace("<caster.name>", sender.getName());
                        rawTrueArg = rawTrueArg.replace("<target.name>", sender.getName());
                        rawTrueArg = rawTrueArg.replace("<trigger.name>", sender.getName());
                        rawTrueArg = rawTrueArg.replace("%player%", sender.getName());
                        rawTrueArg = rawTrueArg.replace("%player_name%", sender.getName());

                        rawTrueArg = rawTrueArg.replace("<caster.uuid>", senderUUIDIG);
                        rawTrueArg = rawTrueArg.replace("<target.uuid>", senderUUIDIG);
                        rawTrueArg = rawTrueArg.replace("<trigger.uuid>", senderUUIDIG);
                    }
                }
                // Replace <$nd>
                rawTrueArg = rawTrueArg.replace("<$nd>", "&");

                // Replace
                args[s] = rawTrueArg;
                //CHN//OotilityCeption. Log("\u00a78GOOP\u00a7b OS=\u00a77 Result \u00a73#" + s + "\u00a77: \u00a7b" + args[s]);
            }


            // Which command thoi?
            switch (cmd) {
                //region stasis
                case stasis:
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
                    break;
                //endregion
                //region help
                case help:
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.help");
                    }

                    if (permission) {

                        logReturn.add("\u00a7e______________________________________________");
                        logReturn.add("\u00a73Help, \u00a77Available Commands:");
                        for (GooP_Commands o : GooP_Commands.values()) { logReturn.add("\u00a73 - \u00a7e" + o.toString()); }
                    } else {


                        // Tell him lmao
                        logReturn.add(OotilityCeption.LogFormat("\u00a7cYou are helpless..."));
                    }
                    break;
                //endregion
                //region consumeitem
                case consumeitem:
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
                    break;
                //endregion
                //region testinventory
                case testinventory:
                    //   0      1              2       3    4 5 6     [7]       7 [8]     8 [9]  9 [10]     args.Length
                    // /goop testinventory <player> <slot> {n b t} [amount] [objective] [score] [comp]  [Shulker Box Name Filter]
                    //   -      0              1       2    3 4 5     [6]       6 [7]     7 [8]  8 [9]      args[n]

                    // Check 5 Permission
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.testinventory");
                    }

                    // Got perms?
                    if (permission) {

                        // How many args this man got?
                        if (args.length >= 6) {
                            Entity asDroppedItem = OotilityCeption.getEntityByUniqueId(args[1]);
                            if (!(asDroppedItem instanceof Item)) { asDroppedItem = null; }

                            // Gets that player boi
                            ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[1], null);
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Some scoreboards to test
                            Objective targetObjective = null;
                            PlusMinusPercent amountPMP = null;
                            PlusMinusPercent score = null;

                            String objectiveStr = null;
                            String amountPmPText = null;
                            int nameFilterBegin = 32767;
                            int scorelessNameFilterBegin = 32767;

                            QuickNumberRange itemAmountTest = null;
                            boolean toCompletion = false;

                            /*
                             * If any of the optional terms is used
                             */
                            if (args.length >= 7) {

                                // Attempt to parse sixth argument as an amount range to accept
                                itemAmountTest = QuickNumberRange.FromString(args[6]);
                                int iAD = 0;

                                // Item Amount Test was specified
                                if (itemAmountTest != null) { iAD = 1; }

                                // If there is only one optional argument
                                if (args.length == (7 + iAD)) {

                                    // It must be comp
                                    toCompletion = args[6 + iAD].equals("comp");

                                    // Name filter??? (of one word)
                                    scorelessNameFilterBegin = 6 + iAD + (toCompletion ? 1 : 0);

                                // There is two optional arguments
                                } else if (args.length == (8 + iAD)) {

                                    // They must be an objective and a scoreo
                                    objectiveStr = args[6 + iAD];
                                    amountPmPText = args[7 + iAD];

                                    // Name filter??? (two words)
                                    scorelessNameFilterBegin = 6 + iAD;

                                // There is three optional arguments
                                } else if (args.length >= (9 + iAD)) {

                                    // They must be an objective and a score
                                    objectiveStr = args[6 + iAD];
                                    amountPmPText = args[7 + iAD];
                                    toCompletion = args[8 + iAD].equals("comp");

                                    // Was the first one, comp?
                                    if (toCompletion) {

                                        // Begin index is 9 + iAD
                                        nameFilterBegin = 9 + iAD;

                                        // Name filter must begin after comp keyword
                                        scorelessNameFilterBegin = 9 + iAD;

                                    // Not comp, must be the start of the name filter
                                    } else {

                                        // Name filter is 8 + iAD
                                        nameFilterBegin = 8 + iAD;

                                        // Except the last three word could have been it
                                        scorelessNameFilterBegin = 6 + iAD;
                                    }
                                }
                            }

                            // Some bool to know if failure
                            boolean failure = false;

                            // Does the player exist?
                            if (targets.size() == 0 && asDroppedItem == null) {

                                // Failure
                                failure = true;

                                // Add a trash null entry
                                targets.add(null);

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", "Target must be an online player!"));
                            }

                            //Switch for sintax
                            boolean allowNull = false;
                            if (OotilityCeption.IsInvalidItemNBTtestString(args[3], args[4], args[5], logAddition)) {
                                // Ew
                                failure = true;

                                // Log it
                                if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Test Inventory", logAddition.GetValue()));}
                            } else { allowNull = (args[3].equals("v") && (args[4].toLowerCase()).equals("air"));  }

                            // Can it parse the score?
                            boolean scoreExpectedScoreboard = false;
                            boolean uiAmount = false, asSlot = false;
                            if (amountPmPText != null) {

                                // Will it set the objective to the count of items in the inventory?
                                asSlot = amountPmPText.toLowerCase().contains("slot");
                                uiAmount = amountPmPText.toLowerCase().contains("amount") || asSlot;    // Cling onto UIAmount system, the slot.
                                if (uiAmount || asSlot) {

                                    // Build Amount PMP base
                                    amountPMP = PlusMinusPercent.GetPMP(amountPmPText.toLowerCase().replace("amount", "1"), null);

                                    // If it didnt parse, default to just set
                                    if (amountPMP == null) { amountPMP = new PlusMinusPercent(1D, false, false); }

                                    // This guy tryna have an objective, man
                                    scoreExpectedScoreboard = true;
                                }

                                // Get score increment as PMP
                                score = PlusMinusPercent.GetPMP(amountPmPText, logAddition);
                                if (score != null) { scoreExpectedScoreboard = true; }

                                // Does the scoreboard objective exist?
                                targetObjective = OotilityCeption.GetObjective(objectiveStr);
                            }

                            // Could not find the objective?
                            if (targetObjective == null && amountPmPText != null) {

                                /*
                                 * So the player may specify a shulker box name filter without
                                 * an objective/score pair, in which case the score text will not
                                 * be a PMP nor 'amount' and will start immediately after comp.
                                 */
                                failure = failure || scoreExpectedScoreboard;

                                // Mention
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback && failure) logReturn.add(OotilityCeption.LogFormat("Test Inventory", "Scoreboard Objective '\u00a73" + objectiveStr + "\u00a77' does not exist."));
                            }

                            // Name
                            String fltr = null;
                            if (args.length > scorelessNameFilterBegin) {

                                // Identify where the name filter truly begins
                                int trueNameFilter = scorelessNameFilterBegin;

                                // If expected scoreboard, use the alternative
                                if (scoreExpectedScoreboard) { trueNameFilter = nameFilterBegin; }

                                //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Args Length:\u00a79 " + args.length);
                                //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Scoreless F:\u00a73 " + scorelessNameFilterBegin);
                                //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 True Name F:\u00a7b " + trueNameFilter);

                                // If still can build the name
                                if (args.length > trueNameFilter) {

                                    // Stringbuild the remaining args
                                    StringBuilder tot = new StringBuilder();
                                    for (int ar = trueNameFilter; ar < args.length; ar++) {
                                        if (!(tot.length() == 0)) { tot.append(" "); }
                                        tot.append(args[ar]); }

                                    // Build
                                    fltr = tot.toString();
                                    //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Searching in boxes of name '" + fltr + "'");
                                }
                            }

                            // Everthing went all right?
                            if (!failure) {

                                // Compare dropped item
                                if (asDroppedItem != null) {
                                    int kount = 0;
                                    Integer slotS = null;

                                    // Get Item
                                    ItemStack targetItem = OotilityCeption.FromDroppedItem(asDroppedItem);
                                    //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Found Item " + OotilityCeption.GetItemName(targetItem));

                                    // If it is not null
                                    if (!OotilityCeption.IsAirNullAllowed(targetItem)) {

                                        // Found something, does it match thoi?
                                        if (OotilityCeption.MatchesItemNBTtestString(targetItem, args[3], args[4], args[5], logAddition)) {
                                            //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                            // COunt
                                            kount += targetItem.getAmount();
                                            slotS = 0;
                                            if (chained) { OotilityCeption.Slot4Success(successSlots, new ISSInventory(0, null), OotilityCeption.comma); }
                                        }

                                    // Item Stack is null or air, are we searching for that?
                                    } else if (allowNull) {
                                        //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                        // Count
                                        kount++;
                                        slotS = 0;
                                        if (chained) { OotilityCeption.Slot4Success(successSlots, new ISSInventory(0, null), OotilityCeption.comma); }
                                    }


                                    boolean success;
                                    String logrt;

                                    // If found
                                    if (kount > 0) {

                                        logrt = "Item successfully detected, counted a total of \u00a7e" + kount + "\u00a77. ";

                                        // Does it proc?
                                        if (itemAmountTest != null) {

                                            if (itemAmountTest.InRange(kount)) {

                                                logrt += "Furthermore, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. ";
                                                success = true;

                                            } else {

                                                logrt += "However, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7cdoes not\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to fail thus. ";
                                                success = false;
                                            }

                                            // No amount to say anything, true by default
                                        } else { success = true; }

                                        // Still succeeded?
                                        if (success) {

                                            // Some objective operation will be undergone
                                            if (targetObjective != null) {

                                                // Provided an amount
                                                if (asSlot) {

                                                    if (slotS == null) { slotS = -32767; }

                                                    // Iamount pmp
                                                    double negativity = amountPMP.getValue();
                                                    amountPMP.OverrideValue(slotS * negativity);

                                                    // Done
                                                    OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), amountPMP);
                                                    logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a73" + OotilityCeption.GetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString()) + "\u00a77. ";

                                                    // Provided a score
                                                } else if (uiAmount) {

                                                    // Iamount pmp
                                                    double negativity = amountPMP.getValue();
                                                    amountPMP.OverrideValue(kount * negativity);

                                                    // Done
                                                    OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), amountPMP);
                                                    logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a73" + OotilityCeption.GetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString()) + "\u00a77. ";

                                                    // Provided a score
                                                } else if (score != null) {

                                                    //Behold
                                                    OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), score);
                                                    logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a73" + OotilityCeption.GetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString()) + "\u00a77. ";
                                                }
                                            }

                                            // Run Chain
                                            if (chained) {
                                                commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@t", successSlots.toString()));
                                                commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(kount)));
                                            }
                                        }

                                        // Nothing was found
                                    } else {

                                        logrt = "Nothing in \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77's slots \u00a7e" + args[2] + "\u00a77 matched \u00a7e" + args[3] + " " + args[4] + " " + args[5] + "\u00a77. ";
                                        success = uiAmount && !asSlot;

                                        // Does it proc?
                                        if (itemAmountTest != null) {

                                            if (itemAmountTest.InRange(0)) {

                                                logrt += "However, the amount of stuff found (\u00a7b0\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to succeed thus.";
                                                commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(0)));
                                                success = true;

                                            } else {

                                                logrt += "Furthermore, the amount of stuff found (\u00a7b0\u00a77) \u00a7cdoes not\u00a77 even fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. ";
                                                success = false;
                                            }
                                        }

                                        // Difference between amount and no amount
                                        if (uiAmount && success) {

                                            // Is there a target objective?
                                            if (success && targetObjective != null) {

                                                // Iamount pmp
                                                amountPMP.OverrideValue(0.0D);

                                                // Well nothing means 0 this time
                                                OotilityCeption.SetEntryScore(targetObjective, asDroppedItem.getUniqueId().toString(), amountPMP);

                                                // Log
                                                logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + OotilityCeption.GetItemName(targetItem) + "\u00a77 is now \u00a730\u00a77. ";
                                            }
                                        }
                                    }

                                    // Well
                                    if (success) {

                                        // Run Chain
                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                                        commandChain.chain(chained, asDroppedItem.getUniqueId(), sender);

                                    } else {

                                        // N00b
                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                                    }
                                }

                                // For every target
                                for (Player target : targets) {
                                    failure = false;

                                    //Lets get that inven slot
                                    RefSimulator<String> slotFailure = new RefSimulator<>("");
                                    ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[2], target, slotFailure);

                                    // So, does the slot make no sense?
                                    if (slott.size() == 0) {
                                        // Failure
                                        failure = true;

                                        // If no slots were wrong
                                        if (slotFailure.getValue() == null && Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Test Inventory", "The slots specified didn't make sense in this context. As if trying to access shulker boxes where there are none, or using container placeholder names in non-goop inventories.")); }
                                    }

                                    // Log
                                    if (slotFailure.getValue() != null) { logReturn.add(OotilityCeption.LogFormat("Test Inventory", slotFailure.getValue())); }

                                    // Nice sintax!
                                    if (!failure) {

                                        // Count matches
                                        int kount = 0;
                                        Integer slotS = null;

                                        // For every slot
                                        for (ItemStackSlot tSlot : slott) {

                                            // Time to get that item stack
                                            ItemStackLocation tISource = OotilityCeption.getItemFromPlayer(target, tSlot, fltr);
                                            //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Looking at \u00a7e" + tSlot.toString() + "\u00a77: \u00a78(\u00a76" + tSlot.getLocation().toString() + "\u00a78;\u00a79" + tISource.getClass().getSimpleName() + "\u00a78)");

                                            // If slot made sense
                                            if (tISource != null) {

                                                // Get Item
                                                ItemStack targetItem = tISource.getItem();
                                                //TSI//OotilityCeption.Log("\u00a78GOO\u00a7b TSI\u00a77 Found Item " + OotilityCeption.GetItemName(targetItem));

                                                // If it is not null
                                                if (!OotilityCeption.IsAirNullAllowed(targetItem)) {

                                                    // Found something, does it match thoi?
                                                    if (OotilityCeption.MatchesItemNBTtestString(targetItem, args[3], args[4], args[5], logAddition)) {
                                                        //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                                        // COunt
                                                        kount += targetItem.getAmount();
                                                        slotS = tSlot.getSlot();
                                                        if (chained) { OotilityCeption.Slot4Success(successSlots, tSlot, OotilityCeption.comma); }

                                                        // Is this the whole damn thing? No? Then break it
                                                        if ((asSlot || !uiAmount) && !toCompletion) {
                                                            //TSI//OotilityCeption.Log("\u00a7b\u00a7oCompletion Reached, breaking. ");
                                                            break; }

                                                    }

                                                // Item Stack is null or air, are we searching for that?
                                                } else if (allowNull) {
                                                    //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                                    // Count
                                                    kount++;
                                                    if (chained) { OotilityCeption.Slot4Success(successSlots, tSlot, OotilityCeption.comma); }

                                                    // Is this the whole damn thing? No? Then break it
                                                    slotS = tSlot.getSlot();
                                                    if ((asSlot || !uiAmount) && !toCompletion) {
                                                        //TSI//OotilityCeption.Log("\u00a7b\u00a7oCompletion Reached, breaking. ");
                                                        break; }

                                                }

                                            // Null Item Stack Location? I guess thats air?
                                            } else if (allowNull) {

                                                //TSI//OotilityCeption.Log("\u00a7a\u00a7oMatched");

                                                // Count
                                                kount++;
                                                slotS = tSlot.getSlot();
                                                if (chained) { OotilityCeption.Slot4Success(successSlots, tSlot, OotilityCeption.comma); }

                                                // Is this the whole damn thing? No? Then break it
                                                if ((asSlot || !uiAmount) && !toCompletion) {
                                                    //TSI//OotilityCeption.Log("\u00a7b\u00a7oCompletion Reached, breaking. ");
                                                    break; }
                                            }
                                        }

                                        boolean success;
                                        String logrt;

                                        // If found
                                        if (kount > 0) {

                                            logrt = "Item \u00a7e" + args[3] + " " + args[4] + " " + args[5] + "\u00a77 successfully detected in \u00a73" + target.getName()  + "\u00a77's slots \u00a7b" + args[2] +  "\u00a77, counted a total of \u00a7e" + kount + "\u00a77. ";

                                            // Does it proc?
                                            if (itemAmountTest != null) {

                                                if (itemAmountTest.InRange(kount)) {

                                                    logrt += "Furthermore, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77.";
                                                    success = true;

                                                } else {

                                                    logrt += "However, the amount of stuff found (\u00a7b" + kount + "\u00a77) \u00a7cdoes not\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to fail thus. ";
                                                    success = false;
                                                }

                                            // No amount to say anything, true by default
                                            } else { success = true; }

                                            // Still succeeded?
                                            if (success) {

                                                // Some objective operation will be undergone
                                                if (targetObjective != null) {

                                                    // Provided an amount
                                                    if (asSlot) {

                                                        if (slotS == null) { slotS = -32767; }

                                                        // Iamount pmp
                                                        double negativity = amountPMP.getValue();
                                                        amountPMP.OverrideValue(slotS * negativity);

                                                        // Done
                                                        OotilityCeption.SetPlayerScore(targetObjective, target, amountPMP);
                                                        logrt += "Their score '\u00a73" + targetObjective.getName() + "\u00a77 is now \u00a73" + OotilityCeption.GetPlayerScore(targetObjective, target) + "\u00a77. ";

                                                    // Provided a score
                                                    } else if (uiAmount) {

                                                        // Iamount pmp
                                                        double negativity = amountPMP.getValue();
                                                        amountPMP.OverrideValue(kount * negativity);

                                                        // Done
                                                        OotilityCeption.SetPlayerScore(targetObjective, target, amountPMP);
                                                        logrt += "Their score '\u00a73" + targetObjective.getName() + "\u00a77 is now \u00a73" + OotilityCeption.GetPlayerScore(targetObjective, target) + "\u00a77. ";

                                                    // Provided a score
                                                    } else if (score != null) {

                                                        //Behold
                                                        OotilityCeption.SetPlayerScore(targetObjective, target, score);
                                                        logrt += "Their score '\u00a73" + targetObjective.getName() + "\u00a77 is now \u00a73" + OotilityCeption.GetPlayerScore(targetObjective, target) + "\u00a77. ";
                                                    }
                                                }

                                                // Run Chain
                                                if (chained) {
                                                    commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@t", successSlots.toString()));
                                                    commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(kount)));
                                                }
                                            }

                                        // Nothing was found
                                        } else {

                                            logrt = "Nothing in \u00a73" + target.getName() + "\u00a77's slots \u00a7b" + args[2] + "\u00a77 matched \u00a7e" + args[3] + " " + args[4] + " " + args[5] + "\u00a77. ";
                                            success = uiAmount && !asSlot;

                                            // Does it proc?
                                            if (itemAmountTest != null) {

                                                if (itemAmountTest.InRange(0)) {

                                                    logrt += "However, the amount of stuff found (\u00a7b0\u00a77) \u00a7adoes\u00a77 fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. The command was forced to succeed thus.";
                                                    commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", "0"));
                                                    success = true;

                                                } else {

                                                    logrt += "Furthermore, the amount of stuff found (\u00a7b0\u00a77) \u00a7cdoes not\u00a77 even fall in the range \u00a73" + itemAmountTest.toString() + "\u00a77. ";
                                                    success = false;
                                                }
                                            }

                                            // Difference between amount and no amount
                                            if (uiAmount && success) {

                                                // Is there a target objective?
                                                if (success && targetObjective != null) {

                                                    // Iamount pmp
                                                    amountPMP.OverrideValue(0.0D);

                                                    // Well nothing means 0 this time
                                                    OotilityCeption.SetPlayerScore(targetObjective, target, amountPMP);

                                                    // Log
                                                    logrt += "Score '\u00a73" + targetObjective.getName() + "\u00a77' of \u00a73" + target.getName() + "\u00a77 is now \u00a730\u00a77. ";
                                                }
                                            }
                                        }


                                        // Well
                                        if (success) {

                                            // Run Chain
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                                            commandChain.chain(chained, target, sender);

                                        } else {

                                            // N00b
                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Test Inventory", logrt));
                                            if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, target.getPlayer(), target.getPlayer(), null, null))); }
                                        }
                                    }
                                }
                            }

                        // If they were calling the help form of the command
                        } else if (args.length == 1) {
                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Test Inventory, \u00a77Makes sure the player has an item in his inven.");
                            logReturn.add("\u00a73Usage: \u00a7e/goop testinventory <player> <slot> {nbt} [amount] [objective] [score] [comp] [shulker...]");
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player target of the command.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slots in players inventory.");
                            logReturn.add("\u00a73 - \u00a7e{nbt} \u00a77These are the formats that match your plugins:");
                            logReturn.add("\u00a73 --> \u00a7ee <enchantment name> <level> \u00a77Tests for an enchantment.");
                            logReturn.add("\u00a73 --> \u00a7ev <material> * \u00a77Tests for a vanilla item.");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) { logReturn.add("\u00a73 --> \u00a7em <mmoitem type> <mmoitem id> \u00a77Tests for it being a precise mmoitem."); }

                            logReturn.add("\u00a73 - \u00a7e[amount] \u00a77Range of amount by which this command succeeds.");
                            logReturn.add("\u00a73 - \u00a7e[scoreboard] \u00a77Scoreboard objective name that will be modified IF the item matches perfectly.");
                            logReturn.add("\u00a73 - \u00a7e[value] \u00a77Score value that will be given to the player.");
                            logReturn.add("\u00a73 --> \u00a7eamount \u00a77keyword will set the score to the amount of that item the player has.");
                            logReturn.add("\u00a73 - \u00a7e[comp] \u00a77To count the items in all the slots.");
                            logReturn.add("\u00a73 - \u00a7e[shulker....] \u00a77Name of shulker boxes to inspect (All if missing).");
                            logReturn.add("\u00a73 --> \u00a77Will only affect Shulker Box Slots.");

                        // Incorrect number of argumenst. These beople
                        } else {
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat("Test Inventory", "Incorrect usage. For info: \u00a7e/goop testinventory"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop testinventory <player> <slot> {nbt} <scoreboard> <value>");
                            }
                        }

                    // No permission
                    } else {

                        // Tell him lmao
                        logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to check inventories!"));
                    }
                    break;
                //endregion
                //region gamerule
                case gamerule:
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
                    break;
                //endregion
                //region mmoitems
                case mmoitems:
                    //   0      1        2      args.Length
                    // /goop mmoitems {action}
                    //   -      0        1      args[n]

                    // FFS Check for MMOItems
                    if (Gunging_Ootilities_Plugin.foundMMOItems) {
                        GooPMMOItems.onCommand_GooPMMOItems(sender, command, label, args, senderLocation, chained, commandChain, logReturnUrn, failMessage);

                        // Extract
                        if (logReturnUrn.getValue() != null) {

                            // For
                            for (String ret : logReturnUrn.getValue()) {

                                // Add
                                logReturn.add(ret);
                            }
                        }

                    // MMOItems not installed. Returning
                    } else {

                        // Tell him lmao
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("\u00a77These commands are to be used with the third party plugin \u00a7e\u00a7lMMOItems \u00a77which you dont have installed."));
                    }
                    break;
                //endregion
                //region optifine
                case optifine:
                    //   0      1         2    args.Length
                    // /goop optifine {action}
                    //   -      0         1     args[n]

                    // Check 5 Permission
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.optifine");
                    }

                    // Got permission?
                    if (permission) {
                        if (args.length >= 2) {
                            // Some bool to know if failure
                            boolean failure = false;
                            ArrayList<Player> targets;

                            switch (args[1].toLowerCase()) {
                                //region glintEnchant
                                case "glintenchant":
                                    //   0      1        2            3             4       5     args.Length
                                    // /goop optifine glintEnchant <glint name> <player> <slot>
                                    //   -      0        1            2             3       4     args[n]

                                    // Correct number of args?
                                    if (args.length == 5) {

                                        // Gets that player boi
                                        targets = OotilityCeption.GetPlayers(senderLocation, args[3], null);
                                        RefSimulator<String> logAddition = new RefSimulator<>("");
                                        OptiFineGlint tGlint = OptiFineGlint.IsGlintLoaded(args[2], logAddition);

                                        // Does the player exist?
                                        if (targets.size() < 1) {
                                            // Failure
                                            failure = true;

                                            // Notify the error
                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Enchant", "Target must be an online player!"));
                                        }

                                        // Is such glint not loaded
                                        if (tGlint == null) {
                                            // Failure
                                            failure = true;

                                            // Notify the error
                                            logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Enchant", logAddition.GetValue()));
                                        }

                                        if (!failure) {

                                            // Each layer
                                            for (Player target : targets) {
                                                failure = false;

                                                //Lets get that inven slot
                                                RefSimulator<String> slotFailure = new RefSimulator<>("");
                                                ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[4], target, slotFailure);

                                                // So, does the slot make no sense?
                                                if (slott.size() == 0) {
                                                    // Failure
                                                    failure = true;
                                                }

                                                // Log
                                                if (slotFailure.getValue() != null) {
                                                    logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Enchant", slotFailure.getValue()));
                                                }

                                                // Bice Sintax
                                                if (!failure) {

                                                    // Preparation of Methods
                                                    TargetedItems executor = new TargetedItems(false, true,
                                                            chained, commandChain, sender, failMessage,

                                                            // What method to use to process the item
                                                            iSource -> OptiFineGlint.ApplyGlint(iSource.getValidOriginal(), tGlint, iSource.getLogAddition()),

                                                            // When will it succeed
                                                            iSource -> iSource.getResult() != null,

                                                            null
                                                    );

                                                    // Register the ItemStacks}
                                                    executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                                    // Process the stuff
                                                    executor.process();

                                                    // Was there any log messages output?
                                                    if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Enchant", executor.getIncludedStrBuilder().toString())); }
                                                }
                                            }
                                        }

                                    // Incorrect number of args
                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Enchant", "Incorrect usage. For info: \u00a7e/goop optifine"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop optifine glintEnchant <glint name> <player> <slot>");
                                        }
                                    }
                                    break;
                                //endregion
                                //region glintDefine
                                case "glintdefine":
                                    //   0      1        2            3             4                 5               6         args.Length
                                    // /goop optifine glintDefine <glint name> <enchantment> <enchantment level> [lore line]
                                    //   -      0        1            2             3                 4               5         args[n]

                                    // Correct number of args?
                                    if (args.length >= 5) {

                                        // Enchantment exsts?
                                        Enchantment tEnch = OotilityCeption.GetEnchantmentByName(args[3]);
                                        if (tEnch == null) {

                                            // Failure
                                            failure = true;

                                            // log
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Define", "Enchantment '\u00a73" + args[3] + "\u00a77' doesnt exist. Remember to use vanilla names.")); }
                                        }

                                        // Enchantment Level Parses?
                                        Integer eLevel = 0;
                                        if (args[4].equalsIgnoreCase("none")) {

                                            // No Level
                                            eLevel = null;

                                        // Does it parse?
                                        } else if (OotilityCeption.IntTryParse(args[4])) {

                                            // Get that number, straight up
                                            eLevel = OotilityCeption.ParseInt(args[4]);

                                        // Doesnt work rip
                                        } else {

                                            // Failure
                                            failure = true;

                                            // Log
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Define", "Expected integer number or \u00a7enone\u00a77 keyword instead of '\u00a73" + args[4] + "\u00a77'.")); }
                                        }

                                        // Bice sintax
                                        if (!failure) {

                                            // Build lore Line out of the remainder args
                                            StringBuilder lLine = null;
                                            if (args.length >= 6) {

                                                // Gather initial value
                                                lLine = new StringBuilder(args[5]);
                                                for (int i = 6; i < args.length; i++) {

                                                    // Add, separating with spaces
                                                    lLine.append(" ").append(args[i]);
                                                }
                                            }

                                            // Define Enchantment
                                            RefSimulator<String> logAddition = new RefSimulator<>("");
                                            OptiFineGlint.DefineGlint(args[2], tEnch, eLevel, lLine.toString(), logAddition);

                                            // Log whatever
                                            if (logAddition.GetValue() != null) {
                                                logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Define", logAddition.GetValue()));
                                            }
                                        }

                                    // Incorrect number of args
                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("OptiFine - Glint Define", "Incorrect usage. For info: \u00a7e/goop optifine"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop optifine glintDefine <glint name> <enchantment> <enchantment level> [lore line]");
                                        }
                                    }
                                    break;
                                    //endregion
                                //region json
                                case "togglejson":
                                    //   0      1        2        args.Length
                                    // /goop optifine toggleJSON
                                    //   -      0        1        args[n]

                                    // If mc version matches
                                    if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) {

                                        // Get player
                                        if (senderIsPlayer) {

                                            // Get Target
                                            Player target = (Player) sender;

                                            // Get Item in Mainahnd
                                            ItemStack iSource = target.getInventory().getItemInMainHand();

                                            // Is it air?
                                            if (OotilityCeption.IsAirNullAllowed(iSource)) {

                                                // Failure
                                                failure = true;

                                                // Notify
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Toggle JSON", "You must be holding something")); }

                                            } else if (!iSource.hasItemMeta()) {

                                                // Failure
                                                failure = true;

                                                // Notify
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Toggle JSON", "Your item must have a Custom Model Data value for this!")); }

                                            } else if (!iSource.getItemMeta().hasCustomModelData()) {

                                                // Failure
                                                failure = true;

                                                // Notify
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Toggle JSON", "Your held item must have a value of Custom Model Data for this.")); }
                                            }

                                            // Bryce Syntax
                                            if (!failure) {

                                                // Is it already loaded lma0?
                                                CustomModelDataLink alterior = CustomModelDataLink.getFrom(iSource);

                                                // Create if missing
                                                if (alterior == null) { alterior = new CustomModelDataLink(iSource.getType(), iSource.getItemMeta().getCustomModelData(), new ArrayList<>()); CustomModelDataLink.LoadNew(alterior); }

                                                // Does it have it already?
                                                if (alterior.HasReason(CMD_Link_Reasons.AsJSON_Furniture)) {

                                                    // Remove
                                                    alterior.RemoveReason(CMD_Link_Reasons.AsJSON_Furniture);
                                                    alterior.RemoveReason(CMD_Link_Reasons.AsMMOItem);

                                                    // Save
                                                    alterior.Save();

                                                    // Notify
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Toggle JSON", "Successfuly \u00a7cunregistered \u00a73" + iSource.getType() + "\u00a77-\u00a73" + iSource.getItemMeta().getCustomModelData() + "\u00a77 from being a JSON Furniture.")); }

                                                } else {

                                                    // Add
                                                    alterior.AddReason(CMD_Link_Reasons.AsJSON_Furniture);

                                                    // Is it a MMOItem?
                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) {

                                                        // Is it?
                                                        if (GooPMMOItems.IsMMOItem(iSource)) {

                                                            // Get Data
                                                            RefSimulator<String> mType = new RefSimulator<>(""), mID = new RefSimulator<>("");
                                                            GooPMMOItems.GetMMOItemInternals(iSource, mType, mID);

                                                            // Build and append meta
                                                            alterior.AddReason(CMD_Link_Reasons.AsMMOItem);
                                                            alterior.AddMeta(CMD_Link_Reasons.AsMMOItem, "m " + mType.getValue() + " " + mID.getValue());
                                                        }
                                                    }

                                                    // Save
                                                    alterior.Save();

                                                    // Notify
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Toggle JSON", "Successfuly \u00a7aregistered \u00a73" + iSource.getType() + "\u00a77-\u00a73" + iSource.getItemMeta().getCustomModelData() + "\u00a77 as a JSON Furniture.")); }
                                                }
                                            }

                                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                            // Mention
                                            logReturn.add(OotilityCeption.LogFormat("OptiFine - Toggle JSON", "You must call this as a player, holding the item you want to include."));
                                        }

                                    // No 1.14 lmao?
                                    } else {

                                        if (Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("OptiFine - Toggle JSON", "This links to item Custom Model Data, as such it requires MC version 1.14 or newer.")); }
                                    }
                                    break;
                                //endregion
                                default:
                                    // I have no memory of that shit
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("OptiFine", "'\u00a73" + args[1] + "\u00a77' is not a valid OptiFine action! do \u00a7e/goop optifine\u00a77 for the list of actions."));
                                    break;
                            }


                        } else if (args.length == 1) {
                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73GooP-OptiFine, \u00a77Stuff to use with OptiFine's CEM and CIT.");
                            logReturn.add("\u00a73Usage: \u00a7e/goop optifine {action}");
                            logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                            logReturn.add("\u00a73 --> \u00a7eglintEnchant <player> <slot> <glint name>");
                            logReturn.add("\u00a73      * \u00a77Enchants, as cosmetically as possible,");
                            logReturn.add("\u00a73        \u00a77adding lore if defined, the item the");
                            logReturn.add("\u00a73        \u00a77target player has in the specified slot.");
                            logReturn.add("\u00a73 --> \u00a7eglintDefine <glint name> <ench> <ench level> [lore line]");
                            logReturn.add("\u00a73      * \u00a77Defines a CIT custom glint, that uses");
                            logReturn.add("\u00a73        \u00a77target enchantment at target level, as");
                            logReturn.add("\u00a73        \u00a77well as a lore line to accompany it.");
                            logReturn.add("\u00a73 --> \u00a7etoggleJSON");
                            logReturn.add("\u00a73      * \u00a77Allows to place your item as a block in");
                            logReturn.add("\u00a73        \u00a77the world (in the head of an armor stand)");
                            logReturn.add("\u00a73        \u00a77as if it were a vanilla block.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Target slot in player's inventory.");
                            logReturn.add("\u00a73 --> \u00a77Possible slots: \u00a7bhead\u00a73, \u00a7bchest\u00a73, \u00a7blegs\u00a73, \u00a7bfeet\u00a73, \u00a7bmainhand\u00a73, \u00a7boffhand\u00a73, and any number \u00a7b0\u00a73-\u00a7b35\u00a73.");
                            logReturn.add("\u00a73 - \u00a7e<ench level> \u00a77Level of enchantment the .properties file matches.");
                            logReturn.add("\u00a73 --> \u00a77Theoretically, any integer number between \u00a7b-32768 \u00a77and \u00a7b32767\u00a77; or, the \u00a7bnone\u00a77 keyword.");

                        } else {
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat("OptiFine", "Incorrect usage. For info: \u00a7e/goop optifine"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop optifine {action}");
                            }
                        }

                        // No perms
                    } else {
                        // Tell him lmao
                        logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to mess with textures!"));
                    }
                    break;
                //endregion
                //region reload
                case reload:
                    //   0      1    args.Length
                    // /goop reload
                    //   -      0    args[n]

                    // Check 5 Permission
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.reload");
                    }

                    // Got permission?
                    if (permission) {

                        // Reload
                        Gunging_Ootilities_Plugin.theMain.Reload(false);

                        // Will always announce reloads. Regardless of log gamerules.
                        logReturn.add(OotilityCeption.LogFormat("Reload", "Reloaded all \u00a7e.yml\u00a77 files."));

                    // No perms
                    } else {

                        // Clarify it lack of perms
                        logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to reload the configs!"));
                    }
                    break;
                    //endregion
                //region nbt
                case nbt:
                    //   0      1         2    args.Length
                    // /goop nbt {action}
                    //   -      0         1     args[n]

                    // To extract log return
                    // Delegate onto NBT
                    onCommand_GooPNBT(sender, command, label, args, senderLocation, chained, commandChain, logReturnUrn, failMessage);

                    // Extract
                    if (logReturnUrn.getValue() != null) {

                        // For
                        for (String ret : logReturnUrn.getValue()) {

                            // Add
                            logReturn.add(ret);
                        }
                    }
                    break;
                //endregion
                //region unlocc
                case unlockables:
                    // Delegate onto
                    onCommand_GooPUnlock(sender, command, label, args, senderLocation, chained, commandChain, logReturnUrn, failMessage);

                    // Extract
                    if (logReturnUrn.getValue() != null) {

                        // For
                        for (String ret : logReturnUrn.getValue()) {

                            // Add
                            logReturn.add(ret);
                        }
                    }
                    break;
                //endregion
                //region customstructures
                case customstructures:
                    //   0          1             2    args.Length
                    // /goop customstructures {action}
                    //   -          0             1     args[n]

                    // Check 5 Permission
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.customstructures");
                    }

                    // Got permission?
                    if (permission) {
                        if (args.length >= 2) {
                            // Some bool to know if failure
                            boolean failure = false;

                            switch (args[1].toLowerCase()) {
                                //region edit
                                case "edit":
                                    //   0           1           2     3     args.Length
                                    // /goop customstructures  edit {action}
                                    //   -           0           1     2     args[n]

                                    // Correct number of args?
                                    if (args.length >= 3) {

                                        switch (args[2].toLowerCase()) {
                                            //region options
                                            case "options":
                                                //   0           1           2     3       4          5            args.Length
                                                // /goop customstructures  edit options {action} <structure name>
                                                //   -           0           1     2       3          4            args[n]

                                                // Correct number of args?
                                                if (args.length >= 5) {

                                                    CSStructure csRef = null;
                                                    if (!CSManager.csLoadedStructures.containsKey(args[4])) {

                                                        // Fail
                                                        failure = true;

                                                        // Log
                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Structure \u00a73" + args[4] + "\u00a77 not loaded."));

                                                    } else {

                                                        // Get
                                                        csRef = CSManager.csLoadedStructures.get(args[4]);
                                                    }

                                                    switch (args[3].toLowerCase()) {
                                                        //region vaultCost
                                                        case "vaultcost":
                                                            //   0           1          2     3         4          5            6       args.Length
                                                            // /goop customstructures edit options vaultcost <structure name> <cost>
                                                            //   -           0          1     2         3          4            5       args[n]

                                                            // Correct number of args?
                                                            if (args.length == 6) {

                                                                Double cost = null;
                                                                if (OotilityCeption.DoubleTryParse(args[5])) { cost = Double.parseDouble(args[5]); } else {

                                                                    // Fail
                                                                    failure = true;

                                                                    // Log
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Value \u00a73" + args[5] + "\u00a77 is not a number! "));
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Edit
                                                                    csRef.setVaultCost(cost);

                                                                    // Get Logger
                                                                    RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                    // Edit n Roll
                                                                    CSManager.saveOptions(csRef, logAddition);

                                                                    // Log Results
                                                                    if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", logAddition.GetValue()));}
                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Incorrect usage. For info: \u00a7e/goop customstructures edit options"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit options vaultCost <structure name> <cost>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region omni-interactive
                                                        case "omni":
                                                        case "omniinteractive":
                                                            //   0           1          2     3         4                   5          6       args.Length
                                                            // /goop customstructures edit options omniinteractive <structure name> <omni?>
                                                            //   -           0          1     2         3                   4          5       args[n]

                                                            // Correct number of args?
                                                            if (args.length == 6) {

                                                                Boolean permutativitty = null;
                                                                if (OotilityCeption.BoolTryParse(args[5])) { permutativitty = OotilityCeption.BoolParse(args[5]); } else {

                                                                    // Fail
                                                                    failure = true;

                                                                    // Log
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Value \u00a73" + args[5] + "\u00a77 must be either \u00a7btrue\u00a77 or \u00a7bfalse\u00a77. "));
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Edit
                                                                    csRef.setAllowPermutations(permutativitty);
                                                                    csRef.recalculatePermutations();

                                                                    // Get Logger
                                                                    RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                    // Edit n Roll
                                                                    CSManager.saveOptions(csRef, logAddition);

                                                                    // Log Results
                                                                    if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", logAddition.GetValue()));}
                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Incorrect usage. For info: \u00a7e/goop customstructures edit options"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit options omni <structure name> <omni?>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region blacklist
                                                        case "blacklist":
                                                        case "worldblacklist":
                                                            //   0           1          2     3         4           5             6       args.Length
                                                            // /goop customstructures edit options blacklist <structure name> [-][world]
                                                            //   -           0          1     2         3           4             5       args[n]

                                                            // Correct number of args?
                                                            if (args.length == 6 || args.length == 5) {

                                                                boolean removing = false;
                                                                String worldName = null;

                                                                // World operation
                                                                if (args.length == 6) {

                                                                    // Removing world or what
                                                                    removing = args[5].startsWith("-");
                                                                    worldName = removing ? args[5].substring(1) : args[5];

                                                                    // World exists?
                                                                    if (Bukkit.getWorld(worldName) == null) {

                                                                        // Snooze
                                                                        failure = true;

                                                                        // Log
                                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "World \u00a73" + worldName + "\u00a77 does not exist. "));
                                                                    }
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Just displaying the world blacklist
                                                                    if (worldName == null) {

                                                                        logReturn.add("\u00a7e______________________________________________");
                                                                        logReturn.add("\u00a77World Blacklist of Structure \u00a73" + args[4] + "\u00a77:");

                                                                        if (csRef.getWorldsBlacklist().size() == 0) {
                                                                            logReturn.add("\u00a73> \u00a77No Worlds Blacklisted \u00a73<");

                                                                        } else { for (String wrld : csRef.getWorldsBlacklist()) {  logReturn.add("\u00a7c - \u00a77" + wrld); } }

                                                                    } else {

                                                                        boolean worky = false;
                                                                        if (removing) { worky = csRef.unBlacklistWorld(worldName); } else if (!csRef.getWorldsBlacklist().contains(worldName)) { csRef.blacklistWorld(worldName); }

                                                                        if (worky) {

                                                                            // Get Logger
                                                                            RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                            // Edit n Roll
                                                                            CSManager.saveOptions(csRef, logAddition);

                                                                            // Log Results
                                                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", logAddition.GetValue()));}

                                                                        } else {
                                                                            logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Nothing happened,\u00a73 " + worldName + (removing ? "\u00a77 was not in the blacklist. " : "\u00a77 was already blacklisted. ")));
                                                                        }
                                                                    }
                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Incorrect usage. For info: \u00a7e/goop customstructures edit options"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit options blacklist <structure name> [-][world]");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region whitelist
                                                        case "whitelist":
                                                        case "worldwhitelist":
                                                            //   0           1          2     3         4           5             6       args.Length
                                                            // /goop customstructures edit options whitelist <structure name> [-][world]
                                                            //   -           0          1     2         3           4             5       args[n]

                                                            // Correct number of args?
                                                            if (args.length == 6 || args.length == 5) {

                                                                boolean removing = false;
                                                                String worldName = null;

                                                                // World operation
                                                                if (args.length == 6) {

                                                                    // Removing world or what
                                                                    removing = args[5].startsWith("-");
                                                                    worldName = removing ? args[5].substring(1) : args[5];

                                                                    // World exists?
                                                                    if (Bukkit.getWorld(worldName) == null) {

                                                                        // Snooze
                                                                        failure = true;

                                                                        // Log
                                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "World \u00a73" + worldName + "\u00a77 does not exist. "));
                                                                    }
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Just displaying the world blacklist
                                                                    if (worldName == null) {

                                                                        logReturn.add("\u00a7e______________________________________________");
                                                                        logReturn.add("\u00a77World Whitelist of Structure \u00a73" + args[4] + "\u00a77:");

                                                                        if (csRef.getWorldsBlacklist().size() == 0) {
                                                                            logReturn.add("\u00a73> \u00a77No Worlds Whitelisted \u00a73<");

                                                                        } else { for (String wrld : csRef.getWorldsBlacklist()) {  logReturn.add("\u00a7c - \u00a77" + wrld); } }

                                                                    } else {

                                                                        boolean worky = false;
                                                                        if (removing) { worky = csRef.unWhitelistWorld(worldName); } else if (!csRef.getWorldsWhitelist().contains(worldName)) { csRef.whitelistWorld(worldName); }

                                                                        if (worky) {

                                                                            // Get Logger
                                                                            RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                            // Edit n Roll
                                                                            CSManager.saveOptions(csRef, logAddition);

                                                                            // Log Results
                                                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", logAddition.GetValue()));}

                                                                        } else {
                                                                            logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Nothing happened,\u00a73 " + worldName + (removing ? "\u00a77 was not in the whitelist. " : "\u00a77 was already whitelisted. ")));
                                                                        }
                                                                    }
                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "Incorrect usage. For info: \u00a7e/goop customstructures edit options"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit options whitelist <structure name> [-][world]");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        default:
                                                            // I have no memory of that shit
                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Options", "'\u00a73" + args[2] + "\u00a77' is not a valid Custom Structures Edit Options action! do \u00a7e/goop customstructures edit options\u00a77 for the list of actions."));
                                                            break;
                                                    }

                                                    // Incorrect number of args
                                                } else if (args.length == 3) {

                                                    logReturn.add("\u00a7e______________________________________________");
                                                    logReturn.add("\u00a73Custom Structures - \u00a7bEdit Options, \u00a77Various options of the structure");
                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit options {action}");
                                                    logReturn.add("\u00a73 - \u00a7e{action} \u00a77What action to perform:");
                                                    if (Gunging_Ootilities_Plugin.foundVault) {
                                                        logReturn.add("\u00a73 --> \u00a7evaultCost <structure name> <cost>");
                                                        logReturn.add("\u00a73      * \u00a77Currency cost of using the structure");  }
                                                    logReturn.add("\u00a73 --> \u00a7eomniInteractive <structure name> <omni?>");
                                                    logReturn.add("\u00a73      * \u00a77Usually structures activate only by interacting with");
                                                    logReturn.add("\u00a73        \u00a77the structure core, but setting this to \u00a7btrue\u00a77 will");
                                                    logReturn.add("\u00a73        \u00a77allow any block of the structure to activate it.");
                                                    logReturn.add("\u00a73        \u00a7cIt is an exponential calculation, use at own risk.");
                                                    logReturn.add("\u00a73 --> \u00a7eworldWhitelist <structure name> [-][world]");
                                                    logReturn.add("\u00a73      * \u00a77Structure only triggers in this world.");
                                                    logReturn.add("\u00a73      * \u00a77Use the \u00a7b-\u00a77 prefix to remove.");
                                                    logReturn.add("\u00a73 --> \u00a7eworldBlacklist <structure name> [-][world]");
                                                    logReturn.add("\u00a73      * \u00a77Structure never triggers in this world.");
                                                    logReturn.add("\u00a73      * \u00a77Use the \u00a7b-\u00a77 prefix to remove.");

                                                } else {

                                                    // Notify
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                        logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Incorrect usage. For info: \u00a7e/goop customstructures edit actions"));
                                                        logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit actions {action}");
                                                    }
                                                }
                                                break;
                                            //endregion
                                            //region actions
                                            case "actions":
                                                //   0           1           2     3       4          5            args.Length
                                                // /goop customstructures  edit actions {action} <structure name>
                                                //   -           0           1     2       3          4            args[n]

                                                // Correct number of args?
                                                if (args.length >= 5) {

                                                    CSStructure csRef = null;
                                                    if (!CSManager.csLoadedStructures.containsKey(args[4])) {

                                                        // Fail
                                                        failure = true;

                                                        // Log
                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Structure \u00a73" + args[4] + "\u00a77 not loaded."));

                                                    } else {

                                                        // Get
                                                        csRef = CSManager.csLoadedStructures.get(args[4]);
                                                    }

                                                    switch (args[3].toLowerCase()) {
                                                        //region list
                                                        case "list":
                                                            //   0           1          2     3     4          5         args.Length
                                                            // /goop customstructures edit actions list <structure name>
                                                            //   -           0          1     2     3          4        args[n]

                                                            // Correct number of args?
                                                            if (args.length == 5) {

                                                                // Bice sintax
                                                                if (!failure) {
                                                                    logReturn.add("\u00a7e______________________________________________");
                                                                    logReturn.add("\u00a77Actions of structure \u00a73" + args[4] + "\u00a77:");

                                                                    if (csRef.getStructureActions() == null) {
                                                                        logReturn.add("\u00a73> \u00a77No Actions Defined \u00a73<");

                                                                    } else if (csRef.getStructureActions().size() == 0) {
                                                                        logReturn.add("\u00a73> \u00a77No Actions Defined \u00a73<");

                                                                    } else {
                                                                        for (int i = 0; i < csRef.getStructureActions().size(); i++ ) {  logReturn.add("\u00a73" + i + "\u00a7e - \u00a77" + csRef.getStructureActions().get(i)); }
                                                                    }
                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Incorrect usage. For info: \u00a7e/goop customstructures edit actions"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit actions list <structure name>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region remove
                                                        case "remove":
                                                            //   0           1          2     3       4         5             6    args.Length
                                                            // /goop customstructures edit actions remove <structure name> <index>
                                                            //   -           0          1     2       3         4             5    args[n]

                                                            // Correct number of args?
                                                            if (args.length == 6) {

                                                                Integer indx = null;
                                                                if (!OotilityCeption.IntTryParse(args[5])) {

                                                                    // Filure time
                                                                    failure = true;

                                                                    // Note
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Parameter index must be an integer number"));

                                                                } else {

                                                                    indx = OotilityCeption.ParseInt(args[5]);
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Remove action at that index
                                                                    ArrayList<String> csActions = csRef.getStructureActions();

                                                                    // If exists
                                                                    if (csActions != null) {

                                                                        // If fits
                                                                        if (indx < csActions.size() && indx >= 0) {

                                                                            // Remove alv
                                                                            csActions.remove((int)indx);

                                                                            // Get Logger
                                                                            RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                            // Edit n Roll
                                                                            CSManager.EditStructureActions(args[4], csActions, logAddition);

                                                                            // Log Results
                                                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", logAddition.GetValue()));}

                                                                        } else {

                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Index out of range"));
                                                                        }

                                                                    } else {

                                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Index out of range (no actions to begin remove from anyway)"));
                                                                    }

                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Incorrect usage. For info: \u00a7e/goop customstructures edit actions"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit actions remove <structure name> <index>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region edit
                                                        case "edit":
                                                            //   0           1          2     3      4         5             6       7+      args.Length
                                                            // /goop customstructures edit actions edit <structure name> <index> <action...>
                                                            //   -           0          1     2      3         4             5       6+      args[n]

                                                            // Correct number of args?
                                                            if (args.length >= 7) {

                                                                Integer indx = null;
                                                                if (!OotilityCeption.IntTryParse(args[5])) {

                                                                    // Filure time
                                                                    failure = true;

                                                                    // Note
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Parameter index must be an integer number"));

                                                                } else {

                                                                    indx = OotilityCeption.ParseInt(args[5]);
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Remove action at that index
                                                                    ArrayList<String> csActions = csRef.getStructureActions();

                                                                    // If exists
                                                                    if (csActions != null) {

                                                                        // If fits
                                                                        if (indx < csActions.size()) {

                                                                            StringBuilder fullParam = new StringBuilder(args[6]);
                                                                            for (int i = 7; i < args.length; i++) { fullParam.append(" ").append(args[i]); }

                                                                            // Remove alv
                                                                            csActions.set(indx, fullParam.toString());

                                                                            // Get Logger
                                                                            RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                            // Edit n Roll
                                                                            CSManager.EditStructureActions(args[4], csActions, logAddition);

                                                                            // Log Results
                                                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", logAddition.GetValue()));}

                                                                        } else {

                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Index out of range"));
                                                                        }

                                                                    } else {

                                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Index out of range (no actions to begin remove from anyway)"));
                                                                    }

                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Incorrect usage. For info: \u00a7e/goop customstructures edit actions"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit actions edit <structure name> <index> <command>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region add
                                                        case "add":
                                                            //   0           1          2     3      4         5             6+      args.Length
                                                            // /goop customstructures edit actions add <structure name> <action...>
                                                            //   -           0          1     2      3         4             5+      args[n]

                                                            // Correct number of args?
                                                            if (args.length >= 6) {

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Remove action at that index
                                                                    ArrayList<String> csActions = csRef.getStructureActions();

                                                                    // Make sure it exists
                                                                    if (csActions == null) { csActions = new ArrayList<>(); }

                                                                    StringBuilder fullParam = new StringBuilder(args[5]);
                                                                    for (int i = 6; i < args.length; i++) { fullParam.append(" ").append(args[i]); }
                                                                    if (chained) { fullParam.append(" oS= ").append(chainedNoLocation); }
                                                                    String fP = fullParam.toString();
                                                                    fP = fP.replace("<oS>", "oS=");

                                                                    // Remove alv
                                                                    csActions.add(fP);

                                                                    // Get Logger
                                                                    RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                    // Edit n Roll
                                                                    CSManager.EditStructureActions(args[4], csActions, logAddition);

                                                                    // Log Results
                                                                    if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", logAddition.GetValue()));}
                                                                }

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Incorrect usage. For info: \u00a7e/goop customstructures edit actions"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit actions edit <structure name> <index> <command>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        default:
                                                            // I have no memory of that shit
                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "'\u00a73" + args[2] + "\u00a77' is not a valid Custom Structures Edit Actions action! do \u00a7e/goop customstructures edit actions\u00a77 for the list of actions."));
                                                            break;
                                                    }

                                                    // Incorrect number of args
                                                } else if (args.length == 3) {

                                                    logReturn.add("\u00a7e______________________________________________");
                                                    logReturn.add("\u00a73Custom Structures - \u00a7bEdit Actions, \u00a77What commands will the structure run when used?.");
                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit actions {action}");
                                                    logReturn.add("\u00a73 - \u00a7e{action} \u00a77What action to perform:");
                                                    logReturn.add("\u00a73 --> \u00a7elist <structure name>");
                                                    logReturn.add("\u00a73      * \u00a77Shows what commands are executed by a structure.");
                                                    logReturn.add("\u00a73 --> \u00a7eadd <structure name> <command...>");
                                                    logReturn.add("\u00a73      * \u00a77Adds commands to be executed by a structure.");
                                                    logReturn.add("\u00a73 --> \u00a7eremove <structure name> <index>");
                                                    logReturn.add("\u00a73      * \u00a77Removes commands from being executed by a structure.");
                                                    logReturn.add("\u00a73 --> \u00a7eedit <structure name> <index> <command...>");
                                                    logReturn.add("\u00a73      * \u00a77Edits the commands that are executed by a structure.");

                                                } else {

                                                    // Notify
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                        logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Actions", "Incorrect usage. For info: \u00a7e/goop customstructures edit actions"));
                                                        logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit actions {action}");
                                                    }
                                                }
                                                break;
                                            //endregion
                                            //region triggers
                                            case "triggers":
                                                //   0           1          2     3     args.Length
                                                // /goop customstructures edit triggers {action}
                                                //   -           0          1     2     args[n]

                                                // Correct number of args?
                                                if (args.length >= 4) {

                                                    switch (args[3].toLowerCase()) {
                                                        //region list
                                                        case "list":
                                                            //   0           1          2       3    4          5         args.Length
                                                            // /goop customstructures edit triggers list [structure name]
                                                            //   -           0          1       2    3          4          args[n]

                                                            // Correct number of args?
                                                            if (args.length == 5) {

                                                                // Is structure loaded?
                                                                if (!CSManager.csLoadedStructures.containsKey(args[4])) {

                                                                    // Loaded strucutre of such name
                                                                    failure = true;

                                                                    // Mention
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - List Triggers","There isnt any loaded structure of name \u00a73" + args[4] + "\u00a77!.")); }
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {
                                                                    logReturn.add("\u00a77Triggers of structure \u00a73" + args[4] + "\u00a77:");

                                                                    ArrayList<CSTrigger> triggList = CSManager.csLoadedStructures.get(args[4]).getStructureTriggers();

                                                                    if (triggList == null) {
                                                                        logReturn.add("\u00a73> \u00a77No Triggers Defined \u00a73<");

                                                                    } else if (triggList.size() == 0) {
                                                                        logReturn.add("\u00a73> \u00a77No Triggers In Structure \u00a73<");

                                                                    } else {

                                                                        for (CSTrigger trig : triggList) { logReturn.add("\u00a7e - \u00a77" + trig); }
                                                                    }
                                                                }

                                                            // Incorrect number of args
                                                            } else if (args.length == 4) {

                                                                logReturn.add("\u00a73Possible structure Triggers:");
                                                                logReturn.add("\u00a7e--------- \u00a77Always Available:");
                                                                logReturn.add("\u00a7e > \u00a73INTERACT");
                                                                logReturn.add("\u00a73   \u00a77When a player right-clicks the core.");
                                                                logReturn.add("\u00a7e > \u00a73PUNCH");
                                                                logReturn.add("\u00a73   \u00a77When a player left-clicks the core.");
                                                                logReturn.add("\u00a7e > \u00a73COMPLETE");
                                                                logReturn.add("\u00a73   \u00a77When a player places the core block last");
                                                                logReturn.add("\u00a7e > \u00a73BREAK");
                                                                logReturn.add("\u00a73   \u00a77When a player breaks the core block first");
                                                                logReturn.add("");
                                                                logReturn.add("\u00a7e--------- \u00a77When the core block is a pressure plate:");
                                                                logReturn.add("\u00a7e > \u00a73PRESSUREPLATE_PLAYERS");
                                                                logReturn.add("\u00a73   \u00a77When an player steps on the core block");
                                                                logReturn.add("\u00a7e > \u00a73PRESSUREPLATE_MONSTERS");
                                                                logReturn.add("\u00a73   \u00a77When a monster steps on the core block");
                                                                logReturn.add("\u00a7e > \u00a73PRESSUREPLATE_ANIMALS");
                                                                logReturn.add("\u00a73   \u00a77When an animal steps on the core block");
                                                                logReturn.add("\u00a7e > \u00a73PRESSUREPLATE_ITEMS");
                                                                logReturn.add("\u00a73   \u00a77When an item is dropped on to the core block");

                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - List Triggers", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers list [structure name]");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region remove
                                                        case "remove":
                                                            //   0           1          2       3    4          5             6          args.Length
                                                            // /goop customstructures edit triggers remove <structure name> <trigger name>
                                                            //   -           0          1       2    3          4             5           args[n]

                                                            // Correct number of args?
                                                            if (args.length == 6) {

                                                                // Is structure loaded?
                                                                if (!CSManager.csLoadedStructures.containsKey(args[4])) {

                                                                    // Loaded strucutre of such name
                                                                    failure = true;

                                                                    // Mention
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Triggers","There isnt any loaded structure of name \u00a73" + args[4] + "\u00a77!.")); }
                                                                }

                                                                // Is it an actual trigger
                                                                CSTrigger trig = null;
                                                                try {
                                                                    // Yes, it seems to be
                                                                    trig = CSTrigger.valueOf(args[5].toUpperCase());

                                                                    // Not recognized
                                                                } catch (IllegalArgumentException ex) {

                                                                    // Fail
                                                                    failure = true;

                                                                    // Note
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Triggers","There isnt any trigger of name \u00a7e" + args[5] + "\u00a77!.")); }
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Get the list of triggers
                                                                    ArrayList<CSTrigger> eval = CSManager.csLoadedStructures.get(args[4]).getStructureTriggers();

                                                                    // Cant remove if doesnt exist
                                                                    if (!eval.contains(trig)) {

                                                                        // Note that it was already present
                                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Triggers","Structure of name \u00a73" + args[4] + " \u00a77didnt have the \u00a7e" + args[5] + "\u00a77 trigger.")); }

                                                                    // Remove it
                                                                    } else {

                                                                        // Add
                                                                        eval.remove(trig);

                                                                        // Git Ref
                                                                        RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                        // Append
                                                                        CSManager.EditStructureTriggers(args[4], eval, logAddition);

                                                                        // Mention
                                                                        if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Triggers", logAddition.GetValue())); }
                                                                    }
                                                                }

                                                            // Incorrect number of args
                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Triggers", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers remove <structure name> <trigger name>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region add
                                                        case "add":
                                                            //   0           1          2       3    4          5             6          args.Length
                                                            // /goop customstructures edit triggers add <structure name> <trigger name>
                                                            //   -           0          1       2    3          4             5           args[n]

                                                            // Correct number of args?
                                                            if (args.length == 6) {

                                                                // Is structure loaded?
                                                                if (!CSManager.csLoadedStructures.containsKey(args[4])) {

                                                                    // Loaded strucutre of such name
                                                                    failure = true;

                                                                    // Mention
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Triggers","There isnt any loaded structure of name \u00a73" + args[4] + "\u00a77!.")); }
                                                                }

                                                                // Is it an actual trigger
                                                                CSTrigger trig = null;
                                                                try {
                                                                    // Yes, it seems to be
                                                                    trig = CSTrigger.valueOf(args[5].toUpperCase());

                                                                    // Not recognized
                                                                } catch (IllegalArgumentException ex) {

                                                                    // Fail
                                                                    failure = true;

                                                                    // Note
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Triggers","There isnt any trigger of name \u00a7e" + args[5] + "\u00a77!.")); }
                                                                }

                                                                // Bice sintax
                                                                if (!failure) {

                                                                    // Get the list of triggers
                                                                    ArrayList<CSTrigger> eval = CSManager.csLoadedStructures.get(args[4]).getStructureTriggers();

                                                                    // No duplicate triggers
                                                                    if (eval.contains(trig)) {

                                                                        // Note that it was already present
                                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Triggers","Structure of name \u00a73" + args[4] + "\u00a77 already has trigger \u00a7e" + args[5] + "\u00a77.")); }

                                                                        // Add it
                                                                    } else {

                                                                        // Add
                                                                        eval.add(trig);

                                                                        // Git Ref
                                                                        RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                        // Append
                                                                        CSManager.EditStructureTriggers(args[4], eval, logAddition);

                                                                        // Mention
                                                                        if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Triggers", logAddition.GetValue())); }
                                                                    }

                                                                }

                                                                // Incorrect number of args
                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Triggers", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers add <structure name> <trigger name>");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region parameter
                                                        case "parameter":
                                                            //   0           1          2       3        4        5          6                7           8       args.Length
                                                            // /goop customstructures edit triggers parameter {action} <structure name> <trigger name> {action}
                                                            //   -           0          1       2        3        4          5                6           7       args[n]

                                                            // Correct number of args?
                                                            if (args.length >= 7) {

                                                                // Is structure loaded?
                                                                CSStructure csTarget = null;
                                                                if (!CSManager.csLoadedStructures.containsKey(args[5])) {

                                                                    // Loaded strucutre of such name
                                                                    failure = true;

                                                                    // Mention
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters","There isnt any loaded structure of name \u00a73" + args[5] + "\u00a77!.")); }
                                                                } else {

                                                                    // Retrieve
                                                                    csTarget = CSManager.csLoadedStructures.get(args[5]);
                                                                }

                                                                // Is it an actual trigger
                                                                CSTrigger trigr = null;
                                                                try {
                                                                    // Yes, it seems to be
                                                                    trigr = CSTrigger.valueOf(args[6].toUpperCase());

                                                                    // Not recognized
                                                                } catch (IllegalArgumentException ex) {

                                                                    // Fail
                                                                    failure = true;

                                                                    // Note
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters","There isnt any trigger of name \u00a7e" + args[6] + "\u00a77!.")); }
                                                                }

                                                                switch (args[4].toLowerCase()) {
                                                                    //region list
                                                                    case "list":
                                                                        //   0           1          2       3        4      5          6                7       args.Length
                                                                        // /goop customstructures edit triggers parameter list <structure name> <trigger name>
                                                                        //   -           0          1       2        3      4          5                6       args[n]

                                                                        // Correct number of args?
                                                                        if (args.length == 7) {

                                                                            // So far, both the structure and trigger parsed....
                                                                            if (!failure) {

                                                                                // Some wild info on the parameters
                                                                                logReturn.add("\u00a7e______________________________________________");
                                                                                logReturn.add("\u00a77Parameters of the Trigger \u00a73" + trigr + "\u00a77:");
                                                                                ArrayList<String> ret = new ArrayList<>();
                                                                                switch (trigr) {
                                                                                    case BREAK:
                                                                                    case COMPLETE:
                                                                                    case SNEAK_COMPLETE:
                                                                                    case SNEAK_BREAK:
                                                                                        logReturn.add("\u00a77This trigger doesnt support parameters!");
                                                                                        break;
                                                                                    case INTERACT:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.INTERACT);
                                                                                        logReturn.add("\u00a7b\u00a7oThe item the player is holding must match these when they interact");
                                                                                        break;
                                                                                    case SNEAK_INTERACT:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.SNEAK_INTERACT);
                                                                                        logReturn.add("\u00a7b\u00a7oThe item the player is holding must match these when they interact (while sneaking).");
                                                                                        break;
                                                                                    case PUNCH:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.PUNCH);
                                                                                        logReturn.add("\u00a7b\u00a7oThe item the player is holding must match these when they punch");
                                                                                        break;
                                                                                    case SNEAK_PUNCH:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.SNEAK_PUNCH);
                                                                                        logReturn.add("\u00a7b\u00a7oThe item the player is holding must match these when they punch (while sneaking)");
                                                                                        break;
                                                                                    case PRESSUREPLATE_PLAYERS:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.PRESSUREPLATE_PLAYERS);
                                                                                        logReturn.add("\u00a7b\u00a7oThe item the player is holding must match these when they step on the plate");
                                                                                        break;
                                                                                    case SNEAK_PRESSUREPLATE_PLAYERS:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.SNEAK_PRESSUREPLATE_PLAYERS);
                                                                                        logReturn.add("\u00a7b\u00a7oThe item the player is holding must match these when they step on the plate (while sneaking)");
                                                                                        break;
                                                                                    case PRESSUREPLATE_ITEMS:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.PRESSUREPLATE_ITEMS);
                                                                                        logReturn.add("\u00a7b\u00a7oThe item dropped on the pressure plate must match these.");
                                                                                        break;
                                                                                    case PRESSUREPLATE_MONSTERS:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.PRESSUREPLATE_MONSTERS);
                                                                                        logReturn.add("\u00a7b\u00a7oThe monsters that step on the pressure plate must match these.");
                                                                                        break;
                                                                                    case PRESSUREPLATE_ANIMALS:
                                                                                        ret = csTarget.getTriggerParameters().get(CSTrigger.PRESSUREPLATE_ANIMALS);
                                                                                        logReturn.add("\u00a7b\u00a7oThe animals that step on the pressure plate must match these.");
                                                                                        break;
                                                                                }

                                                                                if (!(csTarget.getStructureTriggers().contains(trigr))) {

                                                                                    // Filure time
                                                                                    failure = true;

                                                                                    // Note
                                                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - List Trigger Parameters", "Structure \u00a73" + args[5] + "\u00a77 doesnt have that trigger!"));
                                                                                }

                                                                                // Bice sintax
                                                                                if (!failure) {
                                                                                    if (ret != null) {
                                                                                        for (int i = 0; i < ret.size(); i++) { logReturn.add("\u00a73" + i + "\u00a7e - \u00a77" + ret.get(i)); }

                                                                                    } else {
                                                                                        logReturn.add("\u00a73> \u00a77No Parameters \u00a73<");
                                                                                    }
                                                                                }

                                                                            }
                                                                        } else {

                                                                            // Notify
                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                                logReturn.add(OotilityCeption.LogFormat("Custom Structures - List Trigger Parameters", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                                logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers parameters list <structure name> <trigger name>");
                                                                            }
                                                                        }
                                                                        break;
                                                                    //endregion
                                                                    //region remove
                                                                    case "remove":
                                                                        //   0           1          2       3        4      5          6                7           8       args.Length
                                                                        // /goop customstructures edit triggers parameter remove <structure name> <trigger name> <parameter index>
                                                                        //   -           0          1       2        3      4          5                6           7       args[n]


                                                                        // Correct number of args?
                                                                        if (args.length == 8) {

                                                                            // So far, both the structure and trigger parsed....
                                                                            if (!failure) {

                                                                                if (!csTarget.getStructureTriggers().contains(trigr)) {

                                                                                    // Filure time
                                                                                    failure = true;

                                                                                    // Note
                                                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Trigger Parameters", "That structure doesnt have that trigger!"));
                                                                                }
                                                                            }

                                                                            Integer indx = null;
                                                                            if (!OotilityCeption.IntTryParse(args[7])) {

                                                                                // Filure time
                                                                                failure = true;

                                                                                // Note
                                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Trigger Parameters", "Parameter index must be an integer number"));

                                                                            } else {

                                                                                indx = OotilityCeption.ParseInt(args[7]);
                                                                            }


                                                                            // Bice sintax
                                                                            if (!failure) {

                                                                                ArrayList<String> ret = new ArrayList<>();
                                                                                RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                                switch (trigr) {
                                                                                    case BREAK:
                                                                                    case COMPLETE:
                                                                                    case SNEAK_COMPLETE:
                                                                                    case SNEAK_BREAK:
                                                                                        ret = null;
                                                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Trigger Parameters", "This trigger doesnt support parameters!"));
                                                                                        break;
                                                                                    default:
                                                                                        ret = csTarget.getTriggerParameters().get(trigr);
                                                                                        break;
                                                                                }

                                                                                if (ret != null) {
                                                                                    if (ret.size() > indx && indx >= 0) {

                                                                                        // Index in range I guess I uh remove it?
                                                                                        ret.remove((int)indx);

                                                                                        CSManager.EditStructureTriggerParameters(args[5], trigr, ret, logAddition);

                                                                                        if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Trigger Parameters", logAddition.GetValue())); }

                                                                                    } else {
                                                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Trigger Parameters", "Index out of range!"));
                                                                                    }
                                                                                } else {
                                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Trigger Parameters", "Structure has no parameters to remove for trigger \u00a7e" + trigr));
                                                                                }
                                                                            }

                                                                        } else {

                                                                            // Notify
                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                                logReturn.add(OotilityCeption.LogFormat("Custom Structures - Remove Trigger Parameters", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                                logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers parameters remove <structure name> <trigger name> <parameter index>");
                                                                            }
                                                                        }
                                                                        break;
                                                                    //endregion
                                                                    //region edit
                                                                    case "edit":
                                                                        //   0           1          2       3        4      5          6                7             8               9 10 [11]     args.Length
                                                                        // /goop customstructures edit triggers parameter edit <structure name> <trigger name> <parameter index> <parameter args>
                                                                        //   -           0          1       2        3      4          5                6             7               8  9 [10]     args[n]

                                                                        // Correct number of args?
                                                                        if (args.length == 10 || args.length == 11 || args.length == 12) {

                                                                            // So far, both the structure and trigger parsed....
                                                                            if (!failure) {

                                                                                if (!csTarget.getStructureTriggers().contains(trigr)) {

                                                                                    // Filure time
                                                                                    failure = true;

                                                                                    // Note
                                                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", "That structure doesnt have that trigger!"));
                                                                                }
                                                                            }


                                                                            Integer indx = null;
                                                                            if (!OotilityCeption.IntTryParse(args[7])) {

                                                                                // Filure time
                                                                                failure = true;

                                                                                // Note
                                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", "Parameter index must be an integer number"));

                                                                            } else {

                                                                                indx = OotilityCeption.ParseInt(args[7]);
                                                                            }

                                                                            // Bice sintax
                                                                            if (!failure) {

                                                                                // BUild Parameter
                                                                                StringBuilder fullParam = new StringBuilder(args[8]);
                                                                                for (int i = 9; i < args.length; i ++) { fullParam.append(" ").append(args[i]); }

                                                                                ArrayList<String> ret;
                                                                                RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                                // Get may-existent amount
                                                                                String amount = null;
                                                                                if (args.length == 12) { amount = args[11]; }

                                                                                switch (trigr) {
                                                                                    case BREAK:
                                                                                    case COMPLETE:
                                                                                    case SNEAK_COMPLETE:
                                                                                    case SNEAK_BREAK:
                                                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", "This trigger doesnt support parameters!"));
                                                                                        break;
                                                                                    case INTERACT:
                                                                                    case PUNCH:
                                                                                    case SNEAK_INTERACT:
                                                                                    case SNEAK_PUNCH:
                                                                                    case SNEAK_PRESSUREPLATE_PLAYERS:
                                                                                    case PRESSUREPLATE_PLAYERS:
                                                                                    case PRESSUREPLATE_ITEMS:
                                                                                        ret = csTarget.getTriggerParameters().get(trigr);

                                                                                        // Of type NBT ITEM
                                                                                        if (args.length >= 11)  {
                                                                                            if (!OotilityCeption.IsInvalidItemNBTtestString(args[8], args[9], args[10], amount, logAddition)) {

                                                                                                if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", logAddition.GetValue())); }

                                                                                                if (ret.size() > indx && indx >= 0) {

                                                                                                    ret.add(indx, fullParam.toString());

                                                                                                    CSManager.EditStructureTriggerParameters(args[5], trigr, ret, logAddition);

                                                                                                    if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", logAddition.GetValue())); }

                                                                                                } else {

                                                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", "Index out of Range!"));
                                                                                                }

                                                                                            }  else if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", logAddition.GetValue())); }

                                                                                        }  else {

                                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", "Item NBT Filters require 3 arguments. You've provided two: \u00a7e" + args[8] + " " + args[9]));
                                                                                        }

                                                                                        break;
                                                                                    case PRESSUREPLATE_MONSTERS:
                                                                                    case PRESSUREPLATE_ANIMALS:
                                                                                        ret = csTarget.getTriggerParameters().get(trigr);

                                                                                        // Of type NBT Entity
                                                                                        if (!OotilityCeption.IsInvalidEntityNBTtestString(args[8], args[9], logAddition)) {

                                                                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", logAddition.GetValue())); }

                                                                                            if (ret.size() > indx) {

                                                                                                ret.add(indx, fullParam.toString());

                                                                                                CSManager.EditStructureTriggerParameters(args[4], trigr, ret, logAddition);

                                                                                                if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", logAddition.GetValue())); }

                                                                                            } else {

                                                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", "Index out of Range!"));
                                                                                            }

                                                                                        } else if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", logAddition.GetValue())); }
                                                                                        break;
                                                                                }
                                                                            }

                                                                        } else {

                                                                            // Notify
                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                                logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Trigger Parameters", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                                logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers parameters edit <structure name> <trigger name> <parameter index> <parameter args>");
                                                                            }
                                                                        }
                                                                        break;
                                                                    //endregion
                                                                    //region add
                                                                    case "add":
                                                                        //   0           1          2       3        4      5          6                7        8 9 [10]       args.Length
                                                                        // /goop customstructures edit triggers parameter add <structure name> <trigger name> <parameter args>
                                                                        //   -           0          1       2        3      4          5                6        7 8 [9]        args[n]

                                                                        // Correct number of args?
                                                                        if (args.length == 9 || args.length == 10 || args.length == 11) {

                                                                            // So far, both the structure and trigger parsed....
                                                                            if (!failure) {

                                                                                if (!csTarget.getStructureTriggers().contains(trigr)) {

                                                                                    // Filure time
                                                                                    failure = true;

                                                                                    // Note
                                                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", "That structure doesnt have that trigger!"));
                                                                                }
                                                                            }


                                                                            // Bice sintax
                                                                            if (!failure) {

                                                                                // BUild Parameter
                                                                                StringBuilder fullParam = new StringBuilder(args[7]);
                                                                                for (int i = 8; i < args.length; i ++) { fullParam.append(" ").append(args[i]); }

                                                                                ArrayList<String> ret;
                                                                                RefSimulator<String> logAddition = new RefSimulator<>("");

                                                                                // Get may-existent amount
                                                                                String amount = null;
                                                                                if (args.length == 11) { amount = args[10]; }

                                                                                switch (trigr) {
                                                                                    case BREAK:
                                                                                    case COMPLETE:
                                                                                    case SNEAK_BREAK:
                                                                                    case SNEAK_COMPLETE:
                                                                                        // Yea no
                                                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", "This trigger doesnt support parameters!"));
                                                                                        break;
                                                                                    case INTERACT:
                                                                                    case PUNCH:
                                                                                    case SNEAK_INTERACT:
                                                                                    case SNEAK_PRESSUREPLATE_PLAYERS:
                                                                                    case PRESSUREPLATE_ITEMS:
                                                                                    case PRESSUREPLATE_PLAYERS:
                                                                                    case SNEAK_PUNCH:
                                                                                        ret = csTarget.getTriggerParameters().get(trigr);

                                                                                        if (ret == null) { ret = new ArrayList<>(); }

                                                                                        // Of type NBT ITEM
                                                                                        if (args.length >= 10) {

                                                                                            // Test for vailidty
                                                                                            if (!OotilityCeption.IsInvalidItemNBTtestString(args[7], args[8], args[9], amount, logAddition)) {

                                                                                                // Cook and add
                                                                                                ret.add(fullParam.toString());

                                                                                                // Log
                                                                                                if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", logAddition.GetValue()));
                                                                                                }

                                                                                                // Save
                                                                                                CSManager.EditStructureTriggerParameters(args[5], trigr, ret, logAddition);
                                                                                            }

                                                                                            // Tell success or failure
                                                                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", logAddition.GetValue())); }

                                                                                        // Not enough args
                                                                                        } else {

                                                                                            // Tell that they need more args
                                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", "Item NBT Filters require 3 arguments."));
                                                                                        }

                                                                                        break;
                                                                                    case PRESSUREPLATE_MONSTERS:
                                                                                    case PRESSUREPLATE_ANIMALS:
                                                                                        ret = csTarget.getTriggerParameters().get(trigr);

                                                                                        if (ret == null) { ret = new ArrayList<>(); }

                                                                                        // Of type NBT Entity
                                                                                        if (!OotilityCeption.IsInvalidEntityNBTtestString(args[7], args[8], logAddition)) {

                                                                                            ret.add(fullParam.toString());

                                                                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", logAddition.GetValue())); }

                                                                                            CSManager.EditStructureTriggerParameters(args[5], trigr, ret, logAddition);
                                                                                        }

                                                                                        if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", logAddition.GetValue())); }
                                                                                        break;
                                                                                }
                                                                            }

                                                                        } else {

                                                                            // Notify
                                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                                logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                                logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers parameters add <structure name> <trigger name> <parameter args>");
                                                                            }
                                                                        }
                                                                        break;
                                                                    //endregion
                                                                    default:
                                                                        // I have no memory of that shit
                                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Add Trigger Parameters", "'\u00a73" + args[4] + "\u00a77' is not a valid Custom Structures Edit Trigger-parameters Action! do \u00a7e/goop customstructures edit triggers\u00a77 for the list of actions."));
                                                                        break;
                                                                }

                                                            // Incorrect number of args
                                                            } else {

                                                                // Notify
                                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                                    logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Triggers", "Incorrect usage. For info: \u00a7e/goop customstructures edit triggers"));
                                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers parameter <sub action key> <structure name> <trigger name> {sub action}");
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        default:
                                                            // I have no memory of that shit
                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Triggers", "'\u00a73" + args[3] + "\u00a77' is not a valid Custom Structures Edit Triggers Action! do \u00a7e/goop customstructures edit triggers\u00a77 for the list of actions."));
                                                            break;
                                                    }

                                                // Incorrect number of args
                                                } else if (args.length == 3) {

                                                    logReturn.add("\u00a7e______________________________________________");
                                                    logReturn.add("\u00a73Custom Structures - \u00a7bEdit, \u00a77Edits a loaded structure.");
                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit triggers {action}");
                                                    logReturn.add("\u00a73 - \u00a7e{action} \u00a77What action to perform:");
                                                    logReturn.add("\u00a73 --> \u00a7elist");
                                                    logReturn.add("\u00a73      * \u00a77Lists the triggers of structures.");
                                                    logReturn.add("\u00a73 --> \u00a7elist <structure name>");
                                                    logReturn.add("\u00a73      * \u00a77Lists the triggers of a loaded structure.");
                                                    logReturn.add("\u00a73 --> \u00a7eadd <structure name> <trigger name>");
                                                    logReturn.add("\u00a73      * \u00a77Adds a trigger to a structure.");
                                                    logReturn.add("\u00a73 --> \u00a7eremove <structure name> <trigger name>");
                                                    logReturn.add("\u00a73      * \u00a77Removes a trigger from a structure.");
                                                    logReturn.add("\u00a73 --> \u00a7eparameter list <structure name> <trigger name>");
                                                    logReturn.add("\u00a73      * \u00a77Lists the trigger parameters of a loaded structure");
                                                    logReturn.add("\u00a73 --> \u00a7eparameter add <structure name> <trigger name> <parameter args>");
                                                    logReturn.add("\u00a73      * \u00a77Adds a trigger parameter to a loaded structure");
                                                    logReturn.add("\u00a73 --> \u00a7eparameter remove <structure name> <trigger name> <parameter index>");
                                                    logReturn.add("\u00a73      * \u00a77Removes a trigger parameter from a loaded structure");
                                                    logReturn.add("\u00a73 --> \u00a7eparameter edit <structure name> <trigger name> <parameter index> <new parameter args>");
                                                    logReturn.add("\u00a73      * \u00a77Edits a trigger parameter of a loaded structure");

                                                } else {

                                                    // Notify
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                        logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit", "Incorrect usage. For info: \u00a7e/goop customstructures edit"));
                                                        logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit {action}");
                                                    }
                                                }
                                                break;
                                            //endregion
                                            //region composition
                                            case "composition":
                                                //   0           1          2       3              4               5         6   7   8   9  args.Length
                                                // /goop customstructures edit composition <structure name> <cuboid radius> [w] [x] [y] [z]
                                                //   -           0          1       2              3               4         5   6   7   8  args[n]

                                                // Correct number of args?
                                                if (args.length == 5 || args.length == 9) {

                                                    // Gets that location boi
                                                    Location targetLocation = null;
                                                    if (args.length == 5) {
                                                        if (senderIsPlayer) {

                                                            // Just target location I guess?
                                                            Block bLock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                                            // If exists
                                                            if (bLock == null) {

                                                                // Invalid location
                                                                failure = true;

                                                                // Mention
                                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition","You are not looking at any block!")); }

                                                                // I suppose its not air, right?
                                                            } else if (!OotilityCeption.IsAir(bLock.getType())) {

                                                                // Git Target Location
                                                                targetLocation = bLock.getLocation();

                                                                // Nvm it is air.
                                                            } else {

                                                                // Invalid location
                                                                failure = true;

                                                                // Mention
                                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition","You are not looking at any block!")); }

                                                            }

                                                        } else {
                                                            // Vro need coords
                                                            failure = true;

                                                            // Say
                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition", "When editiong composition of structures from the console, you must specify co-ordinates and a world!")); }
                                                        }

                                                        // Build Location from args, later, if they parse.
                                                    }

                                                    // Is structure loaded?
                                                    if (!CSManager.csLoadedStructures.containsKey(args[3])) {

                                                        // Loaded strucutre of such name
                                                        failure = true;

                                                        // Mention
                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition","There isnt any loaded structure of name \u00a73" + args[3] + "\u00a77!.")); }
                                                    }

                                                    // Size Parses?
                                                    Integer size = null; boolean asSelection = false;
                                                    if (args[4].toLowerCase().equals("selection")) {
                                                        asSelection = true;
                                                    } else if (!OotilityCeption.IntTryParse(args[4])) {

                                                        // Bruh
                                                        failure = true;

                                                        // Mention
                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition", "Expected integer number for cuboid radius instead of \u00a73" + args[4])); }
                                                    } else { size = OotilityCeption.ParseInt(args[4]); }

                                                    // Through Selection huh?
                                                    Player target = null;
                                                    if (asSelection) {

                                                        if (!Gunging_Ootilities_Plugin.foundWorldEdit) {

                                                            // Not so fast!
                                                            failure = true;

                                                            // Mention
                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition","To import structures from a WorldEdit selection, you need WorldEdit!"));
                                                        }

                                                        // Player must be sender
                                                        if (senderIsPlayer) {

                                                            // Store
                                                            target = (Player) sender;

                                                            // Selection?
                                                            if (Gunging_Ootilities_Plugin.foundWorldEdit) {

                                                                // Have they no selection?
                                                                if (!GooPWorldEdit.HasSelection(target)) {

                                                                    // Not so fast!
                                                                    failure = true;

                                                                    // Mention
                                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition","To import from selections, you must have a WorldEdit region selected."));
                                                                }
                                                            }

                                                        // Not a playerrr?
                                                        } else {

                                                            // Not so fast!
                                                            failure = true;

                                                            // Mention
                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition","To import from selections, the command must be called in-game, not from the console."));
                                                        }
                                                    }

                                                    // Parse location?
                                                    if (args.length == 9) {

                                                        // Make Error Messager
                                                        RefSimulator<String> logAddition = new RefSimulator<>("");

                                                        // Relativity
                                                        Player rel = null;
                                                        if (senderIsPlayer) { rel = (Player)sender;}

                                                        // Get
                                                        targetLocation = OotilityCeption.ValidLocation(rel, args[5], args[6], args[7], args[8], logAddition);

                                                        // Ret
                                                        if (targetLocation == null) { failure = true; }

                                                        // Add Log
                                                        if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition", logAddition.GetValue())); }
                                                    }

                                                    // Bice sintax
                                                    if (!failure) {

                                                        // Obtain composition
                                                        ArrayList<CSBlock> comp = null;
                                                        if (asSelection) {

                                                            // Get From Selecton
                                                            comp = CSManager.GenerateStructureFromPlayerSelection(targetLocation.getBlock(), target);

                                                        } else {

                                                            // Get From Radius
                                                            comp = CSManager.GenerateStructureFromWorld(targetLocation.getBlock(), size);
                                                        }
                                                        // Git Ref
                                                        RefSimulator<String> logAddition = new RefSimulator<>("");

                                                        // Ediet structure
                                                        CSManager.EditStructureComposition(args[3], comp, logAddition);

                                                        // Log
                                                        if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition", logAddition.GetValue())); }
                                                    }

                                                // Incorrect number of args
                                                } else if (args.length == 3) {

                                                    logReturn.add("\u00a7e______________________________________________");
                                                    logReturn.add("\u00a73Custom Structures - \u00a7bEdit Composition, \u00a77Edits the YML of a structure from something in the world.");
                                                    logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit composition <structure name> <cuboid radius> [w x y z]");
                                                    logReturn.add("\u00a73 - \u00a7e<structure name> \u00a77Name of loaded structure");
                                                    logReturn.add("\u00a73 - \u00a7e<cuboid radius> \u00a77Blocks around the 'core' to account for.");
                                                    logReturn.add("\u00a7e ---> \u00a77Air and Barrier blocks will be ignored (match any block)");
                                                    logReturn.add("\u00a7e ---> \u00a77Structure Void blocks will be saved as Air (match air)");
                                                    logReturn.add("\u00a73 - \u00a7e[w] \u00a77World");
                                                    logReturn.add("\u00a73 - \u00a7e[x y z] \u00a77Coords");
                                                    logReturn.add("\u00a78/goop customstructures edit composition Stove 1 world 420 69 30");
                                                    logReturn.add("\u00a73If coords are not specified, the 'core' will be the block you're looking at.");

                                                } else {

                                                    // Notify
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                        logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit Composition", "Incorrect usage. For info: \u00a7e/goop customstructures edit composition"));
                                                        logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit composition <structure name> <cuboid radius> [w x y z]");
                                                    }
                                                }
                                                break;
                                            //endregion

                                            default:
                                                // I have no memory of that shit
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit", "'\u00a73" + args[2] + "\u00a77' is not a valid Custom Structures Edit Action! do \u00a7e/goop customstructures edit\u00a77 for the list of actions."));
                                                break;
                                        }

                                    // Incorrect number of args
                                    } else if (args.length == 2) {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Custom Structures - \u00a7bEdit, \u00a77Edits a loaded structure.");
                                        logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit {action}");
                                        logReturn.add("\u00a73 - \u00a7e{action} \u00a77What action to perform:");
                                        logReturn.add("\u00a73 --> \u00a7ecomposition <structure name> <cuboid radius> [world x y z]");
                                        logReturn.add("\u00a73      * \u00a77Edits what blocks compose a structure.");
                                        logReturn.add("\u00a73      * \u00a73Barriers and Air wont be saved (will match any block)");
                                        logReturn.add("\u00a73      * \u00a73Structure Void will be saved as AIR (will match air)");
                                        logReturn.add("\u00a73 --> \u00a7etriggers {action}");
                                        logReturn.add("\u00a73      * \u00a77Stuff regarding what makes the structure activate.");
                                        logReturn.add("\u00a73 --> \u00a7eactions {action}");
                                        logReturn.add("\u00a73      * \u00a77Manage what commands to run when the structure activates.");
                                        logReturn.add("\u00a73 --> \u00a7eoption {action}");
                                        logReturn.add("\u00a73      * \u00a77Tweaks for the functioning of the custom structure.");

                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("Custom Structures - Edit", "Incorrect usage. For info: \u00a7e/goop customstructures edit"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop customstructures edit {action}");
                                        }
                                    }
                                    break;
                                //endregion
                                //region list
                                case "list":
                                    //   0           1          2     args.Length
                                    // /goop customstructures list
                                    //   -           0          1     args[n]

                                    // Correct number of args?
                                    if (args.length == 2) {

                                        logReturn.add("\u00a7e______________________________________________");
                                        if (CSManager.loadedStructures.size() == 0) {
                                            logReturn.add("\u00a73Custom Structures - \u00a7bList, \u00a77Lists all loaded structures, there arent any right now though.");
                                        } else {
                                            logReturn.add("\u00a73Custom Structures - \u00a7bList, \u00a77All loaded structures:");
                                            for (CSStructure struct : CSManager.loadedStructures) { logReturn.add("\u00a73 - \u00a77" + struct.getStructureName()); }
                                        }

                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - List", "Incorrect usage. \u00a7e/goop customstructures list")); }
                                    }
                                    break;
                                //endregion
                                //region import
                                case "import":
                                    //   0           1           2         3                4          5   6   7   8    args.Length
                                    // /goop customstructures import <structure name> <cuboid radius> [w] [x] [y] [z]
                                    //   -           0           1          2               3          4   5   6   9  args[n]

                                    // Correct number of args?
                                    if (args.length == 4 || args.length == 8) {

                                        // Gets that location boi
                                        Location targetLocation = null;
                                        if (args.length == 4) {
                                            if (senderIsPlayer) {

                                                // Just target location I guess?
                                                Block bLock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                                // If exists
                                                if (bLock == null) {

                                                    // Invalid location
                                                    failure = true;

                                                    // Mention
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import","You are not looking at any block!"));

                                                    // I suppose its not air, right?
                                                } else if (!OotilityCeption.IsAir(bLock.getType())) {

                                                    // Git Target Location
                                                    targetLocation = bLock.getLocation();

                                                    // Nvm it is air.
                                                } else {

                                                    // Invalid location
                                                    failure = true;

                                                    // Mention
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import","You are not looking at any block!"));

                                                }

                                            } else {
                                                // Vro need coords
                                                failure = true;

                                                // Say
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import", "When importing structures from the console, you must specify co-ordinates and a world!"));
                                            }

                                        // Build Location from args, later, if they parse.
                                        }

                                        // Is structure loaded?
                                        if (CSManager.csLoadedStructures.containsKey(args[2])) {

                                            // Loaded strucutre of such name
                                            failure = true;

                                            // Mention
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import","There already is a loaded structure of name \u00a73" + args[2] + "\u00a77!. Instead, try \u00a7e/goop customstructures edit composition " + args[2] + " " + args[3]));
                                        }

                                        // Size Parses?
                                        Integer size = null; boolean asSelection = false;
                                        if (args[3].toLowerCase().equals("selection")) {
                                            asSelection = true;
                                        } else if (!OotilityCeption.IntTryParse(args[3])) {

                                            // Bruh
                                            failure = true;

                                            // Mention
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import", "Expected integer number for cuboid radius instead of '\u00a73" + args[3] + "\u00a77'"));

                                        } else { size = OotilityCeption.ParseInt(args[3]); }

                                        // Through Selection huh?
                                        Player target = null;
                                        if (asSelection) {

                                            if (!Gunging_Ootilities_Plugin.foundWorldEdit) {

                                                // Not so fast!
                                                failure = true;

                                                // Mention
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import","To import structures from a WorldEdit selection, you need WorldEdit!"));
                                            }

                                            // Player must be sender
                                            if (senderIsPlayer) {

                                                // Store
                                                target = (Player) sender;

                                                // Selection?
                                                if (Gunging_Ootilities_Plugin.foundWorldEdit) {

                                                    // Have they no selection?
                                                    if (!GooPWorldEdit.HasSelection(target)) {

                                                        // Not so fast!
                                                        failure = true;

                                                        // Mention
                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import","To import from selections, you must have a WorldEdit region selected."));
                                                    }
                                                }

                                            // Not a playerrr?
                                            } else {

                                                // Not so fast!
                                                failure = true;

                                                // Mention
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import","To import from selections, the command must be called in-game, not from the console."));
                                            }
                                        }

                                        // Parse location?
                                        if (args.length == 8) {

                                            // Make Error Messager
                                            RefSimulator<String> logAddition = new RefSimulator<>("");

                                            // Relativity
                                            Player rel = null;
                                            if (senderIsPlayer) { rel = (Player)sender;}

                                            // Get
                                            targetLocation = OotilityCeption.ValidLocation(rel, args[4], args[5], args[6], args[7], logAddition);

                                            // Ret
                                            if (targetLocation == null) { failure = true; }

                                            // Add Log
                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import", logAddition.GetValue())); }
                                        }

                                        // Bice sintax
                                        if (!failure) {

                                            // Obtain composition
                                            ArrayList<CSBlock> comp = null;
                                            if (asSelection) {

                                                // Get From Selecton
                                                comp = CSManager.GenerateStructureFromPlayerSelection(targetLocation.getBlock(), target);

                                            } else {

                                                // Get From Radius
                                                comp = CSManager.GenerateStructureFromWorld(targetLocation.getBlock(), size);
                                            }

                                            // Git Ref
                                            RefSimulator<String> logAddition = new RefSimulator<>("");

                                            // Save structure
                                            CSManager.SaveNewStructure(args[2], comp, null, null, logAddition);

                                            // Log
                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import", logAddition.GetValue())); }
                                        }

                                        // Incorrect number of args
                                    } else if (args.length == 2) {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Custom Structures - \u00a7bImport, \u00a77Writes a YML structure from something in the world.");
                                        logReturn.add("\u00a73Usage: \u00a7e/goop customstructures import <structure name> <cuboid radius> [w x y z]");
                                        logReturn.add("\u00a73 - \u00a7e<structure name> \u00a77Name of the new structure");
                                        logReturn.add("\u00a73 - \u00a7e<cuboid radius> \u00a77Blocks around the 'core' to account for.");
                                        logReturn.add("\u00a7e ---> \u00a77Air and Barrier blocks will be ignored (match any block)");
                                        logReturn.add("\u00a7e ---> \u00a77Structure Void blocks will be saved as Air (match air)");
                                        logReturn.add("\u00a73 - \u00a7e[w] \u00a77World");
                                        logReturn.add("\u00a73 - \u00a7e[x y z] \u00a77Coords");
                                        logReturn.add("\u00a78/goop customstructures import Stove 1 world 420 69 30");
                                        logReturn.add("\u00a73If coords are not specified, the 'core' will be the block you're looking at.");

                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("Custom Structures - Import", "Incorrect usage. For info: \u00a7e/goop customstructures import"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop customstructures import <structure name> <cuboid radius> [w x y z]");
                                        }
                                    }
                                    break;
                                //endregion
                                //region build
                                case "build":
                                    //   0           1           2         3          4   5   6   7   8 args.Length
                                    // /goop customstructures build <structure name> [w] [x] [y] [z] [f]
                                    //   -           0           1          2         3   4   5   6   7 args[n]

                                    // Correct number of args?
                                    if (args.length == 3 || args.length >= 7) {

                                        // Gets that location boi
                                        Location targetLocation = null;
                                        if (args.length == 3) {
                                            if (senderIsPlayer) {

                                                // Just target location I guess?
                                                Block bLock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                                // If exists
                                                if (bLock == null) {

                                                    // Invalid location
                                                    failure = true;

                                                    // Mention
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build","You are not looking at any block!"));

                                                // I suppose its not air, right?
                                                } else if (!OotilityCeption.IsAir(bLock.getType())) {

                                                    // Git Target Location
                                                    targetLocation = bLock.getLocation();

                                                // Nvm it is air.
                                                } else {

                                                    // Invalid location
                                                    failure = true;

                                                    // Mention
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build","You are not looking at any block!"));

                                                }

                                            } else {
                                                // Vro need coords
                                                failure = true;

                                                // Say
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build", "When building structures from the console, you must specify co-ordinates and a world!"));
                                            }

                                        // Build Location from args, later, if they parse.
                                        }

                                        // Is structure loaded?
                                        if (!CSManager.csLoadedStructures.containsKey(args[2])) {

                                            // No loaded strucutre of such name
                                            failure = true;

                                            // Mention
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build","No loaded structure of such name \u00a73" + args[2]));
                                        }

                                        // Parse location?
                                        if (args.length >= 7) {

                                            // Make Error Messager
                                            RefSimulator<String> logAddition = new RefSimulator<>("");

                                            // Relativity
                                            Player rel = null;
                                            if (senderIsPlayer) { rel = (Player)sender;}

                                            // Get
                                            targetLocation = OotilityCeption.ValidLocation(rel, args[3], args[4], args[5], args[6], logAddition);

                                            // Ret
                                            if (targetLocation == null) { failure = true; }

                                            // Add Log
                                            if (logAddition.GetValue() != null) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build", logAddition.GetValue())); }
                                        }

                                        Orientations o = Orientations.SouthForward;
                                        if (args.length >= 8) {

                                            try {

                                                // Add those
                                                o = Orientations.valueOf(args[7]);

                                            } catch (IllegalArgumentException ignored) {

                                                // No loaded strucutre of such name
                                                failure = true;

                                                // Mention
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build","Unknown facing direction \u00a73" + args[7]));
                                            }
                                        }

                                        // Bice sintax
                                        if (!failure) {

                                            // Build at location
                                            CSManager.csLoadedStructures.get(args[2]).generateAt(null, targetLocation, o,false, false, false);

                                            // Mention success
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build","Successfuly Generated Structure \u00a73" + args[2] + "\u00a77 at \u00a7e " + OotilityCeption.BlockLocation2String(targetLocation))); }
                                        }

                                        // Incorrect number of args
                                    } else if (args.length == 2) {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Custom Structures - \u00a7bBuild, \u00a77Place loaded structure in the world.");
                                        logReturn.add("\u00a73Usage: \u00a7e/goop customstructures build <structure name> [w x y z] [facing]");
                                        logReturn.add("\u00a73 - \u00a7e<structure name> \u00a77Name of the loaded structure");
                                        logReturn.add("\u00a73 - \u00a7e[w] \u00a77World");
                                        logReturn.add("\u00a73 - \u00a7e[x y z] \u00a77Coords");
                                        logReturn.add("\u00a73 - \u00a7e[facing] \u00a77Facing direction, by default south.");
                                        logReturn.add("\u00a78/goop customstructures build Stove world 420 69 30");

                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("Custom Structures - Build", "Incorrect usage. For info: \u00a7e/goop customstructures build"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop customstructures build <structure name> [w x y z]");
                                        }
                                    }
                                    break;
                                //endregion

                                default:
                                    // I have no memory of that shit
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Custom Structures", "'\u00a73" + args[1] + "\u00a77' is not a valid Custom Structures action! do \u00a7e/goop customstructures\u00a77 for the list of actions."));
                                    break;
                            }


                        } else if (args.length == 1) {
                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Custom Structures, \u00a77Make player-buildable stuff come to life!");
                            logReturn.add("\u00a73Usage: \u00a7e/goop customstructures {action}");
                            logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                            logReturn.add("\u00a73 --> \u00a7eimport <structure name> <cuboid radius> [world x y z]");
                            logReturn.add("\u00a73      * \u00a77Creates a new structure from the blocks you are looking at.");
                            logReturn.add("\u00a73        \u00a77You must edit it further for it to actually work.");
                            logReturn.add("\u00a73      * \u00a73Barriers and Air wont be saved (will match any block)");
                            logReturn.add("\u00a73      * \u00a73Structure Void will be saved as AIR (will match air)");
                            logReturn.add("\u00a73 --> \u00a7eedit {action}");
                            logReturn.add("\u00a73      * \u00a77Edit structures realtime in-game.");
                            logReturn.add("\u00a73 --> \u00a7ebuild <structure name> [world x y z]");
                            logReturn.add("\u00a73      * \u00a77Places such structure in the world,");
                            logReturn.add("\u00a73        \u00a77Centered on the block you are looking at.");
                            logReturn.add("\u00a73 --> \u00a7elist");
                            logReturn.add("\u00a73      * \u00a77List all loaded structures.");

                        } else {
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat("NBT", "Incorrect usage. For info: \u00a7e/goop nbt"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop nbt {action}");
                            }
                        }

                        // No perms
                    } else {
                        // Tell him lmao
                        logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to configure custom structures!"));
                    }
                    break;
                //endregion
                //region mythicmobs
                case mythicmobs:
                    //   0      1        2      args.Length
                    // /goop mythicmobs {action}
                    //   -      0        1      args[n]

                    // FFS Check for MMOItems
                    if (Gunging_Ootilities_Plugin.foundMythicMobs) {

                        // Check 5 Permission
                        if (senderIsPlayer) {
                            // Solid check for permission
                            permission = sender.hasPermission("gunging_ootilities_plugin.mythicmobs");
                        }

                        // Got permission?
                        if (permission) {
                            if (args.length >= 2) {
                                // Some bool to know if failure
                                boolean failure = false;

                                switch (args[1].toLowerCase()) {
                                    //region var
                                    case "var":
                                        //   0      1        2     3         4           5         6        args.Length
                                        // /goop mythicmobs var <entity> <variable> <operation> [range]
                                        //   -      0        1     2         3           4         7        args[n]

                                        // Correct number of args
                                        if (args.length >= 5 && args.length <= 6) {

                                            String cstName = args[2];
                                            boolean isGlobal = "global".equals(cstName);

                                            // Try to find entity
                                            ArrayList<Entity> casters = null;
                                            if (!isGlobal) {
                                                casters = new ArrayList<>();
                                                ArrayList<Player> pTargets = OotilityCeption.GetPlayers(senderLocation, cstName, null);
                                                Entity tEntity = OotilityCeption.getEntityByUniqueId(cstName);
                                                if (tEntity != null && !(tEntity instanceof Player)) { casters.add(tEntity); }
                                                for (Player target : pTargets) { casters.add(target); }

                                                // Still Null?
                                                if (casters.size() < 1) {

                                                    // No entity was found
                                                    failure = true;

                                                    // Yike
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", "No entity nor player was found."));
                                                }
                                            }

                                            // Var name experiences no failures
                                            String varName = args[3];

                                            String varOpp = args[4];
                                            boolean readonly = "read".equalsIgnoreCase(varOpp);
                                            boolean unsetOperation = "unset".equalsIgnoreCase(varOpp);
                                            PlusMinusPercent pmp = new PlusMinusPercent(0D, true, false);
                                            if (!readonly && !unsetOperation) {
                                                RefSimulator<String> logAddition = new RefSimulator<>(null);
                                                pmp = PlusMinusPercent.GetPMP(varOpp, logAddition);
                                                if (logAddition.getValue() != null) {
                                                    logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", logAddition.getValue())); }
                                                if (pmp == null) { failure = true; }
                                            }
                                            if (unsetOperation) { pmp = null; }

                                            // Parse range of success
                                            QuickNumberRange qnr = null;
                                            boolean varIsSetFalse = false;
                                            if (args.length >= 6) {
                                                String varRange = args[5];
                                                varIsSetFalse = "null".equalsIgnoreCase(varRange);
                                                if (!varIsSetFalse) {
                                                    qnr = QuickNumberRange.FromString(varRange);
                                                    if (qnr == null) {
                                                        failure = true;
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", "Invalid number range\u00a7e " + varRange + "\u00a77. "));
                                                    }
                                                } }

                                            // Nice Sintax
                                            if (!failure) {
                                                if (isGlobal) {

                                                    // Sudoskill Skidush
                                                    Double val = GooPMythicMobs.modifyVariable(null, varName, pmp);

                                                    // Compare and succeed
                                                    if ((qnr == null && !varIsSetFalse) || (val == null && varIsSetFalse) || (val != null && qnr.InRange(val))) {
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", "Global variable\u00a7e " + varName + "\u00a77 is \u00a7b" + val + "\u00a77, \u00a7aSucceeded\u00a77. "));

                                                        commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(val)));

                                                        commandChain.chain(chained, (Player) null, sender);
                                                    } else {
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", "Global variable\u00a7e " + varName + "\u00a77 is \u00a7b" + val + "\u00a77, \u00a7cFail\u00a77. "));
                                                    }

                                                } else {

                                                    // For
                                                    for (Entity kaster : casters) {

                                                        // Sudoskill Skidush
                                                        Double val = GooPMythicMobs.modifyVariable(kaster, varName, pmp);

                                                        if ((qnr == null && !varIsSetFalse) || (val == null && varIsSetFalse) || (val != null && qnr.InRange(val))) {

                                                            // Log Output
                                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", "Variable\u00a7e " + varName + "\u00a77 of\u00a73 " + kaster.getName() + "\u00a77 is \u00a7b" + val + "\u00a77, \u00a7aSucceeded\u00a77. "));

                                                            // Proc Chain
                                                            Player chainProc = null;
                                                            if (kaster instanceof Player) { chainProc = (Player) kaster; }

                                                            commandChain.setChainedCommand(OotilityCeption.ReplaceFirst(commandChain.getChainedCommand(), "@v", String.valueOf(val)));

                                                            // Run Chain
                                                            commandChain.chain(chained, chainProc, sender);

                                                        } else {

                                                            // Log Output
                                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", "Variable\u00a7e " + varName + "\u00a77 of\u00a73 " + kaster.getName() + "\u00a77 is \u00a7b" + val + "\u00a77, \u00a7cFail\u00a77. "));
                                                        }
                                                    }
                                                }
                                            }

                                        } else if (args.length == 2) {
                                            logReturn.add("\u00a7e______________________________________________");
                                            logReturn.add("\u00a73Var, \u00a77Checks the MythicMobs variable of an entity");
                                            logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs var <entity> <variable> <operation> [range]");
                                            logReturn.add("\u00a73 - \u00a7e<entity> \u00a77UUID of the entity, or player name");
                                            logReturn.add("\u00a73 --> \u00a7bglobal \u00a77keyword to check for global vars");
                                            logReturn.add("\u00a73 - \u00a7e<variable> \u00a77Variable name");
                                            logReturn.add("\u00a73 - \u00a7e<operation> \u00a77Operation to perform on this variable");
                                            logReturn.add("\u00a73 --> \u00a7bread \u00a77keyword to make no changes");
                                            logReturn.add("\u00a73 --> \u00a7bunset \u00a77keyword to unset the variable");
                                            logReturn.add("\u00a73 - \u00a7e[range] \u00a77Range of values for the command to succeed");
                                            logReturn.add("\u00a73 --> \u00a7bnull \u00a77detects unset variables");

                                        } else {

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                logReturn.add(OotilityCeption.LogFormat("MythicMobs - Var", "Incorrect usage. For info: \u00a7e/goop mythicmobs var"));
                                                logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs var <entity> <variable> <operation> [range]");
                                            }
                                        }

                                        break;
                                    //endregion
                                    //region runSkillAS
                                    case "runskillas":
                                        //   0      1           2           3             4           5            6        7       args.Length
                                        // /goop mythicmobs runskillas <skill name> <uuid/pname> [@<target>] [~<trigger>] [v[NAME]=[VALUE];[NAME2]=[VALUE2]]
                                        //   -      0           1           2             3           4            5        6       args[n]

                                        // Correct number of args
                                        if (args.length >= 4 && args.length <= 7) {

                                            // Skill exists?
                                            if (!GooPMythicMobs.SkillExists(args[2])) {

                                                // Fail
                                                failure = true;

                                                // Note
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Run Skill As", "There is no Mythic Skill of name \u00a73" + args[2]));
                                            }

                                            // Try to find entity
                                            ArrayList<Entity> casters = new ArrayList<>();
                                            ArrayList<Player> pTargets = OotilityCeption.GetPlayers(senderLocation, args[3], null);
                                            Entity tEntity = OotilityCeption.getEntityByUniqueId(args[3]);
                                            if (tEntity != null && !(tEntity instanceof Player)) { casters.add(tEntity); }
                                            for (Player target : pTargets) { casters.add(target); }

                                            // Still Null?
                                            if (casters.size() < 1) {

                                                // No entity was found
                                                failure = true;

                                                // Yike
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Run Skill As", "No entity nor player was found."));
                                            }

                                            Entity tTarEntity = null, tTriEntity = null;
                                            ArrayList<CompactCodedValue> vars = new ArrayList<>();
                                            if (args.length > 4) {

                                                String targetTry = null, triggerTry = null, varTry = null;
                                                // Attain
                                                for (int i = 4; i < args.length; i++) {

                                                    // Gather
                                                    if (args[i].startsWith("@")) { targetTry = args[i].substring(1); }
                                                    if (args[i].startsWith("~")) { triggerTry = args[i].substring(1); }
                                                    if (args[i].startsWith("v")) { varTry = args[i].substring(1); }
                                                }

                                                // If had target
                                                if (targetTry != null) {

                                                    // Try to find entity
                                                    tTarEntity = Bukkit.getPlayerExact(targetTry);
                                                    if (tTarEntity == null) {

                                                        // Its not a player name, lets git from UUID
                                                        tTarEntity = OotilityCeption.getEntityByUniqueId(targetTry);
                                                    }

                                                    // Still Null?
                                                    if (tTarEntity == null) {

                                                        // Yike
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Run Skill As", "Target entity was invalid, ignoring."));
                                                    }
                                                }

                                                // If had target
                                                if (triggerTry != null) {

                                                    // Try to find entity
                                                    tTriEntity = Bukkit.getPlayerExact(triggerTry);
                                                    if (tTriEntity == null) {

                                                        // Its not a player name, lets git from UUID
                                                        tTriEntity = OotilityCeption.getEntityByUniqueId(triggerTry);
                                                    }

                                                    // Still Null?
                                                    if (tTriEntity == null) {

                                                        // Yike
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Run Skill As", "Trigger entity was invalid, ignoring."));
                                                    }
                                                }

                                                // If vars exist BRUH
                                                if (varTry != null) {

                                                    // Variables, anyone?
                                                    vars = CompactCodedValue.ListFromString(varTry.replace((Gunging_Ootilities_Plugin.gMyMoRuSkAsDoUnSp ? "__" : "-"), " "));
                                                }
                                            }

                                            // Bryce Sintax
                                            if (!failure) {

                                                // For
                                                for (Entity kaster : casters) {

                                                    // Sudoskill Skidush
                                                    GooPMythicMobs.ExecuteMythicSkillAs(args[2], kaster, tTriEntity, tTarEntity, vars);

                                                    // Log Output
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Run Skill As", "Forced \u00a7f" + kaster.getName() + "\u00a77 to run Mythic Skill \u00a7e" + args[2]));


                                                    // Proc Chain
                                                    Player chainProc = null;
                                                    if (kaster instanceof Player) { chainProc = (Player) kaster; }

                                                    // Run Chain
                                                    commandChain.chain(chained, chainProc, sender);
                                                }
                                            }

                                        } else if (args.length == 2) {
                                            logReturn.add("\u00a7e______________________________________________");
                                            logReturn.add("\u00a73Run Skill As, \u00a77Sudoskills an entity.");
                                            logReturn.add("\u00a73Usage: \u00a7e/goop runSkillAs <skill name> <entity> [@<target.uuid>] [~<trigger.uuid>] [v[NAME]=[VALUE];[NAME2]=[VALUE2]]");
                                            logReturn.add("\u00a73 - \u00a7e<skill name> \u00a77Internal name of the Mythic Skill");
                                            logReturn.add("\u00a73 - \u00a7e<entity> \u00a77UUID of the entity to sudoskill");
                                            logReturn.add("\u00a73 --> \u00a77May be a player name to sudoskill players.");
                                            logReturn.add("\u00a73 - \u00a7e[@<target.uuid>] \u00a77UUID of an entity to set as skill target.");
                                            logReturn.add("\u00a73 - \u00a7e[~<trigger.uuid>] \u00a77UUID of an entity to set as skill trigger.");
                                            logReturn.add("\u00a73 - \u00a7e[v[NAME]=[VALUE];[NAME2]=[VALUE2]] \u00a77Dynamic vars \u00a7b<goop.dynamic.[NAME]>");
                                            logReturn.add("\u00a73Examples:");
                                            logReturn.add("\u00a77 /goop runSkillAs RadiantDiscThrow gunging @atuosto");
                                            logReturn.add("\u00a78 -> Caster is player 'gunging', target is player 'atuosto'");
                                            logReturn.add("\u00a77 /goop runSkillAs Poison gunging ~cocopad @libraryaddict");
                                            logReturn.add("\u00a78 -> Caster is player 'gunging', target is player 'libraryaddict', trigger will be player 'cocopad'");

                                        } else {

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                logReturn.add(OotilityCeption.LogFormat("MythicMobs - Run Skill As", "Incorrect usage. For info: \u00a7e/goop mythicmobs runSkillAs"));
                                                logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs runskillas <skill name> <(uuid)/(player name)>");
                                            }
                                        }

                                        break;
                                    //endregion
                                    //region minion
                                    case "minion":
                                    case "minions":
                                        //   0      1          2        3             4            5          6         7               args.Length
                                        // /goop mythicmobs minion <owner uuid> <minion uuid> [leashrange] [skill] [pvpBlock]
                                        //   -      0          1        2             3            4          5         6               args[n]

                                        // Correct number of args
                                        if (args.length >= 4 && args.length <= 7) {

                                            // Get minion and owner
                                            Entity mOwner = OotilityCeption.getEntityByUniqueId(args[2]);
                                            if (mOwner == null) { mOwner = (Entity) OotilityCeption.GetPlayer(args[2], false); }
                                            Entity mMinion = OotilityCeption.getEntityByUniqueId(args[3]);

                                            // They existed right?
                                            if (mOwner == null) {

                                                // Fail
                                                failure = true;

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "Could not find owner \u00a73" + args[2]));
                                            }
                                            if (mMinion == null) {

                                                // Fail
                                                failure = true;

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "Could not find minion \u00a73" + args[3]));

                                            // Is it already an entity?
                                            } else if (SummonerClassMinion.isMinion(mMinion)) {

                                                // Fail
                                                failure = true;

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "That \u00a7e" + mMinion.getName() + "\u00a77 is already a minion."));

                                            } else if (mMinion instanceof Player) {

                                                // Fail
                                                failure = true;

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "Players cannot be minions."));
                                            }

                                            // Leash range valid?
                                            double leashRange = 20;
                                            if (args.length >= 5) {

                                                // Try parse?
                                                if (OotilityCeption.DoubleTryParse(args[4])) {

                                                    // Parse
                                                    leashRange = Double.parseDouble(args[4]);

                                                } else {

                                                    // Fail
                                                    failure = true;

                                                    // Note
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "Expected numeric value instead of \u00a73" + args[4]));
                                                }
                                            }

                                            // Get MM Skill
                                            String skill = null;
                                            if (args.length >= 6) {

                                                // Skill exists?
                                                if (!GooPMythicMobs.SkillExists(args[5])) {

                                                    // Fail
                                                    failure = true;

                                                    // Note
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "There is no Mythic Skill of name \u00a73" + args[5]));

                                                // Skill does exist, nice.
                                                } else {

                                                    // Store
                                                    skill = args[5];
                                                }
                                            }

                                            boolean pvpBlock = false;
                                            if (args.length >= 7) {

                                                // Skill exists?
                                                if (!OotilityCeption.BoolTryParse(args[6])) {

                                                    // Fail
                                                    failure = true;

                                                    // Note
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "Expected boolean value instead of \u00a73" + args[6]));

                                                    // Skill does exist, nice.
                                                } else {

                                                    // Store
                                                    pvpBlock = OotilityCeption.BoolParse(args[6]);
                                                }
                                            }

                                            // Bryce Sintax
                                            if (!failure) {

                                                // Create entity
                                                SummonerClassMinion newMinion = new SummonerClassMinion(mOwner, mMinion);

                                                // Set the options
                                                newMinion.setLeashRange(leashRange);
                                                newMinion.setSkillOnRemove(skill);
                                                newMinion.setPreventPlayerDamage(pvpBlock);

                                                // Enable
                                                newMinion.Enable();

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "Registered minion \u00a7e" + mMinion.getName() + "\u00a77 under the command of \u00a73" + mOwner.getName()));

                                                // Proc Chain
                                                Player chainProc = null;
                                                if (mOwner instanceof Player) { chainProc = (Player) mOwner; }

                                                // Run Chain
                                                commandChain.chain(chained, chainProc, sender);
                                            }

                                        } else if (args.length == 2) {
                                            logReturn.add("\u00a7e______________________________________________");
                                            logReturn.add("\u00a73Minion, \u00a77Classifies an entity as someone's minion.");
                                            logReturn.add("\u00a73Usage: \u00a7e/goop minion <owner uuid> <minion uuid> [leashrange] [skill] [pvpBlock]");
                                            logReturn.add("\u00a73 - \u00a7e<owner uuid> \u00a77UUID of the entity, or player name, who will own the minion.");
                                            logReturn.add("\u00a73 - \u00a7e<minion uuid> \u00a77UUID of the entity that will be the minion.");
                                            logReturn.add("\u00a73 - \u00a7e[leashrange] \u00a77Will teleport minion to owner if they are too far.");
                                            logReturn.add("\u00a73 --> \u00a77Default is set to \u00a7b20\u00a77, measured in blocks.");
                                            logReturn.add("\u00a73 - \u00a7e[skill] \u00a77Mythic Skill to run when minion dies due to this command.");
                                            logReturn.add("\u00a73 --> \u00a7d@Self \u00a77The minion.");
                                            logReturn.add("\u00a73 --> \u00a7e@Trigger \u00a77The owner");
                                            logReturn.add("\u00a73 --> \u00a7a@Origin \u00a77Owner's death location.");
                                            logReturn.add("\u00a73 - \u00a7e[pvpBlock] \u00a77Prevent players from dealing damage to this minion?");
                                            logReturn.add("\u00a78Minions will die when their owner dies or leaves the server.");
                                            logReturn.add("\u00a78If they run a mythic skill, you must kill them through it; They will run that instead of dying.");

                                        } else {

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                logReturn.add(OotilityCeption.LogFormat("MythicMobs - Minion", "Incorrect usage. For info: \u00a7e/goop mythicmobs minion"));
                                                logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs minion <owner uuid/name> <minion uuid> [leashrange] [skill]");
                                            }
                                        }
                                        break;
                                    //endregion
                                    //region damageTakenLink
                                    case "damagetakenlink":
                                        //   0      1              2              3             4               5                   6             7         8       args.Length
                                        // /goop mythicmobs damagetakenlink <source uuid> <receiver uuid> [transfer percent] [prevent percent] [loud]  [duration]
                                        //   -      0              1              2             3               4                   5             6         7       args[n]

                                        // Correct number of args
                                        if (args.length >= 4 && args.length <= 8) {

                                            // Try to find entity
                                            ArrayList<Entity> sources = new ArrayList<>();
                                            ArrayList<Player> pSources = OotilityCeption.GetPlayers(senderLocation, args[2], null);
                                            if (!args[2].contains(";")) {
                                                Entity sEntity = OotilityCeption.getEntityByUniqueId(args[2]);
                                                if (sEntity != null && !(sEntity instanceof Player)) { sources.add(sEntity); }
                                            } else {
                                                for (String str : args[2].split(";")) {
                                                    Entity sEntity = OotilityCeption.getEntityByUniqueId(str);
                                                    if (sEntity != null && !(sEntity instanceof Player)) { sources.add(sEntity); } } }

                                            for (Player target : pSources) { sources.add(target); }
                                            if (sources.size() < 1) {
                                                // Fail
                                                failure = true;

                                                // Bruh
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Source entity not found, unable to link."));
                                            }

                                            // Try to find entity
                                            ArrayList<Entity> receivers = new ArrayList<>();
                                            ArrayList<Player> pReceivers = OotilityCeption.GetPlayers(senderLocation, args[3], null);
                                            Entity rEntity = OotilityCeption.getEntityByUniqueId(args[3]);
                                            Entity receiver = null;
                                            if (rEntity != null && !(rEntity instanceof Player)) { receivers.add(rEntity); }
                                            for (Player target : pReceivers) { receivers.add(target); }
                                            if (receivers.size() < 1) {
                                                // Fail
                                                failure = true;

                                                // Bruh
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Receiver entity not found, unable to link."));
                                            } else {

                                                // Get first heck
                                                receiver = receivers.get(0);
                                            }
                                            if (receiver == null) {
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Invalid Receiver"));
                                                failure = true;
                                            }

                                            double transferPercent = 1.0D, preventPercent = 1.0D;
                                            if (args.length >= 5) {
                                                String tP = args[4]; boolean pc = false;
                                                if (args[4].endsWith("%")) { pc = true;  tP = args[4].substring(0, args[4].length() - 1); }

                                                if (OotilityCeption.DoubleTryParse(tP)) {

                                                    // Parse
                                                    transferPercent = Double.parseDouble(tP);

                                                    // Multiply
                                                    if (pc) { transferPercent *= 0.01D; }

                                                } else {

                                                    // Fail
                                                    failure = true;

                                                    // Bruh
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Expected numeric value like \u00a7b0.4\u00a77 or \u00a7b40%\u00a77 (two versions to write the same thing) instead of \u00a7e" + tP));
                                                }
                                            }
                                            if (args.length >= 6) {
                                                String tP = args[5]; boolean pc = false;
                                                if (args[5].endsWith("%")) { pc = true;  tP = args[5].substring(0, args[5].length() - 1); }

                                                if (OotilityCeption.DoubleTryParse(tP)) {

                                                    // Parse
                                                    preventPercent = Double.parseDouble(tP);

                                                    // Multiply
                                                    if (pc) { preventPercent *= 0.01D; }

                                                } else {

                                                    // Fail
                                                    failure = true;

                                                    // Bruh
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Expected numeric value like \u00a7b0.8\u00a77 or \u00a7b80%\u00a77 (two versions to write the same thing) instead of \u00a7e" + tP));
                                                }
                                            }
                                            boolean silent = false;
                                            if (args.length >= 7) {

                                                if (OotilityCeption.BoolTryParse(args[6])) {

                                                    // Parse
                                                    silent = !OotilityCeption.BoolParse(args[6]);

                                                } else {

                                                    // Fail
                                                    failure = true;

                                                    // Bruh
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Expected \u00a7btrue\u00a77 or \u00a7bfalse\u00a77 instead of \u00a7e" + args[6]));
                                                }
                                            }

                                            // Timer
                                            OptimizedTimeFormat otf = null;
                                            if (args.length >= 8) {

                                                // Identify time measure
                                                String magnitude = args[7].substring(0, args[7].length() - 1);
                                                String units = args[7].substring(args[7].length() - 1);

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

                                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Time units '" + units + "\u00a77' not recognized. Must use \u00a7bs m h d \u00a77or\u00a7b y \u00a77(Second, Minute, Hour, Day, Year)"));

                                                            break;
                                                    }

                                                } else {

                                                    // Fail
                                                    failure = true;

                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Expected an integer for time value instead of \u00a73" + magnitude));
                                                }
                                            }

                                            // Bryce Sintax
                                            if (!failure) {
                                                //DTL//OotilityCeption.Log("\u00a78DTL\u00a73 CMD\u00a77 Receiver (" + receiver.getName() + " | " + receiver.getCustomName() + ")\u00a7b " + receiver.getUniqueId().toString());

                                                // Receiver identified as receiver
                                                GooPUnlockables uck = GooPUnlockables.From(receiver.getUniqueId(), EntityLinkedEntity.transferLinkReceiver);
                                                ScoreboardLinks.watchNoDupe(receiver.getUniqueId());
                                                uck.SetTimed(otf, true);
                                                uck.Unlock();
                                                uck.setPersistent(false);

                                                /*
                                                 * # All entities who are not receivers will be linked to the same receiver.
                                                 *
                                                 * # The receiver cannot be linked to themselves
                                                 */
                                                ArrayList<EntityLinkedEntity> linkedEntities = new ArrayList<>();
                                                for (Entity target : sources) {
                                                    if (target == null) { continue; }
                                                    //DTL//OotilityCeption.Log("\u00a78DTL\u00a73 CMD\u00a77 Source (" + target.getName() + " | " + target.getCustomName() + ")\u00a7b " + target.getUniqueId().toString());

                                                    // Not the reciever is it
                                                    if (target.getUniqueId().equals(receiver.getUniqueId())) {
                                                        //DTL//OotilityCeption.Log("\u00a78DTL\u00a73 CMD\u00a7c Cancelled:\u00a76 Source equals Receiver");
                                                        continue; }

                                                    // The target is not a receiver is they
                                                    GooPUnlockables uuck = GooPUnlockables.From(target.getUniqueId(), EntityLinkedEntity.transferLinkReceiver);
                                                    uuck.CheckTimer();
                                                    uuck.setPersistent(false);

                                                    // If unlocked, this is a receiver
                                                    if (uuck.IsUnlocked()) {
                                                        //DTL//OotilityCeption.Log("\u00a78DTL\u00a73 CMD\u00a7c Cancelled:\u00a76 Source is a Receiver");
                                                        continue; }

                                                    // Build end unlockable
                                                    GooPUnlockables linkUCK = GooPUnlockables.From(target.getUniqueId(), EntityLinkedEntity.transferLinkParticipant + receiver.getUniqueId().toString());
                                                    linkUCK.SetTimed(otf, true);
                                                    linkUCK.Unlock();
                                                    linkUCK.setPersistent(false);

                                                    // All right, add
                                                    linkedEntities.add(new DamageTransferLink(target, ObjectiveLinks.DamageTransferLink, linkUCK, receiver, uck, preventPercent, transferPercent, silent)); }

                                                // Activate receiver
                                                if (linkedEntities.size() > 0) {

                                                    // FOreach
                                                    for (EntityLinkedEntity source : linkedEntities) {

                                                        // Load it hell yea
                                                        ScoreboardLinks.linkNoDupe(source);

                                                        // Log Output
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Now trasnfering \u00a7e" + (transferPercent * 100D) + "%\u00a77 of damage dealt from \u00a73" + source.getEntity().getName() + "\u00a77 to \u00a73" + receiver.getName())); }

                                                        Player target = null;
                                                        if (source.getEntity() instanceof Player) { target = (Player) source.getEntity(); }

                                                        // Run Chain
                                                        commandChain.chain(chained, target, sender);
                                                    }
                                                }
                                            }
                                        } else if (args.length == 2) {
                                            logReturn.add("\u00a7e______________________________________________");
                                            logReturn.add("\u00a73Damage Taken Link, \u00a77Transfers damage from one entity to another.");
                                            logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs damagetakenlink <source uuid> <receiver uuid> [transfer percent] [prevent percent] [loud] [duration]");
                                            logReturn.add("\u00a73 - \u00a7e<source uuid> \u00a77Entities that are directly receiving the damage (\u00a78; separated list\u00a77) ");
                                            logReturn.add("\u00a73 - \u00a7e<reciver uuid> \u00a77Entity to which the damage will be transfered");
                                            logReturn.add("\u00a73 - \u00a7e[transfer percent] \u00a77Fraction of the damage that will be transferred (Default: \u00a7b1\u00a77=\u00a7b100%\u00a77)");
                                            logReturn.add("\u00a73 - \u00a7e[prevent percent] \u00a77Lessening of the damage taken by the source (Default: \u00a7b1\u00a77=\u00a7b100%\u00a77)");
                                            logReturn.add("\u00a73 - \u00a7e[loud] \u00a77Should the damage transfer fire the entity damage event (kinda advanced)?");
                                            logReturn.add("\u00a73 - \u00a7e[duration] \u00a77Time that the link will last (Inlcude units, \u00a7bs m h d\u00a77, forever if unspecified)");
                                            logReturn.add("\u00a78Wears out when the time runs out, when the receiver dies, or when the server reboots.");

                                        } else {

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                logReturn.add(OotilityCeption.LogFormat("MythicMobs - Damage Taken Link", "Incorrect usage. For info: \u00a7e/goop mythicmobs damageTakenLink"));
                                                logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs damagetakenlink <source uuid> <receiver uuid> [transfer percent] [prevent percent] [loud] [duration]");
                                            }
                                        }

                                        break;
                                        //endregion
                                    default:
                                        // I have no memory of that shit
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("MythicMobs", "'\u00a73" + args[1] + "\u00a77' is not a valid MythicMobs action! do \u00a7e/goop mythicmobs\u00a77 for the list of actions."));
                                        break;
                                }

                            } else if (args.length == 1) {
                                logReturn.add("\u00a7e______________________________________________");
                                logReturn.add("\u00a73GooP-MythicMobs, \u00a77Related to the third party plugin.");
                                logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs {action}");
                                logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                                logReturn.add("\u00a73 --> \u00a7erunSkillAs <skill name> <entity uuid> [@<target.uuid>] [~<trigger.uuid>] [v[NAME]=[VALUE];[NAME2]=[VALUE2]]");
                                logReturn.add("\u00a73      * \u00a77Sudoskills the entity to run a mythicmobs skill.");
                                logReturn.add("\u00a73      * \u00a73Can be a player name instead of entity uuid.");
                                logReturn.add("\u00a73 --> \u00a7eminion <owner uuid> <entity uuid> [leashrange] [skill]");
                                logReturn.add("\u00a73      * \u00a77If the owner dies or logs out, the minion dies.");


                            } else {
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                    logReturn.add(OotilityCeption.LogFormat("MythicMobs", "Incorrect usage. For info: \u00a7e/goop mythicmobs"));
                                    logReturn.add("\u00a73Usage: \u00a7e/goop mythicmobs {action}");
                                }
                            }

                            // No perms
                        } else {
                            // Tell him lmao
                            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to use mythicmobs-related commands!"));
                        }

                        // MMOItems not installed. Returning
                    } else {

                        // Tell him lmao
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("\u00a77These commands are to be used with the third party plugin \u00a7e\u00a7lMythicMobs \u00a77which you dont have installed."));
                    }
                    break;
                //endregion
                //region scoreboard
                case scoreboard:
                    //   0      1         2    args.Length
                    // /goop scoreboard {action}
                    //   -      0         1     args[n]

                    // Check 5 Permission
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.scoreboard");
                    }

                    // Got permission?
                    if (permission) {
                        if (args.length >= 2) {
                            // Some bool to know if failure
                            boolean failure = false;

                            // Some scoreboards to test
                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                            Scoreboard targetScoreboard = manager.getMainScoreboard();

                            switch (args[1].toLowerCase()) {
                                //region top
                                case "top":
                                    //   0        1      2           3                4            5     6      args.Length
                                    // /goop scoreboard top <objective source> <objective target> <n> [max swp]
                                    //   -        0      1           2                3            4     5      args[n]

                                    // Correct number of args?
                                    if (args.length >= 5 && args.length <= 6) {

                                        Objective sourceObjective = targetScoreboard.getObjective(args[2]);
                                        Objective targetObjective = targetScoreboard.getObjective(args[3]);
                                        boolean tExists = true;

                                        // Does the scoreboard objective exist?
                                        if (sourceObjective == null) {
                                            // Failure
                                            failure = true;

                                            // Mention
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Top N", "Scoreboard Objective '\u00a73" + args[2] + "\u00a77' does not exist."));
                                        }

                                        // Does the scoreboard objective exist?
                                        if (targetObjective == null) {
                                            // Target doesnt exist
                                            tExists = false;
                                        }

                                        // Reasonable name
                                        if (args[3].length() > 16 && GooP_MinecraftVersions.GetMinecraftVersion() < 18)  {

                                            // Fail
                                            failure = true;

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Top N", "Output objective name '\u00a73" + args[3] + "\u00a77' must not be longer than 16 characters."));
                                        }

                                        ArrayList<EntityType> et = null;
                                        int maxSWP = -1;
                                        if (args.length >= 6) {

                                            /*
                                             * If it has commas, Entity List
                                             * If it is a number, raw number
                                             * If neither but existing, fail
                                             * If missing, online players
                                             */
                                            
                                            if (args[5].contains(",")) {
                                                
                                                // Parse entity list
                                                et = new ArrayList<>();
                                                
                                                // Split by commas
                                                for (String str : args[5].split(",")) {
                                                    
                                                    // Parses as Entity List?
                                                    try {
                                                        
                                                        // Agony
                                                        EntityType type = EntityType.valueOf(str);
                                                        
                                                        et.add(type);
                                                        
                                                    // 
                                                    } catch (IllegalArgumentException ignored) { }
                                                }
                                                
                                            // Parse as entity list
                                            } else if (OotilityCeption.IntTryParse(args[5])) {
                                                
                                                // Number
                                                maxSWP = OotilityCeption.ParseInt(args[5]);
                                                
                                            // Single Entity
                                            } else {

                                                // Parses as Entity List?
                                                try {

                                                    // Agony
                                                    EntityType type = EntityType.valueOf(args[5]);

                                                    // Might just be players in which there is a quicker way: Leaving it null
                                                    if (type != EntityType.PLAYER) {

                                                        // Create
                                                        et = new ArrayList<>();
                                                        et.add(type);
                                                    }

                                                // Failure
                                                } catch (IllegalArgumentException ignored) {
                                                    
                                                    // Fail
                                                    failure = true;

                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Top N", "Expected integer number or EntityType instead of '\u00a73" + args[5] + "\u00a77'"));
                                                }
                                            }
                                        }

                                        // Does n parse?
                                        Integer n = null;
                                        if (OotilityCeption.IntTryParse(args[4])) {
                                            n = OotilityCeption.ParseInt(args[4]);
                                        } else {

                                            // Fail
                                            failure = true;

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Top N", "Expected integer number for \u00a7e\u00a7on\u00a77 instead of '\u00a73" + args[4] + "\u00a77'"));
                                        }

                                        // Bice sintax
                                        if (!failure) {

                                            // Get all participants
                                            ArrayList<OrderedScoreboardEntry> scoreboardParticipants;

                                            // Prepare entries override
                                            if (maxSWP > 0) {

                                                // Get all participants of the sweep
                                                scoreboardParticipants = OotilityCeption.SortEntriesOf(sourceObjective, null, maxSWP);

                                            // Et not null?
                                            } else if (et != null) {

                                                // UUIDs
                                                ArrayList<String> entities = new ArrayList<>();

                                                // Adds players as names
                                                if (et.contains(EntityType.PLAYER)) {
                                                    et.remove(EntityType.PLAYER);
                                                    for (Player plyr : Bukkit.getOnlinePlayers()) {
                                                        if (plyr == null) { continue; } entities.add(plyr.getName()); } }

                                                // All loaded entities of thay type
                                                for (World wrdl : Bukkit.getWorlds()) {

                                                    // Add all loaded entities of type!
                                                    for (Chunk chk : wrdl.getLoadedChunks()) {

                                                        // Get entities
                                                        for (Entity ent : chk.getEntities()) {

                                                            // Type contained?
                                                            if (et.contains(ent.getType())) {

                                                                // Add UUID
                                                                entities.add(ent.getUniqueId().toString());
                                                            }
                                                        }
                                                    }
                                                }

                                                // Get all participants of the sweep
                                                scoreboardParticipants = OotilityCeption.SortEntriesOf(sourceObjective, entities, entities.size());

                                            // Not null lets go
                                            } else {

                                                ArrayList<String> playersOnline = new ArrayList<>();
                                                for (Player plyr : Bukkit.getOnlinePlayers()) { if (plyr == null) { continue; } playersOnline.add(plyr.getName());}

                                                // Get all participants of the sweep
                                                scoreboardParticipants = OotilityCeption.SortEntriesOf(sourceObjective, playersOnline, playersOnline.size());
                                            }

                                            //SCR//OotilityCeption.Log("\u00a7e-\u00a7b-\u00a73-\u00a77 Resultant entries \u00a7b" + scoreboardParticipants.size());
                                            //SCR//try { for (String str : io.lumine.mythic.lib.api.util.ui.SilentNumbers.transcribeList(scoreboardParticipants, (s) -> (s == null ? "null" : ((OrderedScoreboardEntry) s).getEntry() + ", \u00a77Score\u00a7b " + ((OrderedScoreboardEntry) s).getScore() + "\u00a77, Value \u00a7e" + ((OrderedScoreboardEntry) s).getValue()))) { OotilityCeption.Log("\u00a7e:\u00a73:\u00a77 " + str); } } catch (IllegalArgumentException ignored) {}

                                            // Refresh the target
                                            if (tExists) { targetObjective.unregister(); }
                                            targetObjective = targetScoreboard.registerNewObjective(args[3], "dummy", args[3]);

                                            // Log as Feedback
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) {
                                                logReturn.add(OotilityCeption.LogFormat("Scoreboard - Top N","Resulting Order:")); }

                                            // Get the top n yes
                                            int p = 1; Integer lastScore = null; int emergencyEscaper = 0;
                                            for (int i = (scoreboardParticipants.size() - 1); (i >= 0) && (n > 0); i--) { emergencyEscaper++; if (emergencyEscaper > 200) { Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cFatal Loop Error\u00a77 (I guess), Escaped at 200th iteration, seems an infinite loop."); break; }

                                                // N decrease
                                                n--;

                                                // Get obsered participant
                                                OrderedScoreboardEntry obs = scoreboardParticipants.get(i);

                                                // Valid?
                                                if (obs != null) {

                                                    // Get Entry
                                                    String ntry = obs.getEntry();
                                                    //SCR//OotilityCeption.Log("\u00a7e>\u00a7b>\u00a73>\u00a77 " + obs.getEntry() + "\u00a7b " + obs.getScore() + " \u00a7e" + obs.getValue());

                                                    // Valid?
                                                    if (ntry != null) {

                                                        // Valid?
                                                        if (ntry.length() <= 40) {

                                                            // Get Score
                                                            Score qScore = sourceObjective.getScore(ntry);
                                                            int qScored = qScore.getScore();

                                                            // Compare to previous
                                                            if (lastScore != null) {

                                                                // Does it equal the last score? Add a tolerance for the place they occupy and number of used spots
                                                                if (lastScore.equals(qScored)) { p--; n++; } }

                                                            // Aquire last score
                                                            lastScore = qScored;

                                                            // Get target score
                                                            Score fn = targetObjective.getScore(ntry);

                                                            // Set palce
                                                            fn.setScore(p);

                                                            // Log as Feedback
                                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) {
                                                                logReturn.add("  \u00a7e#" + p + " \u00a77" + ntry + " \u00a78(\u00a73" + OotilityCeption.GetEntryScore(sourceObjective, ntry) + "\u00a78)"); }

                                                            // P increase
                                                            p++;
                                                        }
                                                        //SCR// else { OotilityCeption.Log("\u00a7c>\u00a73>\u00a77 Entry too Long"); }
                                                    }
                                                    //SCR// else { OotilityCeption.Log("\u00a7c>\u00a73>\u00a77 Null Entry"); }
                                                }
                                                //SCR// else { OotilityCeption.Log("\u00a7c>\u00a73>\u00a77 Null Ordered Scoreboard Entry"); }
                                            }

                                            // Run Chain
                                            commandChain.chain(chained, (Player) null, sender);
                                        }

                                    // Incorrect number of args
                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("Scoreboard - Top N", "Incorrect usage. For info: \u00a7e/goop scoreboard"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop scoreboard top <objective source> <objective target> <n> [maxswp]");
                                        }
                                    }
                                    break;
                                //endregion
                                //region last
                                case "last":
                                    //   0        1      2           3                4            5       6    args.Length
                                    // /goop scoreboard last <objective source> <objective target> <n> [maxswp]
                                    //   -        0      1           2                3            4       5    args[n]

                                    // Correct number of args?
                                    if (args.length >= 5 && args.length <= 6) {

                                        Objective sourceObjective = targetScoreboard.getObjective(args[2]);
                                        Objective targetObjective = targetScoreboard.getObjective(args[3]);
                                        boolean tExists = true;

                                        // Does the scoreboard objective exist?
                                        if (sourceObjective == null) {
                                            // Failure
                                            failure = true;

                                            // Mention
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Last N", "Scoreboard Objective '\u00a73" + args[2] + "\u00a77' does not exist."));
                                        }

                                        // Does the scoreboard objective exist?
                                        if (targetObjective == null) {
                                            // Target doesnt exist
                                            tExists = false;
                                        }

                                        // Reasonable name
                                        if (args[3].length() > 16 && GooP_MinecraftVersions.GetMinecraftVersion() < 18)  {

                                            // Fail
                                            failure = true;

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Last N", "Output objective name '\u00a73" + args[3] + "\u00a77' must not be longer than 16 characters."));
                                        }

                                        ArrayList<EntityType> et = null;
                                        int maxSWP = -1;
                                        if (args.length >= 6) {

                                            /*
                                             * If it has commas, Entity List
                                             * If it is a number, raw number
                                             * If neither but existing, fail
                                             * If missing, online players
                                             */

                                            if (args[5].contains(",")) {

                                                // Parse entity list
                                                et = new ArrayList<>();

                                                // Split by commas
                                                for (String str : args[5].split(",")) {

                                                    // Parses as Entity List?
                                                    try {

                                                        // Agony
                                                        EntityType type = EntityType.valueOf(str);

                                                        et.add(type);

                                                        //
                                                    } catch (IllegalArgumentException ignored) { }
                                                }

                                            // Parse as entity list
                                            } else if (OotilityCeption.IntTryParse(args[5])) {

                                                // Number
                                                maxSWP = OotilityCeption.ParseInt(args[5]);

                                            // Single Entity
                                            } else {

                                                // Parses as Entity List?
                                                try {

                                                    // Agony
                                                    EntityType type = EntityType.valueOf(args[5]);

                                                    // Might just be players in which there is a quicker way: Leaving it null
                                                    if (type != EntityType.PLAYER) {

                                                        // Create
                                                        et = new ArrayList<>();
                                                        et.add(type);
                                                    }

                                                    // Failure
                                                } catch (IllegalArgumentException ignored) {

                                                    // Fail
                                                    failure = true;

                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Last N", "Expected integer number or EntityType instead of '\u00a73" + args[5] + "\u00a77'"));
                                                }
                                            }
                                        }

                                        // Does n parse?
                                        Integer n = null;
                                        if (OotilityCeption.IntTryParse(args[4])) {
                                            n = OotilityCeption.ParseInt(args[4]);
                                        } else {

                                            // Fail
                                            failure = true;

                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Last N", "Expected integer number for \u00a7e\u00a7on\u00a77 instead of '\u00a73" + args[4] + "\u00a77'"));
                                        }

                                        // Bice sintax
                                        if (!failure) {

                                            // Get all participants
                                            ArrayList<OrderedScoreboardEntry> scoreboardParticipants;

                                            // Prepare entries override
                                            if (maxSWP > 0) {

                                                // Get all participants of the sweep
                                                scoreboardParticipants = OotilityCeption.SortEntriesOf(sourceObjective, null, maxSWP);

                                            // Et not null?
                                            } else if (et != null) {

                                                // UUIDs
                                                ArrayList<String> entities = new ArrayList<>();

                                                // Adds players as names
                                                if (et.contains(EntityType.PLAYER)) {
                                                    et.remove(EntityType.PLAYER);
                                                    for (Player plyr : Bukkit.getOnlinePlayers()) {
                                                        if (plyr == null) { continue; } entities.add(plyr.getName()); } }

                                                // All loaded entities of thay type
                                                for (World wrdl : Bukkit.getWorlds()) {

                                                    // Add all loaded entities of type!
                                                    for (Chunk chk : wrdl.getLoadedChunks()) {

                                                        // Get entities
                                                        for (Entity ent : chk.getEntities()) {

                                                            // Type contained?
                                                            if (et.contains(ent.getType())) {

                                                                // Add UUID
                                                                entities.add(ent.getUniqueId().toString());
                                                            }
                                                        }
                                                    }
                                                }

                                                // Get all participants of the sweep
                                                scoreboardParticipants = OotilityCeption.SortEntriesOf(sourceObjective, entities, entities.size());

                                            // Not null lets go
                                            } else {

                                                ArrayList<String> playersOnline = new ArrayList<>();
                                                for (Player plyr : Bukkit.getOnlinePlayers()) { if (plyr == null) { continue; } playersOnline.add(plyr.getName());}

                                                // Get all participants of the sweep
                                                scoreboardParticipants = OotilityCeption.SortEntriesOf(sourceObjective, playersOnline, playersOnline.size());
                                            }

                                            // Refresh the target
                                            if (tExists) { targetObjective.unregister(); }
                                            targetObjective = targetScoreboard.registerNewObjective(args[3], "dummy", args[3]);

                                            // Get the top n yes
                                            int p = 1; Integer lastScore = null; int emergencyEscaper = 0;
                                            for (int i = 0; (i < scoreboardParticipants.size()) && (n > 0); i++) { emergencyEscaper++; if (emergencyEscaper > 200) { Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cFatal Loop Error\u00a77 (I guess), Escaped at 200th iteration, seems an endless loop."); break; }

                                                // N decrease
                                                n--;

                                                // Get obsered participant
                                                OrderedScoreboardEntry obs = scoreboardParticipants.get(i);

                                                // Valid?
                                                if (obs != null) {

                                                    // Get Entry
                                                    String ntry = obs.getEntry();

                                                    // Valid?
                                                    if (ntry != null) {

                                                        // Valid?
                                                        if (ntry.length() <= 40) {

                                                            // Get Score
                                                            Score qScore = sourceObjective.getScore(ntry);
                                                            int qScored = qScore.getScore();

                                                            // Compare to previous
                                                            if (lastScore != null) {

                                                                // Does it equal the last score? Add a tolerance for the place they occupy and number of used spots
                                                                if (lastScore.equals(qScored)) { p--; n++; }
                                                            }

                                                            // Aquire last score
                                                            lastScore = qScored;

                                                            // Get target score
                                                            Score fn = targetObjective.getScore(ntry);

                                                            // Set palce
                                                            fn.setScore(p);

                                                            // P increase
                                                            p++;
                                                        }
                                                    }
                                                }
                                            }

                                            // Run Chain
                                            commandChain.chain(chained, (Player) null, sender);
                                        }

                                        // Incorrect number of args
                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("Scoreboard - Last N", "Incorrect usage. For info: \u00a7e/goop scoreboard"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop scoreboard last <objective source> <objective target> <n> [maxswp]");
                                        }
                                    }
                                    break;
                                //endregion
                                //region range
                                case "range":
                                    //   0        1      2       3       4           5      args.Length
                                    // /goop scoreboard range <entry> <objective> <range>
                                    //   -        0      1       2       3           4      args[n]

                                    // Correct number of args?
                                    if (args.length == 5) {

                                        // Attempt to get players
                                        ArrayList<Player> players = OotilityCeption.GetPlayers(senderLocation, args[2], null);

                                        // Range valid?
                                        QuickNumberRange qnr = QuickNumberRange.FromString(args[4]);

                                        // Source objective exists
                                        Objective sourceObjective = targetScoreboard.getObjective(args[3]);

                                        // Does the scoreboard objective exist?
                                        if (sourceObjective == null) {
                                            // Failure
                                            failure = true;

                                            // Mention
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Range", "Objective '\u00a73" + args[3] + "\u00a77' does not exist."));
                                        }

                                        // Does the scoreboard objective exist?
                                        if (qnr == null) {

                                            // Failure
                                            failure = true;

                                            // Mention
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Range", "Invalid range '\u00a73" + args[4] + "\u00a77', expected a numeric range like \u00a7e0..10"));

                                        }

                                        // Bice sintax
                                        if (!failure) {

                                            // As each player
                                            if (players.size() > 0) {

                                                // For every player
                                                for (Player target : players) {

                                                    // Get entry score ig
                                                    int score = OotilityCeption.GetPlayerScore(sourceObjective, target);

                                                    // Check entry score
                                                    if (qnr.InRange(score)) {

                                                        // Log as Feedback
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) {
                                                            logReturn.add(OotilityCeption.LogFormat("Scoreboard - Range","Player '\u00a73" + target.getName() + "\u00a77's score for " + sourceObjective.getName() + " was \u00a7b'" + score + "\u00a77' which is \u00a7ain desired range " + args[4] + "\u00a77.")); }

                                                        // Run Chain
                                                        commandChain.chain(chained, target, sender);

                                                    } else {

                                                        // Send fail message
                                                        if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, target.getPlayer(), target.getPlayer(), null, null))); }

                                                        // Mention
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Range", "Player '\u00a73" + target.getName() + "\u00a77's score for " + sourceObjective.getName() + " was \u00a7b'" + score + "\u00a77' which is \u00a7c not in desired range " + args[4] + "\u00a77."));
                                                    }
                                                }

                                            // As pure entry
                                            } else {

                                                // Get entry score ig
                                                int score = OotilityCeption.GetEntryScore(sourceObjective, args[2]);

                                                // Check entry score
                                                if (qnr.InRange(score)) {

                                                    // Log as Feedback
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) {
                                                        logReturn.add(OotilityCeption.LogFormat("Scoreboard - Range","Entry '\u00a73" + args[2] + "\u00a77' of objective " + sourceObjective.getName() + " had a score of \u00a7b'" + score + "\u00a77' which is \u00a7ain desired range " + args[4] + "\u00a77.")); }

                                                    // Run Chain
                                                    commandChain.chain(chained, (Player) null, sender);

                                                } else {

                                                    // Mention
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Range", "Entry '\u00a73" + args[2] + "\u00a77' of objective " + sourceObjective.getName() + " had a score of \u00a7b'" + score + "\u00a77' which is \u00a7c not in desired range " + args[4] + "\u00a77."));
                                                }
                                            }
                                        }

                                    // Incorrect number of args
                                    } else {

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("Scoreboard - Range", "Incorrect usage. For info: \u00a7e/goop scoreboard"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop scoreboard range <entry> <objective> <range>");
                                        }
                                    }
                                    break;
                                //endregion
                                //region damageTakenLink
                                case "damagetakenlink":
                                    //   0      1           2                3             4            5             6         args.Length
                                    // /goop mythicmobs damagetakenlink <entity uuid> <objective> [players only] [dont delete]
                                    //   -      0           1                2             3            4             5         args[n]

                                    // Correct number of args
                                    if (args.length == 4 || args.length == 5 || args.length == 6) {

                                        // Try to find entity
                                        ArrayList<Entity> targets = new ArrayList<>();
                                        ArrayList<Player> pTargets = OotilityCeption.GetPlayers(senderLocation, args[2], null);
                                        Entity tEntity = OotilityCeption.getEntityByUniqueId(args[2]);
                                        if (tEntity != null && !(tEntity instanceof Player)) { targets.add(tEntity); }
                                        for (Player target : pTargets) { targets.add(target); }
                                        if (targets.size()< 1) {
                                            // Fail
                                            failure = true;

                                            // Bruh
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Damage Taken Link", "Entity not found, unable to link."));
                                        }

                                        // Some scoreboards to test
                                        Objective targetObjective = targetScoreboard.getObjective(args[3]);

                                        // Bool
                                        boolean pOnly = false;
                                        if (args.length == 5) {
                                            if (OotilityCeption.BoolTryParse(args[4])) {
                                                pOnly = Boolean.parseBoolean(args[4]);
                                            } else {

                                                // Fail
                                                failure = true;
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Damage Taken Link", "Expected \u00a7etrue \u00a77or \u00a7efalse \u00a77instead of \u00a73" + args[4]));

                                            }
                                        }
                                        // Bool
                                        boolean dDelete = false;
                                        if (args.length == 6) {
                                            if (OotilityCeption.BoolTryParse(args[5])) {
                                                pOnly = Boolean.parseBoolean(args[5]);
                                            } else {

                                                // Fail
                                                failure = true;
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Damage Taken Link", "Expected \u00a7etrue \u00a77or \u00a7efalse \u00a77instead of \u00a73" + args[5]));

                                            }
                                        }

                                        // Bryce Sintax
                                        if (!failure) {

                                            // Refresh the objective
                                            if (targetObjective != null && !dDelete) { targetObjective.unregister(); }
                                            targetObjective = targetScoreboard.registerNewObjective(args[3], "dummy", args[3]);

                                            // FOreach
                                            for (Entity targ : targets) {

                                                // Create link and load it hell yea
                                                ScoreboardLinks.linkNoDupe(new ObjectiveLinkedEntity(targ, ObjectiveLinks.DamageTakenLink, null, targetObjective, pOnly));

                                                // Log Output
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard - Damage Taken Link", "Now tracking damage dealt to \u00a7f" + targ.getName() + "\u00a77 into objective \u00a7e" + args[3]));

                                                Player target = null;
                                                if (targ instanceof Player) { target = (Player) targ; }

                                                // Run Chain
                                                commandChain.chain(chained, target, sender);
                                            }
                                        }
                                    } else if (args.length == 2) {
                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Damage Taken Link, \u00a77Stores damage taken in a scoreboard.");
                                        logReturn.add("\u00a73Usage: \u00a7e/goop scoreboard damageTakenLink <entity uuid> <objective> [players only] [dont delete]");
                                        logReturn.add("\u00a73 - \u00a7e<entity uuid> \u00a77Which entity to link");
                                        logReturn.add("\u00a73 - \u00a7e<objective> \u00a77Name of scoreboard where damage will be stored");
                                        logReturn.add("\u00a73 - \u00a7e[players only] \u00a77Should ignore damage sources from non-players?");
                                        logReturn.add("\u00a73 - \u00a7e[dont delete] \u00a77Wheter to reset the score when this command is run.");

                                    } else {

                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            logReturn.add(OotilityCeption.LogFormat("Scoreboard - Damage Taken Link", "Incorrect usage. For info: \u00a7e/goop scoreboard damageTakenLink"));
                                            logReturn.add("\u00a73Usage: \u00a7e/goop scoreboard damageTakenLink <entity uuid> <objective> [players only]");
                                        }
                                    }

                                    break;
                                //endregion
                                default:
                                    // I have no memory of that shit
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Scoreboard", "'\u00a73" + args[1] + "\u00a77' is not a valid Scoreboard action! do \u00a7e/goop scoreboard\u00a77 for the list of actions."));
                                    break;
                            }


                        } else if (args.length == 1) {
                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Scoreboard Operations, \u00a77Literally why arent these vanilla omg.");
                            logReturn.add("\u00a73Usage: \u00a7e/goop scoreboard {action}");
                            logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                            logReturn.add("\u00a73 --> \u00a7etop <objective source> <objective target> <n> [maxswp]");
                            logReturn.add("\u00a73      * \u00a77Get the top n players from the source objective");
                            logReturn.add("\u00a73 --> \u00a7elast <objective source> <objective target> <n> [maxswp]");
                            logReturn.add("\u00a73      * \u00a77Get the last n players from the source objective");
                            logReturn.add("\u00a73 --> \u00a7erange <entry or players> <objective> <range>");
                            logReturn.add("\u00a73      * \u00a77Check that such entry is in such range.");
                            logReturn.add("\u00a73 --> \u00a7edamagetakenlink <uuid> <objective> [players only] [dont delete]");
                            logReturn.add("\u00a73      * \u00a77Keeps track of how much damage is dealt to an entity.");
                            logReturn.add("\u00a73        \u00a77and who is dealing the damage.");
                            logReturn.add("\u00a73      * \u00a77It will reset the \u00a7eobjective \u00a77unless \u00a7edont delete\u00a7");
                            logReturn.add("\u00a73      * \u00a77is set to \u00a7efalse\u00a77. Will create new if missing.");

                        } else {
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat("Scoreboard", "Incorrect usage. For info: \u00a7e/goop scoreboard"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop scoreboard {action}");
                            }
                        }

                    // No perms
                    } else {
                        // Tell him lmao
                        logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to perform scoreboard operations!"));
                    }
                    break;
                    //endregion
                //region containers
                case containers:
                    //   0      1         2    args.Length
                    // /goop containers {action}
                    //   -      0         1     args[n]

                    // To extract log return
                    // Delegate onto
                    GOOPCCommands.onCommand_GooPContainers(sender, command, label, args, senderLocation, chained, commandChain, chainedNoLocation, logReturnUrn, failMessage);

                    // Extract
                    if (logReturnUrn.getValue() != null) {

                        // For
                        for (String ret : logReturnUrn.getValue()) {

                            // Add
                            logReturn.add(ret);
                        }
                    }
                    break;
                //endregion
                //region grief
                case grief:
                    //   0     1     2       3     4 5 6 7      8            9               10                    11               12          args.Length
                    // /goop grief <b|l> <player> <w x y z> <radius> [asCuboid = false] [pickaxe power] [bedrock break bypass] [reboot key]
                    //   -     0     1       2     3 4 5 6      7            8               9                    10                11          args[n]

                    // Check 5 Permission
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.grief");
                    }

                    // GOt permission?
                    if (permission) {

                        // Chek for args length
                        if (args.length >= 8 && args.length <= 12) {

                            // CHeck for failure
                            boolean failure = false;
                            boolean incBlocks = false, incLiquids = false;
                            ArrayList<Material> matMask = new ArrayList<>();
                            if (args[1].length() > 2) {

                                // Cannot be legacy letters bruh
                                ArrayList<String> griefs = new ArrayList<>();
                                if (args[1].contains(",")) { for(String argGrief : args[1].split(",")) { griefs.add(argGrief); } } else { griefs.add(args[1]); }

                                for (String griefMat : griefs) {
                                    Material m = OotilityCeption.getMaterial(griefMat);
                                    if (m != null) { matMask.add(m); } else {

                                        // Notify the error
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Grief", "Unknown block material '\u00a7e" + griefMat + "\u00a77' "));
                                    }
                                }

                                // Legacy letters fail condition
                                if (matMask.size() == 0) {

                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Grief", "You must specify which materials to break for this command. "));
                                }

                            } else {

                                // Using legacy letters
                                incBlocks = args[1].contains("b");
                                incLiquids = args[1].contains("l");

                                // Legacy letters fail condition
                                if (!incBlocks && !incLiquids) {
                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Grief", "You must specify if mining blocks (\u00a7bb\u00a77), liquids (\u00a7bl\u00a77), or both (\u00a7bbl\u00a77)!"));
                                }
                            }

                            // Gets that player boi
                            ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[2], null);
                            if (targets.size() < 1) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Grief", "Target must be an online player!"));
                            }

                            // Make Error Messager
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Radius is an integer
                            if (!OotilityCeption.IntTryParse(args[7])) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Grief", "Radius must be an integer number."));
                            }

                            // If args long enough
                            if (args.length >= 9) {

                                // Parse cuboid
                                if (!OotilityCeption.BoolTryParse(args[8])) {
                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Grief", "Expected \u00a7btrue \u00a77or \u00a7bfalse \u00a77instead of \u00a73" + args[8]));
                                }
                            }

                            // If args long enough
                            int pickPower = 4;
                            if (args.length >= 10) {

                                // Parse cuboid
                                if (!OotilityCeption.IntTryParse(args[9])) {
                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Grief", "Pickaxe Power must be an integer number."));

                                    // Parse
                                } else {

                                    pickPower = OotilityCeption.ParseInt(args[9]);
                                }
                            }

                            // Should it bypass even though gamerule?
                            boolean bedrockBypass = false;
                            if (args.length >= 11) {

                                // Parse boolean
                                bedrockBypass = args[10].equals("true");
                            }

                            String rebootKey = null;
                            if (args.length >= 12) {

                                // Get key
                                rebootKey = args[11];
                            }

                            // Foreach player
                            for (Player target : targets) {

                                //region Gets that location boi
                                Location targetLocation;

                                // Get
                                targetLocation = OotilityCeption.ValidLocation(target, args[3], args[4], args[5], args[6], logAddition);

                                // Ret
                                if (targetLocation == null) {
                                    failure = true;
                                }

                                // Add Log
                                if (logAddition.GetValue() != null) {
                                    logReturn.add(OotilityCeption.LogFormat("Grief", logAddition.GetValue()));
                                }
                                //endregion

                                if (!failure) {

                                    // Parse the success
                                    int radii = Math.abs(OotilityCeption.ParseInt(args[7]));
                                    boolean asCuboid = false;
                                    if (args.length >= 9) {
                                        asCuboid = Boolean.parseBoolean(args[8]);
                                    }

                                    // Get BLocks
                                    ArrayList<Block> bkks = new ArrayList<>();

                                    // Triple Integral over Cartesian Co-Ordinates
                                    for (int xRl = (targetLocation.getBlockX() - radii); xRl <= (targetLocation.getBlockX() + radii); xRl++) {
                                        for (int yRl = (targetLocation.getBlockY() - radii); yRl <= (targetLocation.getBlockY() + radii); yRl++) {
                                            for (int zRl = (targetLocation.getBlockZ() - radii); zRl <= (targetLocation.getBlockZ() + radii); zRl++) {

                                                // Ignore if outside the world
                                                if ((GooP_MinecraftVersions.GetMinecraftVersion() < 18 && yRl >= 0 && yRl <= 255) || (GooP_MinecraftVersions.GetMinecraftVersion() >= 18 && yRl >= -64 && yRl <= 319)) {
                                                    double range = Math.sqrt(Math.pow(xRl - targetLocation.getBlockX(), 2) + Math.pow(yRl - targetLocation.getBlockY(), 2) + Math.pow(zRl - targetLocation.getBlockZ(), 2));

                                                    // Ignore if outside the sphere region
                                                    if (asCuboid || (radii >= range)) {

                                                        // Get such block from world
                                                        Block tBlock = targetLocation.getWorld().getBlockAt(xRl, yRl, zRl);

                                                        // Include in array
                                                        if (!OotilityCeption.IsAirNullAllowed(tBlock)) {

                                                            // Going by BL keywords
                                                            if (incLiquids || incBlocks) {

                                                                // If liquid
                                                                if (tBlock.isLiquid() && incLiquids) {

                                                                    // Add as liquid
                                                                    bkks.add(tBlock);
                                                                }

                                                                // If solid
                                                                if (!tBlock.isLiquid() && incBlocks) {

                                                                    // Bedrock-type block
                                                                    boolean canBreakAsBedrock = Gunging_Ootilities_Plugin.griefBreaksBedrock || bedrockBypass || tBlock.getType() != Material.BEDROCK;
                                                                    boolean unbreakableAtAll =
                                                                            tBlock.getType() == Material.END_PORTAL_FRAME ||
                                                                                    tBlock.getType() == Material.END_PORTAL ||
                                                                                    tBlock.getType() == Material.COMMAND_BLOCK ||
                                                                                    tBlock.getType() == Material.CHAIN_COMMAND_BLOCK ||
                                                                                    tBlock.getType() == Material.REPEATING_COMMAND_BLOCK ||
                                                                                    tBlock.getType() == Material.STRUCTURE_BLOCK ||
                                                                                    tBlock.getType() == Material.STRUCTURE_VOID ||
                                                                                    tBlock.getType() == Material.BARRIER ||
                                                                                    tBlock.getType() == Material.END_GATEWAY;

                                                                    if (canBreakAsBedrock && !unbreakableAtAll) {

                                                                        // Add as solid
                                                                        bkks.add(tBlock);
                                                                    }
                                                                }

                                                            // Going by material mask
                                                            } else {

                                                                // Add if contained
                                                                if (matMask.contains(tBlock.getType())) { bkks.add(tBlock); }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // If any
                                    if (bkks.size() > 0) {

                                        // Compute tool
                                        ItemStack t0l;
                                        switch (pickPower) {
                                            default:
                                                t0l = new ItemStack(Material.STONE);
                                                break;
                                            case -1:
                                                t0l = new ItemStack(Material.AIR);
                                                break;
                                            case 1:
                                                t0l = new ItemStack(Material.WOODEN_PICKAXE);
                                                break;
                                            case 2:
                                                t0l = new ItemStack(Material.STONE_PICKAXE);
                                                break;
                                            case 3:
                                                t0l = new ItemStack(Material.IRON_PICKAXE);
                                                break;
                                            case 4:
                                                t0l = new ItemStack(Material.DIAMOND_PICKAXE);
                                                break;
                                            case 5:
                                                t0l = new ItemStack(GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE));
                                                break;
                                        }

                                        // GOd damn run event
                                        GooPGriefEvent evG = new GooPGriefEvent(target, bkks, t0l, rebootKey);
                                        Bukkit.getPluginManager().callEvent(evG);

                                        // Notify the success
                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback)
                                            logReturn.add(OotilityCeption.LogFormat("Grief", "Forced player \u00a73" + target.getName() + "\u00a77 to mine/dry \u00a7e" + bkks.size() + "\u00a77 blocks."));

                                        // Run Chain
                                        commandChain.chain(chained, target, sender);

                                    } else {

                                        if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, target.getPlayer(), target.getPlayer(), null, null))); }

                                        // lol found no
                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback)
                                            logReturn.add(OotilityCeption.LogFormat("Grief", "No blocks were found as to force player \u00a73" + target.getName() + "\u00a77 to mine/dry them."));
                                    }
                                }
                            }

                        } else if (args.length == 1) {
                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Grief, \u00a77Simulates a player breaking blocks manually.");
                            logReturn.add("\u00a73Usage: \u00a7e/goop grief <b|l> <player> <w x y z> <radius> [asCuboid] [pickaxe power] [bedrock break] [reboot key]");
                            logReturn.add("\u00a73 - \u00a7e<b|l> \u00a77Remove blocks and/or liquids?");
                            logReturn.add("\u00a73 --> \u00a73b \u00a77Only blocks");
                            logReturn.add("\u00a73 --> \u00a73l \u00a77Only liquids");
                            logReturn.add("\u00a73 --> \u00a73bl \u00a77Both");
                            logReturn.add("\u00a73 --> \u00a77Or the comma separated list of blocks to destroy. ");
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Break the blocks as whom.");
                            logReturn.add("\u00a73 - \u00a7e<w x y z> \u00a77Epicenter of the breaking");
                            logReturn.add("\u00a73 - \u00a7e<radius> \u00a77Size of grief.");
                            logReturn.add("\u00a73 - \u00a7e[asCuboid] \u00a77Instead of a sphere, carve as a cube.");
                            logReturn.add("\u00a73 - \u00a7e[pickaxe power] \u00a77Tier of the pickaxe supposedly used.");
                            logReturn.add("\u00a73 --> \u00a730 \u00a77Fist");
                            logReturn.add("\u00a73 --> \u00a731 \u00a77Wooden/Gold");
                            logReturn.add("\u00a73 --> \u00a732 \u00a77Stone");
                            logReturn.add("\u00a73 --> \u00a733 \u00a77Iron");
                            logReturn.add("\u00a73 --> \u00a734 \u00a77Diamond \u00a78(default)");
                            logReturn.add("\u00a73 --> \u00a735 \u00a77Netherite");
                            logReturn.add("\u00a73 - \u00a7e[bedrock break] \u00a77If the '\u00a73Grief Breaks Bedrock\u00a77' gamerule is disabled, this command won't break vanilla-unbreakable blocks (bedrock, end portal frames...) unless this is set to \u00a7btrue\u00a77.");
                            logReturn.add("\u00a73 - \u00a7e[reboot key] \u00a77This will repair the blocks next time goop reloads. ");
                        } else {
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat("Grief", "Incorrect usage. For info: \u00a7e/goop grief"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop grief <b|l> <player> <w x y z> <radius> [asCuboid] [pickaxePower] [bedrock break] [reboot key]");
                            }
                        }

                        // No perms
                    } else {

                        // Clarify it lack of perms
                        logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to power-grief the terrain!"));
                    }
                    break;
                //endregion
                //region compare
                case compare:
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
                    break;
                //endregion
                //region tell
                case tell:
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
                    break;
                //endregion
                //region tp
                case tp:
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
                    break;
                //endregion
                //region delay
                case delay:
                    //   0    1      2        3...      args.Length
                    // /goop delay <ticks> <command...>
                    //   -    0      1        2...      args[n]

                    // Check 5 Permission
                    if (senderIsPlayer) {
                        // Solid check for permission
                        permission = sender.hasPermission("gunging_ootilities_plugin.delay");
                    }

                    // GOt permission?
                    if (permission) {

                        // Chek for args length
                        if (args.length >= 3) {

                            // CHeck failure
                            boolean failure = false;

                            // CHeck for failure
                            Integer ticks2Wait = null;
                            if (OotilityCeption.IntTryParse(args[1])) {

                                // Parse
                                ticks2Wait = OotilityCeption.ParseInt(args[1]);

                            // Otherwise failure
                            } else {

                                // Fail
                                failure = true;

                                // Notify
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat("Delay", "Expected integer number of ticks instead of \u00a73" + args[1])); }

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

                                // Run
                                (new BukkitRunnable() {
                                    public void run() {

                                        // Just run I guess
                                        OotilityCeption.SendConsoleCommand(cmddd, null, null, null);
                                    }

                                }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), ticks2Wait);
                            }

                        } else if (args.length == 1) {
                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Delay, \u00a77Execute a command after some ticks");
                            logReturn.add("\u00a73Usage: \u00a7e/goop delay <ticks> <command>");
                            logReturn.add("\u00a73 - \u00a7e<ticks> \u00a77Ticks to wait, 1 sec = 20 ticks");
                            logReturn.add("\u00a73 - \u00a7e<command> \u00a77Command to execute");

                        } else {
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat("Delay", "Incorrect usage. For info: \u00a7e/goop delay"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop delay <ticks> <command>");
                            }
                        }

                    // No perms
                    } else {

                        // Clarify it lack of perms
                        logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oWait a moment to use this command."));
                    }
                    break;
                //endregion
                //region chance
                case chance:
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
                    break;
                //endregion
                //region vault
                case vault:
                    //   0      1        2      args.Length
                    // /goop vault {action}
                    //   -      0        1      args[n]

                    // FFS Check for MMOItems
                    if (Gunging_Ootilities_Plugin.foundVault) {

                        // Check 5 Permission
                        if (senderIsPlayer) {
                            // Solid check for permission
                            permission = sender.hasPermission("gunging_ootilities_plugin.vault");
                        }

                        // Got permission?
                        if (permission) {
                            if (args.length >= 2) {
                                // Some bool to know if failure
                                boolean failure = false;
                                ArrayList<OfflinePlayer> targets = null;

                                switch (args[1].toLowerCase()) {
                                    //region charge
                                    case "charge":
                                        //   0      1     2       3       4         args.Length
                                        // /goop vault charge <player> <amount>
                                        //   -      0     1       2       3         args[n]

                                        // Git Vault
                                        if (args.length == 4) {

                                            // Gets that player boi
                                            targets = OotilityCeption.GetPlayers(senderLocation, args[2], true, null);

                                            // Does the player exist?
                                            if (targets.size() < 1) {
                                                // Failure
                                                failure = true;

                                                // Notify the error
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Target must be a player!"));
                                            }

                                            // Parse amount?
                                            Double charge = null;
                                            if (OotilityCeption.DoubleTryParse(args[3])) { charge = Double.parseDouble(args[3]); }
                                            if (charge < 0) { charge = null; }
                                            if (charge == null) {

                                                // Failure
                                                failure = true;

                                                // Notify the error
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Expected positive numeric value instead of \u00a73" + args[3]));
                                            }

                                            // Valid Sintax
                                            if (!failure) {

                                                // Execute for each player
                                                for (OfflinePlayer target : targets) {

                                                    // Get this' eco
                                                    double balance = GooPVault.GetPlayerBalance(target);

                                                    // Enough?
                                                    if (balance >= charge) {

                                                        // Charge I guess
                                                        GooPVault.Withdraw(target, charge);

                                                        // Run Chain
                                                        commandChain.chain(chained, target.getPlayer(), sender);

                                                        // Say that
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Charged \u00a7e" + charge + " to player \u00a73" + target.getName() + "\u00a77, they now have \u00a7e" + GooPVault.GetPlayerBalance(target)));

                                                    } else {

                                                        // Say that
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Player \u00a73" + target.getName() + "\u00a77 doesnt have enough money!"));

                                                        // Notify
                                                        if (failMessage != null && target.isOnline()) { target.getPlayer().sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage.replace("%charge%", String.valueOf(charge)), target.getPlayer(), target.getPlayer(), null, null))); }
                                                    }
                                                }
                                            }

                                        // Incorrect number of args
                                        } else {

                                            // Notify
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                logReturn.add(OotilityCeption.LogFormat("Vault - Charge", "Incorrect usage. For info: \u00a7e/goop vault"));
                                                logReturn.add("\u00a73Usage: \u00a7e/goop vault charge <player> <amount>");
                                            }
                                        }

                                        break;
                                    //endregion
                                    //region balancecheck
                                    case "checkbalance":
                                        //   0      1     2             3       4         5         6       args.Length
                                        // /goop vault checkbalance <player> <range> [objective] [±][score][%]
                                        //   -      0     1             2       3         4         5       args[n]
                                        argsMinLength = 4;
                                        argsMaxLength = 6;
                                        usage = "/goop vault checkbalance <player> <range> [objective] [±][score][%]";
                                        subcommand = "Check Balance";
                                        subcategory = "Vault - Check Balance";

                                        // Help form?
                                        if (args.length == 2)  {

                                            logReturn.add("\u00a7e______________________________________________");
                                            logReturn.add("\u00a73Vault - \u00a7b" + subcommand + ",\u00a77 Check the balance of players.");
                                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                                            logReturn.add("\u00a73 - \u00a7e<range> \u00a77Balance range by which command succeeds.");
                                            logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Scoreboard to output the result.");
                                            logReturn.add("\u00a73 - \u00a7e[±][score][%] \u00a77Scoreboard operation if command succeeds.");
                                            logReturn.add("\u00a73      * \u00a7bamount\u00a77 keyword to set the score to the player's balance.");

                                            // Correct number of args?
                                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                            // Gets that player boi
                                            targets = OotilityCeption.GetPlayers(senderLocation, args[2], true, null);

                                            // Does the player exist?
                                            if (targets.size() < 1) {
                                                // Failure
                                                failure = true;

                                                // Notify the error
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target must be a player!"));
                                            }

                                            // Parse amount?
                                            QuickNumberRange charge = null;
                                            charge = QuickNumberRange.FromString(args[3]);
                                            if (charge == null) {

                                                // Failure
                                                failure = true;

                                                // Notify the error
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected a number or numeric range instead of \u00a73" + args[3] + "\u00a77. Ranges are specified with two numbers separated by \u00a7bb\u00a77. Example \u00a7e-4\u00a77 and \u00a7e32.4\u00a77: \u00a7b-4..32.5\u00a77. They are inclusive, and you may not specify either of the bounds (\u00a7b10..\u00a77 will match anything equal or greater than 10)."));
                                            }

                                            Objective objective = null;
                                            PlusMinusPercent scoreOps = null;
                                            boolean asAmount = false;
                                            RefSimulator<String> logAddtion = new RefSimulator<>("");
                                            if (args.length >= 6) {

                                                // Get
                                                objective = OotilityCeption.GetObjective(args[4]);
                                                scoreOps = PlusMinusPercent.GetPMP(args[5], logAddtion);

                                                // Log
                                                if (objective == null) {

                                                    // Fail
                                                    failure = true;

                                                    // Notify the error
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Objective \u00a73" + args[4] + "\u00a77 not found."));
                                                }

                                                // Aequal?
                                                if (args[5].toLowerCase().equals("amount")) {

                                                    asAmount = true;

                                                // PMP
                                                } else if (scoreOps == null) {

                                                    // Fail
                                                    failure = true;

                                                    // Notify the error
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected numeric value, PMP, or \u00a7bamount\u00a77 keyword instead of \u00a7e" + args[5] + "\u00a77."));
                                                }
                                            }

                                            // Valid Sintax
                                            if (!failure) {

                                                // Execute for each player
                                                for (OfflinePlayer target : targets) {

                                                    // Get this' eco
                                                    double balance = GooPVault.GetPlayerBalance(target);

                                                    // Enough?
                                                    if (charge.InRange(balance)) {

                                                        // Edit score
                                                        if (objective != null) {

                                                            // Amount?
                                                            if (asAmount) { scoreOps = new PlusMinusPercent(balance, false, false); }

                                                            // Operate
                                                            OotilityCeption.SetEntryScore(objective, target.getName().toString(), scoreOps);
                                                        }

                                                        // Run Chain
                                                        commandChain.chain(chained, target.getPlayer(), sender);

                                                        // Say that
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Player \u00a73" + target.getName() + "\u00a77 has \u00a7e" + balance + "\u00a77. \u00a7aSuccessfuly\u00a77 in range \u00a73" + args[3]));

                                                    } else {

                                                        // Failure
                                                        if (failMessage != null && target.isOnline()) { target.getPlayer().sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage.replace("%charge%", charge.qrToString()), target.getPlayer(), target.getPlayer(), null, null))); }

                                                        // Say that
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Player \u00a73" + target.getName() + "\u00a77 has \u00a7e" + balance + "\u00a77. \u00a7cNot within\u00a77 range \u00a73" + args[3]));
                                                    }
                                                }
                                            }

                                        // Incorrect number of args
                                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                            // Notify Error
                                            if (args.length >= argsMinLength) {
                                                logReturn.add(OotilityCeption.LogFormat("Vault - " + subcommand, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop vault checkbalance"));

                                            } else {

                                                logReturn.add(OotilityCeption.LogFormat("Vault - " + subcommand, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop vault checkbalance"));
                                            }

                                            // Notify Usage
                                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        }

                                        break;
                                    //endregion
                                    //region operation
                                    case "operation":
                                        //   0      1     2       3       4         args.Length
                                        // /goop vault operation <player> <value>
                                        //   -      0     1       2       3         args[n]

                                        // Git Vault
                                        if (args.length == 4) {

                                            // Gets that player boi
                                            targets = OotilityCeption.GetPlayers(senderLocation, args[2], true, null);

                                            // Does the player exist?
                                            if (targets.size() < 1) {
                                                // Failure
                                                failure = true;

                                                // Notify the error
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Operation", "Target must be a player!"));
                                            }

                                            // Parse amount?
                                            RefSimulator<String> logAddition = new RefSimulator<>("");
                                            PlusMinusPercent charge = PlusMinusPercent.GetPMP(args[3], logAddition);
                                            if (logAddition.getValue() != null) {

                                                // Notify
                                                logReturn.add(OotilityCeption.LogFormat("Vault - Operation", logAddition.getValue()));
                                            }
                                            if (charge == null) {

                                                // Failure
                                                failure = true;
                                            }

                                            // Valid Sintax
                                            if (!failure) {

                                                // Execute for each player
                                                for (OfflinePlayer target : targets) {

                                                    // Get this' eco
                                                    double balance = GooPVault.GetPlayerBalance(target);

                                                    // Charge I guess
                                                    GooPVault.SetPlayerBalance(target, charge);

                                                    // Say that
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Vault - Operation", "Player \u00a73" + target.getName() + "\u00a77 now has a balance of \u00a7e" + GooPVault.GetPlayerBalance(target)));


                                                    // Run Chain
                                                    commandChain.chain(chained, target.getPlayer(), sender);
                                                }
                                            }

                                            // Incorrect number of args
                                        } else {

                                            // Notify
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                logReturn.add(OotilityCeption.LogFormat("Vault - Operation", "Incorrect usage. For info: \u00a7e/goop vault"));
                                                logReturn.add("\u00a73Usage: \u00a7e/goop vault operation <player> [±]<amount>[%]");
                                            }
                                        }

                                        break;
                                    //endregion
                                    default:
                                        // I have no memory of that shit
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Vault", "'\u00a73" + args[1] + "\u00a77' is not a valid Vault action! do \u00a7e/goop vault\u00a77 for the list of actions."));
                                        break;
                                }

                            } else if (args.length == 1) {
                                logReturn.add("\u00a7e______________________________________________");
                                logReturn.add("\u00a73GooP-Vault, \u00a77Related to the third party plugin.");
                                logReturn.add("\u00a73Usage: \u00a7e/goop vault {action}");
                                logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                                logReturn.add("\u00a73 --> \u00a7echarge <player> <amount>");
                                logReturn.add("\u00a73      * \u00a77If the player has enoigh eco, charges this amount.");
                                logReturn.add("\u00a73      * \u00a77If the target doesnt have enough, fails and doesn't do anything.");
                                logReturn.add("\u00a73 --> \u00a7eoperation <player> [±]<amount>[%]");
                                logReturn.add("\u00a73      * \u00a77Performs a PMP Operation on the player's balance.");

                            } else {
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                    logReturn.add(OotilityCeption.LogFormat("Vault", "Incorrect usage. For info: \u00a7e/goop vault"));
                                    logReturn.add("\u00a73Usage: \u00a7e/goop vault {action}");
                                }
                            }

                        // No perms
                        } else {

                            // Tell him lmao
                            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to use vault-related commands!"));
                        }

                    // Vault not installed. Returning
                    } else {

                        // Tell him lmao
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("\u00a77These commands are to be used with the third party plugin \u00a7e\u00a7lVault \u00a77which you dont have installed."));
                    }
                    break;
                //endregion
                //region sudo
                case sudo:
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
                    break;
                //endregion
                //region permission
                case permission:
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
                    break;
                //endregion
                default: break;
            }
        }

        //
        // MESSAGE LOGGING TIME YEET
        //

        // If the caster is a player, it sends messages.
        if (senderIsPlayer) {

            // I guess this works?
            for (String s : logReturn) {
                sender.sendMessage(s);

                if (Gunging_Ootilities_Plugin.devLogging) { if (Gunging_Ootilities_Plugin.devPlayer != null) { Gunging_Ootilities_Plugin.devPlayer.sendMessage(ChatColor.YELLOW + sender.getName() + s); } }
            }

        // Otherwise this B is the console
        } else {

            // Print a line for each thing
            for (String s : logReturn) {
                oots.CLog(s);

                if (Gunging_Ootilities_Plugin.devLogging) { if (Gunging_Ootilities_Plugin.devPlayer != null) { Gunging_Ootilities_Plugin.devPlayer.sendMessage("\u00a76CONSOLE " + s); } }
            }
        }

        return false;
        //return super.onCommand(sender, command, label, args);
    }

    public void onCommand_GooPUnlock(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args, @Nullable Location senderLocation, boolean chained, @Nullable SuccessibleChain commandChain, @NotNull RefSimulator<List<String>> logReturnUrn, @Nullable String failMessage) {
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
    public void onCommand_GooPNBT(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args, @Nullable Location senderLocation, boolean chained, @Nullable SuccessibleChain commandChain, @NotNull RefSimulator<List<String>> logReturnUrn, @Nullable String failMessage) {
        // Has permission?
        boolean permission = true;

        // What will be said to the caster (caster = sender of command)
        List<String> logReturn = new ArrayList<>();

        // Check 5 Permission
        if (sender instanceof Player) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.nbt");
        }

        // Got permission?
        if (permission) {
            if (args.length >= 2) {
                // Some bool to know if failure
                boolean failure = false;

                // Gets the players
                ArrayList<Player> targets = new ArrayList<>();
                Entity asDroppedItem = null;

                // Get the players
                int playerIndex = 2;
                String subsonic = args[1].toLowerCase();
                if (subsonic.endsWith("lore")) { playerIndex++; }
                if (args.length > playerIndex) {

                    // Actually Get
                    targets = OotilityCeption.GetPlayers(senderLocation, args[playerIndex], null);
                    asDroppedItem = OotilityCeption.getEntityByUniqueId(args[playerIndex]);
                }
                if (!(asDroppedItem instanceof Item)) { asDroppedItem = null; }
                StringBuilder successSlots = new StringBuilder();
                Attribute attribute = null;

                // Help Parameters
                int argsMinLength, argsMaxLength;
                String subcommand, subsection, usage;

                // Amount of successes
                int succ = 0;

                switch (subsonic) {
                    //region Rename
                    case "rename":
                        //   0    1      2      3      4    5+       args.Length
                        // /goop nbt rename <player> <slot> <name>
                        //   -    0     1      2       3    4+       args[n]
                        argsMinLength = 5;
                        usage = "/goop nbt rename <player> <slot> <name>";
                        subcommand = "Rename";
                        subsection = "NBT - Rename";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Changes the name of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e<name> \u00a77Name to set to the items.");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength) {

                            // Gets that player boi
                            RefSimulator<String> refAddition = new RefSimulator<>("");

                            // Does the player exist?
                            if (targets.size() < 1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            // Build Lore Line ig
                            String aLoreLine = "";
                            if (!failure) {

                                // Build lore Line out of the remainder args
                                StringBuilder lLine;

                                // Gather initial value
                                lLine = new StringBuilder(args[4]);
                                for (int i = 5; i < args.length; i++) {

                                    // Add, separating with spaces
                                    lLine.append(" ").append(args[i]);
                                }

                                // Parse
                                aLoreLine = lLine.toString();
                                aLoreLine = OotilityCeption.ParseColour(aLoreLine);
                            }

                            if (!failure) {

                                // Copy of finals
                                final String finalALoreLine = aLoreLine;

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(false, true,
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.RenameItem(iSource.getValidOriginal(), finalALoreLine, iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Store scores
                                        null
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Rename funny item
                                if (asDroppedItem != null) { asDroppedItem.setCustomName(OotilityCeption.GetItemName(OotilityCeption.RenameItem(OotilityCeption.FromDroppedItem(asDroppedItem), aLoreLine, refAddition))); }

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString())); }

                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    /*
                    //region revar
                    case "revar":
                    case "revariable":
                        //   0    1    2      3      4           5+       args.Length
                        // /goop nbt revar <player> <slot> <variable=value...;variable=value...>
                        //   -    0    1      2       3         4+       args[n]
                        argsMinLength = 5;
                        usage = "/goop nbt rename <player> <slot> <name>";
                        subcommand = "Rename";
                        subsection = "NBT - Rename";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Changes the name of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e<name> \u00a77Name to set to the items.");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Does the player exist?
                            if (targets.size() < 1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            // Build Lore Line ig
                            NameVariableOperation nvo = null;
                            if (!failure) {

                                // Build lore Line out of the remainder args
                                String aLoreLine = "";
                                StringBuilder lLine;

                                // Gather initial value
                                lLine = new StringBuilder(args[4]);
                                for (int i = 5; i < args.length; i++) {

                                    // Add, separating with spaces
                                    lLine.append(" ").append(args[i]);
                                }

                                // Cooked
                                ArrayList<NameVariable> iOperation = new ArrayList<>();
                                aLoreLine = lLine.toString();

                                // Split into names and vars
                                if (aLoreLine.contains(";")) {

                                    // Split
                                    String[] nVars = aLoreLine.split(";");

                                    // Evaluate
                                    for (String nvr : nVars) {

                                        // Get Nmae
                                        NameVariable baked = OotilityCeption.GetNameVariable(nvr.replace("<&sc>", ";").replace("<$sc>", ";"));

                                        // If valid
                                        if (baked != null) { iOperation.add(baked); }
                                    }

                                // Seems to be simple
                                } else {

                                    // Add if valid
                                    NameVariable bar = OotilityCeption.GetNameVariable(aLoreLine);
                                    if (bar == null) { if (aLoreLine.equals("PAPI")) { iOperation.add(new NameVariable("PAPI", "")); } } else { iOperation.add(bar); }
                                }


                                // Does the player exist?
                                if (iOperation.size() < 1) {
                                    // Failure
                                    failure = true;

                                    // Notify the error
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Must specify at least one variable in the format \u00a73[name_no_spaces]=[text with <$sc> instead of semicolons]\u00a77. To specify more than one variable, make a semicolon separated list."));

                                } else {

                                    // If there is only one and it is the PAPI
                                    if (iOperation.size() == 1 && iOperation.get(0).getIdentifier().equals("PAPI")) {

                                        // Its a just refresh
                                        nvo = new NameVariableOperation();

                                    } else {

                                        // Fill as var replace
                                        nvo = new NameVariableOperation(iOperation);
                                    }
                                }
                            }

                            // Dropped item?
                            if (asDroppedItem != null && !failure) {

                                //Time to get that item stack
                                ItemStack targetItem = OotilityCeption.FromDroppedItem(asDroppedItem);

                                // Any item found?
                                if (!OotilityCeption.IsAirNullAllowed(targetItem)) {

                                    // Get and Update
                                    ItemStack resul = OotilityCeption.RenameItem(targetItem, nvo, null, logAddition);

                                    // If there was success
                                    if (resul != null) {

                                        // Replace Item
                                        OotilityCeption.SetDroppedItemItemStack(asDroppedItem, resul);
                                        succ++;

                                        // Rename Dropped Item lma0
                                        asDroppedItem.setCustomName(OotilityCeption.GetItemName(resul));
                                    }

                                    // Log if exists
                                    if (logAddition != null) {

                                        if (logAddition.GetValue() != null) {

                                            logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.GetValue()));
                                        }
                                    }

                                    // On Success
                                    if (succ > 0) {

                                        // Run Chain
                                        if (chained) {
                                            OotilityCeption.SendAndParseConsoleCommand(asDroppedItem.getUniqueId(), chainedCommand, sender, null, null, null);
                                        }
                                    }
                                }
                            }

                            if (!failure) {

                                // Apply to item

                                // For every player
                                for (Player target : targets) {
                                    failure = false;

                                    //Lets get that inven slot
                                    RefSimulator<String> slotFailure = new RefSimulator<>("");
                                    ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[3], target, slotFailure);

                                    // So, does the slot make no sense?
                                    if (slott.size() == 0) {
                                        // Failure
                                        failure = true;
                                    }

                                    // Log
                                    if (slotFailure.getValue() != null) {
                                        logReturn.add(OotilityCeption.LogFormat(subsection, slotFailure.getValue()));
                                    }

                                    // Bice sintax
                                    if (!failure) {

                                        // For every slot
                                        for (ItemStackSlot oSlot : slott) {

                                            // Get Item
                                            ItemStackLocation tISource = OotilityCeption.getItemFromPlayer(target, oSlot);

                                            // If non-null
                                            if (tISource != null) {

                                                //Time to get that item stack
                                                ItemStack targetItem = tISource.getItem();

                                                // Any item found?
                                                if (!OotilityCeption.IsAirNullAllowed(targetItem)) {

                                                    // Get and Update
                                                    ItemStack resul = OotilityCeption.RenameItem(targetItem, nvo, target, logAddition);

                                                    // Scry Through Lore
                                                    resul = OotilityCeption.RevariabilizeLore(resul, nvo, target);

                                                    // If there was success
                                                    if (resul != null) {

                                                        // Replace Item
                                                        tISource.ReplaceItem(resul);
                                                        succ++;
                                                        if (chained) { OotilityCeption.Slot4Success(successSlots, oSlot, OotilityCeption.comma); }
                                                    }

                                                    // Log if exists
                                                    if (logAddition != null) {

                                                        if (logAddition.GetValue() != null) {

                                                            logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.GetValue()));
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // On Success
                                        if (succ > 0) {

                                            // Run Chain
                                            if (chained) {
                                                chainedCommand = OotilityCeption.ReplaceFirst(chainedCommand, "@t", successSlots.toString());
                                                OotilityCeption.SendAndParseConsoleCommand(target, chainedCommand, sender, null, null, null);
                                            }
                                        } else {

                                            if (failMessage != null) { target.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, target.getPlayer(), target.getPlayer(), null, null))); }
                                        }
                                    }
                                }
                            }


                            // Incorrect number of args
                        } else {

                            // Notify
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage. For info: \u00a7e/goop nbt"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop nbt revariable <player> <slot> <variable=value...>");
                            }
                        }
                        break;
                    //endregion
                    */
                    //region Add Lore
                    case "addlore":
                        //   0    1      2      3       4        5    6+       args.Length
                        // /goop nbt addLore <index> <player> <slot> [fv] <lore line>
                        //   -    0      1       2       3       4    5+       args[n]
                        argsMinLength = 6;
                        usage = "/goop nbt addLore <index> <player> <slot> [fv] <lore...>";
                        subcommand = "Add Lore";
                        subsection = "NBT - Add Lore";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Changes the lore of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<index> \u00a77Where to insert the lore line.");
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) {
                                logReturn.add("\u00a73 - \u00a7e[fv] \u00a77MMOItem lore is targetted by default, if you are editing");
                                logReturn.add("           a MMOItem; Use this to force-target vanilla lore.");
                                logReturn.add("           Vanilla lore changes often undone in MMOItems.");
                            }
                            logReturn.add("\u00a73 - \u00a7e<lore...> \u00a77Lore to add to the items.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Index Parses?
                            Integer iIndex = 0;
                            if (args[2].equalsIgnoreCase("top")) {

                                // Very first entry
                                iIndex = 0;

                                // Alternate keyword parse
                            } else if (args[2].equalsIgnoreCase("bottom")) {

                                // No Leve = last entry
                                iIndex = null;

                                // Does it parse?
                            } else if (OotilityCeption.IntTryParse(args[2])) {

                                // Get that number, straight up
                                iIndex = OotilityCeption.ParseInt(args[2]);

                                // Doesnt work rip
                            } else {

                                // Failure
                                failure = true;

                                // Log
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat(subsection, "Expected index to be an integer number or \u00a7etop\u00a77/\u00a7ebottom\u00a77 keywords instead of '\u00a73" + args[2] + "\u00a77'.")); }
                            }

                            // Does the player exist?
                            if (targets.size() <  1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            boolean fVanilla = false;
                            int loreAdd = 0;
                            if (args.length >= 7) {
                                if (OotilityCeption.BoolTryParse(args[5])) {
                                    fVanilla = OotilityCeption.BoolParse(args[5]);
                                    loreAdd = 1;
                                }
                            }

                            // Parse Lore line
                            String aLoreLine = "";
                            if (!failure) {

                                // Build lore Line out of the remainder args
                                StringBuilder lLine;

                                // Gather initial value
                                lLine = new StringBuilder(args[5 + loreAdd]);
                                for (int i = 6 + loreAdd; i < args.length; i++) {

                                    // Add, separating with spaces
                                    lLine.append(" ").append(args[i]);
                                }
                                aLoreLine = lLine.toString();
                            }

                            if (!failure) {

                                // Copy of finals
                                final String finalALoreLine = aLoreLine;
                                final Integer finalIIndex = iIndex;

                                // Preparation of Methods
                                TargetedItems executor;

                                if (fVanilla) {
                                    executor = new TargetedItems(false, true,
                                            chained, commandChain, sender, failMessage,

                                            // What method to use to process the item
                                            iSource -> OotilityCeption.AppendLoreLineVanilla(iSource.getValidOriginal(), finalALoreLine, iSource.getEntity(), finalIIndex, iSource.getLogAddition()),

                                            // When will it succeed
                                            iSource -> iSource.getResult() != null,

                                            // Store scores
                                            null
                                    );

                                } else {
                                    executor = new TargetedItems(false, true,
                                            chained, commandChain, sender, failMessage,

                                            // What method to use to process the item
                                            iSource -> OotilityCeption.AppendLoreLine(iSource.getValidOriginal(), finalALoreLine, iSource.getEntity(), finalIIndex, iSource.getLogAddition()),

                                            // When will it succeed
                                            iSource -> iSource.getResult() != null,

                                            // Store scores
                                            null
                                    );
                                }

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[4], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString())); }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Remove Lore
                    case "removelore":
                        //   0    1      2         3       4        5         6        args.Length
                        // /goop nbt removeLore <index> <player> <slot> [forcevanilla]
                        //   -    0      1         2       3        4         5        args[n]
                        argsMinLength = 5;
                        argsMaxLength = 6;
                        usage = "/goop nbt removeLore <index> <player> <slot> [fv]";
                        subcommand = "Remove Lore";
                        subsection = "NBT - Remove Lore";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Removes lore from items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<index> \u00a77Which lore line to remove.");
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) {
                                logReturn.add("\u00a73 - \u00a7e[fv] \u00a77MMOItem lore is targetted by default, if you are editing");
                                logReturn.add("           a MMOItem; Use this to force-target vanilla lore.");
                                logReturn.add("           Vanilla lore changes often undone in MMOItems.");
                            }

                            // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            boolean revAll = false;
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Index Parses?
                            Integer iIndex = 0;
                            if (args[2].equalsIgnoreCase("top")) {

                                // Very first entry
                                iIndex = 0;

                                // Alternate keyword parse
                            } else if (args[2].equalsIgnoreCase("bottom")) {

                                // No Leve = last entry
                                iIndex = null;

                                // Alternate keyword parse
                            } else if (args[2].equalsIgnoreCase("all")) {

                                // No Leve = last entry
                                revAll = true;

                                // Does it parse?
                            } else if (OotilityCeption.IntTryParse(args[2])) {

                                // Get that number, straight up
                                iIndex = OotilityCeption.ParseInt(args[2]);

                                // Doesnt work rip
                            } else {

                                // Failure
                                failure = true;

                                // Log
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat(subsection, "Expected index to be an integer number or \u00a7etop\u00a77/\u00a7ebottom\u00a77 keywords instead of '\u00a73" + args[2] + "\u00a77'.")); }
                            }

                            // Does the player exist?
                            if (targets.size() < 1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            boolean fVanilla = false;
                            if (args.length == 6) {
                                if (OotilityCeption.BoolTryParse(args[5])) {
                                    fVanilla = OotilityCeption.BoolParse(args[5]);

                                } else {
                                    // Fail
                                    failure = true;

                                    // Log
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Expected '\u00a7etrue\u00a77' or '\u00a7efalse\u00a77' instead of '\u00a73" + args[5] + "\u00a77'"));
                                }
                            }

                            if (!failure) {

                                // Copy of finals
                                final Integer finalIIndex = iIndex;
                                final boolean finalRevAll = revAll;

                                // Preparation of Methods
                                TargetedItems executor;
                                if (fVanilla) {
                                    executor = new TargetedItems(false, true,
                                            chained, commandChain, sender, failMessage,

                                            // What method to use to process the item
                                            iSource -> OotilityCeption.RemoveLoreLineVanilla(iSource.getValidOriginal(), finalIIndex, finalRevAll, iSource.getLogAddition()),

                                            // When will it succeed
                                            iSource -> iSource.getResult() != null,

                                            // Store scores
                                            null
                                    );

                                } else {
                                    executor = new TargetedItems(false, true,
                                            chained, commandChain, sender, failMessage,

                                            // What method to use to process the item
                                            iSource -> OotilityCeption.RemoveLoreLine(iSource.getValidOriginal(), finalIIndex, finalRevAll, iSource.getLogAddition()),

                                            // When will it succeed
                                            iSource -> iSource.getResult() != null,

                                            // Store scores
                                            null
                                    );
                                }

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[4], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) {
                                    logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString()));
                                }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;

                    //endregion
                    //region Attributes
                    case "mspeed":
                    case "movementspeed":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_MOVEMENT_SPEED); }

                    case "mhealth":
                    case "maxhealth":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_MAX_HEALTH); }

                    case "adamage":
                    case "attackdamage":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_ATTACK_DAMAGE); }

                    case "armour":
                    case "armor":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_ARMOR); }

                    case "atoughness":
                    case "armortoughness":
                    case "armourtoughness":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_ARMOR_TOUGHNESS); }

                    case "aspeed":
                    case "attackspeed":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_ATTACK_SPEED); }

                    case "kresistance":
                    case "knockbackr":
                    case "knockbackresistance":
                    case "kres":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_KNOCKBACK_RESISTANCE); }

                    case "luck":
                        if (attribute == null) { attribute = GooP_MinecraftVersions.GetVersionAttribute(GooPVersionAttributes.GENERIC_LUCK); }

                        //   0    1      2      3      4       5         6       args.Length
                        // /goop nbt mspeed <player> <slot> <value> [objective]
                        //   -    0     1      2       3       4         5       args[n]
                        argsMinLength = 5;
                        argsMaxLength = 6;
                        usage = "/goop nbt " + subsonic + " <player> <slot> [±][value][%] [objective]";
                        subcommand = OotilityCeption.TitleCaseConversion(attribute.toString());
                        subsection = "NBT - " + subcommand;

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Modifies the " + subcommand +  " bonus of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e[±][value][%] \u00a77Amount of attribute to set.");
                            logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Save the result onto the player's score.");
                            logReturn.add("\u00a78Score is the final value miltiplied by 10.");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Some scoreboards to test
                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                            Scoreboard targetScoreboard = manager.getMainScoreboard();
                            Objective targetObjective = null;
                            RefSimulator<Double> scor = null;
                            if (args.length == 6) {
                                targetObjective = targetScoreboard.getObjective(args[5]);
                                scor = new RefSimulator<>(0.0);
                            }

                            // Does the player exist?
                            if (targets.size() < 1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback)
                                    logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            PlusMinusPercent pValue = PlusMinusPercent.GetPMP(args[4], logAddition);
                            if (logAddition.getValue() != null) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue()));
                            }
                            if (pValue == null) {
                                // Failure
                                failure = true;
                            }

                            if (args.length == 6 && targetObjective == null) {
                                // Failure
                                failure = true;

                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback)
                                    logReturn.add(OotilityCeption.LogFormat(subsection, "Scoreboard objective \u00a73" + args[5] + "\u00a77 does not exist."));
                            }

                            // For cummulative score
                            double tScore = 0.0;

                            if (!failure) {

                                // Copy of finals
                                final Objective finalTargetObjective = targetObjective;
                                final boolean useObjective = targetObjective != null;
                                final Attribute finalAttribute = attribute;

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(false, !pValue.isNeutral(),
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.SetAttribute(iSource.getValidOriginal(), pValue, finalAttribute, iSource.getRef_dob_a(), iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Handle score if
                                        (iSource, sInfo) -> {

                                            // If the scoreboard stuff is even active
                                            if (useObjective) {

                                                // Initialize at zero
                                                sInfo.setInitZero(finalTargetObjective);

                                                // Add the individual stat values
                                                sInfo.addScoreboardOpp(finalTargetObjective, iSource.getRef_dob_a().getValue() * 10, true, false);
                                            }

                                            // Succesible Value
                                            sInfo.setValueOfSuccess(iSource.getRef_dob_a().getValue());
                                        }
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) {
                                    logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString()));
                                }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Custom Model Data
                    case "cmodeldata":
                    case "custommodeldata":
                    case "cmdata":
                    case "cmodeld":
                    case "custommd":
                        //   0    1      2          3      4       5         6       args.Length
                        // /goop nbt cmodeldata <player> <slot> <value> [objective]
                        //   -    0     1           2       3       4         5       args[n]
                        argsMinLength = 5;
                        argsMaxLength = 6;
                        usage = "/goop nbt cmodeldata <player> <slot> [±][value][%] [objective]";
                        subcommand = "Custom Model Data";
                        subsection = "NBT - Custom Model Data";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Modifies the CustomModelData value of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e[±][value][%] \u00a77Value of model to set.");
                            logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Save the result onto the player's score.");
                            logReturn.add("\u00a78Score is the final value miltiplied by 10.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Some scoreboards to test
                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                            Scoreboard targetScoreboard = manager.getMainScoreboard();
                            Objective targetObjective = null;
                            RefSimulator<Double> scor = null;
                            if (args.length == 6) {
                                targetObjective = targetScoreboard.getObjective(args[5]);
                                scor = new RefSimulator<>(0.0);
                            }

                            // Does the player exist?
                            if (targets.size() < 1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback)
                                    logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            PlusMinusPercent pValue = PlusMinusPercent.GetPMP(args[4], logAddition);
                            if (logAddition.getValue() != null) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue()));
                            }
                            if (pValue == null) {
                                // Failure
                                failure = true;
                            }

                            if (args.length == 6 && targetObjective == null) {
                                // Failure
                                failure = true;

                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback)
                                    logReturn.add(OotilityCeption.LogFormat(subsection, "Scoreboard objective \u00a73" + args[5] + "\u00a77 does not exist."));
                            }

                            // Correct MC Versions?
                            if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0) {

                                // BRUH
                                failure = true;

                                // Notify the error
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback)
                                    logReturn.add(OotilityCeption.LogFormat(subsection, "\u00a7cCustom Model Data is for Minecraft 1.14+"));

                            }

                            // For cummulative score
                            double tScore = 0.0;

                            if (!failure) {

                                // Copy of finals
                                final Objective finalTargetObjective = targetObjective;
                                final boolean useObjective = targetObjective != null;

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(false, !pValue.isNeutral(),
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.SetCMD(iSource.getValidOriginal(), pValue, iSource.getRef_dob_a(), iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Handle score if
                                        (iSource, sInfo) -> {

                                            // If the scoreboard stuff is even active
                                            if (useObjective) {

                                                // Initialize at zero
                                                sInfo.setInitZero(finalTargetObjective);

                                                // Add the individual stat values
                                                sInfo.addScoreboardOpp(finalTargetObjective, iSource.getRef_dob_a().getValue() * 10, true, false);
                                            }

                                            // Succesible Value
                                            sInfo.setValueOfSuccess(iSource.getRef_dob_a().getValue());
                                        }
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) {
                                    logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString()));
                                }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Durability Damage
                    case "damage":
                    case "durability":
                        //   0    1     2      3       4       5       6        7        8      args.Length
                        // /goop nbt damage <player> <slot> <value> [break] [usemax] [objective]
                        //   -    0     1      2       3       4       5        6        7       args[n]
                        argsMinLength = 5;
                        argsMaxLength = 8;
                        usage = "/goop nbt damage <player> <slot> [±][damage][%] [preventBreak] [useMax] [objective]";
                        subcommand = "Durability Damage";
                        subsection = "NBT - Durability Damage";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Modify durability damage of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e[±][damage][%] \u00a77Damage the item has taken.");
                            logReturn.add("\u00a73 - \u00a7e[preventBreak] \u00a77If the damage exceeds max durability, destroy item?.");
                            logReturn.add("\u00a73 --> \u00a7btrue \u00a77Item will always survive with 1 durability left.");
                            logReturn.add("\u00a73 --> \u00a7bfalse \u00a77Item might break because of this command.");
                            logReturn.add("\u00a73 - \u00a7e[useMax] \u00a77Perform operation based on max durability?");
                            logReturn.add("\u00a73 --> \u00a7btrue \u00a77Damage operation uses the item max durability.");
                            logReturn.add("\u00a73 --> \u00a7bfalse \u00a77Damage operation uses the item current durability.");
                            logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Save the result onto the player's score.");
                            logReturn.add("\u00a78Score is the final value miltiplied by 10.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Boolean to save or keep
                            boolean preventBreaking = true;
                            boolean useMaxDura = false;
                            String scoreboardGit = null;
                            if (args.length >= 6) {
                                //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Optional Arguments");

                                String preventBreakingRaw = args[5];
                                String useMaxDuraRaw = null;

                                // It did not parse, is there a seventh?
                                if (args.length == 7) {

                                    // Maybe it means the max dura
                                    useMaxDuraRaw = args[6];

                                } else if (args.length == 8) {

                                    // Okay we know what it is
                                    useMaxDuraRaw = args[6];

                                    // Take that as the score
                                    scoreboardGit = args[7];
                                }

                                //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Arg Prevent Break:\u00a73 " + preventBreakingRaw);
                                //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Arg Use Max:\u00a73 " + useMaxDuraRaw);
                                //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Arg Score:\u00a73 " + scoreboardGit);

                                // Does it parse as boolean?
                                if (OotilityCeption.BoolTryParse(preventBreakingRaw)) {

                                    // Thats the prevent break
                                    preventBreaking = Boolean.parseBoolean(preventBreakingRaw);
                                    //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Parsed Prevent Break:\u00a7b " + preventBreaking);

                                // If the size is exactly six, the scoreboard may alternatively have been there
                                } else if (args.length == 6) {

                                    // Length is 6 and it is not a bool value, assume it is the coreboard
                                    scoreboardGit = args[5];
                                    //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Transfer to Score:\u00a7b " + scoreboardGit);

                                // It doesnt parse but it is of length 7 or 8, thats error parsing that boolean
                                } else {

                                    // Boolean parse error
                                    failure = true;

                                    // Notify
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Expected \u00a7btrue\u00a77 or \u00a7bfalse\u00a77 instead of \u00a73" + preventBreakingRaw + "\u00a77 for prevent breaking option."));
                                }

                                // Specified max dura raw?
                                if (useMaxDuraRaw != null) {
                                    //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Step Parse\u00a7c " + useMaxDuraRaw);

                                    // Does it parse as boolean?
                                    if (OotilityCeption.BoolTryParse(useMaxDuraRaw)) {

                                        // Thats the use max durability
                                        useMaxDura = Boolean.parseBoolean(useMaxDuraRaw);
                                        //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Parsed Max Dura:\u00a7b " + useMaxDura);

                                        // If the size is exactly seven, the scoreboard may alternatively have been there
                                    } else if (args.length == 7) {

                                        // Length is 6 and it is not a bool value, assume it is the coreboard
                                        scoreboardGit = args[6];
                                        //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Transfer to Score:\u00a7b " + scoreboardGit);

                                    // It doesnt parse but it is of length 8, thats error parsing that boolean
                                    } else {

                                        // Boolean parse error
                                        failure = true;

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Expected \u00a7btrue\u00a77 or \u00a7bfalse\u00a77 instead of \u00a73" + useMaxDuraRaw + "\u00a77 for using max durability option."));
                                    }
                                    //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Step Parse\u00a7a " + useMaxDura);
                                }
                                //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Step Dura\u00a7a " + useMaxDura);
                            }
                            //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Step -2\u00a7a " + useMaxDura);

                            // Some scoreboards to test
                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                            Scoreboard targetScoreboard = manager.getMainScoreboard();
                            Objective targetObjective = null;
                            RefSimulator<Double> scor = null;
                            if (scoreboardGit != null) {
                                targetObjective = targetScoreboard.getObjective(scoreboardGit);
                                scor = new RefSimulator<>(0.0);
                            }
                            //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Step -1\u00a7a " + useMaxDura);

                            // Does the player exist?
                            if (targets.size() <  1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }
                            //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Step 0\u00a7a " + useMaxDura);

                            PlusMinusPercent pValue = PlusMinusPercent.GetPMP(args[4], logAddition);
                            if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue())); }
                            if (pValue == null) {
                                // Failure
                                failure = true;
                            }
                            //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Step 1\u00a7a " + useMaxDura);

                            if (scoreboardGit != null && targetObjective == null) {
                                // Failure
                                failure = true;

                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Scoreboard objective \u00a73" + scoreboardGit + "\u00a77 does not exist."));
                            }

                            // For cummulative score
                            double tScore = 0.0;
                            //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Pre Failure\u00a7a " + useMaxDura);

                            if (!failure) {

                                // Copy of finals
                                final Objective finalTargetObjective = targetObjective;
                                final boolean useObjective = targetObjective != null;
                                boolean finalPreventBreaking = preventBreaking;
                                boolean finalUseMax = useMaxDura;
                                //DUR//OotilityCeption.Log("\u00a78COMMAND\u00a73 DUR\u00a77 Passing Use Max Dura:\u00a72 " + finalUseMax);

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(false, !pValue.isNeutral(),
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.SetDurability(iSource.getValidOriginal(), iSource.getPlayer(), pValue, iSource.getRef_dob_a(), finalPreventBreaking, finalUseMax, iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Handle score if
                                        (iSource, sInfo) -> {

                                            // If the scoreboard stuff is even active
                                            if (useObjective) {

                                                // Initialize at zero
                                                sInfo.setInitZero(finalTargetObjective);

                                                // Add the individual stat values
                                                sInfo.addScoreboardOpp(finalTargetObjective, iSource.getRef_dob_a().getValue() * 10, true, false);
                                            }

                                            // Succesible Value
                                            sInfo.setValueOfSuccess(iSource.getRef_dob_a().getValue());
                                        }
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString())); }

                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Enchantment
                    case "enchant":
                    case "enchantment":
                    case "enchantments":
                        //   0    1         2        3      4       5      6         7       args.Length
                        // /goop nbt enchantment <player> <slot> <ench> <value> [objective]
                        //   -    0         1        2      3       4      5         6       args[n]
                        argsMinLength = 6;
                        argsMaxLength = 7;
                        usage = "/goop nbt enchantment <player> <slot> <enchantment> [±][level][%] [objective]";
                        subcommand = "Enchantments";
                        subsection = "NBT - Enchantments";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Modify durability damage of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e<enchantment> \u00a77Enchantment to apply.");
                            logReturn.add("\u00a73 --> \u00a7ball \u00a77Keyword to target every enchantment.");
                            logReturn.add("\u00a73 - \u00a7e[±][level][%] \u00a77Enchantment level to set.");
                            logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Save the final ench level onto the player's score.");
                            logReturn.add("\u00a78Score is the final value miltiplied by 10.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Some scoreboards to test
                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                            Scoreboard targetScoreboard = manager.getMainScoreboard();
                            Objective targetObjective = null;
                            RefSimulator<Integer> scor = null;
                            if (args.length == 7) {
                                targetObjective = targetScoreboard.getObjective(args[6]);
                                scor = new RefSimulator<>(0);
                            }

                            // Does the player exist?
                            if (targets.size() <  1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            // Enchantment exsts?
                            Enchantment tEnch = null;
                            if (!args[4].toLowerCase().equals("all")) {

                                // If it is not 'all', get target
                                tEnch = OotilityCeption.GetEnchantmentByName(args[4]);

                                // If it wasn't found
                                if (tEnch == null) {

                                    // Failure
                                    failure = true;

                                    // log
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { logReturn.add(OotilityCeption.LogFormat(subsection, "Enchantment '\u00a73" + args[4] + "\u00a77' does not exist. Remember to use vanilla names.")); }
                                }
                            }

                            PlusMinusPercent pValue = PlusMinusPercent.GetPMP(args[5], logAddition);
                            if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue())); }
                            if (pValue == null) {
                                // Failure
                                failure = true;
                            }

                            if (args.length == 7 && targetObjective == null) {
                                // Failure
                                failure = true;

                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Scoreboard objective \u00a73" + args[6] + "\u00a77 does not exist."));
                            }

                            // For cummulative score
                            int tScore = 0;

                            if (!failure) {

                                // Copy of finals
                                final Objective finalTargetObjective = targetObjective;
                                final boolean useObjective = targetObjective != null;
                                final ArrayList<Enchantment> enchs = new ArrayList<>();
                                if (tEnch == null) { enchs.addAll(Arrays.asList(Enchantment.values())); } else { enchs.add(tEnch); }

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(false, !pValue.isNeutral(),
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.EnchantmentOperation(iSource.getValidOriginal(), enchs, pValue, iSource.getRef_int_a(), iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Handle score if
                                        (iSource, sInfo) -> {

                                            // If the scoreboard stuff is even active
                                            if (useObjective) {

                                                // Initialize at zero
                                                sInfo.setInitZero(finalTargetObjective);

                                                // Add the individual stat values
                                                sInfo.addScoreboardOpp(finalTargetObjective, iSource.getRef_int_a().getValue() * 10, true, false);
                                            }

                                            // Succesible Value
                                            sInfo.setValueOfSuccess(iSource.getRef_int_a().getValue());
                                        }
                                    );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) {
                                    logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString()));
                                }
                            }


                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Morph Material
                    case "setmaterial":
                        //   0    1      2          3       4       5       args.Length
                        // /goop nbt setmaterial <player> <slot> <material>
                        //   -    0      1          2       3       4       args[n]
                        argsMinLength = 5;
                        argsMaxLength = 5;
                        usage = "/goop nbt setMaterial <player> <slot> <material>";
                        subcommand = "Set Material";
                        subsection = "NBT - Set Material";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Modify durability damage of items.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e<material> \u00a77Material to which transform this item.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi

                            // Does the player exist?
                            if (targets.size() <  1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            // Is it a real material?
                            Material mat = null;
                            try {
                                // Yes, it seems to be
                                mat = Material.valueOf(args[4].toUpperCase());

                                // Well it is supported then

                                // Not recognized
                            } catch (IllegalArgumentException ex) {

                                // Not recognized
                                failure = true;

                                // Notify the error
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "That is not a valid material!"));

                            }

                            if (!failure) {

                                // Copy of finals
                                Material finalMat = mat;

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(false, true,
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.SetMaterial(iSource.getValidOriginal(), finalMat, iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Handle score if
                                        null
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString())); }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Amount
                    case "amount":
                        //   0    1    2      3      4       5         6       args.Length
                        // /goop nbt amount <player> <slot> <value> [objective]
                        //   -    0    1      2       3       4         5       args[n]
                        argsMinLength = 5;
                        argsMaxLength = 6;
                        usage = "/goop nbt amount <player> <slot> [±][amount][%] [objective]";
                        subcommand = "Amount";
                        subsection = "NBT - Amount";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Modify the amount of items in this stack.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e[±][amount][%] \u00a77Amount of items to set.");
                            logReturn.add("\u00a73 - \u00a7e[objective] \u00a77Save the final ench level onto the player's score.");
                            logReturn.add("\u00a78Score is the final value miltiplied by 10.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Some scoreboards to test
                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                            Scoreboard targetScoreboard = manager.getMainScoreboard();
                            Objective targetObjective = null;
                            RefSimulator<Double> scor = null;
                            if (args.length == 6) {
                                targetObjective = targetScoreboard.getObjective(args[5]);
                                scor = new RefSimulator<>(0.0);
                            }

                            // Does the player exist?
                            if (targets.size() <  1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            PlusMinusPercent pValue = PlusMinusPercent.GetPMP(args[4], logAddition);
                            if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue())); }
                            if (pValue == null) {
                                // Failure
                                failure = true;
                            }

                            if (args.length == 6 && targetObjective == null) {
                                // Failure
                                failure = true;

                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Scoreboard objective \u00a73" + args[5] + "\u00a77 does not exist."));
                            }

                            // For cummulative score
                            double tScore = 0.0;

                            if (!failure) {

                                // Copy of finals
                                final Objective finalTargetObjective = targetObjective;
                                final boolean useObjective = targetObjective != null;

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(false, !pValue.isNeutral(),
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.SetAmount(iSource.getValidOriginal(), pValue, iSource.getRef_dob_a(), iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Handle score if
                                        (iSource, sInfo) -> {

                                            // Re-set amount .-.
                                            iSource.getResult().setAmount(OotilityCeption.RoundToInt(iSource.getRef_dob_a().getValue()));

                                            // If the scoreboard stuff is even active
                                            if (useObjective) {

                                                // Initialize at zero
                                                sInfo.setInitZero(finalTargetObjective);

                                                // Add the individual stat values
                                                sInfo.addScoreboardOpp(finalTargetObjective, iSource.getRef_dob_a().getValue() * 10, true, false);
                                            }

                                            // Succesible Value
                                            sInfo.setValueOfSuccess(iSource.getRef_dob_a().getValue());
                                        }
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString())); }
                            }


                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Set Item
                    case "setitem":
                        //   0    1      2      3       4       5 6 7           8          args.Length
                        // /goop nbt setitem <player> <slot> {nbt string} [±][amount][%]
                        //   -    0     1      2        3       4 5 6           7          args[n]
                        argsMinLength = 7;
                        argsMaxLength = 8;
                        usage = "/goop nbt setItem <player> <slot> {nbt} [±][amount][%]";
                        subcommand = "Set Item";
                        subsection = "NBT - Set Item";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Change the item in this stack.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot of the target item.");
                            logReturn.add("\u00a73 - \u00a7e{nbt} \u00a77These are the formats that match your plugins:");
                            logReturn.add("\u00a73 --> \u00a7ee <enchantment name> <level> \u00a77Tests for an enchantment.");
                            logReturn.add("\u00a73 --> \u00a7ev <material> * \u00a77Tests for a vanilla item.");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) { logReturn.add("\u00a73 --> \u00a7em <mmoitem type> <mmoitem id> \u00a77Tests for it being a precise mmoitem."); }
                            logReturn.add("\u00a73 - \u00a7e[±][amount][%] \u00a77Edit the amount of items in the stack, too.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Does the player exist?
                            if (targets.size() <  1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            // Valid NBT String?
                            ItemStack tSource = null; logAddition.setValue("");
                            if (OotilityCeption.IsInvalidItemNBTtestString(args[4], args[5], args[6], logAddition)) {

                                // Fail lol
                                failure = true;

                                // Log
                                if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue())); }

                                // Why it says it is valid
                            } else {

                                // Git
                                tSource = OotilityCeption.ItemFromNBTTestString(args[4], args[5], args[6], "1", logAddition);

                                // Null?
                                if (tSource == null) {

                                    // Failure
                                    failure = true;

                                    // Log
                                    if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue())); }
                                }
                            }

                            // Check PMP
                            PlusMinusPercent pValue;
                            if (args.length == 8) {
                                pValue = PlusMinusPercent.GetPMP(args[7], logAddition);
                                if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue())); }
                                if (pValue == null) {
                                    // Failure
                                    failure = true;
                                }

                            } else {
                                pValue = new PlusMinusPercent(1.0, false, false);
                            }

                            if (!failure) {

                                // Copy of finals
                                final ItemStack finalTSource = tSource;

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(true, true,
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.SetItem(iSource.getOriginal(), finalTSource, pValue, iSource.getRef_int_a(), iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> true,

                                        // Handle score if
                                        ((iSource, sInfo) -> {

                                            // Set amount, again
                                            iSource.getResult().setAmount(iSource.getRef_int_a().getValue());
                                        })
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString())); }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region Copy Item
                    case "copy":
                        //   0    1    2      3       4          5               6        args.Length
                        // /goop nbt copy <player> <slots> <source-slot> [±][amount][%]
                        //   -    0    1      2       3          4               5          args[n]
                        argsMinLength = 5;
                        argsMaxLength = 6;
                        usage = "/goop nbt copy <player> <slots> <source-slot> [±][amount][%]";
                        subcommand = "Copy";
                        subsection = "NBT - copy";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73NBT - \u00a7b" + subcommand + ",\u00a77 Copy the contents from one to another slot.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player who has the item.");
                            logReturn.add("\u00a73 - \u00a7e<slots> \u00a77Slots onto which to copy the target item.");
                            logReturn.add("\u00a73 - \u00a7e<source-slot> \u00a77Slot of the target item to copy.");
                            logReturn.add("\u00a73 --> \u00a7cCannot \u00a77target multiple slots.");
                            logReturn.add("\u00a73 - \u00a7e[±][amount][%] \u00a77Edit the amount of items in the stack, too.");

                        // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gets that player boi
                            RefSimulator<String> logAddition = new RefSimulator<>("");

                            // Does the player exist?
                            if (targets.size() <  1 && asDroppedItem == null) {
                                // Failure
                                failure = true;

                                // Notify the error
                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subsection, "Target must be an online player!"));
                            }

                            // Other dropped item I guess
                            Entity fromDroppedEnt = OotilityCeption.getEntityByUniqueId(args[4]);

                            // Check PMP
                            PlusMinusPercent pValue;
                            if (args.length >= 6) {
                                pValue = PlusMinusPercent.GetPMP(args[5], logAddition);
                                if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subsection, logAddition.getValue())); }

                                if (pValue == null) {

                                    // Failure
                                    failure = true; }

                            } else { pValue = new PlusMinusPercent(1.0, false, false); }

                            if (!failure) {

                                // Copy of finals
                                final ItemStack finalFromDropped = OotilityCeption.FromDroppedItem(fromDroppedEnt);

                                // Preparation of Methods
                                TargetedItems executor = new TargetedItems(true, true,
                                        chained, commandChain, sender, failMessage,

                                        // What method to use to process the item
                                        iSource -> OotilityCeption.CopyItem(iSource.getOriginal(), args[4], iSource.getPlayer(), finalFromDropped, pValue, iSource.getRef_int_a(), iSource.getLogAddition()),

                                        // When will it succeed
                                        iSource -> iSource.getResult() != null,

                                        // Handle score if
                                        ((iSource, sInfo) -> {

                                            // Set amount, again
                                            iSource.getResult().setAmount(iSource.getRef_int_a().getValue());
                                        })
                                );

                                // Register the ItemStacks
                                if (asDroppedItem != null) { executor.registerDroppedItem((Item) asDroppedItem); }
                                executor.registerPlayers(targets, args[3], executor.getIncludedStrBuilder());

                                // Process the stuff
                                executor.process();

                                // Was there any log messages output?
                                if (executor.getIncludedStrBuilder().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subsection, executor.getIncludedStrBuilder().toString())); }
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop nbt " + subsection));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subsection, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop nbt " + subsection));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    default:
                        // I have no memory of that shit
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("NBT", "'\u00a73" + args[1] + "\u00a77' is not a valid NBT action! do \u00a7e/goop nbt\u00a77 for the list of actions."));
                        break;
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73NBT Manipulation, \u00a77Mostly add lore and edit item name.");
                logReturn.add("\u00a73Usage: \u00a7e/goop nbt {action}");
                logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                logReturn.add("\u00a73 --> \u00a7erename <player> <slot> <item name>");
                logReturn.add("\u00a73      * \u00a77Sets the name of target item.");
                logReturn.add("\u00a73 --> \u00a7eaddLore <index> <player> <slot> [fv] <lore line>");
                logReturn.add("\u00a73      * \u00a77Adds a lore line to the item. <index> supports keywords \u00a7etop\u00a77 and \u00a7ebottom\u00a77.");
                logReturn.add("\u00a73 --> \u00a7eremoveLore <index> <player> <slot> [fv]");
                logReturn.add("\u00a73      * \u00a77Removes a line of lore. <index> supports keywords \u00a7etop\u00a77 and \u00a7ebottom\u00a77.");
                logReturn.add("\u00a73 --> \u00a7emSpeed <player> <slot> [±]<movement speed value>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the movement speed attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7emHealth <player> <slot> [±]<max health>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the health boost attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7earmour <player> <slot> [±]<armor>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the armour boost attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7eaToughness <player> <slot> [±]<armor toughness>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the armor toughness attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7eaDamage <player> <slot> [±]<attack damage>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the attack damage attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7eaSpeed <player> <slot> [±]<attack speed>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the attack speed attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7ekResistance <player> <slot> [±]<knockback resistance>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the knockback resistance attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7eluck <player> <slot> [±]<luck>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the luck attribute of the item.");
                logReturn.add("\u00a73 --> \u00a7ecModelData <player> <slot> [±]<custom model data>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies the CustomModelData of the item.");
                logReturn.add("\u00a73 --> \u00a7eenchantment <player> <slot> <enchantment> [±]<enchantment>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Modifies an enchantment of the item.");
                logReturn.add("\u00a73 --> \u00a7esetMaterial <player> <slot> <material>");
                logReturn.add("\u00a73      * \u00a77Sets the material of target item.");
                logReturn.add("\u00a73 --> \u00a7eamount <player> <slot> [±]<quantity>[%] [objective]");
                logReturn.add("\u00a73      * \u00a77Increases or decreases the amount of target item.");
                logReturn.add("\u00a73 --> \u00a7esetItem <player> <slot> {nbt} [[±]<amount>[%]]");
                logReturn.add("\u00a73      * \u00a77Sets target item, {nbt} can be for vanilla or MMOItem.");
                logReturn.add("\u00a73      * \u00a77Amount operation applied to old item amount, if there was.");
                logReturn.add("\u00a73 --> \u00a7edamage <player> <slot> {nbt} [[±]<repair>[%]]");
                logReturn.add("\u00a73      * \u00a77Modifies durability of item.");
                logReturn.add("\u00a73      * \u00a73Can never break or go negative - will be set to 1 instead.");
                //logReturn.add("\u00a73 --> \u00a7erevar <player> <slot> <variable=value...>");
                //logReturn.add("\u00a73      * \u00a77Changes the value of a variable in the name/lore of an item");
                logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Target slot in player's inventory.");
                logReturn.add("\u00a73 --> \u00a77Possible slots: \u00a7bhead\u00a73, \u00a7bchest\u00a73, \u00a7blegs\u00a73, \u00a7bfeet\u00a73, \u00a7bmainhand\u00a73, \u00a7boffhand\u00a73, and any number \u00a7b0\u00a73-\u00a7b35\u00a73.");
                if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a73 - \u00a7e[fv] \u00a77Forces vanilla lore operations (For MMOItems).");
                if (Gunging_Ootilities_Plugin.foundMMOItems) { logReturn.add("\u00a73         **\u00a77If not set or false, all operations will target MMOItem item data."); }
                logReturn.add("\u00a73 - \u00a7e[objective] \u00a77If specified, the resulting value of the attribute will be stored in that objective score of the target player.");

            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("NBT", "Incorrect usage. For info: \u00a7e/goop nbt"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop nbt {action}");
                }
            }

            // No perms
        } else {
            // Tell him lmao
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou dont have permission to edit item nbt!"));
        }

        //Set Log Return Urn Value
        logReturnUrn.SetValue(logReturn);
    }

    /**
     * Sets the one slot the player clicked.
     *
     * @param player Player who is clicking
     * @param location Location where they did click
     * @param number Slot number they clicked
     */
    public static void setProvidedSlot(@NotNull UUID player, @NotNull SearchLocation location, int number) {

        // The one to set
        ItemStackSlot ret;
        switch (location) {
            default:

                // Just in inventory
                ret = new ISSInventory(number, null);
                break;
            case OBSERVED_CONTAINER:
            case PERSONAL_CONTAINER:

                // Just in observed container
                ret = new ISSObservedContainer(number, null, null);
                break;
            case ENDERCHEST:

                // Just in inventory
                ret = new ISSEnderchest(number, null);
                break;
        }

        // Put simple
        ArrayList<ItemStackSlot> cleared = new ArrayList<>();
        cleared.add(ret);

        // Register
        providedSlot.put(player, cleared);
    }

    /**
     * Sets the one slot the player clicked.
     *
     * @param player Player who is clicking
     * @param location Location where they did click
     * @param number Slot number they clicked
     */
    public static void setProvidedSlots(@NotNull UUID player, @NotNull ArrayList<ItemStackSlot> locs) {

        // Register
        providedSlot.put(player, locs);
    }

    /**
     * @param player Player you are querying.
     *
     * @return All the provided slots of the player (generally one) with
     *         or without the key prefixes.
     */
    @Nullable public static String getProvidedSlot(@Nullable UUID player, boolean includeLocationPrefixes) {
        if (player == null) { return null; }

        ArrayList<ItemStackSlot> found = providedSlot.get(player);
        if (found == null || found.size() == 0) { return null; }

        boolean separated = false;
        StringBuilder builder = new StringBuilder();

        // All right begin building it
        for (ItemStackSlot slot : found) {

            // Separate with comma
            if (separated) { builder.append(","); } else { separated = true; }

            // Append slot
            if (includeLocationPrefixes) {

                // Append with prefix
                builder.append(slot.toString());

            } else {

                // Append without prefix
                builder.append(slot.getSlot());
            }
        }

        // Yeah
        return builder.toString();
    }

    @NotNull static HashMap<UUID, ArrayList<ItemStackSlot>> providedSlot = new HashMap<>();
}
