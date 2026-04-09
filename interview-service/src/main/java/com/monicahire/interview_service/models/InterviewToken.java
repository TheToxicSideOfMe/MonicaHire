package com.monicahire.interview_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewToken {

    @Id
    private String id;

    private String token;
    private String candidateId;
    private String jobId;
    private String companyId;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;

    public enum TokenStatus {
        PENDING,
        USED,
        EXPIRED
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        if (this.token == null) this.token = UUID.randomUUID().toString();
        this.status = TokenStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}