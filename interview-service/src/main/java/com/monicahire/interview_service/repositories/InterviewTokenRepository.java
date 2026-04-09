package com.monicahire.interview_service.repositories;

import com.monicahire.interview_service.models.InterviewToken;
import com.monicahire.interview_service.models.InterviewToken.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewTokenRepository extends JpaRepository<InterviewToken, String> {

    Optional<InterviewToken> findByToken(String token);

    Optional<InterviewToken> findByCandidateIdAndJobId(String candidateId, String jobId);

    List<InterviewToken> findAllByStatusAndExpiresAtBefore(TokenStatus status, LocalDateTime now);
}