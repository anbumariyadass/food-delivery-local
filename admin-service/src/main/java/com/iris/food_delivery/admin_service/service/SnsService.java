package com.iris.food_delivery.admin_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

@Service
public class SnsService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic.arn}")  // Get the ARN from application.properties
    private String topicArn;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String publishMessage(String message, String subject) {
        PublishRequest request = PublishRequest.builder()
                .message(message)
                .subject(subject)
                .topicArn(topicArn)
                .build();

        PublishResponse response = snsClient.publish(request);
        return response.messageId();
    }
    
    /**
     * Subscribe an email to the SNS topic.
     */
    public String subscribeEmail(String email) {
        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email") // Use 'email' protocol
                .endpoint(email)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);
        return response.subscriptionArn();
    }
    
    
}
