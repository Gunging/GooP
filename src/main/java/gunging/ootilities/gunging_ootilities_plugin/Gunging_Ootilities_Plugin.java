package gunging.ootilities.gunging_ootilities_plugin;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.*;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCListener;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCCommands;
import gunging.ootilities.gunging_ootilities_plugin.containers.interaction.ContainersInteractionHandler;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CustomStructures;
import gunging.ootilities.gunging_ootilities_plugin.events.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation.GTranslationManager;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ApplicableMask;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ConverterTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class Gunging_Ootilities_Plugin extends JavaPlugin implements Listener {

    // Gamerules
    public static Boolean sendGooPSuccessFeedback = false;
    public static Boolean sendGooPFailFeedback = false;
    public static Boolean blockImportantErrorFeedback = false;
    public static Boolean saveGamerulesConfig = false;
    public static Boolean griefBreaksBedrock = false;
    public static Boolean anvilRenameEnabled = false;
    public static boolean spamPunchingJSON = false;
    public static boolean useMMOLibDefenseConvert = false;
    public static Boolean devLogging = true;
    public static Player devPlayer = null;
    public static Double nameRangeExclusionMin = null;
    public static Double nameRangeExclusionMax = null;
    public static Integer placeholderReadableness = null;
    public static Double summonKillDistance = null;
    @NotNull public static HashMap<String, String> commandFailDisclosures = new HashMap<>();

    boolean loading = false;
    boolean loaded = false;

    // Compatibilities
    public static Boolean foundWorldGuard = false;
    public static Boolean foundMMOItems = false;
    public static Boolean foundMMOCore = false;
    public static Boolean foundGraveyards = false;
    public static Boolean foundMythicMobs = false;
    public static Boolean foundPlaceholderAPI = false;
    public static Boolean foundMCMMO = false;
    public static Boolean foundVault = false;
    public static Boolean foundWorldEdit = false;
    public static Boolean foundDiscordSRV = false;
    public static Boolean foundGriefPrevention = false;
    public static Boolean foundTowny = false;
    public static Boolean asPaperSpigot = false;

    public static Boolean usingMMOItemShrubs = false;
    public static Boolean usingXBow2RocketLauncher = false;

    final Plugin plugin = this;
    public static Plugin getPlugin() { return theMain.plugin; }

    // Singleton
    public static Gunging_Ootilities_Plugin theMain;
    public static OotilityCeption theOots;

    // Persistent Data
    public FileConfigPair optiFineGlintPair, translationsPair, mySQLHostInfo, customModelDataLinkPair, goopUnlockables, applicableMasksPair, mmoitemsConverterPair, globalContainerContents, listPlaceholderPair, fontsPair;
    public ArrayList<FileConfigPair> customStructurePairs, containerTemplatesPairs, recipesPairs, ingredientsPairs;
    public HashMap<YamlConfiguration, FileConfigPair> storageRoots = new HashMap<>();

    static long bootTime = 0;
    public static long getBootTime() { return bootTime; }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Override
    public void onLoad() {
        bootTime = System.currentTimeMillis();
        loading = true;
        theMain = this;
        theOots = new OotilityCeption();

        // Singleton
        OotilityCeption.Fill_xSupression();

        // Is it Paper Spigot?
        @SuppressWarnings("ConstantConditions") EntityDeathEvent paperSpigot = new EntityDeathEvent(null, null);
        //noinspection ConstantConditions
        if (paperSpigot instanceof Cancellable) { asPaperSpigot = true; }

        // Ready all materials!
        GooP_MinecraftVersions.InitializeMaterials();

        //region Premium Custom Structures Attempt
        try {
            // Sweet, it is there
            GooPP_CustomStructures premiumCSCheck = new GooPP_CustomStructures();

            // Well
            premiumCSCheck.CompatibilityCheck();
            CustomStructures.Enable();

        } catch (NoClassDefFoundError ignored) { }
        //endregion

        //region Premium Containers Attempt
        try {
            // Sweet, it is there
            GooPP_Containers premiumCNCheck = new GooPP_Containers();

            // Well
            premiumCNCheck.CompatibilityCheck();
            GOOPCManager.enable();

        } catch (NoClassDefFoundError ignored) { }
        //endregion

        //region World Guard Compatibility Attempt
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            foundWorldGuard = true;
            GooPWorldGuard.LoadNRegisterFlags();

        } else {

            try {
                // Sweet, it is there
                new GooPWorldGuard();

                // Well
                GooPWorldGuard.LoadNRegisterFlags();
                foundWorldGuard = true;

            } catch (NoClassDefFoundError e) {
                // Vr0
                foundWorldGuard = false;
            }
        }
        //endregion

        //region World Edit Compatibility Attempt
        try {
            // Sweet, it is there
            GooPWorldEdit worldEditCheck = new GooPWorldEdit();

            // Well
            worldEditCheck.CompatibilityCheck();
            foundWorldEdit = GooPWorldEdit.pluginExisted();

        } catch (Throwable e) {

            // Vr0
            foundWorldEdit = false;
        }
        //endregion

        //region DiscordSRV Compatibility Attempt
        try {
            // Sweet, it is there
            GooPDiscordSRV discordSRV = new GooPDiscordSRV();

            // Welp
            discordSRV.CompatibilityCheck();

            // Subscribed the hecc off it
            foundDiscordSRV = true;

        } catch (Throwable e) {

            // Vr0
            foundDiscordSRV = false;
        }
        //endregion

        //region GriefPrevention Compatibility Attempt
        try {
            // Sweet, it is there
            new GooPGriefPrevention();

            // Anyway
            foundGriefPrevention = GooPGriefPrevention.claimsMarketExisted();

        } catch (Throwable e) {
            e.printStackTrace();
            // Vr0
            foundGriefPrevention = false;
        }
        //endregion

        //region Towny Compatibility Attempt
        try {
            // Sweet, it is there
            new GooPTowny();

            // Yeah
            foundTowny = true;

        } catch (Throwable e) {

            // Vr0
            foundTowny = false;
        }
        //endregion

        //region MythicMobs Compatibility Attempt
        if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            foundMythicMobs = true;

        } else {

            try {
                // Sweet, it is there
                GooPMythicMobs mythicMobsCheck = new GooPMythicMobs();

                // Welp
                mythicMobsCheck.CompatibilityCheck();
                foundMythicMobs = true;

            } catch (NoClassDefFoundError e) {
                // Vr0
                foundMythicMobs = false;
            }
        }
        //endregion

        //region McMMO Compatibility Attempt
        if (getServer().getPluginManager().getPlugin("mcMMO") != null) {
            foundMCMMO = true;

        } else {

            try {
                // Sweet, it is there
                GooPMCMMO mcmmoCheck = new GooPMCMMO();

                // Welp
                mcmmoCheck.CompatibilityCheck();
                foundMCMMO = true;

            } catch (NoClassDefFoundError e) {
                // Vr0
                foundMCMMO = false;
            }
        }
        //endregion

        //region MMOItems Compatibility Attempt
        if (getServer().getPluginManager().getPlugin("MMOItems") != null) {
            foundMMOItems = GooPMMOItems.RegisterContainersEquipment();

        } else {

            try {
                // Sweet, it is there
                GooPMMOItems mmoitemsCheck = new GooPMMOItems();

                // Welp
                foundMMOItems = mmoitemsCheck.CompatibilityCheck();

            } catch (NoClassDefFoundError e) {
                // Vr0
                foundMMOItems = false;
            }
        }

        //region MMOItems Subversions and Stat Registry
        if (foundMMOItems) {

            // Register Stats
            GooPMMOItems.RegisterCustomStats(getConfig().getInt("MiscStatAmount", 3));
        }
        //endregion
        //endregion

        //region MMOCore Compatibility Attempt
        if (getServer().getPluginManager().getPlugin("MMOCore") != null) {
            foundMMOCore = true;

        } else {

            try {
                // Sweet, it is there
                GooPMMOCore mmocoreCheck = new GooPMMOCore();

                // Welp
                mmocoreCheck.CompatibilityCheck();
                foundMMOCore = true;

            } catch (NoClassDefFoundError e) {
                // Vr0
                foundMMOCore = false;
            }
        }
        //endregion

        //region PlaceholderAPI Compatibility Attempt
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            foundPlaceholderAPI = true;

        } else {

            try {
                // Sweet, it is there
                GooPPlaceholderAPI papiCheck = new GooPPlaceholderAPI();

                // Welp
                papiCheck.CompatibilityCheck();
                foundPlaceholderAPI = true;

            } catch (NoClassDefFoundError e) {
                // Vr0
                foundPlaceholderAPI = false;
            }
        }
        //endregion

        //region Graveyards Compatibility Attempt
        try {
            // Sweet, it is there
            GooPGraveyards graveyardsCheck = new GooPGraveyards();

            // Welp
            graveyardsCheck.CompatibilityCheck();
            foundGraveyards = true;

        } catch (NoClassDefFoundError e) {
            // Vr0
            foundGraveyards = false;

        } catch (Throwable e) {
            // Vr0
            foundGraveyards = true;
        }
        //endregion

        //region Vault Compatibility Attempt
        try {

            // So
            GooPVault.CompatibilityCheck();
            foundVault = true;

        } catch (NoClassDefFoundError e) {
            // Vr0
            foundVault = false;

        }
        //endregion

        loaded = true;
    }
    public boolean HasLoaded() { return loaded; }
    public boolean IsLoading() { return loading; }

    /**
     * @param name Plugin name
     *
     * @return If this is enabled
     */
    public boolean isPluginEnabled(@NotNull String name) {

        // If existing and enabled
        Plugin plugin =getServer().getPluginManager().getPlugin(name);
        if (plugin == null) { return false; }
        return plugin.isEnabled();
    }

    @Override public void onEnable() {

        //region Re-enable or Re-disable plugins in case they crashed in their OnEnable
        if (foundWorldGuard) { foundWorldGuard = isPluginEnabled("WorldGuard"); }
        if (foundMythicMobs) { foundMythicMobs = isPluginEnabled("MythicMobs"); }
        if (foundMCMMO) { foundMCMMO = isPluginEnabled("mcMMO"); }
        if (foundMMOItems) { foundMMOItems = isPluginEnabled("MMOItems"); }
        if (foundMMOCore) { foundMMOCore = isPluginEnabled("MMOCore"); }
        if (foundPlaceholderAPI) { foundPlaceholderAPI = isPluginEnabled("PlaceholderAPI"); }
        if (foundGraveyards) { foundGraveyards = isPluginEnabled("Graveyards"); }
        if (foundVault) {
            try {
                // Sweet, it is there
                GooPVault vaultCheck = new GooPVault();

                // Well
                foundVault = vaultCheck.SetupEconomy(getServer());

            } catch (NoClassDefFoundError e) {

                // Vr0
                foundVault = false;
            }
        }
        //endregion

        // Startup pre warms
        GooP_MinecraftVersions.GetMinecraftVersion();
        ContainersInteractionHandler.registerHandlers();

        // Register Events
        getServer().getPluginManager().registerEvents(new DeathPrevent(), theMain);
        getServer().getPluginManager().registerEvents(new CustomStructures(), theMain);
        getServer().getPluginManager().registerEvents(new ScoreboardLinks(), theMain);
        getServer().getPluginManager().registerEvents(new XBow_Rockets(), theMain);
        getServer().getPluginManager().registerEvents(new GOOPCListener(), theMain);
        getServer().getPluginManager().registerEvents(new GooP_FontUtils(), theMain);
        getServer().getPluginManager().registerEvents(new SummonerClassUtils(), theMain);
        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { getServer().getPluginManager().registerEvents(new JSONPlacerUtils(), theMain); }
        if (foundMMOItems) {
            getServer().getPluginManager().registerEvents(new OnApplyCommand(), theMain);
        }
        if (foundMythicMobs) { getServer().getPluginManager().registerEvents(new GooPMythicMobs(), theMain); }

        // Schedule insync
        Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), ((Runnable) new SummonerClassUtils()), 40, 200);

        //noinspection ConstantConditions
        getCommand("gungingootilities").setExecutor(new GungingOotilities());
        //noinspection ConstantConditions
        getCommand("gungingootilities").setTabCompleter(new GungingOotilitiesTab());

        //noinspection ConstantConditions
        getCommand("gremove").setExecutor(new GOOPCCommands());
        //noinspection ConstantConditions
        getCommand("gpublic").setExecutor(new GOOPCCommands());
        //noinspection ConstantConditions
        getCommand("gprivate").setExecutor(new GOOPCCommands());
        //noinspection ConstantConditions
        getCommand("gmodify").setExecutor(new GOOPCCommands());
        //noinspection ConstantConditions
        getCommand("ginfo").setExecutor(new GOOPCCommands());

        // Load the commands tab
        GungingOotilitiesTab.Start();

        //region Announce compatibilities

        theOots.CPLog("Checking for Premium Modules...");
        if (CustomStructures.IsPremiumEnabled()) {
            theOots.CPLog(ChatColor.YELLOW + "Premium Custom Structures found\u00a77.");
        } else {
            theOots.CPLog(ChatColor.GOLD + "Premium Custom Structures not found\u00a77.");
        }
        if (GOOPCManager.isPremiumEnabled()) {
            theOots.CPLog(ChatColor.YELLOW + "Premium Containers found\u00a77.");
        } else {
            theOots.CPLog(ChatColor.GOLD + "Premium Containers not found\u00a77.");
        }

        theOots.CPLog("Enabling GooP Extensions...");
        if (usingMMOItemShrubs) {
            theOots.CPLog(ChatColor.GREEN + "MMOItem Shrubs enabled\u00a77.");
        } else {
            theOots.CPLog(ChatColor.DARK_GREEN + "MMOItem Shrubs not found\u00a77.");
        }

        if (usingXBow2RocketLauncher){
            theOots.CPLog(ChatColor.GREEN + "Crossbow2RocketLauncher enabled\u00a77.");
        } else {
            theOots.CPLog(ChatColor.DARK_GREEN + "Crossbow2RocketLauncher not found\u00a77.");
        }

        theOots.CPLog("Checking for Compatible Plugins...");
        if (foundWorldGuard){
            theOots.CPLog(ChatColor.AQUA + "WorldGuard found\u00a77.");
        } else {
            theOots.CPLog(ChatColor.BLUE + "WorldGuard not found\u00a77.");
        }

        if (foundWorldEdit){
            theOots.CPLog(ChatColor.AQUA + "WorldEdit found\u00a77.");
        } else {
            theOots.CPLog(ChatColor.BLUE + "WorldEdit not found\u00a77.");
        }

        if (foundDiscordSRV){
            theOots.CPLog(ChatColor.AQUA + "DiscordSRV found\u00a77.");
        } else {
            theOots.CPLog(ChatColor.BLUE + "DiscordSRV not found\u00a77.");
        }

        if (foundMMOItems) {
            theOots.CPLog(ChatColor.AQUA + "MMOItems found\u00a77.");

        } else {
            theOots.CPLog(ChatColor.BLUE + "MMOItems not found\u00a77.");
        }

        if (foundMMOCore) {
            theOots.CPLog(ChatColor.AQUA + "MMOCore found\u00a77.");
        } else {
            theOots.CPLog(ChatColor.BLUE + "MMOCore not found\u00a77.");
        }

        if (foundVault) {
            theOots.CPLog(ChatColor.AQUA + "Vault found\u00a77.");
        }else {
            theOots.CPLog(ChatColor.BLUE + "Vault not found\u00a77.");
        }

        if (foundGraveyards){
            theOots.CPLog(ChatColor.AQUA + "Graveyards found\u00a77.");
        } else {

            theOots.CPLog(ChatColor.BLUE + "Graveyards not found\u00a77.");
        }

        if (foundMythicMobs){
            theOots.CPLog(ChatColor.AQUA + "MythicMobs found\u00a77.");

            // Register placeholders
            GooPMythicMobs.RegisterPlaceholders(foundMMOItems);
        } else {

            theOots.CPLog(ChatColor.BLUE + "MythicMobs not found\u00a77.");
        }

        if (foundMCMMO){

            theOots.CPLog(ChatColor.AQUA + "McMMO found\u00a77.");
        } else {

            theOots.CPLog(ChatColor.BLUE + "McMMO not found\u00a77.");
        }

        if (foundPlaceholderAPI){

            theOots.CPLog(ChatColor.AQUA + "PlaceholderAPI found\u00a77.");
            (new GooPPlaceholderAPI()).register();

        } else {

            theOots.CPLog(ChatColor.BLUE + "PlaceholderAPI not found\u00a77.");
        }

        if (foundGriefPrevention) {
            theOots.CPLog(ChatColor.AQUA + "GriefPrevention found\u00a77.");
        }

        if (foundTowny){
            theOots.CPLog(ChatColor.AQUA + "Towny found\u00a77.");
        }
        //endregion

        // Load Configuration Files
        Reload(true);

        // Finish
        theOots.CPLog(ChatColor.GREEN + "Initialization Complete. Success.");

        // The delayed startup is currently only used for MMOItems, unnecessary to launch it without
        if (!foundMMOItems) { return; }

        // Funny delayed startup
        (new BukkitRunnable() {
            public void run() {

                // Basically Save
                GooPMMOItems.ReloadMiscStatLore();
            }

        }).runTaskLaterAsynchronously(Gunging_Ootilities_Plugin.theMain, 2L);
    }

    public void Reload(boolean reloadInstances) {

        // Retrieve Config Values
        saveDefaultConfig();
        reloadConfig();
        /// Template to gather config values
        ///String sText = getConfig().getString("SampleText");
        ///String sFirstValue = getConfig().getStringList("SampleTArray").get(0);
        String nameRangeExclusionMinRaw = null, nameRangeExclusionMaxRaw = null, placeholderReadablenessRaw = null;
        useMMOLibDefenseConvert = getConfig().getBoolean("ConverterUsesDefense", false);
        spamPunchingJSON = getConfig().getBoolean("JSONFurnitureSpamPunchingBreak", true);
        if (getConfig().contains("SendSuccessFeedback"))
            sendGooPSuccessFeedback = getConfig().getBoolean("SendSuccessFeedback");
        if (getConfig().contains("SummonLeashKillDistance"))
            summonKillDistance = getConfig().getDouble("SummonLeashKillDistance", 60);
        if (getConfig().contains("SendFailFeedback")) sendGooPFailFeedback = getConfig().getBoolean("SendFailFeedback");
        if (getConfig().contains("BlockErrorFeedback"))
            blockImportantErrorFeedback = getConfig().getBoolean("BlockErrorFeedback");
        if (getConfig().contains("SaveGameruleChanges"))
            saveGamerulesConfig = getConfig().getBoolean("SaveGameruleChanges");
        if (getConfig().contains("GriefBedrockCommand"))
            griefBreaksBedrock = getConfig().getBoolean("GriefBedrockCommand");
        if (getConfig().contains("AnvilRename")) anvilRenameEnabled = getConfig().getBoolean("AnvilRename");
        if (getConfig().contains("NameExclusionMin"))
            nameRangeExclusionMinRaw = getConfig().getString("NameExclusionMin");
        if (getConfig().contains("NameExclusionMax"))
            nameRangeExclusionMaxRaw = getConfig().getString("NameExclusionMax");
        if (getConfig().contains("NameNumberDecimals"))
            placeholderReadablenessRaw = getConfig().getString("NameNumberDecimals");
        if (getConfig().contains("SuggestedGemstoneColours"))
            GungingOotilitiesTab.gemstoneColours = getConfig().getStringList("SuggestedGemstoneColours");

        // Clear that
        commandFailDisclosures.clear();

        // Include legacy bind
        String legacyVaultLowBalance = getConfig().getString("VaultLowBalance");
        if (legacyVaultLowBalance != null) { commandFailDisclosures.put("VaultLowBalance", legacyVaultLowBalance); }

        // Compile fail messages
        FileConfigPair failMessages = GetConfigAt(null, "fail-messages.yml", true, false);
        if (failMessages != null) {

            // Extract storage
            YamlConfiguration fmConfig = failMessages.getStorage();

            // Go through each value
            for (String key : fmConfig.getKeys(false)) {

                // Ignore invalid keys
                if (key == null) { continue; }

                // Get as string
                String message = fmConfig.getString(key);

                // Valid?
                if (message != null) {

                    // Include in disclosures
                    commandFailDisclosures.put(key, message);
                }
            }
        }

        // Parse some stuff
        if (placeholderReadablenessRaw == null) {
            placeholderReadableness = 2;
        } else {

            // Parses?
            if (OotilityCeption.IntTryParse(placeholderReadablenessRaw)) {

                // Parse
                placeholderReadableness = OotilityCeption.ParseInt(placeholderReadablenessRaw);

            } else {

                // Default
                placeholderReadableness = 2;
            }
        }
        if (nameRangeExclusionMaxRaw == null) {
            nameRangeExclusionMax = 0.01;
        } else {

            // Parses?
            if (OotilityCeption.DoubleTryParse(nameRangeExclusionMaxRaw)) {

                // Parse
                nameRangeExclusionMax = Double.parseDouble(nameRangeExclusionMaxRaw);

            } else if (nameRangeExclusionMaxRaw.equals("infinity")) {

                // Actually set as null
                nameRangeExclusionMax = null;

                // Otherwised efailt
            } else {

                // Default
                nameRangeExclusionMax = 0.01;
            }
        }
        if (nameRangeExclusionMinRaw == null) {
            nameRangeExclusionMin = -0.01;
        } else {

            // Parses?
            if (OotilityCeption.DoubleTryParse(nameRangeExclusionMinRaw)) {

                // Parse
                nameRangeExclusionMin = Double.parseDouble(nameRangeExclusionMinRaw);

            } else if (nameRangeExclusionMinRaw.equals("-infinity")) {

                // Actually set as null
                nameRangeExclusionMin = null;

                // Otherwised efailt
            } else {

                // Default
                nameRangeExclusionMin = 0.01;
            }
        }

        // Clear Latest References
        l8st.clear();

        // Get Custom Configs
        SetupPersistentData(reloadInstances);
    }

    public void UpdateConfigBool(String path, Boolean value) {
        // If the path isnt contained, it will be created, but lets log the change.
        if (!getConfig().contains(path)) {
            theOots.CPLog("New \u00a7aconfig.yml\u00a77 path '\u00a73" + path + "\u00a77' requested. \u00a76This should only happen to outdated configs.");
        }

        getConfig().set(path, value);
    }
    public void UpdateConfigInteger(String path, Integer value) {
        // If the path isn't contained, it will be created, but lets log the change.
        if (!getConfig().contains(path)) {
            theOots.CPLog("New \u00a7aconfig.yml\u00a77 path '\u00a73" + path + "\u00a77' requested. \u00a76This should only happen to outdated configs.");
        }

        getConfig().set(path, value);
    }

    public void SetupPersistentData(boolean reloadInstances) {

        //region Translations
        translationsPair = GetConfigAt(null, "plugin-messages.yml", true, false);
        if (translationsPair != null) { storageRoots.put(translationsPair.getStorage(), translationsPair); }
        //endregion

        //region OptiFine Glints
        optiFineGlintPair = GetConfigAt(null, "opti-fine-glints.yml", true, false);
        if (optiFineGlintPair != null) { storageRoots.put(optiFineGlintPair.getStorage(), optiFineGlintPair); }
        //endregion

        //region List Placeholders MythicMobs stuff
        if (foundMythicMobs) {
            listPlaceholderPair = GetConfigAt(null, "list-placeholders.yml", true, false);
            if (listPlaceholderPair != null) { storageRoots.put(listPlaceholderPair.getStorage(), listPlaceholderPair); }
        }
        //endregion

        //region Font Utils
        fontsPair = GetConfigAt(null, "font-codes.yml", true, false);
        if (fontsPair != null) { storageRoots.put(fontsPair.getStorage(), fontsPair); }
        //endregion

        //region Enchantment Scourge
        FileConfigPair enchScourge = GetConfigAt(null, "enchantment-delete.yml", true, false);
        blacklistedEnchantments.clear();
        if (enchScourge != null) {

            // Scourge Load
            List<String> rawBlacklistedEnchantments = enchScourge.getStorage().getStringList("DeletedEnchantments");
            for (String str : rawBlacklistedEnchantments) { Enchantment ench = OotilityCeption.GetEnchantmentByName(str); if (ench != null) { blacklistedEnchantments.add(ench); } }

            // Enchantment Replace
            replacementEnchantment = OotilityCeption.GetEnchantmentByName(enchScourge.getStorage().getString("ReplacementEnchantment", "no"));
        }


        //endregion

        //region MySQL Params
        //SQL//mySQLHostInfo = GetConfigAt(null, "mysql-support.yml", true, false);
        //SQL//if (mySQLHostInfo != null) { storageRoots.put(mySQLHostInfo.getStorage(), mySQLHostInfo); }
        //endregion

        //region Custom Structures
        customStructurePairs = GetConfigsAt("custom-structures");
        for (FileConfigPair fcP : customStructurePairs) { storageRoots.put(fcP.getStorage(), fcP); }
        //endregion

        //region Applicable Masks and Converter
        if (foundMMOItems) {
            applicableMasksPair = GetConfigAt(null, "onapply-masks.yml", true, false);
            if (applicableMasksPair != null) { storageRoots.put(applicableMasksPair.getStorage(), applicableMasksPair); }

            mmoitemsConverterPair = GetConfigAt(null, "mmoitems-converter.yml", true, false);
            if (mmoitemsConverterPair != null) { storageRoots.put(mmoitemsConverterPair.getStorage(), mmoitemsConverterPair); }
        }
        //endregion

        //region Containers
        containerTemplatesPairs = GetConfigsAt("container-templates");
        containerTemplatesPairs.add(GetConfigAt(null, "container-templates.yml", false, true)); // Backward Compatibility.
        for (FileConfigPair fcP : containerTemplatesPairs) { storageRoots.put(fcP.getStorage(), fcP); }

        GOOPCManager.updatePhysicalFormats(GetConfigsAt("container-instances/physical")); // Backward Compatibility
        GOOPCManager.updatePersonalFormats(GetConfigsAt("container-instances/personal")); // Backward Compatibility
        //endregion

        //region Recipes & Ingredients
        //recipesPairs = GetConfigsAt("container-templates/recipes");
        //for (FileConfigPair fcP : recipesPairs) { storageRoots.put(fcP.getStorage(), fcP); }
        //
        //ingredientsPairs = GetConfigsAt("container-templates/ingredients");
        //for (FileConfigPair fcP : ingredientsPairs) { storageRoots.put(fcP.getStorage(), fcP); }
        //endregion

        //region Custom Model Data Link
        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) {
            customModelDataLinkPair = GetConfigAt(null, "custom-model-data-links.yml", true, false);
            if (customModelDataLinkPair != null) { storageRoots.put(customModelDataLinkPair.getStorage(), customModelDataLinkPair); }
        }
        //endregion

        //region Examples
        GetConfigAt(null, "external-examples/GooP/container-templates/ConsumableBuffs.yml", true, false);
        GetConfigAt(null, "external-examples/GooP/container-templates/SmoothieMaker.yml", true, false);
        GetConfigAt(null, "external-examples/GooP/custom-structures/netheritegolem.yml", true, false);
        GetConfigAt(null, "external-examples/GooP/custom-structures/SmoothieMaker.yml", true, false);

        GetConfigAt(null, "external-examples/MMOItems/dynamic/mythic-mobs-abilities/prismanecer.yml", true, false);
        GetConfigAt(null, "external-examples/MMOItems/item/conbuff.yml", true, false);
        GetConfigAt(null, "external-examples/MMOItems/item/consumable.yml", true, false);
        GetConfigAt(null, "external-examples/MMOItems/language/stats.yml", true, false);

        GetConfigAt(null, "external-examples/MythicMobs/Packs/GooPExamples/Items/items_goop.yml", true, false);
        GetConfigAt(null, "external-examples/MythicMobs/Packs/GooPExamples/Mobs/mobs_goop.yml", true, false);
        GetConfigAt(null, "external-examples/MythicMobs/Packs/GooPExamples/Skills/skills_goop.yml", true, false);
        //endregion

        //region Unlockables
        goopUnlockables = GetConfigAt("persistent-data", "unlockdata.yml", false, true);
        storageRoots.put(goopUnlockables.getStorage(), goopUnlockables);
        //endregion

        //region Reload what must be reloaded
        GTranslationManager.reloadTranslations();
        OptiFineGlint.ReloadGlints(theOots);
        GooP_FontUtils.ReloadFonts(theOots);
        GooPUnlockables.Reload(theOots);
        //SQL//GooPSQL.Reload();

        // CustomStructures supports JSON Furniture so CMD Links must load first.
        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { CustomModelDataLink.ReloadCustomModelDataLinks(theOots); }

        // Containers supports Appliccable Masks so thay must load first
        if (foundMMOItems) {
            ApplicableMask.reloadMasks();
            ConverterTypes.ConverterReload(); }

        if (foundMythicMobs) { GooPMythicMobs.ReloadListPlaceholders(theOots); }

        // Reload the big ones gg
        CustomStructures.ReloadStructures(theOots);
        GOOPCManager.reloadContainers();
        //endregion
    }

    //region Enchantment Scourge
    @NotNull public static ArrayList<Enchantment> blacklistedEnchantments = new ArrayList<>();
    @Nullable public static Enchantment replacementEnchantment = null;
    //endregion

    public boolean savingTheseTicks;
    public HashMap<String, FileConfigPair> l8st = new HashMap<>();
    public ArrayList<FileConfigPair> pairs2save = new ArrayList<>();
    public void SaveFile(FileConfigPair csPair) {
        // Assuming it is ALWAYS the latest or a modification of the latest

        // Update the latest
        l8st.put(csPair.getFile().getPath(), csPair);
        //DBG*/theOots.ECP Log("MMOItem Shrubs", "Made changes to storage pertaining to \u00a7b" + csPair.getFile().getPath());

        //region Make sure it is marked to be saved
        if (pairs2save == null) { pairs2save = new ArrayList<>(); }

        //endregion
        pairs2save.add(csPair);

        // Save the file half a second from now with all its changes
        if (!Gunging_Ootilities_Plugin.theMain.savingTheseTicks && !Gunging_Ootilities_Plugin.theMain.shutDown) {
            Gunging_Ootilities_Plugin.theMain.savingTheseTicks = true;

            (new BukkitRunnable() {
                public void run() {

                    // Basically Save
                    Gunging_Ootilities_Plugin.theMain.FileSaveExecution();

                    // Allow to save again
                    Gunging_Ootilities_Plugin.theMain.savingTheseTicks = false;

                }

            }).runTaskLaterAsynchronously(Gunging_Ootilities_Plugin.theMain, 10L);
        }
    }
    public void FileSaveExecution() {

        while (pairs2save.size() > 0) {

            FileConfigPair fl = pairs2save.get(0);

            // Basically Saves it
            try {

                // Save all files
                fl.getStorage().save(fl.getFile());

            } catch (IOException ignored) { }

            // Remove
            pairs2save.remove(0);
        }
    }
    @NotNull public FileConfigPair GetLatest(@NotNull FileConfigPair csPair) {

        // Make sure it exists
        if (l8st == null) { l8st = new HashMap<>(); }

        // If theres anything in there
        if (l8st.size() > 0) {

            // FInd
            FileConfigPair fcp = l8st.get(csPair.getFile().getPath());

            if (fcp != null) {

                return new FileConfigPair(csPair.getFile(), fcp.getStorage());

            } else return csPair;

        // If no latest version exists
        } else {

            // Then this is the latest
            return csPair;
        }
    }

    // Parent path is subfolder name I guess. "subfolder" in Gunging_Ootilities_Plugin/subfolder

    /**
     * Will create directory if it doesnt exist
     *
     * @param parentPath Path of folder
     * @param name Name of file (include extension plz)
     * @param exampleResource Is there an example resource that should be saved in this place if it doesnt exist yet?
     * @param expectInvalid If it wasn't supposed to be invalid, the creation of it will be logged into the console
     *
     * @return {@link NotNull} unless there was a <b>parsing error reading the file</b>.
     */
    @Contract("_,_,_,true->!null")
    @Nullable public FileConfigPair GetConfigAt(@Nullable String parentPath, @NotNull String name, boolean exampleResource, boolean expectInvalid) {
        // Make parent location (if exists)
        File parentDir = getDataFolder();
        if (parentPath != null) {

            // Retrieve ig
            parentDir = new File(getDataFolder(), parentPath);

            // Create if doesnt exist
            if (!parentDir.exists()) {

                // Create
                parentDir.mkdirs();

                // Re-Load
                parentDir = new File(getDataFolder(), parentPath);
            }
        }

        // Now get that file. ParentDir is like supposed to be guaranteed to exist
        File keyFile = new File(parentDir, name);

        // Does file already exist
        if (!keyFile.exists()) {

            // Creates the directory (folder) structure
            keyFile.getParentFile().mkdirs();

            // Saves Default File
            if (exampleResource) {
                // Save
                saveResource(name, false);

                // Log
                theOots.CPLog("Config file \u00a73" + name + "\u00a77 not found, created new.");
            }

            //region Attempt to load again
            keyFile = new File(parentDir, name);
        }

        // Does the YML load?
        YamlConfiguration valueYML = new YamlConfiguration();
        try {

            // Valid YML
            valueYML.load(keyFile);

        // Invalid YML for OptiFine Glints
        } catch (IOException | InvalidConfigurationException e) {

            // Announce
            if (!expectInvalid) {
                theOots.CPLog("Parsing error encountered when loading \u00a7" + name + "\u00a77.");
                return null;
            }
        }

        return new FileConfigPair(keyFile, valueYML);
    }
    public ArrayList<FileConfigPair> GetConfigsAt(String parentPath) {
        // Make parent location (if exists)
        File parentDir = getDataFolder();
        if (parentPath != null) {

            // Retrieve ig
            parentDir = new File(getDataFolder(), parentPath);

            // Create if doesnt exist
            if (!parentDir.exists()) {

                // Create
                parentDir.mkdirs();

                // Re-Load
                parentDir = new File(getDataFolder(), parentPath);
            }
        }

        // Gather yml files
        File[] foundExtStructures = parentDir.listFiles( new ExtensionsFilter(new String[] {".yml"}));
        ArrayList<File> foundFiles = new ArrayList<>();

        // If found any file
        if (foundExtStructures != null) {

            // At least one I guess
            if (foundExtStructures.length >= 1) {

                // Literally adds all the files. Wow!
                Collections.addAll(foundFiles, foundExtStructures);
            }
        }

        // Which Custom Structures are valid?
        ArrayList<FileConfigPair> ret = new ArrayList<>();
        for(File csPath : foundFiles) {

            // Attempt to load each one of them
            try {
                // Valid YML
                YamlConfiguration csStorage = new YamlConfiguration();
                csStorage.load(csPath);

                // Success
                ret.add(new FileConfigPair(csPath, csStorage));

            // Invalid YML for OptiFine Glints
            } catch (IOException | InvalidConfigurationException e) {

                // Announce
                theOots.CPLog("Parsing error encountered when loading \u00a7c" + csPath.getName() + "\u00a77.");
            }
        }

        // Return those that parsed
        return ret;
    }

    public void SaveResourceSubDir(File dataFolder, String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {

            }
        } catch (IOException ex) {
        }
    }

    public boolean shutDown;
    @Override
    public void onDisable() {

        // Save config?
        saveConfig();

        // Save all files
        shutDown = true;

        // Boom saving time
        GOOPCManager.saveAllContents();
        GooPUnlockables.SaveAll();

        // Destroy minions
        SummonerClassUtils.RemoveAllMinions();

        FileSaveExecution();
        theOots.CPLog("All files saved.");

        // Plugin shutdown logic
        theOots.CPLog(ChatColor.RED + "Shut Down. Concluded.");
    }
    public static void EnableMMOItemShrubs() { usingMMOItemShrubs = true; }
    public static void EnableCrossbow2RocketLauncher() { usingXBow2RocketLauncher = true; }
}
