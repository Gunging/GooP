package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Teleports the caster to the target, will teleport to somewhere hidden from the target,
 * apply invisibility, change gamemode to spectator, and set visible equipment to air.
 */
public class UCMSilentTeleport extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

    public UCMSilentTeleport(CustomMechanic manager, String line, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), line, mlc);
        construct(mlc);
    }
    public UCMSilentTeleport(SkillExecutor manager, String line, MythicLineConfig mlc) {
        super(manager, line, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        setAsyncSafe(false);
        //targetArmorStands = mlc.getBoolean(new String[]{"targetarmorstands", "ta"}, false);
    }

    public void hide(@NotNull LivingEntity whom) {

        // Apply Invisibility
        whom.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, false,false));

        // Yeah the rest of the stuff
        if (whom instanceof Player) {

            // Spectator Mode
            ((Player) whom).setGameMode(GameMode.SPECTATOR);

            // Items to drop
            ArrayList<ItemStack> dropped = new ArrayList<>();

            // Haha
            ItemStack helm = ((Player) whom).getInventory().getHelmet();
            if (helm != null) { ((Player) whom).getInventory().setHelmet(null); dropped.add(helm); }

            ItemStack ches = ((Player) whom).getInventory().getChestplate();
            if (ches != null) { ((Player) whom).getInventory().setChestplate(null); dropped.add(ches); }

            ItemStack legs = ((Player) whom).getInventory().getLeggings();
            if (legs != null) { ((Player) whom).getInventory().setLeggings(null); dropped.add(legs); }

            ItemStack bots = ((Player) whom).getInventory().getBoots();
            if (bots != null) { ((Player) whom).getInventory().setBoots(null); dropped.add(bots); }

            ItemStack off = ((Player) whom).getInventory().getItemInOffHand();
            if (!OotilityCeption.IsAirNullAllowed(off)) { ((Player) whom).getInventory().setItemInOffHand(null); dropped.add(off); }

            ItemStack main = ((Player) whom).getInventory().getItemInMainHand();
            if (!OotilityCeption.IsAirNullAllowed(main)) { ((Player) whom).getInventory().setItemInMainHand(null); dropped.add(main); }

            // Attempt to put it in the non-shown inventory slots I guess
            for (ItemStack drop : dropped) {

                // Find a good spot I guess
                for (int i = 9; i < 36; i++) {

                    // Current
                    ItemStack current = ((Player) whom).getInventory().getItem(i);

                    // What
                    if (OotilityCeption.IsAirNullAllowed(current)) {

                        // Put there
                        ((Player) whom).getInventory().setItem(i, drop);
                        break;

                    // Similar?
                    } else if (current.isSimilar(drop)) {

                        // Add up (might exceed max stack but :snooze:)
                        ((Player) whom).getInventory().setItem(i, OotilityCeption.asQuantity(current, current.getAmount() + drop.getAmount()));
                    }
                }
            }
        }
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) { return castAtLocation(skillMetadata, abstractEntity.getLocation()); }

    @Override
    public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {

        // Get the god damned world
        World world = Bukkit.getWorld(abstractLocation.getWorld().getName());
        if (world == null) { return SkillResult.ERROR; }

        // Clone
        AbstractLocation target = new AbstractLocation(
                abstractLocation.getWorld(),
                abstractLocation.getX(),
                abstractLocation.getY() + 25,
                abstractLocation.getZ(),
                abstractLocation.getYaw(),
                abstractLocation.getPitch());

        // Number #1: Hide the caster grrr
        if (skillMetadata.getCaster().getEntity().getBukkitEntity() instanceof LivingEntity) {

            // Yeah...
            hide((LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity());
        }

        // Look
        target.setDirection(new AbstractVector(abstractLocation.getX() - target.getX(), abstractLocation.getY() - target.getY(), abstractLocation.getZ() - target.getZ()).normalize());

        // Go go go
        skillMetadata.getCaster().getEntity().teleport(target);

        // Success
        return SkillResult.SUCCESS;
    }
}
