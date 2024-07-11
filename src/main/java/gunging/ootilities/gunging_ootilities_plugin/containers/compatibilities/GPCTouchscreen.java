package gunging.ootilities.gunging_ootilities_plugin.containers.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GPCTouchscreen {

    /**
     * @param associatee Player firing events.
     * @return The events fired by this player this tick.
     */
    @NotNull public ArrayList<InventoryClickEvent> getEvents(@NotNull Player associatee) {

        // Clear feed if out of date
        if (getCurrentTick() != Gunging_Ootilities_Plugin.getCurrentTick()) { currentFeed.clear(); currentTick = Gunging_Ootilities_Plugin.getCurrentTick(); }

        // Add to feed
        return currentFeed.getOrDefault(associatee.getUniqueId(), new ArrayList<>());
    }

    /**
     * @param associatee Player firing this event
     * @param event Event being fired
     */
    public void addEvent(@NotNull Player associatee, @NotNull InventoryClickEvent event) {

        // Clear feed if out of date
        if (getCurrentTick() != Gunging_Ootilities_Plugin.getCurrentTick()) { currentFeed.clear(); currentTick = Gunging_Ootilities_Plugin.getCurrentTick(); }

        // Add to list
        ArrayList<InventoryClickEvent> current = currentFeed.get(associatee.getUniqueId());
        if (current == null) { current = new ArrayList<>(); }
        current.add(event);

        // Add to feed
        currentFeed.put(associatee.getUniqueId(), current);
    }

    /**
     * @return The tick currently in evaluation
     */
    public int getCurrentTick() { return currentTick; }
    int currentTick = -1;

    /**
     * @return The events fired this tick, per player
     */
    @NotNull public HashMap<UUID, ArrayList<InventoryClickEvent>> getCurrentFeed() { return currentFeed; }
    @NotNull final HashMap<UUID, ArrayList<InventoryClickEvent>> currentFeed = new HashMap<>();
}
