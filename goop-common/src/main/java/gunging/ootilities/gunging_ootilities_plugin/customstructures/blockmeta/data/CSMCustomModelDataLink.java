package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import gunging.ootilities.gunging_ootilities_plugin.misc.CustomModelDataLink;
import org.jetbrains.annotations.NotNull;

public class CSMCustomModelDataLink extends CSMRelativeOrientation {

    /**
     * @return The value of this flag
     */
    @NotNull public CustomModelDataLink getLink() { return link; }
    @NotNull CustomModelDataLink link;

    /**
     * @param link The value of this flag
     */
    public void setLink(@NotNull CustomModelDataLink link) { this.link = link; }

    /**
     * @return The facing orientation of this Custom Model Data link
     */
    @NotNull public RelativeOrientations getFacing() { return getValue().size() >= 1 ? getValue().get(0) : RelativeOrientations.FORWARD; }

    /**
     * @param link Value of this flag.
     */
    public CSMCustomModelDataLink(@NotNull RelativeOrientations orientation, @NotNull CustomModelDataLink link) { super(orientation); this.link = link; }

    @Override public String toString() { return super.toString() + " " + getLink().getParentMaterial().toString() + " " + getLink().getCustomModelData(); }
}