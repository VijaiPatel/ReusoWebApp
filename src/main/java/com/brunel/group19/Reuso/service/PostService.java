package com.brunel.group19.Reuso.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.brunel.group19.Reuso.model.Post;
import com.brunel.group19.Reuso.model.PostDto;
import com.brunel.group19.Reuso.model.PostStep;
import com.brunel.group19.Reuso.model.Tag;
import com.brunel.group19.Reuso.repository.PostRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    @Autowired private TagService tagger;
    @Autowired private ModelMapper mapper;
    @Autowired private UserService userService;
    @Autowired private PostRepository postRepository;
    @Autowired private S3Service s3;

    public PostDto toDto(Post entity) {
        PostDto newDto = mapper.map(entity, PostDto.class);
        newDto.setAuthor(userService.toDto(entity.getAuthor()));
        newDto.setTags(tagger.toStringSet(entity.getTags()));
        newDto.setPostSteps(getPostStepsFromID(entity.getId()));
        return newDto;
    }

    public Post toEntity(PostDto dto) {
        Post newPost = mapper.map(dto, Post.class);
        newPost.setTags(tagger.toSet(dto.getTags()));
        return newPost;
    }

    public void editEntity(PostDto dto, Post entity) {
        mapper.map(dto, entity);
        updatePostJsonFromPostStepsSet(dto);
        Set<Tag> newTags = tagger.toSet(dto.getTags());
        entity.setTags(newTags);
        postRepository.save(entity);
    }

    public List<Post> findPostsByTags(Set<String> queries) {
        Set<String> tags = new HashSet<>();
        queries.forEach(q -> tags.addAll(tagger.fuzzSearch(q)));
        
        LinkedHashMap<Post, Integer> found = new LinkedHashMap<>();
        for (String tag: tags) {
            postRepository.findByTags_Name(tag)
                .forEach(post -> {
                    int score = (found.containsKey(post)) ? found.get(post)+1 : 1;
                    found.put(post, score);
                });
        }
        // sort the hashmap by values and return sorted list of post objects
        List<Post> listResult = new ArrayList<>();
        found.entrySet().stream()
            .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
            .forEach(k -> listResult.add(k.getKey()));
        
        return listResult;
    }

    public String updatePostJsonFromPostStepsSet(PostDto dto) {
        String key = "posts/" + dto.getId() + "/" + "post.json";
        try {
            ObjectMapper objMap = new ObjectMapper();
            String json = objMap.writeValueAsString(dto.getPostSteps());
            File jsonFile = File.createTempFile("values", ".json");
            FileWriter pen = new FileWriter(jsonFile);
            pen.write(json);
            pen.close();
            s3.upload(jsonFile, key);
            jsonFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    private ArrayList<PostStep> getPostStepsFromID(Long postId) {
        String key = "posts/"+postId+"/post.json";
        ArrayList<PostStep> postSteps = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            postSteps = objectMapper.readValue(
                s3.download(key), 
                new TypeReference<ArrayList<PostStep>>(){});
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postSteps;
    }
}
