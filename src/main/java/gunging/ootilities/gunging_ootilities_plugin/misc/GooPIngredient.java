package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GooPIngredient {

    //region Whois
    String name;
    public String getName() { return name; }

    ArrayList<NBTFilter> validReplacements;

    /**
     * An ingredient at these offsets from the core ingredient.
     * <p></p>
     * One may use any of these NBT Filters as substitutes for each other (say, OAK_PLANKs or PINE_PLANKs or JUNGLE_PLANKs...) when filling this ingredient.
     */
    public GooPIngredient(@NotNull ArrayList<NBTFilter> ingredient, int yOffset, int xOffset, String nmae) {
        validReplacements = ingredient;
        setVerticalOffset(yOffset);
        setHorizontalOffset(xOffset);
        AmountizeIngredients();
        name = nmae;
    }

    /**
     * An ingredient at these offsets from the core ingredient
     */
    public GooPIngredient(@NotNull NBTFilter ingredient, int yOffset, int xOffset, String nmae) {
        validReplacements = new ArrayList<>();
        validReplacements.add(ingredient);
        setVerticalOffset(yOffset);
        setHorizontalOffset(xOffset);
        AmountizeIngredients();
        name = nmae;
    }

    /**
     * Recipes forcefully require an amount, it cannot be unspecified.
     */
    void AmountizeIngredients() {

        // Amountillado
        for (int n = 0; n < validReplacements.size(); n++) {

            // Get
            NBTFilter nbt = validReplacements.get(n);

            // Fix
            if (nbt.getAmount() == null) {
                validReplacements.get(n).setAmount(1);
            }

            // Uh no
            if (nbt.getFilterKey().equals("i")) {

                // ew no
                validReplacements.remove(nbt);
                n--;

                // Log
                Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cYou must not use the \u00a76i (ingredient)\u00a7c Item NBT Filter to define ingredients.\u00a77 Line \u00a7e" + nbt.toString() + "\u00a77 when defining ingredient \u00a73" + getName() + "\u00a77. \u00a74Ignored.");
            }
        }
    }

    /**
     * Is such an item suitable to fulfill this ingredient?
     */
    public boolean Matches(ItemStack what) {

        // Try every substitute
        for (NBTFilter fil : validReplacements) {

            // One success is enough!
            if (OotilityCeption.MatchesItemNBTtestString(what, fil, null)) { return true; }
        }

        // Nope
        return false;
    }
    //endregion

    //region Relative Location Stuff
    /**
     * The vertical shift in slots relative to the core ingredient.
     * <p></p>
     * Always Negative
     */
    public int getVerticalOffset() {
        return verticalOffset;
    }

    /**
     * The vertical shift in slots relative to the core ingredient.
     * <p></p>
     * Always Negative
     */
    public void setVerticalOffset(int verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    /**
     * The vertical shift in slots relative to the core ingredient.
     * <p></p>
     * Always Negative
     */
    int verticalOffset = 0;

    /**
     * The horizontal shift in slots relative to the core ingredient.
     * <p></p>
     * Always Positive
     */
    public int getHorizontalOffset() {
        return horizontalOffset;
    }

    /**
     * The horizontal shift in slots relative to the core ingredient.
     * <p></p>
     * Always Positive
     */
    public void setHorizontalOffset(int horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    /**
     * The horizontal shift in slots relative to the core ingredient.
     * <p></p>
     * Always Positive
     */
    int horizontalOffset = 0;
    //endregion

    //region Manager
    static HashMap<String, GooPIngredient> loadedIngs = new HashMap<>();
    public static void Enable(GooPIngredient ing) {

        // If invalid
        if (Get(ing.getName()) == null) {

            // Load :B
            loadedIngs.put(ing.getName(), ing);
        }
    }

    @Nullable
    public static GooPIngredient Get(@NotNull String name) {

        // Loaded?
        return loadedIngs.get(name);
    }

    @NotNull
    public static ArrayList<String> GetLoadedIngrs() { return new ArrayList<>(loadedIngs.keySet());}

    public void Reload() {

        // Clear Loaded Array
        loadedIngs.clear();

        // Evaluate very loaded ingredient
        for (FileConfigPair csPair : Gunging_Ootilities_Plugin.theMain.ingredientsPairs) {

            // Load the YML from it
            YamlConfiguration csConfig = csPair.getStorage();

            // Examine Every Entry
            for(Map.Entry<String, Object> val : (csConfig.getValues(false)).entrySet()) {

                // Get Template Name
                String tName = val.getKey();

                ArrayList<String> cont = new ArrayList<>();
                if (csConfig.contains(tName + ".Items")) { cont = new ArrayList<>(csConfig.getStringList(tName + ".Items")); }

                // Register if any
                if (cont.size() > 0) {

                    // Get
                    GooPIngredient neww = Deserialize(cont, tName);

                    // Add
                    Enable(neww);
                }
            }
        }
    }
    //endregion

    /**
     * Meant to deserialize a compound ingredient. Not ingredients in recipes
     */
    @NotNull
    public static GooPIngredient Deserialize(@NotNull ArrayList<String> serializedContents, @NotNull String asName) {

        // Array
        ArrayList<NBTFilter> ret = new ArrayList<>();

        // Well
        for (String ing : serializedContents) {

            // Split
            String[] ingSplit = ing.split(" ");

            // Must have at least three
            if (ingSplit.length == 3 || ingSplit.length == 4) {

                // Identify
                String filter = ingSplit[0];
                String prime = ingSplit[1];
                String dime = ingSplit[2];
                String amount = null; if (ingSplit.length == 4) { amount = ingSplit[3]; }
                RefSimulator<String> logAddition = new RefSimulator<>("");

                // Proc
                if (!filter.equals("i")) {

                    // Evaluate
                    if (!OotilityCeption.IsInvalidItemNBTtestString(filter, prime, dime, amount, logAddition)) {

                        // Parse amount bruh
                        Integer parsed = null;
                        if (amount != null) { parsed = OotilityCeption.ParseInt(amount); }

                        // Create and add
                        ret.add(new NBTFilter(filter, prime, dime, parsed));

                    // Log I guess
                    } else if (logAddition.getValue() != null) {

                        // Log
                        Gunging_Ootilities_Plugin.theOots.CPLog("\u00a7cCould not load ingredient \u00a73" + asName + "\u00a7c: \u00a77" + logAddition.getValue());
                    }
                }
            }
        }

        // Proc ig
        return new GooPIngredient(ret, 0, 0, asName);
    }
}
