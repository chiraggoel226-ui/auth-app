package com.substring.auth.auth_app.Security;

import com.substring.auth.auth_app.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader =
                request.getHeader("Authorization");


        // ==========================================
        // NO JWT
        // ==========================================

        if (
                authHeader == null
                        || !authHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(request, response);

            return;
        }


        // ==========================================
        // EXTRACT TOKEN
        // ==========================================

        final String jwt =
                authHeader.substring(7);


        // Empty token
        if (jwt.isBlank()) {

            filterChain.doFilter(request, response);

            return;
        }


        try {

            // ==========================================
            // EXTRACT USERNAME
            // ==========================================

            String username =
                    jwtService.extractUsername(jwt);


            // ==========================================
            // USERNAME FOUND AND NOT AUTHENTICATED
            // ==========================================

            if (
                    username != null
                            &&
                            SecurityContextHolder
                                    .getContext()
                                    .getAuthentication()
                                    == null
            ) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);


                // ==========================================
                // VALIDATE TOKEN
                // ==========================================

                if (
                        jwtService.isTokenValid(
                                jwt,
                                userDetails
                        )
                ) {

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );
                }
            }

        } catch (Exception e) {

            // ==========================================
            // INVALID / EXPIRED JWT
            //
            // DO NOT REDIRECT TO /LOGIN
            // Simply continue as unauthenticated.
            // ==========================================

            SecurityContextHolder
                    .clearContext();
        }


        // ==========================================
        // CONTINUE REQUEST
        // ==========================================

        filterChain.doFilter(
                request,
                response
        );
    }
}