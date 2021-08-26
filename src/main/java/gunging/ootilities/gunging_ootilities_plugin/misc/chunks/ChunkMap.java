package gunging.ootilities.gunging_ootilities_plugin.misc.chunks;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A chunk map for Bukkit Locations.
 *
 * @param <T> The type associated with this location
 */
public class ChunkMap<T> {

    @NotNull final HashMap<World, HashMap<CKS_Major, HashMap<CKS_Minor, HashMap<Location, T>>>> chunky = new HashMap<>();

    public ChunkMap() {}
    public ChunkMap(@Nullable HashMap<Location, T> transcribe) {

        // Include
        if (transcribe != null) { put(transcribe); }
    }

    /**
     * @param transcribe Add all these entries to the Chunk Map
     */
    public void put(@NotNull HashMap<Location, T> transcribe) {

        // Go through all of them
        for (Location l : transcribe.keySet()) {
            if (l == null) { continue; }

            // Add that
            put(l, transcribe.get(l));
        }
    }

    public ArrayList<T> getValues() {

        // A value to return
        ArrayList<T> ret = new ArrayList<>();

        // Through every world
        for (World w : chunky.keySet()) {
            HashMap<CKS_Major, HashMap<CKS_Minor, HashMap<Location, T>>> worldMap = chunky.get(w);

            // Through every major chunk
            for (CKS_Major M : worldMap.keySet()) {
                HashMap<CKS_Minor, HashMap<Location, T>> majorMap = worldMap.get(M);

                // Through every minor chunk
                for (CKS_Minor m : majorMap.keySet()) {
                    HashMap<Location, T> minorMap = majorMap.get(m);

                    // Through every location
                    for (Location l : minorMap.keySet()) { ret.add(minorMap.get(l)); }
                }
            }
        }

        // That's all the values, yes.
        return ret;
    }
    public ArrayList<ChunkMapEntry<T>> getEntries() {

        // A value to return
        ArrayList<ChunkMapEntry<T>> ret = new ArrayList<>();

        // Through every world
        for (World w : chunky.keySet()) {
            HashMap<CKS_Major, HashMap<CKS_Minor, HashMap<Location, T>>> worldMap = chunky.get(w);

            // Through every major chunk
            for (CKS_Major M : worldMap.keySet()) {
                HashMap<CKS_Minor, HashMap<Location, T>> majorMap = worldMap.get(M);

                // Through every minor chunk
                for (CKS_Minor m : majorMap.keySet()) {
                    HashMap<Location, T> minorMap = majorMap.get(m);

                    // Through every location
                    for (Location l : minorMap.keySet()) { ret.add(new ChunkMapEntry<>(minorMap.get(l), l, m, M)); }
                }
            }
        }

        // That's all the values, yes.
        return ret;
    }

    public void clear() { chunky.clear(); }

    /**
     * Links this thing to this location
     *
     * @param loc Location to save this thing at
     *
     * @param thing The thing to link to that location
     */
    public void put(@NotNull Location loc, @Nullable T thing) {

        // Prepare search values
        World world = loc.getWorld();
        CKS_Major major = CKS_Major.GetFrom(loc);
        CKS_Minor minor = CKS_Minor.GetFrom(loc);

        // Generate maps and get the correct arrays
        HashMap<CKS_Major, HashMap<CKS_Minor, HashMap<Location, T>>> worldMap = chunky.computeIfAbsent(world, k -> new HashMap<>());
        HashMap<CKS_Minor, HashMap<Location, T>> majorMap = worldMap.computeIfAbsent(major, k -> new HashMap<>());
        HashMap<Location, T> minorMap = majorMap.computeIfAbsent(minor, k -> new HashMap<>());

        // Put it there
        minorMap.put(loc, thing);
    }

    /**
     * @param entry Includes this entry
     */
    public void put(@NotNull ChunkMapEntry<T> entry) { put(entry.getLoc(), entry.getValue()); }
    /**
     * @param entries Includes these entries
     */
    public void put(@NotNull ArrayList<ChunkMapEntry<T>> entries) { for (ChunkMapEntry<T> cme : entries) { if (cme != null) { put(cme); } } }

    /**
     * @param loc Place to put a null entry
     */
    public void remove(@NotNull Location loc) { put(loc, null); }

    /**
     * Links this thing to this location
     *
     * @param loc Location to save this thing at
     *
     * @return The thing associated to this location
     */
    @Nullable public T get(@Nullable Location loc) {
        if (loc == null) { return null; }

        // Prepare search values
        World world = loc.getWorld();
        CKS_Major major = CKS_Major.GetFrom(loc);
        CKS_Minor minor = CKS_Minor.GetFrom(loc);

        // Generate maps and get the correct arrays
        HashMap<CKS_Major, HashMap<CKS_Minor, HashMap<Location, T>>> worldMap = chunky.computeIfAbsent(world, k -> new HashMap<>());
        HashMap<CKS_Minor, HashMap<Location, T>> majorMap = worldMap.computeIfAbsent(major, k -> new HashMap<>());
        HashMap<Location, T> minorMap = majorMap.computeIfAbsent(minor, k -> new HashMap<>());

        // Put it there
        return minorMap.get(loc);
    }
}
