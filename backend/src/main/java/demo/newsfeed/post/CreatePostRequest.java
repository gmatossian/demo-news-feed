package demo.newsfeed.post;

public record CreatePostRequest(String title, String body, Long authorId) {
}
