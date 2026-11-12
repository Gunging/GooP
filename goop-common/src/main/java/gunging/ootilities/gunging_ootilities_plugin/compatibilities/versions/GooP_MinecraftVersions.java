package gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions;

import gunging.ootilities.goop.core.McVersion;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Resolves Materials, EntityTypes, Enchantments, Attributes and PotionEffectTypes that were
 * renamed, added or removed between Minecraft versions.
 * <p>
 * Everything is looked up <b>by name or namespaced key at runtime</b>, never through a
 * compile-time constant such as {@code Material.PALE_OAK_BOAT} or {@code Attribute.MAX_HEALTH}.
 * That keeps this class compiling against every Paper API GooP is built with (1.20.1 and
 * 1.21.1) and makes it resolve correctly on whichever server it actually runs on (up to 26.x).
 * It also avoids touching {@link Attribute} through enum-only bytecode, since Attribute is an
 * enum before 1.21.3 and an interface afterwards.
 */
public class GooP_MinecraftVersions {

    /**
     * Version of the running server, in the GooP 1.x double format ({@code 1.21.4 -> 21.4},
     * {@code 26.1 -> 126.1}). Kept so existing {@code mcVersion >= x} checks keep working;
     * new code should use {@link #getVersion()}.
     */
    public static double mcVersion = -1.0;

    @Nullable private static McVersion version;

    /** @return The Minecraft version of the running server. */
    @NotNull public static McVersion getVersion() {
        if (version == null) {
            McVersion parsed = McVersion.parse(Bukkit.getMinecraftVersion());
            if (parsed == null) { parsed = McVersion.parse(Bukkit.getVersion()); }
            version = parsed != null ? parsed : McVersion.of(1, 20, 1);
        }
        return version;
    }

    /**
     * Returns the minecraft version ignoring that first 1 (Minecraft 1.15.2 returns 15.2).
     * Year based releases return {@code 100 + year}, see {@link McVersion#toLegacyDouble()}.
     */
    public static double GetMinecraftVersion() {
        if (mcVersion < 0) { mcVersion = getVersion().toLegacyDouble(); }
        return mcVersion;
    }

    public static HashMap<GooPVersionMaterials, Material> versionMaterials = new HashMap<>();
    public static HashMap<GooPVersionEntities, EntityType> versionEntityTypes = new HashMap<>();
    public static HashMap<GooPVersionEnchantments, Enchantment> versionEnchantments = new HashMap<>();
    public static HashMap<GooPVersionAttributes, Attribute> versionAttributes = new HashMap<>();
    public static HashMap<GooPVersionPotionEffects, PotionEffectType> versionPotionEffects = new HashMap<>();

    //region Name tables (generated from the GooP 1.x per-version tables)
    /** Extra names tried for GooPVersionMaterials constants, newest name first; others are looked up by their own name. */
    private static final Map<String, String[]> ALIASES_MATERIAL = new HashMap<>();
    static {
        ALIASES_MATERIAL.put("CACTUS_GREEN", new String[]{"GREEN_DYE", "CACTUS_GREEN"});
        ALIASES_MATERIAL.put("DANDELION_YELLOW", new String[]{"YELLOW_DYE", "DANDELION_YELLOW"});
        ALIASES_MATERIAL.put("GRASS_PATH", new String[]{"DIRT_PATH", "GRASS_PATH"});
        ALIASES_MATERIAL.put("ROSE_RED", new String[]{"RED_DYE", "ROSE_RED"});
        ALIASES_MATERIAL.put("SIGN", new String[]{"OAK_SIGN", "SIGN"});
        ALIASES_MATERIAL.put("WALL_SIGN", new String[]{"OAK_WALL_SIGN", "WALL_SIGN"});
        ALIASES_MATERIAL.put("POTTERY_SHARD_ARCHER", new String[]{"ARCHER_POTTERY_SHERD", "POTTERY_SHARD_ARCHER"});
        ALIASES_MATERIAL.put("POTTERY_SHARD_PRIZE", new String[]{"PRIZE_POTTERY_SHERD", "POTTERY_SHARD_PRIZE"});
        ALIASES_MATERIAL.put("POTTERY_SHARD_ARMS_UP", new String[]{"ARMS_UP_POTTERY_SHERD", "POTTERY_SHARD_ARMS_UP"});
        ALIASES_MATERIAL.put("POTTERY_SHARD_SKULL", new String[]{"SKULL_POTTERY_SHERD", "POTTERY_SHARD_SKULL"});
        ALIASES_MATERIAL.put("ARCHER_POTTERY_SHERD", new String[]{"ARCHER_POTTERY_SHERD", "POTTERY_SHARD_ARCHER"});
        ALIASES_MATERIAL.put("ARMS_UP_POTTERY_SHERD", new String[]{"ARMS_UP_POTTERY_SHERD", "POTTERY_SHARD_ARMS_UP"});
        ALIASES_MATERIAL.put("PRIZE_POTTERY_SHERD", new String[]{"PRIZE_POTTERY_SHERD", "POTTERY_SHARD_PRIZE"});
        ALIASES_MATERIAL.put("SKULL_POTTERY_SHERD", new String[]{"SKULL_POTTERY_SHERD", "POTTERY_SHARD_SKULL"});
        ALIASES_MATERIAL.put("GRASS", new String[]{"SHORT_GRASS", "GRASS"});
        ALIASES_MATERIAL.put("SHORT_GRASS", new String[]{"SHORT_GRASS", "GRASS"});
        ALIASES_MATERIAL.put("SCUTE", new String[]{"TURTLE_SCUTE", "SCUTE"});
        ALIASES_MATERIAL.put("TURTLE_SCUTE", new String[]{"TURTLE_SCUTE", "SCUTE"});
    }

