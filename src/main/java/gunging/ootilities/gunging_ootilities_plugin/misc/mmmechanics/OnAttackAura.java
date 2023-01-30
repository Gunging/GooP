package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
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
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class OnAttackAura extends Aura implements ITargetedEntitySkill {

    @NotNull PlaceholderString skillName;
    @Nullable Skill metaskill;
    protected boolean cancelDamage;
    protected boolean traceSource;
    protected boolean modDamage = false;
    protected PlaceholderDouble damageAdd;
    protected PlaceholderDouble damageMult;

    public OnAttackAura(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public OnAttackAura(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillName.get());
        this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage", "cd"}, false);
        this.traceSource = mlc.getBoolean(new String[]{"tracesource", "ts"}, true);
        String damageAdd = mlc.getString(new String[]{"damageadd", "add", "a"}, (String)null);
        String damageMult = mlc.getString(new String[]{"damagemultiplier", "multiplier", "m"}, (String)null);
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

        new OnAttackAura.Tracker(caster, data, target);
        return SkillResult.SUCCESS;
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
            this.registerAuraComponent(
                     Events.subscribe(EntityDamageByEntityEvent.class)
                            .filter((event) -> event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
                            .filter((event) -> {
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
                                if (executeAuraSkill(Optional.ofNullable(metaskill), meta)) {
                                    consumeCharge();
                                    if (cancelDamage) {
                                        event.setCancelled(true);
                                    } else if (modDamage) {
                                        double damage = calculateDamage(meta, target, event.getDamage());
                                        event.setDamage(damage);
                                    }
                                }

                            }));

            executeAuraSkill(OnAttackAura.this.onStartSkill, this.skillMetadata);
        }
    }
}
