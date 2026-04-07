package com.monicahire.subscription_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monicahire.subscription_service.models.Plan;

public interface PlanRepository extends JpaRepository<Plan,Plan.PlanName> {

}
