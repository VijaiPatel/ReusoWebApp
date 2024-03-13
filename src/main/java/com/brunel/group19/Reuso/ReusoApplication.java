package com.brunel.group19.Reuso;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class ReusoApplication {

    @Bean public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean public ModelMapper modelMapper() {
        ModelMapper map = new ModelMapper();
        map.getConfiguration().setSkipNullEnabled(true);
        return map;
    }
	public static void main(String[] args) {
		SpringApplication.run(ReusoApplication.class, args);
	}

}
