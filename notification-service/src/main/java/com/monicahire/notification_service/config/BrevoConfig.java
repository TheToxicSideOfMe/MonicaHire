package com.monicahire.notification_service.config;

import sibApi.TransactionalEmailsApi;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


@org.springframework.context.annotation.Configuration
public class BrevoConfig {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Bean
    public TransactionalEmailsApi transactionalEmailsApi() {
        ApiClient client = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) client.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);
        return new TransactionalEmailsApi();
    }
}