package com.monicahire.candidate_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

    @Id
    private String id;

    // Job context
    private String jobId;
    private String companyId;

    // Phase 1 — filled on apply
    private String name;
    private String phone;
    private String email;
    private String location;
    private String cvUrl;

    // Phase 2 — filled on interview submit
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<AnswerEntry> answers;

    // Evaluation — filled after EvaluationAgent runs
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

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    private CandidateStatus status;

    private LocalDateTime submittedAt;
    private LocalDateTime evaluatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CandidateStatus {
        PENDING_INTERVIEW,
        SUBMITTED,
        EVALUATED,
        SHORTLISTED,
        REJECTED,
        HIRED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerEntry {
        private String question;
        private String answer;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.status = CandidateStatus.PENDING_INTERVIEW;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}