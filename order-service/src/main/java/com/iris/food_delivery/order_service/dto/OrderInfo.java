package com.iris.food_delivery.order_service.dto;

public class OrderInfo {
	private Long orderId;

    private String dlvryPartnerUserName;
    private String dlvryPartnerName;
    private Double totalPrice;
    
    private String customerContactName;
    private String customerContactAddress;
    private String customerContactEmail;
    private String customerContactPhone;
    private String restaurantName;
    private String orderStatus;
    
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getDlvryPartnerUserName() {
		return dlvryPartnerUserName;
	}
	public void setDlvryPartnerUserName(String dlvryPartnerUserName) {
		this.dlvryPartnerUserName = dlvryPartnerUserName;
	}
	public String getDlvryPartnerName() {
		return dlvryPartnerName;
	}
	public void setDlvryPartnerName(String dlvryPartnerName) {
		this.dlvryPartnerName = dlvryPartnerName;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getCustomerContactName() {
		return customerContactName;
	}
	public void setCustomerContactName(String customerContactName) {
		this.customerContactName = customerContactName;
	}
	public String getCustomerContactAddress() {
		return customerContactAddress;
	}
	public void setCustomerContactAddress(String customerContactAddress) {
		this.customerContactAddress = customerContactAddress;
	}
	public String getCustomerContactEmail() {
		return customerContactEmail;
	}
	public void setCustomerContactEmail(String customerContactEmail) {
		this.customerContactEmail = customerContactEmail;
	}
	public String getCustomerContactPhone() {
		return customerContactPhone;
	}
	public void setCustomerContactPhone(String customerContactPhone) {
		this.customerContactPhone = customerContactPhone;
	}
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
}
