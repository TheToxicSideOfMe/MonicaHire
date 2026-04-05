package com.monicahire.auth_service.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "credentials")
@NoArgsConstructor
@AllArgsConstructor
public class Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // UUID, generated at registration

    private String email;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // COMPANY, ADMIN

    private boolean isVerified;
    private LocalDateTime createdAt;

    public enum Role{
        ADMIN,
        COMPANY
    }
}