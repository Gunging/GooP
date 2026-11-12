package gunging.ootilities.gunging_ootilities_plugin.containers.interaction;

public enum CIInteractingFailReason {
    UNKNOWN_SLOT,
    EDGE_ITEM,
    DEFAULT_ITEM,
    DISPLAY_ITEM,
    RESTRICTIONS_UNMET,
    UNKNOWN_SLOT_TYPE,
    RESULT_SLOT_TYPE,
    CANNOT_STORE,

    /**
     * This prevents containers from storing container bags
     */
    CONTAINERCEPTION,

    /**
     * This prevents containers from storing written books (players like to fill them with garbage characters and overflow memory)
     */
    COUNTER_OVERFLOW
}
