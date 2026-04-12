package com.monicahire.subscription_service.scheduler;

import com.monicahire.subscription_service.models.Subscription;
import com.monicahire.subscription_service.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(cron = "0 0 8 * * *") // every day at 8am
    @Transactional
    public void checkExpiredSubscriptions() {
        List<Subscription> expired = subscriptionRepository
                .findAllByStatusAndEndDateBefore(
                        Subscription.SubscriptionStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (Subscription subscription : expired) {
            subscription.setStatus(Subscription.SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);

            Map<String, String> event = new HashMap<>();
            event.put("companyId", subscription.getCompanyId());
            // email and companyName need to come from user-service
            // for now we publish companyId and let notification-service
            // or we add a UserClient here — see note below
            kafkaTemplate.send("subscription.expired", subscription.getCompanyId(), event);

            log.info("Subscription expired for companyId={}", subscription.getCompanyId());
        }

        if (!expired.isEmpty()) {
            log.info("Marked {} subscriptions as expired", expired.size());
        }
    }
}