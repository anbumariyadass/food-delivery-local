package com.iris.food_delivery.identity_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.iris.food_delivery.identity_service.jwt.JwtFilter;
import com.iris.food_delivery.identity_service.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
	
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/identity/register/**", "/identity/login", "/identity/checkUserAvailable/**", "/identity/ping").permitAll() // Public endpoints
	                .anyRequest().authenticated() // All other endpoints require authentication
	            )
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Ensures no session is stored
	            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class); // JWT authentication

	        return http.build();
	    }
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for hashing passwords
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }
    
    @Bean
    @Lazy // Breaks circular dependency
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

}

