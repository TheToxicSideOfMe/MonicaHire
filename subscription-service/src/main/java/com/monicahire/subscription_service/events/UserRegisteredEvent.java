package com.monicahire.subscription_service.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegisteredEvent {
    private String id;
    private String email;
    private String role;
    private String createdAt;
}