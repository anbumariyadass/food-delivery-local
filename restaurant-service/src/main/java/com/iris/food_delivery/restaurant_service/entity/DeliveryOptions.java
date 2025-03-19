package com.iris.food_delivery.restaurant_service.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class DeliveryOptions {
    private boolean takeaway;
    private boolean homeDelivery;
	public boolean isTakeaway() {
		return takeaway;
	}
	public void setTakeaway(boolean takeaway) {
		this.takeaway = takeaway;
	}
	public boolean isHomeDelivery() {
		return homeDelivery;
	}
	public void setHomeDelivery(boolean homeDelivery) {
		this.homeDelivery = homeDelivery;
	}
    
    
}