    /** Extra names tried for GooPVersionEntities constants, newest name first; others are looked up by their own name. */
    private static final Map<String, String[]> ALIASES_ENTITYTYPE = new HashMap<>();
    static {
        ALIASES_ENTITYTYPE.put("PIG_ZOMBIE", new String[]{"ZOMBIFIED_PIGLIN", "PIG_ZOMBIE"});
        ALIASES_ENTITYTYPE.put("ZOMBIFIED_PIGLIN", new String[]{"ZOMBIFIED_PIGLIN", "PIG_ZOMBIE"});
        ALIASES_ENTITYTYPE.put("CHEST_BOAT", new String[]{"OAK_CHEST_BOAT", "CHEST_BOAT"});
        ALIASES_ENTITYTYPE.put("DROPPED_ITEM", new String[]{"ITEM", "DROPPED_ITEM"});
        ALIASES_ENTITYTYPE.put("ITEM", new String[]{"ITEM", "DROPPED_ITEM"});
        ALIASES_ENTITYTYPE.put("LEASH_HITCH", new String[]{"LEASH_KNOT", "LEASH_HITCH"});
        ALIASES_ENTITYTYPE.put("LEASH_KNOT", new String[]{"LEASH_KNOT", "LEASH_HITCH"});
        ALIASES_ENTITYTYPE.put("ENDER_SIGNAL", new String[]{"EYE_OF_ENDER", "ENDER_SIGNAL"});
        ALIASES_ENTITYTYPE.put("EYE_OF_ENDER", new String[]{"EYE_OF_ENDER", "ENDER_SIGNAL"});
        ALIASES_ENTITYTYPE.put("SPLASH_POTION", new String[]{"POTION", "SPLASH_POTION"});
        ALIASES_ENTITYTYPE.put("POTION", new String[]{"POTION", "SPLASH_POTION"});
        ALIASES_ENTITYTYPE.put("THROWN_EXP_BOTTLE", new String[]{"EXPERIENCE_BOTTLE", "THROWN_EXP_BOTTLE"});
        ALIASES_ENTITYTYPE.put("EXPERIENCE_BOTTLE", new String[]{"EXPERIENCE_BOTTLE", "THROWN_EXP_BOTTLE"});
        ALIASES_ENTITYTYPE.put("PRIMED_TNT", new String[]{"TNT", "PRIMED_TNT"});
        ALIASES_ENTITYTYPE.put("TNT", new String[]{"TNT", "PRIMED_TNT"});
        ALIASES_ENTITYTYPE.put("FIREWORK", new String[]{"FIREWORK_ROCKET", "FIREWORK"});
        ALIASES_ENTITYTYPE.put("FIREWORK_ROCKET", new String[]{"FIREWORK_ROCKET", "FIREWORK"});
        ALIASES_ENTITYTYPE.put("MINECART_COMMAND", new String[]{"COMMAND_BLOCK_MINECART", "MINECART_COMMAND"});
        ALIASES_ENTITYTYPE.put("COMMAND_BLOCK_MINECART", new String[]{"COMMAND_BLOCK_MINECART", "MINECART_COMMAND"});
        ALIASES_ENTITYTYPE.put("MINECART_CHEST", new String[]{"CHEST_MINECART", "MINECART_CHEST"});
        ALIASES_ENTITYTYPE.put("CHEST_MINECART", new String[]{"CHEST_MINECART", "MINECART_CHEST"});
        ALIASES_ENTITYTYPE.put("MINECART_FURNACE", new String[]{"FURNACE_MINECART", "MINECART_FURNACE"});
        ALIASES_ENTITYTYPE.put("FURNACE_MINECART", new String[]{"FURNACE_MINECART", "MINECART_FURNACE"});
        ALIASES_ENTITYTYPE.put("MINECART_TNT", new String[]{"TNT_MINECART", "MINECART_TNT"});
        ALIASES_ENTITYTYPE.put("TNT_MINECART", new String[]{"TNT_MINECART", "MINECART_TNT"});
        ALIASES_ENTITYTYPE.put("MINECART_HOPPER", new String[]{"HOPPER_MINECART", "MINECART_HOPPER"});
        ALIASES_ENTITYTYPE.put("HOPPER_MINECART", new String[]{"HOPPER_MINECART", "MINECART_HOPPER"});
        ALIASES_ENTITYTYPE.put("MINECART_MOB_SPAWNER", new String[]{"SPAWNER_MINECART", "MINECART_MOB_SPAWNER"});
        ALIASES_ENTITYTYPE.put("SPAWNER_MINECART", new String[]{"SPAWNER_MINECART", "MINECART_MOB_SPAWNER"});
        ALIASES_ENTITYTYPE.put("MUSHROOM_COW", new String[]{"MOOSHROOM", "MUSHROOM_COW"});
        ALIASES_ENTITYTYPE.put("MOOSHROOM", new String[]{"MOOSHROOM", "MUSHROOM_COW"});
        ALIASES_ENTITYTYPE.put("SNOWMAN", new String[]{"SNOW_GOLEM", "SNOWMAN"});
        ALIASES_ENTITYTYPE.put("SNOW_GOLEM", new String[]{"SNOW_GOLEM", "SNOWMAN"});
        ALIASES_ENTITYTYPE.put("ENDER_CRYSTAL", new String[]{"END_CRYSTAL", "ENDER_CRYSTAL"});
        ALIASES_ENTITYTYPE.put("END_CRYSTAL", new String[]{"END_CRYSTAL", "ENDER_CRYSTAL"});
        ALIASES_ENTITYTYPE.put("FISHING_HOOK", new String[]{"FISHING_BOBBER", "FISHING_HOOK"});
        ALIASES_ENTITYTYPE.put("FISHING_BOBBER", new String[]{"FISHING_BOBBER", "FISHING_HOOK"});
        ALIASES_ENTITYTYPE.put("LIGHTNING", new String[]{"LIGHTNING_BOLT", "LIGHTNING"});
        ALIASES_ENTITYTYPE.put("LIGHTNING_BOLT", new String[]{"LIGHTNING_BOLT", "LIGHTNING"});
        ALIASES_ENTITYTYPE.put("BOAT", new String[]{"OAK_BOAT", "BOAT"});
    }

