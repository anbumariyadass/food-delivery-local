package com.iris.food_delivery.restaurant_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iris.food_delivery.restaurant_service.entity.Restaurant;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByUserName(String uname);
}

