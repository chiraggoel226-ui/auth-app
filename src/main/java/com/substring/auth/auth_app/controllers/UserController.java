package com.substring.auth.auth_app.controllers;

import com.substring.auth.auth_app.dtos.UserDto;
import com.substring.auth.auth_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
     UserService userService;



    @PostMapping
    public ResponseEntity<?>  createUser(@RequestBody UserDto userDto){

    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));

    }
}
