package gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.mmoitem_shrubs.MMOItem_Shrub;
import gunging.ootilities.mmoitem_shrubs.MMOItem_Shrub_Manager;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class XBow_Loaded_Stat extends StringStat {

    public XBow_Loaded_Stat() { super("GOOP_XBOW_LOADED", GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CROSSBOW), "Loaded Item Display Name", new String[]{"The display name of the arrow", "preloaded in this crossbow."}, new String[]{"all"}, GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CROSSBOW) ); }

    //@Override
    public void whenApplied(@NotNull ItemStackBuilder item, @NotNull StringData data) {

        // Create a new ultimate item stack
        ItemStack loadd = new ItemStack(Material.ARROW);

        // Change its name
        OotilityCeption.RenameItem(loadd, data.toString(), null);

        // Get meta
        ItemMeta iiMeta = item.getMeta();

        if (iiMeta instanceof CrossbowMeta) {

            // Edit actual item
            CrossbowMeta iMeta = (CrossbowMeta) iiMeta;
            ArrayList<ItemStack> ldds = new ArrayList<>();
            ldds.add(loadd);
            iMeta.setChargedProjectiles(ldds);

            // Hopefuly this will have charged the projectile
            item.getItemStack().setItemMeta(iMeta);
        }
    }
}
