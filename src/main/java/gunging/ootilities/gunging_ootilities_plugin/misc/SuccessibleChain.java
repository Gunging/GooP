package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.SuccessibleFlareReceptor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * All the per-command information for when a command succeeds.
 */
public class SuccessibleChain {

    @NotNull String chainedCommand;

    @NotNull public String getChainedCommand() { return chainedCommand; }
    public void setChainedCommand(@NotNull String command) { chainedCommand = command; }

    @Nullable UUID successibleFlare;

    @Nullable public UUID getSuccessibleFlare() { return successibleFlare; }
    public void setSuccessibleFlare(@Nullable UUID flare) { successibleFlare = flare; }

    public SuccessibleChain(@Nullable String command, @Nullable UUID flare) {
        chainedCommand = (command == null) ? "" : command;
        successibleFlare = flare;
    }

    public void flare() {
        if (getSuccessibleFlare() == null) {
            //SUC//OotilityCeption.Log("\u00a78SUC\u00a7c FLR\u00a77 Null flare for \u00a7c " + chainedCommand);
            return; }

        //SUC//OotilityCeption.Log("\u00a78SUC\u00a7c FLR\u00a77 Successible flare sent\u00a7e " + getSuccessibleFlare().toString());
        SuccessibleFlareReceptor rec = OotilityCeption.successFlareReceptors.get(getSuccessibleFlare());
        if (rec == null) { return; }

        //SUC//OotilityCeption.Log("\u00a78SUC\u00a7c FLR\u00a77 Successible flare delivered\u00a7e " + rec.getClass().getName());
        OotilityCeption.successFlareReceptors.remove(getSuccessibleFlare());
        rec.received();
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable Player asChainResult, @Nullable CommandSender konsole) {
        chain(chained, asChainResult, konsole, null, null, null, null);
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable Player asChainResult, @Nullable CommandSender konsole, @Nullable Entity asEntity) {
        chain(chained, asChainResult, konsole, asEntity, null, null, null);
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable Player asChainResult, @Nullable CommandSender konsole, @Nullable Entity asEntity, @Nullable Player asPlayer) {
        chain(chained, asChainResult, konsole, asEntity, asPlayer, null, null);
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable Player asChainResult, @Nullable CommandSender konsole, @Nullable Entity asEntity, @Nullable Player asPlayer, @Nullable Block asBlock, @Nullable ItemStack asItem) {

        //SUC//OotilityCeption.Log("\u00a78SUC\u00a7c FLR\u00a77 Chaining \u00a79 " + chainedCommand);
        if (chained && !chainedCommand.isEmpty()) {
            String flaredChained = OotilityCeption.ProcessGooPSuccessibilityFlare(chainedCommand, getSuccessibleFlare(), null);
            OotilityCeption.SendAndParseConsoleCommand(asChainResult, flaredChained, konsole, asEntity, asPlayer, asBlock, asItem);}
        if (!chained) { flare(); }
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable UUID asChainResult, @Nullable CommandSender konsole) {
        chain(chained, asChainResult, konsole, null, null, null, null);
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable UUID asChainResult, @Nullable CommandSender konsole, @Nullable Entity asEntity) {
        chain(chained, asChainResult, konsole, asEntity, null, null, null);
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable UUID asChainResult, @Nullable CommandSender konsole, @Nullable Entity asEntity, @Nullable Player asPlayer) {
        chain(chained, asChainResult, konsole, asEntity, asPlayer, null, null);
    }

    /**
     * Sends the next command of the chain, or fires the successible flare if it's the last one
     */
    public void chain(boolean chained, @Nullable UUID asChainResult, @Nullable CommandSender konsole, @Nullable Entity asEntity, @Nullable Player asPlayer, @Nullable Block asBlock, @Nullable ItemStack asItem) {

        //SUC//OotilityCeption.Log("\u00a78SUC\u00a7c FLR\u00a77 Chaining \u00a73 " + chainedCommand);
        if (chained && !chainedCommand.isEmpty()) {
            String flaredChained = OotilityCeption.ProcessGooPSuccessibilityFlare(chainedCommand, getSuccessibleFlare(), null);
            OotilityCeption.SendAndParseConsoleCommand(asChainResult, flaredChained, konsole, asEntity, asPlayer, asBlock, asItem); }
        if (!chained) { flare(); }
    }
}
