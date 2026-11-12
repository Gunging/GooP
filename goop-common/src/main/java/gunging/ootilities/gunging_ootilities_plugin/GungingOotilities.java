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
            GooPCommandContext ctx = new GooPCommandContext(sender, command, label, args, senderIsPlayer, senderLocation,
                    failMessage, successibilityFlare, logReturn, supp, cmd, successSlots, permission, trueArgs, chained,
                    chanedArgs, commandChain, chainedNoLocation, logReturnUrn, senderUUIDIG, oots, this);
            switch (cmd) {
                //region stasis
                case stasis:
                    { Boolean result = GooPCmdStasis.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region help
                case help:
                    { Boolean result = GooPCmdHelp.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region consumeitem
                case consumeitem:
                    { Boolean result = GooPCmdConsumeItem.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region testinventory
                case testinventory:
                    { Boolean result = GooPCmdTestInventory.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region gamerule
                case gamerule:
                    { Boolean result = GooPCmdGamerule.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region mmoitems
                case mmoitems:
                    { Boolean result = GooPCmdMMOItems.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region optifine
                case optifine:
                    { Boolean result = GooPCmdOptiFine.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region reload
                case reload:
                    { Boolean result = GooPCmdReload.run(ctx); if (result != null) { return result; } }
                    break;
                //region nbt
                case nbt:
                    { Boolean result = GooPCmdNBT.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region unlocc
                case unlockables:
                    { Boolean result = GooPCmdUnlockables.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region customstructures
                case customstructures:
                    { Boolean result = GooPCmdCustomStructures.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region mythicmobs
                case mythicmobs:
                    { Boolean result = GooPCmdMythicMobs.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region scoreboard
                case scoreboard:
                    { Boolean result = GooPCmdScoreboard.run(ctx); if (result != null) { return result; } }
                    break;
                //region containers
                case containers:
                    { Boolean result = GooPCmdContainers.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region grief
                case grief:
                    { Boolean result = GooPCmdGrief.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region compare
                case compare:
                    { Boolean result = GooPCmdCompare.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region tell
                case tell:
                    { Boolean result = GooPCmdTell.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region tp
                case tp:
                    { Boolean result = GooPCmdTp.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region delay
                case delay:
                    { Boolean result = GooPCmdDelay.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region chance
                case chance:
                    { Boolean result = GooPCmdChance.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region vault
                case vault:
                    { Boolean result = GooPCmdVault.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region sudo
                case sudo:
                    { Boolean result = GooPCmdSudo.run(ctx); if (result != null) { return result; } }
                    break;
                //endregion
                //region permission
                case permission:
                    { Boolean result = GooPCmdPermission.run(ctx); if (result != null) { return result; } }
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
