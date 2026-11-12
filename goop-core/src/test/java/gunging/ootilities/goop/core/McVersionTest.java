package gunging.ootilities.goop.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class McVersionTest {

    @Test
    void parsesCleanAndDecoratedStrings() {
        assertEquals(McVersion.of(1, 21, 4), McVersion.parse("1.21.4"));
        assertEquals(McVersion.of(26, 1, 0), McVersion.parse("26.1"));
        assertEquals(McVersion.of(26, 1, 2), McVersion.parse("26.1.2"));
        assertEquals(McVersion.of(1, 19, 4), McVersion.parse("git-Paper-550 (MC: 1.19.4)"));
        assertEquals(McVersion.of(1, 20, 1), McVersion.parse("1.20.1-R0.1-SNAPSHOT"));
        assertEquals(McVersion.of(26, 3, 0), McVersion.parse("26.3.build.249-stable"));
        assertNull(McVersion.parse("no numbers here"));
        assertNull(McVersion.parse(null));
    }

    @Test
    void doubleDigitPatchIsNotConfusedWithSingleDigit() {
        // GooP 1.x used contains("1.21.1") and read 1.21.10 / 1.21.11 as 1.21.1
        McVersion v = McVersion.parse("1.21.10");
        assertEquals(10, v.getPatch());
        assertTrue(v.isAtLeast(1, 21, 9));
        assertTrue(McVersion.parse("1.21.11").compareTo(McVersion.parse("1.21.10")) > 0);
        assertTrue(McVersion.parse("1.21.2").compareTo(McVersion.parse("1.21.10")) < 0);
    }

    @Test
    void yearBasedReleasesSortAfterEveryClassicRelease() {
        assertTrue(McVersion.parse("26.1").isAtLeast(1, 21, 11));
        assertTrue(McVersion.parse("26.1").isBelow(26, 1, 2));
        assertTrue(McVersion.parse("26.3").isAtLeast(26, 2, 0));
        assertTrue(McVersion.parse("1.20.6").isBelow(1, 21, 0));
    }

    @Test
    void legacyDoubleKeepsOldComparisonsMeaningful() {
        assertEquals(20.6, McVersion.parse("1.20.6").toLegacyDouble(), 1e-9);
        assertEquals(21.4, McVersion.parse("1.21.4").toLegacyDouble(), 1e-9);
        assertTrue(McVersion.parse("1.21.11").toLegacyDouble() >= 21.6);
        assertTrue(McVersion.parse("26.1").toLegacyDouble() > McVersion.parse("1.21.11").toLegacyDouble());
    }
}
