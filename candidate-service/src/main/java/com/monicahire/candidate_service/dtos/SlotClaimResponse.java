package com.monicahire.candidate_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotClaimResponse {
    private boolean allowed;
    private String reason;
}