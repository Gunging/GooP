package gunging.ootilities.goop.core;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Matches names against GooP's {@code *} wildcards: {@code DARK_OAK_*}, {@code *_SWORD},
 * {@code *ICE*}, or a plain name.
 * <p>
 * A pattern without {@code *} must match the whole name. GooP 1.x only checked that each piece
 * appeared somewhere in order, so {@code ICE} also matched {@code MELON_SLICE} and
 * {@code PACKED_ICE}, and {@code SWORD_1} matched {@code SWORD_10}; {@code /goop consumeitem}
 * then removed the wrong items. Write {@code *ICE*} for the old "contains" behaviour.
 */
public final class GlobMatcher {

    private GlobMatcher() { }

    /**
     * @param target     The name being tested, such as a material or MMOItem ID
     * @param pattern    The pattern, where {@code *} matches any run of characters (including none)
     * @param ignoreCase If letter case should be ignored
     *
     * @return If the whole target matches the pattern
     */
    public static boolean matches(@NotNull String target, @NotNull String pattern, boolean ignoreCase) {
        String t = ignoreCase ? target.toUpperCase(Locale.ROOT) : target;
        String p = ignoreCase ? pattern.toUpperCase(Locale.ROOT) : pattern;

        // No wildcard: exact match only
        if (p.indexOf('*') < 0) { return t.equals(p); }

        String[] pieces = p.split("\\*", -1);
        String first = pieces[0];
        String last = pieces[pieces.length - 1];

        // Anchored ends: text before the first * must start the target, text after the last * must end it
        if (!t.startsWith(first)) { return false; }
        int cursor = first.length();
        int end = t.length() - last.length();
        if (end < cursor || !t.endsWith(last)) { return false; }

        // Middle pieces must appear in order, without overlapping each other or the ends
        for (int i = 1; i < pieces.length - 1; i++) {
            String piece = pieces[i];
            if (piece.isEmpty()) { continue; }
            int found = t.indexOf(piece, cursor);
            if (found < 0 || found + piece.length() > end) { return false; }
            cursor = found + piece.length();
        }
        return true;
    }

    /**
     * GooP 1.x matching, kept for servers whose configs were written around it
     * ({@code LegacyWildcardMatching: true} in config.yml). Each piece between {@code *} only
     * has to appear somewhere in the target, in order, so {@code ICE} matches
     * {@code MELON_SLICE}. Reproduces the original algorithm exactly, including that the
     * search for a piece resumes at the start of the previous match rather than after it.
     */
    public static boolean matchesLoosely(@NotNull String target, @NotNull String pattern, boolean ignoreCase) {
        String mat = ignoreCase ? target.toUpperCase(Locale.ROOT) : target;
        for (String matcher : pattern.split("\\*")) {
            String cher = ignoreCase ? matcher.toUpperCase(Locale.ROOT) : matcher;
            int m = mat.indexOf(cher);
            if (m < 0) { return false; }
            mat = mat.substring(m);
        }
        return true;
    }
}
