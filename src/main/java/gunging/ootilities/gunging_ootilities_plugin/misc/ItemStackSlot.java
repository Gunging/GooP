package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.ContainerTemplateGooP;
import gunging.ootilities.gunging_ootilities_plugin.containers.PersonalContainerGooP;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class ItemStackSlot {

    // Slot
    Integer slot;

    // Upper Range
    Integer upperRange;

    // Is Any?
    boolean isAny;

    // Target Personal
    @Nullable PersonalContainerGooP pSource = null;

    // Parent Slot
    @Nullable ItemStackSlot parentSlot;

    // Quickly where it is
    @NotNull SearchLocation locationName;

    // Placeholder for containers, maybe?
    @Nullable String goopContainersPlaceholder;

    @Override public String toString() {
        StringBuilder s = new StringBuilder();
        switch (locationName) {
            case INVENTORY: break;
            case ENDERCHEST: s.append("e"); break;
            case OBSERVED_CONTAINER: s.append("c"); break;
            case PERSONAL_CONTAINER: s.append("|").append(pSource.getParentTemplate().getInternalName()).append("|"); break;
            case SHULKER_PERSONAL_CONTAINER:;
            case SHULKER_OBSERVED_CONTAINER:
            case SHULKER_INVENTORY:
            case SHULKER_ENDERCHEST:
                s.append(parentSlot.toString()).append(".");
                break;
        }

        if (isAny()){ s.append("*"); } else {

            if (!upperRange.equals(slot)) {
                s.append(slot).append("-").append(upperRange);
            } else {
                s.append(slot);
            }
        }

        return s.toString();
    }

    /**
     * Creates a new Item Stack Slot Location thing. Well, it has much information.
     * @param targetLocation The 'general' kind of inventory the slot is in.
     * @param targetSlot The actual index of the slot. May be NULL for 'any'
     * @param parent If within a shulker box, the slot of thay shulker box. May be NULL
     */
    public ItemStackSlot(@Nullable SearchLocation targetLocation, @Nullable Integer targetSlot, @Nullable ItemStackSlot parent) {

        // Set
        slot = targetSlot;
        upperRange = targetSlot;
        parentSlot = parent;
        locationName = targetLocation;
        isAny = (targetSlot == null);
    }

    public Integer getSlot() { return slot; }
    public Integer GetSlot() { return slot; }
    @Nullable public ItemStackSlot getParentSlot() { return parentSlot; }
    @Nullable public ItemStackSlot GetParentSlot() { return parentSlot; }
    public Integer getUpperRange() { return upperRange; }
    public Integer GetUpperRange() { return upperRange; }
    public boolean WithinRange(int querySlot) { return (querySlot >= slot) && (querySlot <= upperRange); }
    public boolean isAny() { return isAny; }
    public boolean IsAny() { return isAny; }
    public void SetUpperRange(int total) {

        // If naturally larger, thats fine
        if (total > slot) {
            upperRange = total;

        // Otherwise, fit
        } else {
            upperRange = slot;
            slot = total;
        }
    }
    public void SetPersonalContainer(@Nullable PersonalContainerGooP cName) {

        // If it exists
        pSource = cName;
    }
    public void SetGooPContainersPlaceholder(@Nullable String namespace) { goopContainersPlaceholder = namespace; }
    @Nullable public PersonalContainerGooP GetPersonalContainer() {

        // If it exists
        return pSource;
    }

    /**
     * Retrieves all the ItemStackSlots that this RANGE or ANY type ItemStackSlot represents
     * @return All the item stack slots this slot represents. Will return itself if it is not of types RANGE nor ANY.
     */
    @NotNull public ArrayList<ItemStackSlot> Elaborate(@Nullable Player forObservedContainer) {
        // Ret
        ArrayList<ItemStackSlot> ret = new ArrayList<>();
        //SLOT//OotilityCeption.Log("\u00a78SLOT \u00a74ELB \u00a77Parsing Slot \u00a73" + toString());

        // If it is a placeholder slot
        if (goopContainersPlaceholder != null) {
            //SLOT//OotilityCeption.Log("\u00a78SLOT \u00a74ELB \u00a77Placeholder \u00a73" + goopContainersPlaceholder);

            // Attempt to get
            ContainerTemplateGooP observed = null;
            boolean asPers = false;

            if(pSource != null) {
                //SLOT//OotilityCeption.Log("\u00a78SLOT \u00a74ELB \u00a77Personal Procedure");
                asPers = true;

                // Attempt to get
                observed = pSource.GetParentTemplate();
            } else

            // Stop if null
            if (forObservedContainer != null) {
                //SLOT//OotilityCeption.Log("\u00a78SLOT \u00a74ELB \u00a77Observed Procedure");

                // Attempt to get
                observed = ContainerTemplateGooP.GetObservedStationTemplate(forObservedContainer.getUniqueId(), null);

            // Allow personal?
            }

            // If ovbserved existed
            if (observed != null) {
                //SLOT//OotilityCeption.Log("\u00a78SLOT \u00a74ELB \u00a77Found Observed \u00a76" + observed.getInternalName());

                // Get
                ArrayList<ItemStackSlot> prep = observed.GetPlaceholderSlots(goopContainersPlaceholder);
                
                if (asPers) {
                    
                    // Add processed
                    for (ItemStackSlot pr : prep) {
                        
                        ItemStackSlot processed = new ItemStackSlot(SearchLocation.PERSONAL_CONTAINER, pr.slot, pr.parentSlot);
                        processed.SetPersonalContainer(pSource);
                        ret.add(processed);
                    }
                    
                } else { ret = prep; }
            }

        } else if (IsAny()) {

            // Identify Inventory SIze
            int size = 0;
            switch (getLocation()) {
                case INVENTORY:
                    // Inventory consists of 36 slots
                    size = 36;

                    // Plus OFFHAND and Armour
                    ret.add(new ItemStackSlot(getLocation(), 103, getParentSlot()));
                    ret.add(new ItemStackSlot(getLocation(), 102, getParentSlot()));
                    ret.add(new ItemStackSlot(getLocation(), 101, getParentSlot()));
                    ret.add(new ItemStackSlot(getLocation(), 100, getParentSlot()));
                    ret.add(new ItemStackSlot(getLocation(), -106, getParentSlot()));
                    ret.add(new ItemStackSlot(getLocation(), -107, getParentSlot()));
                    break;
                case ENDERCHEST:
                case SHULKER_ENDERCHEST:
                case SHULKER_INVENTORY:
                case SHULKER_OBSERVED_CONTAINER:
                case SHULKER_PERSONAL_CONTAINER:
                    // All Shulkers are 3 rows of 9 slots
                    size = 27;
                    break;
                case OBSERVED_CONTAINER:

                    // Stop if null
                    if (forObservedContainer != null) {

                        // Get COntainer Size
                        size = ContainerTemplateGooP.GetObservedStationSize(forObservedContainer.getUniqueId(), null);
                    }
                    break;
                case PERSONAL_CONTAINER:

                    if (pSource == null) {

                        // BRUH Not supposed to happen
                        size = 0;
                    } else if (pSource.getParentTemplate() == null) {

                        // BRUH Not supposed to happen
                        size = 0;

                    } else {

                        // Get COntainer Size
                        size = pSource.getParentTemplate().getTotalSlotCount();
                    }
                    break;
            }

            // All between
            for (int s = 0; s < size; s++) {

                // Cook
                ItemStackSlot newRet = new ItemStackSlot(getLocation(), s, getParentSlot());

                // Set Personal Reference
                if (pSource != null) { newRet.SetPersonalContainer(pSource); }

                // Add
                ret.add(newRet);
            }

        } else if (!getUpperRange().equals(getSlot())) {

            // All between
            for (int s = getSlot(); s <= getUpperRange(); s++) {

                // Cook
                ItemStackSlot newRet = new ItemStackSlot(getLocation(), s, getParentSlot());

                // Set Personal Reference
                if (pSource != null) { newRet.SetPersonalContainer(pSource); }

                // Add
                ret.add(newRet);
            }

        } else {

            // Only itself
            ret.add(this);
        }

        //SLOT//OotilityCeption.Log("\u00a7f--------------- \u00a77Pre Parent Sweep:");
        //SLOT//for (ItemStackSlot vsvss : ret) { OotilityCeption.Log("  \u00a7f> \u00a7b#" + vsvss.getSlot() + "\u00a77 " + vsvss.getLocation().toString()); }

        ArrayList<ItemStackSlot> parentedSweep = new ArrayList<>();
        // Does this have a parent?
        if (getParentSlot() != null) {

            // Who does the parent slot represent?
            //Gunging_Ootilities_Plugin.theOots.C PLog("Parent Elaboration: \u00a7b" + ret.size() + "<" + elaboratedParent.size() + "\u00a77; \u00a7e" + slot + "-" + getUpperRange() + "\u00a76<\u00a7e" + getParentSlot().getSlot() + "-" + getParentSlot().getUpperRange());
            ArrayList<ItemStackSlot> elaboratedParent = getParentSlot().Elaborate(forObservedContainer);

            //SLOT//OotilityCeption.Log("\u00a7f>>>>>>>>>>>>>>> \u00a77Parent Sweep \u00a78<<<<<<<<<<<<<<<");
            // Careful evaluation
            if (forObservedContainer != null) {
                 // Time to do the parent sweep yeet
                for (ItemStackSlot oParent :elaboratedParent) {

                    // Get target
                    ItemStackLocation oSource = OotilityCeption.GetInvenItem(forObservedContainer, oParent);
                    //SLOT//OotilityCeption.Log(" \u00a78>\u00a7f> \u00a77Examining \u00a73#" + oParent.getSlot() + "\u00a78 " + oParent.getLocation() + "\u00a77 of \u00a7e" + forObservedContainer.getName());
                    //Gunging_Ootilities_Plugin.theOots.C PLog("Observing Parent \u00a7b#" + oParent.getSlot());

                    // If existed
                    if (oSource != null) {
                        //Gunging_Ootilities_Plugin.theOots.C PLog("\u00a7o Existed");

                        // If there actually was a shilker box in there
                        if (!OotilityCeption.IsAirNullAllowed(oSource.getItem())) {
                            //Gunging_Ootilities_Plugin.theOots.C PLog("\u00a7o NonAIR");
                            //SLOT//OotilityCeption.Log(" \u00a78>\u00a7a> \u00a77Found " + OotilityCeption.GetItemName(oSource.getItem()));

                            // If Shulker
                            if (OotilityCeption.IsShulkerBox(oSource.getItem().getType())) {
                                //SLOT//OotilityCeption.Log(" \u00a78>\u00a7a> \u00a77Identified as Shulker Box. Elaborating fr...");
                                // Gunging_Ootilities_Plugin.theOots.C PLog("\u00a7a\u00a7o Shulker Box");

                                // Alr lets re-do it all
                                for (ItemStackSlot oSlot : ret) {

                                    ItemStackSlot gen = new ItemStackSlot(oSlot.getLocation(), oSlot.getSlot(), oParent);
                                    gen.SetPersonalContainer(GetPersonalContainer());

                                    // Parent Sweep Build
                                    parentedSweep.add(gen);
                                }
                            } else {
                                //SLOT//OotilityCeption.Log(" \u00a78>\u00a7c> \u00a77Not a Shulker BOx ");
                            }
                        } else {
                            //SLOT//OotilityCeption.Log(" \u00a78>\u00a7c> \u00a77Not an Item ");
                        }
                    }
                }

            // If no player is provided, will give the raw full arrays.
            } else {
                //SLOT//OotilityCeption.Log(" \u00a78>\u00a7f>\u00a7e> \u00a77\u00a77lFull Sweep ");

                // Time to do the FULL parent sweep yoot
                for (ItemStackSlot oParent : elaboratedParent) {

                    // Alr lets re-do it all
                    for (ItemStackSlot oSlot : ret) {

                        // Parent Sweep Build
                        parentedSweep.add(new ItemStackSlot(oSlot.getLocation(), oSlot.getSlot(), oParent));
                    }
                }
            }

            //SLOT//OotilityCeption.Log("\u00a78--------------- \u00a77Post Parent Sweep:");
            //SLOT//for (ItemStackSlot vsvss : parentedSweep) { OotilityCeption.Log("  \u00a78> \u00a7b#" + vsvss.getSlot() + "\u00a77 " + vsvss.getLocation().toString() + "\u00a78< \u00a73#" + vsvss.getParentSlot().getSlot() + "\u00a78 " + vsvss.getParentSlot().getLocation() ); }

            // Return the sweep result
            return parentedSweep;

        } else {

            // Thats good then
            return ret;
        }
    }

    public SearchLocation getLocation() { return locationName; }
    public SearchLocation GetLocation() { return locationName; }
    public boolean IsInInventory() { return locationName == SearchLocation.INVENTORY || locationName == SearchLocation.SHULKER_INVENTORY; }
    public boolean IsInEnderchest() { return locationName == SearchLocation.ENDERCHEST || locationName == SearchLocation.SHULKER_ENDERCHEST; }
    public boolean IsInPersonalContainer() { return locationName == SearchLocation.PERSONAL_CONTAINER || locationName == SearchLocation.SHULKER_PERSONAL_CONTAINER; }
    public boolean IsInOpenContainer() { return locationName == SearchLocation.OBSERVED_CONTAINER || locationName == SearchLocation.SHULKER_OBSERVED_CONTAINER; }
    public boolean IsInShulker() { return locationName == SearchLocation.SHULKER_PERSONAL_CONTAINER || locationName == SearchLocation.SHULKER_OBSERVED_CONTAINER || locationName == SearchLocation.SHULKER_INVENTORY || locationName == SearchLocation.SHULKER_ENDERCHEST; }

}
