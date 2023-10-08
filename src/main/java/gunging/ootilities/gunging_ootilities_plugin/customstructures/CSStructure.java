package gunging.ootilities.gunging_ootilities_plugin.customstructures;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPVault;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Physical;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMatchResult;
import gunging.ootilities.gunging_ootilities_plugin.events.JSONPlacerUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * All information needed to check if a block in the world should run which command.
 */
public class CSStructure {

    //region o-o-o-o-o-o-o-o-o-o Variables o-o-o-o-o-o-o-o-o-o
    /**
     * Name of the structure
     */
    @NotNull String structureName;
    /**
     * @return Name of the structure
     */
    @NotNull public String getStructureName() { return structureName; }
    /**
     * @param structureName Name of the structure
     */
    public void setStructureName(@NotNull String structureName) { this.structureName = structureName; }

    //region Composition
    /**
     * <b>Use at own risk.</b> Exponential computational problem the more
     * structure composition there is.
     *
     * @param allow If this structure can be right-clicked by any of its blocks
     *              (as opposed to only by its structure core)
     */
    public void setAllowPermutations(boolean allow) { allowPermutations = allow; }
    /**
     * <b>Use at own risk.</b> Exponential computational problem the more
     * structure composition there is.
     *
     * @return If this structure can be right-clicked by any of its blocks
     *         (as opposed to only by its structure core)
     */
    public boolean isAllowPermutations() { return allowPermutations; }
    boolean allowPermutations;

    /**
     * Center of the Structure
     */
    @NotNull CSBlock structureCenter;
    /**
     * @return Center of the Structure
     */
    @NotNull public CSBlock getStructureCenter() { return structureCenter; }
    /**
     * @param structureCenter Center of the Structure
     */
    public void setStructureCenter(@NotNull CSBlock structureCenter) { this.structureCenter = structureCenter; }
    /**
     * List of blocks that make up this structure.
     */
    @NotNull ArrayList<CSBlock> structureComposition;
    /**
     * List of blocks that make up this structure.
     */
    @NotNull public ArrayList<CSBlock> getStructureComposition() { return structureComposition; }
    /**
     * List of blocks that make up this structure.
     */
    public void setStructureComposition(@NotNull ArrayList<CSBlock> structureComposition) { this.structureComposition = structureComposition; }

    @NotNull final ArrayList<String> worldsWhitelist = new ArrayList<>();
    @NotNull final ArrayList<String> worldsBlacklist = new ArrayList<>();
    /**
     * @return Worlds this is allowed to work in
     */
    @NotNull public ArrayList<String> getWorldsWhitelist() { return worldsWhitelist; }
    /**
     * @return Worlds this is not allowed to work in
     */
    @NotNull public ArrayList<String> getWorldsBlacklist() { return worldsBlacklist; }
    /**
     * @param world World to block this structure from working
     */
    public void blacklistWorld(@NotNull String world) { worldsBlacklist.add(world); }
    /**
     * @param world World this structure will work in
     */
    public void whitelistWorld(@NotNull String world) { worldsWhitelist.add(world); }
    /**
     * @param world World to unblock this structure from working
     */
    public boolean unBlacklistWorld(@NotNull String world) { return worldsBlacklist.remove(world); }
    /**
     * @param world Remove restriction of only working in this world
     */
    public boolean unWhitelistWorld(@NotNull String world) { return worldsWhitelist.remove(world); }

    /**
     * @param world World to test
     * @return If this structure works in this world
     */
    public boolean isWorkInWorld(@NotNull String world) {

        // Any blacklist world met will cause it to not work
        for (String blacklisted : getWorldsBlacklist()) {

            if (blacklisted.equals(world)) { return false; }
        }

        // No whitelist requirement, success
        if (getWorldsWhitelist().size() == 0) { return true; }

        // Must match whitelisted
        for (String whitelisted : getWorldsWhitelist()) {

            if (whitelisted.equals(world)) { return true; }
        }

        // Not whitelisted
        return false;
    }
    //endregion

    //region Triggering
    /**
     * Sorts the blocks of the composition based on what
     * block type they are, so that if permutations are
     * enabled, one can easily iterate through all the
     * possibilities.
     */
    public void recalculatePermutations() {

        // Clear previous results
        permutations.clear();

        // Done if no permutations
        if (isAllowPermutations()) {

            // For all composition blocks
            for (CSBlock cor : getStructureComposition()) {

                // Well lets see
                ArrayList<CSBlock> core = permutations.get(cor.getBlockType());
                if (core == null) { core = new ArrayList<>(); }

                // If it has that same block type, add it
                core.add(cor);
                permutations.put(cor.getBlockType(), core);
            }

        // No permutations, just the center block
        } else {

            // Add core block
            ArrayList<CSBlock> core = new ArrayList<>();
            core.add(getStructureCenter());
            permutations.put(getStructureCenter().getBlockType(), core);
        }
    }

