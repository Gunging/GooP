package gunging.ootilities.gunging_ootilities_plugin.misc;

public enum GTL_SummonerClass {

    /**
     * Called when a minion exceeds minion capacity
     */
    MINION_EXCEED_CAP,

    /**
     * Called when a minion is not summoned because it would exceed
     * the minion capacity of the summoner
     */
    MINION_WOULD_EXCEED_CAP,

    /**
     * Called when a minion dies in battle
     */
    MINION_DEATH,
}
