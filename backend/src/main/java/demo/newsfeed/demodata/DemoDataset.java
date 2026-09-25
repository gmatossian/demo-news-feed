package demo.newsfeed.demodata;

import java.time.Instant;
import java.util.List;

/** The committed fictional starting dataset (resources/seed/demo-data.json). */
public record DemoDataset(List<SeedAuthor> authors, List<SeedPost> posts) {

    public record SeedAuthor(long id, String name) {
    }

    /** Seed posts carry fixed IDs below 1000 so they keep the same links after every reset. */
    public record SeedPost(long id, long authorId, String title, String body, Instant publishedAt, Instant updatedAt) {
    }
}
