package com.iris.food_delivery.order_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.iris.food_delivery.order_service.dto.ApiResponse;
import com.iris.food_delivery.order_service.entity.Order;
import com.iris.food_delivery.order_service.service.OrderService;
import com.iris.food_delivery.order_service.service.SqsService;
import com.iris.food_delivery.order_service.service.UserService;

@RestController
@CrossOrigin(origins = "*")  // Allows requests from any origin
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private SqsService sqsService;
	
	@Autowired
    private OrderService orderService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/ping")
	public ResponseEntity<ApiResponse> ping(){
		return ResponseEntity.ok(new ApiResponse("Hello from Order service...", null));
	}
	
	//Get All Orders
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllOrders() {
        List<Order> allOrders = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", allOrders));
    }

    //Get Order Details for the speicific Order ID
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId).get();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", order));
    }
    
    //Get all the orders for the customer
    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse> getAllOrdersForCustomer() {
    	List<Order> customerOrders = orderService.getAllOrdersForCustomer(userService.getLoggedInUser());
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", customerOrders));
    }
    
    //Get all the orders for the restaurant
    @GetMapping("/restaurant")
    @PreAuthorize("hasAuthority('ROLE_RESTAURANT')")
    public ResponseEntity<ApiResponse> getAllOrdersForRestaurant() {
    	List<Order> restaurantOrders = orderService.getAllOrdersForRestaurant(userService.getLoggedInUser());
    	return ResponseEntity.ok(new ApiResponse("SUCCESS", restaurantOrders));
    }
    
    //Get all the orders for the delivery partner
    @GetMapping("/dlvrypartner")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse> getAllOrdersForDeliveryPartner() {
    	List<Order> dlvryPartnerOrders = orderService.getAllOrdersForDeliveryPartner(userService.getLoggedInUser());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", dlvryPartnerOrders));
    }
    
    //Get all the not delivered orders for the customer
    @GetMapping("/customer/notdelivered")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse> getNotDeliveredOrdersForCustomer() {
    	List<Order> customerOrders = orderService.getNotDeliveredOrdersForCustomer(userService.getLoggedInUser());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", customerOrders));
    }
    
    //Get all the not delivered orders for the restaurant
    @GetMapping("/restaurant/notdelivered")
    @PreAuthorize("hasAuthority('ROLE_RESTAURANT')")
    public ResponseEntity<ApiResponse> getNotDeliveredOrdersForRestaurant() {
    	List<Order> restaurantOrders =  orderService.getNotDeliveredOrdersForRestaurant(userService.getLoggedInUser());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", restaurantOrders));
    }
    
    //Get all the not delivered orders for the delivery partner
    @GetMapping("/dlvrypartner/notdelivered")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse> getNotDeliveredOrdersForDeliveryPartner() {
    	List<Order> dlvryPartnerOrders =  orderService.getNotDeliveredOrdersForDeliveryPartner(userService.getLoggedInUser());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", dlvryPartnerOrders));
    }
    
    //Create Order
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.saveOrder(order);
        sqsService.sendMessage(createdOrder);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", createdOrder));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", null));
    }
    
    @PutMapping("/{orderId}/updateOrderStatus")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT', 'ROLE_DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String orderStatus) {
        
        orderService.updateOrderStatus(orderId, orderStatus);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", null));
    }
}
