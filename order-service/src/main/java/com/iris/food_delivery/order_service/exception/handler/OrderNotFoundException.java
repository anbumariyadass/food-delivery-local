package com.iris.food_delivery.order_service.exception.handler;

public class OrderNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OrderNotFoundException(String message) {
        super(message);
    }
}