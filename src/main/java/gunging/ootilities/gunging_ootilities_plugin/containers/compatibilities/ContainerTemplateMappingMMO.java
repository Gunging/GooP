package gunging.ootilities.gunging_ootilities_plugin.containers.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCStation;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.misc.IntVector2;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.CustomInventoryCheck;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContainerTemplateMappingMMO extends VanillaInventoryMapping implements CustomInventoryCheck, Listener {

    //region Constructor
    /**
     * @return The template by which this mapping may find items.
     */
    @NotNull public GOOPCTemplate getTemplate() { return template; }
    @NotNull GOOPCTemplate template;
    /**
     * @param template The template by which this mapping may find items.
     */
    public void setTemplate(@NotNull GOOPCTemplate template) {
        this.template = template;
        refreshTemplateVars();
    }
    /**
     * @param template The template by which this mapping may find items.
     */
    public ContainerTemplateMappingMMO(@NotNull GOOPCTemplate template) throws IllegalArgumentException {
        super();
        this.template = template;
        //noinspection ConstantConditions
        this.resultSlot = getTemplate().getResultSlot();
        refreshTemplateVars();
    }
    //endregion

    //region Template Reading
    /**
     * Finds the extremes of the crafting grid.
     */
    public void refreshTemplateVars() throws IllegalArgumentException {

        // Illegal
        if (getTemplate().getResultSlot() == null) { throw new IllegalArgumentException("Template " + getTemplate().getInternalName() + " has no result slot, it cannot be a crafting station. "); }
        resultSlot = getTemplate().getResultSlot();

        // Well does it have at least one storage slot??
        int localLeft = 100, localRight = -100, localBottom = 100, localTop = -100, storageCount = 0;

        // Go through every slot
        for (Map.Entry<Integer, GOOPCSlot> slotEntry : getTemplate().getSlotsContent().entrySet()) {
            GOOPCSlot containerSlot = slotEntry.getValue();
            Integer slotNumber = slotEntry.getKey();

            // No null
            if (containerSlot == null || slotNumber == null) { continue; }

            // Only storage slots count
            if (!containerSlot.isForStorage()) { continue; }
            storageCount++;

            // All right get absolutes
            IntVector2 abs = getAbsoluteWH(slotNumber);

            // Is it more left than the left?
            if (abs.getX() < localLeft) { localLeft = abs.getX(); }

            // Is it below the most below?
            if (abs.getY() < localBottom) { localBottom = abs.getY(); }

            // Is it right of the most right?
            if (abs.getX() > localRight) { localRight = abs.getX(); }

            // Is it above the most above
            if (abs.getY() > localTop) { localTop = abs.getY(); }
        }
        if (storageCount <= 0) { throw new IllegalArgumentException("Template " + getTemplate().getInternalName() + " has no storage slots, it cannot be a crafting station. "); }

        // Accept
        leftmostWidth = localLeft;
        rightmostWidth = localRight;
        bottommostHeight = localBottom;
        topmostHeight = localTop;

        // Count size
        enclosedWidth = 1 + getRightmostWidth() - getLeftmostWidth();
        enclosedHeight = 1 + getTopmostHeight() - getBottommostHeight();
        enclosedSize = storageCount;

        // Logging
        //CTM//OotilityCeption.Log("\u00a78CTM \u00a76RD\u00a77 Top Left:\u00a7e " + leftmostWidth + ", " + topmostHeight);
        //CTM//OotilityCeption.Log("\u00a78CTM \u00a76RD\u00a77 Bot Right:\u00a7e " + rightmostWidth + ", " + bottommostHeight);
        //CTM//OotilityCeption.Log("\u00a78CTM \u00a76RD\u00a77 Size:\u00a7e " + enclosedWidth + "x" + enclosedHeight + "=" + enclosedSize);
    }

    @NotNull GOOPCSlot getResultSlot() { return resultSlot; }
    @NotNull GOOPCSlot resultSlot;

    /**
     * @return W position of the leftmost storage slot (that can be used for crafting).
     *         <br>
     *         <b>Closest to zero</b>, tends positively.
     */
    public int getLeftmostWidth() { return leftmostWidth; }
    int leftmostWidth;

    /**
     * @return W position of the rightmost storage slot (that can be used for crafting)
     *         <br>
     *         <b>Furthest from zero</b> in the positive direction
     */
    public int getRightmostWidth() { return rightmostWidth; }
    int rightmostWidth;

    /**
     * @return H position of the topmost storage slot (that can be used for crafting).
     *         <br>
     *         <b>Closest to zero</b>, tends negatively.
     */
    public int getTopmostHeight() { return topmostHeight; }
    int topmostHeight;

    /**
     * @return H position of the bottommost storage slot (that can be used for crafting)
     *         <br>
     *         <b>Furthest from zero</b> in the negative direction
     */
    public int getBottommostHeight() { return bottommostHeight; }
    int bottommostHeight;

    /**
     * @return Size of the rectangle formed between the topmost left slot
     *         and the rightmost bottom slot.
     */
    public int getEnclosedSize() { return enclosedSize; }
    int enclosedSize;

    /**
     * @return The number of rows (inclusive) between top and bottom slot.
     */
    public int getEnclosedHeight() { return enclosedHeight; }
    int enclosedHeight;

    /**
     * @return The number of columns (inclusive) between left and right slot.
     */
    public int getEnclosedWidth() { return enclosedWidth; }
    int enclosedWidth;

    /**
     * @param absoluteSlot Continuous number from 0 to the max number of slots (multiple of 9).
     *
     * @return The width and height of this slot:
     *         <br>
     *         <b>Width is always positive or 0</b><br>
     *         <b>Height is always negative or 0</b>
     */
    @NotNull IntVector2 getAbsoluteWH(int absoluteSlot) {

        // Mod 9? The width of the slot
        int x = absoluteSlot % 9;

        // Remove the excess, divide by nine, that's the row.
        int y = OotilityCeption.RoundToInt((absoluteSlot - x) / 9.00D);

        // Row goes down ~ into negative regime.
        return new IntVector2(x, -y);
    }

    /**
     * @param relativeW Relative width from the top left
     * @param relativeH Relative height from the top left
     *
     * @return The absolute width and height of this slot:
     *         <br>
     *         <b>Width is always positive or 0</b><br>
     *         <b>Height is always negative or 0</b>
     */
    @NotNull IntVector2 getAbsoluteWH(int relativeW, int relativeH) {

        // Row goes down ~ into negative regime.
        return new IntVector2(relativeW + getLeftmostWidth(), relativeH + getTopmostHeight());
    }

    /**
     * @param absoluteSlot Continuous number from 0 to the max number of slots (multiple of 9).
     *
     * @return The width and height of this slot, but relative to the topmost and leftmost slots:
     *         <br>
     *         <b>Width is always positive or 0</b><br>
     *         <b>Height is always negative or 0</b>
     */
    @NotNull IntVector2 getRelativeWH(int absoluteSlot) {

        // Yeah
        IntVector2 abs = getAbsoluteWH(absoluteSlot);

        // Relative X is how far away from the leftmost
        int x = abs.getX() - getLeftmostWidth();

        // Relative Y is how below the topmost
        int y = abs.getY() - getTopmostHeight();

        // Row goes down ~ into negative regime.
        return new IntVector2(x, y);
    }

    /**
     * @param relativeW Width away from leftmost crafting
     * @param relativeH Height away from topmost crafting
     *
     * @return The slot number of this container that corresponds to this
     *         W and H relative to the top left crafting slot.
     */
    int getAbsoluteSlotFromRelativeWH(int relativeW, int relativeH) {

        // Transform to absolute
        IntVector2 abs = getAbsoluteWH(relativeW, relativeH);

        // Convert to absolutes and return
        return getAbsoluteSlotFromAbsoluteWH(abs.getX(), abs.getY());
    }


    /**
     * @param absoluteW Width away from 0
     * @param absoluteH Height away from 0
     *
     * @return The slot number of this container that corresponds to this
     *         W and H absolute to the inventory GUI
     */
    int getAbsoluteSlotFromAbsoluteWH(int absoluteW, int absoluteH) {

        // Every negative row adds nine, and the width adds one.
        return (-absoluteH * 9) + absoluteW;
    }

    /**
     * Shorthand to throw the exception that says "HEY! Theres no such slot"
     *
     * @param out Slot number that caused the exception
     *
     * @throws IllegalArgumentException Always
     */
    void throwExceedBounds(int out) throws IllegalArgumentException { throw new IllegalArgumentException("Template " + getTemplate().getInternalName() + " has no data for slot '" + out + "'"); }
    /**
     * Shorthand to throw the exception that says "HEY! Theres no such slot"
     *
     * @param w Horizontal number that caused the exception
     * @param h Vertical number that caused the exception
     *
     * @throws IllegalArgumentException Always
     */
    void throwExceedBounds(int w, int h) throws IllegalArgumentException { throw new IllegalArgumentException("Template " + getTemplate().getInternalName() + " has no data for slot at width '" + w + "' and height '" + h + "'"); }
    //endregion

    //region Main Inventory
    @Override
    public int getMainWidth(int slot) throws IllegalArgumentException {

        // Find the template slot
        GOOPCSlot containerSlot = getTemplate().getSlotAt(slot);

        // If it does not exist, its out of bounds
        if (containerSlot == null) { throwExceedBounds(slot); return -1; }

        /*
         * So this method returns the 'Main Width' which is the horizontal
         * distance between this slot and the leftmost crafting slot.
         *
         * Negative one if not for storage
         */
        if (!containerSlot.isForStorage()) { return -1; }

        // Return its width minus the leftmost width
        return getRelativeWH(containerSlot.getSlotNumber()).getX();
    }
    @Override
    public int getMainHeight(int slot) throws IllegalArgumentException {

        // Find the template slot
        GOOPCSlot containerSlot = getTemplate().getSlotAt(slot);

        // If it does not exist, its out of bounds
        if (containerSlot == null) { throwExceedBounds(slot); return 1; }

        /*
         * So this method returns the 'Main Width' which is the horizontal
         * distance between this slot and the leftmost crafting slot.
         *
         * Negative one if not for storage
         */
        if (!containerSlot.isForStorage()) { return 1; }

        // Return its width minus the leftmost width
        return getRelativeWH(containerSlot.getSlotNumber()).getY();
    }
    @Override
    public int getMainSlot(int width, int height) throws IllegalArgumentException {

        // Transform into slot
        int slot = getAbsoluteSlotFromRelativeWH(width, height);

        // Find the template slot
        GOOPCSlot containerSlot = getTemplate().getSlotAt(slot);

        // If it does not exist, its out of bounds
        if (containerSlot == null) { throwExceedBounds(width, height); return -1; }

        // Well that slot is the one I guess
        return slot;
    }
    @Override
    public int getMainInventoryStart() { return getAbsoluteSlotFromAbsoluteWH(getLeftmostWidth(), getTopmostHeight()); }
    @Override
    public int getMainInventorySize() { return getEnclosedSize(); }
    @Override
    public int getMainInventoryWidth() { return getEnclosedWidth(); }
    @Override
    public int getMainInventoryHeight() { return getEnclosedHeight(); }
    /**
     * Returns a mythic recipe inventory from the provided inventory.
     * <p></p>
     * The one defined as the <b>main</b> inventory.
     *
     * @param inventory Inventory to extract ItemStacks from
     *
     * @return A built Mythic Recipe Inventory
     */
    @Override @NotNull public MythicRecipeInventory getMainMythicInventory(@NotNull Inventory inventory) {

        // Output
        //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a77 Reading Main Mythic of\u00a7e " + getTemplate().getInternalName());

        // Must find container!
        InventoryView view = getViewFromLastTargetInvenSuccess(inventory);
        if (view == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a7c Unregistered View");
            return super.getMainMythicInventory(inventory); }

        // All right find deployed
        GOOPCStation deployed = (GOOPCStation) GOOPCManager.getContainer(view);
        if (deployed == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a7c View does not encode for deployed container");
            return super.getMainMythicInventory(inventory); }

        // Find observed
        ContainerInventory inven = deployed.getObservedBy(inventory.getViewers().get(0).getUniqueId());
        if (inven == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a7c Deployed container has no observer");
            return super.getMainMythicInventory(inventory); }

        // Create
        MythicRecipeInventory main = new MythicRecipeInventory();

        // For each Main size
        for (int h = 0; h < getMainInventoryHeight(); h++) {

            // Make row
            ItemStack[] row = new ItemStack[getMainInventoryWidth()];

            for (int w = 0; w < getMainInventoryWidth(); w++) {

                // Find
                ItemStack found = inven.getNonDefaultItem(getMainSlot(w, -h));

                // Non-null
                if (found == null) { found = new ItemStack(Material.AIR); }
                //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a77 At\u00a7c " + w + ", " + h + "\u00a77 found " + OotilityCeption.GetItemName(found, true) + " \u00a78~ " + found.getType().toString());

                // Fill row
                row[w] = found;
            }

            // Add row
            main.addRow(row);
        }

        // Output
        //RDR//for (String str : main.toStrings("\u00a78RDR \u00a74GOT\u00a77 Main ")) { OotilityCeption.Log(str); }

        // That's it
        return main;
    }
    //endregion

    //region Result Inventory
    @Override
    public int getResultWidth(int slot) throws IllegalArgumentException {

        // Is it the same as the result slot? accept
        if (slot == getResultSlot().getSlotNumber()) { return 0; }

        // Out of bounds
        throwExceedBounds(slot); return 0;
    }
    @Override
    public int getResultHeight(int slot) throws IllegalArgumentException {

        // Is it the same as the result slot? accept
        if (slot == getResultSlot().getSlotNumber()) { return 0; }

        // Out of bounds
        throwExceedBounds(slot); return 0;
    }
    @Override
    public int getResultSlot(int width, int height) throws IllegalArgumentException {
        if (width == 0 && height == 0) { return getResultSlot().getSlotNumber(); }

        throwExceedBounds(width, height); return 0;
    }
    @Override
    public int getResultInventoryStart() { return getResultSlot().getSlotNumber(); }
    @Override
    public int getResultInventorySize() { return 1; }
    @Override
    public int getResultInventoryWidth() { return 1; }
    @Override
    public int getResultInventoryHeight() { return 1; }
    @Override public boolean isResultSlot(int slot) { return slot == getResultSlot().getSlotNumber(); }
    /**
     * Returns a mythic recipe inventory from the provided inventory.
     * <p></p>
     * The one defined as the <b>result</b> inventory.
     *
     * @param inventory Inventory to extract ItemStacks from
     *
     * @return A built Mythic Recipe Inventory
     */
    @Override @NotNull public MythicRecipeInventory getResultMythicInventory(@NotNull Inventory inventory) {
        //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a77 Reading Result Mythic of\u00a7e " + getTemplate().getInternalName());

        // Must find container!
        InventoryView view = getViewFromLastTargetInvenSuccess(inventory);
        if (view == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a7c Unregistered View");
            return super.getResultMythicInventory(inventory); }

        // All right find deployed
        GOOPCStation deployed = (GOOPCStation) GOOPCManager.getContainer(view);
        if (deployed == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a7c View does not encode for deployed container");
            return super.getResultMythicInventory(inventory); }

        // Find observed
        ContainerInventory inven = deployed.getObservedBy(inventory.getViewers().get(0).getUniqueId());
        if (inven == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a7c Deployed container has no observer");
            return super.getResultMythicInventory(inventory); }

        // Create
        MythicRecipeInventory result = new MythicRecipeInventory();

        // For each result size
        for (int h = 0; h < getResultInventoryHeight(); h++) {

            // Make row
            ItemStack[] row = new ItemStack[getResultInventoryWidth()];

            for (int w = 0; w < getResultInventoryWidth(); w++) {

                // Result slot guaranteed to be of type RESULT such that edited layer is checked
                ItemStack found = inven.getNonDefaultItem(getResultSlot(w, -h));

                // Non-null
                if (found == null) { found = new ItemStack(Material.AIR); }
                //RDR//OotilityCeption.Log("\u00a78RDR \u00a74GOT\u00a77 At\u00a7c " + w + ", " + h + "\u00a77 found " + OotilityCeption.GetItemName(found, true) + " \u00a78~ " + found.getType().toString());

                // Fill row
                row[w] = found;
            }

            // Add row
            result.addRow(row);
        }

        // Output
        //RDR//for (String str : result.toStrings("\u00a78RDR \u00a74GOT\u00a77 Result ")) { OotilityCeption.Log(str); }

        // That's it
        return result;
    }
    //endregion

    //region Side Inventories, not supported apparently
    @Override
    public int getSideWidth(@NotNull String side, int slot) throws IllegalArgumentException { throwSideInventoryException(side); return 0; }
    @Override
    public int getSideHeight(@NotNull String side, int slot) throws IllegalArgumentException { throwSideInventoryException(side); return 0; }
    @Override
    public int getSideSlot(@NotNull String side, int width, int height) throws IllegalArgumentException { throwSideInventoryException(side); return 0; }
    @Override
    public int getSideInventoryStart(@NotNull String side) throws IllegalArgumentException { throwSideInventoryException(side); return 0; }
    @Override
    public int getSideInventorySize(@NotNull String side) throws IllegalArgumentException { throwSideInventoryException(side); return 0; }
    @Override
    public int getSideInventoryWidth(@NotNull String side) throws IllegalArgumentException { throwSideInventoryException(side); return 0; }
    @Override
    public int getSideInventoryHeight(@NotNull String side) throws IllegalArgumentException { throwSideInventoryException(side); return 0; }

    @Override
    public boolean applyToSideInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalSide, @NotNull String sideKeyName, boolean amountOnly) { return false; }
    //endregion

    //region Override workings of inventories

    /**
     * Makes use of all the get inventory methods to
     * build a MythicBlueprintInventory.
     * <p></p>
     * <b>Make sure the inventory is correct</b> to assimilate
     * with this inventory mapping, as it is untested what would
     * happen if you try to read, say a furnace inventory, with
     * the Crafting Table Mapping.
     *
     * @param inven The inventory you want to transcribe the contents of
     *
     * @return A built MythicBlueprintInventory.
     *
     * @see #getMainMythicInventory(Inventory)
     * @see #getResultMythicInventory(Inventory)
     * @see #getSideMythicInventories(Inventory)
     */
    @Override @NotNull public MythicBlueprintInventory extractFrom(@NotNull Inventory inven) {

        //RDR//OotilityCeption.Log("\u00a78RDR \u00a76CI\u00a77 Getting Main Inventory...\u00a78 " + getClass().getName());
        MythicRecipeInventory main = getMainMythicInventory(inven);
        //RDR//OotilityCeption.Log("\u00a78Extract \u00a76CR\u00a77 Getting Result Inventory...");
        MythicRecipeInventory result = getResultMythicInventory(inven);
        //RDR//OotilityCeption.Log("\u00a78Extract \u00a76CS\u00a77 Getting Side Inventories...");
        HashMap<String, MythicRecipeInventory> sideInventories = getSideMythicInventories(inven);

        // Create blueprint alv
        MythicBlueprintInventory inventory = new MythicBlueprintInventory(main, result);
        for (String side : sideInventories.keySet()) { inventory.addSideInventory(side, sideInventories.get(side)); }

        // That's it
        return inventory;
    }
    /**
     * Applies the contents of a Mythic Recipe Inventory to this Inventory.
     * <p></p>
     * Targets the main inventory.
     *
     * @param inventory Inventory to dump the items to.
     *
     * @param finalMain Mythic Inventory to get Side from
     *
     * @param amountOnly Sometimes one knows for sure that the new and old inventories differ
     *                     only in the amount of items, in which case, it is more optimized to
     *                     decrease the amounts rather than replacing the items.
     */
    @Override public void applyToMainInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalMain, boolean amountOnly) {

        //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a77 Applying Main Mythic of\u00a7e " + getTemplate().getInternalName());

        // Must find container!
        InventoryView view = getViewFromLastTargetInvenSuccess(inventory);
        if (view == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a7c Unregistered View");
            super.applyToMainInventory(inventory, finalMain, amountOnly); return; }

        // All right find deployed
        GOOPCStation deployed = (GOOPCStation) GOOPCManager.getContainer(view);
        if (deployed == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a7c View does not encode for deployed container");
            super.applyToMainInventory(inventory, finalMain, amountOnly); return; }

        // Find observed
        ContainerInventory inven = deployed.getObservedBy(inventory.getViewers().get(0).getUniqueId());
        if (inven == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a7c Deployed container has no observer");
            super.applyToMainInventory(inventory, finalMain, amountOnly); return; }

        // Skips
        int z = 0;

        // For every main slot
        for (int s = getMainInventoryStart(); s < (getMainInventorySize() + getMainInventoryStart() + z); s++) {

            // Read
            int w = getMainWidth(s);
            int h = getMainHeight(s);

            // Any of them extraneous?
            if (w < 0 || h > 0) { z++; continue; }

            // ItemStack to set
            ItemStack toSet = finalMain.getItemAt(w, h);

            // Okay so... set it?
            if (amountOnly) {

                // Only edit amount
                ItemStack current = inven.getItem(s);

                // Set amount
                if (current != null) { current = OotilityCeption.asQuantity(current, toSet != null ? toSet.getAmount() : 0); }

                // Set item
                toSet = current;

            }

            // Set that damned item
            if (inven.getTemplate().isStorageSlot(s)) {

                //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a77 At\u00a7c " + w + ", " + h + "\u00a77 put in storage " + OotilityCeption.GetItemName(toSet, true) + " \u00a78~ " + (toSet == null ? "null" : toSet.getType().toString()));


                // Set in storage
                inven.setStorageItem(s, toSet);

            // Not storage slot
            } else {

                //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a77 At\u00a7c " + w + ", " + h + "\u00a77 put in edited " + OotilityCeption.GetItemName(toSet, true) + " \u00a78~ " + (toSet == null ? "null" : toSet.getType().toString()));

                // Only display
                inven.setEditedItem(s, toSet);
            }
            
            // Update
            inven.updateSlot(s);
        }
    }
    /**
     * Applies the contents of a Mythic Recipe Inventory to this Inventory.
     * <p></p>
     * Targets the result inventory.
     *
     * @param inventory Inventory to dump the items to.
     *
     * @param finalResult Mythic Inventory to get result from
     *
     * @param amountOnly Sometimes one knows for sure that the new and old inventories differ
     *                     only in the amount of items, in which case, it is more optimized to
     *                     decrease the amounts rather than replacing the items.
     */
    @Override public void applyToResultInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalResult, boolean amountOnly) {

        //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a77 Applying Result Mythic of\u00a7e " + getTemplate().getInternalName());

        // Must find container!
        InventoryView view = getViewFromLastTargetInvenSuccess(inventory);
        if (view == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a7c Unregistered View");
            super.applyToResultInventory(inventory, finalResult, amountOnly); return; }

        // All right find deployed
        GOOPCStation deployed = (GOOPCStation) GOOPCManager.getContainer(view);
        if (deployed == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a7c View does not encode for deployed container");
            super.applyToResultInventory(inventory, finalResult, amountOnly); return; }

        // Find observed
        ContainerInventory inven = deployed.getObservedBy(inventory.getViewers().get(0).getUniqueId());
        if (inven == null) {
            //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a7c Deployed container has no observer");
            super.applyToResultInventory(inventory, finalResult, amountOnly); return; }

        // Skips
        int z = 0;

        // For every result slot
        for (int s = getResultInventoryStart(); s < (getResultInventorySize() + getResultInventoryStart() + z); s++) {

            // Read
            int w = getResultWidth(s);
            int h = getResultHeight(s);

            // Any of them extraneous?
            if (w < 0 || h > 0) { z++; continue; }

            // ItemStack to set
            ItemStack toSet = finalResult.getItemAt(w, h);

            // Okay so... set it?
            if (amountOnly) {

                // Only edit amount
                ItemStack current = inven.getItem(s);

                // Set amount
                if (current != null) { current = OotilityCeption.asQuantity(current, toSet != null ? toSet.getAmount() : 0); }

                // Set item
                toSet = current;

            }

            // Set that damned item
            if (inven.getTemplate().isStorageSlot(s)) {

                //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a77 At\u00a7c " + w + ", " + h + "\u00a77 put in storage " + OotilityCeption.GetItemName(toSet, true) + " \u00a78~ " + (toSet == null ? "null" : toSet.getType().toString()));

                // Set in storage
                inven.setStorageItem(s, toSet);

            // Not storage slot
            } else {

                //RDR//OotilityCeption.Log("\u00a78RDR \u00a79APP\u00a77 At\u00a7c " + w + ", " + h + "\u00a77 put in edited " + OotilityCeption.GetItemName(toSet, true) + " \u00a78~ " + (toSet == null ? "null" : toSet.getType().toString()));

                // Only display
                inven.setEditedItem(s, toSet);
            }
            
            // Update
            inven.updateSlot(s);
        }
    }
    //endregion

    //region Identify Inventory
    @Override public boolean mainIsResult() { return false; }
    @Nullable
    @Override public MythicRecipeStation getIntendedStation() { return getTemplate().getCustomMythicLibRecipe() == null ? MythicRecipeStation.WORKBENCH : MythicRecipeStation.CUSTOM; }
    @NotNull @Override public InventoryType getIntendedInventory() { return InventoryType.CHEST; }
    @NotNull @Override public ArrayList<String> getSideInventoryNames() { return sNames; }
    @NotNull final static ArrayList<String> sNames = new ArrayList<>();
    @Override public boolean IsTargetInventory(@NotNull InventoryView view) {

        // No more matching
        if (isDiscontinued()) { return false; }

        // Please must be in USAGE mode
        if (GOOPCManager.isUsage_Preview(view) || GOOPCManager.isUsage_EditionCommands(view)
                || GOOPCManager.isUsage_EditionDisplay(view) || GOOPCManager.isUsage_EditionStorage(view)) {

            // None of this
            return false;
        }

        // Must be of the same container
        GOOPCTemplate temp = GOOPCManager.getContainerTemplate(view);

        // Null means no
        if (temp == null) { return false; }

        // If the IDs match its the same-yo
        if (temp.getInternalID() != getTemplate().getInternalID()) { return false; }

        // Include
        lastTargetInventorySuccess.put(view.getTopInventory().getViewers().get(0).getUniqueId(), view);
        return true;
    }
    @NotNull HashMap<UUID, InventoryView> lastTargetInventorySuccess = new HashMap<>();
    @Nullable InventoryView getViewFromLastTargetInvenSuccess(@NotNull Inventory inventory) {
        if (inventory.getViewers().size() == 0) { return null; }
        return lastTargetInventorySuccess.get(inventory.getViewers().get(0).getUniqueId()); }
    @NotNull @Override public String getCustomStationKey() { return getTemplate().getCustomMythicLibRecipe() == null ? getTemplate().getInternalName() : getTemplate().getCustomMythicLibRecipe(); }
    //endregion

    //region Reloading
    /**
     * @return If GooP has been reloaded, this inventory mapping may not be valid anymore and
     *         it has become discontinued, such that it wont be active in the system anymore.
     */
    public boolean isDiscontinued() { return discontinued; }
    boolean discontinued;
    /**
     * Mark this mapping as discontinued, presumably because GooP was reloaded and the template
     * layout edited.
     */
    public void discontinue() { discontinued = true; }
    //endregion
}