    /** Extra names tried for GooPVersionEnchantments constants, newest name first; others are looked up by their own name. */
    private static final Map<String, String[]> ALIASES_ENCHANTMENT = new HashMap<>();
    static {
        ALIASES_ENCHANTMENT.put("PROTECTION_ENVIRONMENTAL", new String[]{"PROTECTION", "PROTECTION_ENVIRONMENTAL"});
        ALIASES_ENCHANTMENT.put("PROTECTION", new String[]{"PROTECTION", "PROTECTION_ENVIRONMENTAL"});
        ALIASES_ENCHANTMENT.put("PROTECTION_FIRE", new String[]{"FIRE_PROTECTION", "PROTECTION_FIRE"});
        ALIASES_ENCHANTMENT.put("FIRE_PROTECTION", new String[]{"FIRE_PROTECTION", "PROTECTION_FIRE"});
        ALIASES_ENCHANTMENT.put("PROTECTION_FALL", new String[]{"FEATHER_FALLING", "PROTECTION_FALL"});
        ALIASES_ENCHANTMENT.put("FEATHER_FALLING", new String[]{"FEATHER_FALLING", "PROTECTION_FALL"});
        ALIASES_ENCHANTMENT.put("PROTECTION_EXPLOSIONS", new String[]{"BLAST_PROTECTION", "PROTECTION_EXPLOSIONS"});
        ALIASES_ENCHANTMENT.put("BLAST_PROTECTION", new String[]{"BLAST_PROTECTION", "PROTECTION_EXPLOSIONS"});
        ALIASES_ENCHANTMENT.put("PROTECTION_PROJECTILE", new String[]{"PROJECTILE_PROTECTION", "PROTECTION_PROJECTILE"});
        ALIASES_ENCHANTMENT.put("PROJECTILE_PROTECTION", new String[]{"PROJECTILE_PROTECTION", "PROTECTION_PROJECTILE"});
        ALIASES_ENCHANTMENT.put("OXYGEN", new String[]{"RESPIRATION", "OXYGEN"});
        ALIASES_ENCHANTMENT.put("RESPIRATION", new String[]{"RESPIRATION", "OXYGEN"});
        ALIASES_ENCHANTMENT.put("WATER_WORKER", new String[]{"AQUA_AFFINITY", "WATER_WORKER"});
        ALIASES_ENCHANTMENT.put("AQUA_AFFINITY", new String[]{"AQUA_AFFINITY", "WATER_WORKER"});
        ALIASES_ENCHANTMENT.put("DAMAGE_ALL", new String[]{"SHARPNESS", "DAMAGE_ALL"});
        ALIASES_ENCHANTMENT.put("SHARPNESS", new String[]{"SHARPNESS", "DAMAGE_ALL"});
        ALIASES_ENCHANTMENT.put("DAMAGE_UNDEAD", new String[]{"SMITE", "DAMAGE_UNDEAD"});
        ALIASES_ENCHANTMENT.put("SMITE", new String[]{"SMITE", "DAMAGE_UNDEAD"});
        ALIASES_ENCHANTMENT.put("DAMAGE_ARTHROPODS", new String[]{"BANE_OF_ARTHROPODS", "DAMAGE_ARTHROPODS"});
        ALIASES_ENCHANTMENT.put("BANE_OF_ARTHROPODS", new String[]{"BANE_OF_ARTHROPODS", "DAMAGE_ARTHROPODS"});
        ALIASES_ENCHANTMENT.put("LOOT_BONUS_MOBS", new String[]{"LOOTING", "LOOT_BONUS_MOBS"});
        ALIASES_ENCHANTMENT.put("LOOTING", new String[]{"LOOTING", "LOOT_BONUS_MOBS"});
        ALIASES_ENCHANTMENT.put("DIG_SPEED", new String[]{"EFFICIENCY", "DIG_SPEED"});
        ALIASES_ENCHANTMENT.put("EFFICIENCY", new String[]{"EFFICIENCY", "DIG_SPEED"});
        ALIASES_ENCHANTMENT.put("DURABILITY", new String[]{"UNBREAKING", "DURABILITY"});
        ALIASES_ENCHANTMENT.put("UNBREAKING", new String[]{"UNBREAKING", "DURABILITY"});
        ALIASES_ENCHANTMENT.put("LOOT_BONUS_BLOCKS", new String[]{"FORTUNE", "LOOT_BONUS_BLOCKS"});
        ALIASES_ENCHANTMENT.put("FORTUNE", new String[]{"FORTUNE", "LOOT_BONUS_BLOCKS"});
        ALIASES_ENCHANTMENT.put("ARROW_DAMAGE", new String[]{"POWER", "ARROW_DAMAGE"});
        ALIASES_ENCHANTMENT.put("POWER", new String[]{"POWER", "ARROW_DAMAGE"});
        ALIASES_ENCHANTMENT.put("ARROW_KNOCKBACK", new String[]{"PUNCH", "ARROW_KNOCKBACK"});
        ALIASES_ENCHANTMENT.put("PUNCH", new String[]{"PUNCH", "ARROW_KNOCKBACK"});
        ALIASES_ENCHANTMENT.put("ARROW_FIRE", new String[]{"FLAME", "ARROW_FIRE"});
        ALIASES_ENCHANTMENT.put("FLAME", new String[]{"FLAME", "ARROW_FIRE"});
        ALIASES_ENCHANTMENT.put("ARROW_INFINITE", new String[]{"INFINITY", "ARROW_INFINITE"});
        ALIASES_ENCHANTMENT.put("INFINITY", new String[]{"INFINITY", "ARROW_INFINITE"});
        ALIASES_ENCHANTMENT.put("LUCK", new String[]{"LUCK_OF_THE_SEA", "LUCK"});
        ALIASES_ENCHANTMENT.put("LUCK_OF_THE_SEA", new String[]{"LUCK_OF_THE_SEA", "LUCK"});
    }

