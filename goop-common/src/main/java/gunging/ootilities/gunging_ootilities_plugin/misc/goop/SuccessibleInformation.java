package gunging.ootilities.gunging_ootilities_plugin.misc.goop;

import gunging.ootilities.gunging_ootilities_plugin.misc.PlusMinusPercent;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * All the per-entity information when a command
 * succeeds lets goooo.
 */
public class SuccessibleInformation {

    @NotNull HashMap<Objective, ArrayList<PlusMinusPercent>> scoreboardOpps = new HashMap<>();
    @NotNull public HashMap<Objective, ArrayList<PlusMinusPercent>> getScoreboardOpps() { return scoreboardOpps; }
    @NotNull ArrayList<Objective> finalizedObjectives = new ArrayList<>();
    @NotNull ArrayList<Objective> initializedObjectives = new ArrayList<>();

    /**
     * Uses {@link String#valueOf(Object)}
     *
     * @param value Value used to replace the next @v in the successible command chain
     */
    public void setValueOfSuccess(@Nullable Object value) { valueOfSuccess = (value == null) ? null : String.valueOf(value); }
    @Nullable String valueOfSuccess;
    @Nullable public String getValueOfSuccess() { return valueOfSuccess; }

    /**
     * Prevents new operations from being added to it, and sets
     * this operation as the last operation that will happen.
     *
     * @param objective Objective to affect
     * @param opp Operation to perform
     *
     * @see PlusMinusPercent
     */
    public void setFinalScoreboardOpp(@NotNull Objective objective, @NotNull PlusMinusPercent opp) {
        if (finalizedObjectives.contains(objective)) { return; }

        ArrayList<PlusMinusPercent> pOpp = scoreboardOpps.get(objective);
        if (pOpp == null) { pOpp = new ArrayList<>(); }
        pOpp.add(opp);

        finalizedObjectives.add(objective);
        scoreboardOpps.put(objective, pOpp);
    }

    /**
     * Adds an operation to the very beginning of this chain.
     * Will be ignored subsequent calls (only works once).
     *
     * @param objective Objective to affect
     * @param opp Operation to perform
     *
     * @see PlusMinusPercent
     */
    public void setInitScoreboardOpp(@NotNull Objective objective, @NotNull PlusMinusPercent opp) {
        if (initializedObjectives.contains(objective)) { return; }
        if (finalizedObjectives.contains(objective)) { return; }

        ArrayList<PlusMinusPercent> pOpp = scoreboardOpps.get(objective);
        if (pOpp == null) { pOpp = new ArrayList<>(); }
        pOpp.add(0, opp);

        initializedObjectives.add(objective);
        scoreboardOpps.put(objective, pOpp);
    }
    /**
     * Easily add a scoreboard operation to perform at the end of this chain.
     *
     * @param objective Objective to affect
     * @param value Value to operate
     * @param additive Is it relative?
     * @param multiplicative Is it multiplicative?
     *
     * @see PlusMinusPercent
     */
    public void setInitScoreboardOpp(@NotNull Objective objective, @Nullable Integer value, boolean additive, boolean multiplicative) {
        if (value == null) { value = 0; }
        PlusMinusPercent opp = new PlusMinusPercent((double) (int) value, additive, multiplicative);

        setInitScoreboardOpp(objective, opp);
    }
    /**
     * Easily add a scoreboard operation to perform at the end of this chain.
     *
     * @param objective Objective to affect
     * @param value Double value to operate
     * @param additive Is it relative?
     * @param multiplicative Is it multiplicative?
     *
     * @see PlusMinusPercent
     */
    public void setInitScoreboardOpp(@NotNull Objective objective, @Nullable Double value, boolean additive, boolean multiplicative) {
        if (value == null) { value = 0D; }
        PlusMinusPercent opp = new PlusMinusPercent(value, additive, multiplicative);

        setInitScoreboardOpp(objective, opp);
    }
    /**
     * Prior to any operation, resets this scoreboard objective.
     *
     * @param objective Objective to affect
     *
     * @see #setInitScoreboardOpp(Objective, PlusMinusPercent)
     */
    public void setInitZero(@NotNull Objective objective) {
        PlusMinusPercent opp = new PlusMinusPercent(0D, false, false);

        setInitScoreboardOpp(objective, opp);
    }

    /**
     * Easily add a scoreboard operation to perform at the end of this chain.
     *
     * @param objective Objective to affect
     * @param opp Operation to perform
     *
     * @see PlusMinusPercent
     */
    public void addScoreboardOpp(@NotNull Objective objective, @NotNull PlusMinusPercent opp) {
        if (finalizedObjectives.contains(objective)) { return; }

        ArrayList<PlusMinusPercent> pOpp = scoreboardOpps.get(objective);
        if (pOpp == null) { pOpp = new ArrayList<>(); }
        pOpp.add(opp);

        scoreboardOpps.put(objective, pOpp);
    }
    /**
     * Easily add a scoreboard operation to perform at the end of this chain.
     *
     * @param objective Objective to affect
     * @param value Value to operate
     * @param additive Is it relative?
     * @param multiplicative Is it multiplicative?
     *
     * @see PlusMinusPercent
     */
    public void addScoreboardOpp(@NotNull Objective objective, @Nullable Integer value, boolean additive, boolean multiplicative) {
        if (value == null) { value = 0; }
        PlusMinusPercent opp = new PlusMinusPercent((double) (int) value, additive, multiplicative);

        addScoreboardOpp(objective, opp);
    }
    /**
     * Easily add a scoreboard operation to perform at the end of this chain.
     *
     * @param objective Objective to affect
     * @param value Double value to operate
     * @param additive Is it relative?
     * @param multiplicative Is it multiplicative?
     *
     * @see PlusMinusPercent
     */
    public void addScoreboardOpp(@NotNull Objective objective, @Nullable Double value, boolean additive, boolean multiplicative) {
        if (value == null) { value = 0D; }
        PlusMinusPercent opp = new PlusMinusPercent(value, additive, multiplicative);

        addScoreboardOpp(objective, opp);
    }

    public SuccessibleInformation() {}

    @NotNull final StringBuilder slots4Success = new StringBuilder();
    @NotNull public StringBuilder getSlots4Success() { return slots4Success; };
}
