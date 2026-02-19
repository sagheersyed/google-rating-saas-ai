package com.google.rating.saas_ai.controller;

import com.google.rating.saas_ai.entity.Subscription;
import com.google.rating.saas_ai.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Subscription> createSubscription(
            @RequestParam Long businessId,
            @RequestParam Subscription.SubscriptionPlan plan) {

        if (businessId == null || businessId <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        if (plan == null) {
            throw new IllegalArgumentException("Subscription plan cannot be null");
        }

        Subscription subscription = subscriptionService.createSubscription(businessId, plan);
        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<Subscription> renewSubscription(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Subscription ID must be a positive number");
        }

        Subscription subscription = subscriptionService.renewSubscription(id);
        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Subscription ID must be a positive number");
        }

        subscriptionService.cancelSubscription(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Subscription>> getBusinessSubscriptions(@PathVariable Long businessId) {
        if (businessId == null || businessId <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        List<Subscription> subscriptions = subscriptionService.getBusinessSubscriptions(businessId);
        return ResponseEntity.ok(subscriptions);
    }
}