    /** Extra names tried for GooPVersionAttributes constants, newest name first; others are looked up by their own name. */
    private static final Map<String, String[]> ALIASES_ATTRIBUTE = new HashMap<>();
    static {
        ALIASES_ATTRIBUTE.put("GENERIC_MAX_HEALTH", new String[]{"MAX_HEALTH", "GENERIC_MAX_HEALTH"});
        ALIASES_ATTRIBUTE.put("MAX_HEALTH", new String[]{"MAX_HEALTH", "GENERIC_MAX_HEALTH"});
        ALIASES_ATTRIBUTE.put("GENERIC_FOLLOW_RANGE", new String[]{"FOLLOW_RANGE", "GENERIC_FOLLOW_RANGE"});
        ALIASES_ATTRIBUTE.put("FOLLOW_RANGE", new String[]{"FOLLOW_RANGE", "GENERIC_FOLLOW_RANGE"});
        ALIASES_ATTRIBUTE.put("GENERIC_KNOCKBACK_RESISTANCE", new String[]{"KNOCKBACK_RESISTANCE", "GENERIC_KNOCKBACK_RESISTANCE"});
        ALIASES_ATTRIBUTE.put("KNOCKBACK_RESISTANCE", new String[]{"KNOCKBACK_RESISTANCE", "GENERIC_KNOCKBACK_RESISTANCE"});
        ALIASES_ATTRIBUTE.put("GENERIC_MOVEMENT_SPEED", new String[]{"MOVEMENT_SPEED", "GENERIC_MOVEMENT_SPEED"});
        ALIASES_ATTRIBUTE.put("MOVEMENT_SPEED", new String[]{"MOVEMENT_SPEED", "GENERIC_MOVEMENT_SPEED"});
        ALIASES_ATTRIBUTE.put("GENERIC_FLYING_SPEED", new String[]{"FLYING_SPEED", "GENERIC_FLYING_SPEED"});
        ALIASES_ATTRIBUTE.put("FLYING_SPEED", new String[]{"FLYING_SPEED", "GENERIC_FLYING_SPEED"});
        ALIASES_ATTRIBUTE.put("GENERIC_ATTACK_DAMAGE", new String[]{"ATTACK_DAMAGE", "GENERIC_ATTACK_DAMAGE"});
        ALIASES_ATTRIBUTE.put("ATTACK_DAMAGE", new String[]{"ATTACK_DAMAGE", "GENERIC_ATTACK_DAMAGE"});
        ALIASES_ATTRIBUTE.put("GENERIC_ATTACK_KNOCKBACK", new String[]{"ATTACK_KNOCKBACK", "GENERIC_ATTACK_KNOCKBACK"});
        ALIASES_ATTRIBUTE.put("ATTACK_KNOCKBACK", new String[]{"ATTACK_KNOCKBACK", "GENERIC_ATTACK_KNOCKBACK"});
        ALIASES_ATTRIBUTE.put("GENERIC_ATTACK_SPEED", new String[]{"ATTACK_SPEED", "GENERIC_ATTACK_SPEED"});
        ALIASES_ATTRIBUTE.put("ATTACK_SPEED", new String[]{"ATTACK_SPEED", "GENERIC_ATTACK_SPEED"});
        ALIASES_ATTRIBUTE.put("GENERIC_ARMOR", new String[]{"ARMOR", "GENERIC_ARMOR"});
        ALIASES_ATTRIBUTE.put("ARMOR", new String[]{"ARMOR", "GENERIC_ARMOR"});
        ALIASES_ATTRIBUTE.put("GENERIC_ARMOR_TOUGHNESS", new String[]{"ARMOR_TOUGHNESS", "GENERIC_ARMOR_TOUGHNESS"});
        ALIASES_ATTRIBUTE.put("ARMOR_TOUGHNESS", new String[]{"ARMOR_TOUGHNESS", "GENERIC_ARMOR_TOUGHNESS"});
        ALIASES_ATTRIBUTE.put("GENERIC_LUCK", new String[]{"LUCK", "GENERIC_LUCK"});
        ALIASES_ATTRIBUTE.put("LUCK", new String[]{"LUCK", "GENERIC_LUCK"});
        ALIASES_ATTRIBUTE.put("HORSE_JUMP_STRENGTH", new String[]{"JUMP_STRENGTH", "HORSE_JUMP_STRENGTH"});
        ALIASES_ATTRIBUTE.put("JUMP_STRENGTH", new String[]{"JUMP_STRENGTH", "HORSE_JUMP_STRENGTH"});
        ALIASES_ATTRIBUTE.put("ZOMBIE_SPAWN_REINFORCEMENTS", new String[]{"SPAWN_REINFORCEMENTS", "ZOMBIE_SPAWN_REINFORCEMENTS"});
        ALIASES_ATTRIBUTE.put("SPAWN_REINFORCEMENTS", new String[]{"SPAWN_REINFORCEMENTS", "ZOMBIE_SPAWN_REINFORCEMENTS"});
    }

