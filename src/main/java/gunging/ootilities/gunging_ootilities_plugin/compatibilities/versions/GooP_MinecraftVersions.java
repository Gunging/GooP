package gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

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
            } else if (vers.contains("1.17")) {
                mcVersion = 17.0;
                if (vers.contains("1.17.1")) { mcVersion = 17.1; }
                if (vers.contains("1.17.2")) { mcVersion = 17.2; }
                if (vers.contains("1.17.3")) { mcVersion = 17.3; }
            }

            return mcVersion;

        } else {

            // Return the gathered value
            return mcVersion;
        }
    }

    public static HashMap<GooPVersionMaterials, Material> versionMaterials = new HashMap();
    public static HashMap<GooPVersionEntities, EntityType> versionEntityTypes = new HashMap();
    public static HashMap<GooPVersionEnchantments, Enchantment> versionEnchantments = new HashMap();
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

        // Finally, fill the holes with null material VOID_AIR
        for (GooPVersionMaterials gvm : GooPVersionMaterials.values()) {

            // If its not contianed
            if (!versionMaterials.containsKey(gvm)) {

                // Add it as VOID_AIR
                versionMaterials.put(gvm, Material.VOID_AIR);
            }
        }
        //endregion

        //region Entity Types
        //region Minecraft 1.15-
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

        //region Minecraft 1.15-
        if (mcVersion <= 15.0) {
            versionEntityTypes.put(GooPVersionEntities.PIG_ZOMBIE, GetEntityTypeFromString("PIG_ZOMBIE"));
        }
        //endregion

        //region Minecraft 1.16+
        if (mcVersion >= 16.0) {
            versionEntityTypes.put(GooPVersionEntities.ZOMBIFIED_PIGLIN, EntityType.ZOMBIFIED_PIGLIN);
            versionEntityTypes.put(GooPVersionEntities.HOGLIN, EntityType.HOGLIN);
            versionEntityTypes.put(GooPVersionEntities.PIGLIN, EntityType.PIGLIN);
            versionEntityTypes.put(GooPVersionEntities.STRIDER, EntityType.STRIDER);
            versionEntityTypes.put(GooPVersionEntities.ZOGLIN, EntityType.ZOGLIN);
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

        //region Entity Types
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

        // Finally, fill the holes with null entity UNKNOWN
        for (GooPVersionEnchantments gvm : GooPVersionEnchantments.values()) {

            // If its not contianed
            if (!versionEnchantments.containsKey(gvm)) {

                // Add it as NULL
                versionEnchantments.put(gvm, null);
            }
        }
        //endregion
    }

    //region Materials
    /**
     * Returns a material from a string.
     *
     * @return Either the Material, or VOID_AIR if it doesnt exist.
     */
    public static Material GetMaterialFromString(String str) {

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
}
