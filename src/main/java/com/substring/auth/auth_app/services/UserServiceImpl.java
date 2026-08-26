package com.substring.auth.auth_app.services;

import com.substring.auth.auth_app.dtos.UserDto;
import com.substring.auth.auth_app.entities.User;
import com.substring.auth.auth_app.exception.ResourceNotFoundException;
import com.substring.auth.auth_app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor

public class UserServiceImpl implements UserService{

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ModelMapper modelMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail()==null || userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is Required");
        }
        if (userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
        User user= modelMapper.map(userDto,User.class);
        User savedUser= userRepository.save(user);

        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {

       User user= userRepository.findByEmail(email).orElseThrow(()-> new  ResourceNotFoundException("No user with this id"));
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        UUID uid= UUID.fromString(userId);
        User existingUser=userRepository.findById(uid).orElseThrow(()-> new  ResourceNotFoundException("No user with this id"));
        if(userDto.getEmail()==null) existingUser.setName(userDto.getName());
        if(userDto.getImage()==null) existingUser.setImage(userDto.getImage());
        if(userDto.getPassword()==null) existingUser.setPassword(userDto.getPassword());
        if(userDto.getProvider()==null) existingUser.setProvider(userDto.getProvider());
        existingUser.setEnable(userDto.isEnable());
        User updatedUser=userRepository.save(existingUser);

        return modelMapper.map(updatedUser,UserDto.class);
    }

    @Override
    public void deleteUserById(String userId) {

        UUID uuid = UUID.fromString(userId);
        User user= userRepository.findById(uuid).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);

    }

    @Override
    public UserDto getUserById(String userId) {
        User user=userRepository.findById(UUID.fromString(userId)).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public Iterable<UserDto> getAllUser() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user,UserDto.class)).toList();
    }
}
