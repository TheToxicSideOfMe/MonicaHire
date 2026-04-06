package com.monicahire.auth_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredProducer {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredProducer.class);
    private static final String TOPIC = "user.registered";

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public UserRegisteredProducer(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(UserRegisteredEvent event) {
        kafkaTemplate.send(TOPIC, event.getId(), event);
        log.info("Published user.registered event for: {}", event.getEmail());
    }
}