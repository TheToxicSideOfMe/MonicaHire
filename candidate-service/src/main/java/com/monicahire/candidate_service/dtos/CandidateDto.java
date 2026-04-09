package com.monicahire.candidate_service.dtos;

import com.monicahire.candidate_service.models.Candidate.AnswerEntry;
import com.monicahire.candidate_service.models.Candidate.CandidateStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CandidateDto {

    // ── Phase 1 ───────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyRequest {
        private String jobId;
        private String name;
        private String phone;
        private String location;
        private String cvUrl;
    }

    // ── Phase 2 ───────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmitRequest {
        private String token;
        private List<String> answers;  // index-matched to job questions, we pair them in service
    }

    // ── Company actions ───────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private CandidateStatus status;  // SHORTLISTED, REJECTED, HIRED
    }

    // ── Responses ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateResponse {
        private String id;
        private String jobId;
        private String companyId;
        private String name;
        private String phone;
        private String location;
        private String cvUrl;
        private List<AnswerEntry> answers;
        private CandidateStatus status;

        // Scores — null until evaluated
        private Double globalScore;
        private Double cvScore;
        private Double interviewScore;
        private Double aiPercentage;
        private Double experienceMatch;
        private Double skillsMatch;
        private Double educationMatch;
        private Double cultureFit;
        private Double communicationScore;
        private Double mindsetScore;
        private Double potentialScore;
        private String note;

        private LocalDateTime submittedAt;
        private LocalDateTime evaluatedAt;
        private LocalDateTime createdAt;
    }
}