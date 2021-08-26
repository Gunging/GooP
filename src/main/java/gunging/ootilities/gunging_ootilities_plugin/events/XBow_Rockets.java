package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPMMOItemsItemStats;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionMaterials;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class XBow_Rockets implements Listener {

    public static HashMap<UUID, Player> fireworkSources = new HashMap<>();
    public static HashMap<UUID, Float> bowDrawForce = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBowDraw(EntityShootBowEvent event) {

        // No need if cancelled
        if (event.isCancelled()) { return; }

        // If the shooter is a PLAYER and the shot is a FIREWORK
        if (event.getEntity() instanceof Player) {

            // Get item in hand?
            ItemStack handItem = event.getBow();
            if (!OotilityCeption.IsAirNullAllowed(handItem)) {

                // Is it a bow?
                if (handItem.getType() == Material.BOW) {

                    // Put force as whatever it is
                    bowDrawForce.put(event.getEntity().getUniqueId(), event.getForce());

                    // Is it a crossbow?
                } else if (OotilityCeption.IsCrossbow(handItem.getType())) {

                    // Put force as 1, for crossbows only fire when fully loaded
                    bowDrawForce.put(event.getEntity().getUniqueId(), 1.0F);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnXBowFire(EntityShootBowEvent event) {

        // No need if cancelled
        if (event.isCancelled()) { return; }

        // If the shooter is a PLAYER and the shot is a FIREWORK
        if (event.getEntity() instanceof Player) {

            // Store fireworks PVP data
            if (event.getProjectile() instanceof Firework) {

                // Add to firework sources, if there is any entity with scoreboard links alv
                if (ScoreboardLinks.olEntities.size() > 0) {

                    // Store the source of thay firework
                    fireworkSources.put(event.getProjectile().getUniqueId(), (Player) event.getEntity());

                    // Otherwise clear, for fun!
                } else { fireworkSources.clear(); }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnXBowInteract(PlayerInteractEvent event) {

        // Ignore completely if not 1.14 yet
        if (GooP_MinecraftVersions.GetMinecraftVersion() < 14.0 || !Gunging_Ootilities_Plugin.foundMMOItems) { return; }

        // Is the held item non-null
        if (!OotilityCeption.IsAirNullAllowed(event.getItem())) {

            // Gather
            ItemStack xBow = event.getItem();

            // If is crossbow
            if (xBow.getType() == GooP_MinecraftVersions.GetVersionMaterial(GooPVersionMaterials.CROSSBOW)) {

                // Does it have both the uninteractable and arrow item stat things?
                Boolean interacc = GooPMMOItems.GetBooleanStatValue(xBow, GooPMMOItems.Stat(GooPMMOItemsItemStats.DISABLE_INTERACTION), null, false);
                String errow = GooPMMOItems.GetStringStatValue(xBow, GooPMMOItems.XBOW_LOADED_STAT, null, false);

                // IF both non-null
                if (interacc != null && errow != null) {

                    // Get Meta
                    CrossbowMeta iMeta = (CrossbowMeta) xBow.getItemMeta();

                    // Is it empty now? Then replenish the original
                    if (iMeta.getChargedProjectiles().size() == 0) {

                        // Create a new ultimate item stack
                        ItemStack loadd = new ItemStack(Material.ARROW);

                        // Change its name
                        OotilityCeption.RenameItem(loadd, errow, null);

                        // Edit actual item
                        ArrayList<ItemStack> ldds = new ArrayList<>();
                        ldds.add(loadd);
                        iMeta.setChargedProjectiles(ldds);

                        // Hopefuly this will have charged the projectile
                        xBow.setItemMeta(iMeta);
                    }
                }
            }
        }
    }
}
