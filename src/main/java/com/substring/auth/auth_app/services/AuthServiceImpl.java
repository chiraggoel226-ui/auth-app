package com.substring.auth.auth_app.services;

import com.substring.auth.auth_app.dtos.UserDto;
import com.substring.auth.auth_app.exception.InvalidCredentialsException;
import com.substring.auth.auth_app.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    // ==========================================
    // REGISTER
    // ==========================================

    @Override
    public UserDto registerUser(UserDto userDto) {

        if (userDto.getPassword() == null
                || userDto.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        // Encode password before saving
        userDto.setPassword(
                passwordEncoder.encode(
                        userDto.getPassword()
                )
        );

        // Create user
        return userService.createUser(userDto);
    }


    // ==========================================
    // LOGIN
    // ==========================================

    @Override
    public UserDto loginUser(
            String email,
            String password
    ) {

        // Validate email
        if (email == null || email.isBlank()) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }


        // Validate password
        if (password == null || password.isBlank()) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }


        // Find user
        UserDto user;

        try {

            user = userService.getUserByEmail(email);

        } catch (ResourceNotFoundException e) {

            // Don't reveal whether email exists
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }


        // Check stored password
        if (user.getPassword() == null
                || !passwordEncoder.matches(
                password,
                user.getPassword()
        )) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }


        // Check whether account is enabled
        if (!user.isEnable()) {

            throw new InvalidCredentialsException(
                    "Account is disabled"
            );
        }


        // Login successful
        return user;
    }
}