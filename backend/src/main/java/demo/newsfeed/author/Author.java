package demo.newsfeed.author;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A fictional author label chosen when composing a post. Authors are fixed demo data;
 * they are not accounts and selecting one is not authentication.
 */
@Entity
@Table(name = "author")
public class Author {

    @Id
    private Long id;

    private String name;

    protected Author() {
        // for JPA
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
