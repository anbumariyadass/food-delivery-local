package com.iris.food_delivery.restaurant_service.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Extract username
        } else {
            return principal.toString(); // If username is a plain string
        }
    }

}

