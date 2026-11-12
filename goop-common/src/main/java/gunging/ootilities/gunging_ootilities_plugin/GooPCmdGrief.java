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
 * The {@code /goop grief} subcommand, moved verbatim out of {@link GungingOotilities#onCommand}.
 */
final class GooPCmdGrief {

    private GooPCmdGrief() { }

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
        return null;
    }
}
