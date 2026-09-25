package demo.newsfeed.post;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import demo.newsfeed.demodata.DemoDataService;
import tools.jackson.databind.json.JsonMapper;

/** HTTP-level checks of the post rules, starting each test from the seed dataset. */
@SpringBootTest
@AutoConfigureMockMvc
class PostApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JsonMapper json;

    @Autowired
    DemoDataService demoData;

    @BeforeEach
    void startFromSeedData() {
        demoData.reset();
    }

    @Test
    void feedIsNewestFirstWithHigherIdWinningTimestampTies() throws Exception {
        // Seed posts 5 and 6 share a publication time.
        mvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(contains(8, 7, 6, 5, 4, 3, 2, 1)))
                .andExpect(jsonPath("$[2].publishedAt").value("2026-09-18T09:00:00Z"))
                .andExpect(jsonPath("$[3].publishedAt").value("2026-09-18T09:00:00Z"));
    }

    @Test
    void createdPostIsStoredNormalizedAndAppearsFirstInFeed() throws Exception {
        send(post("/api/posts"), Map.of("title", "  New post  ", "body", "\r\nLine one\r\n\r\n  indented\n", "authorId", 4))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/posts/")))
                .andExpect(jsonPath("$.id").value(greaterThanOrEqualTo(1000)))
                .andExpect(jsonPath("$.title").value("New post"))
                .andExpect(jsonPath("$.body").value("Line one\n\n  indented"))
                .andExpect(jsonPath("$.author.name").value("Mateo Driftwood"))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));

        mvc.perform(get("/api/posts"))
                .andExpect(jsonPath("$[0].title").value("New post"));
    }

    @Test
    void invalidCreateReturnsFieldErrorsAndStoresNothing() throws Exception {
        send(post("/api/posts"), Map.of("title", " \t ", "body", "\n\n", "authorId", 99))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid post"))
                .andExpect(jsonPath("$.errors.title").value("Enter a title."))
                .andExpect(jsonPath("$.errors.body").value("Enter the post text."))
                .andExpect(jsonPath("$.errors.authorId").value("Choose one of the listed authors."));

        send(post("/api/posts"), Map.of("title", "😀".repeat(121), "body", "ok"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title must be 120 characters or fewer."))
                .andExpect(jsonPath("$.errors.authorId").value("Choose an author."));

        mvc.perform(get("/api/posts")).andExpect(jsonPath("$.length()").value(8));
    }

    @Test
    void maximumLengthsCountingEmojiAsOneCharacterAreAccepted() throws Exception {
        send(post("/api/posts"), Map.of("title", "😀".repeat(120), "body", "😀".repeat(5_000), "authorId", 1))
                .andExpect(status().isCreated());
    }

    @Test
    void editChangesTextButKeepsAuthorAndPublicationTime() throws Exception {
        send(put("/api/posts/2"), Map.of("title", "Repair café moves to Sundays", "body", "Updated text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Repair café moves to Sundays"))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.publishedAt").value("2026-09-10T17:45:00Z"))
                .andExpect(jsonPath("$.updatedAt").value(notNullValue()));

        // Editing does not move the post in the feed.
        mvc.perform(get("/api/posts"))
                .andExpect(jsonPath("$[*].id").value(contains(8, 7, 6, 5, 4, 3, 2, 1)));
    }

    @Test
    void editCannotChangeAuthorOrPublicationTime() throws Exception {
        send(put("/api/posts/2"), Map.of("title", "T", "body", "B", "authorId", 3))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Field 'authorId' is not accepted by this endpoint."));
        send(put("/api/posts/2"), Map.of("title", "T", "body", "B", "publishedAt", "2030-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/posts/2"))
                .andExpect(jsonPath("$.title").value("Repair café returns to the old fire station"))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));
    }

    @Test
    void savingUnchangedTextDoesNotMarkThePostEdited() throws Exception {
        String title = "Repair café returns to the old fire station";
        String body = json.readTree(mvc.perform(get("/api/posts/2")).andReturn().getResponse().getContentAsString())
                .get("body").asString();

        send(put("/api/posts/2"), Map.of("title", title + "  ", "body", body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));
    }

    @Test
    void invalidEditIsRejected() throws Exception {
        send(put("/api/posts/2"), Map.of("title", "Line\nbreak", "body", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title must be a single line."))
                .andExpect(jsonPath("$.errors.body").value("Enter the post text."));
    }

    @Test
    void deletedPostDisappearsAndResolvesAsMissing() throws Exception {
        mvc.perform(delete("/api/posts/3")).andExpect(status().isNoContent());

        mvc.perform(get("/api/posts/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Post not found"));
        mvc.perform(get("/api/posts")).andExpect(jsonPath("$[*].id").value(not(hasItem(3))));
    }

    @Test
    void missingPostsReturn404ForEveryOperation() throws Exception {
        mvc.perform(get("/api/posts/12345")).andExpect(status().isNotFound());
        send(put("/api/posts/12345"), Map.of("title", "T", "body", "B")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/posts/12345")).andExpect(status().isNotFound());
    }

    private ResultActions send(MockHttpServletRequestBuilder request, Map<String, ?> body) throws Exception {
        return mvc.perform(request.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)));
    }
}
