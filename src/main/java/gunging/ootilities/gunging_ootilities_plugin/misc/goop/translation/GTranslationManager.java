package gunging.ootilities.gunging_ootilities_plugin.misc.goop.translation;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GTranslationContainer;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.GTL_Containers;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.GTL_SummonerClass;
import gunging.ootilities.gunging_ootilities_plugin.misc.GTranslationSummoner;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        ct(GTL_Containers.BAGS_FIRST_OPEN_STACK, fromSection(containersSection,"BagFirstOpenUnstackable", "&cTo open this %container%&c you must hold one %item% at a time. "));

        ct(GTL_Containers.GPROTECTION_BLOCK_NOT_CONTAINER, fromSection(containersSection,"BlockNotContainer", "&cThis block is in the incorrect magical waves, try another one. "));
        ct(GTL_Containers.GPROTECTION_BLOCK_NOT_FOUND, fromSection(containersSection,"BlockNotFound", "&cYou are not looking at anything, please look at a block. "));
        ct(GTL_Containers.GPROTECTION_PLAYER_NOT_FOUND, fromSection(containersSection,"ProtectionPlayerNotFound", "&cPlayer &6%player%&c was not found. "));

        ct(GTL_Containers.GPROTECTION_NOT_MEMBER, fromSection(containersSection,"ProtectionNotMember", "&cThis %container%&c is protected by a magical spell. "));
        ct(GTL_Containers.GPROTECTION_NOT_ADMIN, fromSection(containersSection,"ProtectionNotAdmin", "&cYou need to be an admin of this %container%&c to do this. "));
        ct(GTL_Containers.GPROTECTION_NOT_OWNER, fromSection(containersSection,"ProtectionNotOwner", "&cYou need to be the owner of this %container%&c to do this. "));

        ct(GTL_Containers.GREMOVE_EDITION_SUCCESS, fromSection(containersSection,"ProtectionEdit", "&aSuccessfully edited protection of this %container%&a. "));
        ct(GTL_Containers.GREMOVE_REMOVE_SUCCESS, fromSection(containersSection,"ProtectionRemove", "&aSuccessfully removed protection from this %container%&a. "));
        ct(GTL_Containers.GREMOVE_CREATE_SUCCESS, fromSection(containersSection,"ProtectionCreate", "&aSuccessfully protected this %container%&a. "));
        ct(GTL_Containers.GREMOVE_NO_CHANGE, fromSection(containersSection,"ProtectionNoChange", "&eNothing happened, the protection already was like that. "));

        ct(GTL_Containers.GMODIFY_REMOVE_MEMBER, fromSection(containersSection,"ProtectionRemoveMember", "&cRemoved player &6%player%&c from this %container%. "));
        ct(GTL_Containers.GMODIFY_ADD_MEMBER, fromSection(containersSection,"ProtectionAddMember", "&aAdded a new member &6%player%&a to this %container%. "));
        ct(GTL_Containers.GMODIFY_ADD_ADMIN, fromSection(containersSection,"ProtectionAddAdmin", "&aAdded a new admin &6%player%&a to this %container%. "));
        ct(GTL_Containers.GMODIFY_CANNOT_REMOVE, fromSection(containersSection,"ProtectionCannotRemove", "&eNothing happened, this person cannot be removed from this %container%. "));
        ct(GTL_Containers.GMODIFY_UNREGISTERED, fromSection(containersSection,"ProtectionNotExist", "&eNothing happened, this %container% is unprotected. "));

        ct(GTL_Containers.GMODIFY_HELP_1, fromSection(containersSection,"ProtectionHelp1", "&3/gmodify &7Allow other people to open your storage. "));
        ct(GTL_Containers.GMODIFY_HELP_2, fromSection(containersSection,"ProtectionHelp2", "&7Usage: &e/gmodify <player> "));
        ct(GTL_Containers.GMODIFY_HELP_3, fromSection(containersSection,"ProtectionHelp3", "&8 > &e@<player> &8will promote to admin."));
        ct(GTL_Containers.GMODIFY_HELP_4, fromSection(containersSection,"ProtectionHelp4", "&8 > &e-<player> &8will remove that player."));

        ct(GTL_Containers.GINFO_TITLE, fromSection(containersSection,"GInfoTitle", "&7&m====&7> &3%container%"));
        ct(GTL_Containers.GINFO_PHYSICAL_LOCATION, fromSection(containersSection,"GInfoPhysicalLocation", "&7Location: &e%location%"));
        ct(GTL_Containers.GINFO_PHYSICAL_PROTECTION, fromSection(containersSection,"GInfoPhysicalProtection", "&7Protection Type: &e%protection%"));
        ct(GTL_Containers.GINFO_PHYSICAL_MEMBERS_TITLE, fromSection(containersSection,"GInfoPhysicalMembersTitle", "&7Members: "));
        ct(GTL_Containers.GINFO_PHYSICAL_MEMBER, fromSection(containersSection,"GInfoPhysicalMember", " &e- &7%player%"));
        ct(GTL_Containers.GINFO_PHYSICAL_ADMIN, fromSection(containersSection,"GInfoPhysicalAdmin", " &bADMIN &e- &7%player%"));
        ct(GTL_Containers.GINFO_PHYSICAL_OWNER, fromSection(containersSection,"GInfoPhysicalOwner", " &3OWNER &e- &7%player%"));
        ct(GTL_Containers.GINFO_PERSONAL_OWNER, fromSection(containersSection,"GInfoPersonalOwner", "&7Code UUID: &e%uuid%"));
        ct(GTL_Containers.GINFO_PERSONAL_NO_OWNER, fromSection(containersSection,"GInfoPersonalNoOwner", "&7Code UUID: &eNone! This is brand-new!"));
        ct(GTL_Containers.GINFO_SEENS_TITLE, fromSection(containersSection,"GInfoSeensTitle", "&7Opened History: "));
        ct(GTL_Containers.GINFO_SEEN, fromSection(containersSection,"GInfoSeen", "&e%time% ago: &7%message%"));

        // Anyway
        summonerTranslation.clear();
        ConfigurationSection summonerSection = translationsPair.getStorage().getConfigurationSection("SummonerClassUtils");
        addTranslation(GTL_SummonerClass.MINION_DEATH, fromSection(summonerSection,"MinionDeath", "&8&l[<#ff6bb7>&l!&8&l] <#ffc4e2>Your <#d594eb>%minion_name%<#ffc4e2> has fallen in battle. "));
        addTranslation(GTL_SummonerClass.MINION_EXCEED_CAP, fromSection(summonerSection,"MinionExceedCapacity", "&8&l[<#ff6bb7>&l!&8&l] <#ffc4e2>Your elemental control over <#d594eb>%minion_name%<#ffc4e2> is not strong enough and collapses! "));
        addTranslation(GTL_SummonerClass.MINION_WOULD_EXCEED_CAP, fromSection(summonerSection,"MinionWouldExceedCapacity", "&8&l[<#ff6bb7>&l!&8&l] <#ffc4e2>Your fail to summon <#d594eb>%minion_name%<#ffc4e2> because your elemental control is not strong enough! "));

    }

    /**
     * @param section Section
     * @param path Path to value
     * @param defaultValue Default value
     * @return Value at that section
     */
    @Contract("_,_,!null->!null")
    @Nullable static String fromSection(@Nullable ConfigurationSection section, @NotNull String path, @Nullable String defaultValue) {
        if (section == null) { return defaultValue; }
        return section.getString(path, defaultValue); }

    //region Containers
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
    //endregion

    //region Summoner Class Utils
    /**
     * @param key Translation Key
     *
     * @return Translation of this
     */
    @NotNull public static String getSummonerTranslation(@NotNull GTL_SummonerClass key) { GTranslationSummoner t = summonerTranslation.get(key); if (t == null) { return ""; } return t.getTranslation(); }
    @NotNull final static HashMap<GTL_SummonerClass, GTranslationSummoner> summonerTranslation = new HashMap<>();
    /**
     * @param key key
     * @param message message
     */
    public static void addTranslation(@NotNull GTL_SummonerClass key, @NotNull String message) { summonerTranslation.put(key, new GTranslationSummoner(key, message)); }
    //endregion
}
