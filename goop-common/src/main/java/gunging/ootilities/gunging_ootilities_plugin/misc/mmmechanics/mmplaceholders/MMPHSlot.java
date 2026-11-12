package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.GungingOotilities;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;

public class MMPHSlot extends MMPlaceholder {

    @Override
    public String apply(PlaceholderMeta metadata, String arg) {

        // If there was an argument to the placeholder
        if (arg == null) { return "{arg}"; }

        // Was it <goop.slot.provided> ?
        if (arg.toLowerCase().equals("provided")) {

            // Get provided
            String providedSlot = GungingOotilities.getProvidedSlot(metadata.getCaster().getEntity().getUniqueId(), false);

            // Return the value of that 'provided slot'
            if (providedSlot != null) { return providedSlot; } else { return "{ent}"; }

            // The slot is invalid (only <goop.slot.provided> is supported for now)
        } else if (arg.toLowerCase().equals("provided.full")) {

            // Get provided
            String providedSlot = GungingOotilities.getProvidedSlot(metadata.getCaster().getEntity().getUniqueId(), true);

            // Return the value of that 'provided slot'
            if (providedSlot != null) { return providedSlot; } else { return "{ent}"; }

            // The slot is invalid (only <goop.slot.provided> is supported for now)
        } else { return "{slt}"; }
    }

    static MetaPlaceholder inst = null;
    public static MetaPlaceholder getInst() {
        if (inst != null) { return inst; }
        inst = Placeholder.meta(new MMPHSlot());
        return inst; }
}
