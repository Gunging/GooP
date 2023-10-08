package gunging.ootilities.gunging_ootilities_plugin.customstructures;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.CSMetaSource;
import gunging.ootilities.gunging_ootilities_plugin.events.JSONPlacerUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A block with co-ordinates relative to a core.
 */
public class CSBlock {

    //region Constructor
    /**
     * @return Material expected.
     */
    @NotNull public Material getBlockType() { return blockType; }
    @NotNull Material blockType;

    /**
     * @return Distance to the right from the core block.
     */
    public int getSideOffset() { return sideOffset; }
    final int sideOffset;

    /**
     * @return Distance upward in the positive Y, from the core.
     */
    public int getVerticalOffset() { return verticalOffset; }
    final int verticalOffset;

    /**
     * @return Distance forward from the core block.
     */
    public int getForwardOffset() { return forwardOffset; }
    final int forwardOffset;
    
    /**
     * A block with material and co-ordiantes relative to a core block.
     *
     * @param blockType Material of block
     * @param sideOffset Side Offset
     * @param verticalOffset Vertical Offset
     * @param forwardOffset Forward Offset
     */
    public CSBlock(@NotNull Material blockType, int sideOffset, int verticalOffset, int forwardOffset) {
        this.blockType = blockType;
        this.sideOffset = sideOffset;
        this.verticalOffset = verticalOffset;
        this.forwardOffset = forwardOffset;
    }

    /**
     * @return Additional information, apart from block type, to make the Custom Structure match blocks more specifically.
     */
    @NotNull public HashMap<CSMetaSource, CSMeta> getBlockMeta() { return blockMeta; }
    @NotNull final HashMap<CSMetaSource, CSMeta> blockMeta = new HashMap<>();
    /**
     * @return If this has any meta restrictions.
     */
    public boolean hasMetaRestrictions() { return getBlockMeta().size() > 0; }
    //endregion

    /**
     * @return If this is the core block, each offset is equal to zero.
     */
    public boolean isCore() { return getForwardOffset() == 0 && getVerticalOffset() == 0 && getSideOffset() == 0; }

    /**
     * @param core The core block, to which the side, vertical, and forward directions are basically added.
     *
     * @return The absolute world locations of this block, this block remaining relative to the provided core.
     */
    @NotNull public Location getRelativeTo(@NotNull Location core) { return getRelativeTo(core, Orientations.SouthForward); }

    /**
     * @param core The core block, to which the side, vertical, and forward directions are basically added.
     * 
     * @param orientation The orientation to evaluate relativity from. Forward axis is different 
     *                    depending on which direction is forward.
     * 
     * @return The absolute world locations of this block, this block remaining relative to the provided core.
     */
    @NotNull public Location getRelativeTo(@NotNull Location core, @NotNull Orientations orientation) {
        // Get a copy of Origin Location
        Location ret = new Location(core.getWorld(), 0, 0, 0).add(core);

        // Process Offsets
        RefSimulator<Integer> rawX = new RefSimulator<>(getSideOffset());
        RefSimulator<Integer> rawY = new RefSimulator<>(getVerticalOffset());
        RefSimulator<Integer> rawZ = new RefSimulator<>(getForwardOffset());
        CSStructure.bakeOrientations(rawX, rawY, rawZ, orientation);

        // Return new location
        return ret.add(rawX.getValue(), rawY.getValue(), rawZ.getValue());
    }

    /**
     * @param input Block to examine, that is in the relative location to the core that this custom
     *              structure block should occupy.
     * 
     * @param inRelativeTo Forward orientations, to parse block meta that requires facing.
     * 
     * @return If this block has the correct type and meta in the correct orientations.
     */
    public boolean matches(@NotNull Block input, @NotNull Orientations inRelativeTo) {
        //MCH//OotilityCeption. Log("Examining @\u00a7b" + inRespectTo.toString() + "\u00a77: \u00a7e" + bSource.getType().toString());
        
        // Type must match, easiest check
        if (!getBlockType().equals(input.getType())) { return false; }

        // Match all alv
        for (Map.Entry<CSMetaSource, CSMeta> meta : getBlockMeta().entrySet()) {

            // Snooze
            CSMetaSource source = meta.getKey();
            CSMeta metaData = meta.getValue();

            // The law requires me to say
            if (source == null || metaData == null) { continue; }

            try {

                //noinspection unchecked
                if (!source.matches(input, metaData, inRelativeTo)) { return false; }

            } catch (ClassCastException ignored) {

                Gunging_Ootilities_Plugin.theOots.CPLog(OotilityCeption.LogFormat("Custom Structures Reload", "\u00a7cError when comparing composition meta \u00a7e" + source.getInternalName() + "\u00a7c: \u00a77 Could not cast MetaData. "));
            }
        }

        // No flag rejected it
        return true;
    }

