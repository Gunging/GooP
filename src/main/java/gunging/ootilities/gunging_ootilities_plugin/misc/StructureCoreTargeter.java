package gunging.ootilities.gunging_ootilities_plugin.misc;


import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSActivation;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSManager;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class StructureCoreTargeter extends ILocationSelector {
    boolean interaction;
    boolean centered;
    boolean all;
    public StructureCoreTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        centered = mlc.getBoolean(new String[]{"blockCenter", "centered", "center" , "c"}, true);
        interaction = mlc.getBoolean(new String[]{"interactedBlock", "interaction" , "interacted", "interact", "i"}, false);
        all = mlc.getBoolean(new String[]{"all", "allblocks", "ab", "fullstructure", "fs"}, false);
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata skillMetadata) {
        ArrayList<AbstractLocation> ret = new ArrayList<>();
        CSActivation activity = CSManager.getLastActivation(skillMetadata.getCaster().getEntity().getUniqueId());

        // If valid, target this location
        if (activity != null) {

            if (all) {

                // All blocks of the structure
                for (Block block : activity.getWhere().getWorldBlocks()) {

                    // Simply add all the blocks
                    ret.add(BukkitAdapter.adapt(central(clone(block.getLocation()))));
                }

            } else {

                // Get Location
                Location base;

                // Using the interacted or the core block?
                if (interaction) { base = activity.getWhereInteractedClone(); } else { base = activity.getWhereCoreClone(); }

                // Simple
                ret.add(BukkitAdapter.adapt(central(base)));
            }
        }

        return ret;
    }

    /**
     * @return Clone of location
     */
    @NotNull public Location clone(@NotNull Location loc) { return new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()); }

    /**
     * @param loc Input location (already cloned from original)
     *
     * @return If {@link #centered}, it returns the center of this block. Otherwise no changes.
     */
    @NotNull Location central(@NotNull Location loc) { return centered ? loc.add(0.5, 0.5, 0.5) : loc; }
}
