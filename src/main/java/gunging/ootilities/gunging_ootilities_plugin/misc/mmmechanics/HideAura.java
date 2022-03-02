package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import io.lumine.xikage.mythicmobs.skills.*;
import io.lumine.xikage.mythicmobs.skills.auras.Aura;
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.utils.Events;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Hides a player from monsters or players.
 */
public class HideAura extends Aura implements ITargetedEntitySkill {

    boolean hideFromMonsters;
    @NotNull PlaceholderString skillName;
    @Nullable Skill metaskill;

    public HideAura(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        hideFromMonsters = mlc.getBoolean(new String[] { "hideFromMonsters", "hfm" }, true);
        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillName.get());

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
        SkillCaster hideTarget;

        // Will be caster of the skill, as a mythicmob
        if (MythicMobs.inst().getMobManager().isActiveMob(target)) {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Caster as ActiveMob");

            // Just pull the mythicmob
            hideTarget = MythicMobs.inst().getMobManager().getMythicMobInstance(target);

            // If its a player or some other non-mythicmob
        } else {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Caster as Non MM");

            // I guess make a new caster out of them
            hideTarget = new GenericCaster(target);
        }

        // Clears the target of any entity around the player
        for (Mob nearby : hideTarget.getEntity().getBukkitEntity().getWorld().getEntitiesByClass(Mob.class)) {

            // Target of them is the receiver of the hide aura?
            if (nearby.getTarget() != null && nearby.getTarget().getUniqueId().equals(hideTarget.getEntity().getUniqueId())) {

                // Entity can no longer see them
                nearby.setTarget(null);
            }
        }

        new HideAura.Tracker(hideTarget, data, target);
        return true;
    }

    private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
        public Tracker(SkillCaster caster, SkillMetadata data, AbstractEntity entity) {
            super(caster, entity, data);
            this.start();
        }

        public void auraStart() {

            // Entity target event tracker, target must not be null
            this.registerAuraComponent(Events.subscribe(EntityTargetEvent.class).filter((event) -> {

                // Filter out null target
                if (event.getTarget() == null) { return false; }

                // Target must be the entity under the hide aura
                return event.getTarget().getUniqueId().equals(((AbstractEntity)this.entity.get()).getUniqueId());

            }).handler((event) -> {

                // Consume Charge
                this.consumeCharge();

                // Cancel event
                event.setCancelled(true);

                // Refresh
                if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillName.get(this.skillMetadata, this.skillMetadata.getCaster().getEntity()));}

                // Run skill
                if (metaskill != null) {

                    // Run Skill ig
                    SkillMetadata meta = this.skillMetadata.deepClone();

                    // Target is target yea
                    AbstractEntity target = BukkitAdapter.adapt(event.getEntity());
                    meta.setTrigger(target);

                    // Execute
                    this.executeAuraSkill(Optional.ofNullable(metaskill), meta);
                }

            }));
            this.executeAuraSkill(HideAura.this.onStartSkill, this.skillMetadata);
        }
    }
}
