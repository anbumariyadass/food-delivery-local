package com.iris.food_delivery.customer_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iris.food_delivery.customer_service.entity.Customer;
import com.iris.food_delivery.customer_service.repository.CustomerRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	public Customer saveCustomer(Customer customer) {
		Customer savedCustomer = customerRepository.save(customer);
		return savedCustomer;
	}
	
	public Customer getCustomer(String customerName) {
		Customer customer = null;
		Optional<Customer> customerOptional = customerRepository.findById(customerName);
		if (customerOptional.isPresent()) {
		    customer = customerOptional.get();
		    // do something with customer
		} else {
			throw new RuntimeException("Customer details not found");
		}

		return customer;
	}
	
	public List<Customer> getAllCustomer() {
		return customerRepository.findAll();
	}
	
	@Transactional
	public void deleteCustomer(String customerName) {
		customerRepository.deleteById(customerName);
	}
}
