package com.brunel.group19.Reuso.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.brunel.group19.Reuso.authentication.repository.UserRepository;
import com.brunel.group19.Reuso.repository.PostRepository;
import com.brunel.group19.Reuso.service.TagService;


@SpringBootTest
class ModelTest {

		@Autowired PostRepository samplePostRepositoryTest;
		@Autowired private UserRepository sampleUserRepository;
		@Autowired private TagService tagV;

		@Test
		public void testCaseOne() {
			
			// Test Case #1
			 UserModel mockUser1 = sampleUserRepository
			            .save(new UserModel("user1", "abc@.", "pass1"));

			Post testPost1 = new Post();
	        testPost1.setAuthor(mockUser1);

	        String caption1 = "Recycled Magazine Cork Board";
		    testPost1.setCaption(caption1);
		    
	        String[] arr = {"Board", "easy", "Magazine"};
	        Set<String> tag1 = new HashSet<>(Arrays.asList(arr));
			testPost1.setTags(tagV.toSet(tag1));
			
			samplePostRepositoryTest.save(testPost1);

			// Test if the informations are as expected
			assertThat(testPost1.getAuthor()).isEqualTo(mockUser1);
			assertThat(testPost1.getCaption()).isEqualTo(caption1);
			assertThat(testPost1.getTags()).isEqualTo(tag1);
			
			samplePostRepositoryTest.flush();

			List<Post> dbcontents = samplePostRepositoryTest.findAll();
			assertThat(dbcontents.size()).isEqualTo(2);

			// First the dummy element that is saved in database by DBRunner:
			assertThat(dbcontents.get(0).getAuthor()).isEqualTo(mockUser1);
			assertThat(dbcontents.get(0).getCaption()).isEqualTo("caption");
			assertThat(dbcontents.get(0).getTags()).isEqualTo("tags");

			// Now the above element:
			assertThat(dbcontents.get(0).getAuthor()).isEqualTo(mockUser1);
			assertThat(dbcontents.get(0).getCaption()).isEqualTo(caption1);
			assertThat(dbcontents.get(0).getTags()).isEqualTo(tag1);
		}

		@Test
		public void testCaseTwo() {
			
			// Test Case #2
			 UserModel mockUser2 = sampleUserRepository
			            .save(new UserModel("user2", "efg@.", "pass2"));
			 
			Post testPost2 = new Post();
	        testPost2.setAuthor(mockUser2);

	        
	        String caption2 = "Smashed Soda Can Animals";
		    testPost2.setCaption(caption2);
	        String[] arr = {"Soda can", "animal"};
	        Set<String> tag2 = new HashSet<>(Arrays.asList(arr));
			testPost2.setTags(tagV.toSet(tag2));

			samplePostRepositoryTest.save(testPost2);
			
			assertThat(testPost2.getAuthor()).isEqualTo(mockUser2);
			assertThat(testPost2.getCaption()).isEqualTo(caption2);
			assertThat(testPost2.getTags()).isEqualTo(tag2);
			samplePostRepositoryTest.flush();

			List<Post> dbcontents = samplePostRepositoryTest.findAll();
			assertThat(dbcontents.size()).isEqualTo(2);

			// First the dummy element that is saved in database by DBRunner:
			assertThat(dbcontents.get(0).getAuthor()).isEqualTo(mockUser2);
			assertThat(dbcontents.get(0).getCaption()).isEqualTo("caption");
			assertThat(dbcontents.get(0).getTags()).isEqualTo("tags");

			// Now the above element:
			assertThat(dbcontents.get(0).getAuthor()).isEqualTo(mockUser2);
			assertThat(dbcontents.get(0).getCaption()).isEqualTo(caption2);
			assertThat(dbcontents.get(0).getTags()).isEqualTo(tag2);

		}

		@Test
		public void testCaseThree() {

			// Test Case #3
			UserModel mockUser3 = sampleUserRepository
		            .save(new UserModel("user3", "hij@.", "pass3"));
			
			Post testPost3 = new Post();
	        testPost3.setAuthor(mockUser3);

	        
	        String caption3 = "Recycled Paper Bag Bracelets";
		    testPost3.setCaption(caption3);
	        String[] arr = {"Paper bag", "bracelet"};
	        Set<String> tag3 = new HashSet<>(Arrays.asList(arr));
			testPost3.setTags(tagV.toSet(tag3));

			samplePostRepositoryTest.save(testPost3);
			
			assertThat(testPost3.getAuthor()).isEqualTo(mockUser3);
			assertThat(testPost3.getCaption()).isEqualTo(caption3);
			assertThat(testPost3.getTags()).isEqualTo(tag3);
			
			samplePostRepositoryTest.flush();
			
			List<Post> dbcontents = samplePostRepositoryTest.findAll();
			assertThat(dbcontents.size()).isEqualTo(2);

			// First the dummy element that is saved in database by DBRunner:
			assertThat(dbcontents.get(0).getAuthor()).isEqualTo(mockUser3);
			assertThat(dbcontents.get(0).getCaption()).isEqualTo("caption");
			assertThat(dbcontents.get(0).getTags()).isEqualTo("tags");

			// Now the above element:
			assertThat(dbcontents.get(0).getAuthor()).isEqualTo(mockUser3);
			assertThat(dbcontents.get(0).getCaption()).isEqualTo(caption3);
			assertThat(dbcontents.get(0).getTags()).isEqualTo(tag3);

		}

}
	
	
	
	

	
