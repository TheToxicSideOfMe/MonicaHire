package com.monicahire.subscription_service.consumers;

import com.monicahire.subscription_service.events.UserRegisteredEvent;
import com.monicahire.subscription_service.services.SubscriptionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredConsumer {

    private final SubscriptionService subscriptionService;

    public UserRegisteredConsumer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "user.registered", groupId = "subscription-service")
    public void handle(UserRegisteredEvent event) {
        subscriptionService.createInitialSubscription(event.getId());
    }
}