package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class SudoOwnerMechanic extends SkillMechanic implements IMetaSkill {
    boolean casterAsTrigger;
    Boolean targetMinionsSelf;
    boolean targetMinionsAny;
    boolean targetArmorStands;
    PlaceholderString skillname;
    Skill metaskill;

    public SudoOwnerMechanic(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public SudoOwnerMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        casterAsTrigger = mlc.getBoolean(new String[]{"setcasterastrigger", "cat"}, false);
        String tmS = mlc.getString(new String[]{"targetownminions", "tom"}, "none");
        if (OotilityCeption.BoolTryParse(tmS)) { targetMinionsSelf = Boolean.parseBoolean(tmS); } else { targetMinionsSelf = null; }
        targetMinionsAny = mlc.getBoolean(new String[]{"targetminions", "tm"}, true);
        targetArmorStands = mlc.getBoolean(new String[]{"targetarmorstands", "ta"}, false);
        skillname = mlc.getPlaceholderString(new String[]{"skill", "s", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillname.get());

        // Attempt to fix meta skill
        if (metaskill == null) {
            //MM//OotilityCeption.Log("\u00a7c--->> \u00a7eMeta Skill Failure \u00a7c<<---");

            // Try again i guess?
            (new BukkitRunnable() {
                public void run() {

                    // Run Async
                    metaskill = GooPMythicMobs.GetSkill(skillname.get());

                }
            }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
        }
    }

    @Override
    public SkillResult cast(@NotNull SkillMetadata data) {
        Collection<AbstractEntity> targets = new HashSet<>();
        if (data.getEntityTargets() != null) { targets = new HashSet<>(data.getEntityTargets()); }

        // Get from placeholders :eyes1:
        if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillname.get(data, data.getCaster().getEntity()));}

        //MM//OotilityCeption.Log("\u00a73--- \u00a77Original Targets \u00a73---");
        //MM//for (AbstractEntity t : targets) { OotilityCeption.Log(" \u00a79> \u00a7f" + t.getName()); }
        //MM//OotilityCeption.Log("");

        //MM//OotilityCeption.Log("\u00a73 ? \u00a77Target Present?");
        if (targeter.isPresent()) {
            //MM//OotilityCeption.Log("\u00a7b  + \u00a77Yes");

            SkillTargeter configTarget = targeter.get();
            if (configTarget instanceof IEntitySelector) {
                //MM//OotilityCeption.Log("\u00a7b  + \u00a77Is Entity Selector");

                targets = ((IEntitySelector)configTarget).getEntities(data);
                //MM//OotilityCeption.Log("");
                //MM//OotilityCeption.Log("\u00a7b -- \u00a77Processed Sudo Targets \u00a7b--");
                //MM//for (AbstractEntity t : targets) { OotilityCeption.Log("   \u00a7b> \u00a7f" + t.getName()); }
                ((IEntitySelector)configTarget).filter(data, this.targetsCreativePlayers());
            }
        }

        // Get Owner
        Entity castr = data.getCaster().getEntity().getBukkitEntity();
        Entity owner = SummonerClassUtils.GetOwner(castr);
        if (owner == null) { owner = MinionEmancipation.OldOwner(castr.getUniqueId()); }
        AbstractEntity t = null; if (owner != null) { t = BukkitAdapter.adapt(owner); }

        // If targets existed
        if (targets != null && metaskill != null && t != null) {
            //MM//OotilityCeption.Log("\u00a73 >>> \u00a77Running for " + t.getName());

            // Filter targets
            HashSet<AbstractEntity> fTargets = new HashSet<>();
            for (AbstractEntity tar : targets) {
                //MM//OotilityCeption.Log("\u00a7d >> \u00a77 \u00a7f" + tar.getName() + " (" + tar.getBukkitEntity().getType().toString() + ")");

                // Include?
                boolean armorstandFailure = (tar.getBukkitEntity() instanceof ArmorStand) && !targetArmorStands;

                // Minions?
                SummonerClassMinion min = SummonerClassUtils.GetMinion(tar.getUniqueId());
                boolean minionFailure = (min != null) && !targetMinionsAny;

                // Self minions?
                boolean isComrade = false;
                boolean comradeFailure = false;
                if (min != null) {
                    isComrade = (min.getOwner().getUniqueId().equals(owner.getUniqueId()));
                    boolean tMinionSelf = false;
                    if (targetMinionsSelf != null) { tMinionSelf = targetMinionsSelf; }
                    comradeFailure = isComrade && !tMinionSelf; }

                // COmrade override
                boolean comradeOverride = OotilityCeption.If(targetMinionsSelf) && isComrade;
                //MM//OotilityCeption.Log("\u00a7c   >? \u00a77ArmorStand Faill: \u00a7f" + armorstandFailure);
                //MM//OotilityCeption.Log("\u00a73   >? \u00a77Minion Generic F: \u00a7f" + minionFailure);
                //MM//OotilityCeption.Log("\u00a7e   >? \u00a77Minion Comrade F: \u00a7f" + comradeFailure);
                //MM//OotilityCeption.Log("\u00a7a   >? \u00a77Comrade-Generc O: \u00a7f" + comradeOverride);

                // Will accept if it did not fail any
                if (!armorstandFailure && !comradeFailure && (!minionFailure || comradeOverride)) {

                    // Add it
                    fTargets.add(tar);
                }
            }
            targets = fTargets;
            //MM//for (AbstractEntity targ : targets) { OotilityCeption.Log("\u00a7a >\u00a7eT\u00a73T\u00a7cT\u00a77 " + targ.getName());}

            // Find caster
            SkillCaster caster;

            // Will be caster of the skill, as a mythicmob
            if (MythicBukkit.inst().getMobManager().isActiveMob(t)) {
                //MM//OotilityCeption.Log("\u00a73  * \u00a77Caster as ActiveMob");

                // Just pull the mythicmob
                caster = MythicBukkit.inst().getMobManager().getMythicMobInstance(t);

                // If its a player or some other non-mythicmob
            } else {
                //MM//OotilityCeption.Log("\u00a73  * \u00a77Caster as Non MM");

                // I guess make a new caster out of them
                caster = new GenericCaster(t);
            }

            // Copy data and replace caster
            final SkillMetadata clonedData = data.deepClone();
            clonedData.setCaster(caster);
            /*CURRENT-MMOITEMS*/clonedData.setEntityTargets(targets);
            //YE-OLDEN-MMO//clonedData.setEntityTargets(new HashSet<>(targets));

            // Replace trigger
            if (casterAsTrigger) {
                //MM//OotilityCeption.Log("\u00a7e  * \u00a77Trigger E As: " + castr.getCustomName());
                AbstractEntity trigr = BukkitAdapter.adapt(castr);
                //MM//OotilityCeption.Log("\u00a7e  * \u00a77Trigger A As: " + trigr.getName());

                // Replace
                clonedData.setTrigger(trigr);
                //MM//OotilityCeption.Log("\u00a7e  * \u00a77Trigger S As: " + clonedData.getTrigger().getName());
            }

            // ??
            if (!metaskill.isUsable(clonedData)) { return SkillResult.ERROR; }
            //MM//OotilityCeption.Log("\u00a7a  + \u00a77Useable");

            // Run skill sync or async
            if (forceSync) {
                //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Async");

                clonedData.setIsAsync(false);
                (new BukkitRunnable() {
                    public void run() {

                        // Run Async
                        clonedData.setIsAsync(false);
                        metaskill.execute(clonedData);

                    }
                }).runTask(MythicBukkit.inst());

                // Forncing Sync
            } else {
                //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Sync");

                // Run Sync
                metaskill.execute(clonedData);
            }

            // Success I guess
            return SkillResult.SUCCESS;

        // Targets were null :B
        } else {

            // Log Failurii
            //MM//OotilityCeption.Log("\u00a7c ? \u00a77Nonull Targets: \u00a7f" + (targets != null));
            //MM//OotilityCeption.Log("\u00a73 ? \u00a77Nonull Casterr: \u00a7f" + (castr != null));
            //MM//OotilityCeption.Log("\u00a7e ? \u00a77Nonull Metaskl: \u00a7f" + (metaskill != null));

            // L
            return SkillResult.ERROR;
        }
    }
}