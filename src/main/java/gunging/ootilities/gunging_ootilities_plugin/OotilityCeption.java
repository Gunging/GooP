package gunging.ootilities.gunging_ootilities_plugin;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPPlaceholderAPI;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionEnchantments;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionEntities;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.containers.ContainerTemplateGooP;
import gunging.ootilities.gunging_ootilities_plugin.containers.PersonalContainerGooP;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Bisected;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.bukkit.Bukkit.*;

public class OotilityCeption {

    //region Debugging
    public static void Log(@Nullable String arg) {

        // Is Dev Logging null?
        if (Gunging_Ootilities_Plugin.devLogging){

            // Is Specified?
            if (Gunging_Ootilities_Plugin.devLogga != null) {

                // Send 2 Both
                Gunging_Ootilities_Plugin.theOots.DLog(Gunging_Ootilities_Plugin.devLogga, arg);

            } else {

                // Console-Only Log then
                Gunging_Ootilities_Plugin.theOots.CLog(arg);
            }
        }
    }

    ///C: Console Log
    ///P: Player Log
    ///D: 'Double' Console and Player Log
    ///-P: Plugin Format (just add [GooP] Prefix)
    public void CPLog(String arg2format) {
        getServer().getConsoleSender().sendMessage(LogFormat(arg2format));
    }
    public void PPLog(Player verysoft, String arg2format) { verysoft.sendMessage(LogFormat(arg2format)); }
    public void DPLog(Player verysoft, String arg2format) { CPLog(arg2format); PPLog(verysoft, arg2format); }
    public void PLog(Player verysoft, String arg2format) { verysoft.sendMessage(arg2format); }
    public void DLog(Player verysoft, String arg2format) { CLog(arg2format); PLog(verysoft, arg2format); }
    public void CLog(String arg) { getServer().getConsoleSender().sendMessage(arg); }

    public static void Log4Success(RefSimulator<String> stLogger, Boolean stState, String message) {

        // Anything to log?
        if (stLogger != null) {

            // Send Feedback?
            if (stState) {

                // Debug Message
                stLogger.SetValue(message);

                // No Feedback of this kind
            } else {

                // Nothing
                stLogger.SetValue(null);
            }
        }
    }

    public static String LogFormat(String message) {
        return "\u00a73[\u00a7eGooP\u00a73] \u00a77" + message;
    }
    public static String LogFormat(String specificommand, String message) {
        return "\u00a73[\u00a7eGooP \u00a7b\u00a7o" + specificommand + "\u00a73] \u00a77" + message;
    }

    static boolean nextMessagesRunning = false;
    static HashMap<Player, ArrayList<String>> nextMessages = new HashMap<>();
    static HashMap<Player, ArrayList<Integer>> concurrencyForTheFrame = new HashMap<>();
    /**
     * Sends a list of messages to that player 1 tick after this is called.
     *
     * Latest Concurrency ID: 11
     * @param player Player who to send those messages
     * @param message Message to add to the list
     * @param concurrencyID Only the first message of each ID will be sent - a method called 1000 times in a frame will end up producing only 1 message next frame.
     */
    public static void SendMessageNextTick(Player player, String message, int concurrencyID) {

        // Make sure we're not dealing with nulls
        nextMessages.computeIfAbsent(player, k -> new ArrayList<>());
        concurrencyForTheFrame.computeIfAbsent(player, k -> new ArrayList<>());

        // If the player is not associated to this concurrency ID yet)
        if (!concurrencyForTheFrame.get(player).contains(concurrencyID)) {

            // Add to both arrays
            nextMessages.get(player).add(message);
            concurrencyForTheFrame.get(player).add(concurrencyID);

            // Start the shit
            if (!nextMessagesRunning) {

                // Set
                nextMessagesRunning = true;

                // Run
                (new BukkitRunnable() {
                    public void run() {

                        // No longer running
                        nextMessagesRunning = false;

                        // Send every message to every player
                        for (Player py : nextMessages.keySet()) {

                            // Get list
                            ArrayList<String> messages = nextMessages.get(py);

                            // Send all
                            for (String str : messages) { py.sendMessage(str); }
                        }

                        // Clear both arrays
                        nextMessages.clear();
                        concurrencyForTheFrame.clear();

                    }

                }).runTaskLater(Gunging_Ootilities_Plugin.theMain.getPlugin(), 2L);
            }
        }
    }

    @NotNull public static ArrayList<OrderedScoreboardEntry> SortEntriesOf(@Nullable Objective obj) {

        /*SCR*/OotilityCeption.Log("\u00a7e-\u00a7b-\u00a73-\u00a77 Ordering scoreboard entries of \u00a7e" + obj.getName());

        // Get all participants
        ArrayList<OrderedScoreboardEntry> ret = new ArrayList<>();

        if (obj == null || obj.getScoreboard() == null) {
            /*SCR*/OotilityCeption.Log("\u00a7e-\u00a7b-\u00a73-\u00a7c Scoreboard was null, cancelling.");
            return ret; }

        // Get Entries IG
        ArrayList<String> entries = new ArrayList<>(obj.getScoreboard().getEntries());

        /*SCR*/OotilityCeption.Log("\u00a7e-\u00a7b-\u00a73-\u00a77 Found \u00a7b" + entries.size() + "\u00a77 entries:");
        /*SCR*/try { for (String str : io.lumine.mythic.lib.api.util.ui.SilentNumbers.transcribeList(entries, (s) -> (s == null ? "null" : ((String) s) + " \u00a7b" + obj.getScore((String) s).getScore()))) { OotilityCeption.Log("\u00a7e:\u00a73:\u00a77 " + str); } } catch (IllegalArgumentException ignored) {}

        int emergencyEscaper = 0;
        // Get Participants
        for (String r : entries) { emergencyEscaper++; if (emergencyEscaper > 200) { Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cFatal Loop Error\u00a77 (I guess), Escaped at 200th iteration, seems an infinite cycle."); break; }

            // Just check validity
            if (r != null) {

                // Correct length
                if (r.length() <= 40) {

                    // Get tha damn score
                    Score tar = obj.getScore(r);

                    // If valid
                    if (tar.getScore() != 0) {

                        // Theres
                        ret.add(new OrderedScoreboardEntry(tar));
                        /*SCR*/OotilityCeption.Log("\u00a7e:\u00a7b:\u00a73:\u00a77 Included \u00a7a" + r + "\u00a77, score \u00a7b" + tar.getScore());

                    }
                    /*SCR*/else { OotilityCeption.Log("\u00a7e:\u00a7b:\u00a73:\u00a77 Excluded \u00a7c" + r + "\u00a77, score \u00a7b" + tar.getScore()); }
                }
                /*SCR*/ else { OotilityCeption.Log("\u00a7e:\u00a7b:\u00a73:\u00a77 Excluded \u00a7c" + r + "\u00a77, too long."); } } }

        // Sort T_T
        Collections.sort(ret);

        // Return
        return ret;
    }

    //region External Module Logging
    public void ECPLog(String ePrefix, String arg2format) { getServer().getConsoleSender().sendMessage(ELogFormat(ePrefix, arg2format)); }
    public void EPPLog(Player verysoft, String ePrefix, String arg2format) { verysoft.sendMessage(ELogFormat(ePrefix, arg2format)); }
    public void EDPLog(Player verysoft, String ePrefix, String arg2format) { ECPLog(ePrefix, arg2format); EPPLog(verysoft, ePrefix, arg2format); }

    public String ELogFormat(String ePrefix, String message) { return "\u00a73[\u00a7e" + ePrefix + "\u00a73] \u00a77" + message; }

    public String ELogFormat(String ePrefix, String specificommand, String message) {
        return "\u00a73[\u00a7e" + ePrefix + " \u00a7b\u00a7o" + specificommand + "\u00a73] \u00a77" + message;
    }
    //endregion
    //protected static String[] errMsgs = new String[] { "Ticking entity detected at chunk -2 65 in world world", "Error when parsing config file at column 19", "Using legacy materials is deprecated.", "Error while loading registry, please enable debug mode for stack trace.", "Stored position of entity 069a79f4-44e9-4726-a5be-fca90e38aaf5 is corrupt.", "Unkown parameter '94726a5bef' at c34, p4", "No se ha podido aplicar el protocolo en la entidad ZOMBIE porque esta deshabilitado en el mundo WORLD por otro plugin. Por favor, desactivalo en el archivo config.yml de este plugin o evita que el otro plugin meta sus narices donde no debe." };
    //endregion

    //region Scoreboards

    /**
     * You must have parsed all placeholders or whatever before calling.
     * <p></p>
     * Null if any error is encountered.
     */
    @Nullable
    public static Double MathExpressionResult(@NotNull String expression, @NotNull ArrayList<MathVariable> values) {

        try {

            // Are they calculable
            ExpressionBuilder calc = new ExpressionBuilder(expression);

            // Introduce variables I gues
            for (MathVariable mv : values) {
                if (mv.isVariable()) {

                    // Input var
                    calc.variable(mv.getIdentifier());
                }
            }

            // Build ig
            Expression ex = calc.build();

            // Introduce variables again I gues
            for (MathVariable mv : values) {
                if (mv.isVariable()) {

                    // Input var
                    ex.setVariable(mv.getIdentifier(), mv.getValue());
                }
            }

            // Evaluate
            return ex.evaluate();

        } catch (Exception e) {

            // Ya no
            if (Gunging_Ootilities_Plugin.devLogging) { e.printStackTrace(); }
            return null;
        }
    }

    public static void SetPlayerScore(@NotNull Objective objective, @NotNull Player player, @NotNull PlusMinusPercent score) {

        // Apply
        SetEntryScore(objective, player.getName(), score);
    }
    public static void SetEntryScore(@NotNull Objective objective, @NotNull String entree, @NotNull PlusMinusPercent score) {

        // Apply
        SetEntryScore(objective, entree, RoundToInt(score.apply(GetEntryScore(objective, entree) + 0.0D)));
    }
    public static int GetPlayerScore(@NotNull Objective objective, @NotNull Player player) {
        return GetEntryScore(objective, player.getName());
    }
    public static int GetEntryScore(@NotNull Objective objective, @NotNull String entree) {
        Score sc = objective.getScore(entree);

        if (sc != null) { return sc.getScore(); }
        return 0;
    }
    public static void SetPlayerScore(@NotNull Objective objective, @NotNull Player player, int score) {

        // Set
        SetEntryScore(objective, player.getName(), score);
    }
    public static void SetEntryScore(@NotNull Objective objective, @NotNull String entry, int score) {

        // Set
        objective.getScore(entry).setScore(score);
    }
    public static void SetEntityScore(@NotNull Objective objective, @NotNull Entity player, @NotNull PlusMinusPercent score) {

        // Apply
        SetEntityScore(objective, player, RoundToInt(score.apply(GetEntityScore(objective, player) + 0.0D)));
    }
    public static int GetEntityScore(@NotNull Objective objective, @NotNull Entity player) {
        return objective.getScore(player.getUniqueId().toString()).getScore();
    }
    public static void SetEntityScore(@NotNull Objective objective, @NotNull Entity player, int score) {

        // Set
        objective.getScore(player.getUniqueId().toString()).setScore(score);
    }

    /**
     * Attempts to get objective. Will be null if not registered.
     */
    @Nullable public static Objective GetObjective(@Nullable String name) {
        if (name == null) { return null; }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard targetScoreboard = manager.getMainScoreboard();
        return targetScoreboard.getObjective(name);
    }
    //endregion

    @NotNull
    public static ItemStack asQuantity(@NotNull ItemStack iSource, int qty) {

        ItemStack clonium = iSource.clone();
        clonium.setAmount(qty);
        return clonium;
    }

    public static boolean ChanceSuccess(int percentChance) {
        Random rand = new Random();
        return rand.nextInt(100) <= percentChance;
    }

    /**
     * Gets the Display Name of that item. If it has no display name, then the material name.
     * @return null if the item is null
     */
    @Nullable
    public static String GetItemName(@Nullable ItemStack tItem) {

        // Require non-null
        if (tItem != null) {

            // Require non-air
            if (!IsAir(tItem.getType())) {

                // What should be obvious
                String ret = "";
                if (tItem.getItemMeta().hasDisplayName()) {
                    ret = tItem.getItemMeta().getDisplayName();

                    //NME//Log("\u00a78Display Name:\u00a7f   " + tItem.getItemMeta().getDisplayName());
                }

                // Well it may be like empty or smthn
                if (ret.length() == 0) { ret = "\u00a77\u00a7f" + TitleCaseConversion(tItem.getType().name().replace("_", " ")); }

                // Return that
                return ret;

            } else {

                // I guess its air
                return "\u00a77\u00a7fAir";
            }

        // That shit's null wth
        } else {

            // Nothing
            return null;
        }
    }
    @NotNull
    public static String TitleCaseConversion(@NotNull String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return "";
        }

        if (StringUtils.length(inputString) == 1) {
            return inputString.toUpperCase();
        }

        StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());

