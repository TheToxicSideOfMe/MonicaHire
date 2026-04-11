package com.monicahire.report_service.kafka;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestedEvent {
    private String reportId;
    private String candidateId;
    private String jobId;
    private String companyId;
}