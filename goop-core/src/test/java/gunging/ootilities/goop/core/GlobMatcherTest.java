package gunging.ootilities.goop.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobMatcherTest {

    @Test
    void plainNamesMatchExactlyOnly() {
        // The reported bug: /goop consumeitem ... v ICE removed melon slices
        assertTrue(GlobMatcher.matches("ICE", "ICE", true));
        assertFalse(GlobMatcher.matches("MELON_SLICE", "ICE", true));
        assertFalse(GlobMatcher.matches("PACKED_ICE", "ICE", true));
        assertFalse(GlobMatcher.matches("SWORD_10", "SWORD_1", true));
        assertTrue(GlobMatcher.matches("melon_slice", "MELON_SLICE", true));
        assertFalse(GlobMatcher.matches("melon_slice", "MELON_SLICE", false));
    }

    @Test
    void wildcardsAnchorTheirEnds() {
        assertTrue(GlobMatcher.matches("DARK_OAK_LOG", "DARK_OAK_*", true));
        assertFalse(GlobMatcher.matches("STRIPPED_DARK_OAK_LOG", "DARK_OAK_*", true));
        assertTrue(GlobMatcher.matches("DIAMOND_SWORD", "*_SWORD", true));
        assertFalse(GlobMatcher.matches("DIAMOND_SWORD_HILT", "*_SWORD", true));
        assertTrue(GlobMatcher.matches("PACKED_ICE", "*ICE*", true));
        assertTrue(GlobMatcher.matches("MELON_SLICE", "*ICE", true));
        assertTrue(GlobMatcher.matches("ANYTHING", "*", true));
        assertTrue(GlobMatcher.matches("", "*", true));
    }

    @Test
    void middlePiecesAreOrderedAndDoNotOverlap() {
        assertTrue(GlobMatcher.matches("A_B_C", "A*B*C", true));
        assertFalse(GlobMatcher.matches("A_C_B", "A*B*C", true));
        // "AB" must not reuse the same characters for both ends
        assertFalse(GlobMatcher.matches("AB", "AB*AB", true));
        assertTrue(GlobMatcher.matches("ABAB", "AB*AB", true));
        assertTrue(GlobMatcher.matches("SWORD_10", "SWORD_1*", true));
    }

    @Test
    void looseMatchingKeepsGooP1Behaviour() {
        assertTrue(GlobMatcher.matchesLoosely("MELON_SLICE", "ICE", true));
        assertTrue(GlobMatcher.matchesLoosely("SWORD_10", "SWORD_1", true));
        assertTrue(GlobMatcher.matchesLoosely("STRIPPED_DARK_OAK_LOG", "DARK_OAK_*", true));
        assertTrue(GlobMatcher.matchesLoosely("ANYTHING", "*", true));
        assertFalse(GlobMatcher.matchesLoosely("DIAMOND_SWORD", "AXE", true));
        // Original quirk: the next piece may reuse the characters of the previous one
        assertTrue(GlobMatcher.matchesLoosely("AB", "AB*AB", true));
    }
}
