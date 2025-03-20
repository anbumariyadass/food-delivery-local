package com.iris.food_delivery.order_service.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "ORDER_DETAILS")
public class OrderDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDtlId;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    @JsonIgnore  // This prevents infinite recursion
    private Order order;

    private String itemName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
	public Long getOrderDtlId() {
		return orderDtlId;
	}
	public void setOrderDtlId(Long orderDtlId) {
		this.orderDtlId = orderDtlId;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

}
