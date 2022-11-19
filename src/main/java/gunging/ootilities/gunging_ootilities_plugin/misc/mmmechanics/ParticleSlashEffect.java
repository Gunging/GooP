package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.mechanics.ParticleEffect;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class ParticleSlashEffect extends ParticleEffect implements ITargetedEntitySkill, ITargetedLocationSkill {

    @NotNull final SlashLocations<Boolean> particleEffect;

        //NEWEN//public ParticleSlashEffect(SkillExecutor manager, File file, String skill, @NotNull MythicLineConfig mlc) {
        //NEWEN//super(manager, file, skill, mlc);
        /*OLDEN*/ public ParticleSlashEffect(SkillExecutor manager, String skill, @NotNull MythicLineConfig mlc) {
        /*OLDEN*/ super(manager, skill, mlc);
        GooPMythicMobs.newenOlden = true;

        particleEffect = new SlashLocations<>(mlc, (data, slashedLocation, funnies) -> {

            //noinspection unchecked
            this.playEffect(data, slashedLocation, (Collection<AbstractEntity>) funnies[0]);

            // Not needed, return statement
            return true;
        });
    }

    public HashSet<AbstractEntity> get(SkillMetadata data) { return new HashSet<>(MythicBukkit.inst().getEntityManager().getPlayers(data.getCaster().getEntity().getWorld())); }

    /**
     * Idk how audiences work and honestly its just a bunch of implementation problems.
     * This gets all players within 48 blocks of epicenter.
     *
     * @param target Target Location
     * @return Payers nearby that should be able to see the particle effect.
     */
    Collection<AbstractEntity> GetAudience(@NotNull AbstractLocation target) {

        // Literally just return all players within 48 blocks
        return new ArrayList<>(target.getWorld().getPlayersNearLocation(target, 48));
    }

    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
        Collection<AbstractEntity> audienceList = GetAudience(target);

        particleEffect.playParticleSlashEffect(data, target, audienceList);

        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a C\u00a77 Casting Location Particle Slash, Horizontal?\u00a7b" + horizontal);
        return SkillResult.SUCCESS;
    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Collection<AbstractEntity> audienceList = GetAudience(target.getLocation());

        particleEffect.playParticleSlashEffect(data, target.getLocation(), audienceList);

        //MM//OotilityCeption.Log("\u00a78PSlash\u00a7a C\u00a77 Casting Entity Particle Slash, Horizontal?\u00a7b" + horizontal);
        return SkillResult.SUCCESS;
    }
}
