package gunging.ootilities.gunging_ootilities_plugin.misc.goop;

/**
 * This class detects when a GooP command chain with a succeeded.
 *
 * In a poetic way, it is a "light sensor" that detects the "flare..." whatever
 */
public interface SuccessibleFlareReceptor {

    /**
     * Ran when the command was received with a success.
     */
    void received();
}
