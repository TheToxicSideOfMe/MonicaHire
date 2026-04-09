package com.monicahire.candidate_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenGenerateRequest {
    private String candidateId;
    private String jobId;
    private String companyId;
}
