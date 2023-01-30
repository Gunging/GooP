package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.scheduler.BukkitRunnable;

public class AsOrigin extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
    boolean targetArmorStands;
    PlaceholderString skillName;
    Skill metaskill;

    public AsOrigin(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public AsOrigin(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        targetArmorStands = mlc.getBoolean(new String[]{"targetarmorstands", "ta"}, false);
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

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity == null) { return SkillResult.INVALID_TARGET; }

        // Cast at that location
        return castAtLocation(skillMetadata, abstractEntity.getLocation());
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {

        // For every target
        //MM//OotilityCeption.Log("\u00a73 >>> \u00a77Running for\u00a7b " + abstractLocation.getWorld() + " " + abstractLocation.getX() + " " + abstractLocation.getY() + " " + abstractLocation.getZ());

        // Copy data and replace caster
        final SkillMetadata clonedData = skillMetadata.deepClone();
        clonedData.setOrigin(abstractLocation);

        // ??
        if (!metaskill.isUsable(clonedData)) { return SkillResult.ERROR; }
        //MM//OotilityCeption.Log("\u00a7a  + \u00a77Usable");

        // Run skill sync or async
        if (forceSync) {
            //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Async");

            clonedData.setIsAsync(false);
            (new BukkitRunnable() {
                public void run() {

                    // Run Async
                    clonedData.setIsAsync(false);
                    metaskill.execute(clonedData);

                }
            }).runTask(MythicBukkit.inst());

            // Forcing Sync
        } else {
            //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Sync");

            // Run Sync
            metaskill.execute(clonedData);
        }

        return SkillResult.SUCCESS;
    }
}
