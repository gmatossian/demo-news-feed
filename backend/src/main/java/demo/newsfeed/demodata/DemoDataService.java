package demo.newsfeed.demodata;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds a fresh database and restores the demo dataset on request. Both operations touch
 * only the application's tables; they never modify source files or anything else.
 */
@Service
public class DemoDataService {

    private final JdbcClient jdbc;
    private final DemoDataset dataset;

    DemoDataService(JdbcClient jdbc, DemoDataset dataset) {
        this.jdbc = jdbc;
        this.dataset = dataset;
    }

    /**
     * Loads the dataset only into a database that has never been seeded (no authors).
     * A seeded database is left untouched, even if every post has since been deleted.
     *
     * @return whether seed data was inserted
     */
    @Transactional
    public boolean seedIfFresh() {
        Integer authorCount = jdbc.sql("SELECT COUNT(*) FROM author").query(Integer.class).single();
        if (authorCount > 0) {
            return false;
        }
        insertDataset();
        return true;
    }

    /**
     * Replaces all posts and authors with the original dataset in one transaction:
     * if any step fails, the previous data remains. Seed posts reuse their fixed IDs;
     * the generated-ID sequence is not restarted, so new posts cannot collide with them.
     */
    @Transactional
    public ResetResult reset() {
        jdbc.sql("DELETE FROM post").update();
        jdbc.sql("DELETE FROM author").update();
        insertDataset();
        return new ResetResult(dataset.authors().size(), dataset.posts().size());
    }

    public long postCount() {
        return jdbc.sql("SELECT COUNT(*) FROM post").query(Long.class).single();
    }

    private void insertDataset() {
        for (DemoDataset.SeedAuthor author : dataset.authors()) {
            jdbc.sql("INSERT INTO author (id, name) VALUES (:id, :name)")
                    .param("id", author.id())
                    .param("name", author.name())
                    .update();
        }
        for (DemoDataset.SeedPost post : dataset.posts()) {
            jdbc.sql("""
                    INSERT INTO post (id, author_id, title, body, published_at, updated_at)
                    VALUES (:id, :authorId, :title, :body, :publishedAt, :updatedAt)
                    """)
                    .param("id", post.id())
                    .param("authorId", post.authorId())
                    .param("title", post.title())
                    .param("body", post.body())
                    .param("publishedAt", post.publishedAt())
                    .param("updatedAt", post.updatedAt())
                    .update();
        }
    }

    public record ResetResult(int authors, int posts) {
    }
}
