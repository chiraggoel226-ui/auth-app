package com.substring.auth.auth_app.services;

import com.substring.auth.auth_app.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto userDto) {

        // Encode password before saving
        userDto.setPassword(
                passwordEncoder.encode(userDto.getPassword())
        );

        // Create user
        return userService.createUser(userDto);
    }

    @Override
    public UserDto loginUser(String email, String password) {

        // Find user by email
        UserDto user = userService.getUserByEmail(email);

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Return user after successful login
        return user;
    }
}