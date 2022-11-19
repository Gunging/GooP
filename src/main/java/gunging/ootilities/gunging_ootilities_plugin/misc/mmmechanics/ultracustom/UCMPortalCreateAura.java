package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.OnAttackAura;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * When a portal is created
 *
 * + when theplayer lihgts nither portal
 * + when nither portal is created in another world to pair the nither portal entered for travel
 * + when end portal is created by eye of ender placement
 * + when end platform is created because of entering end portal
 */
public class UCMPortalCreateAura extends Aura implements ITargetedEntitySkill {

    @NotNull PlaceholderString skillName;
    @Nullable Skill metaskill;
    protected boolean cancelDamage;
    protected boolean endPortal;
    protected boolean travel;
    //NEWEN//public UCMPortalCreateAura(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
        //NEWEN//super(manager, file, skill, mlc);
        /*OLDEN*/ public UCMPortalCreateAura(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        /*OLDEN*/ super(manager, skill, mlc);
        GooPMythicMobs.newenOlden = true;

        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillName.get());
        this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage", "cd"}, false);
        this.endPortal = mlc.getBoolean(new String[]{"endPortal", "end", "ep"}, false);
        this.travel = mlc.getBoolean(new String[]{"travelPair", "tp", "travel", "pair"}, false);

        // Attempt to fix meta skill
        if (metaskill == null) {
            //MM//OotilityCeption.Log("\u00a7c--->> \u00a7eMeta Skill Failure \u00a7c<<---");

            // Try again i guess?
            (new BukkitRunnable() {
                public void run() {

                    // Run Async
                    metaskill = GooPMythicMobs.GetSkill(skillName.get());

                }
            }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
        }
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        // Find caster
        SkillCaster caster;

        // Will be caster of the skill, as a mythicmob
        if (MythicBukkit.inst().getMobManager().isActiveMob(target)) {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Caster as ActiveMob");

            // Just pull the mythicmob
            caster = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);

            // If its a player or some other non-mythicmob
        } else {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Caster as Non MM");

            // I guess make a new caster out of them
            caster = new GenericCaster(target);
        }

        new UCMPortalCreateAura.Tracker(caster, data, target);
        return SkillResult.SUCCESS;
    }

    private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
        public Tracker(SkillCaster caster, SkillMetadata data, AbstractEntity entity) {
            super(caster, entity, data);
            this.start();
        }

        public void auraStart() {

            if (endPortal) {
                this.registerAuraComponent(
                        Events.subscribe(PlayerInteractEvent.class)
                                .filter((event) -> {
                                    //SOM//OotilityCeption.Log("\u00a7cStep 3 \u00a77Subscribe Run: " + getName(event.getEntity()) + "\u00a77 vs " + getName(this.entity.get()) + "\u00a78 ~\u00a7e " + event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId()));

                                    // Find the true entity
                                    if (event.getClickedBlock() == null) { return false; }
                                    if (!(event.getClickedBlock().getBlockData() instanceof EndPortalFrame)) { return false; }
                                    if (OotilityCeption.IsAirNullAllowed(event.getItem())) { return false; }
                                    if (event.getItem().getType() != Material.ENDER_EYE) { return false; }
                                    EndPortalFrame frame = (EndPortalFrame) event.getClickedBlock().getBlockData();

                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 Block\u00a7e " + (event.getClickedBlock() == null ? "\u00a7cnull" : event.getClickedBlock().getType().toString()));
                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 Class\u00a7e " + (event.getClickedBlock() == null ? "\u00a7cnull" : event.getClickedBlock().getClass().getSimpleName()));
                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 Data\u00a7e " + (event.getClickedBlock() == null ? "\u00a7cnull" : event.getClickedBlock().getBlockData().getClass().getSimpleName()));
                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 State\u00a7e " + (event.getClickedBlock() == null ? "\u00a7cnull" : event.getClickedBlock().getState().getClass().getSimpleName()));

                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 Block\u00a7e " + (trueDamager == null ? "\u00a7cnull" : trueDamager.getType().toString()));
                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 Item\u00a7e " + OotilityCeption.GetItemName(event.getItem()));
                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 Action\u00a7e " + event.getAction().toString());
                                    //EVN//OotilityCeption.Log("\u00a78UCM\u00a73 PCA\u00a77 Hand\u00a7e " + (event.getHand() == null ? "\u00a7cnull" : event.getHand().toString()));

                                    // I guess make a list of nearby poral frames
                                    int success = 0;
                                    for (int x = -4; x <= 4; x++) {
                                        for (int z = -4; z <= 4; z++) {
                                            if (x == 0 && z == 0) { continue; }

                                            // I guess
                                            Block at = event.getClickedBlock().getRelative(x, 0, z);

                                            // Skip non end portal frames
                                            if (!(at.getBlockData() instanceof EndPortalFrame)) { continue; }

                                            /*
                                             * If it is an end portal frame, and it has no eye, then
                                             * putting this eye down won't complete portal so
                                             */
                                            if (!((EndPortalFrame) at.getBlockData()).hasEye()) { return false; }
                                            success++;
                                        }
                                    }

                                    // No eye means activation
                                    return !frame.hasEye() && success >= 11;

                                }).handler((event) -> {

                            // Clone, sure
                            SkillMetadata meta = this.skillMetadata.deepClone();
                            meta.setOrigin(BukkitAdapter.adapt(event.getClickedBlock().getLocation()));

                            // Refresh
                            if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillName.get(meta, meta.getCaster().getEntity()));}

                            //SOM// OotilityCeption.Log("\u00a7cStep 4 \u00a77Aura Run:\u00a7d " + logSkillData(meta) + "\u00a7b " + metaskill.getInternalName());

                            // Execute
                            if (executeAuraSkill(Optional.ofNullable(metaskill), meta)) {
                                consumeCharge();
                                if (cancelDamage) {
                                    event.setCancelled(true);
                                }
                            }
                        }));

            // not end portal
            } else {
                this.registerAuraComponent(
                        Events.subscribe(PortalCreateEvent.class)
                                .filter((event) -> {
                                    //SOM//OotilityCeption.Log("\u00a7cStep 3 \u00a77Subscribe Run: " + getName(event.getEntity()) + "\u00a77 vs " + getName(this.entity.get()) + "\u00a78 ~\u00a7e " + event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId()));

                                    // Find the true entity
                                    Entity trueDamager = event.getEntity();

                                    if (trueDamager == null) { return false; }

                                    return trueDamager.getUniqueId().equals(((AbstractEntity)this.entity.get()).getUniqueId());

                                }).handler((event) -> {

                            // Clone, sure
                            SkillMetadata meta = this.skillMetadata.deepClone();

                            // Target is target yea
                            meta.setOrigin(BukkitAdapter.adapt(event.getBlocks().get(0).getLocation()));

                            // Refresh
                            if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillName.get(meta, meta.getCaster().getEntity()));}

                            //SOM// OotilityCeption.Log("\u00a7cStep 4 \u00a77Aura Run:\u00a7d " + logSkillData(meta) + "\u00a7b " + metaskill.getInternalName());

                            // Execute
                            if (executeAuraSkill(Optional.ofNullable(metaskill), meta)) {
                                consumeCharge();
                                if (cancelDamage) {
                                    event.setCancelled(true);
                                }
                            }

                        }));
            }

            executeAuraSkill(UCMPortalCreateAura.this.onStartSkill, this.skillMetadata);
        }
    }
}