    /** Extra names tried for GooPVersionPotionEffects constants, newest name first; others are looked up by their own name. */
    private static final Map<String, String[]> ALIASES_POTIONEFFECTTYPE = new HashMap<>();
    static {
        ALIASES_POTIONEFFECTTYPE.put("SLOW", new String[]{"SLOWNESS", "SLOW"});
        ALIASES_POTIONEFFECTTYPE.put("SLOWNESS", new String[]{"SLOWNESS", "SLOW"});
        ALIASES_POTIONEFFECTTYPE.put("FAST_DIGGING", new String[]{"HASTE", "FAST_DIGGING"});
        ALIASES_POTIONEFFECTTYPE.put("HASTE", new String[]{"HASTE", "FAST_DIGGING"});
        ALIASES_POTIONEFFECTTYPE.put("SLOW_DIGGING", new String[]{"MINING_FATIGUE", "SLOW_DIGGING"});
        ALIASES_POTIONEFFECTTYPE.put("MINING_FATIGUE", new String[]{"MINING_FATIGUE", "SLOW_DIGGING"});
        ALIASES_POTIONEFFECTTYPE.put("INCREASE_DAMAGE", new String[]{"STRENGTH", "INCREASE_DAMAGE"});
        ALIASES_POTIONEFFECTTYPE.put("STRENGTH", new String[]{"STRENGTH", "INCREASE_DAMAGE"});
        ALIASES_POTIONEFFECTTYPE.put("HEAL", new String[]{"INSTANT_HEALTH", "HEAL"});
        ALIASES_POTIONEFFECTTYPE.put("INSTANT_HEALTH", new String[]{"INSTANT_HEALTH", "HEAL"});
        ALIASES_POTIONEFFECTTYPE.put("HARM", new String[]{"INSTANT_DAMAGE", "HARM"});
        ALIASES_POTIONEFFECTTYPE.put("INSTANT_DAMAGE", new String[]{"INSTANT_DAMAGE", "HARM"});
        ALIASES_POTIONEFFECTTYPE.put("JUMP", new String[]{"JUMP_BOOST", "JUMP"});
        ALIASES_POTIONEFFECTTYPE.put("JUMP_BOOST", new String[]{"JUMP_BOOST", "JUMP"});
        ALIASES_POTIONEFFECTTYPE.put("CONFUSION", new String[]{"NAUSEA", "CONFUSION"});
        ALIASES_POTIONEFFECTTYPE.put("NAUSEA", new String[]{"NAUSEA", "CONFUSION"});
        ALIASES_POTIONEFFECTTYPE.put("DAMAGE_RESISTANCE", new String[]{"RESISTANCE", "DAMAGE_RESISTANCE"});
        ALIASES_POTIONEFFECTTYPE.put("RESISTANCE", new String[]{"RESISTANCE", "DAMAGE_RESISTANCE"});
    }

