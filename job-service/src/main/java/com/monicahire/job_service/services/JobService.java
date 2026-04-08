package com.monicahire.job_service.services;

import com.monicahire.job_service.clients.MonicaAiClient;
import com.monicahire.job_service.clients.SubscriptionClient;
import com.monicahire.job_service.dtos.CreateJobRequest;
import com.monicahire.job_service.dtos.JobResponse;
import com.monicahire.job_service.dtos.QuotaCheckResponse;
import com.monicahire.job_service.models.Job;
import com.monicahire.job_service.repositories.JobRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SubscriptionClient subscriptionClient;
    private final MonicaAiClient monicaAiClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public JobService(
            JobRepository jobRepository,
            SubscriptionClient subscriptionClient,
            MonicaAiClient monicaAiClient,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.jobRepository = jobRepository;
        this.subscriptionClient = subscriptionClient;
        this.monicaAiClient = monicaAiClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public JobResponse createJob(String companyId, CreateJobRequest request) {
        // 1. Check quota
        QuotaCheckResponse quota = subscriptionClient.checkJobQuota(companyId);
        if (!quota.isAllowed()) {
            throw new RuntimeException(quota.getReason());
        }

        // 2. Save job with empty questions
        Job job = new Job();
        job.setCompanyId(companyId);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        job.setWorkMode(request.getWorkMode());
        job.setExperienceYears(request.getExperienceYears());
        job.setQuestions(List.of());
        Job saved = jobRepository.save(job);

        // 3. Call monica-ai JobSetupAgent
        List<String> questions = monicaAiClient.setupJob(companyId, saved);

        // 4. Update job with questions
        saved.setQuestions(questions);
        jobRepository.save(saved);

        // 5. Publish job.created
        kafkaTemplate.send("job.created", companyId, saved);

        return toResponse(saved);
    }

    public List<JobResponse> getMyJobs(String companyId) {
        return jobRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public JobResponse getJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return toResponse(job);
    }

    public JobResponse closeJob(String jobId, String companyId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        job.setStatus(Job.JobStatus.CLOSED);
        return toResponse(jobRepository.save(job));
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getCompanyId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getEmploymentType(),
                job.getWorkMode(),
                job.getExperienceYears(),
                job.getStatus(),
                job.getQuestions(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}