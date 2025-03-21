package com.iris.food_delivery.cart_service.exception.handler;

public class CartNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CartNotFoundException(String message) {
        super(message);
    }
}
