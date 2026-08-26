package com.substring.auth.auth_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;


    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(secret)
        );
    }


    // ==========================================
    // Generate JWT using email
    // ==========================================

    public String generateToken(String username) {

        return Jwts.builder()

                .subject(username)

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )

                .signWith(getSigningKey())

                .compact();
    }


    // ==========================================
    // Generate JWT using UserDetails
    // ==========================================

    public String generateToken(
            UserDetails userDetails
    ) {

        return generateToken(
                userDetails.getUsername()
        );
    }


    // ==========================================
    // Extract username/email
    // ==========================================

    public String extractUsername(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // ==========================================
    // Extract any claim
    // ==========================================

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return resolver.apply(claims);
    }


    // ==========================================
    // Validate token
    // ==========================================

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username =
                extractUsername(token);

        return username.equals(
                userDetails.getUsername()
        )
                && !isTokenExpired(token);
    }


    // ==========================================
    // Check expiration
    // ==========================================

    private boolean isTokenExpired(
            String token
    ) {

        Date expirationDate =
                extractClaim(
                        token,
                        Claims::getExpiration
                );

        return expirationDate.before(
                new Date()
        );
    }
}