package com.monicahire.job_service.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanLimitsResponse {
    private int maxJobs;
    private int maxCandidatesPerJob;
    private int maxReports;
}