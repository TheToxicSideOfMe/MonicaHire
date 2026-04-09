package com.monicahire.candidate_service.services;

import com.monicahire.candidate_service.clients.InterviewClient;
import com.monicahire.candidate_service.clients.JobClient;
import com.monicahire.candidate_service.clients.MonicaAiClient;
import com.monicahire.candidate_service.dtos.*;
import com.monicahire.candidate_service.kafka.CandidateSubmittedEvent;
import com.monicahire.candidate_service.models.Candidate;
import com.monicahire.candidate_service.models.Candidate.AnswerEntry;
import com.monicahire.candidate_service.models.Candidate.CandidateStatus;
import com.monicahire.candidate_service.repositories.CandidateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final JobClient jobClient;
    private final InterviewClient interviewClient;
    private final MonicaAiClient monicaAiClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CandidateService(
            CandidateRepository candidateRepository,
            JobClient jobClient,
            InterviewClient interviewClient,
            MonicaAiClient monicaAiClient,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.candidateRepository = candidateRepository;
        this.jobClient = jobClient;
        this.interviewClient = interviewClient;
        this.monicaAiClient = monicaAiClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    // ── Phase 1 — Apply ───────────────────────────────────────────────────────

    @Transactional
    public CandidateDto.CandidateResponse apply(CandidateDto.ApplyRequest request) {

        // 1. Check job capacity — claim slot before saving anything (no orphans)
        SlotClaimResponse slot = jobClient.claimSlot(request.getJobId());
        if (!slot.isAllowed()) {
            throw new RuntimeException(slot.getReason());
        }

        // 2. Fetch job to get companyId
        JobResponse job = jobClient.getJob(request.getJobId());

        // 3. Save candidate now that slot is confirmed
        Candidate candidate = new Candidate();
        candidate.setJobId(request.getJobId());
        candidate.setCompanyId(job.getCompanyId());
        candidate.setName(request.getName());
        candidate.setPhone(request.getPhone());
        candidate.setLocation(request.getLocation());
        candidate.setCvUrl(request.getCvUrl());
        Candidate saved = candidateRepository.save(candidate);

        // 4. Generate interview token
        TokenGenerateResponse tokenResponse = interviewClient.generateToken(
                saved.getId(), saved.getJobId(), saved.getCompanyId()
        );

        // 5. Publish candidate.created → notification-service sends interview email
        kafkaTemplate.send("candidate.created", saved.getCompanyId(), new java.util.HashMap<>() {{
            put("candidateId", saved.getId());
            put("candidateName", saved.getName());
            put("candidateEmail", saved.getPhone()); // swap for email once field is added
            put("jobId", saved.getJobId());
            put("interviewToken", tokenResponse.getToken());
        }});

        log.info("Candidate {} applied for job {} — interview token generated", saved.getId(), saved.getJobId());

        return toResponse(saved);
    }

    // ── Phase 2 — Submit interview ────────────────────────────────────────────

    @Transactional
    public CandidateDto.CandidateResponse submit(CandidateDto.SubmitRequest request) {
 
        // 1. Validate and consume the token
        TokenValidationResponse validation = interviewClient.validateToken(request.getToken());
        if (!validation.isValid()) {
            throw new RuntimeException(validation.getReason());
        }
 
        // 2. Load candidate
        Candidate candidate = candidateRepository.findById(validation.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
 
        // 3. Fetch job questions and pair with answers
        JobResponse job = jobClient.getJob(candidate.getJobId());
        List<String> questions = job.getQuestions();
        List<String> answers = request.getAnswers();
 
        if (answers.size() != questions.size()) {
            throw new RuntimeException(
                    "Answer count mismatch — expected " + questions.size() + ", got " + answers.size()
            );
        }
 
        List<AnswerEntry> pairedAnswers = IntStream.range(0, questions.size())
                .mapToObj(i -> new AnswerEntry(questions.get(i), answers.get(i)))
                .toList();
 
        // 4. Save answers and mark SUBMITTED
        candidate.setAnswers(pairedAnswers);
        candidate.setStatus(CandidateStatus.SUBMITTED);
        candidate.setSubmittedAt(LocalDateTime.now());
        Candidate saved = candidateRepository.save(candidate);
 
        // 5. Publish candidate.submitted → consumer will call EvaluationAgent
        kafkaTemplate.send("candidate.submitted", saved.getCompanyId(),
                new CandidateSubmittedEvent(
                        saved.getId(),
                        saved.getJobId(),
                        saved.getCompanyId(),
                        saved.getCvUrl(),
                        pairedAnswers
                )
        );
 
        log.info("Candidate {} submitted for job {} — evaluation queued", saved.getId(), saved.getJobId());
 
        return toResponse(saved);
    }

    // ── Company actions ───────────────────────────────────────────────────────

    @Transactional
    public CandidateDto.CandidateResponse updateStatus(String candidateId, String companyId, CandidateStatus status) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!candidate.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        candidate.setStatus(status);
        Candidate saved = candidateRepository.save(candidate);

        // Publish status change for notification-service
        kafkaTemplate.send("candidate.status.changed", companyId, saved);

        return toResponse(saved);
    }

    public List<CandidateDto.CandidateResponse> getCandidatesByJob(String jobId, String companyId) {
        return candidateRepository.findByJobId(jobId)
                .stream()
                .filter(c -> c.getCompanyId().equals(companyId))
                .map(this::toResponse)
                .toList();
    }

    public CandidateDto.CandidateResponse getCandidate(String candidateId, String companyId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!candidate.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        return toResponse(candidate);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyEvaluation(Candidate candidate, EvaluationResponse eval) {
        candidate.setGlobalScore(eval.getGlobalScore());
        candidate.setCvScore(eval.getCvScore());
        candidate.setInterviewScore(eval.getInterviewScore());
        candidate.setAiPercentage(eval.getAiPercentage());
        candidate.setExperienceMatch(eval.getExperienceMatch());
        candidate.setSkillsMatch(eval.getSkillsMatch());
        candidate.setEducationMatch(eval.getEducationMatch());
        candidate.setCultureFit(eval.getCultureFit());
        candidate.setCommunicationScore(eval.getCommunicationScore());
        candidate.setMindsetScore(eval.getMindsetScore());
        candidate.setPotentialScore(eval.getPotentialScore());
        candidate.setNote(eval.getNote());
    }

    private CandidateDto.CandidateResponse toResponse(Candidate c) {
        return new CandidateDto.CandidateResponse(
                c.getId(), c.getJobId(), c.getCompanyId(),
                c.getName(), c.getPhone(), c.getLocation(), c.getCvUrl(),
                c.getAnswers(), c.getStatus(),
                c.getGlobalScore(), c.getCvScore(), c.getInterviewScore(),
                c.getAiPercentage(), c.getExperienceMatch(), c.getSkillsMatch(),
                c.getEducationMatch(), c.getCultureFit(), c.getCommunicationScore(),
                c.getMindsetScore(), c.getPotentialScore(), c.getNote(),
                c.getSubmittedAt(), c.getEvaluatedAt(), c.getCreatedAt()
        );
    }
}