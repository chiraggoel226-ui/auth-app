package com.substring.auth.auth_app.services;

import com.substring.auth.auth_app.dtos.UserDto;

public interface UserService {



    UserDto createUser(UserDto userDto);


    UserDto getUserByEmail(String email);


    UserDto updateUser(UserDto userDto , String userId);


    void deleteUserById(String userId);


    UserDto getUserById(String userId);


    Iterable<UserDto> getAllUser();







}
