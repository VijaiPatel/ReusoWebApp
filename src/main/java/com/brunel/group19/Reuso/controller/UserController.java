package com.brunel.group19.Reuso.controller;

import com.brunel.group19.Reuso.authentication.models.AuthUserDto;
import com.brunel.group19.Reuso.authentication.repository.UserRepository;
import com.brunel.group19.Reuso.model.UserModel;
import com.brunel.group19.Reuso.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String emailRegex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    private static final String usernameRegex = "^[a-zA-Z0-9._-]{3,16}$";

    @PostMapping("/sign-up") ResponseEntity<?> signUp(
            @RequestBody AuthUserDto user) {
        if (user.getEmail().matches(emailRegex) && user.getUsername().matches(usernameRegex)) {
            UserModel newUser = new UserModel();
            newUser.setUsername(user.getUsername());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(newUser);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/verify/username") ResponseEntity<?> checkUsernameExists(
            @RequestParam String q) {
        if (userRepository.existsByUsername(q)) {
            return new ResponseEntity<String>("Username already exists", HttpStatus.CONFLICT);
        } else return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verify/email") ResponseEntity<?> checkEmailExists(
            @RequestParam String q) {
        if (userRepository.existsByEmail(q)) {
            return new ResponseEntity<String>("Email address already used", HttpStatus.CONFLICT);
        } else return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/profile/private") UserModel getUser(Authentication auth) {
        return userService.authenticatedUser(auth);
    }

    @GetMapping("/profile/public/{user}") ResponseEntity<?> getPublicUser(
            @PathVariable(value = "user") String username) {
        UserModel entity = userRepository.findByUsername(username);
        if (entity != null) {
            return ResponseEntity.ok(userService.toDto(entity));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}