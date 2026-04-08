package com.monicahire.job_service.clients;

import com.monicahire.job_service.models.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class MonicaAiClient {

    private final WebClient webClient;

    public MonicaAiClient(@Value("${monica-ai.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public List<String> setupJob(String companyId, Job job) {
        // Will be fully wired when JobSetupAgent is built in monica-ai
        // For now returns empty list so job creation works end to end
        return List.of();
    }
}