package com.monicahire.subscription_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUsage {

    @Id
    private String id;

    @Column(unique = true)
    private String companyId;

    private int jobsUsed;
    private int reportsUsed;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.jobsUsed = 0;
        this.reportsUsed = 0;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}