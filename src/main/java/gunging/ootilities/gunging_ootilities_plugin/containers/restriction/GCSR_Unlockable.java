package gunging.ootilities.gunging_ootilities_plugin.containers.restriction;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.SlotRestriction;
import gunging.ootilities.gunging_ootilities_plugin.misc.GooPUnlockables;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Player must have an unlockable active yes.
 */
public class GCSR_Unlockable extends SlotRestriction {

    @NotNull final ArrayList<String> goals;

    public GCSR_Unlockable(@NotNull ArrayList<String> goals) {
        super();
        this.goals = goals;
    }

    public GCSR_Unlockable(@NotNull String goals) {
        super();
        this.goals = new ArrayList<>();
        this.goals.add(goals);
    }

    @Override
    public boolean isUnlockedFor(@NotNull Player player) {

        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a77 Player has UCK? \u00a77" + serialize());

        // No goal restrictions = no failure
        if (goals.size() == 0) {
            //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7a No goals");
            return true; }

        // Evaluate every goal
        for (String g : goals) {

            boolean unlocked = false;
            boolean negated = g.startsWith("!");
            String processed = negated ? g.substring(1) : g;

            GooPUnlockables uck = GooPUnlockables.Get(player.getUniqueId(), processed);

            // Valid?
            if (uck != null) {

                // Check time
                uck.CheckTimer();

                unlocked = uck.IsUnlocked();
                //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7a Found unlocked");
            }

            if (unlocked == !negated) { return true; }
        }

        // No goals met
        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7c No Match");
        return false;
    }
    @NotNull
    @Override
    public String serialize() {

        // List separated by ∪s
        StringBuilder sb = new StringBuilder();
        for (String clas : goals) { OotilityCeption.ListIntoBuilder(sb, clas, "∪"); }
        return sb.toString();
    }

    @NotNull
    @Override
    public ArrayList<String> appendLore(@NotNull ArrayList<String> item) {

        // Build allowed class
        StringBuilder sb = new StringBuilder();
        for (String clas : goals) { OotilityCeption.ListIntoBuilder(sb, clas, " \u00a77or\u00a7b "); }

        // Yes
        item.add(OotilityCeption.ParseColour("\u00a73>\u00a77 GooPUnlocked:\u00a7b " + sb.toString()));

        // Return thay
        return item;
    }

    @NotNull public static GCSR_Unlockable deserialize(@NotNull String serialized) {

        // Create array
        ArrayList<String> profess = new ArrayList<>();

        // Split by union symbol
        if (serialized.contains("∪")){

            // Classes
            String[] classes = serialized.split("∪");
            profess.addAll(Arrays.asList(classes));

        } else {

            // Just add it itself
            profess.add(serialized);
        }

        // Parse error
        return new GCSR_Unlockable(profess);
    }
}
