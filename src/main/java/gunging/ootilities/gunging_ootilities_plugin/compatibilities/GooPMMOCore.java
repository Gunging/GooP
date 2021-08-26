package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.api.player.stats.PlayerStats;
import net.Indyuce.mmocore.api.player.stats.StatType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GooPMMOCore {

    public GooPMMOCore() {}

    public void CompatibilityCheck() {

        // Attempt to load
        StatType test = StatType.ATTACK_DAMAGE;
    }

    public static double CummulativeDoubleStat(Player target, String statname, double base) {
        Double ret = CummulativeDoubleStat(target, statname);
        if (ret == null) { ret = base; } else { ret += base; }
        return ret;
    }

    public static Double CummulativeDoubleStat(Player target, String statname) {

        // Get Statt
        switch (statname.toLowerCase()) {
            case "attackdamage":
            case "adamage":
            case "admg":
            case "attack":
                return CummulativeDoubleStat(target, StatType.ATTACK_DAMAGE);
            case "attackspeed":
            case "aspeed":
                return CummulativeDoubleStat(target, StatType.ATTACK_SPEED);
            case "armor":
                return CummulativeDoubleStat(target, StatType.ARMOR);
            case "armortoughness":
            case "atoughness":
            case "toughness":
            case "atough":
            case "at":
                return CummulativeDoubleStat(target, StatType.ARMOR_TOUGHNESS);
            case "knockbackresistance":
            case "kresistance":
            case "knockback":
            case "kres":
                return CummulativeDoubleStat(target, StatType.KNOCKBACK_RESISTANCE);
            case "movementspeed":
            case "mspeed":
            case "speed":
                return CummulativeDoubleStat(target, StatType.MOVEMENT_SPEED);
            case "maxhealth":
            case "mhealth":
                return CummulativeDoubleStat(target, StatType.MAX_HEALTH);
            case "criticalstrikechance":
            case "critchance":
            case "criticalchance":
            case "critc":
                return CummulativeDoubleStat(target, StatType.CRITICAL_STRIKE_CHANCE);
            case "criticalstrikepower":
            case "critpower":
            case "critpow":
            case "criticalpower":
            case "critp":
                return CummulativeDoubleStat(target, StatType.CRITICAL_STRIKE_POWER);
            case "weapondamage":
                return CummulativeDoubleStat(target, StatType.WEAPON_DAMAGE);
            case "skilldamage":
                return CummulativeDoubleStat(target, StatType.SKILL_DAMAGE);
            case "projectiledamage":
            case "projdamage":
                return CummulativeDoubleStat(target, StatType.PROJECTILE_DAMAGE);
            case "magicdamage":
                return CummulativeDoubleStat(target, StatType.MAGIC_DAMAGE);
            case "physicaldamage":
            case "meleedamage":
                return CummulativeDoubleStat(target, StatType.PHYSICAL_DAMAGE);

                // MMOItems-Exclusives: Pass on to function
            case "luck":
            case "undeaddamage":
            case "misca":
            case "miscb":
            case "miscc":
                if (Gunging_Ootilities_Plugin.foundMMOItems) { return GooPMMOItems.CummaltiveEquipmentDoubleStatValue(target, statname); }
            default:

                // Attempt to get stat i guess
                try {

                    // Return Thay Stat
                    StatType stt = StatType.valueOf(statname.toUpperCase().replace(" ", "_").replace("-","_"));
                    return CummulativeDoubleStat(target, stt);

                } catch (IllegalArgumentException ignored) { }

                if (Gunging_Ootilities_Plugin.foundMMOItems) {

                    return GooPMMOItems.CummaltiveEquipmentDoubleStatValue(target, statname);
                }

                return null;
        }
    }

    public static Double CummulativeDoubleStat(Player target, StatType statName) {

        // Get Basic Organizational Classes
        PlayerData pData = PlayerData.get(target);
        PlayerStats pSats = pData.getStats();
        return pSats.getStat(statName);

        /*
        StatInstance pStat = pData.getStats().getInstance(statName);

        // Get Base
        double psTotal = pStat.getBase();
        OotilityCeption.Log("\u00a73Base as: \u00a7e" + psTotal);

        // Get Modifiers
        ArrayList<StatModifier> psMods = new ArrayList<>(pStat.getModifiers());
        OotilityCeption.Log("Applying Flat Modifiers");

        // For each modifier
        for (StatModifier psm : psMods) {

            if (psm.getType() == ModifierType.FLAT) {

                OotilityCeption.Log("\u00a7b+ \u00a77" + psm.getValue());
                psTotal += psm.getValue();
            }
        }
        OotilityCeption.Log("Applying Scaling Modifiers");

        // For each modifier
        for (StatModifier psm : psMods) {

            if (psm.getType() == ModifierType.RELATIVE) {

                OotilityCeption.Log("\u00a7b* \u00a77" + psm.getValue());
                psTotal *= (1.0D + (psm.getValue() * 0.01D));
            }
        }

        return psTotal; */
    }
}
