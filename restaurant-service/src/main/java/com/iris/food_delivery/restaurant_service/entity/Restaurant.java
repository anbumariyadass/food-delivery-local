package com.iris.food_delivery.restaurant_service.entity;


import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

@Entity
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

	private String userName;
    
    @Embedded
    private Address address;

    private String phone;
    private String cuisine;
    private double rating;

    @ElementCollection
    @CollectionTable(name = "restaurant_opening_hours", joinColumns = @JoinColumn(name = "restaurant_id"))
    @MapKeyColumn(name = "day")
    @Column(name = "hours")
    private Map<String, String> openingHours;

    @Embedded
    private DeliveryOptions deliveryOptions;

    private int averagePricePerPerson;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dish> dishes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public Map<String, String> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(Map<String, String> openingHours) {
		this.openingHours = openingHours;
	}

	public DeliveryOptions getDeliveryOptions() {
		return deliveryOptions;
	}

	public void setDeliveryOptions(DeliveryOptions deliveryOptions) {
		this.deliveryOptions = deliveryOptions;
	}

	public int getAveragePricePerPerson() {
		return averagePricePerPerson;
	}

	public void setAveragePricePerPerson(int averagePricePerPerson) {
		this.averagePricePerPerson = averagePricePerPerson;
	}

	public List<Dish> getDishes() {
		return dishes;
	}

	public void setDishes(List<Dish> dishes) {
		this.dishes = dishes;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
