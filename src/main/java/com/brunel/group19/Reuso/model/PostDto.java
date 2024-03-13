package com.brunel.group19.Reuso.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class PostDto {
    private Long id;
    private Date createdAt;
    private String caption;
    private ArrayList<PostStep> postSteps;
    private Set<String> tags;
    private UserDto author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public ArrayList<PostStep> getPostSteps() {
        return postSteps;
    }

    public void setPostSteps(ArrayList<PostStep> postSteps) {
        this.postSteps = postSteps;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

}
