package com.iris.food_delivery.delivery_service.repository;

import com.iris.food_delivery.delivery_service.entity.DeliveryOrderTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryOrderTrackerRepository extends JpaRepository<DeliveryOrderTracker, Long> {
    // Fetch all tracking records for a given orderId
	List<DeliveryOrderTracker> findByOrderIdOrderByTrackerIdAsc(Long orderId);
}

