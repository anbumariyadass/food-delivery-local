package com.iris.food_delivery.order_service.repository;


import  com.iris.food_delivery.order_service.entity.Order;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByCustomerUserNameOrderByOrderIdDesc(String customerUserName);
	
	List<Order> findByRestaurantUserNameOrderByOrderIdDesc(String restaurantUserName);
	
	List<Order> findByDlvryPartnerUserNameOrderByOrderIdDesc(String dlvryPartnerUserName);
	
	List<Order> findByCustomerUserNameAndOrderStatusNotOrderByOrderIdDesc(String customerUserName, String orderStatus);

	List<Order> findByRestaurantUserNameAndOrderStatusNotOrderByOrderIdDesc(String restaurantUserName, String orderStatus);

	List<Order> findByDlvryPartnerUserNameAndOrderStatusNotOrderByOrderIdDesc(String dlvryPartnerUserName, String orderStatus);
	
	@Modifying
    @Transactional
    @Query("UPDATE Order o SET o.orderStatus = :orderStatus, o.statusUpdtOn = CURRENT_TIMESTAMP WHERE o.orderId = :orderId")
    int updateOrderStatus(Long orderId, String orderStatus);

}
