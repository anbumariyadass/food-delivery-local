package com.iris.food_delivery.delivery_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iris.food_delivery.delivery_service.dto.ApiResponse;
import com.iris.food_delivery.delivery_service.entity.DeliveryOrderTracker;
import com.iris.food_delivery.delivery_service.entity.OrderDelivery;
import com.iris.food_delivery.delivery_service.service.OrderDeliveryService;
import com.iris.food_delivery.delivery_service.service.SqsListenerService;
import com.iris.food_delivery.delivery_service.service.UserService;

@RestController
@CrossOrigin(origins = "*")  // Allows requests from any origin
@RequestMapping("/delivery")
public class DeliveryController {
	
	@Autowired
	private SqsListenerService sqsListenerService;
	
	@Autowired
	private OrderDeliveryService orderDeliveryService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/ping")
	public ResponseEntity<ApiResponse> ping(){
		return ResponseEntity.ok(new ApiResponse("Hello from Delivery service...", null));
	}
	
	//Receive orders from SQS
    @PostMapping("/processOrders")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse> receiveOrders() {
    	List<OrderDelivery> orderDeliveryList = sqsListenerService.receiveMessages();
    	orderDeliveryList.forEach(orderDelivery -> orderDeliveryService.saveOrderDelivery(orderDelivery));
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", null));
    }
    
    @GetMapping("/myorders")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse> getAllOrdersForDeliveryPartner() {
    	List<OrderDelivery> orderDeliveryList = orderDeliveryService.findOrdersByDeliveryPartner(userService.getLoggedInUser());
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", orderDeliveryList));
    }
    
    @GetMapping("/myorders/notdelivered")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse> getNotDeliveredOrdersForDeliveryPartner() {
    	List<OrderDelivery> orderDeliveryList = orderDeliveryService.findNotDeliveredOrdersByDeliveryPartner(userService.getLoggedInUser());
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", orderDeliveryList));
    }
    
    @PutMapping("/{orderId}/updateOrderStatus")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String orderStatus) {
        
    	orderDeliveryService.updateOrderStatus(orderId, orderStatus, userService.getLoggedInUser());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", null));
    }
    
    @GetMapping("/{orderId}/trackOrder")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse> getTrackOrderInfo(
            @PathVariable Long orderId) {
    	List<DeliveryOrderTracker> orderDeliveryTracker = orderDeliveryService.findAllTrackerInfoForOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", orderDeliveryTracker));
    }

}
