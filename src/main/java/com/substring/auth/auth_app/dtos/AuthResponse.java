package com.substring.auth.auth_app.dtos;

import com.substring.auth.auth_app.entities.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String id;

    private String email;

    private String name;

    private String image;

    private boolean enable;

    private Provider provider;

    private String token;
}