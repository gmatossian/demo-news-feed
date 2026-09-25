package demo.newsfeed.post;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
class PostController {

    private final PostService service;

    PostController(PostService service) {
        this.service = service;
    }

    @GetMapping
    List<PostSummaryResponse> feed() {
        return service.feed().stream().map(PostSummaryResponse::from).toList();
    }

    @GetMapping("/{id}")
    PostResponse get(@PathVariable long id) {
        return PostResponse.from(service.get(id));
    }

    @PostMapping
    ResponseEntity<PostResponse> create(@RequestBody CreatePostRequest request) {
        PostResponse created = PostResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/posts/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    PostResponse update(@PathVariable long id, @RequestBody UpdatePostRequest request) {
        return PostResponse.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
