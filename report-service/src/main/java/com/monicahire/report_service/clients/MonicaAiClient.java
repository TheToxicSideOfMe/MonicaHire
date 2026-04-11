package com.monicahire.report_service.clients;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Component
public class MonicaAiClient {

    private final WebClient webClient;

    public MonicaAiClient(@Value("${monica-ai.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public ReportAgentResponse generateReport(ReportAgentRequest request) {
        return webClient.post()
                .uri("/agents/report")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReportAgentResponse.class)
                .block();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportAgentRequest {
        private String companyId;
        private String jobId;
        private String candidateId;
        private CandidateClient.CandidateDetail candidate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportAgentResponse {
        @JsonProperty("report_markdown")
        private String reportMarkdown;
    }
}