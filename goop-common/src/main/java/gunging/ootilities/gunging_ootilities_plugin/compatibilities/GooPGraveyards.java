package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooPVersionPotionEffects;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Bridge to Lumine's Graveyards plugin, used when GooP prevents a death and has to "respawn" the
 * player at their graveyard instead.
 * <p>
 * Graveyards is a premium plugin without a public API artifact, so it is reached through
 * reflection on the {@code Graveyard} class. Missing classes or methods simply disable the bridge.
 */
public class GooPGraveyards {

    private static final String GRAVEYARD_CLASS = "io.lumine.xikage.graveyards.Graveyard";

    public GooPGraveyards() { }

    /**
     * Throws if the installed Graveyards does not expose what this bridge needs, so the caller
     * leaves the compatibility disabled.
     */
    public void CompatibilityCheck() throws ReflectiveOperationException {
        Class<?> graveyard = Class.forName(GRAVEYARD_CLASS);
        graveyard.getMethod("getNearestGraveyard", Player.class);
        graveyard.getMethod("getSpawnLocation");
        graveyard.getMethod("getImmunityTicks");
    }

    public static void SimulateGraveyardsRespawn(Player gPlayer) {
        Object gGrave = invokeStatic("getNearestGraveyard", gPlayer);
        if (gGrave == null) { return; }

        // Respawn immunity
        Object immunity = invoke(gGrave, "getImmunityTicks");
        if (immunity instanceof Number && ((Number) immunity).intValue() > 0) {
            PotionEffectType resistance = GooP_MinecraftVersions.GetVersionPotionEffect(GooPVersionPotionEffects.DAMAGE_RESISTANCE);
            if (resistance != null) { gPlayer.addPotionEffect(new PotionEffect(resistance, ((Number) immunity).intValue(), 100)); }
        }

        // Respawn skill
        if (Gunging_Ootilities_Plugin.foundMythicMobs) {
            Object skill = invoke(gGrave, "getMythicSkill");
            if (skill instanceof Optional && ((Optional<?>) skill).isPresent()) {
                GooPMythicMobs.GraveyardsRespawnSkill(String.valueOf(((Optional<?>) skill).get()), gPlayer.getLocation(), gPlayer);
            }
        }

        Object spawn = invoke(gGrave, "getSpawnLocation");
        if (spawn instanceof Location) { gPlayer.teleport((Location) spawn, PlayerTeleportEvent.TeleportCause.PLUGIN); }
    }

    @Nullable private static Object invokeStatic(@NotNull String method, @NotNull Player player) {
        try {
            Method m = Class.forName(GRAVEYARD_CLASS).getMethod(method, Player.class);
            return m.invoke(null, player);
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

    @Nullable private static Object invoke(@NotNull Object target, @NotNull String method) {
        try {
            return target.getClass().getMethod(method).invoke(target);
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }
}
