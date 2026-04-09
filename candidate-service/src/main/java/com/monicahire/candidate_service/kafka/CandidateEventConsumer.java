package com.monicahire.candidate_service.kafka;

import com.monicahire.candidate_service.clients.MonicaAiClient;
import com.monicahire.candidate_service.dtos.EvaluationRequest;
import com.monicahire.candidate_service.dtos.EvaluationResponse;
import com.monicahire.candidate_service.models.Candidate;
import com.monicahire.candidate_service.models.Candidate.CandidateStatus;
import com.monicahire.candidate_service.repositories.CandidateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CandidateEventConsumer {

    private final CandidateRepository candidateRepository;
    private final MonicaAiClient monicaAiClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CandidateEventConsumer(
            CandidateRepository candidateRepository,
            MonicaAiClient monicaAiClient,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.candidateRepository = candidateRepository;
        this.monicaAiClient = monicaAiClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "candidate.submitted", groupId = "candidate-service")
    @Transactional
    public void onCandidateSubmitted(CandidateSubmittedEvent event) {
        log.info("Evaluation started for candidateId={} jobId={}", event.getCandidateId(), event.getJobId());

        Candidate candidate = candidateRepository.findById(event.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + event.getCandidateId()));

        // Call EvaluationAgent — this is the slow part, runs in background
        EvaluationResponse evaluation = monicaAiClient.evaluate(
                new EvaluationRequest(
                        event.getCompanyId(),
                        event.getJobId(),
                        event.getCvUrl(),
                        event.getAnswers()
                )
        );

        // Save all scores
        candidate.setGlobalScore(evaluation.getGlobalScore());
        candidate.setCvScore(evaluation.getCvScore());
        candidate.setInterviewScore(evaluation.getInterviewScore());
        candidate.setAiPercentage(evaluation.getAiPercentage());
        candidate.setExperienceMatch(evaluation.getExperienceMatch());
        candidate.setSkillsMatch(evaluation.getSkillsMatch());
        candidate.setEducationMatch(evaluation.getEducationMatch());
        candidate.setCultureFit(evaluation.getCultureFit());
        candidate.setCommunicationScore(evaluation.getCommunicationScore());
        candidate.setMindsetScore(evaluation.getMindsetScore());
        candidate.setPotentialScore(evaluation.getPotentialScore());
        candidate.setNote(evaluation.getNote());
        candidate.setStatus(CandidateStatus.EVALUATED);
        candidate.setEvaluatedAt(LocalDateTime.now());

        Candidate evaluated = candidateRepository.save(candidate);

        // Publish candidate.evaluated → notification-service
        kafkaTemplate.send("candidate.evaluated", event.getCompanyId(), evaluated);

        log.info("Evaluation complete for candidateId={} globalScore={}", 
                evaluated.getId(), evaluated.getGlobalScore());
    }
}