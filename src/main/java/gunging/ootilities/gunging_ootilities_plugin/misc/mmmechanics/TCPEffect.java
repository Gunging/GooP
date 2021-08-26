package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractVector;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.mechanics.ParticleEffect;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderFloat;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    @NotNull public final PlaceholderFloat radius, rotationFraction;

    public TCPEffect(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.radius = mlc.getPlaceholderFloat(new String[]{"radius", "r"}, 5.0F);
        this.rotationFraction = mlc.getPlaceholderFloat(new String[]{"rotation", "rot", "rotationfraction", "rf"}, 1F);
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
        double o = hor;
        double p = ver;
        double rf = rotationFraction.get(); if (rf == 0) { rf = 1; }
        double e = (Math.PI * 2.0D) / rf;
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
        double t_x = (hor * g_x_dir) + (ver * r_y_dir * g_z_dir) + (fro * r_x_dir);
        double t_y = (ver * r_z_dir * g_x_dir) - (ver * r_x_dir * g_z_dir) + (fro * r_y_dir);
        double t_z = (hor * g_z_dir) - (ver * r_y_dir * g_x_dir) + (fro * r_z_dir);
        return (new AbstractVector(t_x, t_y, t_z)).multiply(radius.get());
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
