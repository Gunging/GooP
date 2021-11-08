package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractVector;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.mechanics.ParticleEffect;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderFloat;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * The transformed co-ordinates particle effect, it
 * generates a vector between the caster and the target
 * so that the particles can be oriented in a relative
 * forward rather than absolute.
 *
 * @author Gunging
 */
public class TCPEffect extends ParticleEffect {
    @NotNull public final PlaceholderFloat radius, rotation, fOff, sOff, vOff, xOff, yOff, zOff;

    boolean useDegrees;

    public TCPEffect(String skill, MythicLineConfig mlc) {
        super(skill, mlc);

        this.fOff =   mlc.getPlaceholderFloat(new String[]{"fOff"}, 0.0F);
        this.sOff =   mlc.getPlaceholderFloat(new String[]{"sOff"}, 0.0F);
        this.vOff =   mlc.getPlaceholderFloat(new String[]{"vOff"}, 0.0F);
        this.xOff =   mlc.getPlaceholderFloat(new String[]{"xOff"}, 0.0F);
        this.yOff =   mlc.getPlaceholderFloat(new String[]{"yOff"}, 0.0F);
        this.zOff =   mlc.getPlaceholderFloat(new String[]{"zOff"}, 0.0F);

        this.radius = mlc.getPlaceholderFloat(new String[]{"radius", "r"}, 5.0F);
        this.rotation = mlc.getPlaceholderFloat(new String[]{"rotation", "rot"}, 1F);

        this.useDegrees = mlc.getBoolean(new String[]{"useDegrees", "degrees", "ud"}, true);
    }

    /**
     * Will convert this to radians if the user is
     * writing in degrees, otherwise, leaves it
     * untouched.
     *
     * @param e Angle that is in degrees IFF and only if
     *          the user also set {@link #useDegrees} to
     *          true.
     *
     * @return This angle as radians.
     */
    public double toRadians(double e) {
        if (useDegrees) { e *= Math.PI; e /= 180.0; }
        return e;
    }

    /**
     * @param data Data from which to calculate rotations
     *
     * @param hor Relative Horizontal
     * @param ver Relative Vertical
     * @param fro Relative forward
     *
     * @return Transforms these relative x y and z values into absolutes, ready to add to the location of the
     *         origin of the projectile and display.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    public AbstractVector transform(@NotNull SkillMetadata data, @NotNull AbstractLocation target, double hor, double ver, double fro) {
        AbstractLocation source = data.getCaster().getLocation();

        // Rotate input relatives about the relative forward axis
        double o = hor + sOff.get(data);
        double p = ver + vOff.get(data);
        double f = fro + fOff.get(data);
        double e = toRadians(rotation.get(data));
        double ce = Math.cos(e), se = Math.sin(e);

        hor = ((o * ce) - (p * se));
        ver = ((o * se) + (p * ce));

        //Calculate the relative direction vectors
        double r_x = target.getX() - source.getX();
        double r_y = target.getY() - source.getY();
        double r_z = target.getZ() - source.getZ();
        double r_magnitude = Math.sqrt((r_x * r_x) + (r_y * r_y) + (r_z * r_z));
        double r_x_dir = r_x / r_magnitude;
        double r_y_dir = r_y / r_magnitude;
        double r_z_dir = r_z / r_magnitude;

        double g_x = r_z;
        double g_z = -r_x;
        double g_magnitude = Math.sqrt((r_z * r_z) + (r_x * r_x));
        double g_x_dir = g_x / g_magnitude;
        double g_z_dir = g_z / g_magnitude;

        // Perform conversions
        double t_x = (hor * g_x_dir) + (ver * r_y_dir * g_z_dir) + (f * r_x_dir);
        double t_y = (ver * r_z_dir * g_x_dir) - (ver * r_x_dir * g_z_dir) + (f * r_y_dir);
        double t_z = (hor * g_z_dir) - (ver * r_y_dir * g_x_dir) + (f * r_z_dir);

        // Add offsets
        t_x += xOff.get(data);
        t_y += yOff.get(data);
        t_z += zOff.get(data);

        return (new AbstractVector(t_x, t_y, t_z)).multiply(radius.get(data));
    }

    public HashSet<AbstractEntity> get(SkillMetadata data) {
        return new HashSet<>(MythicMobs.inst().getEntityManager().getPlayers(data.getCaster().getEntity().getWorld()));
    }

    /**
     * Idk how audiences work and honestly its just a bunch of implementation problems.
     * This gets all players within 48 blocks of epicenter.
     *
     * @param target Target Location
     * @return Payers nearby that should be able to see the particle effect.
     */
    Collection<AbstractEntity> GetAudience(@NotNull AbstractLocation target) {

        // Literally just return all players within 48 blocks
        return new ArrayList<>(target.getWorld().getPlayersNearLocation(target, 48));
    }
}
