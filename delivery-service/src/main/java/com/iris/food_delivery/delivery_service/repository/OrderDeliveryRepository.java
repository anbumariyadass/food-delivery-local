package com.iris.food_delivery.delivery_service.repository;

import com.iris.food_delivery.delivery_service.entity.OrderDelivery;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {

    // Fetch all orders by delivery partner username
    List<OrderDelivery> findByDlvryPartnerUserNameOrderByOrderIdDesc(String dlvryPartnerUserName);
    
 // Fetch all orders by delivery partner username 
    List<OrderDelivery> findByDlvryPartnerUserNameAndOrderStatusNotOrderByOrderIdDesc(String restaurantName, String orderStatus);
    
    @Modifying
    @Transactional
    @Query("UPDATE OrderDelivery o SET o.orderStatus = :orderStatus WHERE o.orderId = :orderId")
    int updateOrderStatus(Long orderId, String orderStatus);

}
