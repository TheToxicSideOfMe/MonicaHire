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
        Map<String, Object> body = Map.of(
                "company_id", companyId,
                "job_id", job.getId(),
                "title", job.getTitle(),
                "description", job.getDescription(),
                "location", job.getLocation() != null ? job.getLocation() : "",
                "employmentType", job.getEmploymentType().name(),
                "workMode", job.getWorkMode().name(),
                "experienceYears", job.getExperienceYears()
        );
    
        // blocking call — job creation waits for questions before saving
        var response = webClient.post()
                .uri("/agents/setup-job")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    
        return (List<String>) response.get("questions");
    }

    
}