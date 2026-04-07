package com.monicahire.subscription_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monicahire.subscription_service.models.SubscriptionUsage;

public interface SubscriptionUsageRepository extends JpaRepository<SubscriptionUsage, String> {
    Optional<SubscriptionUsage>findByCompanyId(String companyId);
}