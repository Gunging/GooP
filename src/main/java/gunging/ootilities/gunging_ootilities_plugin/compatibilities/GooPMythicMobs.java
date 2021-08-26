package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.google.common.collect.Sets;
import gunging.ootilities.gunging_ootilities_plugin.GungingOotilities;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.GooP_FontUtils;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
import gunging.ootilities.gunging_ootilities_plugin.misc.CompactCodedValue;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.ListPlaceholder;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.*;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicReloadedEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.skills.Skill;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;
import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GooPMythicMobs implements Listener {

    public GooPMythicMobs() { }

    @SuppressWarnings("unused")
    public void CompatibilityCheck() { MythicMob mbb = null; }

    public static ArrayList<String> GetMythicMobTypes() {

        ArrayList<String> ret = new ArrayList<>();

        for (MythicMob mb : MythicMobs.inst().getMobManager().getMobTypes()) { ret.add(mb.getInternalName()); }

        return ret;
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
    }

    public static void RegisterPlaceholders(boolean withMMOItems) {

        // Register OnApply Placeholder
        MythicMobs.inst().getPlaceholderManager().register("goop.slot", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing slot name}"; }

            // For now, only <goop.slot.provided> is valid
            if (arg.toLowerCase().equals("provided")) {

                if (GungingOotilities.providedSlot.containsKey(metadata.getCaster().getEntity().getUniqueId())) {

                    return String.valueOf(GungingOotilities.providedSlot.get(metadata.getCaster().getEntity().getUniqueId()));

                } else { return "Invalid Entity"; }

            } else { return "Invalid Slot"; }
            // Ok
        }));

        // Register Bow Draw Placeholder
        MythicMobs.inst().getPlaceholderManager().register("goop.bowdraw", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing caster/trigger arg}"; }

            // <goop.bowdraw.caster> To get last amount of bow drawin the caster did
            if (arg.toLowerCase().equals("caster")) {

                // Get or default
                Float f = XBow_Rockets.bowDrawForce.get(metadata.getCaster().getEntity().getUniqueId());
                if (f == null) { f = 0F; }

                return OotilityCeption.RemoveDecimalZeros(String.valueOf(f));

                // <goop.bowdraw.trigger> To get last amount of bow drawing the trigger did
            } else if (arg.toLowerCase().equals("trigger")) {

                if (XBow_Rockets.bowDrawForce.containsKey(metadata.getTrigger().getUniqueId())) {

                    return OotilityCeption.RemoveDecimalZeros(String.valueOf(XBow_Rockets.bowDrawForce.get(metadata.getCaster().getEntity().getUniqueId())));

                } else { return "00"; }

            } else { return "000"; }
        }));

        // List placeholders
        MythicMobs.inst().getPlaceholderManager().register("goop.ordered", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing list name}"; }

            // Strip ordered index
            int lastDot = arg.lastIndexOf('.');
            int orderedIndex = 0;

            // If there was a dot
            if (lastDot > 0 && arg.length() > (lastDot + 1)) {

                // Crop
                String postdot = arg.substring(lastDot + 1);

                // Does it parse
                if (OotilityCeption.IntTryParse(postdot)) {

                    // Store ordered index
                    orderedIndex = OotilityCeption.ParseInt(postdot);

                    // Crop
                    arg = arg.substring(0, lastDot);
                }
            }

            // Get List
            ListPlaceholder lph = ListPlaceholder.Get(arg);

            // Did it exist?
            if (lph != null) {

                // Well return the next balue-yo!
                return lph.NextListItem(orderedIndex);

            } else {

                // Invalid list
                return "Invalid List of Name '" + arg + "'";
            }

        }));

        // List placeholders
        MythicMobs.inst().getPlaceholderManager().register("goop.random", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing list name}"; }

            // Get List
            ListPlaceholder lph = ListPlaceholder.Get(arg);

            // Did it exist?
            if (lph != null) {

                // Well return the next balue-yo!
                return lph.RandomListItem();

            } else {

                // Invalid list
                return "Invalid List of Name '" + arg + "'";
            }


        }));

        // List placeholders
        MythicMobs.inst().getPlaceholderManager().register("goop.font", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing font code}"; }

            // Get Code
            String code = GooP_FontUtils.CodeFrom(arg);

            // Wasit?
            if (code != null) { return code; }
            else { return "{invalid code}"; }
        }));

        // List placeholders
        MythicMobs.inst().getPlaceholderManager().register("goop.dynamic", Placeholder.meta((metadata, arg) -> {

            // If valid
            if (arg == null) { return "{missing dynamic code}"; }

            // Get From Caster
            String value = ValueFromDynamic(metadata.getCaster().getEntity().getUniqueId(), arg);
            if (value == null) { value = ""; }

            // Return thay
            return value;

        }));

        // List placeholders
        MythicMobs.inst().getPlaceholderManager().register("goop.owner", Placeholder.meta((metadata, arg) -> {

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

        MythicMobs.inst().getPlaceholderManager().register("goop.ownerpapi", Placeholder.meta((metadata, arg) -> {
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

        // Ok so iof mmoitems loaded
        if (withMMOItems) {

            //region Register Cummulative Placeholders
            MythicMobs.inst().getPlaceholderManager().register("goop.castermmostat", Placeholder.meta((metadata, arg) -> {
                // If valid
                if (arg == null) { return "{missing mmostat name}"; }

                // Get Player
                Player tPlayer = Bukkit.getPlayer(metadata.getCaster().getEntity().getUniqueId());

                if (tPlayer != null) {

                    // A value to return
                    Double result = GooPMMOLib.CDoubleStat(tPlayer, arg);

                    // Adjust
                    if (result == null) { return "00"; }

                    return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

                } else { return "0000"; }

            }));

            MythicMobs.inst().getPlaceholderManager().register("goop.ownermmostat", Placeholder.meta((metadata, arg) -> {
                // If valid
                if (arg == null) { return "{missing mmostat name}"; }

                // Get Player
                Entity tPlayer = SummonerClassUtils.GetOwner(metadata.getCaster().getEntity().getUniqueId());

                if (tPlayer != null) {

                    // Not a player
                    if (tPlayer instanceof Player) {

                        // A value to return
                        Double result = GooPMMOLib.CDoubleStat((Player) tPlayer, arg);

                        // Adjust
                        if (result == null) { return "00"; }

                        return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

                    // Zero I guess
                    } else { return "000"; }

                } else { return "0000"; }

            }));

            MythicMobs.inst().getPlaceholderManager().register("goop.triggermmostat", Placeholder.meta((metadata, arg) -> {
                // If valid
                if (arg == null) { return "{missing mmostat name}"; }

                // Get Player
                Player tPlayer = Bukkit.getPlayer(metadata.getTrigger().getUniqueId());

                if (tPlayer != null) {

                    // A value to return
                    Double result = GooPMMOLib.CDoubleStat(tPlayer, arg);

                    // Adjust
                    if (result == null) { return "00"; }

                    return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

                } else { return "0.¿000"; }

            }));
            //endregion
        }

        // With McMMO?
        if (Gunging_Ootilities_Plugin.foundMCMMO) {

            MythicMobs.inst().getPlaceholderManager().register("goop.castermcmmostat", Placeholder.meta((metadata, arg) -> {
                // If valid
                if (arg == null) { return "{missing mcmmostat name}"; }

                // Get Player
                Player tPlayer = Bukkit.getPlayer(metadata.getCaster().getEntity().getUniqueId());

                if (tPlayer != null) {

                    // A value to return
                    Double result = GooPMCMMO.MCMMODoubleStat(tPlayer, arg);

                    // Adjust
                    if (result == null) { return "00"; }

                    return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

                } else { return "0000"; }

            }));

            MythicMobs.inst().getPlaceholderManager().register("goop.ownermcmmostat", Placeholder.meta((metadata, arg) -> {
                // If valid
                if (arg == null) { return "{missing mcmmostat name}"; }

                // Get Player
                Entity tPlayer = SummonerClassUtils.GetOwner(metadata.getCaster().getEntity().getUniqueId());

                if (tPlayer != null) {

                    if (tPlayer instanceof Player) {

                        // A value to return
                        Double result = GooPMCMMO.MCMMODoubleStat((Player) tPlayer, arg);

                        // Adjust
                        if (result == null) { return "00"; }

                        return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

                    } else { return "000"; }

                } else { return "0000"; }

            }));

            MythicMobs.inst().getPlaceholderManager().register("goop.triggermcmmostat", Placeholder.meta((metadata, arg) -> {
                // If valid
                if (arg == null) { return "{missing mcmmostat name}"; }

                // Get Player
                Player tPlayer = Bukkit.getPlayer(metadata.getTrigger().getUniqueId());

                if (tPlayer != null) {

                    // A value to return
                    Double result = GooPMCMMO.MCMMODoubleStat(tPlayer, arg);

                    // Adjust
                    if (result == null) { return "00"; }

                    return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

                } else { return "0000"; }

            }));
        }
    }

    /**
     * @param name Name of the mythic item in files
     * @return The mythic item of this name if it exists
     */
    @Nullable public static ItemStack getMythicItem(@Nullable String name) {
        if (name == null) { return null; }

        // All right
        Optional<MythicItem> hasMythicItem = MythicMobs.inst().getItemManager().getItem(name);

        // Cancel present
        if (!hasMythicItem.isPresent()) { return null; }

        // Yeah just that I guess
        return ((BukkitItemStack)((MythicItem)hasMythicItem.get()).generateItemStack(1)).build();
    }

    @EventHandler
    public void OnRegisterCustomMechanics(MythicMechanicLoadEvent event) {

        // Switch Mechanic ig
        switch (event.getMechanicName().toLowerCase()) {
            case "effect:particleslash":
                event.register(new ParticleSlashEffect(event.getContainer().getConfigLine(), event.getConfig()));
                break;
            case "goopondamaged":
            case "gondamaged":
            case "ondamagedg":
                event.register(new OnDamagedAura(event.getContainer().getConfigLine(), event.getConfig()));
                break;
            case "gooponshoot":
            case "gonshoot":
            case "onshootg":
                event.register(new OnShootAura(event.getContainer().getConfigLine(), event.getConfig()));
                break;
            case "gooponattack":
            case "gonattack":
            case "onattackg":
                event.register(new OnAttackAura(event.getContainer().getConfigLine(), event.getConfig()));
                break;
            case "goopminion":
                event.register(new MinionMechanic(event.getContainer(), event.getConfig()));
                break;
            case "copycatequipment":
                event.register(new CopyCatEquipmentMechanic(event.getContainer(), event.getConfig()));
                break;
            case "goopsettrigger":
            case "goopastrigger":
                event.register(new AsTrigger(event.getContainer(), event.getConfig()));
                break;
            case "goopsetorigin":
            case "goopasorigin":
                event.register(new AsOrigin(event.getContainer(), event.getConfig()));
                break;
            case "goopsummonminion":
            case "goopsummonminions":
                event.register(new SummonMinionMechanic(event.getContainer(), event.getConfig()));
                break;
            case "goopreleaseminion":
            case "goopreleaseminions":
                event.register(new MinionEmancipation(event.getContainer(), event.getConfig()));
                break;
            case "goopsudoowner":
                event.register(new SudoOwnerMechanic(event.getContainer(), event.getConfig()));
                break;
            case "gooprally":
            case "rallyall":
                event.register(new RallyAll(event.getContainer(), event.getConfig()));
                break;
            case "goopsudominions":
            case "goopsudominion":
                event.register(new SudoMinionsMechanic(event.getContainer(), event.getConfig()));
                break;
            default: break;
        }
    }
    @EventHandler
    public void OnRegisterCustomConditions(MythicConditionLoadEvent event) {
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
            case "gooplayer":
            case "goopadmin":
                event.register(new AdminCondition(event.getConfig()));
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
    @EventHandler
    public void OnRegisterCustomTargeters(MythicTargeterLoadEvent event) {

        // Switch Mechanic ig
        switch (event.getTargeterName().toLowerCase()) {
            case "goopminions":
            case "goopminion":
                event.register(new MinionsTargeter(event.getConfig()));
                break;
            case "goopowner":
                event.register(new MinionsOwnerTargeter(event.getConfig()));
                break;
            case "goopscore":
                event.register(new ScoreboardTargeter(event.getConfig()));
                break;
            case "gooptag":
                event.register(new TagTargeter(event.getConfig()));
                break;
            default: break;
        }
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
    @Nullable
    public static String ValueFromDynamic(@NotNull UUID pertaint, @Nullable String code) {
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

    @EventHandler
    public void OnMMReload(MythicReloadedEvent event) {

        // Re-register I guess
        RegisterPlaceholders(Gunging_Ootilities_Plugin.foundMMOItems);
    }

    /**
     * Returns the Entity if it was successfuly spawned.
     */
    @Nullable
    public static Entity SpawnMythicMob(@Nullable String name, @Nullable Location location) {
        if (name == null) { return null; }
        if (location == null) { return null; }

        if (IsMythicMobLoaded(name)) {

            // Get Manager
            MobManager mm = MythicMobs.inst().getMobManager();

            if (mm == null) { return null; }

            // Spawn mob?
            ActiveMob mob = mm.spawnMob(name, location);

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

    public static boolean IsMythicMobLoaded(String name) {

        // Try
        MythicMob mobtest = MythicMobs.inst().getMobManager().getMythicMob(name);

        // Try
        return mobtest != null;
    }

    public static Boolean IsMythicMobOfInternalID(Entity targetEntity, String mythicmobName, RefSimulator<String> logger) {

        // CHeck that it is a mythicmob to begin with
        if (IsMythicMob(targetEntity)) {

            // Just do thay
            if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(targetEntity).getType().getInternalName().equals(mythicmobName)) {

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
        return MythicMobs.inst().getAPIHelper().isMythicMob(targetEntity);
    }

    public static Boolean GraveyardsRespawnSkill(String skillName, Location loc, Player pl) {
        Optional mSkillFk = MythicMobs.inst().getSkillManager().getSkill(skillName);

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
                mSkill.execute(SkillTrigger.API, skCaster, skPlayer, skLocation, skHash, (HashSet)null, 1);

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

        Optional<Skill> mSkillFk = MythicMobs.inst().getSkillManager().getSkill(skillName);

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

            Optional<Skill> mSkillFk = MythicMobs.inst().getSkillManager().getSkill(skillName);
            if (mSkillFk == null) { return null; }
            if (mSkillFk.isPresent()) { return mSkillFk.get(); }
        }

        return null;
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
    public static Boolean ExecuteMythicSkillAs(String skillName, Entity caster, Entity trigger, ArrayList<Entity> targets, Location origin, ArrayList<CompactCodedValue> vars) {

        // Fix vars
        if (vars == null) { vars = new ArrayList<>(); }

        // Retrieve the skill
        Skill mSkill = GetSkill(skillName);

        if (caster == null) { return false; }

        if (mSkill != null) {

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
            HashSet skHash = Sets.newHashSet(), skHashL = Sets.newHashSet();
            if (targets != null) {
                if (targets.size() > 0) {

                    // Add every target
                    for (Entity ent : targets) {

                        // If non-null
                        if (ent != null) {

                            // Add entity and its location
                            skHash.add(BukkitAdapter.adapt(ent));
                            skHashL.add(BukkitAdapter.adapt(ent.getLocation()));
                        }
                    }
                }
            }

            // UUUuuuh idk what happens if targets size is 0
            if (skHash.size() == 0) {

                // Targets is only the player
                skHash.add(skCaEntity);
                skHashL.add(skCaEntity);
            }

            // Cast!
            mSkill.execute(SkillTrigger.API, skCaster, skTrEntity, skLocation, skHash, skHashL, 1);

            return true;

        } else return false;
    }
    //endregion
}