package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOLib;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicCrucible;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerOpeningReason;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerSlotTypes;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerTypes;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.GPCProtection;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GCT_PlayerTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.containers.restriction.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation.GTranslationManager;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ApplicableMask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GOOPCCommands implements CommandExecutor {

    /** @noinspection unused*/
    public static void onCommand_GooPContainers(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args, @Nullable Location senderLocation, boolean chained, @Nullable SuccessibleChain commandChain, @Nullable String chainedNoLocation, @NotNull RefSimulator<List<String>> logReturnUrn, @Nullable String failMessage) {
        // Has permission?
        boolean permission = true;

        // What will be said to the caster (caster = sender of command)
        List<String> logReturn = new ArrayList<>();

        // Check 5 Permission
        if (sender instanceof Player) {
            // Solid check for permission
            permission = sender.hasPermission("gunging_ootilities_plugin.containers");
        }

        // Got permission?
        if (permission) {

            if (args.length >= 2) {

                // Help Parameters
                int argsMinLength, argsMaxLength;
                String subcommand, usage, subcategory;

                // Some bool to know if failure
                boolean failure = false;

                switch (args[1].toLowerCase()) {
                    //region close
                    case "close":
                        //   0        1       2       3     args.Length
                        // /goop containers close <player>
                        //   -        0       1       2     args[n]

                        // Correct number of args?
                        argsMaxLength = 3;
                        usage = "/goop containers close <player>";
                        subcommand = "Close";
                        subcategory = "Containers - Close";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Forces the target player to close their inventory.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player to close the inventory of.");
                            logReturn.add("\u00a76This command solely forces the player to close the inventory they are looking at.");

                            // Correct number of args?
                        } else if (args.length <= argsMaxLength) {

                            // Gather Player
                            ArrayList<Player> targets = OotilityCeption.GetPlayers(senderLocation, args[2], null);

                            // If null, failure
                            if (targets.size() == 0) {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify an online player to open the container to."));
                            }

                            // So that is a success
                            if (!failure) {

                                for (Player target : targets) {

                                    // Open it for them
                                    target.closeInventory();

                                    // Log Success
                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Forced \u00a73" + target.getName() + "\u00a77 to close their inventory. "));

                                    // Run Chain
                                    commandChain.chain(chained, target, sender);
                                }
                            }

                            // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers close"));

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region open
                    case "open":
                        //   0        1      2         3        4        5   args.Length
                        // /goop containers open <container> [player] [mode]
                        //   -        0      1         2        3       4    args[n]

                        // Correct number of args?
                        argsMaxLength = 5;
                        usage = "/goop containers open <container> [player] [mode]";
                        subcommand = "Open";
                        subcategory = "Containers - Open";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Opens a non-physical container.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the container template.");
                            logReturn.add("\u00a73 - \u00a7e[player] \u00a77Player who will open the container.");
                            logReturn.add("\u00a73 - \u00a7e[mode] \u00a77Mode of interacting.");
                            logReturn.add("\u00a73 ---> \u00a7bUSAGE \u00a77Normal use, normal features.");
                            logReturn.add("\u00a73 ---> \u00a7bPREVIEW \u00a77Locks storage slots but commands still clickable.");

                            // Correct number of args?
                        } else if (args.length <= argsMaxLength) {

                            // Gather Player
                            Player target = null;
                            if (args.length >= 4) {

                                // If specified, just get target player
                                target = Bukkit.getPlayerExact(args[3]);

                            } else if (sender instanceof Player) {

                                // If not specified, it must be the sender
                                target = (Player) sender;
                            }

                            // Additional mode?
                            ContainerOpeningReason reason = ContainerOpeningReason.USAGE;
                            if (args.length >= 5) {

                                // Preview
                                if (args[4].equalsIgnoreCase("PREVIEW")) { reason = ContainerOpeningReason.LOCK_STORAGE;}
                            }

                            // If null, failure
                            if (target == null) {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify an online player to open the container to."));
                            }

                            // Is container loaded?
                            GOOPCTemplate template = GCL_Templates.getByInternalName(args[2]);
                            if (template == null) {

                                // Fat L
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Could not find any loaded container of name \u00a73" + args[2]));

                                // So it exists; Was it physical?
                            } else if (template.isPhysical()) {

                                // Wont open PHYSICAl
                                failure = true;

                                // Notify Failure
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect command to open \u00a73PHYSICAL\u00a77 containers. Use \u00a7e/goop containers access\u00a77 instead."));

                                // Not Pers and not Station
                            } else if (!template.isPersonal() && !template.isStation()) {

                                // Wont open anything else
                                failure = true;

                                // Notify Failure
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect command to open this container, to use this command, must select a container of type \u00a73PERSONAL\u00a77 or \u00a73STATION\u00a77."));
                            }

                            // So that is a success
                            if (!failure) {

                                // Open it for them
                                template.getDeployed().openForPlayer(target, reason);

                                // Run whatever command-yo
                                if (template.hasCommandsOnOpen()) { template.executeCommandsOnOpen(target); } //  && (GOOPCManager.isPremiumEnabled() || target.isOp())

                                // Log Success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Opened container \u00a73" + template.getInternalName() + "\u00a77 to \u00a73" + target.getName() + "\u00a77 successfully."));

                                // Run Chain
                                commandChain.chain(chained, target, sender);
                            }

                        // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers open"));

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region tempequip
                    case "tempequip":
                        //   0        1         2           3         4     5       6 7 8    9         args.Length
                        // /goop containers tempeEquip <container> <slot> <player> <m T I> [time]
                        //   -        0         1           2         3     4       5 6 7    8         args[n]

                        // Correct number of args?
                        argsMaxLength = 9;
                        argsMinLength = 8;
                        usage = "/goop containers tempeEquip <container> <slot> <player> <m TYPE ID> [time]";
                        subcommand = "TempEquip";
                        subcategory = "Containers - TempEquip";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Temporarily equips a MMOItem.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the personal container template.");
                            logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slot to equip the item in");
                            logReturn.add("\u00a73 - \u00a7e<player> \u00a77Player to equip the item to");
                            logReturn.add("\u00a73 - \u00a7e<m TYPE ID> \u00a77MMOItem to equip.");
                            logReturn.add("\u00a73 - \u00a7e[time] \u00a77Time it will remain equipped.");
                            logReturn.add("\u00a78This command simulates the entire setup of the tips section of the equipment page. However it does not include the call to /goop mmoitems equipmentUpdate ~ https://sites.google.com/view/gootilities/core-plugin-goop/containers/personal-containers/containers-equipment?authuser=0");

                            // Correct number of args?
                        } else if (args.length <= argsMaxLength && args.length >= argsMinLength) {

                            // Gather Player
                            Player target = Bukkit.getPlayerExact(args[4]);

                            // If null, failure
                            if (target == null) {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify an online player to open the container to."));
                            }

                            // Is container loaded?
                            GOOPCTemplate template = GCL_Templates.getByInternalName(args[2]);
                            GOOPCPersonal personal = null;
                            if (template == null) {

                                // Fat L
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Could not find any loaded container of name \u00a73" + args[2]));

                                // So it exists; Was it physical?
                            } else if (!(template.getDeployed() instanceof GOOPCPersonal)) {

                                // Wont open anything else
                                failure = true;

                                // Notify Failure
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Container must be of type \u00a73PERSONAL\u00a77 to equip items."));

                            } else {

                                // Cast
                                personal = (GOOPCPersonal) template.getDeployed();
                            }

                            // Setup the slot
                            GOOPCSlot goopcSlot = null;

                            // Slot I guess
                            int slotNumber = 0;
                            if (OotilityCeption.IntTryParse(args[3])) {

                                // Parse int
                                slotNumber = OotilityCeption.ParseInt(args[3]);

                                // Yeah
                                if (template != null && (template.getTotalSlotCount() <= slotNumber || slotNumber < 0)) {

                                    // failure
                                    failure = true;

                                    // Notify
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Slot number \u00a73" + args[3] + "\u00a77 out of range, must be between \u00a7b0\u00a77 (inclusive) and\u00a7b " + template.getTotalSlotCount() + "\u00a77 (exclusive)."));
                                } else {

                                    // Get slot
                                    goopcSlot = template.getSlotAt(slotNumber);

                                    // Yeah
                                    if (goopcSlot == null) {

                                        // failure
                                        failure = true;

                                        // Notify
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Slot number \u00a73" + args[3] + "\u00a77 was invalid. "));
                                    }
                                }

                            } else {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected integer number instead of \u00a73" + args[3] + "\u00a77 for slot number. "));
                            }

                            ItemStack toEquip = null;
                            if (OotilityCeption.IsInvalidItemNBTtestString(args[5], args[6], args[7], null, null)) {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Invalid item\u00a73 " + args[5] + " " + args[6] + " " + args[7]));
                            } else {

                                // Accept
                                toEquip = OotilityCeption.ItemFromNBTTestString(args[5], args[6], args[7], null, null);

                                // Attempt to get
                                if (toEquip == null) {

                                    // failure
                                    failure = true;

                                    // Notify
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Invalid item\u00a73 " + args[5] + " " + args[6] + " " + args[7]));
                                }
                            }

                            // Timer
                            OptimizedTimeFormat otf = null;
                            if (args.length >= 9) {

                                // Identify time measure
                                String magnitude = args[8].substring(0, args[8].length() - 1);
                                String units = args[8].substring(args[8].length() - 1);

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

                            // So that is a success
                            if (!failure) {

                                // Build goal
                                String uckGoal = (args[6] + "_" + args[7]).toUpperCase();

                                // Not setup?
                                if (goopcSlot.getRestrictedBehaviour() != RestrictedBehaviour.DESTROY) {

                                    // Setup the slot
                                    goopcSlot.setSlotType(ContainerSlotTypes.STORAGE);
                                    goopcSlot.setForEquipment(true);
                                    goopcSlot.setRestrictedBehaviour(RestrictedBehaviour.DESTROY);
                                    goopcSlot.addRestriction(new GCSR_Unlockable(uckGoal));

                                    // Save
                                    GCL_Templates.saveTemplate(template);
                                }

                                // Unlock Unlockable
                                GooPUnlockables uck = GooPUnlockables.From(target.getUniqueId(), uckGoal);
                                uck.CheckTimer();
                                uck.SetTimed(otf);
                                uck.Unlock();

                                // Equip and update inven
                                personal.setAndSaveOwnerItem(target.getUniqueId(), slotNumber, toEquip, true);

                                // Log Success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Equipped " + OotilityCeption.GetItemName(toEquip) + "\u00a77 onto slot \u00a7b#" + slotNumber + "\u00a77 of container \u00a73" + template.getInternalName() + "\u00a77 of player \u00a73" + target.getName() + "\u00a77 successfully."));

                                // Run Chain
                                commandChain.chain(chained, target, sender);
                            }

                            // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers tempEquip"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers tempEquip"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region playerinventory
                    case "playerinventory":
                        //   0        1             2         3        4        5   args.Length
                        // /goop containers playerInventory <container> [player]
                        //   -        0             1         2        3       4    args[n]

                        // Correct number of args?
                        argsMaxLength = 5;
                        argsMinLength = 3;
                        usage = "/goop containers playerInventory <container> [player]";
                        subcommand = "Player Inventory";
                        subcategory = "Containers - Player Inventory";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Set someone's PLAYER inventory.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the container template.");
                            logReturn.add("\u00a73 ---> \u00a7bCLEAR \u00a77Removes the containers-PLAYER inventory.");
                            logReturn.add("\u00a73 - \u00a7e[player] \u00a77Whose inventory to set.");
                            logReturn.add("\u00a73 ---> \u00a7bDEFAULT \u00a77Sets this as the default, for players with no inventory set. ");

                            // Correct number of args?
                        } else if (args.length <= argsMaxLength && args.length >= argsMinLength) {

                            // Gather Player
                            Player target = null;
                            boolean defaulting = false;
                            if (args.length >= 4) {

                                // If specified, just get target player
                                target = Bukkit.getPlayerExact(args[3]);
                                defaulting = "DEFAULT".equals(args[3]);

                            } else if (sender instanceof Player) {

                                // If not specified, it must be the sender
                                target = (Player) sender;
                            }

                            // If null, failure
                            if (target == null && !defaulting) {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify an online player to open the container to, or the \u00a7bDEFAULT\u00a77 keyword."));
                            }

                            // Is container loaded?
                            GOOPCTemplate template = null;
                            boolean clearing = "CLEAR".equals(args[2]);
                            if (!clearing) { template = GCL_Templates.getByInternalName(args[2]); }
                            if (template == null && !clearing) {

                                // Fat L
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Could not find any loaded container of name \u00a73" + args[2]));

                                // So it exists; Was it physical?
                            } else if (!clearing && !template.isPlayer()) {

                                // Wont open anything else
                                failure = true;

                                // Notify Failure
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "To use this command, must select a container of type \u00a73PLAYER\u00a77."));
                            }

                            // So that is a success
                            if (!failure) {

                                // Registering a player inventory
                                if (!clearing) {
                                    
                                    // New one to set
                                    GOOPCPlayer player = (GOOPCPlayer) template.getDeployed();

                                    // For everyone??
                                    if (defaulting) {

                                        // Set as default
                                        GCL_Player.setDefaultInventory(player);

                                    // For a target player, only
                                    } else {

                                        // Activate it for them, this method includes removing from previous active and saving changes
                                        player.setActiveFor(target.getUniqueId());
                                    }

                                    // Log Success
                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Set PLAYER container \u00a73" + template.getInternalName() + "\u00a77 as the inventory of \u00a73" + (defaulting ? "all players by default" : target.getName()) + "\u00a77 successfully."));

                                // Clearing player inventory
                                } else {

                                    // Clearing default?
                                    if (defaulting) {

                                        // Default this
                                        GCL_Player.setDefaultInventory(null);

                                        // Log Success
                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Cleared default player inventory. "));

                                    } else {

                                        // Clear for player
                                        GOOPCPlayer rpg = GCL_Player.getInventoryFor(target);

                                        // Remove it yeah
                                        if (rpg != null) {

                                            // Remove and save
                                            rpg.removeActiveFor(target.getUniqueId());
                                            GCL_Player.saveActives(rpg);
                                        }

                                        // Log Success
                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Cleared the player inventory assigned to \u00a73" + target.getName() + "\u00a77. They will now use the default inventory. "));
                                    }
                                }

                                // Defaulting?
                                if (defaulting) {

                                    // All online players
                                    for (Player onl : Bukkit.getServer().getOnlinePlayers()) {

                                        // Must have no overriding inventory (be subject to the default one)f
                                        if (GCL_Player.getOverridingInventoryFor(onl.getUniqueId()) == null) {

                                            // Refresh
                                            GCL_Player.corroborateInventory(onl, false);
                                        }
                                    }

                                } else {

                                    // Specifically to that target
                                    GCL_Player.corroborateInventory(target, false);
                                }

                                // Run Chain
                                if (!defaulting) { commandChain.chain(chained, target, sender); }
                            }

                            // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers playerInventory"));

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region see
                    case "see":
                        //   0        1      2         3           [4]      4 [5]     5 [6]        args.Length
                        // /goop containers see <container type> [opener] <player> [preview]
                        //   -        0      1         2           [3]      3 [4]     4 [5]        args[n]

                        // Correct number of args?
                        argsMinLength = 4;
                        argsMaxLength = 6;
                        usage = "/goop containers see <container> [player] <owner> [mode]";
                        subcommand = "See";
                        subcategory = "Containers - See";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Opens someone's personal container.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the container template.");
                            logReturn.add("\u00a73 - \u00a7e[player] \u00a77Player who will open the container.");
                            logReturn.add("\u00a73 - \u00a7e<owner> \u00a77Owner of the container being opened.");
                            logReturn.add("\u00a73 ---> \u00a7b(any uuid) \u00a77UUID of owner, they all exist.");
                            logReturn.add("\u00a73 - \u00a7e[mode] \u00a77Mode of interacting.");
                            logReturn.add("\u00a73 ---> \u00a7bUSAGE \u00a77Normal use, normal features.");
                            logReturn.add("\u00a73 ---> \u00a7bPREVIEW \u00a77Locks storage slots but commands still clickable.");

                            // Correct number of args?
                        } else if (args.length <= argsMaxLength && args.length >= argsMinLength) {

                            String openerName = null;
                            String ownerName = args[3];
                            ContainerOpeningReason reason = ContainerOpeningReason.USAGE;

                            // Preview
                            if (args.length == 5) {

                                boolean prevv = args[4].equalsIgnoreCase("PREVIEW");
                                boolean usagg = args[4].equalsIgnoreCase("USAGE");

                                // Parse preview?
                                if (prevv || usagg) {

                                    /*
                                     *  Well this means it is in normal format, and preview will parse appropriately
                                     */
                                    reason = prevv ? ContainerOpeningReason.LOCK_STORAGE : ContainerOpeningReason.USAGE;

                                    // Its not an opening reason, which means it'll try to be a player
                                } else {

                                    // Oracle is [3]
                                    openerName = args[3];
                                    ownerName = args[4];
                                }

                                // Full information?
                            } else if (args.length == 6) {

                                // Oracle is [3]
                                openerName = args[3];
                                ownerName = args[4];

                                /*
                                 * Parse opening reason
                                 */
                                boolean prevv = args[5].equalsIgnoreCase("PREVIEW");
                                boolean usagg = args[5].equalsIgnoreCase("USAGE");

                                // Parse preview?
                                if (prevv || usagg) {

                                    /*
                                     *  Well this means it is in normal format, and preview will parse appropiately
                                     */
                                    reason = prevv ? ContainerOpeningReason.LOCK_STORAGE : ContainerOpeningReason.USAGE;

                                    // Its not an opening reason, which means it'll try to be a player
                                } else {

                                    // Cannot be sent from console
                                    failure = true;

                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Invalid opening mode '\u00a73" + args[5] + "\u00a77', expected\u00a7b PREVIEW\u00a77 or \u00a7bUSAGE"));
                                }
                            }

                            // Get Self
                            Player opener = null;
                            if (openerName == null) {

                                if (sender instanceof Player) {
                                    opener = (Player) sender;

                                } else {

                                    // Cannot be sent from console
                                    failure = true;
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "This command cannot be called from the console without a player."));
                                }
                            } else {

                                // Gather Player
                                opener = (Player) OotilityCeption.GetPlayer(openerName, false);

                                // If null, failure
                                if (opener == null) {

                                    // failure
                                    failure = true;

                                    // Notify
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify the player that will see the inventory of someone else, '\u00a73" + openerName + "\u00a77' not found."));
                                }
                            }

                            // Get owner UUID
                            UUID ownerUUID = OotilityCeption.UUIDFromString(ownerName);
                            OfflinePlayer offlineOwner = OotilityCeption.GetPlayer(ownerName, true);

                            // Not a UUID? Okay lets seek a player
                            if (ownerUUID == null) {

                                // Gather Player
                                if (offlineOwner != null) { ownerUUID = offlineOwner.getUniqueId(); }
                            }

                            // If null, failure
                            if (ownerUUID == null) {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify a valid player or UUID, '\u00a73" + ownerName + "\u00a77' not found."));
                            }

                            // Is container loaded?
                            GOOPCTemplate template = GCL_Templates.getByInternalName(args[2]);
                            if (template == null) {

                                // Fat L
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Could not find any loaded container of name \u00a73" + args[2]));

                                // So it exists; Was it physical?
                            } else if (!template.isPersonal() && !template.isStation()) {

                                // Wont open non-PERSONAL
                                failure = true;

                                // Notify Failure
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target container is \u00a7cnot PERSONAL\u00a77, this method is intended to look at \u00a73PERSONAL\u00a77 type containers."));
                            }

                            if (template.isStation() && offlineOwner.getPlayer() == null) {

                                // Wont open non-PERSONAL
                                failure = true;

                                // Notify Failure
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target container is of type STATION such that it requires an online player, but \u00a73" + ownerName + "\u00a77 is offline."));
                            }

                            // So that is a success
                            if (!failure) {

                                // Find
                                ContainerInventory cInventory = null;

                                if (template.getDeployed() instanceof GOOPCPersonal) {

                                    // Will open target player's version. Get the corresponding Personal Container
                                    GOOPCPersonal personal = (GOOPCPersonal) template.getDeployed();

                                    // Open it
                                    personal.openForPlayer(opener, ownerUUID, reason);

                                    // Find
                                    cInventory = personal.getObservedBy(opener.getUniqueId());

                                } else if (template.getDeployed() instanceof GOOPCStation) {

                                    // Will open target player's version. Get the corresponding Personal Container
                                    GOOPCStation station = (GOOPCStation) template.getDeployed();

                                    // Open it
                                    station.openForPlayer(opener, offlineOwner.getPlayer(), -1, reason);

                                    // Find
                                    cInventory = station.getObservedBy(opener.getUniqueId());
                                }

                                // Commands on open I guess
                                if (cInventory != null && cInventory.getObservers().size() == 1 && template.hasCommandsOnOpen()) {

                                    Player runner = offlineOwner == null ? null : offlineOwner.getPlayer();
                                    if (runner == null) { runner = opener; }
                                    template.executeCommandsOnOpen(runner);  // if (GOOPCManager.isPremiumEnabled() || runner.isOp()) { }
                                }

                                // Log Success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "\u00a7e" + opener.getName() + " \u00a77opened " + ownerName + "'s container \u00a73" + template.getInternalName() + "\u00a77 successfully."));

                                // Run Chain
                                commandChain.chain(true, (Player) null, sender);
                            }

                            // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers see"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers see"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region unregister
                    case "unregister":
                        //   0        1         2           3        4      5 6 7 8      args.Length
                        // /goop containers unregister <container> <owner> [w x y z]
                        //   -        0         1           2        3      4 5 6 7      args[n]

                        // Correct number of args?
                        argsMinLength = 4;
                        argsMaxLength = 8;
                        usage = "/goop containers unregister <container> <owner> [w x y z]";
                        subcommand = "Unregister";
                        subcategory = "Containers - Unregister";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Delete someone's personal container.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the container template.");
                            logReturn.add("\u00a73 - \u00a7e<owner> \u00a77Owner of the container being deleted.");
                            logReturn.add("\u00a73 ---> \u00a7b(any uuid) \u00a77UUID of owner.");
                            logReturn.add("\u00a73 - \u00a7e[w x y z] \u00a77Coordinates to drop items at.");
                            logReturn.add("\u00a73 ---> \u00a7b(nothing) \u00a77Items will be lost, destroyed.");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            String ownerName = args[3];
                            UUID ownerUUID = OotilityCeption.UUIDFromString(ownerName);
                            Player locationPlayer = null;

                            // Not a UUID? Okay lets seek a player
                            if (ownerUUID == null) {

                                // Gather Player
                                OfflinePlayer ownerASPlayer = OotilityCeption.GetPlayer(ownerName, true);
                                if (ownerASPlayer != null) {
                                    ownerUUID = ownerASPlayer.getUniqueId();
                                    if (ownerASPlayer.isOnline()) { locationPlayer = ownerASPlayer.getPlayer(); }
                                } }

                            // If null, failure
                            if (ownerUUID == null) {

                                // failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify a valid player or UUID, '\u00a73" + ownerName + "\u00a77' not found."));
                            } else if (locationPlayer == null && sender instanceof Player) { locationPlayer = (Player) sender; }

                            //region Gets that location boi
                            Location targetLocation = null;

                            // Get
                            RefSimulator<String> logAddition = new RefSimulator<>("");
                            if (args.length >= 8) {
                                targetLocation = OotilityCeption.ValidLocation(locationPlayer, args[4], args[5], args[6], args[7], logAddition);

                                // Ret
                                if (targetLocation == null) { failure = true; }

                                // Add Log
                                if (logAddition.GetValue() != null) {
                                    logReturn.add(OotilityCeption.LogFormat("Grief", logAddition.GetValue()));
                                }
                            }
                            //endregion

                            // Is container loaded?
                            GOOPCTemplate template = GCL_Templates.getByInternalName(args[2]);
                            if (template == null) {

                                // Fat L
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Could not find any loaded container of name \u00a73" + args[2]));

                                // So it exists; Was it physical?
                            } else if (!template.isPersonal()) {

                                // Wont open non-PERSONAL
                                failure = true;

                                // Notify Failure
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target container is \u00a7cnot PERSONAL\u00a77, this method is intended to look at \u00a73PERSONAL\u00a77 type containers."));
                            }

                            // So that is a success
                            if (!failure) {

                                // Will open target player's version. Get the corresponding Personal Container
                                GOOPCPersonal personal = (GOOPCPersonal) template.getDeployed();

                                // If a locationw as specified, drop all the contents there
                                if (targetLocation != null) {

                                    // Fetch the items
                                    HashMap<Integer, ItemStack> contents = personal.getOwnerInventory(ownerUUID);

                                    // Spill them
                                    for (Map.Entry<Integer, ItemStack> item : contents.entrySet()) {

                                        // Get itemstack
                                        ItemStack stack = item.getValue();
                                        if (OotilityCeption.IsAirNullAllowed(stack)) { continue; }

                                        // Drop there
                                        targetLocation.getWorld().dropItemNaturally(targetLocation, stack);
                                    }
                                }

                                // Unregister
                                personal.closeInventory(personal.getOpenedInstance(ownerUUID), true);
                                // <drop items / already happened>>
                                GCL_Personal.unloadDelete(personal, ownerUUID);


                                // Log Success
                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Deleted " + ownerName + "'s container \u00a73" + template.getInternalName() + "\u00a77 successfully. " + (targetLocation != null ? "Dropped the items at\u00a7b " + OotilityCeption.BlockLocation2String(targetLocation) : "")));

                                // Run Chain
                                commandChain.chain(chained, (Player) null, sender);
                            }

                            // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers unregister"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers unregister"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region access
                    case "access":
                        //   0         1       2       3          4      5 6 7 8    args.Length
                        // /goop containers access <container> [player] [w x y z]
                        //   -         0       1       2          3      4 5 6 7   args[n]

                        // Correct number of args?
                        argsMinLength = 3;
                        argsMaxLength = 8;
                        usage = "/goop containers access <container> [player] [w x y z]";
                        subcommand = "Access";
                        subcategory = "Containers - Access";

                        // Help form?
                        if (args.length == 2)  {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Opens a physical container.");
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                            logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the container template.");
                            logReturn.add("\u00a73 - \u00a7e[player] \u00a77Player who will open the container.");
                            logReturn.add("\u00a73 - \u00a7e[w x y z] \u00a77Coordinates of the container to open.");

                            // Correct number of args?
                        } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                            // Gather Player
                            ArrayList<Player> targets = new ArrayList<>();
                            if (args.length == 4 || args.length == 8) {

                                // If specified, just get target player
                                targets = OotilityCeption.GetPlayers(senderLocation, args[3], null);

                            } else if (sender instanceof Player) {

                                // If not specified, it must be the sender
                                targets.add((Player) sender);
                            }

                            // If null, failure
                            if (targets.size() < 1) {

                                // Failure
                                failure = true;

                                // Notify
                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify an online player to open the container to."));
                            }

                            // Is container loaded?
                            GOOPCTemplate template = GCL_Templates.getByInternalName(args[2]);
                            boolean isGeneralAccessMode = args[2].toUpperCase().equals("OPEN_ONLY");
                            if (!isGeneralAccessMode) {

                                // If its not using the 'open_only' keyword. Load it to be created
                                if (template == null) {

                                    // Fat L
                                    failure = true;

                                    // Notify
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Could not find any loaded container of name \u00a73" + args[2]));

                                    // It is loaded, is it not a PHYSICAL type?
                                } else if (!template.isPhysical()) {

                                    // Fail
                                    failure = true;

                                    // Notify
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Target container is not a \u00a7ePHYSICAL\u00a77 container!"));
                                }
                            }

                            // Gets that location boi
                            Location targetLocation = null;
                            if (args.length < 7) {
                                if (sender instanceof Player) {

                                    // Just target location I guess?
                                    Block bLock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                    // If exists
                                    if (bLock == null) {

                                        // Invalid location
                                        failure = true;

                                        // Mention
                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory,"You are not looking at any block!"));

                                        // I suppose its not air, right?
                                    } else if (!OotilityCeption.IsAir(bLock.getType())) {

                                        // Git Target Location
                                        targetLocation = bLock.getLocation();

                                        // Nvm it is air.
                                    } else {

                                        // Invalid location
                                        failure = true;

                                        // Mention
                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory,"You are not looking at any block!"));

                                    }

                                } else {

                                    // Vro need coords
                                    failure = true;

                                    // Say
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "When making players access PHYSICAl containers, you must specify co-ordinates and a world!"));
                                }

                                // Build Location from args, later, if they parse.
                            }

                            // Foreach player
                            for (Player target : targets) {

                                // Parse location?
                                if (args.length == 7 || args.length == 8) {
                                    int additive = 0; if (args.length == 8) { additive++; }

                                    // Make Error Messager
                                    RefSimulator<String> logAddition = new RefSimulator<>("");

                                    // Get
                                    targetLocation = OotilityCeption.ValidLocation(target, args[3 + additive], args[4 + additive], args[5 + additive], args[6 + additive], logAddition);

                                    // Ret
                                    if (targetLocation == null) {
                                        failure = true;
                                    }

                                    // Add Log
                                    if (logAddition.GetValue() != null) {
                                        if (logAddition.GetValue().length() > 0) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, logAddition.GetValue()));
                                        }
                                    }
                                }

                                // Get Instance
                                GPCContent contents;
                                boolean createInstead = false;
                                if (!failure) {

                                    // If everything is still valid, get that container
                                    contents = GCL_Physical.getContainerAt(targetLocation);

                                    // If there wasn't one
                                    if (contents == null) {

                                        // If it was supposed to only open
                                        if (isGeneralAccessMode) {

                                            // Won't do anything further
                                            failure = true;

                                            // Well its technically a failure
                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback)
                                                logReturn.add(OotilityCeption.LogFormat(subcategory, "There is no container bound to that block!"));

                                        } else {

                                            // Will create one
                                            createInstead = true;
                                        }

                                    // Found an instance in there already
                                    } else {

                                        // But this is asking to open/create a specific type of container?
                                        if (template != null) {

                                            // Does it match the specified template?
                                            if (!contents.getContainer().getTemplate().getInternalName().equals(template.getInternalName())) {

                                                // Won't do anything further
                                                failure = true;

                                                // Well its technically a failure
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback)
                                                    logReturn.add(OotilityCeption.LogFormat(subcategory, "There is already a container bound to that block (\u00a7e" + contents.getContainer().getTemplate().getInternalName() + "\u00a77) which is not the specified one (\u00a73" + template.getInternalName() + "\u00a77)."));
                                            }
                                        }

                                        // Can the instance NOT be opened by that player?
                                        if (!contents.isMember(target.getUniqueId()) && !target.isOp()) {

                                            // Won't do anything further
                                            failure = true;

                                            // Well its technically a failure
                                            delayedMessage(target, GTranslationManager.c(GTL_Containers.GPROTECTION_NOT_MEMBER).replace("%container%", contents.getContainer().getTemplate().getTitle()), 3);
                                        }
                                    }
                                }

                                // So that is a success
                                if (!failure) {
                                    GOOPCPhysical physical = (GOOPCPhysical) template.getDeployed();

                                    // Create Instance if Necessary
                                    if (createInstead) {

                                        // Get Ref
                                        RefSimulator<String> logAddition = new RefSimulator<>("");

                                        // CREATE BOOM (With no inherent structure)
                                        physical.createPhysicalInstanceAt(targetLocation, target.getUniqueId(), logAddition);

                                        // Log
                                        if (logAddition.getValue() != null) { if (logAddition.getValue().length() > 0) { logReturn.add(OotilityCeption.LogFormat(subcategory, logAddition.getValue())); } }
                                    }

                                    // Open it
                                    physical.openForPlayer(target, targetLocation, ContainerOpeningReason.USAGE);

                                    // Run whatever command-yo
                                    if (template.hasCommandsOnOpen()) { template.executeCommandsOnOpen(target); } //  && (GOOPCManager.isPremiumEnabled() || target.isOp())

                                    // Mention it
                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) {
                                        logReturn.add(OotilityCeption.LogFormat("Containers - Access", "Player \u00a73" + target.getName() + "\u00a77 successfully accessed container at \u00a7e" + OotilityCeption.BlockLocation2String(targetLocation)));
                                    }

                                    // Run Chain
                                    commandChain.chain(chained, target, sender);
                                }
                            }

                            // Incorrect number of args
                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                            // Notify Error
                            if (args.length >= argsMinLength) {
                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers access"));

                            } else {

                                logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers access"));
                            }

                            // Notify Usage
                            logReturn.add("\u00a73Usage: \u00a7e" + usage);
                        }
                        break;
                    //endregion
                    //region config
                    case "config":
                        //   0        1         2       3+     args.Length
                        // /goop containers  config {action}
                        //   -        0         1       2+     args[n]

                        // Correct number of args?
                        if (args.length >= 3) {

                            switch (args[2].toLowerCase()) {
                                //region New
                                case "new":
                                    //   0      1          2    3         4                 5          6        args.Length
                                    // /goop containers config new <container name> <container type> [rows]
                                    //   -      0          1    2         3                 4          5        args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    argsMaxLength = 6;
                                    usage = "/goop containers config new <name> <type> [rows]";
                                    subcommand = "Config New";
                                    subcategory = "Containers - Config New";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Creates a new Container Template.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<name> \u00a77Internal name of the container (not displayed).");
                                        logReturn.add("\u00a73 - \u00a7e<type> \u00a77How does it save items put inside it?");
                                        logReturn.add("\u00a73 ---> \u00a7bSTATION \u00a77Does not save items, returns them to player when closed.");
                                        logReturn.add("\u00a73 ---> \u00a7bPERSONAL \u00a77Saves items by player, like enderchests.");
                                        logReturn.add("\u00a73 ---> \u00a7bPHYSICAL \u00a77Saves items by location in the world, like chests.");
                                        logReturn.add("\u00a73 ---> \u00a7bPLAYER \u00a77Override the player inventory slots with this.");
                                        logReturn.add("\u00a73 - \u00a7e[rows] \u00a77Size of the container, how many rows.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaedd
                                        if (GCL_Templates.isTemplateLoaded(args[3])) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name already exists!"));
                                        }

                                        // Attempt to get
                                        ContainerTypes storageType = null;
                                        try {

                                            // Attempt to parse
                                            storageType = ContainerTypes.valueOf(args[4].toUpperCase());

                                        } catch (IllegalArgumentException e) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify a valid Container Type instead of \u00a73" + args[4]));
                                        }

                                        // Get Number of ROws
                                        int rows = 3;
                                        if (args.length == 6) {

                                            // Try to parse
                                            if (OotilityCeption.IntTryParse(args[5])) {

                                                // Parse da shit
                                                rows = OotilityCeption.ParseInt(args[5]);

                                                if (rows < 1 || rows > 6) {

                                                    // Failure
                                                    failure = true;

                                                    // Note
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected integer number from 1 to 6 (inclusive) instead of \u00a73" + args[5]));
                                                }

                                            } else {

                                                // Failure
                                                failure = true;

                                                // Note
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected integer number from 1 to 6 (inclusive) instead of \u00a73" + args[5]));
                                            }
                                        }

                                        // Well that's a simple success isn't it
                                        if (!failure) {
                                            if (storageType == ContainerTypes.PLAYER) { rows= 4; }

                                            // Refine contents lmaO
                                            HashMap<Integer, GOOPCSlot> refinedContents = GOOPCTemplate.buildValidSlotsContent(new ArrayList<>(), rows);

                                            // Create new container!
                                            GOOPCTemplate newTemplate;
                                            switch (storageType) {
                                                case PLAYER:
                                                    newTemplate = new GCT_PlayerTemplate(args[3], refinedContents, storageType, OotilityCeption.TitleCaseConversion(args[3]), false);
                                                    break;
                                                default:
                                                    newTemplate = new GOOPCTemplate(args[3], refinedContents, storageType, OotilityCeption.TitleCaseConversion(args[3]), false);
                                                    break;
                                            }
                                            newTemplate.setInternalID(GCL_Templates.getLoaded().size());

                                            // Save it and load it, yeet
                                            boolean success = GOOPCManager.registerNewTemplate(newTemplate);

                                            // Note
                                            if (success) { logReturn.add(OotilityCeption.LogFormat(subcategory, "Created new \u00a7e" + storageType.toString() + "\u00a77 container named \u00a73" + args[3])); }
                                            else { logReturn.add(OotilityCeption.LogFormat(subcategory, "\u00a7cFailed to create new \u00a7e" + storageType.toString() + "\u00a7c container named \u00a73" + args[3])); }
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config new"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config new"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Edge Material
                                case "edgematerial":
                                    //   0      1          2        3       args.Length
                                    // /goop containers config edgeMaterial
                                    //   -      0          1        2       args[n]

                                    // Correct number of args?
                                    if (args.length == 3) {

                                        // Get Self
                                        Player self = null;
                                        if (sender instanceof Player) {
                                            self = (Player) sender;

                                        } else {

                                            // Cannot be sent from console
                                            failure = true;
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Containers - Get Edge Material", "This command cannot be called from the console."));
                                        }

                                        // Pretty Straight-Forward
                                        if (!failure) {

                                            // Give player some edge
                                            HashMap<Integer, ItemStack> ret = self.getInventory().addItem(new ItemStack(GOOPCTemplate.CONTAINER_EDGE_MATERIAL));

                                            // Success if eempty
                                            if (ret.size() == 0) {

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat("Containers - Get Edge Material", "You have been given a copy of the edge material!"));

                                                // Notify
                                            } else {

                                                // FOricbly
                                                logReturn.add(OotilityCeption.LogFormat("Containers - Get Edge Material", "There is no space in your inventory!"));
                                            }
                                        }

                                        // Incorrect number of args
                                    } else {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7bGet Edge Material, \u00a77Get the 'edge material.'");
                                        logReturn.add("\u00a73Usage: \u00a7e/goop containers config getEdgeMaterial");
                                        logReturn.add("\u00a73      * \u00a77The Edge Material will transform into container");
                                        logReturn.add("\u00a73        \u00a77edges once the edition is complete.");
                                        logReturn.add("\u00a73      * For use with /goop containers config contents");
                                        logReturn.add("\u00a73      * \u00a77The Edge Material will transform into container");
                                        logReturn.add("\u00a73        \u00a77edges once the edition is complete.");

                                    }
                                    break;
                                //endregion
                                //region Title
                                case "title":
                                    //   0      1          2     3      4      5+       args.Length
                                    // /goop containers config title <container> <title>
                                    //   -      0          1     2      3      4+       args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    usage = "/containers config title <container> <title...>";
                                    subcommand = "Config Title";
                                    subcategory = "Containers - Config Title";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Creates a new Container Template.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the template you are editing.");
                                        logReturn.add("\u00a73 - \u00a7e<title...> \u00a77Title to display to players, may have color codes.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength) {

                                        // Make sure it is not loaedd
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Containers - Title", "A container of that name doesnt exist!"));
                                        }
                                        else if (template.isPlayer()) {
                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Changing the title of \u00a73PLAYER\u00a77 containers is not supported. "));
                                        }

                                        // Well that's a simple success isn't it
                                        if (!failure) {

                                            // Build the title
                                            StringBuilder titleBuilder = new StringBuilder(args[4]);

                                            // Build
                                            for (int i = 5; i < args.length; i++) {

                                                // Append With a Space
                                                titleBuilder.append(" ").append(args[i]);
                                            }

                                            // FInished
                                            String title = titleBuilder.toString();

                                            // Note
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Renamed \u00a7e" + OotilityCeption.ParseColour(template.getTitle()) + "\u00a77 to \u00a73" + OotilityCeption.ParseColour(title)));

                                            // Rename
                                            template.setTitle(title);

                                            // Save
                                            GCL_Templates.saveTemplate(template);
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config title"));

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Commands
                                case "commands":
                                    //   0      1          2     3          4         5           6           7+       args.Length
                                    // /goop containers config commands <container> <slot> [onStore/onTake] <command...>
                                    //   -      0          1     2          3         4            5           6+       args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    usage = "/goop containers config commands <container> <slot> [onStore/onTake] <command...>";
                                    subcommand = "Config Commands";
                                    subcategory = "Containers - Config Commands";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Creates a new Container Template.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the template you are editing.");
                                        logReturn.add("\u00a73 - \u00a7e<slot> \u00a77Slots to bind command to, or these keywords:");
                                        logReturn.add("\u00a73 ---> \u00a7bonClose \u00a77Commands that run when the container is open.");
                                        logReturn.add("\u00a73 ---> \u00a7bonOpen \u00a77Commands that run when closing the inventory.");
                                        logReturn.add("\u00a73 - \u00a7e[onStore/onTake] \u00a77For storage slots that you can put items onto:");
                                        logReturn.add("\u00a73 ---> \u00a7bonStore \u00a77Runs when an item is put into the slot.");
                                        logReturn.add("\u00a73 ---> \u00a7bonTake \u00a77Runs when an item is taken out of the slot.");
                                        logReturn.add("\u00a73 - \u00a7e<command...> \u00a77Command to add to the list of commands, or:");
                                        logReturn.add("\u00a73 ---> \u00a7bclear \u00a77Keyword to clear the command list.");
                                        logReturn.add("\u00a73 ---> \u00a7bremove.N \u00a77Keyword to remove the Nth command of the list.");
                                        logReturn.add("\u00a78Suggest to look at \u00a76/goop containers config view <container>\u00a78 to see slots. ");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength) {

                                        // Make sure it is not loaedd
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesnt exist!"));
                                        }

                                        // Get target slot
                                        boolean scryInstead = false, onCloseInstead = false, onOpenInstead = false;
                                        ArrayList<ItemStackSlot> targetSlots = OotilityCeption.getInventorySlots(args[4], null, null);
                                        if (args[4].toLowerCase().equals("slots")) {

                                            // Scry time
                                            scryInstead = true;

                                            // Commands when closing the container
                                        } else if (args[4].toLowerCase().equals("onclose")) {

                                            // Cloase time
                                            onCloseInstead = true;

                                            // Commands when opening the container
                                        } else if (args[4].toLowerCase().equals("onopen")) {

                                            // Cloase time
                                            onOpenInstead = true;

                                            // Doesnt make sense
                                        } else if (targetSlots.size() == 0) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "You must specify a range of slots, or a keyword (\u00a7bonOpen\u00a77,\u00a7bonClose\u00a77) instead of \u00a73" + args[4]));
                                        }

                                        // What is it-yo
                                        boolean onClick = false, onTake = false, onStore = false; int commandStartIndex = 5;
                                        if (args.length >= 6) {

                                            if (args[5].length() > 0) {

                                                onTake = args[5].toLowerCase().equals("ontake");
                                                onStore = args[5].toLowerCase().equals("onstore");
                                                onClick = !onTake && !onStore;

                                                // Skip that keyword
                                                if (onTake || onStore) { commandStartIndex++; }
                                            }
                                        }

                                        // Cant scry if console
                                        if (scryInstead) {

                                            if (!(sender instanceof Player)) {

                                                // Failure
                                                failure = true;

                                                // Note
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Sorry, but you cant do this from the console. That is --- seeing the slots of this container. "));
                                            }

                                        }
                                        
                                        // No OnOpen / OnTake for Player Inventory
                                        if (template != null && template.isPlayer() && (onStore || onTake)) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Commands \u00a7eonClose\u00a77 and \u00a7eonOpen\u00a77 are not supported for \u00a73PLAYER\u00a77 inventories. "));
                                        }

                                        // Well, supposing everything makes sense
                                        if (!failure) {

                                            // Should we scry?
                                            if (scryInstead) {

                                                // Preview
                                                GOOPCManager.previewContainerContents(template, (Player) sender);

                                            } else {

                                                // Build the command arg
                                                String builtCommand = "";

                                                // Is it a remove?
                                                if (args.length > commandStartIndex) {

                                                    if (args[commandStartIndex].length() > 0) {

                                                        StringBuilder tTitle = new StringBuilder(args[commandStartIndex]);

                                                        // Build
                                                        for (int i = commandStartIndex+1; i < args.length; i++) { tTitle.append(" ").append(args[i]); }

                                                        // Append chained
                                                        if (chained) { tTitle.append(" oS= ").append(chainedNoLocation); }

                                                        // FInished
                                                        builtCommand = tTitle.toString();
                                                        builtCommand = builtCommand.replace("<oS>", "oS=");
                                                    }
                                                }

                                                boolean isClear = builtCommand.equals("clear");
                                                boolean isRemove = builtCommand.startsWith("remove.");
                                                int removeIdx = -1;
                                                int trueSuccesses = 0;
                                                if (isRemove) {
                                                    String iRemove = builtCommand.substring("remove.".length());
                                                    if (OotilityCeption.IntTryParse(iRemove)) {

                                                        // Yes
                                                        removeIdx = OotilityCeption.ParseInt(iRemove);

                                                    } else {
                                                        isRemove = false;

                                                        // Note
                                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Remove keyword\u00a7b remove.\u00a77 detected but the index is not a positive integer number: \u00a73" + iRemove));
                                                    }
                                                }

                                                // Supposed to be onClose?
                                                String affectedSlots = "";
                                                if (onCloseInstead) {

                                                    // Edit Commands on Close
                                                    if (isClear) { template.setCommandsOnClose((String) null); }
                                                    else if (isRemove) { template.getCommandsOnClose().remove(removeIdx); }
                                                    else { template.addCommandsOnClose(builtCommand); }
                                                    trueSuccesses++;

                                                } else if (onOpenInstead) {

                                                    // Edit Commands on Open
                                                    if (isClear) { template.setCommandsOnOpen((String) null); }
                                                    else if (isRemove) { template.getCommandsOnOpen().remove(removeIdx); }
                                                    else { template.addCommandsOnOpen(builtCommand); }
                                                    trueSuccesses++;

                                                } else {
                                                    StringBuilder affectedSlotsBuilder = new StringBuilder();

                                                    // Edit all slots
                                                    //CHN//OotilityCeption. Log("\u00a78GCC\u00a7b CMD\u00a77 Target Slots\u00a7e " + args[4]);
                                                    for (ItemStackSlot slotLocation : targetSlots) {
                                                        //CHN//OotilityCeption. Log("\u00a78GCC\u00a7b CMD\u00a77 Slot\u00a7e " + slotLocation);
                                                        if (slotLocation == null) {
                                                            //CHN//OotilityCeption. Log("\u00a78GCC\u00a7b CMD\u00a7c null");
                                                            continue; }
                                                        if (slotLocation.isInShulker()) {
                                                            //CHN//OotilityCeption. Log("\u00a78GCC\u00a7b CMD\u00a7c shulker");
                                                            continue; }
                                                        if (!slotLocation.isInInventory()) {
                                                            //CHN//OotilityCeption. Log("\u00a78GCC\u00a7b CMD\u00a7c non-inventory");
                                                            continue; }

                                                        GOOPCSlot targetSlot = template.getSlotAt(slotLocation.getSlot());
                                                        if (targetSlot == null) {

                                                            // Note
                                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Slot \u00a7e#" + slotLocation.getSlot() + "\u00a77 is out of range!"));

                                                            continue; }

                                                        if (affectedSlotsBuilder.length() > 0) { affectedSlotsBuilder.append('\u00a7').append('7').append(',').append(' '); }
                                                        affectedSlotsBuilder.append('\u00a7').append('e').append('#').append(slotLocation.getSlot());
                                                        trueSuccesses++;

                                                        // Solid set the damn command
                                                        if (onClick) {

                                                            // Edit Commands on Click
                                                            if (isClear) { targetSlot.setCommandsOnClick((String) null); }
                                                            else if (isRemove) { targetSlot.getCommandsOnClick().remove(removeIdx); }
                                                            else { targetSlot.addCommandsOnClick(builtCommand); }
                                                        }
                                                        if (onTake) {

                                                            // Edit Commands on Take
                                                            if (isClear) { targetSlot.setCommandsOnTake((String) null); }
                                                            else if (isRemove) { targetSlot.getCommandsOnTake().remove(removeIdx); }
                                                            else { targetSlot.addCommandsOnTake(builtCommand); }
                                                        }
                                                        if (onStore) {

                                                            // Edit Commands on Store
                                                            if (isClear) { targetSlot.setCommandsOnStore((String) null); }
                                                            else if (isRemove) { targetSlot.getCommandsOnStore().remove(removeIdx); }
                                                            else { targetSlot.addCommandsOnStore(builtCommand); }
                                                        }
                                                    }
                                                    affectedSlots = affectedSlotsBuilder.toString();
                                                }

                                                // If all of the slots were out of range.
                                                if (trueSuccesses == 0) {

                                                    // Note
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "The command had no effect. "));

                                                // Death
                                                } else {

                                                    String bindMessage;
                                                    if (isRemove) { bindMessage = "Removed command \u00a7e#" + removeIdx + "\u00a77 of "; }
                                                    else if (isClear) { bindMessage = "Cleared commands of ";  }
                                                    else { bindMessage = "Bound command \u00a7e" + builtCommand + "\u00a77 to ";  }

                                                    // If to slot?
                                                    if (!onCloseInstead && !onOpenInstead) {

                                                        // Note
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, bindMessage + "slots \u00a7e" + affectedSlots + "\u00a77 of container \u00a73" + template.getInternalName()));
                                                    } else if (onCloseInstead) {

                                                        // Note
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, bindMessage + "when a player closes container \u00a73" + template.getInternalName()));
                                                    } else {

                                                        // Note
                                                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, bindMessage + "when a player opens container \u00a73" + template.getInternalName()));
                                                    }
                                                }

                                                // Yes
                                                GCL_Templates.saveTemplate(template);
                                            }
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config commands"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config commands"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region View
                                case "view":
                                    //   0      1          2     3       4        args.Length
                                    // /goop containers config view <container>
                                    //   -      0          1     2       3        args[n]

                                    // Correct number of args?
                                    argsMinLength = 4;
                                    argsMaxLength = 4;
                                    usage = "/goop containers config view <container>";
                                    subcommand = "Config View";
                                    subcategory = "Containers - Config View";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 View for debugging templates.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the template you are editing.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaedd
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesnt exist!"));
                                        }

                                        if (!(sender instanceof Player)) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Sorry, but you cant open the debug view from the console. "));
                                        }

                                        // Well, supposing everything makes sense
                                        if (!failure) {

                                            // Should we scry?
                                            GOOPCManager.previewContainerContents(template, (Player) sender);
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config view"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config view"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Options
                                case "options":
                                    //   0      1          2     3       4         5        6       args.Length
                                    // /goop containers config view <container> <option> <value>
                                    //   -      0          1     2       3         4        5       args[n]

                                    // Correct number of args?
                                    argsMinLength = 6;
                                    argsMaxLength = 6;
                                    usage = "/goop containers config options <container> <option> <value>";
                                    subcommand = "Config Options";
                                    subcategory = "Containers - Config Options";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Enable or disable a few options.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the template you are editing.");
                                        logReturn.add("\u00a73 - \u00a7e<option> \u00a77Internal name of the template you are editing.");
                                        if (Gunging_Ootilities_Plugin.foundMythicLib) logReturn.add("\u00a73 --> \u00a7bMythicStation\u00a77 If this container is a unique station, default \u00a7bfalse\u00a77.");
                                        if (Gunging_Ootilities_Plugin.foundMythicCrucible) logReturn.add("\u00a73 --> \u00a7bMythicStation\u00a77 If this container is a unique station, default \u00a7bfalse\u00a77.");
                                        logReturn.add("\u00a73 --> \u00a7bAllowDrag\u00a77 If players are allowed to drag items through, by default \u00a7btrue\u00a77.");
                                        logReturn.add("\u00a73 --> \u00a7bDragOverflow\u00a77 If inventory slots are included in the provided slot placeholder, by default \u00a7bfalse\u00a77.");
                                        logReturn.add("\u00a73 --> \u00a7bEdgeFormations\u00a77 If container edges get a texture depending on their position relative to other edges.");
                                        if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a73 --> \u00a7bDuplicateEquipment\u00a77 If equipping the same MMOItem in two different slots stacks, by default \u00a7btrue\u00a77.");
                                        logReturn.add("\u00a73 - \u00a7e<value> \u00a77Usually \u00a7btrue\u00a77 or \u00a7bfalse\u00a77, yeah.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaded
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesn't exist!"));
                                        }

                                        int optionNumber = -1;
                                        switch (args[4].toLowerCase()) {
                                            case "mythiclibstation":
                                            case "mythicstation":
                                            case "mythiccruciblestation":
                                                optionNumber = 0;

                                                if (template != null && !template.isStation()) {

                                                    // Failure
                                                    failure = true;

                                                    // Note
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Option \u00a7bMythicStation\u00a77 only useable for containers of type \u00a7bSTATION\u00a77.")); }
                                                break;
                                            case "allowdrag":
                                                optionNumber = 1;
                                                break;
                                            case "dragoverflow":
                                                optionNumber = 2;
                                                break;
                                            case "duplicateequipment":
                                                optionNumber = 3;
                                                break;
                                            case "edgeformations":
                                                optionNumber = 4;
                                                break;
                                        }
                                        if (optionNumber == -1) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Please specify a valid option to edit: \u00a7bMythicLibStation\u00a77, \u00a7bEdgeFormations\u00a77, \u00a7bAllowDrag\u00a77, \u00a7bDragOverflow\u00a77, or \u00a7bDuplicateEquipment\u00a77 ~ instead of\u00a73 " + args[4]));
                                        }

                                        boolean val = false;

                                        if (OotilityCeption.BoolTryParse(args[5])) {

                                            // Parse it
                                            val = OotilityCeption.BoolParse(args[5]);

                                        } else {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected \u00a7btrue\u00a77 or \u00a7bfalse\u00a77 instead of \u00a73" + args[5]));
                                        }

                                        // Well, supposing everything makes sense
                                        if (!failure) {

                                            String logga = "";

                                            switch (optionNumber) {
                                                case 0:
                                                    template.setCustomMythicLibRecipe(val ? template.getInternalName() : null);

                                                    if (val) {
                                                        logga = "Made template\u00a73" + template.getInternalName() + "\u00a77 its own MythicLib station. ";
                                                    } else {
                                                        logga = "Made template\u00a73" + template.getInternalName() + "\u00a77 a variant of the workbench (can use it to craft anything you can craft in the workbench). ";
                                                    }
                                                    break;
                                                case 1:
                                                    template.setAllowDragEvents(val);

                                                    if (val) {
                                                        logga = "Allowed dragging items through storage slots for template\u00a73" + template.getInternalName();
                                                    } else {
                                                        logga = "Dragging items through storage slots for template\u00a73" + template.getInternalName() + "\u00a77 is now disabled. ";
                                                    }
                                                    break;
                                                case 2:
                                                    template.setProvidedDragExtendsToInventory(val);

                                                    if (val) {
                                                        logga = "Dragging items across \u00a73" + template.getInternalName() + "\u00a7a will\u00a77 include any inventory slots dragged-through in the provided slot placeholder.";
                                                    } else {
                                                        logga = "Dragging items across \u00a73" + template.getInternalName() + "\u00a7c will not\u00a77 include any inventory slots dragged-through in the provided slot placeholder.";
                                                    }
                                                    break;
                                                case 3:
                                                    template.setAllowDuplicateEquipment(val);

                                                    if (val) {
                                                        logga = "Equipping two of the same MMOItem in different slots of \u00a73" + template.getInternalName() + "\u00a7a will\u00a77 provide bonuses from both of them.";
                                                    } else {
                                                        logga = "Equipping two of the same MMOItem in different slots of \u00a73" + template.getInternalName() + "\u00a7c will ignore\u00a77 the second one, and provide no bonuses.";
                                                    }
                                                    break;
                                                case 4:
                                                    template.setEdgeFormations(val);
                                                    template.processContentEdges();

                                                    if (val) {
                                                        logga = "Edges of \u00a73" + template.getInternalName() + "\u00a77 will have different custom model data depending on adjacent edges, ranging from 2985 to 3000. ";
                                                    } else {
                                                        logga = "All edges of \u00a73" + template.getInternalName() + "\u00a77 will have custom model data of 3000.";
                                                    }
                                                    break;
                                            }

                                            // Yeah
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, logga)); }

                                            // Save it
                                            GCL_Templates.saveTemplate(template);
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config view"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config view"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Aliases
                                case "aliase":
                                case "aliases":
                                    //   0      1          2     3          4         5       6    args.Length
                                    // /goop containers config aliases <container> <slots> [alias]
                                    //   -      0          1     2          3         4       5    args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    argsMaxLength = 6;
                                    usage = "/goop containers config aliases <container> <slots> [alias]";
                                    subcommand = "Config Aliases";
                                    subcategory = "Containers - Config Aliases";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Rename slots for ease of use.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the template you are editing.");
                                        logReturn.add("\u00a73 - \u00a7e<slots> \u00a77Slots that will be given another name.");
                                        logReturn.add("\u00a73 - \u00a7e[alias] \u00a77Name to easily reference those slots.");
                                        logReturn.add("\u00a73 ---> \u00a77Not specifying one will clear the alias of these slots.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaedd
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesnt exist!"));
                                        }


                                        //Lets get that inven slot
                                        RefSimulator<String> slotFailure = new RefSimulator<>("");
                                        ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[4], null, slotFailure);

                                        // So, does the slot make no sense?
                                        if (slott.size() == 0) {
                                            // Failure
                                            failure = true;

                                            // If no slots were wrong
                                            if (slotFailure.getValue() == null && Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "The slots specified didn't make sense in this context. As if trying to access shulker boxes where there are none, or using container placeholder names in non-goop inventories.")); }
                                        }

                                        // Log
                                        if (slotFailure.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subcategory, slotFailure.getValue())); }

                                        // Well that's a simple success isn't it
                                        if (!failure) {

                                            // Get alias
                                            String tAlias = null; int tCOunt = 0;
                                            if (args.length == 6) { tAlias = args[5]; }

                                            // Perform all changes
                                            for (ItemStackSlot sl : slott) {
                                                if (sl == null) { continue; }
                                                if (sl.isInShulker()) { continue; }
                                                if (!sl.isInInventory()) { continue; }

                                                // If it fits in the container
                                                if (sl.getSlot() >= 0 && sl.getSlot() < template.getTotalSlotCount()) {

                                                    // Well, get that slot and set its damn alias
                                                    template.getSlotAt(sl.getSlot()).setAlias(tAlias);
                                                    tCOunt++;

                                                } else {

                                                    // Note
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Slot \u00a7e#" + sl.getSlot() + "\u00a77 is out of range!"));
                                                }

                                            }


                                            // If existed
                                            if (tAlias != null) {

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Bound \u00a7e" + tCOunt + "\u00a77 slots to the alias \u00a73" + tAlias));
                                            } else {

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Removed aliases from \u00a7e" + tCOunt + "\u00a77 slots."));
                                            }

                                            // Save I guess
                                            GCL_Templates.saveTemplate(template);

                                            // Force reprocess
                                            template.reloadAliases();
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config aliases"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config aliases"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Equipment
                                case "equipment":
                                    //   0      1          2      3          4          5         6      args.Length
                                    // /goop containers config equipment <container> <slots> <equipment?>
                                    //   -      0          1      2          3          4         5      args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    argsMaxLength = 6;
                                    usage = "/goop containers config equipment <container> <slots> [equipment?]";
                                    subcommand = "Config Equipment";
                                    subcategory = "Containers - Config Equipment";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Items put here will add their stats to the player.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the personal container template.");
                                        logReturn.add("\u00a73 - \u00a7e<slots> \u00a77Slots that you are editing.");
                                        logReturn.add("\u00a73 ---> \u00a7btrue \u00a77Allows duplicate equipment, two of the same MMOItem will sum.");
                                        logReturn.add("\u00a73 ---> \u00a7bfalse \u00a77Denies duplicate equipment, two of the same MMOItem wont work.");
                                        logReturn.add("\u00a73 - \u00a7e[equipment?] \u00a77Should these be equipment slots?");
                                        logReturn.add("\u00a73 ---> \u00a7btrue \u00a77Yes, items put here will grant stats to the player.");
                                        logReturn.add("\u00a73 ---> \u00a7bfalse \u00a77No, as usual, items wont grant their stats.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaedd
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesnt exist!"));
                                        }


                                        //Lets get that inven slot
                                        RefSimulator<String> slotFailure = new RefSimulator<>("");
                                        ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[4], null, slotFailure);
                                        Boolean duper = null;

                                        // Toggling Dupe Equipment
                                        if (args.length == 5) {

                                            // So, does the slot make no sense?
                                            if (OotilityCeption.BoolTryParse(args[4])) {

                                                // Parse it
                                                duper = OotilityCeption.BoolParse(args[4]);

                                            } else {
                                                // Failure
                                                failure = true;

                                                // If no slots were wrong
                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected \u00a7btrue\u00a77 or \u00a7bfalse\u00a77 instead of '\u00a73" + args[4] + "\u00a77' for wheter or not the container should stack items equipped of the same MMOItem (duplicate equipment). ")); }
                                            }

                                        } else {

                                            // So, does the slot make no sense?
                                            if (slott.size() == 0) {
                                                // Failure
                                                failure = true;

                                                // If no slots were wrong
                                                if (slotFailure.getValue() == null && Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "The slots specified didn't make sense in this context. As if trying to access shulker boxes where there are none, or using container placeholder names in non-goop inventories.")); }
                                            }

                                            // Log
                                            if (slotFailure.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subcategory, slotFailure.getValue())); }
                                        }

                                        // Get boolean
                                        boolean toggle = false;
                                        if (args.length >= 6) {
                                            if (OotilityCeption.BoolTryParse(args[5])) {

                                                // Parse bruh
                                                toggle = Boolean.parseBoolean(args[5]);

                                            } else {

                                                // Fail
                                                failure = true;

                                                // Notify
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected \u00a7btrue\u00a77 or \u00a7bfalse\u00a77 instead of \u00a7e" + args[5]));
                                            }
                                        }


                                        // Well that's a simple success isn't it
                                        if (!failure) {
                                            int totalCount = 0;

                                            // Editing allowing duplication
                                            if (duper == null) {

                                                // Perform all changes
                                                for (ItemStackSlot sl : slott) {

                                                    // If it is not null I guess
                                                    if (sl != null) {

                                                        // If its of simple inventory type
                                                        if (sl.getLocation() == SearchLocation.INVENTORY) {

                                                            if (template.getSlotAt(sl.getSlot()) != null) {

                                                                // Well, get that slot and set its damn alias
                                                                template.getSlotAt(sl.getSlot()).setForEquipment(toggle);
                                                                totalCount++;

                                                            } else {

                                                                // Note
                                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Slot \u00a7e#" + sl.getSlot() + "\u00a77 is out of range!"));
                                                            }
                                                        }
                                                    }
                                                }

                                                if (toggle) {

                                                    // Note
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Transformed \u00a7e" + totalCount + "\u00a77 slots into \u00a79Equipment\u00a77 slots."));
                                                } else {

                                                    // Note
                                                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Those \u00a7e" + totalCount + "\u00a77 are \u00a7cno longer\u00a77 equipment slots."));
                                                }

                                            // Edit Dupe Allowance
                                            } else {

                                                // Yes
                                                template.setAllowDragEvents(duper);

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Set template \u00a73" + template.getInternalName() + "\u00a77's Allow Duplicate Equipment to \u00a7b" + duper));
                                            }

                                            // Save I guess
                                            GCL_Templates.saveTemplate(template);

                                            // Refresh
                                            template.refreshEquipmentSlots();
                                        }

                                    // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config equipment"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config equipment"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Station
                                case "station":
                                    //   0      1          2      3          4          5        6      args.Length
                                    // /goop containers config station <container> resultSlot [slot]
                                    //   -      0          1      2          3          4        5      args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    argsMaxLength = 6;
                                    usage = "/goop containers config station <container> resultSlot [slot]";
                                    subcommand = "Config Station";
                                    subcategory = "Containers - Config Station";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Settings for custom crafting stations.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the station container template.");
                                        logReturn.add("\u00a73 - \u00a7eresultSlot \u00a77Means you are setting the result slot.");
                                        logReturn.add("\u00a73 - \u00a7e[slot] \u00a77Which slot shall be the result slot (clear if none specified). ");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaded
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesn't exist!"));

                                        // Station container??
                                        }

                                        Integer slotNumberLMAO = null;
                                        if (args.length >= 6) {

                                            if (OotilityCeption.IntTryParse(args[5])) {

                                                slotNumberLMAO = OotilityCeption.ParseInt(args[5]);

                                            } else {

                                                // Failure
                                                failure = true;

                                                // Note
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected an integer number for slot number to make the result slot, instead of \u00a73" + args[5]));
                                            }
                                        }

                                        // Yes
                                        GOOPCStation station = null;
                                        if (template != null && template.getDeployed() instanceof GOOPCStation) {
                                            station = (GOOPCStation) template.getDeployed(); } else {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "The station configuration command can only be used with \u00a7bSTATION\u00a77 type containers. "));
                                        }

                                        // Well that's a simple success isn't it
                                        if (!failure) {

                                            // Clear previous result slot
                                            for (GOOPCSlot slot : template.getSlotsContent().values()) {
                                                if (slot == null) { continue; }

                                                // No more RESULT
                                                if (slot.isForResult()) { slot.setSlotType(ContainerSlotTypes.DISPLAY); }
                                            }

                                            // If turning it ON?
                                            GOOPCSlot target = template.getSlotAt(slotNumberLMAO);
                                            if (target != null) { target.setSlotType(ContainerSlotTypes.RESULT); }

                                            // You too, noob
                                            template.processContentEdges();

                                            // Refresh vanilla mapping
                                            boolean toggle = slotNumberLMAO != null;
                                            if (Gunging_Ootilities_Plugin.foundMythicLib) { toggle = GooPMMOLib.a(station); }
                                            if (Gunging_Ootilities_Plugin.foundMythicCrucible) { toggle = GooPMythicCrucible.a(station); }

                                            if (toggle) {

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Container template \u00a73" + template.getInternalName() + "\u00a77 is now a MythicLib station of result slot\u00a7b " + (template.getResultSlot() != null ? template.getResultSlot().getSlotNumber() : "\u00a7cnull ~ very bad please tell gunging about this")));
                                            } else {

                                                // Note
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Container template \u00a73" + template.getInternalName() + "\u00a77 is no longer a MythicLib crafting station. "));
                                            }

                                            // Save I guess
                                            GCL_Templates.saveTemplate(template);

                                            // Refresh
                                            template.refreshEquipmentSlots();
                                        }

                                    // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config equipment"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config equipment"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Contents
                                case "contents":
                                    //   0      1          2     3           4           args.Length
                                    // /goop containers config contents <container>
                                    //   -      0          1     2           3           args[n]

                                    // Correct number of args?
                                    argsMinLength = 4;
                                    argsMaxLength = 4;
                                    usage = "/goop containers config contents <container>";
                                    subcommand = "Config Contents";
                                    subcategory = "Containers - Config Contents";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Edit the displayed items and the storage slots.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Internal name of the template you are editing.");
                                        logReturn.add("\u00a77Edit the 'contents' of your container in the following order:");
                                        logReturn.add("\u00a73       #1 \u00a77Display Items - Unmovable items for showcase");
                                        logReturn.add("\u00a7e           > \u00a77Put all items for display during this phase.");
                                        if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a7e           >>> \u00a77MMOItems will store their reference,");
                                        if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a7e               \u00a77so they will update when edited from");
                                        if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a7e               \u00a77/mmoitems browse");
                                        logReturn.add("\u00a73       #2 \u00a77Storage Slots - Where can players put items");
                                        logReturn.add("\u00a7e           > \u00a77Upon closing, the slots that don't hold the same");
                                        logReturn.add("\u00a7e             \u00a77item as the DISPLAY part will be marked as STORAGE");
                                        logReturn.add("\u00a78To get the edge material use \u00a76/goop containers config edgeMaterial");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Cannot be send from the console
                                        if (!(sender instanceof Player)) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Can only edit the contents of a container from in-game."));
                                        }

                                        // Make sure it is not loaded
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesn't exist!"));
                                        }

                                        // Well that's a simple success isn't it
                                        if (!failure) {

                                            // Unnecessary Ret
                                            RefSimulator<String> logAddition = new RefSimulator<>(null);

                                            // Mark such a player as an editor
                                            GOOPCManager.editionBegin((Player)sender, template, logAddition);

                                            // If nonnull
                                            if (logAddition.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subcategory, logAddition.getValue())); }
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config contents"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config contents"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Masks
                                case "masks":
                                    //   0      1          2     3          4            5       6          7      args.Length
                                    // /goop containers config masks <container name> <slots> [<type/id> <name>]
                                    //   -      0          1     2          3            4       5          6      args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    argsMaxLength = 7;
                                    usage = "/goop containers config masks <container name> <slots> [<type/id> <name>]";
                                    subcommand = "Config Masks";
                                    subcategory = "Containers - Config Masks";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Allows only certain items to be put in STORAGE slots.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the target container.");
                                        logReturn.add("\u00a73 - \u00a7e<slots> \u00a77The slots and ranges of slots you want to mask.");
                                        logReturn.add("\u00a73 - \u00a7e<type/id> \u00a77Specify if this is to restrict type or ids.");
                                        logReturn.add("\u00a7e  --> \u00a7etype \u00a77Specify an OnApply Mask. Will only allow mmoitems");
                                        logReturn.add("\u00a7e                  \u00a77that fit the mask to be placed in such slot.");
                                        logReturn.add("\u00a7e  --> \u00a7eid \u00a77Specify the exact internal MMOItem ID of the item");
                                        logReturn.add("\u00a7e                \u00a77that you intend to fit here.");
                                        logReturn.add("\u00a7e                \u00a7bUse \u00a7e+\u00a7b prefix to add items to existing list");
                                        logReturn.add("\u00a7e                \u00a7bUse \u00a7e-\u00a7b prefix to remove items from list");
                                        logReturn.add("\u00a7e                \u00a7bUse \u00a7e!\u00a7b prefix per-item for blacklist mode");
                                        logReturn.add("\u00a7e                \u00a7bYou may specify a comma separated list of items");
                                        logReturn.add("\u00a7e                \u00a7bEx:\u00a76 -MANGO,PEAR,BANANA&7 Removes items from list");
                                        logReturn.add("\u00a7e                \u00a7bEx:\u00a76 +APPLE,!PINEAPPLE&7 Adds apple to whitelist, pineapple to blacklist");
                                        logReturn.add("\u00a7e                \u00a7bEx:\u00a76 APPLE,!PINEAPPLE&7 Replaces previous whitelist and blacklists");
                                        logReturn.add("\u00a73 - \u00a7e<name> \u00a77The mask name (for types) or id (for ids) to check.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaded
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesnt exist!"));
                                        }

                                        //Lets get that inven slot
                                        RefSimulator<String> slotFailure = new RefSimulator<>("");
                                        ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[4], null, slotFailure);

                                        // So, does the slot make no sense?
                                        if (slott.size() == 0) {
                                            // Failure
                                            failure = true;

                                            // If no slots were wrong
                                            if (slotFailure.getValue() == null && Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "The slots specified didn't make sense in this context. As if trying to access shulker boxes where there are none, or using container placeholder names in non-goop inventories.")); }
                                        }

                                        // Log
                                        if (slotFailure.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subcategory, slotFailure.getValue())); }

                                        boolean forType = false, forID = false;
                                        ApplicableMask oaMask = null;
                                        if (args.length > 5) {

                                            forType = args[5].toLowerCase().equals("type");
                                            forID = args[5].toLowerCase().equals("id");

                                            // Failure if neither
                                            if (!forID && !forType) {

                                                // Failure
                                                failure = true;

                                                // Note
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Must specify what you're trying to mask, is it the \u00a7btype\u00a77 or the \u00a7bid\u00a77 of MMOItems?"));
                                            }

                                            if (forType && args.length > 6) {

                                                /*
                                                 * It feels horrible to specify a MMOItem Type and get said 'You gotta
                                                 * write it in the seed-packets.yml file snoozer; So this will actually
                                                 * create a type from the specified types and attempt tu succeed.
                                                 */
                                                oaMask = ApplicableMask.getMask(args[6]);

                                                // Succeess?
                                                if (oaMask == null) {

                                                    // Failure
                                                    failure = true;

                                                    // Note
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "For Type Mask restrictions, you must specify a comma-separated list of MMOItem Types, or any OnApplyMask that you previously defined in \u00a7eonapply-masks.yml"));
                                                }
                                            }
                                        }

                                        // Well that's a simple success isn't it
                                        if (!failure) {

                                            int tCOunt = 0;
                                            // Perform all changes
                                            for (ItemStackSlot sl : slott) {
                                                if (sl == null) { continue; }
                                                if (sl.getLocation() != SearchLocation.INVENTORY) { continue; }

                                                // If it fits in the container
                                                if (sl.getSlot() >= 0 && sl.getSlot() < template.getTotalSlotCount()) {

                                                    if (template.getSlotAt(sl.getSlot()) != null) {

                                                        // Well, get that slot and set its damn type mask
                                                        if (forType) {

                                                            // Do Appliccable Mask Shit
                                                            template.getSlotAt(sl.getSlot()).setTypeMask(oaMask);

                                                            // Otherwise it must be 5 iD
                                                        } else if (forID) {

                                                            // Snooze
                                                            String restrictions = null;
                                                            if (args.length > 6) { restrictions = args[6]; }
                                                            if (restrictions.equals("-all")) { restrictions = null; }

                                                            // Do Appliccable Mask Shit
                                                            template.getSlotAt(sl.getSlot()).loadKindIDRestrictions(restrictions);

                                                            // Must be-a removing
                                                        } else {

                                                            // Do Appliccable Mask Shit
                                                            template.getSlotAt(sl.getSlot()).setTypeMask(null);
                                                            template.getSlotAt(sl.getSlot()).loadKindIDRestrictions(null);
                                                        }

                                                        // Inkrease
                                                        tCOunt++;
                                                    } else {

                                                        // Note
                                                        if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Slot \u00a7e#" + sl.getSlot() + "\u00a77 of container is &cnull&7. Please contact &egunging&7 about this."));
                                                    }

                                                } else {

                                                    // Note
                                                    if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Slot \u00a7e#" + sl.getSlot() + "\u00a77 is out of range!"));
                                                }
                                            }

                                            if (forID || forType) {

                                                // Mention
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Masked the storage properties of \u00a7e" + tCOunt + "\u00a77 slots"));
                                            } else {

                                                // Mention
                                                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Removed all masks of the storage properties of \u00a7e" + tCOunt + "\u00a77 slots"));
                                            }

                                            // Save I guess
                                            GCL_Templates.saveTemplate(template);
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config masks"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config masks"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Rows
                                case "rows":
                                    //   0      1          2     3          4       5       args.Length
                                    // /goop containers config rows <container> <amount>
                                    //   -      0          1     2          3       4       args[n]

                                    // Correct number of args?
                                    argsMinLength = 5;
                                    argsMaxLength = 5;
                                    usage = "/goop containers config rows <container> <amount>";
                                    subcommand = "Config Rows";
                                    subcategory = "Containers - Config Rows";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Height of the container GUI.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the target container.");
                                        logReturn.add("\u00a73 - \u00a7e<amount> \u00a77Number of rows it should have. ");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaedd
                                        GOOPCTemplate template = GCL_Templates.getByInternalName(args[3]);
                                        if (template == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesnt exist!"));
                                        } else if (template.isPlayer()) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Cannot modify row number of \u00a73PLAYER\u00a77-type containers; Will remain as \u00a7e4\u00a77. "));
                                        }

                                        // Make sure rows parse
                                        Integer rows = null;
                                        if (OotilityCeption.IntTryParse(args[4])) {

                                            // Parse
                                            rows = OotilityCeption.ParseInt(args[4]);

                                            // If out of bounds
                                            if (rows < 1 || rows > 6) {

                                                // Failure
                                                failure = true;

                                                // Note
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Number of rows must be either 1, 2, 3, 4, 5, or 6."));
                                            }

                                        } else {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected integer number instead of \u00a73" + args[4]));
                                        }

                                        // Well that's a simple success isn't it
                                        if (!failure) {

                                            // Note
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Resized \u00a7e" + OotilityCeption.ParseColour(template.getTitle()) + "\u00a77 to have \u00a73" + rows + "\u00a77 rows."));
                                            template.setHeight(rows);

                                            // Rerow
                                            GCL_Templates.saveTemplate(template);
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config rows"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config rows"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                //region Restrictions
                                case "restrictions":
                                case "restrict":
                                    //   0      1          2      3          4                5         6       7       args.Length
                                    // /goop containers config restrictions <container name> <slots> <kind> <value>
                                    //   -      0          1      2          3                4         5      6        rgs[n]

                                    // Correct number of args?
                                    argsMinLength = 6;
                                    argsMaxLength = 7;
                                    usage = "/goop containers config restrictions <container> <slots> <kind> <value>";
                                    subcommand = "Config Restrictions";
                                    subcategory = "Containers - Config Restrictions";

                                    // Help form?
                                    if (args.length == 3)  {

                                        logReturn.add("\u00a7e______________________________________________");
                                        logReturn.add("\u00a73Containers - \u00a7b" + subcommand + ",\u00a77 Require permissions to use certain slots.");
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                        logReturn.add("\u00a73 - \u00a7e<container> \u00a77Name of the target container.");
                                        logReturn.add("\u00a73 - \u00a7e<slots> \u00a77The slots and ranges of slots you want to bind.");
                                        logReturn.add("\u00a73 - \u00a7e<kind> \u00a77Kind of restriction to setup.");
                                        logReturn.add("\u00a73 --> \u00a7ebehaviour");
                                        logReturn.add("\u00a73      * \u00a77If the player no longer has the permission, what");
                                        logReturn.add("\u00a73        \u00a77happens to items already stored in restricted slots?");
                                        logReturn.add("\u00a73 --> \u00a7elevel");
                                        logReturn.add("\u00a73      * \u00a77A range of levels to use this slot");
                                        logReturn.add("\u00a73 --> \u00a7eclass");
                                        logReturn.add("\u00a73      * \u00a77Class required to interact with");
                                        logReturn.add("\u00a73 --> \u00a7epermission");
                                        logReturn.add("\u00a73      * \u00a77List of permissions that player must match");
                                        logReturn.add("\u00a73 --> \u00a7eunlockable");
                                        logReturn.add("\u00a73      * \u00a77GooP Unlockable goals that must be unlocked");
                                        logReturn.add("\u00a73 - \u00a7e<value> \u00a77Value of the restriction.");

                                        // Correct number of args?
                                    } else if (args.length >= argsMinLength && args.length <= argsMaxLength) {

                                        // Make sure it is not loaedd
                                        GOOPCTemplate tTemplate = GCL_Templates.getByInternalName(args[3]);
                                        if (tTemplate == null) {

                                            // Failure
                                            failure = true;

                                            // Note
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "A container of that name doesnt exist!"));
                                        }

                                        //Lets get that inven slot
                                        RefSimulator<String> slotFailure = new RefSimulator<>("");
                                        ArrayList<ItemStackSlot> slott = OotilityCeption.getInventorySlots(args[4], null, slotFailure);

                                        // So, does the slot make no sense?
                                        if (slott.size() == 0) {
                                            // Failure
                                            failure = true;

                                            // If no slots were wrong
                                            if (slotFailure.getValue() == null && Gunging_Ootilities_Plugin.sendGooPFailFeedback) { logReturn.add(OotilityCeption.LogFormat(subcategory, "The slots specified didn't make sense in this context. As if trying to access shulker boxes where there are none, or using container placeholder names in non-goop inventories.")); }
                                        }

                                        // Log
                                        if (slotFailure.getValue() != null) { logReturn.add(OotilityCeption.LogFormat(subcategory, slotFailure.getValue())); }

                                        // Fail kind
                                        boolean ofBehaviour = false, ofList = false, ofQNR = false;
                                        String kind = args[5].toLowerCase().replace(" ", "_").replace("-", "_");
                                        switch (kind) {
                                            default:
                                                // Fail
                                                failure = true;

                                                // Notify
                                                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected a kind of restriction, be it \u00a7bbehaviour\u00a77, \u00a7blevel\u00a77, \u00a7bclass\u00a77, \u00a7bpermission\u00a77, or \u00a7bunlockable\u00a77 instead of \u00a7e" + kind));
                                                break;
                                            case "behaviour":
                                                ofBehaviour = true;
                                                break;
                                            case "level":
                                                ofQNR = true;
                                                break;
                                            case "class":
                                            case "permission":
                                            case "unlockable":
                                                ofList = true;
                                                break;
                                        }

                                        RestrictedBehaviour rb = null;
                                        QuickNumberRange qnr = null;
                                        ArrayList<ArrayList<String>> partitions = new ArrayList<>();
                                        boolean asClear = false;
                                        if (args.length >= 7) {
                                            if (ofBehaviour) {

                                                // Parse
                                                try { rb = RestrictedBehaviour.valueOf(args[6]); } catch (IllegalArgumentException ignored) {

                                                    // Fail
                                                    failure = true;

                                                    // Notify
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected a restricted behaviour, be it \u00a7bLOCK\u00a77, \u00a7bTAKE\u00a77, \u00a7bDROP\u00a77, or \u00a7bDESTROY\u00a77 instead of \u00a7e" + args[6]));
                                                }
                                            } else if (ofQNR) {

                                                qnr = QuickNumberRange.FromString(args[6]);

                                                if (qnr == null) {

                                                    // Failure
                                                    failure = true;

                                                    // Notify the error
                                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Expected a number or numeric range instead of \u00a73" + args[6] + "\u00a77. Ranges are specified with two numbers separated by \u00a7bb\u00a77. Example \u00a7e-4\u00a77 and \u00a7e32.4\u00a77: \u00a7b-4..32.5\u00a77. They are inclusive, and you may not specify either of the bounds (\u00a7b10..\u00a77 will match anything equal or greater than 10)."));
                                                }

                                            } else if (ofList) {

                                                // Parse Array
                                                partitions = GCSR_Permission.Parse(args[6]);
                                            }

                                        } else {
                                            asClear = true;
                                        }

                                        // Well that's a simple success isn't it
                                        if (!failure) {
                                            int tCOunt = 0;

                                            // Perform all changes
                                            for (ItemStackSlot sl : slott) {

                                                // If it is not null I guess
                                                if (sl != null) {

                                                    // If its of simple inventory type
                                                    if (sl.getLocation() == SearchLocation.INVENTORY) {

                                                        // If it fits in the container
                                                        if (sl.getSlot() >= 0 && sl.getSlot() < tTemplate.getTotalSlotCount()) {

                                                            GOOPCSlot slt = tTemplate.getSlotAt(sl.getSlot());

                                                            if (slt != null) {

                                                                // Update behaviour
                                                                if (ofBehaviour) {

                                                                    if (asClear) { rb = RestrictedBehaviour.TAKE; }

                                                                    // Set behaviour-yo
                                                                    slt.setRestrictedBehaviour(rb);

                                                                } else {

                                                                    // Well, get update restriction... start by keeping all others
                                                                    ArrayList<SlotRestriction> srs = new ArrayList<>(slt.getRestrictions());
                                                                    ArrayList<SlotRestriction> srsLast = new ArrayList<>();

                                                                    switch (kind) {
                                                                        case "level":

                                                                            // Keep all others
                                                                            for (SlotRestriction sr : srs) { if (!(sr instanceof GCSR_Level)) { srsLast.add(sr); }}

                                                                            // Build and add new
                                                                            if (!asClear) { srsLast.add(new GCSR_Level(qnr)); }
                                                                            break;
                                                                        case "class":

                                                                            // Keep all others
                                                                            for (SlotRestriction sr : srs) { if (!(sr instanceof GCSR_Class)) { srsLast.add(sr); }}

                                                                            // Build and add new ones
                                                                            if (!asClear) for (ArrayList<String> str : partitions) { if (str.isEmpty()) { continue; } srsLast.add(new GCSR_Class(str)); }
                                                                            break;
                                                                        case "permission":

                                                                            // Keep all others
                                                                            for (SlotRestriction sr : srs) { if (!(sr instanceof GCSR_Permission)) { srsLast.add(sr); }}

                                                                            // Build and add new ones
                                                                            if (!asClear) for (ArrayList<String> str : partitions) { if (str.isEmpty()) { continue; } srsLast.add(new GCSR_Permission(str)); }
                                                                            break;
                                                                        case "unlockable":

                                                                            // Keep all others
                                                                            for (SlotRestriction sr : srs) { if (!(sr instanceof GCSR_Unlockable)) { srsLast.add(sr); }}

                                                                            // Build and add new ones
                                                                            if (!asClear) for (ArrayList<String> str : partitions) { if (str.isEmpty()) { continue; } srsLast.add(new GCSR_Unlockable(str)); }
                                                                            break;
                                                                    }

                                                                    slt.clearRestrictions();
                                                                    for (SlotRestriction sr : srsLast) { slt.addRestriction(sr); }
                                                                }

                                                                tCOunt++;

                                                            } else {

                                                                // Note
                                                                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Containers - Equipment", "Slot \u00a7e#" + sl.getSlot() + "\u00a77 is \u00a7cnull\u00a77, notify \u00a7egunging\u00a77 about this."));
                                                            }

                                                        } else {

                                                            // Note
                                                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) logReturn.add(OotilityCeption.LogFormat("Containers - Equipment", "Slot \u00a7e#" + sl.getSlot() + "\u00a77 is out of range!"));
                                                        }
                                                    }
                                                }
                                            }

                                            // Note
                                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) logReturn.add(OotilityCeption.LogFormat(subcategory, "Updated restrictions of \u00a7e" + tCOunt + "\u00a77 slots."));

                                            // Save I guess
                                            GCL_Templates.saveTemplate(tTemplate);
                                        }

                                        // Incorrect number of args
                                    } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                        // Notify Error
                                        if (args.length >= argsMinLength) {
                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a7e many\u00a77 args). For info: \u00a7e/goop containers config restriction"));

                                        } else {

                                            logReturn.add(OotilityCeption.LogFormat(subcategory, "Incorrect usage (too\u00a76 few\u00a77 args). For info: \u00a7e/goop containers config restriction"));
                                        }

                                        // Notify Usage
                                        logReturn.add("\u00a73Usage: \u00a7e" + usage);
                                    }
                                    break;
                                //endregion
                                default:
                                    // I have no memory of that shit
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Containers - Config", "'\u00a73" + args[2] + "\u00a77' is not a valid Containers Config Action! do \u00a7e/goop containers config\u00a77 for the list of actions."));
                                    break;
                            }

                            // Incorrect number of args
                        } else if (args.length == 2) {

                            logReturn.add("\u00a7e______________________________________________");
                            logReturn.add("\u00a73Containers - \u00a7bConfig, \u00a77Edits and/or creates a container.");
                            logReturn.add("\u00a73Usage: \u00a7e/goop containers config {action}");
                            logReturn.add("\u00a73 - \u00a7e{action} \u00a77What action to perform:");
                            logReturn.add("\u00a73 --> \u00a7enew <container name> <container type>");
                            logReturn.add("      \u00a7e<type> \u00a77How is your container accessed? Is it persistent?");
                            logReturn.add("\u00a73       *\u00a7bPHYSICAL \u00a77Same functionality as a chest.");
                            logReturn.add("\u00a73       *\u00a7bPERSONAL \u00a77Same functionality as an enderchest.");
                            logReturn.add("\u00a73       *\u00a7bSTATION \u00a77Drops 'stored' items to the ground when closed.");
                            logReturn.add("\u00a73 --> \u00a7econtents <container name>");
                            logReturn.add("\u00a73      * \u00a77Edit the 'contents' of your container in the following order:");
                            logReturn.add("\u00a73       #1 \u00a77Display Items - Unmovable items for showcase");
                            logReturn.add("\u00a7e           > \u00a77Put all items for display during this phase.");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a7e           >>> \u00a77MMOItems will store their reference,");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a7e               \u00a77so they will update when edited from");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) logReturn.add("\u00a7e               \u00a77/mmoitems browse");
                            logReturn.add("\u00a73       #2 \u00a77Storage Slots - Where can players put items");
                            logReturn.add("\u00a7e           > \u00a77Upon closing, the slots that dont hold the same");
                            logReturn.add("\u00a7e             \u00a77item as the DISPLAY part will be marked as STORAGE");
                            logReturn.add("\u00a73 --> \u00a7etitle");
                            logReturn.add("\u00a73      * \u00a77Sets the title players will see.");
                            logReturn.add("\u00a73 --> \u00a7erows");
                            logReturn.add("\u00a73      * \u00a77Modifies the number of rows (size of container).");
                            logReturn.add("\u00a7c      * Only accepted row counts are: 1, 2, 3, 4, 5, and 6");
                            logReturn.add("\u00a73 --> \u00a7ecommands");
                            logReturn.add("\u00a73      * \u00a77Allows you to bind commands to slots.");
                            logReturn.add("\u00a73 --> \u00a7eedgeMaterial");
                            logReturn.add("\u00a73      * \u00a77Obtain the material placeholder for edges.");
                            logReturn.add("\u00a73 --> \u00a7ealiases");
                            logReturn.add("\u00a73      * \u00a77Allows you to name groups of slots.");
                            logReturn.add("\u00a73        - Only one alias per slot is supported. New ones will overwrite previous.");
                            logReturn.add("\u00a73 --> \u00a7emasks");
                            logReturn.add("\u00a73      * \u00a77Which items can be put in the specified slots?");
                            logReturn.add("\u00a73        - Belong to a MMOItems TYPE or ID, helmets, etc...");
                            logReturn.add("\u00a73 --> \u00a7erestrictions");
                            logReturn.add("\u00a73      * \u00a77Which players can put items in the specified slots?");
                            logReturn.add("\u00a73        - Permission restrictions, class, level...");
                            logReturn.add("\u00a73 --> \u00a7eview");
                            logReturn.add("\u00a73      * \u00a77See the complete layout of the container.");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) {
                                logReturn.add("\u00a73 --> \u00a7eequipment");
                                logReturn.add("\u00a73      * \u00a77Decide if MMOItems put here are equipped.");
                            }

                        } else {

                            // Notify
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                logReturn.add(OotilityCeption.LogFormat("Containers - Config", "Incorrect usage. For info: \u00a7e/goop containers config"));
                                logReturn.add("\u00a73Usage: \u00a7e/goop containers config {action}");
                            }
                        }
                        break;
                    //endregion
                    default:
                        // I have no memory of that shit
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) logReturn.add(OotilityCeption.LogFormat("Containers", "'\u00a73" + args[1] + "\u00a77' is not a valid Containers action! do \u00a7e/goop containers\u00a77 for the list of actions."));
                        break;
                }

            } else if (args.length == 1) {
                logReturn.add("\u00a7e______________________________________________");
                logReturn.add("\u00a73Containers, \u00a77Custom menus and storage stations.");
                logReturn.add("\u00a73Usage: \u00a7e/goop containers {action}");
                logReturn.add("\u00a73 - \u00a7e{action} \u00a77What actions to perform:");
                logReturn.add("\u00a73 --> \u00a7eopen <container type> [player] [mode]");
                logReturn.add("\u00a73      * \u00a77Opens a non-physical container for a player.");
                logReturn.add("\u00a73 --> \u00a7esee <container type> <player>");
                logReturn.add("\u00a73      * \u00a77Opens target player's non-physical container");
                logReturn.add("\u00a73        \u00a77to the admin calling this command.");
                logReturn.add("\u00a73 --> \u00a7egoop containers access <container type> [player] [w x y z]");
                logReturn.add("\u00a73      * \u00a77Allows player to open a physical container.");
                logReturn.add("\u00a73      * \u00a77Will create the container if it doesnt exist.");
                logReturn.add("\u00a73 --> \u00a7econfig {action}");
                logReturn.add("\u00a73      * \u00a77Allows in-game configuration of containers.");

            } else {
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                    logReturn.add(OotilityCeption.LogFormat("Containers", "Incorrect usage. For info: \u00a7e/goop containers"));
                    logReturn.add("\u00a73Usage: \u00a7e/goop containers {action}");
                }
            }

        // No perms
        } else {
            // Tell him lmao
            //logReturn.add(OotilityCeption.LogFormat("Containers is a feature coming soon!"));
            logReturn.add(OotilityCeption.LogFormat("\u00a7c\u00a7oYou don't have permission to use containers this way!"));
        }

        //Set Log Return Urn Value
        logReturnUrn.SetValue(logReturn);
    }

    /**
     * Parses colors apparently
     */
    public static void delayedMessage(@Nullable Player player, @Nullable String message, int concurrencyID) {
        if (message == null) { return; }
        message = OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(message, player, player, null, null));
        OotilityCeption.SendMessageNextTick(player, OotilityCeption.ParseColour(message), concurrencyID); }

    /**
     * Parses colors apparently
     */
    public static void message(@NotNull Player player, @Nullable String message) {
        if (message == null) { return; }
        message = OotilityCeption.ParseConsoleCommand(message, player, player, null, null);
        player.sendMessage(message); }

    @SuppressWarnings("ConstantConditions") @Override public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args) {

        // Other stuff
        GPCProtection protection = null;
        // If it is a containers protection thing
        switch (command.getName().toLowerCase()) {
            default: break;
            //region G. Remove || Private || Public
            case "gremove":
                if (protection == null) { protection = GPCProtection.UNREGISTERED; }
            case "gprivate":
                if (protection == null) { protection = GPCProtection.PRIVATE; }
            case "gpublic":
                if (protection == null) { protection = GPCProtection.PUBLIC; }
                //      0       args.Length
                // /gremove
                //      -       args[n]

                // Gather Player
                if (sender instanceof Player) {

                    // If not specified, it must be the sender
                    Player owner = (Player) sender;

                    // Just target location I guess?
                    Block block = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                    // Failure reasons
                    if (OotilityCeption.IsAirNullAllowed(block)) {

                        // Looking at air
                        delayedMessage(owner, GTranslationManager.c(GTL_Containers.GPROTECTION_BLOCK_NOT_FOUND), 3);
                        return false;
                    }

                    // Edit all containers there
                    ArrayList<GPCContent> inherentContents = GCL_Physical.getContainersAt(block.getLocation());
                    if (inherentContents.size() == 0) {

                        // Looking at air
                        delayedMessage(owner, GTranslationManager.c(GTL_Containers.GPROTECTION_BLOCK_NOT_CONTAINER), 3);
                        return false;
                    }

                    // Evaluate all of them
                    for (GPCContent inherentContent : inherentContents) {
                        if (inherentContent == null) { continue; }

                        // Is it unprotected currently?
                        if (inherentContent.isUnprotected()) {

                            if (protection == GPCProtection.UNREGISTERED) {

                                // Nothing happened
                                delayedMessage(owner, GTranslationManager.c(GTL_Containers.GREMOVE_NO_CHANGE).replace("%container%", inherentContent.getContainer().getTemplate().getTitle()), 3);
                                return false;

                            } else {

                                // Created protection
                                delayedMessage(owner, GTranslationManager.c(GTL_Containers.GREMOVE_CREATE_SUCCESS).replace("%container%", inherentContent.getContainer().getTemplate().getTitle()), 3);
                            }

                        } else {

                            // To change protection type, must be *the* owner of the container.
                            if (!inherentContent.isOwner(owner.getUniqueId()) && !owner.isOp()) {

                                // Looking at air
                                delayedMessage(owner, GTranslationManager.c(GTL_Containers.GPROTECTION_NOT_OWNER).replace("%container%", inherentContent.getContainer().getTemplate().getTitle()), 3);
                                return false;
                            }

                            // Removing
                            if (protection == GPCProtection.UNREGISTERED) {

                                // Nothing happened
                                delayedMessage(owner, GTranslationManager.c(GTL_Containers.GREMOVE_REMOVE_SUCCESS).replace("%container%", inherentContent.getContainer().getTemplate().getTitle()), 3);

                            // Editing
                            } else {

                                // Created protection
                                delayedMessage(owner, GTranslationManager.c(GTL_Containers.GREMOVE_EDITION_SUCCESS).replace("%container%", inherentContent.getContainer().getTemplate().getTitle()), 3);
                            }
                        }

                        // Change the protection without changing the owner.
                        inherentContent.editProtection(protection, owner.getUniqueId());
                        inherentContent.getContainer().saveLocationItems(inherentContent);
                    }

                } else {

                    // Notify thay console
                    sender.sendMessage(OotilityCeption.LogFormat("You can only edit GooP Containers protections in-game."));
                }
                break;
            //endregion
            //region G. Modify
            case "gmodify":
                //      0           1           args.Length
                // /gmodify <player name ig>
                //      -           0           args[n]

                // Gather Player
                if (sender instanceof Player) {

                    // If not specified, it must be the sender
                    Player caller = (Player) sender;

                    // Is they using it as /help?
                    if (args.length != 1) {

                        // Send message to thay
                        delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_HELP_1), 8);
                        delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_HELP_2), 9);
                        delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_HELP_3), 10);
                        delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_HELP_4), 11);

                    // Called for usage
                    } else {

                        // Just target location I guess?
                        Block block = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                        // Failure reasons
                        if (OotilityCeption.IsAirNullAllowed(block)) {

                            // Looking at air
                            delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_BLOCK_NOT_FOUND), 3);
                            return false;
                        }

                        // Edit all containers there
                        ArrayList<GPCContent> inherentContents = GCL_Physical.getContainersAt(block.getLocation());
                        if (inherentContents.size() == 0) {

                            // Looking at air
                            delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_BLOCK_NOT_CONTAINER), 3);
                            return false;
                        }

                        // Get data of new addition
                        String nmae = args[0];
                        boolean removeInstead = nmae.startsWith("-");
                        boolean asAdmin = nmae.startsWith("@");
                        if (removeInstead || asAdmin) { nmae = args[0].substring(1); }  //Remove special char

                        // Get offline player
                        OfflinePlayer target = Bukkit.getOfflinePlayer(nmae);
                        if (!target.hasPlayedBefore()) {

                            // Looking at air
                            delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_PLAYER_NOT_FOUND).replace("%player%", nmae), 3);
                            return false;
                        }

                        // For all contents
                        for (GPCContent content : inherentContents) {

                            /*
                             * No unregistered ones
                             */
                            if (content.isUnprotected()) {

                                // Looking at air
                                delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_NOT_ADMIN).replace("%container%", content.getContainer().getTemplate().getTitle()), 3);
                                return false;
                            }

                            /*
                             * If they are not admins of the container, they cannot use this command at all
                             */
                            if (!content.isAdmin(caller.getUniqueId()) && !caller.isOp()) {

                                // Looking at air
                                delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_NOT_ADMIN).replace("%container%", content.getContainer().getTemplate().getTitle()), 3);
                                return false;
                            }

                            /*
                             * To add admins, they must be the owner
                             */
                            if (asAdmin && !content.isOwner(caller.getUniqueId()) && !caller.isOp()) {

                                // Looking at air
                                delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_NOT_OWNER).replace("%container%", content.getContainer().getTemplate().getTitle()), 3);
                                return false;
                            }

                            /*
                             * To remove admins, they must be the owner
                             */
                            if (removeInstead && content.isAdmin(target.getUniqueId()) && !content.isOwner(caller.getUniqueId()) && !caller.isOp()) {

                                // Looking at air
                                delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_NOT_OWNER).replace("%container%", content.getContainer().getTemplate().getTitle()), 3);
                                return false;
                            }

                            /*
                             * No one can remove the owner
                             */
                            if (content.isOwner(target.getUniqueId())) {

                                // Looking at air
                                delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_CANNOT_REMOVE).replace("%container%", content.getContainer().getTemplate().getTitle()), 3);
                                return false;
                            }

                            // Add/Remove Member/Admin
                            if (removeInstead) {

                                // Remove qld scrub
                                content.getContainerMembers().remove(target.getUniqueId());
                                content.getContainerAdmins().remove(target.getUniqueId());

                                // Looking at air
                                delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_REMOVE_MEMBER).replace("%container%", content.getContainer().getTemplate().getTitle()).replace("%player%", nmae), 3);

                            // Adding Member/Admin
                            } else {

                                // Add member if not member already
                                if (!content.getContainerMembers().contains(target.getUniqueId())) { content.getContainerMembers().add(target.getUniqueId()); }

                                // AS admin perhaps?
                                if (asAdmin) {

                                    // Add admin if not admin already
                                    if (!content.getContainerAdmins().contains(target.getUniqueId())) { content.getContainerAdmins().add(target.getUniqueId()); }

                                    // Looking at air
                                    delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_ADD_ADMIN).replace("%container%", content.getContainer().getTemplate().getTitle()).replace("%player%", nmae), 3);

                                // Adding member
                                } else {

                                    // Force and add

                                    // Looking at air
                                    delayedMessage(caller, GTranslationManager.c(GTL_Containers.GMODIFY_ADD_MEMBER).replace("%container%", content.getContainer().getTemplate().getTitle()).replace("%player%", nmae), 3);
                                }
                            }

                            // Save changes
                            content.getContainer().saveLocationItems(content);
                        }
                    }

                } else {

                    // Notify thay console
                    sender.sendMessage(OotilityCeption.LogFormat("You can only modify GooP Containers members in-game."));
                }
                break;
            //endregion
            //region G. Info
            case "ginfo":
                //    0         1           args.Length
                // /ginfo
                //    -         0           args[n]

                // Gather Player
                if (sender instanceof Player) {

                    // If not specified, it must be the sender
                    Player caller = (Player) sender;

                    /*
                     * Kinds of container information that could be gotten
                     */
                    GOOPCPersonal asPersonal = null;
                    UUID personalOwner = null;
                    HashMap<UUID, NameVariable> personalSeens;

                    ArrayList<GPCContent> asPhysical = null;

                    /*
                     * Bag functionality of Personal Container ~ Held Item
                     */
                    if (Gunging_Ootilities_Plugin.foundMMOItems) {

                        // Get Held item
                        ItemStack heldItem = ((Player) sender).getInventory().getItemInMainHand();

                        // Found?
                        if (heldItem != null) {

                            // Does it open a container?
                            String container = GooPMMOItems.GetStringStatValue(heldItem, GooPMMOItems.CONTAINER, caller, false);

                            // Existed?
                            if (container != null) {

                                // Get Container and ID
                                String encodedName = "";
                                String encodedOwner = "";

                                // New vs Old
                                if (container.contains(" ")) {

                                    // Split
                                    String[] splt = container.split(" ");
                                    if (splt.length >= 1) { encodedName = splt[0]; if (splt.length >= 2) { encodedOwner = splt[1]; } }

                                } else {

                                    // Assume it is
                                    encodedName = container;
                                }

                                // Get UUID?
                                asPersonal = GCL_Personal.getByInternalName(encodedName);
                                personalOwner = OotilityCeption.UUIDFromString(encodedOwner);
                            }
                        }
                    }

                    // No personal found, lets try physical
                    if (asPersonal == null) {

                        // Just target location I guess?
                        Block block = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                        // Failure reasons
                        if (OotilityCeption.IsAirNullAllowed(block)) {

                            // Looking at air
                            delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_BLOCK_NOT_FOUND), 3);
                            return false;
                        }

                        // Edit all containers there
                        ArrayList<GPCContent> inherentContents = GCL_Physical.getContainersAt(block.getLocation());
                        if (inherentContents.size() == 0) {

                            // Looking at air
                            delayedMessage(caller, GTranslationManager.c(GTL_Containers.GPROTECTION_BLOCK_NOT_CONTAINER), 3);
                            return false;
                        }

                        // Evaluate all of them
                        asPhysical = inherentContents;
                    }

                    // Log Personal Container
                    if (asPersonal != null) {
                        message(caller, GTranslationManager.c(GTL_Containers.GINFO_TITLE).replace("%container%", asPersonal.getTemplate().getTitle()));

                        // Found uuid?
                        if (personalOwner == null) {

                            // Quote the name of the container:
                            message(caller, GTranslationManager.c(GTL_Containers.GINFO_PERSONAL_NO_OWNER));

                            // Not quite
                        } else {

                            // Quote the name of the container:
                            message(caller, GTranslationManager.c(GTL_Containers.GINFO_PERSONAL_OWNER).replace("%uuid%", personalOwner.toString()));
                        }

                        // Get Seens
                        personalSeens = asPersonal.getSeens(personalOwner);
                        if (personalSeens.size() != 0) {

                            // List them
                            message(caller, GTranslationManager.c(GTL_Containers.GINFO_SEENS_TITLE));

                            // List
                            for (NameVariable seen : personalSeens.values()) {

                                // Get time difference
                                String otf = seen.getIdentifier();
                                String message = seen.getValue();

                                // Yes
                                OptimizedTimeFormat timeFormat = new OptimizedTimeFormat(otf);
                                String timeval = OotilityCeption.NicestTimeValueFrom((double) OotilityCeption.SecondsElapsedSince(timeFormat, OptimizedTimeFormat.Current()));

                                // Log Message
                                message(caller, GTranslationManager.c(GTL_Containers.GINFO_SEEN).replace("%time%", timeval).replace("%message%", message));
                            }
                        }
                    }

                    // Log Physical Containers
                    if (asPhysical != null && asPhysical.size() > 0) {

                        // Evaluate all of them
                        for (GPCContent content : asPhysical) {

                            // Quote the name of the container:
                            message(caller, GTranslationManager.c(GTL_Containers.GINFO_TITLE).replace("%container%", content.getContainer().getTemplate().getTitle()));
                            message(caller, GTranslationManager.c(GTL_Containers.GINFO_PHYSICAL_LOCATION).replace("%location%", OotilityCeption.BlockLocation2String(content.getLocation())));
                            message(caller, GTranslationManager.c(GTL_Containers.GINFO_PHYSICAL_PROTECTION).replace("%protection%", OotilityCeption.TitleCaseConversion(content.getProtectionType().toString())));

                            // If not unregistered
                            if (!content.isUnprotected()) {

                                // List members and owner
                                message(caller, GTranslationManager.c(GTL_Containers.GINFO_PHYSICAL_MEMBERS_TITLE));

                                // Owner
                                message(caller, GTranslationManager.c(GTL_Containers.GINFO_PHYSICAL_OWNER).replace("%player%", Bukkit.getOfflinePlayer(content.getContainerOwner()).getName()));

                                // Admins
                                for (UUID asmin : content.getContainerAdmins()) {

                                    // If it is not the owner (I think this is impossible to be true but whatever)
                                    if (!content.isOwner(asmin)) {

                                        // List admin
                                        message(caller, GTranslationManager.c(GTL_Containers.GINFO_PHYSICAL_ADMIN).replace("%player%", Bukkit.getOfflinePlayer(asmin).getName()));
                                    }
                                }

                                // Members
                                for (UUID membr : content.getContainerMembers()) {

                                    // If it is not the owner (I think this is impossible to be true but whatever)
                                    if (!content.isAdmin(membr)) {

                                        // List admin
                                        message(caller, GTranslationManager.c(GTL_Containers.GINFO_PHYSICAL_OWNER).replace("%player%", Bukkit.getOfflinePlayer(membr).getName()));
                                    }
                                }
                            }

                            // Get Seens
                            personalSeens = content.getSeens();
                            if (personalSeens.size() != 0) {

                                // List them
                                message(caller, GTranslationManager.c(GTL_Containers.GINFO_SEENS_TITLE));

                                // List
                                for (NameVariable seen : personalSeens.values()) {

                                    // Get time difference
                                    String otf = seen.getIdentifier();
                                    String message = seen.getValue();

                                    // Yes
                                    OptimizedTimeFormat timeFormat = new OptimizedTimeFormat(otf);
                                    String timeval = OotilityCeption.NicestTimeValueFrom((double) OotilityCeption.SecondsElapsedSince(timeFormat, OptimizedTimeFormat.Current()));

                                    // Log Message
                                    message(caller, GTranslationManager.c(GTL_Containers.GINFO_SEENS_TITLE).replace("%time%", timeval).replace("%message%", message));
                                }
                            }
                        }
                    }

                } else {

                    // Notify thay console
                    sender.sendMessage(OotilityCeption.LogFormat("You can see container info in-game by the use of this command. "));
                }
                break;
            //endregion
        }

        return false;
    }
}
