package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import jdk.internal.loader.BootLoader;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomModelDataLink {

    public static HashMap<Material, HashMap<Integer, CustomModelDataLink>> cmdLinks = new HashMap<>();

    Material parentMaterial;
    Integer linkCMD;
    ArrayList<CMD_Link_Reasons> reasonsOfLink;
    HashMap<CMD_Link_Reasons, Object> linkMeta;
    public CustomModelDataLink(Material parent, Integer link, ArrayList<CMD_Link_Reasons> reasons) {
        parentMaterial = parent;
        linkCMD = link;
        reasonsOfLink = reasons;
        linkMeta = new HashMap<>();
    }

    /**
     * Will append some data to the reason it was linked.
     * <p></p>
     * Will FAIL and not do anything if: <p>
     *  - The reason doesn't exist, or it is not a reason this is linked</p>
     */
    public void AddMeta(@Nullable CMD_Link_Reasons reason, @Nullable Object meta) {

        // If reason exists
        if (reason != null) {

            // If has reason
            if (HasReason(reason)){

                // Append ig
                linkMeta.put(reason, meta);
            }
        }
    }

    /**
     * Gets the meta linked to that reason.
     * <p></p>
     * Will return NULL if: <p>
     *  - The reason doesn't exist, or it is not a reason this is linked</p>
     *  - The meta linked to that reason is NULL</p>
     */
    @Nullable
    public Object GetMeta(@Nullable CMD_Link_Reasons reason) {

        // Reason exists?
        if (reason != null) {

            // Contained?
            if (HasReason(reason)){

                // Doubly Contained?
                if (linkMeta.containsKey(reason)) {

                    // Return thay Ig
                    return linkMeta.get(reason);
                }
            }
        }

        // Noep
        return null;
    }

    public static void ReloadCustomModelDataLinks(OotilityCeption oots) {

        // Starts Fresh
        cmdLinks = new HashMap<>();

        // If there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.customModelDataLinkPair != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.customModelDataLinkPair;
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Log da shit
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()) {

                // Get Mask Name
                String tName = val.getKey();

                //YML//OotilityCeption. Log("\u00a7eName: \u00a77" + tName);

                // Does it parse?
                Material mat = null;
                try {

                    // Parse as MATERIAL
                    mat = Material.valueOf(tName);

                // Invalid
                } catch (IllegalArgumentException e) {

                    oots.CLog(OotilityCeption.LogFormat("CMD Links","Error when loading Material for CMD Link '\u00a73" + tName + "\u00a77': Invalid Material"));
                }

                // Proceed if non-null
                if (mat != null) {

                    //YML//OotilityCeption. Log("\u00a77Material: \u00a7b" + mat.toString());

                    // Get Configuration Section?
                    ConfigurationSection ofgSection = ofgStorage.getConfigurationSection(tName);

                    // If non-null
                    if (ofgSection != null) {

                        //YML//OotilityCeption. Log("\u00a78Step \u00a77Section");

                        for(Map.Entry<String, Object> sect : (ofgSection.getValues(false)).entrySet()) {

                            // Get in format CMD_###
                            String tCMD = sect.getKey();
                            Integer cmd = null;

                            //YML//OotilityCeption. Log("\u00a7eMinor Name: \u00a77" + tCMD);

                            // Parse
                            if (tCMD.contains("CMD_")) {

                                // Get Number alone
                                String unparsed = tCMD.substring("CMD_".length());

                                // Does it parse?
                                if (OotilityCeption.IntTryParse(unparsed)) {

                                    // Parse
                                    cmd = Integer.parseInt(unparsed);
                                } else {

                                    // Log
                                    oots.CLog(OotilityCeption.LogFormat("CMD Links","Error when loading for CMD Link for Material '\u00a73" + tName + "\u00a77': Expected integer number instead of \u00a7e" + unparsed));
                                }

                            } else {

                                // Log
                                oots.CLog(OotilityCeption.LogFormat("CMD Links","Error when loading for CMD Link for Material '\u00a73" + tName + "\u00a77': Link '\u00a7e" + tCMD + "\u00a7' not in the format '\u00a7eCMD_[Integer]\u00a77'"));

                            }

                            // If valid
                            if (cmd != null) {

                                //YML//OotilityCeption. Log("\u00a7eStripped as ID: \u00a7b" + cmd);

                                // Any reason to stop?
                                boolean failure = false;
                                ArrayList<CMD_Link_Reasons> reasons = new ArrayList<>();

                                //region As JSON Block?
                                Boolean asJSON = null;
                                if (ofgSection.contains(tCMD + ".AsJSONFurniture")) { asJSON = ofgSection.getBoolean(tCMD + ".AsJSONFurniture"); }

                                // If not, since its the only thing, it shall stop
                                if (OotilityCeption.If(asJSON)) { reasons.add(CMD_Link_Reasons.AsJSON_Furniture);  } //YML//OotilityCeption. Log("\u00a73 - \u00a77As Furniture")
                                //endregion

                                //region As MMOItem?
                                String asMMOItemParams = null;
                                if (ofgSection.contains(tCMD + ".AsMMOItem")) { asMMOItemParams = ofgSection.getString(tCMD + ".AsMMOItem"); }

                                // Attempted to exist?
                                if (asMMOItemParams != null) {

                                    // Valid?
                                    if (!OotilityCeption.IsInvalidItemNBTtestString(asMMOItemParams, null)) {

                                        // Add reason
                                        reasons.add(CMD_Link_Reasons.AsMMOItem);
                                    }
                                }
                                //endregion

                                // A reason for failure is having no reasons
                                if (reasons.size() == 0) { failure = true; }

                                // Ay parse I guess
                                if (!failure) {

                                    // Create a new CMD Link linking JSON
                                    CustomModelDataLink newCMDL = new CustomModelDataLink(mat, cmd, reasons);

                                    // load
                                    LoadNew(newCMDL);

                                    // Append Metadata
                                    newCMDL.AddMeta(CMD_Link_Reasons.AsMMOItem, asMMOItemParams);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Will that reason, if it doesnt already have it.
     */
    public void AddReason(@NotNull CMD_Link_Reasons reas) { if (!reasonsOfLink.contains(reas)) { reasonsOfLink.add(reas); } }

    /**
     * Will remove that reason, if it has it.
     */
    public void RemoveReason(@NotNull CMD_Link_Reasons reas) { reasonsOfLink.remove(reas); }

    public void Save() {

        // Get source configuration
        FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.customModelDataLinkPair;

        // Get the latest version of the storage
        ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

        // Modify Storage
        YamlConfiguration ofgStorage = ofgPair.getStorage();

        // Get
        ConfigurationSection ofgSection = ofgStorage.getConfigurationSection(getParentMaterial().toString());

        // Create config section
        if (ofgSection == null) { ofgSection = ofgStorage.createSection(getParentMaterial().toString()); }

        // Get Path
        String path = "CMD_" + getCustomModelData().toString();

        // Reset Path
        ofgSection.set(path, null);

        // For every reason
        for (CMD_Link_Reasons reason : reasonsOfLink) {

            // Which is it?
            switch (reason) {
                case AsJSON_Furniture:

                    // Store thay
                    ofgSection.set(path + ".AsJSONFurniture", true);
                    break;
                case AsMMOItem:

                    // Must have meta, otherwise I snooze
                    Object meta = GetMeta(CMD_Link_Reasons.AsMMOItem); String truMeta = null;
                    if (meta instanceof String) { truMeta = (String) meta; }

                    // Store thay
                    ofgSection.set(path + ".AsMMOItem", truMeta);
                    break;
                default: break;
            }
        }

        // Save
        Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);
    }

    public boolean HasReason(CMD_Link_Reasons reas) { return reasonsOfLink.contains(reas); }
    public Material getParentMaterial() { return parentMaterial; }
    public Integer getCustomModelData() { return linkCMD; }
    public ArrayList<CMD_Link_Reasons> getReasons() { return reasonsOfLink; }

    /**
     * MAKE SURE THERE ISNT ONE ALREADY LOADED as it will be overwritten
     */
    public static void LoadNew(CustomModelDataLink unloaded) {

        // Load
        cmdLinks.computeIfAbsent(unloaded.getParentMaterial(), k -> new HashMap<>());

        // Get thay
        HashMap<Integer, CustomModelDataLink> intLinks = cmdLinks.get(unloaded.getParentMaterial());

        // Override
        intLinks.put(unloaded.getCustomModelData(), unloaded);

        // Put ig
        cmdLinks.put(unloaded.getParentMaterial(), intLinks);
    }

    /**
     * Attempts to get existing. Returns null if none is defined for this item, or this item doesnt even qualify to have one.
     */
    public static CustomModelDataLink getFrom(ItemStack iSource) {

        if (OotilityCeption.IsAirNullAllowed(iSource)) { return null; }

        // No CMD no Service
        if (!iSource.hasItemMeta()) { return null; }
        if (!iSource.getItemMeta().hasCustomModelData()) {  return null; }

        // Return Custom Model Data of iSource
        return getFrom(iSource.getType(), iSource.getItemMeta().getCustomModelData());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CustomModelDataLink)) { return false; }

        // Both material and CMD must equal
        return ((CustomModelDataLink) obj).getParentMaterial().equals(getParentMaterial())
                && ((CustomModelDataLink) obj).getCustomModelData().equals(getCustomModelData());
    }

    @NotNull public ItemStack toStack() {

        // Create new
        ItemStack stack = new ItemStack(getParentMaterial());

        // Meta?
        if (!stack.hasItemMeta()) { return stack; }

        // Get meta
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) { return stack; }

        // Set
        meta.setCustomModelData(getCustomModelData());

        // Yes
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * @param material The material in query.
     *
     * @param modelData The custom model data in query
     *
     * @return If the link exists, that one that is loaded corresponding to
     *         this material and custom model data number.
     */
    @Nullable public static CustomModelDataLink getFrom(@NotNull Material material, @NotNull Integer modelData) {

        // Find array
        HashMap<Integer, CustomModelDataLink> loadedLinks = cmdLinks.get(material);

        // Missing?
        if (loadedLinks == null) { return null; }

        // Return the one that matches the model data.
        return loadedLinks.get(modelData);
    }
}
