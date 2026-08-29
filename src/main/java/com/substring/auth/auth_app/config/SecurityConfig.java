package com.substring.auth.auth_app.config;

import com.substring.auth.auth_app.Security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@AllArgsConstructor
public class SecurityConfig {


    private final OAuth2AuthenticationSuccessHandler successHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        http

                // ==========================================
                // CORS
                // ==========================================

                .cors(
                        Customizer.withDefaults()
                )


                // ==========================================
                // CSRF
                // ==========================================

                .csrf(
                        csrf -> csrf.disable()
                )


                // ==========================================
                // SESSION
                // ==========================================

                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.IF_REQUIRED
                                )
                )


                // ==========================================
                // AUTHORIZATION
                // ==========================================

                .authorizeHttpRequests(

                        authorize -> authorize


                                // ==================================
                                // NORMAL AUTH APIs
                                // ==================================

                                .requestMatchers(
                                        "/api/v1/auth/**"
                                )
                                .permitAll()


                                // ==================================
                                // OAUTH2
                                // ==================================

                                .requestMatchers(
                                        "/oauth2/**"
                                )
                                .permitAll()


                                .requestMatchers(
                                        "/login/**"
                                )
                                .permitAll()


                                // ==================================
                                // OPTIONS / CORS
                                // ==================================

                                .requestMatchers(
                                        HttpMethod.OPTIONS,
                                        "/**"
                                )
                                .permitAll()


                                // ==================================
                                // EVERYTHING ELSE
                                // ==================================

                                .anyRequest()
                                .authenticated()
                )


                // ==========================================
                // API 401 INSTEAD OF OAUTH LOGIN PAGE
                // ==========================================

                .exceptionHandling(
                        exception -> exception
                                .defaultAuthenticationEntryPointFor(
                                        new HttpStatusEntryPoint(
                                                HttpStatus.UNAUTHORIZED
                                        ),
                                        request ->
                                                request.getRequestURI()
                                                        .startsWith("/api/")
                                )
                )


                // ==========================================
                // JWT FILTER
                // ==========================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )


                // ==========================================
                // OAUTH2 LOGIN
                // ==========================================

                .oauth2Login(
                        oauth2 -> oauth2
                                .successHandler(
                                        successHandler
                                )
                );


        return http.build();
    }


    // ==========================================
    // PASSWORD ENCODER
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ==========================================
    // CORS
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://auth-app-frontend-eta.vercel.app"
                )
        );


        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );


        configuration.setAllowedHeaders(
                List.of("*")
        );


        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}