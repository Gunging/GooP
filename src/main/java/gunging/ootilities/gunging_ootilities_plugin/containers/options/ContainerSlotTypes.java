package gunging.ootilities.gunging_ootilities_plugin.containers.options;

/**
 * General behaviour of Container Slots
 */
public enum ContainerSlotTypes {

    /**
     * The slot does not store items, just shows some default item.
     */
    DISPLAY,

    /**
     * The slot supports storing items in it. May show a default item while empty.
     */
    STORAGE,

    /**
     * The slot is completely uninteractable and textured automatically.
     */
    EDGE,

    /**
     * Players can pickup items from it but not put into it, will only trigger
     * commands if successfully picked up, etc.
     */
    RESULT
}
