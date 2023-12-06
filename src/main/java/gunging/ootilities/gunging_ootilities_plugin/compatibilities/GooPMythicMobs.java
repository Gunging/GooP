package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.google.common.collect.Sets;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.GooP_FontUtils;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom.UCMCMIWarpTargeter;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom.UCMPortalCreateAura;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom.UCMRecipeUnlock;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom.UCMSilentTeleport;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.StrideMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.placeholders.PlaceholderManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@SuppressWarnings("unused")
public class GooPMythicMobs implements Listener {

    //region Versioning
    public GooPMythicMobs() {}

    public void CompatibilityCheck() { MythicMob mbb = null; }
    static int mmVer(@Nullable String numb) {if (!OotilityCeption.IntTryParse(numb)) { return -1; }return OotilityCeption.ParseInt(numb);}
    /**
     * @return Major MythicMobs version; ex the X in MythicMobs X.2.0
     */
    public static int getVersionMajor() { return versionMajor; }
    static int versionMajor = -2;

    /**
     * @return Minor MythicMobs version; ex the X in MythicMobs 5.X.0
     */
    public static int getVersionMinor() { return versionMinor; }
    static int versionMinor = -2;

    /**
     * @return MythicMobs build version; ex the X in MythicMobs 5.2.X
     */
    public static int getVersionBuild() { return versionBuild; }
    static int versionBuild = -2;

    /**
     * @return MythicMobs version
     */
    @NotNull public static String getVersion() { return MythicBukkit.inst().getVersion(); }

    /**
     * Parse the mythicmob version
     */
    public static void identifyVersion() {
        String[] ver = getVersion().split("\\.");
        if (ver.length > 0) { versionMajor = mmVer(ver[0]); }
        if (ver.length > 1) { versionMinor = mmVer(ver[1]); }
        if (ver.length > 2) { versionBuild = mmVer(ver[2]); }
    }
    //endregion

    @Nullable public static QuickNumberRange rangedDoubleToQNR(@Nullable String value) {
        if (value == null) { return null; }
        String[] split;

        Double min;
        Double max;

        // Range min..max
        if (value.contains("to")) {
            split = value.split("to");
            min = Double.valueOf(split[0]);
            max = Double.valueOf(split[1]);

            // Range min..max
        } else if (!value.startsWith("-") && value.contains("-")) {
            split = value.split("-");
            min = (double)Integer.parseInt(split[0]);
            max = (double)Integer.parseInt(split[1]);

            // Not a range
        } else {

            // X..
            String s;
            if (value.startsWith(">")) {
                s = value.substring(1);
                min = Double.valueOf(s);
                max = null;

                // ..X
            } else if (value.startsWith("<")) {
                s = value.substring(1);
                min = null;
                max = Double.valueOf(s);

                //  X
            } else {
                min = Double.valueOf(value);
                max = Double.valueOf(value);
            }
        }

        return new QuickNumberRange(min, max);
    }

