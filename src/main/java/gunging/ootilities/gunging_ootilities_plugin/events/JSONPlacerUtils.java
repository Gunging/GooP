package gunging.ootilities.gunging_ootilities_plugin.events;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class JSONPlacerUtils implements Listener {

    static boolean warnedJSON = false;
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnJSONBlockPlace(PlayerInteractEvent event) {

        if (warnedJSON) { return; }

        try {

            // If not cancelled, and no block is in hand (for such a case it will be managed in OnBlockPlace)
            if (!event.isCancelled()) {

                // Placing by Player Interact Event
                if (!event.isBlockInHand()) {

                    // What kinda action is it?
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                        // If non-null block
                        if (event.getClickedBlock() != null) {
                            //TST//OotilityCeption. Log("\u00a7aStep 3: \u00a77Block Placement Simulation");

                            // Is it JSON Furniture?
                            if (IsJSON_Furniture(event.getItem())) {
                                //TST//OotilityCeption. Log("\u00a7aStep 4: \u00a77Identified as JSON");

                                // Get its block location
                                Block truBlock = event.getClickedBlock().getRelative(event.getBlockFace());

                                // Cancel the event, this is a furniture block anyway
                                event.setCancelled(true);

                                // If it is air
                                if (OotilityCeption.IsAirNullAllowed(truBlock)) {
                                    //TST//OotilityCeption. Log("\u00a7aStep 5: \u00a77 Placed on Air");

                                    boolean success = SetOntoBlock(event.getPlayer(), truBlock, event.getItem(), ArmorStandCardinallyLookingAt(event.getPlayer()));

                                    // If got cancelled, probalby remove one?
                                    if (success && (event.getPlayer().getGameMode() != GameMode.CREATIVE)) {
                                        //TXR//OotilityCeption. Log("\u00a7aStep R");

                                        // Decrease
                                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                                    }
                                }
                            }
                        }
                    }
                }

                // Removing by persistent punching
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

                    // If non-null block
                    if (event.getClickedBlock() != null) {

                        // If it could be a JSON Furniture
                        if (event.getClickedBlock().getType() == Material.BARRIER) {

                            // To store reference
                            RefSimulator<ArmorStand> disp = new RefSimulator<>(null);

                            // Is it JSON Furniture?
                            if (IsJSON_Furniture(event.getClickedBlock(), disp)) {
                                //TST//OotilityCeption. Log("\u00a7dStep 1: \u00a77Identified as JSON Block");

                                // Is they in creative?
                                boolean breakingPoint;

                                // If in creative
                                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {

                                    // Get Sword
                                    ItemStack sord = event.getPlayer().getInventory().getItemInMainHand();

                                    // If not air
                                    if (!OotilityCeption.IsAirNullAllowed(sord)) {

                                        // If not sword
                                        if (!OotilityCeption.IsSword(sord.getType())) {

                                            // Break
                                            breakingPoint = true;
                                        } else {

                                            // Cancel Breakk
                                            breakingPoint = false;
                                        }

                                        // Its the fist
                                    } else {

                                        // Break
                                        breakingPoint = true;
                                    }

                                    // Not in creative, 4 clicks per second enoughh
                                } else {
                                    // Get or Create
                                    BlockHitRememberances bhr = BlockHitRememberances.GetOrCreate(event.getPlayer(), event.getClickedBlock());

                                    //TST//OotilityCeption. Log("\u00a78PPS: \u00a73" + bhr.GetPPS());
                                    //TST//OotilityCeption. Log("\u00a78PAmount: \u00a73" + bhr.GetTotalPunches());
                                    //TST//OotilityCeption. Log("\u00a78Age: \u00a73" + OotilityCeption.SecondsElapsedSince(bhr.originTimeReference, OptimizedTimeFormat.Current()));

                                    // Reset if older than idk 2 seconds?
                                    bhr.ResetIfAged(2);

                                    // Register as a punch
                                    bhr.Punch();

                                    // Should have hit it like 4 times in 2 seconds
                                    breakingPoint = bhr.PPSExceed(2) && bhr.MinimumPunches(4);

                                    //TST//OotilityCeption. Log("PPS: \u00a7b" + bhr.GetPPS());
                                    //TST//OotilityCeption. Log("PAmount: \u00a7b" + bhr.GetTotalPunches());
                                    //TST//OotilityCeption. Log("Age: \u00a7b" + OotilityCeption.SecondsElapsedSince(bhr.originTimeReference, OptimizedTimeFormat.Current()));
                                }

                                // Alr, I guess they is breaking the block.
                                if (breakingPoint) {

                                    // Do they have auth?
                                    BlockBreakEvent evM = new BlockBreakEvent(event.getClickedBlock(), event.getPlayer());
                                    Bukkit.getPluginManager().callEvent(evM);

                                    //TST//OotilityCeption. Log("Block Break Cancel: \u00a7c" + evM.isCancelled());
                                    // Proceed
                                    if (!evM.isCancelled()) {

                                        // Break Barrier
                                        event.getClickedBlock().setType(Material.AIR);

                                        // Get Armor Stand Item Stack
                                        ItemStack dropp = new ItemStack(disp.getValue().getItem(EquipmentSlot.HEAD));
                                        ItemStack trueDrop = null;

                                        // Kill the display
                                        disp.getValue().remove();

                                        // If not in creative
                                        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {

                                            // Could it be a MMOitem?
                                            if (Gunging_Ootilities_Plugin.foundMMOItems) {

                                                // Is it?
                                                if (GooPMMOItems.IsMMOItem(dropp)) {

                                                    // Get Type and Value Information
                                                    RefSimulator<String> mType = new RefSimulator<>(null), mID = new RefSimulator<>(null);
                                                    GooPMMOItems.GetMMOItemInternals(dropp, mType, mID);

                                                    // Drop is the resultant
                                                    trueDrop = GooPMMOItems.GetMMOItemOrDefault(mType.getValue(), mID.GetValue());

                                                // Drop thay
                                                } else {

                                                    // Drop as-is
                                                    trueDrop = dropp;
                                                }

                                            } else {

                                                // Drop as-is
                                                trueDrop = dropp;
                                            }

                                            // If ended up existing
                                            if (!OotilityCeption.IsAirNullAllowed(trueDrop)) {

                                                // Drop in thay place
                                                event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), trueDrop);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        // Ay
        } catch (NoSuchMethodError e) {

            warnedJSON = true;

            Gunging_Ootilities_Plugin.theOots.CLog("JSON Furniture for non-block items is only available on Paper Spigot.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnBlockPlace(BlockPlaceEvent event) {

        // If not cancelled
        if (!event.isCancelled() && event.getPlayer().getGameMode() != GameMode.ADVENTURE) {

            // Is it JSON Furniture?
            if (IsJSON_Furniture(event.getItemInHand())) {

                // Cancel the event, thats how it works.
                event.setCancelled(true);

                (new BukkitRunnable() {
                    public void run() {

                        // Replace block
                        boolean success = SetOntoBlock(event.getPlayer(), event.getBlock(), event.getItemInHand(), ArmorStandCardinallyLookingAt(event.getPlayer()));

                        //TXR//OotilityCeption. Log("Success: \u00a7b" + success);
                        //TXR//OotilityCeption. Log("Creativity: \u00a7e" + (event.getPlayer().getGameMode() != GameMode.CREATIVE));

                        // Remove one from the held item if it was placed
                        if (success && (event.getPlayer().getGameMode() != GameMode.CREATIVE)) {
                            //TXR//OotilityCeption. Log("\u00a7aStep R");

                            // Remove 1 from amount
                            event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
                        }

                    }

                }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
            }
        }
    }

    /**
     * Will orient the armor stand along X or Z axis, looking at the player in the best of its ability while mantaining this orientation. Assumes they are in the same world.
     */
    public static Orientations ArmorStandCardinallyLookingAt(Player someone) {

        // Where are the player looking at?
        switch (someone.getFacing()) {
            case EAST_NORTH_EAST:
            case EAST_SOUTH_EAST:
            case EAST: return Orientations.WestForward;
            case WEST_NORTH_WEST:
            case WEST_SOUTH_WEST:
            case WEST: return Orientations.EastForward;
            case SOUTH_EAST:
            case SOUTH_SOUTH_EAST:
            case SOUTH_SOUTH_WEST:
            case SOUTH: return Orientations.NorthForward;
            default: return Orientations.SouthForward;
        }
    }

    /**
     * Will return false if it fails for any reason. <p>
     * <p></p>
     * Success means that the player asAuth can build there, and that 'there' exists and the item also exists.
     * <p>Build check bypassed if asAuth is null.
     * <p></p>
     * As part of the success, the item is placed in the head of an armor stand, and the block becomes a barrier block.
     */
    public static boolean SetOntoBlock(@Nullable Player asAuth, @Nullable Block iBlock, @Nullable ItemStack iSource, @NotNull Orientations armorstandForward) {

        // No null yo
        if (iBlock == null) { return false; }
        if (iSource == null) { return false; }

        // Unlink from origin and set amount to 1
        iSource = OotilityCeption.SetAmount(new ItemStack(iSource), new PlusMinusPercent(1.0, false, false), null, null);

        // Success?
        boolean success = (asAuth == null);

        // Attempt to break through event
        if (!success) {

            if (OotilityCeption.IsAir(iBlock.getType())) {

                // Place through event
                BlockPlaceEvent evP = new BlockPlaceEvent(iBlock, iBlock.getState(), iBlock, new ItemStack(Material.STONE), asAuth, true, EquipmentSlot.HAND);
                Bukkit.getPluginManager().callEvent(evP);

                //TXR//OotilityCeption. Log("Block \u00a7fPlace Cancel: \u00a7c" + evP.isCancelled());
                success = !evP.isCancelled();

            } else {

                // Break through event
                BlockBreakEvent evM = new BlockBreakEvent(iBlock, asAuth);
                Bukkit.getPluginManager().callEvent(evM);

                //TXR//OotilityCeption. Log("Block Break Cancel: \u00a7c" + evM.isCancelled());
                success = !evM.isCancelled();
            }
        }

        // Entities blocking the way?
        if (success) {

            // False if there is an entity standing on thay block lol
            for (Entity ent : OotilityCeption.EntitiesNearLocation(OotilityCeption.New(iBlock.getLocation()).add(0.5, 0.5, 0.5), 0.5)) {

                // If armour stand
                if (ent instanceof ArmorStand) {

                    // If not market
                    if (!((ArmorStand) ent).isMarker()) {

                        success = false;
                    }

                // Its not an armor stand nor item frame
                } else if (!(ent instanceof ItemFrame)) {

                    // Disallow
                    success = false;
                }
            }
        }

        // Block becomes of BARRIER type
        if (success) {

            // Break Naturally
            if (OotilityCeption.IsAir(iBlock.getType())) { iBlock.breakNaturally(); }

            // Set into a BARRIER
            iBlock.setType(Material.BARRIER);

            Location targetLocation = GetDisplayLocation(iBlock);

            // Set Look Rotation
            switch (armorstandForward) {
                case SouthForward:
                    targetLocation.setYaw(0);
                    break;
                case WestForward:
                    targetLocation.setYaw(90);
                    break;
                case NorthForward:
                    targetLocation.setYaw(180);
                    break;
                case EastForward:
                    targetLocation.setYaw(270);
                    break;
            }

            // Generates a new one and saves it and BLEH
            ArmorStand display = (ArmorStand) iBlock.getWorld().spawnEntity(GetDisplayLocation(iBlock), EntityType.ARMOR_STAND);

            // Telport wth
            display.teleport(targetLocation);

            // Sets all base meta data
            display.setInvulnerable(true);
            display.setMarker(true);
            display.setVisible(false);
            display.setGravity(false);
            display.setSilent(true);
            display.addScoreboardTag("GooP_JSON");

            // Set Helm
            display.setItem(EquipmentSlot.HEAD, iSource);

            // Success
            return true;
        }

        // lmao couldnt destroy it, thats a fat L I guess
        return false;
    }

    // Location
    public static Location GetDisplayLocation(Block iBlock) { return new Location(iBlock.getWorld(), locDifference.getX(), locDifference.getY(), locDifference.getZ()).add(iBlock.getLocation()); }
    public static Location ReverseDisplayLocation(Entity iEntity) { return new Location(iEntity.getWorld(), -locDifference.getX(), -locDifference.getY(), -locDifference.getZ()).add(iEntity.getLocation()); }
    static final Location locDifference = new Location(null, 0.5D, 0.01D, 0.5D);

    /**
     * Will only return true if presented with an item that is JSON Furniture-linked
     */
    public static boolean IsJSON_Furniture(ItemStack iSource) {
        // Bruh no pre-1.14
        if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0) { return false; }

        // If valid
        CustomModelDataLink cmdLink = CustomModelDataLink.getFrom(iSource);

        // Was there?
        if (cmdLink != null) {

            // Is it AS JSON?
            return cmdLink.HasReason(CMD_Link_Reasons.AsJSON_Furniture);
        }

        // Nope
        return false;
    }

    /**
     * Will only return true if presented with a block containing an item that is JSON Furniture-linked
     * <p>
     * Will also destroy extraneous entities
     */
    public static boolean IsJSON_Furniture(Block iSource, RefSimulator<ArmorStand> display) {
        // Bruh no pre-1.14
        if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0) {
            return false;
        }

        // Get Entities within chunk
        ArrayList<ArmorStand> cEntities = new ArrayList<>();

        // Include if armor stand with tag
        for (Entity ent : OotilityCeption.EntitiesNearLocation(GetDisplayLocation(iSource), 0.1)) {

            // Is Armor Stand?
            if (ent instanceof ArmorStand) {

                // Has tag?
                if (ent.getScoreboardTags().contains("GooP_JSON")) {

                    // Armor Stand Tiem
                    cEntities.add((ArmorStand)ent);
                }
            }
        }

        // There should only be one
        if (cEntities.size() > 0) {

            // AH
            if (cEntities.size() > 1) {

                // Remove all except the very first
                for (int c = 1; c < cEntities.size(); c++) {

                    // Remove from world
                    cEntities.get(c).remove();

                    // Remove from lizt
                    cEntities.remove(c);

                    // Repeat Index
                    c--;
                }
            }

            // Should it NOT be?
            if (iSource.getType() == Material.BARRIER) {

                // Set reference
                if (display != null) { display.setValue(cEntities.get(0)); }

                // Return success
                return true;

            // NOT A BARRIER, MUST DESTROY
            } else {

                // DELEET
                cEntities.get(0).remove();
            }
        }

        // Nope
        return false;
    }

    @EventHandler
    public void OnChunkLoad(ChunkLoadEvent event) {

        // Find armor stands with tag
        for (Entity ent : event.getChunk().getEntities()) {

            // Is Armor Stand?
            if (ent instanceof ArmorStand) {

                // Has tag?
                if (ent.getScoreboardTags().contains("GooP_JSON")) {

                    // Check
                    IsJSON_Furniture(ent.getWorld().getBlockAt(ReverseDisplayLocation(ent)), null);
                }
            }
        }
    }

    //region Ah yes much JSON placer --- ANVIL TAG RENAMING (vanilla)

    public static boolean oGrabby = false;
    static ArrayList<InventoryView> ogViewii = new ArrayList<>();
    @EventHandler
    public void OnAnvilRepair(InventoryClickEvent event) {

        // MMOItems method overrides
        if (!Gunging_Ootilities_Plugin.anvilRenameEnabled) { return; }
        if (!(event.getInventory() instanceof AnvilInventory)) { return; }
        if (!(event.getWhoClicked() instanceof Player)) { return; }
        Player player = (Player) event.getWhoClicked();

        // Get thay
        ItemStack iResult = event.getCurrentItem();

        // If they clicked the result slotte
        if (event.getRawSlot() == 2 && !OotilityCeption.IsAirNullAllowed(iResult) && OotilityCeption.IsAirNullAllowed(event.getCursor())) {

            // Revalidate name
            NameVariable oTextii = iTextii.get(player.getUniqueId());

            if (oTextii != null) {

                // Git lore
                ArrayList<String> oLoreii = iTextiiLinq.get(player.getUniqueId());

                // Get text
                ItemMeta iMeta = iResult.getItemMeta();

                // Return to Meta
                iMeta.setDisplayName(oTextii.getValue());

                // Actually Rename
                iResult.setItemMeta(iMeta);
                iResult = OotilityCeption.SetLore(iResult, oLoreii);

                // Set
                event.setCurrentItem(iResult);

                // Only possible if MMOItems enabled
                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    // Register for evaluation
                    ogViewii.add(event.getView());

                    // Ay
                    if (!oGrabby) {

                        // Yes
                        oGrabby = true;

                        // Alter
                        (new BukkitRunnable() {
                            public void run() {

                                // No
                                oGrabby = false;

                                for (InventoryView uid : ogViewii) {

                                    // Get Inventory
                                    Inventory inven = uid.getTopInventory();
                                    HumanEntity holder = uid.getPlayer();

                                    // So they clicked the result and is still there no0v
                                    ItemStack iCombinee = inven.getItem(1);
                                    ItemStack iResult = inven.getItem(2);

                                    //  Still There
                                    if (!OotilityCeption.IsAirNullAllowed(iResult)) {

                                        // Get any var target in the combinee
                                        String varTarget = GooPMMOItems.GetStringStatValue(iCombinee, GooPMMOItems.REVARIABLE, (Player) holder, true);

                                        // Had it a variable?
                                        if (varTarget != null) {

                                            //  Force to Pick Up
                                            holder.setItemOnCursor(iResult.clone());
                                            inven.setItem(2, null);
                                            inven.setItem(0, null);

                                            // Is that B a consumable lma0
                                            if (GooPMMOItems.IsConsumable(iCombinee, (Player) holder)) {

                                                // Consume I
                                                inven.setItem(1, OotilityCeption.asQuantity(iCombinee,iCombinee.getAmount() - 1));
                                            }
                                        }
                                    }

                                }

                                // Klear
                                ogViewii.clear();

                                // Applied
                                //NEK//OotilityCeption. Log("\u00a72Rref:\u00a7f " + OotilityCeption.GetItemName(inv.getItem(2)));
                            }

                        }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 2L);
                    }
                }
            }

        }
    }
    boolean invenstrtrd = false;
    HashMap<UUID, ItemStack> iResultii = new HashMap<>();
    HashMap<UUID, NameVariable> iTextii = new HashMap<>();
    HashMap<UUID, ArrayList<String>> iTextiiLinq = new HashMap<>();
    HashMap<UUID, String> iVarii = new HashMap<>();
    HashMap<UUID, AnvilInventory> iInvenii = new HashMap<>();
    @EventHandler(priority = EventPriority.HIGH)
    public void OnAnvilReprep(PrepareResultEvent event) {

        // MMOItems method overrides
        if (!Gunging_Ootilities_Plugin.anvilRenameEnabled) { return; }
        if (!(event.getInventory() instanceof AnvilInventory)) { return; }
        if (!(event.getView().getPlayer() instanceof Player)) { return; }
        Player player = (Player) event.getView().getPlayer();

        // Get as Anvil Inventor
        AnvilInventory inv = (AnvilInventory) event.getInventory();

        // Must hab item
        ItemStack iSource = inv.getItem(0);
        ItemStack iCombinee = inv.getItem(1);
        ItemStack iResult = inv.getItem(2);
        //region String text = inv.getRenameText();
        String text = inv.getRenameText();
        if (text == null) { text = ""; }
        boolean atGat = text.contains("øg");
        text = text.replace("@", "<&at>").replace("%", "<&pc>");

        //NEK//String eSource = " \u00a7b-", eCombinee = " \u00a7b-", eResult = " \u00a7b-";
        //NEK//if (!OotilityCeption.IsAirNullAllowed(iSource)) { eSource = " \u00a7b" + OotilityCeption.GetEnchLevel(iSource, Enchantment.DURABILITY); }
        //NEK//if (!OotilityCeption.IsAirNullAllowed(iCombinee)) { eCombinee = " \u00a7b" + OotilityCeption.GetEnchLevel(iCombinee, Enchantment.DURABILITY); }
        //NEK//if (!OotilityCeption.IsAirNullAllowed(iResult)) { eResult = " \u00a7b" + OotilityCeption.GetEnchLevel(iResult, Enchantment.DURABILITY); }
        //NEK//OotilityCeption. Log("\u00a77Prep \u00a73" + player.getName() + "\u00a77:");
        //NEK//OotilityCeption. Log("\u00a73    RText>\u00a7f   " + text);
        //NEK//OotilityCeption. Log("\u00a73    ISour>\u00a7f   " + OotilityCeption.GetItemName(iSource) + eSource);
        //NEK//OotilityCeption. Log("\u00a73    IComb>\u00a7f   " + OotilityCeption.GetItemName(iCombinee) + eCombinee);
        //NEK//OotilityCeption. Log("\u00a73    IResu>\u00a7f   " + OotilityCeption.GetItemName(iResult) + eResult);
        //endregion

        // No need to continue if nulla
        if (OotilityCeption.IsAirNullAllowed(iSource)) {

            // If either source or result are null, clear operations.
            //NEK//OotilityCeption. Log("\u00a7c (null cancellation) ");
            return;
        }

        // Players cannot write section signs in-game. It must be jinxed.
        if (atGat || text.contains("\u00a7")) {

            // iResult must exist
            if (!OotilityCeption.IsAirNullAllowed(iResult)) {

                // Get item meta
                ItemMeta iMeta = iResult.getItemMeta();

                // Get Source Name
                text = OotilityCeption.GetItemName(iSource);

                // Return to Meta
                iMeta.setDisplayName(text);

                // Actually Rename
                iResult.setItemMeta(iMeta);
                //NEK//OotilityCeption. Log("\u00a7aFinished");
                //NEK//OotilityCeption. Log("\u00a72 >\u00a77 " + text);
            }

        // So its something actually written by the player
        } else {

            // Get any var target in the combinee
            String varTarget = null;
            if (Gunging_Ootilities_Plugin.foundMMOItems) { varTarget = GooPMMOItems.GetStringStatValue(iCombinee, GooPMMOItems.REVARIABLE, player, false); }

            // Suppose the var target exists, but there is no result.
            if (OotilityCeption.IsAirNullAllowed(iResult) && varTarget != null) {

                // The result shall become a klone of the source
                iResult = iSource.clone();
            }

            // If result exists
            if (!OotilityCeption.IsAirNullAllowed(iResult)) {

                // Get item meta
                ItemMeta iMeta = iResult.getItemMeta();

                // Get Variable
                String varTTarget = "name";
                if (varTarget != null) {

                    // Remember thay
                    varTTarget = varTarget;

                    // Consume
                    //if (GooPMMOItems.IsConsumable(iCombinee, player)) { inv.getItem(1).setAmount(iCombinee.getAmount() - 1); }
                }

                // Compare to olden vars
                String oVarii = iVarii.get(player.getUniqueId());
                NameVariable oTextii = iTextii.get(player.getUniqueId());
                String finalName = null;
                ArrayList<String> finalLore = null;

                // Is it the smae
                if (oVarii != null && oTextii != null) {

                    // Get previous text
                    String oText = oTextii.getIdentifier();

                    if (varTTarget.equals(oVarii) && text.equals(oText)) {

                        // Uh yes no change; Get the previous operation result
                        finalName = oTextii.getValue();
                        finalLore = iTextiiLinq.get(player.getUniqueId());
                    }
                }

                // Was there any change
                if (finalName == null) {
                    // Actual Text
                    String acText = text;

                    // Color Text if Permission
                    if (!OotilityCeption.hasPermission(player, "anvil", "colors")) { acText = text.replace("&", "<~and>"); }

                    // Gib Operation
                    NameVariableOperation nvo = new NameVariableOperation(new NameVariable(varTTarget, acText));
                    finalName = OotilityCeption.RerenameNameVarialbes(player, nvo, OotilityCeption.GetItemName(iSource), null, iSource);

                    // Fix lore :b:erhaps
                    iResult = OotilityCeption.RevariabilizeLore(iResult, nvo, player);
                    finalLore = OotilityCeption.GetLore(iResult);
                }

                // Its a 'success,' store variables
                iVarii.put(player.getUniqueId(), varTTarget);
                iTextii.put(player.getUniqueId(), new NameVariable(text, finalName));
                iTextiiLinq.put(player.getUniqueId(), OotilityCeption.GetLore(iResult));

                // Return to Meta
                iMeta.setDisplayName(finalName);

                // Actually Rename
                iResult.setItemMeta(iMeta);
                iResult = OotilityCeption.SetLore(iResult, finalLore);
                //NEK//OotilityCeption. Log("\u00a7aFinished");
                //NEK//OotilityCeption. Log("\u00a72 >\u00a77 " + finalName);
            }

        } //*/

        // If success
        if (!OotilityCeption.IsAirNullAllowed(iResult)) {

            // Add
            iInvenii.put(player.getUniqueId(), inv);
            iResultii.put(player.getUniqueId(), iResult);

            // Ay
            if (!invenstrtrd) {

                // Yes
                invenstrtrd = true;

                // Alter
                (new BukkitRunnable() {
                    public void run() {

                        // No
                        invenstrtrd = false;

                        for (UUID uid : iResultii.keySet()) {

                            // Get Inven
                            AnvilInventory inven = iInvenii.get(uid);
                            ItemStack iRes = iResultii.get(uid);

                            // Still something in source right
                            if (!OotilityCeption.IsAirNullAllowed(inv.getItem(0))) {

                                // Apply
                                if (inven != null && iRes != null) {

                                    // Ah yes set result
                                    inven.setItem(2, iRes);
                                }
                            }
                        }

                        // Klear
                        iInvenii.clear();
                        iResultii.clear();

                        // Applied
                        //NEK//OotilityCeption. Log("\u00a72Rref:\u00a7f " + OotilityCeption.GetItemName(inv.getItem(2)));
                    }

                }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 2L);
            }
        }
    }
    //endregion
}
