package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class CanPvPCondition extends CustomMMCondition implements IEntityComparisonCondition {

    public CanPvPCondition(@NotNull MythicLineConfig mlc) { super(mlc); }

    public boolean check(AbstractEntity entity, AbstractEntity target) {

        // Cancel if event in progress, to prevent StackOverflow
        if (counterEvent) { return false; }

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
        counterEvent = true;
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0);
        try { Bukkit.getPluginManager().callEvent(event); counterEvent = false; } catch (Throwable ignored) { counterEvent = false; return false; }

        // Did it get cancelled?
        return !event.isCancelled();
    }

    static boolean counterEvent;
}
