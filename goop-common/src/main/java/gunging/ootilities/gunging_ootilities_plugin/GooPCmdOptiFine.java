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
 * The {@code /goop optifine} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdOptiFine {

    private GooPCmdOptiFine() { }

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
        return null;
    }
}
