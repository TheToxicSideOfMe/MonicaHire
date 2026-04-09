package com.monicahire.candidate_service.clients;

import com.monicahire.candidate_service.dtos.EvaluationRequest;
import com.monicahire.candidate_service.dtos.EvaluationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MonicaAiClient {

    private final WebClient webClient;

    public MonicaAiClient(@Value("${monica-ai.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public EvaluationResponse evaluate(EvaluationRequest request) {
        return webClient.post()
                .uri("/agents/evaluate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EvaluationResponse.class)
                .block();
    }
}