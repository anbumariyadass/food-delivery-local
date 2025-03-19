package com.iris.food_delivery.restaurant_service.exception.handler;

public class RestaurantNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RestaurantNotFoundException(String message) {
        super(message);
    }
}
