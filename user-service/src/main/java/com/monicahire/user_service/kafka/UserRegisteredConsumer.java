package com.monicahire.user_service.kafka;

import com.monicahire.user_service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
 
@Component
public class UserRegisteredConsumer {
 
    private static final Logger log = LoggerFactory.getLogger(UserRegisteredConsumer.class);
 
    private final UserService userService;
 
    public UserRegisteredConsumer(UserService userService) {
        this.userService = userService;
    }
 
    @KafkaListener(
        topics = "user.registered",
        groupId = "user-service-group"
    )
    public void consume(UserRegisteredEvent event) {
        log.info("Received user.registered event for: {}", event.getEmail());
        try {
            userService.createInitialProfile(event.getId(), event.getEmail());
            log.info("Created initial profile for: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to create profile for {}: {}", event.getEmail(), e.getMessage());
        }
    }
}