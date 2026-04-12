package com.monicahire.notification_service.kafka;

import com.monicahire.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final EmailService emailService;

    // ── user.registered → welcome email ──────────────────────────────────────

    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void onUserRegistered(@Payload Map<String, String> event) {
        log.info("Received user.registered event for companyId={}", event.get("companyId"));
        emailService.sendWelcomeEmail(
                event.get("email"),
                event.get("companyName")
        );
    }

    // ── candidate.created → interview link email ──────────────────────────────

    @KafkaListener(topics = "candidate.created", groupId = "notification-service")
    public void onCandidateCreated(@Payload Map<String, String> event) {
        log.info("Received candidate.created event for candidateId={}", event.get("candidateId"));
        emailService.sendInterviewEmail(
                event.get("candidateEmail"),
                event.get("candidateName"),
                event.get("jobId"),
                event.get("interviewToken")
        );
    }

    // ── candidate.status.changed → status email ───────────────────────────────

    @KafkaListener(topics = "candidate.status.changed", groupId = "notification-service")
    public void onCandidateStatusChanged(@Payload Map<String, Object> event) {
        log.info("Received candidate.status.changed event for candidateId={}", event.get("id"));
        emailService.sendStatusEmail(
                (String) event.get("email"),
                (String) event.get("name"),
                (String) event.get("status")
        );
    }

    // ── subscription.expired → expiry email ───────────────────────────────────

    @KafkaListener(topics = "subscription.expired", groupId = "notification-service")
    public void onSubscriptionExpired(@Payload Map<String, String> event) {
        log.info("Received subscription.expired event for companyId={}", event.get("companyId"));
        emailService.sendSubscriptionExpiredEmail(
                event.get("email"),
                event.get("companyName")
        );
    }
}