    //endregion

    /** @return The names to try for this constant, newest first. */
    @NotNull private static String[] candidates(@NotNull Map<String, String[]> aliases, @NotNull String name) {
        String[] found = aliases.get(name);
        return found != null ? found : new String[]{name};
    }

    public static void InitializeMaterials() {
        GetMinecraftVersion();

        for (GooPVersionMaterials gvm : GooPVersionMaterials.values()) {
            Material found = Material.VOID_AIR;
            for (String name : candidates(ALIASES_MATERIAL, gvm.name())) {
                Material m = Material.getMaterial(name);
                if (m != null) { found = m; break; }
            }
            versionMaterials.put(gvm, found);
        }

        for (GooPVersionEntities gve : GooPVersionEntities.values()) {
            EntityType found = EntityType.UNKNOWN;
            for (String name : candidates(ALIASES_ENTITYTYPE, gve.name())) {
                EntityType e = GetEntityTypeFromString(name);
                if (e != EntityType.UNKNOWN) { found = e; break; }
            }
            versionEntityTypes.put(gve, found);
        }

        for (GooPVersionEnchantments gve : GooPVersionEnchantments.values()) {
            Enchantment e = GetEnchantmentFromString(gve.name());
            if (e != null) { versionEnchantments.put(gve, e); }
        }

        for (GooPVersionAttributes gva : GooPVersionAttributes.values()) {
            Attribute a = GetAttributeFromString(gva.name());
            if (a != null) { versionAttributes.put(gva, a); }
        }

        for (GooPVersionPotionEffects gvp : GooPVersionPotionEffects.values()) {
            PotionEffectType p = GetPotionEffectFromString(gvp.name());
            if (p != null) { versionPotionEffects.put(gvp, p); }
        }
    }

