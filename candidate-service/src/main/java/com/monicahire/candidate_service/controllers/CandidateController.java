package com.monicahire.candidate_service.controllers;

import com.monicahire.candidate_service.dtos.CandidateDto;
import com.monicahire.candidate_service.models.Candidate.CandidateStatus;
import com.monicahire.candidate_service.services.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    // Public — called from the apply form (no auth)
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody CandidateDto.ApplyRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.apply(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Public — called from the interview link (token is the auth)
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody CandidateDto.SubmitRequest request) {
        try {
            return ResponseEntity.ok(candidateService.submit(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Company — get all candidates for a job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<CandidateDto.CandidateResponse>> getCandidatesByJob(
            @PathVariable String jobId,
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(candidateService.getCandidatesByJob(jobId, companyId));
    }

    // Company — get single candidate
    @GetMapping("/{candidateId}")
    public ResponseEntity<?> getCandidate(
            @PathVariable String candidateId,
            @RequestHeader("X-User-Id") String companyId
    ) {
        try {
            return ResponseEntity.ok(candidateService.getCandidate(candidateId, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Company — update candidate status (shortlist, reject, hire)
    @PatchMapping("/{candidateId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String candidateId,
            @RequestHeader("X-User-Id") String companyId,
            @RequestBody CandidateDto.StatusUpdateRequest request
    ) {
        try {
            return ResponseEntity.ok(candidateService.updateStatus(candidateId, companyId, request.getStatus()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}