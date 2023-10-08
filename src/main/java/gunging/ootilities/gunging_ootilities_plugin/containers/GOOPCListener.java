package gunging.ootilities.gunging_ootilities_plugin.containers;

import gunging.ootilities.gunging_ootilities_plugin.GungingOotilities;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.containers.compatibilities.GPCTouchscreen;
import gunging.ootilities.gunging_ootilities_plugin.containers.interaction.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.ISSObservedContainer;
import gunging.ootilities.gunging_ootilities_plugin.containers.inventory.PersonalContainerInventory;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Personal;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Physical;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GCL_Player;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GTL_Containers;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerOpeningReason;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GCT_PlayerTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GOOPCPlayer;
import gunging.ootilities.gunging_ootilities_plugin.misc.SearchLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISLInventory;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ISSInventory;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation.GTranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GOOPCListener implements Listener {

    int list = 585228082;
    int ener = 705596427;

    @NotNull static HashMap<String, Long> clickCooldowns = new HashMap<>();
    @NotNull static final GPCTouchscreen touchscreen = new GPCTouchscreen();

    //region Inventory Corroboration Agency
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnGamemodeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) { return; }

        // Yes
        (new BukkitRunnable() {
            public void run() {

                // Run delayed by 1 tick
                GCL_Player.corroborateInventory(event.getPlayer(), false);
            }
        }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnRPGInventoryClose(InventoryCloseEvent event) {

        // Fast cancellation
        if (!(event.getPlayer() instanceof Player)) { return; }
        if (event.getView().getType() != InventoryType.CRAFTING) { return; }

        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 RPG Inven Close ----------\u00a7b " + event.getView().getType().toString());

        // Clear those agony items
        for (int i = 0; i <= 4; i++) {

            // Get that item
            ItemStack item = event.getInventory().getItem(i);
            //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 #" + i + ": \u00a7b" + OotilityCeption.GetItemName(item));

            // Destroy it
            if (GOOPCManager.isDefaultItem(item)) { item.setAmount(0); }
        }

        // Yes
        (new BukkitRunnable() {
            public void run() {

                // Run delayed by 1 tick
                GCL_Player.corroborateInventory((Player) event.getPlayer(), true);
            }
        }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);

        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 RPG Inven Close Fin ----------\u00a7b " + event.getView().getType().toString());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnRPGInventorySlotsClear(PrepareItemCraftEvent event) {
        if (event.getView().getType() != InventoryType.CRAFTING) { return; }
        if (!(event.getView().getPlayer() instanceof Player)) { return; }
        Player player = (Player) event.getView().getPlayer();

        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 Prepare Crafting Event --------------");
        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 Size: \u00a7b" + event.getInventory().getSize());
        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 #0: \u00a7b" + OotilityCeption.GetItemName(event.getInventory().getItem(0), true));
        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 #1: \u00a7b" + OotilityCeption.GetItemName(event.getInventory().getItem(1), true));
        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 #2: \u00a7b" + OotilityCeption.GetItemName(event.getInventory().getItem(2), true));
        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 #3: \u00a7b" + OotilityCeption.GetItemName(event.getInventory().getItem(3), true));
        //EVN//OotilityCeption.Log("\u00a78CLICK\u00a7a EV\u00a77 #4: \u00a7b" + OotilityCeption.GetItemName(event.getInventory().getItem(4), true));

        /*/ Remove count before anything
        for (int i = 0; i < 5; i++) {
            ItemStack obs = event.getInventory().getItem(i);
            if (OotilityCeption.IsAirNullAllowed(obs)) { continue; }
            if (GOOPCManager.isDefaultItem(obs) || OotilityCeption.IsEncrypted(obs, GOOPCTemplate.EDGE_ENCRYPTION_CODE)) {

                // Set amount to zero
                event.getInventory().getItem(i).setAmount(0); } } //*/

        // Get RPG
        GOOPCPlayer rpg = GCL_Player.getInventoryFor(player);
        if (rpg == null) { return; }

        // Temporary Item Stack for evaluation
        ISLInventory location = OotilityCeption.getItemFromPlayer(player, -8, true);
        if (location == null) { return; }

        // Item Expected
        GOOPCSlot display = rpg.getTemplate().getSlotAt(location.getSlot());
        if (display == null) { return; }

        // Apply
        rpg.applyToLocation(player, location, display, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnLogin(PlayerJoinEvent event) { GCL_Player.corroborateInventory(event.getPlayer(), false); }
    //endregion
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnClick(InventoryClickEvent event) {
        if (event.getView().getType() == InventoryType.CREATIVE) { return; }
        if (!(event.getView().getPlayer() instanceof Player)) { return; }

        /*
         * Touch screen mode fires three events the same tick sometimes, these
         * constitute one transaction and must be respected and not cancelled.
         *
         * The order is:
         *      PICKUP_ALL
         *      <actual event of interest>
         *      PLACE_ALL
         *
         * Containers must avoid cancelling the latter two due to cooldown purposes.
         */
        if (event.getAction() == InventoryAction.PICKUP_ALL) { touchscreen.addEvent((Player) event.getView().getPlayer(), event); }

        //region OnClick
        if (event.isCancelled()) { return; }

        //CLI//final UUID prio = UUID.randomUUID();
        //CLI//OotilityCeption.Log("\u00a7a--------------------------------------------------\u00a72 " + prio.toString());
        //EVN//OotilityCeption.Log("\u00a78GCLISTENER\u00a7a EV\u00a77 View \u00a7a" + event.getView().getType().toString());
        //EVN//OotilityCeption.Log("\u00a78GCLISTENER\u00a7a EV\u00a77 Slot \u00a7b#" + event.getSlot() + " \u00a78(Raw\u00a79 #" + event.getRawSlot() + "\u00a78)");
        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a78 Tick Time:\u00a73 " + Gunging_Ootilities_Plugin.getCurrentTick());

        // Identify
        InventoryView view = event.getView();
        final Player player = (Player) view.getPlayer();
        final GOOPCDeployed deployedContainer = GOOPCManager.getContainer(view);
        final GOOPCPlayer deployedRPG = GCL_Player.getInventoryFor(player);
        boolean nullContainer = (deployedContainer == null);
        boolean nullRPGInven = (deployedRPG == null);
        if (nullContainer && nullRPGInven) { return; }
        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Identified Container\u00a7a " + (deployedContainer == null ? "null" : deployedContainer.getTemplate().getInternalName()));
        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Null Container?\u00a7e " + nullContainer);
        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Null RPG Inven?\u00a7b " + nullRPGInven);

        /*
         * Now we know it is a GooP Containers interaction.
         * Time to start cancelling for any suspicious reason.
         */
//        if (!GOOPCManager.isPremiumEnabled() && !player.isOp()) {
//
//            // Mention what is wrong
//            event.getView().getPlayer().sendMessage(OotilityCeption.LogFormat("Hey! Only OP players can use \u00a7e\u00a7l\u00a7oGooP Containers\u00a77 without the Premium Version!"));
//
//            // Cancel
//            event.setCancelled(true);
//            return;
//        }

        /*
         * Special Edition Modes: These wont support any of the containers special functions, as
         * they are meant to do their own things. Most actions are disabled and behave as usual
         * inventories.
         */
        if (GOOPCManager.isUsage_EditionCommands(view)) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Command Edition ~ No allowed actions");

            // No interactions with the container are supported
            event.setCancelled(true);
            return;

            // If it is either of the CONTENTS PHASES
        } else if (GOOPCManager.isUsage_EditionDisplay(view)) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Display Edition ~ All allowed actions");

            // All is allowed, and nothing special happens.
            return;

        } else if (GOOPCManager.isUsage_EditionStorage(view)) {

            // Cancel if it is an edge :V
            if (OotilityCeption.IsEncrypted(event.getCurrentItem(), GOOPCTemplate.EDGE_ENCRYPTION_CODE)) {
                //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Usage Edition ~ No edge interaction");
                event.setCancelled(true); }

            //CLI// else { OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Usage Edition ~ Allowed action"); }

            // Nothing else happens
            return;
        }

        // That's not good
        final ContainerInventory observed = deployedContainer == null ? null : deployedContainer.getObservedBy(player.getUniqueId());
        if (observed == null && !nullContainer) {
            Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cContainers OnClick Error: \u00a76Invalid Observed Container");

            // Log this error
            event.setCancelled(true); return; }

        // Obtain the action. If not found, it is not supported.
        ContainersClickHandler handler = ContainersInteractionHandler.getHandlerFor(event.getAction());
        if (handler == null) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Unsupported Action\u00a7c " + event.getAction().toString());
            event.setCancelled(true); return; }

        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Handling Via \u00a7a " + handler.getClass().getSimpleName());

        // What is the meaning of this?
        boolean clickedTopInventory = event.getRawSlot() < event.getView().getTopInventory().getSize();
        boolean affectsContainer = !nullContainer && (clickedTopInventory || handler.affectsTop(event));
        boolean affectsRPGInven = !nullRPGInven && (!clickedTopInventory || handler.affectsBottom(event) || view.getType() == InventoryType.CRAFTING);

        /*
         * It is not our business if the container was not clicked, and the inventory action affects
         * only one slot. Suppose though that it does affect multiple slots, then if it is not affecting
         * the top, we are clear.
         *
         * Suppose the action only affects the bottom, if there is no player-type container its also not
         * my business
         */
        if (!affectsContainer && !affectsRPGInven) {

            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Not business of GooP Containers (not affecting top inventory)");
            return; }

        /*
         * Slow down the input processing
         *
         * Unfortunately, GOOP Containers agonizes with auto-clickers, must prevent.
         */
        String slowmodeUUID = nullContainer ? player.getName() : observed.getInstanceID();

        // Evaluate click cooldown
        Long current = System.currentTimeMillis();
        Long last = clickCooldowns.get(slowmodeUUID);
        if (last != null && (current - last) < 170) {

            // Find the other events fired this tick
            ArrayList<InventoryClickEvent> touchscreenBurst = touchscreen.getEvents(player);
            if (touchscreenBurst.size() == 0) {

                //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a7c Cooldown Cancellation:\u00a7e " + (current - last));

                // This person is trying to glitch the container
                event.setCancelled(true); return;

            // Touchscreen mode event sequence allowed through
            } else {

                //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a7c Cooldown Cancellation \u00a76TS-Averted\u00a7c:\u00a7e " + (current - last));
            }

            // Update to current cooldown
        } else { clickCooldowns.put(slowmodeUUID, current); }

        // Carry out the operation
        ContainersInteractionResult result = handler.handleContainersOperation(deployedContainer, observed, deployedRPG, player, event);

        // Update slot
        GungingOotilities.setProvidedSlot(player.getUniqueId(), SearchLocation.OBSERVED_CONTAINER, event.getSlot());

        /*
         * The result operation will be null when the thing has finalized
         * without anything further to run or do. Like an edge being clicked.
         *
         * The handler is in charge of cancelling the event.
         */
        if (result == null) {

            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73RS\u00a77 Handling Finished, no result.");
            return; }

        //CLI//OotilityCeption.Log("\u00a7e--------------------------------------------------\u00a76 " + prio.toString());
        boolean procEquipmentUpdate = false;

        // Container, unknown
        if (!nullContainer) {

            // Save item changes
            for (Integer slotUpdate : result.getSlotsUpdate()) {

                // If personal, watch for equipment update.
                if (Gunging_Ootilities_Plugin.foundMMOItems && deployedContainer.getTemplate().isPersonal()
                        &&  deployedContainer.getTemplate().getEquipmentSlots().contains(slotUpdate)) { procEquipmentUpdate = true; }

                // Update the slots of the result
                if (!result.isPreventEquipmentUpdate()) {
                    //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Updating inventory slot \u00a7b#" + slotUpdate);
                    observed.updateSlot(slotUpdate);
                }
            }
        }

        if (!nullRPGInven) {

            // Save item changes
            for (CIContainerInteracting slotUpdate : result.getPlayerSlotsUpdate()) {

                // This will verify that the default item is shown
                //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Refreshing player inventory slot \u00a7b#" + slotUpdate.getContainerSlot().getSlotNumber());
                if (OotilityCeption.IsAirNullAllowed(slotUpdate.getCurrentItem())) { slotUpdate.setItem(slotUpdate.getCurrentItem()); }
            }
        }

        // Equipment Update
        if (procEquipmentUpdate) { GooPMMOItems.UpdatePlayerEquipment(((PersonalContainerInventory) observed).getOwnerUUID()); }

        // Run commands
        for (String cmd : result.getCommands()) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Sending Command \u00a73" + cmd);

            // Run commands, parse placeholders
            OotilityCeption.SendConsoleCommand(GOOPCManager.parseAsContainers(cmd, observed), player, player, null, null);
        }

        // Cursor Update
        if (result.isCursorUpdate() && !result.isCursorUpdateResolved()) {

            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Pre Cursor Edition (natural)");
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a73 +\u00a77 Current: " + OotilityCeption.GetItemName(player.getItemOnCursor(), true));
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a73 +\u00a77 Edited: " + OotilityCeption.GetItemName(result.getEditedCursor(), true));
            player.setItemOnCursor(result.getEditedCursor());

            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Post Cursor Edition");
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a73 +\u00a77 Current: " + OotilityCeption.GetItemName(player.getItemOnCursor(), true));
            expressCursorEdition.remove(player.getUniqueId()); }

        // Send update because uuuh
        (new BukkitRunnable() { public void run() { player.updateInventory(); } }).runTaskLater(Gunging_Ootilities_Plugin.theMain, 1L);

        /* DEPRECATED Express Cursor Edition
        if (result.isCursorUpdate()) { expressCursorEdition.put(player.getUniqueId(), result); }
        ArrayList<InventoryClickEvent> touchscreenBurst = new ArrayList<>(touchscreen.getEvents(player));

        // Execute the actions next tick (so that the slot makes sense to a human dev).
        (new BukkitRunnable() {
            public void run() {
                boolean procEquipmentUpdate = false;
                //LGY//OotilityCeption.Log("\u00a7e--------------------------------------------------\u00a76 " + prio.toString());

                if (!nullContainer) {

                    // Save item changes
                    for (Integer slotUpdate : result.getSlotsUpdate()) {

                        // If personal, watch for equipment update.
                        if (Gunging_Ootilities_Plugin.foundMMOItems && deployedContainer.getTemplate().isPersonal()
                                &&  deployedContainer.getTemplate().getEquipmentSlots().contains(slotUpdate)) { procEquipmentUpdate = true; }

                        //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Updating inventory slot \u00a7b#" + slotUpdate);
                        observed.updateSlot(slotUpdate);
                    }
                }

                if (!nullRPGInven) {

                    // Save item changes
                    for (CIContainerInteracting slotUpdate : result.getPlayerSlotsUpdate()) {

                        // This will verify that the default item is shown
                        //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Refreshing player inventory slot \u00a7b#" + slotUpdate.getContainerSlot().getSlotNumber());
                        if (OotilityCeption.IsAirNullAllowed(slotUpdate.getCurrentItem())) { slotUpdate.setItem(slotUpdate.getCurrentItem()); }
                    }
                }

                // Equipment Update
                if (procEquipmentUpdate) { GooPMMOItems.UpdatePlayerEquipment(((PersonalContainerInventory) observed).getOwnerUUID()); }

                // Run commands
                for (String cmd : result.getCommands()) {
                    //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Sending Command \u00a73" + cmd);

                    // Run commands, parse placeholders
                    OotilityCeption.SendConsoleCommand(GOOPCManager.parseAsContainers(cmd, observed), player, player, null, null);
                }

                // Cursor Update
                if (result.isCursorUpdate() && !result.isCursorUpdateResolved()) {


                    // Find the other events fired this tick
                    if (touchscreenBurst.size() > 0) {

                        // Would somehow intercept the next, PLACE_ALL event, and put the cursor result in it

                    } else {

                        //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Pre Cursor Edition (natural)");
                        //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a73 +\u00a77 Current: " + OotilityCeption.GetItemName(player.getItemOnCursor(), true));
                        //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a73 +\u00a77 Edited: " + OotilityCeption.GetItemName(result.getEditedCursor(), true));
                        player.setItemOnCursor(result.getEditedCursor());

                        //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Post Cursor Edition");
                        //LGY//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a73 +\u00a77 Current: " + OotilityCeption.GetItemName(player.getItemOnCursor(), true));
                    }
                    expressCursorEdition.remove(player.getUniqueId()); }

            }

        }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
        */


        //endregion

    }
    @NotNull static final HashMap<UUID, ContainersInteractionResult> expressCursorEdition = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDrag(InventoryDragEvent event) {
        //EVN//OotilityCeption.Log("\u00a78GCLISTENER \u00a79XPL\u00a77 .-----.-----.----> Drag Event <-----.-----.----.");
        //EVN//OotilityCeption.Log("\u00a78GCLISTENER \u00a79XPL\u00a77 Drag Type:\u00a79 " + event.getType().toString());
        //EVN//OotilityCeption.Log("\u00a78GCLISTENER \u00a79XPL\u00a77 Cursor: " + OotilityCeption.GetItemName(event.getCursor(), true));
        //EVN//OotilityCeption.Log("\u00a78GCLISTENER \u00a79XPL\u00a77 Old Cursor: " + OotilityCeption.GetItemName(event.getOldCursor(), true));
        //EVN//OotilityCeption.Log("\u00a78GCLISTENER \u00a79XPL\u00a77 New Items: ");
        //EVN//for (Map.Entry<Integer, ItemStack> aff : event.getNewItems().entrySet()) { OotilityCeption.Log("\u00a78GCLISTENER \u00a79XPL\u00a7b @" + aff.getKey() + "\u00a77 " + OotilityCeption.GetItemName(aff.getValue(), true));}

        // Fast cancellation
        if (event.isCancelled()) { return; }
        if (!(event.getView().getPlayer() instanceof Player)) { return; }

        // Identify
        InventoryView view = event.getView();
        final GOOPCDeployed deployed = GOOPCManager.getContainer(view);
        final Player player = (Player) view.getPlayer();
        final GOOPCPlayer rpgDeployed = GCL_Player.getInventoryFor(player);
        boolean nullContainer = (deployed == null);
        boolean nullRPGInven = (rpgDeployed == null);
        if (nullContainer && nullRPGInven) { return; }

        /*
         * Now we know it is a GooP Containers interaction.
         * Time to start cancelling for any suspicious reason.
         */
//        if (!GOOPCManager.isPremiumEnabled() && !player.isOp()) {
//
//            // Mention what is wrong
//            event.getView().getPlayer().sendMessage(OotilityCeption.LogFormat("Hey! Only OP players can use \u00a7e\u00a7l\u00a7oGooP Containers\u00a77 without the Premium Version!"));
//
//            // Cancel
//            event.setCancelled(true);
//            return;
//        }

        /*
         * Special Edition Modes: These wont support any of the containers special functions, as
         * they are meant to do their own things. Most actions are disabled and behave as usual
         * inventories.
         */
        if (GOOPCManager.isUsage_EditionCommands(view)) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Command Edition ~ No allowed actions");

            // No interactions with the container are supported
            event.setCancelled(true);
            return;

            // If it is either of the CONTENTS PHASES
        } else if (GOOPCManager.isUsage_EditionDisplay(view)) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Display Edition ~ All allowed actions");

            // All is allowed, and nothing special happens.
            return;

        } else if (GOOPCManager.isUsage_EditionStorage(view)) {

            // Cancel if any item is a container edge
            for (Map.Entry<Integer, ItemStack> affected : event.getNewItems().entrySet()) {

                if (OotilityCeption.IsEncrypted(affected.getValue(), GOOPCTemplate.EDGE_ENCRYPTION_CODE)) {
                    //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Usage Edition Drag ~ No edge interaction");
                    event.setCancelled(true); }

                //CLI// else { OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Usage Edition Drag ~ Allowed action"); }
            }

            // Nothing else happens
            return;
        }

        // That's not good
        final ContainerInventory observed = deployed != null ? deployed.getObservedBy(player.getUniqueId()) : null;
        if (observed == null && !nullContainer) {
            Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cContainers OnClick Error: \u00a76Invalid Observed Container");

            // Log this error
            event.setCancelled(true); return; }

        // Obtain the action. If not found, it is not supported.
        ContainersDragHandler handler = ContainersInteractionHandler.getHandlerFor(event.getType());
        if (handler == null) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Unsupported Drag\u00a7c " + event.getType().toString());
            event.setCancelled(true); return; }
        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Handling Via \u00a7a " + handler.getClass().getSimpleName());

        /*
         * Did it happen in the actual container inventory?
         * (Was any raw slot greater than the size of the top inventory)
         *
         * Cancels if at least one is within the top, as these
         * drag actions are not supported in Containers.
         */
        boolean confinedTop = event.getView().getType() != InventoryType.CRAFTING, confinedBot = true;
        int topSize = event.getView().getTopInventory().getSize();
        for (Integer rawSlot : event.getRawSlots()) {

            // If the click happened in the top, it is not confined to the bottom
            if (rawSlot < topSize) { confinedBot = false; }

            // If the click happened in the bottom, it is not confined to the top
            else { confinedTop = false; }
        }

        /*
         * It is not our business if the container was not clicked, and the inventory action affects
         * only one slot. Suppose though that it does affect multiple slots, then if it is not affecting
         * the top, we are clear.
         *
         * If its not affecting the container, its not my business.
         */
        if ((confinedBot && nullRPGInven) || (confinedTop && nullContainer)) {

            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Not business of GooP Containers (not affecting top inventory)");
            return; }

        // Template allows no drag
        if ((!nullContainer && !deployed.getTemplate().isAllowDragEvents())
            || (!nullRPGInven && !rpgDeployed.getTemplate().isAllowDragEvents())) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73LI\u00a77 Template\u00a7c disallows drag");

            // No more event
            event.setCancelled(true); return; }

        /*
         * Slow down the input processing
         *
         * Unfortunately, GOOP Containers agonizes with auto-clickers, must prevent.
         */
        String slowmodeUUID = nullContainer ? player.getName() : observed.getInstanceID();

        // Evaluate click cooldown
        Long current = System.currentTimeMillis();
        Long last = clickCooldowns.get(slowmodeUUID);
        if (last != null && (current - last) < 170) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73RS\u00a7c Coolbreak downed:\u00a7e " + (current - last));

            // This person is trying to glitch the container
            event.setCancelled(true); return;

        // Update to current cooldown
        } else { clickCooldowns.put(slowmodeUUID, current); }

        // Carry out the operation
        ContainersInteractionResult result = handler.handleContainersOperation(deployed, observed, rpgDeployed, player, event);

        // Build slots
        ArrayList<ItemStackSlot> providedSlots = new ArrayList<>();
        for (Integer rawSlot : event.getRawSlots()) {

            // On observed container
            if (rawSlot < topSize) {

                // If the container exists, check for edge slots
                if (!nullContainer) {

                    // Not edge slots!!
                    if (deployed.getTemplate().isEdgeSlot(rawSlot)) { continue; }
                }

                // If RPG Present, I guess it might decide to remove...
                else {

                    // Not supported
                    continue;
                }

                // Include
                providedSlots.add(new ISSObservedContainer(rawSlot, null, null));

            // Normal inventory, only count if the template extends it
            } else {

                // If the container exists, check for edge slots
                if (!nullRPGInven) {

                    // Not edge slots!!
                    if (rpgDeployed.getTemplate().isEdgeSlot(rawSlot)) { continue; }
                }

                // If RPG Present, I guess it might decide to remove...
                else {

                    // Not extensible
                    if (!deployed.getTemplate().isProvidedDragExtendsToInventory()) { continue; }
                }

                // Include adjusted for bottom inventory
                providedSlots.add(new ISSInventory(rawSlot - topSize, null));
            }
        }

        // Update slot
        GungingOotilities.setProvidedSlots(player.getUniqueId(), providedSlots);

        /*
         * The result operation will be null when the thing has finalized
         * without anything further to run or do. Like an edge being clicked.
         *
         * The handler is in charge of cancelling the event.
         */
        if (result == null) {
            //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a73RS\u00a77 Handling Finished, no result.");
            return; }

        // Express Cursor Edition
        if (result.isCursorUpdate()) { expressCursorEdition.put(player.getUniqueId(), result); }

        // Execute the actions next tick (so that the slot makes sense to a human dev).
        (new BukkitRunnable() {
            public void run() {
                boolean procEquipmentUpdate = false;

                // Save item changes
                if (!nullContainer) {
                    for (Integer slotUpdate : result.getSlotsUpdate()) {

                        // If personal, watch for equipment update.
                        if (Gunging_Ootilities_Plugin.foundMMOItems && deployed.getTemplate().isPersonal()
                                &&  deployed.getTemplate().getEquipmentSlots().contains(slotUpdate)) { procEquipmentUpdate = true; }

                        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Updating inventory slot \u00a7b#" + slotUpdate);
                        observed.updateSlot(slotUpdate); }
                }

                if (!nullRPGInven) {

                    // Save item changes
                    for (CIContainerInteracting slotUpdate : result.getPlayerSlotsUpdate()) {

                        // This will verify that the default item is shown
                        //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Refreshing player inventory slot \u00a7b#" + slotUpdate.getContainerSlot().getSlotNumber());
                        if (OotilityCeption.IsAirNullAllowed(slotUpdate.getCurrentItem())) { slotUpdate.setItem(slotUpdate.getCurrentItem()); }
                    }
                }

                // Equipment Update
                if (procEquipmentUpdate) { GooPMMOItems.UpdatePlayerEquipment(((PersonalContainerInventory) observed).getOwnerUUID()); }

                // Run commands
                for (String cmd : result.getCommands()) {
                    //CLI//OotilityCeption.Log("\u00a78GCLISTENER \u00a79RS\u00a77 Sending Command \u00a73" + cmd);

                    // Run commands, parse placeholders
                    OotilityCeption.SendConsoleCommand(GOOPCManager.parseAsContainers(cmd, observed), player, player, null, null);
                }

                // Cursor Update
                if (result.isCursorUpdate() && !result.isCursorUpdateResolved()) {
                    player.setItemOnCursor(result.getEditedCursor());
                    expressCursorEdition.remove(player.getUniqueId()); }
            }

        }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
    }
    @EventHandler
    public void OnInvenClose(InventoryCloseEvent event) {

        // Fast cancellation
        if (!(event.getView().getPlayer() instanceof Player)) { return; }

        // Identify
        InventoryView view = event.getView();
        GOOPCDeployed deployed = GOOPCManager.getContainer(view);
        Player player = (Player) view.getPlayer();
        if (deployed == null) { return; }

        /*
         * Special Edition Modes: These wont support any of the containers special functions, as
         * they are meant to do their own things. Most actions are disabled and behave as usual
         * inventories.
         */
        if (GOOPCManager.isUsage_EditionCommands(view)) {

            /*
             * If the player is looking at the edition commands
             * of the player inventory but not the special slots,
             * closing the config view of it opens the special slots.
             */
            if (deployed instanceof GOOPCPlayer && view.getTopInventory().getSize() < 50) {

                // Evaluate if closing next tick
                (new BukkitRunnable() {
                    public void run() {

                        // Open special slots view
                        GOOPCPlayer.previewContainerContents((GCT_PlayerTemplate) deployed.getTemplate(), player);
                    }
                }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
            }

            // Nothing else happens
            return;

            // If it is either of the CONTENTS PHASES
        } else if (GOOPCManager.isUsage_EditionDisplay(view)) {

            /*
             * Closing the display inventory when its a player inventory of special
             * slots causes the advance event via the player edition.
             *
             * Otherwise, it advances normally.
             */
            if (deployed instanceof GOOPCPlayer && view.getTopInventory().getSize() > 50) {

                // Finalize edition player-wise
                GOOPCPlayer.editionAdvance(player, (GCT_PlayerTemplate) deployed.getTemplate(), event.getInventory());
                return;
            }

            // Advance to next edition step
            GOOPCManager.editionAdvance(player, deployed.getTemplate(), event.getInventory());
            return;

        } else if (GOOPCManager.isUsage_EditionStorage(view)) {

            /*
             * Closing the storage inventory when its a player inventory of special
             * slots causes the finalize event via the player edition.
             *
             * Otherwise, it finalizes normally.
             */
            if (deployed instanceof GOOPCPlayer && view.getTopInventory().getSize() > 50) {

                // Finalize edition player-wise
                GOOPCPlayer.editionFinalize(player, (GCT_PlayerTemplate) deployed.getTemplate(), event.getInventory());
                return;
            }

            // Finalize edition normally
            GOOPCManager.editionFinalize(player, deployed.getTemplate(), event.getInventory());
            return;
        }

        /*
         * Now we know it is a GooP Containers interaction.
         * Time to start cancelling for any suspicious reason.
         */
//        if (!GOOPCManager.isPremiumEnabled() && !player.isOp()) {
//
//            // Mention what is wrong
//            event.getView().getPlayer().sendMessage(OotilityCeption.LogFormat("Hey! Only OP players can use \u00a7e\u00a7l\u00a7oGooP Containers\u00a77 without the Premium Version!"));
//            return; }

        // Is it closed already? Might cause Infinite Loops if the command run on close closes inventory..
        if (!deployed.isLooking(player.getUniqueId())) { return; }

        // Close for player
        deployed.closeForPlayer(player);

        ContainersInteractionResult rest = expressCursorEdition.get(player.getUniqueId());
        if (rest != null && !rest.isCursorUpdateResolved()) {

            // Force put
            player.setItemOnCursor(rest.getEditedCursor());
            rest.setCursorUpdateResolved(true);
            expressCursorEdition.remove(player.getUniqueId());

            //CUR// OotilityCeption.Log("\u00a78GCL \u00a7cCLS\u00a7e !\u00a77 Interrupted Cursor Update due to Closure");
            //CUR// OotilityCeption.Log("\u00a78GCL \u00a7cCLS\u00a7e !\u00a77 Current Cursor " + OotilityCeption.GetItemName(player.getItemOnCursor(), true));
            //CUR// OotilityCeption.Log("\u00a78GCL \u00a7cCLS\u00a7e !\u00a77 Edited Cursor " + OotilityCeption.GetItemName(rest.getEditedCursor(), true));
        }

        // If commands on close exist
        if (!deployed.getTemplate().hasCommandsOnClose()) { return; }

        // Run commands on close
        deployed.getTemplate().executeCommandsOnClose(player);
    }
    @EventHandler
    public void OnLogout(PlayerQuitEvent event) { GOOPCManager.unregisterEdition(event.getPlayer().getUniqueId()); }
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnPlayerBagOpen(PlayerInteractEvent event) {
        if (GOOPCManager.getLoadedPersonalNames().size() == 0) { return; }

        // MMOItems only
        if (!Gunging_Ootilities_Plugin.foundMMOItems) { return; }
        if (event.getHand() != EquipmentSlot.HAND) { return; }
        if (event.getPlayer().isSneaking()) { return; }
        if (event.getItem() == null) { return; }

        // What kinda action is it?
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            // Does it open a container?
            String container = GooPMMOItems.GetStringStatValue(event.getItem(), GooPMMOItems.CONTAINER, event.getPlayer(), false);

            // Existed?
            if (container != null) {

                // Get Container and ID
                String tCont = "";
                String tUID = "";

                // New vs Old
                if (container.contains(" ")) {

                    // Split
                    String[] split = container.split(" ");
                    if (split.length >= 1) { tCont = split[0]; if (split.length >= 2) { tUID = split[1]; } }

                } else {

                    // Assume it is
                    tCont = container;
                }

                // Get UUID?
                GOOPCPersonal actualContainer = GCL_Personal.getByInternalName(tCont);
                UUID actualUUID = OotilityCeption.UUIDFromString(tUID);

                // Found container?
                if (actualContainer != null) {

                    // Found uuid?
                    if (actualUUID == null) {

                        // Cancel if stack greater than 1
                        if (event.getItem().getAmount() > 1) {
                            GOOPCCommands.delayedMessage(event.getPlayer(), GTranslationManager.c(GTL_Containers.BAGS_FIRST_OPEN_STACK).replace("%container%", actualContainer.getTemplate().getTitle()).replace("%item%", OotilityCeption.GetItemName(event.getItem())), 12);
                            event.setCancelled(true);
                            return; }

                        // Generate New
                        actualUUID = UUID.randomUUID();

                        // Save
                        ItemStack res = GooPMMOItems.SetStringStatData(event.getItem(), GooPMMOItems.CONTAINER, container + " " + actualUUID.toString());

                        // Success?
                        if (res != null) {

                            // Set
                            event.getPlayer().getInventory().setItemInMainHand(res);
                        }
                    }

                    // Open for player!
                    actualContainer.openForPlayer(event.getPlayer(), actualUUID, ContainerOpeningReason.USAGE);
                    actualContainer.updateSeen(actualUUID, event.getPlayer().getUniqueId(), "Opened by \u00a73" + event.getPlayer().getName());
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnBagDrop(PlayerDropItemEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // MMOItems only
        if (!Gunging_Ootilities_Plugin.foundMMOItems) { return; }

        // Does it open a container?
        String container = GooPMMOItems.GetStringStatValue(OotilityCeption.FromDroppedItem(event.getItemDrop()), GooPMMOItems.CONTAINER, event.getPlayer(), false);

        // Existed?
        if (container != null) {

            // Get Container and ID
            String tCont = "";
            String tUID = "";

            // New vs Old
            if (container.contains(" ")) {

                // Split
                String[] split = container.split(" ");
                if (split.length >= 1) { tCont = split[0]; if (split.length >= 2) { tUID = split[1]; } }

            } else {

                // Assume it is
                tCont = container;
            }

            // Get UUID?
            GOOPCPersonal actualContainer = GCL_Personal.getByInternalName(tCont);
            UUID actualUUID = OotilityCeption.UUIDFromString(tUID);

            // Found container?
            if (actualContainer != null) {

                // Found uuid?
                if (actualUUID != null) {

                    // Open for player!
                    actualContainer.updateSeen(actualUUID, event.getPlayer().getUniqueId(), "Dropped by \u00a73" + event.getPlayer().getName());
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnBagPickup(EntityPickupItemEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // MMOItems only
        if (!Gunging_Ootilities_Plugin.foundMMOItems) { return; }
        if(!(event.getEntity() instanceof Player)) { return; }

        // Does it open a container?
        String container = GooPMMOItems.GetStringStatValue(OotilityCeption.FromDroppedItem(event.getItem()), GooPMMOItems.CONTAINER, (Player) event.getEntity(), false);

        // Existed?
        if (container != null) {

            // Get Container and ID
            String tCont = "";
            String tUID = "";

            // New vs Old
            if (container.contains(" ")) {

                // Split
                String[] split = container.split(" ");
                if (split.length >= 1) { tCont = split[0]; if (split.length >= 2) { tUID = split[1]; } }

            } else {

                // Assume it is
                tCont = container;
            }

            // Get UUID?
            GOOPCPersonal actualContainer = GCL_Personal.getByInternalName(tCont);
            UUID actualUUID = OotilityCeption.UUIDFromString(tUID);

            // Found container?
            if (actualContainer != null) {

                // Found uuid?
                if (actualUUID != null) {

                    // Open for player!
                    actualContainer.updateSeen(actualUUID, event.getEntity().getUniqueId(), "Picked up by \u00a73" + event.getEntity().getName());
                }
            }
        }
    }

    //region Breaking Containers
    @EventHandler(priority = EventPriority.HIGH)
    public void OnBlockBreak(BlockBreakEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(event.getBlock().getLocation());
        ArrayList<GPCContent> toDestroy = new ArrayList<>();

        for (GPCContent content : physicalFound) {

            // Can it be destroyed by environmental causes?
            if (content.canDestroy(event.getPlayer())) { toDestroy.add(content); continue; }

            // It is protected, cancel event
            event.setCancelled(true);
        }

        for (GPCContent content : toDestroy) { content.destroy(); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnExplosion(EntityExplodeEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        for (int i = 0; i < event.blockList().size(); i++) {
            Block block = event.blockList().get(i);

            // Find Inherentials
            ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(block.getLocation());
            ArrayList<GPCContent> toDestroy = new ArrayList<>();

            // Yeah this block may be protected...
            for (GPCContent content : physicalFound) {

                // Can it be destroyed by environmental causes?
                if (content.canDestroy(null)) { toDestroy.add(content); continue; }

                // Remove from effect, it is protected.
                event.blockList().remove(i);

                // Repeat index
                i--;
            }

            for (GPCContent content : toDestroy) { content.destroy(); }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnBlockFade(BlockFadeEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(event.getBlock().getLocation());
        ArrayList<GPCContent> toDestroy = new ArrayList<>();

        for (GPCContent content : physicalFound) {

            // Can it be destroyed by environmental causes?
            if (content.canDestroy(null)) { toDestroy.add(content); continue; }

            // It is protected, cancel event
            event.setCancelled(true);
        }

        for (GPCContent content : toDestroy) { content.destroy(); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnLeavesDecay(LeavesDecayEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(event.getBlock().getLocation());
        ArrayList<GPCContent> toDestroy = new ArrayList<>();

        for (GPCContent content : physicalFound) {

            // Can it be destroyed by environmental causes?
            if (content.canDestroy(null)) { toDestroy.add(content); continue; }

            // It is protected, cancel event
            event.setCancelled(true);
        }

        for (GPCContent content : toDestroy) { content.destroy(); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnExplode(BlockExplodeEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(event.getBlock().getLocation());
        ArrayList<GPCContent> toDestroy = new ArrayList<>();

        for (GPCContent content : physicalFound) {

            // Can it be destroyed by environmental causes?
            if (content.canDestroy(null)) { toDestroy.add(content); continue; }

            // It is protected, cancel event
            event.setCancelled(true);
        }

        for (GPCContent content : toDestroy) { content.destroy(); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockPhysics(BlockPhysicsEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(event.getBlock().getLocation());

        // Any found? disallow physics
        if (physicalFound.size() > 0) { event.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnBlockPlace(BlockPlaceEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(event.getBlock().getLocation());
        ArrayList<GPCContent> toDestroy = new ArrayList<>();

        for (GPCContent content : physicalFound) {

            // Can it be destroyed by environmental causes?
            if (content.canDestroy(null)) { toDestroy.add(content); continue; }

            // It is protected, cancel event
            event.setCancelled(true);
        }

        for (GPCContent content : toDestroy) { content.destroy(); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnLiquidFlow(BlockFromToEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        ArrayList<GPCContent> physicalFound = GCL_Physical.getContainersAt(event.getBlock().getRelative(event.getFace()).getLocation());
        ArrayList<GPCContent> toDestroy = new ArrayList<>();

        for (GPCContent content : physicalFound) {

            // Can it be destroyed by environmental causes?
            if (content.canDestroy(null)) { toDestroy.add(content); continue; }

            // It is protected, cancel event
            event.setCancelled(true);
        }

        for (GPCContent content : toDestroy) { content.destroy(); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPistonExtend(BlockPistonExtendEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        for (Block block : event.getBlocks()) {

            // Check which blocks would move (If shrubs was loaded)
            ArrayList<Block> realBlocks = new ArrayList<>();
            realBlocks.add(block);

            // Now lets suppose shrubs is loaded
            if (Gunging_Ootilities_Plugin.usingMMOItemShrubs) {

                // Adds to the real array of blocks all the blocks 15 in front, until another block of the array is met or air is found
                realBlocks.addAll(OotilityCeption.BlocksInFront(OotilityCeption.AdjustBisected(block), event.getDirection(), 15, event.getBlocks(), true)); }

            // Examine
            for (Block realBlock : realBlocks) {

                // Any container at that? Cancel
                if (GCL_Physical.getContainersAt(OotilityCeption.AdjustBisected(realBlock).getLocation()).size() > 0) { event.setCancelled(true); return; }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPistonRetract(BlockPistonRetractEvent event) {
        if (GOOPCManager.getLoadedPhysicalNames().size() == 0) { return; }

        // Early cancellation
        if (event.isCancelled()) { return; }

        // Check every block
        for (Block block : event.getBlocks()) {

            // Check which blocks would move (If shrubs was loaded)
            ArrayList<Block> realBlocks = new ArrayList<>();
            realBlocks.add(block);

            // Now lets suppose shrubs is loaded
            if (Gunging_Ootilities_Plugin.usingMMOItemShrubs) {

                // Adds to the real array of blocks all the blocks 15 in front, until another block of the array is met or air is found
                realBlocks.addAll(OotilityCeption.BlocksInFront(OotilityCeption.AdjustBisected(block), event.getDirection(), 15, event.getBlocks(), true)); }

            // Examine
            for (Block realBlock : realBlocks) {

                // Any container at that? Cancel
                if (GCL_Physical.getContainersAt(OotilityCeption.AdjustBisected(realBlock).getLocation()).size() > 0) { event.setCancelled(true); return; }
            }
        }
    }
    //endregion
}
