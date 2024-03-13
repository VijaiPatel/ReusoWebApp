package com.brunel.group19.Reuso;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.brunel.group19.Reuso.model.UserModel;
import com.brunel.group19.Reuso.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReusoApplicationTests {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository repo;

	@Test
	public void testCreateUser() {
		UserModel user = new UserModel();
		user.setEmail("johndoe@gmail.com");
		user.setPassword("johndoe21");
		user.setFirstname("John");
		user.setLastname("Doe");

		UserModel savedUser = repo.save(user);

		UserModel existUser = entityManager.find(UserModel.class, savedUser.getId());

		assertThat(user.getEmail()).isEqualTo(existUser.getEmail());

	}
	
	@Test
	public void testFindUserByEmail() {
		String email = "johndoe@gmail.com";
		
		UserModel user = repo.findByEmail(email);
		
		assertThat(user).isNotNull();
	}

}