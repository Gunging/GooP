package gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.sources;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.type.CSMSString;
import gunging.ootilities.gunging_ootilities_plugin.misc.Orientations;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.blockmeta.data.CSMString;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * <b>Requires Paper Spigot!</b>
 */
public class CSMSPlayerHead extends CSMSString {

    /**
     * This flag will have its corresponding {@link CSMString}
     * for the texture of this head yeah.
     */
    public CSMSPlayerHead() { super("SKULL"); }

    @Override @NotNull
    public Block apply(@NotNull Block block, @NotNull CSMString meta, @NotNull Orientations inRelativeTo) {
        BlockState input = block.getState();
        //TEX// OotilityCeption.Log("\u00a78APPLY \u00a7bSK\u00a77 Skull Eval\u00a73 " + block.getType());

        // Block data bisected??
        if (!(input instanceof Skull)) {
            //TEX// OotilityCeption.Log("\u00a78APPLY \u00a7bSK\u00a77 Not skull");
            return block; }
        Skull skull = (Skull) input;

        // Player skull right
        if (skull.getType() != Material.PLAYER_HEAD && skull.getType() != Material.PLAYER_WALL_HEAD) {
            //TEX// OotilityCeption.Log("\u00a78APPLY \u00a7bSK\u00a77 Not player skull");
            return block; }

        // Create profile with texture
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", meta.getValue()));

        // Attempt to apply
        try {

            // Set skull value
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);

        }catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }

        // Update data
        skull.update();

        // Yeah
        return block;
    }

    @Override @Nullable
    public CSMString fromBlock(@NotNull Block block, @NotNull Orientations inRelativeTo) {
        BlockState input = block.getState();
        //TEX// OotilityCeption.Log("\u00a78READ \u00a7bSK\u00a77 Skull Eval\u00a73 " + block.getType());

        // Block data bisected??
        if (!(input instanceof Skull)) {
            //TEX// OotilityCeption.Log("\u00a78READ \u00a7bSK\u00a77 Not skull");
            return null; }

        // Get Profile
        String tex = getTexture((Skull) input);

        // Invalid -> no match
        if (tex == null) {
            //TEX// OotilityCeption.Log("\u00a78READ \u00a7bSK\u00a77 No texture");
            return null; }

        // Return data, where 'true' means its the bottom half.
        return new CSMString(tex);
    }

    @Override
    public boolean matches(@NotNull Block block, @NotNull CSMString meta, @NotNull Orientations inRelativeTo) {
        BlockState input = block.getState();
        //TEX// OotilityCeption.Log("\u00a78MATCH \u00a7bSK\u00a77 Skull Eval\u00a73 " + block.getType());

        // Cannot hve this data, no match.
        if (!(input instanceof Skull)) {
            //TEX// OotilityCeption.Log("\u00a78MATCH \u00a7bSK\u00a77 Not skull");
            return false; }

        // Get Profile
        String tex = getTexture((Skull) input);

        // Invalid -> no match
        if (tex == null) {
            //TEX// OotilityCeption.Log("\u00a78MATCH \u00a7bSK\u00a77 No texture");
            return false; }

        // Compare
        return tex.equals(meta.getValue());
    }

    @Nullable public static String getTexture(Skull skull) {

        // Player skull right
        if (skull.getType() != Material.PLAYER_HEAD && skull.getType() != Material.PLAYER_WALL_HEAD) {
            //TEX// OotilityCeption.Log("\u00a78CSMeta \u00a7bSK\u00a77 No player skull");
            return null; }

        // Get Profile
        PlayerProfile profile = skull.getPlayerProfile();

        // Invalid -> no match
        if (profile == null) {
            //TEX// OotilityCeption.Log("\u00a78CSMeta \u00a7bSK\u00a77 No player profile");
            return null; }

        // Find profile
        for (ProfileProperty property : profile.getProperties()) {
            //TEX// OotilityCeption.Log("\u00a78CSMeta \u00a7bSK\u00a7a +\u00a77 Property\u00a7b " + (property == null ? "null" : property.getName()));

            // Is it the texture one?
            if (property == null || !property.getName().equals("textures")) { continue; }

            // Good one
            return property.getValue();
        }
        //TEX// OotilityCeption.Log("\u00a78CSMeta \u00a7bSK\u00a77 No texture found");

        // No data
        return null;
    }
}