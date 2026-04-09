package com.monicahire.job_service.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotClaimResponse {
    private boolean allowed;
    private String reason;
}