    /**
     * @return All the blocks of the composition, sorted by which
     *         material of block they are.
     */
    @NotNull public HashMap<Material, ArrayList<CSBlock>> getPermutations() { return permutations; }
    @NotNull final HashMap<Material, ArrayList<CSBlock>> permutations = new HashMap<>();

    /**
     * Reasons the structure will run
     */
    @NotNull ArrayList<CSTrigger> structureTriggers;
    /**
     * @return Reasons the structure will run
     */
    @NotNull public ArrayList<CSTrigger> getStructureTriggers() { return structureTriggers; }
    /**
     * @param structureTriggers  Reasons the structure will run
     */
    public void setStructureTriggers(@NotNull ArrayList<CSTrigger> structureTriggers) { this.structureTriggers = structureTriggers; }
    /**
     * Constrains to running the structure
     */
    @NotNull HashMap<CSTrigger, ArrayList<String>> triggerParameters = new HashMap<>();
    /**
     * @return Constrains to running the structure
     */
    @NotNull public HashMap<CSTrigger, ArrayList<String>> getTriggerParameters() { return triggerParameters; }
    /**
     * @param triggerParameters Constrains to running the structure
     */
    public void setTriggerParameters(@NotNull HashMap<CSTrigger, ArrayList<String>> triggerParameters) { this.triggerParameters = triggerParameters; }
    /**
     * How often who can this activate
     */
    @NotNull HashMap<UUID, GooPUnlockables> perEntityCooldowns = new HashMap<>();
    /**
     * @return How often who can this activate
     */
    @NotNull public HashMap<UUID, GooPUnlockables> getPerEntityCooldowns() { return perEntityCooldowns; }
    /**
     * @param perEntityCooldowns How often who can this activate
     */
    public void setPerEntityCooldowns(@NotNull HashMap<UUID, GooPUnlockables> perEntityCooldowns) { this.perEntityCooldowns = perEntityCooldowns; }
    /**
     * Global Cooldown
     */
    @NotNull OptimizedTimeFormat globalCooldown = new OptimizedTimeFormat();
    /**
     * @return Global Cooldown
     */
    @NotNull public OptimizedTimeFormat getGlobalCooldown() { return globalCooldown; }
    /**
     * @param globalCooldown Global Cooldown
     */
    public void setGlobalCooldown(@NotNull OptimizedTimeFormat globalCooldown) { this.globalCooldown = globalCooldown; }
    //endregion

    //region Actions
    /**
     * List of commands the structure will run
     */
    @NotNull ArrayList<String> structureActions;
    /**
     * @return List of commands the structure will run
     */
    @NotNull public ArrayList<String> getStructureActions() { return structureActions; }
    /**
     * @param structureActions List of commands the structure will run
     */
    public void setStructureActions(@NotNull ArrayList<String> structureActions) { this.structureActions = structureActions; }
    /**
     * If this structure has GooP Containers compatibility
     */
    boolean containersLinked = false;
    /**
     * @return If this structure has GooP Containers compatibility
     */
    public boolean isContainersLinked() { return containersLinked; }
    /**
     * @param containersLinked If this structure has GooP Containers compatibility
     */
    public void setContainersLinked(boolean containersLinked) { this.containersLinked = containersLinked; }
    /**
     * If this structure self destructs when used
     */
    boolean breakOnUse = false;
    /**
     * @return If this structure self destructs when used
     */
    public boolean isBreakOnUse() { return breakOnUse; }
    /**
     * @param breakOnUse If this structure self destructs when used
     */
    public void setBreakOnUse(boolean breakOnUse) { this.breakOnUse = breakOnUse; }
    //region Special Command Treatment
    /**
     * Evaluates every action, in search for a few special cases.
     */
    public void evaluateForSpecialCommands() {

        // Analyze every
        boolean foundContainers = false;
        boolean willBreak = false;
        for (String act : structureActions) {

            // If access
            if (act.toLowerCase().startsWith("goop containers access")) { foundContainers = true; } else

                // If break
                if (act.toLowerCase().startsWith("goop-break-structure")) { willBreak = true; }
        }

        // None
        containersLinked = foundContainers;
        breakOnUse = willBreak;
    }
    /**
     * Inherent structure information for Containers
     */
    @NotNull HashMap<Location, ArrayList<CSBlock>> inherentialsStorage = new HashMap<>();
    /**
     * @return Inherent structure information for Containers
     */
    @NotNull public HashMap<Location, ArrayList<CSBlock>> getInherentialsStorage() { return inherentialsStorage; }
    /**
     * @param inherentialsStorage Inherent structure information for Containers
     */
    public void setInherentialsStorage(@NotNull HashMap<Location, ArrayList<CSBlock>> inherentialsStorage) { this.inherentialsStorage = inherentialsStorage; }
    //endregion
    //endregion

    //region Vault
    /**
     * Vault cost of using
     */
    double vaultCost = 0D;
    /**
     * @return if it has vault cost of using
     */
    public boolean hasVaultCost() { return vaultCost != 0D; }
    /**
     * @param v Vault cost of using
     */
    public void setVaultCost(double v) { vaultCost = v; }
    /**
     * @return Vault cost of using
     */
    public double getVaultCost() { return vaultCost; }
    //endregion
    //endregion

