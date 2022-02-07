package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityComparisonCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CanPvPCondition extends CustomMMCondition implements IEntityComparisonCondition {

    public CanPvPCondition(@NotNull MythicLineConfig mlc) { super(mlc); }

    public boolean check(AbstractEntity entity, AbstractEntity target) {

        // Must exist, the entity
        if (target == null) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c No target");
            return false; }

        // Must exist, the entity
        if (entity == null) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c No entity");
            return false; }

        // Identify entity
        Entity attacker = entity.getBukkitEntity();
        Entity victim = target.getBukkitEntity();

        // Run event
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, new HashMap<>(), new HashMap<>(), false);
        Bukkit.getPluginManager().callEvent(event);

        // Did it get cancelled?
        return !event.isCancelled();
    }
}
