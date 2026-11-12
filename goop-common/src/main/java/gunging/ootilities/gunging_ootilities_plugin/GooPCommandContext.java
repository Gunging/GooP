package gunging.ootilities.gunging_ootilities_plugin;

import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import gunging.ootilities.gunging_ootilities_plugin.misc.SuccessibleChain;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Everything {@link GungingOotilities#onCommand} has prepared by the time it dispatches to a
 * subcommand: the parsed arguments, the sender's relative location, the chain/fail-message
 * state and the shared feedback list. Each {@code GooPCmd*} class receives one of these.
 */
final class GooPCommandContext {

    @NotNull final CommandSender sender;
    final Command command;
    @NotNull final String label;
    @NotNull final String[] args;
    final boolean senderIsPlayer;
    @Nullable final Location senderLocation;
    @Nullable final String failMessage;
    @Nullable final UUID successibilityFlare;
    /** Feedback lines, sent to the sender after the subcommand ran */
    @NotNull final List<String> logReturn;
    final boolean supp;
    @NotNull final GooP_Commands cmd;
    @NotNull final StringBuilder successSlots;
    final boolean permission;
    @NotNull final ArrayList<String> trueArgs;
    final boolean chained;
    @NotNull final StringBuilder chanedArgs;
    @NotNull final SuccessibleChain commandChain;
    @Nullable final String chainedNoLocation;
    @NotNull final RefSimulator<List<String>> logReturnUrn;
    @Nullable final String senderUUIDIG;
    @NotNull final OotilityCeption oots;
    @NotNull final GungingOotilities self;

    GooPCommandContext(@NotNull CommandSender sender, Command command, @NotNull String label, @NotNull String[] args,
                       boolean senderIsPlayer, @Nullable Location senderLocation, @Nullable String failMessage,
                       @Nullable UUID successibilityFlare, @NotNull List<String> logReturn, boolean supp,
                       @NotNull GooP_Commands cmd, @NotNull StringBuilder successSlots, boolean permission,
                       @NotNull ArrayList<String> trueArgs, boolean chained, @NotNull StringBuilder chanedArgs,
                       @NotNull SuccessibleChain commandChain, @Nullable String chainedNoLocation,
                       @NotNull RefSimulator<List<String>> logReturnUrn, @Nullable String senderUUIDIG,
                       @NotNull OotilityCeption oots, @NotNull GungingOotilities self) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
        this.senderIsPlayer = senderIsPlayer;
        this.senderLocation = senderLocation;
        this.failMessage = failMessage;
        this.successibilityFlare = successibilityFlare;
        this.logReturn = logReturn;
        this.supp = supp;
        this.cmd = cmd;
        this.successSlots = successSlots;
        this.permission = permission;
        this.trueArgs = trueArgs;
        this.chained = chained;
        this.chanedArgs = chanedArgs;
        this.commandChain = commandChain;
        this.chainedNoLocation = chainedNoLocation;
        this.logReturnUrn = logReturnUrn;
        this.senderUUIDIG = senderUUIDIG;
        this.oots = oots;
        this.self = self;
    }
}