    /**
     * Create a new structure. This does not really load it yet.
     *
     * @param name Name of the structure. Must be unique if you intend to load it
     * @param structureCore Core block of the structure
     * @param structureBlocks Composition of the Structure
     * @param structureAction Actions of the Structure
     * @param structureTrigger Triggers of the Structure
     */
    public CSStructure(@NotNull String name, @NotNull CSBlock structureCore, @NotNull ArrayList<CSBlock> structureBlocks, @NotNull ArrayList<String> structureAction, @NotNull ArrayList<CSTrigger> structureTrigger) {
        // Straight-Forward Shit
        structureName = name;
        structureCenter = structureCore;
        structureComposition = structureBlocks;
        structureActions = structureAction;
        structureTriggers = structureTrigger;

        /*
         * Any action uses containers physicals?
         * Any break structure instance?
         */
        evaluateForSpecialCommands();
    }

    /**
     * @param worldCenter Block clicked by the player
     * @param forwardOrientation Expected orientation of the structure
     * @param mirrorForward Flip in forward?
     * @param mirrorSide Flip in sideways?
     * @param upsideDown Flip in upside down?
     *
     * @return If the structure matched, the list of the blocks that matched.
     */
    @Nullable public CSMatchResult structureMatches(@NotNull Block worldCenter, @NotNull Orientations forwardOrientation, boolean mirrorForward, boolean mirrorSide, boolean upsideDown) {
        //OotilityCeption.Log("Evaluating \u00a73" + structureName + "\u00a77 at orientation \u00a7e" + forwardOrientation + " \u00a77(\u00a7b" + containersLinked + "\u00a77)");

        // Assume the core is the structure center
        return structureMatches(getStructureCenter(), worldCenter, forwardOrientation, mirrorForward, mirrorSide, upsideDown);
    }
    /**
     * @param interactedBlock Block clicked by the player
     * @param forwardOrientation Expected orientation of the structure
     * @param mirrorForward Flip in forward?
     * @param mirrorSide Flip in sideways?
     * @param upsideDown Flip in upside down?
     *
     * @return If the structure matched, the list of the blocks that matched.
     */
    @Nullable public CSMatchResult structureMatches(@NotNull CSBlock csInteractedBlock, @NotNull Block interactedBlock, @NotNull Orientations forwardOrientation, boolean mirrorForward, boolean mirrorSide, boolean upsideDown) {
        //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a77 Evaluating \u00a73" + structureName + "\u00a77 at orientation \u00a7b" + forwardOrientation + "\u00a77 (Inherential? \u00a7b" + containersLinked + "\u00a77)");
        //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a77 Core Block \u00a7e" + asCore.getSideOffset() + " " + asCore.getVerticalOffset() + " " + asCore.getForwardOffset() + "\u00a7d " + asCore.getBlockType().toString());
        //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a77 Wrld Block \u00a7e" + worldCenter.getX() + " " + worldCenter.getY() + " " + worldCenter.getZ() + "\u00a7d " + worldCenter.getType().toString());

        // Result
        ArrayList<Block> ret = new ArrayList<>();
        Block worldCore = csInteractedBlock.isCore() ? interactedBlock : null;
        // Quickly check that it even matches the center block
        if (!csInteractedBlock.matches(interactedBlock, forwardOrientation)) {
            //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a7c World Center Match Failure");
            return null; }

        int sideMultiplier = ((OotilityCeption.Convert2Int(mirrorSide) * -2) + 1);
        int verticalMultiplier = ((OotilityCeption.Convert2Int(upsideDown) * -2) + 1);
        int forwardMultiplier = ((OotilityCeption.Convert2Int(mirrorForward) * -2) + 1);
        ArrayList<CSBlock> temporalInherential = new ArrayList<>();

        /*
         * The inherent structure is the position of all blocks that matched,
         * relative to the core, with forcibly SouthForward orientations.
         *
         * If permutations are enabled, we must perform this operation to
         * rotate the coordinates of this permuted block, in order to add/subtract
         * them later when calculating the inherent structure which shall always
         * be relative to the CORE block, not the permuted block.
         */
        RefSimulator<Integer> transX = new RefSimulator<>(csInteractedBlock.getSideOffset());
        RefSimulator<Integer> transY = new RefSimulator<>(csInteractedBlock.getVerticalOffset());
        RefSimulator<Integer> transZ = new RefSimulator<>(csInteractedBlock.getForwardOffset());
        if (isAllowPermutations() && containersLinked) { bakeOrientations(transX, transY, transZ, forwardOrientation); }

        //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a77 Transformed Core Offsets:\u00a7e " + transX.getValue() + " " + transY.getValue() + " " + transZ.getValue());

         // Check each of the blocks until one not matches :v
        for (CSBlock csBlock : structureComposition) {
            //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a7b +\u00a77 Structure Block:\u00a7e " + csBlock.getSideOffset() + " " + csBlock.getVerticalOffset() + " " + csBlock.getForwardOffset() + "\u00a7d " + csBlock.getBlockType().toString());

            CSBlock inherential;

            // If it is not the same block that already matched
            if (csBlock.getForwardOffset()  != csInteractedBlock.getForwardOffset() ||
                csBlock.getSideOffset()     != csInteractedBlock.getSideOffset() ||
                csBlock.getVerticalOffset() != csInteractedBlock.getVerticalOffset()) {

                // Obtain the TRUE relative testing co-ordinates
                RefSimulator<Integer> rawX = new RefSimulator<>(csBlock.getSideOffset()     - csInteractedBlock.getSideOffset());
                RefSimulator<Integer> rawY = new RefSimulator<>(csBlock.getVerticalOffset() - csInteractedBlock.getVerticalOffset());
                RefSimulator<Integer> rawZ = new RefSimulator<>(csBlock.getForwardOffset()  - csInteractedBlock.getForwardOffset());
                bakeOrientations(rawX, rawY, rawZ, forwardOrientation);
                //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a7b +\u00a77 Transformed Offsets:\u00a7e " + rawX.getValue() + " " + rawY.getValue() + " " + rawZ.getValue());

                // Mirror and Offset
                Integer expctdX = rawX.getValue();
                expctdX *= sideMultiplier;
                expctdX += interactedBlock.getX();

                // Mirror and Offset
                Integer expctdY = rawY.getValue();
                expctdY *= verticalMultiplier;
                expctdY += interactedBlock.getY();

                // Mirror and Offset
                Integer expctdZ = rawZ.getValue();
                expctdZ *= forwardMultiplier;
                expctdZ += interactedBlock.getZ();

                // Get block at those co-ordinates
                Block targetBlock = interactedBlock.getWorld().getBlockAt(expctdX, expctdY, expctdZ);
                //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a7b +\u00a77 World Offsets:\u00a7e " + expctdX + " " + expctdY + " " + expctdZ + "\u00a7d " + targetBlock.getType());

                // The structure did not match.
                if (!csBlock.matches(targetBlock, forwardOrientation)) {
                    //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a7c World Match Failure");
                    return null; }

                // Build inherential
                inherential = new CSBlock(csBlock.getBlockType(), expctdX - interactedBlock.getX() + transX.GetValue(), expctdY - interactedBlock.getY() + transY.getValue(), expctdZ - interactedBlock.getZ() + transZ.getValue());

                // Include world block
                ret.add(targetBlock);
                if (csBlock.isCore()) { worldCore = targetBlock; }

            } else {
                //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a7b World Center Exception");

                // Inherential is its own relative position to the core block
                inherential = new CSBlock(csBlock.getBlockType(), transX.GetValue(), transY.getValue(), transZ.getValue());

                // Include world block
                ret.add(interactedBlock);
            }

            // If inherent structure, include the data
            if (containersLinked) { temporalInherential.add(inherential); }
        }

        if (worldCore == null) {
            Gunging_Ootilities_Plugin.theOots.CLog(OotilityCeption.LogFormat("CustomStructures","Structure matched without matching core. \u00a7cReport to gunging. \u00a78gunging.ootilities.gunging_ootilities_plugin.customstructures.CSStructure"));
            return null; }

        // If linked to a PHYSICAL container, store its inherent structure link. It was approved so most likely about to run.
        if (containersLinked) {

            // Inherentially Store
            inherentialsStorage.put(interactedBlock.getLocation(), temporalInherential); }
        //SMC//OotilityCeption.Log("\u00a78GCS\u00a73 SMC\u00a7a World Match Success");

        // Has made it this far without returning null... APPROVED
        return new CSMatchResult(ret, interactedBlock, csInteractedBlock, worldCore, forwardOrientation);
    }
    /**
     * Checks if any orientation can match this structure at that block
     *
     * @param worldCenter That block to test at
     * @param mirrorForward Should mirror Z?
     * @param mirrorSide Should mirror X?
     * @param upsideDown Should mirror Y?
     * @return If it matched, the list of blocks in the world that matched.
     */
    @Nullable public CSMatchResult structureMatches(@NotNull Block worldCenter, boolean mirrorForward, boolean mirrorSide, boolean upsideDown) {

        // Tests for center block, it is at least of the same type right
        for (CSBlock core : getPermutations().get(worldCenter.getType())) {

            // Tester list
            CSMatchResult ret = structureMatches(core, worldCenter, Orientations.NorthForward, mirrorForward, mirrorSide, upsideDown);

            // Tests North
            if (ret != null) { return ret; }

            // Test west
            ret = structureMatches(core, worldCenter, Orientations.WestForward, mirrorForward, mirrorSide, upsideDown);
            if (ret != null) { return ret; }

            // Tests South
            ret = structureMatches(core, worldCenter, Orientations.SouthForward, mirrorForward, mirrorSide, upsideDown);
            if (ret != null) { return ret; }

            // Tests East
            ret = structureMatches(core, worldCenter, Orientations.EastForward, mirrorForward, mirrorSide, upsideDown);
            if (ret != null) { return ret; }
        }

        // None matched, Mega Fat L
        return null;
    }

