package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.GOOPCManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LocationsInSlash extends ILocationSelector {

    @NotNull final SlashLocations<AbstractLocation> particleEffect;

    public LocationsInSlash(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);

        particleEffect = new SlashLocations<>(mlc, (data, slashedLocation, funnies) -> {

            // Exactly what the doctor ordered
            return slashedLocation;
        });

        // Time delay not supported
        particleEffect.slashDelay = PlaceholderFloat.of("0");
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata skillMetadata) {

        // Only include each entity once
        HashSet<AbstractLocation> v = new HashSet<>();

        // For each location targets
        if (skillMetadata.getLocationTargets() != null && GOOPCManager.isLocationsInSlash()) {
            for (AbstractLocation w : skillMetadata.getLocationTargets()) {
                v.addAll(particleEffect.playParticleSlashEffect(skillMetadata, w)); }
        }

        // For each location targets
        if (skillMetadata.getEntityTargets() != null && GOOPCManager.isLocationsInSlash()) {
            for (AbstractEntity w : skillMetadata.getEntityTargets()) {
                if (w == null) { continue; }
                v.addAll(particleEffect.playParticleSlashEffect(skillMetadata, w.getLocation())); }
        }

        // That's the result
        return v;
    }
}
