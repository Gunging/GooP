package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class OnShootAura extends Aura implements ITargetedEntitySkill {
    @NotNull PlaceholderString skillName;
    @Nullable Skill metaskill;
    protected boolean cancelEvent;
    protected boolean forceAsPower;

    public OnShootAura(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillName.get());
        this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce"}, false);
        this.forceAsPower = mlc.getBoolean(new String[]{"forceaspower", "fap"}, true);

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
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Target as ActiveMob");

            // Just pull the mythicmob
            caster = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);

            // If its a player or some other non-mythicmob
        } else {
            //SOM//OotilityCeption.Log("\u00a73  * \u00a77Target as Non MM");

            // I guess make a new caster out of them
            caster = new GenericCaster(target);
        }

        new OnShootAura.Tracker(caster, data, target);
        return SkillResult.SUCCESS;
    }

    private class Tracker extends AuraTracker implements IParentSkill, Runnable {
        public Tracker(SkillCaster caster, SkillMetadata data, AbstractEntity entity) {
            super(caster, entity, data);
            this.start();
        }

        public void auraStart() {
            this.registerAuraComponent((Terminable)
                    Events.subscribe(EntityShootBowEvent.class).filter((event) -> {

                    //SOM//OotilityCeption.Log("\u00a7cStep 3 \u00a77Subscribe Run: " + getName(event.getEntity()) + "\u00a77 vs " + getName(this.entity.get()) + "\u00a78 ~\u00a7e " + event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId()));

                    return event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId());

                    }).handler((event) -> {

                        // Clone metadata
                        SkillMetadata meta = this.skillMetadata.deepClone();

                        // Target obviously the projectile
                        AbstractEntity projectile = BukkitAdapter.adapt(event.getProjectile());
                        meta.setTrigger(projectile);

                        // Refresh
                        if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillName.get(meta, meta.getCaster().getEntity()));}

                        //SOM//OotilityCeption.Log("\u00a7cStep 4 \u00a77Aura Run:\u00a7d " + logSkillData(meta) + "\u00a7b " + metaskill.getInternalName());
                        if (this.executeAuraSkill(Optional.ofNullable(metaskill), meta)) {

                            this.consumeCharge();

                            if (cancelEvent) { event.setCancelled(true); }
                        }

                    }));
            this.executeAuraSkill(OnShootAura.this.onStartSkill, this.skillMetadata);
        }
    }

    @NotNull public static String getName(@Nullable AbstractEntity ent) {
        if (ent == null) { return "null"; }
        if (ent.getCustomName() != null) { return ent.getCustomName().toString(); }
        return ent.getBukkitEntity().getType().toString();
    }
    @NotNull public static String getName(@Nullable Entity ent) {
        if (ent == null) { return "null"; }
        if (ent.getCustomName() != null) { return ent.getCustomName(); }
        return ent.getType().toString();
    }
    @NotNull public static String logSkillData(@Nullable SkillMetadata meta) {
        if (meta == null) { return "\u00a7d null\u00a7c @null\u00a7e ~null"; }

        String caster = "\u00a7d " + getName(meta.getCaster().getEntity());
        ArrayList<String> targets = new ArrayList<>();
        for (AbstractEntity target : meta.getEntityTargets()) { targets.add("\u00a7c @" + getName(target)); }
        String trigger = "\u00a7e ~" + getName(meta.getTrigger());

        return caster + collapseList(targets, " ") + trigger;
    }

    @NotNull
    public static String collapseList(@NotNull ArrayList<String> list, @NotNull String separator) {
        StringBuilder sb = new StringBuilder();
        boolean af = false;

        for (String str : list) {
            if (str == null) {
                str = "null";
            }

            if (af) {
                sb.append(separator);
            }

            af = true;
            sb.append(str);
        }

        return sb.toString();
    }
}
