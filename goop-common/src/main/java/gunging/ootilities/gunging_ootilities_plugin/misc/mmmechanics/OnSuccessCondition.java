package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.SuccessibleFlareReceptor;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OnSuccessCondition extends CustomMMCondition implements ISkillMetaCondition, SuccessibleFlareReceptor {

    boolean flared = false;

    @NotNull PlaceholderString command;

    public OnSuccessCondition(@NotNull MythicLineConfig mlc) {
        super(mlc);

        command = mlc.getPlaceholderString(new String[]{"command", "cmd", "c"}, "goop tell <target.name> &3Consider specifying a command in the &e- GooPOnSuccess{c=###}&3 condition. ");
        //SUC//OotilityCeption.Log("\u00a78CMD\u00a7f OSC\u00a77 Loaded OS{}\u00a73 " + command.toString());
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {
        flared = false;

        // Parse command for this check
        String parsedCommand = command.get(skillMetadata);

        Entity asCaster = skillMetadata.getCaster().getEntity().getBukkitEntity();
        Player asPlayer = (asCaster instanceof Player) ? (Player) asCaster : null;

        // Prepare successible and send, I guess
        UUID successibleID = UUID.randomUUID();

        //SUC//OotilityCeption.Log("\u00a78CMD\u00a7f OSC\u00a77 Sending command\u00a77e " + parsedCommand + "\u00a77 under flare\u00a7e " + successibleID.toString());
        String flaredCommand = OotilityCeption.ProcessGooPSuccessibilityFlare(parsedCommand, successibleID, this);
        //SUC//OotilityCeption.Log("\u00a78CMD\u00a7f OSC\u00a72 ->\u00a7f " + flaredCommand);
        OotilityCeption.SendConsoleCommand(flaredCommand, asCaster, asPlayer, asCaster.getLocation().getBlock(), null);
        //SUC//OotilityCeption.Log("\u00a78CMD\u00a7f OSC\u00a77 Finished stack");

        return neg(flared);
    }

    @Override
    public void received() { flared = true; }
}
