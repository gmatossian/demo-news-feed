package demo.newsfeed.author;

public record AuthorResponse(long id, String name) {

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.getId(), author.getName());
    }
}
