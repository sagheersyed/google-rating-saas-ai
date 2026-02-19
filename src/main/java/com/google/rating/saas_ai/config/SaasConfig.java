// NEW_FILE_CODE
package com.google.rating.saas_ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "saas")
public class SaasConfig {

    private Google google = new Google();
    private Webhook webhook = new Webhook();
    private Subscription subscription = new Subscription();

    // Getters and setters
    public Google getGoogle() {
        return google;
    }

    public void setGoogle(Google google) {
        this.google = google;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    public void setWebhook(Webhook webhook) {
        this.webhook = webhook;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public static class Google {
        private String apiKey;
        private String clientId;
        private String clientSecret;

        // Getters and setters
        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

    public static class Webhook {
        private String secret;
        private String verificationToken;

        // Getters and setters
        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getVerificationToken() {
            return verificationToken;
        }

        public void setVerificationToken(String verificationToken) {
            this.verificationToken = verificationToken;
        }
    }

    public static class Subscription {
        private boolean autoRenewEnabled = true;
        private int renewalReminderDays = 3;
        private int trialPeriodDays = 7;

        // Getters and setters
        public boolean isAutoRenewEnabled() {
            return autoRenewEnabled;
        }

        public void setAutoRenewEnabled(boolean autoRenewEnabled) {
            this.autoRenewEnabled = autoRenewEnabled;
        }

        public int getRenewalReminderDays() {
            return renewalReminderDays;
        }

        public void setRenewalReminderDays(int renewalReminderDays) {
            this.renewalReminderDays = renewalReminderDays;
        }

        public int getTrialPeriodDays() {
            return trialPeriodDays;
        }

        public void setTrialPeriodDays(int trialPeriodDays) {
            this.trialPeriodDays = trialPeriodDays;
        }
    }
}
