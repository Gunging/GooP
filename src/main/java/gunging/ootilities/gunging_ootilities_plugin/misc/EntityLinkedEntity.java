package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EntityLinkedEntity extends LinkedEntity {


    public static final String transferLinkReceiver = "TLR_";
    public static final String transferLinkParticipant = "TLP_";

    @NotNull UUID receiverUUID;
    @NotNull Entity receiver;
    @Nullable GooPUnlockables receiverValidity;

    public EntityLinkedEntity(@NotNull Entity target, @NotNull ObjectiveLinks reasoning, @Nullable GooPUnlockables end, @NotNull Entity receiver, @Nullable GooPUnlockables receiverValidity) {
        super(target, reasoning, end);
        this.receiver = receiver;
        receiverUUID = receiver.getUniqueId();
        this.receiverValidity = receiverValidity;
    }

    @NotNull public UUID getReceiverUUID() { return receiverUUID; }
    @NotNull public Entity getReceiver() { return receiver; }

    /**
     * @return If the receiver is linked to an Unlockable, if ths
     *         becomes locked, the receiver is said to be invalid.
     */
    public boolean isReceiverValid() {

        if (getReceiverValidity() != null) {
            getReceiverValidity().CheckTimer();
            /*DMG*/OotilityCeption.Log("Validity? \u00a7b- \u00a7e" + getReceiverValidity().IsUnlocked());
            return getReceiverValidity().IsUnlocked();
        }

        // Always valid i guess
        /*DMG*/OotilityCeption.Log("No Unlockable \u00a7b- \u00a7aValid");
        return true;
    }

    @Nullable GooPUnlockables getReceiverValidity() { return receiverValidity; }
}
