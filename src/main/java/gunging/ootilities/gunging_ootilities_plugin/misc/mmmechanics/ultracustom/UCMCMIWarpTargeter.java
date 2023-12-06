package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics.ultracustom;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPCMI;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPEssentials;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class UCMCMIWarpTargeter extends ILocationSelector {

    @NotNull PlaceholderString nominalWarp;

    public UCMCMIWarpTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        nominalWarp = mlc.getPlaceholderString(new String[] { "warp", "w", "name", "n" }, null);
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata skillMetadata) {

        // Collections
        HashSet<AbstractLocation> collectionLocations = new HashSet<>();

        // Get got
        String got = nominalWarp.get(skillMetadata);
        //CMI//OotilityCeption.Log("\u00a78UCM\u00a76 CMI\u00a77 Warp\u00a7e " + got);

        // I guess get the warp name
        Location loc = Gunging_Ootilities_Plugin.foundEssentials ? GooPEssentials.getWarp(got) : GooPCMI.getWarp(got);
        //CMI//OotilityCeption.Log("\u00a78UCM\u00a76 CMI\u00a77 Loc\u00a73 " + (loc != null ? OotilityCeption.BlockLocation2String(loc) : "null"));

        // Save location
        if (loc != null) { collectionLocations.add(BukkitAdapter.adapt(loc)); }

        // Adapt and return
        return collectionLocations;
    }
}
