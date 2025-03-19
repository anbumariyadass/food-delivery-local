package com.iris.food_delivery.restaurant_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iris.food_delivery.restaurant_service.entity.Dish;

import jakarta.transaction.Transactional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
	@Transactional
    void deleteByRestaurantId(Long restaurantId);
}

