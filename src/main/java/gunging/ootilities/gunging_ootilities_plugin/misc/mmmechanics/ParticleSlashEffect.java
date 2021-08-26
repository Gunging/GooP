package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractVector;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderFloat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class ParticleSlashEffect extends TCPEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
    @NotNull final PlaceholderFloat arcFraction;
    final boolean horizontal;

    public ParticleSlashEffect(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.arcFraction = mlc.getPlaceholderFloat(new String[]{"arc", "a"}, 6F);
        this.horizontal = mlc.getBoolean(new String[]{"horizontal", "h", "isHorizontal", "ih"}, true);

        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a L\u00a77 Loaded Particle Slash, Horizontal?\u00a7b" + horizontal);
    }

    public boolean castAtLocation(SkillMetadata data, AbstractLocation target) {
        this.playParticleSlashEffect(data, target);
        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a C\u00a77 Casting Location Particle Slash, Horizontal?\u00a7b" + horizontal);
        return false;
    }

    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        this.playParticleSlashEffect(data, target.getLocation());
        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a C\u00a77 Casting Entity Particle Slash, Horizontal?\u00a7b" + horizontal);
        return false;
    }

    protected void playParticleSlashEffect(SkillMetadata data, AbstractLocation target) {
        AbstractLocation location = target.clone();
        location.add(0.0D, (double)this.yOffset, 0.0D);
        Collection<AbstractEntity> audienceList = GetAudience(target);

        // Calculate some
        Random random = new Random(System.nanoTime());
        int amount = this.amount.get(data);
        double arc = ((2.0D * 3.141592653589793D) / arcFraction.get(data));

        for(int i = 0; i < amount; ++i) {

            // Get a random location along the arc
            double rnd = (random.nextDouble() - 0.5D) * arc;
            double x = Math.sin(rnd); double y = x;
            double z = Math.cos(rnd);
            if (horizontal) { y = 0; } else { x = 0; }
            AbstractVector vector = transform(data, target, x, y, z);
            //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a A\u00a77 Transformed\u00a7b " + x + " " + y + " " + z + " \u00a77to\u00a7e " + vector.getX() + " " + vector.getY() + " " + vector.getZ());

            // Add, play, subtract
            location.add(vector);
            this.playEffect(data, location, audienceList);
            location.subtract(vector);
        }
    }
}
