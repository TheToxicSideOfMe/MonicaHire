package com.monicahire.job_service.dtos;

import com.monicahire.job_service.models.Job.EmploymentType;
import com.monicahire.job_service.models.Job.WorkMode;
import lombok.Data;

@Data
public class CreateJobRequest {
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private int experienceYears;
}