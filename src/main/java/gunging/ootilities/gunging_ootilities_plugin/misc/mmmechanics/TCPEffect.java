package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import org.jetbrains.annotations.NotNull;
/**
 * The transformed co-ordinates particle effect, it
 * generates a vector between the caster and the target
 * so that the particles can be oriented in a relative
 * forward rather than absolute.
 *
 * @author Gunging
 */
public class TCPEffect implements Cloneable {

    @NotNull public PlaceholderFloat slashRadius, rotation, fOff, sOff, vOff, xOff, yOff, zOff, fRot, sRot, vRot, rRot;
    @NotNull public PlaceholderFloat fScale, sScale, vScale, xScale, yScale, zScale;
    public final boolean fromOrigin, fromTrigger;
    boolean useDegrees;

    /** @noinspection MethodDoesntCallSuperMethod*/
    @Override
    public TCPEffect clone() {

        // Copy values yeah
        return new TCPEffect(slashRadius, rotation, fOff, sOff, vOff, xOff,yOff, zOff, fScale, sScale, vScale,xScale, yScale, zScale, fromOrigin, fromTrigger, useDegrees);
    }

    public TCPEffect(@NotNull PlaceholderFloat slashRadius, @NotNull PlaceholderFloat rotation, @NotNull PlaceholderFloat fOff, @NotNull PlaceholderFloat sOff, @NotNull PlaceholderFloat vOff, @NotNull PlaceholderFloat xOff, @NotNull PlaceholderFloat yOff, @NotNull PlaceholderFloat zOff, @NotNull PlaceholderFloat fScale, @NotNull PlaceholderFloat sScale, @NotNull PlaceholderFloat vScale, @NotNull PlaceholderFloat xScale, @NotNull PlaceholderFloat yScale, @NotNull PlaceholderFloat zScale, boolean fromOrigin, boolean fromTrigger, boolean useDegrees) {
        this.slashRadius = slashRadius;
        this.rotation = rotation;
        this.fOff = fOff;
        this.sOff = sOff;
        this.vOff = vOff;
        this.xOff = xOff;
        this.yOff = yOff;
        this.zOff = zOff;
        this.fScale = fScale;
        this.sScale = sScale;
        this.vScale = vScale;
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
        this.fromOrigin = fromOrigin;
        this.fromTrigger = fromTrigger;
        this.useDegrees = useDegrees;
    }
    public TCPEffect(@NotNull MythicLineConfig mlc) {

        // Relative Scale Vector
        this.fScale =   mlc.getPlaceholderFloat(new String[]{"fScale"}, 1.0F);
        this.sScale =   mlc.getPlaceholderFloat(new String[]{"sScale"}, 1.0F);
        this.vScale =   mlc.getPlaceholderFloat(new String[]{"vScale"}, 1.0F);

        // Relative Offset Vector
        this.fOff =   mlc.getPlaceholderFloat(new String[]{"fOff"}, 0.0F);
        this.sOff =   mlc.getPlaceholderFloat(new String[]{"sOff"}, 0.0F);
        this.vOff =   mlc.getPlaceholderFloat(new String[]{"vOff"}, 0.0F);

        // Relative Rotation Quaternion
        this.fRot =   mlc.getPlaceholderFloat(new String[]{"fRot"}, 0.0F);
        this.sRot =   mlc.getPlaceholderFloat(new String[]{"sRot"}, 0.0F);
        this.vRot =   mlc.getPlaceholderFloat(new String[]{"vRot"}, 0.0F);
        this.rRot =   mlc.getPlaceholderFloat(new String[]{"rRot"}, 0.0F);

        // Absolute Scale Vector
        this.xScale =   mlc.getPlaceholderFloat(new String[]{"xScale"}, 1.0F);
        this.yScale =   mlc.getPlaceholderFloat(new String[]{"yScale"}, 1.0F);
        this.zScale =   mlc.getPlaceholderFloat(new String[]{"zScale"}, 1.0F);

        // Absolute Offset Vector
        this.xOff =   mlc.getPlaceholderFloat(new String[]{"xOff"}, 0.0F);
        this.yOff =   mlc.getPlaceholderFloat(new String[]{"yOff"}, 0.0F);
        this.zOff =   mlc.getPlaceholderFloat(new String[]{"zOff"}, 0.0F);

        // Actual Parameters
        this.slashRadius = mlc.getPlaceholderFloat(new String[]{"slashRadius", "sr"}, null);
        if (slashRadius == null) { this.slashRadius = mlc.getPlaceholderFloat(new String[]{"r", "radius"}, 5.0F); }
        this.rotation = mlc.getPlaceholderFloat(new String[]{"rotation", "rot"}, 0F);

        this.useDegrees = mlc.getBoolean(new String[]{"useDegrees", "degrees", "ud"}, true);
        this.fromOrigin = mlc.getBoolean(new String[]{"fromOrigin", "fo", "fOrigin"}, false);
        this.fromTrigger = mlc.getBoolean(new String[]{"fromTrigger", "ft", "fTrigger"}, false);
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
    @NotNull public AbstractVector transform(@NotNull SkillMetadata data, @NotNull AbstractLocation target, double hor, double ver, double fro) {
        //SRC//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Dynamic Source\u00a73 APPLIED");

        // Transform
        return transform(data, getSource(data), target, hor, ver, fro);
    }
    @NotNull public AbstractLocation getSource(@NotNull SkillMetadata data) {

        // Trigger exists and from trigger? Use that location
        return (fromTrigger && data.getTrigger() != null) ? data.getTrigger().getLocation() :

                        // From origin? or use caster location
                        fromOrigin ? data.getOrigin() : data.getCaster().getLocation();
    }
    @NotNull public AbstractVector transform(@NotNull SkillMetadata data, @NotNull AbstractLocation source, @NotNull AbstractLocation target, double rHor, double rVer, double rFor) {
        //SRC//OotilityCeption.Log("\u00a78SLH \u00a73D\u00a77 Source\u00a79 " + source.getX() + " " + source.getY() + " " + source.getZ());

        // Prevent singularities
        if (source.getX() == target.getX() && source.getY() == target.getY() && source.getZ() == target.getZ()) {

            // Target will be right above source
            target = source.clone().add(new AbstractVector(0, 0.001, 0)); }

        double r = slashRadius.get(data);

        // Rotate input relatives about the relative forward axis
        double o = (rHor * sScale.get(data)) + (sOff.get(data) / r);
        double p = (rVer * vScale.get(data)) + (vOff.get(data) / r);
        double f = (rFor * fScale.get(data)) + (fOff.get(data) / r);
        double e = toRadians(rotation.get(data));
        double ce = Math.cos(e), se = Math.sin(e);

        rHor = ((o * ce) - (p * se));
        rVer = ((o * se) + (p * ce));

        // Forward vector
        double r_x = target.getX() - source.getX();
        double r_y = target.getY() - source.getY();
        double r_z = target.getZ() - source.getZ();
        double r_magnitude = Math.sqrt((r_x * r_x) + (r_y * r_y) + (r_z * r_z));
        double r_x_dir = r_x / r_magnitude;
        double r_y_dir = r_y / r_magnitude;
        double r_z_dir = r_z / r_magnitude;

        // I think this is the part where it rotates the axes to align?
        double g_x = r_z;
        double g_z = -r_x;
        double g_magnitude = Math.sqrt((r_z * r_z) + (r_x * r_x));
        double g_x_dir = g_x / g_magnitude;
        double g_z_dir = g_z / g_magnitude;

        // Perform conversions
        double t_x = (rHor * g_x_dir) + (rVer * r_y_dir * g_z_dir) + (f * r_x_dir);
        double t_y = (rVer * r_z_dir * g_x_dir) - (rVer * r_x_dir * g_z_dir) + (f * r_y_dir);
        double t_z = (rHor * g_z_dir) - (rVer * r_y_dir * g_x_dir) + (f * r_z_dir);

        // Add offsets
        t_x *= xScale.get(data);
        t_y *= yScale.get(data);
        t_z *= zScale.get(data);

        t_x += (xOff.get(data) / r);
        t_y += (yOff.get(data) / r);
        t_z += (zOff.get(data) / r);

        return (new AbstractVector(t_x, t_y, t_z)).multiply(r);
    }
}
