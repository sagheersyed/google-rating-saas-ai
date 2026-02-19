package com.google.rating.saas_ai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.handler.GoogleReviewsWebhookHandler;
import com.google.rating.saas_ai.service.BusinessService;
import com.google.rating.saas_ai.service.ReviewService;
import com.google.rating.saas_ai.validation.ValidationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    private final BusinessService businessService;
    private final ValidationUtil validationUtil;

    public BusinessController(BusinessService businessService, ValidationUtil validationUtil) {
        this.businessService = businessService;
        this.validationUtil = validationUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Business> registerBusiness(@RequestBody Business business) {
        // Validate the business entity
        validationUtil.validateBusiness(business);

        Business registeredBusiness = businessService.registerBusiness(
                business.getBusinessName(),
                business.getGooglePlaceId(),
                business.getBusinessEmail()
        );
        return ResponseEntity.ok(registeredBusiness);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Business> getBusinessById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        Business business = businessService.getBusinessById(id);
        return ResponseEntity.ok(business);
    }

    @GetMapping("/place/{googlePlaceId}")
    public ResponseEntity<Business> getBusinessByGooglePlaceId(@PathVariable String googlePlaceId) {
        validationUtil.validateGooglePlaceId(googlePlaceId);

        Business business = businessService.getBusinessByGooglePlaceId(googlePlaceId);
        return ResponseEntity.ok(business);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Business> updateBusiness(@PathVariable Long id, @RequestBody Business business) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        // Validate the business entity
        validationUtil.validateBusiness(business);

        Business updatedBusiness = businessService.updateBusiness(id, business);
        return ResponseEntity.ok(updatedBusiness);
    }

    @GetMapping
    public ResponseEntity<List<Business>> getAllBusinesses() {
        List<Business> businesses = businessService.getAllBusinesses();
        return ResponseEntity.ok(businesses);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateBusiness(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        businessService.activateBusiness(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateBusiness(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        businessService.deactivateBusiness(id);
        return ResponseEntity.ok().build();
    }
}
