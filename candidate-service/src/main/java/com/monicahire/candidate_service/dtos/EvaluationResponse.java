package com.monicahire.candidate_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    @JsonProperty("global_score")    private Double globalScore;
    @JsonProperty("cv_score")        private Double cvScore;
    @JsonProperty("interview_score") private Double interviewScore;
    @JsonProperty("ai_percentage")   private Double aiPercentage;
    @JsonProperty("experience_match") private Double experienceMatch;
    @JsonProperty("skills_match")    private Double skillsMatch;
    @JsonProperty("education_match") private Double educationMatch;
    @JsonProperty("culture_fit")     private Double cultureFit;
    @JsonProperty("communication_score") private Double communicationScore;
    @JsonProperty("mindset_score")   private Double mindsetScore;
    @JsonProperty("potential_score") private Double potentialScore;
    @JsonProperty("note")            private String note;
}