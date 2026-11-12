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
 * The {@code /goop scoreboard} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdScoreboard {

    private GooPCmdScoreboard() { }

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
        return null;
        //endregion
    }
}
