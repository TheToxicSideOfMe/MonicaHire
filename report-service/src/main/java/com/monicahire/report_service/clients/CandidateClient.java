package com.monicahire.report_service.clients;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CandidateClient {

    private final WebClient webClient;

    public CandidateClient(@Value("${candidate-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public CandidateDetail getCandidate(String candidateId, String companyId) {
        return webClient.get()
                .uri("/candidates/{candidateId}", candidateId)
                .header("X-User-Id", companyId)
                .retrieve()
                .bodyToMono(CandidateDetail.class)
                .block();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerEntry {
        private String question;
        private String answer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateDetail {
        private String id;
        private String jobId;
        private String companyId;
        private String name;
        private String phone;
        private String location;
        private String cvUrl;
        private List<AnswerEntry> answers;
        private String status;
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
        private LocalDateTime createdAt;
        private LocalDateTime evaluatedAt;
    }
}