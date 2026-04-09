package com.monicahire.candidate_service.clients;

import com.monicahire.candidate_service.dtos.JobResponse;
import com.monicahire.candidate_service.dtos.SlotClaimResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JobClient {

    private final WebClient webClient;

    public JobClient(@Value("${job-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public SlotClaimResponse claimSlot(String jobId) {
        return webClient.post()
                .uri("/jobs/{jobId}/claim-slot", jobId)
                .retrieve()
                .bodyToMono(SlotClaimResponse.class)
                .block();
    }

    public JobResponse getJob(String jobId) {
        return webClient.get()
                .uri("/jobs/{jobId}", jobId)
                .retrieve()
                .bodyToMono(JobResponse.class)
                .block();
    }
}