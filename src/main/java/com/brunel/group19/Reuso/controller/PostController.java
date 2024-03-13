package com.brunel.group19.Reuso.controller;

import java.util.Optional;

import com.brunel.group19.Reuso.controller.exceptionhandling.ApiError;
import com.brunel.group19.Reuso.model.Post;
import com.brunel.group19.Reuso.model.PostDto;
import com.brunel.group19.Reuso.repository.PostRepository;
import com.brunel.group19.Reuso.service.PostService;
import com.brunel.group19.Reuso.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired private PostRepository postRepository;
    @Autowired private PostService postService;
    @Autowired private UserService userService;

    @PostMapping("/new")
    PostDto newPost(Authentication auth, @RequestBody PostDto post) {
        Post newPost = postService.toEntity(post);
        newPost.setAuthor(userService.authenticatedUser(auth));
        Long id = postRepository.save(newPost).getId();
        post.setId(id);
        postService.updatePostJsonFromPostStepsSet(post);
        return postService.toDto(postRepository.getOne(id));
    }

    @PutMapping("{id}")
    ResponseEntity<?> editPost(
                Authentication auth, 
                @RequestBody PostDto post, 
                @PathVariable(value = "id") Long postId) {
        Optional<Post> postEntity = postRepository.findById(postId);
        if (postEntity.isEmpty()) {
            return new ResponseEntity<Object>(
                new ApiError(HttpStatus.NOT_FOUND, "Not Found"), 
                HttpStatus.NOT_FOUND);
        }
        Post editable = postEntity.get();
        if (editable.getAuthor().getId() != userService.authenticatedUser(auth).getId()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        postService.editEntity(post, editable);
        return ResponseEntity.ok().body(editable.getId());
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getPostById(@PathVariable(value = "id") Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (postRepository.findById(postId).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(postService.toDto(post.get()));
    }

    @DeleteMapping("/{id}") 
    void deletePostById (@PathVariable(value = "id") Long postId) {
        postRepository.deleteById(postId);
    }

}
