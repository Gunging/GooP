package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMCustomModelDataLink;
import gunging.ootilities.gunging_ootilities_plugin.misc.CustomModelDataLink;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CSMSCustomModelDataLink extends CSMetaSource<CSMCustomModelDataLink> {

    public CSMSCustomModelDataLink(@NotNull String internalName) { super(internalName); }

    @Override
    public @Nullable CSMCustomModelDataLink fromString(@Nullable String serialized) {

        // No lol
        if (serialized == null) { return null; }

        // No boolean no service
        if (!serialized.contains(" ")) { return null; }

        // Yes
        String[] spaceSplit = serialized.split(" ");
        if (!OotilityCeption.IntTryParse(spaceSplit[1])) { return null; }
        Material mat;
        try { mat = Material.valueOf(spaceSplit[0].toUpperCase()); } catch (IllegalArgumentException ignored) { return null; }
        int result = OotilityCeption.ParseInt(spaceSplit[1]);

        // Attempt to find
        CustomModelDataLink found = CustomModelDataLink.getFrom(mat, result);
        if (found == null) { return null; }

        // Parse direction
        RelativeOrientations forward = RelativeOrientations.FORWARD;
        if (spaceSplit.length >= 3) { try { forward = RelativeOrientations.valueOf(spaceSplit[2].toUpperCase()); } catch (IllegalArgumentException ignored) { return null; } }

        // Yes
        return new CSMCustomModelDataLink(forward, found);
    }

    @Override
    public @NotNull String toString(@NotNull CSMCustomModelDataLink serializable) {

        // Um yeah just that
        return serializable.getLink().getParentMaterial().toString() + " " + serializable.getLink().getCustomModelData() + " " + serializable.getFacing().toString();
    }
}