package gunging.ootilities.gunging_ootilities_plugin.containers.loader;

/**
 * Containers translations enum
 */
public enum GTL_Containers {
    /**
     * Can only open a container bag for the first time if it
     * the stack is of size = 1
     */
    BAGS_FIRST_OPEN_STACK,

    //region Protections
    /**
     * You are not looking at anything
     */
    GPROTECTION_BLOCK_NOT_FOUND,
    /**
     * The block is not a container, and thus physical
     * container protection commands are useless.
     */
    GPROTECTION_BLOCK_NOT_CONTAINER,
    /**
     * GModify Incorrect Player Name
     */
    GPROTECTION_PLAYER_NOT_FOUND,

    /**
     * The player cannot access this container because
     * they have no perms to access it.
     */
    GPROTECTION_NOT_MEMBER,
    /**
     * The player cannot add members because they
     * are not admins.
     */
    GPROTECTION_NOT_ADMIN,
    /**
     * The player cannot remove the protection or
     * break the container because they are not the
     * owner.
     */
    GPROTECTION_NOT_OWNER,

    /**
     * Successfully edited the protection of this
     */
    GREMOVE_EDITION_SUCCESS,
    /**
     * Successfully removed the protection from this
     */
    GREMOVE_REMOVE_SUCCESS,
    /**
     * Successfully protected this container
     */
    GREMOVE_CREATE_SUCCESS,
    /**
     * The protection was the same
     */
    GREMOVE_NO_CHANGE,

    /**
     * GModify Remove Member
     */
    GMODIFY_REMOVE_MEMBER,
    /**
     * GModify Add Member
     */
    GMODIFY_ADD_MEMBER,
    /**
     * GModify Add Admin
     */
    GMODIFY_ADD_ADMIN,
    /**
     * GModify Attempting to Remove Owner
     */
    GMODIFY_CANNOT_REMOVE,
    /**
     * GModify Attempting to edit the protection that doesnt exist
     */
    GMODIFY_UNREGISTERED,

    /**
     * GModify Help Line #1
     */
    GMODIFY_HELP_1,
    /**
     * GModify Help Line #2
     */
    GMODIFY_HELP_2,
    /**
     * GModify Help Line #3
     */
    GMODIFY_HELP_3,
    /**
     * GModify Help Line #4
     */
    GMODIFY_HELP_4,

    /**
     * GInfo Title
     */
    GINFO_TITLE,
    /**
     * GInfo Location (For Physical Containers)
     */
    GINFO_PHYSICAL_LOCATION,
    /**
     * GInfo Protection Type (For Physical Containers)
     */
    GINFO_PHYSICAL_PROTECTION,
    /**
     * GInfo Members Title (For Physical Containers)
     */
    GINFO_PHYSICAL_MEMBERS_TITLE,
    /**
     * GInfo Members (For Physical Containers)
     */
    GINFO_PHYSICAL_MEMBER,
    /**
     * GInfo Admins (For Physical Containers)
     */
    GINFO_PHYSICAL_ADMIN,
    /**
     * GInfo Owner (For Physical Containers)
     */
    GINFO_PHYSICAL_OWNER,
    /**
     * GInfo Owner (For Personal Containers)
     */
    GINFO_PERSONAL_OWNER,
    /**
     * GInfo No Owner Found (For Personal Containers)
     */
    GINFO_PERSONAL_NO_OWNER,
    /**
     * GInfo Seens Title
     */
    GINFO_SEENS_TITLE,
    /**
     * GInfo Seen
     */
    GINFO_SEEN
    //endregion
}
