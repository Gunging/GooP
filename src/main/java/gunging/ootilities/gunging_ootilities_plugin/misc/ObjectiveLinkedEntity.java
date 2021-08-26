package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObjectiveLinkedEntity extends LinkedEntity {
    @NotNull Objective objectiveTarget;
    @NotNull Boolean playersOnly;

    public ObjectiveLinkedEntity(@NotNull Entity target, @NotNull ObjectiveLinks reasoning, @Nullable GooPUnlockables end, @NotNull Objective scoreboard, @NotNull Boolean ignoreNonPlayers) {
        super(target, reasoning, end);
        objectiveTarget = scoreboard;
        playersOnly = ignoreNonPlayers;
    }

    public void ApplyLink(@NotNull Entity trigger, int value) {

        if (trigger instanceof Player || !playersOnly) {

            // Well just set the score alv I guess
            Score tScore = objectiveTarget.getScore(trigger.getName());
            tScore.setScore(tScore.getScore() + value);
        }
    }
}
