package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.EntitiesInRadiusTargeter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntitiesInSlash extends EntitiesInRadiusTargeter {

    @NotNull final SlashLocations<Collection<AbstractEntity>> particleEffect;

    public EntitiesInSlash(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);

        particleEffect = new SlashLocations<>(mlc, (data, slashedLocation, funnies) -> {

            // Create new set
            HashSet<AbstractEntity> set = new HashSet<>();

            // Exactly what the doctor ordered
            return getEntitiesNearPoint(data, slashedLocation);
        });

        // Time delay not supported
        particleEffect.slashDelay = PlaceholderFloat.of("0");
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata) {

        // Only include each entity once
        HashMap<UUID, AbstractEntity> v = new HashMap<>();

        // For each location targets
        if (skillMetadata.getLocationTargets() != null && GOOPCManager.isEntitiesInSlash()) {

            for (AbstractLocation w : skillMetadata.getLocationTargets()) {
                ArrayList<Collection<AbstractEntity>> x = particleEffect.playParticleSlashEffect(skillMetadata, w);
                for (Collection<AbstractEntity> y : x) { for (AbstractEntity z : y) { v.put(z.getUniqueId(), z); } } }
        }

        // For each location targets
        if (skillMetadata.getEntityTargets() != null && GOOPCManager.isEntitiesInSlash()) {

            for (AbstractEntity w : skillMetadata.getEntityTargets()) {
                if (w == null) { continue; }
                ArrayList<Collection<AbstractEntity>> x = particleEffect.playParticleSlashEffect(skillMetadata, w.getLocation());
                for (Collection<AbstractEntity> y : x) { for (AbstractEntity z : y) { v.put(z.getUniqueId(), z); } } }
        }

        // That's the result
        return v.values();
    }
}
