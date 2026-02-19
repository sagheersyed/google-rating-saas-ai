// NEW_FILE_CODE
package com.google.rating.saas_ai.service;

import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.entity.Subscription;
import com.google.rating.saas_ai.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final BusinessService businessService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               BusinessService businessService) {
        this.subscriptionRepository = subscriptionRepository;
        this.businessService = businessService;
    }

    public Subscription createSubscription(Long businessId, Subscription.SubscriptionPlan plan) {
        Business business = businessService.getBusinessById(businessId);

        // Cancel any existing active subscriptions
        List<Subscription> existingSubscriptions = subscriptionRepository.findByBusiness(business);
        for (Subscription sub : existingSubscriptions) {
            if (sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
                sub.setStatus(Subscription.SubscriptionStatus.INACTIVE);
                subscriptionRepository.save(sub);
            }
        }

        Subscription subscription = new Subscription();
        subscription.setBusiness(business);
        subscription.setPlan(plan);
        subscription.setAmount(java.math.BigDecimal.valueOf(plan.getPrice()));
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(30)); // 30-day cycle
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);

        // Update business subscription status
        business.setIsSubscribed(true);
        business.setSubscriptionExpiryDate(subscription.getEndDate());

        return subscriptionRepository.save(subscription);
    }

    public Subscription renewSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(30));
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);

        // Update business subscription status
        Business business = subscription.getBusiness();
        business.setIsSubscribed(true);
        business.setSubscriptionExpiryDate(subscription.getEndDate());

        return subscriptionRepository.save(subscription);
    }

    public void cancelSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);

        // Update business subscription status
        Business business = subscription.getBusiness();
        business.setIsSubscribed(false);

        subscriptionRepository.save(subscription);
    }

    public boolean isSubscriptionActive(Long businessId) {
        Business business = businessService.getBusinessById(businessId);
        return business.getIsSubscribed() &&
                (business.getSubscriptionExpiryDate() == null ||
                        business.getSubscriptionExpiryDate().isAfter(LocalDateTime.now()));
    }

    public List<Subscription> getBusinessSubscriptions(Long businessId) {
        Business business = businessService.getBusinessById(businessId);
        return subscriptionRepository.findByBusiness(business);
    }

    public void checkExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findByStatusAndEndDateBefore(Subscription.SubscriptionStatus.ACTIVE, LocalDateTime.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(Subscription.SubscriptionStatus.EXPIRED);

            // Update business subscription status
            Business business = subscription.getBusiness();
            business.setIsSubscribed(false);

            subscriptionRepository.save(subscription);
        }
    }
}
