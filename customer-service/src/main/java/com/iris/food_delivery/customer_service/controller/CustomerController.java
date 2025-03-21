package com.iris.food_delivery.customer_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iris.food_delivery.customer_service.dto.ApiResponse;
import com.iris.food_delivery.customer_service.service.CustomerService;
import com.iris.food_delivery.customer_service.service.UserService;

import com.iris.food_delivery.customer_service.entity.Customer;

@RestController
@CrossOrigin(origins = "*")  // Allows requests from any origin
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/ping")
	public ResponseEntity<ApiResponse> ping(){
		return ResponseEntity.ok(new ApiResponse("Hello from Customer service...", null));
	}
	
	@PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse> saveCustomer(@RequestBody Customer customer) {
    	Customer savedCustomer = customerService.saveCustomer(customer);
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", savedCustomer));
    }
    

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse> getCustomer() {
    	Customer customer = customerService.getCustomer(userService.getLoggedInUser());
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", customer));
    }
    
    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllCustomers() {
    	List<Customer> customers = customerService.getAllCustomer();
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", customers));
    }
    
    @DeleteMapping("/delete/{customerUserName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable String customerUserName ) {
    	customerService.deleteCustomer(customerUserName);
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", null));
    }
}
