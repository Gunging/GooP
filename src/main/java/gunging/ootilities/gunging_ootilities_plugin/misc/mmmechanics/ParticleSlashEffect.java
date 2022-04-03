package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Random;

public class ParticleSlashEffect extends TCPEffect implements ITargetedEntitySkill, ITargetedLocationSkill {
    @NotNull final PlaceholderFloat arc, points, slashDelay, skew;
    final boolean horizontal, randomPoints;

    public ParticleSlashEffect(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        this.arc = mlc.getPlaceholderFloat(new String[]{"arc", "a"}, 130F);
        this.points = mlc.getPlaceholderFloat(new String[]{"points", "p"}, 12F);
        this.slashDelay = mlc.getPlaceholderFloat(new String[]{"slashdelay", "sd"}, 0F);
        this.skew = mlc.getPlaceholderFloat(new String[]{"skew"}, 1F);
        this.horizontal = mlc.getBoolean(new String[]{"horizontal", "h", "isHorizontal", "ih"}, true);
        this.randomPoints = mlc.getBoolean(new String[]{"randomPoints", "rP"}, true);

        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a L\u00a77 Loaded Particle Slash, Horizontal?\u00a7b" + horizontal);
    }

    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
        this.playParticleSlashEffect(data, target);
        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a C\u00a77 Casting Location Particle Slash, Horizontal?\u00a7b" + horizontal);
        return SkillResult.SUCCESS;
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        this.playParticleSlashEffect(data, target.getLocation());
        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a C\u00a77 Casting Entity Particle Slash, Horizontal?\u00a7b" + horizontal);
        return SkillResult.SUCCESS;
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
        int slashTicks = OotilityCeption.RoundToInt(Math.ceil(lashTimer));
        int simultaneous = lashTimer >= 1 ? 1 : OotilityCeption.RoundToInt(1 / lashTimer);
        //DLY//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Delay read\u00a7b " + lashTimer);
        //DLY//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Slash Ticks\u00a7b " + slashTicks);
        //DLY//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Simultaneous\u00a7b " + simultaneous);

        // All in one tick
        if (slashTicks <= 0) {
            //DLY//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Mode \u00a79INSTANT\u00a77 ~ Total\u00a75" + pointsTotal + "\u00a77 points");

            // One instantaneous
            for (double i = 0; i < pointsTotal; i++) {
                //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Point\u00a7a " + i);

                // Generate all points
                slashGen(random, data, target, location, audienceList, circumference, pointsTotal, fraction, skewed, b, i);
            }

        // Repeating
        } else {
            //DLY//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Mode \u00a79INSTANT\u00a77 ~ Total\u00a75" + pointsTotal + "\u00a77 points ~ Ticking at \u00a7b " + slashTicks);

            // Store the current i
            RefSimulator<Integer> i = new RefSimulator<>(0);

            (new BukkitRunnable() {
                public void run() {
                    //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Points starting at \u00a7a " + i.getValue() + "\u00a77 through\u00a7a " + (i.getValue() + simultaneous));

                    // One instantaneous
                    for (double I = 0; I < simultaneous; I++) {
                        //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Point\u00a7a " + i.getValue() + "\u00a78 ~ Loop Index \u00a72" + I);

                        // Cancel
                        if (i.getValue() >= pointsTotal) {
                            //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Slash completed: \u00a7eTotal Points Reached");

                            this.cancel(); return; }

                        // Generate all points
                        slashGen(random, data, target, location, audienceList, circumference, pointsTotal, fraction, skewed, b, i.getValue());

                        // Increase
                        i.setValue(i.getValue() + 1);

                        // Cancel
                        if (i.getValue() >= pointsTotal) {
                            //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Slash completed: \u00a7eTotal Points Spawned");
                            this.cancel(); return; }
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
