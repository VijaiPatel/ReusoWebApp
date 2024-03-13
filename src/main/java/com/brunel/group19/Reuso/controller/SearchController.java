package com.brunel.group19.Reuso.controller;

import java.util.Set;

import com.brunel.group19.Reuso.repository.PostRepository;
import com.brunel.group19.Reuso.service.PostService;
import com.brunel.group19.Reuso.service.TagService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired private TagService tagService;
    @Autowired private PostService postService;
    @Autowired private PostRepository postRepository;

    @PostMapping("/tags") @ResponseBody
    Set<String> searchExistingTags(@RequestBody String[] query) {
        return tagService.fuzzSearch(query);
    }

    @GetMapping("/posts") ResponseEntity<?> allPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        JSONArray result = new JSONArray();
        Pageable paging = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        postRepository.findAll(paging)
                .forEach(post -> {
                    JSONObject json = new JSONObject();
                    json.put("id", post.getId());
                    json.put("author", post.getAuthor().getUsername());
                    result.put(json);
                });
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }

    @PostMapping("/posts/tag/") ResponseEntity<?> postsByTag(
            @RequestBody Set<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        JSONArray result = new JSONArray();
        postService.findPostsByTags(tags)
                .forEach(postEnt -> {
                    result.put(postService.toDto(postEnt));
                });
        int len = result.length();
        int startIndex = (page < len/size) ? page : len/size;
        int endIndex = (page < len/size) ? size : len%size;
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toList().subList(startIndex, endIndex));
   }

   @GetMapping("/posts/id") ResponseEntity<?> postByUser(
		   @RequestParam Long id,
		   @RequestParam(defaultValue = "0") int page,
		   @RequestParam(defaultValue = "6") int size) {
		   JSONArray result = new JSONArray();
		   Pageable paging = PageRequest.of(
               page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		   postRepository.findByAuthor_Id(id, paging)
		    .forEach(post -> {
                JSONObject json = new JSONObject();
                json.put("id", post.getId());
                json.put("caption", post.getCaption());
                result.put(json);
		    });
		   return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
		}
}