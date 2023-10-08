package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data;

import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;
import org.bukkit.block.data.FaceAttachable;
import org.jetbrains.annotations.NotNull;

public class CSMFaceAttachable  extends CSMeta {

    /**
     * @return The value of this flag
     */
    @NotNull
    public FaceAttachable.AttachedFace getValue() { return value; }
    @NotNull FaceAttachable.AttachedFace value;

    /**
     * @param value The value of this flag
     */
    public void setValue(@NotNull FaceAttachable.AttachedFace value) { this.value = value; }

    /**
     * @param value Value of this flag.
     */
    public CSMFaceAttachable(@NotNull FaceAttachable.AttachedFace value) { this.value = value; }

    @Override public String toString() { return getValue().toString(); }
}