    /**
     * Rotates the expected co-ordinates to the orientation being tested.
     *
     * @param side Expected X
     * @param vertical Expected Y
     * @param forward Expected Z
     * @param forwardOrientation Testing Orientation
     */
    public static void bakeOrientations(@NotNull RefSimulator<Integer> side, @NotNull RefSimulator<Integer> vertical, @NotNull RefSimulator<Integer> forward, @NotNull Orientations forwardOrientation) {

        // Obtain the TRUE testing co-ordinates
        int expctdX = side.getValue();
        switch (forwardOrientation) {
            case SouthForward: expctdX = side.getValue(); break;
            case EastForward: expctdX = forward.getValue(); break;
            case NorthForward: expctdX = -side.getValue(); break;
            case WestForward: expctdX = -forward.getValue(); break; }
        //expctdX *= sideMultiplier;
        //expctdX += worldCenter.getX();

        int expctdY = vertical.getValue();
        //expctdY *= verticalMultiplier;
        //expctdY += worldCenter.getY();

        int expctdZ = forward.getValue();
        switch (forwardOrientation) {
            case SouthForward: expctdZ = forward.getValue(); break;
            case EastForward:  expctdZ = -side.getValue(); break;
            case NorthForward: expctdZ = -forward.getValue(); break;
            case WestForward: expctdZ = side.getValue(); break; }
        //expctdZ *= forwardMultiplier;
        //expctdZ += worldCenter.getZ();

        // Return Results
        side.setValue(expctdX);
        vertical.setValue(expctdY);
        forward.setValue(expctdZ);
    }

