package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitTriggerMetadata;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.logging.MythicLogger;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import io.lumine.xikage.mythicmobs.skills.*;
import io.lumine.xikage.mythicmobs.skills.auras.Aura;
import io.lumine.xikage.mythicmobs.skills.damage.DamageMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.util.annotations.MythicField;
import io.lumine.xikage.mythicmobs.util.annotations.MythicMechanic;
import io.lumine.xikage.mythicmobs.utils.Events;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A GooP-ified version of OnDamagedMechanic.
 *
 * @author Ashijin but mildly edited by Gunging
 */

@MythicMechanic(
        author = "Ashijin",
        name = "ondamaged",
        description = "Applies an aura to the target that triggers a skill when they take damage"
)
public class OnDamagedAura extends Aura implements ITargetedEntitySkill {
    @NotNull PlaceholderString skillName;
    @Nullable Skill metaskill;
    protected boolean cancelDamage;
    protected boolean modDamage = false;
    protected double damageSub;
    protected double damageMult;
    @NotNull private final HashMap<String, Double> damageModifiers = new HashMap();

    public OnDamagedAura(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillName.get());
        this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage", "cd"}, false);
        this.damageSub = mlc.getDouble(new String[]{"damagesub", "sub", "s"}, 0.0D);
        this.damageMult = mlc.getDouble(new String[]{"damagemultiplier", "multiplier", "m"}, 1.0D);
        PlaceholderString strDamageMod = mlc.getPlaceholderString(new String[]{"damagemodifiers", "damagemods", "damagemod"}, (String)null, new String[0]);
        if (strDamageMod != null) {
            String[] lstDamageMod = strDamageMod.toString().split(",");
            String[] var5 = lstDamageMod;
            int var6 = lstDamageMod.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String dm = var5[var7];

                try {
                    try {
                        if (!MythicMobs.isVolatile()) {
                            EntityDamageEvent.DamageCause.valueOf(dm.toUpperCase());
                        }
                    } catch (Error | Exception var13) {
                        MythicLogger.errorMechanicConfig(this, mlc, "Custom damage modifiers require MythicMobs Premium to use.");
                        continue;
                    }

                    String[] split = dm.split(" ");
                    String type = split[0];
                    double mod = Double.valueOf(split[1]);
                    this.damageModifiers.put(type, mod);
                } catch (Exception var14) {
                    MythicLogger.errorMechanicConfig(this, mlc, "Invalid syntax for DamageModifier");
                }
            }
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

        if (this.damageSub != 0.0D || this.damageMult != 1.0D || this.damageModifiers.size() > 0) {
            this.modDamage = true;
        }


        //SOM//OotilityCeption.Log("\u00a7cStep 0 \u00a7eMechanic Load");
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

        new OnDamagedAura.Tracker(caster, data, target);
        return true;
    }

    protected double calculateDamage(AbstractEntity entity, EntityDamageByEntityEvent event) {
        if (this.cancelDamage) {
            return 0.0D;
        } else {
            double damage = event.getFinalDamage();
            Optional<Object> maybeData = entity.getMetadata("skill-damage");
            String damageType;
            if (maybeData.isPresent()) {
                DamageMetadata data = (DamageMetadata)maybeData.get();
                damageType = data.getElement() == null ? "SKILL" : data.getElement();
            } else {
                damageType = event.getCause().toString();
            }

            double mod = (Double)this.damageModifiers.getOrDefault(damageType.toUpperCase(), 1.0D);
            return (damage - this.damageSub) * this.damageMult * mod;
        }
    }

    private class Tracker extends AuraTracker implements IParentSkill, Runnable {
        public Tracker(SkillCaster caster, SkillMetadata data, AbstractEntity entity) {
            super(caster, entity, data);

            //SOM//OotilityCeption.Log("\u00a7cStep 1 \u00a7eTracker Stride");
            this.start();
        }

        public void auraStart() {

            //SOM//OotilityCeption.Log("\u00a7cStep 2 \u00a7eAura Start");

            // Subscribe Event
            this.registerAuraComponent(Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.HIGHEST).filter((event) -> {

                //SOM//OotilityCeption.Log("\u00a7cStep 3 \u00a7Subscribe Run");

                return event.getEntity().getUniqueId().equals(((AbstractEntity)this.entity.get()).getUniqueId());

            }).handler((event) -> {


                //SOM//OotilityCeption.Log("\u00a7cStep 4 \u00a7eEvent Run");

                // Deep Clone ofc
                SkillMetadata meta = this.skillMetadata.deepClone();

                // Find the true entity
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

                // Set the target of the skill to be the attacking entity
                if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillName.get(meta, meta.getCaster().getEntity()));}
                AbstractEntity targ = BukkitAdapter.adapt(trueDamager);
                meta.setTrigger(targ);
                BukkitTriggerMetadata.apply(meta, event);

                //SOM//OotilityCeption.Log("\u00a7cStep 5 \u00a7eTarget Set");
                //SOM//OotilityCeption.Log("\u00a7cStep 5.5 \u00a7eMetrics");
                //SOM//OotilityCeption.Log("\u00a7cStep 5.5 \u00a7e*\u00a77 Entity Present?\u00a7b " + entity.isPresent());
                //SOM//OotilityCeption.Log("\u00a7cStep 5.5 \u00a7e*\u00a77 Skill Present?\u00a7b " + (metaskill != null));
                //SOM//if (metaskill != null) OotilityCeption.Log("\u00a7cStep 5.5 \u00a7e*\u00a77 Skill Usable?\u00a7b " + metaskill.isUsable(meta));

                // Consume charge whatever
                if (this.executeAuraSkill(Optional.ofNullable(metaskill), meta, false)) {

                    //SOM//OotilityCeption.Log("\u00a7cStep 6 \u00a7eExecuted Aura Skill");
                    this.consumeCharge();
                    if (cancelDamage) {

                        //SOM//OotilityCeption.Log("\u00a7cStep 6.5 \u00a7eCancelling Damage");
                        event.setCancelled(true);
                    } else if (modDamage) {
                        
                        //SOM//OotilityCeption.Log("\u00a7cStep 6.5 \u00a7eModified Damage");
                        double damage = calculateDamage((AbstractEntity)this.entity.get(), event);
                        event.setDamage(damage);
                    }
                }

            }));

            //SOM//OotilityCeption.Log("\u00a7cStep 2.5 \u00a7eAura Applied");

            // Activate Aura
            this.executeAuraSkill(onStartSkill, this.skillMetadata);
        }
    }
}