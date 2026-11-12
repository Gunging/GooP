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
 * The {@code /goop mythicmobs} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdMythicMobs {

    private GooPCmdMythicMobs() { }

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
        return null;
    }
}
