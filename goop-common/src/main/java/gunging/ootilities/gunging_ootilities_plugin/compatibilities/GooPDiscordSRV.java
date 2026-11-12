package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePostProcessEvent;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.GooP_FontUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GooPDiscordSRV {

    public static GooPDiscordSRV theGDSRV = null;

    public GooPDiscordSRV() { }

    public void CompatibilityCheck() {

        // If non null
        if (theGDSRV != null) { return; }

        // Subscribe itself
        DiscordSRV.api.subscribe(this);

        // Store
        theGDSRV = this;
    }

    public void SendMessage(Player asAuth, String msg) { SendMessage(asAuth, msg, "global"); }
    public void SendMessage(Player asAuth, String msg, String channel) {

        // Send Message lma0
        Bukkit.getScheduler().runTaskAsynchronously(DiscordSRV.getPlugin(), () -> {
            DiscordSRV.getPlugin().processChatMessage(asAuth, msg, DiscordSRV.getPlugin().getOptionalChannel(channel), false);
        });
    }

    @Subscribe
    public void OnDiscordChatSent(GameChatMessagePostProcessEvent messageSent) {

        // Unparse Codes and Set
        messageSent.setProcessedMessage(GooP_FontUtils.UnparseFontLinks(messageSent.getProcessedMessage()));
    }

    @Subscribe
    public void OnDiscordChatReceive(DiscordGuildMessagePostProcessEvent messageSent) {

        // Unparse Codes and Set
        messageSent.setProcessedMessage(GooP_FontUtils.ParseFontLinks(messageSent.getProcessedMessage()));
    }
}
