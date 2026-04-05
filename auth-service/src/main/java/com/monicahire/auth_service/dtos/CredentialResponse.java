package com.monicahire.auth_service.dtos;
import com.monicahire.auth_service.models.Credential.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
}