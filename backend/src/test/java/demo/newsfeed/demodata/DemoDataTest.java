package demo.newsfeed.demodata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/** Seeding and reset against a disposable in-memory database. */
@SpringBootTest
class DemoDataTest {

    @Autowired
    DemoDataService demoData;

    @Autowired
    JdbcClient jdbc;

    @MockitoSpyBean
    DemoDataset dataset;

    @BeforeEach
    void startFromSeedData() {
        demoData.reset();
    }

    @AfterEach
    void restoreRealDataset() {
        reset(dataset);
    }

    @Test
    void ordinaryStartupSeedingNeverOverwritesExistingData() {
        jdbc.sql("UPDATE post SET title = 'Changed locally' WHERE id = 1").update();
        jdbc.sql("DELETE FROM post WHERE id = 2").update();

        assertThat(demoData.seedIfFresh()).isFalse();
        assertThat(titleOf(1)).isEqualTo("Changed locally");
        assertThat(postIds()).doesNotContain(2L);
    }

    @Test
    void anEmptiedFeedStaysEmptyAcrossStartups() {
        jdbc.sql("DELETE FROM post").update();

        assertThat(demoData.seedIfFresh()).isFalse();
        assertThat(demoData.postCount()).isZero();
    }

    @Test
    void resetRestoresTheExactSeedDatasetWithStableIds() {
        jdbc.sql("UPDATE post SET title = 'Changed locally' WHERE id = 1").update();
        jdbc.sql("DELETE FROM post WHERE id = 2").update();
        insertUserPost("Created by a user");

        DemoDataService.ResetResult result = demoData.reset();

        assertThat(result).isEqualTo(new DemoDataService.ResetResult(5, 8));
        assertThat(postIds()).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
        assertThat(titleOf(1)).isEqualTo("Larchmere Library extends Saturday hours");
    }

    @Test
    void postsCreatedAfterResetGetFreshIdsWithoutCollisions() {
        long before = insertUserPost("Before reset");
        demoData.reset();
        long after = insertUserPost("After reset");

        assertThat(after).isGreaterThan(before).isGreaterThanOrEqualTo(1000);
    }

    @Test
    void failedResetRollsBackAndKeepsTheCurrentData() {
        long userPost = insertUserPost("Keep me");
        DemoDataset.SeedPost first = dataset.posts().getFirst();
        // A duplicate seed ID makes the reset fail part-way through its inserts.
        doReturn(List.of(first, first)).when(dataset).posts();

        assertThatThrownBy(() -> demoData.reset()).isInstanceOf(RuntimeException.class);

        assertThat(postIds()).contains(userPost, 1L, 8L).hasSize(9);
    }

    private long insertUserPost(String title) {
        jdbc.sql("""
                INSERT INTO post (author_id, title, body, published_at)
                VALUES (1, :title, 'Body', CURRENT_TIMESTAMP)
                """).param("title", title).update();
        return jdbc.sql("SELECT MAX(id) FROM post").query(Long.class).single();
    }

    private String titleOf(long id) {
        return jdbc.sql("SELECT title FROM post WHERE id = ?").param(id).query(String.class).single();
    }

    private List<Long> postIds() {
        return jdbc.sql("SELECT id FROM post ORDER BY id").query(Long.class).list();
    }
}
