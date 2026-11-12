package gunging.ootilities.gunging_ootilities_plugin.containers.options;

public enum GPCProtection {

    /**
     * Only the members of the container can edit the storage.
     *
     * Only the owner can break this container.
     */
    PRIVATE,

    /**
     * Anyone can edit the storage.
     *
     * Only the owner can break this container.
     */
    PUBLIC,

    /**
     * Anyone can view the storage, but only members can edit it.
     *
     * Only the owner can break this container.
     */
    DISPLAY,

    /**
     * Anyone can edit the storage.
     *
     * Anyone can break this container.
     */
    UNREGISTERED
}
