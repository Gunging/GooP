package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;

public class MMPHBowdraw extends MMPlaceholder {

    static MetaPlaceholder inst = null;
    public static MetaPlaceholder getInst() {
        if (inst != null) { return inst; }
        inst = Placeholder.meta(new MMPHBowdraw());
        return inst; }

    @Override
    public String apply(PlaceholderMeta metadata, String arg) {

        // If valid
        if (arg == null) { return "{arg}"; }

        // <goop.bowdraw.caster> To get last amount of bow drawin the caster did
        if (arg.toLowerCase().equals("caster")) {

            // Get or default
            Float f = XBow_Rockets.bowDrawForce.get(metadata.getCaster().getEntity().getUniqueId());
            if (f == null) { f = 0F; }

            return OotilityCeption.RemoveDecimalZeros(String.valueOf(f));

            // <goop.bowdraw.trigger> To get last amount of bow drawing the trigger did
        } else if (arg.toLowerCase().equals("trigger")) {

            if (XBow_Rockets.bowDrawForce.containsKey(metadata.getTrigger().getUniqueId())) {

                return OotilityCeption.RemoveDecimalZeros(String.valueOf(XBow_Rockets.bowDrawForce.get(metadata.getCaster().getEntity().getUniqueId())));

            } else { return "00"; }

        } else { return "000"; }
    }
}
