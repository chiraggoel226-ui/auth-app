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
                // OAuth2 requires session
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

                                // NORMAL AUTH APIs
                                .requestMatchers(
                                        "/api/v1/auth/**"
                                )
                                .permitAll()

                                // OAuth2 authorization
                                .requestMatchers(
                                        "/oauth2/**"
                                )
                                .permitAll()

                                // OAuth2 callback
                                .requestMatchers(
                                        "/login/**"
                                )
                                .permitAll()

                                // CORS
                                .requestMatchers(
                                        HttpMethod.OPTIONS,
                                        "/**"
                                )
                                .permitAll()

                                // Everything else
                                .anyRequest()
                                .authenticated()
                )


                // ==========================================
                // EXCEPTION HANDLING
                //
                // FIX (was the bug):
                // ------------------------------------------
                // The old config used
                // defaultAuthenticationEntryPointFor(...) scoped
                // to "/api/**" only. Because .oauth2Login() is
                // also enabled below, Spring Security
                // internally registers its OWN default
                // AuthenticationEntryPoint
                // (LoginUrlAuthenticationEntryPoint -> "/login").
                //
                // Depending on internal configurer ordering,
                // that OAuth2 entry point can win over the
                // path-scoped one for ANY AuthenticationException
                // thrown during a request - including ones
                // thrown deep inside your controller/service
                // layer (e.g. from getUserByEmail()), even on a
                // permitAll() endpoint like /api/v1/auth/login.
                //
                // That is why POST /api/v1/auth/login was
                // returning "302 Found" with
                // "Location: https://.../login" instead of a
                // JSON 401/error response.
                //
                // FIX: make HttpStatusEntryPoint(401) the
                // single, UNCONDITIONAL default entry point for
                // the whole app. This is safe because OAuth2
                // redirects (e.g. /oauth2/authorization/google)
                // are triggered by explicit navigation to that
                // URL, NOT by AuthenticationException handling,
                // so this change does not break OAuth2 login.
                // ==========================================

                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint(
                                        new HttpStatusEntryPoint(
                                                HttpStatus.UNAUTHORIZED
                                        )
                                )
                )


                // ==========================================
                // DISABLE FORM LOGIN
                // ==========================================

                .formLogin(
                        form -> form.disable()
                )


                // ==========================================
                // DISABLE HTTP BASIC
                // ==========================================

                .httpBasic(
                        basic -> basic.disable()
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
                        "PATCH",
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