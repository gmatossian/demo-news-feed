package demo.newsfeed.post;

import java.time.Instant;

import demo.newsfeed.author.AuthorResponse;

public record PostResponse(
        long id,
        String title,
        String body,
        AuthorResponse author,
        Instant publishedAt,
        Instant updatedAt) {

    static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                AuthorResponse.from(post.getAuthor()),
                post.getPublishedAt(),
                post.getUpdatedAt());
    }
}
