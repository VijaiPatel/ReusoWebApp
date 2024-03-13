package com.brunel.group19.Reuso.service;

import com.brunel.group19.Reuso.authentication.repository.UserRepository;
import com.brunel.group19.Reuso.model.UserDto;
import com.brunel.group19.Reuso.model.UserModel;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private ModelMapper mapper;
    
    public UserDto toDto(UserModel entity) {
        return mapper.map(entity, UserDto.class);
    }
    public UserModel toEntity (UserDto dto) {
        return mapper.map(dto, UserModel.class);
    }
    public UserModel authenticatedUser (Authentication auth) {
        return userRepository.findByUsername(auth.getPrincipal().toString());
    }
}
