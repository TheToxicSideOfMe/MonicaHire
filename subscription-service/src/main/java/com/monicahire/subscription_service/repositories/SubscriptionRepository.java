package com.monicahire.subscription_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monicahire.subscription_service.models.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription,String> {
    Boolean existsByCompanyId(String companyId);
    Optional<Subscription>findByCompanyId(String companyId);
}