    public static boolean newenOlden = true;
    @EventHandler public void OnRegisterCustomMechanics(MythicMechanicLoadEvent event) {
        CustomMechanic customMechanic = event.getContainer();
        String line = event.getConfig().getLine();
        MythicLineConfig mlc = event.getConfig();
        SkillExecutor skillExecutor = customMechanic.getManager();
        boolean mm52 = (getVersionMajor() == 5 && getVersionMinor() >= 2) || (getVersionMajor() > 5);

        // Switch Mechanic ig
        switch (event.getMechanicName().toLowerCase()) {
            case "recipeunlock":
            case "recipelock":
                event.register(mm52 ? new UCMRecipeUnlock(customMechanic, line, mlc) : new UCMRecipeUnlock(skillExecutor, line, mlc));
                break;
            case "silentteleport":
            case "stp":
                event.register(mm52 ? new UCMSilentTeleport(customMechanic, line, mlc) : new UCMSilentTeleport(skillExecutor, line, mlc));
                break;
            case "goopstride":
                event.register(mm52 ? new StrideMechanic(customMechanic, line, mlc) : new StrideMechanic(skillExecutor, line, mlc));
                break;
            case "onportalcreateg":
                event.register(mm52 ? new UCMPortalCreateAura(customMechanic, line, mlc) : new UCMPortalCreateAura(skillExecutor, line, mlc));
                break;
            case "vexcharging":
                event.register(mm52 ? new VexChargingMechanic(customMechanic, line, mlc) : new VexChargingMechanic(skillExecutor, line, mlc));
                break;
            case "effect:particleslash":
                event.register(mm52 ? new ParticleSlashEffect(customMechanic, line, mlc) : new ParticleSlashEffect(skillExecutor, line, mlc));
                break;
            case "mmodamage":

                // Replaces MythicLib MMODamage
                if (!Gunging_Ootilities_Plugin.foundMythicLib && !Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Register own MMODamage
                    event.register(mm52 ? new MMODamageReplacement(customMechanic, line, mlc) : new MMODamageReplacement(skillExecutor, line, mlc));
                }
                break;
            case "goopondamaged":
            case "gondamaged":
            case "ondamagedg":
                event.register(mm52 ? new OnDamagedAura(customMechanic, line, mlc) : new OnDamagedAura(skillExecutor, line, mlc));
                break;
            case "gooponshoot":
            case "gonshoot":
            case "onshootg":
                event.register(mm52 ? new OnShootAura(customMechanic, line, mlc) : new OnShootAura(skillExecutor, line, mlc));
                break;
            case "gooponattack":
            case "gonattack":
            case "onattackg":
                event.register(mm52 ? new OnAttackAura(customMechanic, line, mlc) : new OnAttackAura(skillExecutor, line, mlc));
                break;
            case "hideaura":
                event.register(mm52 ? new HideAura(customMechanic, line, mlc) : new HideAura(skillExecutor, line, mlc));
                break;
            case "rebootbreak":
                event.register(mm52 ? new RebootBreak(customMechanic, line, mlc) : new RebootBreak(skillExecutor, line, mlc));
                break;
            case "rebootrepair":
                event.register(mm52 ? new RebootRepair(customMechanic, line, mlc) : new RebootRepair(skillExecutor, line, mlc));
                break;
            case "goopminion":
                event.register(mm52 ? new MinionMechanic(customMechanic, line, mlc) : new MinionMechanic(skillExecutor, line, mlc));
                break;
            case "copycatequipment":
                event.register(mm52 ? new CopyCatEquipmentMechanic(customMechanic, line, mlc) : new CopyCatEquipmentMechanic(skillExecutor, line, mlc));
                break;
            case "goopsettrigger":
            case "goopastrigger":
                event.register(mm52 ? new AsTrigger(customMechanic, line, mlc) : new AsTrigger(skillExecutor, line, mlc));

                break;
            case "goopsetorigin":
            case "goopasorigin":
                event.register(mm52 ? new AsOrigin(customMechanic, line, mlc) : new AsOrigin(skillExecutor, line, mlc));
                break;
            case "goopdeferred":
                event.register(mm52 ? new Deferred(customMechanic, line, mlc) : new Deferred(skillExecutor, line, mlc));
                break;
            case "goopsummonminion":
            case "goopsummonminions":
                event.register(mm52 ? new SummonMinionMechanic(customMechanic, line, mlc) : new SummonMinionMechanic(skillExecutor, line, mlc));
                break;
            case "goopreleaseminion":
            case "goopreleaseminions":
                event.register(mm52 ? new MinionEmancipation(customMechanic, line, mlc) : new MinionEmancipation(skillExecutor, line, mlc));
                break;
            case "goopsudoowner":
                event.register(mm52 ? new SudoOwnerMechanic(customMechanic, line, mlc) : new SudoOwnerMechanic(skillExecutor, line, mlc));
                break;
            case "gooprally":
            case "rallyall":
                event.register(mm52 ? new RallyAll(customMechanic, line, mlc) : new RallyAll(skillExecutor, line, mlc));
                break;
            case "goopsudominions":
            case "goopsudominion":
                event.register(mm52 ? new SudoMinionsMechanic(customMechanic, line, mlc) : new SudoMinionsMechanic(skillExecutor, line, mlc));
                break;
            default: break;
        }
    }
    @EventHandler public void OnRegisterCustomConditions(MythicConditionLoadEvent event) {
        //MM//OotilityCeption.Log("\u00a7aConditions Load Event");
        //MM//OotilityCeption.Log("\u00a77Name: \u00a76" + event.getConditionName());
        //MM//OotilityCeption.Log("\u00a72Line: \u00a7e" + event.getConfig().getLine());
        //MM//OotilityCeption.Log("\u00a72Key: \u00a7e" + event.getConfig().getKey());
        //MM//OotilityCeption.Log("\u00a72FileName: \u00a7e" + event.getConfig().getFileName());
        //MM//OotilityCeption.Log("\u00a73Argument: \u00a7e" + event.getContainer().getConditionArgument());
        //MM//OotilityCeption.Log("\u00a73ActionVar: \u00a7e" + event.getContainer().getActionVar());

        // Switch Mechanic ig
        String nmae = event.getConditionName().toLowerCase();
        int s = event.getConditionName().indexOf(" "); if (s > 0) { nmae = nmae.substring(0, s); }
        switch (nmae) {
            case "distancefromorigin":
            case "origindistance":
            case "gooporigindistance":
                event.register(new DistanceFromOriginCondition(event.getConfig()));
                break;
            case "distancefromtrigger":
                event.register(new DistanceFromTriggerCondition(event.getConfig()));
                break;
            case "isvexcharging":
            case "ischargingvex":
                event.register(new VexChargingCondition(event.getConfig()));
                break;
            case "gooplayer":
            case "goopadmin":
                event.register(new AdminCondition(event.getConfig()));
                break;
            case "canpvp":
            case "canpve":
            case "canattack":
                event.register(new CanPvPCondition(event.getConfig()));
                break;
            case "goopvelocity":
            case "goopmotion":
                event.register(new AbsoluteMotionCondition(event.getConfig()));
                break;
            case "isminion":
            case "isgoopminion":
                event.register(new GooPMinionCondition(event.getConfig()));
                break;
            case "goopunlockable":
            case "unlockable":
                event.register(new UnlockableCondition(event.getConfig()));
                break;
            default: break;
        }
    }
    @EventHandler public void OnRegisterCustomTargeters(MythicTargeterLoadEvent event) {
        SkillExecutor exec = event.getContainer().getManager();
        MythicLineConfig mlc = event.getConfig();

        // Switch Mechanic ig
        switch (event.getTargeterName().toLowerCase()) {
            case "goopstructurecore":
            case "goopcore":
                event.register(new StructureCoreTargeter(exec, mlc));
                break;
            case "goopminions":
            case "goopminion":
                event.register(new MinionsTargeter(exec, mlc));
                break;
            case "goopowner":
                event.register(new MinionsOwnerTargeter(exec, mlc));
                break;
            case "goopscore":
                event.register(new ScoreboardTargeter(exec, mlc));
                break;
            case "cmiwarp":
                if (Gunging_Ootilities_Plugin.foundCMI ||
                    Gunging_Ootilities_Plugin.foundEssentials) { event.register(new UCMCMIWarpTargeter(exec, mlc)); }
                break;
            case "gooptag":
                event.register(new TagTargeter(exec, mlc));
                break;
            case "locationsinslash":
            case "slashlocations":
                // if (GOOPCManager.isLocationsInSlash()) { event.register(new LocationsInSlash(exec, mlc)); }
                event.register(new LocationsInSlash(exec, mlc));
                break;
            case "entitiesinslash":
            case "slashentities":
                //if (GOOPCManager.isEntitiesInSlash()) { event.register(new EntitiesInSlash(exec, mlc)); }
                event.register(new EntitiesInSlash(exec, mlc));
                break;
            default: break;
        }
    }
    @EventHandler public void OnMMReload(MythicReloadedEvent event) {

        // Re-register I guess
        RegisterPlaceholders(Gunging_Ootilities_Plugin.foundMMOItems);

        // Reload Timers I Suppose
        ReloadPlayerOnTimers();
    }

