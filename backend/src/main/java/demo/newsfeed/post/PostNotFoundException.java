package demo.newsfeed.post;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(long id) {
        super("Post " + id + " does not exist. It may have been deleted.");
    }
}
