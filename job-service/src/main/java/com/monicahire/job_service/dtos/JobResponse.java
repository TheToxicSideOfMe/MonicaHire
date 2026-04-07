package com.monicahire.job_service.dtos;

import com.monicahire.job_service.models.Job.EmploymentType;
import com.monicahire.job_service.models.Job.JobStatus;
import com.monicahire.job_service.models.Job.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class JobResponse {
    private String id;
    private String companyId;
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private int experienceYears;
    private JobStatus status;
    private List<String> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}