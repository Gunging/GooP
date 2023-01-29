package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.mm52.BKCAuraMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Hides a player from monsters or players.
 */
public class HideAura extends BKCAuraMechanic implements ITargetedEntitySkill {

    boolean hideFromMonsters;
    @NotNull PlaceholderString skillName;
    @Nullable Skill metaskill;

    public HideAura(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);

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

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        // Find caster
        SkillCaster hideTarget;

        // Will be caster of the skill, as a mythicmob
        if (MythicBukkit.inst().getMobManager().isActiveMob(target)) {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Caster as ActiveMob");

            // Just pull the mythicmob
            hideTarget = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);

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
        return SkillResult.SUCCESS;
    }

    private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
        public Tracker(SkillCaster caster, SkillMetadata data, AbstractEntity entity) {
            super(caster, entity, data);
            this.start();
        }

        public void auraStart() {

            // Entity target event tracker, target must not be null
            this.registerAuraComponent( (Terminable)
                    Events.subscribe(EntityTargetEvent.class).filter((event) -> {

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