        Stream.of(inputString.split(" ")).forEach(stringPart ->
        {
            if (stringPart.length() > 1)
                resultPlaceHolder.append(stringPart.substring(0, 1)
                        .toUpperCase())
                        .append(stringPart.substring(1)
                                .toLowerCase());
            else
                resultPlaceHolder.append(stringPart.toUpperCase());

            resultPlaceHolder.append(" ");
        });
        return StringUtils.trim(resultPlaceHolder.toString());
    }
    @Nullable
    public static String ParsePlaceholder(@Nullable String sourceString,@Nullable String placeholderChars,@Nullable String replaceWith) {
        if (sourceString == null || placeholderChars == null) { return null; }
        if (replaceWith == null) { replaceWith = ""; }

        return sourceString.replace(placeholderChars, replaceWith);
    }
    @NotNull
    public static String ParseAsGoop(@NotNull Player asPlayer, @NotNull String cmd) {
        cmd = cmd.replace("%player%", asPlayer.getName());
        cmd = cmd.replace("%player_name%", asPlayer.getName());
        cmd = cmd.replace("%player_uuid%", asPlayer.getUniqueId().toString());
        cmd = cmd.replace("%player_world%", asPlayer.getLocation().getWorld().getName());
        cmd = cmd.replace("%player_x%", String.valueOf(asPlayer.getLocation().getX()));
        cmd = cmd.replace("%player_y%", String.valueOf(asPlayer.getLocation().getY()));
        cmd = cmd.replace("%player_z%", String.valueOf(asPlayer.getLocation().getZ()));

        int lastIndex = 0;
        while (cmd.contains("%player_score_")) {

            int score = cmd.indexOf("%player_score_", lastIndex);

            // no more
            if (score < 0) { break; }

            lastIndex = score + "%player_score_".length();

            // What should the score be
            String objective = cmd.substring(score + "%player_score_".length());

            // Closing %?
            int perc = objective.indexOf('%');
            if (perc >= 0) {

                // Got the name?
                String scoreName = objective.substring(0, perc);

                // Get objective
                Objective obj = GetObjective(scoreName);

                // Existed?
                if (obj != null) {


                    // All right strip befre
                    String before = cmd.substring(0, score);
                    String after = objective.substring(perc + 1);
                    int pScore = GetPlayerScore(obj, asPlayer);

                    // There
                    cmd = before + RemoveDecimalZeros(String.valueOf(pScore)) + after;
                }
            }
        }

        return ParseAsEntity(asPlayer, cmd);
    }
    @NotNull
    public static String ParseAsEntity(@NotNull Entity asEntity,@NotNull  String cmd) {
        cmd = cmd.replace("%entity%", String.valueOf(asEntity.getType().toString()));
        cmd = cmd.replace("%entity_name%", String.valueOf(asEntity.getName()));
        cmd = cmd.replace("%entity_uuid%", String.valueOf(asEntity.getUniqueId().toString()));
        cmd = cmd.replace("%entity_world%", asEntity.getLocation().getWorld().getName());
        cmd = cmd.replace("%entity_x%", String.valueOf(asEntity.getLocation().getX()));
        cmd = cmd.replace("%entity_y%", String.valueOf(asEntity.getLocation().getY()));
        cmd = cmd.replace("%entity_z%", String.valueOf(asEntity.getLocation().getZ()));
        cmd = cmd.replace("%world%", asEntity.getLocation().getWorld().getName());
        cmd = cmd.replace("%x%", String.valueOf(asEntity.getLocation().getX()));
        cmd = cmd.replace("%y%", String.valueOf(asEntity.getLocation().getY()));
        cmd = cmd.replace("%z%", String.valueOf(asEntity.getLocation().getZ()));
        cmd = cmd.replace("%uuid%", asEntity.getUniqueId().toString());
        cmd = cmd.replace("%entity_type%", asEntity.getType().toString());

        return cmd;
    }
    @NotNull
    public static String ParseAsGoop(@NotNull Block asBlock,@NotNull  String cmd) {
        cmd = cmd.replace("%structure-blockcenter-comma%", (asBlock.getX() + 0.5D) + "," + (asBlock.getY() + 0.5D) + "," + (asBlock.getZ() + 0.5D));
        cmd = cmd.replace("%structure-blockcenter%", (asBlock.getX() + 0.5D) + " " + (asBlock.getY() + 0.5D) + " " + (asBlock.getZ() + 0.5D));
        cmd = cmd.replace("%structure-center-comma%", (asBlock.getX()) + "," + (asBlock.getY()) + "," + (asBlock.getZ()));
        cmd = cmd.replace("%structure-center%", (asBlock.getX()) + " " + (asBlock.getY()) + " " + (asBlock.getZ()));
        cmd = cmd.replace("%pos_x%",  String.valueOf(asBlock.getX()));
        cmd = cmd.replace("%pos_y%",  String.valueOf(asBlock.getY()));
        cmd = cmd.replace("%pos_z%",  String.valueOf(asBlock.getZ()));
        cmd = cmd.replace("%world%",  asBlock.getWorld().getName());

        return cmd;
    }
    @NotNull
    public static String ParseAsGoop(@NotNull ItemStack asItem,@NotNull  String cmd) {
        if (Gunging_Ootilities_Plugin.foundMMOItems) {
            cmd = cmd.replace("%item_level%", String.valueOf(GooPMMOItems.GetItemLevelNonull(asItem)));
        }

        cmd = cmd.replace("%item_name%",  GetItemName(asItem));

        return cmd;
    }
    @NotNull
    public static String BlockLocation2String(@NotNull Location loc) {
        return loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }
    public static double SumAsVectors(double d1, double d2) { return Math.pow(Math.pow(d1, 2) + Math.pow(d2, 2), 0.5); }
    public static Vector VectorScale(Vector inp, double scale) {
        return new Vector(inp.getX() * scale, inp.getY() * scale, inp.getZ() * scale);
    }
    public static double RoundAtPlaces(double number, int decimals) {
        long rounded = Math.round(number * Math.pow(10, decimals));
        return rounded / Math.pow(10, decimals);
    }
    public static int RoundToInt(double number) {
        return (int) Math.round(number);
    }

    /**
     * Will return negative if the origin happened after the 'current'
     */
    public static long SecondsElapsedSince(@NotNull OptimizedTimeFormat originOfTimer, @NotNull  OptimizedTimeFormat currentTime) {

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.clear();
        calendar2.clear();
        calendar1.set(originOfTimer.year, originOfTimer.month, originOfTimer.day, originOfTimer.hour, originOfTimer.minute, originOfTimer.second);
        calendar2.set(currentTime.year, currentTime.month, currentTime.day, currentTime.hour, currentTime.minute, currentTime.second);
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long ret = diff / 1000;

        return ret;
    }
    public static boolean LocationTolerance(Location expected, Location observed, double tolerance) {

        // If Smae World
        if (expected.getWorld().equals(observed.getWorld())) {

            // If within
            return  (NumericTolerance(expected.getX(), observed.getX(), tolerance)) &&
                    (NumericTolerance(expected.getZ(), observed.getZ(), tolerance)) &&
                    (NumericTolerance(expected.getY(), observed.getY(), tolerance));

        // Not even in same world lol
        } else {

            // Faux
            return false;
        }
    }
    public static boolean NumericTolerance(double expected, double observed, double tolerance) { return  (Math.abs(observed-expected) <= Math.abs(tolerance)); }
    public static boolean IsDivisibleBy(double what, double by) {

        double div = what / by;
        int dib = RoundToInt(div);

        // Retrun if they equal
        return (dib + 0.0D) == div;
    }

    /**
     * Returns true only if BOOL is true.
     * @param bool Value that may be null
     * @return False if null, False if false, True if true
     */
    public static boolean If(@Nullable Boolean bool) { if (bool == null) { return false; } return bool; }
    public static boolean hasPermission(@NotNull CommandSender sender, @NotNull String major, @NotNull String minor) { return hasPermission(sender, major, minor, true); }
    public static boolean hasPermission(@NotNull CommandSender sender, @NotNull String major, @NotNull String minor, boolean silent) {

        // If player
        if (sender instanceof Player) {

            // is they op
            if (sender.isOp()) { return true; }

            boolean perm = sender.hasPermission("gunging_ootilities_plugin." + major + "." + minor);

            if (!perm && !silent) {

                sender.sendMessage("\u00a7cYou dont have permission!");
            }

            return perm;
        }

        // Console yea
        return true;
    }

    //region Type Identifying
    public static ArrayList<Material> matArmor = new ArrayList<>();
    public static void BuildIdentifyingArrays() {

        // Solid, Iterate through all of them
        for (Material m : Material.values()) {
            if (IsArmor(m)) { matArmor.add(m); }
        }
    }

    // Block Types
    @Nullable public static Material getMaterial(@Nullable String mat) { if (mat == null) { return null; } try { return Material.valueOf(mat); } catch (IllegalArgumentException ignored) { return null; } }
    public static boolean IsShulkerBox(@NotNull Material mat) {
        switch (mat) {
            case SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                return true;
            default: return false;
        }
    }
    @NotNull public static boolean IsLeaves(@NotNull Material mat) {
        switch (mat) {
            case OAK_LEAVES:
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case SPRUCE_LEAVES:
                return true;
            default: return false;
        }
    }
    /**
     * Will only check that this type is AIR. If you are examining an ItemStack, keep in mind that the AMOUNT could be ZERO (And I personally think that is air, idk about you).
     */
    public static boolean IsAir(@NotNull Material target) {
        return (target == Material.AIR) ||
                (target == Material.CAVE_AIR) ||
                (target == Material.VOID_AIR) ||
                (target == Material.LEGACY_AIR);
    }
    /**
     * Will return TRUE if iTarget is either NULL, Count is 0, or IsAir()
     */
    public static boolean IsAirNullAllowed(ItemStack iTarget) {
        if (iTarget == null) return true;
        if (iTarget.getAmount() < 1) return true;
        return IsAir(iTarget.getType());
    }
    /**
     * Will return TRUE if iTarget is either NULL or IsAir()
     */
    public static boolean IsAirNullAllowed(Block iTarget) {
        if (iTarget == null) return true;
        return IsAir(iTarget.getType());
    }
    public static boolean IsButton(@NotNull Material target) {
        return (target == Material.ACACIA_BUTTON) ||
                (target == Material.BIRCH_BUTTON) ||
                (target == Material.DARK_OAK_BUTTON) ||
                (target == Material.JUNGLE_BUTTON) ||
                (target == Material.OAK_BUTTON) ||
                (target == Material.SPRUCE_BUTTON) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CRIMSON_BUTTON)) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.WARPED_BUTTON)) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.POLISHED_BLACKSTONE_BUTTON)) ||
                (target == Material.STONE_BUTTON);
    }
    public static boolean IsPressurePlate(@NotNull Material target) {
        return (target == Material.ACACIA_PRESSURE_PLATE) ||
                (target == Material.BIRCH_PRESSURE_PLATE) ||
                (target == Material.DARK_OAK_PRESSURE_PLATE) ||
                (target == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) ||
                (target == Material.JUNGLE_PRESSURE_PLATE) ||
                (target == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) ||
                (target == Material.OAK_PRESSURE_PLATE) ||
                (target == Material.SPRUCE_PRESSURE_PLATE) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CRIMSON_PRESSURE_PLATE)) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.WARPED_PRESSURE_PLATE)) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.POLISHED_BLACKSTONE_PRESSURE_PLATE)) ||
                (target == Material.STONE_PRESSURE_PLATE);
    }
    public static boolean IsDoor(@NotNull Material target) {
        return (target == Material.DARK_OAK_DOOR) ||
                (target == Material.ACACIA_DOOR) ||
                (target == Material.BIRCH_DOOR) ||
                (target == Material.IRON_DOOR) ||
                (target == Material.JUNGLE_DOOR) ||
                (target == Material.OAK_DOOR) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.WARPED_DOOR)) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CRIMSON_DOOR)) ||
                (target == Material.SPRUCE_DOOR);
    }
    public static boolean IsTrapdoor(@NotNull Material target) {
        return (target == Material.ACACIA_TRAPDOOR) ||
                (target == Material.BIRCH_TRAPDOOR) ||
                (target == Material.DARK_OAK_TRAPDOOR) ||
                (target == Material.IRON_TRAPDOOR) ||
                (target == Material.OAK_TRAPDOOR) ||
                (target == Material.JUNGLE_TRAPDOOR) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.WARPED_TRAPDOOR)) ||
                (target == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CRIMSON_TRAPDOOR)) ||
                (target == Material.SPRUCE_TRAPDOOR);
    }

    // Entity Types
    public static boolean IsMonster(EntityType target) {
        return (target == EntityType.BLAZE) ||
                (target == EntityType.CAVE_SPIDER) ||
                (target == EntityType.CREEPER) ||
                (target == EntityType.DROWNED) ||
                (target == EntityType.ELDER_GUARDIAN) ||
                (target == EntityType.ENDER_DRAGON) ||
                (target == EntityType.ENDERMAN) ||
                (target == EntityType.ENDERMITE) ||
                (target == EntityType.EVOKER) ||
                (target == EntityType.GHAST) ||
                (target == EntityType.GIANT) ||
                (target == EntityType.GUARDIAN) ||
                (target == EntityType.HUSK) ||
                (target == EntityType.ILLUSIONER) ||
                (target == EntityType.MAGMA_CUBE) ||
                (target == EntityType.PHANTOM) ||
                (target == EntityType.SILVERFISH) ||
                (target == EntityType.SKELETON) ||
                (target == EntityType.SHULKER) ||
                (target == EntityType.SLIME) ||
                (target == EntityType.SPIDER) ||
                (target == EntityType.STRAY) ||
                (target == EntityType.VEX) ||
                (target == EntityType.VINDICATOR) ||
                (target == EntityType.WITCH) ||
                (target == EntityType.WITHER) ||
                (target == EntityType.WITHER_SKELETON) ||
                (target == EntityType.ZOMBIE) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.PIG_ZOMBIE)) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.ZOMBIFIED_PIGLIN)) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.HOGLIN)) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.PIGLIN)) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.STRIDER)) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.ZOGLIN)) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.PILLAGER)) ||
                (target == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.RAVAGER)) ||
                (target == EntityType.ZOMBIE_VILLAGER);
    }

    // Tool Types
    public static boolean IsTool(@NotNull Material iType) {

        return (iType == Material.DIAMOND_PICKAXE) ||
                (iType == Material.IRON_PICKAXE) ||
                (iType == Material.GOLDEN_PICKAXE) ||
                (iType == Material.STONE_PICKAXE) ||
                (iType == Material.WOODEN_PICKAXE) ||
                (iType == Material.DIAMOND_SHOVEL) ||
                (iType == Material.IRON_SHOVEL) ||
                (iType == Material.GOLDEN_SHOVEL) ||
                (iType == Material.STONE_SHOVEL) ||
                (iType == Material.WOODEN_SHOVEL) ||
                (iType == Material.DIAMOND_HOE) ||
                (iType == Material.IRON_HOE) ||
                (iType == Material.GOLDEN_HOE) ||
                (iType == Material.STONE_HOE) ||
                (iType == Material.WOODEN_HOE) ||
                (iType == Material.DIAMOND_AXE) ||
                (iType == Material.IRON_AXE) ||
                (iType == Material.GOLDEN_AXE) ||
                (iType == Material.STONE_AXE) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_PICKAXE)) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_SHOVEL)) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_HOE)) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_AXE)) ||
                (iType == Material.WOODEN_AXE);
    }
    public static boolean IsFishingRod(@NotNull Material iType) {
        return (iType == Material.FISHING_ROD);
    }
    public static boolean IsTrident(@NotNull Material iType) {
        return (iType == Material.TRIDENT);
    }
    public static boolean IsRangedWeapon(@NotNull Material iType) {
        return (iType == Material.BOW) || IsCrossbow(iType);
    }
    public static boolean IsCrossbow(@NotNull Material iType) {
        return (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CROSSBOW));
    }
    public static boolean IsMeleeWeapon(@NotNull Material iType) {
        return IsSword(iType) || IsAxe(iType) || IsTrident(iType);
    }
    public static boolean IsSword(@NotNull Material iType) {

        return (iType == Material.DIAMOND_SWORD) ||
                (iType == Material.IRON_SWORD) ||
                (iType == Material.GOLDEN_SWORD) ||
                (iType == Material.STONE_SWORD) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_SWORD)) ||
                (iType == Material.WOODEN_SWORD);
    }
    public static boolean IsHead(@NotNull Material iType) {
        return (iType == Material.CREEPER_HEAD) ||
                (iType == Material.DRAGON_HEAD) ||
                (iType == Material.PLAYER_HEAD) ||
                (iType == Material.ZOMBIE_HEAD) ||
                (iType == Material.WITHER_SKELETON_SKULL) ||
                (iType == Material.SKELETON_SKULL);
    }
    public static boolean IsAxe(@NotNull Material iType) {

        return (iType == Material.DIAMOND_AXE) ||
                (iType == Material.IRON_AXE) ||
                (iType == Material.GOLDEN_AXE) ||
                (iType == Material.STONE_AXE) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_AXE)) ||
                (iType == Material.WOODEN_AXE);
    }

    // Armor
    public static boolean IsArmor(@NotNull Material iType) {
        return IsHelmet(iType) || IsBoots(iType) || IsLeggings(iType) || IsChestplate(iType);
    }
    public static boolean IsBoots(@NotNull Material iType) {
        return (iType == Material.DIAMOND_BOOTS) ||
                (iType == Material.IRON_BOOTS) ||
                (iType == Material.GOLDEN_BOOTS) ||
                (iType == Material.CHAINMAIL_BOOTS) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_BOOTS)) ||
                (iType == Material.LEATHER_BOOTS);
    }
    public static boolean IsHelmet(@NotNull Material iType) {
        return (iType == Material.DIAMOND_HELMET) ||
                (iType == Material.IRON_HELMET) ||
                (iType == Material.GOLDEN_HELMET) ||
                (iType == Material.CHAINMAIL_HELMET) ||
                (iType == Material.LEATHER_HELMET) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_HELMET)) ||
                (iType == Material.TURTLE_HELMET);
    }
    public static boolean IsChestplate(@NotNull Material iType) {
        return (iType == Material.DIAMOND_CHESTPLATE) ||
                (iType == Material.IRON_CHESTPLATE) ||
                (iType == Material.GOLDEN_CHESTPLATE) ||
                (iType == Material.CHAINMAIL_CHESTPLATE) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_CHESTPLATE)) ||
                (iType == Material.LEATHER_CHESTPLATE);
    }
    public static boolean IsLeggings(@NotNull Material iType) {
        return (iType == Material.DIAMOND_LEGGINGS) ||
                (iType == Material.IRON_LEGGINGS) ||
                (iType == Material.GOLDEN_LEGGINGS) ||
                (iType == Material.CHAINMAIL_LEGGINGS) ||
                (iType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_LEGGINGS)) ||
                (iType == Material.LEATHER_LEGGINGS);
    }

    /**
     * Used to restrict containes from storing books, which players like to overload with characters and cause memory leaks. Im dealing with none of that shit.
     *
     * DOES NOT ACCOUNT FOR CHESTS WITH SAVED NBT. Only Shulker Boxes or itself.
     * @return Will return true if it is a Written Book, a Writeable Book, or if it is a shulker box that contains any of these.
     */
    public static boolean ContainsWrittenBook(ItemStack iStack) {

        // Technically air and null itemstacks are not books nor contain them
        if (IsAirNullAllowed(iStack)) { return false; }

        // Dip search?
        if (IsShulkerBox(iStack.getType())) {

            // Iterate whole inventory
            BlockStateMeta bsm = (BlockStateMeta) iStack.getItemMeta();
            ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

            // For every slot
            for (int sl = 0; sl < 27; sl++) {

                // Identify observed
                ItemStack obs = boxx.getInventory().getItem(sl);

                // Does it contain a written book? RETURN THE INFORMATION
                if (ContainsWrittenBook(obs)) { return true; }
            }

            // Made it this far? Yo clear man
            return false;

        // Just itself
        } else {

            // Evaluate material itself
            Material type = iStack.getType();
            return type == Material.WRITABLE_BOOK || type == Material.WRITTEN_BOOK || type == Material.LEGACY_WRITTEN_BOOK;
        }
    }
    public static boolean ContainsContainerBag(ItemStack iStack) {

        // Technically air and null itemstacks are not books nor contain them
        if (IsAirNullAllowed(iStack)) { return false; }

        // Dip search?
        if (IsShulkerBox(iStack.getType())) {

            // Iterate whole inventory
            BlockStateMeta bsm = (BlockStateMeta) iStack.getItemMeta();
            ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

            // For every slot
            for (int sl = 0; sl < 27; sl++) {

                // Identify observed
                ItemStack obs = boxx.getInventory().getItem(sl);

                // Does it contain a written book? RETURN THE INFORMATION
                if (ContainsWrittenBook(obs)) { return true; }
            }

            // Made it this far? Yo clear man
            return false;

            // Just itself
        } else {

            // Evaluate material itself
            String container = GooPMMOItems.GetStringStatValue(iStack, GooPMMOItems.CONTAINER, null, false);
            return container != null;
        }
    }
    //endregion

    //region Enchantment Manipulation
    public static boolean TestForEnchantment(ItemStack eItem, String eName, Integer eLvl, RefSimulator<String> logOutput){

        // Well lets get that enchantment shall we?
        Enchantment targetEnch = GetEnchantmentByName(eName);

        // Run da shit
        return TestForEnchantment(eItem, targetEnch, eLvl, logOutput);
    }
    public static boolean TestForEnchantment(ItemStack eItem, Enchantment targetEnch, Integer eLvl, RefSimulator<String> logOutput) {

        // Success?
        if (targetEnch != null){

            // Well does the item contain it?
            if (eItem.getEnchantments().containsKey(targetEnch)){

                // Does it care about level?
                if (eLvl == null) {

                    // Log if appropiate
                    OotilityCeption.Log4Success(logOutput, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Found item with the \u00a7acorrect \u00a77enchantment.");

                    // Yes! It works
                    return true;

                // What about the correct level
                } else if (eItem.getEnchantmentLevel(targetEnch) == eLvl) {

                    // Log if appropiate
                    OotilityCeption.Log4Success(logOutput, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Found item with the \u00a7acorrect \u00a77enchantment and enchantment level.");


                    // Yes! It works
                    return true;

                // No correct enchantment level
                } else {

                    // Log if appropiate
                    OotilityCeption.Log4Success(logOutput, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Target Item \u00a73" + eItem.getItemMeta().getDisplayName() + "\u00a77 does not have the expected \u00a73" + targetEnch.getKey().getKey() + "\u00a77 level '\u00a73" + eLvl + "\u00a77.'");

                }

            // Target item has no such enchantment
            } else {

                // Get item name (if possible)
                String itName = "";
                ItemMeta meta = eItem.getItemMeta();
                if (meta != null) { itName = " " + meta.getDisplayName(); }

                // Log if appropiate
                OotilityCeption.Log4Success(logOutput, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Target Item\u00a73" + itName + "\u00a77 does not contain such enchantment '\u00a73" + targetEnch.getKey().getKey() + "\u00a77'.");
            }

            // The enchantment does not exist
        } else {

            // Log if appropiate
            OotilityCeption.Log4Success(logOutput, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Enchantment '\u00a73" + targetEnch.getKey().getKey() + "\u00a77' not found. Remember to use vanilla enchantment names.");
        }

        return false;
    }
    public static Enchantment GetEnchantmentByName(String name) {
        try {
            Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));

            if (enchant == null) {
                enchant = Enchantment.getByKey(NamespacedKey.minecraft(name.toUpperCase()));
                if (enchant == null) {
                    return null;
                }
            }
            return enchant;

        } catch (Exception ext) {

            return null;
        }
    }
    public static Integer GetEnchLevel(ItemStack eItem, Enchantment tEnchant) {

        // Is it contained?
        if (eItem.containsEnchantment(tEnchant)) {

            // Return its level
            return eItem.getEnchantmentLevel(tEnchant);

        } else {

            // Technically Level 0
            return 0;
        }
    }
    public static ItemStack AddUselessEnchantmentOfLevel(ItemStack iSource, Integer eLevel) {

        if (iSource != null && eLevel != null) {

            // Check its not air
            if (IsAir(iSource.getType())) {

                // Bitch is air
                return null;

            // Its not air
            } else {

                // Product to bake
                ItemStack iProduct = iSource;
                Boolean success = false;

                // Any enchantment already of required level?
                for (Map.Entry<Enchantment, Integer> cEnchant : iSource.getEnchantments().entrySet()) {

                    // There seems to be an enchantment of target level already
                    if (cEnchant.getValue() == eLevel) {

                        // This shit was born ready
                        success = true;
                    }
                }

                // Still no success?
                if (!success) {

                    // Wouldn't want to override shit
                    if (eLevel > 0) {

                        // Check for the Ultimate Enchantments - Those that don't work over lvl 1
                        if (!success && GetEnchLevel(iSource, Enchantment.WATER_WORKER) == 1) { iProduct.addUnsafeEnchantment(Enchantment.WATER_WORKER, eLevel); success = true; }
                        if (!success && GetEnchLevel(iSource, Enchantment.ARROW_INFINITE) == 1) { iProduct.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, eLevel);  success = true; }
                        if (!success && GetEnchLevel(iSource, Enchantment.SILK_TOUCH) == 1) { iProduct.addUnsafeEnchantment(Enchantment.SILK_TOUCH, eLevel);  success = true; }
                        if (!success && GetEnchLevel(iSource, Enchantment.VANISHING_CURSE) == 1) { iProduct.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, eLevel);  success = true; }
                        if (!success && GetEnchLevel(iSource, Enchantment.CHANNELING) == 1) { iProduct.addUnsafeEnchantment(Enchantment.CHANNELING, eLevel);  success = true; }
                        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.MULTISHOT)) == 1) { iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.MULTISHOT), eLevel);  success = true; } }
                        if (!success && GetEnchLevel(iSource, Enchantment.BINDING_CURSE) == 1) { iProduct.addUnsafeEnchantment(Enchantment.BINDING_CURSE, eLevel);  success = true; }
                        if (!success && GetEnchLevel(iSource, Enchantment.DEPTH_STRIDER) == 3 && eLevel > 3) { iProduct.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, eLevel); success = true;  }
                    }

                    // At this point, adding a new enchantment is what must be done.
                    if (!success) {

                        // What the fuck is it?
                        Boolean isArmor = IsArmor(iSource.getType()),
                                isBow = IsRangedWeapon(iSource.getType()),
                                isCrossbow = IsCrossbow(iSource.getType()),
                                isWeapon = IsMeleeWeapon(iSource.getType()),
                                isSword = IsSword(iSource.getType()),
                                isTool = IsTool(iSource.getType()),
                                isTrident = IsTrident(iSource.getType()),
                                isRod = IsFishingRod(iSource.getType());

                        //region Lolz Priority: P2W Scuba Diving Helmet from blocks
                        if (!success && GetEnchLevel(iSource, Enchantment.WATER_WORKER) == 0 && (!isArmor && !isBow && !isWeapon && !isTool && !isRod)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.WATER_WORKER, eLevel); }
                        if (!success && GetEnchLevel(iSource, Enchantment.OXYGEN) == 0 && (!isArmor && !isBow && !isWeapon && !isTool && !isRod)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.OXYGEN, eLevel); }
                        //endregion

                        //region High Priority: Per-item comp. useless enchantments
                        if (!success) {
                            if (!success && GetEnchLevel(iSource, Enchantment.LUCK) == 0 && (!isRod)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LUCK, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LURE) == 0 && (!isRod)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LURE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.OXYGEN) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.OXYGEN, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LOYALTY) == 0 && (!isTrident)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LOYALTY, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.RIPTIDE) == 0 && (!isTrident)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.RIPTIDE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DIG_SPEED) == 0 && (!isTool)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DIG_SPEED, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.IMPALING) == 0 && (!isWeapon)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.IMPALING, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_FIRE) == 0 && (!isBow)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_FIRE, eLevel); }
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.PIERCING)) == 0 && (!isCrossbow)) { success = true; iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.PIERCING), eLevel); } }
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.MULTISHOT)) == 0 && (!isCrossbow)) { success = true; iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.MULTISHOT), eLevel); } }
                            if (!success && GetEnchLevel(iSource, Enchantment.CHANNELING) == 0 && (!isTrident)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.CHANNELING, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_DAMAGE) == 0 && (!isBow)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.FROST_WALKER) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.FROST_WALKER, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.WATER_WORKER) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.WATER_WORKER, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DEPTH_STRIDER) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.SWEEPING_EDGE) == 0 && (!isSword)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, eLevel); }
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.QUICK_CHARGE)) == 0 && (!isCrossbow)) { success = true; iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.QUICK_CHARGE), eLevel); } }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_KNOCKBACK) == 0 && (!isBow)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_FIRE) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_FALL) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LOOT_BONUS_BLOCKS) == 0 && (!isTool)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_INFINITE) == 0 && (!isBow || isCrossbow)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_EXPLOSIONS) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_PROJECTILE) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_ENVIRONMENTAL) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, eLevel); }
                        }
                        //endregion

                        //region Medium Priority: Marginaly Useful Enchantments
                        if (!success) {
                            if (!success && GetEnchLevel(iSource, Enchantment.DAMAGE_ALL) == 0 && (!isWeapon)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.FIRE_ASPECT) == 0 && (!isWeapon)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DAMAGE_UNDEAD) == 0 && (!isWeapon)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DAMAGE_ARTHROPODS) == 0 && (!isWeapon)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LOOT_BONUS_MOBS) == 0 && (!isWeapon && !isBow)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.KNOCKBACK, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.SILK_TOUCH) == 0 && (!isTool)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.SILK_TOUCH, eLevel); }
                        }
                        //endregion

                        //region Low Priority: Useful Enchantments
                        if (!success) {
                            if (!success && GetEnchLevel(iSource, Enchantment.WATER_WORKER) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.WATER_WORKER, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DAMAGE_ARTHROPODS) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_EXPLOSIONS) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.CHANNELING) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.CHANNELING, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_FALL) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.FIRE_ASPECT) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DEPTH_STRIDER) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DIG_SPEED) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DIG_SPEED, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_FIRE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_FIRE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_FIRE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LOOT_BONUS_BLOCKS) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.FROST_WALKER) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.FROST_WALKER, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.IMPALING) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.IMPALING, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_INFINITE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LOOT_BONUS_MOBS) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.KNOCKBACK, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LOYALTY) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LOYALTY, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LUCK) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LUCK, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.LURE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.LURE, eLevel); }
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.MULTISHOT)) == 0) { success = true; iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.MULTISHOT), eLevel); } }
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.PIERCING)) == 0) { success = true; iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.PIERCING), eLevel); } }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_DAMAGE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_PROJECTILE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.PROTECTION_ENVIRONMENTAL) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.ARROW_KNOCKBACK) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, eLevel); }
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.QUICK_CHARGE)) == 0) { success = true; iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.QUICK_CHARGE), eLevel); } }
                            if (!success && GetEnchLevel(iSource, Enchantment.OXYGEN) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.OXYGEN, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.RIPTIDE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.RIPTIDE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DAMAGE_ALL) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.SILK_TOUCH) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.SILK_TOUCH, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DAMAGE_UNDEAD) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.SWEEPING_EDGE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.THORNS) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.THORNS, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.DURABILITY) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.DURABILITY, eLevel); }
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (!success && GetEnchLevel(iSource, GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.SOUL_SPEED)) == 0) { success = true; iProduct.addUnsafeEnchantment(GooP_MinecraftVersions.GetVersionEnchantment(GooPVersionEnchantments.SOUL_SPEED), eLevel); } }
                            if (!success && GetEnchLevel(iSource, Enchantment.MENDING) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.MENDING, eLevel); }
                        }
                        //endregion

                        //region Ass Priority: Curses
                        if (!success){
                            if (!success && GetEnchLevel(iSource, Enchantment.BINDING_CURSE) == 0 && (!isArmor)) { success = true; iProduct.addUnsafeEnchantment(Enchantment.BINDING_CURSE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.VANISHING_CURSE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, eLevel); }
                            if (!success && GetEnchLevel(iSource, Enchantment.BINDING_CURSE) == 0) { success = true; iProduct.addUnsafeEnchantment(Enchantment.BINDING_CURSE, eLevel); }
                        }
                        //endregion
                    }
                }

                // Result the Finale
                return  iProduct;
            }
        }

        // Bitch is null
        return null;
    }
    @Nullable
    public static ItemStack EnchantmentOperation(@Nullable ItemStack iSource,@Nullable  Enchantment tEnchant,@Nullable  PlusMinusPercent operation,@Nullable  RefSimulator<Integer> result,@Nullable  RefSimulator<String> logger) {

        if (iSource != null && operation != null && tEnchant != null) {

            // Check its not air
            if (IsAir(iSource.getType())) {

                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant add enchantments to air!");

                // Bitch is air
                return null;

            // Its not air
            } else {

                // Product to bake
                ItemStack iProduct = iSource;

                // Get current enchantment level
                Integer currentValue = GetEnchLevel(iSource, tEnchant);

                // Get the terminant level
                Integer finalValue = (int)Math.round(operation.apply((double)((int)currentValue)));

                // Replace
                if (currentValue != 0) { iProduct.removeEnchantment(tEnchant); }

                // If its zero, it just gets removed.
                if (finalValue != 0) {

                    // Yeet
                    iProduct.addUnsafeEnchantment(tEnchant, finalValue);
                }

                // Export Result
                if (result != null) { result.setValue(finalValue); }

                // Log
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfuly set \u00a73" + GetItemName(iSource) + "\u00a77 enchantment \u00a73" + tEnchant.getName() + "\u00a77 level to \u00a73" + finalValue);

                // Result the Finale
                return  iProduct;
            }
        } else { Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Invalid arguments."); }

        // Bitch is null
        return null;
    }
    //endregion

    //region HEX-1.16 support thing, and Name Encryption
    @NotNull
    public static String ParseColour(@NotNull String source) {
        // Parse as pre-1.16
        if (GooP_MinecraftVersions.GetMinecraftVersion() < 16.0) {
            return ActuallyParseColour(source);

        // Parse as 1.16
        } else {
            return ActuallyParseColour(ParseHexColors(source));
        }
    }

    @NotNull
    static String ActuallyParseColour(@NotNull String source) {

        // Get as char array bruh
        ArrayList<Character> asCharArray = new ArrayList<>();
        for (char c : source.toCharArray()) { asCharArray.add(c); }

        // Appent
        StringBuilder str = new StringBuilder("");

        // Evaluate
        for (int c = 0; c < asCharArray.size(); c++) {

            // If the current is & and the next fits
            boolean isAmpersand = asCharArray.get(c).equals('&');

            // Check
            if (isAmpersand && (c + 1 < asCharArray.size())) {

                // Is Code?
                boolean isCode = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(asCharArray.get(c + 1)) > -1;

                // Was it?
                if (isCode) {

                    // Append code
                    str.append('\u00a7');

                // Was not a code
                } else {

                    // Append normally
                    str.append(asCharArray.get(c));
                }

            // Was not an ampersand
            } else {

                // Append normally
                str.append(asCharArray.get(c));
            }
        }

        // Build
        return str.toString();
    }

    public static String ParseHexColors(String source) {

        // Mc 1.16+
        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 16.0) {

            // If it has a chance of contianing a colour
            if (source.contains("<#")) {
                if (source.contains(">")) {

                    // Format: <#FF00FF>
                    StringBuilder ret; int bIndex = 2;
                    String[] afterGT = source.split("<#");

                    // If it started with this thing
                    if (source.startsWith("<#")) {

                        // If it is not a minimum of 7 characters long; It is not a hex code.
                        if (afterGT[1].length() < 7) {

                            // If its less than 7 long, it is absolutely not a hex code.
                            ret = new StringBuilder("<#" + afterGT[1]);

                        // If it is at least 7 chars long; It must be 6 chars followed by the seventh being a greater than.
                        } else if (afterGT[1].charAt(6) != '>') {

                            // Otherwise, its not a hex code
                            ret = new StringBuilder("<#" + afterGT[1]);

                        // If it meets the format <#??????>, then it will be attempted to be parssed as well
                        } else{

                            // First hex code parse attempt includes the very first term.
                            bIndex = 1;
                            ret = new StringBuilder("");
                        }

                    // If the first entry is not trying to even be a hex, then let the thing begin with thay
                    } else {

                        ret = new StringBuilder(afterGT[0]);
                        bIndex = 1;
                    }

                    // "<#>"
                    // "" ">"                  "<#>"

                    // "<#> <#F"
                    // "" "> "              "<#>" + "F"

                    // <#FF00FF>The big<# black <#FF00FF>fox jumped over <#>the laxy dog
                    // ""                      "" + "FF00FF>The big" " black " "FF00FF>fox jumped over " ">the laxy dog"

                    // <#FF00FFFThe big<# black <#FF00FF>fox jumped over <#>the laxy dog
                    // ""      "<#FF00FFFThe big" + " black " "FF00FF>fox jumped over " ">the laxy dog"

                    // <#<#FF00FF>The big<# black <#FF00FF>fox jumped over <#>the laxy dog
                    // "" ""                 "<#" + "FF00FF>The big" " black " "FF00FF>fox jumped over " ">the laxy dog"

                    // <#d<#FF00FF>The big<# black <#FF00FF>fox jumped over <#>the laxy dog
                    // "" "d"               "<#d" + "FF00FF>The big" " black " "FF00FF>fox jumped over " ">the laxy dog"

                    // The big<# black <#FF00FF>fox jumped over <#>the laxy dog
                    // "The big"        "The big" + " black " "FF00FF>fox jumped over " ">the laxy dog"

                    // Attempts to format. At this point we can assume they all begin with <# and are possible color codes
                    for (int ostr = bIndex; ostr < afterGT.length; ostr++) {

                        // Get currently observed string
                        String str = afterGT[ostr];

                        // If it is not a minimum of 7 characters long; It is not a hex code.
                        if (str.length() < 7) {

                            // Just add it to ret
                            ret.append("<#" + str);

                        // If it is at least 7 chars long; It must be 6 chars followed by the seventh being a greater than.
                        } else if (str.charAt(6) != '>') {

                            // Otherwise, its not a hex code
                            ret.append("<#" + str);

                        // If it meets the format <#??????>, then it will be attempted to be parsed
                        } else{

                            // Gather the message (whatever follows the hex)
                            String message = str.substring(7);

                            // Gather the hex (everything until the >, without the >)
                            String hex = str.substring(0, 6);

                            // Parse chat color
                            String hex2ChatColor = ChatColorFromHex(hex);

                            // Add this stuff
                            ret.append(hex2ChatColor + message);
                        }
                    }

                    // Return the result
                    return ret.toString();
                }
            }
        }

        // Didn't have any to begin with
        return source;
    }

    /**
     * Checks that a color in HEX has a valid rgb value, and transforms it into minecraft chat format.
     *
     * @param hex 6-digit value you want to transform: FF00FF
     * @return Minecraft format hex value &x&F&F&0&0&F&F basically
     */
    @Nullable
    public static String ChatColorFromHex(@Nullable String hex) {
        if (hex != null) {

            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 16.0) {

                // If string is basically a hex code
                if ( hex.length() == 6 ) {

                    // Attempt to actually parse
                    try {

                        // Parse value
                        Integer.parseInt( hex, 16 );

                    // Incorrect format
                    } catch ( NumberFormatException ex ) {

                        // Say they messed it up
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cHex Colors Conversion Failed: \u00a77Invalid HEX \u00a7e" + hex); }

                        // Return white alv
                        return ChatColor.WHITE.toString();
                    }

                    // She hexed me!
                    return IntermitentEncode("x" + hex, false);
                }

            } else {

                // Say they messed it up
                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) { Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cHex Colors Conversion Ignored: \u00a7c Hex Colors are only supported in MC 1.16+"); }

                // Return white alv
                return ChatColor.WHITE.toString();
            }
        }

        return null;
    }

    /**
     * Puts a section sign before every character of this string, making it invisible in-game.
     */
    @NotNull
    public static String IntermitentEncode(@NotNull String source, boolean supress_x) {

        // Well it parses, so time to parse as an actual HEX
        StringBuilder spell = new StringBuilder("");

        // Add every hex code
        for (char c : source.toCharArray()) {

            // Add char with section sign
            spell.append(new String(new char[] {ChatColor.COLOR_CHAR, c}));
        }


        // She hexed me!
        String sbell = spell.toString();

        if (supress_x) {

            // Replace all bruh
            for (String chr : nvf_xSupression.keySet()) {

                sbell = sbell.replace(chr, nvf_xSupression.get(chr));
            }

            // Return thay
            return sbell;

        } else {

            // Just that
            return sbell;
        }
    }

    /**
     * Removes all section signs from this string.
     */
    @NotNull
    public static String IntermitentDecode(@NotNull String source, boolean unsupress_x) {

        // Not contained? i sleep
        if (!source.contains("\u00a7")) { return source; }

        if (unsupress_x) {

            // Replace all bruh
            for (String chr : nvf_xSupression.keySet()) {

                source = source.replace(nvf_xSupression.get(chr), chr);
            }

        }

        // Well it parses, so time to parse as an actual HEX
        StringBuilder spell = new StringBuilder("");

        // Add every hex code
        for (String c : (source.replace("\u00a7\u00a7", "\u00a7ÑÑÑ")).split("\u00a7")) {

            // is it actually a section sign lma0
            if (c.equals("ÑÑÑ")) {

                // Append as color code
                spell.append("\u00a7");

            } else {

                // Add char
                spell.append(c);
            }
        }

        // She hexed me!
        return spell.toString();
    }

    static final String encryptionPre = "\u00a77<\u00a73[ \u00a7e", encryptionPost = " \u00a73]\u00a77>";
    public static ItemStack NameEncrypt(ItemStack iSource, String value) {

        // Rename to thay
        return RenameItem(iSource, encryptionPre + value + encryptionPost, null);
    }
    public static boolean IsEncrypted(ItemStack iEval, String encryption) {

        if (iEval == null) { return false; }

        // Get its name
        String iName = GetItemName(iEval);

        // Does it begin how its supposed to?
        if (iName.startsWith(encryptionPre) && iName.endsWith(encryptionPost)) {

            // If no key is specified
            if (encryption == null) {

                // It is encrypted... with something.
                return true;

                // Alr lets see if it matches the evaluation
            } else {

                // Get String
                String exRemoved = iName.substring(encryptionPre.length());

                // Get Encrypted Thing
                String exEncrypt = exRemoved.substring(0, exRemoved.indexOf(encryptionPost));

                // Evaluate
                return exEncrypt.equals(encryption);
            }
        }

        // Its not encrypted
        return false;
    }

    static final String namePlaceholderFrames = "\u00a7¤\u00a7g";
    static final String namePlaceholderHide = "\u00a7¤\u00a7p";
    public static final String nameVariableFrames = "\u00a7ø\u00a7g";
    static final String nameVariableColorDelimiter = "\u00a7~\u00a7g";
    static final HashMap<String, String> nvf_xSupression = new HashMap<>();
    public static void Fill_xSupression() {

        nvf_xSupression.put("\u00a70", "\u00a7°\u00a7¬\u00a7j");
        nvf_xSupression.put("\u00a71", "\u00a7°\u00a7¬\u00a7v");
        nvf_xSupression.put("\u00a72", "\u00a7°\u00a7¬\u00a7¡");
        nvf_xSupression.put("\u00a73", "\u00a7°\u00a7¬\u00a7z");
        nvf_xSupression.put("\u00a74", "\u00a7°\u00a7¬\u00a7¿");
        nvf_xSupression.put("\u00a75", "\u00a7°\u00a7¬\u00a7+");
        nvf_xSupression.put("\u00a76", "\u00a7°\u00a7¬\u00a7-");
        nvf_xSupression.put("\u00a77", "\u00a7°\u00a7¬\u00a7^");
        nvf_xSupression.put("\u00a78", "\u00a7°\u00a7¬\u00a7¨");
        nvf_xSupression.put("\u00a79", "\u00a7°\u00a7¬\u00a7$");

        nvf_xSupression.put("\u00a7a", "\u00a7°\u00a7¡\u00a7j");
        nvf_xSupression.put("\u00a7b", "\u00a7°\u00a7¡\u00a7v");
        nvf_xSupression.put("\u00a7c", "\u00a7°\u00a7¡\u00a7¡");
        nvf_xSupression.put("\u00a7d", "\u00a7°\u00a7¡\u00a7z");
        nvf_xSupression.put("\u00a7A", "\u00a7°\u00a7j\u00a7j");
        nvf_xSupression.put("\u00a7B", "\u00a7°\u00a7j\u00a7v");
        nvf_xSupression.put("\u00a7C", "\u00a7°\u00a7j\u00a7¡");
        nvf_xSupression.put("\u00a7D", "\u00a7°\u00a7j\u00a7z");

        nvf_xSupression.put("\u00a7e", "\u00a7°\u00a7¿\u00a7j");
        nvf_xSupression.put("\u00a7f", "\u00a7°\u00a7¿\u00a7v");
        nvf_xSupression.put("\u00a7k", "\u00a7°\u00a7¿\u00a7¡");
        nvf_xSupression.put("\u00a7l", "\u00a7°\u00a7¿\u00a7z");
        nvf_xSupression.put("\u00a7E", "\u00a7°\u00a7v\u00a7j");
        nvf_xSupression.put("\u00a7F", "\u00a7°\u00a7v\u00a7v");
        nvf_xSupression.put("\u00a7K", "\u00a7°\u00a7v\u00a7¡");
        nvf_xSupression.put("\u00a7L", "\u00a7°\u00a7v\u00a7z");

        nvf_xSupression.put("\u00a7m", "\u00a7°\u00a7$\u00a7j");
        nvf_xSupression.put("\u00a7n", "\u00a7°\u00a7$\u00a7v");
        nvf_xSupression.put("\u00a7o", "\u00a7°\u00a7$\u00a7¡");
        nvf_xSupression.put("\u00a7r", "\u00a7°\u00a7$\u00a7z");
        nvf_xSupression.put("\u00a7x", "\u00a7°\u00a7$\u00a7¿");
        nvf_xSupression.put("\u00a7M", "\u00a7°\u00a7y\u00a7j");
        nvf_xSupression.put("\u00a7N", "\u00a7°\u00a7y\u00a7v");
        nvf_xSupression.put("\u00a7O", "\u00a7°\u00a7y\u00a7¡");
        nvf_xSupression.put("\u00a7R", "\u00a7°\u00a7y\u00a7z");
        nvf_xSupression.put("\u00a7X", "\u00a7°\u00a7y\u00a7¿");
    }

    /**
     * Examines every lore line and reparses all GooP Name Variables
     */
    @Nullable
    public static ItemStack RevariabilizeLore(@Nullable ItemStack iSource, @NotNull NameVariableOperation nvo, @Nullable Player asPlayer) {
        // Error L
        if (iSource == null) { return null; }

        // Get all tis lore
        ArrayList<String> rLore = OotilityCeption.GetLore(iSource);
        ArrayList<String> tLore = new ArrayList<>();

        // Cook them all
        for (String str : rLore) {

            // Bake
            String fin = OotilityCeption.RerenameNameVarialbes(asPlayer, nvo, str, null, iSource, false);

            // Add I Gues
            tLore.add(fin);
        }

        // Set lore
        return OotilityCeption.SetLore(iSource, tLore);
    }

    /**
     * Will perform such naming operation with such string.
     * <p></p>
     * Use <b><~and></b> to denote ampersands that are not color codes.
     *
     * */
    @NotNull
    public static String RerenameNameVarialbes(@Nullable Player whom, @NotNull NameVariableOperation nvOperation, @Nullable String iSource, @Nullable Block blockForPlaceholders, @Nullable ItemStack itemForPlaceholders) {
        return RerenameNameVarialbes(whom, nvOperation, iSource, blockForPlaceholders, itemForPlaceholders, true);
    }
    public static String RerenameNameVarialbes(@Nullable Player whom, @NotNull NameVariableOperation nvOperation, @Nullable String iSource, @Nullable Block blockForPlaceholders, @Nullable ItemStack itemForPlaceholders, boolean nameAsReplace) {
        if (iSource == null) { iSource = ""; } else { iSource = iSource.replace("&", "<~and>"); }

        // Boolean to know if even tyring to do all this craze
        boolean performOperation = !nvOperation.isReplace();
        if (nvOperation.isReplace()) {

            // Does it have two
            int firstat = nvOperation.getReplace().indexOf('@');

            if (firstat >= 0) {

                // Sub
                String secondat = nvOperation.getReplace().substring(firstat + 1);

                // L
                performOperation = secondat.contains("@");
            }

        }

        // Does it have colons bruh
        if (performOperation) {
           //NEK*/Log("\u00a7e>>>>>>>>> \u00a76Performing Naming Operaions:");
           //NEK*/Log("\u00a7e>>>> \u00a77Operation:\u00a73 " + nvOperation.asOperation);
           //NEK*/Log("\u00a7e>>>> \u00a77Source:\u00a73 " + iSource);

            // Build the string to build
            String iOperation = "";

            // If its just updating the placeholders, the operation is just the source ffs
            if (nvOperation.isReplaceholder()) {
               //NEK*/Log("\u00a7cPerforming as Placeholder");

                // Nothing special
                iOperation = DenameNameVariables(iSource);
               //NEK*/Log("\u00a7fOperation: \u00a73" + iOperation);

            } else {

                // Extrakt all the existing variable values
                String rawSource = DenameNameVariables(iSource);
               //NEK*/Log("Denamed: " + rawSource);

                // Organize Values
                HashMap<String, NameVariable> preRNV = new HashMap<>();
                ArrayList<NameVariable> preRNA = new ArrayList<>();

                // Split
                String[] kourse = rawSource.split("@");
                boolean kounterAppend = true;
                boolean nameDefined = false;

               //NEK*/Log(" \u00a7e----------------- \u00a77Evaluating Source \u00a7e-----------------");
                // Evaluate each ig
                for (String str : kourse) {
                   //NEK*/Log("\u00a7e>>>\u00a77 " + str);

                    // Has it a code
                    NameVariable code = null;
                    if (!kounterAppend) {
                       //NEK*/Log("\u00a7b     +\u00a77 Could be Variable");

                        // Valid as a name varialbe?
                        code = GetNameVariable(str);
                    }

                    // Did it exist?
                    if (code != null) {
                       //NEK*/Log("\u00a73     +\u00a77 Found Variable");
                       //NEK*/Log("\u00a7e     vari>\u00a77 " + code.getIdentifier());
                       //NEK*/Log("\u00a76     valu>\u00a77 " + code.getValue());

                        // Register varialbe if new
                        NameVariable oused = preRNV.get(code.getIdentifier());
                        if (oused == null) { preRNV.put(code.getIdentifier(), code);
                        if (code.getIdentifier().equals("name")) { nameDefined = true; } }
                       //NEK*/Log("\u00a7c     valf>\u00a77 " + preRNV.get(code.getIdentifier()).getValue());

                        // Add it regardless
                        preRNA.add(code);
                       //NEK*/Log("\u00a7c + " + code.getIdentifier() + "\u00a77." + code.getValue());

                        // Counter next colon append
                        kounterAppend = true;

                    // Was not a code, return original colon
                    } else {

                        // If last one wasnt a code
                        if (!kounterAppend) {

                            // Append as a simple string
                            if (str.length() > 0) {
                                preRNA.add(new NameVariable(null, "@" + str));
                               //NEK*/Log("\u00a7c +\u00a77 @" + str);
                            }

                        } else {

                            // NExt one shall again
                            kounterAppend = false;

                            // Append as a simple string
                            if (str.length() > 0) {
                                preRNA.add(new NameVariable(null, str));
                               //NEK*/Log("\u00a7c +\u00a77 " + str);
                            }
                        }
                    }
                }

                // Requires name var to be defined?
                boolean nameRequired = false;

                // Edit variables based on the nvOperation's contets
                if (nvOperation.isRevariable()) {
                   //NEK*/Log("\u00a76It is a revar operation. Interpreting...");

                    // Go through each registered varialbe
                   //NEK*/Log(" \u00a7e>~~~~~~ \u00a77Reading Operation Vars \u00a7e~~~~~~<");
                    for (NameVariable nv : nvOperation.getVariables()) {
                       //NEK*/Log("\u00a7e>>>\u00a77 " + nv);

                        // Define name
                        if (nv.getIdentifier().equals("name")) {

                            // Is name
                            nameRequired = true;
                           //NEK*/Log("\u00a7b  ~ ~ It is the \u00a7nname\u00a7b ~ ~ ~");
                        }

                        // Add if contained
                        if (preRNV.containsKey(nv.getIdentifier())) {
                           //NEK*/Log("\u00a7b     ?\u00a77 Replaced Variable");

                            // Edit its preRNV
                            preRNV.put(nv.getIdentifier(), nv);
                           //NEK*/Log("\u00a7a     now>\u00a77 " + nv);

                        // Its being set I gues
                        } else {

                            // Edit its preRNV
                            preRNV.put(nv.getIdentifier(), nv);
                           //NEK*/Log("\u00a7a     put>\u00a77 " + nv);
                        }
                    }

                    // Find name
                    if (nameRequired && !nameDefined && nameAsReplace) {
                       //NEK*/Log("\u00a7b  ~ ~ Seeking \u00a7nname\u00a7b ~ ~ ~");

                        // Scry the largest unvariabled text
                        int larg8st = -1, largestIndex = -1;

                        // For each defined variable
                        for (int c = 0; c < preRNA.size(); c++) {

                            // Get
                            NameVariable nv = preRNA.get(c);

                            // If has no identifier
                            if (!nv.isVariable()) {

                                // Whats its length?
                                int length = nv.getValue().length();

                                // Laerger?
                                if (length > larg8st) {

                                    // Store
                                    larg8st = length;

                                    // Store index
                                    largestIndex = c;
                                }
                            }
                        }

                        // Name not found? That transforms all this into a replace name operation BRUH
                        if (largestIndex < 0) {
                            //NEK*/Log("\u00a7b  ~ ~ Transforming into a \u00a79Replace Operation\u00a7b ~ ~ ~");

                            // iOperation becomes the value of this
                            NameVariableOperation trueNVM = new NameVariableOperation(nvOperation.getVariable("name").getValue());

                            // Take that path
                            return RerenameNameVarialbes(whom, trueNVM, iSource, blockForPlaceholders, itemForPlaceholders);

                        // Largest Index Found, intercept
                        } else {

                            // Thats the name now
                            preRNA.get(largestIndex).setIdentifier("name");
                           //NEK*/Log("\u00a73 ~ ~ Name Indexed At: \u00a77" + largestIndex);
                           //NEK*/Log("\u00a73       ~~> \u00a77" + preRNA.get(largestIndex));
                        }
                    }

                    // If it is only changing the variables, this builds the same thing as source but replacing old variables
                    StringBuilder iOBuilder = new StringBuilder("");

                   //NEK*/Log(" \u00a76>~~~>>~ \u00a7fBuilding IOperation \u00a76~<<~~~<");
                    for (NameVariable nv : preRNA) {
                       //NEK*/Log("\u00a7e>>>\u00a77 " + nv);

                        // Was it an actual variable?
                        if (nv.isVariable()) {

                            // Does it have a value?
                            if (preRNV.get(nv.getIdentifier()) != null) {

                                // Append as variable
                                iOBuilder.append("@").append(nv.getIdentifier()).append("=").append(preRNV.get(nv.getIdentifier()).getValue()).append("@");
                               //NEK*/Log("\u00a76   + \u00a77 @" + nv.getIdentifier() + "=" + preRNV.get(nv.getIdentifier()).getValue() + "@");

                            } else {

                               //NEK*/Log("\u00a7cFATAL ERROR:\u00a77 This should really have a value - " + nv);
                            }

                        // Not a variable, not our business
                        } else {

                            // Append whatever
                            iOBuilder.append(nv.getValue());
                           //NEK*/Log("\u00a76   + \u00a77 " + nv.getValue());
                        }
                    }

                    // Build string
                    iOperation = iOBuilder.toString();
                    //NEK*/Log("\u00a7e>>> IOperation:\u00a77   \"" + iOperation + "\"");

                // If it is replace, however, it may be reordering and shit
                } else if (nvOperation.isReplace()) {
                   //NEK*/Log("\u00a76It is a replace operation. Obtaining final order");

                    // Extract real order
                    ArrayList<NameVariable> realOrder = new ArrayList<>();
                    HashMap<String, NameVariable> repRNV = new HashMap<>();
                    ArrayList<NameVariable> repRNA = new ArrayList<>();

                    // Split
                    String[] mourse = nvOperation.getReplace().split("@");
                    kounterAppend = true;

                   //NEK*/Log(" \u00a7a----------------- \u00a77Evaluating Replace \u00a7a-----------------");
                    // Evaluate each ig
                    for (String str : mourse) {
                       //NEK*/Log("\u00a7a>>>\u00a77 " + str);

                        // Has it a code
                        NameVariable code = null;
                        boolean addedAsSpec = false;
                        if (!kounterAppend) {
                           //NEK*/Log("\u00a7b     +\u00a77 Could be Variable");

                            // Valid as a name varialbe?
                            code = GetNameVariable(str);

                            // Null but has no spaces?
                            if (code == null && !str.contains(" ")) {
                               //NEK*/Log("\u00a7b     ?\u00a77 Undefined Variable");

                                // The new one will take priority if defined
                                if (repRNV.get(str) != null) {
                                   //NEK*/Log("\u00a73     ?\u00a77 Found Current Definition (\u00a78repRNV\u00a73)");

                                    // Found value. Bake
                                    code = new NameVariable(str, repRNV.get(str).getValue());

                                // If the new wasnt defined but was in original, use original
                                } else  if (preRNV.get(str) != null) {
                                   //NEK*/Log("\u00a73     ?\u00a77 Found Source Definition (\u00a78preRNV\u00a73)");

                                    // Found value. Bake
                                    code = new NameVariable(str, preRNV.get(str).getValue());

                                // A code but not defined; hmmm
                                } else {
                                   //NEK*/Log("\u00a7c     ?\u00a77 Found No Definition");

                                    // Add the whole source
                                    if (str.equals("fullname") || str.equals("otherx") || str.equals("other") || str.equals("name")) {
                                       //NEK*/Log("\u00a7a     vari>\u00a77 " + str);

                                        // Exists
                                        addedAsSpec = true;

                                        // Oh yea add all those Bs
                                        repRNA.add(new NameVariable(str, ""));
                                       //NEK*/Log("\u00a7c + " + str + "\u00a77.\u00a78[SPEC]");

                                        // Counter next colon append
                                        kounterAppend = true;
                                    }
                                }
                            }
                        }

                        // If it hasnt been added yet
                        if (!addedAsSpec) {

                            // Did it exist?
                            if (code != null) {
                               //NEK*/Log("\u00a73     +\u00a77 Found Variable");
                               //NEK*/Log("\u00a7a     vari>\u00a77 " + code.getIdentifier());
                               //NEK*/Log("\u00a72     valu>\u00a77 " + code.getValue());

                                // Register varialbe if new
                                NameVariable oused = repRNV.get(code.getIdentifier());
                                if (oused == null) { repRNV.put(code.getIdentifier(), code); }
                                if (code.getIdentifier().equals("name")) { nameDefined = true; }
                               //NEK*/Log("\u00a7c     valf>\u00a77 " + repRNV.get(code.getIdentifier()).getValue());

                                // Add it regardless
                                repRNA.add(code);
                               //NEK*/Log("\u00a7c + " + code.getIdentifier() + "\u00a77." + code.getValue());

                                // Counter next colon append
                                kounterAppend = true;

                            // Was not a code, return original colon
                            } else {

                                // If last one wasnt a code
                                if (!kounterAppend) {

                                    /*/ Append as a simple string
                                    if (str.length() > 0) {
                                        repRNA.add(new NameVariable(null, "@" + str));
                                       /*NEK//Log("\u00a7c +\u00a77 @" + str);
                                    }   //*/

                                } else {

                                    // NExt one shall again
                                    kounterAppend = false;

                                    // Append as a simple string
                                    if (str.length() > 0) {
                                        repRNA.add(new NameVariable(null, str));
                                       //NEK*/Log("\u00a7c +\u00a77 " + str);
                                    }
                                }
                            }
                        }
                    }

                   //NEK*/Log(" \u00a7e>~~~~~~ \u00a77Filling Olden Vars \u00a7e~~~~~~<");
                    // Gather olden values
                    for (NameVariable nv : repRNA) {
                       //NEK*/Log("\u00a76>>>\u00a77 " + nv);

                        // If it is a variable
                        if (nv.isVariable()) {

                            // If missing from repRNV
                            if (repRNV.get(nv.getIdentifier()) == null) {
                               //NEK*/Log("\u00a7c     - \u00a77 Missing (\u00a78repRNV\u00a77)");

                                // If contained in pre
                                if (preRNV.containsKey(nv.getIdentifier())) {
                                   //NEK*/Log("\u00a7e     + \u00a77 Found (\u00a78preRNV\u00a77)");

                                    // Add from preRNV
                                    repRNV.put(nv.getIdentifier(), preRNV.get(nv.getIdentifier()));
                                   //NEK*/Log("\u00a76        > " + preRNV.get(nv.getIdentifier()));
                                }
                            }

                            // Is it name?
                            if (nv.getIdentifier().equals("name")) {
                                //DBG//Log("\u00a7b  ~ ~ It is the \u00a7nname\u00a7b ~ ~ ~");

                                // Define name
                                nameRequired = true;
                            }
                        }
                    }

                    // Find name
                    if (nameRequired && !nameDefined) {
                       //NEK*/Log("\u00a7b  ~ ~ Seeking \u00a7nname\u00a7b ~ ~ ~");

                        // Scry the largest unvariabled text
                        int larg8st = -1, largestIndex = -1;

                        // For each defined variable
                        for (int c = 0; c < preRNA.size(); c++) {

                            // Get
                            NameVariable nv = preRNA.get(c);

                            // If has no identifier
                            if (!nv.isVariable()) {

                                // Whats its length?
                                int length = nv.getValue().length();

                                // Laerger?
                                if (length > larg8st) {

                                    // Store
                                    larg8st = length;

                                    // Store index
                                    largestIndex = c;
                                }
                            }
                        }

                        // Name not found? That transforms all this into a replace name operation BRUH
                        if (largestIndex < 0) {

                            // All names become the equivalent of fullname
                            for (NameVariable nv : repRNA) {

                                // If it is a variable
                                if (nv.isVariable()) {

                                    // IF name, set as full name
                                    if (nv.getIdentifier().equals("name")) { nv.setIdentifier("fullname"); }
                                }
                            }

                            // Largest Index Found, intercept
                        } else {

                            // Thats the name now
                            preRNA.get(largestIndex).setIdentifier("name");
                            preRNV.put("name", preRNA.get(largestIndex));
                            repRNV.put("name", preRNA.get(largestIndex));
                           //NEK*/Log("\u00a73 ~ ~ Name Indexed At: \u00a77" + largestIndex);
                           //NEK*/Log("\u00a73       ~~> \u00a77" + preRNA.get(largestIndex));
                        }
                    }

                    // Count usages
                    HashMap<String, Integer> logRep = new HashMap<>();
                    HashMap<String, Integer> logExp = new HashMap<>();
                    HashMap<String, Integer> logPre = new HashMap<>();

                    // For each variable in the original
                    for (NameVariable nv : preRNA) {

                        // If it is a variable
                        if (nv.isVariable()) {

                            // If it is not the specs
                            if (!nv.getIdentifier().equals("other") && !nv.getIdentifier().equals("otherx") && !nv.getIdentifier().equals("fullname")) {

                                // For each added in the original; count
                                Integer current = logPre.get(nv.getIdentifier());
                                if (current == null) { current = 0; } current++;
                                logPre.put(nv.getIdentifier(), current);
                            }
                        }
                    }
                    // For each variable in the expected
                    for (NameVariable nv : repRNA) {

                        // Only for valid variables
                        if (nv.isVariable()) {

                            // If it is not the specs
                            if (!nv.getIdentifier().equals("other") && !nv.getIdentifier().equals("otherx") && !nv.getIdentifier().equals("fullname")) {

                                // For each added in the original; count
                                Integer current = logExp.get(nv.getIdentifier());
                                if (current == null) { current = 0; }
                                current++;

                                logExp.put(nv.getIdentifier(), current);
                            }
                        }
                    }


                   //NEK*/Log(" \u00a7a>~~~>>~ \u00a7fBuilding Real Order \u00a7a~<<~~~<");
                    // Build the thing
                    for (NameVariable nv : repRNA) {
                       //NEK*/Log("\u00a7a>>>\u00a77 " + nv);

                        // Add if not variable
                        if (!nv.isVariable()) {
                           //NEK*/Log("\u00a7c  ~>\u00a77 Not a Variable");

                            realOrder.add(nv);
                           //NEK*/Log("\u00a7c   ~+>\u00a77 " + nv);

                        // It is a variable
                        } else {

                            // If it is full name
                            if (nv.getIdentifier().equals("fullname")) {
                               //NEK*/Log("\u00a7c  ~> \u00a7nFull Name");

                                // Add the whole damn source
                                realOrder.addAll(preRNA);
                                //NEK*/for (NameVariable var2 : preRNA) { Log("\u00a7c   ~+>\u00a77 " + var2);  }

                            // If its those unused
                            } else if (nv.getIdentifier().equals("other")) {
                               //NEK*/Log("\u00a7c  ~> \u00a7nOther");

                                // Go through all the originals and add if they dont have alogExp count
                                for (NameVariable preNV : preRNA) {

                                    // Must be variable
                                    if (preNV.isVariable()) {

                                        // Does it have a count?
                                        if (logExp.get(preNV.getIdentifier()) == null) {

                                            // Add thus
                                            realOrder.add(preNV);
                                           //NEK*/Log("\u00a7c   ~+>\u00a77 " + preNV);

                                        } else {

                                           //NEK*/Log("\u00a74   ~-~\u00a77 " + preNV);
                                        }

                                    // Add non variabels
                                    } else {

                                        // Just add
                                        realOrder.add(preNV);
                                       //NEK*/Log("\u00a7c   ~+>\u00a77 " + preNV);
                                    }
                                }

                            // Add those missing in the current than exist in the original
                            } else if (nv.getIdentifier().equals("otherx")) {
                               //NEK*/Log("\u00a7c  ~> \u00a7nOther X");

                                HashMap<String, Integer> logLoc = new HashMap<>();

                                // Through them originals in order
                                for (NameVariable onv : preRNA) {

                                    // Only adds variables
                                    if (onv.isVariable()) {

                                        // Get this count
                                        Integer localCurrent = logLoc.get(onv.getIdentifier());
                                        if (localCurrent == null) { localCurrent = 0; } localCurrent++;
                                        logLoc.put(onv.getIdentifier(), localCurrent);

                                        // How many have been
                                        Integer current = logRep.get(onv.getIdentifier());
                                        Integer maximum = logExp.get(onv.getIdentifier());
                                        Integer original = logPre.get(onv.getIdentifier());
                                        if (current == null) { current = 0; }
                                        if (maximum == null) { maximum = 0; }
                                        if (original == null) { original = 0; }
                                        int tobeadded = maximum - current;
                                        int tobeaddedLocal = original - localCurrent;
                                       //NEK*/Log("\u00a7e   ~ Maxim:\u00a7f " + maximum);
                                       //NEK*/Log("\u00a76   ~ Origi:\u00a7f " + original);
                                       //NEK*/Log("\u00a7b   ~ LCurr:\u00a7f " + localCurrent);
                                       //NEK*/Log("\u00a73   ~ GCurr:\u00a7f " + current);
                                       //NEK*/Log("\u00a7a   ~ LTBAd:\u00a7f " + tobeaddedLocal);
                                       //NEK*/Log("\u00a72   ~ GTBAd:\u00a7f " + tobeadded);

                                        // If enough ahve been added, straight up add no more alv
                                        if (maximum < original) {

                                            // If this one exceedes the number already-added
                                            if (localCurrent > current) {

                                                // If this is missing more than will be added
                                                if (tobeaddedLocal >= tobeadded) {

                                                    // Add thus
                                                    realOrder.add(onv);
                                                   //NEK*/Log("\u00a7c   ~+>\u00a77 " + onv);
                                                } else {

                                                   //NEK*/Log("\u00a74   ~ Local tba exceeds original tba");
                                                   //NEK*/Log("\u00a74   ~-~\u00a77 " + onv);
                                                }

                                            } else {

                                               //NEK*/Log("\u00a74   ~ Local current exceeds original current");
                                               //NEK*/Log("\u00a74   ~-~\u00a77 " + onv);
                                            }

                                        } else {
                                           //NEK*/Log("\u00a74   ~ Maximum exceeds Original");
                                           //NEK*/Log("\u00a74   ~-~\u00a77 " + onv);
                                        }

                                    // add non-variables unrestrictedly
                                    } else {

                                        // Just add
                                        realOrder.add(onv);
                                       //NEK*/Log("\u00a7c   ~+>\u00a77 " + onv);
                                    }
                                }

                            // Not special
                            }  else {
                               //NEK*/Log("\u00a7c  ~>\u00a77 Simple Variable");

                                // Add
                                realOrder.add(nv);
                               //NEK*/Log("\u00a7c   ~+>\u00a77 " + nv);

                                // For each added in the original; count
                                Integer current = logRep.get(nv.getIdentifier());
                                if (current == null) { current = 0; } current++;
                                logRep.put(nv.getIdentifier(), current);
                               //NEK*/Log("\u00a74   ~#>\u00a77 " + current);
                            }

                        }
                    }

                   //NEK*/Log(" \u00a7e>~~~>>~ \u00a77Refilling Olden Vars \u00a7e~<<~~~<");
                    // Re-Gather olden values (i guess)
                    for (NameVariable nv : realOrder) {
                       //NEK*/Log("\u00a7e>>>\u00a77 " + nv);

                        // If it is a variable
                        if (nv.isVariable()) {

                            // If missing from repRNV
                            if (repRNV.get(nv.getIdentifier()) == null) {
                               //NEK*/Log("\u00a7c     - \u00a77 Missing (\u00a78repRNV\u00a77)");

                                // If contained in pre
                                if (preRNV.containsKey(nv.getIdentifier())) {
                                   //NEK*/Log("\u00a7e     + \u00a77 Found (\u00a78preRNV\u00a77)");

                                    // Add from preRNV
                                    repRNV.put(nv.getIdentifier(), preRNV.get(nv.getIdentifier()));
                                   //NEK*/Log("\u00a76        > " + preRNV.get(nv.getIdentifier()));
                                }
                            }
                        }
                    }

                    // If it is only changing the variables, this builds the same thing as source but replacing old variables
                    StringBuilder iOBuilder = new StringBuilder("");

                    //NEK*/Log(" \u00a76>~~~>>~ \u00a7fBuilding IOperation \u00a76~<<~~~<");
                    for (NameVariable nv : realOrder) {
                        //NEK*/Log("\u00a7e>>>\u00a77 " + nv);

                        // Was it an actual variable?
                        if (nv.isVariable()) {

                            // Append as variable
                            iOBuilder.append("@").append(nv.getIdentifier()).append("=").append(repRNV.get(nv.getIdentifier()).getValue()).append("@");

                            //NEK*/Log("\u00a76   + \u00a77 @" + nv.getIdentifier() + "=" + repRNV.get(nv.getIdentifier()).getValue() + "@");

                            // Not a variable, not our business
                        } else {

                            // Append whatever
                            iOBuilder.append(nv.getValue());
                            //NEK*/Log("\u00a76   + \u00a77 " + nv.getValue());
                        }
                    }

                    // Build string
                    iOperation = iOBuilder.toString();
                    //NEK*/Log("\u00a7e>>> IOperation:\u00a77   \"" + iOperation + "\"");
                }
            }

            // Organize Values
            HashMap<String, NameVariable> sRNV = new HashMap<>();

            // Split
            String[] kodes = iOperation.split("@");

            // Is it only the name :wazowskibruhmoment:
            if (kodes.length == 2 && iOperation.startsWith("@name=")) {
                //NEK*/Log("\u00a77 ~ Apparently Name Only ~ ");

                // Get actual name
                NameVariable iName = GetNameVariable(kodes[1]);

                // Valid? Game.
                if (iName != null) {

                    // Transfurm
                    String iOName = iName.getValue().replace("<&at>", "@").replace("<&pc>", "%");

                    //NEK*/Log("\u00a76Name Only. \u00a77Result: " + iOName);

                    // Thats it
                    return ParseColour(iOName).replace("<~and>", "&");
                }
            }

            // Finished rebuilt
            StringBuilder builder = new StringBuilder("");
            StringBuilder colorOutputCompound = new StringBuilder("");
            boolean kounterAppend = true;
            String lastColorOutput = "";

            //NEK*/Log(" \u00a76>~~~>>~~~>>~~~> \u00a7fBuilding Name \u00a76<~~~<<~~~<<~~~<");
            // Evaluate each ig
            for (String str : kodes) {
                //NEK*/Log("\u00a7e>>>\u00a77 " + str);

                // Has it a code
                String code = null;
                RefSimulator<NameVariable> rnv = new RefSimulator<>(null);
                if (!kounterAppend) {

                    // Gather
                    code = EncodedNameVariable(whom, str, lastColorOutput, rnv, blockForPlaceholders, itemForPlaceholders);

                    if (code == null && !(str.contains(" "))) {

                        // Attempt to get previous value
                        NameVariable sCode = sRNV.get(str);

                        // Existed?
                        if (sCode != null) { code = sCode.getValue(); } else { code = ""; }
                    }
                }

                // Did it exist?
                if (code != null) {

                    // If valid
                    if (code.length() > 0) {

                        // Append as-is
                        builder.append(code);

                        // Store varialbe
                        if (rnv.getValue() != null) {
                            sRNV.put(rnv.GetValue().getIdentifier(), rnv.getValue());
                        }
                    }

                    // Counter next colon append
                    kounterAppend = true;

                // Was not a code, return original colon
                } else {

                    // If last one wasnt a code
                    if (!kounterAppend) {

                        // Return
                        builder.append("@").append(str);

                    } else {

                        // Ok last one was a code, no need to return colon
                        builder.append(str);

                        // NExt one shall again
                        kounterAppend = false;
                    }

                }

                // Get Last Colour
                colorOutputCompound.append(str);
                lastColorOutput = ColorCodesOutput(ParseColour(colorOutputCompound.toString()));
            }

            // Solidify
            String finished = builder.toString();

            // Does it have an extra colon?
            if (!iOperation.startsWith("@") && finished.startsWith("@")) { finished = finished.substring(1); }
            // Return colons of ending
            if (iOperation.endsWith("@")) {

                // Identify last colon
                int trailingColons = 0;
                for (int t = (iOperation.length()-1); t >= 0; t--) {

                    // Is it a colon?
                    if (iOperation.charAt(t) == '@') {

                        // This is a trailing colon
                        trailingColons = t;

                        // Otherwise break
                    } else {

                        t = -1;
                    }
                }

                // Add 1 if last one was a counter parse
                if (kounterAppend) { trailingColons++; }

                // Get those
                String tColons = iOperation.substring(trailingColons);
                finished = finished + tColons;
            }

            // Transfurm
            finished = finished.replace("<&at>", "@").replace("<&pc>", "%").replace("<$at>", "@").replace("<$pc>", "%");

            // Return thay
            return ParseColour(finished).replace("<~and>", "&").replace("  ", " ");
        }

        // Transfurm
        String iOperation = nvOperation.getReplace().replace("<&at>", "@").replace("<&pc>", "%").replace("<$at>", "@").replace("<$pc>", "%");

       //NEK*/Log("\u00a7cNo Rename Operation Required. \u00a77Result: " + iOperation);
        // Not vald
        return iOperation.replace("  ", " ");
    }

    /**
     * Parses a name placeholder assuming it is in the correct sintax.
     * <p></p>
     * Original sintax: @name=Whatever Value@
     * <p>Sintax you'd pass onto this: name=Whatever Value</p>
     * <p></p>
     * Basically expects a name and an equals sign. If this is not the case, it returns null.
     */
    @Nullable
    static String EncodedNameVariable(@Nullable Player p, @NotNull String source, @Nullable String colorInformation, @Nullable RefSimulator<NameVariable> variableOutput, @Nullable Block asBlock, @Nullable ItemStack asItem) {
        if (colorInformation == null) { colorInformation = ""; }

        // Get Name Variable
        NameVariable nv = GetNameVariable(source);

        // Valid?
        if (nv != null) {

            // Get Identifier and Value
            String name = nv.getIdentifier();
            String containedValue = nv.getValue();

            // Encode name
            String encodedName = nameVariableFrames + IntermitentEncode(name, true) + nameVariableFrames + colorInformation + nameVariableColorDelimiter;

            // Gather the value and parse placeholders
            String value = EncodedNamePlaceholdersParse(p, containedValue, asBlock, asItem);

            // Build
            String retCode = encodedName + value + nameVariableFrames;

            // Fill RefSimulator
            if (variableOutput != null) { variableOutput.setValue(new NameVariable(name, retCode));}

            // Return Framed
            return retCode;
        }

        // Meh
        return null;
    }

    /**
     * If it exists, gets the name and value of something in the following format:
     * <p></p>
     * <code>[name with no spaces]=[value]</code>
     * <p></p>
     * As a NameVariable class.
     */
    @Nullable
    static NameVariable GetNameVariable(@NotNull String source) {

        // Equals sign and space
        int equalsLoc = source.indexOf("=");

        // Found equals?
        if (equalsLoc >= 1) {

            // Strip both sides
            String name = source.substring(0, equalsLoc);

            // It has no spaces right
            if (!name.contains(" ")) {

                // Contianed
                String containedValue = source.substring(equalsLoc + 1);

                // Return Framed
                return new NameVariable(name, containedValue);
            }
        }

        // Meh
        return null;
    }

    /**
     * Parses and Encodes placeholders to be used within names.
     */
    @NotNull
    static String EncodedNamePlaceholdersParse(@Nullable Player p, @NotNull String source, @Nullable Block asBlock, @Nullable ItemStack asItem) {

        // Does it have colons bruh
        if (source.contains("%")) {
            //NEK*/Log(" \u00a7eHad Placeholders:\u00a77 " + source);

            // Split
            String[] kodes = source.split("%");

            // Finished rebuilt
            StringBuilder builder = new StringBuilder("");
            StringBuilder asIntermitent = new StringBuilder("");
            boolean kounterAppend = true;
            boolean hidden = false;
            boolean asNumeric = false;
            String lastColorOutput = "";

            // Evaluate each ig
            //NEK*/Log(" \u00a76>~~~~~~ \u00a77Finding Placeholders \u00a76~~~~~~<");
            for (String str : kodes) {
                //NEK*/Log(" \u00a7e >>>\u00a77 " + str);

                // Has it a code
                String code = null;
                if (!kounterAppend) {
                    //NEK*/Log("    \u00a7a+ \u00a77Could be placeholder");

                    // Imagine if it still had the %s
                    String percents = "%" + str + "%";

                    // All right, lets see if this can call itself a placeholder
                    code = ParseConsoleCommand(percents, p, p, asBlock, asItem);
                    //NEK*/Log("   \u00a7a=> \u00a77Sent \u00a77" + percents);
                    //NEK*/Log("   \u00a72=> \u00a77Rcvd \u00a77" + code);

                    // *Does it look the same?* Then it wasnt a placeholder = meh
                    if (percents.equals(code)) { code = null; }
                }

                // Did it exist?
                if (code != null) {
                    //NEK*/Log("    \u00a7e+ \u00a77Was Placeholder");

                    // Was it numeric?
                    if (DoubleTryParse(code) && !hidden) {

                        // Parse
                        double asDouble = Double.parseDouble(code);
                        asNumeric = true;

                        // Make range
                        QuickNumberRange qnr = new QuickNumberRange(Gunging_Ootilities_Plugin.nameRangeExclusionMin, Gunging_Ootilities_Plugin.nameRangeExclusionMax);
                        //NEK*/Log("    \u00a7a? \u00a77Value \u00a7b" + asDouble + "\u00a77, fits between? \u00a7f" + Gunging_Ootilities_Plugin.nameRangeExclusionMin + " \u00a77 & \u00a7f" + Gunging_Ootilities_Plugin.nameRangeExclusionMax);

                        // Within range? Hide this operation
                        if (qnr.InRange(asDouble)) { hidden = true; }
                        //NEK*/Log("    \u00a7c- \u00a77Hidden? \u00a7e" + hidden);

                        // Make Readable
                        code = ReadableRounding(asDouble, Gunging_Ootilities_Plugin.placeholderReadableness);
                    }

                    // So the code is the placeholder but as parsed, now lets bake it
                    if (!hidden) {
                        String encodedPlaceholder = namePlaceholderFrames + IntermitentEncode(str, true) + namePlaceholderFrames + lastColorOutput + nameVariableColorDelimiter;

                        builder.append(encodedPlaceholder).append(code).append(namePlaceholderFrames);
                        //NEK*/Log(" \u00a7c+\u00a77 " + encodedPlaceholder + code + namePlaceholderFrames);
                    }

                    // Append and further encode
                    String hiddenPlaceholder = namePlaceholderHide + IntermitentEncode(str, true) + namePlaceholderHide;
                    asIntermitent.append(hiddenPlaceholder);
                    //NEK*/Log(" \u00a79+\u00a77 " + hiddenPlaceholder);

                    // Counter next colon append
                    kounterAppend = true;

                // Was not a code, return original colon
                } else {

                    // If last one wasnt a code
                    if (!kounterAppend) {

                        // Return
                        if (!hidden) {
                            builder.append("%").append(str);
                            //NEK*/Log(" \u00a7c+\u00a77 %" + str);
                        }
                        asIntermitent.append("\u00a7%").append(IntermitentEncode(str, true));
                        //NEK*/Log(" \u00a79+\u00a77 \u00a7%" + IntermitentEncode(str, true));

                    } else {

                        // Ok last one was a code, no need to return colon
                        if (!hidden) {
                            builder.append(str);
                            //NEK*/Log(" \u00a7c+\u00a77" + str);
                        }

                        asIntermitent.append(IntermitentEncode(str, true));
                        //NEK*/Log(" \u00a79+\u00a77 " + IntermitentEncode(str, true));

                        // NExt one shall again
                        kounterAppend = false;
                    }

                    // Not a placeholder, last becomes this I guess
                    lastColorOutput = ColorCodesOutput(ParseColour(str));
                }
            }

            // Solidify
            String finished = null;
            if (hidden) { finished = asIntermitent.toString(); } else { finished = builder.toString(); }

            // Extraneouschar
            String xChar = "%"; if (hidden) { xChar = "\u00a7%"; }

            // Does it have an extra colon?
            if (!source.startsWith("%") && finished.startsWith(xChar)) { finished = finished.substring(xChar.length()); }

            // Return colons of ending
            if (source.endsWith("%")) {

                // Identify last colon
                int trailingColons = 0;
                for (int t = (source.length()-1); t >= 0; t--) {

                    // Is it a colon?
                    if (source.charAt(t) == '%') {

                        // This is a trailing colon
                        trailingColons = t;

                        // Otherwise break
                    } else {

                        t = -1;
                    }
                }

                // Add 1 if last one was a counter parse
                if (kounterAppend) { trailingColons++; }

                // Get those
                String tColons = source.substring(trailingColons);
                if (hidden) { finished = finished + IntermitentEncode(tColons, true); } else { finished = finished + tColons; }
            }

            // Remove +-
            if (asNumeric) {

                // If numeric then it may be a plus negative trash
                finished = finished.replace("+-", "-");
            }

            // Return thay
            //NEK*/Log("\u00a76Result >>\u00a77 " + finished);
            return finished;
        }

        // Meh
        return source;

    }

    @Nullable
    public static String GetEncodedNameVariable(@Nullable String source, @NotNull String var) {
        if (source == null) { return null; }

        // Must be encoded
        if (!source.contains(nameVariableFrames)) { return null; }

        // Alr so decode
        String decoded = DenameNamePlaceholders(source);

        // Now search for name
        int varIDX = decoded.indexOf(var + "=");
        if (varIDX >= 0) {
            int lgth = varIDX + var.length() + 1;

            // ALr strip until next @
            int atIDX = decoded.indexOf('@', lgth);

            // Found?
            if (atIDX >= 0) {

                // Strip
                String value = decoded.substring(lgth, atIDX);
            }
        }

        // Found none
        return null;
    }

    /**
     * Returns an encrypted name string into its human-readable state..
     */
    @NotNull
    public static String DenameNameVariables(@NotNull String source) {

       //NEK*/Log("Received to Dename: " + source);

        // Does it have colons bruh
        if (source.contains(nameVariableFrames)) {

            // Split
            String[] kodes = source.split(nameVariableFrames);

            // Finished rebuilt
            StringBuilder builder = new StringBuilder("");
            boolean kounterAppend = true;
            boolean asValue = false;


           //NEK*/Log(" \u00a7b----------------- \u00a77Unparsing Name Vars \u00a7b-----------------");
            // Evaluate each ig
            for (String str : kodes) {
                // Unparse <&at>s
                str = str.replace("@", "<&at>");
               //NEK*/Log("\u00a7b>>>\u00a77 " + str);

                // Is it a code?
                if (!kounterAppend) {
                   //NEK*/Log("\u00a72     +\u00a77 Found Variable");

                    // But is it the value :0

                    // Remove Intermitent shit
                    String asRawCode = IntermitentDecode(str, true);

                    // Append decoded
                    builder.append("@").append(asRawCode).append("=");
                   //NEK*/Log("\u00a7c +\u00a77 @" + asRawCode + "=");

                    // Counter next colon append
                    kounterAppend = true;

                    // Next one is REALLY expected to be the value
                    asValue = true;

                // Was not a code, return original colon
                } else {

                    // We expecting the value?
                    if (asValue) {
                       //NEK*/Log("\u00a72     +\u00a77 Found Value");

                        // Next one wont be :thinking:
                        asValue = false;

                        String readableValue = DenameNamePlaceholders(str);

                        // Just append :)
                        builder.append(readableValue).append("@");
                       //NEK*/Log("\u00a7c +\u00a77 " + readableValue + "@");

                    } else {
                       //NEK*/Log("\u00a73     +\u00a77 Found Generic");

                        // Ok last one was a code, no need to return colon
                        builder.append(str);
                       //NEK*/Log("\u00a7c +\u00a77 " + str);

                        // NExt one shall again
                        kounterAppend = false;
                    }
                }
            }

            // Solidify
            String finished = builder.toString();

            // Does it have an extra colon?
            //finished = finished.substring(nameVariableFrames.length());

            // Return thay
            return finished;
        }

        // No need for operations
        return source;
    }

    /**
     * Returns an encrypted name parameter to human-readable state.
     * <p></p>
     * Will correctly revert encrypted placeholders too.
     */
    @NotNull
    static String DenameNamePlaceholders(@NotNull String source) {

       //NEK*/Log("\u00a7b>>> Received to Deplace: " + source);

        // Crop first color codes
        int firstColorDelimiter = source.indexOf(nameVariableColorDelimiter);
        if (firstColorDelimiter >= 0) {
            source = source.substring(firstColorDelimiter + nameVariableColorDelimiter.length());

           //NEK*/Log("\u00a7b>>> Adapted Deplace: " + source);
        }

        // Does it have colons bruh
        if (source.contains(namePlaceholderFrames)) {

            // Split
            String[] kodes = source.split(namePlaceholderFrames);

            // Finished rebuilt
            StringBuilder builder = new StringBuilder("");
            boolean kounterAppend = true;
            boolean asValue = false;

           //NEK*/Log(" \u00a7d----------------- \u00a77Unparsing Name Vars \u00a7d-----------------");
            // Evaluate each ig
            for (String str : kodes) {
                // Unparse <&pc>s
                str = str.replace("%", "<&pc>");
               //NEK*/Log("\u00a7d>>>\u00a77 " + str);

                // Did it exist?
                if (!kounterAppend) {
                   //NEK*/Log("\u00a75     +\u00a77 Found Variable");

                    // Remove Intermitent shit
                    String asRawPlaceholder = IntermitentDecode(str, true);

                    // Append decoded
                    builder.append("%").append(asRawPlaceholder).append("%");
                   //NEK*/Log("\u00a7c +\u00a77 %" + asRawPlaceholder + "%");

                    // Counter next colon append
                    kounterAppend = true;

                    // Next one is REALLY expected to be the value
                    asValue = true;

                    // Was not a code, return original colon
                } else {

                    // We expecting the value?
                    if (asValue) {
                       //NEK*/Log("\u00a7c     -\u00a77 Ignored Value");

                        // Next one wont be :thinking:
                        asValue = false;

                        // The value is not appended back, it is just deleted.

                    } else {
                       //NEK*/Log("\u00a75     +\u00a77 Found Generic");

                        // Ok last one was a code, no need to return colon
                        builder.append(str);
                       //NEK*/Log("\u00a7c +\u00a77 " + str);

                        // NExt one shall again
                        kounterAppend = false;
                    }
                }
            }

            // Solidify
            String finished = builder.toString();
           //NEK*/Log("\u00a7b>>>>>  \u00a77Result: " + finished);

            // Does it have an extra colon?
            //finished = finished.substring(namePlaceholderFrames.length());

            // Return thay
            return finished;
        }

        // Maybe it is a hidden placeholder placeholdeer
        else if (source.contains(namePlaceholderHide)) {

            // Split
            String[] kodes = source.split(namePlaceholderHide);

            // Finished rebuilt
            StringBuilder builder = new StringBuilder("");
            boolean kounterAppend = true;

            // Evaluate each ig
            for (String str : kodes) {
                // Unparse <&pc>s
                str = str.replace("%", "<&pc>");

                // Has it a code
                boolean asCode = false;
                if (!kounterAppend) {
                    asCode = true;
                }

                // Did it exist?
                if (asCode) {

                    // Remove Intermitent shit
                    String asRawPlaceholder = IntermitentDecode(str, true);

                    // Append decoded
                    builder.append("%").append(asRawPlaceholder).append("%");

                    // Counter next colon append
                    kounterAppend = true;

                    // Was not a code, return original colon
                } else {
                    // If last one wasnt a code
                    if (!kounterAppend) {

                        // Return
                        builder.append(namePlaceholderHide).append(IntermitentDecode(str, true));

                    } else {

                        // Ok last one was a code, no need to return colon
                        builder.append(IntermitentDecode(str, true));

                        // NExt one shall again
                        kounterAppend = false;
                    }
                }
            }

            // Solidify
            String finished = builder.toString();

            // Does it have an extra colon?
            finished = finished.substring(namePlaceholderHide.length());

            // Return thay
            return finished;
        }

        // No need for operations
        return source;
    }

    /**
     * Suppose you were to write another text after <b>source</b>.
     * <p></p>
     * This will give you the format codes it will adopt by being in front of <b>source</b>.
     * <p></p>
     * Will search and return in section signs.
     * <p>If no color codes are present it will return empty.</p>
     */
    @NotNull
    public static String ColorCodesOutput(@NotNull String source) {

        // Get as char array bruh
        ArrayList<Character> asCharArray = new ArrayList<>();
        for (char c : source.toCharArray()) { asCharArray.add(c); }

        // Appent
        StringBuilder str = new StringBuilder("");

        int lateColorIndex = 0;
        // From the end, which is the latest color code
        for (int c = (asCharArray.size() - 1); c >= 0; c--) {

            // If the current is & and the next fits
            boolean isAmpersand = asCharArray.get(c).equals('\u00a7');

            // Check
            if (isAmpersand && (c + 1 < asCharArray.size())) {

                // Get that char
                char ch = asCharArray.get(c + 1);

                // Is Code?
                boolean isCode = "0123456789AaBbCcDdEeFfRrXx".indexOf(ch) > -1;

                // Found last color code
                if (isCode) {

                    // Was it x, as to gather x?
                    if (ch == 'x') {

                        // If within range
                        if ((c + 13) < asCharArray.size()) {

                            // Must get next 6 codes
                            String asHex = IntermitentDecode(source.substring(c + 2), false);

                            // Gib the first six
                            String asHexVal = asHex.substring(0, 6);

                            // Append thay
                            String asHexCode = ChatColorFromHex(asHexVal);

                            // Valid? then append
                            if (asHexCode != null) {

                                // Append code
                                str.append(asHexCode);

                                // Store index
                                lateColorIndex = c + 13;

                                // Break
                                c = -10;
                            }
                        }

                    } else {

                        // Append code
                        str.append('\u00a7');
                        str.append(ch);

                        // Store index
                        lateColorIndex = c + 1;

                        // Break
                        c = -10;
                    }
                }
            }
        }

        // From the end, which is the latest color code
        for (int c = lateColorIndex; c < asCharArray.size(); c++) {

            // If the current is & and the next fits
            boolean isAmpersand = asCharArray.get(c).equals('\u00a7');

            // Check
            if (isAmpersand && (c + 1 < asCharArray.size())) {

                // Get that char
                char ch = asCharArray.get(c + 1);

                // Is Code?
                boolean isCode = "kKlLmMnNoO".indexOf(ch) > -1;

                // Found last color code
                if (isCode) {

                    // Append code
                    str.append('\u00a7');
                    str.append(ch);
                }
            }
        }

        // Build
        return str.toString();
    }
    //endregion

    //region Lore and NBT Manipulation
    public static Integer BakeIndex4Add(Integer original, int maxValue) {
        // If the original is null, it means the max value
        if (original == null) {

            return maxValue;

        // If the original is non-negative. It is a normal index
        } else if (original >= 0) {

            // If the original meets or exceeds the max value. its an error
            if (original <= maxValue) {

                // If the original fits in the max value, it is the correct index
                return original;

            // Index out of range
            } else {

                // Error
                return null;
            }

        // If the original is negative
        } else {

            // Sum it to the max value
            original += maxValue;

            // If it is at least 0
            if (original >= 0) {

                // Its a success
                return original;

            // Otherwise its out of range
            } else {

                return null;
            }
        }
    }
    public static Integer BakeIndex4Remove(Integer original, int maxValue) {
        // If the original is null, it means the max value
        if (original == null) {

            return (maxValue - 1);

        // If the original is non-negative. It is a normal index
        } else if (original >= 0) {

            // If the original meets or exceeds the max value. its an error
            if (original < maxValue) {

                // If the original fits in the max value, it is the correct index
                return original;

            // Index out of range
            } else {

                // Error
                return null;
            }

        // If the original is negative
        } else {

            // Sum it to the max value
            original += (maxValue - 1);

            // If it is at least 0
            if (original >= 0) {

                // Its a success
                return original;

            // Otherwise its out of range
            } else {

                return null;
            }
        }
    }
    @Nullable
    public static ItemStack RenameItem(@Nullable ItemStack iSource,@NotNull String tName,@Nullable  RefSimulator<String> logger) {
        return RenameItem(iSource, ParseColour(tName), null, logger);
    }
    @Nullable
    public static ItemStack RenameItem(@Nullable ItemStack iSource,@NotNull String tName, @Nullable Player parseAs, @Nullable  RefSimulator<String> logger) { return RenameItem(iSource, new NameVariableOperation(tName), parseAs, logger); }
    public static ItemStack RenameItem(@Nullable ItemStack iSource,@NotNull NameVariableOperation tName, @Nullable Player parseAs, @Nullable  RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Unrwap and reparse
                String tProcessed = RerenameNameVarialbes(parseAs, tName, OotilityCeption.GetItemName(iSource), null, iSource);

                // Return to Meta
                iMeta.setDisplayName(tProcessed);

                // Append to Source
                iSource.setItemMeta(iMeta);

                // Log Success
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Sucessfully renamed item to " + tProcessed);

                // Reutrn thay
                return  iSource;

                // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant rename air!");
                return null;
            }
        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant rename something that doesnt exist!");
            return null;

        }
    }

    public static ItemStack AppendLoreLine(@Nullable ItemStack iSource, @NotNull String tLoreLine, @Nullable  Integer index, @Nullable RefSimulator<String> logger) {
        return AppendLoreLine(iSource, tLoreLine, null, index, logger);
    }
    public static ItemStack AppendLoreLine(@Nullable ItemStack iSource, @NotNull String tLoreLine, @Nullable Player parseAs, @Nullable  Integer index, @Nullable RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Internally through MMOItems
                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Check if MMOItem
                    if (GooPMMOItems.IsMMOItem(iSource)) {

                        // Cook Lore Line. When adding lore there is no source, always brand new
                        tLoreLine = RerenameNameVarialbes(parseAs, new NameVariableOperation(tLoreLine), null, null, iSource);

                        // Cool MMOItem adding shit
                        ItemStack result = GooPMMOItems.MMOItemAddLoreLine(iSource, tLoreLine, index, logger);
                        return result;

                    // Just vanilla lol
                    } else {

                        // Do it vanilla wise
                        return  AppendLoreLineVanilla(iSource, tLoreLine, index, logger);
                    }

                // Just vanilla lol
                } else {

                    // Do it vanilla wise
                    return  AppendLoreLineVanilla(iSource, tLoreLine, index, logger);
                }

            // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant add lore lines to air!");
                return null;
            }
        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant add lore lines to something that doesnt exist!");
            return null;

        }
    }
    @Nullable
    public static ItemStack AppendLoreLineVanilla(@Nullable ItemStack sSource, @NotNull String tLoreLine,@Nullable  Integer index,@Nullable  RefSimulator<String> logger) {
        return AppendLoreLineVanilla(sSource, tLoreLine, null, index, logger);
    }
    @Nullable
    public static ItemStack AppendLoreLineVanilla(@Nullable ItemStack sSource, @NotNull String tLoreLine, @Nullable Player parseAs, @Nullable  Integer index,@Nullable  RefSimulator<String> logger) {

        // Dont modify the original bruh
        ItemStack iSource = new ItemStack(sSource);

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Cook Lore Line. When adding lore there is no source, always brand new
                tLoreLine = RerenameNameVarialbes(parseAs, new NameVariableOperation(tLoreLine), null, null, iSource);

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Get Existing Lore
                ArrayList<String> iLore = new ArrayList<String>();
                if (iMeta.getLore() != null) { iLore = new ArrayList<String>(iMeta.getLore()); }

                // Choose True Index
                Integer tIndex = BakeIndex4Add(index, iLore.size());
                if (tIndex == null) {
                    // Log Error:
                    OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Cant add lore line: Index out of range!");

                    // Nothing happen
                    return null;
                }

                // Add to Existing Lore
                iLore.add(tIndex, ParseColour(tLoreLine));

                // Return to Meta
                iMeta.setLore(iLore);

                // Append to Source
                iSource.setItemMeta(iMeta);

                // Log Success
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Sucessfully added lore line at index \u00a73" + tIndex + "\u00a77: " + ParseColour(tLoreLine));

                // Reutrn thay
                return  iSource;

            // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant add lore lines to air!");
                return null;
            }

        // Well thats null
        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant add lore lines to something that doesnt exist!");
            return null;

        }
    }

    public static ItemStack RemoveLoreLine(ItemStack iSource, Integer index, RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Internally through MMOItems
                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Check if MMOItem
                    if (GooPMMOItems.IsMMOItem(iSource)) {

                        // Cool MMOItem removing shit
                        return GooPMMOItems.MMOItemRemoveLoreLine(iSource, index, logger);

                    // Just vanilla lol
                    } else {

                        // Do It Vanilla
                        return RemoveLoreLineVanilla(iSource, index, logger);
                    }

                // Just vanilla lol
                } else {

                    // Do It Vanilla
                    return RemoveLoreLineVanilla(iSource, index, logger);
                }

            // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant remove lore lines from air!");
                return null;
            }
        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant remove lore lines from something that doesnt exist!");
            return null;

        }
    }
    public static ItemStack RemoveLoreLineVanilla(ItemStack iSource, Integer index, RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Get Existing Lore
                ArrayList<String> iLore = new ArrayList<String>();
                ArrayList<String> vLore = new ArrayList<String>();

                if (iMeta.getLore() != null) {
                    // If the item has any damn lore
                    iLore = new ArrayList<String>(iMeta.getLore());

                    // Make sure lore size is more than zero
                    if (iLore.size() > 0) {

                        // Choose True Index
                        Integer tIndex = BakeIndex4Remove(index, iLore.size());
                        if (tIndex == null) {
                            // Log Error:
                            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Cant remove lore line: Index out of bounds!");

                            // Nothing happen
                            return null;
                        }

                        // Rememberance 5 Log
                        String rememberance = "";

                        // Remove that lore line by copying all except it
                        for (int v = 0; v < iLore.size(); v++) {

                            // Copy if not the index to remove
                            if (v != tIndex) { vLore.add(iLore.get(v)); } else { rememberance = iLore.get(v); }
                        }

                        // Return to Meta
                        iMeta.setLore(vLore);

                        // Append to Source
                        iSource.setItemMeta(iMeta);

                        // Log Success
                        OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Sucessfully removed lore line at index \u00a73" + tIndex + "\u00a77: " + rememberance);

                        // Reutrn thay
                        return  iSource;

                    // Item has no lore
                    } else {

                        // Log and cancel
                        Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant remove lore if the item has no lore to begin with!");
                        return null;
                    }

                // Item has no lore
                } else {

                    // Log and cancel
                    Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant remove lore if the item has no lore to begin with!");
                    return null;
                }


            // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant remove lore lines from air!");
                return null;
            }

        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant remove lore lines from something that doesnt exist!");
            return null;

        }
    }

    /**
     * Retuns an array containing the lore of whatever provided.
     * <p></p>
     * Will retrieve MMOItems lore instead if the item is a MMOItem
     * <p></p>
     * Any errors return an empty array.
     */
    @NotNull
    public static ArrayList<String> GetLore(@Nullable ItemStack iSource) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Internally through MMOItems
                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Check if MMOItem
                    if (GooPMMOItems.IsMMOItem(iSource)) {

                        // Cool MMOItem adding shit
                        return GooPMMOItems.MMOItemGetLore(iSource);

                        // Just vanilla lol
                    } else {

                        // Do it vanilla wise
                        return GetLoreVanilla(iSource);
                    }

                    // Just vanilla lol
                } else {

                    // Do it vanilla wise
                    return GetLoreVanilla(iSource);
                }

                // Well that is air
            }
        }

        // Empty Array
        return new ArrayList<>();
    }
    @NotNull
    public static ArrayList<String> GetLoreVanilla(@Nullable ItemStack sSource) {

        // Dont modify the original bruh
        ItemStack iSource = sSource.clone();

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Get Existing Lore
                ArrayList<String> iLore = new ArrayList<String>();
                if (iMeta.getLore() != null) { iLore = new ArrayList<String>(iMeta.getLore()); }

                // Return thay
                return iLore;
            }
        }

        // Empty Array
        return new ArrayList<>();
    }

    /**
     * Sets the lore of an item.
     * <p></p>
     * Will target MMOItems lore instead if the item is a MMOItem
     * <p></p>
     * Any errors return a null item
     */
    @Nullable
    public static ItemStack SetLore(@Nullable ItemStack iSource, @Nullable ArrayList<String> lore) {
        if (lore == null) { lore = new ArrayList<>(); }

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Internally through MMOItems
                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Check if MMOItem
                    if (GooPMMOItems.IsMMOItem(iSource)) {

                        // Cool MMOItem adding shit
                        return GooPMMOItems.MMOItemSetLore(iSource, lore);

                        // Just vanilla lol
                    } else {

                        // Do it vanilla wise
                        return SetLoreVanilla(iSource, lore);
                    }

                    // Just vanilla lol
                } else {

                    // Do it vanilla wise
                    return SetLoreVanilla(iSource, lore);
                }

                // Well that is air
            }
        }

        // Empty Array
        return null;
    }
    @Nullable
    public static ItemStack SetLoreVanilla(@Nullable ItemStack sSource, @Nullable ArrayList<String> lore) {
        if (lore == null) { lore = new ArrayList<>(); }

        // Dont modify the original bruh
        ItemStack iSource = sSource.clone();

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Get Existing Lore
                iMeta.setLore(lore);

                // Set Meta
                iSource.setItemMeta(iMeta);

                // Return thay
                return iSource;
            }
        }

        // Empty Array
        return null;
    }

    public static ItemStack SetAttribute(ItemStack iSource, PlusMinusPercent operation, Attribute attrib, RefSimulator<Double> result, RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Internally through MMOItems
                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Check if MMOItem
                    if (GooPMMOItems.IsMMOItem(iSource)) {

                        // Cool MMOItem removing shit
                        return GooPMMOItems.MMOItemModifyAttribute(iSource, operation, attrib, result, logger);

                        // Just vanilla lol
                    } else {

                        // Do It Vanilla
                        return SetAttributeVanilla(iSource, operation, attrib, result, logger);
                    }

                    // Just vanilla lol
                } else {

                    // Do It Vanilla
                    return SetAttributeVanilla(iSource, operation, attrib, result, logger);
                }

                // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit attributes of air!");
                return null;
            }
        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit attributes of something that doesnt exist!");
            return null;

        }
    }
    public static ItemStack SetAttributeVanilla(ItemStack iSource, PlusMinusPercent operation, Attribute attrib, RefSimulator<Double> result, RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta and attributes
                ItemMeta iMeta = iSource.getItemMeta();

                // Get the whole multimap first
                Multimap<Attribute,AttributeModifier> attribs = iMeta.getAttributeModifiers();  // Whole Multimap
                Collection<AttributeModifier> iAttrib = null;   // Whole Attributes of same kind
                AttributeModifier tAttrib = null;   // Target Single Attribute
                boolean suc = false;

                // Extract the correct attributes if the multimap exists
                if (attribs != null) {

                    // Multimap Exists, so extracts attributes of same kind
                    iAttrib = attribs.get(attrib);

                    // Were there attributes of same kind?
                    if (iAttrib != null) {
                        if (iAttrib.size() > 0) {

                            // Get the first one alv
                            tAttrib = Iterables.get(iAttrib, 0);
                            suc = true;

                            // Removes it bruh
                            iMeta.removeAttributeModifier(attrib, tAttrib);

                            // Edit
                            Double fValue = operation.apply(tAttrib.getAmount());
                            if (result != null) { result.setValue(fValue);}
                            tAttrib = new AttributeModifier(UUID.randomUUID(), attrib.toString(), fValue, AttributeModifier.Operation.ADD_NUMBER, MostSenseFromItem(iSource.getType()));
                        }
                    }
                }

                // For whatever reason, there are no attributes of this kind to begin with
                if (!suc) {
                    double vanillaDefaulter = 0.0;
                    RefSimulator<Double> vanillaDefaulterRef = new RefSimulator<>(0.0);

                    // Gather initial vanilla
                    switch (attrib) {
                        case GENERIC_ATTACK_DAMAGE:
                            GatherDefaultVanillaAttributes(iSource.getType(), null, vanillaDefaulterRef, null, null, null, null);
                            if (vanillaDefaulterRef.getValue() > 0) { vanillaDefaulter = OotilityCeption.RoundAtPlaces(vanillaDefaulterRef.getValue(), 1); }
                            break;
                        case GENERIC_ATTACK_SPEED:
                            GatherDefaultVanillaAttributes(iSource.getType(), null, null, vanillaDefaulterRef, null, null, null);
                            vanillaDefaulter = vanillaDefaulterRef.GetValue();
                            break;
                        case GENERIC_ARMOR:
                            GatherDefaultVanillaAttributes(iSource.getType(), null, null, null, vanillaDefaulterRef, null, null);
                            vanillaDefaulter = vanillaDefaulterRef.GetValue();
                            break;
                        case GENERIC_ARMOR_TOUGHNESS:
                            GatherDefaultVanillaAttributes(iSource.getType(), null, null, null, null, vanillaDefaulterRef, null);
                            vanillaDefaulter = vanillaDefaulterRef.GetValue();
                            break;
                        case GENERIC_KNOCKBACK_RESISTANCE:
                            GatherDefaultVanillaAttributes(iSource.getType(), null, null, null, null, null, vanillaDefaulterRef);
                            vanillaDefaulter = vanillaDefaulterRef.GetValue();
                            break;
                    }

                    // Create new attribute
                    Double fValue = operation.apply(vanillaDefaulter);
                    if (result != null) { result.setValue(fValue);}
                    tAttrib = new AttributeModifier(UUID.randomUUID(), attrib.toString(), fValue, AttributeModifier.Operation.ADD_NUMBER, MostSenseFromItem(iSource.getType()));
                }

                // Put that damn attribute modifier in there
                iMeta.addAttributeModifier(attrib, tAttrib);

                // Insert into item
                iSource.setItemMeta(iMeta);

                // Finish
                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified attribute of \u00a7f" + GetItemName(iSource));  }
                return iSource;


            // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit attributes of air!");
                return null;
            }

        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit attributes of something that doesnt exist!");
            return null;

        }
    }
    public static EquipmentSlot MostSenseFromItem(Material source) {

        // Is it a weapon?
        if (IsMeleeWeapon(source) || IsRangedWeapon(source) || IsTool(source)) {

            // In the hand, cuz weapon
            return EquipmentSlot.HAND;

        } else if (IsArmor(source)) {

            if (IsBoots(source)) {

                // Boot ig
                return EquipmentSlot.FEET;

            } else if (IsLeggings(source)) {

                // Legg ig
                return EquipmentSlot.LEGS;

            } else if (IsChestplate(source)) {

                // Chest ig
                return EquipmentSlot.CHEST;

            } else {

                // Helmet ig
                return EquipmentSlot.HEAD;
            }

        } else {

            // Treat is as a damn charm
            return EquipmentSlot.OFF_HAND;
        }
    }

    /**
     * Returns the item stack having performed the operation on its CMD.
     * <p></p>
     * NULL if it fails for any reason, like:
     * <p> - MC version is not 1.14+
     * </p> - Item is null, or it cannot have CustomModelData
     * @param iSource Item to modify CustomModelData
     * @param operation Operation to apply to current CMD
     * @param result Stores the final CustomModelData for easy access
     * @param logger String saying what, if anything, went wrong.
     */
    @Nullable
    public static ItemStack SetCMD(@Nullable ItemStack iSource, @NotNull  PlusMinusPercent operation, @Nullable RefSimulator<Double> result, @Nullable RefSimulator<String> logger) {

        if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0) {

            // Log and cancel
            Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Failed to edit Custom Model Data: \u00a7cMinecraft Version Must be at least 1.14");
            return null;
        }

            // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta and attributes
                ItemMeta iMeta = iSource.getItemMeta();

                // Get base model data I guess
                int mData = 0;
                if (iMeta.hasCustomModelData()) { mData = iMeta.getCustomModelData(); }

                // Apply operation
                mData = (int)Math.round(operation.apply((double)mData));
                if (result != null) { result.setValue((double)mData);}

                // Edit meta
                iMeta.setCustomModelData(mData);

                // Insert into item
                iSource.setItemMeta(iMeta);

                // Finish
                if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) { Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified Custom Model Data of \u00a7f" + GetItemName(iSource));  }
                return iSource;


            // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit custom model data of air!");
                return null;
            }

        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit custom model data of something that doesnt exist!");
            return null;

        }
    }

    /**
     * Returns the item stack having performed the operation on its Amount.
     * <p></p>
     * NULL if it fails for any reason, like:
     * </p> - Item is null
     * @param iSource Item stack to modify amount
     * @param operation Operation to apply to current amount
     * @param result Stores the final Amount for easy access
     * @param logger String saying what, if anything, went wrong.
     * @return DEBUG_STICK ItemStack if Amount ends up being ZERO or negative.
     */
    @Nullable
    public static ItemStack SetAmount(@Nullable ItemStack iSource, @NotNull PlusMinusPercent operation, @Nullable RefSimulator<Double> result, @Nullable RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Source name
                String sName = GetItemName(iSource);

                // Get base model data I guess
                int mData = iSource.getAmount();

                // Apply operation
                mData = (int)Math.round(operation.apply((double)mData));
                if (result != null) { result.setValue((double)mData);}

                // Final AMount?
                if (mData <= 0) {

                    // Notify
                    Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified amount of \u00a7f" + sName);

                    // Return
                    return new ItemStack(Material.DEBUG_STICK);
                }

                // Insert into item
                iSource.setAmount(mData);

                // Finish
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified amount of \u00a7f" + sName);
                return iSource;


            // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Uuuuuh cant really edit the amount of air.");
                return null;
            }

        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Can't edit the amount of something that doesn't exist!");
            return null;

        }
    }

    /**
     * Performs an operation on the durability of an item. If it succeeded on setting the durability, it will return the modified ItemStack.
     * <p>Will target MMOItems durability if applicable.</p> Does not bypass Unbreakable attribute.
     * <p></p>
     * Reasons it could fail: <p>
     * + The item does not exist </p>
     * + The item cannot be damaged (say, a stone block) <p>
     * <p><p>
     * If it would break by receiving such damage, it is prevented from breaking, and this will not fail
     * and just return the item at 1 remaining durability.
     * @param iSource Item to modify damage of
     * @param operation Operation to apply, based on the current damage of the item
     * @param result Stores numerically the final damage the item has
     * @param logger Stores a string saying what went wrong, if something did.
     */
    @Nullable
    public static ItemStack SetDurability(@Nullable ItemStack iSource, @Nullable Player mmoitemsDurabilityComp, @NotNull PlusMinusPercent operation, @Nullable RefSimulator<Double> result, @Nullable RefSimulator<String> logger) {
        return SetDurability(iSource, mmoitemsDurabilityComp, operation, result, true, logger);
    }

    /**
     * Performs an operation on the durability of an item. If it succeeded on setting the durability, it will return the modified ItemStack.
     * <p>Will target MMOItems durability if applicable.</p> Does not bypass Unbreakable attribute.
     * <p></p>
     * Reasons it could fail: <p>
     * + The item does not exist </p>
     * + The item cannot be damaged (say, a stone block) <p>
     * <p><p>
     * If it would break by receiving such damage, but it is prevented from breaking, this will not fail
     * and just return the item at 1 remaining durability.
     * @param iSource Item to modify damage of
     * @param operation Operation to apply, based on the current damage of the item
     * @param result Stores numerically the final damage the item has
     * @param mmoitemsDurabilityComp player to say 'your MMOItem broke!' if it is a MMOItem
     * @param preventBreaking If the operation would break the item, it will set at 1 remaining durabilit left
     * @param logger Stores a string saying what went wrong, if something did.
     * @return DEBUG_STICK ItemStack if the item broke.
     */
    @Nullable
    public static ItemStack SetDurability(@Nullable ItemStack iSource, @Nullable Player mmoitemsDurabilityComp, @NotNull PlusMinusPercent operation, @Nullable RefSimulator<Double> result, boolean preventBreaking, @Nullable RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Internally through MMOItems
                if (Gunging_Ootilities_Plugin.foundMMOItems && mmoitemsDurabilityComp != null) {

                    // Check if MMOItem
                    if (GooPMMOItems.IsMMOItem(iSource)) {

                        // Cool MMOItem removing shit
                        return GooPMMOItems.MMOItemModifyDurability(iSource, mmoitemsDurabilityComp, operation, result, preventBreaking, logger);

                        // Just vanilla lol
                    } else {

                        // Do It Vanilla
                        return SetDurabilityVanilla(iSource, operation, result, preventBreaking, logger);
                    }

                    // Just vanilla lol
                } else {

                    // Do It Vanilla
                    return SetDurabilityVanilla(iSource, operation, result, preventBreaking, logger);
                }

                // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant repair air!");
                return null;
            }

        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant repair something that doesnt exist!");
            return null;

        }
    }

    /**
     * Performs an operation on the durability of an item. If it succeeded on setting the durability, it will return the modified ItemStack.
     * <p>Will target MMOItems durability if applicable.</p> Does not bypass Unbreakable attribute.
     * <p></p>
     * Reasons it could fail: <p>
     * + The item does not exist </p>
     * + The item cannot be damaged (say, a stone block) <p>
     * <p><p>
     * If it would break by receiving such damage, it is prevented from breaking, and this will not fail
     * and just return the item at 1 remaining durability.
     * @param iSource Item to modify damage of
     * @param operation Operation to apply, based on the current damage of the item
     * @param result Stores numerically the final damage the item has
     * @param logger Stores a string saying what went wrong, if something did.
     */
    @Nullable
    public static ItemStack SetDurabilityVanilla(@Nullable ItemStack iSource, @NotNull PlusMinusPercent operation, @Nullable RefSimulator<Double> result, @Nullable RefSimulator<String> logger) { return SetDurabilityVanilla(iSource, operation, result, true, logger); }

    /**
     * Performs an operation on the durability of an item. If it succeeded on setting the durability, it will return the modified ItemStack.
     * <p>Will target MMOItems durability if applicable.</p> Does not bypass Unbreakable attribute.
     * <p></p>
     * Reasons it could fail: <p>
     * + The item does not exist </p>
     * + The item cannot be damaged (say, a stone block) <p>
     * <p><p>
     * If it would break by receiving such damage, but it is prevented from breaking, this will not fail
     * and just return the item at 1 remaining durability.
     * @param iSource Item to modify damage of
     * @param operation Operation to apply, based on the current damage of the item
     * @param result Stores numerically the final damage the item has
     * @param preventBreaking If the operation would break the item, it will set at 1 remaining durabilit left
     * @param logger Stores a string saying what went wrong, if something did.
     * @return DEBUG_STICK ItemStack if the item broke.
     */
    @Nullable
    public static ItemStack SetDurabilityVanilla(@Nullable ItemStack iSource, @NotNull PlusMinusPercent operation, @Nullable RefSimulator<Double> result, boolean preventBreaking, @Nullable RefSimulator<String> logger) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta and attributes
                ItemMeta iMeta = iSource.getItemMeta();

                // Is it damageable?
                if (iMeta instanceof Damageable) {

                    // Get name
                    String iName = OotilityCeption.GetItemName(iSource);

                    // Apply operation and store
                    Double fValue = operation.apply(((Damageable) iMeta).getDamage() * 1.0D);

                    // Restrict to 1, if it is preventing from breaking
                    if ((fValue >= iSource.getType().getMaxDurability()) && preventBreaking) { fValue = (iSource.getType().getMaxDurability() - 1.0D); } else if (fValue > iSource.getType().getMaxDurability()) { fValue = iSource.getType().getMaxDurability() + 1.0D; }
                    if (fValue < 0) { fValue = 0.0D; }
                    if (result != null) { result.setValue(fValue);}

                    // Did it break?
                    if (fValue > iSource.getType().getMaxDurability()) {

                        Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified durability of \u00a7f" + iName + "\u00a77, it broke though");
                        return new ItemStack(Material.DEBUG_STICK);
                    }

                    // Repair Accordingly
                    ((Damageable) iMeta).setDamage((int) Math.round(fValue));

                    // Insert into item
                    iSource.setItemMeta(iMeta);

                    // Finish
                    Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified durability of \u00a7f" + iName);
                    return iSource;

                } else {

                    // Log and cancel
                    Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "That item does not support durability.");
                    return null;
                }

                // Well that is air
            } else {

                // Log and cancel
                Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant repair air!");
                return null;
            }

        } else {

            // Log and cancel
            Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant repair something that doesnt exist!");
            return null;

        }
    }

    /**
     * Returns the max durbility of thay item stack, if it exists and could have durability.
     * <p></p>
     * Will automatically retrieve the durability MMOItem stat if it is a MMOItem with that stat.
     * @return Null if <code>iSource</code> cant support durability, or its Max Durability
     */
    @Nullable
    public static Integer GetMaxDurability(@Nullable ItemStack iSource, Player mmoitemsDurabilityComp) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta
                ItemMeta iMeta = iSource.getItemMeta();

                // Internally through MMOItems
                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Check if MMOItem
                    if (GooPMMOItems.IsMMOItem(iSource)) {

                        // Cool MMOItem removing shit
                        return GooPMMOItems.GetMaxDurabilityOf(iSource);

                        // Just vanilla lol
                    } else {

                        // Do It Vanilla
                        return GetMaxDurabilityVanilla(iSource);
                    }

                    // Just vanilla lol
                } else {

                    // Do It Vanilla
                    return GetMaxDurabilityVanilla(iSource);
                }

                // Well that is air
            } else {

                // Log and cancel
                return null;
            }
        } else {

            // No Source
            return null;
        }
    }

    /**
     * Strictly gets the vanilla durability of thay item.
     * <p></p>
     * Null if whatever you pass as iSource does not support durability, or its max durability.
     */
    @Nullable
    public static Integer GetMaxDurabilityVanilla(@Nullable ItemStack iSource) {

        // Check that iSource exists
        if (iSource != null) {

            // Check that its not air
            if (!IsAir(iSource.getType())) {

                // Get item meta and attributes
                ItemMeta iMeta = iSource.getItemMeta();

                // Is it damageable?
                if (iMeta instanceof Damageable) {

                    // Return thay
                    return Integer.valueOf(iSource.getType().getMaxDurability());

                } else {

                    // Log and cancel
                    return null;
                }

                // Well that is air
            } else {

                // Log and cancel
                return null;
            }

        } else {

            // Log and cancel
            return null;

        }
    }

    /**
     * Will return a head with the given texture, or NULL if anything goes wrong.
     * Thanks to Homer04 from spigot.
     */
    public static ItemStack GetSkullFrom(String b64stringtexture) {

        // Game Profile Stuff
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();

        // Ew
        if (propertyMap == null) { return null; }

        // Get Texture I guess
        propertyMap.put("textures", new Property("textures", b64stringtexture));

        // Make Item Stack
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();

        // Attempt to get
        try { getField(headMetaClass, "profile", GameProfile.class, 0).set(headMeta, profile); } catch (IllegalArgumentException | IllegalAccessException e) { return null; }

        // Return completed
        head.setItemMeta(headMeta);
        return head;
    }
    private static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        // Search in parent classes
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }
    //endregion

    //region Math
    public static float MathClamp(float val, float min, float max) {
        float ret = val;

        if (val > max) { ret = max; }
        if (val < min) { ret = min; }

        return ret;
    }
    public static boolean IntTryParse(@Nullable String value) {
        if (value == null) { return false; }
        // Remove zeros

        try {

            // Yes
            ParseInt(RemoveDecimalZeros(value));
            return true;

        } catch (NumberFormatException e) {

            return false;
        }
    }

    /**
     * Parses an int from a string even if it contains a decimal point (But it must be followed only by zeros)
     * Will generate an error if the string is not in numeric format
     */
    public static int ParseInt(@NotNull String value) {
        return Integer.parseInt(RemoveDecimalZeros(value));
    }

    /**
     * Parses an long from a string even if it contains a decimal point (But it must be followed only by zeros)
     * Will generate an error if the string is not in numeric format
     */
    public static long ParseLong(@NotNull String value) { return Long.parseLong(RemoveDecimalZeros(value)); }

    /**
     * If given some value that ends in .000 (any number of zeros), it will remove the decimal point and the zeros.
     *
     * If it has any decimal number, it will remove all zeros after it,
     * <p>
     *     "1.0000" will retun "1"
     * </p>
     *     "1.000100" will return "1.0001"
     *     <p>
     *         Intended to be used to allow to parse '8.0' as an integer value "8"; BRUH
     *     </p>
     * If the value is not a number that ends in .00000000..., it will return it unchanged.
     */
    @Nullable
    public static String RemoveDecimalZeros(@Nullable String source) {
        if (source == null) { return null; }

        if (source.contains(".")) {

            // Get
            String decimals = source.substring(source.lastIndexOf("."));

            // Find last nonzero char
            int lC = -1;

            // Evaulate all zeroes
            for (int c = 1; c < decimals.length(); c++) {

                // Get Char
                char ch = decimals.charAt(c);

                // Is it not a zero
                if (ch != '0') {

                    // AH cancel
                    lC = c;
                }
            }

            // Return thay
            return source.substring(0, source.lastIndexOf(".") + lC + 1);
        }

        // AH cancel
        return source;
    }
    public static boolean DoubleTryParse(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean BoolTryParse(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean BoolParse(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns an UUID from thay string, or null if it is not in UUID format.
     */
    public static UUID UUIDFromString(String anything) {

        // Correct Format?
        if (anything.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {

            // Return thay
            return UUID.fromString(anything);
        }

        // No
        return null;
    }
    public static Integer Convert2Int(Boolean value) { if (If(value)) { return 1; } else { return  0; } }
    public static boolean Convert2Bool(Integer value) { if (value == 1) { return true; } else { return  false; } }

    /**
     * A modulus is defined when the modulus marker begins. Basically allows all freedom of color codes before a comma that you may not want in there if there is no other entry afterwards.
     * @param source For example: <code>Color &c#r[#&7, ]&a#g[#&7, ]&9#b[#]</code>
     * @param modulusMarker Lets say you wanted to fill the RED color in the example, you'd use #r to target <code>#r[#&7, ]</code>
     * @param replace Will replace the # within the modulus by this. For example FF
     * @param keep Actaully doing any replacing, instead of just removing the whole thing <code>#r[#&7, ]</code>
     * @return The parsed value, For example:  <code>Color: &cFF, &a#g[#&7, ]&9#b[#]</code>  OR, if !keep: <code>Color: &c&a#g[#&7, ]&9#b[#]</code>
     */
    public static String ModulusReplace(String source, String modulusMarker, String replace, boolean keep) {

        // Get the first instance of it
        int mmIdx = source.indexOf(modulusMarker);

        // Found? And reasonable length?
        if (mmIdx > 0 && source.length() > (mmIdx + modulusMarker.length() + 2)) {

            // Strip
            String modulusBegin = source.substring(mmIdx + modulusMarker.length());

            // Find closing
            int mmCIdx = modulusBegin.indexOf(']');

            // Found?
            if (mmCIdx > 0) {

                // Strip wholly
                String sourceB4 = source.substring(0, mmIdx);
                String sourceAft = source.substring(mmIdx + modulusMarker.length() + mmCIdx + 1);

                // If keep
                if (keep) {

                    // Evaluate
                    String modulusInsert = modulusBegin.substring(1, mmCIdx);

                    // Replace
                    modulusInsert = modulusInsert.replace("#", replace);

                    // Return parsed
                    return sourceB4 + modulusInsert + sourceAft;

                    // Otherwise just remove
                } else {

                    // Strip the modulus [ ]
                    return sourceB4 + sourceAft;
                }
            }
        }

        // I guess not found
        return source;
    }

    public static String NicestTimeValueFrom(Double seconds) {

        // If more than 1 minute
        if (seconds > 60) {

            // Is it greater than 1800?
            if (seconds > 1800) {

                // Dive by 1800
                double div1800 = seconds / 1800.0D;

                // Get the difference from an integer rounding [0-0.99]
                double differnce = Math.round(div1800) - div1800;

                // If it was nice (within 9 minutes of half an hour, continue as hours)
                if (differnce < 0.34) {

                    // Return as minutes alv
                    return ReadableRounding(OotilityCeption.RoundAtPlaces(seconds / 3600.0D, 1),1) + "h";
                }

                // Difference was kinda sensitive. Will evaluate as minutes I guess

                // BUT first, if it would hit 1000 mintues, force-convert to hours ~ with two decimal places :)
                if (seconds > 60000) {

                    // Return as minutes alv
                    return ReadableRounding(OotilityCeption.RoundAtPlaces(seconds / 3600.0D, 2), 1) + "h";
                }
            }

            // Dive by 30
            double div30 = seconds / 30.0D;

            // Get the difference from an integer rounding [0-0.99]
            double differnce = Math.round(div30) - div30;

            // If it was nice (within 9 seconds of half a minute, continue as minutes)
            if (differnce < 0.34) {

                // Return as minutes alv
                return ReadableRounding(OotilityCeption.RoundAtPlaces(seconds / 60.0D, 1), 1) + "m";
            }

            // Difference was kinda sensitive. Will use seconds

            // BUT first, if it would hit 1000 seconds, force-convert to seconds ~ with two decimal places :)
            if (seconds > 1000) {

                // Return as minutes alv
                return ReadableRounding(OotilityCeption.RoundAtPlaces(seconds / 60.0D, 2), 1) + "m";
            }
        }

        // Return as seconds alv
        return ReadableRounding(OotilityCeption.RoundAtPlaces(seconds, 1), 1) + "s";
    }

    /**
     * Straight up rounding something like 2.3, in String.valueOf, makes it a 2.0; That .0 being ugly af imo.
     *
     * This will remove such .0
     * <p>
     *     Also, if it was something like 2.03003, would it round to 2.0300, it will remove those pesky 00 and return 2.03
     * </p>
     */
    public static String ReadableRounding(Double something, Integer decimals) {

        // Round to decimals ig
        return RemoveDecimalZeros(String.valueOf(RoundAtPlaces(something, decimals)));
    }

    /**
     * Returns a random integer number
     * @param min Minimum value (Inclusive)
     * @param max Maximum value (Inclusive)
     */
    public static int GetRandomInt(int min, int max) {
        return (int) (Math.round((Math.random() * (max - min)) + min));
    }
    public static Entity getEntityByUniqueId(String uniqueId) {

        // Attempt to get UUID
        UUID suID = UUIDFromString(uniqueId);

        // Was it balid?
        if (suID != null){
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    for (Entity entity : chunk.getEntities()) {
                        if (entity.getUniqueId().equals(suID))
                            return entity;
                    }
                }
            }
        }

        // Failure - Return null
        return null;
    }
    public static ArrayList<String> toStringArray(ArrayList<UUID> obj) {
        // A Ret 2 Return
        ArrayList<String> ret = new ArrayList<>();

        // ToString()-ify every object
        for (UUID o : obj) { ret.add(o.toString()); }

        // Return
        return ret;
    }
    //endregion

    //region Misc
    /**
     * Valid Location, separated with commas instead of spaces
     * @param wXYZ In the format w,x,y,z   OR   x,y,z
     * @param logger Log Arguments
     * @return A location if exists, or NULL if anything doesnt make sense
     */
    public static Location ValidLocation(String wXYZ, RefSimulator<String> logger) {
        // Split
        String[] lSplit = wXYZ.split(",");

        // Correct number of args
        if (lSplit.length == 4) {
            return ValidLocation(lSplit[0], lSplit[1], lSplit[2], lSplit[3], logger);

        // Not exactly four HUH
        } else {

            Log4Success(logger, Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Error when parsing location string \u00a7e" + wXYZ + "\u00a77: \u00a7cNot in the expected format \u00a76World,X,Y,Z");
            return null;
        }
    }

    /**
     * Valid Location, from strings
     * @param worldName Name of the world
     * @param veX Position X
     * @param veY Position Y
     * @param veZ Position Z
     * @param logger Log Arguments
     * @return A location if exists, or NULL if anything doesnt make sense
     */
    public static Location ValidLocation(String worldName, String veX, String veY, String veZ, RefSimulator<String> logger) {
        return ValidLocation(null, null, worldName, veX, veY, veZ, logger);
    }

    /**
     * Valid Location, from strings
     * @param worldName Name of the world
     * @param veX Position X
     * @param veY Position Y
     * @param veZ Position Z
     * @param logger Log Arguments
     * @return A location if exists, or NULL if anything doesnt make sense
     */
    public static Location ValidLocation(Location relativity, String worldName, String veX, String veY, String veZ, RefSimulator<String> logger) {
        return ValidLocation(null, relativity, worldName, veX, veY, veZ, logger);
    }

    /**
     * Enhanced Valid Location, supporting relative co-ordinates of a player (~) and target locations (^)
     * @param relativity Player to parse ^ and ~s
     * @param worldName Name of the world
     * @param veX Position X
     * @param veY Position Y
     * @param veZ Position Z
     * @param logger Log Arguments
     * @return A location if exists, or NULL if anything doesnt make sense
     */
    public static Location ValidLocation(Player relativity, String worldName, String veX, String veY, String veZ, RefSimulator<String> logger) {
        return ValidLocation(relativity, null, worldName, veX, veY, veZ, logger);
    }

    /**
     * Enhanced Valid Location, supporting relative co-ordinates of a player (~) and target locations (^)
     * @param relativity Player to parse ^ relatives
     * @param actualRelativity Location to parse ~ relatives
     * @param worldName Name of the world
     * @param veX Position X
     * @param veY Position Y
     * @param veZ Position Z
     * @param logger Log Arguments
     * @return A location if exists, or NULL if anything doesnt make sense
     */
    public static Location ValidLocation(@Nullable Player relativity, @Nullable  Location actualRelativity, @NotNull String worldName, @NotNull String veX, @NotNull String veY, @NotNull String veZ, @Nullable RefSimulator<String> logger) {

        // Final variables
        World wrdl = null;
        Integer weX = null, weY = null, weZ = null;

        // Absolutist stuff
        boolean failure = false;
        String loggerRet = "";

        //region Relativist wave
        Integer sourceX = null, sourceY = null, sourceZ = null;
        double relX = 0.0, relY = 0.0, relZ = 0.0;
        if (actualRelativity == null && relativity != null) { actualRelativity = relativity.getLocation(); }
        if (actualRelativity != null) {

            // Should even find target location values?
            int minDst = 0, maxDist = 100;
            boolean teX = veX.startsWith("^"), teY = veY.startsWith("^"), teZ = veZ.startsWith("^");
            boolean reX = veX.startsWith("~"), reY = veY.startsWith("~"), reZ = veZ.startsWith("~");
            boolean getTarget = teX || teY || teZ;
            boolean worldT = worldName.contains("~"), worldC = worldName.contains("^");
            if (worldT || worldC) { wrdl = actualRelativity.getWorld(); }

            // Get Target Block
            Block targetBlock = null;
            if (getTarget && relativity != null) {

                // Aquire limiting for location
                if (worldName.length() > 1 && (worldT || worldC)) {

                    // Adapt world
                    wrdl = actualRelativity.getWorld();

                    // Cut strings after each
                    String minimumCut = "";
                    if (worldT) { minimumCut = worldName.substring(worldName.indexOf("~") + 1); }
                    String maximumCut = "";
                    if (worldC) { maximumCut = worldName.substring(worldName.indexOf("^") + 1); }

                    //// Assume they are now at the form ##$## (where # is a number, and $ is either ^ or ~ )

                    // Strip
                    if (minimumCut.contains("^")) { minimumCut = minimumCut.substring(0, minimumCut.indexOf("^")); }
                    if (maximumCut.contains("~")) { maximumCut = maximumCut.substring(0, maximumCut.indexOf("~")); }

                    //// Guaranteed to be a ##; Assumed to be positive

                    // If tilde (minimum)
                    if (worldT) {

                        // Attempt to parse
                        if (IntTryParse(minimumCut)) {

                            // Yes
                            minDst = Integer.parseInt(minimumCut);

                            // Failure
                        } else {

                            failure = true;

                            // Mention
                            loggerRet += "\u00a77Could not parse numeric value '\u00a7e" + minimumCut + "\u00a77' for minimum distance";
                        }
                    }

                    // If circunflex accent (maximum)
                    if (worldC) {

                        // Attempt to parse
                        if (IntTryParse(maximumCut)) {

                            // Yes
                            maxDist = Integer.parseInt(maximumCut);

                            // Failure
                        } else {

                            failure = true;

                            // Mention
                            loggerRet += "\u00a77Could not parse numeric value '\u00a7e" + maximumCut + "\u00a77' for maximum distance";
                        }
                    }
                }

                // Just target location I guess?
                targetBlock = ((Player)relativity).getTargetBlockExact(maxDist, FluidCollisionMode.NEVER);

                // Cancel if within min dist (and not null already lma0)
                if (targetBlock != null) {
                    Location tS = new Location(relativity.getWorld(), relativity.getLocation().getBlockX(), relativity.getLocation().getBlockY(), relativity.getLocation().getBlockZ()).subtract(targetBlock.getLocation());
                    if (Math.pow(Math.pow(tS.getBlockX(), 2) + Math.pow(tS.getBlockY(), 2) + Math.pow(tS.getBlockZ(), 2), 0.5) < minDst) { targetBlock = null; }
                }
            }

            // Get relativities
            if ((teX || reX) && veX.length() > 1) {

                // Crop
                String modX = veX.substring(1);

                // Store that minus
                int mod = 1;
                if (modX.startsWith("-") && modX.length() > 1) { mod = -1; modX = modX.substring(1); }

                // Attempt to parse
                if (DoubleTryParse(modX)) {

                    // Yes
                    relX = Double.parseDouble(modX) * mod;

                // Failure
                } else {

                    failure = true;

                    // Mention
                    loggerRet += "\u00a77Could not parse numeric value '\u00a7e" + modX + "\u00a77' for X co-ordinate shift";
                }
            }
            if ((teY || reY) && veY.length() > 1) {

                // Crop
                String modY = veY.substring(1);

                // Store that minus
                int mod = 1;
                if (modY.startsWith("-") && modY.length() > 1) { mod = -1; modY = modY.substring(1); }

                // Attempt to parse
                if (DoubleTryParse(modY)) {

                    // Yes
                    relY = Double.parseDouble(modY) * mod;

                    // Failure
                } else {

                    failure = true;

                    // Mention
                    loggerRet += "\u00a77Could not parse numeric value '\u00a7e" + modY + "\u00a77' for Y co-ordinate shift";
                }
            }
            if ((teZ || reZ) && veZ.length() > 1) {

                // Crop
                String modZ = veZ.substring(1);

                // Store that minus
                int mod = 1;
                if (modZ.startsWith("-") && modZ.length() > 1) { mod = -1; modZ = modZ.substring(1); }

                // Attempt to parse
                if (DoubleTryParse(modZ)) {

                    // Yes
                    relZ = Double.parseDouble(modZ) * mod;

                // Failure
                } else {

                    failure = true;

                    // Mention
                    loggerRet += "\u00a77Could not parse numeric value '\u00a7e" + modZ + "\u00a77' for Z co-ordinate shift";
                }
            }

            // Get source co-ordinates
            if (targetBlock != null) {

                // Cop those target onese
                if (teX) { sourceX = targetBlock.getX(); }
                if (teY) { sourceY = targetBlock.getY(); }
                if (teZ) { sourceZ = targetBlock.getZ(); }

            // If was supposed to tho
            } else if (getTarget) {

                // Could not find target block
                failure = true;

                // Mention
                loggerRet += "\u00a77Could not find target block (within the min and max constraints specified).";
            }

            if (reX) { sourceX = actualRelativity.getBlockX(); }
            if (reY) { sourceY = actualRelativity.getBlockY(); }
            if (reZ) { sourceZ = actualRelativity.getBlockZ(); }
        }
        //endregion

        // If world still null
        if (wrdl == null) {

            // Attempt to get by name
            wrdl = Bukkit.getWorld(worldName);

            if (wrdl == null) {
                // World failure
                failure = true;

                // Mention
                loggerRet += "\u00a77World \u00a73" + worldName + "\u00a77 doesnt exist! ";
            }
        }

        // IF the source is still null, parse normally
        if (sourceX != null) {

            // So there was a target/relative co-ord
            weX = RoundToInt(sourceX + relX);

        // Attempt to parse at all
        } else if (IntTryParse(veX)) { weX = ParseInt(veX); } else if (!failure) {

            // Parse7 failure
            failure = true;

            // Mention
            loggerRet += "\u00a77Expected integer number for X co-ordinate instead of \u00a73" + veX + "\u00a77. ";
        }

        // IF the source is still null, parse normally
        if (sourceY != null) {

            // So there was a target/relative co-ord
            weY = RoundToInt(sourceY + relY);

        // Attempt to parse at all
        } else if (IntTryParse(veY)) { weY = ParseInt(veY); } else if (!failure) {

            // Parse7 failure
            failure = true;

            // Mention
            loggerRet += "\u00a77Expected integer number for Y co-ordinate instead of \u00a73" + veY + "\u00a77. ";
        }

        // IF the source is still null, parse normally
        if (sourceZ != null) {

            // So there was a target/relative co-ord
            weZ = RoundToInt(sourceZ + relZ);

        // Attempt to parse at all
        } else if (IntTryParse(veZ)) { weZ = ParseInt(veZ); } else if (!failure) {

            // Parse7 failure
            failure = true;

            // Mention
            loggerRet += "\u00a77Expected integer number for Z co-ordinate instead of \u00a73" + veZ + "\u00a77. ";
        }

        // If there was something o log
        if (loggerRet.length() > 0) {

            // Notify
            Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, loggerRet);

        // Otherwise, if logger even exists
        } else if (logger != null) {

            // Clear
            logger.SetValue(null);
        }

        // Return location
        if (!failure) { return new Location(wrdl, weX, weY, weZ); } else { return null; }
    }

    public static void GatherDefaultVanillaAttributes(Material tType, RefSimulator<String> tName, RefSimulator<Double> vDamage, RefSimulator<Double> vSpeed, RefSimulator<Double> vArmor, RefSimulator<Double> vArmorT, RefSimulator<Double> mKRes) {

        //region Sword
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_SWORD)) {
            if (tName != null) { tName.SetValue("SWORD"); }
            if (vDamage != null) { vDamage.SetValue(7.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.6); }
        } else
        if (tType == Material.DIAMOND_SWORD) {
            if (tName != null) { tName.SetValue("SWORD"); }
            if (vDamage != null) { vDamage.SetValue(6.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.6); }
        } else
        if (tType == Material.IRON_SWORD) {
            if (tName != null) { tName.SetValue("SWORD"); }
            if (vDamage != null) { vDamage.SetValue(5.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.6); }
        } else
        if (tType == Material.GOLDEN_SWORD) {
            if (tName != null) { tName.SetValue("SWORD"); }
            if (vDamage != null) { vDamage.SetValue(3.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.6); }
        } else
        if (tType == Material.STONE_SWORD) {
            if (tName != null) { tName.SetValue("SWORD"); }
            if (vDamage != null) { vDamage.SetValue(4.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.6); }
        } else
        if (tType == Material.WOODEN_SWORD) {
            if (tName != null) { tName.SetValue("SWORD"); }
            if (vDamage != null) { vDamage.SetValue(3.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.6); }
        }
        //endregion

        //region Axe
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_AXE)) {
            if (tName != null) { tName.SetValue("AXE"); }
            if (vDamage != null) { vDamage.SetValue(9.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.DIAMOND_AXE) {
            if (tName != null) { tName.SetValue("AXE"); }
            if (vDamage != null) { vDamage.SetValue(8.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.IRON_AXE) {
            if (tName != null) { tName.SetValue("AXE"); }
            if (vDamage != null) { vDamage.SetValue(8.0); }
            if (vSpeed != null) { vSpeed.SetValue(0.9); }
        } else
        if (tType == Material.GOLDEN_AXE) {
            if (tName != null) { tName.SetValue("AXE"); }
            if (vDamage != null) { vDamage.SetValue(6.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.STONE_AXE) {
            if (tName != null) { tName.SetValue("AXE"); }
            if (vDamage != null) { vDamage.SetValue(8.0); }
            if (vSpeed != null) { vSpeed.SetValue(0.8); }
        } else
        if (tType == Material.WOODEN_AXE) {
            if (tName != null) { tName.SetValue("AXE"); }
            if (vDamage != null) { vDamage.SetValue(6.0); }
            if (vSpeed != null) { vSpeed.SetValue(0.8); }
        }
        //endregion

        //region Tool
        if (tType == Material.SHEARS) {
            if (tName != null) { tName.SetValue("TOOL"); }
        } else
        if (tType == Material.FLINT_AND_STEEL) {
            if (tName != null) { tName.SetValue("TOOL"); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_PICKAXE)) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(5.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.2); }
        } else
        if (tType == Material.DIAMOND_PICKAXE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(4.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.2); }
        } else
        if (tType == Material.IRON_PICKAXE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(3.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.2); }
        } else
        if (tType == Material.GOLDEN_PICKAXE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(1.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.2); }
        } else
        if (tType == Material.STONE_PICKAXE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(2.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.2); }
        } else
        if (tType == Material.WOODEN_PICKAXE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(1.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.2); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_SHOVEL)) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(5.5); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.DIAMOND_SHOVEL) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(4.5); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.IRON_SHOVEL) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(3.5); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.GOLDEN_SHOVEL) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(1.5); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.STONE_SHOVEL) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(2.5); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.WOODEN_SHOVEL) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(1.5); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_HOE)) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(0.01); }
            if (vSpeed != null) { vSpeed.SetValue(4.0); }
        } else
        if (tType == Material.DIAMOND_HOE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(0.01); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.IRON_HOE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(0.01); }
            if (vSpeed != null) { vSpeed.SetValue(3.0); }
        } else
        if (tType == Material.GOLDEN_HOE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(0.01); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.STONE_HOE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(0.01); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        } else
        if (tType == Material.WOODEN_HOE) {
            if (tName != null) { tName.SetValue("TOOL"); }
            if (vDamage != null) { vDamage.SetValue(0.01); }
            if (vSpeed != null) { vSpeed.SetValue(1.0); }
        }
        //endregion

        //region Armor
        if (tType == Material.TURTLE_HELMET) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(2.0); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_HELMET)) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(3.0); }
            if (vArmorT != null) { vArmorT.SetValue(3.0); }
            if (mKRes != null) { mKRes.SetValue(1.0); }
        } else
        if (tType == Material.DIAMOND_HELMET) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(3.0); }
            if (vArmorT != null) { vArmorT.SetValue(2.0); }
        } else
        if (tType == Material.IRON_HELMET) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(2.0); }
        } else
        if (tType == Material.GOLDEN_HELMET) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(2.0); }
        } else
        if (tType == Material.CHAINMAIL_HELMET) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(2.0); }
        } else
        if (tType == Material.LEATHER_HELMET) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(1.0); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_CHESTPLATE)) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(8.0); }
            if (vArmorT != null) { vArmorT.SetValue(3.0); }
            if (mKRes != null) { mKRes.SetValue(1.0); }
        } else
        if (tType == Material.DIAMOND_CHESTPLATE) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(8.0); }
            if (vArmorT != null) { vArmorT.SetValue(2.0); }
        } else
        if (tType == Material.IRON_CHESTPLATE) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(6.0); }
        } else
        if (tType == Material.GOLDEN_CHESTPLATE) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(5.0); }
        } else
        if (tType == Material.CHAINMAIL_CHESTPLATE) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(5.0); }
        } else
        if (tType == Material.LEATHER_CHESTPLATE) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(3.0); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_LEGGINGS)) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(6.0); }
            if (vArmorT != null) { vArmorT.SetValue(3.0); }
            if (mKRes != null) { mKRes.SetValue(1.0); }
        } else
        if (tType == Material.DIAMOND_LEGGINGS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(6.0); }
            if (vArmorT != null) { vArmorT.SetValue(2.0); }
        } else
        if (tType == Material.IRON_LEGGINGS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(5.0); }
        } else
        if (tType == Material.GOLDEN_LEGGINGS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(3.0); }
        } else
        if (tType == Material.CHAINMAIL_LEGGINGS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(4.0); }
        } else
        if (tType == Material.LEATHER_LEGGINGS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(2.0); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.NETHERITE_BOOTS)) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(3.0); }
            if (vArmorT != null) { vArmorT.SetValue(3.0); }
            if (mKRes != null) { mKRes.SetValue(1.0); }
        } else
        if (tType == Material.DIAMOND_BOOTS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(3.0); }
            if (vArmorT != null) { vArmorT.SetValue(2.0); }
        } else
        if (tType == Material.IRON_BOOTS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(2.0); }
        } else
        if (tType == Material.GOLDEN_BOOTS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(1.0); }
        } else
        if (tType == Material.CHAINMAIL_BOOTS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(1.0); }
        } else
        if (tType == Material.LEATHER_BOOTS) {
            if (tName != null) { tName.SetValue("ARMOR"); }
            if (vArmor != null) { vArmor.SetValue(1.0); }
        }
        //endregion

        //region Other
        if (tType == Material.TRIDENT) {
            if (tName != null) { tName.SetValue("SPEAR"); }
            if (vDamage != null) { vDamage.SetValue(8.0); }
            if (vSpeed != null) { vSpeed.SetValue(1.58); }
        } else
        if (tType == Material.LEAD) {
            if (tName != null) { tName.SetValue("WHIP"); }
            if (vDamage != null) { vDamage.SetValue(4.0); }
            if (vSpeed != null) { vSpeed.SetValue(2.8); }
        } else
        if (tType == Material.FISHING_ROD) {
            if (tName != null) { tName.SetValue("WHIP"); }
            if (vDamage != null) { vDamage.SetValue(3.0); }
            if (vSpeed != null) { vSpeed.SetValue(3.4); }
        }
        if (tType == Material.CARROT_ON_A_STICK) {
            if (tName != null) { tName.SetValue("WHIP"); }
            if (vDamage != null) { vDamage.SetValue(4.0); }
            if (vSpeed != null) { vSpeed.SetValue(3.2); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.WARPED_FUNGUS_ON_A_STICK)) {
            if (tName != null) { tName.SetValue("WHIP"); }
            if (vDamage != null) { vDamage.SetValue(6.0); }
            if (vSpeed != null) { vSpeed.SetValue(3.0); }
        } else
        if (tType == Material.BOW) {
            if (tName != null) { tName.SetValue("BOW"); }
            if (vDamage != null) { vDamage.SetValue(6.0); }
        } else
        if (tType == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CROSSBOW)) {
            if (tName != null) { tName.SetValue("CROSSBOW"); }
            if (vDamage != null) { vDamage.SetValue(8.0); }
        } else
        if (tType == Material.SHIELD) {
            if (tName != null) { tName.SetValue("SHIELD"); }
        }
        //endregion
    }

    /**
     * Runs an event and returns if it was cancelled.
     */
    public static boolean CallCancellableEvent(@NotNull Cancellable event) {
        // Call Event
        Bukkit.getPluginManager().callEvent((Event) event);

        return event.isCancelled();
    }

    public static ArrayList<Entity> EntitiesNearLocation(Location iBlock, double tolerance) {

        // A list 2 return
        ArrayList<Entity> cEntities = new ArrayList<>();

        // Include if armor stand with tag
        for (Entity ent : iBlock.getChunk().getEntities()) {

            // Approximate Location?
            if (OotilityCeption.LocationTolerance(iBlock, ent.getLocation(), tolerance)) {

                // Add to list
                cEntities.add(ent);
            }
        }

        // Ret
        return cEntities;
    }
    public static Location New(Location toCopy) { return new Location(toCopy.getWorld(), toCopy.getX(), toCopy.getY(), toCopy.getZ()); }
    //endregion

    /**
     * Will add the separator and the append to the end of this string builder.
     *
     * However, will omit he separator if this is the very first entry.
     *
     * <p></p>
     * <code>potato</code>
     * <p>vs</p>
     * <code>apple,potato</code>
     *
     * @param builder A string builder
     * @param append Suppose a next entry, <code>potato</code>
     * @param separator Suppose the separator, <code>,</code> (comma)
     */
    public static void ListIntoBuilder(@NotNull StringBuilder builder, @NotNull String append, @NotNull String separator) {

        if (builder.length() == 0) {

            builder.append(append);
        } else {

            builder.append(separator).append(append);
        }
    }

    /**
     * Include as a list of slots
     */
    public static void Slot4Success(@NotNull StringBuilder builder, @NotNull ItemStackSlot append, @NotNull String separator) {

        // Include for subsequent entries
        if (!(builder.length() == 0)) { builder.append(separator); }

        // Include shlot
        builder.append(append);
    }

    public static final String comma = ",";

    //region Command Sending
    public static void SendUnparsedConsoleCommand(String command) {
        // Create a command sender
        ConsoleCommandSender konsole = Bukkit.getServer().getConsoleSender();

        SendConsoleCommand(command, konsole);
    }
    public static void SendConsoleCommand(String command, Entity asEntity) {
        SendConsoleCommand(command, asEntity, null, null);
    }
    public static void SendConsoleCommand(String command, Player asPlayer) {
        if(asPlayer != null) { command = ProcessGooPRelativityOfCommand(command, asPlayer.getLocation()); }
        SendConsoleCommand(command, asPlayer, asPlayer, null);
    }
    public static void SendConsoleCommand(String command, Block asBlock) {
        SendConsoleCommand(command, null, null, asBlock);
    }
    public static void SendConsoleCommand(String command, Entity asEntity, Block withBlock) {
        SendConsoleCommand(command, asEntity, null, withBlock);
    }
    public static void SendConsoleCommand(String command, @Nullable Player asPlayer, Block withBlock) {
        SendConsoleCommand(command, asPlayer, asPlayer, withBlock);
    }
    public static void SendConsoleCommand(String command, Entity asEntity, @Nullable Player asPlayer, Block asBlock) {
        SendConsoleCommand(command, asEntity, asPlayer, asBlock, null);
    }
    public static void SendConsoleCommand(String command, Entity asEntity,  @Nullable Player asPlayer, Block asBlock, ItemStack asItem) {
        // Create a command sender
        ConsoleCommandSender konsole = Bukkit.getServer().getConsoleSender();

        SendAndParseConsoleCommand(asPlayer, command, konsole, asEntity, asPlayer, asBlock, asItem);
    }
    public static void SendConsoleCommand(String command, ConsoleCommandSender konsole) {
        SendAndParseConsoleCommand(command, konsole, null, null, null);
    }

    /**
     * Will go to {@link Gunging_Ootilities_Plugin#commandFailDisclosures} and
     * fetch the correct value encrypted in this subcommand
     *
     * @param subcommand Subcommand in the form <b><code>[MESSAGE]subcommand</code></b>
     *                   <br>
     *                   Where the subcommand is
     *                   <code>/goop [SUBCOMMAND] ...</code> <br> <br>
     *
     * @param failMessage Container to store the resultant fail message
     *
     * @return The subcommand, stripped of any Fail Message Codification
     */
    @NotNull public static String ProcessFailMessageOfCommand(@NotNull String subcommand, @NotNull RefSimulator<String> failMessage) {

        // What name
        String messageName = null;

        // Save location elsewhere
        String locationParams = "";
        int locOpen = subcommand.indexOf("<");
        int locClose = subcommand.indexOf(">");
        if (locOpen >= 0 && locClose > 0 && locClose > (locOpen + 1)) {
            locationParams = subcommand.substring(locOpen, locClose);
            subcommand = subcommand.replace(locationParams, ""); }

        // Does it have it?
        int firstOpen = subcommand.indexOf("[");
        int firstClose = subcommand.indexOf("]");
        if (firstOpen >= 0 && firstClose > 0 && firstClose > (firstOpen + 1)) {

            // All right, strip
            messageName = subcommand.substring(firstOpen + 1, firstClose);
        }

        // Remove Brackets
        if (firstClose > 0) { subcommand = subcommand.substring(firstClose + 1); }

        // Valid subcommand tho?
        try {
            GooP_Commands gcmd = GooP_Commands.valueOf(subcommand.toLowerCase());

            // Special case
            if (gcmd == GooP_Commands.vault) {

                // Vault legacy registered?
                String vaultLegacy = Gunging_Ootilities_Plugin.commandFailDisclosures.get("VaultLowBalance");

                // Registered?
                if (vaultLegacy != null) {

                    // Put
                    failMessage.setValue(vaultLegacy);

                    // Done
                    return locationParams + subcommand;
                }
            }

        } catch (IllegalArgumentException ignored) { return locationParams + subcommand; }

        // Well
        if (messageName == null) { messageName = subcommand.toLowerCase(); }

        // Registered?
        failMessage.setValue(Gunging_Ootilities_Plugin.commandFailDisclosures.get(messageName));

        // Nothing more
        return locationParams + subcommand;
    }
    @NotNull public static String ProcessGooPRelativityOfCommand(@NotNull String cmd, @Nullable Location relativity) {

        // Valid location?
        if (relativity == null) { return cmd; }

        // Not Goop?
        if (!cmd.startsWith("goop ") && ! cmd.startsWith("ootilitiception ")) { return cmd; }

        // World with name? ew
        if (relativity.getWorld().getName().contains(" ")) { return cmd; }

        // Append Location lma0
        String[] args = cmd.split(" ");
        if (args.length >= 2) {

            // Get subcommand
            String subcommand = args[1];

            // Get Location baked
            String loc = "<" + relativity.getWorld().getName() + "," + relativity.getX() + "," + relativity.getY() + "," + relativity.getZ() + ">";

            // Modify
            args[1] = loc + subcommand;

            // Rebuild
            StringBuilder cmdBuilder = new StringBuilder(args[0]);
            for (int s = 1; s < args.length; s++) { cmdBuilder.append(" ").append(args[s]); }
            cmd = cmdBuilder.toString();
        }

        // Return
        return cmd;
    }

    /**
     * Will actually only do the parsing, as if you were sending a command to the console, but not send it.
     * Perses GooP and PAPI placeholders.
     */
    public static String ParseConsoleCommand(String cmd, Entity asEntity, Player asPlayer, Block asBlock) {
        return ParseConsoleCommand(cmd, asEntity, asPlayer, asBlock, null);
    }
    public static String ParseConsoleCommand(String cmd, Entity asEntity, Player asPlayer, Block asBlock, ItemStack asItem) {

        // Parse as PAPI
        if (asPlayer != null) {

            // Parse as GOOP
            cmd = OotilityCeption.ParseAsGoop(asPlayer, cmd);

            if (Gunging_Ootilities_Plugin.foundPlaceholderAPI) { cmd = GooPPlaceholderAPI.Parse(asPlayer, cmd); }
        }

        if (asBlock != null) {

            // Parse as GOOP
            cmd = OotilityCeption.ParseAsGoop(asBlock, cmd);
        }

        if (asItem != null) {

            // Parse as GOOP
            cmd = OotilityCeption.ParseAsGoop(asItem, cmd);
        }

        if (asEntity != null) {

            // Parse as GOOP
            cmd = OotilityCeption.ParseAsEntity(asEntity, cmd);
        }

        return cmd;
    }
    public static void SendAndParseConsoleCommand(String cmd, CommandSender konsole, Entity asEntity, Player asPlayer, Block asBlock) {
        SendAndParseConsoleCommand(cmd, konsole, asEntity, asPlayer, asBlock, null);
    }
    public static void SendAndParseConsoleCommand(String cmd, CommandSender konsole, Entity asEntity, Player asPlayer, Block asBlock, ItemStack asItem) {
        SendAndParseConsoleCommand((Player) null, cmd, konsole, asEntity, asPlayer, asBlock);
    }
    public static void SendAndParseConsoleCommand(UUID asChainResult, String cmd, CommandSender konsole, Entity asEntity, Player asPlayer, Block asBlock) {
        SendAndParseConsoleCommand(asChainResult, cmd, konsole, asEntity, asPlayer, asBlock, null);
    }
    public static void SendAndParseConsoleCommand(UUID asChainResult, String cmd, CommandSender konsole, Entity asEntity, Player asPlayer, Block asBlock, ItemStack asItem) {
        // Parse
        cmd = ParseConsoleCommand(cmd, asEntity, asPlayer, asBlock, asItem);

        // First index of @s
        if (asChainResult != null) { cmd = ReplaceFirst(cmd, "@s", asChainResult.toString()); }

        // Parsed and Imbound
        Bukkit.dispatchCommand(konsole, cmd.replace("<$pc>", "%").replace("<$nd>", "&"));

    }
    public static void SendAndParseConsoleCommand(Player asChainResult, String cmd, CommandSender konsole, Entity asEntity, Player asPlayer, Block asBlock) {
        SendAndParseConsoleCommand(asChainResult, cmd, konsole, asEntity, asPlayer, asBlock, null);
    }
    public static void SendAndParseConsoleCommand(Player asChainResult, String cmd, CommandSender konsole, Entity asEntity, Player asPlayer, Block asBlock, ItemStack asItem) {
        // Parse
        cmd = ParseConsoleCommand(cmd, asEntity, asPlayer, asBlock, asItem);

        // First index of @s
        if (asChainResult != null) { cmd = ReplaceFirst(cmd, "@s", asChainResult.getName()); }

        // Parsed and Imbound
        Bukkit.dispatchCommand(konsole, cmd.replace("<$pc>", "%").replace("<$nd>", "&"));
    }


    @NotNull public static String ReplaceFirst(@NotNull String source, @NotNull String placeholder, @NotNull String replace) {

        // First index of @s
        int cRindex = source.indexOf(placeholder);
        if (cRindex > 0) {

            // B4 and Aft
            String cB4 = source.substring(0, cRindex);
            String Aft = source.substring(cRindex + placeholder.length());

            // Inklude name
            source = cB4 + replace + Aft;
        }

        return source;
    }

    public static void SendConsoleCommands(ArrayList<String> command) {

        // Create a command sender
        ConsoleCommandSender konsole = Bukkit.getServer().getConsoleSender();

        // Send commands
        for (String str : command) { SendConsoleCommand(str, konsole); }
    }
    //endregion

    //region Entity Targetting
    /**
     * Returns the item stack of that dropped item if such item stack exists, otherwise null.
     */
    @Nullable
    public static ItemStack FromDroppedItem(@Nullable Entity itm) {

        // No null
        if (!(itm instanceof Item)) { return null; }

        // Get Item?
        return ((Item) itm).getItemStack();
    }

    /**
     * If that entity is a dropped item, it will set it to the provided item stack.
     * <p></p>
     * If the item stack is null or air or has 0 amount, the dropped item will be destroyed.
     */
    public static void SetDroppedItemItemStack(@Nullable Entity droppedItem, @Nullable ItemStack iSource) {

        // Ignore if null
        if (!(droppedItem instanceof Item)) { return; }

        // Destroy if air
        if (IsAirNullAllowed(iSource)) { droppedItem.remove(); return; }

        // Amount?
        if (iSource.getAmount() < 1) { droppedItem.remove(); return; }

        // Well Set IG
        ((Item) droppedItem).setItemStack(iSource);
    }

    /**
     * Gets the player, offline or not, based on such name.
     * <p></p>
     * Supports a few vanilla targetters like @a, @p, and @r.
     * <p></p>
     * Also [distanceMIN..MAX] and [tag=] and [gamemode=] and [limit=] and [scores={[objective]=[scoreMIN..scoreMAX]}]
     * <p></p>
     * @param name 'name of the player'
     * <p></p>
     * @param relativity If not null, allows to parse @p and [distance=..] because they are relative to a position.
     * <p></p>
     * @param includeOffline If set to false, it will not attempt to find <code>OfflinePlayer</code>s (Returning only <code>Player</code>s).
     *                       Vanilla selectors will NEVER attempt to get offline players.
     * <p></p>
     * @return An array with the players found. If the <code>OfflinePlayer</code> has never played in this server, they wont be included.
     */
    @NotNull
    public static ArrayList<OfflinePlayer> GetPlayers(Location relativity, String name, boolean includeOffline, RefSimulator<String> logReturn) {

        // A list to return
        ArrayList<OfflinePlayer> ret = new ArrayList<>();

        // Split by commas
        ArrayList<String> names = new ArrayList<>();
        if (name.contains(";")) {

            // For each different
            for (String str : name.split(";")) {

                // Add to name
                names.add(str);
            }

        // Otherwise just include the whole thing
        } else {

            // Inklude
            names.add(name);
        }

        // For each parsable name
        for (String nme : names) {

            // There is no player with @ as their first name char ffs
            if (nme.startsWith("@") && nme.length() >= 2) {

                // Get Prime Key
                String primeChar = nme.substring(1, 2);

                // Get matching players
                ArrayList<Player> blayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                //region Proceess tags: distance, gamemode, tag, scores

                // Process tags
                QuickNumberRange attemptedDistance = RangeFromBracketsTab(nme, "distance");
                String gmString = ValueFromBracketsTab(nme, "gamemode");
                GameMode actualGameMode = null;
                if (gmString != null) { try { actualGameMode = GameMode.valueOf(gmString); } catch (IllegalArgumentException ignored) {} }
                ArrayList<String> tagChecks = new ArrayList<>();
                String tgVal = ValueFromBracketsTab(nme, "tag");
                if (tgVal != null) { tagChecks.addAll(Arrays.asList((UnwrapFromCurlyBrackets(tgVal).split(",")))); }
                ArrayList<ScoreRequirements> scoreChecks = ScoreRequirements.FromCompactString(ValueFromBracketsTab(nme, "scores"));

                // Well check all of them i guess
                ArrayList<Player> approvedPlayers = new ArrayList<>();
                for (Player p : blayers) {
                    //DBG//Log("Checking player \u00a73" + p.getName());

                    // Failure
                    boolean failure = false;

                    // Start with distance o/
                    if (attemptedDistance != null) {
                        //DBG//Log("\u00a72Attempting Distance");

                        // Check distance
                        if (relativity == null) {
                            //DBG//Log("\u00a7cNo Relativity");

                            // Complain
                            Log4Success(logReturn, Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Could not parse \u00a7edistance\u00a77 selector argument because no relative location was provided.");
                            break;

                        // Good, there is a check
                        } else {
                            //DBG//Log("\u00a72Found Relativity");

                            // In world?
                            if (p.getWorld().equals(relativity.getWorld())) {
                                //DBG//Log("\u00a72Player in World");

                                // Get distance
                                double dist = p.getLocation().distance(relativity);

                                // Does it check? Fails if not in range
                                failure = !attemptedDistance.InRange(dist);
                                //DBG//Log("\u00a7aChecked Distance: \u00a7b" + dist + "\u00a77 resulted in \u00a7e" + !failure + "\u00a77 of being within range");

                            // NOT IN WORLD REE
                            } else {
                                //DBG//Log("\u00a7cPlayer Not in World");

                                // No hell no
                                failure = true;
                            }
                        }
                    } else {
                        //DBG//Log("\u00a76No Distance");
                    }

                    // Hasnt failed huh
                    if (!failure) {

                        // That gamemode tho
                        if (actualGameMode != null) {

                            // What is they on?
                            if (p.getGameMode() != actualGameMode) {
                                // Not the correct one oi
                                failure = true;
                            }
                        }

                        // Still going on?
                        if (!failure) {

                            // Every tag check
                            if (tagChecks.size() > 0) {

                                // Get those tags
                                ArrayList<String> pTags = new ArrayList<>(p.getScoreboardTags());

                                // Compare each
                                for (String tg : tagChecks) {

                                    // Is it not contained?
                                    if (!pTags.contains(tg)) {
                                        // Failureee
                                        failure = true;
                                        break;
                                    }
                                }
                            }

                            // Finally
                            if (!failure) {

                                // Any score reqs?
                                if (scoreChecks.size() > 0) {

                                    // Alr evaluate each
                                    for (ScoreRequirements sR : scoreChecks) {

                                        // Was it a valid objective?
                                        if (sR.validObjective()) {

                                            // Well get player's score
                                            int pScore = GetPlayerScore(sR.getObjective(), p);

                                            // Compare it
                                            failure = !sR.InRange(pScore + 0.0D) || failure;
                                        }
                                    }
                                }

                                // Did you qualify?
                                if (!failure) {
                                    //DBG//Log("\u00a7aAdded");
                                    approvedPlayers.add(p);  }
                            }
                        }
                    }
                }

                // Whoever made it made it
                blayers = new ArrayList<>(approvedPlayers);
                //endregion

                // Does it have a limit?
                int trueLimit = -1;
                Integer attemptedLimit = IntegerFromBracketsTab(nme, "limit");
                boolean flipClosest = false;
                if (attemptedLimit != null) {
                    trueLimit = attemptedLimit; //LMT//Log("Limit found! \u00a7e" + trueLimit);

                    // Is it negative-yo?
                    if (attemptedLimit  < 0) {

                        // Positivize
                        flipClosest = true;
                        trueLimit *= -1;
                    }
                }

                // Get applicable players
                switch (primeChar) {
                    case "p": // Closest player
                        // Invalid if relativity is null
                        if (relativity == null) {

                            // Log and Ignore
                            Log4Success(logReturn, Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Could not parse \u00a7e@p\u00a77 selector because no relative location was provided.");
                            blayers.clear();

                        } else {

                            // Default limit 1
                            if (trueLimit < 0) { trueLimit = 1; }

                            // Quien da mas?
                            ArrayList<OrderableListEntry<Player>> worldPlayers = new ArrayList<>();

                            // Well check all of them i guess
                            for (Player p : blayers) {

                                // Same world?
                                if (p.getWorld().equals(relativity.getWorld())) {

                                    // Flip order
                                    if (flipClosest) {

                                        // Add as ordeal
                                        worldPlayers.add(new OrderableListEntry<>(p, p.getLocation().distance(relativity)));
                                        //LMT//Log("Added \u00a73" + p.getName() + "\u00a77 at distance \u00a7a" + p.getLocation().distance(relativity));

                                    } else {

                                        // Add as ordeal
                                        worldPlayers.add(new OrderableListEntry<>(p, -p.getLocation().distance(relativity)));
                                        //LMT//Log("Added \u00a73" + p.getName() + "\u00a77 at distance \u00a7a" + -p.getLocation().distance(relativity));
                                    }
                                }
                            }

                            // Sort the hell of them
                            Collections.sort(worldPlayers);

                            // New reference
                            blayers = new ArrayList<>();

                            // Choose the closest ones first
                            for (int i = 0; (i < worldPlayers.size()) && (i < trueLimit); i++) {

                                // Inklude
                                blayers.add(worldPlayers.get(i).getStoredValue());
                                //LMT//Log("\u00a7aKept \u00a73" + worldPlayers.get(i).getStoredValue().getName());
                            }
                        }
                        break;
                    case "a": // All players is ALL PLAYERS
                        break;
                    case "r": // Random players

                        // Default limit 1
                        if (trueLimit < 0) { trueLimit = 1; }

                        // Get Initial
                        ArrayList<Player> initial = new ArrayList<>(blayers);

                        // New Reference
                        blayers = new ArrayList<>();

                        // Chosen
                        int chosen = 0;

                        // Choose the closest ones first
                        for (int i = GetRandomInt(0, initial.size() - 1); chosen < trueLimit; i = GetRandomInt(0, initial.size() - 1)) {
                            //LMT//Log("Between \u00a7b0 \u00a77and \u00a7b" + (initial.size()-1) + "\u00a77, chose \u00a7a" + i);

                            // Everyone has been chosen
                            if (initial.size() == 0) {
                                //LMT//Log("\u00a7cNo players. Break");
                                // STAHP
                                break;

                            } else {

                                // CHOOSE!!!
                                Player chosenOne = initial.get(i);

                                // Add
                                blayers.add(chosenOne);
                                //LMT//Log("Found \u00a73" + chosenOne.getName() + "\u00a77. \u00a7aAdded");

                                // Deleet
                                initial.remove(i);

                                // Inkrease
                                chosen++;

                                // Break
                                if (initial.size() == 0) {
                                    //LMT//Log("\u00a79Chose All. Break");
                                    break; }
                            }
                        }
                        break;
                    default:

                        // Complain
                        Log4Success(logReturn, Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Could not parse selector \u00a7e@" + primeChar);
                        blayers.clear();
                    break;
                }

                // So thats the result. Include in ret
                ret.addAll(blayers);
                return ret;

            // Its not a command selector
            } else {

                // First attempt as get player exact o/
                OfflinePlayer py = GetPlayer(nme, includeOffline);

                // Was it null?
                if (py != null) {

                    // Add them
                    ret.add(py);
                }
            }
        }

        return ret;
    }

    /**
     * Gets the online player of such name.
     * <p></p>
     * Supports a few vanilla targetters like @a, @p, and @r.
     * <p></p>
     * Also [distanceMIN..MAX] and [tag=] and [gamemode=] and [limit=] and [scores={[objective]=[scoreMIN..scoreMAX]}]
     * <p></p>
     * @param name 'name of the player'
     * <p></p>
     * @param relativity If not null, allows to parse @p and [distance=..] because they are relative to a position.
     * <p></p>
     * @return An array with the players found.
     */
    @NotNull
    public static ArrayList<Player> GetPlayers(Location relativity, String name, RefSimulator<String> logReturn) {

        // Array of Players
        ArrayList<Player> ret = new ArrayList<>();

        // Lmao gets players
        ArrayList<OfflinePlayer> playrs = GetPlayers(relativity, name, false, logReturn);

        // Eaxmine and include
        for (OfflinePlayer py : playrs) {

            // Guaranteed to be online ffs.
            ret.add((Player) py);
        }

        // Return results
        return ret;
    }

    /**
     * Will return a player. Supports getting offline players thoi, but will only return them if they have played before.
     * @param name Name of the player
     * @param allowOffline Wether to actually go for offline players
     * @return NULL if the player has never played before, or if they are not online and you are not allowing offlines.
     */
    public static OfflinePlayer GetPlayer(String name, boolean allowOffline) {

        // Retruning value
        Player py = null;

        // Can you get a UUID from it?
        UUID possibleUUID = UUIDFromString(name);
        if (possibleUUID != null) { py = Bukkit.getPlayer(possibleUUID); }

        // Found it? thats done
        if (py != null) { return py; }

        // First attempt as get player exact o/
        py = Bukkit.getPlayerExact(name);

        // Was it null?
        if (py == null) {

            // Will it attempt to get offlines?
            if (allowOffline) {

                // Attempt to get offline
                OfflinePlayer player = Bukkit.getOfflinePlayer(name);

                // Has played b4?
                if (player.hasPlayedBefore()) {

                    // I guess add
                    return player;
                }
            }

        // There is a player with that name. Highest priority.
        } else {

            // Add them
            return py;
        }

        return null;
    }

    /**
     * Need not necessarily within {}s, will crop them out if they are there.
     */
    public static String UnwrapFromCurlyBrackets(String source) {
        if (source == null) { return null; }

        // Unrwarp if Existing
        if (source.endsWith("}")) { source = source.substring(0, source.length() -1); }
        if (source.startsWith("{")) { source = source.substring(1); }

        return source;
    }

    /**
     * <p>
     * A brackets tab is the name I came up with at 3am for a list between brakcets.
     *
     * Example (vanilla selectors): <code>@a[distance=..10,tag=Whatevr,limit=4]</code>
     * </p>
     *
     * @param source The whole thing; Ex <code>@a[distance=..10,tag=Whatevr,limit=4]</code>
     *
     * @param tag The tag you're interested in; Ex <code>limit</code>
     *
     * @return if there was no tag included, or incorrect format, null. Otherwise, the parsed value; Ex <code>4</code>
     */
    public static Integer IntegerFromBracketsTab(String source, String tag) {

        // Get supposed numeric value
        String limitString = ValueFromBracketsTab(source, tag);
        if (limitString != null) {

            // Parse I guess
            if (IntTryParse(limitString)) {

                // Parses
                return ParseInt(limitString);
            }
        }

        return null;
    }
    /**
     * <p>
     * A brackets tab is the name I came up with at 3am for a list between brakcets.
     *
     * Example (vanilla selectors): <code>@a[distance=..10,tag=Whatevr]</code>
     * </p>
     *
     * @param source The whole thing; Ex <code>@a[distance=..10,tag=Whatevr]</code>
     *
     * @param tag The tag you're interested in; Ex <code>distance</code>
     *
     * @return if there was no tag included, or incorrect format, null. Otherwise, a QuickNumberRange class parsing the value; Ex <code>..10</code>
     */
    public static QuickNumberRange RangeFromBracketsTab(String source, String tag) {

        // Get
        String limitString = ValueFromBracketsTab(source, tag);
        if (limitString != null) {

            // Parse
            return QuickNumberRange.FromString(limitString);
        }

        return null;
    }
    /**
     * <p>
     * A brackets tab is the name I came up with at 3am for a list between brakcets.
     *
     * Example (vanilla selectors): <code>@a[distance=..10,tag=Whatevr]</code>
     * </p>
     *
     * @param source The whole thing; Ex <code>@a[distance=..10,tag=Whatevr]</code>
     *
     * @param tag The tag you're interested in; Ex <code>distance</code>
     *
     * @return if there was no tag included, or incorrect format, null. Otherwise, the string value; Ex <code>..10</code>
     */
    public static String ValueFromBracketsTab(String source, String tag) {

        //PRSE//Log("Searching for \u00a7e" + tag + "= \u00a77 within \u00a79" + source);
        int limitSt = source.indexOf(tag + "=");
        int trueLimit = -1;
        if (limitSt > 0) {

            // Crop the hell of it
            String limitCropB4 = source.substring(limitSt + tag.length() + 1);
            //PRSE//Log("Cropped B4 As \u00a73" + limitCropB4);

            // Find the end, may it be a , or a ]; Whichever comes first
            int limitCropEnd = -1;

            // Find closing curly bracket I guess; Must begin with a curly bracket for that.
            int curlyIndex = -1;
            if (limitCropB4.startsWith("{")) { curlyIndex = limitCropB4.indexOf("}");}
            if (curlyIndex == -1) { curlyIndex = 0; }

            // Get the index of a comma, starting after the closing bracket.
            int limitCropComma = limitCropB4.indexOf(",", curlyIndex);

            int limitCropClose = limitCropB4.indexOf("]");
            if (limitCropComma > 0) { limitCropEnd = limitCropComma; }
            if (limitCropClose > 0) { if (limitCropEnd > 0) { if (limitCropClose < limitCropEnd) { limitCropEnd = limitCropClose; } } else { limitCropEnd = limitCropClose; } }
            //PRSE//OotilityCeption. Log("Found end index at \u00a7b" + limitCropEnd);

            // Found an end?
            if (limitCropEnd > 0) {
                //PRSE//OotilityCeption. Log("Cropping Aft As \u00a7c" + limitCropB4.substring(limitCropEnd));
                //PRSE//OotilityCeption. Log("Returning Value \u00a7e" + limitCropB4.substring(0, limitCropEnd));

                // Parse I guess
                return limitCropB4.substring(0, limitCropEnd);
            }
        }

        return null;
    }
    //endregion

    //public static void MbN9T() {
        //OfflinePlayer m8 = Bukkit.getOfflinePlayer(UUID.fromString("df930b7b-a84d-4f76-90ac-33be6a5b6c88"));
        //if (m8.isBanned()) { Gunging_Ootilities_Plugin.daybroken = true; } }
    //region Item/Entity NBT Parsers
    public static String[] itemNBTcharKeys = new String[] {"m", "e", "v"};
    public static boolean IsInvalidItemNBTtestString(String compoundFilter, RefSimulator<String> logger) {

        // Split
        String[] mmSplit = compoundFilter.split(" ");

        // Exactly three
        if (mmSplit.length == 3 || mmSplit.length == 4) {
            String dataAmount = null;
            if (mmSplit.length == 4) { dataAmount = mmSplit[3]; }

            // Pass on
            return IsInvalidItemNBTtestString(mmSplit[0], mmSplit[1], mmSplit[2], dataAmount, logger);
        }

        // INVALDI!
        return true;
    }
    public static boolean IsInvalidItemNBTtestString(String itemNBTcharKey, String dataPrime, String dataDime, RefSimulator<String> logger) {

        // Just pass it on with null amount
        return IsInvalidItemNBTtestString(itemNBTcharKey, dataPrime, dataDime, null, logger);
    }
    public static boolean IsInvalidItemNBTtestString(String itemNBTcharKey, String dataPrime, String dataDime, String dataAmount, RefSimulator<String> logger) {

        switch (itemNBTcharKey) {
            case "m":
                dataPrime = dataPrime.toUpperCase().replace(" ","_").replace("-", "_");
                dataDime = dataDime.toUpperCase().replace(" ","_").replace("-", "_");
                // Make sure MMOItems is enabled
                if (!Gunging_Ootilities_Plugin.foundMMOItems) {
                    // Announce
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The key '\u00a73" + itemNBTcharKey + "\u00a77' is meant to use with the third party plugin \u00a7e\u00a7lMMOItems\u00a77.");

                    // Failiure
                    return true;
                } else {

                    // Is the type valid?
                    if (!GooPMMOItems.GetMMOItem_TypeNames().contains(dataPrime)) {

                        // Announce
                        Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The MMOItem Type '\u00a73" + dataPrime + "\u00a77' is not loaded.");

                        // Failiure
                        return true;
                    }

                }

                // Clear logger
                if (logger != null) { logger.SetValue(null); }
                break;
            case "e":
                // ENch exists?
                if (!GungingOotilitiesTab.enchantmentsTab.contains(dataPrime)) {
                    // Mention his stupidity
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Enchantment '\u00a73" + dataPrime + "\u00a77' doesnt exist.");

                    // Failure
                    return true;
                }

                // Did this man use correct integer sintax
                if (!IntTryParse(dataDime)) {
                    // Mention his stupidity
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Enchantment Level '\u00a73" + dataDime + "\u00a77' is not an integer number.");

                    // Failure
                    return true;
                }

                // Clear logger
                if (logger != null) { logger.SetValue(null); }
                break;
            case "v":
                // Check Material
                try {
                    // Yes, it seems to be
                    Material vanilla = Material.valueOf(dataPrime.toUpperCase());

                    // Clear logger
                    if (logger != null) { logger.SetValue(null); }

                // Not recognized
                } catch (IllegalArgumentException ex) {
                    // Log it
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Material \u00a7c" + dataPrime + "\u00a77 doesnt exist.");

                    // Thats ass material type
                    return true;
                }

                break;
            case "i":
                if (GooPIngredient.Get(dataPrime) == null) {
                    // Log it
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Could not find GooP Ingredient '\u00a7c" + dataPrime + "\u00a77'.");

                    // Thats not a loaded ingredient
                    return true;
                }
                break;
            default:
                // Thats not a keyword for nbt matching!
                Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The key '\u00a73" + itemNBTcharKey + "\u00a77' is not a supported nbt test key!");

                // Failure
                return true;
        }

        if (dataAmount != null) {

            if (!IntTryParse(dataAmount)) {

                // Thats ass amount
                Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Expected integer number for AMOUNT instead of '\u00a73" + dataAmount + "\u00a77'");

                // Failure
                return true;
            }
        }

        // No Failure, Apparently
        return false;
    }
    public static boolean MatchesItemNBTtestString(ItemStack tItemStack, String itemNBTcharKey, String dataPrime, String dataDime, RefSimulator<String> logger) {

        // Just pass it on with null amount
        return MatchesItemNBTtestString(tItemStack, itemNBTcharKey, dataPrime, dataDime, (Integer) null, logger);
    }
    public static boolean MatchesItemNBTtestString(@Nullable ItemStack tItemStack,@Nullable String itemNBTcharKey,@Nullable String dataPrime,@Nullable String dataDime,@Nullable String dataAmount,@Nullable RefSimulator<String> logger) {
        Integer amountD = null; if (OotilityCeption.IntTryParse(dataAmount)) { amountD = ParseInt(dataAmount); }
        return MatchesItemNBTtestString(tItemStack, itemNBTcharKey, dataPrime, dataDime, amountD, logger);
    }
    public static boolean MatchesItemNBTtestString(@Nullable ItemStack tItemStack,@Nullable String itemNBTcharKey,@Nullable String dataPrime,@Nullable String dataDime,@Nullable Integer dataAmount,@Nullable RefSimulator<String> logger) {
        if (tItemStack == null || itemNBTcharKey == null || dataPrime == null || dataDime == null) { return false; }
        return MatchesItemNBTtestString(tItemStack, new NBTFilter(itemNBTcharKey, dataPrime, dataDime, dataAmount), logger);
    }
    public static boolean MatchesItemNBTtestString(@Nullable ItemStack tItemStack,@Nullable NBTFilter nbtFilter, @Nullable RefSimulator<String> logger) {
        if (tItemStack == null || nbtFilter == null) { return false; }

        // Test for amount first - If amount is defined but it doesnt match it, its a straight no
        if (nbtFilter.getAmount() != null) { if (tItemStack.getAmount() != nbtFilter.getAmount()) { return false; } }

        //Ay so, are we testing for mythicitem type or enchantments?
        switch (nbtFilter.getFilterKey()) {
            case "m":
                if (Gunging_Ootilities_Plugin.foundMMOItems) { return GooPMMOItems.MMOItemMatch(tItemStack, nbtFilter.getDataPrime(), nbtFilter.getDataDime()); } else return false;
            case "e": return TestForEnchantment(tItemStack, nbtFilter.getDataPrime(), ParseInt(nbtFilter.getDataDime()), logger);
            case "v":
                // MMOItems are technically not vanilla so it will return false
                if (Gunging_Ootilities_Plugin.foundMMOItems) { if (GooPMMOItems.IsMMOItem(tItemStack)) { return false; } }

                // Return whether or not the type matches.
                return (tItemStack.getType() == Material.valueOf(nbtFilter.getDataPrime().toUpperCase()));
            case "i":
                // Get Ingredient of Data Prime
                GooPIngredient ing = GooPIngredient.Get(nbtFilter.getDataPrime());
                if (ing == null) { return false; }

                // Found?
                return ing.Matches(tItemStack);
            default: return false;
        }
    }

    /**
     * IT IS ASSUMED THAT YOU CHECKED IT IS VALID WITH IsInvalidItemNBTtestString <p>
     * Gets the item that matches such NBT Test Key. ONLY SUPPORTS v AND m KEYS! Using the enchantment one doesnt even make sense bruh.
     * @param compound an item filter
     * @return NULL if the test key is invalid.
     */
    @Nullable
    public static ItemStack ItemFromNBTTestString(@Nullable String compoundFilter, @Nullable RefSimulator<String> logger) {
        if (compoundFilter == null) { return null; }

        // Split
        String[] mmSplit = compoundFilter.split(" ");

        // Exactly three
        if (mmSplit.length == 3 || mmSplit.length == 4) {
            String dataAmount = null;
            if (mmSplit.length == 4) { dataAmount = mmSplit[3]; }

            // Pass on
            return ItemFromNBTTestString(mmSplit[0], mmSplit[1], mmSplit[2], dataAmount, logger);
        }

        // INVALDI!
        return null;

    }

    /**
     * IT IS ASSUMED THAT YOU CHECKED IT IS VALID WITH IsInvalidItemNBTtestString <p>
     * Gets the item that matches such NBT Test Key. ONLY SUPPORTS v AND m KEYS! Using the enchantment one doesnt even make sense bruh.
     * @param itemNBTcharKey Either v (for vanilla item) or m (for MMOItem)
     * @param dataPrime Vanilla Material (for vanilla item) or MMOItem Type (for MMOItem)
     * @param dataDime Pre 1.13 Damage Value (not supported) or MMOITem ID (for MMOItem)
     * @param dataAmount How many the count of the ItemStack (generally 1 I guess)
     * @return NULL if the test key is invalid.
     */
    @Nullable
    public static ItemStack ItemFromNBTTestString(@NotNull String itemNBTcharKey, @NotNull String dataPrime, @NotNull String dataDime, @Nullable String dataAmount, @Nullable RefSimulator<String> logger) {

        // No null amount
        if (dataAmount == null) { dataAmount = "1"; }

        // Test Char Key
        switch (itemNBTcharKey) {
            case "m":
                // Make sure MMOItems is enabled
                if (!Gunging_Ootilities_Plugin.foundMMOItems) {
                    // Announce
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The key '\u00a73" + itemNBTcharKey + "\u00a77' is meant to use with the third party plugin \u00a7e\u00a7lMMOItems\u00a77.");

                    // Failiure
                    return null;
                } else {

                    // Clear logger
                    if (logger != null) { logger.SetValue(null); }

                    // Get MMOItem
                    ItemStack ret = GooPMMOItems.GetMMOItemOrDefault(dataPrime, dataDime);

                    // Set AMount
                    ret.setAmount(ParseInt(dataAmount));

                    // Return
                    return ret;
                }
            case "e":
                // Ench exists?
                Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The key '\u00a73" + itemNBTcharKey + "\u00a77' is not meant to used to generate items!");

                // Nothing
                return null;
            case "v":
                // Check Material
                try {
                    // Yes, it seems to be
                    Material vanilla = Material.valueOf(dataPrime.toUpperCase());

                    // GEnerate Item Stack
                    ItemStack ret = new ItemStack(vanilla);

                    // Set amount
                    ret.setAmount(ParseInt(dataAmount));

                    // Return
                    return ret;

                // Not recognized
                } catch (IllegalArgumentException ex) {
                    // Log it
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Material \u00a7c" + dataPrime + "\u00a77 doesnt exist.");

                    // Thats ass material type
                    return null;
                }
            default:
                // Thats not a keyword for nbt matching!
                Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The key '\u00a73" + itemNBTcharKey + "\u00a77' is not a supported nbt test key!");

                // Failure
                return null;
        }
    }

    public static String[] entityNBTcharKeys = new String[] {"m", "v"};
    public static boolean IsInvalidEntityNBTtestString(String entityNBTcharKey, String dataPrime, RefSimulator<String> logger) {

        switch (entityNBTcharKey) {
            case "m":
                // Make sure MMOItems is enabled
                if (!Gunging_Ootilities_Plugin.foundMythicMobs) {
                    // Announce
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The key '\u00a73" + entityNBTcharKey + "\u00a77' is meant to use with the third party plugin \u00a7e\u00a7lMythicMobs\u00a77.");

                    // Failiure
                    return true;
                }
                break;
            case "v":
                // Check Material
                try {
                    // Yes, it seems to be
                    EntityType vanilla = EntityType.valueOf(dataPrime.toUpperCase());

                    // Not recognized
                } catch (IllegalArgumentException ex) {
                    // Log it
                    Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Entity Type \u00a7c" + dataPrime + "\u00a77 doesnt exist.");

                    // Thats ass material type
                    return true;
                }

                break;
            default:
                // Thats not a keyword for nbt matching!
                Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "The key '\u00a73" + entityNBTcharKey + "\u00a77' is not a supported entity test key!");

                // Failure
                return true;
        }

        // No Failure, Apparently
        return false;
    }
    public static boolean MatchesEntityNBTtestString(Entity tEntity, String entityNBTcharKey, String dataPrime, RefSimulator<String> logger) {

        //Ay so, are we testing for mythicitem type or enchantments?
        switch (entityNBTcharKey) {
            case "m": return GooPMythicMobs.IsMythicMobOfInternalID(tEntity, dataPrime, logger);
            case "v": return (tEntity.getType() == EntityType.valueOf(dataPrime.toUpperCase()));
            default: return false;
        }
    }
    //endregion

    //region Inventory Evaluation
    public static ItemStackSlot GetInventorySlot(String zlot) {
        int ret = -1;

        // Yo guap where's it at
        ItemStackSlot ratt = null;
        SearchLocation loc = SearchLocation.INVENTORY;
        SearchLocation sideLocation = SearchLocation.SHULKER_INVENTORY;
        PersonalContainerGooP exContainer = null;
        boolean hasDot = zlot.contains(".");

        // Is the slot a number (Must not be shulker box)
        if (IntTryParse(zlot) && !hasDot) {

            // Get that number, straight up
            ret = ParseInt(zlot);

            //SLT//Log("\u00a78Slot \u00a7bNormal\u00a77 Parsed as slot \u00a7b#" + ret);
            return new ItemStackSlot(loc, ret, null);

        // Maybe its a word
        } else {
            boolean success = false;
            switch (zlot) {
                case "head":
                    ret = 103;
                    success = true;
                    break;
                case "chest":
                    ret = 102;
                    success = true;
                    break;
                case "legs":
                    ret = 101;
                    success = true;
                    break;
                case "feet":
                    ret = 100;
                    success = true;
                    break;
                case "mainhand":
                    ret = -7;
                    success = true;
                    break;
                case "offhand":
                    ret = -106;
                    success = true;
                    break;
                case "cursor":
                    ret = -107;
                    success = true;
                    break;
                case "any":

                    //SLT//Log("\u00a78Slot \u00a7bKeywrd\u00a77 Parsed as slot \u00a7bany");
                    return new ItemStackSlot(loc, null, null);
                default: break;
            }

            // Return if early success
            if (success) {
                //SLT//Log("\u00a78Slot \u00a7bKeywrd\u00a77 Parsed as slot \u00a7b#" + ret + "\u00a78 (" + zlot + ")");
                return new ItemStackSlot(loc, ret, null); }
        }

        //Gunging_Ootilities_Plugin.theOots.C Log("-------------------------------");
        //Gunging_Ootilities_Plugin.theOots.C Log("Evaluating <\u00a7e" + zlot + "\u00a77>");

        // Hasn't been found? Must be an advanced slot
        // c23: Observed Container
        // ec23: Enderchest
        // 23.4: Slot 4 of Shulker box at slot 23
        // ec23.*; Any slot of Shulker Box at enderchest 23
        // 5-12; All slots inclusive 5 to 12
        // * Any Inventory Slot
        // ec* Any Enderchest Slot
        // c* Any observed container slot
        // c*.* Any observed container slot within shulkers
        // c*.3-5 Slots 3 to 5 in shulkers in obsrved container slot
        // |ENDERCRATE|3 Slot 3 in the Personal Container ENDERCRATE
        // c<COOLBORDER> Stands for all slots with placeholder COOLBORDER in the obsrved container
        // |ENDERCRATE|<ENDERTOP> Stands for all slots in personal container ENDERCRATE associated with placeholder ENDERTOP
        boolean acceptsPlaceholders = false;
        String source = "";

        // What does it start with? Enderchest?
        if (zlot.startsWith("ec")) {

            // Enderchest Identified
            loc = SearchLocation.ENDERCHEST;
            sideLocation = SearchLocation.SHULKER_ENDERCHEST;
            zlot = zlot.substring(2);
            source = "ec";

            //SLT//Log("\u00a78Slot \u00a7aEnderchest\u00a77 Identified enderchest, slot \u00a7e" + zlot);

        } else if (zlot.startsWith("c")) {

            // Enderchest Identified
            loc = SearchLocation.OBSERVED_CONTAINER;
            sideLocation = SearchLocation.SHULKER_OBSERVED_CONTAINER;
            zlot = zlot.substring(1);
            acceptsPlaceholders = true;
            source = "c";

            //SLT//Log("\u00a78Slot \u00a7aObserved\u00a77 Identified observed container, slot \u00a7e" + zlot);

        } else if (zlot.startsWith("|")) {

            // Enderchest Identified
            loc = SearchLocation.PERSONAL_CONTAINER;
            sideLocation = SearchLocation.SHULKER_PERSONAL_CONTAINER;

            // Get String
            String exRemoved = zlot.substring(1);
            String exName = null;

            //SLT//Log("\u00a78Slot \u00a7ePersonal\u00a77 Identifiying container...");
            if (exRemoved.contains("|")) {

                // Parse
                exName = exRemoved.substring(0, exRemoved.indexOf("|"));
                //SLT//Log("\u00a78Slot \u00a7ePersonal\u00a77 Determined As \u00a7e" + exName);

                // Get
                exContainer = ContainerTemplateGooP.perTemplatePersonalContainers.get(exName);

                // Retrun if null
                if (exContainer == null) {
                    //SLT//Log("\u00a78Slot \u00a7ePersonal\u00a7c Not Loaded");
                return null; }

            } else {

                // Invalid
                //SLT//Log("\u00a78Slot \u00a7ePersonal\u00a7c Missing Container End");
                return null;
            }

            // Strip the name
            zlot = zlot.substring(exName.length() + 2);
            //SLT//Log("\u00a78Slot \u00a7ePersonal\u00a77 Identified \u00a76" + exName + "\u00a77, slot \u00a7e" + zlot);
            acceptsPlaceholders = true;

            source = "|" + exName + "|";
        }

        // Does it have a dot?
        if (hasDot) {
            //SLT//Log("\u00a78Slot \u00a7dShulker\u00a77 Interpreting as shulker slot");
            // Split
            String[] dotSplit = zlot.replace('.','Ñ').split("Ñ");

            // Sintax Error?
            if (dotSplit.length != 2) {

                // Sintax Error
                //SLT//Log("\u00a78Slot \u00a7dShulker\u00a7c Unparseable slot \u00a78(Dot does not split in two)");
                return null;

            } else {

                // Shulker Boxxer
                ItemStackSlot shulk = GetInventorySlot(source + dotSplit[0]);

                // If success
                if (shulk != null) {

                    // Set the personal container o.o
                    if (exContainer != null) {
                        //SLT//Log("\u00a78Slot \u00a76Personal Shulker\u00a77 Personal container attached \u00a7e" + exContainer.getParentTemplate().getInternalName());
                        shulk.SetPersonalContainer(exContainer); }

                    // Get Target Index
                    String dime = dotSplit[1];
                    // Is it any?
                    if (dime.contains("*")) {
                        //SLT//Log("\u00a78Slot \u00a7dShulker Any\u00a7b any \u00a77slot try");

                        // Only one correct thing
                        if (dime.equals("*")) {
                            //SLT//Log("\u00a78Slot \u00a7dShulker Any\u00a7b any \u00a77slot identified");

                            // Shulk
                            ratt = new ItemStackSlot(sideLocation, null, shulk);

                        } else {

                            // Sintax Error
                            //SLT//Log("\u00a78Slot \u00a7dShulker Any\u00a7c Invalid");
                            return null;
                        }

                    // Is it a placeholder?
                    } else if (acceptsPlaceholders && dime.startsWith("<") && dime.endsWith(">")) {

                        //Parse
                        String exName = dime.substring(1, dime.length() - 1);
                        //SLT//Log("\u00a78Slot \u00a7dShulker Placeholder\u00a79 placeholder \u00a77slot, \u00a7e" + exName);

                        // I guess it works, anything could be a placeholder
                        ratt = new ItemStackSlot(sideLocation, null, shulk);
                        ratt.SetGooPContainersPlaceholder(exName);

                    // Otherwise valid integer
                    } else {

                        if (dime.contains("-")) {
                            //SLT//Log("\u00a78Slot \u00a7dShulker Range\u00a73 range \u00a77identified...");

                            // Split
                            String[] dashSplit = dime.split("-");

                            // Defined length
                            if (dashSplit.length != 2) {

                                // Sintax Error
                                //SLT//Log("\u00a78Slot \u00a7dShulker Range\u00a7c Invalid Range \u00a78(No two split gen)");
                                return null;

                            // Correct Length
                            } else {

                                // Does it parse?
                                if (OotilityCeption.IntTryParse(dashSplit[0])) {

                                    // Parse?
                                    if (OotilityCeption.IntTryParse(dashSplit[1])) {

                                        // Actually parse
                                        Integer min = ParseInt(dashSplit[0]);
                                        int max = ParseInt(dashSplit[1]);

                                        // Build
                                        ratt = new ItemStackSlot(sideLocation, min, shulk);
                                        ratt.SetUpperRange(max);
                                        //SLT//Log("\u00a78Slot \u00a7dShulker Range\u00a77 Generated as \u00a7e" + min + " trhu " + max);

                                    } else {

                                        // Syntax Error
                                        //SLT//Log("\u00a78Slot \u00a7dShulker Range\u00a7c Unparsable high bound \u00a76" + dashSplit[1]);
                                        return null;
                                    }

                                } else {

                                    // Syntax Error
                                    //SLT//Log("\u00a78Slot \u00a7dShulker Range\u00a7c Unparsable low bound \u00a76" + dashSplit[0]);
                                    return null;
                                }
                            }

                        } else if (OotilityCeption.IntTryParse(dime)) {
                            //SLT//Log("\u00a78Slot \u00a7dShulker\u00a77 Simple numeric slot identified \u00a7e" + ParseInt(dime));

                            // Parsing Success
                            ratt = new ItemStackSlot(sideLocation, ParseInt(dime), shulk);

                        } else {

                            // Sintax Error
                            //SLT//Log("\u00a78Slot \u00a7dShulker\u00a7c Could not parse slot \u00a76" + dime);
                            return null;
                        }

                    }

                // If parent slot not found
                } else {

                    // Sintax Error
                    //SLT//Log("\u00a78Slot \u00a7dShulker\u00a77 Parent slot was invalid");
                    return null;
                }
            }

        // No so it should parse naturally
        } else {

            // Is it any?
            if (zlot.contains("*")) {
                //SLT//Log("\u00a78Slot \u00a7bAny\u00a77 slot try");

                // Only one thing
                if (zlot.equals("*")) {
                    //SLT//Log("\u00a78Slot \u00a7bAny \u00a77slot identified");

                    // Any Slot within EnderChest
                    ratt = new ItemStackSlot(loc, null, null);

                } else {

                    // Sintax Error
                    //SLT//Log("\u00a78Slot \u00a7bAny\u00a7c Invalid");
                    return null;
                }

            // Is it a placeholder?
            } else if (acceptsPlaceholders && zlot.startsWith("<") && zlot.endsWith(">")) {

                    //Parse
                    String exName = zlot.substring(1, zlot.length() - 1);
                    //SLT//Log("\u00a78Slot \u00a79Placeholder \u00a77slot, \u00a7e" + exName);

                    // I guess it works, anything could be a placeholder
                    ratt = new ItemStackSlot(loc, null, null);
                    ratt.SetGooPContainersPlaceholder(exName);

            // Otherwise valid integer
            } else {

                // If contained?
                if (zlot.contains("-")) {
                    //SLT//Log("\u00a78Slot \u00a73Range \u00a77identified...");

                    // Split
                    String[] dashSplit = zlot.split("-");

                    // Defined length
                    if (dashSplit.length != 2) {

                        // Sintax Error
                        //SLT//Log("\u00a78Slot \u00a73Range\u00a7c Invalid Range \u00a78(No two split gen)");
                        return null;

                    // Correct Length
                    } else {

                        // Does it parse?
                        if (OotilityCeption.IntTryParse(dashSplit[0])) {

                            // Parse?
                            if (OotilityCeption.IntTryParse(dashSplit[1])) {

                                // Actually parse
                                Integer min = ParseInt(dashSplit[0]);
                                int max = ParseInt(dashSplit[1]);

                                // Build
                                ratt = new ItemStackSlot(loc, min, null);
                                ratt.SetUpperRange(max);
                                //SLT//Log("\u00a78Slot \u00a73Range\u00a77 Generated as \u00a7e" + min + " trhu " + max);

                            } else {

                                // Syntax Error
                                //SLT//Log("\u00a78Slot \u00a73Range\u00a7c Unparsable high bound \u00a76" + dashSplit[1]);
                                return null;
                            }

                        } else {

                            // Syntax Error
                            //SLT//Log("\u00a78Slot \u00a73Range\u00a7c Unparsable low bound \u00a76" + dashSplit[0]);
                            return null;
                        }
                    }

                } else if (OotilityCeption.IntTryParse(zlot)) {
                    //SLT//Log("\u00a78Slot \u00a7eSlot\u00a77 Simple numeric slot identified \u00a7e" + ParseInt(zlot));

                    // Parsing Success
                    ratt = new ItemStackSlot(loc, ParseInt(zlot), null);

                } else {

                    // Sintax Error
                    //SLT//Log("\u00a78Slot \u00a7eSlot\u00a7c Could not parse slot \u00a76" + zlot);
                    return null;
                }
            }
        }

        // Does it target the observed container
        if (exContainer != null) {
            //SLT//Log("\u00a78Slot \u00a76Personal\u00a77 Personal container attached \u00a7e" + exContainer.getParentTemplate().getInternalName());
            ratt.SetPersonalContainer(exContainer); }

        return ratt;
    }
    public static ArrayList<ItemStackSlot> GetInventorySlots(String arg, Player elaborator, RefSimulator<String> logger) {
        // Will begin with the value to return
        ArrayList<ItemStackSlot> slott = new ArrayList<>();
        String[] slots = new String[] { arg };
        StringBuilder invalids = new StringBuilder();
        if (arg.contains(",")) { slots = arg.split(","); }
        for (String sl : slots) {
            //SLOT//OotilityCeption. Log(("Parsing Slot \u00a7f" + sl);

            // Does it parse?
            ItemStackSlot oSlot = OotilityCeption.GetInventorySlot(sl);

            // Gather
            if (oSlot != null) {

                // Thats the one
                slott.addAll(oSlot.Elaborate(elaborator));

            } else {
                //SLOT//OotilityCeption. Log(("\u00a78\u00a7oInvalid");

                // Include in invalids
                invalids.append("\u00a77, \u00a73").append(sl);
            }
        }

        if (invalids.length() > 3) {

            // You see, it makes no sense vro
            Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "\u00a77Invalid Slots Encountered: " + (invalids.toString()).substring(3));

        } else if (logger != null) { logger.setValue(null); }

        // Return
        return slott;
    }

    public static ItemStackLocation GetInvenItem(Player target, Integer slott, boolean continuousFormat) {
        ItemStackSlot tSlot = new ItemStackSlot(SearchLocation.INVENTORY, slott, null);

        // Bake slott
        if (continuousFormat) {

            // Cancel if the slot equals the held.
            if (slott >= 0 && (slott == target.getInventory().getHeldItemSlot())) { return null; }

            int trueSlot = slott;
            // Is it the cursor?
            if (slott == -7) { trueSlot = -107; }
            // Is it the mainhand?
            if (slott == -6) { trueSlot = target.getInventory().getHeldItemSlot(); }
            // Is it the offhand?
            if (slott == -5) { trueSlot = -106; }
            // Is it the Helmet?
            if (slott == -4) { trueSlot = 103; }
            // Is it the Chest?
            if (slott == -3) { trueSlot = 102; }
            // Is it the Legs?
            if (slott == -2) { trueSlot = 101; }
            // Is it the Boots?
            if (slott == -1) { trueSlot = 100; }

            // Bake
            tSlot = new ItemStackSlot(SearchLocation.INVENTORY, trueSlot, null);
        }

        return GetInvenItem(target, tSlot);
    }
    public static ItemStackLocation GetInvenItem(Player target, ItemStackSlot tSlot) { return GetInvenItem(target, tSlot, null); }
    public static ItemStackLocation GetInvenItem(Player target, ItemStackSlot tSlot, String shulkerBoxNameFilter) {

        // ItemStack to return
        ItemStack ret = null;
        ItemStackLocation ratt = null;

        // Easy Access Params
        Integer slott = tSlot.getSlot();
        Integer majorSlot;
        boolean succ = true;

        // This function does not accept 'ANY'
        if (slott == null) { return null; }

        // Does not accept ranges
        if (!tSlot.getUpperRange().equals(slott)) { return null; }

        switch (tSlot.getLocation()) {
            case INVENTORY:
                // Is it one of the 36 slots?
                if (slott >= 0 && slott < 36) { ret = target.getInventory().getItem(slott); }

                // Is it the cursor?
                else if (slott == -107) { ret = target.getOpenInventory().getCursor(); }

                // Is it the mainhand?
                else if (slott == -7) { ret = target.getInventory().getItemInMainHand(); }

                // Is it the offhand?
                else if (slott == -106) { ret = target.getInventory().getItemInOffHand(); }

                // Is it the Helmet?
                else if (slott == 103) { ret = target.getInventory().getHelmet(); }

                // Is it the Chest?
                else if (slott == 102) { ret = target.getInventory().getChestplate(); }

                // Is it the Legs?
                else if (slott == 101) { ret = target.getInventory().getLeggings(); }

                // Is it the Boots?
                else if (slott == 100) { ret = target.getInventory().getBoots(); }

                // Must have found smthn
                if (ret == null) { return null; }

                // Build ISL
                ratt = new ItemStackLocation(ret, target.getInventory(), slott, SearchLocation.INVENTORY, null);
                break;
            case ENDERCHEST:

                // Get from Enderchest, if within range
                if (slott >= 0 && slott < 27) { ret = target.getEnderChest().getItem(slott); }

                // Must have found smthn
                if (ret == null) { return null; }

                // Build ISL
                ratt = new ItemStackLocation(ret, target.getEnderChest(), slott, SearchLocation.ENDERCHEST, null);
                break;

            case OBSERVED_CONTAINER:

                // Pass on to ContainerTemplateGooP
                return ContainerTemplateGooP.GetObservedStationItemStack(target.getUniqueId(), slott, null);

            case PERSONAL_CONTAINER:

                // Slot must specify personal
                if (tSlot.GetPersonalContainer() != null) {

                    // Get from Enderchest, if within range
                    if (slott >= 0 && slott < tSlot.GetPersonalContainer().getParentTemplate().getTotalSlotCount()) { ret = tSlot.GetPersonalContainer().GetInventoryItem(target.getUniqueId(), slott); }

                    // Must have found smthn
                    if (ret == null) { return null; }

                    // Build ISL
                    ratt = new ItemStackLocation(ret, tSlot.GetPersonalContainer(), target.getUniqueId(), slott, SearchLocation.PERSONAL_CONTAINER, null);

                } else {

                    // Uh nope
                    return null;
                }
                break;

            case SHULKER_INVENTORY:
                // Must have a parent
                if (tSlot.getParentSlot() == null) { return null; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();

                // This function does not accept 'ANY'
                if (majorSlot == null) { return null; }

                // Is it one of the 36 slots?
                if (majorSlot >= 0 && majorSlot < 36) { ret = target.getInventory().getItem(majorSlot); }

                // Is it the mainhand?
                else if (majorSlot == -7) { ret = target.getInventory().getItemInMainHand(); }

                // Is it the offhand?
                else if (majorSlot == -106) { ret = target.getInventory().getItemInOffHand(); }

                // Is it the Helmet?
                else if (majorSlot == 103) { ret = target.getInventory().getHelmet(); }

                // Is it the Chest?
                else if (majorSlot == 102) { ret = target.getInventory().getChestplate(); }

                // Is it the Legs?
                else if (majorSlot == 101) { ret = target.getInventory().getLeggings(); }

                // Is it the Boots?
                else if (majorSlot == 100) { ret = target.getInventory().getBoots(); }

                // Must have found smthn
                if (ret == null) { return null; }

                // Is it a shulker boxx?
                if (shulkerBoxNameFilter != null) { succ = OotilityCeption.ParseColour(OotilityCeption.GetItemName(ret)).equals(OotilityCeption.ParseColour(shulkerBoxNameFilter)); }
                if (IsShulkerBox(ret.getType()) && succ) {

                    // Get Meta
                    BlockStateMeta bsm = (BlockStateMeta) ret.getItemMeta();
                    ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                    // Clear
                    ItemStack trueRet = null;

                    // Is it one of the 27 slots?
                    if (slott >= 0 && slott < 27) { trueRet = boxx.getInventory().getItem(slott); }

                    // Must have found smthn
                    if (trueRet == null) { return null; }

                    // Build ISL
                    ratt = new ItemStackLocation(trueRet, boxx.getInventory(), slott, SearchLocation.SHULKER_INVENTORY, new ItemStackLocation(ret, target.getInventory(), majorSlot, SearchLocation.INVENTORY, null));

                } else {

                    // Nope.
                    return null;
                }
                break;


            case SHULKER_ENDERCHEST:
                // Must have a parent
                if (tSlot.getParentSlot() == null) { return null; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();

                // This function does not accept 'ANY'
                if (majorSlot == null) { return null; }

                // Is it one of the 36 slots?
                if (majorSlot >= 0 && majorSlot < 27) { ret = target.getEnderChest().getItem(majorSlot); }

                // Must have found smthn
                if (ret == null) { return null; }

                // Is it a shulker boxx?
                if (shulkerBoxNameFilter != null) { succ = OotilityCeption.ParseColour(OotilityCeption.GetItemName(ret)).equals(OotilityCeption.ParseColour(shulkerBoxNameFilter)); }
                if (IsShulkerBox(ret.getType()) && succ) {

                    // Get Meta
                    BlockStateMeta bsm = (BlockStateMeta) ret.getItemMeta();
                    ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                    // Clear
                    ItemStack trueRet = null;

                    // Is it one of the 36 slots?
                    if (slott >= 0 && slott < 27) { trueRet = boxx.getInventory().getItem(slott); }

                    // Must have found smthn
                    if (trueRet == null) { return null; }

                    // Build ISL
                    ratt = new ItemStackLocation(trueRet, boxx.getInventory(), slott, SearchLocation.SHULKER_ENDERCHEST, new ItemStackLocation(ret, target.getEnderChest(), majorSlot, SearchLocation.ENDERCHEST, null));

                } else {

                    // Nope.
                    return null;
                }
                break;


            case SHULKER_OBSERVED_CONTAINER:
                // Must have a parent
                if (tSlot.getParentSlot() == null) { return null; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();

                // Pass on to ContainerTemplateGooP
                ItemStackLocation os = ContainerTemplateGooP.GetObservedStationItemStack(target.getUniqueId(), majorSlot, null);

                // Must have found something
                if (os == null) { return null; }

                // Get Item Stack
                ret = os.getItem();

                // Must have found smthn
                if (ret == null) { return null; }

                // Is it a shulker boxx?
                if (shulkerBoxNameFilter != null) { succ = OotilityCeption.ParseColour(OotilityCeption.GetItemName(ret)).equals(OotilityCeption.ParseColour(shulkerBoxNameFilter)); }
                if (IsShulkerBox(ret.getType()) && succ) {

                    // Get Meta
                    BlockStateMeta bsm = (BlockStateMeta) ret.getItemMeta();
                    ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                    // Clear
                    ItemStack trueRet = null;

                    // Is it one of the 36 slots?
                    if (slott >= 0 && slott < 27) { trueRet = boxx.getInventory().getItem(slott); }

                    // Must have found smthn
                    if (trueRet == null) { return null; }

                    // Build ISL
                    ratt = new ItemStackLocation(trueRet, boxx.getInventory(), slott, SearchLocation.SHULKER_OBSERVED_CONTAINER, os);

                } else {

                    // Nope.
                    return null;
                }
                break;

            case SHULKER_PERSONAL_CONTAINER:

                // Must have a parent
                if (tSlot.getParentSlot() == null) { return null; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();
                //SLT//OotilityCeption.Log("\u00a76[\u00a78SPC\u00a76]\u00a77 Parent found: \u00a7e" + majorSlot);

                // Slot must specify personal
                if (tSlot.GetPersonalContainer() != null) {
                    //SLT//OotilityCeption.Log("\u00a76[\u00a78SPC\u00a76]\u00a77 Container found: \u00a7e" + tSlot.GetPersonalContainer().getParentTemplate().getInternalName());

                    // Get from Enderchest, if within range
                    if (majorSlot >= 0 && majorSlot < tSlot.GetPersonalContainer().getParentTemplate().getTotalSlotCount()) {
                        ret = tSlot.GetPersonalContainer().GetInventoryItem(target.getUniqueId(), majorSlot); }

                    // Must have found smthn
                    if (ret == null) { return null; }
                    //SLT//OotilityCeption.Log("\u00a76[\u00a78SPC\u00a76]\u00a77 Item found: \u00a7e" + OotilityCeption.GetItemName(ret));

                    // Is it a shulker boxx?
                    if (shulkerBoxNameFilter != null) { succ = OotilityCeption.ParseColour(OotilityCeption.GetItemName(ret)).equals(OotilityCeption.ParseColour(shulkerBoxNameFilter)); }

                    if (IsShulkerBox(ret.getType()) && succ) {
                        //SLT//OotilityCeption.Log("\u00a76[\u00a78SPC\u00a76]\u00a77 Shulker Box Identified, getting slot \u00a7e" + slott);

                        // Get Meta
                        BlockStateMeta bsm = (BlockStateMeta) ret.getItemMeta();
                        ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                        // Clear
                        ItemStack trueRet = null;

                        // Is it one of the 27 slots?
                        if (slott >= 0 && slott < 27) { trueRet = boxx.getInventory().getItem(slott); }

                        // Must have found smthn
                        if (trueRet == null) { return null; }
                        //SLT//OotilityCeption.Log("\u00a76[\u00a78SPC\u00a76]\u00a77 True Item found: \u00a7e" + OotilityCeption.GetItemName(trueRet));

                        // Build ISL
                        ratt = new ItemStackLocation(trueRet, boxx.getInventory(), slott, SearchLocation.SHULKER_PERSONAL_CONTAINER, new ItemStackLocation(ret, tSlot.GetPersonalContainer(), target.getUniqueId(), slott, SearchLocation.PERSONAL_CONTAINER, null));

                    } else {

                        // Nope.
                        return null;
                    }

                } else {
                    //SLOT//OotilityCeption. Log(("Parent Slot had no Personal Container");
                    // Uh nope
                    return null;
                }
                break;
        }
        return ratt;
    }
    public static void SetInvenItem(Player target, ItemStackSlot tSlot, ItemStack stacc) {

        // Easy Access Params
        Integer slott = tSlot.getSlot();
        Integer majorSlot;

        // 5 Shulkers
        ItemStack shulk = null;

        // This function does not accept 'ANY'
        if (slott == null) { return; }

        // Does not accept ranges
        if (!tSlot.getUpperRange().equals(slott)) { return; }

        switch (tSlot.getLocation()) {
            case INVENTORY:
                // Is it one of the 36 slots?
                if (slott >= 0 && slott < 36) { target.getInventory().setItem(slott, stacc); }

                // Is it the mainhand?
                else if (slott == -7) { target.getInventory().setItemInMainHand(stacc); }

                // Is it the offhand?
                else if (slott == -106) { target.getInventory().setItemInOffHand(stacc); }

                // Is it the Helmet?
                else if (slott == 103) { target.getInventory().setHelmet(stacc); }

                // Is it the Chest?
                else if (slott == 102) { target.getInventory().setChestplate(stacc); }

                // Is it the Legs?
                else if (slott == 101) { target.getInventory().setLeggings(stacc); }

                // Is it the Boots?
                else if (slott == 100) { target.getInventory().setBoots(stacc); } else { return; }
                break;

            case ENDERCHEST:

                // Get from Enderchest, if within range
                if (slott >= 0 && slott < 27) { target.getEnderChest().setItem(slott, stacc); } else { return; }
                break;

            case OBSERVED_CONTAINER:

                // Pass on to ContainerTemplateGooP
                ContainerTemplateGooP.SetObservedStationItemStack(target.getUniqueId(), slott, stacc, null);
                return;

            case PERSONAL_CONTAINER:

                // Slot must specify personal
                if (tSlot.GetPersonalContainer() != null) {

                    // Get from Enderchest, if within range
                    if (slott >= 0 && slott < tSlot.GetPersonalContainer().getParentTemplate().getTotalSlotCount()) { tSlot.GetPersonalContainer().SetAndSaveInventoryItem(target.getUniqueId(), slott, stacc, null); }

                } else {

                    // Uh nope
                    return;
                }
                break;

            case SHULKER_INVENTORY:
                // Must have a parent
                if (tSlot.getParentSlot() == null) { return; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();

                // This function does not accept 'ANY'
                if (majorSlot == null) { return; }

                // Is it one of the 36 slots?
                if (majorSlot >= 0 && majorSlot < 36) { shulk = target.getInventory().getItem(majorSlot); }

                // Is it the mainhand?
                else if (majorSlot == -7) { shulk = target.getInventory().getItemInMainHand(); }

                // Is it the offhand?
                else if (majorSlot == -106) { shulk = target.getInventory().getItemInOffHand(); }

                // Is it the Helmet?
                else if (majorSlot == 103) { shulk = target.getInventory().getHelmet(); }

                // Is it the Chest?
                else if (majorSlot == 102) { shulk = target.getInventory().getChestplate(); }

                // Is it the Legs?
                else if (majorSlot == 101) { shulk = target.getInventory().getLeggings(); }

                // Is it the Boots?
                else if (majorSlot == 100) { shulk = target.getInventory().getBoots(); }

                // Must have found smthn
                if (shulk == null) { return; }

                // Is it a shulker boxx?
                if (IsShulkerBox(shulk.getType())) {

                    // Get Meta
                    BlockStateMeta bsm = (BlockStateMeta) shulk.getItemMeta();
                    ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                    // Is it one of the 27 slots?
                    if (slott >= 0 && slott < 27) { boxx.getInventory().setItem(slott, stacc); bsm.setBlockState(boxx); shulk.setItemMeta(bsm); } else { return; }

                } else {

                    // Nope.
                    return;
                }
                break;


            case SHULKER_ENDERCHEST:
                // Must have a parent
                if (tSlot.getParentSlot() == null) { return; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();

                // This function does not accept 'ANY'
                if (majorSlot == null) { return; }

                // Is it one of the 36 slots?
                if (majorSlot >= 0 && majorSlot < 27) { shulk = target.getEnderChest().getItem(majorSlot); }

                // Must have found smthn
                if (shulk == null) { return; }

                // Is it a shulker boxx?
                if (IsShulkerBox(shulk.getType())) {

                    // Get Meta
                    BlockStateMeta bsm = (BlockStateMeta) shulk.getItemMeta();
                    ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                    // Clear
                    ItemStack trueRet = null;

                    // Is it one of the 36 slots?
                    if (slott >= 0 && slott < 27) { boxx.getInventory().setItem(slott, stacc); bsm.setBlockState(boxx); shulk.setItemMeta(bsm); } else { return; }

                } else {

                    // Nope.
                    return;
                }
                break;


            case SHULKER_OBSERVED_CONTAINER:
                // Must have a parent
                if (tSlot.getParentSlot() == null) { return; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();

                // Pass on to ContainerTemplateGooP
                ItemStackLocation os = ContainerTemplateGooP.GetObservedStationItemStack(target.getUniqueId(), majorSlot, null);

                // Must have found something
                if (os == null) { return; }

                // Get Item Stack
                shulk = os.getItem();

                // Must have found smthn
                if (shulk == null) { return; }

                // Is it a shulker boxx?
                if (IsShulkerBox(shulk.getType())) {

                    // Get Meta
                    BlockStateMeta bsm = (BlockStateMeta) shulk.getItemMeta();
                    ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                    // Clear
                    ItemStack trueRet = null;

                    // Is it one of the 36 slots?
                    if (slott >= 0 && slott < 27) { trueRet = boxx.getInventory().getItem(slott); bsm.setBlockState(boxx); shulk.setItemMeta(bsm); } else { return; }

                } else {

                    // Nope.
                    return;
                }
                break;

            case SHULKER_PERSONAL_CONTAINER:
                // Must have a parent
                if (tSlot.getParentSlot() == null) { return; }

                // Get Major Slot Information
                majorSlot = tSlot.getParentSlot().getSlot();

                // Slot must specify personal
                if (tSlot.GetPersonalContainer() != null) {

                    // Get from Enderchest, if within range
                    if (majorSlot >= 0 && majorSlot < tSlot.GetPersonalContainer().getParentTemplate().getTotalSlotCount()) { shulk = tSlot.GetPersonalContainer().GetInventoryItem(target.getUniqueId(), majorSlot); }

                    // Must have found smthn
                    if (shulk == null) { return; }

                    // Is it a shulker boxx?
                    if (IsShulkerBox(shulk.getType())) {

                        // Get Meta
                        BlockStateMeta bsm = (BlockStateMeta) shulk.getItemMeta();
                        ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                        // Is it one of the 27 slots?
                        if (slott >= 0 && slott < 27) { boxx.getInventory().setItem(slott, stacc); bsm.setBlockState(boxx); shulk.setItemMeta(bsm); } else { return; }

                    } else {

                        // Nope.
                        return;
                    }

                } else {

                    return;
                }
                break;
        }
    }

    public static ArrayList<ItemStackLocation> InventoryMatching(Player target, String nbtKey, String dataPrime, String dataDime, RefSimulator<Integer> totalFound, String shulkerNameFilter, RefSimulator<String> logger, SearchLocation... locations) {

        // Create Ret
        ArrayList<ItemStackLocation> ret = new ArrayList<>();
        int totalAmount = 0;

        // Search inventories first before shilker
        ArrayList<SearchLocation> primarySweepPlaces = new ArrayList<>();
        ArrayList<SearchLocation> secondarySweepPlaces = new ArrayList<>();

        // Cook some booleans for efficiency
        boolean invShulk = false, ecShulk = false, perShulk = false, obsShulk = false;
        boolean invShulkC = false, ecShulkC = false, perShulkC = false, obsShulkC = false;
        for (SearchLocation loc : locations) {

            // Identify which shulkers we must watch out for
            switch (loc) {
                case SHULKER_INVENTORY:
                    invShulk = true;
                    secondarySweepPlaces.add(loc);
                    break;
                case SHULKER_ENDERCHEST:
                    ecShulk = true;
                    secondarySweepPlaces.add(loc);
                    break;
                case SHULKER_OBSERVED_CONTAINER:
                    obsShulk = true;
                    secondarySweepPlaces.add(loc);
                    break;
                case SHULKER_PERSONAL_CONTAINER:
                    perShulk = true;
                    secondarySweepPlaces.add(loc);
                    break;
                case INVENTORY:
                    primarySweepPlaces.add(loc);
                    break;
                case ENDERCHEST:
                    primarySweepPlaces.add(loc);
                    break;
                case OBSERVED_CONTAINER:
                    primarySweepPlaces.add(loc);
                break;
                case PERSONAL_CONTAINER:
                    primarySweepPlaces.add(loc);
                break;
            }
        }

        // Enable Shulker-Only Search
        for (SearchLocation loc : secondarySweepPlaces) {
            switch (loc) {
                case SHULKER_INVENTORY:

                    // If not contained
                    if (!primarySweepPlaces.contains(SearchLocation.INVENTORY)) {

                        // Inclide and Constrain
                        primarySweepPlaces.add(SearchLocation.INVENTORY);
                        invShulkC = true;
                    }
                    break;
                case SHULKER_ENDERCHEST:

                    // If not contained
                    if (!primarySweepPlaces.contains(SearchLocation.ENDERCHEST)) {

                        // Inclide and Constrain
                        primarySweepPlaces.add(SearchLocation.ENDERCHEST);
                        ecShulkC = true;
                    }
                    break;
                case SHULKER_OBSERVED_CONTAINER:

                    // If not contained
                    if (!primarySweepPlaces.contains(SearchLocation.OBSERVED_CONTAINER)) {

                        // Inclide and Constrain
                        primarySweepPlaces.add(SearchLocation.OBSERVED_CONTAINER);
                        obsShulkC = true;
                    }
                    break;
                case SHULKER_PERSONAL_CONTAINER:

                    // If not contained
                    if (!primarySweepPlaces.contains(SearchLocation.PERSONAL_CONTAINER)) {

                        // Inclide and Constrain
                        primarySweepPlaces.add(SearchLocation.PERSONAL_CONTAINER);
                        perShulkC = true;
                    }
                    break;
            }
        }

        // Store shilker box spaces
        ArrayList<ItemStackLocation> invShulkerBoxes = new ArrayList<>();
        ArrayList<ItemStackLocation> ecShulkerBoxes = new ArrayList<>();
        ArrayList<ItemStackLocation> perShulkerBoxes = new ArrayList<>();
        ArrayList<ItemStackLocation> obsShulkerBoxes = new ArrayList<>();

        // All Locations that must be observed
        for (SearchLocation loc : primarySweepPlaces) {

            // Method differs from location to location
            switch (loc) {
                case INVENTORY:
                    // Inspect whole inventory
                    for (int sl = -7; sl < 36; sl++) {

                        // Temporary Item Stack for evaluation
                        ItemStack targetItem = null;
                        ItemStackLocation tISource = null;

                        // Get item in continuous format
                        tISource = OotilityCeption.GetInvenItem(target, sl, true);

                        // If found
                        if (tISource != null) { targetItem = tISource.getItem(); }

                        // If there was actually something
                        if (targetItem != null) {

                            // If searchiong in here
                            if (!invShulkC) {

                                // Did it match?
                                if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                    // Add to Relevant ITems
                                    ret.add(tISource);

                                    // Increase Amount if defined
                                    totalAmount += targetItem.getAmount();
                                }
                            }

                            // Is it a shulker box thoi?
                            if (invShulk) {

                                // If it is a shulker box
                                if (IsShulkerBox(targetItem.getType())) {

                                    // Matches filter?
                                    boolean nameSuccess = true;
                                    if (shulkerNameFilter != null) { nameSuccess = OotilityCeption.ParseColour(OotilityCeption.GetItemName(targetItem)).equals(OotilityCeption.ParseColour(shulkerNameFilter)); }
                                    if (nameSuccess) {

                                        // Identify it
                                        invShulkerBoxes.add(tISource);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case ENDERCHEST:
                    // Inspect whole inventory
                    for (int sl = 0; sl < 27; sl++) {

                        // Temporary Item Stack for evaluation
                        ItemStack targetItem = null;
                        ItemStackLocation tISource = null;

                        // Get item in continuous format
                        tISource = OotilityCeption.GetInvenItem(target, new ItemStackSlot(SearchLocation.ENDERCHEST, sl, null));

                        // If found
                        if (tISource != null) { targetItem = tISource.getItem(); }

                        // If there was actually something
                        if (targetItem != null) {

                            // If searching in here
                            if (!ecShulkC) {

                                // Did it match?
                                if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                    // Add to Relevant ITems
                                    ret.add(tISource);

                                    // Increase Amount if defined
                                    totalAmount += targetItem.getAmount();
                                }
                            }

                            // Is it a shulker box thoi?
                            if (ecShulk) {

                                // If it is a shulker box
                                if (IsShulkerBox(targetItem.getType())) {

                                    // Matches filter?
                                    boolean nameSuccess = true;
                                    if (shulkerNameFilter != null) { nameSuccess = OotilityCeption.ParseColour(OotilityCeption.GetItemName(targetItem)).equals(OotilityCeption.ParseColour(shulkerNameFilter)); }
                                    if (nameSuccess) {

                                        // Identify it
                                        ecShulkerBoxes.add(tISource);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case PERSONAL_CONTAINER:
                    // For every container loaded wth
                    for (PersonalContainerGooP persCont : ContainerTemplateGooP.loadedPersonalContainers) {

                        // Get Size of Container
                        int size = persCont.getParentTemplate().getTotalSlotCount();

                        // Inspect whole inventory
                        for (int sl = 0; sl < size; sl++) {

                            // If for storage
                            if (persCont.getParentTemplate().IsStorageSlot(sl)) {

                                // Temporary Item Stack for evaluation
                                ItemStack targetItem = null;

                                // Get item in continuous format
                                targetItem = persCont.GetInventoryItem(target.getUniqueId(), sl);

                                // If there was actually something
                                if (targetItem != null) {

                                    // If searching in here
                                    if (!perShulkC) {

                                        // Did it match?
                                        if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                            // Add to Relevant ITems
                                            ret.add(new ItemStackLocation(targetItem, persCont, target.getUniqueId(), sl, SearchLocation.PERSONAL_CONTAINER, null));

                                            // Increase Amount if defined
                                            totalAmount += targetItem.getAmount();
                                        }
                                    }

                                    // Is it a shulker box thoi?
                                    if (perShulk) {

                                        // If it is a shulker box
                                        if (IsShulkerBox(targetItem.getType())) {

                                            // Matches filter?
                                            boolean nameSuccess = true;
                                            if (shulkerNameFilter != null) { nameSuccess = OotilityCeption.ParseColour(OotilityCeption.GetItemName(targetItem)).equals(OotilityCeption.ParseColour(shulkerNameFilter)); }
                                            if (nameSuccess) {

                                                // Identify it
                                                perShulkerBoxes.add(new ItemStackLocation(targetItem, persCont, target.getUniqueId(), sl, SearchLocation.PERSONAL_CONTAINER, null));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case OBSERVED_CONTAINER:
                    // Get Size of Container
                    int size = ContainerTemplateGooP.GetObservedStationSize(target.getUniqueId(), null);

                    // Inspect whole inventory
                    for (int sl = 0; sl < size; sl++) {

                        // Temporary Item Stack for evaluation
                        ItemStack targetItem = null;
                        ItemStackLocation tISource = null;

                        // Get item in continuous format
                        tISource = ContainerTemplateGooP.GetObservedStationItemStack(target.getUniqueId(), sl, null);

                        // If found
                        if (tISource != null) { targetItem = tISource.getItem(); }

                        // If there was actually something
                        if (targetItem != null) {

                            // If searching in here
                            if (!obsShulkC) {

                                // Did it match?
                                if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                    // Add to Relevant ITems
                                    ret.add(tISource);

                                    // Increase Amount if defined
                                    totalAmount += targetItem.getAmount();
                                }
                            }

                            // Is it a shulker box thoi?
                            if (obsShulk) {

                                // If it is a shulker box
                                if (IsShulkerBox(targetItem.getType())) {

                                    // Matches filter?
                                    boolean nameSuccess = true;
                                    if (shulkerNameFilter != null) { nameSuccess = OotilityCeption.ParseColour(OotilityCeption.GetItemName(targetItem)).equals(OotilityCeption.ParseColour(shulkerNameFilter)); }
                                    if (nameSuccess) {

                                        // Identify it
                                        obsShulkerBoxes.add(tISource);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }

        // Secondary Locations
        for (SearchLocation loc : secondarySweepPlaces) {

            // Method differs from location to location
            switch (loc) {
                case SHULKER_INVENTORY:
                    // Every found shulker box
                    for (ItemStackLocation shulk : invShulkerBoxes) {

                        // Inspect whole inventory
                        for (int sl = 0; sl < 27; sl++) {

                            // Temporary Item Stack for evaluation
                            ItemStack targetItem = null;
                            ItemStackLocation tISource = null;

                            // Get Meta
                            BlockStateMeta bsm = (BlockStateMeta) shulk.getItem().getItemMeta();
                            ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                            // Get item in continuous format
                            tISource = new ItemStackLocation(boxx.getInventory().getItem(sl), boxx.getInventory(), sl, SearchLocation.SHULKER_INVENTORY, shulk);

                            // If found
                            if (tISource != null) { targetItem = tISource.getItem(); }

                            // If there was actually something
                            if (targetItem != null) {

                                // Did it match?
                                if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                    // Add to Relevant ITems
                                    ret.add(tISource);

                                    // Increase Amount if defined
                                    totalAmount += targetItem.getAmount();
                                }
                            }
                        }
                    }
                    break;
                case SHULKER_ENDERCHEST:
                    // Every found shulker box
                    for (ItemStackLocation shulk : ecShulkerBoxes) {

                        // Inspect whole inventory
                        for (int sl = 0; sl < 27; sl++) {

                            // Temporary Item Stack for evaluation
                            ItemStack targetItem = null;
                            ItemStackLocation tISource = null;

                            // Get Meta
                            BlockStateMeta bsm = (BlockStateMeta) shulk.getItem().getItemMeta();
                            ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                            // Get item in continuous format
                            tISource = new ItemStackLocation(boxx.getInventory().getItem(sl), boxx.getInventory(), sl, SearchLocation.SHULKER_ENDERCHEST, shulk);

                            // If found
                            if (tISource != null) { targetItem = tISource.getItem(); }

                            // If there was actually something
                            if (targetItem != null) {

                                // Did it match?
                                if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                    // Add to Relevant ITems
                                    ret.add(tISource);

                                    // Increase Amount if defined
                                    totalAmount += targetItem.getAmount();
                                }
                            }
                        }
                    }
                    break;
                case SHULKER_OBSERVED_CONTAINER:
                    // Every found shulker box
                    for (ItemStackLocation shulk : obsShulkerBoxes) {

                        // Inspect whole inventory
                        for (int sl = 0; sl < 27; sl++) {

                            // Temporary Item Stack for evaluation
                            ItemStack targetItem = null;
                            ItemStackLocation tISource = null;

                            // Get Meta
                            BlockStateMeta bsm = (BlockStateMeta) shulk.getItem().getItemMeta();
                            ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                            // Get item in continuous format
                            tISource = new ItemStackLocation(boxx.getInventory().getItem(sl), boxx.getInventory(), sl, SearchLocation.SHULKER_OBSERVED_CONTAINER, shulk);

                            // If found
                            if (tISource != null) { targetItem = tISource.getItem(); }

                            // If there was actually something
                            if (targetItem != null) {

                                // Did it match?
                                if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                    // Add to Relevant ITems
                                    ret.add(tISource);

                                    // Increase Amount if defined
                                    totalAmount += targetItem.getAmount();
                                }
                            }
                        }
                    }
                    break;
                case SHULKER_PERSONAL_CONTAINER:
                    // Every found shulker box
                    for (ItemStackLocation shulk : perShulkerBoxes) {

                        // Inspect whole inventory
                        for (int sl = 0; sl < 27; sl++) {

                            // Temporary Item Stack for evaluation
                            ItemStack targetItem = null;
                            ItemStackLocation tISource = null;

                            // Get Meta
                            BlockStateMeta bsm = (BlockStateMeta) shulk.getItem().getItemMeta();
                            ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();

                            // Get item in continuous format
                            tISource = new ItemStackLocation(boxx.getInventory().getItem(sl), boxx.getInventory(), sl, SearchLocation.SHULKER_PERSONAL_CONTAINER, shulk);

                            // If found
                            if (tISource != null) { targetItem = tISource.getItem(); }

                            // If there was actually something
                            if (targetItem != null) {

                                // Did it match?
                                if (OotilityCeption.MatchesItemNBTtestString(targetItem, nbtKey, dataPrime, dataDime, logger)) {

                                    // Add to Relevant ITems
                                    ret.add(tISource);

                                    // Increase Amount if defined
                                    totalAmount += targetItem.getAmount();
                                }
                            }
                        }
                    }
                    break;
            }
        }

        // Return total found
        if (totalFound != null) { totalFound.setValue(totalAmount); }

        // Return ret
        return ret;
    }
    //endregion

    //region Blocks Management

    /**
     * Returns the bottom half of the block if bisected, or itself if not bisected.
     *
     * @param source Block that may be BISECTED
     */
    public static Block AdjustBisected(Block source) {

        if (source.getBlockData() instanceof Bisected) {

            // If it is the half
            if (((Bisected)source.getBlockData()).getHalf() == Bisected.Half.TOP) {

                // Get the one below
                return source.getRelative(BlockFace.DOWN);
            }
        }

        return source;
    }
    /**
     * Returns the top half of the block if bisected, or itself if not bisected.
     *
     * @param source Block that may be BISECTED
     */
    public static Block InverseAdjustBisected(Block source) {

        if (source.getBlockData() instanceof Bisected) {

            // If it is the half
            if (((Bisected)source.getBlockData()).getHalf() == Bisected.Half.BOTTOM) {

                // Get the one below
                return source.getRelative(BlockFace.UP);
            }
        }

        return source;
    }
    public static boolean IsBottomHalfOfBisected(Block source) {

        if (source.getBlockData() instanceof Bisected) {

            // Get the one below, and identify if its half
            return ((Bisected) source.getBlockData()).getHalf() == Bisected.Half.BOTTOM;
        }

        // Not bisected, not bottom half
        return false;
    }
    public static boolean IsTopHalfOfBisected(Block source) {

        if (source.getBlockData() instanceof Bisected) {

            // Get the one below, and identify if its half
            return ((Bisected) source.getBlockData()).getHalf() == Bisected.Half.TOP;
        }

        // Not bisected, not bottom half
        return false;
    }
    public static Block GetBlockUnder(Block source) {

        // Get the one below
        return source.getRelative(BlockFace.DOWN);
    }

    public static ArrayList<Block> BlocksInFront(Block first, BlockFace dir, int range, boolean cancelOnAir) {
        ArrayList<Block> affected = new ArrayList<>();

        for (int i = 1; i <= range; i++) {

            // Get the block
            Block bkk = GetBlockAt(first.getLocation(), dir, i);

            // Is air? If so, cancel the chain and return this
            if (cancelOnAir) { if(OotilityCeption.IsAir(bkk.getType())) { return affected; } }

            // Add the block at that range
            affected.add(bkk);
        }

        return affected;
    }
    public static ArrayList<Block> BlocksInFront(Block first, BlockFace dir, int range, List<Block> blacklist, boolean cancelOnAir) {
        ArrayList<Block> affected = new ArrayList<>();

        OotilityCeption oots = new OotilityCeption();

        for (int i = 1; i <= range; i++) {

            // Get the block
            Block bkk = GetBlockAt(first.getLocation(), dir, i);

            // Is air? If so, cancel the chain and return this
            if (cancelOnAir) { if(OotilityCeption.IsAir(bkk.getType())) {

                //DBG//oots.ECP Log("Block Counter - \u00a7b\u00a7oAir", "#" + i + ": \u00a73" + bkk.getType());
                return affected;
            } }

            // Is any of the blacklisted blocks this one? If so, return the array without it. It will be considered with its own range.
            for (Block blck : blacklist) { if (LocationEquals(bkk.getLocation(), blck.getLocation())) {

                //DBG//oots.ECP Log("Block Counter - \u00a7b\u00a7oBlacklist", "#" + i + ": \u00a73" + bkk.getType());
                return affected;
            } }

            // Add the block at that range
            affected.add(bkk);
        }

        return affected;
    }
    public static Block GetBlockAt(Location relative, BlockFace dir, int range) {
        return relative.getWorld().getBlockAt((new Location( relative.getWorld(), 0, 0, 0).add(relative).add(PositivelyScaleVector(dir.getDirection(), range))));
    }
    public static org.bukkit.util.Vector PositivelyScaleVector(org.bukkit.util.Vector ver, double sc) {
        return new Vector(ver.getX() * sc, ver.getY() * sc, ver.getZ() * sc);
    }
    public static boolean LocationEquals(Location l1, Location l2) { return (l1.getWorld() == l2.getWorld()) && (l1.getBlockX() == l2.getBlockX()) && (l1.getBlockY() == l2.getBlockY()) && (l1.getBlockZ() == l2.getBlockZ()); }
    //endregion
}