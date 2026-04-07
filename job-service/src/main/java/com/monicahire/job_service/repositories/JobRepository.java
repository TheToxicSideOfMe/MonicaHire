package com.monicahire.job_service.repositories;

import com.monicahire.job_service.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findByCompanyId(String companyId);
    int countByCompanyId(String companyId);
}