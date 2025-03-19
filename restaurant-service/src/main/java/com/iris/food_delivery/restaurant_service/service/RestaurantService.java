package com.iris.food_delivery.restaurant_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iris.food_delivery.restaurant_service.entity.Dish;
import com.iris.food_delivery.restaurant_service.entity.Restaurant;
import com.iris.food_delivery.restaurant_service.exception.handler.RestaurantNotFoundException;
import com.iris.food_delivery.restaurant_service.repository.DishRepository;
import com.iris.food_delivery.restaurant_service.repository.RestaurantRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private DishRepository dishRepository;
    
    public Restaurant saveRestaurant(Restaurant restaurant) {
    	 if (restaurantRepository.findByUserName(restaurant.getUserName()).isPresent()) {
    		 throw new RestaurantNotFoundException("Restaurant already found for " + restaurant.getUserName());
         } else {
        	// Ensure dishes are correctly associated with the restaurant
             if (restaurant.getDishes() != null) {
                 restaurant.getDishes().forEach(dish -> dish.setRestaurant(restaurant));
             }
             return restaurantRepository.save(restaurant);
         }
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
    
    public Restaurant getRestaurant(String restaurantUserName) {
    	Optional<Restaurant> existingRestaurantOpt = restaurantRepository.findByUserName(restaurantUserName);
        
        if (existingRestaurantOpt.isPresent()) {
            return existingRestaurantOpt.get();
        } else {
            throw new RestaurantNotFoundException("Restaurant not found for " + restaurantUserName);
        }
    }
    
    
    @Transactional
    public Restaurant updateRestaurant(String restaurantUserName, Restaurant updatedRestaurant) {
        Optional<Restaurant> existingRestaurantOpt = restaurantRepository.findByUserName(restaurantUserName);
        
        if (existingRestaurantOpt.isPresent()) {
            Restaurant existingRestaurant = existingRestaurantOpt.get();

            // Update basic fields
            existingRestaurant.setName(updatedRestaurant.getName());
            existingRestaurant.setAddress(updatedRestaurant.getAddress());
            existingRestaurant.setPhone(updatedRestaurant.getPhone());
            existingRestaurant.setCuisine(updatedRestaurant.getCuisine());
            existingRestaurant.setRating(updatedRestaurant.getRating());
            existingRestaurant.setOpeningHours(updatedRestaurant.getOpeningHours());
            existingRestaurant.setDeliveryOptions(updatedRestaurant.getDeliveryOptions());
            existingRestaurant.setAveragePricePerPerson(updatedRestaurant.getAveragePricePerPerson());

            // Update dishes
            if (updatedRestaurant.getDishes() != null) {
                List<Dish> newDishes = updatedRestaurant.getDishes();

                // Remove dishes that are not in the updated list
                existingRestaurant.getDishes().removeIf(existingDish -> 
                    newDishes.stream().noneMatch(newDish -> newDish.getId() != null &&
                            newDish.getId().equals(existingDish.getId()))
                );

                // Add or update dishes
                for (Dish newDish : newDishes) {
                    if (newDish.getId() == null) {
                        // New dish: Add to the restaurant
                        newDish.setRestaurant(existingRestaurant);
                        existingRestaurant.getDishes().add(newDish);
                    } else {
                        // Existing dish: Update details
                        Optional<Dish> existingDishOpt = dishRepository.findById(newDish.getId());
                        existingDishOpt.ifPresent(existingDish -> {
                            existingDish.setName(newDish.getName());
                            existingDish.setDescription(newDish.getDescription());
                            existingDish.setPrice(newDish.getPrice());
                        });
                    }
                }
            }

            return restaurantRepository.save(existingRestaurant);
        } else {
            throw new RestaurantNotFoundException("Restaurant not found for " + restaurantUserName);
        }
    }
    
    public List<Dish> getDishesByRestaurant(String restaurantUserName) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByUserName(restaurantUserName);
        if (restaurantOpt.isPresent()) {
            return restaurantOpt.get().getDishes();
        } else {
            throw new RestaurantNotFoundException("Restaurant not found for " + restaurantUserName);
        }
    }

    @Transactional
    public List<Dish> updateDishesForRestaurant(String restaurantUserName, List<Dish> updatedDishes) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByUserName(restaurantUserName);

        if (restaurantOpt.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();

            // Remove old dishes that are not in the updated list
            restaurant.getDishes().removeIf(existingDish -> 
                updatedDishes.stream().noneMatch(newDish -> newDish.getId() != null &&
                        newDish.getId().equals(existingDish.getId()))
            );

            // Add or update dishes
            for (Dish newDish : updatedDishes) {
                if (newDish.getId() == null) {
                    // New dish: Add to the restaurant
                    newDish.setRestaurant(restaurant);
                    restaurant.getDishes().add(newDish);
                } else {
                    // Existing dish: Update details
                    Optional<Dish> existingDishOpt = dishRepository.findById(newDish.getId());
                    existingDishOpt.ifPresent(existingDish -> {
                        existingDish.setName(newDish.getName());
                        existingDish.setDescription(newDish.getDescription());
                        existingDish.setPrice(newDish.getPrice());
                    });
                }
            }

            restaurantRepository.save(restaurant);
            return restaurant.getDishes();
        } else {
            throw new RestaurantNotFoundException("Restaurant not found for " + restaurantUserName);
        }
    }
    
    @Transactional
    public void deleteRestaurantByUserName(String restaurantUserName) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByUserName(restaurantUserName);
        if (restaurantOpt.isPresent()) {
            restaurantRepository.deleteById(restaurantOpt.get().getId());
        } else {
            throw new RestaurantNotFoundException("Restaurant not found for " + restaurantUserName);
        }
    }

    @Transactional
    public void deleteAllDishesForRestaurant(String restaurantUserName) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByUserName(restaurantUserName);
        if (restaurantOpt.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();
            
            // Remove all dishes from the restaurant
            restaurant.getDishes().clear();
            
            // Save the updated restaurant without dishes
            restaurantRepository.save(restaurant);

            // Delete all dishes from the database for this restaurant
            dishRepository.deleteByRestaurantId(restaurantOpt.get().getId());
        } else {
            throw new RestaurantNotFoundException("Restaurant not found for " + restaurantUserName);
        }
    }

}

