package com.iris.food_delivery.identity_service.jwt;


import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expirationMs}")
    private String expireTimeInMilliSec;
    
    private SecretKey getSigningSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // Use UTF-8 encoding

    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claims(Map.of("role", role))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(expireTimeInMilliSec))) 
                .signWith(getSigningSecretKey())
                .compact();
    }

    public String extractUsername(String token) {
    	return Jwts.parser()
    			.verifyWith(getSigningSecretKey())
                .build()
                .parseSignedClaims(token)  // Updated method
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parser()
        		.verifyWith(getSigningSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
    
    public String extractUserRole(String token) {
        return getClaims(token).get("role", String.class); // Extracts 'role' claim
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
        		.verifyWith(getSigningSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

