package com.monicahire.report_service.clients;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SubscriptionClient {

    private final WebClient webClient;

    public SubscriptionClient(@Value("${subscription-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public QuotaCheckResponse checkReportQuota(String companyId) {
        return webClient.get()
                .uri("/api/subscriptions/check-quota/reports")
                .header("X-User-Id", companyId)
                .retrieve()
                .bodyToMono(QuotaCheckResponse.class)
                .block();
    }

    public void incrementReportUsage(String companyId) {
        webClient.post()
                .uri("/api/subscriptions/usage/reports/increment")
                .header("X-User-Id", companyId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotaCheckResponse {
        private boolean allowed;
        private String reason;
    }
}