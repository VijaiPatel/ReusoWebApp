package com.brunel.group19.Reuso.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tag")
public class Tag {
    @Id @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> taggedPosts;

    public Tag(){}

    public Tag(String tagName) {
        this.name = tagName; 
    }


    public Set<Post> getTaggedPosts() {
        return taggedPosts;
    }

    public void setTaggedPosts(Set<Post> taggedPosts) {
        this.taggedPosts = taggedPosts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
