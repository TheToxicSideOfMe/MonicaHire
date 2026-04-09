package com.monicahire.interview_service.services;

import com.monicahire.interview_service.dtos.InterviewTokenDto;
import com.monicahire.interview_service.models.InterviewToken;
import com.monicahire.interview_service.models.InterviewToken.TokenStatus;
import com.monicahire.interview_service.repositories.InterviewTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewTokenService {

    private final InterviewTokenRepository tokenRepository;

    @Value("${interview.token.expiry-hours:72}")
    private int expiryHours;

    @Transactional
    public InterviewTokenDto.TokenResponse generateToken(InterviewTokenDto.GenerateRequest request) {
        tokenRepository.findByCandidateIdAndJobId(request.getCandidateId(), request.getJobId())
                .filter(t -> t.getStatus() == TokenStatus.PENDING)
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .ifPresent(t -> {
                    throw new IllegalStateException(
                            "Active token already exists for candidateId=" + request.getCandidateId()
                            + " jobId=" + request.getJobId()
                    );
                });

        InterviewToken token = new InterviewToken();
        token.setCandidateId(request.getCandidateId());
        token.setJobId(request.getJobId());
        token.setCompanyId(request.getCompanyId());
        token.setExpiresAt(LocalDateTime.now().plusHours(expiryHours));

        InterviewToken saved = tokenRepository.save(token);
        log.info("Generated interview token for candidateId={} jobId={}", request.getCandidateId(), request.getJobId());

        return toTokenResponse(saved);
    }

    @Transactional
    public InterviewTokenDto.ValidationResponse validateToken(String tokenValue) {
        InterviewToken token = tokenRepository.findByToken(tokenValue).orElse(null);

        if (token == null) {
            return invalidResponse("Token not found");
        }
        if (token.getStatus() == TokenStatus.USED) {
            return invalidResponse("Token has already been used");
        }
        if (token.getStatus() == TokenStatus.EXPIRED || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            token.setStatus(TokenStatus.EXPIRED);
            tokenRepository.save(token);
            return invalidResponse("Token has expired");
        }

        token.setStatus(TokenStatus.USED);
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        log.info("Token validated and marked USED for candidateId={} jobId={}", token.getCandidateId(), token.getJobId());

        return new InterviewTokenDto.ValidationResponse(true, null, token.getCandidateId(), token.getJobId(), token.getCompanyId());
    }

    @Transactional(readOnly = true)
    public InterviewTokenDto.TokenResponse getTokenInfo(String tokenValue) {
        InterviewToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenValue));
        return toTokenResponse(token);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireStaleTokens() {
        List<InterviewToken> stale = tokenRepository
                .findAllByStatusAndExpiresAtBefore(TokenStatus.PENDING, LocalDateTime.now());
        stale.forEach(t -> t.setStatus(TokenStatus.EXPIRED));
        tokenRepository.saveAll(stale);
        if (!stale.isEmpty()) log.info("Expired {} stale interview tokens", stale.size());
    }

    private InterviewTokenDto.TokenResponse toTokenResponse(InterviewToken token) {
        return new InterviewTokenDto.TokenResponse(
                token.getToken(),
                token.getCandidateId(),
                token.getJobId(),
                token.getCompanyId(),
                token.getStatus().name(),
                token.getExpiresAt(),
                token.getCreatedAt()
        );
    }

    private InterviewTokenDto.ValidationResponse invalidResponse(String reason) {
        return new InterviewTokenDto.ValidationResponse(false, reason, null, null, null);
    }
}