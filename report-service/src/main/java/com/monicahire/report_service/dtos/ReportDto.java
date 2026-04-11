package com.monicahire.report_service.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportResponse {
        private String id;
        private String candidateId;
        private String jobId;
        private String companyId;
        private String pdfUrl;
        private String status;
        private String errorMessage;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }
}