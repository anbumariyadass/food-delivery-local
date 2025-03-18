package com.iris.food_delivery.identity_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.iris.food_delivery.identity_service.model.User;
import com.iris.food_delivery.identity_service.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.iris.food_delivery.identity_service.dto.UserDTO;
import com.iris.food_delivery.identity_service.execption.handler.UserNotFoundException;
import com.iris.food_delivery.identity_service.jwt.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IdentificationService {
	private static final Logger logger = LoggerFactory.getLogger(IdentificationService.class);
	
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    
    public String encodePassword(String password) {
        return passwordEncoder.encode(password); // Hash the password
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword); // Verify password
    }
    
    public UserDTO register(User user) {
    	Optional<User> userDetails = userRepository.findByUsername(user.getUsername());
    	if (userDetails.isPresent()) {
    		logger.error("{} is already present with the system. Please provide something else.", user.getUsername());
    		throw new UserNotFoundException(user.getUsername() + 
    				" is already present with the system. Please provide something else.");
    	}
        user.setPassword(encodePassword(user.getPassword()));
        userRepository.save(user);
        return new UserDTO(user.getUsername(), user.getRole(), user.getActive());
    }
    
    public UserDTO authenticate(String username, String password) {
        User userDetails = userRepository.findByUsername(username)
        		.orElseThrow(() -> new UserNotFoundException("User not found"));
        
        UserDTO user = new UserDTO();
        if (verifyPassword(password, userDetails.getPassword())) {
        	logger.info("{} logged successfully", username);
        	user.setUsername(userDetails.getUsername());
        	user.setRole(userDetails.getRole());
        	user.setActive(userDetails.getActive());
        	user.setToken(jwtUtil.generateToken(userDetails.getUsername(), userDetails.getRole()));
        } else {
        	logger.error("{} logged in failed", username);
        }
        return user;
    }
    
    public UserDTO updateUser(User user) {
    	User userDetail = userRepository.findByUsername(user.getUsername())
    			.orElseThrow(() -> new UserNotFoundException("User not found"));
    	
    	if (user.getPassword() != null && encodePassword(user.getPassword()) != userDetail.getPassword()) {
    		userDetail.setPassword(encodePassword(user.getPassword()));
    	}
    	
    	if (user.getRole() != null && user.getRole() != userDetail.getRole()) {
    		userDetail.setRole(user.getRole());
    	}
    	
    	if (user.getActive() != null && user.getActive() != userDetail.getActive()) {
    		userDetail.setActive(user.getActive());
    	}
    	
    	User userSaved = userRepository.save(userDetail);
    	
    	return new UserDTO(userSaved.getUsername(), userSaved.getRole(), userSaved.getActive());
    }
    
    @Transactional  
    public void deleteUser(String userName) {
    	logger.info("{} is deleted successfully.", userName);
    	userRepository.deleteByUsername(userName);
    }
    
    public List<UserDTO> getAllUsers() {
    	List<UserDTO> allUsers = new ArrayList<>();
    	 userRepository.findAll().forEach(user -> allUsers.add(
    			 new UserDTO(user.getUsername(), user.getRole(), user.getActive())));
         return allUsers;
    }
    
    public UserDTO getUserInfo(String userName) {
    	User user = userRepository.findByUsername(userName).orElseThrow(() -> new UserNotFoundException(userName + "not found."));
    	return new UserDTO(user.getUsername(), user.getRole(), user.getActive());
    }
    
    public String checkUserNameAvailable(String userName) {
    	Optional<User> user = userRepository.findByUsername(userName);
    	logger.info("Checking the {} is available to create the account.", userName);
        return user.isPresent() ? userName + " is not available for you to create the account. Please provide someother user name." : userName + " is available for you to create the account.";
   }
    
    public UserDetails getUserDetails(String userName) {
    	return userDetailsService.loadUserByUsername(userName);
    }
    
}
