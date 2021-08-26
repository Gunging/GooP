package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

/**
 * Makes the caster copy the equipped items of the target.
 */
public class CopyCatEquipmentMechanic extends SkillMechanic implements ITargetedEntitySkill {

    boolean helmet, chestplate, leggings, boots, mainhand, offhand;

    public CopyCatEquipmentMechanic(CustomMechanic skill, MythicLineConfig mlc) {
        super(skill.getConfigLine(), mlc);

        helmet = mlc.getBoolean(new String[] { "helmet", "h", "helm", "head" }, true);
        chestplate = mlc.getBoolean(new String[] { "chestplate", "chest", "breastplate", "plate", "c", "p" }, true);
        leggings = mlc.getBoolean(new String[] { "leggings", "leggs", "pants", "legs", "l" }, true);
        boots = mlc.getBoolean(new String[] { "boots", "feet", "shoes", "b", "f", "s" }, true);
        mainhand = mlc.getBoolean(new String[] { "mainhand", "main", "hand", "m", "mh" }, true);
        offhand = mlc.getBoolean(new String[] { "offhand", "off", "secondary", "o", "oh" }, true);
    }


    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {

        // Gather all necessary values - caster and target
        Entity target = BukkitAdapter.adapt(targetProbably);
        Entity caster = BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        // Both must be
        if (target instanceof LivingEntity && caster instanceof LivingEntity) {

            // Get target equipment
            EntityEquipment targetItems = ((LivingEntity) target).getEquipment();

            // Get my equipment
            EntityEquipment casterItems = ((LivingEntity) caster).getEquipment();

            // Both should exist
            if (targetItems == null || casterItems == null) { return true; }

            // All right copy those over
            if (helmet) { casterItems.setHelmet(targetItems.getHelmet()); }
            if (chestplate) { casterItems.setChestplate(targetItems.getChestplate()); }
            if (leggings) { casterItems.setLeggings(targetItems.getLeggings()); }
            if (boots) { casterItems.setBoots(targetItems.getBoots()); }
            if (mainhand) { casterItems.setItemInMainHand(targetItems.getItemInMainHand()); }
            if (offhand) { casterItems.setItemInOffHand(targetItems.getItemInOffHand()); }
        }

        // yes
        return true;
    }
}
