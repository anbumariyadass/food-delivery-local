package com.iris.food_delivery.sqssns_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iris.food_delivery.sqssns_service.dto.OrderInfo;
import com.iris.food_delivery.sqssns_service.dto.Order;
import com.iris.food_delivery.sqssns_service.dto.OrderDelivery;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsService {
	
	private final ObjectMapper objectMapper = new ObjectMapper();

    private final SqsClient sqsClient;
    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;
    
    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String sendMessage(Order order) {
    	String responseMessageId = null;
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
                    .queueUrl(queueUrl)
                    .messageBody(orderJson)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(sendMsgRequest);
            System.out.println("Response Message ID :: " + response.messageId());
            responseMessageId = response.messageId();
    	} catch (Exception e) {
    		System.out.println("Exception in SQS Send Message");
    	}
    	return responseMessageId;
    }
    
    public List<OrderDelivery> receiveMessages() {
    	List<OrderDelivery> orderDeliveryList = new ArrayList<>();
    	boolean checkMoreMessages = true;
    	while(checkMoreMessages) {
    		ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            
            if (messages.size() > 0) {
            	System.out.println("Message Size :: " + messages.size());
            	for (Message message : messages) {
                    System.out.println("Received message: " + message.body());
                    
                    try {
                    	 ObjectMapper objectMapper = new ObjectMapper();
                    	 OrderDelivery orderDelivery = objectMapper.readValue(message.body(), OrderDelivery.class);
                    	 orderDeliveryList.add(orderDelivery);
                    } catch (Exception e) {
                    }
                    
                    // Delete the message after processing
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build();

                    sqsClient.deleteMessage(deleteRequest);
                }
            } else {
            	System.out.println("Message Size :: " + messages.size() + ":: No Messages to receive");
            	checkMoreMessages = false;
            }
    	}
    	
    	return orderDeliveryList;
    }

}

