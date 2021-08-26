package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class OrderedScoreboardEntry implements Comparable<OrderedScoreboardEntry> {
    Score source;

    public OrderedScoreboardEntry(Score sc) { source = sc; }

    public String GetEntry() { return source.getEntry(); }
    public String getEntry() { return source.getEntry(); }

    public Objective getObjective() { return source.getObjective(); }
    public Objective GetObjective() { return source.getObjective(); }

    public int getScore() { return source.getScore(); }
    public int GetScore() { return source.getScore(); }
    public int getValue() { return source.getScore(); }
    public int GetValue() { return source.getScore(); }


    @Override
    /*
     * This is where we write the logic to sort. This method sort
     * automatically by the first name in case that the last name is
     * the same.
     */
    public int compareTo(OrderedScoreboardEntry au){
        /*
         * Should return < 0 if this is supposed to be less than au
         * > 0 if this is supposed to be greater than object au
         * and 0 if they are supposed to be equal.
         */

        return getScore() - au.getScore();
    }
}
