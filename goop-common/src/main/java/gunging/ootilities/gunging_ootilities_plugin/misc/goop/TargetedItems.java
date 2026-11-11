package gunging.ootilities.gunging_ootilities_plugin.misc.goop;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.SuccessibleChain;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackLocation;
import gunging.ootilities.gunging_ootilities_plugin.misc.goop.slot.ItemStackSlot;
import gunging.ootilities.gunging_ootilities_plugin.misc.PlusMinusPercent;
import gunging.ootilities.gunging_ootilities_plugin.misc.RefSimulator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * In many commands, items are edited, when a player and slots are specified, or perhaps a dropped item.
 *
 * Allow to easy loop through them all and run the same method.
 *
 * Also store information on successes and failures.
 */
public class TargetedItems {

    /**
     * Initialize one of these things. Not much to see.
     *
     * @param allowAir If air should ever be considered to process.
     *
     * @param applyChanges If the process makes changes to the item that
     *                     should be applied.
     *
     * @param chained If there is a command chain to be ran.
     *
     * @param condition Condition that will cause the command to succeed if any slot
     *                  satisfies it.
     */
    public TargetedItems(boolean allowAir, boolean applyChanges, boolean chained, @Nullable SuccessibleChain chainedCommand,
                         @NotNull CommandSender sender, @Nullable String failMessage,
                         @NotNull TargetedItemAction process,
                         @NotNull TargetedItemSuccessCondition condition,
                         @Nullable TargetedItemScoreHandle scoreHandle
                         ) {
        this.allowAir = allowAir;
        this.process = process;
        this.applyChanges = applyChanges;
        this.chained = chained;
        this.chainedCommand = chainedCommand;
        this.condition = condition;
        this.sender = sender;
        this.failMessage = failMessage;
        this.scoreHandle = scoreHandle;
    }

    /**
     * @return If no slots are targeted.
     */
    public boolean isEmpty() {

        // Count all entries
        int count = registeredDroppedItems.size();
        for (Player p : registeredPlayers.keySet()) { count += registeredPlayers.get(p).size(); }
        return count == 0;
    }

    /**
     * This command is not meant to succeed. It will not say anything negative at the end.
     */
    @NotNull public TargetedItems notSuccessible() { successible = false; return this; }

