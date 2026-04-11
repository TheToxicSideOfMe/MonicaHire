package com.monicahire.report_service.kafka;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.monicahire.report_service.clients.CandidateClient;
import com.monicahire.report_service.clients.FileClient;
import com.monicahire.report_service.clients.MonicaAiClient;
import com.monicahire.report_service.models.Report;
import com.monicahire.report_service.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportEventConsumer {

    private final ReportRepository reportRepository;
    private final CandidateClient candidateClient;
    private final MonicaAiClient monicaAiClient;
    private final FileClient fileClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "report.requested", groupId = "report-service")
    @Transactional
    public void onReportRequested(ReportRequestedEvent event) {
        log.info("Report generation started for candidateId={}", event.getCandidateId());

        Report report = reportRepository.findById(event.getReportId())
                .orElseThrow(() -> new RuntimeException("Report not found: " + event.getReportId()));

        try {
            // 1. Fetch full candidate details
            CandidateClient.CandidateDetail candidate = candidateClient.getCandidate(
                    event.getCandidateId(), event.getCompanyId()
            );

            // 2. Call ReportAgent — returns markdown
            MonicaAiClient.ReportAgentResponse agentResponse = monicaAiClient.generateReport(
                    new MonicaAiClient.ReportAgentRequest(
                            event.getCompanyId(),
                            event.getJobId(),
                            event.getCandidateId(),
                            candidate
                    )
            );

            // 3. Convert markdown to PDF
            byte[] pdfBytes = markdownToPdf(agentResponse.getReportMarkdown(), candidate.getName());

            // 4. Upload PDF to Cloudinary via file-service
            String filename = "report_" + event.getCandidateId() + ".pdf";
            FileClient.UploadResponse upload = fileClient.uploadPdf(pdfBytes, filename);

            // 5. Save completed report
            report.setPdfUrl(upload.getUrl());
            report.setPublicId(upload.getPublicId());
            report.setStatus(Report.ReportStatus.COMPLETED);
            report.setCompletedAt(LocalDateTime.now());
            reportRepository.save(report);

            // 6. Publish report.completed → notification-service
            Map<String, String> completedEvent = new HashMap<>();
            completedEvent.put("reportId", report.getId());
            completedEvent.put("candidateId", event.getCandidateId());
            completedEvent.put("companyId", event.getCompanyId());
            completedEvent.put("pdfUrl", upload.getUrl());
            kafkaTemplate.send("report.completed", event.getCompanyId(), completedEvent);

            log.info("Report completed for candidateId={} pdfUrl={}", event.getCandidateId(), upload.getUrl());

        } catch (Exception e) {
            log.error("Report generation failed for candidateId={}: {}", event.getCandidateId(), e.getMessage());
            report.setStatus(Report.ReportStatus.FAILED);
            report.setErrorMessage(e.getMessage());
            reportRepository.save(report);
        }
    }

    private byte[] markdownToPdf(String markdown, String candidateName) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 60, 60);
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("Candidate Evaluation Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(8);
        document.add(title);

        // Candidate name subtitle
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 13);
        Paragraph subtitle = new Paragraph(candidateName, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(24);
        document.add(subtitle);

        // Render markdown lines
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font boldInline = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        for (String line : markdown.split("\n")) {
            if (line.startsWith("## ")) {
                Paragraph heading = new Paragraph(line.substring(3), headingFont);
                heading.setSpacingBefore(14);
                heading.setSpacingAfter(4);
                document.add(heading);
            } else if (line.startsWith("**") && line.endsWith("**")) {
                Paragraph bold = new Paragraph(line.replaceAll("\\*\\*", ""), boldInline);
                bold.setSpacingAfter(2);
                document.add(bold);
            } else if (line.startsWith("- ")) {
                Paragraph bullet = new Paragraph("• " + line.substring(2), bodyFont);
                bullet.setIndentationLeft(16);
                bullet.setSpacingAfter(2);
                document.add(bullet);
            } else if (!line.isBlank()) {
                Paragraph body = new Paragraph(line, bodyFont);
                body.setSpacingAfter(4);
                document.add(body);
            }
        }

        document.close();
        return out.toByteArray();
    }
}