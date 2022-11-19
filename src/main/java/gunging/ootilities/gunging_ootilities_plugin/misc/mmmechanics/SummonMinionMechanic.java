package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.GTL_SummonerClass;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation.GTranslationManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;

public class SummonMinionMechanic extends SkillMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
    String mmName;
    PlaceholderDouble leashRange;
    PlaceholderDouble kindLimit;
    PlaceholderString kindOverride;
    String mmSkill;
    PlaceholderDouble amount;
    PlaceholderDouble weight;
    PlaceholderDouble level;
    boolean capBreak;
    boolean pvpBlock;


    //NEWEN//public SummonMinionMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
        //NEWEN//super(manager, file, skill, mlc);
        /*OLDEN*/public SummonMinionMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        /*OLDEN*/super(manager, skill, mlc);
        GooPMythicMobs.newenOlden = true;

        mmName = mlc.getString(new String[] { "mob", "m", "type", "t" });
        leashRange = mlc.getPlaceholderDouble(new String[] { "leashrange", "lr" }, 20.0);
        amount = mlc.getPlaceholderDouble(new String[] { "amount", "a", "count", "c" }, 1D);
        weight = mlc.getPlaceholderDouble(new String[] { "weight", "w", "minionweight", "mw" }, 1D);
        kindOverride = mlc.getPlaceholderString(new String[] { "kind", "k" }, mmName);
        kindLimit = mlc.getPlaceholderDouble(new String[] { "kindLimit", "kl" }, 32767D);
        mmSkill = mlc.getString(new String[] { "skill", "s" });
        capBreak = mlc.getBoolean(new String[] { "capbreak", "cb" }, true);
        pvpBlock = mlc.getBoolean(new String[] { "pvpblock", "pvpb", "preventPlayerDamage", "ppd" }, false);
        this.level = mlc.getPlaceholderDouble(new String[]{"level", "l"}, -1.0D);

        // Must be called in Sync
        setAsyncSafe(false);
        //try { ASYNC_SAFE = false; } catch (NoSuchFieldError ignored) { setAsyncSafe(false); }
    }

    void AsMinionSpawn(SkillMetadata skillMetadata, Entity summonner, Location loc) {

        //MM//OotilityCeption. Log("Found Summoner:\u00a73 " + summonner.getName());

        // Cancel if player will exceed cap
        double current = SummonerClassUtils.GetCurrentMinionCount(summonner.getUniqueId());
        double max = SummonerClassUtils.GetMaxMinions(summonner);
        double effectiveLevel = level.get(skillMetadata, skillMetadata.getCaster().getEntity());
        if (effectiveLevel == -1) { effectiveLevel = skillMetadata.getCaster().getLevel(); }
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
        //MM//OotilityCeption. Log("\u00a7a# \u00a77Level:\u00a7f " + effectiveLevel);

        // For each amount
        for (int i = 1; i <= effectiveAmount; i++) {

            // Cap exceed
            if (!capBreak) {
                if ((current + effectiveWeight) >= max) {

                    // Message
                    if (effectiveWeight > 0 && (summonner instanceof Player)) {

                        // Message
                        String message = GTranslationManager.getSummonerTranslation(GTL_SummonerClass.MINION_WOULD_EXCEED_CAP);

                        // Set to space to skip
                        if (message.length() > 0 && !message.equals(" ")) {

                            // Get Manager
                            MobExecutor mm = MythicBukkit.inst().getMobManager();
                            Optional<MythicMob> mob = mm.getMythicMob(mmName);

                            String mmName = mob.isPresent() ? (mob.get().getDisplayName() == null ? mob.get().getEntityType() : mob.get().getDisplayName().get(skillMetadata, skillMetadata.getCaster().getEntity())) : "\u00a74<invalid-mythicmob>";
                            message = message.replace("%minion_name%", mmName);

                            message = OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(message, summonner, (Player) summonner, null, null));
                            ((Player) summonner).sendMessage(message); }
                    }

                    //MM//OotilityCeption. Log("\u00a7c >>\u00a77 Cap Broken");
                    return;

                } else {
                    //MM//OotilityCeption. Log("\u00a7a >>\u00a77 Cap Succeed");
                }
            }

            //MM//OotilityCeption. Log("\u00a73 >\u00a77 " + i);

            // Spawn
            Entity target = GooPMythicMobs.SpawnMythicMob(mmName, loc, effectiveLevel);

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
                    newMinion.setPreventPlayerDamage(pvpBlock);

                    // Enable
                    newMinion.Enable();
                    current += effectiveWeight;
                    //MM//OotilityCeption. Log("\u00a7a# \u00a77Current Cap:\u00a7f " + current + "\u00a78 (+" + effectiveWeight + ")");
                }
            }
        }
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {
        // Gather all necessary values - caster and target
        LivingEntity mob = (LivingEntity) BukkitAdapter.adapt(targetProbably);
        LivingEntity caster = (LivingEntity) BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        if (mob == null) { return SkillResult.ERROR; }

        AsMinionSpawn(skillMetadata, caster, mob.getLocation());

        return SkillResult.SUCCESS;
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation targetProbably) {
        // Gather all necessary values - caster and target
        Location mobLocation = BukkitAdapter.adapt(targetProbably);
        LivingEntity caster = (LivingEntity) BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        AsMinionSpawn(skillMetadata, caster, mobLocation);

        return SkillResult.SUCCESS;
    }
}
