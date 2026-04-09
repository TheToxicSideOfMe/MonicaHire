package com.monicahire.candidate_service.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenGenerateResponse {
    private String token;
    private String candidateId;
    private String jobId;
    private String companyId;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
