package gunging.ootilities.goop.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Minecraft release number that compares correctly across both numbering schemes:
 * the old {@code 1.minor.patch} releases (1.20.6, 1.21.11) and the year based releases
 * that started with 26.1 ({@code 26.1}, {@code 26.1.2}, {@code 26.3}).
 * <p>
 * GooP 1.x stored the version as a double such as {@code 21.1} found with
 * {@code String#contains}, which broke on 26.x (no "1." prefix) and confused
 * 1.21.10 / 1.21.11 with 1.21.1. Every version check should go through this class instead.
 * <p>
 * Ordering: all {@code 1.x.y} releases sort before every year based release, and inside each
 * scheme the numbers compare numerically, so {@code 1.21.10 > 1.21.9} and {@code 26.1 < 26.1.2}.
 */
public final class McVersion implements Comparable<McVersion> {

    /** First number of the version, 1 for the classic scheme, the year (26, 27...) afterwards. */
    private final int major;
    private final int minor;
    private final int patch;

    private static final Pattern VERSION = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");

    public McVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Reads the first {@code a.b[.c]} number found in the text, so it accepts both the clean
     * {@code Bukkit.getMinecraftVersion()} output ({@code "1.21.4"}, {@code "26.1.2"}) and
     * decorated strings such as {@code "git-Paper-550 (MC: 1.19.4)"} or {@code "1.20.1-R0.1-SNAPSHOT"}.
     *
     * @return {@code null} when no version number is present.
     */
    @Nullable
    public static McVersion parse(@Nullable String text) {
        if (text == null) { return null; }

        // Prefer the "(MC: x.y.z)" part of Bukkit.getVersion() when it is present
        int mc = text.indexOf("MC: ");
        String scan = mc >= 0 ? text.substring(mc + 4) : text;

        Matcher m = VERSION.matcher(scan);
        if (!m.find()) { return null; }
        int major = Integer.parseInt(m.group(1));
        int minor = Integer.parseInt(m.group(2));
        int patch = m.group(3) == null ? 0 : Integer.parseInt(m.group(3));
        return new McVersion(major, minor, patch);
    }

    @NotNull
    public static McVersion of(int major, int minor, int patch) { return new McVersion(major, minor, patch); }

    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public int getPatch() { return patch; }

    /** @return If this version is the same as or newer than {@code major.minor.patch} */
    public boolean isAtLeast(int major, int minor, int patch) { return compareTo(new McVersion(major, minor, patch)) >= 0; }

    /** @return If this version is older than {@code major.minor.patch} */
    public boolean isBelow(int major, int minor, int patch) { return !isAtLeast(major, minor, patch); }

    /**
     * The value GooP 1.x used to keep in {@code GooP_MinecraftVersions.mcVersion}: the minor
     * and patch of a 1.x release written as a double ({@code 1.21.4 -> 21.4}). Year based
     * releases map to {@code 100 + year} ({@code 26.1 -> 126.1}) so they stay above every
     * 1.x value and old {@code mcVersion >= 21.3} style checks keep their meaning.
     * <p>
     * Only intended for code that has not been migrated to {@link #isAtLeast} yet; patch
     * numbers of 10 or more cannot be represented faithfully and are clamped to 9.
     */
    public double toLegacyDouble() {
        int p = Math.min(patch, 9);
        if (major == 1) { return minor + (p / 10.0); }
        return 100 + major + (minor / 10.0);
    }

    @Override
    public int compareTo(@NotNull McVersion o) {
        if (major != o.major) { return Integer.compare(major, o.major); }
        if (minor != o.minor) { return Integer.compare(minor, o.minor); }
        return Integer.compare(patch, o.patch);
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof McVersion)) { return false; }
        return compareTo((McVersion) o) == 0;
    }

    @Override public int hashCode() { return (major * 1000 + minor) * 1000 + patch; }

    @Override public String toString() { return patch == 0 ? major + "." + minor : major + "." + minor + "." + patch; }
}
