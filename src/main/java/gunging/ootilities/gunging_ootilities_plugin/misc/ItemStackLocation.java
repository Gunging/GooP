package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.ContainerTemplateGooP;
import gunging.ootilities.gunging_ootilities_plugin.containers.PersonalContainerGooP;
import gunging.ootilities.gunging_ootilities_plugin.containers.PhysicalContainerInstanceGooP;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemStackLocation {

    // The Grandly Observed
    RefSimulator<ItemStack> iSource = new RefSimulator<>(null);

    // Its grand residence
    RefSimulator<Inventory> iLocation = new RefSimulator<>(null);
    RefSimulator<PersonalContainerGooP> personalLocation = new RefSimulator<>(null);
    RefSimulator<PhysicalContainerInstanceGooP> physicalLocation = new RefSimulator<>(null);

    // Its Slot
    Integer slot = null;
    ItemStackLocation parentStack = null;

    // Owner
    UUID owner = null;

    boolean containerDisplay = false;
    public boolean isContainerDisplay() { return containerDisplay; }
    public void setContainerDisplay(boolean val) { containerDisplay = val; }

    // Quickly where it is
    SearchLocation locationName = null;

    // COnstructor
    public ItemStackLocation(ItemStack source, Inventory inven, int slt, SearchLocation place, ItemStackLocation parent) {
        iSource = new RefSimulator<>(source);
        iLocation = new RefSimulator<>(inven);
        slot = slt;
        locationName = place;
        parentStack = parent;
    }
    public ItemStackLocation(ItemStack source, PersonalContainerGooP inven, UUID ownerUUID, int slt, SearchLocation place, ItemStackLocation parent) {
        iSource = new RefSimulator<>(source);
        personalLocation = new RefSimulator<>(inven);
        owner = ownerUUID;
        slot = slt;
        locationName = place;
        parentStack = parent;
    }
    public ItemStackLocation(ItemStack source, PhysicalContainerInstanceGooP inven, int slt, SearchLocation place, ItemStackLocation parent) {
        iSource = new RefSimulator<>(source);
        physicalLocation = new RefSimulator<>(inven);
        slot = slt;
        locationName = place;
        parentStack = parent;

    }

    public boolean IsItemNull() {
        if (iSource == null) return true;
        return iSource.getValue() == null;
    }
    public void ForceOwner(UUID ownr) { owner = ownr; }

    // Informational
    public Integer getSlot() { return slot; }
    public int getAmount() { return iSource.getValue().getAmount(); }
    public ItemStack getItem() { return iSource.getValue(); }

    public void SetItemAmount(Integer amount) {

        // Nonullify
        ItemStack overcopy = new ItemStack(Material.AIR);

        // UH
        if (iSource.getValue() != null) { overcopy = new ItemStack(iSource.getValue()); }

        // Set amount
        overcopy.setAmount(amount);

        // Replace ig
        ReplaceItem(overcopy);
    }
    public void ReplaceItem(@Nullable ItemStack newItem) {

        // Nonullify
        ItemStack overcopy = new ItemStack(Material.AIR);
        if (newItem != null) { if (newItem.getAmount() > 0) { overcopy = new ItemStack(newItem); } }

        // Store New Reference
        iSource = new RefSimulator<>(overcopy);

        switch (locationName) {
            default:

                // Is it the mainhand?
                if (slot == -7) { ((PlayerInventory) iLocation.getValue()).setItemInMainHand(overcopy); }

                // Is it the cursor?
                else if (slot == -107) {

                    // Replace cursor
                    HumanEntity holder = ((PlayerInventory) iLocation.getValue()).getHolder();
                    if (holder != null) { holder.getOpenInventory().setCursor(overcopy); }
                }

                // Is it the offhand?
                else if (slot == -106) { ((PlayerInventory) iLocation.getValue()).setItemInOffHand(overcopy); }

                // Is it the Helmet?
                else if (slot == 103) { ((PlayerInventory) iLocation.getValue()).setHelmet(overcopy); }

                // Is it the Chest?
                else if (slot == 102) { ((PlayerInventory) iLocation.getValue()).setChestplate(overcopy); }

                // Is it the Legs?
                else if (slot == 101) { ((PlayerInventory) iLocation.getValue()).setLeggings(overcopy); }

                // Is it the Boots?
                else if (slot == 100) { ((PlayerInventory) iLocation.getValue()).setBoots(overcopy); }

                // Must be a normal slot
                else { iLocation.getValue().setItem(slot, overcopy); }
                break;
            case OBSERVED_CONTAINER:
            case PERSONAL_CONTAINER:
                // Only PHYSICAL saves changes.
                if (physicalLocation.getValue() != null) {

                    if (isContainerDisplay()) {

                        // Stations behave like normal inventories, actually
                        physicalLocation.getValue().SetInOpen(slot, overcopy, true);

                    } else {

                        // Must set and save it if not the default
                        physicalLocation.getValue().SetAndSaveInventoryItem(slot, overcopy, null, true);
                    }

                } else if (personalLocation.getValue() != null) {

                    if (isContainerDisplay()) {

                        // Stations behave like normal inventories, actually
                        personalLocation.getValue().SetInOpen(owner, slot, overcopy, true, false);

                    } else {

                        // Must set and save it
                        personalLocation.getValue().SetAndSaveInventoryItem(owner, slot, overcopy, null, true, false);

                    }

                } else {

                    boolean defaulting = false;
                    ContainerTemplateGooP temp = null;
                    if (OotilityCeption.IsAirNullAllowed(overcopy) && owner != null) {

                        // Found Template
                        temp = ContainerTemplateGooP.playerOpened.get(owner);
                        if (temp != null) {

                            // Will default if AIR and For Storage
                            defaulting = temp.getSlotAt(slot).IsForStorage();
                        }
                    }

                    // Is it being set to air and it could be defaulted?
                    if (defaulting) {

                        // Set to default-yo
                        iLocation.getValue().setItem(slot, temp.getDefaultContent(slot));

                    // Put thay
                    } else {

                        // Stations behave like normal inventories, actually
                        iLocation.getValue().setItem(slot, overcopy);
                    }
                }
                break;
            case SHULKER_OBSERVED_CONTAINER:
            case SHULKER_PERSONAL_CONTAINER:
            case SHULKER_ENDERCHEST:
            case SHULKER_INVENTORY:

                // Get Shulker box parent
                ItemStack shulkParent = new ItemStack(parentStack.getItem());

                // Get Meta
                BlockStateMeta bsm = (BlockStateMeta) shulkParent.getItemMeta();
                ShulkerBox boxx = (ShulkerBox) bsm.getBlockState();
                boxx.getInventory().setItem(slot, overcopy);
                bsm.setBlockState(boxx);
                boxx.update();
                shulkParent.setItemMeta(bsm);

                // Save is inlcuded in parent
                parentStack.ReplaceItem(shulkParent);
                break;
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
