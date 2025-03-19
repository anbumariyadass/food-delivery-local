package com.iris.food_delivery.restaurant_service.exception.handler;

public class DishNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DishNotFoundException(String message) {
        super(message);
    }
}