    /**
     * @return A list of space-separated meta and data, separated by commas into a single string.
     */
    @NotNull public String serializeMeta() {

        StringBuilder builder = new StringBuilder();
        boolean appended = false;

        // Match all alv
        for (Map.Entry<CSMetaSource, CSMeta> meta : getBlockMeta().entrySet()) {

            // Snooze
            CSMetaSource source = meta.getKey();
            CSMeta metaData = meta.getValue();

            // The law requires me to say
            if (source == null || metaData == null) { continue; }

            if (appended) { builder.append(","); }

            // Serialize
            try {

                //noinspection unchecked
                builder.append(source.getInternalName()).append(" ").append(source.toString(metaData).replace(",", "<&cm>").replace("|", "<&br>"));

                // Success appending
                appended = true;

            } catch (ClassCastException ignored) {

                Gunging_Ootilities_Plugin.theOots.CPLog(OotilityCeption.LogFormat("Custom Structures Reload", "\u00a7cError when saving composition meta \u00a7e" + source.getInternalName() + "\u00a7c: \u00a77 Could not cast MetaData. "));
            }
        }

        // Yeah
        return builder.toString();
    }

    /**
     * @param target Block to convert into the metadata required to succeed.
     *
     * @param inRelativeTo Which direction of the core, absolute with the world?
     */
    public void applyMeta(@NotNull Block target, @NotNull Orientations inRelativeTo) {

        // Break JSON structures
        RefSimulator<ArmorStand> heuh = new RefSimulator<>(null);
        if (JSONPlacerUtils.IsJSON_Furniture(target, heuh) && heuh.getValue() != null) { heuh.getValue().remove(); }

        // Set type
        target.setType(getBlockType());

        // Match all alv
        for (Map.Entry<CSMetaSource, CSMeta> meta : getBlockMeta().entrySet()) {

            // Snooze
            CSMetaSource source = meta.getKey();
            CSMeta metaData = meta.getValue();

            // The law requires me to say
            if (source == null || metaData == null) { continue; }

            // Serialize
            try {
                //OotilityCeption.Log("\u00a78GOOPCS\u00a73 BLD\u00a77 Applying Meta:\u00a7a " + source.getInternalName() + " \u00a77>\u00a7e " + metaData.toString());

                //noinspection unchecked
                target = source.apply(target, metaData, inRelativeTo);

            } catch (ClassCastException ignored) {

                Gunging_Ootilities_Plugin.theOots.CPLog(OotilityCeption.LogFormat("Custom Structures Reload", "\u00a7cError when applying composition meta \u00a7e" + source.getInternalName() + "\u00a7c: \u00a77 Could not cast MetaData. "));
            }
        }
    }

    /**
     * This shall be everything after the ||s, not including the bars themselves.
     *
     * @param serializedMetadata A list of space-separated meta and data, separated by commas into a single string.
     */
    public void deserializeMeta(@NotNull String serializedMetadata) {

        // Clear current
        blockMeta.clear();

        // Split by commas
        ArrayList<String> commaSplit = new ArrayList<>();
        if (serializedMetadata.contains(",")) { commaSplit.addAll(Arrays.asList(serializedMetadata.split(",")));} else { commaSplit.add(serializedMetadata); }

        // Evaluate every registered source.
        for (String serializedMeta : commaSplit) {

            // Split by first space
            int space = serializedMeta.indexOf(" ");

            // Invalid
            if (space <= 0) {
                Gunging_Ootilities_Plugin.theOots.CPLog(OotilityCeption.LogFormat("Custom Structures Reload", "\u00a7cError when parsing composition meta \u00a7e" + serializedMeta + "\u00a7c: \u00a77 Invalid Meta Serialization"));
                continue; }

            // Split by comma
            String metaKey = serializedMeta.substring(0, space);
            String metaValue = serializedMeta.substring(space + 1).replace("<&cm>", ",").replace("<&sp>", " ").replace("<&br>", "|");

            // Find parser
            CSMetaSource source = CSMetaSource.getRegisteredSources().get(metaKey);

            // Must have been valid right
            if (source == null) {
                Gunging_Ootilities_Plugin.theOots.CPLog(OotilityCeption.LogFormat("Custom Structures Reload", "\u00a7cError when parsing composition meta \u00a7e" + serializedMeta + "\u00a7c: \u00a77 Invalid Meta Key\u00a7b " + metaKey));
                continue; }

            // Attempt to read
            CSMeta readMeta = source.fromString(metaValue);

            // Must have been valid right
            if (readMeta == null) {
                Gunging_Ootilities_Plugin.theOots.CPLog(OotilityCeption.LogFormat("Custom Structures Reload", "\u00a7cError when parsing composition meta \u00a7e" + serializedMeta + "\u00a7c: \u00a77 Invalid Meta Value\u00a7b " + metaValue));
                continue; }

            // Accept that shit!
            blockMeta.put(source, readMeta);
        }
    }

    /**
     * Adopts the metadata of this block.
     *
     * @param input The input block to read.
     * @param forward The forward direction.
     */
    public void readMeta(@NotNull Block input, @NotNull Orientations forward) {
        blockMeta.clear();

        // Evaluate every registered source.
        for (Map.Entry<String, CSMetaSource> entry : CSMetaSource.getRegisteredSources().entrySet()) {
            CSMetaSource source = entry.getValue();
            if (source == null) { continue; }

            // Attempt to get
            CSMeta built;

            // Attempt to get
            built = source.fromBlock(input, forward);

            // Skip
            if (built == null) { continue; }

            // Include
            blockMeta.put(source, built);
        }
    }
}
