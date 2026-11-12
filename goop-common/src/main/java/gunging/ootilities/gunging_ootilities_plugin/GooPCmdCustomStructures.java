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
 * The {@code /goop customstructures} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdCustomStructures {

    private GooPCmdCustomStructures() { }

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
        return null;
    }
}
