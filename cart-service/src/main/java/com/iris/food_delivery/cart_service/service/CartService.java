package com.iris.food_delivery.cart_service.service;

import com.iris.food_delivery.cart_service.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iris.food_delivery.cart_service.repository.CartRepository;

@Service
public class CartService {
	@Autowired
    private CartRepository cartRepository;

	public Cart saveCart(Cart cart) {
		cartRepository.save(cart);
		return cart;
	}

	public Cart getCart(String userName) {
		return cartRepository.findByUserName(userName);
	}

	public void deleteCart(String userName) {
		cartRepository.deleteByUserName(userName);
	}
}
