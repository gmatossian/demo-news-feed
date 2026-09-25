package demo.newsfeed.author;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAllByOrderByNameAsc();
}
