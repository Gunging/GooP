package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MMPHMMOStatPlaceholder extends MMPlaceholder {

    @NotNull final MMPHMMOStatTarget whom;
    public MMPHMMOStatPlaceholder(@NotNull MMPHMMOStatTarget whom) { this.whom = whom; }

    @Override
    public String apply(PlaceholderMeta metadata, String arg) {

        // If valid
        if (arg == null) { return "{missing mmostat name}"; }

        // Retrieve entity
        Entity entity = null;

        // Correct one
        switch (whom) {

            // Owner of the caster, considering the caster as a GooP Minion
            case OWNER: entity = SummonerClassUtils.GetOwner(metadata.getCaster().getEntity().getUniqueId());break;

            // Evidently, the caster
            case CASTER: entity = metadata.getCaster().getEntity().getBukkitEntity(); break;

            // Evidently, the trigger
            case TRIGGER: entity = metadata.getTrigger() != null ? metadata.getTrigger().getBukkitEntity() : null; break;
        }

        // Perhaps its an specific slot
        String invenSlot = null, statName = arg;
        if (arg.contains(".")) { String[] argSplit = arg.split("\\."); invenSlot = argSplit[0]; statName = argSplit[1]; }
        ItemStackSlot specificSlot = OotilityCeption.getInventorySlot(invenSlot);

        // Sleep
        if (entity == null) { return "0000"; }

        // Yes
        return getStat(metadata, statName, specificSlot, entity);
    }

    @NotNull public String getStat(@NotNull PlaceholderMeta meta, @NotNull String statName, @Nullable ItemStackSlot slot, @NotNull Entity asEntity) {

        // Cast to player and get over it
        if (asEntity instanceof Player) { return getStat(meta, statName, slot, (Player) asEntity); }

        // Entity Unsupported
        return "000";
    }

    @NotNull public abstract String getStat(@NotNull PlaceholderMeta meta, @NotNull String statName, @Nullable ItemStackSlot slot, @NotNull Player asPlayer);
}
