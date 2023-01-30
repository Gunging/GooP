package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.misc.SummonerClassMinion;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class MinionEmancipation extends SkillMechanic implements ITargetedEntitySkill {

    // Rememberances
    static HashMap<UUID, Entity> minionToOldOwner = new HashMap<>();
    @Nullable public static Entity OldOwner(@NotNull UUID minion) { return minionToOldOwner.get(minion); }

    public MinionEmancipation(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public MinionEmancipation(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        setAsyncSafe(false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity targetProbably) {

        // Valid?
        SummonerClassMinion min = SummonerClassUtils.GetMinion(targetProbably.getUniqueId());

        // Found?
        if (min != null) {

            // Remember...
            minionToOldOwner.put(min.getMinion().getUniqueId(), min.getOwner());

            // Release
            SummonerClassUtils.DisableMinion(min);
        }

        return SkillResult.SUCCESS;
    }
}
