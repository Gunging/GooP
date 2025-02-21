package gunging.ootilities.gunging_ootilities_plugin.customstructures;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPWorldEdit;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionEntities;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMatchResult;
import gunging.ootilities.gunging_ootilities_plugin.events.JSONFurniturePlaceEvent;
import gunging.ootilities.gunging_ootilities_plugin.events.JSONPlacerUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CSManager implements Listener {

    @NotNull static HashMap<UUID, CSActivation> lastActivations = new HashMap<>();
    public static void registerLastActivation(@NotNull CSActivation activation) { lastActivations.put(activation.getWho().getUniqueId(), activation); }
    public static CSActivation getLastActivation(@Nullable UUID uuid) { return lastActivations.get(uuid); }

//    protected static boolean premiumCSEnabled = false;
//    public static boolean IsPremiumEnabled() { return premiumCSEnabled; }
//    public static void Enable() { if (!Gunging_Ootilities_Plugin.theMain.HasLoaded()) { if (Gunging_Ootilities_Plugin.theMain.IsLoading()) { premiumCSEnabled = true; } } }


    public static ArrayList<CSStructure> loadedStructures = new ArrayList<>();
    public static HashMap<String, CSStructure> csLoadedStructures = new HashMap<>();
    public static HashMap<String, FileConfigPair> csStorageStructures = new HashMap<>();

    // Reload Config
    public static void ReloadStructures(OotilityCeption oots) {

        // Starts Fresh
        loadedStructures = new ArrayList<>();
        csLoadedStructures = new HashMap<>();
        lastActivations.clear();

        // No repeating names
        ArrayList<String> usedNames = new ArrayList<>();

        // Every YML Configuration is guaranteed to have parsed by now.
        for (FileConfigPair csPair : Gunging_Ootilities_Plugin.theMain.customStructurePairs) {

            // Load the YML from it
            YamlConfiguration csConfig = csPair.getStorage();

            // Log Path
            //DEBUG//oots.C PLog("\u00a7oNow Loading File \u00a7b" + csConfig.getName());

            // Log da shit
            for (Map.Entry<String, Object> val : (csConfig.getValues(false)).entrySet()) {

                //DEBUG//oots.C PLog("\u00a7a::::: \u00a77Loading Entry \u00a7a" + val.getKey());

                // Get Structure Name
                String tName = val.getKey();

                // Cancel if already exists
                if (usedNames.contains(tName)) {

                    // Ignore
                    oots.CPLog("Multiple Structures have the same name \u00a73" + tName + "\u00a77. \u00a7cIgnoring them all except the first one.");

                    // Name is clear and original
                } else {
                    // Include
                    usedNames.add(tName);

                    // Get the list of blocks this structure is composed of
                    List<String> rawBlockComposition = null;
                    if (csConfig.contains(tName + ".Composition")) {
                        rawBlockComposition = csConfig.getStringList(tName + ".Composition"); }

                    //DEBUG//if (rawBlockComposition != null) { for (String cmp : rawBlockComposition) { oots.C PLog("\u00a7a--------> \u00a77Block \u00a73" + cmp); } }

                    // Get the list of triggers this structure allows
                    List<String> rawTriggers = null;
                    if (csConfig.contains(tName + ".Triggers")) {
                        rawTriggers = csConfig.getStringList(tName + ".Triggers"); }

                    //DEBUG//if (rawTriggers != null) { for (String cmp : rawTriggers) { oots.C PLog("\u00a7a--------> \u00a77Trigger \u00a7e" + cmp); } }

                    // Get the list of commands this structure executes
                    List<String> rawActions = null;
                    if (csConfig.contains(tName + ".Commands")) {
                        rawActions = csConfig.getStringList(tName + ".Commands"); }

                    // Get permutations
                    boolean permutations = OotilityCeption.If(csConfig.getBoolean(tName + ".OmniInteractive"));

                    // Get the list of commands this structure executes
                    List<String> rawWorldWhitelist = csConfig.getStringList(tName + ".WhitelistedWorlds");
                    List<String> rawWorldBlacklist = csConfig.getStringList(tName + ".BlacklistedWorlds");

                    double rawVCost = 0;
                    if (csConfig.contains(tName + ".VaultCost") && Gunging_Ootilities_Plugin.foundVault) {
                        rawVCost = csConfig.getDouble(tName + ".VaultCost", 0); }

                    //DEBUG//if (rawActions != null) { for (String cmp : rawActions) { oots.C PLog("\u00a7a--------> \u00a77Action \u00a7b" + cmp); } }

                    // Will the structure be loaded due to good syntax?
                    boolean failure = false;

                    // Any blocks occur?
                    ArrayList<CSBlock> csBlocks = new ArrayList<>();
                    CSBlock coreBlock = null;
                    if (rawBlockComposition != null) {

                        // That there is at least 1 block in there
                        if (rawBlockComposition.size() > 0) {

                            // For each string
                            for (String srcBlocc : rawBlockComposition) {

                                // Get block only
                                String srcBlockOnly = srcBlocc, srcMeta = null;

                                int indexOfMeta = srcBlocc.indexOf("||");

                                // Does it even have meta?
                                if (indexOfMeta > 0) {

                                    // Strip meta from it
                                    srcBlockOnly = srcBlocc.substring(0, indexOfMeta);
                                    srcMeta = srcBlocc.substring(indexOfMeta + 2);
                                }

                                // Split into args
                                String[] srcSplitt = srcBlockOnly.split(" ");

                                // The Blocc composition
                                Material srcMat = null;
                                Integer srcS = null, srcV = null, srcF = null;

                                // Number of args makes sense?
                                if (srcSplitt.length == 4) {

                                    // Check Material
                                    try {
                                        // Yes, it seems to be
                                        srcMat = Material.valueOf(srcSplitt[0].toUpperCase());

                                        // Not recognized
                                    } catch (IllegalArgumentException ex) {

                                        // That's ass material type
                                        failure = true;

                                        // Log it
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: Material \u00a7c" + srcSplitt[0] + "\u00a77 doesn't exist.");
                                        }
                                    }

                                    // Get Side Offset
                                    if (OotilityCeption.IntTryParse(srcSplitt[1])) {

                                        // Worked
                                        srcS = Integer.parseInt(srcSplitt[1]);

                                        // Nah fam
                                    } else {

                                        // That's not an integer
                                        failure = true;

                                        // Log it
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: Side Offset \u00a7c" + srcSplitt[1] + "\u00a77 is not an integer number.");
                                        }
                                    }

                                    // Get Vertical Offset
                                    if (OotilityCeption.IntTryParse(srcSplitt[2])) {

                                        // Worked
                                        srcV = Integer.parseInt(srcSplitt[2]);

                                        // Nah fam
                                    } else {

                                        // That's not an integer
                                        failure = true;

                                        // Log it
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: Vertical Offset \u00a7c" + srcSplitt[2] + "\u00a77 is not an integer number.");
                                        }
                                    }

                                    // Get Forward Offset
                                    if (OotilityCeption.IntTryParse(srcSplitt[3])) {

                                        // Worked
                                        srcF = Integer.parseInt(srcSplitt[3]);

                                        // Nah fam
                                    } else {

                                        // That's not an integer
                                        failure = true;

                                        // Log it
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: Forward Offset \u00a7c" + srcSplitt[3] + "\u00a77 is not an integer number.");
                                        }
                                    }

                                    // Not <material> <s> <v> <f> syntax
                                } else {

                                    // That's ass syntax
                                    failure = true;

                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                        oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: Expected \u00a7e<Material> <Side Offset> <Vertical Offset> <Forward Offset>\u00a77, Doesn't Match \u00a7c" + srcBlocc);
                                    }

                                }

                                // Good job with that syntax. Create a Structure Block and add it
                                if (!failure) {

                                    // Make
                                    CSBlock csBlock = new CSBlock(srcMat, srcS, srcV, srcF);

                                    // Bake
                                    if (srcMeta != null) {
                                        csBlock.deserializeMeta(srcMeta);
                                    }

                                    // Create and add
                                    csBlocks.add(csBlock);

                                    // Is it the core block?
                                    if (srcS == 0 && srcV == 0 && srcF == 0 && coreBlock == null) {

                                        // Core Block Found
                                        coreBlock = csBlock;

                                    } else if (srcS == 0 && srcV == 0 && srcF == 0) {

                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                            oots.CPLog("Warning issued when loading custom structure \u00a73" + tName + "\u00a77: Multiple blocks are placed in the 0 0 0 coordinate! That makes no sense. Ignoring all but the first.");
                                        }
                                    }
                                }
                            }

                            // List is empty
                        } else {

                            // Structure is Nonexistent
                            failure = true;

                            // Log it
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: No structure can be made up of 0 blocks.");
                            }
                        }

                    } else {

                        // Structure is Nonexistent
                        failure = true;

                        // Log it
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                            oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: Not composed of any blocks");
                        }
                    }

                    // Any triggers occur?
                    ArrayList<CSTrigger> csTriggers = new ArrayList<>();
                    if (rawTriggers != null) {

                        // That there is at least 1 block in there
                        if (rawTriggers.size() > 0) {

                            // For each string
                            for (String srcTrigger : rawTriggers) {

                                // Current Trigger
                                CSTrigger csTrig = null;

                                // Check Trigger
                                try {
                                    // Yes, it seems to be
                                    csTrig = CSTrigger.valueOf(srcTrigger);

                                    // Not recognized
                                } catch (IllegalArgumentException ex) {

                                    // That's ass trigger type
                                    failure = true;

                                    // Log it
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                        oots.CPLog("Error when loading custom structure \u00a73" + tName + "\u00a77: Trigger \u00a7c" + srcTrigger + "\u00a77 doesn't exist.");
                                    }
                                }

                                // Good job with that syntax. Create a Structure Block and add it
                                if (!failure) {

                                    // Create and add
                                    csTriggers.add(csTrig);
                                }
                            }

                            // List is empty
                        } else {

                            // Log it
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                oots.CPLog("Warning issued when loading custom structure \u00a73" + tName + "\u00a77: Structure has no triggers defined!.");
                            }
                        }

                    } else {

                        // Log it
                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                            oots.CPLog("Warning issued when loading custom structure \u00a73" + tName + "\u00a77: There is nothing that will trigger it!");
                        }
                    }

                    // Identify Trigger Parameters
                    ArrayList<String> params = new ArrayList<>();
                    HashMap<CSTrigger, ArrayList<String>> triggerParams = new HashMap<>();
                    RefSimulator<String> logger = new RefSimulator<>("");

                    for (CSTrigger trig : csTriggers) {

                        // Build info
                        switch (trig) {

                            case PRESSUREPLATE_ANIMALS:
                            case PRESSUREPLATE_MONSTERS:
                                // Any Trigger Parameters Defined?
                                if (csConfig.contains(tName + "." + trig.toString())) {

                                    // Clear params
                                    params.clear();

                                    // Add the correctly-parsing ones
                                    for (String trigParam : csConfig.getStringList(tName + "." + trig.toString())) {

                                        // Split into args
                                        String[] nbtSplit = trigParam.split(" ");

                                        // If the length is correct
                                        if (nbtSplit.length == 2) {

                                            // If it makes sense
                                            if (!OotilityCeption.IsInvalidEntityNBTtestString(nbtSplit[0], nbtSplit[1], logger)) {

                                                // Add it to array
                                                params.add(trigParam);

                                                // Otherwise say what happened
                                            } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                                oots.CPLog("Warning issued when loading custom structure \u00a73" + tName + "\u00a77: Invalid \u00a7e" + trig.toString() + "\u00a77 trigger parameter \u00a73" + trigParam + "\u00a77 (" + logger.GetValue() + "\u00a77)");
                                                oots.CPLog("These are the valid entity key and name combinations:");
                                                oots.CPLog("\u00a73--> \u00a7e v <vanilla entity type>\u00a7: Matches for a vanilla entity. \u00a73v SKELETON");
                                                if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                                    oots.CPLog("\u00a73--> \u00a7e m <mythicmob mob id>\u00a7: Matches for a MythicMob type. \u00a73m SkeletonKing");
                                                }
                                            }

                                            // Nah fam wtf is that
                                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                            // Log it
                                            oots.CPLog("Warning issued when loading custom structure \u00a73" + tName + "\u00a77: Invalid \u00a7e" + trig.toString() +  "\u00a77 trigger parameter \u00a73" + trigParam + "\u00a77 (Not in the format \u00a7e<entity key> <entity name>\u00a77)");
                                            oots.CPLog("These are the valid entity key and name combinations:");
                                            oots.CPLog("\u00a73--> \u00a7e v <vanilla entity type>\u00a7: Matches for a vanilla entity. \u00a73v SKELETON");
                                            if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                                oots.CPLog("\u00a73--> \u00a7e m <mythicmob mob id>\u00a7: Matches for a MythicMob type. \u00a73m SkeletonKing");
                                            }
                                        }
                                    }

                                    // Put
                                    triggerParams.put(trig, params);

                                }
                                break;

                            case PRESSUREPLATE_ITEMS:
                            case SNEAK_PRESSUREPLATE_PLAYERS:
                            case PRESSUREPLATE_PLAYERS:
                            case SNEAK_PUNCH:
                            case PUNCH:
                            case SNEAK_INTERACT:
                            case INTERACT:
                                // Any Trigger Parameters Defined?
                                if (csConfig.contains(tName + "." + trig.toString())) {

                                    // Clear
                                    params.clear();

                                    // Add the correctly-parsing ones
                                    for (String trigParam : csConfig.getStringList(tName + "." + trig.toString())) {

                                        // Split into args
                                        String[] nbtSplit = trigParam.split(" ");

                                        // If the length is correct
                                        if (nbtSplit.length == 3 || nbtSplit.length == 4) {

                                            String amount = null;
                                            if (nbtSplit.length > 3) {
                                                amount = nbtSplit[3];
                                            }

                                            // If it makes sense
                                            if (!OotilityCeption.IsInvalidItemNBTtestString(nbtSplit[0], nbtSplit[1], nbtSplit[2], amount, logger)) {

                                                // Add it to array
                                                params.add(trigParam);

                                                // Otherwise say what happened
                                            } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                                oots.CPLog("Warning issued when loading custom structure \u00a73" + tName + "\u00a77: Invalid \u00a7e" + trig.toString() + "\u00a77 trigger parameter \u00a73" + trigParam + "\u00a77 (" + logger.GetValue() + "\u00a77)");
                                                oots.CPLog("These are the valid nbt key and name combinations:");
                                                oots.CPLog("\u00a73--> \u00a7e v <vanilla item name> 0\u00a7: Matches for a vanilla item. \u00a73v NETHER_STAR 0");
                                                oots.CPLog("\u00a73--> \u00a7e e <enchantment name> <enchantment level>\u00a7: Matches for a specific Enchantment-Level combination. \u00a73m sharpness 4");
                                                if (Gunging_Ootilities_Plugin.foundMMOItems) {
                                                    oots.CPLog("\u00a73--> \u00a7e m <mmoitem type> <mmoitem id>\u00a7: Matches for a specific MMOItem. \u00a73m CONSUMABLE MANGO");
                                                }
                                                oots.CPLog("\u00a73\u00a7oThe '0' for the vanilla item nbt is a placeholder for pre 1.13 minecraft versions where 'damage' was used instead of everything having its own id (ANDESITE was STONE 1)");
                                                oots.CPLog("\u00a7e\u00a7oWill check that the PLAYER is holding such item in their MAINHAND when activating the structure.");

                                            }
                                            // Nah fam wtf is that
                                        } else if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                            // Log it
                                            oots.CPLog("Warning issued when loading custom structure \u00a73" + tName + "\u00a77: Invalid \u00a7e" + trig.toString() + "\u00a77 trigger parameter \u00a73" + trigParam + "\u00a77 (Not in the format \u00a7e<nbt key> <primary nbt data> <secondary nbt data>\u00a77)");
                                            oots.CPLog("These are the valid nbt key and data combinations:");
                                            oots.CPLog("\u00a73--> \u00a7e v <vanilla item name> 0\u00a7: Matches for a vanilla item. \u00a73v NETHER_STAR 0");
                                            oots.CPLog("\u00a73--> \u00a7e e <enchantment name> <enchantment level>\u00a7: Matches for a specific Enchantment-Level combination. \u00a73m sharpness 4");
                                            if (Gunging_Ootilities_Plugin.foundMMOItems) {
                                                oots.CPLog("\u00a73--> \u00a7e m <mmoitem type> <mmoitem id>\u00a7: Matches for a specific MMOItem. \u00a73m CONSUMABLE MANGO");
                                            }
                                            oots.CPLog("\u00a73\u00a7oThe '0' for the vanilla item nbt is a placeholder for pre 1.13 minecraft versions where 'damage' was used instead of everything having its own id (ANDESITE was STONE 1)");
                                            oots.CPLog("\u00a7e\u00a7oWill check that the PLAYER is holding such item in their MAINHAND when activating the structure.");
                                        }
                                    }

                                    // Put
                                    triggerParams.put(trig, params);

                                    // If its not there, its not there
                                }
                                break;
                            case COMPLETE:
                            case SNEAK_COMPLETE:
                            case BREAK:
                            case SNEAK_BREAK:
                            default:
                                break;
                        }
                    }

                    // Just Translate Actions into ArrayList from List
                    ArrayList<String> csActions = new ArrayList<>();
                    if (rawActions != null) {

                        // Add any existing actions
                        csActions.addAll(rawActions);
                    }

                    // If its not a failure, load it vro!
                    if (!failure) {

                        // Create
                        CSStructure csStruct = new CSStructure(tName, coreBlock, csBlocks, csActions, csTriggers);
                        csStruct.triggerParameters = triggerParams;
                        csStruct.setVaultCost(rawVCost);
                        csStruct.setAllowPermutations(permutations);
                        csStruct.recalculatePermutations();

                        // Add
                        loadedStructures.add(csStruct);

                        // Map
                        csLoadedStructures.put(tName, loadedStructures.get(loadedStructures.size() - 1));

                        // Put
                        csStorageStructures.put(tName, csPair);

                        // Blacklist
                        for (String world : rawWorldBlacklist) {

                            // Log unknown
                            if (Bukkit.getWorld(world) == null && !Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                oots.CPLog("Warning when loading custom structure \u00a73" + tName + "\u00a77: Unknown world to blacklist\u00a7b " + world + "\u00a77, added to the blacklist list anyway. ");
                            }

                            // Blacklist World
                            csStruct.blacklistWorld(world);
                        }

                        // Whitelist
                        for (String world : rawWorldWhitelist) {

                            // Log unknown
                            if (Bukkit.getWorld(world) == null && !Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {
                                    oots.CPLog("Warning when loading custom structure \u00a73" + tName + "\u00a77: Unknown world to whitelist\u00a7b" + world + "\u00a77, added to the whitelist list anyway. ");
                            }

                            // Blacklist World
                            csStruct.whitelistWorld(world);
                        }
                    }
                }
            }
        }

    }

    public CSManager() {
    }

    /**
     * Gets the blocks from the cuboid the player has selected, relative to the specified core.
     * <p></p>
     * Ignores blocks in different world as the core.
     *
     * @return Empty list if the core is null. Will also return an empty list if WorldEdit is missing.
     * <p></p>
     * Can only return NULL if the player has no selection made (WorldEdit must be present)
     */
    @Nullable public static ArrayList<CSBlock> GenerateStructureFromPlayerSelection(@Nullable Block core, @Nullable Player player) {

        // Check basics
        if (core == null || !Gunging_Ootilities_Plugin.foundWorldEdit) {
            return new ArrayList<>();
        }

        // Get Selection World
        World selectionWorld = GooPWorldEdit.GetSelectionWorld(player);

        // No Selection Made?
        if (selectionWorld == null) {
            return null;
        }

        // Build structure from world
        ArrayList<Block> comp = new ArrayList<>();

        // Check world lol
        if (core.getWorld().equals(selectionWorld)) {

            // Get Max Location
            Location selectionMax = GooPWorldEdit.GetMaximumOfSelecton(player);
            Location selectionMin = GooPWorldEdit.GetMinimumOfSelecton(player);

            // BOth shall exist
            if (selectionMax != null && selectionMin != null) {

                // Triple Integral over Cartesian Co-Ordinates
                for (int xRl = selectionMin.getBlockX(); xRl <= selectionMax.getBlockX(); xRl++) {
                    for (int yRl = selectionMin.getBlockY(); yRl <= selectionMax.getBlockY(); yRl++) {
                        for (int zRl = selectionMin.getBlockZ(); zRl <= selectionMax.getBlockZ(); zRl++) {

                            // If not the core
                            if (core.getX() != xRl || core.getY() != yRl || core.getZ() != zRl) {

                                // Get such block from world
                                Block tBlock = core.getWorld().getBlockAt(xRl, yRl, zRl);

                                // Add if not the core ig
                                comp.add(tBlock);
                            }
                        }
                    }
                }

                // Fail
            } else {

                // No Selection Made
                return null;
            }
        }

        // Bake
        return GenerateStructureFromWorld(core, comp);
    }
    /**
     * Gets the blocks from a cuboid centered on the core and of the specified radius.
     *
     * @return Empty list if the core is null, at least the core otherwise.
     */
    @NotNull public static ArrayList<CSBlock> GenerateStructureFromWorld(@Nullable Block core, int cuboidRadius) {
        if (core == null) { return new ArrayList<>(); }

        // Build structure from world
        ArrayList<Block> comp = new ArrayList<>();

        // Don't waste your time if the radius is 0 lol
        if (cuboidRadius > 0) {

            // Triple Integral over Cartesian Co-Ordinates
            for (int xRl = (core.getX() - cuboidRadius); xRl <= (core.getX() + cuboidRadius); xRl++) {
                for (int yRl = (core.getY() - cuboidRadius); yRl <= (core.getY() + cuboidRadius); yRl++) {
                    for (int zRl = (core.getZ() - cuboidRadius); zRl <= (core.getZ() + cuboidRadius); zRl++) {

                        // If not the core
                        if (core.getX() != xRl || core.getY() != yRl || core.getZ() != zRl) {

                            // Get such block from world
                            Block tBlock = core.getWorld().getBlockAt(xRl, yRl, zRl);

                            // Add if not the core ig
                            comp.add(tBlock);
                        }
                    }
                }
            }
        }

        // Bake
        return GenerateStructureFromWorld(core, comp);
    }
    /**
     * Generates a structure from an array of blocks, relative to a specified core. Will ignore those blocks in a different world from the core.
     *
     * @return Empty list if the core is null. At least the core otherwise.
     */
    @NotNull public static ArrayList<CSBlock> GenerateStructureFromWorld(@Nullable Block core, @NotNull ArrayList<Block> blocks) {

        // Build structure from world
        ArrayList<CSBlock> comp = new ArrayList<>();

        // If null return empty
        if (core == null) { return comp; }
        World coreWorld = core.getWorld();

        //IMP//OotilityCeption.Log("\u00a78CORE\u00a7e IM\u00a77 Importing\u00a7b " + OotilityCeption.BlockLocation2String(core.getLocation()));

        // For every block provided
        for (Block block : blocks) {
            //IMP//OotilityCeption.Log("\u00a78CORE\u00a7e IM\u00a77 Damn\u00a73 " + OotilityCeption.BlockLocation2String(block.getLocation()));

            // Same world RIGHT?
            if (block.getWorld().equals(coreWorld)) {

                // Get Difference coords
                int diffX = (block.getX() - core.getX());
                int diffY = (block.getY() - core.getY());
                int diffZ = (block.getZ() - core.getZ());
                //IMP//OotilityCeption.Log("\u00a78CORE\u00a7e IM\u00a77 Difference\u00a79 " + diffX + " " + diffY + " " + diffZ);

                // Not the core right
                if (diffX != 0 || diffY != 0 || diffZ != 0) {

                    // Get That
                    CSBlock cs = fromDifference(block, diffX, diffY, diffZ);

                    // Add if no null
                    if (cs != null) {

                        // Add
                        comp.add(cs);

                        //IMP//OotilityCeption.Log("\u00a78CORE\u00a7e IM\u00a7a Added " + cs.getBlockType() + " " + cs.getSideOffset() + " " + cs.getVerticalOffset() + " " + cs.getForwardOffset() + " " + cs.serializeMeta());
                    }

                    //IMP//else { OotilityCeption.Log("\u00a78CORE\u00a7e IM\u00a7c Could not add"); }
                }
            }
        }

        // Build core
        comp.add(fromDifference(core, 0, 0, 0));

        return comp;
    }
    /**
     * Gets a Relative Structure Block with those co-ords from the core. This method always reads as {@link Orientations#SouthForward}
     *
     * @return Will be NULL if it is an ignorable type (any AIR, or BARRIER if its not a JSON furniture).
     */
    @Nullable public static CSBlock fromDifference(@NotNull Block tBlock, int diffX, int diffY, int diffZ) {

        // If it is structure void, it will be considered AIR
        if (tBlock.getType() == Material.STRUCTURE_VOID) {

            //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Air Void Detected\u00a79 " + diffX + " " + diffY + " " + diffZ);

            // Add as AIR
            return new CSBlock(Material.AIR, diffX, diffY, diffZ);
            //DEBUG//oots.C PLog("Added block \u00a73AIR\u00a77 with offsets \u00a7e" + (xRl - core.getX()) + "\u00a77, \u00a7e"+ (yRl - core.getY()) + "\u00a77, \u00a7e"+ (zRl - core.getZ()) + "\u00a77.");

        // If its neither Air nor barrier, add it to the composition
        } else if (!tBlock.getType().isAir() && tBlock.getType() != Material.BARRIER) {

            //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Importing Solid\u00a79 " + diffX + " " + diffY + " " + diffZ);

            // Make
            CSBlock csBlock = new CSBlock(tBlock.getType(), diffX, diffY, diffZ);

            //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Created\u00a79 " + csBlock.getBlockType() + " " + csBlock.getSideOffset() + " " + csBlock.getVerticalOffset() + " " + csBlock.getForwardOffset());

            // Bake
            csBlock.readMeta(tBlock, Orientations.SouthForward);

            //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Read Meta\u00a79 " + csBlock.serializeMeta());

            // Add block
            return csBlock;
            //DEBUG//oots.C PLog("Added block \u00a73" + tBlock.getType() + "\u00a77 with offsets \u00a7e" + (xRl - core.getX()) + "\u00a77, \u00a7e"+ (yRl - core.getY()) + "\u00a77, \u00a7e"+ (zRl - core.getZ()) + "\u00a77.");

            // Structure VOID stands for ACTUAL AIR

        // It is a barrier? it may be a JSON
        } else if (tBlock.getType() == Material.BARRIER) {

            // Entity ref
            RefSimulator<ArmorStand> disp = new RefSimulator<>(null);

            // Oh damn it is?
            if (JSONPlacerUtils.IsJSON_Furniture(tBlock, disp)) {

                //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Importing JSON\u00a79 " + diffX + " " + diffY + " " + diffZ);

                // Make
                CSBlock csBlock = new CSBlock(tBlock.getType(), diffX, diffY, diffZ);

                //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Created\u00a79 " + csBlock.getBlockType() + " " + csBlock.getSideOffset() + " " + csBlock.getVerticalOffset() + " " + csBlock.getForwardOffset());

                // Bake
                csBlock.readMeta(tBlock, Orientations.SouthForward);

                //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Read Meta\u00a79 " + csBlock.serializeMeta());

                // Add block
                return csBlock;
            }
        }

        // Null
        //IMP//OotilityCeption.Log("\u00a78CORE\u00a7b FD\u00a77 Ignorable Case\u00a7c " + tBlock.getType().toString());
        return null;
    }
    @NotNull public static ArrayList<String> BlocksToConfig(@Nullable ArrayList<CSBlock> structureBlocks) {

        ArrayList<String> ret = new ArrayList<>();

        if (structureBlocks != null) {

            for (CSBlock csBlock : structureBlocks) {
                if (csBlock == null) { continue; }

                // Get Initial
                String str = csBlock.getBlockType() + " " + csBlock.getSideOffset() + " " + csBlock.getVerticalOffset() + " " + csBlock.getForwardOffset();

                // Add Meta Restrictions
                if (csBlock.hasMetaRestrictions()) {
                    str += "||" + csBlock.serializeMeta();
                }

                // Add in format
                ret.add(str);
            }
        }

        return ret;
    }

    public static ArrayList<String> TriggersToConfig(ArrayList<CSTrigger> structureTriggers) {

        ArrayList<String> ret = new ArrayList<>();

        if (structureTriggers != null) {

            for (CSTrigger csTrig : structureTriggers) {

                // Add in format
                ret.add(csTrig.toString());
            }
        }

        return ret;

    }
    public static void EditStructureComposition(String name, ArrayList<CSBlock> structureBlocks, RefSimulator<String> logger) {

        // Make sure it exists
        if (csStorageStructures.containsKey(name)) {

            // Get source configuration
            FileConfigPair ofgPair = csStorageStructures.get(name);

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Set Composition
            ofgStorage.set(name + ".Composition", BlocksToConfig(structureBlocks));

            // Save
            Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);

            // Edit loaded
            CSStructure csRef = csLoadedStructures.get(name);
            csRef.structureComposition = structureBlocks;
            for (CSBlock csBlock : structureBlocks) { if (csBlock.isCore()) { csRef.structureCenter = csBlock; } }
            csRef.recalculatePermutations();

            // Log
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully edited structure \u00a73" + name + "\u00a77. (No need to reload)");

        } else {

            // Mention it doesn't exist, this shouldn't be called
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "No structure of name \u00a73" + name + "\u00a77 is loaded! \u00a7cCant edit!");
        }
    }
    public static void EditStructureActions(String name, ArrayList<String> structureAction, RefSimulator<String> logger) {

        // Make sure it exists
        if (csStorageStructures.containsKey(name)) {

            // Get source configuration
            FileConfigPair ofgPair = csStorageStructures.get(name);

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Set Actions
            ofgStorage.set(name + ".Commands", structureAction);

            // Save
            Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);

            // Edit loaded
            csLoadedStructures.get(name).structureActions = structureAction;
            csLoadedStructures.get(name).evaluateForSpecialCommands();

            // Log
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully edited structure \u00a73" + name + "\u00a77. (No need to reload)");

        } else {

            // Mention it doesn't exist, this shouldn't be called
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "No structure of name \u00a73" + name + "\u00a77 is loaded! \u00a7cCant edit!");
        }

    }
    public static void saveOptions(@NotNull CSStructure structure, @Nullable RefSimulator<String> logger) {

        // Get source configuration
        FileConfigPair ofgPair = csStorageStructures.get(structure.getStructureName());

        // Get the latest version of the storage
        ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

        // Modify Storage
        YamlConfiguration ofgStorage = ofgPair.getStorage();

        // Set Actions
        ofgStorage.set(structure.getStructureName() + ".VaultCost", structure.getVaultCost() != 0 ? structure.getVaultCost() : null);
        ofgStorage.set(structure.getStructureName() + ".WhitelistedWorlds", structure.getWorldsWhitelist().size() > 0 ? structure.getWorldsWhitelist() : null);
        ofgStorage.set(structure.getStructureName() + ".BlacklistedWorlds", structure.getWorldsBlacklist().size() > 0 ? structure.getWorldsBlacklist() : null);
        ofgStorage.set(structure.getStructureName() + ".OmniInteractive", structure.isAllowPermutations() ? true : null);

        // Save
        Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);

        // Log
        OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully edited structure \u00a73" + structure.getStructureName() + "\u00a77. (No need to reload)");


    }
    public static void EditStructureTriggers(String name, ArrayList<CSTrigger> structureTrigger, RefSimulator<String> logger) {

        // Make sure it exists
        if (csStorageStructures.containsKey(name)) {

            // Get source configuration
            FileConfigPair ofgPair = csStorageStructures.get(name);

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Set Triggers
            ofgStorage.set(name + ".Triggers", TriggersToConfig(structureTrigger));

            // Save
            Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);

            // Edit loaded
            csLoadedStructures.get(name).structureTriggers = structureTrigger;

            // Log
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully edited structure \u00a73" + name + "\u00a77. (No need to reload)");

        } else {

            // Mention it doesn't exist, this shouldn't be called
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "No structure of name \u00a73" + name + "\u00a77 is loaded! \u00a7cCant edit!");
        }

    }
    public static void EditStructureTriggerParameters(String name, CSTrigger trig, ArrayList<String> params, RefSimulator<String> logger) {

        // Make sure it exists
        if (csStorageStructures.containsKey(name)) {

            // Get source configuration
            FileConfigPair ofgPair = csStorageStructures.get(name);

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Set Composition
            ofgStorage.set(name + "." + trig.toString(), params);

            // Save
            Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);

            // Put
            csLoadedStructures.get(name).triggerParameters.put(trig, params);

            // Log
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully edited structure \u00a73" + name + "\u00a77. (No need to reload)");

        } else {

            // Mention it doesn't exist, this shouldn't be called
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "No structure of name \u00a73" + name + "\u00a77 is loaded! \u00a7cCant edit!");
        }

    }
    public static void SaveNewStructure(String name, ArrayList<CSBlock> structureBlocks, ArrayList<String> structureAction, ArrayList<CSTrigger> structureTrigger, RefSimulator<String> logger) {

        // Make sure its actually 'new'
        if (!csLoadedStructures.containsKey(name)) {

            // Create the Parent Directory thing
            FileConfigPair csPair = Gunging_Ootilities_Plugin.theMain.GetConfigAt("custom-structures", name.toLowerCase() + ".yml", false, true);

            // Assuming its going to be a brand new file...
            YamlConfiguration ofgStorage = csPair.getStorage();

            // Set Composition
            ofgStorage.set(name + ".Composition", BlocksToConfig(structureBlocks));

            // Set Triggers
            ofgStorage.set(name + ".Triggers", TriggersToConfig(structureTrigger));

            // Set Actions
            ofgStorage.set(name + ".Commands", structureAction);

            // Save
            Gunging_Ootilities_Plugin.theMain.SaveFile(csPair);

            // Log
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully imported structure: \u00a73" + name + "\u00a77. Load it with \u00a7e/goop reload\u00a77. Further customize it in-game with \u00a7e/goop customstructures\u00a77!");

        } else {

            // Mention it already exists and, this shouldn't be called
            OotilityCeption.Log4Success(logger, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "Structure of name \u00a73" + name + " \u00a77already loaded! \u00a7cCant define a new one!");
        }
    }

    @NotNull public static Orientations CardinallyLooking(@NotNull Entity someone) {
        // Where are the player looking at?
        switch (someone.getFacing()) {
            case EAST_NORTH_EAST:
            case EAST_SOUTH_EAST:
            case EAST:
                return Orientations.EastForward;
            case WEST_NORTH_WEST:
            case WEST_SOUTH_WEST:
            case WEST:
                return Orientations.WestForward;
            case SOUTH_EAST:
            case SOUTH_SOUTH_EAST:
            case SOUTH_SOUTH_WEST:
            case SOUTH:
                return Orientations.SouthForward;
            default:
                return Orientations.NorthForward;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnPressurePlatePress(EntityInteractEvent event) {
        if (loadedStructures.size() == 0) { return; }

        //DEBUG//OotilityCeption oots = new OotilityCeption();
        //DEBUG//OotilityCeption. Log("\u00a7oEntityInteractEvent - by \u00a7b" + event.getEntity().getName() + "\u00a77 on \u00a7e" + event.getBlock().getType());

        // Is it a player?
        if (event.getEntity() instanceof Player) {

            //DEBUG//OotilityCeption. Log("Identified as PLAYER");
            /*

            // Only 'Entity Interact Event' kinda thing is Pressure Plates so...
            if (OotilityCeption.IsPressurePlate(event.getBlock().getType())) {
                //DEBUG//OotilityCeption. Log("Seems to be a PRESSURE PLATE event.");

                // What kinda pressure plate trigger would it be
                CustomStructureTriggers trig = CustomStructureTriggers.PRESSUREPLATE_PLAYERS;
                if (((Player) event.getEntity()).isSneaking()) { trig = CustomStructureTriggers.SNEAK_PRESSUREPLATE_PLAYERS; }
                //DEBUG//OotilityCeption. Log("Expected Trigger: \u00a73" + trig.toString());

                // Any structures that match the trigger and block?
                for (CustomStructure csMatch : MatchesCenterblock(MatchesTrigger(loadedStructures, trig), event.getBlock())) {
                    //DEBUG//OotilityCeption. Log(" \u00a7b+ \u00a77Found " + csMatch.getName());

                    // Well, Until the first one is a full match!
                    if (csMatch.StructureMatches(event.getBlock(), false, false, false)) {
                        //DEBUG//OotilityCeption. Log(" \u00a7a+ \u00a77Matched Fully");

                        // Does player match?
                        if (csMatch.PlayerHoldingMatches((Player)event.getEntity(), trig)) {
                            //DEBUG//OotilityCeption. Log(" \u00a7a+ \u00a77Player Holding Match");

                            // Freaking activate it
                            csMatch.StructureActivation((Player)event.getEntity(), event.getBlock(), CardinallyLooking(event.getEntity()));
                        }
                    }
                }
            }
            // */

            // Only Pressure plate interactions are supported from non-players
        } else if (OotilityCeption.IsPressurePlate(event.getBlock().getType())) {

            //DEBUG//OotilityCeption. Log("Seems to be a PRESSURE PLATE event.");

            // What kinda pressure plate trigger would it be
            CSTrigger trig;

            // Is it a monster
            if (OotilityCeption.IsMonster(event.getEntity().getType())) {

                //DEBUG//OotilityCeption. Log("Identified as MONSTER");

                // Monster kind
                trig = CSTrigger.PRESSUREPLATE_MONSTERS;

                // Dropped Item perhaps?
            } else if (event.getEntity().getType() == GooP_MinecraftVersions.GetVersionEntityType(GooPVersionEntities.DROPPED_ITEM)) {

                //DEBUG//OotilityCeption. Log("Identified as ITEM");

                // Dropped Item
                trig = CSTrigger.PRESSUREPLATE_ITEMS;

                // Otherwise this bad boi is a fucking animal alv
            } else {

                //DEBUG//OotilityCeption. Log("Identified as ANIMAL");

                // Animals alv
                trig = CSTrigger.PRESSUREPLATE_ANIMALS;
            }

            // Any structures that match the trigger and block?
            for (CSStructure csMatch : matchesCoreBlock(MatchesTrigger(loadedStructures, trig), event.getBlock())) {

                //DEBUG//OotilityCeption. Log("\u00a7bReading structure \u00a73" + csMatch.getName());

                // Well, Until the first one is a full match!
                CSMatchResult b = csMatch.structureMatches(event.getBlock(), false, false, false);
                if (b != null) {

                    //DEBUG//OotilityCeption. Log("Composition Match");

                    // Does the entity match any existing filters
                    if (csMatch.PressurePlateEntityMatches(event.getEntity())) {

                        //DEBUG//OotilityCeption. Log("Entity Allowed Match");

                        // Freakin activate it
                        csMatch.activate(event.getEntity(), event.getBlock(), b, CardinallyLooking(event.getEntity()));
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnBlockPlace(BlockPlaceEvent event) {
        if (loadedStructures.size() == 0) { return; }

        CSTrigger trig = CSTrigger.COMPLETE;
        if (event.getPlayer().isSneaking()) {
            trig = CSTrigger.SNEAK_COMPLETE;
        }
        //DEBUG//OotilityCeption.Log("\u00a7oBlockPlaceEvent - by \u00a7b" + event.getPlayer().getName() + "\u00a77 with block \u00a73" + event.getBlockPlaced().getType() + " \u00a7a" + trig.toString());

        // Any structures that match the trigger and block?
        for (CSStructure csMatch : matchesCoreBlock(MatchesTrigger(loadedStructures, trig), event.getBlockPlaced())) {

            //DEBUG//OotilityCeption.Log("Testing Structure \u00a73" + csMatch.getStructureName());

            // Well, Until the first one is a full match!
            CSMatchResult b = csMatch.structureMatches(event.getBlockPlaced(), false, false, false);
            if (b != null) {

                //DEBUG//OotilityCeption.Log("Composition Match");

                // Freakin activate it (Skips holding matches cuz that doesn't apply here)
                csMatch.activate(event.getPlayer(), event.getBlockPlaced(),b, CardinallyLooking(event.getPlayer()));

            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnBlockPlace(JSONFurniturePlaceEvent event) {
        if (loadedStructures.size() == 0) { return; }

        // No player no service
        if (event.getPlayer() == null) { return; }

        CSTrigger trig = CSTrigger.COMPLETE;
        if (event.getPlayer().isSneaking()) { trig = CSTrigger.SNEAK_COMPLETE; }

        //DEBUG//OotilityCeption.Log("\u00a7oJSONFurniturePlaceEvent - by \u00a7b" + event.getPlayer().getName() + "\u00a77 with block \u00a73" + event.getBlock().getType() + " \u00a7a" + trig.toString());

        // Any structures that match the trigger and block?
        for (CSStructure csMatch : matchesCoreBlock(MatchesTrigger(loadedStructures, trig), event.getBlock())) {

            //DEBUG//OotilityCeption.Log("Testing Structure \u00a73" + csMatch.getStructureName());

            // Well, Until the first one is a full match!
            CSMatchResult b = csMatch.structureMatches(event.getBlock(), false, false, false);
            if (b != null) {

                //DEBUG//OotilityCeption.Log("Composition Match");

                // Freakin activate it (Skips holding matches cuz that doesn't apply here)
                csMatch.activate(event.getPlayer(), event.getBlock(),b, CardinallyLooking(event.getPlayer()));

            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnBlockBreak(BlockBreakEvent event) {
        if (loadedStructures.size() == 0) { return; }

        CSTrigger trig = CSTrigger.BREAK;
        if (event.getPlayer().isSneaking()) {
            trig = CSTrigger.SNEAK_BREAK;
        }

        //DEBUG//OotilityCeption oots = new OotilityCeption();
        //DEBUG//oots.C PLog("\u00a7oBlockBreakEvent - by \u00a7b" + event.getPlayer().getName() + "\u00a77 with block \u00a73" + event.getBlock());

        // Any structures that match the trigger and block?
        for (CSStructure csMatch : matchesCoreBlock(MatchesTrigger(loadedStructures, trig), event.getBlock())) {

            //DEBUG//oots.C PLog("Testing Structure \u00a73" + csMatch.getName());

            // Well, Until the first one is a full match!
            CSMatchResult b = csMatch.structureMatches(event.getBlock(), false, false, false);
            if (b != null) {

                //DEBUG//oots.C PLog("Composition Match");

                // Freakin activate it (Skips holding matches cuz that doesn't apply here)
                csMatch.activate(event.getPlayer(), event.getBlock(), b, CardinallyLooking(event.getPlayer()));
            }
        }
    }

    ArrayList<UUID> observedPlayers = new ArrayList<>();
    HashMap<UUID, ArrayList<Block>> pressuredPlayersFake = new HashMap<>();
    HashMap<UUID, ArrayList<Block>> pressuredPlayers = new HashMap<>();
    @EventHandler
    public void OnMove(PlayerMoveEvent bruh) {
        if (loadedStructures.size() == 0) { return; }
        if (!Gunging_Ootilities_Plugin.csPressurePlates) { return; }

        if (observedPlayers.size() == 0) { return; }
        if (!observedPlayers.contains(bruh.getPlayer().getUniqueId())) { return; }

        // Found?
        ArrayList<Block> steppedOn = pressuredPlayers.get(bruh.getPlayer().getUniqueId());
        if (steppedOn == null) { steppedOn = new ArrayList<>(); }

        // Pressure Plate examine
        for (int blockIndex = 0; blockIndex < steppedOn.size(); blockIndex++) {

            // Get
            Block blockItself = steppedOn.get(blockIndex);

            // Remove if unactivated ig
            BlockData bloccData = blockItself.getBlockData();

            // Well uuuuh
            if (bloccData instanceof Powerable) {
                Powerable bloccPower = (Powerable) bloccData;

                if (!bloccPower.isPowered()) {
                    //DBG//OotilityCeption.Log(" \u00a7c- \u00a77Removed");

                    // Remove
                    steppedOn.remove(blockItself);

                    // Repeat Index
                    blockIndex--;
                }

            } else {
                //DBG//OotilityCeption.Log(" \u00a7c- \u00a77Excluded");

                // Remove
                steppedOn.remove(blockItself);

                // Repeat Index
                blockIndex--;
            }
        }

        // Found?
        ArrayList<Block> nextStepOn = pressuredPlayersFake.get(bruh.getPlayer().getUniqueId());
        if (nextStepOn != null) {

            // Add
            for (int n = 0; n < nextStepOn.size();) {

                // No tin right
                if (!steppedOn.contains(nextStepOn.get(n))) {

                    // Add
                    steppedOn.add(nextStepOn.get(n));
                }

                // Remove
                nextStepOn.remove(n);
            }
        }

        // F
        if (steppedOn.size() == 0) { observedPlayers.remove(bruh.getPlayer().getUniqueId()); }

        // Set ig
        pressuredPlayers.put(bruh.getPlayer().getUniqueId(), steppedOn);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerInteract(PlayerInteractEvent event) {
        if (loadedStructures.size() == 0) { return; }

        //OotilityCeption.Log("\u00a7bInteraction: \u00a7aH:" + OotilityCeption.IsAirNullAllowed(event.getItem()) + " B: " + event.getClickedBlock().getType() + " A:" + event.getAction().toString() + " M:" + event.getMaterial() + " N:" + event.getEventName());

        // Wont activate if crouching (pressure plates do activate)
        if (event.getHand() == EquipmentSlot.OFF_HAND) { return; }
        if (event.getClickedBlock() == null) { return; }

        CSTrigger trig = null;

        //DEBUG//OotilityCeption oots = new OotilityCeption();
        //DEBUG//oots.C PLog("\u00a7oPlayerInteractEvent - by \u00a7b" + event.getPlayer().getName() + "\u00a77 of type \u00a7e" + event.getAction() + "\u00a77 with block \u00a73" + event.getClickedBlock());

        // What kinda action is it?
        switch (event.getAction()) {
            //region PHYSICAL
            case PHYSICAL:

                // Either TripWire or Pressure Plate or smth
                if (Gunging_Ootilities_Plugin.csPressurePlates && OotilityCeption.IsPressurePlate(event.getClickedBlock().getType())) {

                    observedPlayers.add(event.getPlayer().getUniqueId());
                    ArrayList<Block> steppedOn = pressuredPlayersFake.get(event.getPlayer().getUniqueId());
                    if (steppedOn == null) { steppedOn = new ArrayList<>(); }
                    ArrayList<Block> steppedOnA = pressuredPlayers.get(event.getPlayer().getUniqueId());
                    if (steppedOnA == null) { steppedOnA = new ArrayList<>(); }

                    // Noncontinuous fist
                    if (!steppedOn.contains(event.getClickedBlock()) && !steppedOnA.contains(event.getClickedBlock())) {
                        //DBG//OotilityCeption.Log(" \u00a7a+ \u00a77Included");

                        // Identify Trigger
                        trig = CSTrigger.PRESSUREPLATE_PLAYERS;
                        if (event.getPlayer().isSneaking()) { trig = CSTrigger.SNEAK_PRESSUREPLATE_PLAYERS; }

                        // Add
                        steppedOn.add(event.getClickedBlock());

                        // Put
                        pressuredPlayersFake.put(event.getPlayer().getUniqueId(), steppedOn);

                    } else {

                        // Identify Trigger
                        trig = CSTrigger.PRESSUREPLATE_PLAYERS_CONTINUOS;
                        if (event.getPlayer().isSneaking()) { trig = CSTrigger.SNEAK_PRESSUREPLATE_PLAYERS_CONTINUOUS; }
                    }
                }
                break;
            //endregion
            //region INTERACT (RIGHT_CLICK_BLOCK)
            case RIGHT_CLICK_BLOCK:

                // Identify Trigger
                trig = CSTrigger.INTERACT;
                if (event.getPlayer().isSneaking()) { trig = CSTrigger.SNEAK_INTERACT; }

                break;
            //endregion
            //region PUNCH (LEFT_CLICK_BLOCK)
            case LEFT_CLICK_BLOCK:

                // Identify Trigger
                trig = CSTrigger.PUNCH;
                if (event.getPlayer().isSneaking()) { trig = CSTrigger.SNEAK_PUNCH; }

                break;
            default: break;
            //endregion
        }

        // If the trig is real
        if (trig != null) {

            // Get
            for (CSStructure csMatch : matchesCoreBlock(MatchesTrigger(loadedStructures, trig), event.getClickedBlock())) {

                //DEBUG//oots.C PLog("\u00a7bReading structure \u00a73" + csMatch.getName());

                // Well, Until the first one is a full match!
                CSMatchResult b = csMatch.structureMatches(event.getClickedBlock(), false, false, false);
                if (b != null) {

                    //DEBUG//oots.C PLog("Composition Match");

                    // Does Player Match?
                    if (csMatch.PlayerHoldingMatches(event.getPlayer(), trig)) {

                        //DEBUG//oots.C PLog("Entity Allowed Match");

                        // Freakin activate it
                        csMatch.activate(event.getPlayer(), event.getClickedBlock(), b, CardinallyLooking(event.getPlayer()));

                        // Cancel EVent I Guess
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /**
     * @param source Custom Structures among which to test this block
     *
     * @param targetBlock Block to be tested
     *
     * @return Which structures' core blocks match this
     */
    public static ArrayList<CSStructure> matchesCoreBlock(@NotNull ArrayList<CSStructure> source, @NotNull Block targetBlock) {

        //DEBUG//OotilityCeption.Log("Searching for structure to match core block \u00a7e" + targetBlock.getType());

        // Creates Array
        ArrayList<CSStructure> ret = new ArrayList<>();

        // Adds matching structures
        for (CSStructure csStructure : source) {

            //DEBUG//OotilityCeption.Log("Evaluating structure \u00a73" + csStructure.getStructureName() + "\u00a7e (\u00a77" + csStructure.getStructureCenter().getBlockType().toString() + "\u00a7e)");

            // Ignore world black whitelist
            if (!csStructure.isWorkInWorld(targetBlock.getWorld().getName())) {

                //DEBUG//OotilityCeption.Log("\u00a7cStructure no worky in this world. ");
                continue; }

            //DEBUG//OotilityCeption.Log("\u00a78CS \u00a7cCBM\u00a77 Air Target Block? " + OotilityCeption.IsAirNullAllowed(targetBlock));
            //DEBUG//OotilityCeption.Log("\u00a78CS \u00a7cCBM\u00a77 JSON Center Block? " + csStructure.getStructureCenter().getBlockMeta().get(CSMSFurnitureJSON.theSource));
            //DEBUG//for (CustomStructureMetaSource sours : csStructure.getStructureCenter().getBlockMeta().keySet()) { OotilityCeption.Log("\u00a78CS \u00a7cCBM\u00a77 Meta Source:\u00a7e " + sours.getInternalName() + " \u00a77>\u00a7e " + csStructure.getStructureCenter().getBlockMeta().get(sours).toString()); }

            // Does the target block match the type?
            if (csStructure.getPermutations().containsKey(targetBlock.getType())) {

                //DEBUG//OotilityCeption.Log("\u00a7aMatches! \u00a77Added " + csStructure.getStructureName() + " \u00a77to result.");

                // Add to list
                ret.add(csStructure);
            }
        }

        return ret;
    }

    public static ArrayList<CSStructure> MatchesTrigger(ArrayList<CSStructure> source, CSTrigger triggeredReason) {

        //DEBUG//OotilityCeption.Log("Searching for structure to match trigger \u00a7e" + triggeredReason);

        // Creates Array
        ArrayList<CSStructure> ret = new ArrayList<>();

        // Adds matching structures
        for (CSStructure csStructure : source) {

            //DEBUG//OotilityCeption.Log("Evaluating structure \u00a73" + csStructure.getStructureName());
            //DEBUG//for (CustomStructureTriggers trig : csStructure.structureTriggers) { OotilityCeption.Log("\u00a7e - \u00a77" + trig); }

            // Does the target block match the type?
            if (csStructure.structureTriggers.contains(triggeredReason)) {

                //DEBUG//OotilityCeption.Log("\u00a7aMatches! \u00a77Added " + csStructure.getStructureName() + " \u00a77to result.");

                // Add to list
                ret.add(csStructure);
            }
        }

        return ret;
    }
}
