package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GooPUnlockables {

    @NotNull
    public UUID getAssociatee() {
        return associatee;
    }

    @NotNull
    public String getGoalname() {
        return goalname;
    }

    //region As Instance
    UUID associatee;
    String goalname;
    boolean isLoaded = false;
    double unlocked = 0.0;
    public boolean IsUnlocked() { return unlocked != 0.0; }
    public boolean IsLoaded() { return isLoaded; }
    void SetUnlock() {
        /*DBG*/OotilityCeption.Log("\u00a77Goal \u00a7f" + getGoalname() + " for \u00a73" + getAssociatee().toString() + "\u00a77 now \u00a7aunlocked");
        SetUnlock(1.0D); }
    void SetLock() {
        /*DBG*/OotilityCeption.Log("\u00a77Goal \u00a7f" + getGoalname() + " for \u00a73" + getAssociatee().toString() + "\u00a77 now \u00a7clocked");
        unlocked = 0.0; }
    /**
     * Rather than as a boolean of Locked/Unlocked, this is considered
     * locked if this value is exactly zero.
     * <p></p>
     * If you try to set this to 0, it will be set to 0.0001 instead.
     */
    public void SetUnlock(double u) {
        if (u == 0) { u = 0.0001D; }
        /*DBG*/OotilityCeption.Log("\u00a77Goal \u00a7f" + getGoalname() + "  for \u00a73" + getAssociatee().toString() + "\u00a77 now \u00a7aunlocked\u00a77 at \u00a7b" + u);
        unlocked = u; }
    /**
     * Rather than as a boolean of Locked/Unlocked, this is considered
     * locked if this value is exactly zero.
     * <p></p>
     * Allows for more information I guess, otherwise why not just use scoreboard tags?
     */
    public double GetUnlock() { return unlocked; }
    /**
     * If too long has elapsed, it will become locked again.
     */
    public void CheckTimer() {

        // TImed?
        if (isTimed()) {

            /*DMG*/OotilityCeption.Log("\u00a76TM\u00a77 Timed, remaining: \u00a7b" + RemainingSeconds());

            // Check
            if (RemainingSeconds() < 0) {

                // Lock
                Lock();

                // Remove
                SetTimed(null);

                /*DMG*/OotilityCeption.Log("\u00a76TM\u00a77 Time ran out, \u00a7cLocked");
            }
        }
    }
    /**
     * Should this be saved across server reboots?
     */
    boolean persistent = true;
    /**
     * @param pers Should this be saved across server reboots?
     */
    public void setPersistent(boolean pers) { persistent = pers; }
    /**
     * @return Should this be saved across server reboots?
     */
    public boolean isPersistent() { return persistent; }

    @Nullable OptimizedTimeFormat timed;
    public boolean isTimed() { return timed != null; }
    public long RemainingSeconds() { return OotilityCeption.SecondsElapsedSince(OptimizedTimeFormat.Current(), timed); }
    public void SetTimed(@Nullable OptimizedTimeFormat timee) { SetTimed(timee, false); }
    public void SetTimed(@Nullable OptimizedTimeFormat timee, boolean keepLonger) {

        // Keeps longest time duration
        if(keepLonger) {

            // Forever
            if (timed == null || timee == null) { timed = null; return; }

            // Alr choose lengthest
            if (OotilityCeption.SecondsElapsedSince(timed, timee) > 0) {

                // Positive time has elapsed - new one is longer
                timed = timee;

            }

        } else {

            timed = timee;
        }
    }
    @Nullable public OptimizedTimeFormat GetTimed() { return timed; }

    public GooPUnlockables(@NotNull UUID linq, @NotNull String goal) {
        associatee = linq;
        goalname = goal;
    }

    public void Unlock() {

        // Unlock this lma0
        Unlock(this);
    }
    public void Lock() {

        // Lock this lma0
        Lock(this);
    }
    public void Save() {

        // Save this lma0
        Save(this);
    }
    public void Load() {
        Load(this);
    }
    //endregion

    //region As Manager
    static HashMap<UUID, ArrayList<GooPUnlockables>> playerUnlocks = new HashMap<>();
    static HashMap<String, HashMap<UUID, GooPUnlockables>> playerUnlocksReverse = new HashMap<>();

    /**
     * Loads an unlockable, fails if there is alraedy one of the same name associated to the same UUID.
     */
    public static void Load(@NotNull GooPUnlockables uck) {

        // If not registered yet
        if (Get(uck.getAssociatee(), uck.getGoalname()) == null) {
            uck.isLoaded = true;

            // Register
            ArrayList<GooPUnlockables> pUCK = playerUnlocks.get(uck.getAssociatee());
            if (pUCK == null) { pUCK = new ArrayList<>(); } pUCK.add(uck);
            playerUnlocks.put(uck.getAssociatee(), pUCK);

            // Getbase
            HashMap<UUID, GooPUnlockables> perUCK = playerUnlocksReverse.get(uck.getGoalname());
            if (perUCK == null) { perUCK = new HashMap<>(); } perUCK.put(uck.getAssociatee(), uck);
            playerUnlocksReverse.put(uck.getGoalname(), perUCK);
        }
    }

    /**
     * Returns such an unlockable if it is defined.
     */
    @Nullable
    public static GooPUnlockables Get(@NotNull UUID whom, @NotNull String goal) {

        // Find in reverse linq
        HashMap<UUID, GooPUnlockables> perUCK = playerUnlocksReverse.get(goal);

        // Existed?
        if (perUCK != null) {

            // Return thay
            return perUCK.get(whom);
        }

        // Bot found
        return null;
    }

    /**
     * Returns such an unlockable. It must be loaded to be found by this method.
     * <p></p>
     * If it doesnt find it, it will create and load automatically.
     */
    @NotNull
    public static GooPUnlockables From(@NotNull UUID whom, @NotNull String goal) {

        // Find in reverse linq
        HashMap<UUID, GooPUnlockables> perUCK = playerUnlocksReverse.get(goal);

        // Ret
        GooPUnlockables ret = null;

        // Existed?
        if (perUCK != null) {

            // Return thay
            ret = perUCK.get(whom);
        }

        // Bot found
        if (ret == null) {

            //Create
            ret = new GooPUnlockables(whom, goal);

            // Load
            ret.Load();
        }

        // Return
        return ret;
    }

    /**
     * The target will now have this unlocked. Saves.
     * <p></p>
     * If the target doesnt have it registered, it will register a new one and unlock it.
     */
    public static void Unlock(@NotNull UUID whoms, @NotNull String goalname) {

        // Get
        GooPUnlockables uck = From(whoms, goalname);

        // Unlock
        Unlock(uck);
    }

    /**
     * Marks it as unlocked. Saves.
     */
    public static void Unlock(@NotNull GooPUnlockables uck) {

        // Unlock
        uck.SetUnlock();
    }

    /**
     * The target will now have this unlocked. Saves.
     * <p></p>
     * If the target doesnt have it registered, it will register a new one and unlock it.
     */
    public static void Lock(@NotNull UUID whoms, @NotNull String goalname) {

        // Get
        GooPUnlockables uck = From(whoms, goalname);

        // Unlock
        Lock(uck);
    }

    /**
     * Marks it as locked. Saves.
     */
    public static void Lock(@NotNull GooPUnlockables uck) {

        // Unlock
        uck.SetLock();
    }

    public static void SaveAll() {
        for (ArrayList<GooPUnlockables> loadeds : playerUnlocks.values()) {

            for (GooPUnlockables loaded : loadeds) {

                Save(loaded);
            }
        }

        //region EZ MM Skill Writer LMA0
        /*
        // Get Source File Pair
        FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.goopUnlockables;
        ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

        // Modify Storage
        YamlConfiguration ofgStorage = ofgPair.getStorage();

        int min = 18, max = 171;

        ArrayList<String> huH = new ArrayList<>();

        for (int i = min; i <= max; i++) {

            huH.add("TAS_" + i + ":");
            huH.add("  Conditions:");
            huH.add("  - variableInRange{var=caster.tas_track;value=>" + (i - 1) + "}");
            huH.add("  - variableInRange{var=caster.tas_track;value=<" + (i + 1) + "}");
            huH.add("  Skills:");
            huH.add("  - variableMath{var=caster.tas_track;equation=@<caster.var.tas_track> + 1@}");
            huH.add("  - sound{s=dodoo.angrasakura.s_" + i + ";p=1.0;v=4.0}");
        }

        ofgStorage.set("ANGRA_MM_SKILLSET", huH);

        // Save
        Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);
        //*/
        //endregion

    }
    /**
     * Saves persistently this data.
     */
    public static void Save(@NotNull GooPUnlockables uck) {

        // If loaded and persistent
        if (uck.IsLoaded() && uck.isPersistent()) {

            // Get Source File Pair
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.goopUnlockables;
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();
            String res;
            // Buold storage
            if (uck.GetUnlock() != 0) {

                StringBuilder fin = new StringBuilder(String.valueOf(uck.GetUnlock()));
                if (uck.isTimed()) { fin.append(" ").append(OptimizedTimeFormat.toString(uck.GetTimed())); }
                res = fin.toString();

            // Null if locked = It does not save in files
            } else { res = null; }
            /*DBG*/OotilityCeption.Log("\u00a77Saved goal \u00a73" + uck.getGoalname() + "\u00a77 for \u00a7e" + uck.getAssociatee() + "\u00a77 as \u00a7b" + res);

            // Set Location in File
            ofgStorage.set(uck.getGoalname() + "." + uck.getAssociatee().toString(), res);

            // Save
            Gunging_Ootilities_Plugin.theMain.SaveFile(ofgPair);
        }
    }

    @Override
    public String toString() {
        return getGoalname() + " \u00a7b" + IsUnlocked();
    }

    public static void Reload(@NotNull OotilityCeption theOots) {

        // B
        if (Gunging_Ootilities_Plugin.theMain.goopUnlockables != null) {

            // Clear arrays
            playerUnlocks.clear();
            playerUnlocksReverse.clear();

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.goopUnlockables;
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Log da shit
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()) {

                // Get Goal Name
                String tName = val.getKey();
                /*DBG*/OotilityCeption.Log("Looking at \u00a73" + tName);

                // Get them UUIDs
                ArrayList<String> thisseUUIDs = new ArrayList<>();

                // Get Configuration Section?
                ConfigurationSection ofgSection = ofgStorage.getConfigurationSection(tName);

                // Found=
                if (ofgSection != null) {

                    // GEt
                    for(Map.Entry<String, Object> bal : (ofgSection.getValues(false)).entrySet()) {

                        // Log
                        /*DBG*/OotilityCeption.Log("\u00a7b + \u00a77" + bal.getKey());

                        // Add I guess?
                        thisseUUIDs.add(bal.getKey());
                    }
                }

                // Get test existance and load
                for (String uid : thisseUUIDs) {

                    // Get double
                    String unlock  = ofgStorage.getString(tName +"." + uid);
                    OptimizedTimeFormat limitant = null;
                    Double unlocc = null;

                    // Is it a simple double?
                    if (unlock != null) {
                        int spindex = unlock.indexOf(" ");
                        if (spindex >= 0) {

                            // Sibstringa t first
                            String opt = unlock.substring(spindex + 1);
                            String doub = unlock.substring(0, spindex);

                            // Opt get
                            limitant = OptimizedTimeFormat.Convert(opt);

                            // try parse
                            if (OotilityCeption.DoubleTryParse(doub)) {

                                // Ey
                                unlocc = Double.parseDouble(doub);
                            }

                        // Was attempting to be a simple double
                        } else {

                            // try parse
                            if (OotilityCeption.DoubleTryParse(unlock)) {

                                // Ey
                                unlocc = Double.parseDouble(unlock);
                            }
                        }
                    }
                    if (unlocc == null) { unlocc = 0D; }

                    UUID uiddd = OotilityCeption.UUIDFromString(uid);

                    // Valid?
                    if (uiddd != null) {

                        // Set
                        GooPUnlockables uck = new GooPUnlockables(uiddd, tName);
                        uck.SetUnlock(unlocc);
                        uck.SetTimed(limitant);
                        uck.Load();

                        // Notifiy
                        /*DBG*/OotilityCeption.Log("Loaded \u00a7e" + tName + " " + unlocc + " \u00a73" + uiddd.toString());
                    }
                }
            }
        }
    }

    @NotNull public static ArrayList<String> GetKnownGoals() { return new ArrayList<>(playerUnlocksReverse.keySet()); }

    @NotNull public static ArrayList<GooPUnlockables> getRegisteredTo(@NotNull UUID uuid) { ArrayList<GooPUnlockables> uck = playerUnlocks.get(uuid); return uck == null ? new ArrayList<>() : uck; }
    //endregion

    //region True Functionlity
    public static boolean HasUnlocked(@NotNull UUID who, @NotNull String goalName) {

        // Get
        GooPUnlockables uck = Get(who, goalName);

        // Found?
        if (uck != null) {

            // Test for timer
            uck.CheckTimer();

            // Return value
            return uck.IsUnlocked();
        }

        // No
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GooPUnlockables)) { return false; }

        if (!((GooPUnlockables) obj).getAssociatee().equals(getAssociatee())) { return false; }

        if (((GooPUnlockables) obj).isTimed()) {

            if (!((GooPUnlockables) obj).GetTimed().equals(GetTimed())) { return false; }
        }

        return ((GooPUnlockables) obj).IsUnlocked() == IsUnlocked();
    }

    //endregion
}
