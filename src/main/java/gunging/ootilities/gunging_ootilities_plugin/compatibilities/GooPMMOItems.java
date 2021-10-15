package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.google.common.collect.Multimap;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.ContainerToMIInventory;
import gunging.ootilities.gunging_ootilities_plugin.misc.PlusMinusPercent;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.LuckStat;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.XBow_Loaded_Stat;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.interaction.GemStone;
import net.Indyuce.mmoitems.api.interaction.UseItem;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.template.NameModifier;
import net.Indyuce.mmoitems.api.item.template.TemplateModifier;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.player.inventory.EquippedPlayerItem;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.Indyuce.mmoitems.stat.data.*;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class GooPMMOItems {

    public boolean CompatibilityCheck() {

        // You got that on you?
        MMOItems.plugin.getTypes().get(null);

        /*
         * Do not erase in the future.
         *
         * Registers the container equipment slots, I guess at one
         * point this measured that MMOItems was up-to-date and that
         * is how this method ended up here.
         */
        return RegisterContainersEquipment();
    }

    static boolean rced = false;
    public static boolean RegisterContainersEquipment() {
        if (rced) { return Gunging_Ootilities_Plugin.foundMMOItems; }
        if(MMOItems.plugin == null) { return false; }
        MMOItems.plugin.registerPlayerInventory(new ContainerToMIInventory());
        //DBG//OotilityCeption.Log("Registerer Containers MIInventory");
        rced = true;
        return true;
    }

    @NotNull public static LiveMMOItem LiveFromNBT(@NotNull NBTItem nbt) {

        // Only if MMOItems gets disabled would this not work
        try {

            // Create new :wazowskibruhmoment:
            return new LiveMMOItem(nbt);

        // Honestly what could possibly go wrong
        } catch (Exception e) {

            Gunging_Ootilities_Plugin.theOots.CPLog("Something weird happened when trying to read a MMOItem, \u00a7cis MMOItems working correctly?");
            e.printStackTrace();
            //noinspection ConstantConditions
            return null;
        }
    }
    @NotNull public static VolatileMMOItem VolatileFromNBT(@NotNull NBTItem nbt) {

        // Only if MMOItems gets disabled would this not work
        try {

            // Create new :wazowskibruhmoment:
            return new VolatileMMOItem(nbt);

        // Honestly what could possibly go wrong
        } catch (Exception e) {

            Gunging_Ootilities_Plugin.theOots.CPLog("Something weird happened when trying to read a MMOItem, \u00a7cis MMOItems working correctly?");
            e.printStackTrace();
            //noinspection ConstantConditions
            return null;
        }
    }

    //region Easy Get of MMOItems as ItemStacks
    /**
     * Gets an MMOItem of such Type and ID if it exists.
     * @return Structure Void Block saying 'Invalid MMOItem' if it doesnt exist.
     */
    @NotNull
    public static ItemStack GetMMOItemOrDefault(@Nullable String type, @Nullable  String id) {

        // Try to get
        ItemStack res = GetMMOItem(type, id);

        // Return 'Default'
        if (res == null) { res = OotilityCeption.RenameItem(new ItemStack(Material.STRUCTURE_VOID), "\u00a7cInvalid MMOItem", null); }

        //DBG*/Gunging_Ootilities_Plugin.theOots.CLog("Providing " + OotilityCeption.GetItemName(res));
        //noinspection ConstantConditions
        return res;
    }

    /**
     * Gets an MMOItem of such Type and ID if it exists.
     * @return NULL If it doesnt exist.
     */
    @Nullable
    public static ItemStack GetMMOItem(@Nullable String type, @Nullable String id) {
        return MMOItems.plugin.getItem(type, id);
    }

    /**
     * The MMOItems reforger allows to update MMOItems in several levels:   <br>
     * + Completely Replace         <br>
     * + Reforge (reroll RNG)       <br>
     * + Update (keep previous RNG) <br>
     *  <p></p>
     * One may also specify which things to keep :)
     *
     * @param item ItemStack before knowing if it even is an MMOItem.
     *
     * @param regenParams In the following order, which things to keep: <br>
     *                    <p><code>(no params)</code> Nothing, regenerate completely.
     *                    </p><code>0</code> Keep Name?
     *                    <p><code>1</code> Keep Lore?
     *                    </p><code>2</code> Keep Enchantments?
     *                    <p><code>3</code> Keep Upgrades?
     *                    </p><code>4</code> Keep Gemstones?
     *                    <p><code>5</code> Keep Soulbound?
     *                    </p><code>6</code> Keep External SH?
     *                    </p><code>7</code> Keep RNG Rolls?
     *                    </p><code>8</code> Keep Modifiers?
     *                    </p><code>9</code> Keep AEnchantments?
     *
     * @return A regenerated ItemStack if successful.
     */
    @Nullable public static ItemStack ReforgeMMOItem(@NotNull ItemStack item, boolean... regenParams) {

        // Create and use reforger
        MMOItemReforger mod = new MMOItemReforger(item);

        // Valid?
        if (!mod.canReforge()) { return null; }

        // Proc
        if (!mod.reforge(new ReforgeOptions(regenParams))) { return null; }

        // Output
        return mod.getResult();
    }

    /**
     * Gets a MMOItem as the actual class itself from the plugin
     * (while other methods return it as a built ItemStack).
     */
    @Nullable public static MMOItem GetMMOItemRaw(@NotNull String type, @NotNull String id) {
        //noinspection ConstantConditions
        return MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(type), id);
    }

    /**
     * Just returns the MMOItem type, if it exists.
     * @return NULL if it doesnt
     */
    @Nullable public static Type GetMMOItemTypeFromString(@Nullable String type) {
        return MMOItems.plugin.getTypes().get(type);
    }

    public static boolean IsConsumable(@Nullable ItemStack consumable, @NotNull Player player) {
        if (consumable == null) { return false; }
        if (!IsMMOItem(consumable)) { return false; }
        return IsConsumable(NBTItem.get(consumable), player);
    }

    public static boolean IsConsumable(@NotNull NBTItem consumable, @NotNull Player player) {

        // Decrease usage if consumable
        return (UseItem.getItem(player, consumable, GetMMOItemType(consumable)) instanceof Consumable);
    }

    public static boolean IsGemstone(@Nullable ItemStack gemstone, @NotNull Player player) {
        if (gemstone == null) { return false; }
        if (!IsMMOItem(gemstone)) { return false; }
        return IsGemstone(NBTItem.get(gemstone), player);
    }

    public static boolean IsGemstone(@NotNull NBTItem gemstone, @NotNull Player player) {

        // Decrease usage if consumable
        return (UseItem.getItem(player, gemstone, GetMMOItemType(gemstone)) instanceof GemStone);
    }

    @NotNull
    public static UseItem GetUseItem(@NotNull NBTItem itm, @NotNull Player player) {

        // Decrease usage if consumable
        return UseItem.getItem(player, itm, GetMMOItemType(itm));
    }
    //endregion

    //region Stat Registry

    public static ItemStat XBOW_LOADED_STAT;
    public static ItemStat GROUND_POUND_STAT;
    public static ItemStat LUCK;
    public static ItemStat MINIONS;
    public static ItemStat APPLICABLE_COMMAND;
    public static ItemStat APPLICABLE_MASK;
    public static ItemStat APPLICABLE_CONSUME;
    public static ItemStat APPLICABLE_LIMIT;
    public static ItemStat APPLICABLE_CLASS;
    public static ItemStat APPLICABLE_TIMES;
    public static ItemStat HAT;
    public static ItemStat REVARIABLE;
    public static ItemStat CONTAINER;
    public static ItemStat ONKILL_COMMAND;
    public static ItemStat ONHIT_COMMAND;
    //region public static ItemStat MISC;
    public static HashMap<String, ItemStat> MISC = new HashMap<>();
    static Material[] miscMats = new Material[]{Material.PUFFERFISH_BUCKET, Material.TROPICAL_FISH_BUCKET, Material.SALMON_BUCKET, Material.COD_BUCKET, Material.WATER_BUCKET, Material.MILK_BUCKET, Material.LAVA_BUCKET, Material.BUCKET};
    static String[] miscOrder = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    @Nullable public static ItemStat GetMiscStat(@NotNull String key) {

        // Convert to nomenklature
        String k = key.toUpperCase();
        if (k.startsWith("MISC")) { k = k.substring("MISC".length()); }
        if (k.startsWith(miscPrefix)) { k = k.substring(miscPrefix.length()); }

        // Find
        return MISC.get(k);
    }

    static int currentMisc = 0, generation = 0, currentMat = 0;
    static final String miscPrefix = "GOOP_MISC_";
    public static void RegisterMiscStat() {

        // Get Name
        StringBuilder mOrder = new StringBuilder(miscPrefix);
        mOrder.append(miscOrder[currentMisc]);
        if (generation > 0) { mOrder.append(generation); }
        currentMisc++; if (currentMisc >= miscOrder.length) { currentMisc = 0; generation++; }
        String name = mOrder.toString();
        String terminology = name.substring(miscPrefix.length());

        // Get Material
        Material mMat = miscMats[currentMat];
        currentMat++; if (currentMat >= miscMats.length) { currentMat = 0; }

        ItemStat MISCC = new DoubleStat(name, mMat, "Extra Stat \u00a7l" + terminology, new String[]{"Doesnt do anything by itself.", "\u00a7a", "You can retrieve it in mythic", "skills though, using these:", "\u00a7e<goop.castermmostat.misc" + terminology.toLowerCase() + ">", "\u00a7e<goop.triggermmostat.misc" + terminology.toLowerCase() + ">"}, new String[]{"!consumable", "!miscellaneous", "all"});
        RegisterStat(name, MISCC);
        MISC.put(terminology, MISCC);
    }
    //endregion

    public static void RegisterCustomStats(int miscAmount) {
        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) {
            XBOW_LOADED_STAT = new XBow_Loaded_Stat();
            RegisterStat(XBOW_LOADED_STAT); }

        LUCK = new LuckStat();
        RegisterStat(LUCK);

        HAT = new BooleanStat("GOOP_HAT", Material.CHAINMAIL_HELMET, "Hat / Helmet", new String[]{"This item will automatically equip", "in the head slot if:", "\u00a7a + \u00a77It is Shift+LeftClicked in inventory", "\u00a7a + \u00a77It is placed on the helmet slot.", "", "This will not:", "\u00a7c - \u00a77Prevent blocks from being placed (disable interactions for this)", "\u00a7c - \u00a77Equip it to the head if the item is right-clicked while held."}, new String[]{"armor", "miscellaneous", "consumable"});
        RegisterStat(HAT);

        for (int m = 1; m <= miscAmount; m++) { RegisterMiscStat(); }

        GROUND_POUND_STAT = new StringStat("GROUND_POUND_SKILL", Material.COARSE_DIRT, "Ground Pound Skill", new String[]{"When the item is dropped to the ground,", "what mythicmob skill to run when", "it hits the ground?", "", "\u00a76Player that dropped the item: \u00a7f@Self", "\u00a76Item Entity Itself: \u00a7e@Trigger"}, new String[]{"all"});
        RegisterStat(GROUND_POUND_STAT);

        if (Gunging_Ootilities_Plugin.foundMythicMobs) {
            MINIONS = new DoubleStat("GOOP_MINIONS", Material.TOTEM_OF_UNDYING, "Max Minions", new String[]{"The max number of entities that this player", "can have as minions, will kill the oldest", "one if this is exceeded and a new spawns.", "", "\u00a7e/goop mythicmobs minion"}, new String[]{"!consumable", "!miscellaneous", "all"});
            RegisterStat(MINIONS);
        }

        ONKILL_COMMAND = new StringStat("GOOP_MM_KILL", Material.BONE, "On Kill Command", new String[]{"Run a command when murdering something", "while having this item equipped.", "", "\u00a73Maybe run a mythic skill?", "\u00a7b/goop mythicmobs runSkillAs <SKILL> %player% @%victim%"}, new String[]{"!consumable", "!miscellaneous", "!skin", "all", "!skin", "!miscellaneous", "!consumable" });
        RegisterStat(ONKILL_COMMAND);

        ONHIT_COMMAND = new StringStat("GOOP_MM_HIT", Material.STONE_SWORD, "On Hit Command", new String[]{"Run a command when damaging something", "while having this item equipped.", "", "\u00a73Maybe run a mythic skill?", "\u00a7b/goop mythicmobs runSkillAs <SKILL> %player% @%victim%"}, new String[]{"!consumable", "!miscellaneous", "!skin", "all", "!skin", "!miscellaneous", "!consumable" });
        RegisterStat(ONHIT_COMMAND);

        APPLICABLE_COMMAND = new StringStat("GOOP_APPLY_COMMAND", Material.FILLED_MAP, "GooP OnApply Command", new String[]{"What command is run when this item", "is applied on other items?", "\u00a7a", "\u00a7aRestrict which types this can be", "\u00a7aapplied to with an apply mask.", "\u00a7a", "\u00a73Supports GooP commands by choosing slot", "\u00a73as \u00a7a%player% %provided-slot%\u00a73;", "\u00a7b\u00a7lPAPI supported."}, new String[]{"all"});
        RegisterStat(APPLICABLE_COMMAND);

        APPLICABLE_MASK = new StringStat("GOOP_APPLY_MASK", Material.FILLED_MAP, "GooP OnApply Mask", new String[]{"Define Masks in \u00a7bonapply-masks.yml", "\u00a7a", "Restricts to which MMOItem types", "this item can be applied to IF", "it has an OnApply Command."}, new String[]{"!gem_stone", "all", "!gem_stone"});
        RegisterStat(APPLICABLE_MASK);

        APPLICABLE_CONSUME = new BooleanStat("GOOP_APPLY_CONSUME", Material.PAPER, "Not Consume on GooP OnApply", new String[]{"Consumables are consumed when using their OnApply.", "This will prevent them from doing so."}, new String[]{"consumable"});
        RegisterStat(APPLICABLE_CONSUME);

        APPLICABLE_CLASS = new StringStat("GOOP_APPLY_CLASS", Material.MAP, "GooP OnApply Reference", new String[]{"Used to limit how many times the", "OnApply Command can run on the", "same item.", "", "\u00a7bIf this is not specified, but a limit is,", "\u00a7bthe limit will apply to only this MMOItem."}, new String[]{"all"});
        RegisterStat(APPLICABLE_CLASS);

        APPLICABLE_LIMIT = new DoubleStat("GOOP_APPLY_LIMIT", Material.MAP, "GooP OnApply Limit", new String[]{"Used to limit how many times the", "OnApply Command can run on the", "same item.", "", "\u00a73This is the number of times OnApply", "\u00a73commands of the same \u00a7bReference\u00a73 will", "\u00a73act upon the same item."}, new String[]{"all"});
        RegisterStat(APPLICABLE_LIMIT);

        APPLICABLE_TIMES = new StringListStat("GOOP_APPLY_TIMES", Material.LIGHT_GRAY_STAINED_GLASS_PANE, "GooP OnApply Repetitions", new String[]{"Tech stat that keeps track of how", "many times an OnApply item has been", "used on this item.", "", "\u00a76I guess you could use it to make", "\u00a76an item think an OnApply has been used", "\u00a76a few times on it already.", "", "\u00a78Format: &n[reference] [number of times]", "\u00a7cEx: &nUpgradeRef 2"}, new String[]{"all"});
        RegisterStat(APPLICABLE_TIMES);

        CONTAINER = new StringStat("GOOP_CONTAINER", Material.CHEST, "GooP Container", new String[]{"This item will serve as a a bag!", "Right-Click to open as if it was a backpack", "", "\u00a7cThe container uuid will be saved in this item.", "\u00a78Can only be opened as a single stack."}, new String[]{"!consumable", "!gem_stone", "!musket", "!bow", "!crossbow", "!lute", "!skin", "all", "!skin", "!consumable", "!gem_stone", "!musket", "!bow", "!crossbow", "!lute"});
        RegisterStat(CONTAINER);

        REVARIABLE = new StringStat("GOOP_REVARIABLE", Material.NAME_TAG, "Revariable", new String[]{"When a player puts this in the second", "anvil slot, and renames the item in", "the first anvil slot, this variable will", "be rewritten by the renaming operation.", "", "\u00a7cCase sensitive\u00a77, no spaces, preferably alphanumeric.", "", "\u00a7cNot working with MMOItems anymore :c"}, new String[]{"miscellaneous", "consumable"});
        RegisterStat(REVARIABLE);
    }

    public static void RegisterStat(@Deprecated String legacyID, @NotNull ItemStat statt) { RegisterStat(statt); }
    public static void RegisterStat(@NotNull ItemStat statt) {
        if (!Gunging_Ootilities_Plugin.foundMMOItems) { return; }
        if (MMOItems.plugin == null || MMOItems.plugin.getStats() == null) { Gunging_Ootilities_Plugin.foundMMOItems = false; return; }
        MMOItems.plugin.getStats().register(statt);
    }
    //endregion

    //region Getting Values (as well as the cummulative forms)

    /**
     * Gets the MMOItems this player has equipped.
     */
    @NotNull public static ArrayList<VolatileMMOItem> GetPlayerEquipment(@NotNull Player player) {

        // Ret
        ArrayList<VolatileMMOItem> vot = new ArrayList<>();

        // Get Eq
        PlayerData p = PlayerData.get(player);
        for (EquippedPlayerItem e : p.getInventory().getEquipped()) {

            // Add
            vot.add(e.getItem());
        }

        // Return thay
        return vot;
    }

    public static double CummaltiveEquipmentDoubleStatValue(@NotNull Player target, @NotNull String statname, double base) {
        Double ret = CummaltiveEquipmentDoubleStatValue(target, statname);
        if (ret == null) { ret = base; } else { ret += base; }
        return ret;
    }
    public static double CummaltiveEquipmentDoubleStatValue(@NotNull Player target, @Nullable ItemStat statname, double base) {
        Double ret = CummaltiveEquipmentDoubleStatValue(target, statname);
        if (ret == null) { ret = base; } else { ret += base; }
        return ret;
    }
    @Nullable public static Double CummaltiveEquipmentDoubleStatValue(@NotNull Player target, @NotNull String statname) {

        // Cut
        String bakedStatname = statname.toLowerCase();
        if (bakedStatname.startsWith("misc")) { bakedStatname = "misc"; }

        // Get Statt
        switch (bakedStatname) {
            case "attackdamage":
            case "adamage":
            case "admg":
            case "attack":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.ATTACK_DAMAGE));
            case "attackspeed":
            case "aspeed":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.ATTACK_SPEED));
            case "armor":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.ARMOR));
            case "defnse":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.DEFENSE));
            case "armortoughness":
            case "atoughness":
            case "toughness":
            case "atough":
            case "at":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.ARMOR_TOUGHNESS));
            case "knockbackresistance":
            case "kresistance":
            case "knockback":
            case "kres":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.KNOCKBACK_RESISTANCE));
            case "movementspeed":
            case "mspeed":
            case "speed":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.MOVEMENT_SPEED));
            case "maxhealth":
            case "mhealth":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.MAX_HEALTH));
            case "luck":
                return CummaltiveEquipmentDoubleStatValue(target, LUCK);
            case "criticalstrikechance":
            case "critchance":
            case "criticalchance":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.CRITICAL_STRIKE_CHANCE));
            case "criticalstrikepower":
            case "critpower":
            case "critpow":
            case "criticalpower":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.CRITICAL_STRIKE_POWER));
            case "weapondamage":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.WEAPON_DAMAGE));
            case "skilldamage":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.SKILL_DAMAGE));
            case "projectiledamage":
            case "projdamage":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.PROJECTILE_DAMAGE));
            case "magicdamage":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.MAGIC_DAMAGE));
            case "physicaldamage":
            case "meleedamage":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.PHYSICAL_DAMAGE));
            case "undeaddamage":
                return CummaltiveEquipmentDoubleStatValue(target, Stat(GooPMMOItemsItemStats.UNDEAD_DAMAGE));
            case "misc":
                return CummaltiveEquipmentDoubleStatValue(target, GetMiscStat(statname));

            default: return CummaltiveEquipmentDoubleStatValue(target, Stat(bakedStatname));
        }
    }

    /**
     * Will sum the value of this stat throughout the player's equipment.
     */
    @Nullable public static Double CummaltiveEquipmentDoubleStatValue(@NotNull Player target, @Nullable ItemStat statName) {

        // If null no
        if (statName == null) { return null; }

        // Attempt via Mythic Lib
        try {

            // IF it works it works
            MMOPlayerData mmo = MMOPlayerData.get(target.getUniqueId());
            return mmo.getStatMap().getStat(statName.getId());

        } catch (Exception ignored) { }

        // Will return null if player doesnt have any at all
        Double ret = null;

        // Get all the items
        ArrayList<VolatileMMOItem> pItems = GetPlayerEquipment(target);

        // I dont know how many there
        for (MMOItem pItem : pItems) {

            if (pItem != null) {

                // Get its stat
                Double cValue = GetDoubleStatValue(pItem, statName);

                // Ad if non-null
                if (cValue != null) {

                    // Set if current is null
                    if (ret == null) {

                        // Set
                        ret = cValue;

                        // Value existed already
                    } else {

                        // Add
                        ret += cValue;
                    }

                    //DBG//oots.C Log("\u00a7e - \u00a77" + OotilityCeption.GetItemName(mod.getItem()) + "\u00a7 " + cValue + " \u00a73Cummulative " + ret);
                }
            }

        }


        // Return the result
        return ret;
    }
    /**
     * If it is base TRUE, a single FALSE will make it FALSE forever.<p></p>
     * If it is base FALSE, a single TRUE will make it TRUE forever
     */
    public static boolean CummaltiveEquipmentBooleanStatValue(@NotNull Player target, @Nullable ItemStat statName, boolean base) {
        if (statName == null) { return base; }
        boolean ret = base;

        //DBG//OotilityCeption oots = new OotilityCeption();
        //DBG//oots.C Log("Getting cummulative \u00a7e" + statName.getName() + "\u00a77 from \u00a73" + target.getName());

        // Get all the items
        ArrayList<VolatileMMOItem> pItems = GetPlayerEquipment(target);

        // I dont know how many there
        for (MMOItem pItem : pItems) {

            if (pItem != null) {

                // Get its stat
                Boolean cValue = GetBooleanStatValue(pItem, statName);

                // If there was a value
                if (cValue != null) {

                    // If it is natively TRUE, a single FALSE will make it FALSE forever
                    if (base) {

                        // Both must be true
                        ret = ret && cValue;

                        // If it is natively FALSE, a single TRUE will make it TRUE forever
                    } else {

                        ret = ret || cValue;
                    }
                    //DBG//oots.C Log("\u00a7e - \u00a77" + OotilityCeption.GetItemName(mod.getItem()) + "\u00a7 " + cValue + " \u00a73Cummulative " + ret);
                }
            }
        }

        // Return the result
        return ret;
    }
    /**
     * Will return an empty array if it fails for any reason.
     */
    @NotNull public static ArrayList<String> CummaltiveEquipmentStringStatValue(@NotNull Player target, @Nullable ItemStat statName) {

        // Will just count the number of unique entries
        ArrayList<String> ret = new ArrayList<>();
        if (statName == null) { return ret; }

        // Get all the items
        ArrayList<VolatileMMOItem> pItems = GetPlayerEquipment(target);

        // I dont know how many there
        for (MMOItem pItem : pItems) {

            if (pItem != null) {

                // Get its stat
                String cValue = GetStringStatValue(pItem, statName);

                // Ad if non-null
                if (cValue != null) {

                    // Add if it is not contained
                    if (!ret.contains(cValue)) { ret.add(cValue); }

                    //DBG//oots.C Log("\u00a7e - \u00a77" + OotilityCeption.GetItemName(mod.getItem()) + "\u00a7 " + cValue + " \u00a73Cummulative " + ret);
                }
            }
        }

        // Return the result
        return ret;
    }

    /**
     * Returns the double data of such stat from that item if it exists (MAKE SURE YOU KNOW IT IS A DOUBLE STAT).
     * <p></p>
     * 'If such stat from that item exists' means that this will return a double if and only if:
     * <p>+ The stat exists and it is a DoubleStat
     * </p>+ The item is a MMOItem that has a value for it.
     * <p></p>
     * All other cases return NULL.
     * @param itemThatMayNotEvenBeAMMOItem Some ItemStack you have.
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @return NULL if there is no such thing to retrieve, or the player doesnt meet the requirements to use.
     */
    @Nullable
    public static Double GetDoubleStatValue(@Nullable ItemStack itemThatMayNotEvenBeAMMOItem, @Nullable ItemStat statName) { return GetDoubleStatValue(itemThatMayNotEvenBeAMMOItem, statName, null, false); }

    /**
     * Returns the double data of such stat from that item if it exists (MAKE SURE YOU KNOW IT IS A DOUBLE STAT).
     * @param mmoitemAsNBTItem NBT Item containing the item stat.
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @return NULL if there is no such thing to retrieve, or the player doesnt meet the requirements to use.
     */
    @Nullable
    public static Double GetDoubleStatValue(@Nullable NBTItem mmoitemAsNBTItem, @Nullable ItemStat statName) { return GetDoubleStatValue(mmoitemAsNBTItem, statName, null, false); }

    /**
     * Gets the Double Value from a stat (MAKE SURE YOU KNOW IT IS A DOUBLE STAT)
     * @param item Item containing the value
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @param considerRPGrequirements Player to reject if they dont meet class reqs and stuff
     * @param silent If they fail the RPG reqs, should the fail message be displayed?
     * @return The value of the stat in the item, or NULL if the item doesn't ahve the stat, or it is not a MMOItem
     */
    @Nullable
    public static Double GetDoubleStatValue(@Nullable ItemStack item, @Nullable ItemStat statName, @Nullable Player considerRPGrequirements, boolean silent) {

        // Test if MMOItem
        if (IsMMOItem(item)) {

            // Return Value
            return GetDoubleStatValue(NBTItem.get(item), statName, considerRPGrequirements, silent);
        }

        return null;
    }

    /**
     * Returns the double data of such stat from that item if it exists (MAKE SURE YOU KNOW IT IS A DOUBLE STAT).
     * @param mmoitemAsNBTItem NBT Item containing the item stat.
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @param considerRPGrequirements Player to test RPG requirements gainst, leave NULL to ignore RPG requirements.
     * @param silent if there is a player to test RPG requirements against, hide the message 'You dont have the correct class' if they fail the test.
     * @return NULL if there is no such thing to retrieve, or the player doesnt meet the requirements to use.
     */
    @Nullable
    public static Double GetDoubleStatValue(@Nullable NBTItem mmoitemAsNBTItem, @Nullable ItemStat statName, @Nullable Player considerRPGrequirements, boolean silent) {
        if (mmoitemAsNBTItem == null) { return null; }
        if (statName == null) { return null; }

        // Consider RPG requirements
        if (MeetsRequirements(considerRPGrequirements, mmoitemAsNBTItem, !silent)) {

            // Convert and Return
            return GetDoubleStatValue(VolatileFromNBT(mmoitemAsNBTItem), statName);
        }

        // Didnt meet the requirements - null
        return null;
    }

    /**
     * Returns the double data of such stat from that item if it exists.
     * @return NULL if there is no such thing to retrieve.
     */
    @Nullable
    public static Double GetDoubleStatValue(@Nullable MMOItem mmoitem, @Nullable ItemStat statName) {
        if (mmoitem == null) { return null; }
        if (statName == null) { return null; }

        // If the player meets the requirements, return the value
        if (mmoitem.hasData(statName)) {

            DoubleData dddData = (DoubleData) mmoitem.getData(statName);
            if (dddData != null) { return dddData.getValue(); }
        }

        return null;
    }

    @Nullable
    public static ArrayList<String> GetStringListStatValue(@Nullable ItemStack itemThatMayNotEvenBeAMMOItem, @Nullable ItemStat statName) { return GetStringListStatValue(itemThatMayNotEvenBeAMMOItem, statName, null, false); }
    /**
     * Returns the double data of such stat from that item if it exists (MAKE SURE YOU KNOW IT IS A DOUBLE STAT).
     * @param mmoitemAsNBTItem NBT Item containing the item stat.
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @return NULL if there is no such thing to retrieve, or the player doesnt meet the requirements to use.
     */
    @Nullable

    /**
     * Returns the double data of such stat from that item if it exists (MAKE SURE YOU KNOW IT IS A DOUBLE STAT).
     * @param mmoitemAsNBTItem NBT Item containing the item stat.
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @param considerRPGrequirements Player to test RPG requirements gainst, leave NULL to ignore RPG requirements.
     * @param silent if there is a player to test RPG requirements against, hide the message 'You dont have the correct class' if they fail the test.
     * @return NULL if there is no such thing to retrieve, or the player doesnt meet the requirements to use.
     */
    public static ArrayList<String> GetStringListStatValue(@Nullable NBTItem mmoitemAsNBTItem, @Nullable ItemStat statName) {
        if (mmoitemAsNBTItem == null) { return null; }

        // Consider RPG requirements
        MeetsRequirements(null, mmoitemAsNBTItem, true);

        // Convert and Return
        return GetStringListStatValue(VolatileFromNBT(mmoitemAsNBTItem), statName);

        // Didnt meet the requirements - null
    }


    /**
     * Returns the double data of such stat from that item if it exists.
     * @return NULL if there is no such thing to retrieve.
     */
    @Nullable
    public static ArrayList<String> GetStringListStatValue(@Nullable MMOItem mmoitem, @Nullable ItemStat statName) {
        if (mmoitem == null) { return null; }
        if (statName == null) { return null; }
        if (!mmoitem.hasData(statName)) { return null; }

        // Get data
        StatData sData = mmoitem.getData(statName);

        // Found?
        if (sData != null) {

            // Is it string list?
            if (sData instanceof StringListData) {

                return ValueOfStringListData(sData);

            } else if (sData instanceof GemSocketsData) {

                // Convert into string list of gem scokets
                return new ArrayList<>(((GemSocketsData)sData).getEmptySlots());
            }
        }

        return null;
    }


    @Nullable
    public static ArrayList<String> ValueOfStringListData(@Nullable StatData stat) {

        if (stat instanceof StringListData) {
            return new ArrayList<String>(((StringListData) stat).getList());
        }

        return null;
    }

    /**
     * Gets the Double Value from a stat (MAKE SURE YOU KNOW IT IS A DOUBLE STAT)
     * @param itemThatMayNotEvenBeAMMOItem Item containing the value
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @param considerRPGrequirements Player to reject if they dont meet class reqs and stuff
     * @param silent If they fail the RPG reqs, should the fail message be displayed?
     * @return The value of the stat in the item, or NULL if the item doesn't ahve the stat, or it is not a MMOItem
     */
    @Nullable
    public static ArrayList<String> GetStringListStatValue(@Nullable ItemStack itemThatMayNotEvenBeAMMOItem, @Nullable ItemStat statName, @Nullable Player considerRPGrequirements, boolean silent) {

        // Test if MMOItem
        if (IsMMOItem(itemThatMayNotEvenBeAMMOItem)) {

            NBTItem itm = NBTItem.get(itemThatMayNotEvenBeAMMOItem);


            if (MeetsRequirements(considerRPGrequirements, itm, !silent)) {


                // Return Value
                return GetStringListStatValue(itm, statName);
            }
        }

        return null;
    }


    /**
     * Gets the Boolean Value from a stat (MAKE SURE YOU KNOW IT IS A BOOLEAN STAT)
     * @param itemThatMayNotEvenBeAMMOItem Item containing the value
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @param considerRPGrequirements Player to reject if they dont meet class reqs and stuff
     * @param silent If they fail the RPG reqs, should the fail message be displayed?
     * @return The value of the stat in the item, or NULL if the item doesn't ahve the stat, or it is not a MMOItem
     */
    @Nullable
    public static Boolean GetBooleanStatValue(@Nullable ItemStack itemThatMayNotEvenBeAMMOItem, @NotNull ItemStat statName, @Nullable Player considerRPGrequirements, boolean silent) {

        if (IsMMOItem(itemThatMayNotEvenBeAMMOItem)) {
            //STAT//OotilityCeption. Log(" §c+§7 As MMOItem");

            // Yes it is, does it have a ... tag?
            return GetBooleanStatValue(NBTItem.get(itemThatMayNotEvenBeAMMOItem), statName, considerRPGrequirements, silent);
        }

        //STAT//OotilityCeption. Log(" §c- Fail");
        return null;
    }

    public static String GetStringStatValue(@Nullable MMOItem mmoitem, @Nullable ItemStat statName) {
        if (mmoitem == null) { return null; }
        if (statName == null) { return null; }

        // Has data??? >:O
        if (mmoitem.hasData(statName)) {

            // If the player meets the requirements, return the value
            StringData sData = (StringData) mmoitem.getData(statName);
            if (sData != null) { return sData.toString(); }
        }

        return null;
    }

    @Nullable
    public static Boolean GetBooleanStatValue(@Nullable NBTItem mmoitemAsNBTItem, @Nullable ItemStat statName, @Nullable Player considerRPGrequirements, boolean silent) {
        if (mmoitemAsNBTItem == null) {
            //STAT//OotilityCeption. Log(" §c- Null Fail");
            return null; }
        if (statName == null) {
            //STAT//OotilityCeption. Log(" §c- Stat Fail");
            return null; }
        //STAT//OotilityCeption. Log(" §c+§7 Null Check Passed");

        // Consider the Requirements
        if (MeetsRequirements(considerRPGrequirements, mmoitemAsNBTItem, !silent)) {
            //STAT//OotilityCeption. Log(" §c+§7 Requirements Met");

            // Convert and Return
            return GetBooleanStatValue(VolatileFromNBT(mmoitemAsNBTItem), statName);
        }
        //STAT//OotilityCeption. Log(" §c- Requirements Fail");
        // Didnt meet the requirements, null.
        return null;
    }
    public static Boolean GetBooleanStatValue(@Nullable MMOItem mmoitem, @Nullable ItemStat statName) {
        if (mmoitem == null) {
            //STAT//OotilityCeption. Log(" §c- Null Fail 2");
            return null; }
        if (statName == null) {
            //STAT//OotilityCeption. Log(" §c- Stat Fail 2");
        return null; }
        //STAT//OotilityCeption. Log(" §c+§7 Null Check 2 Passed");

        // Has data??? >:O
        if (mmoitem.hasData(statName)) {
            //STAT//OotilityCeption. Log(" §c+§7 Found Data");

            // If the player meets the requirements, return the value
            BooleanData dddData = ( BooleanData ) mmoitem.getData(statName);
            if (dddData != null) {
                //STAT//OotilityCeption. Log(" §c+§7 Data Found: \u00a76" + dddData.isEnabled());
                return dddData.isEnabled();
            }
        }

        //STAT//OotilityCeption. Log(" §c+ Data Fail");
        return null;
    }

    /**
     * Gets the String Value from a stat (MAKE SURE YOU KNOW IT IS A STRING STAT)
     * @param itemThatMayNotEvenBeAMMOItem Item containing the value
     * @param statName Use <code>GooPMMOItems.Stat( ... );</code> for this.
     * @param considerRPGrequirements Player to reject if they dont meet class reqs and stuff
     * @param silent If they fail the RPG reqs, should the fail message be displayed?
     * @return The value of the stat in the item, or NULL if the item doesn't ahve the stat, or it is not a MMOItem
     */
    public static String GetStringStatValue(@Nullable ItemStack itemThatMayNotEvenBeAMMOItem, ItemStat statName, Player considerRPGrequirements, boolean silent) {

        if (IsMMOItem(itemThatMayNotEvenBeAMMOItem)) {

            // Yes it is, does it have a ... tag?
            return GetStringStatValue(NBTItem.get(itemThatMayNotEvenBeAMMOItem), statName, considerRPGrequirements, silent);
        }

        return null;
    }
    public static String GetStringStatValue(@Nullable NBTItem mmoitemAsNBTItem, ItemStat statName, Player considerRPGrequirements, boolean silent) {
        if (mmoitemAsNBTItem == null) { return null; }
        if (statName == null) { return null; }

        // Consider the Requirements
        if (GooPMMOItems.MeetsRequirements(considerRPGrequirements, mmoitemAsNBTItem, !silent)) {

            // Convert and Return
            return GetStringStatValue(VolatileFromNBT(mmoitemAsNBTItem), statName);
        }

        // Didnt meet the requirements, null.
        return null;
    }

    // Get Item Name
    public static int GetItemLevelNonull(@Nullable ItemStack iSource) {

        // Get Level
        Integer actualLevel = GetItemLevel(iSource);

        // There
        if (actualLevel == null) { return 0; }
        return actualLevel;
    }
    public static Integer GetItemLevel(@Nullable ItemStack iSource) {
        if (iSource == null) { return null; }
        if (!IsMMOItem(iSource)) { return null; }

        MMOItem mmoitem = VolatileFromNBT(NBTItem.get(iSource));
        if (mmoitem == null) { return null; }

        ItemStat oupgrade = GooPMMOItems.Stat(GooPMMOItemsItemStats.UPGRADE);

        if (oupgrade == null) { return null; }

        if (!mmoitem.hasData(oupgrade)) { return null; }

        // If the player meets the requirements, return the value
        UpgradeData iUpgrade = (UpgradeData) mmoitem.getData(oupgrade);
        if (iUpgrade != null) {

            // Get data
            return iUpgrade.getLevel();
        }

        return null;
    }
    //endregion

    //region Testing RPG Requirements
    public static boolean MeetsRequirements(Player target, ItemStack item, boolean message) { return MeetsRequirements(target, NBTItem.get(item), message); }
    public static boolean MeetsRequirements(Player target, NBTItem item, boolean message) {
        if (target != null) {
            PlayerData iData = PlayerData.get(target);

            if (iData != null) {

                RPGPlayer iRPG = iData.getRPG();

                if (iRPG != null) {

                    return iRPG.canUse(item, message);
                }
            }

            // Guess Yes
            return true;

        } else {

            // Guess Yes
            return true;
        }
    }

    public static int GetPlayerLevel(@NotNull Player target) {

        // Get Data
        PlayerData data = PlayerData.get(target);

        if (data == null) { return 0; }
        if (data.getRPG() == null) { return 0; }

        // Find level
        return data.getRPG().getLevel();
    }

    @NotNull public static String GetPlayerClass(@NotNull Player target) {

        // Get Data
        PlayerData data = PlayerData.get(target);

        if (data == null) { return ""; }
        if (data.getRPG() == null) { return ""; }

        // Find class
        String ret = data.getRPG().getClassName();

        // Never null
        return ret != null ? ret : "";
    }
    //endregion

    //region Gemstone Stuff
    public static Integer MMOItemCountGems(ItemStack base, Boolean includeEmpty, RefSimulator<String> logOutput) {

        // I hope that bitch is not null to begin with
        if (base != null) {

            // Gettat Target NBT
            NBTItem tNBT = NBTItem.get(base);

            // Is it an mmoItem to begin with?
            if (tNBT.hasType()) {

                // Add slot heck
                logOutput.SetValue(null);
                return CountGemSlots(tNBT, includeEmpty);

                // Its a vanilla item. Thus it has no gemslots
            } else {

                logOutput.SetValue(null);
                return 0;
            }

            // This man passing on a null item wth
        } else {

            // Log if appropiate
            OotilityCeption.Log4Success(logOutput, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant count gem slots of air! Well I guess it has 0.");
            return 0;
        }

       // Memmories...
        // OotilityCeption.Log4Success(logOutput, !Gunging_Ootilities_Plugin.blockImportantErrorFeedback, "\u00a7cOutdated MMOItems version. \u00a77Counting Gem Slots to items requires \u00a7aMMOItems 5.4 \u00a7o(Build #254)\u00a77 or newer.");

    }
    public static ItemStack MMOItemAddGemSlot(ItemStack base, String gemSlotColour, RefSimulator<String> logOutput) {
        // Gemstone Support is Enabled (Correct MMOItems version)

        // I hope that bitch is not null to begin with
        if (base != null) {

            // Result
            int count = base.getAmount();
            ItemStack result = null;

            // Gettat Target NBT
            NBTItem tNBT = NBTItem.get(base);

            // Is it an mmoItem to begin with?
            if (tNBT.hasType()) {

                // Add slot heck
                result = AddGemSlot(tNBT, gemSlotColour);

                // Log if appropiate
                if (logOutput != null) {

                    // If there is fail feedback to send
                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback){

                        // Return a Log Message, for good shit.
                        logOutput.SetValue("Added a new \u00a73" + gemSlotColour + "\u00a77 gem slot to \u00a73" + OotilityCeption.GetItemName(base));

                        // Otherwise, send nothing.
                    } else {

                        // Clear da shit
                        logOutput.SetValue(null);
                    }
                }

            // Its a vanilla item. Convert into MMOItem and add tha fucking gem slot
            } else {

                // Is it Air?
                if (OotilityCeption.IsAir(base.getType())) {

                    // Log if appropiate
                    if (logOutput != null) {

                        // If there is fail feedback to send
                        if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback){

                            // Return a Log Message, for good shit.
                            logOutput.SetValue("Cant add \u00a73" + gemSlotColour + "\u00a77 slots to air!");

                            // Otherwise, send nothing.
                        } else {

                            // Clear da shit
                            logOutput.SetValue(null);
                        }
                    }

                // Its an actual item (Not Air)
                } else {

                    // Attempt to Convert to NBT
                    int oCount = base.getAmount();
                    base = ConvertVanillaToMMOItem(base);
                    tNBT = NBTItem.get(base);

                    // Add slot heck
                    result = AddGemSlot(tNBT, gemSlotColour);
                    if (result != null) { result.setAmount(oCount); }

                    // Log if appropiate
                    if (logOutput != null) {

                        // There was an error wth
                        if (result == null) {

                            // If there is fail feedback to send
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) {

                                // Return a Log Message, for good shit.
                                logOutput.SetValue("\u00a77To add gems to vanilla items, they must first be converted to MMOItems. \u00a7cFor that to be possible, please create an empty MMOItem with ID (name) '\u00a7eVANILLA\u00a7c' under the type '\u00a7e" + tNBT.getString("MMOITEMS_ITEM_TYPE") + "\u00a7c.' \u00a77(The type that \u00a73" + base.getType().name() + "\u00a77' falls into).");

                            // Otherwise, send nothing.
                            } else {

                                // Clear da shit
                                logOutput.SetValue(null);
                            }

                        // Perhaps the thing doesnt support gem slots
                        } else if (result.getType() == Material.STRUCTURE_VOID) {

                            // If there is fail feedback to send
                            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback){

                                // Return a Log Message, for good shit.
                                logOutput.SetValue("Item Type \u00a7e" + tNBT.getString("MMOITEMS_ITEM_TYPE") + "\u00a77 does not support Gem Slots. Cancelling operation.");

                            // Otherwise, send nothing.
                            } else {

                                // Clear da shit
                                logOutput.SetValue(null);
                            }

                            // Clear it btw
                            result = null;

                        // If there was a success
                        } else {

                            // If there is success feedback to send
                            if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback){

                                // Return a Log Message, for good shit.
                                logOutput.SetValue("Successfuly Converted \u00a73" + OotilityCeption.GetItemName(base) + "\u00a77 into a MMOItem and added an empty \u00a73" + gemSlotColour + "\u00a77 slot.");

                            // Otherwise, send nothing.
                            } else {

                                // Clear da shit
                                logOutput.SetValue(null);
                            }

                        }
                    }
                }
            }

            if (result != null) { result.setAmount(count); }
            return result;

        // This man passing on a null item wth
        } else {

            // Log if appropiate
            if (logOutput != null) {

                // If there is fail feedback to send
                if (Gunging_Ootilities_Plugin.sendGooPFailFeedback) {

                    // Return a Log Message, for good shit.
                    logOutput.SetValue("Cant add \u00a73" + gemSlotColour + "\u00a77 gem slots to air!");

                // Otherwise, send nothing.
                } else {

                    // Clear da shit
                    logOutput.SetValue(null);
                }
            }
            return null;
        }


        // Gemstone Support is not enabled
    }
    public static ItemStack AddGemSlot(NBTItem base, String gemSlotColour) {

        // Test of existence of such type and IDs
        try {

            // Compatible to Gemstone?
            if (GooPMMOItems.TypeGemstoneCompatible(GooPMMOItems.GetMMOItemType(base))) {

                // Attempt to get MMOItem Instance
                MMOItem mmoitem = new LiveMMOItem(base);

                // Make new mergeable gem sockets data
                GemSocketsData newSocket = new GemSocketsData(new ArrayList<>());
                newSocket.addEmptySlot(gemSlotColour);

                // Merge :b
                mmoitem.mergeData(ItemStats.GEM_SOCKETS, newSocket, null);

                // Get Finished Product
                return mmoitem.newBuilder().build();

            // No. Return a Structure Void Block as a result
            } else {

                // Returning a Structure Void means the type doesn't support gems on it
                return new ItemStack(Material.STRUCTURE_VOID);
            }

            // Seems that those Types/IDs did not match wth
        } catch (NullPointerException e) {
            return null;
        }
    }
    public static Integer CountGemSlots(NBTItem base, Boolean incEmpty) {

        // Test of existance of such type and IDs
        try {

            // Compatible to Gemstone?
            if (GooPMMOItems.TypeGemstoneCompatible(GooPMMOItems.GetMMOItemType(base))) {

                // Attempt to get MMOItem Instance
                MMOItem mmoitem = new LiveMMOItem(base);

                // Get Socket Data
                GemSocketsData sockets = (GemSocketsData) mmoitem.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.GEM_SOCKETS));

                // Has the items any sockets already?
                if (sockets != null) {

                    // Then just add the entry to the existing list
                    int ret = 0;
                    if (sockets.getGemstones() != null) { ret += sockets.getGemstones().size(); }
                    if (sockets.getEmptySlots() != null && incEmpty) { ret += sockets.getEmptySlots().size(); }
                    return ret;

                    // Item has no gem slots at all, straight up 0
                } else {

                    return 0;
                }

                // No. Return a Strucutre Void Block as a result
            } else {

                return 0;
            }

            // Seems that those Types/IDs did not match wth
        } catch (NullPointerException e) {

            return 0;
        }
    }

    /**
     * Will return true if the type provided is compatible with having GemStones.
     * <p></p>
     * This is because some things, like MISCELLANEOUS, cannot have Gem Sockets.
     */
    public static boolean TypeGemstoneCompatible(@Nullable Type tType) {

        // Null is incompatible
        if (tType == null) { return false;}

        // Result Variable
        boolean ret = false;

        // Search Avaliable Stats for Gemstone
        for (ItemStat statt : tType.getAvailableStats()) {

            // Found a Gem Sockets Support?
            if (statt == Stat(GooPMMOItemsItemStats.GEM_SOCKETS)) {

                // Sure, it is supported.
                ret = true;

                // No need to keep searching
                break;
            }
        }

        // Return Result
        return ret;
    }


    /**
     * YOU MUST KNOW THESE ARE MMOITEMS, the GEMSTONE and the TARGET, otherwise it will produce NULL POINTER EXCEPTIONS
     */
    public static boolean GemstoneFitsOnto(Player applier, NBTItem gemstone, NBTItem target) {

        // In what terms are we dealing?
        Type tType = GetMMOItemType(target);

        // Success?
        if (tType != null) {

            // Use
            return GemstoneFitsOnto(applier, gemstone,target, tType);

        } else {

            // I guess not lma0
            return false;
        }
    }

    /**
     * YOU MUST KNOW THESE ARE MMOITEMS, the GEMSTONE and the TARGET, otherwise it will produce NULL POINTER EXCEPTIONS
     */
    public static boolean GemstoneFitsOnto(Player applier, NBTItem gemstone, NBTItem target, Type targetType) {

        // Get Target
        MMOItem targetMMO = VolatileFromNBT(target);
        if (targetMMO == null) { return false; }

        // Does it have any gem sockets?
        if (!targetMMO.hasData(GooPMMOItems.Stat(GooPMMOItemsItemStats.GEM_SOCKETS))) { return false; }

        // Get Ouse Item
        UseItem asUse = GooPMMOItems.GetUseItem(gemstone, applier);

        // Is it a gemstone?
        if (!(asUse instanceof GemStone)) { return false; }

        // Does it match the colours?
        GemStone asGem = (GemStone) asUse;
        String gemType = asGem.getNBTItem().getString("MMOITEMS_GEM_COLOR");

        GemSocketsData sockets = (GemSocketsData)targetMMO.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.GEM_SOCKETS));
        if (!sockets.canReceive(gemType)) { return false; }

        // Does it match the type
        String appliableTypes = asGem.getNBTItem().getString("MMOITEMS_ITEM_TYPE_RESTRICTION");
        if (!appliableTypes.equals("") && (!targetType.isWeapon() || !appliableTypes.contains("WEAPON")) && !appliableTypes.contains(targetType.getItemSet().name()) && !appliableTypes.contains(targetType.getId())) { return false; }

        // HUH, Success!
        return true;
    }
    //endregion

    //region Converting from Vanilla to MMOItem
    public static String VANILLA_MIID = "VANILLA";
    @NotNull public static NBTItem ConvertVanillaToMMOItemAsNBT(@NotNull ItemStack base) {
        return ConvertVanillaToMMOItemAsNBT(base, null, null);
    }

    @NotNull public static NBTItem ConvertVanillaToMMOItemAsNBT(@NotNull ItemStack base, @Nullable String forcedTypePrefix, @Nullable String forcedIDFormat) {

        // Lets very quickly add a display name to this boi
        OotilityCeption.RenameItem(base, OotilityCeption.GetItemName(base), null);

        if (forcedTypePrefix == null) { forcedTypePrefix = ""; }

        // Get NBT item
        NBTItem tNBT = NBTItem.get(base);

        // Attribute Information
        double vDamage = 0.0, vSpeed = 0.0, vArmor = 0.0, vArmorT = 0.0, mHealthT = 0.0, mSpeedT = 0.0, mKRes = 0.0, mLuck = 0.0;

        // Decide what TYPE this item will be
        String tName = "";

        // Get vals
        RefSimulator<String> tNameRef = new RefSimulator<>("MISCELLANEOUS");
        RefSimulator<Double> vDamageRef = new RefSimulator<>(0.0), vSpeedRef = new RefSimulator<>(0.0), vArmorRef = new RefSimulator<>(0.0), vArmorTRef = new RefSimulator<>(0.0), mHealthTRef = new RefSimulator<>(0.0), mKResRef = new RefSimulator<>(0.0);
        OotilityCeption.GatherDefaultVanillaAttributes(base.getType(), tNameRef, vDamageRef, vSpeedRef, vArmorRef, vArmorTRef, mKResRef);

        // If they actually deal damage; in the conversion to MMOItems it is 1 + that;
        if (vDamageRef.getValue() > 0) { vDamage = OotilityCeption.RoundAtPlaces(vDamageRef.getValue(), 1) + 1; }
        vSpeed = vSpeedRef.getValue(); vArmor = vArmorRef.getValue(); vArmorT = vArmorTRef.getValue(); mKRes = mKResRef.getValue(); tName = tNameRef.getValue();

        // Replace if missing
        ArrayList<String> realTypes = GooPMMOItems.GetMMOItem_TypeNames();
        if (!realTypes.contains(tName)) {

            // Well replace I guess
            if (tName.equals("SHIELD")) { tName = "ARMOR"; }
            if (tName.equals("WHIP")) { tName = "MATERIAL"; }

            if (!realTypes.contains(tName)) {

                // Well replace I guess
                tName = "MISCELLANEOUS";
            }
        }

        // Create the Item Tags
        ItemTag tTypeMI = new ItemTag("MMOITEMS_ITEM_TYPE", forcedTypePrefix + tName);
        ItemTag tID = new ItemTag("MMOITEMS_ITEM_ID", forcedIDFormat == null ? VANILLA_MIID : forcedIDFormat);

        // Add those tags
        tNBT.addTag(tTypeMI);
        tNBT.addTag(tID);

        // If applicable, fill item with attributes
        Multimap<Attribute, AttributeModifier> attribs = base.getItemMeta().getAttributeModifiers();
        ItemTag tDamage, tSpeed, tArmor, tArmorToughness, tMHealth, tMSpeed, tKRes, tLuck;

        // If there are any custom attributes
        if (attribs != null) {
            // Retrieve Values
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_ATTACK_DAMAGE)) { vDamage += atrb.getAmount(); }
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_ATTACK_SPEED)) { vSpeed += atrb.getAmount(); }
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_ARMOR)) { vArmor += atrb.getAmount(); }
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_ARMOR_TOUGHNESS)) { vArmorT += atrb.getAmount(); }
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_MOVEMENT_SPEED)) { mSpeedT += atrb.getAmount(); }
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_MAX_HEALTH)) { mHealthT += atrb.getAmount(); }
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_KNOCKBACK_RESISTANCE)) { mKRes += atrb.getAmount(); }
            for (AttributeModifier atrb : attribs.get(Attribute.GENERIC_LUCK)) { mLuck += atrb.getAmount(); }
        }

        // Create Tags
        tDamage = new ItemTag(ItemStats.ATTACK_DAMAGE.getNBTPath(), vDamage);
        tSpeed = new ItemTag(ItemStats.ATTACK_SPEED.getNBTPath(), vSpeed);
        tArmor = new ItemTag(Gunging_Ootilities_Plugin.useMMOLibDefenseConvert ? ItemStats.DEFENSE.getNBTPath() : ItemStats.ARMOR.getNBTPath(), vArmor);
        tArmorToughness = new ItemTag(ItemStats.ARMOR_TOUGHNESS.getNBTPath(), vArmorT);
        tMHealth = new ItemTag(ItemStats.MAX_HEALTH.getNBTPath(), mHealthT);
        tMSpeed = new ItemTag(ItemStats.MOVEMENT_SPEED.getNBTPath(), mSpeedT);
        tKRes = new ItemTag(ItemStats.KNOCKBACK_RESISTANCE.getNBTPath(), mKRes);
        tLuck = new ItemTag(LUCK.getNBTPath(), mLuck);

        // Append Tags
        if (vDamage != 0) { tNBT.addTag(tDamage); }
        if (vSpeed != 0) { tNBT.addTag(tSpeed); }
        if (vArmor != 0) { tNBT.addTag(tArmor); }
        if (vArmorT != 0) { tNBT.addTag(tArmorToughness); }
        if (mSpeedT != 0) { tNBT.addTag(tMSpeed); }
        if (mHealthT != 0) { tNBT.addTag(tMHealth); }
        if (mKRes != 0) { tNBT.addTag(tKRes); }
        if (mLuck != 0) { tNBT.addTag(tLuck); }

        // Enable Upgr8d
        tNBT = EnableParentTypeUpgrades(tNBT, tName, "VANILLA");

        // Complete Operation
        return tNBT;
    }

    @NotNull public static ItemStack ConvertVanillaToMMOItem(@NotNull ItemStack base) {
        return ConvertVanillaToMMOItem(base, null, null);
    }
    @NotNull public static ItemStack ConvertVanillaToMMOItem(@NotNull ItemStack base, @Nullable String forcedTypePrefix, @Nullable String forcedIDFormat) {

        // Store important nbt data
        int oCount = base.getAmount();
        ArrayList<String> oLore = OotilityCeption.GetLore(base);
        Map<Enchantment, Integer> oEnchantments = base.getEnchantments();
        boolean oUnbreak = base.getItemMeta().isUnbreakable();
        Integer oCMD = null;
        if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { if (base.getItemMeta().hasCustomModelData()) { oCMD = base.getItemMeta().getCustomModelData(); } }

        // Convert it as NBT and then Build it
        MMOItem mmoitem = LiveFromNBT(GooPMMOItems.ConvertVanillaToMMOItemAsNBT(base, forcedTypePrefix, forcedIDFormat));
        if (mmoitem == null) { return base; }

        //region Set Original Datas
        // Build and add Enchant - If enchanted to begin with
        if (oEnchantments.keySet().size() > 0) {
            EnchantListData elData = new EnchantListData();
            for (Enchantment e : oEnchantments.keySet()) { elData.addEnchant(e, oEnchantments.get(e)); }
            mmoitem.mergeData(GooPMMOItems.Stat(GooPMMOItemsItemStats.ENCHANTS), elData, null);
        }

        // Lore Adding - If has lore to begin with
        if (oLore != null) {
            if (oLore.size() > 0) {

                StringListData slData = new StringListData(oLore);
                mmoitem.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE), slData);
            }
        }

        // Unbreakable - If Unbrekable
        if (oUnbreak) {
            BooleanData bdData = new BooleanData(true);
            mmoitem.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.UNBREAKABLE), bdData);
        }

        // Custom Model Data if apliccable
        if (oCMD != null) {
            DoubleData cmdData = new DoubleData(oCMD + 0.0D);
            mmoitem.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.CUSTOM_MODEL_DATA), cmdData);
        }
        //endregion

        // Get Finished Product
        ItemStack ret = mmoitem.newBuilder().build();

        // Set original count
        ret.setAmount(oCount);
        return ret;
    }
    public static NBTItem EnableParentTypeUpgrades(NBTItem childNBT, String parentTypeName, String parentID) {

        // If type exists
        MMOItem parenTemplate = GetMMOItemRaw(parentTypeName, parentID);

        // Return if falure
        if (parenTemplate == null) { return childNBT; }

        UpgradeData uData = (UpgradeData) parenTemplate.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.UPGRADE));

        if(uData != null) {
            MMOItem m = LiveFromNBT(childNBT);

            m.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.UPGRADE), uData);

            return m.newBuilder().buildNBT();
        }

        return childNBT;
    }
    //endregion

    //region Modify MMOItem Attributes

    /**
     * Upgrades a MMOItem to this level
     *
     * @param base ItemStack that may not be an MMOItem
     * @param toLevel Level operaton
     * @param breakLimit if it should overshoot the max upgrade level of an item
     * @return <code>null</code> if anything goes wrong
     */
    @Nullable public static ItemStack UpgradeMMOItem(@Nullable ItemStack base, @NotNull PlusMinusPercent toLevel, boolean breakLimit, @Nullable RefSimulator<Integer> finalLevel, @Nullable RefSimulator<String> logger) {

        // Neh
        if (OotilityCeption.IsAirNullAllowed(base)) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant upgrade air!");
            return null; }

        // Is MMOItem right
        NBTItem nbt = NBTItem.get(base);
        if (!nbt.hasType()) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Only MMOItems can be upgraded.");
            return null; }

        // Check volatile
        VolatileMMOItem vol = VolatileFromNBT(nbt);
        if (!vol.hasUpgradeTemplate()) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "This MMOItem has no Upgrade Template (its not uprgadeable).");
            return null; }

        // Live
        LiveMMOItem live = LiveFromNBT(nbt);

        // What level will it be?
        int result = SilentNumbers.floor(toLevel.apply((double) live.getUpgradeLevel()));

        // Respect limit
        boolean limited = false;
        if (!breakLimit && (result > live.getMaxUpgradeLevel())) { result = live.getMaxUpgradeLevel(); limited = true; }

        live.getUpgradeTemplate().upgradeTo(live, result);
        if (finalLevel != null) { finalLevel.setValue(result); }

        if (limited) {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Upgraded to maximum level \u00a7b" + result + "\u00a77 since the upgrade operation would have bypassed it.");
        } else {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Upgraded the item to level \u00a7b" + result + "\u00a77 successfully.");
        }

        // Deal
        return live.newBuilder().build();
    }

    /**
     * Will return the MMOItems lore of this. Not the vanilla.
     */
    @NotNull
    public static ArrayList<String> MMOItemGetLore(@Nullable ItemStack base) {

        // Gemstone Support is Enabled (Correct MMOItems version)
        if (IsMMOItem(base)) {

            // I hope that bitch is not null to begin with
            if (base != null) {

                // Test of existance of such type and IDs
                try {

                    // Attempt to get MMOItem Instance
                    MMOItem mmoitem = VolatileFromNBT(NBTItem.get(base));
                    if (mmoitem == null) { return null; }

                    if (!mmoitem.hasData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE))) { return new ArrayList<>(); }

                    // Get Socket Data
                    StringListData loreData = (StringListData) mmoitem.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE));

                    // Is it the first or last
                    ArrayList<String> iLore = new ArrayList<>();

                    // Has the items any sockets already?
                    if (loreData != null) {

                        // Get original lore
                        if (loreData.getList() != null) { iLore = new ArrayList<>(loreData.getList()); }

                        // Return thay
                        return iLore;
                    }

                    // Had no lore.
                    return new ArrayList<>();

                    // Seems that those Types/IDs did not match wth
                } catch (NullPointerException e) {

                    // Hmph
                    return new ArrayList<>();
                }
            }
        }

        // Empty
        return new ArrayList<>();
    }
    /**
     * Will set the MMOItems lore of this. Fails if its not an MMOItem
     */
    @Nullable
    public static ItemStack MMOItemSetLore(@Nullable ItemStack base, @Nullable ArrayList<String> lore) {
        if (lore == null) { lore = new ArrayList<>(); }
        if (base == null) { return null; }

        // Test of existance of such type and IDs
        try {

            // Attempt to get MMOItem Instance
            MMOItem mmoitem = VolatileFromNBT(NBTItem.get(base));
            if (mmoitem == null) { return null; }

            // Get Socket Data
            StringListData loreData = (StringListData) mmoitem.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE));
            StringListData finalLore  = new StringListData(lore);

            // Has the items any sockets already?
            if (loreData != null) {

                // Remove momentaneously
                mmoitem.removeData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE));
            }

            // Append it to the mmoitem
            mmoitem.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE), finalLore);

            // Get Finished Product
            return mmoitem.newBuilder().build();

            // Seems that those Types/IDs did not match wth
        } catch (NullPointerException e) {

            // Hmph
            return null;
        }
    }
    public static ItemStack MMOItemAddLoreLine(ItemStack base, String loreLine, Integer index, RefSimulator<String> logger) {

        // I hope that bitch is not null to begin with
        if (base != null) {

            // Result
            ItemStack result = null;

            // Gettat Target NBT
            NBTItem tNBT = NBTItem.get(base);

            // Kount
            int kount = base.getAmount();

            // Is it an mmoItem to begin with?
            if (tNBT.hasType()) {

                // Add lore heck
                result = AddLoreLine(tNBT, loreLine, index);

                // Log if Appropiate
                if (result != null) {

                    // Gunging_Ootilities_Plugin.theOots.CLog("\u00a73-------------Resultant Lore--------------");
                    // for (String str : result.getLore()) { Gunging_Ootilities_Plugin.theOots.CLog("\u00a7e - \u00a77" + str); }
                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully added lore line " + OotilityCeption.ParseColour(loreLine) + "\u00a77 to " + OotilityCeption.GetItemName(base));

                } else {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Index out of range!");
                }
            }

            // Set
            if (!OotilityCeption.IsAirNullAllowed(result)) { result.setAmount(kount); }

            return result;

            // This man passing on a null item wth
        } else {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant add lore lines to air!");
            return null;
        }
    }
    public static ItemStack MMOItemRemoveLoreLine(ItemStack base, Integer index, RefSimulator<String> logger) {


        // I hope that bitch is not null to begin with
        if (base != null) {

            // Result
            ItemStack result = null;

            // Gettat Target NBT
            NBTItem tNBT = NBTItem.get(base);

            // Kount
            int kount = base.getAmount();

            // Is it an mmoItem to begin with?
            if (tNBT.hasType()) {

                // Add lore heck
                result = RemoveLoreLine(tNBT, index);

                // Log if Appropiate
                if (result != null) {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully removed lore line from "+ OotilityCeption.GetItemName(base));

                } else {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Index out of range!");
                }
            }

            // Set
            if (!OotilityCeption.IsAirNullAllowed(result)) { result.setAmount(kount); }

            return result;

            // This man passing on a null item wth
        } else {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant remove lore lines from air!");
            return null;
        }
    }
    public static ItemStack MMOItemModifyAttribute(ItemStack base, PlusMinusPercent operation, Attribute attrib, RefSimulator<Double> reslt, RefSimulator<String> logger) {

        // I hope that bitch is not null to begin with
        if (base != null) {

            // Result
            ItemStack result = null;

            // Gettat Target NBT
            NBTItem tNBT = NBTItem.get(base);

            // Store count?
            int kount = base.getAmount();

            // Is it an mmoItem to begin with?
            if (tNBT.hasType()) {

                // Add lore heck
                result = ModifyAttrib(tNBT, operation, attrib, reslt);

                // Log if Appropiate
                if (result != null) {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified attribute \u00a73" + attrib.name() + "\u00a77 of "+ OotilityCeption.GetItemName(base));

                } else {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a77Attribute \u00a73" + attrib.name() + "\u00a77 doesnt seem to be supported as to modify its MMOItem counterpart.");
                }
            }

            // Set amount
            if (!OotilityCeption.IsAirNullAllowed(result)) { result.setAmount(kount); }

            return result;

            // This man passing on a null item wth
        } else {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit attributes of air!");
            return null;
        }
    }
    public static ItemStack SetTier(ItemStack base, String newTier, RefSimulator<String> finalTier, RefSimulator<String> logger) {

        // I hope that bitch is not null to begin with
        if (!OotilityCeption.IsAirNullAllowed(base)) {

            // Result
            ItemStack result;

            // Convert if not a MMOItem
            if (!IsMMOItem(base)) { base = ConvertVanillaToMMOItem(base); }

            // Gettat Target NBT
            NBTItem tNBT = NBTItem.get(base);

            // Kount
            int kount = base.getAmount();

            // Get actual values
            ItemTier actualTier = null;
            if (MMOItems.plugin.getTiers().has(newTier)) { actualTier = MMOItems.plugin.getTiers().get(newTier);  }

            // Add lore heck
            result = SetTier(tNBT, actualTier, finalTier);

            // Log if Appropiate
            if (result != null) {

                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified tier of "+ OotilityCeption.GetItemName(base));

            } else {

                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "\u00a77Failed to modify tier, perhaps \u00a73" + newTier + "\u00a77 is not a loaded tier?");
            }

            // Set
            if (!OotilityCeption.IsAirNullAllowed(result)) { result.setAmount(kount); }

            return result;

            // This man passing on a null item wth
        } else {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant edit tier of air!");
            return null;
        }
    }
    @Nullable public static ItemStack FixStackableness(@Nullable ItemStack base, @Nullable RefSimulator<String> logger) {

        // I hope that bitch is not null to begin with
        if (base != null) {

            // Kount
            int kount = base.getAmount();

            // Gettat Target NBT
            NBTItem b = NBTItem.get(base);

            // Is it an mmoItem t
            // o begin with?
            if (b.hasType()) {

                // Test of existance of such type and IDs
                try {

                    // Re MMOItemize it
                    MMOItem tMMO = LiveFromNBT(b);

                    /*
                    // GEt Type
                    Type typ = GooPMMOItems.GetMMOItemType(b);
                    if (typ == null) { return null; }

                    // Get all available stats
                    ArrayList<ItemStat> aStats = new ArrayList<>(typ.getAvailableStats());

                    // Attempt to get MMOItem Instance
                    MMOItem mmoitem = LiveFromNBT(b);
                    if (mmoitem == null) { return null; }

                    // Get ALl Stats from thay item
                    HashMap<ItemStat, StatData> dStats = new HashMap<>();
                    for (ItemStat statt : aStats) {

                        // Git Data
                        StatData datt = mmoitem.getData(statt);

                        // Log ig
                        //OotilityCeption. Log("-----------------");
                        //OotilityCeption. Log("Stat: \u00a73" + statt.getName());

                        // Contained?
                        if (datt != null) {

                            //region Special Cases (apparently)

                            // This one causes a cast exception; Seems to be retrieved as StringData but should be StringListData
                            if (statt.equals(GooPMMOItems.Stat(GooPMMOItemsItemStats.ITEM_TYPE_RESTRICTION))) {

                                // Is it a damn string data?
                                if (datt instanceof StringData){

                                    // Get String bruh
                                    String datContent = ((StringData) datt).toString();

                                    // Make StringListDatae
                                    StringListData nDatContent = new StringListData();
                                    nDatContent.getList().add(datContent);

                                    //Override
                                    datt = nDatContent;
                                }
                            }

                            // kk
                            //OotilityCeption. Log("N: \u00a7e" + datt.getClass().getName());
                            //OotilityCeption. Log("C: \u00a7e" + datt.getClass().getCanonicalName());
                            //OotilityCeption. Log("S: \u00a7e" + datt.getClass().getSimpleName());
                            //OotilityCeption. Log("T: \u00a7e" + datt.getClass().getTypeName());
                            //endregion

                            // Save
                            dStats.put(statt, datt);
                        }
                    }

                    // Save &7 Lore Lines
                    ItemMeta iMeta = b.getItem().getItemMeta();
                    //ArrayList<String> iLore = new ArrayList<>(iMeta.getLore());
                    ArrayList<String> keptLore = new ArrayList<>();
                    //for (String str : iLore) { if (str.startsWith("\u00a77")) { keptLore.add(str); } }

                    // Extract Tier and Type
                    RefSimulator<String> mType = new RefSimulator<>(""), mID = new RefSimulator<>("");
                    GooPMMOItems.GetMMOItemInternals(b.getItem(), mType, mID);

                    // Refresh
                    ItemStack tSource = GooPMMOItems.GetMMOItem(mType.getValue(), mID.getValue());
                    NBTItem tNBT = NBTItem.get(tSource);
                    MMOItem tMMO = LiveFromNBT(tNBT);
                    if (tMMO == null) { return null; }

                    // Append
                    for (ItemStat statt : dStats.keySet()) {

                        // Edit Lore
                        if (statt == GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE)) {

                            // Append Lore
                            StringListData slDat = (StringListData) dStats.get(statt);

                            // Add
                            slDat.getList().addAll(keptLore);
                        }

                        // Append
                        tMMO.setData(statt, dStats.get(statt));
                    }
                    //*/

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Updated MMOItem to latest format (so it can stack).");

                    // Build
                    ItemStack built = tMMO.newBuilder().build();
                    built.setAmount(kount);
                    return built;

                    // Seems that those Types/IDs did not match wth
                } catch (Exception e) {

                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Could not update MMOItem to latest format");
                    return null;
                }

                    /*
                    try {

                        // Add lore heck
                        if (version_6_0_1) {

                            // Load 6.0 Method
                            result = GooPMMOItems_6_0.Regenerate(tNBT);

                        } else {

                            // Load 5.5- Method
                            result = GooPMMOItems_Pre6.Regenerate(tNBT);
                        }

                        OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Updated MMOItem to latest format (so it can stack).");

                    } catch (Exception ignored) {

                        OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "For reasons unknown, could not fix stackableness of " + OotilityCeption.GetItemName(base) +"!");
                        return null;
                    }       //*/

            } else {

                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "That is not a MMOItem!");

                return null;
            }

            // Set

            // This man passing on a null item wth
        } else {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant fix stackableness of air!");
            return null;
        }
    }

    /**
     * Performs an operation on the durability of an item. If it succeeded on setting the durability, it will return the modified ItemStack.
     * <p>Will target MMOItems durability if applicable.</p> Does not bypass Unbreakable attribute.
     * <p></p>
     * Reasons it could fail: <p>
     * + The item does not exist </p>
     * + The item cannot be damaged (say, a stone block) <p>
     * <p><p>
     * If it would break by receiving such damage, it is prevented from breaking, and this will not fail
     * and just return the item at 1 remaining durability.
     * @param base Item to modify damage of
     * @param operation Operation to apply, based on the current damage of the item
     * @param reslt Stores numerically the final damage the item has
     * @param logger Stores a string saying what went wrong, if something did.
     */
    @Nullable
    public static ItemStack MMOItemModifyDurability(@Nullable ItemStack base, @Nullable Player holder, @NotNull PlusMinusPercent operation, @Nullable RefSimulator<Double> reslt, @Nullable RefSimulator<String> logger) {
        return MMOItemModifyDurability(base, holder, operation, reslt, true, logger);
    }

    /**
     * Performs an operation on the durability of an item. If it succeeded on setting the durability, it will return the modified ItemStack.
     * <p>Will target MMOItems durability if applicable.</p> Does not bypass Unbreakable attribute.
     * <p></p>
     * Reasons it could fail: <p>
     * + The item does not exist </p>
     * + The item cannot be damaged (say, a stone block) <p>
     * <p><p>
     * If it would break by receiving such damage, but it is prevented from breaking, this will not fail
     * and just return the item at 1 remaining durability.
     * @param base Item to modify damage of
     * @param operation Operation to apply, based on the current damage of the item
     * @param holder Player under whose name to break the MMOItem (will display a message of breaking to them).
     * @param reslt Stores numerically the final damage the item has.
     * @param preventBreaking If the operation would break the item, it will set at 1 remaining.
     * @param logger Stores a string saying what went wrong, if something did.
     * @return DEBUG_STICK ItemStack if the item broke.
     */
    @Nullable
    public static ItemStack MMOItemModifyDurability(@Nullable ItemStack base, @Nullable Player holder, @NotNull PlusMinusPercent operation, @Nullable RefSimulator<Double> reslt, boolean preventBreaking, @Nullable RefSimulator<String> logger) {
        String pname = "no";
        if (holder != null) { pname = holder.getName(); }
        //dur//OotilityCeption. Log("Dura As MMOItem: " + OotilityCeption.GetItemName(base) + "\u00a77, holder " + pname + "\u00a77, prevent break \u00a7b" + preventBreaking);

        // Gemstone Support is Enabled (Correct MMOItems version)
        //dur//OotilityCeption. Log("Gem Stuppot Found");

        // I hope that bitch is not null to begin with
        if (base != null) {
            //dur//OotilityCeption. Log("Base Real");

            // Result
            ItemStack result = null;

            // Attempt to get durability item
            DurabilityItem durItem = new DurabilityItem(holder, base);
            if (durItem.isValid()) {
                //dur//OotilityCeption. Log("\u00a7aUses MMOItems dura");

                // Get Name
                String iName = OotilityCeption.GetItemName(base);

                // Durabilitied Item Meta
                ItemMeta dur = null;

                // Get Current amount
                int currentDura = durItem.getDurability();

                // Get max
                int maxDura = durItem.getMaxDurability();
                //dur//OotilityCeption. Log("\u00a77Current Durability: \u00a7b" + currentDura + "/" + maxDura);

                // Simulate Vanilla
                int vanillaStyleDamage = maxDura - currentDura;

                // Perform operation to current damage
                int finalDamage = (int) Math.round(operation.apply(vanillaStyleDamage + 0.0D));
                //dur//OotilityCeption. Log("\u00a77As Damage: \u00a7b" + vanillaStyleDamage + " -> " + finalDamage);

                // Constrain I suppose, if its preventing from breaking or there is no holder :thinking:
                if (preventBreaking) { if (finalDamage >= (maxDura - 1)) { finalDamage = (maxDura - 1); } } else if (finalDamage > maxDura) { finalDamage = maxDura + 1; }
                if (finalDamage < 0) { finalDamage = 0; }

                // Get Actual Durability
                int finalDura = maxDura - finalDamage;

                // Get shift
                int shift = finalDura - currentDura;
                //dur//OotilityCeption. Log("\u00a77Processed: \u00a7e" + finalDamage + " -> " + finalDura + "\u00a77(\u00a7a" + shift + "\u00a77)");

                // Set result
                if (reslt != null) { reslt.setValue(finalDamage + 0.0D); }

                // Did it break? Must GooP do something (because MMOItems will generate an exception)?
                if (finalDura < 0 && holder == null) {
                    //dur//OotilityCeption. Log("\u00a7cBroke - No Playr");

                    // Broke
                    OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified durability of \u00a7f" + iName + "\u00a77, it broke though");

                    // Return as air
                    return new ItemStack(Material.DEBUG_STICK);
                }

                // If positive
                if (shift > 0) {

                    // Add durability
                    dur = durItem.addDurability(shift).toItem().getItemMeta();

                    // If shift negative
                } else {

                    // Substract durability
                    dur = durItem.decreaseDurability(-shift).toItem().getItemMeta();
                }

                // Did it break?
                if (finalDura < 0) {

                    // Can it break?
                    Boolean breakable = GetBooleanStatValue(base, Stat(GooPMMOItemsItemStats.WILL_BREAK),null, true);
                    if (OotilityCeption.If(breakable)) {

                        // Broke
                        OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified durability of \u00a7f" + iName + "\u00a77, it broke though");

                        // Return as air
                        return new ItemStack(Material.DEBUG_STICK);
                    }
                }

                // Convert result
                result = new ItemStack(base);
                result.setItemMeta(dur);

                // Result
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Successfully modified durability of \u00a7f" + iName);

                // If false, do the operation vanilla-wise alv
            } else {

                // Perform operation vanilla-wise
                result = OotilityCeption.SetDurabilityVanilla(base, operation, reslt, preventBreaking, logger);
            }

            return result;

            // This man passing on a null item wth
        } else {

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Cant repair of air!");
            return null;
        }
    }

    /**
     * Will return the durability of thay item, if such a thing can have durability.
     * Gives you the vanilla durability if the custom MAX DURABILITY is not defined.
     * @return Null if such a thing doesnt make sense to have durability.
     */
    @Nullable
    public static Integer GetMaxDurabilityOf(@Nullable ItemStack base) {

        // I hope that bitch is not null to begin with
        if (base != null) {

            // Get stat ig
            Double fromStat = GetDoubleStatValue(base, Stat(GooPMMOItemsItemStats.MAX_DURABILITY));

            // Did it exist?
            if (fromStat != null) {

                // Round-yo
                return OotilityCeption.RoundToInt(fromStat);

            } else {

                // Vanilla it is
                return OotilityCeption.GetMaxDurabilityVanilla(base);
            }

            // This man passing on a null item wth
        } else {

            return null;
        }
    }

    public static ItemStack AddLoreLine(NBTItem base, String loreLine, Integer index) {

        // Test of existance of such type and IDs
        try {

            // Attempt to get MMOItem Instance
            MMOItem mmoitem = LiveFromNBT(base);
            if (mmoitem == null) { return null; }

            // Get Socket Data
            StringListData loreData = (StringListData) mmoitem.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE));
            StringListData finalLore;

            // Is it the first or last
            boolean fORl = (index == null);
            if (index != null) { fORl = (index == 0);}
            ArrayList<String> iLore = new ArrayList<>();

            // Has the items any sockets already?
            if (loreData != null) {

                // Get original lore
                if (loreData.getList() != null) { iLore = new ArrayList<>(loreData.getList()); }

                //Choose True Index
                Integer tIndex = OotilityCeption.BakeIndex4Add(index, iLore.size());
                if (tIndex == null) {
                    // Index out of range
                    return null;
                }

                // Then just add the entry to the existing list
                iLore.add(tIndex, OotilityCeption.ParseColour(loreLine));

                // Create that data, and assign this list to it
                finalLore = new StringListData(iLore);

                // Remove momentaneously
                mmoitem.removeData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE));

                // Item has no lore data at all, must create if  the index makes sense
            } else if (fORl) {

                // Make a new Array of Strings, containing that sole new lore line
                iLore.add(OotilityCeption.ParseColour(loreLine));

                // Create that data, and assign this list to it
                finalLore = new StringListData(iLore);

                // Lore data didnt exist and index did not mean 'line 0' so rip.
            } else {

                // Index Out of Range (No Lore)
                return null;
            }

            // Append it to the mmoitem
            mmoitem.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE), finalLore);

            // Get Finished Product
            return mmoitem.newBuilder().build();

            // Seems that those Types/IDs did not match wth
        } catch (NullPointerException e) {

            // Hmph
            return null;
        }
    }
    public static ItemStack RemoveLoreLine(NBTItem base, Integer index) {

        // Test of existance of such type and IDs
        try {

            // Attempt to get MMOItem Instance
            MMOItem mmoitem = LiveFromNBT(base);
            if (mmoitem == null) { return null; }

            // Get Socket Data
            StringListData loreData = (StringListData) mmoitem.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE));

            // Has the items any sockets already?
            if (loreData != null) {

                // Get Lore Data
                ArrayList<String> iLore = new ArrayList<String>(loreData.getList());

                // Make sure there is any relevant lore data
                if (iLore.size() > 0) {

                    //Choose True Index
                    Integer tIndex = OotilityCeption.BakeIndex4Remove(index, iLore.size());
                    if (tIndex == null) {
                        // Index out of Range
                        return null;
                    }

                    // Then just add the entry to the existing list
                    iLore.remove((int)tIndex);

                    // Remove old lore
                    mmoitem.removeData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE));

                    // Was there any lore left?
                    if (iLore.size() > 0) {

                        // Create that data, and assign this list to it
                        StringListData finalLore = new StringListData(iLore);

                        // Append it to the mmoitem
                        mmoitem.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.LORE), finalLore);
                    }

                    // Item has no actual lore data
                } else {

                    // Thats a fail cuz cant remove what doesnt exist
                    return null;
                }

                // Item has no lore data at all lol
            } else {

                // Thats a fail cuz cant remove what doesnt exist
                return null;
            }

            // Get Finished Product
            return mmoitem.newBuilder().build();

            // Seems that those Types/IDs did not match wth
        } catch (NullPointerException e) {
            return null;
        }
    }
    public static ItemStack ModifyAttrib(NBTItem base, PlusMinusPercent operation, Attribute attrib, RefSimulator<Double> result) {

        // Test of existance of such type and IDs
        try {

            // Attempt to get MMOItem Instance
            MMOItem mmoitem = LiveFromNBT(base);
            if (mmoitem == null) { return null; }

            // Get Socket Data
            DoubleData mData = null;
            ItemStat stt = null;
            switch (attrib) {
                case GENERIC_MOVEMENT_SPEED:
                    stt = GooPMMOItems.Stat(GooPMMOItemsItemStats.MOVEMENT_SPEED);
                    break;
                case GENERIC_ATTACK_DAMAGE:
                    stt = GooPMMOItems.Stat(GooPMMOItemsItemStats.ATTACK_DAMAGE);
                    break;
                case GENERIC_MAX_HEALTH:
                    stt = GooPMMOItems.Stat(GooPMMOItemsItemStats.MAX_HEALTH);
                    break;
                case GENERIC_ARMOR:
                    stt = Gunging_Ootilities_Plugin.useMMOLibDefenseConvert ? ItemStats.DEFENSE : ItemStats.ARMOR;
                    break;
                case GENERIC_ARMOR_TOUGHNESS:
                    stt = GooPMMOItems.Stat(GooPMMOItemsItemStats.ARMOR_TOUGHNESS);
                    break;
                case GENERIC_ATTACK_SPEED:
                    stt = GooPMMOItems.Stat(GooPMMOItemsItemStats.ATTACK_SPEED);
                    break;
                case GENERIC_KNOCKBACK_RESISTANCE:
                    stt = GooPMMOItems.Stat(GooPMMOItemsItemStats.KNOCKBACK_RESISTANCE);
                    break;
                case GENERIC_LUCK:
                    stt = GooPMMOItems.LUCK;
                    break;
                default:
                    return null;
            }

            // Get value
            mData = (DoubleData) mmoitem.getData(stt);

            // Retrieve existing
            double vData = 0.0;
            double cData = 0.0;
            if (mData != null) {

                // Get Current Value
                cData = mData.getValue();

            } else {

                // Otherwise apply operation to zero
                cData = 0.0;
            }

            // Apply operation
            vData = operation.apply(cData);

            // Get difference
            vData -= cData;

            // Obtain difference
            mData = new DoubleData(vData);
            if (result != null) { result.setValue(vData + cData); }

            // Append
            mmoitem.mergeData(stt, mData, null);

            // Get Finished Product
            return mmoitem.newBuilder().build();

            // Seems that those Types/IDs did not match wth
        } catch (NullPointerException e) {
            return null;
        }
    }


    /**
     * Sets the Tier of target Item Stack. Will convert into MMOItem if vanilla, and tier is specified.
     * @param iSource Item Stack you want to know/modify the tier of.
     * @param newTier The new tier, or null if you just want to read the current.
     * @param finalTier Stores the name of the tier the item had at the end of this operation.
     * @return The tier name at the end of the operation, or null if it has no tier.
     */
    public static ItemStack SetTier(NBTItem iSource, ItemTier newTier, RefSimulator<String> finalTier) {

        // Test of existance of such type and IDs
        try {

            // Attempt to get MMOItem Instance
            MMOItem mmoitem = LiveFromNBT(iSource);
            if (mmoitem == null) { return null; }

            // Get Socket Data
            StringData tData = null;

            // Has data?
            if (mmoitem.hasData(GooPMMOItems.Stat(GooPMMOItemsItemStats.TIER))) {

                // TData is that, swell
                tData = (StringData) mmoitem.getData(GooPMMOItems.Stat(GooPMMOItemsItemStats.TIER));

                // Store
                if (finalTier != null) { finalTier.setValue(tData.toString()); }

            // No tier, that sets the return to 'none'
            } else if (finalTier != null) { finalTier.setValue("none"); }

            // Apply if non-null
            if (newTier != null) {

                // Create
                tData = new StringData(newTier.getId());

                // Append
                mmoitem.setData(GooPMMOItems.Stat(GooPMMOItemsItemStats.TIER), tData);

                // Store
                if (finalTier != null) { finalTier.setValue(newTier.getId()); }
            }

            // Get Finished Product
            return mmoitem.newBuilder().build();

            // Seems that those Types/IDs did not match wth
        } catch (NullPointerException e) {
            return null;
        }
    }
    //endregion

    //region Identifying MMOItems

    /**
     * Forces an update of MMOItems player equipment
     */
    public static void UpdatePlayerEquipment(@NotNull OfflinePlayer p) {

        PlayerData.get(p.getUniqueId()).updateInventory();
    }

    /**
     * Checks if that thing is a MMOItem
     */
    @NotNull
    public static Boolean IsMMOItem(@Nullable ItemStack targetItem) {

        // If null then no
        if (targetItem == null) { return false; }

        // Gettat Target NBT
        NBTItem tNBT = NBTItem.get(targetItem);

        // Is it an mmoItem?
        return tNBT.hasType();
    }

    /**
     * Checks if that thing is a MMOItem that matches that type and that ID
     */
    @NotNull
    public static Boolean MMOItemMatch(ItemStack targetItem, String type, String idName) {

        // Gettat Target NBT
        NBTItem tNBT = NBTItem.get(targetItem);

        // Is it an mmoItem?
        if (tNBT.hasType()) {

            // Yea but is it the same type?
            if (type.equalsIgnoreCase(tNBT.getString("MMOITEMS_ITEM_TYPE"))) {

                // Vro are they named the same?
                return "*".equals(idName) || idName.equalsIgnoreCase(tNBT.getString("MMOITEMS_ITEM_ID"));
            }
        }

        return  false;

        //net.Indyuce.mmoitems.api.Type tMMoitem = tNBT.getType();
        //logReturn.add(ChatColor.YELLOW + "MMOItem Type: " + ChatColor.DARK_AQUA + tMMoitem.getId());
        // ItemStat BREAKDOWN
        // Stat Casual Name: ItemStat.getName();
        // MMI Browse Name: ItemStat.getId();
        // NBT Tag Name: ItemStat.getNBTPath();
        // YML-Name: ItemStat.getPath();
        // To get the value: NBTItem.getStat(ItemStat)

        //logReturn.add(ChatColor.GRAY + "\u00a7lStats:");
        //for (ItemStat statt : tMMoitem.getAvailableStats()) { logReturn.add("\u00a7e - \u00a77Stat: \u00a73" + statt.getId() + "\u00a7b " + tNBT.getStat(statt)); }

        //if (tMMoitem.getItemSet() != null) { logReturn.add(ChatColor.GRAY + "Item Set:" + ChatColor.DARK_AQUA + tMMoitem.getItemSet().getName()); }
        //if (tMMoitem.getParent() != null) { logReturn.add(ChatColor.YELLOW + "Parent Name:" + ChatColor.DARK_AQUA + tMMoitem.getParent().getName()); }

        //logReturn.add(ChatColor.YELLOW + "Equipment Type:" + ChatColor.DARK_AQUA + tMMoitem.getEquipmentType().toString());
        //logReturn.add(ChatColor.GRAY + "\u00a7lNBT Tags:");
        //for (String tagg : tNBT.getTags()) { logReturn.add("\u00a7e - \u00a77" + tagg); }
        //String tagg = "";
        //tagg = "MMOITEMS_ITEM_ID"; logReturn.add("\u00a7e - \u00a77" + tagg + " \u00a7b" + tNBT.getString(tagg));
        //tagg = "MMOITEMS_ITEM_TYPE"; logReturn.add("\u00a7e - \u00a77" + tagg + " \u00a7b" + tNBT.getString(tagg));
    }

    static List<Type> lastTypes = new ArrayList<>();
    public static void GetMMOItem_Types() {
        // Clear da shit
        lastTypes.clear();

        // Get those types
        TypeManager types = MMOItems.plugin.getTypes();

        // Add them
        for (net.Indyuce.mmoitems.api.Type t :  types.getAll()) {

            // To the Type Rememberance
            lastTypes.add(t);
        }
    }
    public static ArrayList<String> GetTierNames() {

        // New ArrayList
        ArrayList<String> ret = new ArrayList<>();

        // Add for each tier
        for (ItemTier t : MMOItems.plugin.getTiers().getAll()) {

            // Add name
            ret.add(t.getId());
        }

        return ret;
    }
    public static ArrayList<String> GetMMOItem_TypeNames() {
        // Clear da shit
        lastTypes.clear();

        TypeManager types = MMOItems.plugin.getTypes();
        ArrayList<String> lTypes = new ArrayList<String>();

        // Add them
        for (net.Indyuce.mmoitems.api.Type t :  types.getAll()) {

            // To the Type Rememberance
            lastTypes.add(t);

            // To the returning string
            lTypes.add(t.getId());
        }

        return lTypes;
    }
    public static List<String> GetMMOItem_IDNames(String ofType) {
        // Make sure it exists
        if (lastTypes != null) { if (lastTypes.size() == 0) { GetMMOItem_Types(); } } else { GetMMOItem_Types(); }

        // Well at this point we pretty sure that shit work
        if (lastTypes != null) {

            // I guess
            if (lastTypes.size() > 0) {

                // Get those types
                TypeManager types = MMOItems.plugin.getTypes();
                Type typ = types.get(ofType);

                // Did it exist?
                if (typ != null){

                    // Alr is that even a valid type
                    if (lastTypes.contains(typ)){

                        // I suppose so
                        List<String> idNames = new ArrayList<String>();
                        // Well whats all those IDs
                        //////// ItemManager itemManager = MMOItems.plugin.getItems(). //BRUH NO LIST

                        return idNames;
                    }
                }
            }
        }

        return null;
    }
    static ArrayList<String> statNames = null;
    public static ArrayList<String> GetMMOItem_StatNames() {
        if (statNames != null) { return statNames; }

        // Get Ret
        ArrayList<String> ret = new ArrayList<>();

        // Git Stats
        for (ItemStat st : MMOItems.plugin.getStats().getAll()) {

            // Ad by nmae
            ret.add(st.getId());
        }

        // Return
        statNames = ret;

        return statNames;
    }

    /**
     * Returns the MMOItem type of target NBT Item if it has type.
     */
    @Nullable
    public static Type GetMMOItemType(@Nullable NBTItem target) {

        // From string
        return GetMMOItemTypeFromString((String) GetMMOItemTypeRaw(target));
    }

    @Nullable
    public static Type GetParentmostMMOItemType(@Nullable ItemStack iSource) {

        if (!OotilityCeption.IsAirNullAllowed(iSource)) {

            if (!IsMMOItem(iSource)) { return null; }
            NBTItem iNBT = NBTItem.get(iSource);

            // As helm
            Type mthelm = GooPMMOItems.GetMMOItemType(iNBT);

            // If helm existed
            if (mthelm != null) {

                // Bringeth highest
                while (mthelm.isSubtype()) { mthelm = mthelm.getParent(); }

                // Return thay
                return mthelm;
            }
        }

        return null;
    }

    /**
     * Returns a STRING for MMOItems 6.5+, or a MMOItem Type for 6.4-
     */
    @Nullable
    public static String GetMMOItemTypeRaw(@Nullable NBTItem target) {

        // Get type
        return target.getType();
    }

    /**
     * Will get the MMOItem Type of target ItemStack
     * @param iSource ItemStack you dont even know if its a MMOItem
     * @param failureRet What to return if the ItemStack is NOT a MMOItem
     * @return Either the MMOItem Type (if MMOItem) or failureRet
     */
    @Nullable
    public static String GetMMOItemType(@Nullable ItemStack iSource, @Nullable String failureRet) {

        // If MMOItem
        if (IsMMOItem(iSource)) {

            // What type is it
            NBTItem itm = NBTItem.get(iSource);

            // Return thay
            return itm.getString("MMOITEMS_ITEM_TYPE");

            // If its not a MMOItem
        } else {

            // Clearly returns an invalid MMOItem Type
            return failureRet;
        }
    }

    /**
     * Will get the MMOItem Type of target ItemStack
     * @param iSource ItemStack you dont even know if its a MMOItem
     * @param failureRet What to return if the ItemStack is NOT a MMOItem
     * @return Either the MMOItem Type (if MMOItem) or failureRet
     */
    @Nullable
    @Contract("_, !null -> !null")
    public static String GetMMOItemID(@Nullable ItemStack iSource, @Nullable String failureRet) {

        // If MMOItem
        if (IsMMOItem(iSource)) {

            // What type is it
            NBTItem itm = NBTItem.get(iSource);

            // Return thay
            String ret = itm.getString("MMOITEMS_ITEM_ID");

            return ret == null ? failureRet : ret;

            // If its not a MMOItem
        } else {

            // Clearly returns an invalid MMOItem Type
            return failureRet;
        }
    }

    /**
     * Gets the MMOItem TYPE and MMOItem ID of an item simultaneously.
     * <p></p>
     * If it is not a MMOItem, the RefSimulators will be unchanged.
     * @param iSource Item that may be a MMOItem.
     * @param typeStorage RefSimulator to store TYPE
     * @param idStorage RefSimulator to store ID
     */
    public static void GetMMOItemInternals(@Nullable ItemStack iSource, @NotNull RefSimulator<String> typeStorage, @NotNull RefSimulator<String> idStorage) {

        // If MMOItem
        if (IsMMOItem(iSource)) {

            // What type is it
            NBTItem itm = NBTItem.get(iSource);

            // Return thay
            typeStorage.SetValue(itm.getString("MMOITEMS_ITEM_TYPE"));
            idStorage.SetValue(itm.getString("MMOITEMS_ITEM_ID"));
        }

        //CNV// else { OotilityCeption.Log("\u00a78GOOPMI \u00a7cINTERNALS\u00a77 Object " + OotilityCeption.GetItemName(iSource) + " \u00a77is \u00a7cnot\u00a77 a MMOItem"); }
    }

    /**
     * Checks if it is a valid MMOItems tier.
     */
    public static boolean TierExists(@Nullable String tier) { if (tier == null) { return false; } return MMOItems.plugin.getTiers().has(tier); }

    /**
     * Checks if it is a valid MMOItems Modifier.
     */
    @Nullable public static ItemStack ModifierOperation(@Nullable String rawModifier, @Nullable ItemStack mmo, @Nullable RefSimulator<String> logger) {
        if (rawModifier == null || mmo == null) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Unspecified modifier or item");
            return null; }

        // musta be mmo
        if (!IsMMOItem(mmo)) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Item is not a MMOItem.");
            return null; }

        MMOItemTemplate template = MMOItems.plugin.getTemplates().getTemplate(NBTItem.get(mmo));

        // Sleap
        if (template == null) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "Item is a MMOItem but the template is not loaded, was it deleted?");
            return null; }

        boolean random = rawModifier.equalsIgnoreCase("random");
        boolean clear = rawModifier.equalsIgnoreCase("none");

        boolean modifierLocal = template.hasModifier(rawModifier);
        boolean modifierExists = modifierLocal || MMOItems.plugin.getTemplates().hasModifier(rawModifier);

        // Ay exist?
        if (!random && !clear && !modifierExists) {
            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "There is no modifier of name \u00a73" + rawModifier + "\u00a77.");
            return null; }

        NBTItem nbt = NBTItem.get(mmo);
        LiveMMOItem live = new LiveMMOItem(nbt);

        // Perform operation
        if (random) {

            // Shuffle
            List<TemplateModifier> modifiers = new ArrayList<>(template.getModifiers().values());

            // Add all globally loaded ones
            modifiers.addAll(MMOItems.plugin.getTemplates().getModifiers());

            // Ay exist?
            if (modifiers.size() == 0) {
                OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPFailFeedback, "MMOItem \u00a7e" + template.getType().getId() + " " + template.getId() + "\u00a77 has no modifiers, cant pick a random one thus.");
                return null;
            }

            // Get Modifier
            TemplateModifier modifier = modifiers.get(OotilityCeption.GetRandomInt(0, modifiers.size() - 1));
            UUID modUUID = UUID.randomUUID();

            MMOItemBuilder snoozer = template.newBuilder();

            for (ItemStat stat : modifier.getItemData().keySet()) {

                // Randomize yeah
                StatData statData = modifier.getItemData().get(stat).randomize(snoozer);

                // Is this mergeable?
                if (stat.getClearStatData() instanceof Mergeable) {


                    // Apply onto Stat History
                    StatHistory hist = StatHistory.from(live, stat);

                    // Apply
                    hist.registerModifierBonus(modUUID, statData);

                } else {

                    // Set, there is no more.
                    live.setData(stat, statData);
                }
            }

            // Rename
            if (modifier.hasNameModifier()) {

                // Get name data
                StatHistory hist = StatHistory.from(live, ItemStats.NAME);

                // Create new Name Data
                NameModifier namemod = modifier.getNameModifier();
                NameData modName = new NameData("");

                // Include modifier information
                if (namemod.getType() == NameModifier.ModifierType.PREFIX) { modName.addPrefix(namemod.getFormat()); }
                if (namemod.getType() == NameModifier.ModifierType.SUFFIX) { modName.addSuffix(namemod.getFormat()); }

                // Register onto SH
                hist.registerModifierBonus(modUUID, modName);
            }

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Applied \u00a7b" + modifier.getId() + "\u00a77 to " + OotilityCeption.GetItemName(mmo));

        } else if (clear) {

            // Well I guess go through all stat histories
            for (StatHistory hist : live.getStatHistories()) {

                // Clear modifiers
                hist.clearModifiersBonus();
            }

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Cleared modifiers of " + OotilityCeption.GetItemName(mmo));

        } else {

            // Get Modifier
            TemplateModifier modifier = modifierLocal ? template.getModifier(rawModifier) : MMOItems.plugin.getTemplates().getModifier(rawModifier);
            UUID modUUID = UUID.randomUUID();

            MMOItemBuilder snoozer = template.newBuilder();

            for (ItemStat stat : modifier.getItemData().keySet()) {

                // Randomize yeah
                StatData statData = modifier.getItemData().get(stat).randomize(snoozer);

                // Is this mergeable?
                if (stat.getClearStatData() instanceof Mergeable) {


                    // Apply onto Stat History
                    StatHistory hist = StatHistory.from(live, stat);

                    // Apply
                    hist.registerModifierBonus(modUUID, statData);

                } else {

                    // Set, there is no more.
                    live.setData(stat, statData);
                }
            }

            // Rename
            if (modifier.hasNameModifier()) {

                // Get name data
                StatHistory hist = StatHistory.from(live, ItemStats.NAME);

                // Create new Name Data
                NameModifier namemod = modifier.getNameModifier();
                NameData modName = new NameData("");

                // Include modifier information
                if (namemod.getType() == NameModifier.ModifierType.PREFIX) { modName.addPrefix(namemod.getFormat()); }
                if (namemod.getType() == NameModifier.ModifierType.SUFFIX) { modName.addSuffix(namemod.getFormat()); }

                // Register onto SH
                hist.registerModifierBonus(modUUID, modName);
            }

            OotilityCeption.Log4Success(logger, Gunging_Ootilities_Plugin.sendGooPSuccessFeedback, "Applied \u00a7b" + modifier.getId() + "\u00a77 to " + OotilityCeption.GetItemName(mmo));
        }

        // Yeah that's it
        return live.newBuilder().build();
    }

    public static final String invalidTypeID = "no u";
    //endregion

    //region Item Stat Stuff
    /**
     * Apparently, for MMOItems 6.3, instead of using <code>ItemStat.DURABILITY</code> you have to use <code>ItemStats.DURABILITY</code>.
     *
     * Also, some item stats evolve so this will give you the item stat closest to what you mean if it is not loaded in that MI version.
     * @param statt Stat you want
     * @return The stat you want or the closest stat (if theres one that makes sense) if it doesnt exist in thay MMOItems version.
     */
    @Nullable
    public static ItemStat Stat(@Nullable String statt) {
        if (statt == null) { return null;}
        statt = statt.toUpperCase().replace(" ", "_").replace("-","_");

        // Does it make sense?
        try {

            // Try get
            GooPMMOItemsItemStats trueStat = GooPMMOItemsItemStats.valueOf(statt);

            // Return
            return Stat(trueStat);

        // Name fail
        } catch (IllegalArgumentException e) {

            // No lol
            return StatFromString(statt);
        }
    }

    @Nullable
    public static ItemStat StatFromString(@Nullable String statt) {
        if (statt == null) { return null; }

        // Ret
        ItemStat fo = idstringStats.get(statt);
        if (fo != null) { return fo; }

        // Try as goop misc
        if (statt.startsWith(miscPrefix)) {

            // Gotten
            ItemStat miscGotten = GetMiscStat(statt);

            // Ret
            if (miscGotten != null) {

                // Put
                idstringStats.put(statt, miscGotten);
                return miscGotten;
            }
        }

        // Get that stat that way
        ItemStat o = MMOItems.plugin.getStats().get(statt);

        // Append
        if (o != null) { idstringStats.put(statt, o); }

        // Return
        return o;
    }
    public static HashMap<String, ItemStat> idstringStats = new HashMap<>();

    /**
     * Apparently, for MMOItems 6.3, instead of using <code>ItemStat.DURABILITY</code> you have to use <code>ItemStats.DURABILITY</code>.
     * <p></p>
     * Also, some item stats evolve so this will give you the item stat closest to what you mean if it is not loaded in that MI version.
     * @param statt Stat you want
     * @return The stat you want or the closest stat (if theres one that makes sense) if it doesnt exist in thay MMOItems version.
     */
    @Nullable
    public static ItemStat Stat(@Nullable GooPMMOItemsItemStats statt) {
        if (statt == null) { return null;}

        // Return identified
        ItemStat o = identifiedStats.get(statt);
        if (o != null) { return o; }

        // Get directly as name
        ItemStat ret = MMOItems.plugin.getStats().get(statt.name());

        if (ret == null) {
            switch (statt) {
                case NBT_TAGS: return ItemStats.NBT_TAGS;
                case RESTORE: return ItemStats.RESTORE_HEALTH;

                default:
                    Gunging_Ootilities_Plugin.theOots.CPLog("No Update Stat for \u00a7e" + statt.name() + "\u00a77 is defined!");
                    break;
            }
        }

        // Return that
        o = ret;

        // Append
        if (o != null) { identifiedStats.put(statt, o); }

        // Return
        return o;
    }
    public static HashMap<GooPMMOItemsItemStats, ItemStat> identifiedStats = new HashMap<>();

    // Get Stats AHH
    @Nullable
    public static MMOItem MergeStatData(@NotNull NBTItem iSource, @NotNull ItemStat stat, @NotNull StatData statDaa) {

        // Test of existance of such type and IDs
        try {

            // Attempt to get MMOItem Instance
            MMOItem mmoitem = LiveFromNBT(iSource);

            //STAT//OotilityCeption. Log("    §8>§b£§8<§7 Got Live");

            // Its not socket gems is it?
            if ((GooPMMOItems.Stat(GooPMMOItemsItemStats.GEM_SOCKETS)).getName().equals(stat.getName())) {

                //STAT//OotilityCeption. Log("    §8>§b£§8<§7 As GemstoneData");

                // Must be string list or gemstone
                if (!(statDaa instanceof GemSocketsData) && !(statDaa instanceof StringListData)) {

                    // Get Array
                    ArrayList<String> slottz = ValueOfStringListData(statDaa);

                    //STAT//OotilityCeption. Log("    §8>§3£§8<§7 Got List Data");

                    // Get Socket Data
                    statDaa = new GemSocketsData(slottz);

                    //STAT//OotilityCeption. Log("    §8>§3£§8<§7 Converted to Gemstone Data");

                } else {

                    //STAT//OotilityCeption. Log("    §8>§c£§8<§7 Not a valid Stat Data");
                    return null;
                }
            }

            // just append
            mmoitem.mergeData(stat, statDaa, null);
            StatHistory.from(mmoitem, stat).consolidateEXSH();

            //STAT//OotilityCeption. Log("    §8>§b£§8<§7 Data was Set");

            // Return thay
            return mmoitem;

            // Seems that those Types/IDs did not match wth
        } catch (Exception e) {

            //STAT//OotilityCeption. Log("   \u00a74***\u00a7c Exception - \u00a7n" + e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the stat data of an item, <b>make sure to have a good reason to set it instead of merge it.</b>
     *
     * @param iSource Target Item
     * @param stat Target Stat
     * @param statDaa Setting Data
     * @param asEXSHConsolidate You must have done something to the ORIGINAL and External SH data, setting this
     *                          to <code>true</code> will clear both of these and set the one you are providing
     *                          as the single EXSH data in this.
     *
     * @return The item with its stat recalculated
     */
    @Nullable
    public static MMOItem SetStatData(@NotNull NBTItem iSource, @NotNull ItemStat stat, @NotNull StatData statDaa, boolean asEXSHConsolidate) {

        // Test of existance of such type and IDs
        try {

            // Attempt to get MMOItem Instance
            MMOItem mmoitem = LiveFromNBT(iSource);

            //STAT//OotilityCeption. Log("    §8>§b£§8<§7 Got Live");

            // Its not socket gems is it?
            if ((GooPMMOItems.Stat(GooPMMOItemsItemStats.GEM_SOCKETS)).getName().equals(stat.getName())) {

                //STAT//OotilityCeption. Log("    §8>§b£§8<§7 As GemstoneData");

                // Must be string list or gemstone
                if (!(statDaa instanceof GemSocketsData) && !(statDaa instanceof StringListData)) {

                    // Get Array
                    ArrayList<String> slottz = ValueOfStringListData(statDaa);

                    //STAT//OotilityCeption. Log("    §8>§3£§8<§7 Got List Data");

                    // Get Socket Data
                    statDaa = new GemSocketsData(slottz);

                    //STAT//OotilityCeption. Log("    §8>§3£§8<§7 Converted to Gemstone Data");

                } else {

                    //STAT//OotilityCeption. Log("    §8>§c£§8<§7 Not a valid Stat Data");
                    return null;
                }
            }

            // just append
            StatHistory hist = StatHistory.from(mmoitem, stat);


            //OotilityCeption.Log("\u00a76<---- \u00a7eBefore Setting \u00a76---->");
            //logSH(hist);

            if (asEXSHConsolidate) {

                // Clear original and set as external
                hist.setOriginalData(hist.getItemStat().getClearStatData());
                hist.getExternalData().clear();
                hist.registerExternalData(statDaa);

            } else {

                // Set as original
                hist.setOriginalData(statDaa);
            }
            mmoitem.setData(stat, hist.recalculate(mmoitem.getUpgradeLevel()));

            //STAT//OotilityCeption. Log("    §8>§b£§8<§7 Data was Set");

            // Return thay
            return mmoitem;

            // Seems that those Types/IDs did not match wth
        } catch (Exception e) {

            //STAT//OotilityCeption. Log("   \u00a74***\u00a7c Exception - \u00a7n" + e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Will return null if it fails for any reason.
     */
    @Nullable
    public static ItemStack SetDoubleStatData(@Nullable ItemStack itm, @Nullable String stat, double value) {
        if (stat == null) { return null; }
        return SetDoubleStatData(itm, Stat(stat), value);
    }
    @Nullable
    public static ItemStack SetDoubleStatData(@Nullable ItemStack itm, @Nullable ItemStat stat, double value) {
        if (itm == null) { return null; }
        if (!IsMMOItem(itm)) { return null; }
        if (stat == null) { return null; }
        if (!(stat instanceof DoubleStat)) { return null; }

        // Create Stat Data
        DoubleData dData = new DoubleData(value);
        NBTItem nbt = NBTItem.get(itm);

        // Put
        MMOItem result = MergeStatData(nbt, stat, dData);

        // Return
        if (result == null) { return null; }

        // Build
        return result.newBuilder().build();
    }

    /**
     * Will return null if it fails for any reason.
     */
    @Nullable
    public static ItemStack SetStringStatData(@Nullable ItemStack itm, @Nullable String stat, String value) {
        if (stat == null) { return null; }
        //STAT//OotilityCeption. Log("   \u00a76*\u00a77 Nonull Stat ");
        return SetStringStatData(itm, Stat(stat), value);
    }
    @Nullable
    public static ItemStack SetStringStatData(@Nullable ItemStack itm, @Nullable ItemStat stat, @Nullable String value) {
        if (itm == null) { return null; }
        if (!IsMMOItem(itm)) { return null; }
        if (stat == null) { return null; }
        //STAT//OotilityCeption. Log("   \u00a76*\u00a77 Nonull Qualificatons ");


        // Create Stat Data
        StatData sData;
        NBTItem nbt = NBTItem.get(itm);

        // String list or String?
        if (stat instanceof StringStat) {
            //STAT//OotilityCeption. Log("   \u00a76*\u00a77 As \u00a7eString Stat ");

            // Get as SData
            sData = new StringData(value);

        // Perform List Operations
        } else {
            //STAT//OotilityCeption. Log("   \u00a76*\u00a77 As \u00a7eString List Stat ");

            // Cant be null
            if (value == null) {
                //STAT//OotilityCeption. Log("   \u00a76*\u00a7c No Value ");
                return null;
            }

            // Check as list
            if (!(stat instanceof StringListStat) && !(stat.equals(Stat(GooPMMOItemsItemStats.GEM_SOCKETS)))) {
                //STAT//OotilityCeption. Log("   \u00a76*\u00a7c Not correct stat ");
                return null;
            }

            // Get already existing list
            ArrayList<String> old = GetStringListStatValue(nbt, stat);
            if (old == null) {
                //STAT//OotilityCeption. Log("   \u00a76*\u00a77 No Old Value found, \u00a7eCreated ");
                old = new ArrayList<>();
            }

            // Does it begin with a -
            if (value.startsWith("-")) {
                //STAT//OotilityCeption. Log("   \u00a76*\u00a77 Removant ");

                // Cook
                String v = value.substring(1);

                // Is it contained?
                if (old.contains(v)) {
                    //STAT//OotilityCeption. Log("   \u00a76*\u00a7a Removed Old ");

                    // Remove
                    old.remove(v);

                } else {
                    //STAT//OotilityCeption. Log("   \u00a76*\u00a7e Missing Faulure ");

                    // Nope, fail
                    return null;
                }

            } else {
                //STAT//OotilityCeption. Log("   \u00a76*\u00a7a Added ");

                // Append
                old.add(value);
            }

            // Set Data
            sData = new StringListData(old);

        }

        // Put
        MMOItem result = SetStatData(nbt, stat, sData, false);

        // Return
        if (result == null) {
            //STAT//OotilityCeption. Log("   \u00a76*\u00a7c Fatal Result Error ");
            return null;
        }

        // Build
        return result.newBuilder().build();
    }

    @NotNull
    public static ItemStack Build(@NotNull NBTItem itm) {
        //STAT//OotilityCeption. Log("   \u00a7a*\u00a77 Building ");

        // Build
        return (LiveFromNBT(itm)).newBuilder().build();
    }
    //endregion
}
