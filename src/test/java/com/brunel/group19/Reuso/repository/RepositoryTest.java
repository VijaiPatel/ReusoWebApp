package com.brunel.group19.Reuso.repository;


import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import com.brunel.group19.Reuso.model.Post;
import com.brunel.group19.Reuso.model.UserModel;
import com.brunel.group19.Reuso.service.TagService;
import com.brunel.group19.Reuso.authentication.repository.UserRepository;

//@SpringBootTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RepositoryTest {

	@Autowired MockMvc mockMvcTest;
	@Autowired PostRepository samplePostRepositoryTest;
	@Autowired private TagService tagV;
	@Autowired private UserRepository sampleUserRepository;

	@Test
	public void testSavePost() {
		// Save a post
		UserModel mockUser = sampleUserRepository
				.save(new UserModel("user", "a@b.c", "pass"));
		Post testPost1 = new Post();
		testPost1.setAuthor(mockUser);

		String caption = "T-Shirt Tote Bag";
		testPost1.setCaption(caption);
		String[] arr = {"T-shirt", "bag"};
		Set<String> set = new HashSet<>(Arrays.asList(arr));
		testPost1.setTags(tagV.toSet(set));

		samplePostRepositoryTest.save(testPost1);
		samplePostRepositoryTest.flush();

		List<Post> dbcontents = samplePostRepositoryTest.findAll();
		for (Post post : dbcontents) {
			System.out.println(post.getId());
		}

		Post post2 = samplePostRepositoryTest.findById(testPost1.getId()).get();
		assertThat(post2).isNotNull();
		assertThat(post2.getAuthor()).isEqualTo(testPost1.getAuthor());
		assertThat(post2.getCaption().contains(testPost1.getCaption()));
		assertThat(post2.getTags()).isEqualTo(testPost1.getTags());

	}

	@Test
	public void testDeletePostById() {
		// Delete a post
		UserModel newUser = sampleUserRepository
				.save(new UserModel("user", "a@b.c", "pass"));

		Post newPost = new Post();
		String newCaption = "Bottle Cap Art";
		newPost.setCaption(newCaption);
		String[] newTags = {"Bottle", "cap"};
		newPost.setTags(tagV.toSet(new HashSet<>(Arrays.asList(newTags))));
		newUser.addPost(newPost);
		
		samplePostRepositoryTest.save(newPost);
		assertFalse(samplePostRepositoryTest.existsById(newPost.getId()));
		samplePostRepositoryTest.flush();
		
		assertThat(newPost.getId()).isNotNull();
		Post PostId = samplePostRepositoryTest.findById(newPost.getId()).orElseThrow();

		List<Post> dbcontents = samplePostRepositoryTest.findAll();
		for (Post post : dbcontents) {
			System.out.println(post.getId());
		}

		samplePostRepositoryTest.deleteById(PostId.getId());

	}


}



