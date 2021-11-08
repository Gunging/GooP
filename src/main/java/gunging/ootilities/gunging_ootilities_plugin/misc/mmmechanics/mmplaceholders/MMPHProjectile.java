package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderMeta;
import io.lumine.xikage.mythicmobs.skills.placeholders.types.MetaPlaceholder;

import java.util.ArrayList;

/**
 * Variables intending to allow parametric projectiles???
 */
public class MMPHProjectile extends MMPlaceholder {

    static MetaPlaceholder inst = null;
    public static MetaPlaceholder getInst() {
        if (inst != null) { return inst; }
        inst = Placeholder.meta(new MMPHProjectile());
        return inst; }

    @Override
    public String apply(PlaceholderMeta metadata, String arg) {

        // Time yes
        if (arg == null || !arg.contains(".")) { return String.valueOf(System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime()); }

        // Split by dots
        String[] args = arg.split("\\.");

        ArrayList<String> fun = new ArrayList<>();
        for (int i = 1; i < args.length; i++) { fun.add(args[i].toLowerCase()); }

        // Sin mode?
        boolean sinMode = fun.contains("sin");
        boolean cosMode = fun.contains("cos");
        boolean roundMode = fun.contains("round");
        boolean ceilMode = fun.contains("ceil");
        boolean floorMode = fun.contains("floor");
        boolean degrees = fun.contains("deg");
        boolean milli = fun.contains("m");
        boolean centi = fun.contains("c");
        boolean deci = fun.contains("d");
        boolean deca = fun.contains("da");
        boolean hecto = fun.contains("h");
        boolean kilo = fun.contains("k");

        double product = 1D;
        if (milli)  { product *= 0.001D; }
        if (centi)  { product *= 0.01D;  }
        if (deci)   { product *= 0.1D;   }
        if (deca)   { product *= 10D;    }
        if (hecto)  { product *= 100D;   }
        if (kilo)   { product *= 1000D;  }

        double value = 0;

        /*TIM*/OotilityCeption.Log("\u00a78PROJ\u00a73 DT\u00a77 Arg\u00a7b " + args[0]);
        /*TIM*/for (String str : fun) { OotilityCeption.Log("\u00a78PROJ\u00a73 DT\u00a7b + \u00a77 " + str); }

        // Snoozer time
        if ("time".equals(args[0])) {

            // World time
            if (fun.contains("world")) {
                value = metadata.getCaster().getEntity().getLocation().getWorld().getFullTime();

            // GCD actually
            } else if (fun.contains("gcd")) {
                value = metadata.getCaster().getGlobalCooldown() * 0.05D;

            // System Time
            } else {
                value = (System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime()) * 0.001D ;
            }
        }

        /*TIM*/for (String str : fun) { OotilityCeption.Log("\u00a78PROJ\u00a73 DT\u00a77 Preprocess \u00a7f " + value); }

        value *= product;
        if (degrees) { value *= 180D / Math.PI; }
        if (sinMode) { value = Math.sin(value); }
        if (cosMode) { value = Math.cos(value); }
        if (roundMode) { value = Math.round(value); }
        if (ceilMode) { value = Math.ceil(value); }
        if (floorMode) { value = Math.floor(value); }

        // Perform opps
        return OotilityCeption.RemoveDecimalZeros(String.valueOf(value));
    }
}
