package com.iris.food_delivery.order_service.service;


import com.iris.food_delivery.order_service.entity.Order;
import com.iris.food_delivery.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
	
	private final static String DELIVERED_STATUS = "DELIVERED";

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderId"));
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    public List<Order> getAllOrdersForCustomer(String customerUserName) {
    	return orderRepository.findByCustomerUserNameOrderByOrderIdDesc(customerUserName);
    }
    
    public List<Order> getAllOrdersForRestaurant(String restaurantUserName) {
    	return orderRepository.findByRestaurantUserNameOrderByOrderIdDesc(restaurantUserName);
    }
    
    public List<Order> getAllOrdersForDeliveryPartner(String dlvryPartnerUserName) {
    	return orderRepository.findByDlvryPartnerUserNameOrderByOrderIdDesc(dlvryPartnerUserName);
    }
    
    public List<Order> getNotDeliveredOrdersForCustomer(String customerUserName) {
    	return orderRepository.findByCustomerUserNameAndOrderStatusNotOrderByOrderIdDesc(customerUserName, DELIVERED_STATUS);
    }
    
    public List<Order> getNotDeliveredOrdersForRestaurant(String restaurantUserName) {
    	return orderRepository.findByRestaurantUserNameAndOrderStatusNotOrderByOrderIdDesc(restaurantUserName, DELIVERED_STATUS);
    }
    
    public List<Order> getNotDeliveredOrdersForDeliveryPartner(String dlvryPartnerUserName) {
    	return orderRepository.findByDlvryPartnerUserNameAndOrderStatusNotOrderByOrderIdDesc(dlvryPartnerUserName, DELIVERED_STATUS);
    }
    

    public Order saveOrder(Order order) {
    	order.setOrderStatus("ORDERED");
    	// Ensure order details are correctly associated with the order
        if (order.getOrderDetails() != null) {
        	order.getOrderDetails().forEach(orderDtl -> orderDtl.setOrder(order));
        }
        return orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
    
    
    @Transactional
    public void updateOrderStatus(Long orderId, String orderStatus) {
        int updatedRows = orderRepository.updateOrderStatus(orderId, orderStatus);
        if (updatedRows == 0) {
            throw new RuntimeException("Order not found or update failed for orderId: " + orderId);
        }
    }
}

