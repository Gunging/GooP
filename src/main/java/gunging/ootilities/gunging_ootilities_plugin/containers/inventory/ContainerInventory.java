package gunging.ootilities.gunging_ootilities_plugin.containers.inventory;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.events.ScoreboardLinks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Containers are like normal inventories but more complicated.
 * I manage this in a system of "Layers":
 * <br><br>
 * <b>Layer 0: Default Items   </b><br>
 *      Items hardcoded into the container template.
 *
 * <br><br>
 * <b>Layer 1: Edited Default Items   </b><br>
 *     When performing goop commands on default items, they get
 *     stored in this funny place.
 *
 * <br><br>
 * <b>Layer 2: Storage Items   </b><br>
 *     When a player puts something on a slot, it is saved in this inventory.
 *
 * <br><br>
 * When the inventor is built, the layers are stacked and sent.
 */
public class ContainerInventory {

    //region Constructor
    /**
     * @return The template obeyed by this container inventory
     */
    public @NotNull ContainerReasonProcess getReasonProcess() { return reasonProcess; }
    @NotNull final ContainerReasonProcess reasonProcess;

    /**
     * @return The template obeyed by this container inventory
     */
    public @NotNull GOOPCTemplate getTemplate() { return template; }
    @NotNull final GOOPCTemplate template;

    /**
     * @return The inventory managed by this instance.
     */
    public @NotNull Inventory getInventory() { return inventory; }
    @NotNull final Inventory inventory;

    /**
     * @return The current player who is 'in charge' of this inventory.
     *         PAPI placeholders will parse in their name.
     *         <br><br>
     *         For STATION and PHYSICAL containers, its the player to
     *         first open this container.
     *         <br><br>
     *         For PERSONAL containers, is the player that owns the container
     *         if online, or the player who first opened the container.
     */
    public @NotNull Player getPrincipalObserver() { return principalObserver; }
    @NotNull Player principalObserver;

    /**
     * @return Physical ID (if PHYSICAL) or OID (if PERSONAL).
     */
    @Nullable public String getInstanceID() { return null; }

    /**
     * A multi-layered inventory.
     *
     * @param template The template obeyed by this container inventory
     *
     * @param reason Reason of the creation of this inventory, alters behaviour.
     *
     * @param principal The player who will have authority sometimes.
     */
    @SuppressWarnings("deprecation")
    public ContainerInventory(@NotNull GOOPCTemplate template, @NotNull ContainerReasonProcess reason, @NotNull Player principal) {
        principalObserver = principal;
        observers.add(principal);
        reasonProcess = reason;
        this.template = template;

        // Obtain title
        String title = OotilityCeption.ParseConsoleCommand(getReasonProcess().getTitle(getTemplate(), getInstanceID()), principal, principal, null, null);

        // Starts fresh. Empty inventory of desired size|
        inventory = Bukkit.createInventory(principalObserver, reason.getInventorySize(template), title);

        // Load defaults
        recreateDefaultInventory();
    }
    //endregion

