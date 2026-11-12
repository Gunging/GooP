package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.RelativeOrientations;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMCustomModelDataLink;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMInteger;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSCustomModelDataLink;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSRelativeOrientation;
import gunging.ootilities.gunging_ootilities_plugin.events.JSONPlacerUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.CustomModelDataLink;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSMSFurnitureJSON extends CSMSCustomModelDataLink {

    @NotNull public static CSMSFurnitureJSON theSource = new CSMSFurnitureJSON();

    /**
     * This flag will have its corresponding {@link CSMInteger}
     * as the
     */
    public CSMSFurnitureJSON() { super("JSON"); }

    @Override
    @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMCustomModelDataLink meta, @NotNull Orientations inRelativeTo) {

        // Need 1.14 Minecraft, of course.
        if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0) { return block; }

        // Type must be barrier
        if (block.getType() != Material.BARRIER) { return block; }

        // Will now apply it yeah
        JSONPlacerUtils.SetOntoBlock(null, block, meta.getLink().toStack(),

                // First transform to relative facing, then to axis forward.
                CSMSRelativeOrientation.betweenAbsolutes(CSMSRelativeOrientation.transform(meta.getFacing(), inRelativeTo)));

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMCustomModelDataLink fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {

        // Need 1.14 Minecraft, of course.
        if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0) { return null; }

        // Type must be barrier
        if (block.getType() != Material.BARRIER) { return null; }

        // Get JSON Information
        RefSimulator<ArmorStand> display = new RefSimulator<>(null);
        if (!JSONPlacerUtils.IsJSON_Furniture(block, display)) { return null; }

        // Is such a thing registered?
        CustomModelDataLink link = CustomModelDataLink.getFrom(display.getValue().getItem(EquipmentSlot.HEAD));
        if (link == null) { return null; }

        // What is the facing?
        BlockFace displayFacing = display.GetValue().getFacing();
        RelativeOrientations relativeFacing = CSMSRelativeOrientation.transform(displayFacing, inRelativeTo);

        // Return data, where 'true' means its the bottom half.
        return new CSMCustomModelDataLink(relativeFacing, link);
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMCustomModelDataLink meta, @NotNull Orientations inRelativeTo) {

        // Need 1.14 Minecraft, of course.
        if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0) { return false; }

        // Type must be barrier
        if (block.getType() != Material.BARRIER) { return false; }

        // Get JSON Information
        RefSimulator<ArmorStand> display = new RefSimulator<>(null);
        if (!JSONPlacerUtils.IsJSON_Furniture(block, display)) { return false; }

        // Is such a thing registered?
        CustomModelDataLink link = CustomModelDataLink.getFrom(display.getValue().getItem(EquipmentSlot.HEAD));
        if (link == null) { return false; }

        // What is the facing?
        BlockFace displayFacing = display.GetValue().getFacing();
        RelativeOrientations relativeFacing = CSMSRelativeOrientation.transform(displayFacing, inRelativeTo);

        // Both facing and link must match
        return meta.getLink().equals(link) && meta.getFacing().equals(relativeFacing);
    }
}