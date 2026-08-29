package com.substring.auth.auth_app.config;

import com.substring.auth.auth_app.dtos.UserDto;
import com.substring.auth.auth_app.entities.Provider;
import com.substring.auth.auth_app.security.JwtService;
import com.substring.auth.auth_app.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserService userService;

    private final JwtService jwtService;

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public OAuth2AuthenticationSuccessHandler(
            UserService userService,
            JwtService jwtService,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authorizedClientService = authorizedClientService;
    }


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {


        // ==========================================
        // GET OAUTH USER
        // ==========================================

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();


        // ==========================================
        // GET PROVIDER
        // ==========================================

        OAuth2AuthenticationToken oauth2AuthenticationToken =
                (OAuth2AuthenticationToken) authentication;

        String registrationId =
                oauth2AuthenticationToken
                        .getAuthorizedClientRegistrationId();


        // ==========================================
        // PROVIDER ENUM
        // ==========================================

        Provider provider;

        if ("google".equals(registrationId)) {

            provider = Provider.GOOGLE;

        } else if ("github".equals(registrationId)) {

            provider = Provider.GITHUB;

        } else {

            throw new IllegalArgumentException(
                    "Unsupported OAuth provider: "
                            + registrationId
            );
        }


        // ==========================================
        // GET USER INFORMATION
        // ==========================================

        String email =
                oauth2User.getAttribute("email");

        String name =
                oauth2User.getAttribute("name");

        String image;


        if ("google".equals(registrationId)) {

            image =
                    oauth2User.getAttribute("picture");

        } else {

            image =
                    oauth2User.getAttribute("avatar_url");
        }


        // ==========================================
        // GITHUB EMAIL
        // ==========================================

        if ("github".equals(registrationId)
                && email == null) {

            email =
                    getGithubEmail(
                            oauth2AuthenticationToken
                    );
        }


        // ==========================================
        // GITHUB NAME FALLBACK
        // ==========================================

        if ("github".equals(registrationId)
                && (name == null || name.isBlank())) {

            name =
                    oauth2User.getAttribute("login");
        }


        // ==========================================
        // PRINT USER INFORMATION
        // ==========================================

        System.out.println(
                "OAuth2 Login Successful!"
        );

        System.out.println(
                "Provider: " + provider
        );

        System.out.println(
                "Email: " + email
        );

        System.out.println(
                "Name: " + name
        );

        System.out.println(
                "Image: " + image
        );


        // ==========================================
        // EMAIL VALIDATION
        // ==========================================

        if (email == null || email.isBlank()) {

            throw new IllegalArgumentException(
                    "Email not found from "
                            + provider
                            + " OAuth account."
            );
        }


        // ==========================================
        // FIND OR CREATE USER
        // ==========================================

        UserDto user;

        try {

            user =
                    userService.getUserByEmail(email);

            System.out.println(
                    "User already exists in database."
            );

            System.out.println(
                    "User ID: " + user.getId()
            );

        } catch (Exception e) {

            System.out.println(
                    "User not found. Creating new user..."
            );

            UserDto newUser =
                    UserDto.builder()
                            .email(email)
                            .name(name)
                            .image(image)
                            .provider(provider)
                            .enable(true)
                            .build();

            user =
                    userService.createUser(
                            newUser
                    );

            System.out.println(
                    "New "
                            + provider
                            + " user created!"
            );

            System.out.println(
                    "User ID: " + user.getId()
            );
        }


        // ==========================================
        // GENERATE JWT
        // ==========================================

        String token =
                jwtService.generateToken(email);

        System.out.println(
                "JWT generated successfully!"
        );


        // ==========================================
        // REDIRECT TO REACT
        // ==========================================

        String redirectUrl =
                frontendUrl
                        + "/oauth2/success?token="
                        + token;

        response.sendRedirect(redirectUrl);

        System.out.println(
                "Redirecting to React: "
                        + redirectUrl
        );
    }


    // ==========================================
    // GET GITHUB EMAIL
    // ==========================================

    private String getGithubEmail(
            OAuth2AuthenticationToken authentication
    ) {

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        authentication
                                .getAuthorizedClientRegistrationId(),

                        authentication.getName()
                );


        if (authorizedClient == null) {

            throw new IllegalArgumentException(
                    "GitHub OAuth client not found."
            );
        }


        String accessToken =
                authorizedClient
                        .getAccessToken()
                        .getTokenValue();


        RestClient restClient =
                RestClient.create();


        List<Map<String, Object>> emails =
                restClient
                        .get()
                        .uri("https://api.github.com/user/emails")
                        .headers(headers ->
                                headers.setBearerAuth(
                                        accessToken
                                )
                        )
                        .retrieve()
                        .body(List.class);


        if (emails == null || emails.isEmpty()) {

            throw new IllegalArgumentException(
                    "No email found in GitHub account."
            );
        }


        // ==========================================
        // FIRST PRIORITY:
        // PRIMARY + VERIFIED EMAIL
        // ==========================================

        for (Map<String, Object> emailData : emails) {

            Boolean primary =
                    (Boolean) emailData.get("primary");

            Boolean verified =
                    (Boolean) emailData.get("verified");

            String email =
                    (String) emailData.get("email");


            if (
                    Boolean.TRUE.equals(primary)
                            &&
                            Boolean.TRUE.equals(verified)
                            &&
                            email != null
            ) {

                return email;
            }
        }


        // ==========================================
        // SECOND PRIORITY:
        // VERIFIED EMAIL
        // ==========================================

        for (Map<String, Object> emailData : emails) {

            Boolean verified =
                    (Boolean) emailData.get("verified");

            String email =
                    (String) emailData.get("email");


            if (
                    Boolean.TRUE.equals(verified)
                            &&
                            email != null
            ) {

                return email;
            }
        }


        throw new IllegalArgumentException(
                "No verified email found in GitHub account."
        );
    }
}