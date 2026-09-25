package demo.newsfeed.post;

import java.time.Instant;

import demo.newsfeed.author.Author;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A published plain-text post. The author and publication time are fixed at creation:
 * there are no setters for them and their columns are excluded from SQL updates.
 */
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private Author author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(name = "published_at", nullable = false, updatable = false)
    private Instant publishedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected Post() {
        // for JPA
    }

    Post(Author author, String title, String body, Instant publishedAt) {
        this.author = author;
        this.title = title;
        this.body = body;
        this.publishedAt = publishedAt;
    }

    /**
     * Replaces the title and body. Records {@code updatedAt} only when something actually
     * changed, so saving an unchanged form does not mark the post as edited.
     *
     * @return whether the post changed
     */
    boolean edit(String newTitle, String newBody, Instant now) {
        if (title.equals(newTitle) && body.equals(newBody)) {
            return false;
        }
        title = newTitle;
        body = newBody;
        updatedAt = now;
        return true;
    }

    public Long getId() {
        return id;
    }

    public Author getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
