package com.monicahire.subscription_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @Enumerated(EnumType.STRING)
    private PlanName name;

    private int maxJobs;              // -1 = unlimited
    private int maxCandidatesPerJob;  // -1 = unlimited
    private int maxReports;           // -1 = unlimited
    private double price;

    public enum PlanName {
        STARTER, GROWTH, ENTERPRISE
    }
}