package com.brunel.group19.Reuso.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "post")
public class Post extends Audit {
	private static final long serialVersionUID = 3161098052126322071L;

	@Id @GeneratedValue
    @Column(name ="id", nullable = false, unique = true)
    private Long id;	
	
	@Column(name ="caption")
	private String caption;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "post_tag", 
		joinColumns = @JoinColumn(name = "post_id"), 
		inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags;
	
	@ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
	private UserModel author;
	
	public Post() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public UserModel getAuthor() {
		return author;
	}

	public void setAuthor(UserModel author) {
		this.author = author;
	}

}
