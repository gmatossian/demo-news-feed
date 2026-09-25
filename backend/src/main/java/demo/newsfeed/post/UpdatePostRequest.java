package demo.newsfeed.post;

/**
 * Only the title and body are editable. Sending any other field (such as
 * {@code authorId} or {@code publishedAt}) is rejected with 400.
 */
public record UpdatePostRequest(String title, String body) {
}
