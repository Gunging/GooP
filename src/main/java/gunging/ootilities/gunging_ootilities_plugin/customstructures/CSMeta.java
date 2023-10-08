package gunging.ootilities.gunging_ootilities_plugin.customstructures;

public abstract class CSMeta {

    /**
     * Without units, a value has no meaning. This would give you
     * a value without units... likely for debugging purposes, as
     * it has no meaning without its attached CustomStructureMetaSource
     *
     * @return String representation of this value
     */
    @Override public abstract String toString();
}
