package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.jetbrains.annotations.NotNull;

public class OrderableListEntry<E> implements Comparable<OrderableListEntry<E>> {

    E stored;
    public E getStoredValue() { return stored; }
    public E GetStoredValue() { return getStoredValue(); }

    double ordeal;
    public double getOrdeal() { return ordeal; }
    public double GetOrdeal() { return getOrdeal(); }

    /**
     * <p>
     *     Stores something of whatever type and links it to a number.
     *
     *     You can from there sort like a pro with <code>Collections.sort</code> to order from least (first entry) to greatest (final entry).
     * </p>
     * @param object Object you are giving this numeric reference
     * @param order Numeric reference of value
     */
    public OrderableListEntry(E object, double order) {
        stored = object;
        ordeal = order;
    }

    @Override
    /*
     * This is where we write the logic to sort. This method sort
     * automatically by the first name in case that the last name is
     * the same.
     */
    public int compareTo(@NotNull OrderableListEntry<E> o) {
        /*
         * Should return < 0 if this is supposed to be less than au
         * > 0 if this is supposed to be greater than object au
         * and 0 if they are supposed to be equal.
         */

        // BRUH
        double difference = getOrdeal() - o.getOrdeal();
        if (difference > 0) { return 1; }
        if (difference < 0) { return -1; }
        return 0;
    }
}
