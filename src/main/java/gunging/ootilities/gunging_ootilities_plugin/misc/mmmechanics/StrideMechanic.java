package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

import java.io.File;

/**
 * Literally just boosts an entity in a direction WHY IS THAT SO HARD?
 */
public class StrideMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

    PlaceholderDouble velocity;

    //NEWEN//public StrideMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        //NEWEN//super(manager, file, line, mlc);
        /*OLDEN*/public StrideMechanic(SkillExecutor manager, String line, MythicLineConfig mlc) {
        /*OLDEN*/super(manager, line, mlc);
        GooPMythicMobs.newenOlden = true;
        velocity = mlc.getPlaceholderDouble(new String[]{"velocity", "v"}, 5);
    }

    @Override public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) { return castAtLocation(skillMetadata, abstractEntity.getLocation()); }

    @Override public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {

        // Obtain vector
        AbstractVector target = abstractLocation.toVector();
        AbstractVector origin = skillMetadata.getCaster().getLocation().toVector();

        // Obtain Direction
        AbstractVector direction = new AbstractVector(target.getX() - origin.getX(), target.getY() - origin.getY(), target.getZ() - origin.getZ());

        // Multiply
        AbstractVector vel = direction.normalize().multiply(velocity.get(skillMetadata));

        // Apply velocity
        skillMetadata.getCaster().getEntity().setVelocity(vel.add(skillMetadata.getCaster().getEntity().getVelocity()));

        // Yeah
        return SkillResult.SUCCESS;
    }
}
