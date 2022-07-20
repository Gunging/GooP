package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.mmplaceholders;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;

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
        if (arg == null) { return String.valueOf(System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime()); }

        // Split by dots
        String[] args;
        if (arg.contains(".")) { args = arg.split("\\."); } else { args = new String[] { arg }; }

        ArrayList<String> fun = new ArrayList<>();
        for (int i = 1; i < args.length; i++) { fun.add(args[i].toLowerCase()); }

        // Sin mode?
        boolean sinMode = fun.contains("sin");
        boolean cosMode = fun.contains("cos");
        boolean roundMode = fun.contains("round");
        boolean ceilMode = fun.contains("ceil");
        boolean floorMode = fun.contains("floor");
        boolean degrees = fun.contains("deg");
        boolean radians = fun.contains("rad");
        boolean modular1 = fun.contains("mod");
        boolean modularPI = fun.contains("modPI");
        boolean nano = fun.contains("n");
        boolean micro = fun.contains("u");
        boolean milli = fun.contains("m");
        boolean centi = fun.contains("c");
        boolean deci = fun.contains("d");
        boolean deca = fun.contains("da");
        boolean hecto = fun.contains("h");
        boolean kilo = fun.contains("k");
        boolean times2 = fun.contains("2");
        boolean times3 = fun.contains("3");
        boolean times5 = fun.contains("5");
        boolean times7 = fun.contains("7");

        double product = 1D;
        if (nano)  { product *= 0.000000001D; }
        if (micro)  { product *= 0.000001D; }
        if (milli)  { product *= 0.001D; }
        if (centi)  { product *= 0.01D;  }
        if (deci)   { product *= 0.1D;   }
        if (deca)   { product *= 10D;    }
        if (hecto)  { product *= 100D;   }
        if (kilo)   { product *= 1000D;  }

        double value = 0;
        double valueNormalizer = 1;

        //TIM//OotilityCeption.Log("\u00a78PROJ\u00a73 DT\u00a77 Arg\u00a7b " + args[0]);
        //TIM//for (String str : fun) {//ORG//OotilityCeption.Log("\u00a78PROJ\u00a73 DT\u00a7b + \u00a77 " + str); }

        // Snoozer time
        if ("time".equals(args[0])) {

            // World time
            if (fun.contains("world")) {
                value = metadata.getCaster().getEntity().getLocation().getWorld().getFullTime();

            // GCD actually
            } else if (fun.contains("gcd")) {
                value = metadata.getCaster().getGlobalCooldown();

            // System Time
            } else  {
                value = (System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime());
                valueNormalizer = 0.001D;
            }

        } else if ("origin".equals(args[0]) && metadata instanceof SkillMetadata) {
           //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 Origin Placeholder");
            AbstractLocation origin = ((SkillMetadata) metadata).getOrigin();
            AbstractLocation target;
            if (fun.contains("trigger")) {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 Using trigger");

                // No trigger
                if (metadata.getTrigger() == null) { return "000000"; }
                target = metadata.getTrigger().getLocation();

            } else {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 Using Caster");

                // Yes, caster
                target = metadata.getCaster().getLocation();
            }

            if (fun.contains("x")) {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 X");
                value = origin.getX();

            } else if (fun.contains("y")) {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 Y");
                value = origin.getY();

            } else if (fun.contains("z")) {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 Z");
                value = origin.getZ();

            } else if (fun.contains("dx")) {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 DX");
                value = origin.getX() - target.getX();

            } else if (fun.contains("dy")) {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 DY");
                value = origin.getY() - target.getY();

            } else if (fun.contains("dz")) {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 DZ");
                value = origin.getZ() - target.getZ();

            } else {
               //ORG//OotilityCeption.Log("\u00a73MPH\u00a7a ORIGIN\u00a77 Dist");
                value = origin.distance(target);
            }

        }

        //TIM//OotilityCeption.Log("\u00a78PROJ\u00a73 DT\u00a77 Preprocess \u00a7f " + value + "\u00a78 (norm\u00a73 " + valueNormalizer + "\u00a78)");

        value *= valueNormalizer;
        if (degrees) { value *= 180D / Math.PI; }
        if (radians) { value *= Math.PI / 180D; }
        if (times2) { value *= 2; }
        if (times3) { value *= 3; }
        if (times5) { value *= 5; }
        if (times7) { value *= 7; }
        value *= product;
        if (modular1) { value %= 1; }
        if (modularPI) { value %= Math.PI; }
        if (sinMode) { value = Math.sin(value); }
        if (cosMode) { value = Math.cos(value); }
        if (roundMode) { value = Math.round(value); }
        if (ceilMode) { value = Math.ceil(value); }
        if (floorMode) { value = Math.floor(value); }

        //TIM//OotilityCeption.Log("\u00a78PROJ\u00a73 DT\u00a77 Cooked \u00a7f " + value);

        // Perform opps
        return OotilityCeption.RemoveDecimalZeros(String.valueOf(value));
    }
}
