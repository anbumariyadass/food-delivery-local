package com.iris.food_delivery.delivery_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DELIVERY_PARTNER")
public class DeliveryPartner {

    @Id
    @Column(name = "USER_NAME", nullable = false, unique = true)
    private String userName; // Primary key

    @Column(name = "DLVRY_PARTNER_NAME", nullable = false)
    private String deliveryPartnerName;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PHONE", nullable = false, unique = true)
    private String phone;

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeliveryPartnerName() {
        return deliveryPartnerName;
    }

    public void setDeliveryPartnerName(String deliveryPartnerName) {
        this.deliveryPartnerName = deliveryPartnerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
