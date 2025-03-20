package com.iris.food_delivery.delivery_service.repository;

import com.iris.food_delivery.delivery_service.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, String> {
}
