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
 * The {@code /goop nbt} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdNBT {

    private GooPCmdNBT() { }

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
        // /goop nbt {action}
        //   -      0         1     args[n]

        // To extract log return
        // Delegate onto NBT
        onCommand_GooPNBT(self, sender, command, label, args, senderLocation, chained, commandChain, logReturnUrn, failMessage);

        // Extract
        if (logReturnUrn.getValue() != null) {

            // For
            for (String ret : logReturnUrn.getValue()) {

                // Add
                logReturn.add(ret);
            }
        }
        return null;
    }

    static void onCommand_GooPNBT(@NotNull GungingOotilities self, @NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args, @Nullable Location senderLocation, boolean chained, @Nullable SuccessibleChain commandChain, @NotNull RefSimulator<List<String>> logReturnUrn, @Nullable String failMessage) {
        OotilityCeption oots = self.oots;
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
                        subcommand = OotilityCeption.TitleCaseConversion(GooP_MinecraftVersions.GetAttributeName(attribute));
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
}
