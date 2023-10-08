package gunging.ootilities.gunging_ootilities_plugin.containers.options;

/**
 * Most importantly, the way containers save items within them
 */
public enum ContainerTypes {

    /**
     * Can store items (saved by UUID)
     */
    PERSONAL,

    /**
     * Can store items (saved by World Location)
     */
    PHYSICAL,

    /**
     * Cannot save items in the files
     */
    STATION,

    /**
     * Only one can exist, stores values for player inventory.
     */
    PLAYER
}
