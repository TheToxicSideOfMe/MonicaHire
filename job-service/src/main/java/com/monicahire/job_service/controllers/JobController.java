package com.monicahire.job_service.controllers;

import com.monicahire.job_service.dtos.CreateJobRequest;
import com.monicahire.job_service.dtos.JobResponse;
import com.monicahire.job_service.dtos.SlotClaimResponse;
import com.monicahire.job_service.services.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @RequestHeader("X-User-Id") String companyId,
            @RequestBody CreateJobRequest request
    ) {
        return ResponseEntity.ok(jobService.createJob(companyId, request));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getMyJobs(
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(jobService.getMyJobs(companyId));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable String jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }

    @PatchMapping("/{jobId}/close")
    public ResponseEntity<JobResponse> closeJob(
            @PathVariable String jobId,
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(jobService.closeJob(jobId, companyId));
    }

    /**
     * Called internally by candidate-service only — not exposed to the public via gateway.
     * Atomically checks capacity and claims a slot for an incoming candidate.
     */
    @PostMapping("/{jobId}/claim-slot")
    public ResponseEntity<SlotClaimResponse> claimSlot(@PathVariable String jobId) {
        return ResponseEntity.ok(jobService.claimSlot(jobId));
    }
}