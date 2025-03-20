package com.iris.food_delivery.delivery_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DELIVERY_ORDER_TRACKER")
public class DeliveryOrderTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracker_id")
    private Long trackerId;  // Auto-generated primary key

    @Column(name = "order_id", nullable = false)
    private Long orderId; // No auto-generation

    @Column(name = "order_status", nullable = false)
    private String orderStatus;

    @Column(name = "order_status_on", nullable = false)
    private String orderStatusOn;

    @Column(name = "order_status_updated_by", nullable = false)
    private String orderStatusUpdatedBy;

    // Getters and Setters
    public Long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(Long trackerId) {
        this.trackerId = trackerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusOn() {
        return orderStatusOn;
    }

    public void setOrderStatusOn(String orderStatusOn) {
        this.orderStatusOn = orderStatusOn;
    }

    public String getOrderStatusUpdatedBy() {
        return orderStatusUpdatedBy;
    }

    public void setOrderStatusUpdatedBy(String orderStatusUpdatedBy) {
        this.orderStatusUpdatedBy = orderStatusUpdatedBy;
    }
}

