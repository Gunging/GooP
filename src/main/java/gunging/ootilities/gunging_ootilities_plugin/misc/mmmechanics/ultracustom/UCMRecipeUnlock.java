package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Iterator;

/**
 * Locks and re-unlocks the bukkit recipe so it shows up as "Recipe Unlocked"
 *
 * <b>lock</b> Enabling this option will not re-unlock the recipe, effectively locking it.
 * <b>printRecipes</b> String to filter out recipes to show or debug. Use 'all' for no filter.
 */
public class UCMRecipeUnlock extends SkillMechanic implements ITargetedEntitySkill {

    /**
     * Locking the recipes will not unlock it after locking it....
     */
    boolean lock;

    /**
     * Changes the behaviour to print all the recipes for debugging purposes.
     */
    String printRecipes;

    PlaceholderString namespace, key;

    //NEWEN//public UCMRecipeUnlock(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        //NEWEN//super(manager, file, line, mlc);
        /*OLDEN*/public UCMRecipeUnlock(SkillExecutor manager, String line, MythicLineConfig mlc) {
            /*OLDEN*/super(manager, line, mlc);
        GooPMythicMobs.newenOlden = true;

        this.forceSync = true;
        lock = mlc.getBoolean(new String[]{"lock"}, false);
        printRecipes = mlc.getString(new String[]{"printRecipes", "pr"}, null);
        this.forceSync = true;
        // yeah
        namespace = mlc.getPlaceholderString(new String[]{"namespace", "n"}, "minecraft");
        key = mlc.getPlaceholderString(new String[]{"key", "k"}, null);

        //DBG//OotilityCeption.Log("\u00a78UCM\u00a7a RCU\u00a77 Loading... PR?\u00a7e " + printRecipes);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        //DBG//OotilityCeption.Log("\u00a78UCM\u00a7a RCU\u00a7d " + skillMetadata.getCaster().getName() + "\u00a7c @" + abstractEntity.getName() + " \u00a7e~" + (skillMetadata.getTrigger() == null ? "null" : skillMetadata.getTrigger().getName()) + "\u00a7b Recipe Unlock");

        // Is the PR not null?
        if (printRecipes != null) {

            // Make sure its replaced in colons
            String filter = printRecipes.replace("<&co>", ":").replace("<&sp>", " ").replace("\"", "");
            boolean printAll = filter.equals("all") || filter.isEmpty() || filter.equals(" ");
            boolean success = false;

            //DBG//OotilityCeption.Log("\u00a78UCM\u00a7c RCU\u00a77 Print All:\u00a7e " + printAll + "\u00a77, Filter:\u00a7b " + filter);

            // Iterate
            Iterator<Recipe> recipes = Bukkit.recipeIterator();
            while (recipes.hasNext()) {

                // Choose next recipe
                Recipe recipe = recipes.next();

                // Only keyed recipes are supported
                if (!(recipe instanceof Keyed)) {
                    //DBG//OotilityCeption.Log("\u00a78UCM\u00a7c RCU\u00a77 Keyless Recipe:\u00a7e " + OotilityCeption.GetItemName(recipe.getResult(), true));
                    continue; }

                // Haha result
                ItemStack result = recipe.getResult();

                // All right
                Keyed keyed = (Keyed) recipe;
                // Yeah
                String keyString = keyed.getKey().getKey();
                String nameString = keyed.getKey().getNamespace();

                // Wut
                //DBG//OotilityCeption.Log("\u00a78UCM\u00a7a RCU\u00a77 Namespace:\u00a7e " + nameString + "\u00a77, Key:\u00a7b " + keyString + "\u00a77, Output:\u00a7f " + OotilityCeption.GetItemName(recipe.getResult(), true));

                if (printAll) {
                    success = true;

                    // Print it
                    Gunging_Ootilities_Plugin.theOots.CLog("\u00a78UCM\u00a7a RCU\u00a77 Namespace:\u00a7e " + nameString + "\u00a77, Key:\u00a7b " + keyString + "\u00a77, Output:\u00a7f " + OotilityCeption.GetItemName(result, true));

                // If filter is contained
                } else if (keyString.toLowerCase().contains(filter.toLowerCase())) {
                    success = true;

                    // Only print if the key matches it
                    Gunging_Ootilities_Plugin.theOots.CLog("\u00a78UCM\u00a7a RCU\u00a77 Namespace:\u00a7e " + nameString + "\u00a77, Key:\u00a7b " + keyString + "\u00a77, Output:\u00a7f " + OotilityCeption.GetItemName(result, true));
                }
            }

            // No successes?
            if (!success) {

                // Print it
                Gunging_Ootilities_Plugin.theOots.CLog("\u00a78UCM\u00a7a RCU\u00a7c No recipes with keys that contain this filter:\u00a76 " + filter);
            }

            // That's it
            return SkillResult.SUCCESS;
        }

        if (key == null || namespace == null) { return SkillResult.INVALID_CONFIG; }

        // Caster
        Entity kaster = abstractEntity.getBukkitEntity();

        // Only player
        if (!(kaster instanceof Player)) { return SkillResult.INVALID_TARGET; }

        // Go go go
        String nkKey = key.get(skillMetadata);
        String nkName = namespace.get(skillMetadata);
        NamespacedKey nk = new NamespacedKey(nkName, nkKey);

        // Cast
        Player player = (Player) kaster;

        (new BukkitRunnable() {
            @Override
            public void run() {

                // Un-discover Recipe
                player.undiscoverRecipe(nk);
                //DBG//OotilityCeption.Log("\u00a78UCM\u00a7c RCU\u00a77 " + player.getName() + " Undiscovering:\u00a7b " + nk.toString());
            }
        }).runTask(Gunging_Ootilities_Plugin.theMain);

        // THats it
        if (lock) { return SkillResult.SUCCESS; }

        (new BukkitRunnable() {
            @Override
            public void run() {

                // Discover again I guess
                player.discoverRecipe(nk);
                //DBG//OotilityCeption.Log("\u00a78UCM\u00a7c RCU\u00a77 " + player.getName() + " Discovering:\u00a7b " + nk.toString());
            }
        }).runTask(Gunging_Ootilities_Plugin.theMain);

        // Haha yes
        return SkillResult.SUCCESS;
    }
}