    boolean allowAir, applyChanges, chained, successible = true;
    @NotNull SuccessibleChain chainedCommand;
    @NotNull CommandSender sender;
    @NotNull TargetedItemSuccessCondition condition;
    @NotNull TargetedItemAction process;
    @Nullable TargetedItemScoreHandle scoreHandle;
    @Nullable String failMessage;
    /**
     * Run the Process Method through all items.
     */
    public void process() {
        if (isEmpty()) { return; }

        HashMap<Entity, SuccessibleInformation> successStuff = new HashMap<>();

        // Which items shall be processed
        ArrayList<TargetedItem> iSources = new ArrayList<>();

        // For every item, guaranteed to not be null
        for (Item item : registeredDroppedItems) { iSources.add(new TargetedItem(item)); }

        // For every player, all of them also guaranteed to not be null
        for (Player player : registeredPlayers.keySet()) {
            ArrayList<ItemStackSlot> slots = registeredPlayers.get(player);
            for (ItemStackSlot slot : slots) {
                ItemStackLocation loc = OotilityCeption.getItemFromPlayer(player, slot);
                if (loc == null) { continue; }
                iSources.add(new TargetedItem(player, slot, loc)); } }

        // Nothing null lets goo
        for (TargetedItem iSource : iSources) {

            // Counter air
            if (!allowAir && iSource.isAir()) { continue; }

            // Original Amount
            int originalAmount = 1;
            if (!OotilityCeption.IsAirNullAllowed(iSource.getOriginal())) { originalAmount = iSource.getValidOriginal().getAmount(); }

            // Process it lets go
            iSource.result = process.Process(iSource);

            // Set amount
            if (!OotilityCeption.IsAirNullAllowed(iSource.getResult())) { iSource.getResult().setAmount(originalAmount); }

            // Success?
            if (condition.isSuccess(iSource)) {
                //TRG//OotilityCeption.Log("\u00a78TGI\u00a7a SCS\u00a77 Succeeded for\u00a7f " + OotilityCeption.GetItemName(iSource.getOriginal()));

                // Obtain entity
                SuccessibleInformation sInfo = successStuff.get(iSource.getEntity());
                if (sInfo == null) { sInfo = new SuccessibleInformation(); }

                // Success Stuff
                successCount++;
                succeeded = true;

                /*
                 * What changes must be made to whom score?
                 */
                if (scoreHandle != null) { scoreHandle.handleScores(iSource, sInfo); }

                /*
                 * Apply changes to the item, wherever it is
                 */
                if (applyChanges) {
                    //TRG//OotilityCeption.Log("\u00a78TGI\u00a7a SCS\u00a7a Applying Changes");
                    iSource.ApplyChanges();
                }
                //TRG// else { OotilityCeption.Log("\u00a78TGI\u00a7a SCS\u00a7c Not Applying Changes"); }

                /*
                 * Build the slots of success that succeed from this command.
                 */
                if (chained && iSource.type == TargetedItemType.PLAYER) {
                    //TRG//OotilityCeption.Log("\u00a78TGI\u00a7a SCS\u00a77 Building slots of success\u00a7f " + iSource.player_slot.toString());

                    OotilityCeption.Slot4Success(sInfo.getSlots4Success(), iSource.player_slot, OotilityCeption.comma); }

                // Update
                successStuff.put(iSource.getEntity(), sInfo);
            }

            // Log
            if (iSource.getLogAddition().getValue() != null && !iSource.getLogAddition().getValue().equals("null")) { includedStrBuilder.append(iSource.getLogAddition().GetValue()); }
        }

        // Which players failed?
        ArrayList<Player> totalPlayers = new ArrayList<>(registeredPlayers.keySet());

        // Success every entity
        if (successible) {

            // Carry on success
            for (Entity ent : successStuff.keySet()) {
                if (ent == null) { continue; }

                boolean asPlayer = ent instanceof Player;

                // This player did not fail
                if (asPlayer) { totalPlayers.remove(ent); }

                // Get that success thing
                SuccessibleInformation sInfo = successStuff.get(ent);

                /*
                 * Its important to perform score change BEFORE
                 * chaining the commands so the next commands
                 * can READ the result of this command.
                 */
                for (Objective objective : sInfo.getScoreboardOpps().keySet()) {
                    if (objective == null) { continue; }
                    ArrayList<PlusMinusPercent> pmpList = sInfo.getScoreboardOpps().get(objective);

                    // Execute all operations
                    for (PlusMinusPercent pmp : pmpList) {
                        if (pmp == null) { continue; }

                        // As a player
                        if (asPlayer) {

                            // Run score
                            OotilityCeption.SetPlayerScore(objective, (Player) ent, pmp);

                            // Not a player
                        } else {

                            // Run score
                            OotilityCeption.SetEntryScore(objective, ent.getUniqueId().toString(), pmp);
                        }
                    }

                    if (Gunging_Ootilities_Plugin.sendGooPSuccessFeedback) {

                        if (asPlayer) {

                            // Mention
                            includedStrBuilder.append("Set player\u00a73 ").append(ent.getName()).append("\u00a77's score \u00a7e").append(objective.getName()).append("\u00a77 to \u00a7b").append(OotilityCeption.GetPlayerScore(objective, (Player) ent)).append("\u00a77. ");

                        } else {

                            // Mention
                            includedStrBuilder.append("Set entity\u00a73 ").append(ent.getType().toString()).append("\u00a77's score \u00a7e").append(objective.getName()).append("\u00a77 to \u00a7b").append(OotilityCeption.GetEntryScore(objective, ent.getUniqueId().toString())).append("\u00a77. ");
                        }
                    }
                }

                // Run Chain

                // Chain as player
                if (asPlayer) {
                    //TRG//OotilityCeption.Log("\u00a78TGI\u00a7a SCS\u00a77 Produced slots of success\u00a7f " + sInfo.getSlots4Success().toString());

                    String localChain = OotilityCeption.ReplaceFirst(chainedCommand.getChainedCommand(), "@t", sInfo.getSlots4Success().toString());
                    if (sInfo.getValueOfSuccess() != null) { localChain = OotilityCeption.ReplaceFirst(localChain, "@v", sInfo.getValueOfSuccess()); }
                    SuccessibleChain local = new SuccessibleChain(localChain, chainedCommand.getSuccessibleFlare());
                    local.chain(chained, (Player) ent, sender);

                // Chain as non-player
                } else { chainedCommand.chain(chained, ent.getUniqueId(), sender); }
            }

            // Notify failure
            if (Gunging_Ootilities_Plugin.sendGooPFailFeedback && successStuff.size() == 0) {
                // Log
                includedStrBuilder.append("The command had no effect on anyone. ");
            }
        }

        if (failMessage != null) {

            // For every player
            for (Player failure : totalPlayers) {
                failure.sendMessage(OotilityCeption.ParseColour(OotilityCeption.ParseConsoleCommand(failMessage, failure, failure, null, null)));
            }
        }
    }
    int successCount;
    boolean succeeded;

    @NotNull final StringBuilder includedStrBuilder = new StringBuilder();
    @NotNull public StringBuilder getIncludedStrBuilder() { return includedStrBuilder; }

    /**
     * Players to generate slots from
     */
    @NotNull HashMap<Player, ArrayList<ItemStackSlot>> registeredPlayers = new HashMap<>();
    /**
     * @return Players to generate slots from
     */
    public @NotNull HashMap<Player, ArrayList<ItemStackSlot>> getRegisteredPlayers() { return registeredPlayers; }
    /**
     * Register a player to generate slots from.
     *
     * @param player Player to register yes
     */
    public void registerPlayer(@NotNull Player player, @NotNull String slots, @Nullable StringBuilder ffp) {

        //Lets get that inven slot
        RefSimulator<String> slotFailure = new RefSimulator<>("");
        ArrayList<ItemStackSlot> inventorySlots = OotilityCeption.getInventorySlots(slots, player, slotFailure);

        // Log
        if (ffp != null && slotFailure.getValue() != null) { ffp.append(slotFailure.getValue()); }

        // Register the player
        registeredPlayers.put(player, inventorySlots);
    }
    /**
     * Register a few players to generate slots from idk.
     *
     * @param playerList List of players to register, will skip null ones.
     */
    public void registerPlayers(@NotNull List<Player> playerList, @NotNull String slots, @Nullable StringBuilder ffp) { for (Player p :playerList) { if (p != null) { registerPlayer(p, slots, ffp); } } }

    /**
     * Items to generate slots from
     */
    @NotNull ArrayList<Item> registeredDroppedItems = new ArrayList<>();
    /**
     * @return Items to generate slots from
     */
    public @NotNull ArrayList<Item> getRegisteredDroppedItems() { return registeredDroppedItems; }
    /**
     * Register an item to generate slots from.
     *
     * @param item Item to register yes
     */
    public void registerDroppedItem(@NotNull Item item) { registeredDroppedItems.add(item); }
    /**
     * Register a few items to generate slots from idk.
     *
     * @param itemList List of items to register, will skip null ones.
     */
    public void registerDroppedItems(@NotNull List<Item> itemList) { for (Item p : itemList) { if (p != null) { registerDroppedItem(p); } } }
}
