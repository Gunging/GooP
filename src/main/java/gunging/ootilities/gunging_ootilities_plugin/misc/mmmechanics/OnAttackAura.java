package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import io.lumine.xikage.mythicmobs.skills.*;
import io.lumine.xikage.mythicmobs.skills.auras.Aura;
import io.lumine.xikage.mythicmobs.skills.mechanics.OnAttackMechanic;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.utils.Events;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.OnShootAura.getName;
import static gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.OnShootAura.logSkillData;

public class OnAttackAura extends Aura implements ITargetedEntitySkill {

    @NotNull
    PlaceholderString skillName;
    @Nullable
    Skill metaskill;
    protected boolean cancelDamage;
    protected boolean traceSource;
    protected boolean modDamage = false;
    protected PlaceholderDouble damageAdd;
    protected PlaceholderDouble damageMult;
    public OnAttackAura(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillName.get());
        this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage", "cd"}, false);
        this.traceSource = mlc.getBoolean(new String[]{"tracesource", "ts"}, true);
        String damageAdd = mlc.getString(new String[]{"damageadd", "add", "a"}, (String)null, new String[0]);
        String damageMult = mlc.getString(new String[]{"damagemultiplier", "multiplier", "m"}, (String)null, new String[0]);
        if (damageAdd != null || damageMult != null) {
            this.modDamage = true;
        }

        if (damageAdd == null) {
            this.damageAdd = PlaceholderDouble.of("0");
        } else {
            this.damageAdd = PlaceholderDouble.of(damageAdd);
        }

        if (damageMult == null) {
            this.damageMult = PlaceholderDouble.of("1");
        } else {
            this.damageMult = PlaceholderDouble.of(damageMult);
        }

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

    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        // Find caster
        SkillCaster caster;

        // Will be caster of the skill, as a mythicmob
        if (MythicMobs.inst().getMobManager().isActiveMob(target)) {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Caster as ActiveMob");

            // Just pull the mythicmob
            caster = MythicMobs.inst().getMobManager().getMythicMobInstance(target);

            // If its a player or some other non-mythicmob
        } else {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Caster as Non MM");

            // I guess make a new caster out of them
            caster = new GenericCaster(target);
        }

        new OnAttackAura.Tracker(caster, data, target);
        return true;
    }

    protected double calculateDamage(SkillMetadata data, AbstractEntity target, double damage) {
        return this.cancelDamage ? 0.0D : (damage + this.damageAdd.get(data, target)) * this.damageMult.get(data, target);
    }

    private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
        public Tracker(SkillCaster caster, SkillMetadata data, AbstractEntity entity) {
            super(caster, entity, data);
            this.start();
        }

        public void auraStart() {
            this.registerAuraComponent(Events.subscribe(EntityDamageByEntityEvent.class).filter((event) ->
                    event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                    event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE).filter((event) -> {

                //SOM//OotilityCeption.Log("\u00a7cStep 3 \u00a77Subscribe Run: " + getName(event.getEntity()) + "\u00a77 vs " + getName(this.entity.get()) + "\u00a78 ~\u00a7e " + event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId()));


                // Find the true entity
                Entity trueDamager = event.getDamager();

                // Get True Damager
                if (traceSource) {
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
                }


                return trueDamager.getUniqueId().equals(((AbstractEntity)this.entity.get()).getUniqueId());

            }).filter((event) -> {
                Optional<Object> md = BukkitAdapter.adapt(event.getDamager()).getMetadata("doing-skill-damage");
                return md.map(o -> !(Boolean) o).orElse(true);

            }).handler((event) -> {

                // Clone, sure
                SkillMetadata meta = this.skillMetadata.deepClone();

                // Target is target yea
                AbstractEntity target = BukkitAdapter.adapt(event.getEntity());
                meta.setTrigger(target);

                // Refresh
                if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillName.get(meta, meta.getCaster().getEntity()));}

                //SOM// OotilityCeption.Log("\u00a7cStep 4 \u00a77Aura Run:\u00a7d " + logSkillData(meta) + "\u00a7b " + metaskill.getInternalName());

                // Execute
                if (this.executeAuraSkill(Optional.ofNullable(metaskill), meta)) {
                    this.consumeCharge();
                    if (OnAttackAura.this.cancelDamage) {
                        event.setCancelled(true);
                    } else if (OnAttackAura.this.modDamage) {
                        double damage = OnAttackAura.this.calculateDamage(meta, target, event.getDamage());
                        event.setDamage(damage);
                    }
                }

            }));
            this.executeAuraSkill(OnAttackAura.this.onStartSkill, this.skillMetadata);
        }
    }
}
