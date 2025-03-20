package com.iris.food_delivery.delivery_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.iris.food_delivery.delivery_service.dto.ApiResponse;
import com.iris.food_delivery.delivery_service.dto.OrderInfo;
import com.iris.food_delivery.delivery_service.service.SqsListenerService;

@RestController
@CrossOrigin(origins = "*")  // Allows requests from any origin
@RequestMapping("/delivery")
public class DeliveryController {
	
	@Autowired
	private SqsListenerService sqsListenerService;
	
	@GetMapping("/ping")
	public ResponseEntity<ApiResponse> ping(){
		return ResponseEntity.ok(new ApiResponse("Hello from Delivery service...", null));
	}
	
	//Receive orders from SQS
    @GetMapping("/receiveOrders")
    public ResponseEntity<ApiResponse> receiveOrders() {
    	List<OrderInfo> orderInfoList = sqsListenerService.receiveMessages();
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", orderInfoList));
    }

}
