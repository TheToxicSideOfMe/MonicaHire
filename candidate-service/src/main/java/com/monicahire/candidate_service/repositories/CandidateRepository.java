package com.monicahire.candidate_service.repositories;

import com.monicahire.candidate_service.models.Candidate;
import com.monicahire.candidate_service.models.Candidate.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {

    List<Candidate> findByJobId(String jobId);

    List<Candidate> findByJobIdAndStatus(String jobId, CandidateStatus status);

    List<Candidate> findByCompanyId(String companyId);
}