package com.monicahire.auth_service.dtos;

import java.time.LocalDateTime;

import com.monicahire.auth_service.models.Credential;
import com.monicahire.auth_service.models.Credential.Role;

import lombok.Data;


@Data
public class RegisterResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    public static RegisterResponse from(Credential credential) {
        RegisterResponse response = new RegisterResponse();
        response.setId(credential.getId());
        response.setUsername(credential.getUsername());
        response.setEmail(credential.getEmail());
        response.setRole(credential.getRole());
        response.setCreatedAt(credential.getCreatedAt());
        return response;
    }
}