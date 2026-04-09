package com.monicahire.candidate_service.kafka;

import com.monicahire.candidate_service.models.Candidate.AnswerEntry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSubmittedEvent {
    private String candidateId;
    private String jobId;
    private String companyId;
    private String cvUrl;
    private List<AnswerEntry> answers;
}