package gunging.ootilities.gunging_ootilities_plugin.containers.restriction;

/**
 * When a slot becomes locked with items already in it, what happens to those items?
 *
 *  Checked when a container is opened and closed, or an inventory update happens if they
 *  happen to be equipment slots
 *
 * The question 'who are slots restricted from' has two answers depending on the scenario:
 *
 * If container is PERSONAL or STATION, it is assumed that only the owner has access to it
 * (unless its a bag, which is treated like a PHYSICAL container), such that restricted
 * behaviour kicks in only when the owner has the slot locked.
 *
 * If the container is PHYSICAL, a player interacting with the slot will just fail to
 * put/take items in its place, and this restricted behaviour is just ignored.
 */
public enum RestrictedBehaviour {

    /**
     * Items are now trapped inside this slot.
     */
    LOCK,

    /**
     * Players can only still their items from the slot,
     * even if they cannot interact in any other way.
     */
    TAKE,

    /**
     * Items will forcefully drop to the players inventory.
     */
    DROP,

    /**
     * Items will be destroyed.
     */
    DESTROY
}
