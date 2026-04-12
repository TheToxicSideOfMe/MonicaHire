package com.monicahire.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final TransactionalEmailsApi emailsApi;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // ── Welcome email ─────────────────────────────────────────────────────────

    public void sendWelcomeEmail(String toEmail, String companyName) {
        String subject = "Welcome to MonicaHire!";
        String content = String.format("""
                <h2>Welcome to MonicaHire, %s!</h2>
                <p>Your account has been created successfully.</p>
                <p>You can now start posting jobs and finding the best candidates using AI-powered evaluations.</p>
                <p><a href="%s">Go to Dashboard</a></p>
                <br/>
                <p>The MonicaHire Team</p>
                """, companyName, frontendUrl);

        send(toEmail, companyName, subject, content);
    }

    // ── Interview link email ──────────────────────────────────────────────────

    public void sendInterviewEmail(String toEmail, String candidateName, String jobId, String token) {
        String interviewUrl = String.format("%s/interview?token=%s", frontendUrl, token);
        String subject = "You've been invited to an interview!";
        String content = String.format("""
                <h2>Hello %s,</h2>
                <p>You have been selected to proceed to the interview stage.</p>
                <p>Please click the link below to complete your interview. The link is valid for 72 hours.</p>
                <p><a href="%s" style="background-color:#4F46E5;color:white;padding:12px 24px;text-decoration:none;border-radius:6px;">Start Interview</a></p>
                <br/>
                <p>If the button doesn't work, copy and paste this link:</p>
                <p>%s</p>
                <br/>
                <p>Good luck!</p>
                <p>The MonicaHire Team</p>
                """, candidateName, interviewUrl, interviewUrl);

        send(toEmail, candidateName, subject, content);
    }

    // ── Status change email ───────────────────────────────────────────────────

    public void sendStatusEmail(String toEmail, String candidateName, String status) {
        String subject;
        String content;

        switch (status) {
            case "SHORTLISTED" -> {
                subject = "Great news — you've been shortlisted!";
                content = String.format("""
                        <h2>Congratulations %s!</h2>
                        <p>We're pleased to inform you that you have been <strong>shortlisted</strong> for the position.</p>
                        <p>The hiring team will be in touch with you shortly regarding next steps.</p>
                        <br/>
                        <p>The MonicaHire Team</p>
                        """, candidateName);
            }
            case "HIRED" -> {
                subject = "Offer Extended — Welcome aboard!";
                content = String.format("""
                        <h2>Congratulations %s!</h2>
                        <p>We are thrilled to inform you that you have been <strong>selected for the position</strong>.</p>
                        <p>The hiring team will reach out to you with the official offer and next steps.</p>
                        <br/>
                        <p>The MonicaHire Team</p>
                        """, candidateName);
            }
            case "REJECTED" -> {
                subject = "Update on your application";
                content = String.format("""
                        <h2>Hello %s,</h2>
                        <p>Thank you for taking the time to apply and complete the interview process.</p>
                        <p>After careful consideration, we regret to inform you that we will not be moving forward with your application at this time.</p>
                        <p>We wish you the best in your job search.</p>
                        <br/>
                        <p>The MonicaHire Team</p>
                        """, candidateName);
            }
            default -> {
                log.warn("Unknown status for email notification: {}", status);
                return;
            }
        }

        send(toEmail, candidateName, subject, content);
    }

    // ── Subscription expired email ────────────────────────────────────────────

    public void sendSubscriptionExpiredEmail(String toEmail, String companyName) {
        String subject = "Your MonicaHire subscription has expired";
        String content = String.format("""
                <h2>Hello %s,</h2>
                <p>Your MonicaHire subscription has <strong>expired</strong>.</p>
                <p>To continue posting jobs and evaluating candidates, please renew your subscription.</p>
                <p><a href="%s/billing" style="background-color:#4F46E5;color:white;padding:12px 24px;text-decoration:none;border-radius:6px;">Renew Subscription</a></p>
                <br/>
                <p>The MonicaHire Team</p>
                """, companyName, frontendUrl);

        send(toEmail, companyName, subject, content);
    }

    // ── Core send ─────────────────────────────────────────────────────────────

    private void send(String toEmail, String toName, String subject, String htmlContent) {
        try {
            SendSmtpEmail email = new SendSmtpEmail();

            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(senderEmail);
            sender.setName(senderName);
            email.setSender(sender);

            SendSmtpEmailTo recipient = new SendSmtpEmailTo();
            recipient.setEmail(toEmail);
            recipient.setName(toName);
            email.setTo(List.of(recipient));

            email.setSubject(subject);
            email.setHtmlContent(htmlContent);

            emailsApi.sendTransacEmail(email);
            log.info("Email sent to {} — subject: {}", toEmail, subject);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}