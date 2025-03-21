package com.iris.food_delivery.customer_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iris.food_delivery.customer_service.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

}