    /**
     * Builds the structure at the specified location, in the specified orientation.
     * <p></p>
     * Except its not actually supported to rotate it, so it just builds it there.
     *
     * @param worldCenter The place where the core will be
     * @param forwardOrientation Honestly it currently does nothing.
     */
    public boolean generateAt(@Nullable Player p, @NotNull Location worldCenter, @NotNull Orientations forwardOrientation, boolean mirrorSide, boolean mirrorForward, boolean upsideDown) {

        // Mirroring?
        int sideMultiplier = ((OotilityCeption.Convert2Int(mirrorSide) * -2) + 1);
        int verticalMultiplier = ((OotilityCeption.Convert2Int(upsideDown) * -2) + 1);
        int forwardMultiplier = ((OotilityCeption.Convert2Int(mirrorForward) * -2) + 1);

        // Can the player build this?
        if (p != null) {

            // Basically the composition takes precedence
            for (CSBlock csBlock : structureComposition) {
                if (csBlock == null) { continue; }

                // Obtain the TRUE relative testing co-ordinates
                RefSimulator<Integer> rawX = new RefSimulator<>(csBlock.getSideOffset());
                RefSimulator<Integer> rawY = new RefSimulator<>(csBlock.getVerticalOffset());
                RefSimulator<Integer> rawZ = new RefSimulator<>(csBlock.getForwardOffset());
                bakeOrientations(rawX, rawY, rawZ, forwardOrientation);

                // Mirror and Offset
                int expctdX = rawX.getValue();
                expctdX *= sideMultiplier;
                expctdX += worldCenter.getBlockX();

                // Mirror and Offset
                int expctdY = rawY.getValue();
                expctdY *= verticalMultiplier;
                expctdY += worldCenter.getBlockY();

                // Mirror and Offset
                int expctdZ = rawZ.getValue();
                expctdZ *= forwardMultiplier;
                expctdZ += worldCenter.getBlockZ();

                // Get Location
                Location tLocation = new Location(worldCenter.getWorld(), expctdX, expctdY, expctdZ);

                // Get Block
                Block bkk = worldCenter.getWorld().getBlockAt(tLocation);

                // Run event
                BlockPlaceEvent event = new BlockPlaceEvent(bkk, bkk.getState(true), bkk.getRelative(BlockFace.WEST), p.getInventory().getItemInMainHand(), p, true);
                if (event.isCancelled()) { return false; }
            }
        }

        // Basically the composition takes precedence
        for (CSBlock csBlock : structureComposition) {
            if (csBlock == null) { continue; }

            // Obtain the TRUE relative testing co-ordinates
            RefSimulator<Integer> rawX = new RefSimulator<>(csBlock.getSideOffset());
            RefSimulator<Integer> rawY = new RefSimulator<>(csBlock.getVerticalOffset());
            RefSimulator<Integer> rawZ = new RefSimulator<>(csBlock.getForwardOffset());
            bakeOrientations(rawX, rawY, rawZ, forwardOrientation);

            // Mirror and Offset
            Integer expctdX = rawX.getValue();
            expctdX *= sideMultiplier;
            expctdX += worldCenter.getBlockX();

            // Mirror and Offset
            Integer expctdY = rawY.getValue();
            expctdY *= verticalMultiplier;
            expctdY += worldCenter.getBlockY();

            // Mirror and Offset
            Integer expctdZ = rawZ.getValue();
            expctdZ *= forwardMultiplier;
            expctdZ += worldCenter.getBlockZ();

            // Get Location
            Location tLocation = new Location(worldCenter.getWorld(), expctdX, expctdY, expctdZ);

            // Get Block
            Block bkk = worldCenter.getWorld().getBlockAt(tLocation);

            // Apply
            csBlock.applyMeta(bkk, forwardOrientation);
        }

        // Allowed
        return true;
    }

