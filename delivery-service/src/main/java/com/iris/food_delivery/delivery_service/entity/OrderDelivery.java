package com.iris.food_delivery.delivery_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ORDER_DELIVERY")
public class OrderDelivery {

    @Id
    @Column(name = "order_id")
    private Long orderId; // No auto-generation, manually set

    @Column(name = "dlvry_partner_username")
    private String dlvryPartnerUserName;

    @Column(name = "dlvry_partner_name")
    private String dlvryPartnerName;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "customer_contact_name")
    private String customerContactName;

    @Column(name = "customer_contact_address")
    private String customerContactAddress;

    @Column(name = "customer_contact_email")
    private String customerContactEmail;

    @Column(name = "customer_contact_phone")
    private String customerContactPhone;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "order_status")
    private String orderStatus;

    // Getters and Setters
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

