package gunging.ootilities.gunging_ootilities_plugin.containers.options;

public enum ContainerOpeningReason {

    /**
     * When opened for actual use
     */
    USAGE,

    /**
     * Allows players to click commands, but not use Storage Slots
     */
    LOCK_STORAGE,

    /**
     * When opened for edition of its display contents
     */
    EDITION_DISPLAY,

    /**
     * When opened for edition of its storage contents
     */
    EDITION_STORAGE,

    /**
     * When opened with /goop containers config command slots
     */
    EDITION_COMMANDS,

    /**
     * When opened for edition of its display contents; exclusive to Player-type containers
     * and the unique slots they provide outside of the usual rectangle of slots.
     */
    EDITION_PLAYER_DISPLAY,

    /**
     * When opened for edition of its storage contents; exclusive to Player-type containers
     * and the unique slots they provide outside of the usual rectangle of slots.
     */
    EDITION_PLAYER_STORAGE,

    /**
     * When opened with /goop containers config command slots; exclusive to Player-type
     * containers and the unique slots they provide outside of the usual rectangle of slots.
     */
    EDITION_PLAYER_COMMANDS
}
