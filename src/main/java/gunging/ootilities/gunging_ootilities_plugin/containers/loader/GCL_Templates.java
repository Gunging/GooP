package gunging.ootilities.gunging_ootilities_plugin.containers.loader;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCSlot;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCTemplate;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerTypes;
import gunging.ootilities.gunging_ootilities_plugin.containers.player.GCT_PlayerTemplate;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Micromanages the loading of templates
 */
public class GCL_Templates {

    /**
     * Load a Container Template from a file.
     *
     * @param file File that contains the template
     * @param internalName Name of the template.
     */
    @Nullable public static GOOPCTemplate load(@NotNull YamlConfiguration file, @NotNull String internalName) {

        // Get template section
        ConfigurationSection section = file.getConfigurationSection(internalName);
        if (section == null) {
            Gunging_Ootilities_Plugin.theOots.CPLog("Could not edit the file to load Container Template \u00a7e" + internalName + "\u00a7c. This is not good, report to Gunging.");
            return null;
        }

        //region Reading from File

        // Basic
        String displayTitle = section.getString(YML_TEMPLATE_DISPLAY, "GooP Box");
        String rawType = section.getString(YML_TEMPLATE_TYPE, null);
        int containerHeight = section.getInt(YML_TEMPLATE_ROWS, -1);
        List<String> rawContents = section.getStringList(YML_TEMPLATE_CONTENTS);

        // Command
        ArrayList<String> onCloseCommand;
        ArrayList<String> onOpenCommand;
        String onCloseCmd = section.getString(YML_TEMPLATE_CLOSE_COMMAND, null);
        String onOpenCmd = section.getString(YML_TEMPLATE_OPEN_COMMAND, null);
        if (onCloseCmd != null && onCloseCmd.startsWith("[") && onCloseCmd.endsWith("]")) { onCloseCommand = new ArrayList<>(section.getStringList(YML_TEMPLATE_CLOSE_COMMAND)); } else { onCloseCommand = new ArrayList<>(); onCloseCommand.add(onCloseCmd); }
        if (onOpenCmd != null && onOpenCmd.startsWith("[") && onOpenCmd.endsWith("]")) { onOpenCommand = new ArrayList<>(section.getStringList(YML_TEMPLATE_OPEN_COMMAND)); } else { onOpenCommand = new ArrayList<>(); onOpenCommand.add(onOpenCmd); }

        // Conditional & Misc
        boolean edgeFormations = section.getBoolean(YML_TEMPLATE_FORMATIONS, false);
        boolean allowDuplicateMMOItems = section.getBoolean(YML_TEMPLATE_DUPLICATES, true);
        boolean allowDragEvents = section.getBoolean(YML_TEMPLATE_DRAG_ALLOW, true);
        boolean dragExtendInventory = section.getBoolean(YML_TEMPLATE_DRAG_EXTEND, false);
        String mythicLibRecipe  =section.getString(YML_TEMPLATE_STATION_TYPE, null);

        //endregion

        //region Checking Validity

        // Ah yes still using reference mode
        String error = "Error issued when loading Container Template \u00a73" + internalName + "\u00a77: \u00a7c";
        String warning = "Warning issued when loading Container Template \u00a73" + internalName + "\u00a77: \u00a76";
        OotilityCeption oots = Gunging_Ootilities_Plugin.theOots;
        boolean failure = false;

        // Identify Container Type
        ContainerTypes containerType = null;
        if (rawType == null) {
            // Fail
            failure = true;

            // Log
            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog(error + "Missing container type."); }

        // A container type was specified
        } else {
            try {

                // Attempt to get kind of container
                containerType = ContainerTypes.valueOf(rawType.toUpperCase());

            } catch (IllegalArgumentException e) {

                // Failure
                failure = true;

                // Bruh doesnt exist
                if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog(error + "Invalid container type \u00a7e" + rawType); }
            }
        }

        // Load Contents
        ArrayList<GOOPCSlot> slotContents = new ArrayList<>();
        if (rawContents.size() > 0) {
            //SRZ//OotilityCeption.Log("\u00a78GCT\u00a73 DZC\u00a77 Deserializing \u00a73x" + rawContents.size() + "\u00a77 Contents of \u00a7b" + internalName);

            // For every slot
            for (String rawContent : rawContents) {
                //SRZ//OotilityCeption.Log("\u00a78GCT\u00a73 DZC\u00a77 \u00a7b+\u00a77 " + rawContent);

                // Deserialize it from its condensed form
                GOOPCSlot cSlot = GOOPCSlot.deserialize(rawContent);

                // If not null
                if (cSlot != null) {

                    // Add, its fresh out of the oven
                    slotContents.add(cSlot);
                    //SRZ//OotilityCeption. Log("\u00a78GCT\u00a73 DZC\u00a77 Added at \u00a7e" + cSlot.getSlotNumber() + "\u00a77 as \u00a73" + cSlot.getSlotType() + "\u00a77:" + OotilityCeption.GetItemName(cSlot.getContent()));

                } else {

                    // Shout at the console
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog(warning + "Ignoring invalid content string: \u00a7e" + rawContent); }
                }
            }

        } else {
            // Failure
            failure = true;

            // Note
            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog(error + "Missing contents - Its literally emptier than empty!"); }
        }

        /*
        ItemStack rsItemStack = null;
        Material rsMat = OotilityCeption.getMaterial(rsMaterial);
        if (rsName != null) {

            // Invalid name? Well do we have material?
            if (OotilityCeption.IsInvalidItemNBTtestString(rsName, null)) {

                if (rsMat == null) { rsMat = Material.BARRIER; }

                rsItemStack = OotilityCeption.RenameItem(new ItemStack(rsMat), rsName, null);

            // Generate item from it :deepfry:
            } else {

                rsItemStack = OotilityCeption.ItemFromNBTTestString(rsName, null);

                if (rsItemStack == null) {

                    if (rsMat == null) { rsMat = Material.BARRIER; }

                    rsItemStack = OotilityCeption.RenameItem(new ItemStack(rsMat), rsName, null);
                }
            }
        }
        */

        // Check bounds
        if (containerHeight < 1 || containerHeight > 6) {

            // Fail
            failure = true;
            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog(error + "Number of rows must be between 1 and 6."); }
        }

        // Cancel
        if (failure) { return null; }

        //endregion

        //region Building and Loading

        // Refine contents
        HashMap<Integer, GOOPCSlot> refinedContents = GOOPCTemplate.buildValidSlotsContent(slotContents, containerHeight);

        // Special template types?
        GOOPCTemplate template;
        switch (containerType) {
            case PLAYER:
                template = new GCT_PlayerTemplate(internalName, refinedContents, containerType, displayTitle, edgeFormations);
                ((GCT_PlayerTemplate) template).loadUniqueSlots(slotContents);
                break;
            default:
                template = new GOOPCTemplate(internalName, refinedContents, containerType, displayTitle, edgeFormations);
                break; }

        // Basic
        template.setInternalID(loaded.size());

        // Commands
        template.setCommandsOnClose(onCloseCommand);
        template.setCommandsOnOpen(onOpenCommand);

        // Conditionals & Misc
        template.setAllowDuplicateEquipment(allowDuplicateMMOItems);
        template.setAllowDragEvents(allowDragEvents);
        template.setProvidedDragExtendsToInventory(dragExtendInventory);
        template.setCustomMythicLibRecipe(mythicLibRecipe);

        // Process
        template.reloadAliases();
        template.refreshEquipmentSlots();

        //endregion

        return template;
    }

    /**
     * Load a Container Template
     *
     * @param template Template to register. Unregisters any previous ones of the same Name / ID
     */
    public static void live(@NotNull GOOPCTemplate template, @NotNull FileConfigPair storage) {

        // Unloads any previous one of the same qualifications
        unload(template.getInternalID());
        unload(template.getInternalName());

        // Register in the arrays
        byInternalID.put(template.getInternalID(), template);
        byInternalName.put(template.getInternalName(), template);
        filesByName.put(template.getInternalName(), storage);
        loaded.add(template);
    }
    /**
     * Unload a template of this ID
     *
     * @param byID Internal ID of template to remove
     */
    public static void unload(@Nullable Long byID) {
        if (byID == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCTemplate template = loaded.get(i);

            // Is it the one? No? Skip
            if (template.getInternalID() != byID) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(template.getInternalName());
            byInternalID.remove(template.getInternalID());
            filesByName.remove(template.getInternalName());

            // That's all
            break;
        }
    }
    /**
     * Unload a template of this Name
     *
     * @param byName Internal Name of template to remove
     */
    public static void unload(@Nullable String byName) {
        if (byName == null) { return; }

        // Find
        for (int i = 0; i < loaded.size(); i++) {

            // Get target
            GOOPCTemplate template = loaded.get(i);

            // Is it the one? No? Skip
            if (!template.getInternalName().equals(byName)) { continue; }

            // Unload that shit
            loaded.remove(i);
            byInternalName.remove(template.getInternalName());
            byInternalID.remove(template.getInternalID());
            filesByName.remove(template.getInternalName());

            // That's all
            break;
        }
    }
    /**
     * Clears all arrays
     */
    public static void unloadAll() { loaded.clear(); byInternalName.clear(); byInternalID.clear(); filesByName.clear(); }

    /**
     * @return The Container Templates currently loaded
     */
    @NotNull public static ArrayList<GOOPCTemplate> getLoaded() { return loaded; }
    @NotNull final static ArrayList<GOOPCTemplate> loaded = new ArrayList<>();

    /**
     * @return The Container Templates currently loaded
     */
    @NotNull public static HashMap<Long, GOOPCTemplate> getByInternalID() { return byInternalID; }
    /**
     * @param id ID Currently assigned at this template
     *
     * @return The Container Templates currently loaded of this Internal ID
     */
    @Nullable public static GOOPCTemplate getByInternalID(@Nullable Long id) { if (id == null) { return null; } return getByInternalID().get(id); }
    @NotNull final static HashMap<Long, GOOPCTemplate> byInternalID = new HashMap<>();
    /**
     * @param internalID ID that you want to see if its loaded
     *
     * @return If there is a Container Template of this name
     */
    public static boolean isTemplateLoaded(long internalID) { return getByInternalID(internalID) != null; }

    /**
     * @return The Container Templates currently loaded
     */
    @NotNull public static HashMap<String, GOOPCTemplate> getByInternalName() { return byInternalName; }
    /**
     * @param internalName Name of this template
     *
     * @return The Container Templates currently loaded of this name
     */
    @Nullable public static GOOPCTemplate getByInternalName(@Nullable String internalName) { if (internalName == null) { return null; } return getByInternalName().get(internalName); }
    @NotNull final static HashMap<String, GOOPCTemplate> byInternalName = new HashMap<>();
    /**
     * @param internalName Name that you want to see if its loaded
     *
     * @return If there is a Container Template of this name
     */
    public static boolean isTemplateLoaded(@Nullable String internalName) { return getByInternalName(internalName) != null; }

    /**
     * @return Where is each template stored?
     */
    @NotNull public static HashMap<String, FileConfigPair> getFilesByName() { return filesByName; }
    /**
     * @param internalName Name of the target template
     *
     * @return Where is the target template stored?
     */
    @Nullable public static FileConfigPair getStorage(@Nullable String internalName) {
        if (internalName == null) { return null; }

        FileConfigPair pair = getFilesByName().get(internalName);
        if (pair != null) { return pair; }

        // Create File
        pair = Gunging_Ootilities_Plugin.theMain.GetConfigAt("container-templates/" + internalName, internalName.toLowerCase().replace(" ", "-").replace("_", "-") + ".yml", false, true);

        // Initialize
        ConfigurationSection section = pair.getStorage().getConfigurationSection(internalName);
        if (section == null) { pair.getStorage().createSection(internalName); }

        // Load File
        filesByName.put(internalName, pair);

        // Yes
        return pair;
    }
    @NotNull final static HashMap<String, FileConfigPair> filesByName = new HashMap<>();

    /**
     * Saves all the attributes of all loaded templates
     */
    public static void saveAllLoaded() { for (GOOPCTemplate template : getLoaded()) { saveTemplate(template); } }
    /**
     * Saves all the attributes of this template
     *
     * @param templateID Internal ID of the template.
     */
    public static void saveTemplate(@Nullable Long templateID) { saveTemplate(getByInternalID(templateID)); }
    /**
     * Saves all the attributes of this template
     *
     * @param templateName Internal Name of the template.
     */
    public static void saveTemplate(@Nullable String templateName) { saveTemplate(getByInternalName(templateName)); }
    /**
     * Saves all the attributes of this template
     *
     * @param template Template to save (in the file it came from originally)
     */
    public static void saveTemplate(@Nullable GOOPCTemplate template) {
        if (template == null) { return; }

        // Find its file
        FileConfigPair directory = getStorage(template.getInternalName());
        if (directory == null) { Gunging_Ootilities_Plugin.theOots.CPLog("Could not find the file to save Container Template \u00a7e" + template.getInternalName() + "\u00a7c. This is not good, report to Gunging."); return; }
        directory = Gunging_Ootilities_Plugin.theMain.GetLatest(directory);

        // Modify Storage
        YamlConfiguration storage = directory.getStorage();
        ConfigurationSection section = storage.getConfigurationSection(template.getInternalName());
        if (section == null) { section = storage.createSection(template.getInternalName()); }

        // Basic
        section.set(YML_TEMPLATE_DISPLAY, template.getTitle());
        section.set(YML_TEMPLATE_TYPE, template.getContainerType().toString());
        section.set(YML_TEMPLATE_ROWS, template.getHeight());
        section.set(YML_TEMPLATE_CONTENTS, GOOPCSlot.serializeContents(template.getSlotsContent()));

        // Commands
        section.set(YML_TEMPLATE_CLOSE_COMMAND, template.hasCommandsOnClose() ? template.getCommandsOnClose() : null);
        section.set(YML_TEMPLATE_OPEN_COMMAND, template.hasCommandsOnOpen() ? template.getCommandsOnOpen() : null);

        // Conditional
        section.set(YML_TEMPLATE_FORMATIONS, truePass(template.isEdgeFormations()));
        section.set(YML_TEMPLATE_DUPLICATES, falsePass(template.isAllowDuplicateEquipment()));
        section.set(YML_TEMPLATE_STATION_TYPE, template.getCustomMythicLibRecipe());
        section.set(YML_TEMPLATE_DRAG_ALLOW, template.isAllowDragEvents());
        section.set(YML_TEMPLATE_DRAG_EXTEND, template.isProvidedDragExtendsToInventory());

        // Actually Save
        // storage.set(template.getInternalName(), section);
        Gunging_Ootilities_Plugin.theMain.SaveFile(directory);
    }

    /**
     * @param value A boolean value
     *
     * @return <code>true</code> If the value is true. Otherwise <code>null</code>
     */
    @Nullable protected static Boolean truePass(@Nullable Boolean value) { if (value == null || !value) { return null; } return true; }
    /**
     * @param value A boolean value
     *
     * @return <code>false</code> If the value is false. Otherwise <code>null</code>
     */
    @Nullable protected static Boolean falsePass(@Nullable Boolean value) { if (value == null || value) { return null; } return false; }

    //region YML Configuration Names
    static final String YML_TEMPLATE_ROWS = "NumberOfRows";
    static final String YML_TEMPLATE_TYPE = "ContainerType";
    static final String YML_TEMPLATE_CONTENTS = "Contents";
    static final String YML_TEMPLATE_OPEN_COMMAND = "CommandOnOpen";
    static final String YML_TEMPLATE_CLOSE_COMMAND = "CommandOnClose";
    static final String YML_TEMPLATE_DISPLAY = "DisplayTitle";
    static final String YML_TEMPLATE_FORMATIONS = "EdgeFormations";
    static final String YML_TEMPLATE_DUPLICATES = "AllowDuplicateMMOItems";
    static final String YML_TEMPLATE_STATION_TYPE = "MythicLibStation";
    static final String YML_TEMPLATE_DRAG_ALLOW = "AllowInventoryDrag";
    static final String YML_TEMPLATE_DRAG_EXTEND = "ProvidedDragToInventory";
    //endregion
}
