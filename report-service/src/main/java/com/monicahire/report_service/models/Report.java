package com.monicahire.report_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    private String id;

    private String candidateId;
    private String jobId;
    private String companyId;
    private String pdfUrl;
    private String publicId;  // Cloudinary public ID for deletion if needed

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;  // populated if generation fails

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public enum ReportStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.status = ReportStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}