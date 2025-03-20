package com.iris.food_delivery.order_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsService {

    private final SqsClient sqsClient;

    //@Value("${aws.sqs.queueUrl}")
    private static final String QUEUE_URL = "https://sqs.ap-south-1.amazonaws.com/539247489537/order-queue";

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String sendMessage(String message) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(message)
                .build();

        SendMessageResponse response = sqsClient.sendMessage(sendMsgRequest);
        return response.messageId();
    }
}

