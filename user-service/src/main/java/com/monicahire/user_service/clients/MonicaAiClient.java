package com.monicahire.user_service.clients;


import com.monicahire.user_service.models.CompanyIdentity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class MonicaAiClient {

    private final WebClient webClient;

    public MonicaAiClient(@Value("${monica-ai.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void embedCompany(String companyId, CompanyIdentity identity) {
        Map<String, Object> body = Map.of(
                "company_id", companyId,
                "identity", Map.of(
                        "industry", identity.getIndustry() != null ? identity.getIndustry() : "",
                        "companySize", identity.getCompanySize() != null ? identity.getCompanySize() : "",
                        "mission", identity.getMission() != null ? identity.getMission() : "",
                        "culture", identity.getCulture() != null ? identity.getCulture() : "",
                        "values", identity.getValues() != null ? identity.getValues() : "",
                        "workEnvironment", identity.getWorkEnvironment() != null ? identity.getWorkEnvironment() : "",
                        "tone", identity.getTone() != null ? identity.getTone() : ""
                )
        );

        webClient.post()
                .uri("/rag/embed-company")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        null,
                        error -> System.err.println("[MonicaAiClient] embed-company failed: " + error.getMessage())
                );
    }
}