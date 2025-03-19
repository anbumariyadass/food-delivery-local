package com.iris.food_delivery.restaurant_service.jwt;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    
    private SecretKey getSigningSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // Use UTF-8 encoding

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
    
    public List<String> extractUserRole(String token) {
    	List<String> roles = new ArrayList<>();
    	roles.add("ROLE_" + getClaims(token).get("role", String.class)); // Extracts 'role' claim
    	return roles;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
        		.verifyWith(getSigningSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

