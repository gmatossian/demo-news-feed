package demo.newsfeed.post;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Text rules for post titles and bodies. The frontend mirrors these rules in
 * frontend/src/app/posts/post-text-rules.ts; keep both in step (docs/api.md).
 *
 * <ul>
 * <li>Line endings are normalized to {@code \n}.</li>
 * <li>Leading/trailing characters with the Unicode White_Space property are removed;
 * everything in between (including body line breaks and indentation) is kept.</li>
 * <li>Lengths count Unicode code points, so an emoji counts as one character.</li>
 * </ul>
 */
public final class PostText {

    public static final int TITLE_MAX = 120;
    public static final int BODY_MAX = 5_000;
    static final int EXCERPT_MAX = 200;

    private static final Pattern LINE_ENDINGS = Pattern.compile("\\r\\n?");
    private static final Pattern EDGE_WHITESPACE = Pattern.compile("\\A\\p{IsWhite_Space}+|\\p{IsWhite_Space}+\\z");
    private static final Pattern WHITESPACE_RUN = Pattern.compile("\\p{IsWhite_Space}+");

    private PostText() {
    }

    /** Normalizes line endings and trims edge whitespace; {@code null} becomes empty. */
    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String unified = LINE_ENDINGS.matcher(raw).replaceAll("\n");
        return EDGE_WHITESPACE.matcher(unified).replaceAll("");
    }

    /** Length in Unicode code points. */
    public static int length(String text) {
        return text.codePointCount(0, text.length());
    }

    /** Returns a validation message for a normalized title, if it breaks a rule. */
    static Optional<String> titleProblem(String title) {
        if (title.isEmpty()) {
            return Optional.of("Enter a title.");
        }
        if (title.indexOf('\n') >= 0) {
            return Optional.of("Title must be a single line.");
        }
        if (length(title) > TITLE_MAX) {
            return Optional.of("Title must be " + TITLE_MAX + " characters or fewer.");
        }
        return Optional.empty();
    }

    /** Returns a validation message for a normalized body, if it breaks a rule. */
    static Optional<String> bodyProblem(String body) {
        if (body.isEmpty()) {
            return Optional.of("Enter the post text.");
        }
        if (length(body) > BODY_MAX) {
            return Optional.of("Post text must be 5,000 characters or fewer.");
        }
        return Optional.empty();
    }

    /** A single-line preview for the feed: whitespace collapsed, cut near a word boundary. */
    static String excerpt(String body) {
        String flat = WHITESPACE_RUN.matcher(body).replaceAll(" ").strip();
        if (length(flat) <= EXCERPT_MAX) {
            return flat;
        }
        String cut = flat.substring(0, flat.offsetByCodePoints(0, EXCERPT_MAX));
        int lastSpace = cut.lastIndexOf(' ');
        if (lastSpace > EXCERPT_MAX / 2) {
            cut = cut.substring(0, lastSpace);
        }
        return cut.stripTrailing() + "…";
    }
}
