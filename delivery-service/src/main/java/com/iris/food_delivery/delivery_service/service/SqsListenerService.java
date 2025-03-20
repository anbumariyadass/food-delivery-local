package com.iris.food_delivery.delivery_service.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iris.food_delivery.delivery_service.entity.OrderDelivery;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class SqsListenerService {
	
	private static final String QUEUE_URL = "https://sqs.ap-south-1.amazonaws.com/539247489537/order-queue";

    private final SqsClient sqsClient;

    public SqsListenerService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public List<OrderDelivery> receiveMessages() {
    	List<OrderDelivery> orderDeliveryList = new ArrayList<>();
    	boolean checkMoreMessages = true;
    	while(checkMoreMessages) {
    		ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(QUEUE_URL)
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
                            .queueUrl(QUEUE_URL)
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
