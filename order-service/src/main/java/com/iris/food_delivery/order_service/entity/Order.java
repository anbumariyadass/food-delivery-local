package com.iris.food_delivery.order_service.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ORDERS")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String customerUserName;    
    private String restaurantUserName;
    private String restaurantName;
    private String dlvryPartnerUserName;
    private String dlvryPartnerName;
    private String orderStatus;
    private Double totalPrice;
    private String contactName;
    private String contactAddress;
    private String contactEmail;
    private String contactPhone;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderedOn;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusUpdtOn;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
    
    // Automatically set 'orderedOn' when creating a new order
    @PrePersist
    protected void onCreate() {
        orderedOn = new Date();  // Set current timestamp
    }

    // Automatically update 'statusUpdtOn' when updating an order
    @PreUpdate
    protected void onUpdate() {
        statusUpdtOn = new Date();  // Set current timestamp
    }

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getCustomerUserName() {
		return customerUserName;
	}

	public void setCustomerUserName(String customerUserName) {
		this.customerUserName = customerUserName;
	}

	public String getRestaurantUserName() {
		return restaurantUserName;
	}

	public void setRestaurantUserName(String restaurantUserName) {
		this.restaurantUserName = restaurantUserName;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
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

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public Date getOrderedOn() {
		return orderedOn;
	}

	public void setOrderedOn(Date orderedOn) {
		this.orderedOn = orderedOn;
	}

	public Date getStatusUpdtOn() {
		return statusUpdtOn;
	}

	public void setStatusUpdtOn(Date statusUpdtOn) {
		this.statusUpdtOn = statusUpdtOn;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

}