    /**
     * Activates this structure.
     *
     * This will set cooldowns and such, but wont read them.
     * It is expected that you already checked that the conditions
     * to activate this structure are favourable.
     *
     * @param asEntity Entity activating this structure.
     * @param interactedBlock Block being clicked, the core.
     * @param facing Facing of the structure when it was activated.
     * @param matchResult List of blocks from the world that comprise this structure.
     */
    public void activate(@NotNull Entity asEntity, @NotNull Block interactedBlock, @NotNull CSMatchResult matchResult, @NotNull Orientations facing) {
        //OotilityCeption. Log("\u00a7bCustomStructure \u00a7e- \u00a77As Player");

        // If premium enabled or oped I guess
        boolean asPlayer = asEntity instanceof Player;
//        if (!CustomStructures.IsPremiumEnabled()) {
//
//            // No
//            if (!asPlayer) { return; }
//
//            // Player not op?
//            if (!asEntity.isOp()) {
//                // Mention what is wrong
//                asEntity.sendMessage(OotilityCeption.LogFormat("Hey! Only OP players can use \u00a7e\u00a7l\u00a7oGooP Custom Structures\u00a77 without the Premium Version!"));
//
//                return; } }

        // Register activation
        CSManager.registerLastActivation(new CSActivation(asEntity, matchResult, this));

        // Bake
        ArrayList<String> parsedCommands = parseOrientation(parseAsCustomStructure(getStructureActions(), matchResult), facing);

        // Vault cost?
        if (hasVaultCost() && asPlayer) {

            // Get
            double v = getVaultCost();
            PlusMinusPercent op = new PlusMinusPercent(-v, true, false);

            // Is it charging?
            if (v >= 0) {

                // If balance is not enough
                if (GooPVault.GetPlayerBalance((Player) asEntity) < v) {

                    // Tell no0b
                    String kost = Gunging_Ootilities_Plugin.commandFailDisclosures.get("VaultLowBalance"); if (kost == null) { kost = "&cYou don't have enough balance to use this"; }
                    asEntity.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(kost.replace("%charge%", String.valueOf(v)), asEntity, (Player) asEntity, interactedBlock, null)));
                    return; } }

