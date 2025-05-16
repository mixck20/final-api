package com.deloria.socmed;

import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {
    Iterable<Post> findByAuthorContainingOrTitleContainingOrContentContaining(
            String author, String title, String content);
}
