package com.substring.auth.auth_app.config;

import lombok.AllArgsConstructor;

import com.substring.auth.auth_app.Security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@AllArgsConstructor
public class SecurityConfig {


    private final OAuth2AuthenticationSuccessHandler
            successHandler;


    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        http

                // ============================
                // CORS
                // ============================

                .cors(
                        Customizer.withDefaults()
                )


                // ============================
                // CSRF
                // ============================

                .csrf(
                        csrf -> csrf.disable()
                )


                // ============================
                // SESSION
                // ============================

                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.IF_REQUIRED
                                )
                )


                // ============================
                // AUTHORIZATION
                // ============================

                .authorizeHttpRequests(

                        authorize -> authorize

                                // Register
                                .requestMatchers(
                                        "/api/v1/auth/register"
                                )
                                .permitAll()


                                // Normal Login
                                .requestMatchers(
                                        "/api/v1/auth/login"
                                )
                                .permitAll()


                                // OAuth2 Authorization
                                .requestMatchers(
                                        "/oauth2/**"
                                )
                                .permitAll()


                                // OAuth2 Callback
                                .requestMatchers(
                                        "/login/**"
                                )
                                .permitAll()


                                // Everything else
                                .anyRequest()
                                .authenticated()
                )


                // ============================
                // JWT FILTER
                // ============================

                .addFilterBefore(

                        jwtAuthenticationFilter,

                        UsernamePasswordAuthenticationFilter.class
                )


                // ============================
                // OAUTH2 LOGIN
                // ============================

                .oauth2Login(

                        oauth2 -> oauth2

                                .successHandler(
                                        successHandler
                                )
                );


        return http.build();
    }


    // ================================
    // PASSWORD ENCODER
    // ================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ================================
    // CORS CONFIGURATION
    // ================================

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {


        CorsConfiguration configuration =
                new CorsConfiguration();


        // ================================
        // ALLOWED FRONTENDS
        // ================================

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://auth-app-frontend-eta.vercel.app"
                )
        );


        // ================================
        // ALLOWED METHODS
        // ================================

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );


        // ================================
        // ALLOWED HEADERS
        // ================================

        configuration.setAllowedHeaders(
                List.of("*")
        );


        // ================================
        // CREDENTIALS
        // ================================

        configuration.setAllowCredentials(
                true
        );


        // ================================
        // REGISTER CORS
        // ================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}