            // Apply operation
            GooPVault.SetPlayerBalance((Player) asEntity, op); }

        // Run all commands
        for (String cmd : parsedCommands) {
            if (cmd == null) { continue; }
            //OotilityCeption. Log("\u00a7b - \u00a7f" + cmd);

            // Expect one to be a containers link? (Also non-players cant access physical containers so)
            if (isContainersLinked() && asPlayer) {

                // If it is the sole and rare case in which this structure defines a PHYSICAL container, it must store the location of activation for inherent structure mechanics
                if (cmd.startsWith("goop ")) {

                    // Containers
                    if (cmd.startsWith("goop containers access")) {

                        //   0         1       2           3           4      5 6 7 8    args.Length
                        // /goop containers access <container type> [player] [w x y z]
                        //   -         0       1           2           3      4 5 6 7   args[n]

                        // Eeeeeh it will end up parsing twice which is kinda lame but AAAAAAAA
                        String[] args = OotilityCeption.ParseConsoleCommand(cmd, asEntity, (Player) asEntity, interactedBlock).split(" ");

                        // Parse location?
                        if (args.length == 9) {

                            // Get Location this structure intends to put structure on
                            Location targetLocation = OotilityCeption.ValidLocation((Player) asEntity, args[5], args[6], args[7], args[8], null);

                            // Ret
                            if (targetLocation != null) {

                                // If non-null
                                ArrayList<CSBlock> inherentials = getInherentialsStorage().get(targetLocation);
                                if (inherentials != null) {

                                    // Link to the blocks that have this structure activated
                                    GCL_Physical.registerUnclaimedInherentStructure(targetLocation, inherentials);
                                }

                                // CLear plz
                                getInherentialsStorage().remove(targetLocation);
                            } } } } }

            // Literally just send it
            if (asPlayer) {

                // Send as Player
                OotilityCeption.SendConsoleCommand(OotilityCeption.ProcessGooPRelativityOfCommand(cmd, matchResult.getCoreBlock().getLocation()), (Player) asEntity, interactedBlock);
            } else {

                // Send as normal Entity
                OotilityCeption.SendConsoleCommand(OotilityCeption.ProcessGooPRelativityOfCommand(cmd, matchResult.getCoreBlock().getLocation()), asEntity, interactedBlock); }
        }

        // Break
        if (isBreakOnUse()) {

            // Break every block
            for (Block b : matchResult.getWorldBlocks()) {
                if (b == null) { continue; }

                // Heh
                RefSimulator<ArmorStand> hah = new RefSimulator<>(null);
                if (b.getType() == Material.BARRIER && JSONPlacerUtils.IsJSON_Furniture(b, hah)) { hah.getValue().remove(); }

                // Break it
                b.setType(Material.AIR, true); } }
    }

    @NotNull public static ArrayList<String> parseOrientation(@NotNull ArrayList<String> source, @NotNull Orientations facing) {

        String actForward = "0";
        String actRight = "0";
        String actBack = "0";
        String actLeft = "0";
        switch (facing) {
            case SouthForward:
                actForward = "0";
                actRight = "90";
                actBack = "180";
                actLeft = "270";
                break;
            case WestForward:
                actForward = "90";
                actRight = "180";
                actBack = "270";
                actLeft = "0";
                break;
            case NorthForward:
                actForward = "180";
                actRight = "270";
                actBack = "0";
                actLeft = "90";
                break;
            case EastForward:
                actForward = "270";
                actRight = "0";
                actBack = "90";
                actLeft = "180";
                break;
        }

        ArrayList<String> ret = new ArrayList<>();
        for (String s : source) {
            s = s.replace("%player-forward%", actForward);
            s = s.replace("%player-right%", actRight);
            s = s.replace("%player-back%", actBack);
            s = s.replace("%player-left%", actLeft);
            s = s.replace("%entity-forward%", actForward);
            s = s.replace("%entity-right%", actRight);
            s = s.replace("%entity-back%", actBack);
            s = s.replace("%entity-left%", actLeft);
            ret.add(s);
        }

        return ret;
    }

    @NotNull public ArrayList<String> parseAsCustomStructure(@NotNull ArrayList<String> sours, @NotNull CSMatchResult match) {

        Block blockC = match.getCoreBlock();
        Block blockI = match.getInteractedBlock();

        // Parse according to last orientation
        String yawForward = "0";
        String yawRight = "0";
        String yawBack = "0";
        String yawLeft = "0";
        switch (match.getOrientation()) {
            case SouthForward:
                yawForward = "0";
                yawRight = "90";
                yawBack = "180";
                yawLeft = "270";
                break;
            case WestForward:
                yawForward = "90";
                yawRight = "180";
                yawBack = "270";
                yawLeft = "0";
                break;
            case NorthForward:
                yawForward = "180";
                yawRight = "270";
                yawBack = "0";
                yawLeft = "90";
                break;
            case EastForward:
                yawForward = "270";
                yawRight = "0";
                yawBack = "90";
                yawLeft = "180";
                break;
        }

        ArrayList<String> ret = new ArrayList<>();
        for (String s : sours) {
            s = s.replace("%structure-forward%", yawForward);
            s = s.replace("%structure-right%", yawRight);
            s = s.replace("%structure-back%", yawBack);
            s = s.replace("%structure-left%", yawLeft);

            s = s.replace("%structure-blockcenter-comma%",  (blockC.getX() + 0.5D) + "," + (blockC.getY() + 0.5D) + "," + (blockC.getZ() + 0.5D));
            s = s.replace("%structure-blockcenter%",        (blockC.getX() + 0.5D) + " " + (blockC.getY() + 0.5D) + " " + (blockC.getZ() + 0.5D));
            s = s.replace("%structure-center-comma%",       (blockC.getX()) + "," + (blockC.getY()) + "," + (blockC.getZ()));
            s = s.replace("%structure-center%",             (blockC.getX()) + " " + (blockC.getY()) + " " + (blockC.getZ()));

            s = s.replace("%interacted-blockcenter-comma%", (blockI.getX() + 0.5D) + "," + (blockI.getY() + 0.5D) + "," + (blockI.getZ() + 0.5D));
            s = s.replace("%interacted-blockcenter%",       (blockI.getX() + 0.5D) + " " + (blockI.getY() + 0.5D) + " " + (blockI.getZ() + 0.5D));
            s = s.replace("%interacted-block-comma%",       (blockI.getX()) + "," + (blockI.getY()) + "," + (blockI.getZ()));
            s = s.replace("%interacted-block%",             (blockI.getX()) + " " + (blockI.getY()) + " " + (blockI.getZ()));

            ret.add(s);
        }

        return ret;
    }

    public boolean PressurePlateEntityMatches(Entity vEntity) {
        if (vEntity == null) { return false; }

        CSTrigger tTrigger = null;

        // Is it a monster
        if (OotilityCeption.IsMonster(vEntity.getType())) {

            // As Monster
            tTrigger = CSTrigger.PRESSUREPLATE_MONSTERS;

        // Dropped Item perhaps?
        } else if (vEntity.getType() == EntityType.DROPPED_ITEM) {

            // As Monster
            tTrigger = CSTrigger.PRESSUREPLATE_ITEMS;

        // Otherwise this bad boi is a fucking animal alv
        } else if (vEntity.getType() != EntityType.PLAYER) {

            // As Monster
            tTrigger = CSTrigger.PRESSUREPLATE_ANIMALS;


        // A player, then
        } else if (vEntity instanceof Player) {

            // Get Snealomg
            Player pyr = (Player) vEntity;

            //Sneaking
            if (pyr.isSneaking()) {

                // As Monster
                tTrigger = CSTrigger.SNEAK_PRESSUREPLATE_PLAYERS;

            } else {

                // As Monster
                tTrigger = CSTrigger.PRESSUREPLATE_PLAYERS;
            }
        }

        // Check that it contains it
        if (structureTriggers != null && tTrigger != null) {

            if (structureTriggers.contains(tTrigger)) {

                //DEBUG//oots.C PLog("The TRIGGER is correct. Time to evaluate Params...");

                // Returns true if no specifications are defined
                if (triggerParameters.get(tTrigger) == null) {

                    //DEBUG//oots.C PLog("No Parameters. \u00a7aApproved.");

                    // True Always
                    return true;

                } else if (triggerParameters.get(tTrigger).size() > 0) {

                    // Is entity?
                    boolean asEntity = tTrigger == CSTrigger.PRESSUREPLATE_ANIMALS || tTrigger == CSTrigger.PRESSUREPLATE_MONSTERS;

                    // Check every parameter
                    for (String nbtEntity : triggerParameters.get(tTrigger)) {

                        // Must contian
                        if (nbtEntity.contains(" ")) {

                            // Split into args
                            String[] nbtSplit = nbtEntity.split(" ");

                            // As entity?
                            if (asEntity) {

                                // Pretty sure its valid but
                                if (nbtSplit.length >= 2) {

                                    // Primee
                                    String prime = nbtSplit[0], dime = nbtSplit[1];

                                    //DEBUG//oots.C PLog("Comparing to \u00a7e" + nbtEntity + "\u00a77...");

                                    // True if it matches even one of those
                                    if (OotilityCeption.MatchesEntityNBTtestString(vEntity, prime, dime, null)) {

                                        //DEBUG//oots.C PLog("Matches. \u00a7aApproved.");

                                        // True for once
                                        return true;
                                    }
                                }

                            // As Item
                            } else {

                                //What it is
                                if (nbtSplit.length >= 3) {

                                    //DEBUG//oots.C PLog("Comparing to \u00a7e" + nbtEntity + "\u00a77...");
                                    String amount = null;
                                    if (nbtSplit.length > 3) { amount = nbtSplit[3]; }

                                    // Get ItemStack
                                    ItemStack tItemStack = null;

                                    // Is the entity na item?
                                    if (vEntity instanceof Item) { tItemStack = ((Item)vEntity).getItemStack(); }
                                    if (vEntity instanceof Player) { tItemStack = ((Player) vEntity).getInventory().getItemInMainHand(); }

                                    // True if it matches even one of those
                                    if (OotilityCeption.MatchesItemNBTtestString(tItemStack, nbtSplit[0], nbtSplit[1], nbtSplit[2], amount, null)) {

                                        //DEBUG//oots.C PLog("Matches. \u00a7aApproved.");

                                        // True for once
                                        return true;
                                    }
                                }
                            }
                        }
                    }

                // Not null but size is 0;
                } else {

                    //DEBUG//oots.C PLog("No Parameters. \u00a7aApproved.");

                    // True Always
                    return true;

                }

            // Trigger is not contained
            } else {

                // No
                return false;
            }
        }

        return false;
    }

    public Boolean PlayerHoldingMatches(Player vPlayer, CSTrigger trig) {

        // Check that this shit contains the trigger to begin with, otherwise straight up no
        if (structureTriggers.size() > 0) {

            // If the trigger is in there
            if (structureTriggers.contains(trig)) {

                // CHeck for such animal to be defined.
                boolean ret = false;

                ArrayList<String> trigParams = triggerParameters.get(trig);
                if (trigParams != null && trigParams.size() > 0) {

                    // Check every parameter
                    for (String nbtItem : trigParams) {

                        // Split into args
                        String[] nbtSplit = nbtItem.split(" ");

                        //DEBUG//oots.C PLog("Comparing to \u00a7e" + nbtItem + "\u00a77...");
                        String amount = null;
                        if (nbtSplit.length > 3) { amount = nbtSplit[3]; }

                        // True if it matches even one of those
                        if (OotilityCeption.MatchesItemNBTtestString(vPlayer.getInventory().getItemInMainHand(), nbtSplit[0], nbtSplit[1], nbtSplit[2], amount, null)) {

                            //DEBUG//oots.C PLog("Matches. \u00a7aApproved.");

                            // True for once
                            ret = true;
                        }
                    }
                } else {

                    return true;
                }

                // Return
                return ret;

            } else {

                // Cant match if there is nothing to match to begin with!
                return false;
            }
        }

        return false;
    }
}
