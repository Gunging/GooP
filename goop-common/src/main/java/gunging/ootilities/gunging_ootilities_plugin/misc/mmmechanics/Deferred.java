package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Introduces an interval between the execution of this skill for each of the targets.
 */
public class Deferred extends SkillMechanic implements IMetaSkill {

    PlaceholderDouble ticks;
    PlaceholderString skillName;
    Skill metaskill;

    public Deferred(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }

    public Deferred(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        ticks = mlc.getPlaceholderDouble(new String[]{"ticks", "t", "interval", "i"}, 1);
        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "meta", "m", "mechanics", "$", "()"}, "skill not found");
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

    public void execute(@NotNull SkillMetadata data, @NotNull Collection<AbstractEntity> entityTargets, @NotNull Collection<AbstractLocation> locationTargets, @NotNull Skill deferredSkill) {

        // Copy data and replace caster
        final SkillMetadata clonedData = data.deepClone();
        clonedData.setEntityTargets(entityTargets);
        clonedData.setLocationTargets(locationTargets);
        if (!deferredSkill.isUsable(clonedData)) { return; }

        // Run skill sync or async
        if (forceSync) {
            clonedData.setIsAsync(false);
            (new BukkitRunnable() {
                public void run() {
                    clonedData.setIsAsync(false);
                    deferredSkill.execute(clonedData);
                }
            }).runTask(MythicBukkit.inst());

        } else { deferredSkill.execute(clonedData);}
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        Skill deferredSkill = metaskill;
        if (deferredSkill == null) { deferredSkill = GooPMythicMobs.GetSkill(skillName.get(data, data.getCaster().getEntity())); }
        if (deferredSkill == null) { return SkillResult.ERROR; }
        final Skill skil = deferredSkill;

        // Cast instantly when no interval
        double deference = ticks.get(data);
        if (deference <= 0) {
            execute(data, data.getEntityTargets(), data.getLocationTargets(), skil);
            return SkillResult.SUCCESS; }

        // Calculate delay
        int deferenceTicks = OotilityCeption.RoundToInt(Math.ceil(deference));
        int simultaneous = deference >= 1 ? 1 : OotilityCeption.RoundToInt(1 / deference);

        // Gather them targets
        ArrayList<Object> targets = new ArrayList<>();
        if (data.getEntityTargets() != null) targets.addAll(data.getEntityTargets());
        if (data.getLocationTargets() != null) targets.addAll(data.getLocationTargets());

        // Store the current i
        RefSimulator<Integer> i = new RefSimulator<>(0);

        (new BukkitRunnable() {
            public void run() {

                // One instantaneous
                for (double I = 0; I < simultaneous; I++) {

                    // Cancel
                    if (i.getValue() >= targets.size()) { this.cancel(); return; }

                    Object target = targets.get(i.getValue());
                    ArrayList newLocs = new ArrayList<AbstractLocation>();
                    ArrayList newEnts = new ArrayList<AbstractEntity>();
                    if (target instanceof AbstractEntity) { newEnts.add(target); }
                    if (target instanceof AbstractLocation) { newLocs.add(target); }
                    execute(data.deepClone(), newEnts, newLocs, skil);

                    // Increase
                    i.setValue(i.getValue() + 1);
                    if (i.getValue() >= targets.size()) { this.cancel(); return; }
                }
            }

        }).runTaskTimerAsynchronously(Gunging_Ootilities_Plugin.theMain, 0, deferenceTicks);

        // Success I guess
        return SkillResult.SUCCESS;
    }
}
