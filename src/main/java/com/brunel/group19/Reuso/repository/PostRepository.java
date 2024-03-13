package com.brunel.group19.Reuso.repository;

import java.util.List;

import com.brunel.group19.Reuso.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTags_Name(String tagName);
    List<Post> findByAuthor_Username(String username);
    Page<Post> findByAuthor_Username(String username, Pageable pageable);
    Page<Post> findByAuthor_Id(Long id, Pageable pageable);
}