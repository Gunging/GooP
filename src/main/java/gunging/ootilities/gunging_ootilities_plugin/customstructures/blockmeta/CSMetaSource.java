package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSMeta;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class CSMetaSource<T extends CSMeta> {

    //region Manager
    /**
     * @param source Source of CustomStructureMeta to set live.
     */
    public static void register(@NotNull CSMetaSource source) { registeredSources.put(source.getInternalName(), source); }
    @NotNull static HashMap<String, CSMetaSource> registeredSources = new HashMap<>();
    /**
     * @return All the loaded sources ~ sweet.
     */
    @NotNull public static HashMap<String, CSMetaSource> getRegisteredSources() { return registeredSources; }

    public static void registerAll() {

        // Register these things I guess.
        register(new CSMSBisected());
        register(new CSMSDirectional());
        register(CSMSFurnitureJSON.theSource);
        register(new CSMSMultipleFacing());
        register(new CSMSOpenable());
        register(new CSMSLightable());
        register(new CSMSPowerable());
        register(new CSMSSnowable());
        register(new CSMSWaterlogged());
        register(new CSMSOrientable());
        register(new CSMSSign(0));
        register(new CSMSSign(1));
        register(new CSMSSign(2));
        register(new CSMSSign(3));
        register(new CSMSSlab());
        register(new CSMSFaceAttachable());
        register(new CSMSAgeable());
        register(new CSMSAnalogPower());
        register(new CSMSLevelled());
        register(new CSMSNoteBlockInstrument());
        register(new CSMSNoteBlockNote());
        if (Gunging_Ootilities_Plugin.asPaperSpigot) { register(new CSMSPlayerHead()); }
    }
    //endregion

    //region Instance
    /**
     * @return I guess just some identifier to register and save it by yeah
     */
    @NotNull public String getInternalName() { return internalName; }
    @NotNull final String internalName;

    /**
     * @param internalName Internal name (no spaces!!!!) by which this shall be saved.
     */
    public CSMetaSource(@NotNull String internalName) {
        Validate.isTrue(!internalName.contains(" "), "Internal Name cannot have spaces.");
        this.internalName = internalName;
    }

    /**
     * @param input Input block.
     *
     * @param inRelativeTo Forward direction, I guess?
     *
     * @return This block with this meta applied, if compatible of course.
     */
    @NotNull public abstract Block apply(@NotNull Block input, @NotNull T meta, @NotNull Orientations inRelativeTo);

    /**
     * @param input The input block to read from.
     *
     * @param inRelativeTo Forward direction, I guess?
     *
     * @return The meta this is giving, if it makes sense from this block.
     */
    @Nullable public abstract T fromBlock(@NotNull Block input, @NotNull Orientations inRelativeTo);

    /**
     * @param input Block to examine, that is in the relative location to the core that this custom
     *              structure block should occupy.
     *
     * @param inRelativeTo Forward orientations, to parse block meta that requires facing.
     *
     * @return If this block has the correct type and meta in the correct orientations.
     */
    public abstract boolean matches(@NotNull Block input, @NotNull T meta, @NotNull Orientations inRelativeTo);

    /**
     * @param serialized Everything after the first space, saved in teh files.
     *
     * @return The deserialized Custom Structure Meta
     */
    @Nullable public abstract T fromString(@Nullable String serialized);

    /**
     * @param serializable The data to be serialized yeah.
     *
     * @return The serialized form of this data, ready to be saved to the files.
     */
    @NotNull public abstract String toString(@NotNull T serializable);
    //endregion
}
