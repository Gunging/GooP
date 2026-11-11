package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;

import java.util.ArrayList;
import java.util.HashMap;

public class ListPlaceholder {

    // Ordeal
    public static HashMap<String, ListPlaceholder> loadedListPlaceholders = new HashMap<>();

    /**
     * Will find an existing of these with such a name, or will return null if there is none loaded.
     * @param name Name of the list placeholder you search (Case Sensitive)
     * @return Either the Placeholder you are searching, or null if none is loaded of that name
     */
    public static ListPlaceholder Get(String name) {

        // If there is some
        if (loadedListPlaceholders.containsKey(name)) {

            // Return thay
            return loadedListPlaceholders.get(name);
        }

        // Otherwise its null-yo
        return null;
    }

    String name;
    HashMap<String, Integer> contentsWithWeights = new HashMap<>();
    ArrayList<String> listedContents = new ArrayList<>();
    HashMap<Integer, String> weightedContents = new HashMap<>();
    int totalWeight = 1;

    public String getName() { return name; }
    public String GetName() { return name; }

    public ListPlaceholder(String nmae, ArrayList<String> rawContents) {

        // Make baked contents
        contentsWithWeights = new HashMap<>();
        listedContents = new ArrayList<>();

        // Process contents
        for (String str : rawContents) {

            // Include in the list
            listedContents.add(str);

            // Git weight
            int weight = 1;
            int spIndex = str.lastIndexOf(" ");

            // Get everything after the last space, if there is any
            if (spIndex >= 0 && str.length() > (spIndex + 1)) {

                // Crop
                String w8ght = str.substring(spIndex + 1);

                // Does it parse
                if (OotilityCeption.IntTryParse(w8ght)) {

                    // Is it posiTive?
                    if (OotilityCeption.ParseInt(w8ght) >= 0) {

                        // Store weight
                        weight = OotilityCeption.ParseInt(w8ght);

                        // Crop
                        str = str.substring(0, spIndex);
                    }
                }
            }

            // Add
            contentsWithWeights.put(str, weight);
        }

        // Store name
        name = nmae;

        // Process Weights
        ProcessWeights();
    }

    /**
     * Will load a List Placeholder to the global hashmap to be retrieved with ListPlaceholder.Get();
     *
     * If one of the same name already exists, this will be ignored.
     *
     * @param lph Placeholder ye tryna load.
     */
    public static void Load(ListPlaceholder lph) {

        // Is it not loaed yet
        if (Get(lph.getName()) == null) {

            // Load
            loadedListPlaceholders.put(lph.getName(), lph);
        }
    }

    void ProcessWeights() {

        // Int totale
        totalWeight = 0;

        // Count all values
        for (Integer intgr : contentsWithWeights.values()) { totalWeight += intgr; }

        // Build Weighted Contents
        weightedContents = new HashMap<>();

        Integer builtWeight = 0;

        // Evaluate every string
        for (String str : contentsWithWeights.keySet()) {

            // Get Weight Observed
            int w = contentsWithWeights.get(str);

            // If weight not zero
            if (w > 0) {

                // Add thiss' weight to the current
                builtWeight += contentsWithWeights.get(str);

                // Include like thay
                weightedContents.put(builtWeight, str);
            }
        }
    }

    HashMap<Integer, Integer> currentOrder = new HashMap<>();

    /**
     * Will return the next list item every time this is called.
     * @param orderedIndex For support it being called from multiple places, so that they dont interfere. Each ordered index will keep track of its own 'current' number.
     */
    public String NextListItem(Integer orderedIndex) {

        // MUST have at least one entry I guess
        if (listedContents.size() == 0 ) { return "Empty List Placeholder";}
        currentOrder.putIfAbsent(orderedIndex, 0);

        // Get current
        int currI = currentOrder.get(orderedIndex);

        // Gets the current
        String curr = listedContents.get(currI);

        // Inkreases order by 1
        currI++;

        // Rests if too large
        if (currI >= contentsWithWeights.size()) { currI = 0; }

        // Store currI
        currentOrder.put(orderedIndex, currI);

        // Return
        return curr;
    }

    /**
     * Will return a random item of the list, respecting weights.
     */
    public String RandomListItem() {

        // MUST have at least one entry I guess
        if (weightedContents.size() == 0 ) { return "No placeholder has probability of happening";}

        // Int rand
        int rand = OotilityCeption.GetRandomInt(0, totalWeight);

        // Gets the correct
        String curr = null;
        while (rand <= totalWeight) {

            // Found something?
            if (weightedContents.get(rand) != null) {

                // Equal
                curr = weightedContents.get(rand);

                // Vreak
                break;
            }

            // Inkrease
            rand++;
        }

        // Return
        return curr;
    }
}
