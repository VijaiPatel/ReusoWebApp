package com.brunel.group19.Reuso.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

public class PostControllerRestTemplateTest {

	@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
	class HelloControllerRestTemplateTest 
	{
		@Autowired
		private TestRestTemplate restTemplate;

		@Test
		public void testPostController(){
			assertNotNull(restTemplate.getForObject("http://localhost:8080/posts", String.class));
			
		}

	}

}
