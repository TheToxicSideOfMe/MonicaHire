package com.monicahire.subscription_service.controllers;

import com.monicahire.subscription_service.dtos.PlanLimitsResponse;
import com.monicahire.subscription_service.dtos.QuotaCheckResponse;
import com.monicahire.subscription_service.dtos.SubscriptionResponse;
import com.monicahire.subscription_service.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/my")
    public ResponseEntity<SubscriptionResponse> getMySubscription(
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(subscriptionService.getMySubscription(companyId));
    }

    @GetMapping("/check-quota/jobs")
    public ResponseEntity<QuotaCheckResponse> checkJobQuota(
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(subscriptionService.checkJobQuota(companyId));
    }

    @GetMapping("/check-quota/reports")
    public ResponseEntity<QuotaCheckResponse> checkReportQuota(
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(subscriptionService.checkReportQuota(companyId));
    }

    @GetMapping("/plan-limits")
    public ResponseEntity<PlanLimitsResponse> getPlanLimits(
            @RequestHeader("X-User-Id") String companyId
    ) {
        return ResponseEntity.ok(subscriptionService.getPlanLimits(companyId));
    }
}