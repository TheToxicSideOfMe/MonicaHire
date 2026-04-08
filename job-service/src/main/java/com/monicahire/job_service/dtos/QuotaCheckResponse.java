package com.monicahire.job_service.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuotaCheckResponse {
    private boolean allowed;
    private String reason;
}