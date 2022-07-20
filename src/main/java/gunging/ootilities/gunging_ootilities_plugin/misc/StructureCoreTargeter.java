package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CSActivation;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CustomStructures;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;

import java.util.ArrayList;
import java.util.Collection;

public class StructureCoreTargeter extends ILocationSelector {
    boolean centered;
    public StructureCoreTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        centered = mlc.getBoolean(new String[]{"blockCenter", "centered", "center" , "c"}, true);
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata skillMetadata) {
        ArrayList<AbstractLocation> ret = new ArrayList<>();
        CSActivation activity = CustomStructures.getLastActivation(skillMetadata.getCaster().getEntity().getUniqueId());
        if (activity != null) {

            // Simple
            ret.add(BukkitAdapter.adapt(centered ? activity.getWhereClone().add(0.5, 0.5, 0.5) : activity.getWhere()));
        }

        return ret;
    }
}