    public static ArrayList<String> GetMythicMobTypes() {

        ArrayList<String> ret = new ArrayList<>();

        for (MythicMob mb : MythicBukkit.inst().getMobManager().getMobTypes()) { ret.add(mb.getInternalName()); }

        return ret;
    }
    public static ArrayList<String> GetMythicItemTypes() {

        return new ArrayList<>(MythicBukkit.inst().getItemManager().getItemNames());
    }

    @NotNull public static HashMap<Integer, Skill> playerOnTimers = new HashMap<>();
    public static void ReloadPlayerOnTimers() {
        playerOnTimers.clear();

        // If there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.playerOnTicks != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.playerOnTicks;
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Log da shit
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()){

                // Get LPH Name
                String tName = val.getKey();

                // Get Absolute List
                List<String> rawList = ofgStorage.getStringList(tName);

                // Accept sensible entries
                for (String onTim : rawList) {

                    // Split by spaces
                    if (!onTim.contains(" ")) { continue; }
                    String[] split = onTim.split(" ");

                    // Try parse integer
                    if (OotilityCeption.IntTryParse(split[1])) {

                        // Parse integer
                        Integer ticks = OotilityCeption.ParseInt(split[1]);

                        // Find skill
                        Skill tSkill = GooPMythicMobs.GetSkill(split[0]);

                        // Validated?
                        if (tSkill != null) {

                            // Yea
                            playerOnTimers.put(ticks, tSkill);

                        // Gruno
                        } else {

                            // Notify
                            Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("Player ~OnTimer","Error when loading Player ~OnTimer \u00a7b" + onTim + "\u00a77: Skill does not exist \u00a7e" + split[0]));
                        }

                    // Gruno
                    } else {

                        // Notify
                        Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("Player ~OnTimer","Error when loading Player ~OnTimer \u00a7b" + onTim + "\u00a77: Ticks must be an integer number, not \u00a7e" + split[1]));
                    }
                }
            }
        }

        // Attempt startup
        GooPMythicMobs.startupOnTimers();
    }

    public static boolean startedOnTimers = false;
    public static void  startupOnTimers() {
        if (startedOnTimers) { return; }
        startedOnTimers = true;

        // Funny delayed startup
        (new BukkitRunnable() { public void run() { GooPMythicMobs.runOnTimers(); } }).runTaskTimer(Gunging_Ootilities_Plugin.theMain, 1L, 10L);
    }

    public static void runOnTimers() {

        // Simple eh
        for (Map.Entry<Integer, Skill> pot : playerOnTimers.entrySet()) {

            // If tick divisible by
            if (Gunging_Ootilities_Plugin.getCurrentTick() % pot.getKey() == 0) {

                // Run
                for (Player p : Bukkit.getOnlinePlayers()) {

                    // Run skill as player
                    ExecuteMythicSkillAs(pot.getValue(), p, null, null, null, null, null);
                }
            }
        }
    }

    public static void ReloadListPlaceholders(OotilityCeption oots) {

        // If there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.listPlaceholderPair != null) {

            // Clear lists
            ListPlaceholder.loadedListPlaceholders = new HashMap<>();

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.listPlaceholderPair;
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Log da shit
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()){

                // Get LPH Name
                String tName = val.getKey();

                // If not yet registered
                if (ListPlaceholder.Get(tName) == null) {

                    // Get Absolute List
                    List<String> rawList = ofgStorage.getStringList(tName);

                    // If long enough
                    if (rawList.size() > 0) {

                        // Create
                        ListPlaceholder nLPH = new ListPlaceholder(tName, new ArrayList<>(rawList));

                        // Load
                        ListPlaceholder.Load(nLPH);

                    } else {

                        // Notify
                        oots.CLog(OotilityCeption.LogFormat("List Placeholders","Error when loading LPH '\u00a73" + tName + "\u00a77': This list is empty"));
                    }

                } else {

                    // Notify
                    oots.CLog(OotilityCeption.LogFormat("List Placeholders","Error when loading LPH '\u00a73" + tName + "\u00a77': There is already an LPH with that name!"));
                }
            }
        }

        // Reload Timers I Suppose
        ReloadPlayerOnTimers();
    }
    public static void RegisterPlaceholders(boolean withMMOItems) {
        PlaceholderManager phm = MythicBukkit.inst().getPlaceholderManager();

        // Register OnApply Placeholder
        phm.register("goop.slot", MMPHSlot.getInst());

        // Register Bow Draw Placeholder
        phm.register("goop.bowdraw", MMPHBowdraw.getInst());

        // List placeholders
        phm.register("goop.ordered", MMPHOrdered.getInst());

        // List placeholders
        phm.register("goop.random", MMPHRandom.getInst());

        // Projectile oriented ones
        phm.register("goop.projectile", MMPHProjectile.getInst());

        // List placeholders
        phm.register("goop.font", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing font code}"; }

            // Get Code
            String code = GooP_FontUtils.CodeFrom(arg);

            // Wasit?
            if (code != null) { return code; }
            else { return "{invalid code}"; }
        }));

        // List placeholders
        phm.register("goop.dynamic", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing dynamic code}"; }

            // As numeric?
            String defaultRet = "";
            int dotLoc = arg.indexOf(".");
            if (dotLoc > 0) {
                defaultRet = arg.substring(0, dotLoc);
                arg = arg.substring(dotLoc + 1); }

            // Get From Caster
            String value = ValueFromDynamic(metadata.getCaster().getEntity().getUniqueId(), arg);
            if (value == null) { value = defaultRet; }

            // Return thay
            return value;

        }));

        // List placeholders
        phm.register("goop.owner", Placeholder.meta((metadata, arg) -> {

            // Attempt to get owner
            Entity tPlayer = SummonerClassUtils.GetOwner(metadata.getCaster().getEntity().getUniqueId());
            if (tPlayer == null) { return "null"; }

            // Return their ID easy alv
            if (arg == null) { return tPlayer.getUniqueId().toString(); }

            // Get From Caster
            switch (arg) {
                case "name": return tPlayer.getName();
                case "distance": return String.valueOf(tPlayer.getLocation().distance(metadata.getCaster().getEntity().getBukkitEntity().getLocation()));
                case "x": return String.valueOf(tPlayer.getLocation().getX());
                case "y": return String.valueOf(tPlayer.getLocation().getY());
                case "z": return String.valueOf(tPlayer.getLocation().getZ());
                case "w": return String.valueOf(tPlayer.getLocation().getWorld());
                case "health": return (tPlayer instanceof LivingEntity) ? String.valueOf(((LivingEntity) tPlayer).getHealth()) : "0";
                case "max_health": return (tPlayer instanceof LivingEntity) ? String.valueOf(((LivingEntity) tPlayer).getAttribute(Attribute.GENERIC_MAX_HEALTH)) : "0";
            }

            // Return thay
            return tPlayer.getUniqueId().toString();

        }));

        // Owner PAPI
        phm.register("goop.ownerpapi", Placeholder.meta((metadata, arg) -> {
            // If valid
            if (arg == null) { return "{missing placeholder name}"; }
            if (!Gunging_Ootilities_Plugin.foundPlaceholderAPI) { return "00.000"; }

            // Get Player
            Entity tPlayer = SummonerClassUtils.GetOwner(metadata.getCaster().getEntity().getUniqueId());

            if (tPlayer != null) {

                // Not a player
                if (tPlayer instanceof Player) {

                    // A value to return
                    return OotilityCeption.RemoveDecimalZeros(GooPPlaceholderAPI.Parse((Player) tPlayer, "%" + arg + "%"));

                    // Zero I guess
                } else { return "000"; }

            } else { return "0000"; }

        }));

        // List placeholders
        phm.register("goop.castermmostat", MMPHMMOStat.getInst(MMPHMMOStatTarget.CASTER));
        phm.register("goop.triggermmostat", MMPHMMOStat.getInst(MMPHMMOStatTarget.TRIGGER));
        phm.register("goop.ownermmostat", MMPHMMOStat.getInst(MMPHMMOStatTarget.OWNER));

        // With McMMO?
        if (Gunging_Ootilities_Plugin.foundMCMMO) {
            phm.register("goop.castermcmmostat", MMPHMMOStat.getInst(MMPHMMOStatTarget.CASTER));
            phm.register("goop.triggermcmmostat", MMPHMMOStat.getInst(MMPHMMOStatTarget.TRIGGER));
            phm.register("goop.ownermcmmostat", MMPHMMOStat.getInst(MMPHMMOStatTarget.OWNER));
        }
    }

    /**
     * @param name Name of the mythic item in files
     * @return The mythic item of this name if it exists
     */
    @Nullable public static ItemStack getMythicItem(@Nullable String name) {
        if (name == null) { return null; }

        // All right
        Optional<MythicItem> hasMythicItem = MythicBukkit.inst().getItemManager().getItem(name);

        // Cancel present
        if (!hasMythicItem.isPresent()) { return null; }

        // Yeah just that I guess
        return ((BukkitItemStack)((MythicItem)hasMythicItem.get()).generateItemStack(1)).build();
    }
    @NotNull public static final String MYTHIC_TYPE = "MYTHIC_TYPE";
    public static boolean isMythicItem(@Nullable ItemStack stack) {

        // If exists
        if (stack == null) { return false; }

        // Un parse it
        CompoundTag ct = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(stack);

        // Yo is that a mythic item?
        return ct.containsKey(MYTHIC_TYPE);
    }
    @Nullable public static String getMythicType(@Nullable ItemStack stack) {

        // If exists
        if (stack == null) { return null; }

        // Un parse it
        CompoundTag ct = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(stack);

        // Yo is that a mythic item?
        return ct.getString(MYTHIC_TYPE);
    }

    // By caster and by code
    static HashMap<UUID, HashMap<String, String>> dynamicCodes = new HashMap<>();
    public static void RegisterDynamicCode(@NotNull UUID pertaint, @NotNull CompactCodedValue codedValue) { RegisterDynamicCode(pertaint, codedValue.getID(), codedValue.getValue());}
    public static void RegisterDynamicCode(@NotNull UUID pertaint, @NotNull String id, @NotNull String value) {

        // Make sure its nonull
        dynamicCodes.computeIfAbsent(pertaint, k -> new HashMap<>());

        // Get Thay Array
        HashMap<String, String> codes = dynamicCodes.get(pertaint);

        // Put lol
        codes.put(id, value);
    }

    /**
     * Gets a value sored by a code, per UUID of caster of skill.
     * @return NULL if no value is registered.
     */
    @Nullable public static String ValueFromDynamic(@NotNull UUID pertaint, @Nullable String code) {
        // Get Thay Array
        HashMap<String, String> codes = dynamicCodes.get(pertaint);

        // If exists
        if (codes != null) {

            // Return thay
            return codes.get(code);
        }

        // Nope
        return null;
    }
    /**
     * Returns the Entity if it was successfuly spawned.
     */
    @Nullable public static Entity SpawnMythicMob(@Nullable String name, @Nullable Location location) {
        return SpawnMythicMob(name, location, 1.0D);
    }
    @Nullable public static Entity SpawnMythicMob(@Nullable String name, @Nullable Location location, double level) {
        if (name == null) { return null; }
        if (location == null) { return null; }

        if (IsMythicMobLoaded(name)) {

            // Get Manager
            MobExecutor mm = MythicBukkit.inst().getMobManager();

            if (mm == null) { return null; }

            // Spawn mob?
            ActiveMob mob = mm.spawnMob(name, location, level);

            if (mob == null) { return null; }

            AbstractEntity abs = mob.getEntity();

            if (abs == null) { return null; }

            // Return I guess?
            return abs.getBukkitEntity();

        } else {
            //DBG//OotilityCeption.Log("\u00a7cMythicMob Not Loaded:\u00a77 " + name);
            return null;
        }
    }
    public static boolean IsMythicMobLoaded(@Nullable String name) {
        if (name == null) { return false; }

        // Try
        Optional<MythicMob> mobtest = MythicBukkit.inst().getMobManager().getMythicMob(name);

        // Try
        return mobtest.isPresent();
    }
    public static Boolean IsMythicMobOfInternalID(Entity targetEntity, String mythicmobName, RefSimulator<String> logger) {

        // CHeck that it is a mythicmob to begin with
        if (IsMythicMob(targetEntity)) {

            // Just do thay
            if (MythicBukkit.inst().getAPIHelper().getMythicMobInstance(targetEntity).getType().getInternalName().equals(mythicmobName)) {

                // Success
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Entity is indeed a MythicMob instance of \u00a7e" + mythicmobName + "\u00a77!");

                // Yes
                return true;

            // No lol
            } else {
                // Success
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Entity is a MythicMob, but not an instance of \u00a7e" + mythicmobName);

                // No
                return false;
            }

        } else {

            // Log Return
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Such entity is not a mythicmob, thus it is not an instance of \u00a7e" + mythicmobName);

            // Cant be such MythicMob Internal ID cuz its not a MythicMob to begin with.
            return false;
        }
    }
    public static Boolean IsMythicMob(Entity targetEntity) {

        // Solid yes or no
        return MythicBukkit.inst().getAPIHelper().isMythicMob(targetEntity);
    }
    public static Boolean GraveyardsRespawnSkill(String skillName, Location loc, Player pl) {
        Optional mSkillFk = MythicBukkit.inst().getSkillManager().getSkill(skillName);

        // Is there a skill of that name?
        if (mSkillFk.isPresent()) {

            try {
                // Ok then retrieve the skill
                Skill mSkill = (Skill) mSkillFk.get();

                // Git Player 2.0
                AbstractPlayer skPlayer = BukkitAdapter.adapt(pl);
                GenericCaster skCaster = new GenericCaster(skPlayer);

                // Git Location 2.0
                AbstractLocation skLocation = BukkitAdapter.adapt(loc);

                // Some UUID I suppose
                HashSet skHash = Sets.newHashSet();
                skHash.add(skPlayer);

                // Cast!
                mSkill.execute(SkillTriggers.API, skCaster, skPlayer, skLocation, skHash, (HashSet)null, 1);

                // Success
                return true;

            } catch (Exception e) {

                // RIP
                return false;
            }

        // The skill was not found
        } else {

            // False means the skill does not exist.
            return false;
        }
    }
    public static boolean SkillExists(String skillName) {
        // If null no
        if (skillName == null) { return false; }

        Optional<Skill> mSkillFk = MythicBukkit.inst().getSkillManager().getSkill(skillName);

        // Is there a skill of that name?
        if (mSkillFk.isPresent()) {

            try {
                // Ok then retrieve the skill
                Skill mSkill = (Skill) mSkillFk.get();

                // Success
                return true;

            } catch (Exception e) {

                // RIP
                return false;
            }

            // The skill was not found
        } else {

            // False means the skill does not exist.
            return false;
        }
    }
    public static Skill GetSkill(String skillName) {

        if (SkillExists(skillName)) {

            Optional<Skill> mSkillFk = MythicBukkit.inst().getSkillManager().getSkill(skillName);
            if (mSkillFk == null) { return null; }
            if (mSkillFk.isPresent()) { return mSkillFk.get(); }
        }

        return null;
    }

    @Nullable public static Double modifyVariable(@Nullable Entity mob, @NotNull String variable, @Nullable PlusMinusPercent operation) {

        // No variables no service
        VariableRegistry reg;
        if (mob == null) {
            reg = MythicBukkit.inst().getVariableManager().getGlobalRegistry();
        } else {
            reg = MythicBukkit.inst().getVariableManager().getRegistry(VariableScope.CASTER, BukkitAdapter.adapt(mob));}
        if (reg == null) { return null; }

        // Unset operation
        if (operation == null) {
            reg.remove(variable);
            return null; }

        // No operation
        if (operation.isNeutral()) {

            // Return value
            if (!reg.has(variable)) { return null; }
            return (double) reg.getFloat(variable);
        }

        // If this variable is not set
        if (!reg.has(variable)) {

            // Operation must not be a percent nor relative
            if (!operation.isPercent() && !operation.getRelative()) {
                double val = operation.apply(0D);

                // Set variable and accept
                reg.put(variable, Variable.ofType(VariableType.FLOAT, val));
                return val;

            } else {

                // No change could be made
                return null;
            }
        }

        // Apply operation
        double val = operation.apply((double) reg.getFloat(variable));
        reg.put(variable, Variable.ofType(VariableType.FLOAT, val));
        return val;
    }
    @NotNull public static ArrayList<String> getGlobalVariables() {
        VariableRegistry reg = MythicBukkit.inst().getVariableManager().getGlobalRegistry();
        if (reg == null) { return new ArrayList<>(); }
        return new ArrayList<>(reg.asMap().keySet());
    }

    //region Execute Skill As Family
    public static Boolean ExecuteMythicSkillAs(String skillName, @NotNull  Entity caster) {
        return ExecuteMythicSkillAs(skillName, caster, null, null, caster.getLocation(), null);
    }
    public static Boolean ExecuteMythicSkillAs(String skillName, @NotNull  Entity caster, Entity trigger, Location origin) {
        return ExecuteMythicSkillAs(skillName, caster, trigger, null, origin);
    }
    public static Boolean ExecuteMythicSkillAs(String skillName, @NotNull  Entity caster, Entity trigger, Entity target) {
        return ExecuteMythicSkillAs(skillName, caster, trigger, target, null);
    }
    public static Boolean ExecuteMythicSkillAs(String skillName, @NotNull  Entity caster, Entity trigger, Entity target, ArrayList<CompactCodedValue> vars) {
        // Inklude sole target
        ArrayList<Entity> targts = new ArrayList<>();
        targts.add(target);

        // Run thoise
        return ExecuteMythicSkillAs(skillName, caster, trigger, targts, caster.getLocation(), vars);
    }
    public static Boolean ExecuteMythicSkillAs(String skillName, Entity caster, Entity trigger, ArrayList<Entity> targets, Location origin) { return ExecuteMythicSkillAs(skillName, caster, trigger, targets, origin, null); }
    public static Boolean ExecuteMythicSkillAs(@Nullable String skillName, @Nullable Entity caster, @Nullable Entity trigger, @Nullable ArrayList<Entity> targets, @Nullable Location origin, @Nullable ArrayList<CompactCodedValue> vars) {

        return ExecuteMythicSkillAs(skillName, caster, trigger, targets, null, origin, vars);
    }
    public static Boolean ExecuteMythicSkillAs(@Nullable String skillName, @Nullable Entity caster, @Nullable Entity trigger, @Nullable ArrayList<Entity> entityTargets, @Nullable ArrayList<Location> locationTargets, @Nullable Location origin, @Nullable ArrayList<CompactCodedValue> vars) {

        // Retrieve the skill
        Skill mSkill = GetSkill(skillName);
        if (mSkill == null) { return false; }

        // Proceed as normal
        return ExecuteMythicSkillAs(mSkill, caster, trigger, entityTargets, locationTargets, origin, vars);
    }
    public static Boolean ExecuteMythicSkillAs(@NotNull Skill mSkill, @Nullable Entity caster, @Nullable Entity trigger, @Nullable ArrayList<Entity> entityTargets, @Nullable ArrayList<Location> locationTargets, @Nullable Location origin, @Nullable ArrayList<CompactCodedValue> vars) {

        // Fix vars
        if (vars == null) { vars = new ArrayList<>(); }

        if (caster == null) { return false; }

        // Foreach Compact Code
        for (CompactCodedValue ccv : vars) {

            // Register for caster lol
            if (ccv != null) { RegisterDynamicCode(caster.getUniqueId(), ccv); }
        }

        // Caster - Adapt Player
        AbstractEntity skCaEntity = BukkitAdapter.adapt(caster);
        GenericCaster skCaster = new GenericCaster(skCaEntity);
        AbstractLocation skCaLocation = BukkitAdapter.adapt(caster.getLocation());

        // Trigger - Adapt Item
        AbstractEntity skTrEntity = null;
        if (trigger != null) { skTrEntity = BukkitAdapter.adapt(trigger); } else { skTrEntity = skCaEntity; }

        // Origin - Adapt Item Location
        AbstractLocation skLocation = null;
        if (origin != null) { skLocation = BukkitAdapter.adapt(origin); } else { skLocation = skCaLocation; }

        // Some UUID I suppose
        HashSet<AbstractEntity> skHash = Sets.newHashSet();
        HashSet<AbstractLocation> skHashL = Sets.newHashSet();
        if (entityTargets != null) {
            if (entityTargets.size() > 0) {

                // Add every target
                for (Entity ent : entityTargets) {

                    // If non-null
                    if (ent != null) {

                        // Add entity and its location
                        skHash.add(BukkitAdapter.adapt(ent));
                    }
                }
            }
        }

        if (locationTargets != null) {
            if (locationTargets.size() > 0) {

                // Add every target
                for (Location ent : locationTargets) {

                    // If non-null
                    if (ent != null) {

                        // Add entity and its location
                        skHashL.add(BukkitAdapter.adapt(ent));
                    }
                }
            }
        }

        // UUUuuuh idk what happens if targets size is 0
        if (skHash.size() == 0) {

            // Targets is only the player
            skHash.add(skCaEntity);
        }

        // Cast!
        mSkill.execute(SkillTriggers.API, skCaster, skTrEntity, skLocation, skHash, skHashL, 1);

        return true;

    }
    //endregion
}