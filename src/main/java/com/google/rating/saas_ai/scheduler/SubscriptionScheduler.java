// NEW_FILE_CODE
package com.google.rating.saas_ai.scheduler;

import com.google.rating.saas_ai.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduler.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Check for expired subscriptions daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void checkExpiredSubscriptions() {
        logger.info("Checking for expired subscriptions...");
        subscriptionService.checkExpiredSubscriptions();
        logger.info("Finished checking for expired subscriptions");
    }

    /**
     * Send renewal reminders 3 days before expiration
     */
    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9 AM
    public void sendRenewalReminders() {
        logger.info("Sending subscription renewal reminders...");
        // Implementation for sending renewal reminders would go here
        logger.info("Finished sending subscription renewal reminders");
    }
}
