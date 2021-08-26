package gunging.ootilities.gunging_ootilities_plugin.misc;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The parameters stored to make minions work.
 */
public class SummonerClassMinion {

    //region Entities Themselves
    /**
     * Entity this minion represents
     */
    Entity minion;
    /**
     * Entity this minion represents
     */
    @NotNull public Entity getMinion() { return minion; }

    //region Weight
    /**
     * Usually, minions weigh 1, but I guess some bigger minions could use more than one summon slot.
     */
    double weight = 1.0;
    /**
     * Usually, minions weigh 1, but I guess some bigger minions could use more than one summon slot.
     */
    public double getWeight() { return weight; }
    /**
     * Usually, minions weigh 1, but I guess some bigger minions could use more than one summon slot.
     * <p></p>
     * I guess negative weight minions actually allow more minions to be summoned :thinking:
     */
    public void setWeight(double w) { weight = w; }
    //endregion

    //region Limit
    /**
     * How many minions of the same kind can the user have.
     */
    Integer limit = null;

    /**
     * How many minions of the same kind can the user have.
     */
    public Integer getLimit() { return limit; }

    /**
     * How many minions of the same kind can the user have.
     * <p></p>
     * Note that negative values make no sense
     */
    public void setLimit(Integer l) { limit = l; }

    /**
     * How many minions of the same kind can the user have.
     */
    public boolean hasLimit() { return limit != null; }
    //endregion

    //region Kind
    /**
     * What 'kind' of minion this is.
     * <p></p>
     * Used to restrict the number of minions of the same kind a player can have.
     * <p></p>
     * May also be used to target them via the Mythic Skills targetter
     */
    String kind = null;

    /**
     * What 'kind' of minion this is.
     * <p></p>
     * Used to restrict the number of minions of the same kind a player can have.
     * <p></p>
     * May also be used to target them via the Mythic Skills targetter
     */
    public String getKind() { return kind; }

    /**
     * What 'kind' of minion this is.
     * <p></p>
     * Used to restrict the number of minions of the same kind a player can have.
     * <p></p>
     * May also be used to target them via the Mythic Skills targetter
     */
    public void setKind(String k) { kind = k; }

    /**
     * Does this minion have a 'kind' defined?
     * <p></p>
     * Used to restrict the number of minions of the same kind a player can have.
     * <p></p>
     * May also be used to target them via the Mythic Skills targetter
     */
    public boolean hasKind() { return kind != null; }
    //endregion

    /**
     * Owner of thay entity.
     */
    Entity owner;
    /**
     * Owner of thay entity.
     */
    @NotNull public Entity getOwner() { return owner; }
    //endregion

    //region Status
    /**
     * If it has been enabled. Will remain true until disabled.
     */
    boolean enabled;
    /**
     * If it has been enabled. Will remain true until disabled.
     */
    public boolean isEnabled() { return enabled; }
    public void SCM_Enable() { enabled = true; }
    public void SCM_Disable() { enabled = false; }

    /**
     * The scoreboard tag that characterizes all minions
     */
    public static final String minionTag = "GooP_Minion";
    //endregion

    //region Options
    /**
     * Distance before the minion forcefully teleports to owner.
     * <p></p>
     * The check happens every 10 seconds.
     */
    double leashrange = 20;
    /**
     * Distance before the minion forcefully teleports to owner.
     * <p></p>
     * The check happens every 10 seconds.
     */
    public double getLeashRange() { return leashrange; }
    /**
     * Distance before the minion forcefully teleports to owner.
     * <p></p>
     * The check happens every 10 seconds.
     */
    public void setLeashRange(double range) { if (range < 0) { range = 0;} leashrange = range; }

    /**
     * MythicMobs skill to run when the entity is removed.
     */
    String skillOnRemove = null;
    /**
     * MythicMobs skill to run when the entity is removed.
     */
    @Nullable public String getSkillOnRemove() { return skillOnRemove; }
    /**
     * MythicMobs skill to run when the entity is removed.
     */
    public boolean hasSkillOnRemove() { return skillOnRemove != null; }
    /**
     * MythicMobs skill to run when the entity is removed.
     */
    public void setSkillOnRemove(String skor) { skillOnRemove = skor; }
    //endregion

    /**
     * Creates a new Summoner-Minion relationship.
     * <p>Minions die when their summoner dies, or leaves the server.</p>
     * <p></p>
     * Remeber to <code>Enable()</code>
     * @param asOwner The summoner
     * @param asMinion The Minion
     * @throws IllegalArgumentException If the minion is a Player.
     */
    public SummonerClassMinion(@NotNull Entity asOwner, @NotNull Entity asMinion) throws IllegalArgumentException {

        // Exceptions ew
        if (asMinion instanceof Player) { throw new IllegalArgumentException("Players cannot be minions!"); }

        // Set
        owner = asOwner;
        minion = asMinion;
    }

    /**
     * Will register this minion in the ticking teleportation events and such.
     */
    public void Enable() {

        // Just add lmao
        SummonerClassUtils.EnableMinion(this);

        // Add that OP tag
        getMinion().addScoreboardTag(minionTag);
    }

    /**
     * Will destroy the minion. Usually called when the owner dies.
     * <p></p>
     * If a mythic skill is defined, this will not actually kill the minion,
     * just unregistering from being considered so. I leave to the skill to
     * kill the entity.
     * <p></p>
     * <b>Will also unregister this minion.</b>
     */
    public void Kill() { Kill(false); }

    /**
     * Will destroy the minion. Usually called when the owner dies.
     * <p></p>
     * If a mythic skill is defined, this will not actually kill the minion,
     * just unregistering from being considered so. I leave to the skill to
     * kill the entity.
     * <p></p>
     * <b>Will also unregister this minion.</b>
     * @param abruptly Should ignore mythic skills and just forcibly remove the minion from existance?
     */
    public void Kill(boolean abruptly) {

        // Unregister
        SummonerClassUtils.DisableMinion(this);

        // Test for mythic skill
        if (hasSkillOnRemove() && Gunging_Ootilities_Plugin.foundMythicMobs && !abruptly) {

            // Run Mythic Skill. Minion = Caster | Owner = Trigger |  Minion Location = Origin
            GooPMythicMobs.ExecuteMythicSkillAs(getSkillOnRemove(), getMinion(), getOwner(), getOwner().getLocation());

            // Remove tag
            getMinion().removeScoreboardTag(minionTag);

        // No Mythic Skill on Remvoe
        } else {

            // Kill or remove
            getMinion().remove();
        }
    }

    /**
     * Will be true if the owner is ok.
     */
    public boolean isOwnerValid() { return getOwner().isValid() && !getOwner().isDead(); }

    /**
     * Will be true if the minion is ok.
     */
    public boolean isMinionValid() {
        return !getMinion().isDead() && !getOwner().isDead();  }

    /**
     * Is that already a minion?
     */
    public static boolean isMinion(@Nullable Entity potentialMinion) {

        // If null no lol
        if (potentialMinion == null) { return false; }

        // Check scoreboard tags
        return potentialMinion.getScoreboardTags().contains(minionTag);
    }
}
