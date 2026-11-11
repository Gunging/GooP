package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.misc.ObjectiveLinks;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class LinkedEntity {
    @NotNull Entity linkedEntity;
    @NotNull UUID entityUUID;
    @NotNull ObjectiveLinks reason;
    @Nullable GooPUnlockables end;

    public LinkedEntity(@NotNull Entity target, @NotNull ObjectiveLinks reasoning, @Nullable GooPUnlockables end) {
        linkedEntity = target;
        reason = reasoning;
        entityUUID = target.getUniqueId(); }

    @NotNull public UUID getEntityUUID() { return  entityUUID; }
    @NotNull public UUID GetEntityUUID() { return  getEntityUUID(); }

    @NotNull public Entity getEntity() { return linkedEntity; }
    @NotNull public Entity GetEntity() { return getEntity(); }

    @NotNull public ObjectiveLinks getReason() { return reason; }
    @NotNull public ObjectiveLinks GetReason() { return getReason(); }

    @Nullable public GooPUnlockables getEnd() { return end; }
    @Nullable public GooPUnlockables GetEnd() { return getEnd(); }

    public boolean hasEnded() {

        // Does it have a temporal end?
        if (getEnd() != null) {

            // Check Timer
            getEnd().CheckTimer();

            // If is locked, it has ended
            return !getEnd().IsUnlocked();
        }

        // No
        return false;
    }
}
