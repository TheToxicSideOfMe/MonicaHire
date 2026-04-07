package com.monicahire.subscription_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuotaCheckResponse {
    private boolean allowed;
    private String reason;
}