package demo.newsfeed.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

interface PostRepository extends JpaRepository<Post, Long> {

    /** Feed order: newest publication first; the higher ID wins a timestamp tie. */
    @EntityGraph(attributePaths = "author")
    List<Post> findAllByOrderByPublishedAtDescIdDesc();

    @EntityGraph(attributePaths = "author")
    Optional<Post> findWithAuthorById(long id);
}
