package com.brunel.group19.Reuso.authentication.repository;

import com.brunel.group19.Reuso.model.UserModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
	UserModel findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}