package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.GooPGriefEvent;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class RebootBreak extends SkillMechanic implements ITargetedLocationSkill {

    @Nullable PlaceholderString rebootKey, miningItem;

    public RebootBreak(CustomMechanic manager, String skill, MythicLineConfig mlc) {
        super(manager.getManager(), manager.getFile(), skill, mlc);
        construct(mlc);
    }
    public RebootBreak(SkillExecutor manager, String skill, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        construct(mlc);
    }

    void construct(MythicLineConfig mlc) {
        rebootKey = mlc.getPlaceholderString(new String[]{"rebootKey", "rk"}, null);
        miningItem = mlc.getPlaceholderString(new String[]{"miningItem", "mi"}, null);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {

        // Whatever this means
        Location loc = abstractLocation.toPosition().toLocation();

        // Find block
        Block block = loc.getBlock();

        // Reboot break
        GooPGriefEvent.rebootBreak(block, rebootKey == null ? null : rebootKey.get(skillMetadata));

        // Break block
        block.breakNaturally(miningItem == null ? null : OotilityCeption.ItemFromNBTTestString(miningItem.get(skillMetadata), null));

        // Yeah
        return SkillResult.SUCCESS;
    }
}
