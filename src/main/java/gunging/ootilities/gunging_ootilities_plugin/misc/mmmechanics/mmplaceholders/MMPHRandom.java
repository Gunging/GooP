package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.misc.ListPlaceholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderMeta;
import io.lumine.xikage.mythicmobs.skills.placeholders.types.MetaPlaceholder;

public class MMPHRandom extends MMPlaceholder {

    static MetaPlaceholder inst = null;
    public static MetaPlaceholder getInst() {
        if (inst != null) { return inst; }
        inst = Placeholder.meta(new MMPHRandom());
        return inst; }

    @Override
    public String apply(PlaceholderMeta metadata, String arg) {

        // If valid
        if (arg == null) { return "{arg}"; }

        // Get List
        ListPlaceholder lph = ListPlaceholder.Get(arg);

        // Did it exist?
        if (lph != null) {

            // Well return the next balue-yo!
            return lph.RandomListItem();

        } else {

            // Invalid list
            return "Invalid List of Name '" + arg + "'";
        }
    }
}