    //region Materials
    /**
     * Returns a material from a string.
     *
     * @return Either the Material, or VOID_AIR if it doesnt exist.
     */
    @NotNull public static Material GetMaterialFromString(String str) {
        if (str == null) { return Material.VOID_AIR; }
        Material mat = Material.getMaterial(str.toUpperCase(Locale.ROOT));
        return mat != null ? mat : Material.VOID_AIR;
    }

    /**
     * If the current material exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static Material GetVersionMaterial(GooPVersionMaterials mat, Material defaultIfMissing) {
        Material mt = GetVersionMaterial(mat);
        return mt != Material.VOID_AIR ? mt : defaultIfMissing;
    }

    /**
     * If the current material exists in this minecraft version, return it.
     * Otherwise, returns VOID_AIR.
     */
    public static Material GetVersionMaterial(GooPVersionMaterials mat) { return versionMaterials.get(mat); }
    //endregion

    //region Entities
    /**
     * Returns an EntityType from a string.
     *
     * @return Either the EntityType, or UNKNOWN if it doesnt exist.
     */
    public static EntityType GetEntityTypeFromString(String str) {
        if (str == null) { return EntityType.UNKNOWN; }
        try {
            return EntityType.valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return EntityType.UNKNOWN;
        }
    }

    /**
     * If the current EntityType exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static EntityType GetVersionEntityType(GooPVersionEntities ent, EntityType defaultIfMissing) {
        EntityType mt = GetVersionEntityType(ent);
        return mt != EntityType.UNKNOWN ? mt : defaultIfMissing;
    }

    /**
     * If the current EntityType exists in this minecraft version, return it.
     * Otherwise, returns UNKNOWN.
     */
    public static EntityType GetVersionEntityType(GooPVersionEntities ent) { return versionEntityTypes.get(ent); }
    //endregion

    //region Enchantments
    /**
     * Returns an Enchantment from its name or key. Accepts the namespaced key
     * ({@code sharpness}, {@code minecraft:sharpness}), the current constant name
     * ({@code SHARPNESS}) and the pre-1.20.5 constant name ({@code DAMAGE_ALL}).
     *
     * @return Either the Enchantment, or null if it doesnt exist.
     */
    @Nullable public static Enchantment GetEnchantmentFromString(@Nullable String str) {
        if (str == null) { return null; }
        for (String name : candidates(ALIASES_ENCHANTMENT, bareName(str))) {
            NamespacedKey key = toKey(name);
            if (key == null) { continue; }
            Enchantment e = Enchantment.getByKey(key);
            if (e != null) { return e; }
        }
        return null;
    }

    /**
     * If the current Enchantment exists in this minecraft version, return it.
     * Otherwise, returns NULL.
     */
    public static Enchantment GetVersionEnchantment(GooPVersionEnchantments ench) { return versionEnchantments.get(ench); }
    //endregion

    //region Attributes
    /** Namespaces attributes lived under before Minecraft 1.21.3 dropped them from their keys */
    private static final String[] LEGACY_ATTRIBUTE_PREFIXES = {"generic.", "player.", "horse.", "zombie."};

    /**
     * Returns an Attribute from its name or key. Accepts {@code MAX_HEALTH},
     * {@code GENERIC_MAX_HEALTH}, {@code max_health} and {@code generic.max_health} on every
     * version: before 1.21.3 the registry keys carry a {@code generic./player./horse./zombie.}
     * prefix, afterwards they do not.
     *
     * @return Either the Attributes, or null if it doesnt exist.
     */
    @Nullable public static Attribute GetAttributeFromString(@Nullable String str) {
        if (str == null) { return null; }
        String raw = str.trim();
        if (raw.startsWith("minecraft:")) { raw = raw.substring("minecraft:".length()); }

        // Already a dotted key such as generic.max_health
        if (raw.indexOf('.') >= 0) {
            Attribute direct = attributeByKey(raw.toLowerCase(Locale.ROOT));
            if (direct != null) { return direct; }
            raw = raw.substring(raw.indexOf('.') + 1);
        }

        for (String name : candidates(ALIASES_ATTRIBUTE, raw.toUpperCase(Locale.ROOT))) {
            String lower = name.toLowerCase(Locale.ROOT);

            // Strip a GENERIC_/PLAYER_/HORSE_/ZOMBIE_ style prefix to get the bare attribute name
            String bare = lower;
            for (String prefix : LEGACY_ATTRIBUTE_PREFIXES) {
                String underscored = prefix.replace('.', '_');
                if (lower.startsWith(underscored)) { bare = lower.substring(underscored.length()); break; }
            }

            Attribute modern = attributeByKey(bare);
            if (modern != null) { return modern; }
            for (String prefix : LEGACY_ATTRIBUTE_PREFIXES) {
                Attribute legacy = attributeByKey(prefix + bare);
                if (legacy != null) { return legacy; }
            }
        }
        return null;
    }

