package com.brunel.group19.Reuso.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.brunel.group19.Reuso.model.Post;
import com.brunel.group19.Reuso.model.UserModel;
import com.brunel.group19.Reuso.repository.PostRepository;
import com.brunel.group19.Reuso.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {

	@Autowired MockMvc mockMvcTest;
	@Autowired PostRepository samplePostRepositoryTest;
	@Autowired private TagService tagV;
	
	private UserModel mockUser;
	private String testAuthorname = "testauthorname";
	private String testTagsName = "testtagsname";
	
	
	@Test
	public void findAllPost() throws Exception {
		this.mockMvcTest.perform(get("/posts"))
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void findPostById() throws Exception{
		List<Post> dbcontents = samplePostRepositoryTest.findAll();
		mockMvcTest.perform(get("posts/{id}/{key}", dbcontents.get(0).getId())).andExpect(status().is2xxSuccessful()); 
		
	}
	
	@Test 
	public void findPostByTagsName() {
		assertEquals(testTagsName,
				((UserModel) samplePostRepositoryTest.findByTags_Name(testTagsName)).getUsername()
				);
	}

	@Test 
	public void findPostByAuthorUsername() {
		assertEquals(testAuthorname,
				((UserModel) samplePostRepositoryTest.findByAuthor_Username(testAuthorname)).getUsername()
				);
	}

	@Test
	public void createNewPost() throws Exception {
		Post testPost = new Post();
        testPost.setAuthor(mockUser);

        String[] arr = {"Plastic", "easy"};
        Set<String> set = new HashSet<>(Arrays.asList(arr));
		testPost.setTags(tagV.toSet(set));
		String caption = "Reuse Plastic";
	    testPost.setCaption(caption);

		samplePostRepositoryTest.save(testPost);
        
      
		ObjectMapper mapper = new ObjectMapper();
		String jsonPostTest = mapper.writeValueAsString(testPost);

		
		mockMvcTest
				.perform(post("/posts").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(jsonPostTest))
				
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.post").value(testPost.getAuthor()))
				.andExpect(jsonPath("$.caption").value(testPost.getCaption()))
				.andExpect(jsonPath("$.tag").value(testPost.getTags())).andReturn();

	}
	
	@Test
	public void deleteExistingPost() throws Exception {
		
		List<Post> dbcontents = samplePostRepositoryTest.findAll();
		mockMvcTest.perform(delete("/posts/{id}", dbcontents.get(0).getId())).andExpect(status().is2xxSuccessful());

	}

}



