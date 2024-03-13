package com.brunel.group19.Reuso;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.brunel.group19.Reuso.authentication.repository.UserRepository;
import com.brunel.group19.Reuso.model.Post;
import com.brunel.group19.Reuso.model.Tag;
import com.brunel.group19.Reuso.model.UserModel;
import com.brunel.group19.Reuso.repository.PostRepository;
import com.brunel.group19.Reuso.repository.TagRepository;
import com.brunel.group19.Reuso.service.TagService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional 
public class SqlTests {

    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private TagService tagV;

    private String testUsername = "testusername";
    private String testEmail = "testemail@email.com";
    private String testPassword = "testpassword";
    private UserModel mockUser;

    @Before public void createMockUser() {
        if (!userRepository.existsByUsername(testUsername)) {
            mockUser = new UserModel(testUsername, testEmail, testPassword);
            mockUser = userRepository.save(mockUser);
        }
    }

    @Test public void findUserByName() {
        // Check that user model is added to DB and can be found by name
        assertEquals(
            testUsername,
            userRepository.findByUsername(testUsername).getUsername()
        );
    }

    @Test public void createPostWithCaption() {
        Post testPost = new Post();
        testPost.setAuthor(mockUser);

        postRepository.save(testPost);

        Post fetchPost = postRepository.findById(testPost.getId()).get();

        // Check if post is added to DB
        assertEquals(
            testPost.getId(), 
            fetchPost.getId()
        );
        // Check if post's Author is correctly added to Post entity
        assertEquals(
            testPost.getAuthor().getId(), 
            fetchPost.getAuthor().getId()
        );
        // Check post entity attributes (caption)
        // assertEquals();
    }

    @Test public void createPostWithTag() {
        Post testPost = new Post();
        testPost.setAuthor(mockUser);

        String[] arr = {"tag1", "another Tag2"};
        Set<String> set = new HashSet<>(Arrays.asList(arr));
		testPost.setTags(tagV.toSet(set));

        postRepository.save(testPost);
        
        Post resultPost = postRepository.findById(testPost.getId()).get();
        // Check post is in DB, can be found by id
        assertNotNull(resultPost);
        // Check post author correctly added 
        assertEquals(
            testPost.getAuthor().getId(), 
            resultPost.getAuthor().getId()
        );
        // Check tags are present in Post entity
        assertTrue(
            testPost.getTags()
            .containsAll(resultPost.getTags())
        );
    }

    @Test public void createDeleteUserWithPosts() {
        UserModel newUser = userRepository
            .save(new UserModel("user", "a@b.c", "pass"));

        Post newPost = new Post();
        String[] newTags = {"1TAG", "tag_2"};
        newPost.setTags(tagV.toSet(new HashSet<>(Arrays.asList(newTags))));
        newUser.addPost(newPost);
        
        List<Post> resultPosts = postRepository.findByAuthor_Username("user");
        // Check post caption is correct
        // assertEquals();
        
        resultPosts = postRepository.findByTags_Name("tag2");
        ArrayList<String> searchTags = new ArrayList<>();
        for (Tag tag : resultPosts.get(0).getTags()) {
            searchTags.add(tag.getName());
        }
        // Check tags are correct, and search Posts by Tags is working
        assertTrue(searchTags.contains("1tag"));
        assertTrue(searchTags.contains("tag2"));

        userRepository.delete(newUser);
        // Check user has been successfully deleted
        assertTrue(
            postRepository.findByAuthor_Username("user").isEmpty()
        );
        // Check post deleted from DB
        assertFalse(postRepository.existsById(newPost.getId()));
        // Check tags are still present in database
        assertTrue(tagRepository.existsByName("1tag"));
        assertTrue(tagRepository.existsByName("tag2"));
    }
}
