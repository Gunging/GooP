package gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class GooP_MinecraftVersions {

    public static double mcVersion = -1.0;

    /**
     * It is assumed that this is not minecraft 2.0+ ffs
     * Returns the minecraft version ignoring that first 1.
     *
     * (e.g. Minecraft 1.15.2 returns 15.2)
     * @return (e.g.) Minecraft 1.13.2 returns 13.2
     */
    public static double GetMinecraftVersion() {
        // Gather if hasnt been gathered
        if (mcVersion < 0) {
            String vers = Bukkit.getVersion();

            if (vers.contains("1.13")) {
                mcVersion = 13.0;
                if (vers.contains("1.13.1")) { mcVersion = 13.1; }
                if (vers.contains("1.13.2")) { mcVersion = 13.2; }

            } else if (vers.contains("1.14")) {
                mcVersion = 14.0;
                if (vers.contains("1.14.1")) { mcVersion = 14.1; }
                if (vers.contains("1.14.2")) { mcVersion = 14.2; }
                if (vers.contains("1.14.3")) { mcVersion = 14.3; }
                if (vers.contains("1.14.4")) { mcVersion = 14.4; }

            } else if (vers.contains("1.15")) {
                mcVersion = 15.0;
                if (vers.contains("1.15.1")) { mcVersion = 15.1; }
                if (vers.contains("1.15.2")) { mcVersion = 15.2; }

            } else if (vers.contains("1.16")) {
                mcVersion = 16.0;
                if (vers.contains("1.16.1")) { mcVersion = 16.1; }
                if (vers.contains("1.16.2")) { mcVersion = 16.2; }
                if (vers.contains("1.16.3")) { mcVersion = 16.3; }
                if (vers.contains("1.16.4")) { mcVersion = 16.4; }
                if (vers.contains("1.16.5")) { mcVersion = 16.5; }

            } else if (vers.contains("1.17")) {
                mcVersion = 17.0;
                if (vers.contains("1.17.1")) { mcVersion = 17.1; }

            } else if (vers.contains("1.18")) {
                mcVersion = 18.0;
                if (vers.contains("1.18.1")) { mcVersion = 18.1; }
                if (vers.contains("1.18.2")) { mcVersion = 18.2; }

            } else if (vers.contains("1.19")) {
                mcVersion = 19.0;
                if (vers.contains("1.19.1")) { mcVersion = 19.1; }
                if (vers.contains("1.19.2")) { mcVersion = 19.2; }
                if (vers.contains("1.19.3")) { mcVersion = 19.3; }
                if (vers.contains("1.19.4")) { mcVersion = 19.4; }

            } else if (vers.contains("1.20")) {
                mcVersion = 20.0;
                if (vers.contains("1.20.1")) { mcVersion = 20.1; }
                if (vers.contains("1.20.2")) { mcVersion = 20.2; }
                if (vers.contains("1.20.3")) { mcVersion = 20.3; }
                if (vers.contains("1.20.4")) { mcVersion = 20.4; }
                if (vers.contains("1.20.5")) { mcVersion = 20.5; }
                if (vers.contains("1.20.6")) { mcVersion = 20.6; }

            } else if (vers.contains("1.21")) {
                mcVersion = 21.0;
                if (vers.contains("1.21.1")) { mcVersion = 21.1; }
                if (vers.contains("1.21.2")) { mcVersion = 21.2; }
                if (vers.contains("1.21.3")) { mcVersion = 21.3; }
                if (vers.contains("1.21.4")) { mcVersion = 21.4; }
                if (vers.contains("1.21.5")) { mcVersion = 21.5; }
                if (vers.contains("1.21.6")) { mcVersion = 21.6; }

            } else if (vers.contains("1.22")) {
                mcVersion = 22.0;
                if (vers.contains("1.22.1")) { mcVersion = 22.1; }
                if (vers.contains("1.22.2")) { mcVersion = 22.2; }
                if (vers.contains("1.22.3")) { mcVersion = 22.3; }
                if (vers.contains("1.22.4")) { mcVersion = 22.4; }
                if (vers.contains("1.22.5")) { mcVersion = 22.5; }
                if (vers.contains("1.22.6")) { mcVersion = 22.6; }

            } else if (vers.contains("1.23")) {
                mcVersion = 23.0;
                if (vers.contains("1.23.1")) { mcVersion = 23.1; }
                if (vers.contains("1.23.2")) { mcVersion = 23.2; }
                if (vers.contains("1.23.3")) { mcVersion = 23.3; }
                if (vers.contains("1.23.4")) { mcVersion = 23.4; }
                if (vers.contains("1.23.5")) { mcVersion = 23.5; }
                if (vers.contains("1.23.6")) { mcVersion = 23.6; }
            }

            return mcVersion;

        } else {

            // Return the gathered value
            return mcVersion;
        }
    }

    public static HashMap<GooPVersionMaterials, Material> versionMaterials = new HashMap<>();
    public static HashMap<GooPVersionEntities, EntityType> versionEntityTypes = new HashMap<>();
    public static HashMap<GooPVersionEnchantments, Enchantment> versionEnchantments = new HashMap<>();
    public static HashMap<GooPVersionAttributes, Attribute> versionAttributes = new HashMap<>();
    public static HashMap<GooPVersionPotionEffects, PotionEffectType> versionPotionEffects = new HashMap<>();
    public static void InitializeMaterials() {

        // Get MC Version
        GetMinecraftVersion();

        //region Materials
        //region Minecraft 1.13-
        if (mcVersion < 14.0) {
            versionMaterials.put(GooPVersionMaterials.CACTUS_GREEN, GetMaterialFromString("CACTUS_GREEN"));
            versionMaterials.put(GooPVersionMaterials.DANDELION_YELLOW, GetMaterialFromString("DANDELION_YELLOW"));
            versionMaterials.put(GooPVersionMaterials.ROSE_RED, GetMaterialFromString("ROSE_RED"));
            versionMaterials.put(GooPVersionMaterials.SIGN, GetMaterialFromString("SIGN"));
            versionMaterials.put(GooPVersionMaterials.WALL_SIGN, GetMaterialFromString("WALL_SIGN"));
        }
        //endregion

        //region Minecraft 1.14+
        if (mcVersion >= 14.0) {
            versionMaterials.put(GooPVersionMaterials.ACACIA_SIGN, Material.ACACIA_SIGN);
            versionMaterials.put(GooPVersionMaterials.ACACIA_WALL_SIGN, Material.ACACIA_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.ANDESITE_SLAB, Material.ANDESITE_SLAB);
            versionMaterials.put(GooPVersionMaterials.ANDESITE_STAIRS, Material.ANDESITE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.ANDESITE_WALL, Material.ANDESITE_WALL);

            versionMaterials.put(GooPVersionMaterials.BAMBOO, Material.BAMBOO);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_SAPLING, Material.BAMBOO_SAPLING);
            versionMaterials.put(GooPVersionMaterials.BARREL, Material.BARREL);
            versionMaterials.put(GooPVersionMaterials.BELL, Material.BELL);
            versionMaterials.put(GooPVersionMaterials.BIRCH_SIGN, Material.BIRCH_SIGN);

            versionMaterials.put(GooPVersionMaterials.BIRCH_WALL_SIGN, Material.BIRCH_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.BLACK_DYE, Material.BLACK_DYE);
            versionMaterials.put(GooPVersionMaterials.BLAST_FURNACE, Material.BLAST_FURNACE);
            versionMaterials.put(GooPVersionMaterials.BLUE_DYE, Material.BLUE_DYE);
            versionMaterials.put(GooPVersionMaterials.BRICK_WALL, Material.BRICK_WALL);

            versionMaterials.put(GooPVersionMaterials.BROWN_DYE, Material.BROWN_DYE);

            versionMaterials.put(GooPVersionMaterials.CACTUS_GREEN, Material.GREEN_DYE);
            versionMaterials.put(GooPVersionMaterials.CAMPFIRE, Material.CAMPFIRE);
            versionMaterials.put(GooPVersionMaterials.CARTOGRAPHY_TABLE, Material.CARTOGRAPHY_TABLE);
            versionMaterials.put(GooPVersionMaterials.CAT_SPAWN_EGG, Material.CAT_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.COMPOSTER, Material.COMPOSTER);

            versionMaterials.put(GooPVersionMaterials.CORNFLOWER, Material.CORNFLOWER);
            versionMaterials.put(GooPVersionMaterials.CREEPER_BANNER_PATTERN, Material.CREEPER_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.CROSSBOW, Material.CROSSBOW);
            versionMaterials.put(GooPVersionMaterials.CUT_RED_SANDSTONE_SLAB, Material.CUT_RED_SANDSTONE_SLAB);
            versionMaterials.put(GooPVersionMaterials.CUT_SANDSTONE_SLAB, Material.CUT_SANDSTONE_SLAB);

            versionMaterials.put(GooPVersionMaterials.DANDELION_YELLOW, Material.YELLOW_DYE);
            versionMaterials.put(GooPVersionMaterials.DARK_OAK_SIGN, Material.DARK_OAK_SIGN);
            versionMaterials.put(GooPVersionMaterials.DARK_OAK_WALL_SIGN, Material.DARK_OAK_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.DIORITE_SLAB, Material.DIORITE_SLAB);
            versionMaterials.put(GooPVersionMaterials.DIORITE_STAIRS, Material.DIORITE_STAIRS);

            versionMaterials.put(GooPVersionMaterials.DIORITE_WALL, Material.DIORITE_WALL);

            versionMaterials.put(GooPVersionMaterials.END_STONE_BRICK_SLAB, Material.END_STONE_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.END_STONE_BRICK_STAIRS, Material.END_STONE_BRICK_STAIRS);
            versionMaterials.put(GooPVersionMaterials.END_STONE_BRICK_WALL, Material.END_STONE_BRICK_WALL);

            versionMaterials.put(GooPVersionMaterials.FLETCHING_TABLE, Material.FLETCHING_TABLE);
            versionMaterials.put(GooPVersionMaterials.FLOWER_BANNER_PATTERN, Material.FLOWER_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.FOX_SPAWN_EGG, Material.FOX_SPAWN_EGG);

            versionMaterials.put(GooPVersionMaterials.GLOBE_BANNER_PATTERN, Material.GLOBE_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.GRANITE_SLAB, Material.GRANITE_SLAB);
            versionMaterials.put(GooPVersionMaterials.GRANITE_STAIRS, Material.GRANITE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.GRANITE_WALL, Material.GRANITE_WALL);
            versionMaterials.put(GooPVersionMaterials.GREEN_DYE, Material.GREEN_DYE);

            versionMaterials.put(GooPVersionMaterials.GRINDSTONE, Material.GRINDSTONE);

            versionMaterials.put(GooPVersionMaterials.JIGSAW, Material.JIGSAW);
            versionMaterials.put(GooPVersionMaterials.JUNGLE_SIGN, Material.JUNGLE_SIGN);
            versionMaterials.put(GooPVersionMaterials.JUNGLE_WALL_SIGN, Material.JUNGLE_WALL_SIGN);

            versionMaterials.put(GooPVersionMaterials.LECTERN, Material.LECTERN);
            versionMaterials.put(GooPVersionMaterials.LANTERN, Material.LANTERN);
            versionMaterials.put(GooPVersionMaterials.LEATHER_HORSE_ARMOR, Material.LEATHER_HORSE_ARMOR);
            versionMaterials.put(GooPVersionMaterials.LILY_OF_THE_VALLEY, Material.LILY_OF_THE_VALLEY);
            versionMaterials.put(GooPVersionMaterials.LOOM, Material.LOOM);

            versionMaterials.put(GooPVersionMaterials.MOJANG_BANNER_PATTERN, Material.MOJANG_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.MOSSY_COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_SLAB);
            versionMaterials.put(GooPVersionMaterials.MOSSY_COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.MOSSY_STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.MOSSY_STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_STAIRS);

            versionMaterials.put(GooPVersionMaterials.MOSSY_STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL);

            versionMaterials.put(GooPVersionMaterials.NETHER_BRICK_WALL, Material.NETHER_BRICK_WALL);

            versionMaterials.put(GooPVersionMaterials.OAK_SIGN, Material.OAK_SIGN);
            versionMaterials.put(GooPVersionMaterials.OAK_WALL_SIGN, Material.OAK_WALL_SIGN);

            versionMaterials.put(GooPVersionMaterials.PANDA_SPAWN_EGG, Material.PANDA_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.PILLAGER_SPAWN_EGG, Material.PILLAGER_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.POLISHED_ANDESITE_SLAB, Material.POLISHED_ANDESITE_SLAB);
            versionMaterials.put(GooPVersionMaterials.POLISHED_DIORITE_SLAB, Material.POLISHED_DIORITE_SLAB);
            versionMaterials.put(GooPVersionMaterials.POLISHED_GRANITE_SLAB, Material.POLISHED_GRANITE_SLAB);

            versionMaterials.put(GooPVersionMaterials.POTTED_BAMBOO, Material.POTTED_BAMBOO);
            versionMaterials.put(GooPVersionMaterials.POTTED_CORNFLOWER, Material.POTTED_CORNFLOWER);
            versionMaterials.put(GooPVersionMaterials.POTTED_LILY_OF_THE_VALLEY, Material.POTTED_LILY_OF_THE_VALLEY);
            versionMaterials.put(GooPVersionMaterials.POTTED_WITHER_ROSE, Material.POTTED_WITHER_ROSE);
            versionMaterials.put(GooPVersionMaterials.PRISMARINE_WALL, Material.PRISMARINE_WALL);

            versionMaterials.put(GooPVersionMaterials.RAVAGER_SPAWN_EGG, Material.RAVAGER_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.RED_DYE, Material.RED_DYE);
            versionMaterials.put(GooPVersionMaterials.RED_NETHER_BRICK_SLAB, Material.RED_NETHER_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.RED_NETHER_BRICK_STAIRS, Material.RED_NETHER_BRICK_STAIRS);
            versionMaterials.put(GooPVersionMaterials.RED_NETHER_BRICK_WALL, Material.RED_NETHER_BRICK_WALL);

            versionMaterials.put(GooPVersionMaterials.RED_SANDSTONE_WALL, Material.RED_SANDSTONE_WALL);
            versionMaterials.put(GooPVersionMaterials.ROSE_RED, Material.RED_DYE);

            versionMaterials.put(GooPVersionMaterials.SANDSTONE_WALL, Material.SANDSTONE_WALL);
            versionMaterials.put(GooPVersionMaterials.SCAFFOLDING, Material.SCAFFOLDING);
            versionMaterials.put(GooPVersionMaterials.SIGN, Material.OAK_SIGN);
            versionMaterials.put(GooPVersionMaterials.SKULL_BANNER_PATTERN, Material.SKULL_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.SMITHING_TABLE, Material.SMITHING_TABLE);

            versionMaterials.put(GooPVersionMaterials.SMOKER, Material.SMOKER);
            versionMaterials.put(GooPVersionMaterials.SMOOTH_QUARTZ_SLAB, Material.SMOOTH_QUARTZ_SLAB);
            versionMaterials.put(GooPVersionMaterials.SMOOTH_QUARTZ_STAIRS, Material.SMOOTH_QUARTZ_STAIRS);
            versionMaterials.put(GooPVersionMaterials.SMOOTH_RED_SANDSTONE_SLAB, Material.SMOOTH_RED_SANDSTONE_SLAB);
            versionMaterials.put(GooPVersionMaterials.SMOOTH_RED_SANDSTONE_STAIRS, Material.SMOOTH_RED_SANDSTONE_STAIRS);

            versionMaterials.put(GooPVersionMaterials.SMOOTH_SANDSTONE_SLAB, Material.SMOOTH_SANDSTONE_SLAB);
            versionMaterials.put(GooPVersionMaterials.SMOOTH_SANDSTONE_STAIRS, Material.SMOOTH_SANDSTONE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.SMOOTH_STONE_SLAB, Material.SMOOTH_STONE_SLAB);
            versionMaterials.put(GooPVersionMaterials.SPRUCE_SIGN, Material.SPRUCE_SIGN);
            versionMaterials.put(GooPVersionMaterials.SPRUCE_WALL_SIGN, Material.SPRUCE_WALL_SIGN);

            versionMaterials.put(GooPVersionMaterials.STONECUTTER, Material.STONECUTTER);
            versionMaterials.put(GooPVersionMaterials.STONE_BRICK_WALL, Material.STONE_BRICK_WALL);
            versionMaterials.put(GooPVersionMaterials.STONE_STAIRS, Material.STONE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.SUSPICIOUS_STEW, Material.SUSPICIOUS_STEW);
            versionMaterials.put(GooPVersionMaterials.SWEET_BERRIES, Material.SWEET_BERRIES);

            versionMaterials.put(GooPVersionMaterials.SWEET_BERRY_BUSH, Material.SWEET_BERRY_BUSH);

            versionMaterials.put(GooPVersionMaterials.TRADER_LLAMA_SPAWN_EGG, Material.TRADER_LLAMA_SPAWN_EGG);

            versionMaterials.put(GooPVersionMaterials.WALL_SIGN, Material.OAK_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.WANDERING_TRADER_SPAWN_EGG, Material.WANDERING_TRADER_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.WHITE_DYE, Material.WHITE_DYE);
            versionMaterials.put(GooPVersionMaterials.WITHER_ROSE, Material.WITHER_ROSE);

            versionMaterials.put(GooPVersionMaterials.YELLOW_DYE, Material.YELLOW_DYE);
        }
        //endregion

        //region Minecraft Version 1.15+
        if (mcVersion >= 15.0) {
            versionMaterials.put(GooPVersionMaterials.BEEHIVE, Material.BEEHIVE);
            versionMaterials.put(GooPVersionMaterials.BEE_NEST, Material.BEE_NEST);
            versionMaterials.put(GooPVersionMaterials.BEE_SPAWN_EGG, Material.BEE_SPAWN_EGG);

            versionMaterials.put(GooPVersionMaterials.HONEYCOMB, Material.HONEYCOMB);
            versionMaterials.put(GooPVersionMaterials.HONEYCOMB_BLOCK, Material.HONEYCOMB_BLOCK);
            versionMaterials.put(GooPVersionMaterials.HONEY_BLOCK, Material.HONEY_BLOCK);
            versionMaterials.put(GooPVersionMaterials.HONEY_BOTTLE, Material.HONEY_BOTTLE);
        }
        //endregion

        //region Minecraft Version 1.16+
        if (mcVersion >= 16.0) {
            versionMaterials.put(GooPVersionMaterials.ANCIENT_DEBRIS, Material.ANCIENT_DEBRIS);

            versionMaterials.put(GooPVersionMaterials.BASALT, Material.BASALT);
            versionMaterials.put(GooPVersionMaterials.BLACKSTONE, Material.BLACKSTONE);
            versionMaterials.put(GooPVersionMaterials.BLACKSTONE_SLAB, Material.BLACKSTONE_SLAB);
            versionMaterials.put(GooPVersionMaterials.BLACKSTONE_STAIRS, Material.BLACKSTONE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.BLACKSTONE_WALL, Material.BLACKSTONE_WALL);

            versionMaterials.put(GooPVersionMaterials.CHAIN, Material.CHAIN);
            versionMaterials.put(GooPVersionMaterials.CHISELED_NETHER_BRICKS, Material.CHISELED_NETHER_BRICKS);
            versionMaterials.put(GooPVersionMaterials.CHISELED_POLISHED_BLACKSTONE, Material.CHISELED_POLISHED_BLACKSTONE);
            versionMaterials.put(GooPVersionMaterials.CRACKED_NETHER_BRICKS, Material.CRACKED_NETHER_BRICKS);
            versionMaterials.put(GooPVersionMaterials.CRACKED_POLISHED_BLACKSTONE_BRICKS, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS);

            versionMaterials.put(GooPVersionMaterials.CRIMSON_BUTTON, Material.CRIMSON_BUTTON);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_DOOR, Material.CRIMSON_DOOR);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_FENCE, Material.CRIMSON_FENCE);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_FENCE_GATE, Material.CRIMSON_FENCE_GATE);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_FUNGUS, Material.CRIMSON_FUNGUS);

            versionMaterials.put(GooPVersionMaterials.CRIMSON_HYPHAE, Material.CRIMSON_HYPHAE);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_NYLIUM, Material.CRIMSON_NYLIUM);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_PLANKS, Material.CRIMSON_PLANKS);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_PRESSURE_PLATE, Material.CRIMSON_PRESSURE_PLATE);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_ROOTS, Material.CRIMSON_ROOTS);

            versionMaterials.put(GooPVersionMaterials.CRIMSON_SIGN, Material.CRIMSON_SIGN);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_SLAB, Material.CRIMSON_SLAB);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_STAIRS, Material.CRIMSON_STAIRS);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_STEM, Material.CRIMSON_STEM);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_TRAPDOOR, Material.CRIMSON_TRAPDOOR);

            versionMaterials.put(GooPVersionMaterials.CRIMSON_WALL_SIGN, Material.CRIMSON_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.CRYING_OBSIDIAN, Material.CRYING_OBSIDIAN);
            versionMaterials.put(GooPVersionMaterials.GILDED_BLACKSTONE, Material.GILDED_BLACKSTONE);

            versionMaterials.put(GooPVersionMaterials.LODESTONE, Material.LODESTONE);

            versionMaterials.put(GooPVersionMaterials.MUSIC_DISC_PIGSTEP, Material.MUSIC_DISC_PIGSTEP);

            versionMaterials.put(GooPVersionMaterials.NETHERITE_BLOCK, Material.NETHERITE_BLOCK);
            versionMaterials.put(GooPVersionMaterials.NETHER_GOLD_ORE, Material.NETHER_GOLD_ORE);
            versionMaterials.put(GooPVersionMaterials.NETHER_SPROUTS, Material.NETHER_SPROUTS);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_AXE, Material.NETHERITE_AXE);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_BOOTS, Material.NETHERITE_BOOTS);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_CHESTPLATE, Material.NETHERITE_CHESTPLATE);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_HELMET, Material.NETHERITE_HELMET);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_HOE, Material.NETHERITE_HOE);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_INGOT, Material.NETHERITE_INGOT);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_LEGGINGS, Material.NETHERITE_LEGGINGS);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_PICKAXE, Material.NETHERITE_PICKAXE);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_SCRAP, Material.NETHERITE_SCRAP);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_SHOVEL, Material.NETHERITE_SHOVEL);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_SWORD, Material.NETHERITE_SWORD);

            versionMaterials.put(GooPVersionMaterials.POLISHED_BASALT, Material.POLISHED_BASALT);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE, Material.POLISHED_BLACKSTONE);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_BRICKS, Material.POLISHED_BLACKSTONE_BRICKS);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_BRICK_SLAB, Material.POLISHED_BLACKSTONE_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_BRICK_STAIRS, Material.POLISHED_BLACKSTONE_BRICK_STAIRS);

            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_BRICK_WALL, Material.POLISHED_BLACKSTONE_BRICK_WALL);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_PRESSURE_PLATE, Material.POLISHED_BLACKSTONE_PRESSURE_PLATE);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_SLAB, Material.POLISHED_BLACKSTONE_SLAB);
            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_STAIRS, Material.POLISHED_BLACKSTONE_STAIRS);

            versionMaterials.put(GooPVersionMaterials.POLISHED_BLACKSTONE_WALL, Material.POLISHED_BLACKSTONE_WALL);

            versionMaterials.put(GooPVersionMaterials.POTTED_CRIMSON_FUNGUS, Material.POTTED_CRIMSON_FUNGUS);
            versionMaterials.put(GooPVersionMaterials.POTTED_CRIMSON_ROOTS, Material.POTTED_CRIMSON_ROOTS);
            versionMaterials.put(GooPVersionMaterials.POTTED_WARPED_FUNGUS, Material.POTTED_WARPED_FUNGUS);
            versionMaterials.put(GooPVersionMaterials.POTTED_WARPED_ROOTS, Material.POTTED_WARPED_ROOTS);
            versionMaterials.put(GooPVersionMaterials.PIGLIN_BANNER_PATTERN, Material.PIGLIN_BANNER_PATTERN);

            versionMaterials.put(GooPVersionMaterials.HOGLIN_SPAWN_EGG, Material.HOGLIN_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.PIGLIN_SPAWN_EGG, Material.PIGLIN_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.ZOMBIFIED_PIGLIN_SPAWN_EGG, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.STRIDER_SPAWN_EGG, Material.STRIDER_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.ZOGLIN_SPAWN_EGG, Material.ZOGLIN_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.WARPED_FUNGUS_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK);

            versionMaterials.put(GooPVersionMaterials.QUARTZ_BRICKS, Material.QUARTZ_BRICKS);

            versionMaterials.put(GooPVersionMaterials.RESPAWN_ANCHOR, Material.RESPAWN_ANCHOR);

            versionMaterials.put(GooPVersionMaterials.SHROOMLIGHT, Material.SHROOMLIGHT);
            versionMaterials.put(GooPVersionMaterials.SOUL_CAMPFIRE, Material.SOUL_CAMPFIRE);
            versionMaterials.put(GooPVersionMaterials.SOUL_FIRE, Material.SOUL_FIRE);
            versionMaterials.put(GooPVersionMaterials.SOUL_LANTERN, Material.SOUL_LANTERN);
            versionMaterials.put(GooPVersionMaterials.SOUL_SOIL, Material.SOUL_SOIL);

            versionMaterials.put(GooPVersionMaterials.SOUL_TORCH, Material.SOUL_TORCH);
            versionMaterials.put(GooPVersionMaterials.SOUL_WALL_TORCH, Material.SOUL_WALL_TORCH);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_HYPHAE);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_CRIMSON_STEM, Material.STRIPPED_CRIMSON_STEM);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_WARPED_HYPHAE, Material.STRIPPED_WARPED_HYPHAE);

            versionMaterials.put(GooPVersionMaterials.STRIPPED_WARPED_STEM, Material.STRIPPED_WARPED_STEM);

            versionMaterials.put(GooPVersionMaterials.TARGET, Material.TARGET);
            versionMaterials.put(GooPVersionMaterials.TWISTING_VINES, Material.TWISTING_VINES);
            versionMaterials.put(GooPVersionMaterials.TWISTING_VINES_PLANT, Material.TWISTING_VINES_PLANT);

            versionMaterials.put(GooPVersionMaterials.WARPED_BUTTON, Material.WARPED_BUTTON);
            versionMaterials.put(GooPVersionMaterials.WARPED_DOOR, Material.WARPED_DOOR);
            versionMaterials.put(GooPVersionMaterials.WARPED_FENCE, Material.WARPED_FENCE);
            versionMaterials.put(GooPVersionMaterials.WARPED_FENCE_GATE, Material.WARPED_FENCE_GATE);
            versionMaterials.put(GooPVersionMaterials.WARPED_FUNGUS, Material.WARPED_FUNGUS);

            versionMaterials.put(GooPVersionMaterials.WARPED_HYPHAE, Material.WARPED_HYPHAE);
            versionMaterials.put(GooPVersionMaterials.WARPED_NYLIUM, Material.WARPED_NYLIUM);
            versionMaterials.put(GooPVersionMaterials.WARPED_PLANKS, Material.WARPED_PLANKS);
            versionMaterials.put(GooPVersionMaterials.WARPED_PRESSURE_PLATE, Material.WARPED_PRESSURE_PLATE);
            versionMaterials.put(GooPVersionMaterials.WARPED_ROOTS, Material.WARPED_ROOTS);

            versionMaterials.put(GooPVersionMaterials.WARPED_SIGN, Material.WARPED_SIGN);
            versionMaterials.put(GooPVersionMaterials.WARPED_SLAB, Material.WARPED_SLAB);
            versionMaterials.put(GooPVersionMaterials.WARPED_STAIRS, Material.WARPED_STAIRS);
            versionMaterials.put(GooPVersionMaterials.WARPED_STEM, Material.WARPED_STEM);
            versionMaterials.put(GooPVersionMaterials.WARPED_TRAPDOOR, Material.WARPED_TRAPDOOR);

            versionMaterials.put(GooPVersionMaterials.WARPED_WALL_SIGN, Material.WARPED_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.WARPED_WART_BLOCK, Material.WARPED_WART_BLOCK);
            versionMaterials.put(GooPVersionMaterials.WEEPING_VINES, Material.WEEPING_VINES);
            versionMaterials.put(GooPVersionMaterials.WEEPING_VINES_PLANT, Material.WEEPING_VINES_PLANT);
        }
        //endregion

        //region Minecraft Version 1.17+
        if (mcVersion >= 17.0) {
            //* YE-OLD-MMO
            versionMaterials.put(GooPVersionMaterials.AXOLOTL_SPAWN_EGG, Material.AXOLOTL_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.AMETHYST_SHARD, Material.AMETHYST_SHARD);
            versionMaterials.put(GooPVersionMaterials.AXOLOTL_BUCKET, Material.AXOLOTL_BUCKET);
            versionMaterials.put(GooPVersionMaterials.AZALEA, Material.AZALEA);
            versionMaterials.put(GooPVersionMaterials.AZALEA_LEAVES, Material.AZALEA_LEAVES);
            versionMaterials.put(GooPVersionMaterials.AMETHYST_BLOCK, Material.AMETHYST_BLOCK);
            versionMaterials.put(GooPVersionMaterials.AMETHYST_CLUSTER, Material.AMETHYST_CLUSTER);

            versionMaterials.put(GooPVersionMaterials.BUDDING_AMETHYST, Material.BUDDING_AMETHYST);
            versionMaterials.put(GooPVersionMaterials.BIG_DRIPLEAF, Material.BIG_DRIPLEAF);
            versionMaterials.put(GooPVersionMaterials.BUNDLE, Material.BUNDLE);
            versionMaterials.put(GooPVersionMaterials.BIG_DRIPLEAF_STEM, Material.BIG_DRIPLEAF_STEM);
            versionMaterials.put(GooPVersionMaterials.BLACK_CANDLE_CAKE, Material.BLACK_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.BLUE_CANDLE, Material.BLUE_CANDLE);
            versionMaterials.put(GooPVersionMaterials.BROWN_CANDLE, Material.BROWN_CANDLE);
            versionMaterials.put(GooPVersionMaterials.BLACK_CANDLE, Material.BLACK_CANDLE);
            versionMaterials.put(GooPVersionMaterials.BLUE_CANDLE_CAKE, Material.BLUE_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.BROWN_CANDLE_CAKE, Material.BROWN_CANDLE_CAKE);

            versionMaterials.put(GooPVersionMaterials.CANDLE, Material.CANDLE);
            versionMaterials.put(GooPVersionMaterials.COPPER_INGOT, Material.COPPER_INGOT);
            versionMaterials.put(GooPVersionMaterials.CUT_COPPER, Material.CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.CUT_COPPER_STAIRS, Material.CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.COPPER_BLOCK, Material.COPPER_BLOCK);
            versionMaterials.put(GooPVersionMaterials.COPPER_ORE, Material.COPPER_ORE);
            versionMaterials.put(GooPVersionMaterials.CALCITE, Material.CALCITE);
            versionMaterials.put(GooPVersionMaterials.COBBLED_DEEPSLATE, Material.COBBLED_DEEPSLATE);
            versionMaterials.put(GooPVersionMaterials.CUT_COPPER_SLAB, Material.CUT_COPPER_SLAB);
            versionMaterials.put(GooPVersionMaterials.CRACKED_DEEPSLATE_BRICKS, Material.CRACKED_DEEPSLATE_BRICKS);
            versionMaterials.put(GooPVersionMaterials.CRACKED_DEEPSLATE_TILES, Material.CRACKED_DEEPSLATE_TILES);
            versionMaterials.put(GooPVersionMaterials.COBBLED_DEEPSLATE_WALL, Material.COBBLED_DEEPSLATE_WALL);
            versionMaterials.put(GooPVersionMaterials.COBBLED_DEEPSLATE_STAIRS, Material.COBBLED_DEEPSLATE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.COBBLED_DEEPSLATE_SLAB, Material.COBBLED_DEEPSLATE_SLAB);
            versionMaterials.put(GooPVersionMaterials.CUT_RED_SANDSTONE, Material.CUT_RED_SANDSTONE);
            versionMaterials.put(GooPVersionMaterials.CHISELED_DEEPSLATE, Material.CHISELED_DEEPSLATE);
            versionMaterials.put(GooPVersionMaterials.CYAN_CANDLE_CAKE, Material.CYAN_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.CAVE_VINES, Material.CAVE_VINES);
            versionMaterials.put(GooPVersionMaterials.CAVE_VINES_PLANT, Material.CAVE_VINES_PLANT);
            versionMaterials.put(GooPVersionMaterials.CYAN_CANDLE, Material.CYAN_CANDLE);
            versionMaterials.put(GooPVersionMaterials.CANDLE_CAKE, Material.CANDLE_CAKE);

            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_COPPER_ORE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_GOLD_ORE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE, Material.DEEPSLATE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_COAL_ORE);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_IRON_ORE);
            versionMaterials.put(GooPVersionMaterials.DRIPSTONE_BLOCK, Material.DRIPSTONE_BLOCK);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_TILES, Material.DEEPSLATE_TILES);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_BRICKS, Material.DEEPSLATE_BRICKS);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_BRICK_WALL, Material.DEEPSLATE_BRICK_WALL);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_TILE_WALL, Material.DEEPSLATE_TILE_WALL);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_BRICK_STAIRS, Material.DEEPSLATE_BRICK_STAIRS);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_TILE_STAIRS, Material.DEEPSLATE_TILE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_BRICK_SLAB, Material.DEEPSLATE_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.DEEPSLATE_TILE_SLAB, Material.DEEPSLATE_TILE_SLAB);
            versionMaterials.put(GooPVersionMaterials.DIRT_PATH, Material.DIRT_PATH);   //renamed from GRASS_PATH
            versionMaterials.put(GooPVersionMaterials.GRASS_PATH, Material.DIRT_PATH);   //renamed from GRASS_PATH

            versionMaterials.put(GooPVersionMaterials.EXPOSED_CUT_COPPER_STAIRS, Material.EXPOSED_CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_CUT_COPPER, Material.EXPOSED_CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_COPPER, Material.EXPOSED_COPPER);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_CUT_COPPER_SLAB, Material.EXPOSED_CUT_COPPER_SLAB);

            versionMaterials.put(GooPVersionMaterials.FLOWERING_AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES);
            versionMaterials.put(GooPVersionMaterials.FLOWERING_AZALEA, Material.FLOWERING_AZALEA);

            versionMaterials.put(GooPVersionMaterials.GLOW_LICHEN, Material.GLOW_LICHEN);
            versionMaterials.put(GooPVersionMaterials.GLOW_INK_SAC, Material.GLOW_INK_SAC);
            versionMaterials.put(GooPVersionMaterials.GLOW_SQUID_SPAWN_EGG, Material.GLOW_SQUID_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.GOAT_SPAWN_EGG, Material.GOAT_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.GLOW_ITEM_FRAME, Material.GLOW_ITEM_FRAME);
            versionMaterials.put(GooPVersionMaterials.GLOW_BERRIES, Material.GLOW_BERRIES);
            versionMaterials.put(GooPVersionMaterials.GRAY_CANDLE, Material.GRAY_CANDLE);
            versionMaterials.put(GooPVersionMaterials.GREEN_CANDLE, Material.GREEN_CANDLE);
            versionMaterials.put(GooPVersionMaterials.GRAY_CANDLE_CAKE, Material.GRAY_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.GREEN_CANDLE_CAKE, Material.GREEN_CANDLE_CAKE);

            versionMaterials.put(GooPVersionMaterials.HANGING_ROOTS, Material.HANGING_ROOTS);

            versionMaterials.put(GooPVersionMaterials.LIGHT, Material.LIGHT);
            versionMaterials.put(GooPVersionMaterials.LIGHT_GRAY_CANDLE, Material.LIGHT_GRAY_CANDLE);
            versionMaterials.put(GooPVersionMaterials.LIME_CANDLE, Material.LIME_CANDLE);
            versionMaterials.put(GooPVersionMaterials.LIGHTNING_ROD, Material.LIGHTNING_ROD);
            versionMaterials.put(GooPVersionMaterials.LIGHT_BLUE_CANDLE, Material.LIGHT_BLUE_CANDLE);
            versionMaterials.put(GooPVersionMaterials.LARGE_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD);
            versionMaterials.put(GooPVersionMaterials.LAVA_CAULDRON, Material.LAVA_CAULDRON);
            versionMaterials.put(GooPVersionMaterials.LIGHT_BLUE_CANDLE_CAKE, Material.LIGHT_BLUE_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.LIME_CANDLE_CAKE, Material.LIME_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.LIGHT_GRAY_CANDLE_CAKE, Material.LIGHT_GRAY_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.INFESTED_DEEPSLATE, Material.INFESTED_DEEPSLATE);

            versionMaterials.put(GooPVersionMaterials.MOSS_CARPET, Material.MOSS_CARPET);
            versionMaterials.put(GooPVersionMaterials.MOSS_BLOCK, Material.MOSS_BLOCK);
            versionMaterials.put(GooPVersionMaterials.MAGENTA_CANDLE, Material.MAGENTA_CANDLE);
            versionMaterials.put(GooPVersionMaterials.MEDIUM_AMETHYST_BUD, Material.MEDIUM_AMETHYST_BUD);
            versionMaterials.put(GooPVersionMaterials.MAGENTA_CANDLE_CAKE, Material.MAGENTA_CANDLE_CAKE);

            versionMaterials.put(GooPVersionMaterials.OXIDIZED_CUT_COPPER_STAIRS, Material.OXIDIZED_CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_COPPER, Material.OXIDIZED_COPPER);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_CUT_COPPER_SLAB, Material.OXIDIZED_CUT_COPPER_SLAB);
            versionMaterials.put(GooPVersionMaterials.ORANGE_CANDLE, Material.ORANGE_CANDLE);
            versionMaterials.put(GooPVersionMaterials.ORANGE_CANDLE_CAKE, Material.ORANGE_CANDLE_CAKE);

            versionMaterials.put(GooPVersionMaterials.POLISHED_DEEPSLATE, Material.POLISHED_DEEPSLATE);
            versionMaterials.put(GooPVersionMaterials.POLISHED_DEEPSLATE_WALL, Material.POLISHED_DEEPSLATE_WALL);
            versionMaterials.put(GooPVersionMaterials.POLISHED_DEEPSLATE_STAIRS, Material.POLISHED_DEEPSLATE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.POLISHED_DEEPSLATE_SLAB, Material.POLISHED_DEEPSLATE_SLAB);
            versionMaterials.put(GooPVersionMaterials.PURPLE_CANDLE_CAKE, Material.PURPLE_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.POWDER_SNOW, Material.POWDER_SNOW);
            versionMaterials.put(GooPVersionMaterials.POTTED_AZALEA_BUSH, Material.POTTED_AZALEA_BUSH);
            versionMaterials.put(GooPVersionMaterials.POTTED_FLOWERING_AZALEA_BUSH, Material.POTTED_FLOWERING_AZALEA_BUSH);
            versionMaterials.put(GooPVersionMaterials.POWDER_SNOW_BUCKET, Material.POWDER_SNOW_BUCKET);
            versionMaterials.put(GooPVersionMaterials.PINK_CANDLE, Material.PINK_CANDLE);
            versionMaterials.put(GooPVersionMaterials.POINTED_DRIPSTONE, Material.POINTED_DRIPSTONE);
            versionMaterials.put(GooPVersionMaterials.PURPLE_CANDLE, Material.PURPLE_CANDLE);
            versionMaterials.put(GooPVersionMaterials.POWDER_SNOW_CAULDRON, Material.POWDER_SNOW_CAULDRON);
            versionMaterials.put(GooPVersionMaterials.PINK_CANDLE_CAKE, Material.PINK_CANDLE_CAKE);

            versionMaterials.put(GooPVersionMaterials.ROOTED_DIRT, Material.ROOTED_DIRT);
            versionMaterials.put(GooPVersionMaterials.RAW_IRON_BLOCK, Material.RAW_IRON_BLOCK);
            versionMaterials.put(GooPVersionMaterials.RAW_COPPER_BLOCK, Material.RAW_COPPER_BLOCK);
            versionMaterials.put(GooPVersionMaterials.RAW_GOLD_BLOCK, Material.RAW_GOLD_BLOCK);
            versionMaterials.put(GooPVersionMaterials.RAW_IRON, Material.RAW_IRON);
            versionMaterials.put(GooPVersionMaterials.RAW_COPPER, Material.RAW_COPPER);
            versionMaterials.put(GooPVersionMaterials.RAW_GOLD, Material.RAW_GOLD);
            versionMaterials.put(GooPVersionMaterials.RED_CANDLE, Material.RED_CANDLE);
            versionMaterials.put(GooPVersionMaterials.RED_CANDLE_CAKE, Material.RED_CANDLE_CAKE);

            versionMaterials.put(GooPVersionMaterials.SMALL_DRIPLEAF, Material.SMALL_DRIPLEAF);
            versionMaterials.put(GooPVersionMaterials.SMOOTH_BASALT, Material.SMOOTH_BASALT);
            versionMaterials.put(GooPVersionMaterials.SPORE_BLOSSOM, Material.SPORE_BLOSSOM);
            versionMaterials.put(GooPVersionMaterials.SPYGLASS, Material.SPYGLASS);
            versionMaterials.put(GooPVersionMaterials.SCULK_SENSOR, Material.SCULK_SENSOR);
            versionMaterials.put(GooPVersionMaterials.SMALL_AMETHYST_BUD, Material.SMALL_AMETHYST_BUD);

            versionMaterials.put(GooPVersionMaterials.TUFF, Material.TUFF);
            versionMaterials.put(GooPVersionMaterials.TINTED_GLASS, Material.TINTED_GLASS);

            versionMaterials.put(GooPVersionMaterials.WEATHERED_CUT_COPPER_SLAB, Material.WEATHERED_CUT_COPPER_SLAB);
            versionMaterials.put(GooPVersionMaterials.WAXED_COPPER_BLOCK, Material.WAXED_COPPER_BLOCK);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_COPPER, Material.WAXED_EXPOSED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_COPPER, Material.WAXED_WEATHERED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_COPPER, Material.WAXED_OXIDIZED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_CUT_COPPER, Material.WAXED_CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_CUT_COPPER, Material.WAXED_EXPOSED_CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_CUT_COPPER, Material.WAXED_WEATHERED_CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_CUT_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_CUT_COPPER_STAIRS, Material.WAXED_CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_CUT_COPPER_STAIRS, Material.WAXED_EXPOSED_CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_CUT_COPPER_STAIRS, Material.WAXED_WEATHERED_CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.WAXED_CUT_COPPER_SLAB, Material.WAXED_CUT_COPPER_SLAB);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_CUT_COPPER_SLAB, Material.WAXED_EXPOSED_CUT_COPPER_SLAB);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_CUT_COPPER_SLAB, Material.WAXED_WEATHERED_CUT_COPPER_SLAB);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_CUT_COPPER_SLAB, Material.WAXED_OXIDIZED_CUT_COPPER_SLAB);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_CUT_COPPER_STAIRS, Material.WEATHERED_CUT_COPPER_STAIRS);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_CUT_COPPER, Material.WEATHERED_CUT_COPPER);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_COPPER, Material.WEATHERED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WHITE_CANDLE, Material.WHITE_CANDLE);
            versionMaterials.put(GooPVersionMaterials.WHITE_CANDLE_CAKE, Material.WHITE_CANDLE_CAKE);
            versionMaterials.put(GooPVersionMaterials.WATER_CAULDRON, Material.WATER_CAULDRON);

            versionMaterials.put(GooPVersionMaterials.YELLOW_CANDLE, Material.YELLOW_CANDLE);
            versionMaterials.put(GooPVersionMaterials.YELLOW_CANDLE_CAKE, Material.YELLOW_CANDLE_CAKE);
            // YE-OLD-MMO */
        }
        //endregion

        //region Minecraft Version 1.17-
        if (mcVersion < 17.0) {
            versionMaterials.put(GooPVersionMaterials.GRASS_PATH, GetMaterialFromString("GRASS_PATH"));
        }
        //endregion

        //region Minecraft Version 1.18+
        if (mcVersion >= 18.0) {
            //* YE-OLD-MMO
            versionMaterials.put(GooPVersionMaterials.MUSIC_DISC_OTHERSIDE, Material.MUSIC_DISC_OTHERSIDE);
            // YE-OLD-MMO */
        }
        //endregion

        //region Minecraft Version 1.19+
        if (mcVersion >= 19.0) {
            versionMaterials.put(GooPVersionMaterials.MUD, Material.MUD);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_PLANKS, Material.MANGROVE_PLANKS);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_PROPAGULE, Material.MANGROVE_PROPAGULE);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_LOG, Material.MANGROVE_LOG);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_ROOTS, Material.MANGROVE_ROOTS);
            versionMaterials.put(GooPVersionMaterials.MUDDY_MANGROVE_ROOTS, Material.MUDDY_MANGROVE_ROOTS);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_MANGROVE_LOG, Material.STRIPPED_MANGROVE_LOG);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_MANGROVE_WOOD, Material.STRIPPED_MANGROVE_WOOD);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_WOOD, Material.MANGROVE_WOOD);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_LEAVES, Material.MANGROVE_LEAVES);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_SLAB, Material.MANGROVE_SLAB);
            versionMaterials.put(GooPVersionMaterials.MUD_BRICK_SLAB, Material.MUD_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_FENCE, Material.MANGROVE_FENCE);
            versionMaterials.put(GooPVersionMaterials.PACKED_MUD, Material.PACKED_MUD);
            versionMaterials.put(GooPVersionMaterials.MUD_BRICKS, Material.MUD_BRICKS);
            versionMaterials.put(GooPVersionMaterials.REINFORCED_DEEPSLATE, Material.REINFORCED_DEEPSLATE);
            versionMaterials.put(GooPVersionMaterials.MUD_BRICK_STAIRS, Material.MUD_BRICK_STAIRS);
            versionMaterials.put(GooPVersionMaterials.SCULK, Material.SCULK);
            versionMaterials.put(GooPVersionMaterials.SCULK_VEIN, Material.SCULK_VEIN);
            versionMaterials.put(GooPVersionMaterials.SCULK_CATALYST, Material.SCULK_CATALYST);
            versionMaterials.put(GooPVersionMaterials.SCULK_SHRIEKER, Material.SCULK_SHRIEKER);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_STAIRS, Material.MANGROVE_STAIRS);
            versionMaterials.put(GooPVersionMaterials.MUD_BRICK_WALL, Material.MUD_BRICK_WALL);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_BUTTON, Material.MANGROVE_BUTTON);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_PRESSURE_PLATE, Material.MANGROVE_PRESSURE_PLATE);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_DOOR, Material.MANGROVE_DOOR);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_TRAPDOOR, Material.MANGROVE_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_FENCE_GATE, Material.MANGROVE_FENCE_GATE);
            versionMaterials.put(GooPVersionMaterials.OAK_CHEST_BOAT, Material.OAK_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.SPRUCE_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.BIRCH_CHEST_BOAT, Material.BIRCH_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.JUNGLE_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.ACACIA_CHEST_BOAT, Material.ACACIA_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.DARK_OAK_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_BOAT, Material.MANGROVE_BOAT);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_SIGN, Material.MANGROVE_SIGN);
            versionMaterials.put(GooPVersionMaterials.TADPOLE_BUCKET, Material.TADPOLE_BUCKET);
            versionMaterials.put(GooPVersionMaterials.RECOVERY_COMPASS, Material.RECOVERY_COMPASS);
            versionMaterials.put(GooPVersionMaterials.ALLAY_SPAWN_EGG, Material.ALLAY_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.FROG_SPAWN_EGG, Material.FROG_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.TADPOLE_SPAWN_EGG, Material.TADPOLE_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.WARDEN_SPAWN_EGG, Material.WARDEN_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.MUSIC_DISC_5, Material.MUSIC_DISC_5);
            versionMaterials.put(GooPVersionMaterials.DISC_FRAGMENT_5, Material.DISC_FRAGMENT_5);
            versionMaterials.put(GooPVersionMaterials.GOAT_HORN, Material.GOAT_HORN);
            versionMaterials.put(GooPVersionMaterials.OCHRE_FROGLIGHT, Material.OCHRE_FROGLIGHT);
            versionMaterials.put(GooPVersionMaterials.VERDANT_FROGLIGHT, Material.VERDANT_FROGLIGHT);
            versionMaterials.put(GooPVersionMaterials.PEARLESCENT_FROGLIGHT, Material.PEARLESCENT_FROGLIGHT);
            versionMaterials.put(GooPVersionMaterials.FROGSPAWN, Material.FROGSPAWN);
            versionMaterials.put(GooPVersionMaterials.ECHO_SHARD, Material.ECHO_SHARD);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_WALL_SIGN, Material.MANGROVE_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.POTTED_MANGROVE_PROPAGULE, Material.POTTED_MANGROVE_PROPAGULE);
        }
        //endregion

        //region Minecraft Version 1.19.4+
        if (mcVersion >= 19.4) {
            versionMaterials.put(GooPVersionMaterials.CHERRY_PLANKS, Material.CHERRY_PLANKS);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_PLANKS, Material.BAMBOO_PLANKS);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_MOSAIC, Material.BAMBOO_MOSAIC);
            versionMaterials.put(GooPVersionMaterials.CHERRY_SAPLING, Material.CHERRY_SAPLING);
            versionMaterials.put(GooPVersionMaterials.SUSPICIOUS_SAND, Material.SUSPICIOUS_SAND);
            versionMaterials.put(GooPVersionMaterials.CHERRY_LOG, Material.CHERRY_LOG);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_BLOCK, Material.BAMBOO_BLOCK);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_CHERRY_LOG, Material.STRIPPED_CHERRY_LOG);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_CHERRY_WOOD, Material.STRIPPED_CHERRY_WOOD);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_BAMBOO_BLOCK, Material.STRIPPED_BAMBOO_BLOCK);
            versionMaterials.put(GooPVersionMaterials.CHERRY_WOOD, Material.CHERRY_WOOD);
            versionMaterials.put(GooPVersionMaterials.CHERRY_LEAVES, Material.CHERRY_LEAVES);
            versionMaterials.put(GooPVersionMaterials.TORCHFLOWER, Material.TORCHFLOWER);
            versionMaterials.put(GooPVersionMaterials.PINK_PETALS, Material.PINK_PETALS);
            versionMaterials.put(GooPVersionMaterials.CHERRY_SLAB, Material.CHERRY_SLAB);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_SLAB, Material.BAMBOO_SLAB);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_MOSAIC_SLAB, Material.BAMBOO_MOSAIC_SLAB);
            versionMaterials.put(GooPVersionMaterials.CHISELED_BOOKSHELF, Material.CHISELED_BOOKSHELF);
            versionMaterials.put(GooPVersionMaterials.DECORATED_POT, Material.DECORATED_POT);
            versionMaterials.put(GooPVersionMaterials.CHERRY_FENCE, Material.CHERRY_FENCE);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_FENCE, Material.BAMBOO_FENCE);
            versionMaterials.put(GooPVersionMaterials.CHERRY_STAIRS, Material.CHERRY_STAIRS);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_STAIRS, Material.BAMBOO_STAIRS);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_MOSAIC_STAIRS, Material.BAMBOO_MOSAIC_STAIRS);
            versionMaterials.put(GooPVersionMaterials.CHERRY_BUTTON, Material.CHERRY_BUTTON);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_BUTTON, Material.BAMBOO_BUTTON);
            versionMaterials.put(GooPVersionMaterials.CHERRY_PRESSURE_PLATE, Material.CHERRY_PRESSURE_PLATE);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_PRESSURE_PLATE, Material.BAMBOO_PRESSURE_PLATE);
            versionMaterials.put(GooPVersionMaterials.CHERRY_DOOR, Material.CHERRY_DOOR);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_DOOR, Material.BAMBOO_DOOR);
            versionMaterials.put(GooPVersionMaterials.CHERRY_TRAPDOOR, Material.CHERRY_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_TRAPDOOR, Material.BAMBOO_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.CHERRY_FENCE_GATE, Material.CHERRY_FENCE_GATE);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_FENCE_GATE, Material.BAMBOO_FENCE_GATE);
            versionMaterials.put(GooPVersionMaterials.CHERRY_BOAT, Material.CHERRY_BOAT);
            versionMaterials.put(GooPVersionMaterials.CHERRY_CHEST_BOAT, Material.CHERRY_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_RAFT, Material.BAMBOO_RAFT);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_CHEST_RAFT, Material.BAMBOO_CHEST_RAFT);
            versionMaterials.put(GooPVersionMaterials.CHERRY_SIGN, Material.CHERRY_SIGN);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_SIGN, Material.BAMBOO_SIGN);
            versionMaterials.put(GooPVersionMaterials.OAK_HANGING_SIGN, Material.OAK_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.SPRUCE_HANGING_SIGN, Material.SPRUCE_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.BIRCH_HANGING_SIGN, Material.BIRCH_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.JUNGLE_HANGING_SIGN, Material.JUNGLE_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.ACACIA_HANGING_SIGN, Material.ACACIA_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.CHERRY_HANGING_SIGN, Material.CHERRY_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.DARK_OAK_HANGING_SIGN, Material.DARK_OAK_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_HANGING_SIGN, Material.MANGROVE_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_HANGING_SIGN, Material.BAMBOO_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_HANGING_SIGN, Material.CRIMSON_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.WARPED_HANGING_SIGN, Material.WARPED_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.CAMEL_SPAWN_EGG, Material.CAMEL_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.ENDER_DRAGON_SPAWN_EGG, Material.ENDER_DRAGON_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.IRON_GOLEM_SPAWN_EGG, Material.IRON_GOLEM_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.SNIFFER_SPAWN_EGG, Material.SNIFFER_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.SNOW_GOLEM_SPAWN_EGG, Material.SNOW_GOLEM_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.WITHER_SPAWN_EGG, Material.WITHER_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.PIGLIN_HEAD, Material.PIGLIN_HEAD);
            versionMaterials.put(GooPVersionMaterials.TORCHFLOWER_SEEDS, Material.TORCHFLOWER_SEEDS);
            versionMaterials.put(GooPVersionMaterials.BRUSH, Material.BRUSH);
            versionMaterials.put(GooPVersionMaterials.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.CHERRY_WALL_SIGN, Material.CHERRY_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_WALL_SIGN, Material.BAMBOO_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.OAK_WALL_HANGING_SIGN, Material.OAK_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.SPRUCE_WALL_HANGING_SIGN, Material.SPRUCE_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.BIRCH_WALL_HANGING_SIGN, Material.BIRCH_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.ACACIA_WALL_HANGING_SIGN, Material.ACACIA_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.CHERRY_WALL_HANGING_SIGN, Material.CHERRY_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.JUNGLE_WALL_HANGING_SIGN, Material.JUNGLE_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.DARK_OAK_WALL_HANGING_SIGN, Material.DARK_OAK_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.MANGROVE_WALL_HANGING_SIGN, Material.MANGROVE_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.CRIMSON_WALL_HANGING_SIGN, Material.CRIMSON_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.WARPED_WALL_HANGING_SIGN, Material.WARPED_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.BAMBOO_WALL_HANGING_SIGN, Material.BAMBOO_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.POTTED_TORCHFLOWER, Material.POTTED_TORCHFLOWER);
            versionMaterials.put(GooPVersionMaterials.POTTED_CHERRY_SAPLING, Material.POTTED_CHERRY_SAPLING);
            versionMaterials.put(GooPVersionMaterials.PIGLIN_WALL_HEAD, Material.PIGLIN_WALL_HEAD);
            versionMaterials.put(GooPVersionMaterials.TORCHFLOWER_CROP, Material.TORCHFLOWER_CROP);
        }
        //endregion

        //region Minecraft Version 1.20.1+
        if (mcVersion >= 20.1) {
            versionMaterials.put(GooPVersionMaterials.SUSPICIOUS_GRAVEL, Material.SUSPICIOUS_GRAVEL);
            versionMaterials.put(GooPVersionMaterials.PITCHER_PLANT, Material.PITCHER_PLANT);
            versionMaterials.put(GooPVersionMaterials.SNIFFER_EGG, Material.SNIFFER_EGG);
            versionMaterials.put(GooPVersionMaterials.CALIBRATED_SCULK_SENSOR, Material.CALIBRATED_SCULK_SENSOR);
            versionMaterials.put(GooPVersionMaterials.PITCHER_POD, Material.PITCHER_POD);
            versionMaterials.put(GooPVersionMaterials.MUSIC_DISC_RELIC, Material.MUSIC_DISC_RELIC);
            versionMaterials.put(GooPVersionMaterials.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.ANGLER_POTTERY_SHERD, Material.ANGLER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.ARCHER_POTTERY_SHERD, Material.ARCHER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.ARMS_UP_POTTERY_SHERD, Material.ARMS_UP_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.BLADE_POTTERY_SHERD, Material.BLADE_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.BREWER_POTTERY_SHERD, Material.BREWER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.BURN_POTTERY_SHERD, Material.BURN_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.DANGER_POTTERY_SHERD, Material.DANGER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.EXPLORER_POTTERY_SHERD, Material.EXPLORER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.FRIEND_POTTERY_SHERD, Material.FRIEND_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.HEART_POTTERY_SHERD, Material.HEART_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.HEARTBREAK_POTTERY_SHERD, Material.HEARTBREAK_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.HOWL_POTTERY_SHERD, Material.HOWL_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.MINER_POTTERY_SHERD, Material.MINER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.MOURNER_POTTERY_SHERD, Material.MOURNER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.PLENTY_POTTERY_SHERD, Material.PLENTY_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.PRIZE_POTTERY_SHERD, Material.PRIZE_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.SHEAF_POTTERY_SHERD, Material.SHEAF_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.SHELTER_POTTERY_SHERD, Material.SHELTER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.SKULL_POTTERY_SHERD, Material.SKULL_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.SNORT_POTTERY_SHERD, Material.SNORT_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.PITCHER_CROP, Material.PITCHER_CROP);

            versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_SKULL, Material.SKULL_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_PRIZE, Material.PRIZE_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_ARMS_UP, Material.ARMS_UP_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_ARCHER, Material.ARCHER_POTTERY_SHERD);
        }
        //endregion

        //region Minecraft Version 1.20.6+
        if (mcVersion < 20.1) {
            if (mcVersion >= 19.4) {
                versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_SKULL, GetMaterialFromString("POTTERY_SHARD_SKULL"));
                versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_PRIZE, GetMaterialFromString("POTTERY_SHARD_PRIZE"));
                versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_ARMS_UP, GetMaterialFromString("POTTERY_SHARD_ARMS_UP"));
                versionMaterials.put(GooPVersionMaterials.POTTERY_SHARD_ARCHER, GetMaterialFromString("POTTERY_SHARD_ARCHER"));

                versionMaterials.put(GooPVersionMaterials.SKULL_POTTERY_SHERD, GetMaterialFromString("POTTERY_SHARD_SKULL"));
                versionMaterials.put(GooPVersionMaterials.PRIZE_POTTERY_SHERD, GetMaterialFromString("POTTERY_SHARD_PRIZE"));
                versionMaterials.put(GooPVersionMaterials.ARMS_UP_POTTERY_SHERD, GetMaterialFromString("POTTERY_SHARD_ARMS_UP"));
                versionMaterials.put(GooPVersionMaterials.ARCHER_POTTERY_SHERD, GetMaterialFromString("POTTERY_SHARD_ARCHER"));
            }
        }
        //endregion

        //region Minecraft Version 1.20.6+
        if (mcVersion >= 20.6) {
            versionMaterials.put(GooPVersionMaterials.SHORT_GRASS, Material.SHORT_GRASS);
            versionMaterials.put(GooPVersionMaterials.TURTLE_SCUTE, Material.TURTLE_SCUTE);
            versionMaterials.put(GooPVersionMaterials.TUFF_SLAB, Material.TUFF_SLAB);
            versionMaterials.put(GooPVersionMaterials.TUFF_STAIRS, Material.TUFF_STAIRS);
            versionMaterials.put(GooPVersionMaterials.TUFF_WALL, Material.TUFF_WALL);
            versionMaterials.put(GooPVersionMaterials.CHISELED_TUFF, Material.CHISELED_TUFF);
            versionMaterials.put(GooPVersionMaterials.POLISHED_TUFF, Material.POLISHED_TUFF);
            versionMaterials.put(GooPVersionMaterials.POLISHED_TUFF_SLAB, Material.POLISHED_TUFF_SLAB);
            versionMaterials.put(GooPVersionMaterials.POLISHED_TUFF_STAIRS, Material.POLISHED_TUFF_STAIRS);
            versionMaterials.put(GooPVersionMaterials.POLISHED_TUFF_WALL, Material.POLISHED_TUFF_WALL);
            versionMaterials.put(GooPVersionMaterials.TUFF_BRICKS, Material.TUFF_BRICKS);
            versionMaterials.put(GooPVersionMaterials.TUFF_BRICK_SLAB, Material.TUFF_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.TUFF_BRICK_STAIRS, Material.TUFF_BRICK_STAIRS);
            versionMaterials.put(GooPVersionMaterials.TUFF_BRICK_WALL, Material.TUFF_BRICK_WALL);
            versionMaterials.put(GooPVersionMaterials.CHISELED_TUFF_BRICKS, Material.CHISELED_TUFF_BRICKS);
            versionMaterials.put(GooPVersionMaterials.HEAVY_CORE, Material.HEAVY_CORE);
            versionMaterials.put(GooPVersionMaterials.CHISELED_COPPER, Material.CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_CHISELED_COPPER, Material.EXPOSED_CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_CHISELED_COPPER, Material.WEATHERED_CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_CHISELED_COPPER, Material.OXIDIZED_CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_CHISELED_COPPER, Material.WAXED_CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_CHISELED_COPPER, Material.WAXED_EXPOSED_CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_CHISELED_COPPER, Material.WAXED_WEATHERED_CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_CHISELED_COPPER, Material.WAXED_OXIDIZED_CHISELED_COPPER);
            versionMaterials.put(GooPVersionMaterials.COPPER_DOOR, Material.COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_COPPER_DOOR, Material.EXPOSED_COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_COPPER_DOOR, Material.WEATHERED_COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_COPPER_DOOR, Material.OXIDIZED_COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_COPPER_DOOR, Material.WAXED_COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_COPPER_DOOR, Material.WAXED_EXPOSED_COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_COPPER_DOOR, Material.WAXED_WEATHERED_COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_COPPER_DOOR, Material.WAXED_OXIDIZED_COPPER_DOOR);
            versionMaterials.put(GooPVersionMaterials.COPPER_TRAPDOOR, Material.COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_COPPER_TRAPDOOR, Material.EXPOSED_COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_COPPER_TRAPDOOR, Material.WEATHERED_COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_COPPER_TRAPDOOR, Material.OXIDIZED_COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_COPPER_TRAPDOOR, Material.WAXED_COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_COPPER_TRAPDOOR, Material.WAXED_EXPOSED_COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_COPPER_TRAPDOOR, Material.WAXED_WEATHERED_COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_COPPER_TRAPDOOR, Material.WAXED_OXIDIZED_COPPER_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.ARMADILLO_SCUTE, Material.ARMADILLO_SCUTE);
            versionMaterials.put(GooPVersionMaterials.WOLF_ARMOR, Material.WOLF_ARMOR);
            versionMaterials.put(GooPVersionMaterials.CRAFTER, Material.CRAFTER);
            versionMaterials.put(GooPVersionMaterials.ARMADILLO_SPAWN_EGG, Material.ARMADILLO_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.BOGGED_SPAWN_EGG, Material.BOGGED_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.BREEZE_SPAWN_EGG, Material.BREEZE_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.WIND_CHARGE, Material.WIND_CHARGE);
            versionMaterials.put(GooPVersionMaterials.MACE, Material.MACE);
            versionMaterials.put(GooPVersionMaterials.FLOW_BANNER_PATTERN, Material.FLOW_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.GUSTER_BANNER_PATTERN, Material.GUSTER_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
            versionMaterials.put(GooPVersionMaterials.FLOW_POTTERY_SHERD, Material.FLOW_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.GUSTER_POTTERY_SHERD, Material.GUSTER_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.SCRAPE_POTTERY_SHERD, Material.SCRAPE_POTTERY_SHERD);
            versionMaterials.put(GooPVersionMaterials.COPPER_GRATE, Material.COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_COPPER_GRATE, Material.EXPOSED_COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_COPPER_GRATE, Material.WEATHERED_COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_COPPER_GRATE, Material.OXIDIZED_COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.WAXED_COPPER_GRATE, Material.WAXED_COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_COPPER_GRATE, Material.WAXED_EXPOSED_COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_COPPER_GRATE, Material.WAXED_WEATHERED_COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_COPPER_GRATE, Material.WAXED_OXIDIZED_COPPER_GRATE);
            versionMaterials.put(GooPVersionMaterials.COPPER_BULB, Material.COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.EXPOSED_COPPER_BULB, Material.EXPOSED_COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.WEATHERED_COPPER_BULB, Material.WEATHERED_COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.OXIDIZED_COPPER_BULB, Material.OXIDIZED_COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.WAXED_COPPER_BULB, Material.WAXED_COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.WAXED_EXPOSED_COPPER_BULB, Material.WAXED_EXPOSED_COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.WAXED_WEATHERED_COPPER_BULB, Material.WAXED_WEATHERED_COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.WAXED_OXIDIZED_COPPER_BULB, Material.WAXED_OXIDIZED_COPPER_BULB);
            versionMaterials.put(GooPVersionMaterials.TRIAL_SPAWNER, Material.TRIAL_SPAWNER);
            versionMaterials.put(GooPVersionMaterials.TRIAL_KEY, Material.TRIAL_KEY);
            versionMaterials.put(GooPVersionMaterials.OMINOUS_TRIAL_KEY, Material.OMINOUS_TRIAL_KEY);
            versionMaterials.put(GooPVersionMaterials.VAULT, Material.VAULT);
            versionMaterials.put(GooPVersionMaterials.OMINOUS_BOTTLE, Material.OMINOUS_BOTTLE);
            versionMaterials.put(GooPVersionMaterials.BREEZE_ROD, Material.BREEZE_ROD);

            versionMaterials.put(GooPVersionMaterials.SCUTE, Material.TURTLE_SCUTE);
            versionMaterials.put(GooPVersionMaterials.GRASS, Material.SHORT_GRASS);
        }
        //endregion

        //region Minecraft Version 1.20.6+
        if (mcVersion < 20.6) {
            versionMaterials.put(GooPVersionMaterials.SCUTE, GetMaterialFromString("SCUTE"));
            versionMaterials.put(GooPVersionMaterials.GRASS, GetMaterialFromString("GRASS"));

            versionMaterials.put(GooPVersionMaterials.TURTLE_SCUTE, GetMaterialFromString("SCUTE"));
            versionMaterials.put(GooPVersionMaterials.SHORT_GRASS, GetMaterialFromString("GRASS"));
        }
        //endregion

        //region Minecraft Version 1.21.1+
        if (mcVersion >= 21.1) {
            versionMaterials.put(GooPVersionMaterials.MUSIC_DISC_CREATOR, Material.MUSIC_DISC_CREATOR);
            versionMaterials.put(GooPVersionMaterials.MUSIC_DISC_CREATOR_MUSIC_BOX, Material.MUSIC_DISC_CREATOR_MUSIC_BOX);
            versionMaterials.put(GooPVersionMaterials.MUSIC_DISC_PRECIPICE, Material.MUSIC_DISC_PRECIPICE);
        }
        //endregion

        //region Minecraft Version 1.21.3+
        if (mcVersion >= 21.3) {
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_PLANKS, Material.PALE_OAK_PLANKS);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_SAPLING, Material.PALE_OAK_SAPLING);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_LOG, Material.PALE_OAK_LOG);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_PALE_OAK_LOG, Material.STRIPPED_PALE_OAK_LOG);
            versionMaterials.put(GooPVersionMaterials.STRIPPED_PALE_OAK_WOOD, Material.STRIPPED_PALE_OAK_WOOD);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_WOOD, Material.PALE_OAK_WOOD);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_LEAVES, Material.PALE_OAK_LEAVES);
            versionMaterials.put(GooPVersionMaterials.PALE_MOSS_CARPET, Material.PALE_MOSS_CARPET);
            versionMaterials.put(GooPVersionMaterials.PALE_HANGING_MOSS, Material.PALE_HANGING_MOSS);
            versionMaterials.put(GooPVersionMaterials.PALE_MOSS_BLOCK, Material.PALE_MOSS_BLOCK);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_SLAB, Material.PALE_OAK_SLAB);
            versionMaterials.put(GooPVersionMaterials.CREAKING_HEART, Material.CREAKING_HEART);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_FENCE, Material.PALE_OAK_FENCE);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_STAIRS, Material.PALE_OAK_STAIRS);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_BUTTON, Material.PALE_OAK_BUTTON);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_PRESSURE_PLATE, Material.PALE_OAK_PRESSURE_PLATE);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_DOOR, Material.PALE_OAK_DOOR);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_TRAPDOOR, Material.PALE_OAK_TRAPDOOR);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_FENCE_GATE, Material.PALE_OAK_FENCE_GATE);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_BOAT, Material.PALE_OAK_BOAT);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_CHEST_BOAT, Material.PALE_OAK_CHEST_BOAT);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_SIGN, Material.PALE_OAK_SIGN);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_HANGING_SIGN, Material.PALE_OAK_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.WHITE_BUNDLE, Material.WHITE_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.ORANGE_BUNDLE, Material.ORANGE_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.MAGENTA_BUNDLE, Material.MAGENTA_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.LIGHT_BLUE_BUNDLE, Material.LIGHT_BLUE_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.YELLOW_BUNDLE, Material.YELLOW_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.LIME_BUNDLE, Material.LIME_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.PINK_BUNDLE, Material.PINK_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.GRAY_BUNDLE, Material.GRAY_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.LIGHT_GRAY_BUNDLE, Material.LIGHT_GRAY_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.CYAN_BUNDLE, Material.CYAN_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.PURPLE_BUNDLE, Material.PURPLE_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.BLUE_BUNDLE, Material.BLUE_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.BROWN_BUNDLE, Material.BROWN_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.GREEN_BUNDLE, Material.GREEN_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.RED_BUNDLE, Material.RED_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.BLACK_BUNDLE, Material.BLACK_BUNDLE);
            versionMaterials.put(GooPVersionMaterials.CREAKING_SPAWN_EGG, Material.CREAKING_SPAWN_EGG);
            versionMaterials.put(GooPVersionMaterials.FIELD_MASONED_BANNER_PATTERN, Material.FIELD_MASONED_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.BORDURE_INDENTED_BANNER_PATTERN, Material.BORDURE_INDENTED_BANNER_PATTERN);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_WALL_SIGN, Material.PALE_OAK_WALL_SIGN);
            versionMaterials.put(GooPVersionMaterials.PALE_OAK_WALL_HANGING_SIGN, Material.PALE_OAK_WALL_HANGING_SIGN);
            versionMaterials.put(GooPVersionMaterials.POTTED_PALE_OAK_SAPLING, Material.POTTED_PALE_OAK_SAPLING);
        }
        //endregion

        //region Minecraft Version 1.21.4+
        if (mcVersion >= 21.4) {
            versionMaterials.put(GooPVersionMaterials.OPEN_EYEBLOSSOM, Material.OPEN_EYEBLOSSOM);
            versionMaterials.put(GooPVersionMaterials.CLOSED_EYEBLOSSOM, Material.CLOSED_EYEBLOSSOM);
            versionMaterials.put(GooPVersionMaterials.RESIN_CLUMP, Material.RESIN_CLUMP);
            versionMaterials.put(GooPVersionMaterials.RESIN_BLOCK, Material.RESIN_BLOCK);
            versionMaterials.put(GooPVersionMaterials.RESIN_BRICKS, Material.RESIN_BRICKS);
            versionMaterials.put(GooPVersionMaterials.RESIN_BRICK_STAIRS, Material.RESIN_BRICK_STAIRS);
            versionMaterials.put(GooPVersionMaterials.RESIN_BRICK_SLAB, Material.RESIN_BRICK_SLAB);
            versionMaterials.put(GooPVersionMaterials.RESIN_BRICK_WALL, Material.RESIN_BRICK_WALL);
            versionMaterials.put(GooPVersionMaterials.CHISELED_RESIN_BRICKS, Material.CHISELED_RESIN_BRICKS);
            versionMaterials.put(GooPVersionMaterials.RESIN_BRICK, Material.RESIN_BRICK);
            versionMaterials.put(GooPVersionMaterials.POTTED_OPEN_EYEBLOSSOM, Material.POTTED_OPEN_EYEBLOSSOM);
            versionMaterials.put(GooPVersionMaterials.POTTED_CLOSED_EYEBLOSSOM, Material.POTTED_CLOSED_EYEBLOSSOM);
        }
        //endregion

        // Finally, fill the holes with null material VOID_AIR
        for (GooPVersionMaterials gvm : GooPVersionMaterials.values()) {

            // If its not contained
            if (!versionMaterials.containsKey(gvm)) {

                // Add it as VOID_AIR
                versionMaterials.put(gvm, Material.VOID_AIR);
            }
        }
        //endregion

        //region Entity Types
        //region Minecraft 1.13-
        if (mcVersion <= 13.0) {
            versionEntityTypes.put(GooPVersionEntities.LINGERING_POTION, GetEntityTypeFromString("LINGERING_POTION"));
        }
        //endregion

        //region Minecraft 1.14+
        if (mcVersion >= 14.0) {
            versionEntityTypes.put(GooPVersionEntities.CAT, EntityType.CAT);
            versionEntityTypes.put(GooPVersionEntities.PANDA, EntityType.PANDA);
            versionEntityTypes.put(GooPVersionEntities.PILLAGER, EntityType.PILLAGER);
            versionEntityTypes.put(GooPVersionEntities.RAVAGER, EntityType.RAVAGER);
            versionEntityTypes.put(GooPVersionEntities.TRADER_LLAMA, EntityType.TRADER_LLAMA);
            versionEntityTypes.put(GooPVersionEntities.WANDERING_TRADER, EntityType.WANDERING_TRADER);
            versionEntityTypes.put(GooPVersionEntities.FOX, EntityType.FOX);
        }
        //endregion

        //region Minecraft 1.15+
        if (mcVersion >= 15.0) {
            versionEntityTypes.put(GooPVersionEntities.BEE, EntityType.BEE);
        }
        //endregion

        //region Minecraft 1.16+
        if (mcVersion >= 16.0) {
            versionEntityTypes.put(GooPVersionEntities.ZOMBIFIED_PIGLIN, EntityType.ZOMBIFIED_PIGLIN);
            versionEntityTypes.put(GooPVersionEntities.HOGLIN, EntityType.HOGLIN);
            versionEntityTypes.put(GooPVersionEntities.PIGLIN, EntityType.PIGLIN);
            versionEntityTypes.put(GooPVersionEntities.STRIDER, EntityType.STRIDER);
            versionEntityTypes.put(GooPVersionEntities.ZOGLIN, EntityType.ZOGLIN);

            versionEntityTypes.put(GooPVersionEntities.PIG_ZOMBIE, EntityType.ZOMBIFIED_PIGLIN);
        }
        //endregion

        //region Minecraft 1.16-
        if (mcVersion < 16.0) {
            versionEntityTypes.put(GooPVersionEntities.PIG_ZOMBIE, GetEntityTypeFromString("PIG_ZOMBIE"));
            versionEntityTypes.put(GooPVersionEntities.ZOMBIFIED_PIGLIN, GetEntityTypeFromString("PIG_ZOMBIE"));
        }
        //endregion

        //region Minecraft 1.17+
        if (mcVersion >= 17.0) {
            //* YE-OLD-MMO
            versionEntityTypes.put(GooPVersionEntities.AXOLOTL, EntityType.AXOLOTL);
            versionEntityTypes.put(GooPVersionEntities.GLOW_ITEM_FRAME, EntityType.GLOW_ITEM_FRAME);
            versionEntityTypes.put(GooPVersionEntities.GLOW_SQUID, EntityType.GLOW_SQUID);
            versionEntityTypes.put(GooPVersionEntities.GOAT, EntityType.GOAT);
            versionEntityTypes.put(GooPVersionEntities.MARKER, EntityType.MARKER);
            // YE-OLD-MMO */
        }
        //endregion

        //region Minecraft 1.17+
        if (mcVersion >= 17.0) {
            //* YE-OLD-MMO
            versionEntityTypes.put(GooPVersionEntities.AXOLOTL, EntityType.AXOLOTL);
            versionEntityTypes.put(GooPVersionEntities.GLOW_ITEM_FRAME, EntityType.GLOW_ITEM_FRAME);
            versionEntityTypes.put(GooPVersionEntities.GLOW_SQUID, EntityType.GLOW_SQUID);
            versionEntityTypes.put(GooPVersionEntities.GOAT, EntityType.GOAT);
            versionEntityTypes.put(GooPVersionEntities.MARKER, EntityType.MARKER);
            // YE-OLD-MMO */
        }
        //endregion

        //region Minecraft 1.19+
        if (mcVersion >= 19.0) {
            versionEntityTypes.put(GooPVersionEntities.ALLAY, EntityType.ALLAY);
            versionEntityTypes.put(GooPVersionEntities.FROG, EntityType.FROG);
            versionEntityTypes.put(GooPVersionEntities.TADPOLE, EntityType.TADPOLE);
            versionEntityTypes.put(GooPVersionEntities.WARDEN, EntityType.WARDEN);
        }
        //endregion

        //region Minecraft 1.19.4+
        if (mcVersion >= 19.4) {
            versionEntityTypes.put(GooPVersionEntities.CAMEL, EntityType.CAMEL);
            versionEntityTypes.put(GooPVersionEntities.BLOCK_DISPLAY, EntityType.BLOCK_DISPLAY);
            versionEntityTypes.put(GooPVersionEntities.INTERACTION, EntityType.INTERACTION);
            versionEntityTypes.put(GooPVersionEntities.ITEM_DISPLAY, EntityType.ITEM_DISPLAY);
            versionEntityTypes.put(GooPVersionEntities.SNIFFER, EntityType.SNIFFER);
            versionEntityTypes.put(GooPVersionEntities.TEXT_DISPLAY, EntityType.TEXT_DISPLAY);
        }
        //endregion

        //region Minecraft 1.20.6+
        if (mcVersion >= 20.6) {
            versionEntityTypes.put(GooPVersionEntities.ITEM, EntityType.ITEM);
            versionEntityTypes.put(GooPVersionEntities.LEASH_KNOT, EntityType.LEASH_KNOT);
            versionEntityTypes.put(GooPVersionEntities.EYE_OF_ENDER, EntityType.EYE_OF_ENDER);
            versionEntityTypes.put(GooPVersionEntities.POTION, EntityType.POTION);
            versionEntityTypes.put(GooPVersionEntities.EXPERIENCE_BOTTLE, EntityType.EXPERIENCE_BOTTLE);
            versionEntityTypes.put(GooPVersionEntities.TNT, EntityType.TNT);
            versionEntityTypes.put(GooPVersionEntities.FIREWORK_ROCKET, EntityType.FIREWORK_ROCKET);
            versionEntityTypes.put(GooPVersionEntities.COMMAND_BLOCK_MINECART, EntityType.COMMAND_BLOCK_MINECART);
            versionEntityTypes.put(GooPVersionEntities.CHEST_MINECART, EntityType.CHEST_MINECART);
            versionEntityTypes.put(GooPVersionEntities.FURNACE_MINECART, EntityType.FURNACE_MINECART);
            versionEntityTypes.put(GooPVersionEntities.TNT_MINECART, EntityType.TNT_MINECART);
            versionEntityTypes.put(GooPVersionEntities.HOPPER_MINECART, EntityType.HOPPER_MINECART);
            versionEntityTypes.put(GooPVersionEntities.SPAWNER_MINECART, EntityType.SPAWNER_MINECART);
            versionEntityTypes.put(GooPVersionEntities.MOOSHROOM, EntityType.MOOSHROOM);
            versionEntityTypes.put(GooPVersionEntities.SNOW_GOLEM, EntityType.SNOW_GOLEM);
            versionEntityTypes.put(GooPVersionEntities.END_CRYSTAL, EntityType.END_CRYSTAL);
            versionEntityTypes.put(GooPVersionEntities.FISHING_BOBBER, EntityType.FISHING_BOBBER);
            versionEntityTypes.put(GooPVersionEntities.LIGHTNING_BOLT, EntityType.LIGHTNING_BOLT);
            versionEntityTypes.put(GooPVersionEntities.BREEZE, EntityType.BREEZE);
            versionEntityTypes.put(GooPVersionEntities.WIND_CHARGE, EntityType.WIND_CHARGE);
            versionEntityTypes.put(GooPVersionEntities.BREEZE_WIND_CHARGE, EntityType.BREEZE_WIND_CHARGE);
            versionEntityTypes.put(GooPVersionEntities.ARMADILLO, EntityType.ARMADILLO);
            versionEntityTypes.put(GooPVersionEntities.BOGGED, EntityType.BOGGED);
            versionEntityTypes.put(GooPVersionEntities.OMINOUS_ITEM_SPAWNER, EntityType.OMINOUS_ITEM_SPAWNER);

            versionEntityTypes.put(GooPVersionEntities.LIGHTNING, EntityType.LIGHTNING_BOLT);
            versionEntityTypes.put(GooPVersionEntities.FISHING_HOOK, EntityType.FISHING_BOBBER);
            versionEntityTypes.put(GooPVersionEntities.ENDER_CRYSTAL, EntityType.END_CRYSTAL);
            versionEntityTypes.put(GooPVersionEntities.SNOWMAN, EntityType.SNOW_GOLEM);
            versionEntityTypes.put(GooPVersionEntities.MUSHROOM_COW, EntityType.MOOSHROOM);
            versionEntityTypes.put(GooPVersionEntities.MINECART_MOB_SPAWNER, EntityType.SPAWNER_MINECART);
            versionEntityTypes.put(GooPVersionEntities.MINECART_HOPPER, EntityType.HOPPER_MINECART);
            versionEntityTypes.put(GooPVersionEntities.MINECART_TNT, EntityType.TNT_MINECART);
            versionEntityTypes.put(GooPVersionEntities.MINECART_FURNACE, EntityType.FURNACE_MINECART);
            versionEntityTypes.put(GooPVersionEntities.MINECART_CHEST, EntityType.CHEST_MINECART);
            versionEntityTypes.put(GooPVersionEntities.MINECART_COMMAND, EntityType.COMMAND_BLOCK_MINECART);
            versionEntityTypes.put(GooPVersionEntities.FIREWORK, EntityType.FIREWORK_ROCKET);
            versionEntityTypes.put(GooPVersionEntities.PRIMED_TNT, EntityType.TNT);
            versionEntityTypes.put(GooPVersionEntities.THROWN_EXP_BOTTLE, EntityType.EXPERIENCE_BOTTLE);
            versionEntityTypes.put(GooPVersionEntities.SPLASH_POTION, EntityType.POTION);
            versionEntityTypes.put(GooPVersionEntities.ENDER_SIGNAL, EntityType.EYE_OF_ENDER);
            versionEntityTypes.put(GooPVersionEntities.LEASH_HITCH, EntityType.LEASH_KNOT);
            versionEntityTypes.put(GooPVersionEntities.DROPPED_ITEM, EntityType.ITEM);
        }
        //endregion

        //region Minecraft 1.20.6-
        if (mcVersion < 20.6) {
            versionEntityTypes.put(GooPVersionEntities.LIGHTNING, GetEntityTypeFromString("LIGHTNING"));
            versionEntityTypes.put(GooPVersionEntities.FISHING_HOOK, GetEntityTypeFromString("FISHING_HOOK"));
            versionEntityTypes.put(GooPVersionEntities.ENDER_CRYSTAL, GetEntityTypeFromString("ENDER_CRYSTAL"));
            versionEntityTypes.put(GooPVersionEntities.SNOWMAN, GetEntityTypeFromString("SNOWMAN"));
            versionEntityTypes.put(GooPVersionEntities.MUSHROOM_COW, GetEntityTypeFromString("MUSHROOM_COW"));
            versionEntityTypes.put(GooPVersionEntities.MINECART_MOB_SPAWNER, GetEntityTypeFromString("MINECART_MOB_SPAWNER"));
            versionEntityTypes.put(GooPVersionEntities.MINECART_HOPPER, GetEntityTypeFromString("MINECART_HOPPER"));
            versionEntityTypes.put(GooPVersionEntities.MINECART_TNT, GetEntityTypeFromString("MINECART_TNT"));
            versionEntityTypes.put(GooPVersionEntities.MINECART_FURNACE, GetEntityTypeFromString("MINECART_FURNACE"));
            versionEntityTypes.put(GooPVersionEntities.MINECART_CHEST, GetEntityTypeFromString("MINECART_CHEST"));
            versionEntityTypes.put(GooPVersionEntities.MINECART_COMMAND, GetEntityTypeFromString("MINECART_COMMAND"));
            versionEntityTypes.put(GooPVersionEntities.FIREWORK, GetEntityTypeFromString("FIREWORK"));
            versionEntityTypes.put(GooPVersionEntities.PRIMED_TNT, GetEntityTypeFromString("PRIMED_TNT"));
            versionEntityTypes.put(GooPVersionEntities.THROWN_EXP_BOTTLE, GetEntityTypeFromString("THROWN_EXP_BOTTLE"));
            versionEntityTypes.put(GooPVersionEntities.SPLASH_POTION, GetEntityTypeFromString("SPLASH_POTION"));
            versionEntityTypes.put(GooPVersionEntities.ENDER_SIGNAL, GetEntityTypeFromString("ENDER_SIGNAL"));
            versionEntityTypes.put(GooPVersionEntities.LEASH_HITCH, GetEntityTypeFromString("LEASH_HITCH"));
            versionEntityTypes.put(GooPVersionEntities.DROPPED_ITEM, GetEntityTypeFromString("DROPPED_ITEM"));

            versionEntityTypes.put(GooPVersionEntities.ITEM, GetEntityTypeFromString("DROPPED_ITEM"));
            versionEntityTypes.put(GooPVersionEntities.LEASH_KNOT, GetEntityTypeFromString("LEASH_HITCH"));
            versionEntityTypes.put(GooPVersionEntities.EYE_OF_ENDER, GetEntityTypeFromString("ENDER_SIGNAL"));
            versionEntityTypes.put(GooPVersionEntities.POTION, GetEntityTypeFromString("SPLASH_POTION"));
            versionEntityTypes.put(GooPVersionEntities.EXPERIENCE_BOTTLE, GetEntityTypeFromString("THROWN_EXP_BOTTLE"));
            versionEntityTypes.put(GooPVersionEntities.TNT, GetEntityTypeFromString("PRIMED_TNT"));
            versionEntityTypes.put(GooPVersionEntities.FIREWORK_ROCKET, GetEntityTypeFromString("FIREWORK"));
            versionEntityTypes.put(GooPVersionEntities.COMMAND_BLOCK_MINECART, GetEntityTypeFromString("MINECART_COMMAND"));
            versionEntityTypes.put(GooPVersionEntities.CHEST_MINECART, GetEntityTypeFromString("MINECART_CHEST"));
            versionEntityTypes.put(GooPVersionEntities.FURNACE_MINECART, GetEntityTypeFromString("MINECART_FURNACE"));
            versionEntityTypes.put(GooPVersionEntities.TNT_MINECART, GetEntityTypeFromString("MINECART_TNT"));
            versionEntityTypes.put(GooPVersionEntities.HOPPER_MINECART, GetEntityTypeFromString("MINECART_HOPPER"));
            versionEntityTypes.put(GooPVersionEntities.SPAWNER_MINECART, GetEntityTypeFromString("MINECART_MOB_SPAWNER"));
            versionEntityTypes.put(GooPVersionEntities.MOOSHROOM, GetEntityTypeFromString("MUSHROOM_COW"));
            versionEntityTypes.put(GooPVersionEntities.SNOW_GOLEM, GetEntityTypeFromString("SNOWMAN"));
            versionEntityTypes.put(GooPVersionEntities.END_CRYSTAL, GetEntityTypeFromString("ENDER_CRYSTAL"));
            versionEntityTypes.put(GooPVersionEntities.FISHING_BOBBER, GetEntityTypeFromString("FISHING_HOOK"));
            versionEntityTypes.put(GooPVersionEntities.LIGHTNING_BOLT, GetEntityTypeFromString("LIGHTNING"));
        }
        //endregion

        //region Minecraft 1.21.3+
        if (mcVersion >= 21.3) {
            versionEntityTypes.put(GooPVersionEntities.OAK_BOAT, EntityType.OAK_BOAT);
            versionEntityTypes.put(GooPVersionEntities.OAK_CHEST_BOAT, EntityType.OAK_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.ACACIA_BOAT, EntityType.ACACIA_BOAT);
            versionEntityTypes.put(GooPVersionEntities.ACACIA_CHEST_BOAT, EntityType.ACACIA_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.BAMBOO_RAFT, EntityType.BAMBOO_RAFT);
            versionEntityTypes.put(GooPVersionEntities.BAMBOO_CHEST_RAFT, EntityType.BAMBOO_CHEST_RAFT);
            versionEntityTypes.put(GooPVersionEntities.BIRCH_BOAT, EntityType.BIRCH_BOAT);
            versionEntityTypes.put(GooPVersionEntities.BIRCH_CHEST_BOAT, EntityType.BIRCH_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.CHERRY_BOAT, EntityType.CHERRY_BOAT);
            versionEntityTypes.put(GooPVersionEntities.CHERRY_CHEST_BOAT, EntityType.CHERRY_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.DARK_OAK_BOAT, EntityType.DARK_OAK_BOAT);
            versionEntityTypes.put(GooPVersionEntities.DARK_OAK_CHEST_BOAT, EntityType.DARK_OAK_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.JUNGLE_BOAT, EntityType.JUNGLE_BOAT);
            versionEntityTypes.put(GooPVersionEntities.JUNGLE_CHEST_BOAT, EntityType.JUNGLE_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.MANGROVE_BOAT, EntityType.MANGROVE_BOAT);
            versionEntityTypes.put(GooPVersionEntities.MANGROVE_CHEST_BOAT, EntityType.MANGROVE_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.PALE_OAK_BOAT, EntityType.PALE_OAK_BOAT);
            versionEntityTypes.put(GooPVersionEntities.PALE_OAK_CHEST_BOAT, EntityType.PALE_OAK_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.SPRUCE_BOAT, EntityType.SPRUCE_BOAT);
            versionEntityTypes.put(GooPVersionEntities.SPRUCE_CHEST_BOAT, EntityType.SPRUCE_CHEST_BOAT);
            versionEntityTypes.put(GooPVersionEntities.CREAKING, EntityType.CREAKING);

            versionEntityTypes.put(GooPVersionEntities.BOAT, EntityType.OAK_BOAT);
            versionEntityTypes.put(GooPVersionEntities.CHEST_BOAT, EntityType.OAK_CHEST_BOAT);
        }
        //endregion

        //region Minecraft 1.21.3-
        if (mcVersion < 21.3) {
            versionEntityTypes.put(GooPVersionEntities.BOAT, GetEntityTypeFromString("BOAT"));

            // Bro only lasted a few versions
            if (mcVersion >= 19.0) { versionEntityTypes.put(GooPVersionEntities.CHEST_BOAT, GetEntityTypeFromString("CHEST_BOAT")); }
        }
        //endregion

        //region Minecraft 1.21.4-
        if (mcVersion < 21.4) {

            // Bro only lasted a single release
            if (mcVersion >= 21.3) { versionEntityTypes.put(GooPVersionEntities.CREAKING_TRANSIENT, GetEntityTypeFromString("CREAKING_TRANSIENT")); }
        }
        //endregion

        // Finally, fill the holes with null entity UNKNOWN
        for (GooPVersionEntities gvm : GooPVersionEntities.values()) {

            // If its not contianed
            if (!versionEntityTypes.containsKey(gvm)) {

                // Add it as UNKNOWN
                versionEntityTypes.put(gvm, EntityType.UNKNOWN);
            }
        }
        //endregion

        //region Enchantments
        //region Minecraft 1.14+
        if (mcVersion >= 14.0) {
            versionEnchantments.put(GooPVersionEnchantments.MULTISHOT, Enchantment.MULTISHOT);
            versionEnchantments.put(GooPVersionEnchantments.QUICK_CHARGE, Enchantment.QUICK_CHARGE);
            versionEnchantments.put(GooPVersionEnchantments.PIERCING, Enchantment.PIERCING);
        }
        //endregion

        //region Minecraft 1.16+
        if (mcVersion >= 16.0) {
            versionEnchantments.put(GooPVersionEnchantments.SOUL_SPEED, Enchantment.SOUL_SPEED);
        }
        //endregion

        //region Minecraft 1.19+
        if (mcVersion >= 19.0) {
            versionEnchantments.put(GooPVersionEnchantments.SWIFT_SNEAK, Enchantment.SWIFT_SNEAK);
        }
        //endregion

        //region Minecraft 1.20.6+
        if (mcVersion >= 20.6) {
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION, Enchantment.PROTECTION);
            versionEnchantments.put(GooPVersionEnchantments.FIRE_PROTECTION, Enchantment.FIRE_PROTECTION);
            versionEnchantments.put(GooPVersionEnchantments.FEATHER_FALLING, Enchantment.FEATHER_FALLING);
            versionEnchantments.put(GooPVersionEnchantments.BLAST_PROTECTION, Enchantment.BLAST_PROTECTION);
            versionEnchantments.put(GooPVersionEnchantments.PROJECTILE_PROTECTION, Enchantment.PROJECTILE_PROTECTION);
            versionEnchantments.put(GooPVersionEnchantments.RESPIRATION, Enchantment.RESPIRATION);
            versionEnchantments.put(GooPVersionEnchantments.AQUA_AFFINITY, Enchantment.AQUA_AFFINITY);
            versionEnchantments.put(GooPVersionEnchantments.SHARPNESS, Enchantment.SHARPNESS);
            versionEnchantments.put(GooPVersionEnchantments.SMITE, Enchantment.SMITE);
            versionEnchantments.put(GooPVersionEnchantments.BANE_OF_ARTHROPODS, Enchantment.BANE_OF_ARTHROPODS);
            versionEnchantments.put(GooPVersionEnchantments.LOOTING, Enchantment.LOOTING);
            versionEnchantments.put(GooPVersionEnchantments.EFFICIENCY, Enchantment.EFFICIENCY);
            versionEnchantments.put(GooPVersionEnchantments.UNBREAKING, Enchantment.UNBREAKING);
            versionEnchantments.put(GooPVersionEnchantments.FORTUNE, Enchantment.FORTUNE);
            versionEnchantments.put(GooPVersionEnchantments.POWER, Enchantment.POWER);
            versionEnchantments.put(GooPVersionEnchantments.PUNCH, Enchantment.PUNCH);
            versionEnchantments.put(GooPVersionEnchantments.FLAME, Enchantment.FLAME);
            versionEnchantments.put(GooPVersionEnchantments.INFINITY, Enchantment.INFINITY);
            versionEnchantments.put(GooPVersionEnchantments.LUCK_OF_THE_SEA, Enchantment.LUCK_OF_THE_SEA);
            versionEnchantments.put(GooPVersionEnchantments.DENSITY, Enchantment.DENSITY);
            versionEnchantments.put(GooPVersionEnchantments.BREACH, Enchantment.BREACH);
            versionEnchantments.put(GooPVersionEnchantments.WIND_BURST, Enchantment.WIND_BURST);

            versionEnchantments.put(GooPVersionEnchantments.LUCK, Enchantment.LUCK_OF_THE_SEA);
            versionEnchantments.put(GooPVersionEnchantments.ARROW_INFINITE, Enchantment.INFINITY);
            versionEnchantments.put(GooPVersionEnchantments.ARROW_FIRE, Enchantment.FLAME);
            versionEnchantments.put(GooPVersionEnchantments.ARROW_KNOCKBACK, Enchantment.PUNCH);
            versionEnchantments.put(GooPVersionEnchantments.ARROW_DAMAGE, Enchantment.POWER);
            versionEnchantments.put(GooPVersionEnchantments.LOOT_BONUS_BLOCKS, Enchantment.FORTUNE);
            versionEnchantments.put(GooPVersionEnchantments.DURABILITY, Enchantment.UNBREAKING);
            versionEnchantments.put(GooPVersionEnchantments.DIG_SPEED, Enchantment.EFFICIENCY);
            versionEnchantments.put(GooPVersionEnchantments.LOOT_BONUS_MOBS, Enchantment.LOOTING);
            versionEnchantments.put(GooPVersionEnchantments.DAMAGE_ARTHROPODS, Enchantment.BANE_OF_ARTHROPODS);
            versionEnchantments.put(GooPVersionEnchantments.DAMAGE_UNDEAD, Enchantment.SMITE);
            versionEnchantments.put(GooPVersionEnchantments.DAMAGE_ALL, Enchantment.SHARPNESS);
            versionEnchantments.put(GooPVersionEnchantments.WATER_WORKER, Enchantment.AQUA_AFFINITY);
            versionEnchantments.put(GooPVersionEnchantments.OXYGEN, Enchantment.RESPIRATION);
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_PROJECTILE, Enchantment.PROJECTILE_PROTECTION);
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_EXPLOSIONS, Enchantment.BLAST_PROTECTION);
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_FALL, Enchantment.FEATHER_FALLING);
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_FIRE, Enchantment.FIRE_PROTECTION);
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION);
        }
        //endregion

        //region Minecraft 1.20.6+
        if (mcVersion < 20.6) {
            versionEnchantments.put(GooPVersionEnchantments.LUCK, GetEnchantmentFromString("LUCK"));
            versionEnchantments.put(GooPVersionEnchantments.ARROW_INFINITE, GetEnchantmentFromString("ARROW_INFINITE"));
            versionEnchantments.put(GooPVersionEnchantments.ARROW_FIRE, GetEnchantmentFromString("ARROW_FIRE"));
            versionEnchantments.put(GooPVersionEnchantments.ARROW_KNOCKBACK, GetEnchantmentFromString("ARROW_KNOCKBACK"));
            versionEnchantments.put(GooPVersionEnchantments.ARROW_DAMAGE, GetEnchantmentFromString("ARROW_DAMAGE"));
            versionEnchantments.put(GooPVersionEnchantments.LOOT_BONUS_BLOCKS, GetEnchantmentFromString("LOOT_BONUS_BLOCKS"));
            versionEnchantments.put(GooPVersionEnchantments.DURABILITY, GetEnchantmentFromString("DURABILITY"));
            versionEnchantments.put(GooPVersionEnchantments.DIG_SPEED, GetEnchantmentFromString("DIG_SPEED"));
            versionEnchantments.put(GooPVersionEnchantments.LOOT_BONUS_MOBS, GetEnchantmentFromString("LOOT_BONUS_MOBS"));
            versionEnchantments.put(GooPVersionEnchantments.DAMAGE_ARTHROPODS, GetEnchantmentFromString("DAMAGE_ARTHROPODS"));
            versionEnchantments.put(GooPVersionEnchantments.DAMAGE_UNDEAD, GetEnchantmentFromString("DAMAGE_UNDEAD"));
            versionEnchantments.put(GooPVersionEnchantments.DAMAGE_ALL, GetEnchantmentFromString("DAMAGE_ALL"));
            versionEnchantments.put(GooPVersionEnchantments.WATER_WORKER, GetEnchantmentFromString("WATER_WORKER"));
            versionEnchantments.put(GooPVersionEnchantments.OXYGEN, GetEnchantmentFromString("OXYGEN"));
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_PROJECTILE, GetEnchantmentFromString("PROTECTION_PROJECTILE"));
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_EXPLOSIONS, GetEnchantmentFromString("PROTECTION_EXPLOSIONS"));
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_FALL, GetEnchantmentFromString("PROTECTION_FALL"));
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_FIRE, GetEnchantmentFromString("PROTECTION_FIRE"));
            versionEnchantments.put(GooPVersionEnchantments.PROTECTION_ENVIRONMENTAL, GetEnchantmentFromString("PROTECTION_ENVIRONMENTAL"));

            versionEnchantments.put(GooPVersionEnchantments.PROTECTION, GetEnchantmentFromString("PROTECTION_ENVIRONMENTAL"));
            versionEnchantments.put(GooPVersionEnchantments.FIRE_PROTECTION, GetEnchantmentFromString("PROTECTION_FIRE"));
            versionEnchantments.put(GooPVersionEnchantments.FEATHER_FALLING, GetEnchantmentFromString("PROTECTION_FALL"));
            versionEnchantments.put(GooPVersionEnchantments.BLAST_PROTECTION, GetEnchantmentFromString("PROTECTION_EXPLOSIONS"));
            versionEnchantments.put(GooPVersionEnchantments.PROJECTILE_PROTECTION, GetEnchantmentFromString("PROTECTION_PROJECTILE"));
            versionEnchantments.put(GooPVersionEnchantments.RESPIRATION, GetEnchantmentFromString("OXYGEN"));
            versionEnchantments.put(GooPVersionEnchantments.AQUA_AFFINITY, GetEnchantmentFromString("WATER_WORKER"));
            versionEnchantments.put(GooPVersionEnchantments.SHARPNESS, GetEnchantmentFromString("DAMAGE_ALL"));
            versionEnchantments.put(GooPVersionEnchantments.SMITE, GetEnchantmentFromString("DAMAGE_UNDEAD"));
            versionEnchantments.put(GooPVersionEnchantments.BANE_OF_ARTHROPODS, GetEnchantmentFromString("DAMAGE_ARTHROPODS"));
            versionEnchantments.put(GooPVersionEnchantments.LOOTING, GetEnchantmentFromString("LOOT_BONUS_MOBS"));
            versionEnchantments.put(GooPVersionEnchantments.EFFICIENCY, GetEnchantmentFromString("DIG_SPEED"));
            versionEnchantments.put(GooPVersionEnchantments.UNBREAKING, GetEnchantmentFromString("DURABILITY"));
            versionEnchantments.put(GooPVersionEnchantments.FORTUNE, GetEnchantmentFromString("LOOT_BONUS_BLOCKS"));
            versionEnchantments.put(GooPVersionEnchantments.POWER, GetEnchantmentFromString("ARROW_DAMAGE"));
            versionEnchantments.put(GooPVersionEnchantments.PUNCH, GetEnchantmentFromString("ARROW_KNOCKBACK"));
            versionEnchantments.put(GooPVersionEnchantments.FLAME, GetEnchantmentFromString("ARROW_FIRE"));
            versionEnchantments.put(GooPVersionEnchantments.INFINITY, GetEnchantmentFromString("ARROW_INFINITE"));
            versionEnchantments.put(GooPVersionEnchantments.LUCK_OF_THE_SEA, GetEnchantmentFromString("LUCK"));
        }

        // Finally, fill the holes with null entity UNKNOWN
        for (GooPVersionEnchantments gvm : GooPVersionEnchantments.values()) {

            // If its not contianed
            if (!versionEnchantments.containsKey(gvm)) {

                // Add it as NULL
                versionEnchantments.put(gvm, null);
            }
        }
        //endregion
        //endregion

        //region Attributes
        //region Minecraft 1.20.6+
        if (mcVersion >= 20.6) {
            versionAttributes.put(GooPVersionAttributes.GENERIC_MAX_HEALTH, Attribute.MAX_HEALTH);
            versionAttributes.put(GooPVersionAttributes.MAX_HEALTH, Attribute.MAX_HEALTH);
            versionAttributes.put(GooPVersionAttributes.GENERIC_FOLLOW_RANGE, Attribute.FOLLOW_RANGE);
            versionAttributes.put(GooPVersionAttributes.FOLLOW_RANGE, Attribute.FOLLOW_RANGE);
            versionAttributes.put(GooPVersionAttributes.GENERIC_KNOCKBACK_RESISTANCE, Attribute.KNOCKBACK_RESISTANCE);
            versionAttributes.put(GooPVersionAttributes.KNOCKBACK_RESISTANCE, Attribute.KNOCKBACK_RESISTANCE);
            versionAttributes.put(GooPVersionAttributes.GENERIC_MOVEMENT_SPEED, Attribute.MOVEMENT_SPEED);
            versionAttributes.put(GooPVersionAttributes.MOVEMENT_SPEED, Attribute.MOVEMENT_SPEED);
            versionAttributes.put(GooPVersionAttributes.GENERIC_FLYING_SPEED, Attribute.FLYING_SPEED);
            versionAttributes.put(GooPVersionAttributes.FLYING_SPEED, Attribute.FLYING_SPEED);
            versionAttributes.put(GooPVersionAttributes.GENERIC_ATTACK_DAMAGE, Attribute.ATTACK_DAMAGE);
            versionAttributes.put(GooPVersionAttributes.ATTACK_DAMAGE, Attribute.ATTACK_DAMAGE);
            versionAttributes.put(GooPVersionAttributes.GENERIC_ATTACK_KNOCKBACK, Attribute.ATTACK_KNOCKBACK);
            versionAttributes.put(GooPVersionAttributes.ATTACK_KNOCKBACK, Attribute.ATTACK_KNOCKBACK);
            versionAttributes.put(GooPVersionAttributes.GENERIC_ATTACK_SPEED, Attribute.ATTACK_SPEED);
            versionAttributes.put(GooPVersionAttributes.ATTACK_SPEED, Attribute.ATTACK_SPEED);
            versionAttributes.put(GooPVersionAttributes.GENERIC_ARMOR, Attribute.ARMOR);
            versionAttributes.put(GooPVersionAttributes.ARMOR, Attribute.ARMOR);
            versionAttributes.put(GooPVersionAttributes.GENERIC_ARMOR_TOUGHNESS, Attribute.ARMOR_TOUGHNESS);
            versionAttributes.put(GooPVersionAttributes.ARMOR_TOUGHNESS, Attribute.ARMOR_TOUGHNESS);
            versionAttributes.put(GooPVersionAttributes.FALL_DAMAGE_MULTIPLIER, Attribute.FALL_DAMAGE_MULTIPLIER);
            versionAttributes.put(GooPVersionAttributes.GENERIC_LUCK, Attribute.LUCK);
            versionAttributes.put(GooPVersionAttributes.LUCK, Attribute.LUCK);
            versionAttributes.put(GooPVersionAttributes.MAX_ABSORPTION, Attribute.MAX_ABSORPTION);
            versionAttributes.put(GooPVersionAttributes.SAFE_FALL_DISTANCE, Attribute.SAFE_FALL_DISTANCE);
            versionAttributes.put(GooPVersionAttributes.SCALE, Attribute.SCALE);
            versionAttributes.put(GooPVersionAttributes.STEP_HEIGHT, Attribute.STEP_HEIGHT);
            versionAttributes.put(GooPVersionAttributes.GRAVITY, Attribute.GRAVITY);
            versionAttributes.put(GooPVersionAttributes.HORSE_JUMP_STRENGTH, Attribute.JUMP_STRENGTH);
            versionAttributes.put(GooPVersionAttributes.JUMP_STRENGTH, Attribute.JUMP_STRENGTH);
            versionAttributes.put(GooPVersionAttributes.BURNING_TIME, Attribute.BURNING_TIME);
            versionAttributes.put(GooPVersionAttributes.EXPLOSION_KNOCKBACK_RESISTANCE, Attribute.EXPLOSION_KNOCKBACK_RESISTANCE);
            versionAttributes.put(GooPVersionAttributes.MOVEMENT_EFFICIENCY, Attribute.MOVEMENT_EFFICIENCY);
            versionAttributes.put(GooPVersionAttributes.OXYGEN_BONUS, Attribute.OXYGEN_BONUS);
            versionAttributes.put(GooPVersionAttributes.WATER_MOVEMENT_EFFICIENCY, Attribute.WATER_MOVEMENT_EFFICIENCY);
            versionAttributes.put(GooPVersionAttributes.TEMPT_RANGE, Attribute.TEMPT_RANGE);
            versionAttributes.put(GooPVersionAttributes.BLOCK_INTERACTION_RANGE, Attribute.BLOCK_INTERACTION_RANGE);
            versionAttributes.put(GooPVersionAttributes.ENTITY_INTERACTION_RANGE, Attribute.ENTITY_INTERACTION_RANGE);
            versionAttributes.put(GooPVersionAttributes.BLOCK_BREAK_SPEED, Attribute.BLOCK_BREAK_SPEED);
            versionAttributes.put(GooPVersionAttributes.MINING_EFFICIENCY, Attribute.MINING_EFFICIENCY);
            versionAttributes.put(GooPVersionAttributes.SNEAKING_SPEED, Attribute.SNEAKING_SPEED);
            versionAttributes.put(GooPVersionAttributes.SUBMERGED_MINING_SPEED, Attribute.SUBMERGED_MINING_SPEED);
            versionAttributes.put(GooPVersionAttributes.SWEEPING_DAMAGE_RATIO, Attribute.SWEEPING_DAMAGE_RATIO);
            versionAttributes.put(GooPVersionAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, Attribute.SPAWN_REINFORCEMENTS);
            versionAttributes.put(GooPVersionAttributes.SPAWN_REINFORCEMENTS, Attribute.SPAWN_REINFORCEMENTS);
        }
        //endregion

        //region Minecraft 1.20.6-
        if (mcVersion < 20.6) {
            versionAttributes.put(GooPVersionAttributes.SPAWN_REINFORCEMENTS, GetAttributeFromString("ZOMBIE_SPAWN_REINFORCEMENTS"));
            versionAttributes.put(GooPVersionAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, GetAttributeFromString("ZOMBIE_SPAWN_REINFORCEMENTS"));
            versionAttributes.put(GooPVersionAttributes.JUMP_STRENGTH, GetAttributeFromString("HORSE_JUMP_STRENGTH"));
            versionAttributes.put(GooPVersionAttributes.HORSE_JUMP_STRENGTH, GetAttributeFromString("HORSE_JUMP_STRENGTH"));
            versionAttributes.put(GooPVersionAttributes.LUCK, GetAttributeFromString("GENERIC_LUCK"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_LUCK, GetAttributeFromString("GENERIC_LUCK"));
            versionAttributes.put(GooPVersionAttributes.ARMOR_TOUGHNESS, GetAttributeFromString("GENERIC_ARMOR_TOUGHNESS"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_ARMOR_TOUGHNESS, GetAttributeFromString("GENERIC_ARMOR_TOUGHNESS"));
            versionAttributes.put(GooPVersionAttributes.ARMOR, GetAttributeFromString("GENERIC_ARMOR"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_ARMOR, GetAttributeFromString("GENERIC_ARMOR"));
            versionAttributes.put(GooPVersionAttributes.ATTACK_SPEED, GetAttributeFromString("GENERIC_ATTACK_SPEED"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_ATTACK_SPEED, GetAttributeFromString("GENERIC_ATTACK_SPEED"));
            versionAttributes.put(GooPVersionAttributes.ATTACK_KNOCKBACK, GetAttributeFromString("GENERIC_ATTACK_KNOCKBACK"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_ATTACK_KNOCKBACK, GetAttributeFromString("GENERIC_ATTACK_KNOCKBACK"));
            versionAttributes.put(GooPVersionAttributes.ATTACK_DAMAGE, GetAttributeFromString("GENERIC_ATTACK_DAMAGE"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_ATTACK_DAMAGE, GetAttributeFromString("GENERIC_ATTACK_DAMAGE"));
            versionAttributes.put(GooPVersionAttributes.FLYING_SPEED, GetAttributeFromString("GENERIC_FLYING_SPEED"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_FLYING_SPEED, GetAttributeFromString("GENERIC_FLYING_SPEED"));
            versionAttributes.put(GooPVersionAttributes.MOVEMENT_SPEED, GetAttributeFromString("GENERIC_MOVEMENT_SPEED"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_MOVEMENT_SPEED, GetAttributeFromString("GENERIC_MOVEMENT_SPEED"));
            versionAttributes.put(GooPVersionAttributes.KNOCKBACK_RESISTANCE, GetAttributeFromString("GENERIC_KNOCKBACK_RESISTANCE"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_KNOCKBACK_RESISTANCE, GetAttributeFromString("GENERIC_KNOCKBACK_RESISTANCE"));
            versionAttributes.put(GooPVersionAttributes.FOLLOW_RANGE, GetAttributeFromString("GENERIC_FOLLOW_RANGE"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_FOLLOW_RANGE, GetAttributeFromString("GENERIC_FOLLOW_RANGE"));
            versionAttributes.put(GooPVersionAttributes.MAX_HEALTH, GetAttributeFromString("GENERIC_MAX_HEALTH"));
            versionAttributes.put(GooPVersionAttributes.GENERIC_MAX_HEALTH, GetAttributeFromString("GENERIC_MAX_HEALTH"));
        }
        //endregion
        //endregion

        //region Potion Effects
        //region Minecraft 1.14.4+
        if (mcVersion >= 14.4) {
            versionPotionEffects.put(GooPVersionPotionEffects.BAD_OMEN, PotionEffectType.BAD_OMEN);
            versionPotionEffects.put(GooPVersionPotionEffects.HERO_OF_THE_VILLAGE, PotionEffectType.HERO_OF_THE_VILLAGE);
        }
        //endregion

        //region Minecraft 1.19+
        if (mcVersion >= 19.0) {
            versionPotionEffects.put(GooPVersionPotionEffects.DARKNESS, PotionEffectType.DARKNESS);
        }
        //endregion

        //region Minecraft 1.20.6+
        if (mcVersion >= 20.6) {
            versionPotionEffects.put(GooPVersionPotionEffects.SLOW, PotionEffectType.SLOWNESS);
            versionPotionEffects.put(GooPVersionPotionEffects.SLOWNESS, PotionEffectType.SLOWNESS);
            versionPotionEffects.put(GooPVersionPotionEffects.FAST_DIGGING, PotionEffectType.HASTE);
            versionPotionEffects.put(GooPVersionPotionEffects.HASTE, PotionEffectType.HASTE);
            versionPotionEffects.put(GooPVersionPotionEffects.SLOW_DIGGING, PotionEffectType.MINING_FATIGUE);
            versionPotionEffects.put(GooPVersionPotionEffects.MINING_FATIGUE, PotionEffectType.MINING_FATIGUE);
            versionPotionEffects.put(GooPVersionPotionEffects.INCREASE_DAMAGE, PotionEffectType.STRENGTH);
            versionPotionEffects.put(GooPVersionPotionEffects.STRENGTH, PotionEffectType.STRENGTH);
            versionPotionEffects.put(GooPVersionPotionEffects.HEAL, PotionEffectType.INSTANT_HEALTH);
            versionPotionEffects.put(GooPVersionPotionEffects.INSTANT_HEALTH, PotionEffectType.INSTANT_HEALTH);
            versionPotionEffects.put(GooPVersionPotionEffects.HARM, PotionEffectType.INSTANT_DAMAGE);
            versionPotionEffects.put(GooPVersionPotionEffects.INSTANT_DAMAGE, PotionEffectType.INSTANT_DAMAGE);
            versionPotionEffects.put(GooPVersionPotionEffects.JUMP, PotionEffectType.JUMP_BOOST);
            versionPotionEffects.put(GooPVersionPotionEffects.JUMP_BOOST, PotionEffectType.JUMP_BOOST);
            versionPotionEffects.put(GooPVersionPotionEffects.CONFUSION, PotionEffectType.NAUSEA);
            versionPotionEffects.put(GooPVersionPotionEffects.NAUSEA, PotionEffectType.NAUSEA);
            versionPotionEffects.put(GooPVersionPotionEffects.REGENERATION, PotionEffectType.REGENERATION);
            versionPotionEffects.put(GooPVersionPotionEffects.DAMAGE_RESISTANCE, PotionEffectType.RESISTANCE);
            versionPotionEffects.put(GooPVersionPotionEffects.RESISTANCE, PotionEffectType.RESISTANCE);
            versionPotionEffects.put(GooPVersionPotionEffects.TRIAL_OMEN, PotionEffectType.TRIAL_OMEN);
            versionPotionEffects.put(GooPVersionPotionEffects.RAID_OMEN, PotionEffectType.RAID_OMEN);
            versionPotionEffects.put(GooPVersionPotionEffects.WIND_CHARGED, PotionEffectType.WIND_CHARGED);
            versionPotionEffects.put(GooPVersionPotionEffects.WEAVING, PotionEffectType.WEAVING);
            versionPotionEffects.put(GooPVersionPotionEffects.OOZING, PotionEffectType.OOZING);
            versionPotionEffects.put(GooPVersionPotionEffects.INFESTED, PotionEffectType.INFESTED);
        }
        //endregion

        //region Minecraft 1.20.6-
        if (mcVersion < 20.6) {
            versionPotionEffects.put(GooPVersionPotionEffects.RESISTANCE, GetPotionEffectFromString("DAMAGE_RESISTANCE"));
            versionPotionEffects.put(GooPVersionPotionEffects.DAMAGE_RESISTANCE, GetPotionEffectFromString("DAMAGE_RESISTANCE"));
            versionPotionEffects.put(GooPVersionPotionEffects.NAUSEA, GetPotionEffectFromString("CONFUSION"));
            versionPotionEffects.put(GooPVersionPotionEffects.CONFUSION, GetPotionEffectFromString("CONFUSION"));
            versionPotionEffects.put(GooPVersionPotionEffects.JUMP_BOOST, GetPotionEffectFromString("JUMP"));
            versionPotionEffects.put(GooPVersionPotionEffects.JUMP, GetPotionEffectFromString("JUMP"));
            versionPotionEffects.put(GooPVersionPotionEffects.INSTANT_DAMAGE, GetPotionEffectFromString("HARM"));
            versionPotionEffects.put(GooPVersionPotionEffects.HARM, GetPotionEffectFromString("HARM"));
            versionPotionEffects.put(GooPVersionPotionEffects.INSTANT_HEALTH, GetPotionEffectFromString("HEAL"));
            versionPotionEffects.put(GooPVersionPotionEffects.HEAL, GetPotionEffectFromString("HEAL"));
            versionPotionEffects.put(GooPVersionPotionEffects.STRENGTH, GetPotionEffectFromString("INCREASE_DAMAGE"));
            versionPotionEffects.put(GooPVersionPotionEffects.INCREASE_DAMAGE, GetPotionEffectFromString("INCREASE_DAMAGE"));
            versionPotionEffects.put(GooPVersionPotionEffects.MINING_FATIGUE, GetPotionEffectFromString("SLOW_DIGGING"));
            versionPotionEffects.put(GooPVersionPotionEffects.SLOW_DIGGING, GetPotionEffectFromString("SLOW_DIGGING"));
            versionPotionEffects.put(GooPVersionPotionEffects.HASTE, GetPotionEffectFromString("FAST_DIGGING"));
            versionPotionEffects.put(GooPVersionPotionEffects.FAST_DIGGING, GetPotionEffectFromString("FAST_DIGGING"));
            versionPotionEffects.put(GooPVersionPotionEffects.SLOWNESS, GetPotionEffectFromString("SLOW"));
            versionPotionEffects.put(GooPVersionPotionEffects.SLOW, GetPotionEffectFromString("SLOW"));
        }
        //endregion
        //endregion
    }

    //region Materials
    /**
     * Returns a material from a string.
     *
     * @return Either the Material, or VOID_AIR if it doesnt exist.
     */
    @NotNull public static Material GetMaterialFromString(String str) {

        // Is it a supported plugin command?
        try {

            // Yes, it seems to be
            Material mat = Material.valueOf(str);
            return mat;

        // Not recognized
        } catch (IllegalArgumentException ex) {

            // Return Air
            return Material.VOID_AIR;
        }
    }

    /**
     * If the current material exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static Material GetVersionMaterial(GooPVersionMaterials mat, Material defaultIfMissing) {
        Material mt = GetVersionMaterial(mat);
        if (mt != Material.VOID_AIR) {
            return mt;
        } else {
            return defaultIfMissing;
        }
    }

    /**
     * If the current material exists in this minecraft version, return it.
     * Otherwise, returns VOID_AIR.
     */
    public static Material GetVersionMaterial(GooPVersionMaterials mat) {
        return versionMaterials.get(mat);
    }
    //endregion

    //region Entities
    /**
     * Returns an EntityType from a string.
     *
     * @return Either the EntityType, or UNKNOWN if it doesnt exist.
     */
    public static EntityType GetEntityTypeFromString(String str) {

        // Is it a supported plugin command?
        try {

            // Yes, it seems to be
            EntityType eny = EntityType.valueOf(str);
            return eny;

            // Not recognized
        } catch (IllegalArgumentException ex) {

            // Return Air
            return EntityType.UNKNOWN;
        }
    }

    /**
     * If the current EntityType exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static EntityType GetVersionEntityType(GooPVersionEntities ent, EntityType defaultIfMissing) {
        EntityType mt = GetVersionEntityType(ent);
        if (mt != EntityType.UNKNOWN) {
            return mt;
        } else {
            return defaultIfMissing;
        }
    }

    /**
     * If the current EntityType exists in this minecraft version, return it.
     * Otherwise, returns UNKNOWN.
     */
    public static EntityType GetVersionEntityType(GooPVersionEntities ent) {
        return versionEntityTypes.get(ent);
    }
    //endregion

    //region Enchantments
    /**
     * Returns an Enchantment from a string.
     *
     * @return Either the Enchantment, or null if it doesnt exist.
     */
    @Nullable public static Enchantment GetEnchantmentFromString(@Nullable String str) {
        if (str == null) { return null; };
        str = str.toLowerCase();

        if (mcVersion < 20.3) {

            return Enchantment.getByKey(NamespacedKey.minecraft(str));
        } else {
            if (mcVersion < 21.0 || !Gunging_Ootilities_Plugin.asPaperSpigot) {
                return org.bukkit.Registry.ENCHANTMENT.get(NamespacedKey.minecraft(str));

            } else {
                return io.papermc.paper.registry.RegistryAccess.registryAccess().getRegistry(io.papermc.paper.registry.RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(str));
            }
        }
    }

    /**
     * If the current Enchantment exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static Enchantment GetVersionEnchantment(GooPVersionEnchantments ench, Enchantment defaultIfMissing) {
        Enchantment mt = GetVersionEnchantment(ench);
        if (mt != null) {
            return mt;
        } else {
            return defaultIfMissing;
        }
    }

    /**
     * If the current Enchantment exists in this minecraft version, return it.
     * Otherwise, returns NULL.
     */
    public static Enchantment GetVersionEnchantment(GooPVersionEnchantments ench) {
        return versionEnchantments.get(ench);
    }
    //endregion

    //region Attributes
    /**
     * Returns an Attributes from a string.
     *
     * @return Either the Attributes, or null if it doesnt exist.
     */
    @Nullable public static Attribute GetAttributeFromString(@Nullable String str) {
        if (str == null) { return null; };
        str = str.toLowerCase();

        if (mcVersion < 21.3) {
            return Attribute.valueOf(str);

        } else {
            if (!Gunging_Ootilities_Plugin.asPaperSpigot) {
                return org.bukkit.Registry.ATTRIBUTE.get(NamespacedKey.minecraft(str));

            } else {
                return io.papermc.paper.registry.RegistryAccess.registryAccess().getRegistry(io.papermc.paper.registry.RegistryKey.ATTRIBUTE).get(NamespacedKey.minecraft(str));
            }
        }
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static Attribute GetVersionAttribute(GooPVersionAttributes ench, Attribute defaultIfMissing) {
        Attribute mt = GetVersionAttribute(ench);
        if (mt != null) {
            return mt;
        } else {
            return defaultIfMissing;
        }
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns NULL.
     */
    public static Attribute GetVersionAttribute(GooPVersionAttributes ench) {
        return versionAttributes.get(ench);
    }
    //endregion

    //region PotionEffects
    /**
     * Returns an Attributes from a string.
     *
     * @return Either the Attributes, or null if it doesnt exist.
     */
    @Nullable public static PotionEffectType GetPotionEffectFromString(@Nullable String str) {
        if (str == null) { return null; };
        str = str.toLowerCase();

        if (mcVersion < 21.3) {
            return PotionEffectType.getByKey(NamespacedKey.minecraft(str));

        } else {
            return org.bukkit.Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(str));
        }
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns defaultIfMissing
     */
    public static PotionEffectType GetVersionPotionEffect(GooPVersionPotionEffects ench, PotionEffectType defaultIfMissing) {
        PotionEffectType mt = GetVersionPotionEffect(ench);
        if (mt != null) {
            return mt;
        } else {
            return defaultIfMissing;
        }
    }

    /**
     * If the current Attribute exists in this minecraft version, return it.
     * Otherwise, returns NULL.
     */
    public static PotionEffectType GetVersionPotionEffect(GooPVersionPotionEffects ench) {
        return versionPotionEffects.get(ench);
    }
    //endregion
}
