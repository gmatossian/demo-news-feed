package demo.newsfeed.post;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Same cases as frontend/src/app/posts/post-text-rules.spec.ts. */
class PostTextTest {

    @Test
    void trimsUnicodeWhitespaceAtTheEdgesOnly() {
        assertThat(PostText.normalize(" \t Hello  world　\n")).isEqualTo("Hello  world");
    }

    @Test
    void preservesInternalFormattingAndNormalizesLineEndings() {
        assertThat(PostText.normalize("\r\nFirst\r\n\r\n  - indented\rLast  \n"))
                .isEqualTo("First\n\n  - indented\nLast");
    }

    @Test
    void treatsMissingAndWhitespaceOnlyTextAsEmpty() {
        assertThat(PostText.normalize(null)).isEmpty();
        assertThat(PostText.titleProblem(PostText.normalize(" \n\t "))).contains("Enter a title.");
        assertThat(PostText.bodyProblem(PostText.normalize("\r\n \r\n"))).contains("Enter the post text.");
    }

    @Test
    void countsCodePointsSoEmojiCountAsOneCharacter() {
        assertThat(PostText.length("héllo 😀")).isEqualTo(7);
        assertThat(PostText.titleProblem("😀".repeat(120))).isEmpty();
        assertThat(PostText.titleProblem("😀".repeat(121))).contains("Title must be 120 characters or fewer.");
        assertThat(PostText.bodyProblem("a".repeat(5_000))).isEmpty();
        assertThat(PostText.bodyProblem("a".repeat(5_001))).contains("Post text must be 5,000 characters or fewer.");
    }

    @Test
    void rejectsMultiLineTitles() {
        assertThat(PostText.titleProblem("One\nTwo")).contains("Title must be a single line.");
    }

    @Test
    void excerptFlattensWhitespaceAndCutsLongText() {
        assertThat(PostText.excerpt("Short\n\n  text")).isEqualTo("Short text");
        String excerpt = PostText.excerpt("word ".repeat(100));
        assertThat(excerpt).endsWith("word…");
        assertThat(PostText.length(excerpt)).isLessThanOrEqualTo(PostText.EXCERPT_MAX + 1);
    }
}
