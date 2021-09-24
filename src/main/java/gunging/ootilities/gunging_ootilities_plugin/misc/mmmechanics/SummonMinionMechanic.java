package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.util.annotations.MythicMechanic;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
        author = "gunging",
        name = "GooPSummonMinion",
        description = "Summons a new mythicmob as a minion of the caster."
)
public class SummonMinionMechanic extends SkillMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
    String mmName;
    PlaceholderDouble leashRange;
    PlaceholderDouble kindLimit;
    PlaceholderString kindOverride;
    String mmSkill;
    PlaceholderDouble amount;
    PlaceholderDouble weight;
    boolean capBreak;


    public SummonMinionMechanic(CustomMechanic skill, MythicLineConfig mlc) {
        super(skill.getConfigLine(), mlc);
        mmName = mlc.getString(new String[] { "mob", "m", "type", "t" });
        leashRange = mlc.getPlaceholderDouble(new String[] { "leashrange", "lr" }, 20.0);
        amount = mlc.getPlaceholderDouble(new String[] { "amount", "a", "count", "c" }, 1D);
        weight = mlc.getPlaceholderDouble(new String[] { "weight", "w", "minionweight", "mw" }, 1D);
        kindOverride = mlc.getPlaceholderString(new String[] { "kind", "k" }, mmName);
        kindLimit = mlc.getPlaceholderDouble(new String[] { "kindLimit", "kl" }, 32767D);
        mmSkill = mlc.getString(new String[] { "skill", "s" });
        capBreak = mlc.getBoolean(new String[] { "capbreak", "cb" }, true);

        // Must be called in Sync
        setAsyncSafe(false);
        //try { ASYNC_SAFE = false; } catch (NoSuchFieldError ignored) { setAsyncSafe(false); }
    }

    void AsMinionSpawn(SkillMetadata skillMetadata, Entity summonner, Location loc) {

        //MM//OotilityCeption. Log("Found Summoner:\u00a73 " + summonner.getName());

        // Cancel if player will exceed cap
        double current = SummonerClassUtils.GetCurrentMinionCount(summonner.getUniqueId());
        double max = SummonerClassUtils.GetMaxMinions(summonner);
        double effectiveWeight = weight.get(skillMetadata, skillMetadata.getCaster().getEntity());
        double effectiveAmount = amount.get(skillMetadata, skillMetadata.getCaster().getEntity());
        double effectiveRange = leashRange.get(skillMetadata, skillMetadata.getCaster().getEntity());
        double effectiveKLimit = kindLimit.get(skillMetadata, skillMetadata.getCaster().getEntity());
        String effectiveKind = kindOverride.get(skillMetadata, skillMetadata.getCaster().getEntity());
        //MM//OotilityCeption. Log("\u00a7a# \u00a77Max Cap:\u00a7f " + max);
        //MM//OotilityCeption. Log("\u00a7a# \u00a77Current Cap:\u00a7f " + current);
        //MM//OotilityCeption. Log("\u00a7a# \u00a77Weigh Plus:\u00a7f " + effectiveWeight);
        //MM//OotilityCeption. Log("\u00a7a# \u00a77Amount:\u00a7f " + effectiveAmount);
        //MM//OotilityCeption. Log("\u00a7a# \u00a77Range:\u00a7f " + effectiveRange);
        //MM//OotilityCeption. Log("\u00a7a# \u00a77Kind:\u00a7f " + effectiveKind);
        //MM//OotilityCeption. Log("\u00a7a# \u00a77KindLim:\u00a7f " + effectiveKLimit);

        // For each amount
        for (int i = 1; i <= effectiveAmount; i++) {

            // Cap exceed
            if (!capBreak) {
                if ((current + effectiveWeight) >= max) {
                    //MM//OotilityCeption. Log("\u00a7c >>\u00a77 Cap Broken");
                    return;

                } else {
                    //MM//OotilityCeption. Log("\u00a7a >>\u00a77 Cap Succeed");
                }
            }

            //MM//OotilityCeption. Log("\u00a73 >\u00a77 " + i);

            // Spawn
            Entity target = GooPMythicMobs.SpawnMythicMob(mmName, loc);

            if (target != null) {
                //MM//OotilityCeption. Log("\u00a7a >\u00a77 Spawn Success");

                // Is it minion?
                if (!SummonerClassMinion.isMinion(target)) {
                    //MM//OotilityCeption. Log("\u00a7a >\u00a77 Not Minion");

                    // Create entity
                    SummonerClassMinion newMinion = new SummonerClassMinion(summonner, target);

                    // Set the options
                    newMinion.setLeashRange(effectiveRange);
                    newMinion.setSkillOnRemove(mmSkill);
                    newMinion.setWeight(effectiveWeight);
                    newMinion.setKind(effectiveKind);
                    newMinion.setLimit(OotilityCeption.RoundToInt(effectiveKLimit));

                    // Enable
                    newMinion.Enable();
                    current += effectiveWeight;
                    //MM//OotilityCeption. Log("\u00a7a# \u00a77Current Cap:\u00a7f " + current + "\u00a78 (+" + effectiveWeight + ")");
                }
            }
        }
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {
        // Gather all necessary values - caster and target
        LivingEntity mob = (LivingEntity) BukkitAdapter.adapt(targetProbably);
        LivingEntity caster = (LivingEntity) BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        if (mob == null) { return false; }

        AsMinionSpawn(skillMetadata, caster, mob.getLocation());

        return false;
    }

    @Override
    public boolean castAtLocation(SkillMetadata skillMetadata, AbstractLocation targetProbably) {
        // Gather all necessary values - caster and target
        Location mobLocation = BukkitAdapter.adapt(targetProbably);
        LivingEntity caster = (LivingEntity) BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        AsMinionSpawn(skillMetadata, caster, mobLocation);

        return false;
    }
}
