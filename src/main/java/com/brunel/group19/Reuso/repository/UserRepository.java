package com.brunel.group19.Reuso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.brunel.group19.Reuso.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
	
	@Query("SELECT u FROM UserModel u WHERE u.email = ?1")
    public UserModel findByEmail(String email);

}