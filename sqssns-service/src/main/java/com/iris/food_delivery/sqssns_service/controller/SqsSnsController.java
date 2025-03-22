package com.iris.food_delivery.sqssns_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iris.food_delivery.sqssns_service.service.SnsService;
import com.iris.food_delivery.sqssns_service.service.SqsService;
import com.iris.food_delivery.sqssns_service.dto.ApiResponse;
import com.iris.food_delivery.sqssns_service.dto.Message;
import com.iris.food_delivery.sqssns_service.dto.Order;
import com.iris.food_delivery.sqssns_service.dto.OrderDelivery;

@RestController
@CrossOrigin(origins = "*")  // Allows requests from any origin
@RequestMapping("/sqssns")
public class SqsSnsController {
	@Autowired
	private SnsService snsService;
	
	@Autowired
	private SqsService sqsService;
	
	@GetMapping("/ping")
	public ResponseEntity<ApiResponse> ping(){
		return ResponseEntity.ok(new ApiResponse("Hello from SQS and SNS service...", null));
	}
	
	@PostMapping("/notification/publishMessage")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse> publishMessage(@RequestBody Message message) {
		String response = snsService.publishMessage(message.getContent(), message.getSubject());
		return ResponseEntity.ok(new ApiResponse("SUCCESS", response));
	}
	
	@PostMapping("/notification/addSubscription/{mailId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse> addSubscription(@PathVariable String mailId) {
		String response = snsService.subscribeEmail(mailId);
		return ResponseEntity.ok(new ApiResponse("SUCCESS", response));
	}
	
	@PostMapping("/push/order")
	@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
	public ResponseEntity<ApiResponse> pushOrder(@RequestBody Order order) {
		String response = sqsService.sendMessage(order);
		return ResponseEntity.ok(new ApiResponse("SUCCESS", response));
	}
	
	@GetMapping("/poll/order")
	@PreAuthorize("hasAuthority('ROLE_DELIVERY_PARTNER')")
	public ResponseEntity<ApiResponse> pollOrder() {
		List<OrderDelivery> orderDelivery = sqsService.receiveMessages();
		return ResponseEntity.ok(new ApiResponse("SUCCESS", orderDelivery));
	}
}
