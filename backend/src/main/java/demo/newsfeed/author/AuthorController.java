package demo.newsfeed.author;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
class AuthorController {

    private final AuthorRepository authors;

    AuthorController(AuthorRepository authors) {
        this.authors = authors;
    }

    @GetMapping
    @Transactional(readOnly = true)
    List<AuthorResponse> list() {
        return authors.findAllByOrderByNameAsc().stream().map(AuthorResponse::from).toList();
    }
}
