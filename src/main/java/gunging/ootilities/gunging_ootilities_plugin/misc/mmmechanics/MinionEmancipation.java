package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.util.annotations.MythicMechanic;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@MythicMechanic(
        author = "gunging",
        name = "GooPReleaseMinion",
        description = "The target will no longer be a minion of no one"
)
public class MinionEmancipation extends SkillMechanic implements ITargetedEntitySkill {

    // Rememberances
    static HashMap<UUID, Entity> minionToOldOwner = new HashMap<>();
    @Nullable public static Entity OldOwner(@NotNull UUID minion) { return minionToOldOwner.get(minion); }

    public MinionEmancipation(CustomMechanic skill, MythicLineConfig mlc) {
        super(skill.getConfigLine(), mlc);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {

        // Valid?
        SummonerClassMinion min = SummonerClassUtils.GetMinion(targetProbably.getUniqueId());

        // Found?
        if (min != null) {

            // Remember...
            minionToOldOwner.put(min.getMinion().getUniqueId(), min.getOwner());

            // Release
            SummonerClassUtils.DisableMinion(min);
        }

        return false;
    }
}
