package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ScoreboardLinks implements Listener {

    public static ArrayList<LinkedEntity> olEntities = new ArrayList<>();
    public static ArrayList<UUID> watchedEntities = new ArrayList<>();
    static int hits = 30;


    public static void watchNoDupe(@NotNull UUID uid) {

        // Removes if it existed
        watchedEntities.remove(uid);

        // Includes
        watchedEntities.add(uid);
    }
    public static void linkNoDupe(@NotNull LinkedEntity len) {

        ArrayList<LinkedEntity> newEntities = new ArrayList<>();
        for (LinkedEntity ent : olEntities) {
            if (ent == null) { continue; }

            // Include if not the same uuid
            if (!ent.getEntityUUID().equals(len.getEntityUUID())) {

                newEntities.add(len);
            }
        }

        // Include those
        newEntities.add(len);
        olEntities = newEntities;
    }

    public ScoreboardLinks() { }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnEntityDeath(EntityDeathEvent event) {

        if (olEntities.size() == 0 || event.isCancelled()) { return; }
        if (!watchedEntities.contains(event.getEntity().getUniqueId())) { return; }

        // L
        GooPUnlockables uck = GooPUnlockables.From(event.getEntity().getUniqueId(), EntityLinkedEntity.transferLinkReceiver);
        uck.Lock();
    }

    @NotNull static ArrayList<UUID> counterproc = new ArrayList<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnPlayerDealDamage(EntityDamageByEntityEvent event) {

        //EVENT//OotilityCeption.Log("Cause \u00a7e- \u00a73" + event.getCause().toString());
        //EVENT//OotilityCeption.Log("Original (ABSORPTION) \u00a7e- \u00a73" + event.getOriginalDamage(EntityDamageEvent.DamageModifier.ABSORPTION));
        //EVENT//OotilityCeption.Log("Original (ARMOR) \u00a7e- \u00a73" + event.getOriginalDamage(EntityDamageEvent.DamageModifier.ARMOR));
        //EVENT//OotilityCeption.Log("Original (BASE) \u00a7e- \u00a73" + event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE));
        //EVENT//OotilityCeption.Log("Original (BLOCKING) \u00a7e- \u00a73" + event.getOriginalDamage(EntityDamageEvent.DamageModifier.BLOCKING));
        //EVENT//OotilityCeption.Log("Original (HARD_HAT) \u00a7e- \u00a73" + event.getOriginalDamage(EntityDamageEvent.DamageModifier.HARD_HAT));
        //EVENT//OotilityCeption.Log("Original (MAGIC) \u00a7e- \u00a73" + event.getOriginalDamage(EntityDamageEvent.DamageModifier.MAGIC));
        //EVENT//OotilityCeption.Log("Original (RESISTANCE) \u00a7e- \u00a73" + event.getOriginalDamage(EntityDamageEvent.DamageModifier.RESISTANCE));

        // Who was the true damager?
        Entity trueDamager = event.getDamager();

        //region Get True Damager
        if (event.getDamager() instanceof Projectile) {

            // If shooter is not null
            Projectile arrow = (Projectile) trueDamager;
            if (arrow.getShooter() instanceof Entity) {

                // Real damager is the one who fired this
                trueDamager = (Entity) arrow.getShooter();
            }
        }
        if (event.getDamager() instanceof Firework) {

            // If shooter is not null
            Firework arrow = (Firework) event.getDamager();
            if (XBow_Rockets.fireworkSources.containsKey(arrow.getUniqueId())) {

                // Real damager is the one who fired this
                trueDamager = XBow_Rockets.fireworkSources.get(arrow.getUniqueId());
            }
        }
        //endregion

        // Funny MMOItems plugin thing
        if (trueDamager instanceof Player && Gunging_Ootilities_Plugin.foundMMOItems) {

            // Any skill of such?
            ArrayList<String> mmOHs = GooPMMOItems.CummaltiveEquipmentStringStatValue((Player) trueDamager, GooPMMOItems.ONHIT_COMMAND);
            ArrayList<String> mmOKs = GooPMMOItems.CummaltiveEquipmentStringStatValue((Player) trueDamager, GooPMMOItems.ONKILL_COMMAND);

            // Funny variables
            Location loc = event.getEntity().getLocation();
            double distance = loc.distance(trueDamager.getLocation());
            double actualDamage = event.getFinalDamage(); boolean gracia = false;
            double helth = ((LivingEntity) event.getEntity()).getHealth();
            if (helth < actualDamage) { actualDamage = helth; gracia = true; }

            // Translate all of them
            ArrayList<String> ready = new ArrayList<>();

            // Golpe de Gracia
            if (gracia) {

                // Use the On Kills
                for (String mm : mmOKs) {

                    // Skip that
                    if (mm == null) { continue; }

                    // Parse
                    mm = OotilityCeption.ParseConsoleCommand(mm, trueDamager, (Player) trueDamager, null, null);

                    // Additional parses
                    mm = mm.replace("%victim_uuid%", event.getEntity().getUniqueId().toString());
                    mm = mm.replace("%victim%", event.getEntity().getUniqueId().toString());
                    mm = mm.replace("%victim_name%", event.getEntity().getName());
                    mm = mm.replace("%victim_world%", loc.getWorld().getName());
                    mm = mm.replace("%victim_x%", String.valueOf(loc.getX()));
                    mm = mm.replace("%victim_y%", String.valueOf(loc.getY()));
                    mm = mm.replace("%victim_z%", String.valueOf(loc.getZ()));
                    mm = mm.replace("%projectile_uuid%", event.getDamager().getUniqueId().toString());
                    mm = mm.replace("%projectile%", event.getDamager().getUniqueId().toString());
                    mm = mm.replace("%projectile_name%", event.getDamager().getName());
                    mm = mm.replace("%distance%", String.valueOf(distance));
                    mm = mm.replace("%damage_dealt%", String.valueOf(actualDamage));

                    // Prepared
                    ready.add(mm);
                }

                // Alr send those
                OotilityCeption.SendConsoleCommands(ready);

            // Skip if the damager is blacklisted
            } else if (!counterproc.contains(trueDamager.getUniqueId())) {

                // Add true damager uuid uuuh
                counterproc.add(trueDamager.getUniqueId());

                // Use the On Hits
                for (String mm : mmOHs) {

                    // Skip that
                    if (mm == null) { continue; }

                    // Parse
                    mm = OotilityCeption.ParseConsoleCommand(mm, trueDamager, (Player) trueDamager, null, null);

                    // Additional parses
                    mm = mm.replace("%victim_uuid%", event.getEntity().getUniqueId().toString());
                    mm = mm.replace("%victim%", event.getEntity().getUniqueId().toString());
                    mm = mm.replace("%victim_name%", event.getEntity().getName());
                    mm = mm.replace("%victim_world%", loc.getWorld().getName());
                    mm = mm.replace("%victim_x%", String.valueOf(loc.getX()));
                    mm = mm.replace("%victim_y%", String.valueOf(loc.getY()));
                    mm = mm.replace("%victim_z%", String.valueOf(loc.getZ()));
                    mm = mm.replace("%projectile_uuid%", event.getDamager().getUniqueId().toString());
                    mm = mm.replace("%projectile%", event.getDamager().getUniqueId().toString());
                    mm = mm.replace("%projectile_name%", event.getDamager().getName());
                    mm = mm.replace("%distance%", String.valueOf(distance));
                    mm = mm.replace("%damage_dealt%", String.valueOf(actualDamage));

                    // Prepared
                    ready.add(mm);
                }

                // Alr send those
                OotilityCeption.SendConsoleCommands(ready);

                // Release from blacklist
                counterproc.remove(trueDamager.getUniqueId());
            }
        }

        // If successful
        if (!event.isCancelled() && event.getEntity() instanceof LivingEntity) {

            // Somewhat an arbitrary number but eh
            hits--;
            if (hits < 1) {
                hits = 30;
                CheckLinks(); }

            // Evaluate every linked entity
            for (LinkedEntity linkedEntity : olEntities) {

                // If observed entity is of damage taken
                if (linkedEntity.getReason() == ObjectiveLinks.DamageTakenLink || linkedEntity.getReason() == ObjectiveLinks.DamageTransferLink) {

                    // Does it match the UUID?
                    if (linkedEntity.getEntityUUID().equals(event.getEntity().getUniqueId())) {

                        boolean receiverValid = true;
                        if (linkedEntity instanceof EntityLinkedEntity) {
                            /*DMG*/OotilityCeption.Log("\u00a7e------------------Receiver-----------------");
                            /*DMG*/OotilityCeption.Log("\u00a77Living: \u00a72" + (((EntityLinkedEntity) linkedEntity).getReceiver() instanceof LivingEntity));
                            /*DMG*/OotilityCeption.Log("\u00a77Valid: \u00a73" + ((EntityLinkedEntity) linkedEntity).isReceiverValid());
                            /*DMG*/OotilityCeption.Log("\u00a77UUID: \u00a76" + ((EntityLinkedEntity) linkedEntity).getReceiverUUID());

                            // Only living entity supported for these
                            receiverValid = (((EntityLinkedEntity) linkedEntity).getReceiver() instanceof LivingEntity) && ((EntityLinkedEntity) linkedEntity).isReceiverValid(); }

                        // Skip if the entity has died
                        if (!linkedEntity.getEntity().isValid() || !receiverValid) {

                            /*DMG*/OotilityCeption.Log("\u00a7c------------------Invalid-----------------");
                            /*DMG*/OotilityCeption.Log("\u00a77Receiver: \u00a76" + receiverValid);
                            /*DMG*/OotilityCeption.Log("\u00a77Self: \u00a76" + linkedEntity.getEntity().isValid() + " \u00a78(" + linkedEntity.getEntityUUID() + "\u00a78)");
                            continue; }

                        // Was any damage actually dealt?
                        if (event.getDamage() > 0) {

                            double actualDamage = event.getFinalDamage();
                            double helth = ((LivingEntity) event.getEntity()).getHealth();
                            if (helth <= actualDamage) { actualDamage = helth; }

                            // As objective link
                            if (linkedEntity instanceof ObjectiveLinkedEntity) {
                                ((ObjectiveLinkedEntity) linkedEntity).ApplyLink(trueDamager, (int) Math.round(actualDamage * 10)); }

                            if (linkedEntity instanceof DamageTransferLink) {

                                // Yes
                                double transferredDamage = ((DamageTransferLink) linkedEntity).getTransfer() * actualDamage;

                                double baseDamage = event.getDamage();
                                double finalDamage = event.getFinalDamage();
                                double extraSources = finalDamage - baseDamage;
                                double finalEventDamage = (1 - ((DamageTransferLink) linkedEntity).getPrevent()) * finalDamage;

                                // Remove extra sources sources
                                double finalEventBase = finalEventDamage - extraSources;

                                // Thats the new base
                                event.setDamage(finalEventBase);

                                /*DMG*/OotilityCeption.Log("\u00a7e------------------------------------------");
                                /*DMG*/OotilityCeption.Log("Entity Damage Dealt \u00a7e- \u00a73" + event.getEntity().getName());
                                /*DMG*/OotilityCeption.Log("Damage \u00a7e- \u00a7b" + baseDamage);
                                /*DMG*/OotilityCeption.Log("Final Damage \u00a7e- \u00a7b" + finalDamage);
                                /*DMG*/OotilityCeption.Log("Expected Damage \u00a7e- \u00a73" + finalEventDamage + "\u00a78 (\u00a77" +  (1 - ((DamageTransferLink) linkedEntity).getPrevent()) + "x\u00a78)");
                                /*DMG*/OotilityCeption.Log("Extra Sources \u00a7e- \u00a7b" + extraSources);
                                /*DMG*/OotilityCeption.Log("Final Base \u00a7e- \u00a73" + finalEventBase);
                                /*DMG*/OotilityCeption.Log("Actual Final Base \u00a7e- \u00a73" + event.getDamage());
                                /*DMG*/OotilityCeption.Log("Actual Final Damage \u00a7e- \u00a73" + event.getFinalDamage());
                                /*DMG*/OotilityCeption.Log("Life-Limited Damage \u00a7e- \u00a73" + actualDamage);
                                /*DMG*/OotilityCeption.Log("Transferred Damage \u00a7e- \u00a7d" + transferredDamage + "\u00a78 (\u00a77" +  ((DamageTransferLink) linkedEntity).getTransfer() + "x\u00a78)");


                                if (!((DamageTransferLink) linkedEntity).isSilent()) {

                                    // Fire event
                                    EntityDamageByEntityEvent loudness = new EntityDamageByEntityEvent(event.getDamager(), ((DamageTransferLink) linkedEntity).getReceiver(), EntityDamageEvent.DamageCause.THORNS, transferredDamage);
                                    Bukkit.getPluginManager().callEvent(loudness);
                                    /*DMG*/OotilityCeption.Log("Transferred Evented Damage \u00a7e- \u00a7d" + loudness.getDamage());
                                    /*DMG*/OotilityCeption.Log("Transferred Final Damage \u00a7e- \u00a7d" + loudness.getFinalDamage());

                                    // Cancel and don't apply damage
                                    if (loudness.isCancelled()) {
                                        /*DMG*/OotilityCeption.Log("Event Cancelled \u00a7c---");
                                        return; }
                                    
                                    // Does not update damage because MMOItems scales and crits it which is ridiculous
                                    //transferredDamage = loudness.getDamage();
                                }
                                ((LivingEntity) ((DamageTransferLink) linkedEntity).getReceiver()).damage(transferredDamage);
                            }


                            //EVENT//oots.CLog("\u00a7e------------------------------------------");
                            //EVENT//oots.CLog("Entity Damage Dealt \u00a7e- \u00a73" + event.getEntity().getName());
                            //EVENT//oots.CLog("Damage \u00a7e- \u00a73" + event.getDamage());
                            //EVENT//oots.CLog("Final Damage \u00a7e- \u00a73" + event.getFinalDamage());
                            //EVENT//oots.CLog("Actual Damage \u00a7e- \u00a73" + actualDamage);
                        }
                    }
                }
            }
        }
    }

    public static void CheckLinks() {

        // If successful
        HashMap<UUID, Integer> transfers = new HashMap<>();
        HashMap<UUID, Integer> removed = new HashMap<>();

        // Evaluate every linked entity
        for (int L = 0; L < olEntities.size(); L++) {
            LinkedEntity linkedEntity = olEntities.get(L);
            /*DMG*/OotilityCeption.Log("Linked \u00a7e- " + linkedEntity.getEntity().getName());
            /*DMG*/OotilityCeption.Log("Reason \u00a7e- " + linkedEntity.getReason().toString());

            // If observed entity is of damage taken
            if (linkedEntity.getReason() == ObjectiveLinks.DamageTakenLink || linkedEntity.getReason() == ObjectiveLinks.DamageTransferLink) {

                boolean receiverValid = true;
                if (linkedEntity instanceof EntityLinkedEntity) {
                    UUID rec = ((EntityLinkedEntity) linkedEntity).getReceiverUUID();

                    // Include
                    transfers.putIfAbsent(rec, 0);
                    transfers.put(rec, transfers.get(rec) + 1);

                    receiverValid = ((EntityLinkedEntity) linkedEntity).isReceiverValid();

                    /*DMG*/OotilityCeption.Log("Transfer Count \u00a7b- " + transfers.get(rec));
                    /*DMG*/OotilityCeption.Log("Receiver Valid? \u00a7b- " + receiverValid);
                    /*DMG*/OotilityCeption.Log("\u00a78Receiver: \u00a73- " + ((EntityLinkedEntity) linkedEntity).getReceiver().getName() + " \u00a79" + rec.toString());
                }

                // Remove, skip, and repeat if the entity has died
                if (!linkedEntity.getEntity().isValid() || !receiverValid) {
                    olEntities.remove(L);
                    L--;

                    if (linkedEntity instanceof EntityLinkedEntity) {
                        UUID rec = ((EntityLinkedEntity) linkedEntity).getReceiverUUID();

                        // Include
                        removed.putIfAbsent(rec, 0);
                        removed.put(rec, removed.get(rec) + 1);

                        /*DMG*/OotilityCeption.Log("Removed Count \u00a7c- " + removed.get(rec));
                    }
                }
            }
        }

        for (UUID trans : transfers.keySet()) {
            Integer rem = transfers.get(trans);
            if (rem == null) { continue; }
            Integer mov = removed.getOrDefault(trans, 0);

            // If the number of removed equals the transfers, remove their unlockable
            if (rem <= mov) {

                // Get the timer
                GooPUnlockables uck = GooPUnlockables.From(trans, EntityLinkedEntity.transferLinkReceiver);

                // Remove the timer
                uck.SetTimed(null);

                // Lock it = Unregister it
                uck.Lock();

                /*DMG*/OotilityCeption.Log("No Longer Receiver \u00a7d- " + trans.toString());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnGriefEvent(GooPGriefEvent event) {

        // If not cancelled
        if (!event.isCancelled()) {

            // Send appropriate calls for every block
            for (Block bkk : event.getBlocks()) {

                // If not air
                if (!OotilityCeption.IsAirNullAllowed(bkk)) {

                    // Is it a liquid?
                    if (bkk.isLiquid()) {

                        // Liquid drain instead
                        PlayerBucketFillEvent evL = new PlayerBucketFillEvent(event.getPlayer(), bkk, bkk, BlockFace.UP, Material.BUCKET, new ItemStack(Material.BUCKET));
                        Bukkit.getPluginManager().callEvent(evL);

                        // Was it allowed?
                        if (!evL.isCancelled()) {

                            // Well then, break block I guess
                            bkk.setType(Material.AIR);
                        }

                    // Not a liquid: Block Break
                    } else {

                        // Break it through event
                        BlockBreakEvent evM = new BlockBreakEvent(bkk, event.getPlayer());
                        Bukkit.getPluginManager().callEvent(evM);

                        // Was it allowed?
                        if (!evM.isCancelled()) {

                            // Well then, break block I guess
                            bkk.breakNaturally(event.getTool());
                        }
                    }
                }
            }
        }
    }
}
