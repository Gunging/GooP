package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractVector;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderFloat;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class ParticleSlashEffect extends TCPEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
    @NotNull final PlaceholderFloat arc, points, slashDelay, skew;
    final boolean horizontal, randomPoints;

    public ParticleSlashEffect(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.arc = mlc.getPlaceholderFloat(new String[]{"arc", "a"}, 130F);
        this.points = mlc.getPlaceholderFloat(new String[]{"points", "p"}, 12F);
        this.slashDelay = mlc.getPlaceholderFloat(new String[]{"slashdelay", "sd"}, 0F);
        this.skew = mlc.getPlaceholderFloat(new String[]{"skew"}, 1F);
        this.horizontal = mlc.getBoolean(new String[]{"horizontal", "h", "isHorizontal", "ih"}, true);
        this.randomPoints = mlc.getBoolean(new String[]{"randomPoints", "rP"}, true);

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

        double trueYOffset = 0; Object funnyYOffset = null;
        try { Field fd = getClass().getField("yOffset"); fd.setAccessible(true); funnyYOffset = fd.get(this); } catch (NoSuchFieldException|IllegalAccessException ignored) {}
        if (funnyYOffset instanceof PlaceholderFloat) {

            // Execute
            PlaceholderFloat asPH = (PlaceholderFloat) funnyYOffset;
            trueYOffset = (double) asPH.get(data);

        } else if (funnyYOffset != null) {

            // Frantic
            trueYOffset = (double) funnyYOffset;
        }

        location.add(0.0D, trueYOffset, 0.0D);
        Collection<AbstractEntity> audienceList = GetAudience(target);

        // Calculate some
        Random random = new Random(System.nanoTime());
        double circumference = toRadians(this.arc.get(data));
        double pointsTotal = Math.ceil(this.points.get(data));
        double skewed = skew.get(data);
        double fraction = circumference / pointsTotal;
        double b = fraction * 0.5;

        /*
         * If greater than 1, the delay is how many ticks between points
         *
         * If less than 1, it is inversely proportional to how many simultaneous points
         */
        double lashTimer = slashDelay.get(data);
        int slashTicks = OotilityCeption.RoundToInt(lashTimer);
        int simultaneous = lashTimer >= 1 ? 1 : OotilityCeption.RoundToInt(1 / lashTimer);

        // All in one tick
        if (slashTicks <= 0) {

            // One instantaneous
            for(double i = 0; i < pointsTotal; i++) {

                // Generate all points
                slashGen(random, data, target, location, audienceList, circumference, pointsTotal, fraction, skewed, b, i);
            }

        // Repeating
        } else {

            // Store the current i
            RefSimulator<Integer> i = new RefSimulator<>(0);

            (new BukkitRunnable() {
                public void run() {

                    // One instantaneous
                    for(double I = 0; I < simultaneous; I++) {

                        // Cancel
                        if (i.getValue() >= pointsTotal) { this.cancel(); return; }

                        // Generate all points
                        slashGen(random, data, target, location, audienceList, circumference, pointsTotal, fraction, skewed, b, i.getValue());

                        // Increase
                        i.setValue(i.getValue() + 1);

                        // Cancel
                        if (i.getValue() >= pointsTotal) { this.cancel(); return; }
                    }
                }

            }).runTaskTimerAsynchronously(Gunging_Ootilities_Plugin.theMain, 0, slashTicks);
        }
    }

    void slashGen(@NotNull Random random, @NotNull SkillMetadata data, @NotNull AbstractLocation target, @NotNull AbstractLocation location, @NotNull Collection<AbstractEntity> audienceList, double circumference, double pointsTotal, double fraction, double skewed, double b, double i) {

        // Get a random location along the arc
        double rnd;

        // Funny random arc stuff
        if (randomPoints) {
            rnd = (random.nextDouble() - 0.5D) * circumference;

        // Chad even distribution
        } else { rnd = (b + i * fraction) - (circumference * 0.5D); }

        // Circumference
        rnd += (circumference * 0.5D);

        // Skew
        rnd *= skewed * skewed;

        // Circumference
        rnd -= (circumference * 0.5D);

        double x = Math.sin(rnd); double y = x;
        double z = Math.cos(rnd);
        if (horizontal) { y = 0; } else { x = 0; }
        AbstractVector vector = transform(data, target, x, y, z);
        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a A\u00a77 Transformed\u00a7b " + x + " " + y + " " + z + " \u00a77to\u00a7e " + vector.getX() + " " + vector.getY() + " " + vector.getZ());

        // Add, play, subtract
        location.add(vector);
        /*CURRENT-MMOITEMS*/this.playEffect(data, location, audienceList);
        //YE-OLDEN-MMO//this.playParticleEffect(data, location);
        location.subtract(vector);
    }
}
