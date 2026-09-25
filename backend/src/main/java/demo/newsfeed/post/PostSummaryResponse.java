package demo.newsfeed.post;

import java.time.Instant;

import demo.newsfeed.author.AuthorResponse;

/** A feed entry: the post's metadata plus a short excerpt instead of the full body. */
public record PostSummaryResponse(
        long id,
        String title,
        String excerpt,
        AuthorResponse author,
        Instant publishedAt,
        Instant updatedAt) {

    static PostSummaryResponse from(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                PostText.excerpt(post.getBody()),
                AuthorResponse.from(post.getAuthor()),
                post.getPublishedAt(),
                post.getUpdatedAt());
    }
}
