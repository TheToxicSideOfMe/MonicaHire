package com.monicahire.candidate_service.dtos;

import com.monicahire.candidate_service.models.Candidate.AnswerEntry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    private String companyId;
    private String jobId;
    private String cvUrl;
    private List<AnswerEntry> answers;
}