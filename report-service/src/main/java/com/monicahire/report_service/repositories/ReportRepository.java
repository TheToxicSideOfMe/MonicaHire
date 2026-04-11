package com.monicahire.report_service.repositories;

import com.monicahire.report_service.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

    List<Report> findByCompanyId(String companyId);

    Optional<Report> findByCandidateId(String candidateId);
}