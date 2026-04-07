package com.monicahire.subscription_service.seeders;

import com.monicahire.subscription_service.models.Plan;
import com.monicahire.subscription_service.repositories.PlanRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanSeeder implements ApplicationRunner {

    private final PlanRepository planRepository;

    public PlanSeeder(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (planRepository.count() > 0) return;

        planRepository.saveAll(List.of(
                new Plan(Plan.PlanName.STARTER,    3,   10,  0,   0.0),
                new Plan(Plan.PlanName.GROWTH,     10,  50,  10,  49.0),
                new Plan(Plan.PlanName.ENTERPRISE, -1,  -1,  -1,  199.0)
        ));

        System.out.println("[PlanSeeder] Plans seeded successfully.");
    }
}