    @Nullable private static Attribute attributeByKey(@NotNull String key) {
        NamespacedKey nk = toKey(key);
        return nk == null ? null : Registry.ATTRIBUTE.get(nk);
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static Attribute GetVersionAttribute(GooPVersionAttributes ench, Attribute defaultIfMissing) {
        Attribute mt = GetVersionAttribute(ench);
        return mt != null ? mt : defaultIfMissing;
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns NULL.
     */
    public static Attribute GetVersionAttribute(GooPVersionAttributes ench) { return versionAttributes.get(ench); }

    /**
     * The key of an attribute, read through {@link Keyed} so the call compiles to an interface
     * call that works whether Attribute is an enum (before 1.21.3) or an interface (after).
     */
    @NotNull public static NamespacedKey GetAttributeKey(@NotNull Attribute attribute) { return ((Keyed) attribute).getKey(); }

    /**
     * The attribute's name the way GooP 1.x printed it ({@code GENERIC_MAX_HEALTH} before
     * 1.21.3, {@code MAX_HEALTH} after): its key path upper-cased with dots as underscores.
     * Replaces {@code Attribute#name()}/{@code toString()}, which only exist while Attribute
     * is an enum.
     */
    @NotNull public static String GetAttributeName(@NotNull Attribute attribute) {
        return GetAttributeKey(attribute).getKey().replace('.', '_').toUpperCase(Locale.ROOT);
    }
    //endregion

    //region PotionEffects
    /**
     * Returns a PotionEffectType from its name or key. Accepts the key ({@code slowness}),
     * the current constant name ({@code SLOWNESS}) and the pre-1.20.5 one ({@code SLOW}).
     *
     * @return Either the PotionEffectType, or null if it doesnt exist.
     */
    @Nullable public static PotionEffectType GetPotionEffectFromString(@Nullable String str) {
        if (str == null) { return null; }
        for (String name : candidates(ALIASES_POTIONEFFECTTYPE, bareName(str))) {
            NamespacedKey key = toKey(name);
            if (key == null) { continue; }
            PotionEffectType p = PotionEffectType.getByKey(key);
            if (p != null) { return p; }
        }
        return null;
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static PotionEffectType GetVersionPotionEffect(GooPVersionPotionEffects ench, PotionEffectType defaultIfMissing) {
        PotionEffectType mt = GetVersionPotionEffect(ench);
        return mt != null ? mt : defaultIfMissing;
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns NULL.
     */
    public static PotionEffectType GetVersionPotionEffect(GooPVersionPotionEffects ench) { return versionPotionEffects.get(ench); }
    //endregion

    /**
     * A loaded world by name, or by namespaced key such as {@code minecraft:the_nether}.
     * <p>
     * GooP stores world names. Paper 26.x moved dimension folders and is moving its API from
     * names to keys, but on 26.1 - 26.3 the default worlds keep their old names (world,
     * world_nether, world_the_end), so stored data still resolves; the key form is accepted
     * as well so configs can use it.
     */
    @Nullable public static org.bukkit.World GetWorld(@Nullable String nameOrKey) {
        if (nameOrKey == null) { return null; }
        org.bukkit.World byName = Bukkit.getWorld(nameOrKey);
        if (byName != null || nameOrKey.indexOf(':') < 0) { return byName; }
        NamespacedKey key = NamespacedKey.fromString(nameOrKey.toLowerCase(Locale.ROOT));
        return key == null ? null : Bukkit.getWorld(key);
    }

    /** Characters allowed in a namespaced key path */
    private static final Pattern KEY_PATH = Pattern.compile("[a-z0-9_./-]+");

    /** @return The name upper-cased and without a {@code minecraft:} namespace, for alias lookups. */
    @NotNull private static String bareName(@NotNull String str) {
        String s = str.trim();
        if (s.toLowerCase(Locale.ROOT).startsWith("minecraft:")) { s = s.substring("minecraft:".length()); }
        return s.toUpperCase(Locale.ROOT);
    }

    /** @return A minecraft namespaced key from a name such as SHARPNESS or minecraft:sharpness, null if it is not a valid key. */
    @Nullable private static NamespacedKey toKey(@NotNull String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.startsWith("minecraft:")) { lower = lower.substring("minecraft:".length()); }
        if (!KEY_PATH.matcher(lower).matches()) { return null; }
        return NamespacedKey.minecraft(lower);
    }
}
