package com.monicahire.candidate_service.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Double globalScore;
    private Double cvScore;
    private Double interviewScore;
    private Double aiPercentage;
    private Double experienceMatch;
    private Double skillsMatch;
    private Double educationMatch;
    private Double cultureFit;
    private Double communicationScore;
    private Double mindsetScore;
    private Double potentialScore;
    private String note;
}