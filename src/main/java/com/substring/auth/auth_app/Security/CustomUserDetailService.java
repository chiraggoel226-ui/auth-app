package com.substring.auth.auth_app.Security;

import com.substring.auth.auth_app.entities.User;
import com.substring.auth.auth_app.exception.ResourceNotFoundException;
import com.substring.auth.auth_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

     private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userRepository.findByEmail(username).orElseThrow(()->new ResourceNotFoundException("username not found.notExists"));
        return user;
    }
}
