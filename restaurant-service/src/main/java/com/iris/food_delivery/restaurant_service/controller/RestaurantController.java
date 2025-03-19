package com.iris.food_delivery.restaurant_service.controller;

import java.util.List;

import com.iris.food_delivery.restaurant_service.dto.ApiResponse;
import com.iris.food_delivery.restaurant_service.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.iris.food_delivery.restaurant_service.entity.Restaurant;
import com.iris.food_delivery.restaurant_service.service.RestaurantService;
import com.iris.food_delivery.restaurant_service.service.UserService;

@RestController
@CrossOrigin(origins = "*")  // Allows requests from any origin
@RequestMapping("/restaurant")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private UserService userService;

	@GetMapping("/ping")
	public ResponseEntity<ApiResponse> ping(){
		return ResponseEntity.ok(new ApiResponse("Hello from Restaurant service...", null));
	}
	
	//Add Restaurant Info
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse> addRestaurant(@RequestBody Restaurant restaurant) {
		Restaurant addedRestaurant = restaurantService.saveRestaurant(restaurant);
		return ResponseEntity.ok(new ApiResponse("Restaurant added successfully", addedRestaurant));
	}
	
	//Update Restaurant
	@PutMapping("/update")
	@PreAuthorize("hasAuthority('ROLE_RESTAURANT')")
	public ResponseEntity<ApiResponse> updateRestaurant(@RequestBody Restaurant restaurant) {
		Restaurant updatedRestaurant =restaurantService.updateRestaurant(userService.getLoggedInUser(), restaurant);
		return ResponseEntity.ok(new ApiResponse("Restaurant updated successfully", updatedRestaurant));
	}
	
	//Get Restaurant Info by user name
	@GetMapping("/myDetail")
	@PreAuthorize("hasAuthority('ROLE_RESTAURANT')")
	public ResponseEntity<ApiResponse> getRestaurant() {
		Restaurant restarurant =restaurantService.getRestaurant(userService.getLoggedInUser());
		return ResponseEntity.ok(new ApiResponse("Restaurant details retrieved successfully", restarurant));
	}
	
	// Update dishes for a specific restaurant
	@PutMapping("/dishes")
	@PreAuthorize("hasAuthority('ROLE_RESTAURANT')")
	public ResponseEntity<ApiResponse> updateDishesForRestaurant(@RequestBody List<Dish> dishes) {
		List<Dish> updatedDishes =  restaurantService.updateDishesForRestaurant(userService.getLoggedInUser(), dishes);
		return ResponseEntity.ok(new ApiResponse("Dishes updated successfully", updatedDishes));
	}

	//Retrieve all dishes for a specific restaurant by userName
	@GetMapping("/dishes")
	@PreAuthorize("hasAuthority('ROLE_RESTAURANT')")
	public ResponseEntity<ApiResponse> getDishesByRestaurant() {
		List<Dish> dishes = restaurantService.getDishesByRestaurant(userService.getLoggedInUser());
		return ResponseEntity.ok(new ApiResponse("Dishes retrieved successfully", dishes));
	}
	
	// Delete all dishes for a specific restaurant
	@DeleteMapping("/dishes")
	@PreAuthorize("hasAuthority('ROLE_RESTAURANT')")
	public ResponseEntity<ApiResponse> deleteAllDishesForRestaurant() {
		//String
		String userName = userService.getLoggedInUser();
		restaurantService.deleteAllDishesForRestaurant(userName);
		String response = "All dishes for Restaurant " + userName + " have been deleted successfully!";
		return ResponseEntity.ok(new ApiResponse(response, null));
	}
	
	// Delete a restaurant by user name (also deletes all associated dishes)
	@DeleteMapping("/{restaurantUserName}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse> deleteRestaurantById(@PathVariable String restaurantUserName) {
		restaurantService.deleteRestaurantByUserName(restaurantUserName);
		String response = "Restaurant " + restaurantUserName + " deleted successfully!";
		return ResponseEntity.ok(new ApiResponse(response, null));

	}
	
	@GetMapping("/all")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
	public ResponseEntity<ApiResponse> getAllRestaurants() {
		List<Restaurant> allRestaurants = restaurantService.getAllRestaurants();
		return ResponseEntity.ok(new ApiResponse("All restaurants with dishes", allRestaurants));
	}
	
	@GetMapping("/getUserName") 
	public String getLoggedInUserName() {
		return userService.getLoggedInUser();
	}
}
