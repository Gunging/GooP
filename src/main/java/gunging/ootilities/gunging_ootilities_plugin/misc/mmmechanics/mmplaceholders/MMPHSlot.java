package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.GungingOotilities;
import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderMeta;
import io.lumine.xikage.mythicmobs.skills.placeholders.types.MetaPlaceholder;

public class MMPHSlot extends MMPlaceholder {

    @Override
    public String apply(PlaceholderMeta metadata, String arg) {

        // If there was an argument to the placeholder
        if (arg == null) { return "{arg}"; }

        // Was it <goop.slot.provided> ?
        if (arg.toLowerCase().equals("provided")) {

            // If the caster UUID is associated to a provided slot
            if (GungingOotilities.providedSlot.containsKey(metadata.getCaster().getEntity().getUniqueId())) {

                // Return the value of that 'provided slot'
                return String.valueOf(GungingOotilities.providedSlot.get(metadata.getCaster().getEntity().getUniqueId()));


                // The entity is invalid (it has no provided slot associated to it)
            } else { return "{ent}"; }

            // The slot is invalid (only <goop.slot.provided> is supported for now)
        } else { return "{slt}"; }
    }

    static MetaPlaceholder inst = null;
    public static MetaPlaceholder getInst() {
        if (inst != null) { return inst; }
        inst = Placeholder.meta(new MMPHSlot());
        return inst; }
}
