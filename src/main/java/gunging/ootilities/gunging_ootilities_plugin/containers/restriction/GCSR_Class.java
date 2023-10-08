package gunging.ootilities.gunging_ootilities_plugin.containers.restriction;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.containers.SlotRestriction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Requires a player to be of certain MMOCore class.
 */
public class GCSR_Class extends SlotRestriction {

    @NotNull final ArrayList<String> allowedClasses;

    public GCSR_Class(@NotNull ArrayList<String> allowedClasses) {
        this.allowedClasses = allowedClasses;
    }

    @Override
    public boolean isUnlockedFor(@NotNull Player player) {
        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eU\u00a77 Player has class? \u00a77" + serialize());

        // No class restriction no failure
        if (allowedClasses.size() == 0) {
            //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eU\u00a7a No goals");
            return true; }

        // No MMOCore no service
        if (!Gunging_Ootilities_Plugin.foundMMOItems) {
            //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eU\u00a7c No MMO");
            return false; }

        // If the class is contained-yo
        String clas = GooPMMOItems.GetPlayerClass(player).toUpperCase().replace(" ", "_".replace("-", "_"));
        for (String str : allowedClasses) {
            //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eC\u00a77 Compare player class \u00a7b" + clas + "\u00a77 with \u00a7e" + str);

            if ((str.toUpperCase().replace(" ", "_".replace("-", "_")).equals(clas))) {
                //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eU\u00a7a Matched class \u00a7e" + str);
                return true; } }

        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7eU\u00a7c No match");
        return false;
    }

    @NotNull
    @Override
    public String serialize() {

        // List separated by ∪s
        StringBuilder sb = new StringBuilder();
        for (String clas : allowedClasses) { OotilityCeption.ListIntoBuilder(sb, clas, "∪"); }
        return sb.toString();
    }

    @NotNull
    @Override
    public ArrayList<String> appendLore(@NotNull ArrayList<String> item) {

        // Build allowed class
        StringBuilder sb = new StringBuilder();
        for (String clas : allowedClasses) { OotilityCeption.ListIntoBuilder(sb, clas, " \u00a77or\u00a7b "); }

        // Add the line with color parsed
        item.add(OotilityCeption.ParseColour("\u00a73>\u00a77 Classes:\u00a7b " + sb.toString()));

        // Return thay
        return item;
    }

    @NotNull public static GCSR_Class deserialize(@NotNull String serialized) {

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
        return new GCSR_Class(profess);
    }

}
