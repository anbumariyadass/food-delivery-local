package com.iris.food_delivery.identity_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iris.food_delivery.identity_service.dto.ApiResponse;
import com.iris.food_delivery.identity_service.dto.AuthRequest;
import com.iris.food_delivery.identity_service.dto.UserDTO;
import com.iris.food_delivery.identity_service.model.User;
import com.iris.food_delivery.identity_service.service.IdentificationService;

@RestController
@CrossOrigin(origins = "*")  // Allows requests from any origin
@RequestMapping("/identity")
public class IdentityServiceController {
    @Autowired
    private IdentificationService authenticationService;
    
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse> ping() {
        return ResponseEntity.ok(new ApiResponse("Hello from Identity Service!", null));
    }
    
    //This method will be removed later once the admin user will be added into the application at realtime.
    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse> register() {
    	User user = new User();
    	user.setUsername("admin");
    	user.setPassword("admin");
    	user.setRole("ADMIN");
    	user.setActive("Y");
    	UserDTO userDTO = authenticationService.register(user);
    	return ResponseEntity.ok(new ApiResponse(user.getUsername() + " is created successfully",userDTO));
    	
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody User user) {
    	UserDTO userDTO = authenticationService.register(user);
    	return ResponseEntity.ok(new ApiResponse(user.getUsername() + " is created successfully",userDTO));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthRequest request) {
    	 UserDTO user = authenticationService.authenticate(request.getUsername(), request.getPassword());
    	 if (user != null && user.getToken() != null) {
             return ResponseEntity.ok(new ApiResponse("Login Successful", user));
         } else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                     .body(new ApiResponse("Invalid credentials", null));
         }
    }

    
    @GetMapping("/allUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllUsers() {       
        return ResponseEntity.ok(new ApiResponse("All Users Retrieval Success", authenticationService.getAllUsers()));
    }
    
    @GetMapping("/getUserInfo")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getUserInfo(@RequestParam String userName) {    
    	return ResponseEntity.ok(new ApiResponse("Retrieve User Information Success", authenticationService.getUserInfo(userName)));
    }
    
    @PutMapping("/updateUser")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody User user) {
    	return ResponseEntity.ok(new ApiResponse("User updated successfully", authenticationService.updateUser(user)));
    }
    
    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@RequestParam String userName) {
    	authenticationService.deleteUser(userName);
    	return ResponseEntity.ok(new ApiResponse("User deleted successfully", null));
    }
    
    @GetMapping("/checkUserAvailable/{userName}")
    public ResponseEntity<ApiResponse> checkUserAvailable(@PathVariable String userName) {
    	return ResponseEntity.ok(new ApiResponse(authenticationService.checkUserNameAvailable(userName), null));
    }
    
    @GetMapping("/testJWTAuth")
    public ResponseEntity<ApiResponse> testJWTAuth() {
    	return ResponseEntity.ok(new ApiResponse("Successfully authenticate the JWT token.", null));
    }
    
}
