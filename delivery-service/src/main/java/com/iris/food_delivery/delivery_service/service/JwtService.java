package com.iris.food_delivery.delivery_service.service;

import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtService {

    private final HttpServletRequest request;

    public JwtService(HttpServletRequest request) {
        this.request = request;
    }

    public String getJwtToken() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader; // Remove "Bearer " prefix
        }
        return null;
    }
}


