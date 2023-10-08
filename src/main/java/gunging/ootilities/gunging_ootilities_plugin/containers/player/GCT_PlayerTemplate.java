package gunging.ootilities.gunging_ootilities_plugin.containers.player;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerSlotTypes;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * It is guaranteed to hold a grid of slots of size 36, but has other separate
 * sections that hold information for armor, offhand, and crafting grid slots.
 */
public class GCT_PlayerTemplate extends GOOPCTemplate {

    /**
     * The general operation {@link GOOPCTemplate#buildValidSlotsContent(ArrayList, int)} deletes
     * slots outside of the range of four rows. Yeah.
     *
     * @param unrefinedSlots Slots prior to refining
     */
    public void loadUniqueSlots(@NotNull ArrayList<GOOPCSlot> unrefinedSlots) {

        // Edit with slots
        for (GOOPCSlot slot : unrefinedSlots) {

            // If Slot non-null
            if (slot != null) {

                // Which slot is it?
                switch (slot.getSlotNumber()) {
                    case -106: slot_offhand = slot; break;
                    case 103: slot_helmet = slot; break;
                    case 102: slot_chestplate = slot; break;
                    case 101: slot_leggings = slot; break;
                    case 100: slot_boots = slot; break;
                    case 84: slot_craft_result = slot; break;
                    case 80: slot_craft_UL = slot; break;
                    case 81: slot_craft_UR = slot; break;
                    case 82: slot_craft_BL = slot; break;
                    case 83: slot_craft_BR = slot; break;
                    default: continue;
                }

                // Yes
                //SRZ// OotilityCeption. Log("\u00a78GCT\u00a7b PCZ\u00a77 Put at \u00a7e#" + slot.getSlotNumber() + "\u00a77 slot\u00a7f " + GOOPCSlot.serialize(slot));
            }
        }
    }

    @Override
    public @Nullable GOOPCSlot getSlotAt(@Nullable Integer i) {
        if (i == null) { return null; }

        // Which slot is it?
        switch (i) {
            case -106: return slot_offhand;
            case 103: return slot_helmet;
            case 102: return slot_chestplate;
            case 101: return slot_leggings;
            case 100: return slot_boots;
            case 84: return slot_craft_result;
            case 80: return slot_craft_UL;
            case 81: return slot_craft_UR;
            case 82: return slot_craft_BL;
            case 83: return slot_craft_BR;
            default: return super.getSlotAt(i);
        }

    }

    /**
     * Will rebuild contents with {@link #buildValidSlotsContent(ArrayList, int)} if the size is not equal to 36.
     */
    public GCT_PlayerTemplate(@NotNull String internalName, @NotNull HashMap<Integer, GOOPCSlot> slotContents, @NotNull ContainerTypes storageType, @NotNull String displayTitle, boolean edgeFormations) throws IllegalArgumentException {
        super(internalName, slotContents, storageType, displayTitle, edgeFormations);

        // Fix size.
        if (getTotalSlotCount() != 36) {

            // Slots
            ArrayList<GOOPCSlot> unrefinedContents = new ArrayList<>(slotContents.values());

            // Rebuild slots content
            loadSlotsContent(buildValidSlotsContent(unrefinedContents, 4));
            loadUniqueSlots(unrefinedContents);
        }
    }

    @Override public int getHeight() { return 4; }

    /**
     * Will rebuild {@link #buildValidSlotsContent(ArrayList, int)} if the size is not equal to 36.
     */
    @Override public void loadSlotsContent(@NotNull HashMap<Integer, GOOPCSlot> contents) {
        super.loadSlotsContent(contents);
        //SRZ//OotilityCeption.Log("\u00a78CTPTemplate\u00a7a LD\u00a77 Loaded contents\u00a72 x" + contents.size());

        // Fix size.
        if (getTotalSlotCount() != 36) {
            //SRZ//OotilityCeption. Log("\u00a78CTPTemplate\u00a7a LD\u00a77 Incorrect size, rebuilding. ");

            // Slots
            ArrayList<GOOPCSlot> unrefinedContents = new ArrayList<>(contents.values());

            // Rebuild slots content
            super.loadSlotsContent(buildValidSlotsContent(unrefinedContents, 4));
            loadUniqueSlots(unrefinedContents);
        }
    }
    @Override public @NotNull HashMap<Integer, GOOPCSlot> getSlotsContent() {
        HashMap<Integer, GOOPCSlot> ret = super.getSlotsContent();

        // Add equipment slots
        ret.put(slot_offhand.getSlotNumber(), slot_offhand);
        ret.put(slot_helmet.getSlotNumber(), slot_helmet);
        ret.put(slot_chestplate.getSlotNumber(), slot_chestplate);
        ret.put(slot_leggings.getSlotNumber(), slot_leggings);
        ret.put(slot_boots.getSlotNumber(), slot_boots);

        // Add crafting slots
        ret.put(slot_craft_result.getSlotNumber(), slot_craft_result);
        ret.put(slot_craft_UL.getSlotNumber(), slot_craft_UL);
        ret.put(slot_craft_UR.getSlotNumber(), slot_craft_UR);
        ret.put(slot_craft_BL.getSlotNumber(), slot_craft_BL);
        ret.put(slot_craft_BR.getSlotNumber(), slot_craft_BR);

        return ret;
    }

    // Equipment Slots
    @NotNull GOOPCSlot slot_offhand = new GOOPCSlot(-106, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_helmet = new GOOPCSlot(103, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_chestplate = new GOOPCSlot(102, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_leggings = new GOOPCSlot(101, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_boots = new GOOPCSlot(100, ContainerSlotTypes.STORAGE, null);

    // Crafting slots
    @NotNull GOOPCSlot slot_craft_result = new GOOPCSlot(84, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_craft_UL = new GOOPCSlot(80, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_craft_UR = new GOOPCSlot(81, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_craft_BL = new GOOPCSlot(82, ContainerSlotTypes.STORAGE, null);
    @NotNull GOOPCSlot slot_craft_BR = new GOOPCSlot(83, ContainerSlotTypes.STORAGE, null);
}
