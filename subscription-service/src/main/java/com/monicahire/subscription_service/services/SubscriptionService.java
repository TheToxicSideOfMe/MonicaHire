package com.monicahire.subscription_service.services;

import com.monicahire.subscription_service.dtos.QuotaCheckResponse;
import com.monicahire.subscription_service.dtos.SubscriptionResponse;
import com.monicahire.subscription_service.models.Plan;
import com.monicahire.subscription_service.models.Subscription;
import com.monicahire.subscription_service.models.SubscriptionUsage;
import com.monicahire.subscription_service.repositories.PlanRepository;
import com.monicahire.subscription_service.repositories.SubscriptionRepository;
import com.monicahire.subscription_service.repositories.SubscriptionUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUsageRepository usageRepository;
    private final PlanRepository planRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            SubscriptionUsageRepository usageRepository,
            PlanRepository planRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.usageRepository = usageRepository;
        this.planRepository = planRepository;
    }

    // Called by Kafka consumer on user.registered
    @Transactional
    public void createInitialSubscription(String companyId) {
        if (subscriptionRepository.existsByCompanyId(companyId)) return;

        Subscription subscription = new Subscription();
        subscription.setCompanyId(companyId);
        subscription.setPlan(Plan.PlanName.STARTER);
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(30));
        subscriptionRepository.save(subscription);

        SubscriptionUsage usage = new SubscriptionUsage();
        usage.setCompanyId(companyId);
        usageRepository.save(usage);
    }

    public SubscriptionResponse getMySubscription(String companyId) {
        Subscription subscription = subscriptionRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Plan plan = planRepository.findById(subscription.getPlan())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        SubscriptionUsage usage = usageRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Usage not found"));

        return new SubscriptionResponse(
                companyId,
                subscription.getPlan(),
                subscription.getStatus(),
                plan.getMaxJobs(),
                plan.getMaxCandidatesPerJob(),
                plan.getMaxReports(),
                usage.getJobsUsed(),
                usage.getReportsUsed(),
                subscription.getEndDate()
        );
    }

    public QuotaCheckResponse checkJobQuota(String companyId) {
        Subscription subscription = subscriptionRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            return new QuotaCheckResponse(false, "Subscription is not active");
        }

        Plan plan = planRepository.findById(subscription.getPlan())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (plan.getMaxJobs() == -1) return new QuotaCheckResponse(true, "OK");

        SubscriptionUsage usage = usageRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Usage not found"));

        if (usage.getJobsUsed() >= plan.getMaxJobs()) {
            return new QuotaCheckResponse(false, "Job posting limit reached for your plan");
        }

        return new QuotaCheckResponse(true, "OK");
    }

    public QuotaCheckResponse checkReportQuota(String companyId) {
        Subscription subscription = subscriptionRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            return new QuotaCheckResponse(false, "Subscription is not active");
        }

        Plan plan = planRepository.findById(subscription.getPlan())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (plan.getMaxReports() == -1) return new QuotaCheckResponse(true, "OK");

        SubscriptionUsage usage = usageRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Usage not found"));

        if (usage.getReportsUsed() >= plan.getMaxReports()) {
            return new QuotaCheckResponse(false, "Report limit reached for your plan");
        }

        return new QuotaCheckResponse(true, "OK");
    }

    @Transactional
    public void incrementJobUsage(String companyId) {
        SubscriptionUsage usage = usageRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Usage not found"));
        usage.setJobsUsed(usage.getJobsUsed() + 1);
        usageRepository.save(usage);
    }

    @Transactional
    public void incrementReportUsage(String companyId) {
        SubscriptionUsage usage = usageRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Usage not found"));
        usage.setReportsUsed(usage.getReportsUsed() + 1);
        usageRepository.save(usage);
    }
}