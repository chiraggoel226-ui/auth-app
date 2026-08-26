package com.substring.auth.auth_app.controllers;

import com.substring.auth.auth_app.dtos.AuthResponse;
import com.substring.auth.auth_app.dtos.UserDto;
import com.substring.auth.auth_app.security.JwtService;
import com.substring.auth.auth_app.services.AuthService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;


    // ==============================
    // REGISTER
    // ==============================

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
            @RequestBody UserDto userDto) {

        UserDto registeredUser =
                authService.registerUser(userDto);

        return new ResponseEntity<>(
                registeredUser,
                HttpStatus.CREATED
        );
    }


    // ==============================
    // LOGIN
    // ==============================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(
            @RequestBody UserDto userDto) {

        UserDto user =
                authService.loginUser(
                        userDto.getEmail(),
                        userDto.getPassword()
                );


        // Generate JWT
        String token =
                jwtService.generateToken(
                        user.getEmail()
                );


        // Build response
        AuthResponse response =
                AuthResponse.builder()

                        .id(
                                user.getId() != null
                                        ? user.getId().toString()
                                        : null
                        )

                        .email(user.getEmail())

                        .name(user.getName())

                        .image(user.getImage())

                        .enable(user.isEnable())

                        .provider(user.getProvider())

                        .token(token)

                        .build();


        return ResponseEntity.ok(response);
    }
}