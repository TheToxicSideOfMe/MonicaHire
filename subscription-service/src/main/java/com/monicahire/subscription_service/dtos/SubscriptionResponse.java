package com.monicahire.subscription_service.dtos;

import com.monicahire.subscription_service.models.Plan;
import com.monicahire.subscription_service.models.Subscription.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SubscriptionResponse {
    private String companyId;
    private Plan.PlanName plan;
    private SubscriptionStatus status;
    private int maxJobs;
    private int maxCandidatesPerJob;
    private int maxReports;
    private int jobsUsed;
    private int reportsUsed;
    private LocalDateTime endDate;
}