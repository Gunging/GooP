package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class SlashLocations<T> extends TCPEffect {

    @NotNull public PlaceholderFloat arc, points, slashDelay, skew;
    public boolean horizontal, randomPoints, dynamicSource;
    @NotNull final SlashResult<T> slashResult;

    @Override public SlashLocations<T> clone() { return new SlashLocations<>(slashRadius, rotation, fOff, sOff, vOff, xOff, yOff, zOff, fScale, sScale, vScale, xScale, yScale, zScale, fromOrigin, fromTrigger, useDegrees, arc, points, slashDelay, skew, horizontal, randomPoints, dynamicSource, slashResult); }

    public SlashLocations(@NotNull PlaceholderFloat slashRadius, @NotNull PlaceholderFloat rotation, @NotNull PlaceholderFloat fOff, @NotNull PlaceholderFloat sOff, @NotNull PlaceholderFloat vOff, @NotNull PlaceholderFloat xOff, @NotNull PlaceholderFloat yOff, @NotNull PlaceholderFloat zOff, @NotNull PlaceholderFloat fScale, @NotNull PlaceholderFloat sScale, @NotNull PlaceholderFloat vScale, @NotNull PlaceholderFloat xScale, @NotNull PlaceholderFloat yScale, @NotNull PlaceholderFloat zScale, boolean fromOrigin, boolean fromTrigger, boolean useDegrees, @NotNull PlaceholderFloat arc, @NotNull PlaceholderFloat points, @NotNull PlaceholderFloat slashDelay, @NotNull PlaceholderFloat skew, boolean horizontal, boolean randomPoints, boolean dynamicSource, @NotNull SlashResult<T> slashResult) {
        super(slashRadius, rotation, fOff, sOff, vOff, xOff, yOff, zOff, fScale, sScale, vScale, xScale, yScale, zScale, fromOrigin, fromTrigger, useDegrees);
        this.arc = arc;
        this.points = points;
        this.slashDelay = slashDelay;
        this.skew = skew;
        this.horizontal = horizontal;
        this.randomPoints = randomPoints;
        this.slashResult = slashResult;
        this.dynamicSource = dynamicSource;
    }

    public SlashLocations(@NotNull MythicLineConfig mlc, @NotNull SlashResult<T> slashResult) {

        super(mlc);
        this.arc = mlc.getPlaceholderFloat(new String[]{"arc", "a"}, 130F);
        this.points = mlc.getPlaceholderFloat(new String[]{"points", "p"}, 12F);
        this.slashDelay = mlc.getPlaceholderFloat(new String[]{"slashdelay", "sd"}, 0F);
        this.skew = mlc.getPlaceholderFloat(new String[]{"skew"}, 1F);
        this.horizontal = mlc.getBoolean(new String[]{"horizontal", "h", "isHorizontal", "ih"}, true);
        this.randomPoints = mlc.getBoolean(new String[]{"randomPoints", "rP"}, true);
        this.dynamicSource = mlc.getBoolean(new String[]{"dynamicSource", "dS"}, false);
        this.slashResult = slashResult;

    }

    @NotNull public ArrayList<T> playParticleSlashEffect(SkillMetadata data, AbstractLocation target, Object... funnies) {
        AbstractLocation targetLocationCopy = target.clone();

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

        targetLocationCopy.add(0.0D, trueYOffset, 0.0D);

        // Calculate some
        Random random = new Random(System.nanoTime());
        double circumference = toRadians(this.arc.get(data));
        double pointsTotal = Math.ceil(this.points.get(data));
        double skewed = skew.get(data);
        double fraction = circumference / pointsTotal;
        double b = fraction * 0.5;
        AbstractLocation source = dynamicSource ? null : getSource(data);
        //SRC//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Dynamic Source\u00a7b " + dynamicSource);

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
            //DLY//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Mode \u00a79INSTANT\u00a77 ~ Total\u00a75 " + pointsTotal + "\u00a77 points");

            // Holy damn generation...
            ArrayList<T> ret = new ArrayList<>();

            // One instantaneous
            for (double i = 0; i < pointsTotal; i++) {
                //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Point\u00a7a " + i);

                // Generate all points
                T r = slashGen(random, data, source, target, targetLocationCopy.clone(), circumference, pointsTotal, fraction, skewed, b, i, funnies);

                //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Result\u00a7a " + (r instanceof AbstractLocation ? (((AbstractLocation) r).getX() + " " + ((AbstractLocation) r).getY() + " " + ((AbstractLocation) r).getZ()) : (r == null ? "null" : r.getClass().getName())));
                if (r != null) { ret.add(r); }
            }

            // Yeah
            return ret;

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
                        slashGen(random, data, source, target, targetLocationCopy.clone(), circumference, pointsTotal, fraction, skewed, b, i.getValue(), funnies);

                        // Increase
                        i.setValue(i.getValue() + 1);

                        // Cancel
                        if (i.getValue() >= pointsTotal) {
                            //DLY//OotilityCeption.Log("\u00a78SLH \u00a72DR\u00a77 Slash completed: \u00a7eTotal Points Spawned");
                            this.cancel(); return; }
                    }
                }

            }).runTaskTimerAsynchronously(Gunging_Ootilities_Plugin.theMain, 0, slashTicks);

            // Time delayed is not supported for return value
            return new ArrayList<>();
        }
    }

    @Deprecated
    @Nullable public T slashGen(@NotNull Random random, @NotNull SkillMetadata data, @NotNull AbstractLocation target, @NotNull AbstractLocation sourceLocation, double circumference, double pointsTotal, double fraction, double skewed, double b, double i, Object... funnies) { return slashGen(random, data, null, target, sourceLocation, circumference, pointsTotal, fraction, skewed, b, i, funnies); }
    @Nullable public T slashGen(@NotNull Random random, @NotNull SkillMetadata data, @Nullable AbstractLocation source, @NotNull AbstractLocation target, @NotNull AbstractLocation sourceLocation, double circumference, double pointsTotal, double fraction, double skewed, double b, double i, Object... funnies) {

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
        AbstractVector slashDelta;

        // No fixed source makes it use the current caster/target locations
        if (source == null) { slashDelta = transform(data, target, x, y, z); } else { slashDelta = transform(data, source, target, x, y , z); }
        //DLY//OotilityCeption.Log("\u00a78PSlash\u00a7a A\u00a77 Transformed\u00a7b " + x + " " + y + " " + z + " \u00a77to\u00a7e " + slashDelta.getX() + " " + slashDelta.getY() + " " + slashDelta.getZ());

        // Add, play, subtract
        sourceLocation.add(slashDelta);

        // Result
        return slashResult.process(data, sourceLocation, funnies);
    }
}