    //region Loading
    /**
     * Reloads the default item layer of the inventory
     * straight from the Container Template.
     *<br><br>
     * Does not send inventory updates.
     */
    void recreateDefaultInventory() {

        // Identify the defaults
        layerDefault.clear();
        layerDefault.putAll(getReasonProcess().getDefaultLayer(template));
    }
    /**
     * Accept an override of storage contents directly into the array.
     * <br><br> Does not send inventory updates.
     *
     * @param contents Items to now have as storage items.
     */
    public void loadStorageContents(@NotNull HashMap<Integer, ItemStack> contents) {

        // Clear and put
        layerStorage.clear();

        for (Map.Entry<Integer, ItemStack> entry : contents.entrySet()) {
            if (OotilityCeption.IsAirNullAllowed(entry.getValue())) { continue; }

            layerStorage.put(entry.getKey(), entry.getValue());
            //CLI// OotilityCeption.Log("\u00a78CINVEN \u00a72LOAD\u00a77 Loaded " + OotilityCeption.GetItemName(entry.getValue()) + "\u00a77 into \u00a73#" + entry.getKey());
        }

        if (Gunging_Ootilities_Plugin.blacklistedEnchantments.size() > 0) {

            // Explore all items yeah
            for (Map.Entry<Integer, ItemStack> entry : contents.entrySet()) {
                ItemStack stack = ScoreboardLinks.removeBlacklistedEnchantments(entry.getValue());

                // Set and save changes
                if (stack != null) {

                    setAndSaveStorageItem(entry.getKey(), stack);
                    layerStorage.put(entry.getKey(), stack);
                    updateSlot(entry.getKey());
                }
            }
        }
    }
    /**
     * When editing Container Templates, slots that used to be storage may now be display.
     * Those items gotta get dropped, if there are any loaded that occupy that space!
     * <br><br>
     * This method deletes those items from the files, removes them from the storage
     * layer, and wherever else they are registered as saved items, and drops them to
     * the player specified as receiver.
     * <br><br>
     * This method does not update the observed inventories.
     *
     * @param receiver Player who would receive the extraneous items.
     */
    public void sanitizeStorage(@NotNull Player receiver) { }
    //endregion

    //region Observers
    /**
     * @return All the players currently looking at this inventory.
     */
    @NotNull public ArrayList<Player> getObservers() { return observers; }
    @NotNull final ArrayList<Player> observers = new ArrayList<>();
    /**
     * Register this player as having this inventory opened. <br>
     * Does not actually open the inventory for them.
     *
     * @param observer Player who opened this.
     */
    public void addObserver(@NotNull Player observer) { observers.add(observer); }
    /**
     * This player has closed this inventory
     *
     * @param observer Player who closed this.
     *
     * @param demote If a new principal player should be chosen now that this guy has
     *               closed the container (if it was the principal player).
     *               <br><br>
     *               This is desirable for example in PHYSICAL, where multiple players
     *               may be watching the container, and if the principal closes it then
     *               the PAPI placeholders will parse with someone new.
     *               <br>
     *               However, there is also PERSONAL containers where the PAPI placeholders
     *               should always parse from the principal observer (the owner).
     */
    public void removeObserver(@NotNull Player observer, boolean demote) {

        // Remove from the observers
        observers.remove(observer);

        // Choose new principal? Is there still anyone?
        if (demote && observer.getUniqueId().equals(getPrincipalObserver().getUniqueId()) && observers.size() > 0) {

            // Choose ANYONE as the principal
            principalObserver = observers.get(0);
        }
    }

    /**
     * Rebuilds the inventory seen by all observers. Not to be confused with {@link #acoplar(Inventory)} that
     * prints these contents onto any arbitrary inventory.
     *
     * @see #acoplar(Inventory)
     */
    public void updateInventory() { for (int i = 0; i < getReasonProcess().getInventorySize(getTemplate()); i++) { updateSlot(i); } }
    /**
     * Updates the target slot in the observed inventories.
     *
     * @param slot Slot to update. Ignored if out of range.
     */
    public void updateSlot(@Nullable Integer slot) {
        //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76UPD\u00a77 Updating Slot \u00a73#" + slot);

        // Ignore
        if (slot == null) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76UPD\u00a7c -\u00a77 Null Slot");
            return;
        }

