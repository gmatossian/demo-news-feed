package demo.newsfeed.post;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.newsfeed.author.Author;
import demo.newsfeed.author.AuthorRepository;

/** Post rules: validation, fixed author/publication time, and edit timestamps. */
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository posts;
    private final AuthorRepository authors;
    private final Clock clock;

    PostService(PostRepository posts, AuthorRepository authors, Clock clock) {
        this.posts = posts;
        this.authors = authors;
        this.clock = clock;
    }

    public List<Post> feed() {
        return posts.findAllByOrderByPublishedAtDescIdDesc();
    }

    public Post get(long id) {
        return posts.findWithAuthorById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Transactional
    public Post create(CreatePostRequest request) {
        String title = PostText.normalize(request.title());
        String body = PostText.normalize(request.body());
        Map<String, String> errors = textProblems(title, body);

        Author author = null;
        if (request.authorId() == null) {
            errors.put("authorId", "Choose an author.");
        } else {
            author = authors.findById(request.authorId()).orElse(null);
            if (author == null) {
                errors.put("authorId", "Choose one of the listed authors.");
            }
        }
        if (!errors.isEmpty()) {
            throw new InvalidPostException(errors);
        }
        return posts.save(new Post(author, title, body, now()));
    }

    @Transactional
    public Post update(long id, UpdatePostRequest request) {
        String title = PostText.normalize(request.title());
        String body = PostText.normalize(request.body());
        Map<String, String> errors = textProblems(title, body);
        if (!errors.isEmpty()) {
            throw new InvalidPostException(errors);
        }
        Post post = get(id);
        post.edit(title, body, now());
        return post;
    }

    @Transactional
    public void delete(long id) {
        if (!posts.existsById(id)) {
            throw new PostNotFoundException(id);
        }
        posts.deleteById(id);
    }

    private static Map<String, String> textProblems(String title, String body) {
        Map<String, String> errors = new LinkedHashMap<>();
        PostText.titleProblem(title).ifPresent(message -> errors.put("title", message));
        PostText.bodyProblem(body).ifPresent(message -> errors.put("body", message));
        return errors;
    }

    /** Microsecond precision matches the database column. */
    private Instant now() {
        return Instant.now(clock).truncatedTo(ChronoUnit.MICROS);
    }
}
