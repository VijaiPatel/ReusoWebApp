package com.brunel.group19.Reuso.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "user")
public class UserModel extends Audit {
    private static final long serialVersionUID = 7450833684846285585L;

    @Id @GeneratedValue @Column(name ="id", nullable = false, unique = true)
    private Long id;

    @NotBlank @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank @Email @Column(name = "email", unique = true)
    private String email;

    @NotBlank @JsonIgnore @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "firstname")
    private String firstname;  
    
    @Column(name = "lastname")
    private String lastname;
    
    @Column(name = "contactno", unique = true)
    private String contactno;
    
    @Column(name = "dob") @Temporal(TemporalType.DATE)
    private Date dob;
    
    @Column(name = "gender")
	private String gender;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "status")
    private String status;
    
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Post> posts;
    
    public UserModel() {
        this.posts = new HashSet<Post>();
    }

    public UserModel(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.posts = new HashSet<Post>();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getContactno() {
		return contactno;
	}

	public void setContactno(String contactno) {
		this.contactno = contactno;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public void addPost(Post post) {
        post.setAuthor(this);
        this.posts.add(post);
    }
}
