package com.monicahire.report_service.controllers;

import com.monicahire.report_service.dtos.ReportDto;
import com.monicahire.report_service.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // HR clicks "generate report" for a candidate
    @PostMapping("/generate/{candidateId}")
    public ResponseEntity<?> generateReport(
            @PathVariable String candidateId,
            @RequestParam String jobId,
            @RequestHeader("X-User-Id") String companyId
    ) {
        try {
            ReportDto.ReportResponse response = reportService.requestReport(candidateId, jobId, companyId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // Poll report status / get pdf url once completed
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getReport(
            @PathVariable String candidateId,
            @RequestHeader("X-User-Id") String companyId
    ) {
        try {
            return ResponseEntity.ok(reportService.getReport(candidateId, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get all reports for this company
    @GetMapping
    public ResponseEntity<List<ReportDto.ReportResponse>> getMyReports(
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(reportService.getMyReports(companyId));
    }
}