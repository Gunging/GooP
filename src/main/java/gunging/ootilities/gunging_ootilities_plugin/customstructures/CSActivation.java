package gunging.ootilities.gunging_ootilities_plugin.customstructures;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMatchResult;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class CSActivation {

    @NotNull Entity who;
    @NotNull CSMatchResult where;
    @NotNull CSStructure what;

    /**
     * @return Entity who activated this
     */
    @NotNull public Entity getWho() { return who; }
    /**
     * @return Location where this was activated
     */
    @NotNull public CSMatchResult getWhere() { return where; }
    /**
     * @return Clone of location of the structure core of the structure that produced this activation
     */
    @NotNull public Location getWhereCoreClone() { return new Location(where.getCoreBlock().getWorld(), where.getCoreBlock().getX(), where.getCoreBlock().getY(), where.getCoreBlock().getZ()); }
    /**
     * @return Clone of location interacted by the entity to produce this activation
     */
    @NotNull public Location getWhereInteractedClone() { return new Location(where.getInteractedBlock().getWorld(), where.getInteractedBlock().getX(), where.getInteractedBlock().getY(), where.getInteractedBlock().getZ()); }
    /**
     * @return Structure Activated
     */
    @NotNull public CSStructure getWhat() { return what; }

    public CSActivation(@NotNull Entity who, @NotNull CSMatchResult where, @NotNull CSStructure what) {
        this.who = who;
        this.what = what;
        this.where = where;
    }
}