        // Out of Range
        if (slot < 0 || slot > getInventory().getSize()) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76UPD\u00a7c -\u00a77 Overflow Slot");
            return;
        }

        // Which one (respecting layers)?
        ItemStack found = getReasonProcess().isLoreParser() ? OotilityCeption.ParseLore(getItem(slot), getPrincipalObserver()) : getItem(slot);

        //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76UPD\u00a7a +\u00a77 Placing " + OotilityCeption.GetItemName(found));

        // Edit in inventory with lore parsed if applicable
        getInventory().setItem(slot, found);
    }
    //endregion

    //region Layers System and Contents
    /**
     * @return The items mapped to the default layer. These items are hard-coded
     *         into the container yeah.
     */
    @NotNull public HashMap<Integer, ItemStack> getLayerDefault() { return layerDefault; }
    @NotNull final HashMap<Integer, ItemStack> layerDefault = new HashMap<>();

    /**
     * @return The items mapped to the edited-defaults layer. These items are the
     *         defaults but edited via /goop commands.
     */
    @NotNull public HashMap<Integer, ItemStack> getLayerEdited() { return layerEdited; }
    @NotNull final HashMap<Integer, ItemStack> layerEdited = new HashMap<>();
    /**
     * Accept an override of an edited default directly into the array.
     * <br><br> Does not send inventory updates.
     *
     * @param slot Slot to set the item at
     * @param item ItemStack to store
     */
    public void setEditedItem(int slot, @Nullable ItemStack item) {
        if (slot < 0 || slot >= getReasonProcess().getInventorySize(getTemplate())) { return; }

        // Yeah
        layerEdited.put(slot, item);
    }

    /**
     * @return The items mapped to the storage layer. These items are the
     *         put in there by the player.
     */
    @NotNull public HashMap<Integer, ItemStack> getLayerStorage() { return layerStorage; }
    @NotNull final HashMap<Integer, ItemStack> layerStorage = new HashMap<>();
    /**
     * Accept an override of storage contents directly into the array.
     * <br><br> Does not send inventory updates.
     *
     * @param slot Slot to set the item at
     * @param item ItemStack to store
     */
    public void setStorageItem(int slot, @Nullable ItemStack item) {
        if (slot < 0 || slot >= getReasonProcess().getInventorySize(getTemplate())) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76SET\u00a77 Overflow Slot \u00a73#" + slot);
            return; }

        // Remove if air, instead
        if (OotilityCeption.IsAirNullAllowed(item)) {
            //CLI// OotilityCeption.Log("\u00a78CINVEN \u00a72LOAD\u00a77 Removed item \u00a73#" + slot);
            layerStorage.remove(slot); return; }

        //CLI// OotilityCeption.Log("\u00a78CINVEN \u00a72LOAD\u00a77 Loaded " + OotilityCeption.GetItemName(item) + "\u00a77 into \u00a73#" + slot);
        //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76SET\u00a77 Storing at \u00a73#" + slot + "\u00a77: " + OotilityCeption.GetItemName(item));

        // Yeah
        layerStorage.put(slot, item);
    }
    /**
     * Does not send inventory updates.
     *
     * @param slot Slot to put the item into
     * @param item Item to put in the slot
     */
    public void setAndSaveStorageItem(int slot, @Nullable ItemStack item) {
        //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76SVE\u00a77 Setting and Saving at \u00a73#" + slot + "\u00a77: " + OotilityCeption.GetItemName(item) + "\u00a78 (STN)");

        if (slot < 0 || slot >= getReasonProcess().getInventorySize(getTemplate())) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76SVE\u00a77 Overflow Slot");
            return; }

        // Set it in storage layer
        setStorageItem(slot, item);
    }

    /**
     * Overwrites the contents of this target inventory with those registered here.
     *
     * @param inventory Inventory that will receive the incoming item flux.
     *
     * @see #updateInventory()
     */
    public void acoplar(@NotNull Inventory inventory) {

        // Yeah
        for (int i = 0; i < inventory.getSize(); i++) {

            // Set Item
            inventory.setItem(i, getItem(i));
        }
    }
    /**
     * The actual item shown to the player in the inventory.
     * <br><br>
     * This method will ignore Storage layer outside of Storage Slots, and
     * will ignore Edited layer outside of Display slots.
     *
     * @param slot Inventory slot number
     *
     * @return The item that corresponds to this slot, in the end.
     *         <br><br>
     *         Layer hierarchy: Storage > Edited > Default
     *         <br><br>
     *         Though edited and storage should never share indices.
     *
     * @see #getTrueItem(Integer)
     */
    @Nullable public ItemStack getItem(@Nullable Integer slot) {
        //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a77 Getting item from Slot \u00a73#" + slot);

        // Ignore
        if (slot == null) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a7c -\u00a77 Null Slot");
            return null; }

        // Out of Range
        if (slot < 0 || slot >= getReasonProcess().getInventorySize(getTemplate())) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a7c -\u00a77 Overflow Slot");
            return null; }

        // Edge is default and that's IT
        if (getTemplate().isEdgeSlot(slot)) { return getLayerDefault().get(slot); }

        // Is it a storage slot?
        if (getTemplate().isStorageSlot(slot)) {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a7e ?\u00a77 Checking Storage Layer");

            // Return from storage
            ItemStack ret = getLayerStorage().get(slot);
            if (!OotilityCeption.IsAirNullAllowed(ret)) {
                //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a7a +\u00a77 Found " + OotilityCeption.GetItemName(ret));
                return ret; }

        // Must be display or something else
        } else {
            //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a7e ?\u00a77 Checking Edited Layer");

            // Return from edited
            ItemStack ret = getLayerEdited().get(slot);
            if (ret != null) {
                //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a7a +\u00a77 Found " + OotilityCeption.GetItemName(ret));
                return ret; }
        }
        //CLI//OotilityCeption.Log("\u00a78CONINVEN \u00a76GET\u00a72 +\u00a77 Returning Default " + OotilityCeption.GetItemName(getLayerDefault().get(slot)));

        // Return from defaults
        return getLayerDefault().get(slot);
    }
    /**
     * This method does not care if slots are strictly display or storage.
     *
     * @param slot Inventory slot number
     *
     * @return The item that corresponds to this slot, in the end.
     *         <br><br>
     *         Layer hierarchy: Storage > Edited > Default
     *         <br><br>
     *         Though edited and storage should never share indices.
     *
     * @see #getItem(Integer)
     */
    @Nullable public ItemStack getTrueItem(@Nullable Integer slot) {
        if (slot == null) { return null; }

        // Return from storage
        ItemStack ret = getLayerStorage().get(slot);
        if (ret != null) { return ret; }

        // Return from edited
        ret = getLayerEdited().get(slot);
        if (ret != null) { return ret; }

        // Return from defaults
        return getLayerDefault().get(slot);
    }
    /**
     * The item shown to the player in the inventory.
     * <br><br>
     * This method will return Storage when Storage Slots, and
     * will return Edited layer when Display slots.
     *
     * @param slot Inventory slot number
     *
     * @return Item in this slot corresponding to Storage or Edited Layer.
     *
     * @see #getTrueItem(Integer)
     */
    @Nullable public ItemStack getNonDefaultItem(@Nullable Integer slot) {
        if (slot == null) { return null; }

        // Return from storage
        ItemStack ret = getLayerStorage().get(slot);
        if (ret != null) { return ret; }

        // Return from edited
        ret = getLayerEdited().get(slot);
        return ret;
    }

    /**
     * Slots of type RESULT should check for the EDITED storage layer to determine
     * default-ness because of how the crafting engine works (probably?)
     *
     * @param slot Slot you are targeting
     *
     * @return If there is nothing stored on this slot.
     *         <br><br>
     *         May return a false negative if you use it on display
     *         slots because the player can never store items in it.
     *         <br><br>
     *         <b>This should only be used with storage slots.</b>
     */
    public boolean isDefault(@Nullable Integer slot) {
        if (slot == null) { return true; }
        //CLI// OotilityCeption.Log("\u00a78CINVEN \u00a7aDEF\u00a77 Found " + OotilityCeption.GetItemName(getLayerStorage().get(slot)) + "\u00a77 at \u00a73#" + slot + "\u00a77 ~ Default?\u00a7e " + OotilityCeption.IsAirNullAllowed(getLayerStorage().get(slot)));

        // If null, it is default
        return OotilityCeption.IsAirNullAllowed(getLayerStorage().get(slot));
    }
    //endregion
}
