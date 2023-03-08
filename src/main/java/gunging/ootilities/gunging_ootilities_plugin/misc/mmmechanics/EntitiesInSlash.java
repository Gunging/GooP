package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
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
    @NotNull PlaceholderFloat slashDensity;

    public EntitiesInSlash(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);

        particleEffect = new SlashLocations<>(mlc, (data, slashedLocation, funnies) -> {

            // Exactly what the doctor ordered
            return getEntitiesNearPoint(data, slashedLocation);
        });

        // Time delay not supported
        particleEffect.slashDelay = PlaceholderFloat.of("0");

        this.slashDensity = mlc.getPlaceholderFloat(new String[]{"density", "den"}, 0F);
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata) {

        // Decide density
        double rad = particleEffect.slashRadius.get(skillMetadata);
        int div = Math.abs(OotilityCeption.RoundToInt(slashDensity.get(skillMetadata))) + 1;
        double step = rad / div;

        // Points density?
        double pts = particleEffect.points.get(skillMetadata);
        double density = pts / rad;

        // Only include each entity once
        HashMap<UUID, AbstractEntity> v = new HashMap<>();

        // Density integral
        for (double s = step; s <= rad; s += step) {

            // Clone params
            SlashLocations<Collection<AbstractEntity>> pEffect;

            // Edit slash
            if (div == 1) {

                // Just the usual
                pEffect = particleEffect;

            } else {

                // Cloned usual
                pEffect = particleEffect.clone();

                // Edit radius to match current step
                pEffect.slashRadius = PlaceholderFloat.of(String.valueOf(s));

                // Edit point density
                pEffect.points = PlaceholderFloat.of(String.valueOf(s * density));
            }

            // For each location targets
            if (skillMetadata.getLocationTargets() != null) { // && GOOPCManager.isEntitiesInSlash()

                for (AbstractLocation w : skillMetadata.getLocationTargets()) {
                    ArrayList<Collection<AbstractEntity>> x = pEffect.playParticleSlashEffect(skillMetadata, w);
                    for (Collection<AbstractEntity> y : x) { for (AbstractEntity z : y) { v.put(z.getUniqueId(), z); } } }
            }

            // For each entity targets
            if (skillMetadata.getEntityTargets() != null) { // && GOOPCManager.isEntitiesInSlash()

                for (AbstractEntity w : skillMetadata.getEntityTargets()) {
                    if (w == null) { continue; }
                    ArrayList<Collection<AbstractEntity>> x = pEffect.playParticleSlashEffect(skillMetadata, w.getLocation());
                    for (Collection<AbstractEntity> y : x) { for (AbstractEntity z : y) { v.put(z.getUniqueId(), z); } } }
            }
        }

        // That's the result
        return v.values();
    }
}
