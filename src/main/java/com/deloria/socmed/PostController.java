package com.deloria.socmed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ResponseEntity<Iterable<Post>> getAllPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        Optional<Post> post = postRepository.findById(id);
        return post.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found."));
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post) throws URISyntaxException {
        if (post.getAuthor() == null || post.getAuthor().trim().isEmpty()) {
            post.setAuthor("Anonymous");
        }
        if (post.getTimestamp() == null) {
            post.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")));
        }
        Post savedPost = postRepository.save(post);
        return ResponseEntity.created(new URI("/api/posts/" + savedPost.getId())).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id, @RequestBody Post post) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        if (post.getAuthor() != null && !post.getAuthor().trim().isEmpty()) {
            existing.setAuthor(post.getAuthor());
        }

        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        existing.setImageUrl(post.getImageUrl());

        if (post.getTimestamp() != null) {
            existing.setTimestamp(post.getTimestamp());
        }

        postRepository.save(existing);
        return ResponseEntity.ok("Post with id " + id + " updated.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.deleteById(id);
            return ResponseEntity.ok("Post deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found.");
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkPosts(@RequestBody Iterable<Post> posts) {
        if (posts == null || !posts.iterator().hasNext()) {
            return ResponseEntity.badRequest().body("Request body cannot be empty.");
        }
        for (Post post : posts) {
            if (post.getAuthor() == null || post.getAuthor().trim().isEmpty()) {
                post.setAuthor("Anonymous");
            }
            if (post.getTimestamp() == null) {
                post.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")));
            }
        }
        Iterable<Post> saved = postRepository.saveAll(posts);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> searchPosts(@PathVariable String keyword) {
        Iterable<Post> results = postRepository
                .findByAuthorContainingOrTitleContainingOrContentContaining(keyword, keyword, keyword);
        return ResponseEntity.ok(results);
    }
}