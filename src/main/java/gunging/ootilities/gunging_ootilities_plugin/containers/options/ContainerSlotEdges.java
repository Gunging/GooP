package gunging.ootilities.gunging_ootilities_plugin.containers.options;

public enum ContainerSlotEdges {
    // With all shores
    ISLAND,

    // With West Mainland
    WM_PENNINSULA,      // Only West Mainland
    WM_BRIDGE,          // West and East Mainalnd
        WM_SOUTHSHORE,  // West, East, and North Mainland
        WM_NORTHSHORE,  // West, East, and South Mainland
    WM_NORTHCORNER,     // West and North Mainland
        WM_EASTSHORE,   // West, North, and South Mainland
    WM_SOUTHCORNER,     // West and South Mainland

    // With North Mainland
    NM_PENNINSULA,      // Only North Mainland
    NM_BRIDGE,          // North and South Mainland
        NM_WESTSHORE,   // North, South, and East Mainland
    NM_EASTCORNER,      // North and East Mainland

    // With East Mainland
    EM_PENNINSULA,      // Only East Mainland
    EM_SOUTHCORNER,     // East and South Mainland

    // With South Mainland
    SM_PENNINSULA,      // Only South Mainland

    // With no shores
    MAINLAND
}
