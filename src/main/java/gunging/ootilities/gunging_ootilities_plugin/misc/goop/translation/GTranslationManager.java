package gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GTranslationContainer;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GTL_Containers;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Binds translations to any enum yeah
 */
public class GTranslationManager {

    /**
     * Reload the translations yes, or something
     */
    public static void reloadTranslations() {

        // Fetch pair
        FileConfigPair translationsPair = Gunging_Ootilities_Plugin.theMain.translationsPair;
        if (translationsPair == null) { return; }

        // Anyway
        ctr.clear();
        ConfigurationSection containersSection = translationsPair.getStorage().getConfigurationSection("Containers");
        if (containersSection != null) {
            ct(GTL_Containers.BAGS_FIRST_OPEN_STACK, containersSection.getString("BagFirstOpenUnstackable", "&cTo open this %container%&c you must hold one %item% at a time. "));

            ct(GTL_Containers.GPROTECTION_BLOCK_NOT_CONTAINER, containersSection.getString("BlockNotContainer", "&cThis block is in the incorrect magical waves, try another one. "));
            ct(GTL_Containers.GPROTECTION_BLOCK_NOT_FOUND, containersSection.getString("BlockNotFound", "&cYou are not looking at anything, please look at a block. "));
            ct(GTL_Containers.GPROTECTION_PLAYER_NOT_FOUND, containersSection.getString("ProtectionPlayerNotFound", "&cPlayer &6%player%&c was not found. "));

            ct(GTL_Containers.GPROTECTION_NOT_MEMBER, containersSection.getString("ProtectionNotMember", "&cThis %container%&c is protected by a magical spell. "));
            ct(GTL_Containers.GPROTECTION_NOT_ADMIN, containersSection.getString("ProtectionNotAdmin", "&cYou need to be an admin of this %container%&c to do this. "));
            ct(GTL_Containers.GPROTECTION_NOT_OWNER, containersSection.getString("ProtectionNotOwner", "&cYou need to be the owner of this %container%&c to do this. "));

            ct(GTL_Containers.GREMOVE_EDITION_SUCCESS, containersSection.getString("ProtectionEdit", "&aSuccessfully edited protection of this %container%&a. "));
            ct(GTL_Containers.GREMOVE_REMOVE_SUCCESS, containersSection.getString("ProtectionRemove", "&aSuccessfully removed protection from this %container%&a. "));
            ct(GTL_Containers.GREMOVE_CREATE_SUCCESS, containersSection.getString("ProtectionCreate", "&aSuccessfully protected this %container%&a. "));
            ct(GTL_Containers.GREMOVE_NO_CHANGE, containersSection.getString("ProtectionNoChange", "&eNothing happened, the protection already was like that. "));

            ct(GTL_Containers.GMODIFY_REMOVE_MEMBER, containersSection.getString("ProtectionRemoveMember", "&cRemoved player &6%player%&c from this %container%. "));
            ct(GTL_Containers.GMODIFY_ADD_MEMBER, containersSection.getString("ProtectionAddMember", "&aAdded a new member &6%player%&a to this %container%. "));
            ct(GTL_Containers.GMODIFY_ADD_ADMIN, containersSection.getString("ProtectionAddAdmin", "&aAdded a new admin &6%player%&a to this %container%. "));
            ct(GTL_Containers.GMODIFY_CANNOT_REMOVE, containersSection.getString("ProtectionCannotRemove", "&eNothing happened, this person cannot be removed from this %container%. "));
            ct(GTL_Containers.GMODIFY_UNREGISTERED, containersSection.getString("ProtectionNotExist", "&eNothing happened, this %container% is unprotected. "));

            ct(GTL_Containers.GMODIFY_HELP_1, containersSection.getString("ProtectionHelp1", "&3/gmodify &7Allow other people to open your storage. "));
            ct(GTL_Containers.GMODIFY_HELP_2, containersSection.getString("ProtectionHelp2", "&7Usage: &e/gmodify <player> "));
            ct(GTL_Containers.GMODIFY_HELP_3, containersSection.getString("ProtectionHelp3", "&8 > &e@<player> &8will promote to admin."));
            ct(GTL_Containers.GMODIFY_HELP_4, containersSection.getString("ProtectionHelp4", "&8 > &e-<player> &8will remove that player."));

            ct(GTL_Containers.GINFO_TITLE, containersSection.getString("GInfoTitle", "&7&m====&7> &3%container%"));
            ct(GTL_Containers.GINFO_PHYSICAL_LOCATION, containersSection.getString("GInfoPhysicalLocation", "&7Location: &e%location%"));
            ct(GTL_Containers.GINFO_PHYSICAL_PROTECTION, containersSection.getString("GInfoPhysicalProtection", "&7Protection Type: &e%protection%"));
            ct(GTL_Containers.GINFO_PHYSICAL_MEMBERS_TITLE, containersSection.getString("GInfoPhysicalMembersTitle", "&7Members: "));
            ct(GTL_Containers.GINFO_PHYSICAL_MEMBER, containersSection.getString("GInfoPhysicalMember", " &e- &7%player%"));
            ct(GTL_Containers.GINFO_PHYSICAL_ADMIN, containersSection.getString("GInfoPhysicalAdmin", " &bADMIN &e- &7%player%"));
            ct(GTL_Containers.GINFO_PHYSICAL_OWNER, containersSection.getString("GInfoPhysicalOwner", " &3OWNER &e- &7%player%"));
            ct(GTL_Containers.GINFO_PERSONAL_OWNER, containersSection.getString("GInfoPersonalOwner", "&7Code UUID: &e%uuid%"));
            ct(GTL_Containers.GINFO_PERSONAL_NO_OWNER, containersSection.getString("GInfoPersonalNoOwner", "&7Code UUID: &eNone! This is brand-new!"));
            ct(GTL_Containers.GINFO_SEENS_TITLE, containersSection.getString("GInfoSeensTitle", "&7Opened History: "));
            ct(GTL_Containers.GINFO_SEEN, containersSection.getString("GInfoSeen", "&e%time% ago: &7%message%"));
        }
    }

    /**
     * @param key Translation Key
     *
     * @return Translation of this
     */
    @NotNull public static String c(@NotNull GTL_Containers key) { GTranslationContainer t = ctr.get(key); if (t == null) { return ""; } return t.getTranslation(); }
    @NotNull final static HashMap<GTL_Containers, GTranslationContainer> ctr = new HashMap<>();
    /**
     * @param key key
     * @param message message
     */
    public static void ct(@NotNull GTL_Containers key, @NotNull String message) { ctr.put(key, new GTranslationContainer(key, message)); }
}
