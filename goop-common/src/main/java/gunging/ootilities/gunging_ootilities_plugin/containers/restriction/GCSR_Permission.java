package gunging.ootilities.gunging_ootilities_plugin.containers.restriction;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.containers.SlotRestriction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Requires the player to have a permission to use this slot
 */
public class GCSR_Permission extends SlotRestriction {
    @NotNull final ArrayList<String> permissions;
    @NotNull final ArrayList<String> negatedPermissions;

    public GCSR_Permission(@NotNull ArrayList<String> permissions) {
        super();
        this.permissions = new ArrayList<>();
        this.negatedPermissions = new ArrayList<>();

        for (String str : permissions) {

            // Is it negated?
            if (str.startsWith("!")) { this.negatedPermissions.add(str.substring(1));

            // Normal check, then
            } else { this.permissions.add(str); }
        }
    }

    @NotNull public static ArrayList<ArrayList<String>> Parse(@NotNull String permissionsCompact) {
        ArrayList<ArrayList<String>> partitions = new ArrayList<>();

        ArrayList<String> pars;

        /*
         * 'AND' operator, will require for both conditions to match
         *
         *          this&&that
         *
         */
        if (permissionsCompact.contains("&&")) {

            // Split all the conditions
            pars = new ArrayList<>(Arrays.asList(permissionsCompact.split("&&")));

        } else {

            // There is only one condition
            pars = new ArrayList<>();
            pars.add(permissionsCompact);
        }

        // Evaluate every condition
        for (String par : pars) {
            if (par.isEmpty()) { continue; }

            // Split by commas
            ArrayList<String> part = new ArrayList<>();

            /*
             * 'OR' operator, will require either condition to match
             *
             *   this,that
             *
             *
             * Combined with the AND operator:
             *
             *    this,that&&something
             *
             * Must match 'something' and either 'this' or 'that'
             *
             */
            if (par.contains(",")) {

                // For every entry of the split-by-comma list, add parsing spaces
                for (String p : par.split(",")) { part.add(p.replace("__", " "));}

                // No commas, only one entry, add parsing spaces.
            } else { part.add(par.replace("__", " ")); }

            // Include this list of OR conditions
            partitions.add(part);
        }

        return partitions;
    }

    @Override
    public boolean isUnlockedFor(@NotNull Player player) {

        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a77 Player has perm? \u00a77" + serialize());
        if (player.isOp()) { return isUnlockedForOP(); }

        // No perms restriction no failure
        if ((permissions.size() + negatedPermissions.size()) == 0) {
            //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7a No goals");
            return true; }

        // Check every negated permission for at least one match
        for (String perm : negatedPermissions) {

            // Has permission? Success!
            if (player.hasPermission(perm)) {
                //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7a Found perm");
                return false; } }

        // Check every permission for at least one match
        for (String perm : permissions) {

            // Has permission? Success!
            if (player.hasPermission(perm)) {
                //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7a Found perm");
                return true; } }

        // Player did not match any permission, will succeed if that was the success criterion
        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7c No Match");
        return negatedPermissions.size() > 0;
    }

    /**
     * @return If this is unlocked assuming the target has ALL permissions
     */
    public boolean isUnlockedForOP() {

        // No perms restriction no failure
        if ((permissions.size() + negatedPermissions.size()) == 0) {
            //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7a No goals");
            return true; }

        // This has all permissions, so a single negated permission fauxes
        if (negatedPermissions.size() > 0) { return false; }

        // Requires any permission? We have it so yes.
        return permissions.size() > 0;

        // Player did not match any permission
        //RES//OotilityCeption. Log("\u00a78RESTRICT \u00a7U\u00a7c No Match");
    }

    @NotNull
    @Override
    public ArrayList<String> appendLore(@NotNull ArrayList<String> item) {

        // Build allowed class
        StringBuilder sb = new StringBuilder();
        for (String clas : permissions) { OotilityCeption.ListIntoBuilder(sb, clas, " \u00a77or\u00a7b "); }

        item.add(OotilityCeption.ParseColour("\u00a73>\u00a77 Permissions:\u00a7b " + sb.toString()));

        // Return thay
        return item;
    }

    @NotNull
    @Override
    public String serialize() {

        // List separated by ∪s
        StringBuilder sb = new StringBuilder();
        for (String clas : permissions) { OotilityCeption.ListIntoBuilder(sb, clas, "∪"); }
        return sb.toString();
    }

    @NotNull public static GCSR_Permission deserialize(@NotNull String serialized) {

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
        return new GCSR_Permission(profess);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean com = false;

        // Add positives
        for (String str : permissions) {

            if (com) { builder.append(','); } else { com = true; }

            builder.append(str);
        }

        // Add Negations
        for (String str : negatedPermissions) {

            if (com) { builder.append(','); } else { com = true; }

            builder.append('!').append(str);
        }

        // Build
        return builder.toString();
    }
}
