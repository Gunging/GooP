package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageTransferLink extends EntityLinkedEntity {

    double prevent;
    double transfer;
    boolean silent;

    public DamageTransferLink(@NotNull Entity target, @NotNull ObjectiveLinks reasoning, @Nullable GooPUnlockables end, @NotNull Entity receiver, @Nullable GooPUnlockables receiverValidity, double preventionMultiplier, double transferFraction, boolean silent) {
        super(target, reasoning, end, receiver, receiverValidity);
        prevent = preventionMultiplier;
        transfer = transferFraction;
        this.silent = silent;

        //DMG//OotilityCeption.Log("\u00a73DTL\u00a77 Source: \u00a7b" + target.getName() + " \u00a78(\u00a7b" + target.getUniqueId() + "\u00a78)");
        //DMG//OotilityCeption.Log("\u00a73DTL\u00a77 End: \u00a7b" + end);
        //DMG//OotilityCeption.Log("\u00a73DTL\u00a77 Receiver: \u00a7b" + receiver.getName() + " \u00a78(\u00a7b" + receiver.getUniqueId() + "\u00a78)");
        //DMG//OotilityCeption.Log("\u00a73DTL\u00a77 RValidity: \u00a7b" + receiverValidity);
        //DMG//OotilityCeption.Log("\u00a73DTL\u00a77 Transfer: \u00a7b" + transferFraction + "\u00a77, Prevent: \u00a7b" + preventionMultiplier);
    }

    /**
     * @return Fraction of the damage that is removed from the damage the source would take
     */
    public double getPrevent() { return prevent; }
    /**
     * @return Fraction of the damage that is sent to the receiver
     */
    public double getTransfer() { return transfer; }

    /**
     * @return Should this damage transfer fire EntityDamageByEntity events?
     */
    public boolean isSilent() { return silent; }
}
