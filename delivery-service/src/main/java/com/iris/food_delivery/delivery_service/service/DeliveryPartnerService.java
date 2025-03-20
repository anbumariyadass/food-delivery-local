package com.iris.food_delivery.delivery_service.service;

import com.iris.food_delivery.delivery_service.entity.DeliveryPartner;
import com.iris.food_delivery.delivery_service.repository.DeliveryPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryPartnerService {

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    // Create or Update Delivery Partner
    public DeliveryPartner saveDeliveryPartner(DeliveryPartner deliveryPartner) {
        return deliveryPartnerRepository.save(deliveryPartner);
    }

    // Get Delivery Partner by Username
    public Optional<DeliveryPartner> getDeliveryPartnerByUserName(String userName) {
        return deliveryPartnerRepository.findById(userName);
    }

    // Get All Delivery Partners
    public List<DeliveryPartner> getAllDeliveryPartners() {
        return deliveryPartnerRepository.findAll();
    }

    // Delete Delivery Partner
    public void deleteDeliveryPartner(String userName) {
        deliveryPartnerRepository.deleteById(userName);
    }
}
