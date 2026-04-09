package com.monicahire.candidate_service.clients;

import com.monicahire.candidate_service.dtos.TokenGenerateRequest;
import com.monicahire.candidate_service.dtos.TokenGenerateResponse;
import com.monicahire.candidate_service.dtos.TokenValidateRequest;
import com.monicahire.candidate_service.dtos.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InterviewClient {

    private final WebClient webClient;

    public InterviewClient(@Value("${interview-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public TokenGenerateResponse generateToken(String candidateId, String jobId, String companyId) {
        return webClient.post()
                .uri("/api/interview-tokens/generate")
                .bodyValue(new TokenGenerateRequest(candidateId, jobId, companyId))
                .retrieve()
                .bodyToMono(TokenGenerateResponse.class)
                .block();
    }

    public TokenValidationResponse validateToken(String token) {
        return webClient.post()
                .uri("/api/interview-tokens/validate")
                .bodyValue(new TokenValidateRequest(token))
                .retrieve()
                .bodyToMono(TokenValidationResponse.class)
                .block();
    }
}