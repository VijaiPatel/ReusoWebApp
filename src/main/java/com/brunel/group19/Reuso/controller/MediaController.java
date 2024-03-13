package com.brunel.group19.Reuso.controller;

import java.util.Optional;

import com.brunel.group19.Reuso.model.Post;
import com.brunel.group19.Reuso.model.UserModel;
import com.brunel.group19.Reuso.repository.PostRepository;
import com.brunel.group19.Reuso.service.PostService;
import com.brunel.group19.Reuso.service.S3Service;
import com.brunel.group19.Reuso.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaController {
    @Autowired S3Service s3;
    @Autowired UserService userService;
    @Autowired PostRepository postRepository;
    @Autowired PostService postService;

    @GetMapping(value = "posts/{id}/{key}")
    ResponseEntity<byte[]> getMedia(
                @PathVariable(value = "id") String id, 
                @PathVariable(value = "key") String key) {
        String key_path = "posts/" + id + "/" + key;
        try {
            byte[] data = s3.downloadToBytes(key_path);
            if (data == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(data); 
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "posts/{id}/{key}")
    ResponseEntity<?> postMedia(
                @PathVariable(value = "id") Long id, 
                @PathVariable(value = "key") String key,
                @RequestParam("image") MultipartFile file,
                Authentication auth) {
        UserModel user = userService.authenticatedUser(auth);
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (post.get().getAuthor().getId() != user.getId()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String key_path = "posts/" + id + "/" + key;
        try {
            s3.upload(file, key_path);
        } catch (Exception e) {
            System.err.println(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
