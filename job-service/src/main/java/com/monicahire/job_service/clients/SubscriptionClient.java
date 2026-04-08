package com.monicahire.job_service.clients;

import com.monicahire.job_service.dtos.QuotaCheckResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SubscriptionClient {

    private final WebClient webClient;

    public SubscriptionClient(@Value("${subscription-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public QuotaCheckResponse checkJobQuota(String companyId) {
        return webClient.get()
                .uri("/subscriptions/check-quota/jobs")
                .header("X-User-Id", companyId)
                .retrieve()
                .bodyToMono(QuotaCheckResponse.class)
                .block();
    }
}