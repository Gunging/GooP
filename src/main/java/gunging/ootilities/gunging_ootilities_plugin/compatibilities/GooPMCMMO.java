package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPMCMMO_StatType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

public class GooPMCMMO {

    public void CompatibilityCheck() {

        // Attempt to load
        Double test = AdvancedConfig.getInstance().getDazeBonusDamage();
    }

    // For the dual-cummulativity of some stats
    public static Double MCMMODoubleStat(Player target, String arg) {

        // A value to return
        Double result = null;

        // MMOCore takes precedence (since it totals everything)
        if (Gunging_Ootilities_Plugin.foundMCMMO) {

            // Value from MMOCore
            result = GetMCMMODoubleStat(target, arg);

        } else {

            // Nothing
            result = 0.0;
        }

        // Final
        return result;
    }

    public static double GetMCMMODoubleStat(Player target, String statname, double base) {
        Double ret = GetMCMMODoubleStat(target, statname);
        if (ret == null) { ret = base; } else { ret += base; }
        return ret;
    }

    public static Double GetMCMMODoubleStat(Player target, String statname) {

        // NO NULL
        if (target == null) { return null; }

        // Get Statt
        switch (statname.toLowerCase()) {
            case "unarmed.iron_fist":
            case "unarmed.ironfist":
            case "unarmed.ironarm":
            case "unarmed.iron_arm":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.UNARMED_IRONFIST);
            case "unarmed.berserk":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.UNARMED_BERSERK);
            case "archery.skillshot":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.ARCHERY_SKILLSHOT);
            case "archery.daze":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.ARCHERY_DAZE);
            case "axes.mastery":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.AXES_MASTERY);
            case "axes.crit.pvp":
            case "axes.crit":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.AXES_CRIT_PVP);
            case "axes.crit.pve":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.AXES_CRIT_PVE);
            case "taming.sharp_claws":
            case "taming.sharpened_claws":
            case "taming.claws":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.TAMING_CLAWS);
            case "taming.gore":
                return GetMCMMODoubleStat(target, GooPMCMMO_StatType.TAMING_GORE);
            default:
                return null;
        }
    }

    public static Double GetMCMMODoubleStat(Player target, GooPMCMMO_StatType stat) {

        // NO NULL
        if (target == null) { return null; }

        // Processed Final Multiplier
        Double processedDamage = null;

        // GEt Managers
        McMMOPlayer agressor_McMMO = UserManager.getPlayer(target);
        ArcheryManager archery_ofAgressor = agressor_McMMO.getArcheryManager();
        AxesManager axes_ofAgressor = agressor_McMMO.getAxesManager();
        UnarmedManager unarmed_ofAgressor = agressor_McMMO.getUnarmedManager();
        TamingManager taming_ofAgressor = agressor_McMMO.getTamingManager();

        // Must look at each one damn separately lmao
        switch (stat) {
            default: processedDamage = null; break;
            //region Archery
            case ARCHERY_DAZE:

                // Roll for daze
                if (skillActivationChance(SecondaryAbility.DAZE, archery_ofAgressor, agressor_McMMO, SkillType.ARCHERY)) {

                    // Successful roll
                    processedDamage = AdvancedConfig.getInstance().getDazeBonusDamage();

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }

                break;
            case ARCHERY_SKILLSHOT:

                // Process the shit out of the double stats
                if (archery_ofAgressor.canSkillShot()) {

                    // Skill Shot Modified Damage
                    processedDamage = archery_ofAgressor.skillShot(1.0);

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }
                break;
                //endregion
            //region Axes
            case AXES_MASTERY:

                // Can it use axe master?
                if (axes_ofAgressor.canUseAxeMastery()) {

                    // Axe mastery
                    processedDamage = axes_ofAgressor.axeMastery();

                } else {

                    // Neuter Additive
                    processedDamage = 0.0;
                }
                break;
            case AXES_CRIT_PVE:

                // Roll for crit
                if (skillActivationChance(SecondaryAbility.CRITICAL_HIT, axes_ofAgressor, agressor_McMMO, SkillType.AXES)) {

                    // Successful roll
                    processedDamage = AdvancedConfig.getInstance().getCriticalHitPVEModifier();

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }

                break;
            case AXES_CRIT_PVP:

                // Roll for crit
                if (skillActivationChance(SecondaryAbility.CRITICAL_HIT, axes_ofAgressor, agressor_McMMO, SkillType.AXES)) {

                    // Successful roll
                    processedDamage = AdvancedConfig.getInstance().getCriticalHitPVPModifier();

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }

                break;
                //endregion
            //region Unarmed
            case UNARMED_IRONFIST:

                // Process the shit out of the double stats
                if (unarmed_ofAgressor.canUseIronArm()) {

                    // Skill Shot Modified Damage
                    processedDamage = unarmed_ofAgressor.ironArm();

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }
                break;
            case UNARMED_BERSERK:

                // Process the shit out of the double stats
                if (unarmed_ofAgressor.canUseBerserk()) {

                    // Skill Shot Modified Damage
                    processedDamage = unarmed_ofAgressor.berserkDamage(1.0);

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }
                break;
            //endregion
            //region Taming
            case TAMING_GORE:

                // Roll for daze
                if (skillActivationChance(SecondaryAbility.GORE, archery_ofAgressor, agressor_McMMO, SkillType.TAMING)) {

                    // Successful roll
                    processedDamage = AdvancedConfig.getInstance().getGoreModifier();

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }

                break;
            case TAMING_CLAWS:

                // Process the shit out of the double stats
                if (taming_ofAgressor.canUseSharpenedClaws()) {

                    // Skill Shot Modified Damage
                    processedDamage = taming_ofAgressor.sharpenedClaws();

                } else {

                    // Neuter Additve
                    processedDamage = 0.0;
                }
                break;
            //endregion
        }


        // Return Result
        return processedDamage;
    }

    public static boolean skillActivationChance(SecondaryAbility ability, SkillManager skillManager, McMMOPlayer mcMMOPlayer, SkillType parent) {
        return SkillUtils.activationSuccessful(ability, skillManager.getPlayer(), skillManager.getSkillLevel(), PerksUtils.handleLuckyPerks(mcMMOPlayer.getPlayer(), parent));
    }
}
