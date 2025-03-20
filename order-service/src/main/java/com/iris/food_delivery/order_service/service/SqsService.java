package com.iris.food_delivery.order_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iris.food_delivery.order_service.dto.OrderInfo;
import com.iris.food_delivery.order_service.entity.Order;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsService {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	

    private final SqsClient sqsClient;
    //@Value("${aws.sqs.queueUrl}")
    private static final String QUEUE_URL = "https://sqs.ap-south-1.amazonaws.com/539247489537/order-queue";

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void sendMessage(Order order) {
    	try {
    		OrderInfo orderInfo = new OrderInfo();
    		orderInfo.setOrderId(order.getOrderId());
    		orderInfo.setDlvryPartnerName(order.getDlvryPartnerName());
    		orderInfo.setDlvryPartnerUserName(order.getDlvryPartnerUserName());
    		orderInfo.setTotalPrice(order.getTotalPrice());
    		orderInfo.setCustomerContactName(order.getContactName());
    		orderInfo.setCustomerContactAddress(order.getContactAddress());
    		orderInfo.setCustomerContactEmail(order.getContactEmail());
    		orderInfo.setCustomerContactPhone(order.getContactPhone());
    		orderInfo.setRestaurantName(order.getRestaurantName());
    		orderInfo.setOrderStatus("ORDERED");
    		
    		// Convert Order object to JSON string
    		String orderJson = objectMapper.writeValueAsString(orderInfo);
    		System.out.println("orderJson::"+orderJson);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(QUEUE_URL)
                    .messageBody(orderJson)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(sendMsgRequest);
            System.out.println("Response Message ID :: " + response.messageId());
    	} catch (Exception e) {
    		System.out.println("Exception in SQS Send Message");
    	}
    }
}

