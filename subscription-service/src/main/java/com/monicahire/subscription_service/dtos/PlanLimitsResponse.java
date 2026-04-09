package com.monicahire.subscription_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanLimitsResponse {
    private int maxJobs;
    private int maxCandidatesPerJob;
    private int maxReports;
}
 