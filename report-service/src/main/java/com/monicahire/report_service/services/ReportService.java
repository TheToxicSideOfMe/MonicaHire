package com.monicahire.report_service.services;

import com.monicahire.report_service.clients.SubscriptionClient;
import com.monicahire.report_service.dtos.ReportDto;
import com.monicahire.report_service.kafka.ReportRequestedEvent;
import com.monicahire.report_service.models.Report;
import com.monicahire.report_service.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final SubscriptionClient subscriptionClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public ReportDto.ReportResponse requestReport(String candidateId, String jobId, String companyId) {

        // 1. Check report quota
        SubscriptionClient.QuotaCheckResponse quota = subscriptionClient.checkReportQuota(companyId);
        if (!quota.isAllowed()) {
            throw new RuntimeException(quota.getReason());
        }

        // 2. Check if report already exists for this candidate
        reportRepository.findByCandidateId(candidateId).ifPresent(existing -> {
            if (existing.getStatus() != Report.ReportStatus.FAILED) {
                throw new IllegalStateException("Report already exists for this candidate");
            }
        });

        // 3. Save report as PENDING
        Report report = new Report();
        report.setCandidateId(candidateId);
        report.setJobId(jobId);
        report.setCompanyId(companyId);
        Report saved = reportRepository.save(report);

        // 4. Increment report usage
        subscriptionClient.incrementReportUsage(companyId);

        // 5. Publish report.requested → consumer handles the rest
        kafkaTemplate.send("report.requested", companyId,
                new ReportRequestedEvent(saved.getId(), candidateId, jobId, companyId)
        );

        log.info("Report requested for candidateId={} reportId={}", candidateId, saved.getId());
        return toResponse(saved);
    }

    public ReportDto.ReportResponse getReport(String candidateId, String companyId) {
        Report report = reportRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new RuntimeException("Report not found for candidateId: " + candidateId));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        return toResponse(report);
    }

    public List<ReportDto.ReportResponse> getMyReports(String companyId) {
        return reportRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReportDto.ReportResponse toResponse(Report r) {
        return new ReportDto.ReportResponse(
                r.getId(), r.getCandidateId(), r.getJobId(), r.getCompanyId(),
                r.getPdfUrl(), r.getStatus().name(), r.getErrorMessage(),
                r.getCreatedAt(), r.getCompletedAt()
        );
    }
}