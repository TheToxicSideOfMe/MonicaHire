package com.monicahire.candidate_service.dtos;


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

    public enum EmploymentType {
        FULL_TIME, PART_TIME, FREELANCE, INTERNSHIP
    }

    public enum WorkMode {
        REMOTE, HYBRID, ON_SITE
    }

    public enum JobStatus {
        OPEN, CLOSED
    }
}