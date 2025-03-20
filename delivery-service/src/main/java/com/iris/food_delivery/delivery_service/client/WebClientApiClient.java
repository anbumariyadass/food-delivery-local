package com.iris.food_delivery.delivery_service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.iris.food_delivery.delivery_service.service.JwtService;

import reactor.core.publisher.Mono;

@Component
public class WebClientApiClient {
	
	@Autowired
    private JwtService jwtService;

    public void updateOrderStatus(long orderId, String orderStatus) {
    	
    	System.out.println("orderId :: " + orderId + " :: orderStatus :: " + orderStatus);
    	
    	String url = String.format("http://localhost:8086/order/%d/updateOrderStatus?orderStatus=%s", orderId, orderStatus);
    	
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.AUTHORIZATION, jwtService.getJwtToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Mono<String> response = webClient.put()
                .retrieve()
                .bodyToMono(String.class);

        // Print response
        response.subscribe(System.out::println);
    }
}
