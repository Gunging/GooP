package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.ListPlaceholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderMeta;
import io.lumine.xikage.mythicmobs.skills.placeholders.types.MetaPlaceholder;

public class MMPHOrdered extends MMPlaceholder {

    static MetaPlaceholder inst = null;
    public static MetaPlaceholder getInst() {
        if (inst != null) { return inst; }
        inst = Placeholder.meta(new MMPHOrdered());
        return inst; }

    @Override
    public String apply(PlaceholderMeta metadata, String arg) {

        // If valid
        if (arg == null) { return "{arg}"; }

        // Strip ordered index
        int lastDot = arg.lastIndexOf('.');
        int orderedIndex = 0;

        // If there was a dot
        if (lastDot > 0 && arg.length() > (lastDot + 1)) {

            // Crop
            String postdot = arg.substring(lastDot + 1);

            // Does it parse
            if (OotilityCeption.IntTryParse(postdot)) {

                // Store ordered index
                orderedIndex = OotilityCeption.ParseInt(postdot);

                // Crop
                arg = arg.substring(0, lastDot);
            }
        }

        // Get List
        ListPlaceholder lph = ListPlaceholder.Get(arg);

        // Did it exist?
        if (lph != null) {

            // Well return the next balue-yo!
            return lph.NextListItem(orderedIndex);

        } else {

            // Invalid list
            return "Invalid List of Name '" + arg + "'";
        }
    }
}
