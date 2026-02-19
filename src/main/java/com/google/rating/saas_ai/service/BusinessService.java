package com.google.rating.saas_ai.service;

import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.entity.Subscription;
import com.google.rating.saas_ai.repository.BusinessRepository;
import com.google.rating.saas_ai.repository.SubscriptionRepository;
import com.google.rating.saas_ai.validation.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ValidationUtil validationUtil;

    public BusinessService(BusinessRepository businessRepository,
                           SubscriptionRepository subscriptionRepository,
                           ValidationUtil validationUtil) {
        this.businessRepository = businessRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.validationUtil = validationUtil;
    }

    public Business registerBusiness(String businessName, String googlePlaceId, String businessEmail) {
        // Validate inputs
        validationUtil.validateEmail(businessEmail);
        validationUtil.validateGooglePlaceId(googlePlaceId);

        if (businessName == null || businessName.trim().isEmpty()) {
            throw new IllegalArgumentException("Business name cannot be null or empty");
        }

        // Check if business already exists
        Optional<Business> existingBusiness = businessRepository.findByGooglePlaceId(googlePlaceId);
        if (existingBusiness.isPresent()) {
            throw new RuntimeException("Business with this Google Place ID already exists");
        }

        Business business = new Business(businessName, googlePlaceId, businessEmail);
        validationUtil.validateBusiness(business);
        return businessRepository.save(business);
    }

    public Business updateBusiness(Long businessId, Business updatedBusiness) {
        Business existingBusiness = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Validate the updated business
        validationUtil.validateBusiness(updatedBusiness);

        existingBusiness.setBusinessName(updatedBusiness.getBusinessName());
        existingBusiness.setBusinessEmail(updatedBusiness.getBusinessEmail());
        existingBusiness.setBusinessPhone(updatedBusiness.getBusinessPhone());
        existingBusiness.setBusinessDescription(updatedBusiness.getBusinessDescription());
        existingBusiness.setBusinessCategory(updatedBusiness.getBusinessCategory());
        existingBusiness.setUpdatedAt(LocalDateTime.now());

        return businessRepository.save(existingBusiness);
    }

    public Business getBusinessById(Long businessId) {
        if (businessId == null || businessId <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        return businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    public Business getBusinessByGooglePlaceId(String googlePlaceId) {
        validationUtil.validateGooglePlaceId(googlePlaceId);

        return businessRepository.findByGooglePlaceId(googlePlaceId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    public void activateBusiness(Long businessId) {
        if (businessId == null || businessId <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        Business business = getBusinessById(businessId);
        business.setIsActive(true);
        businessRepository.save(business);
    }

    public void deactivateBusiness(Long businessId) {
        if (businessId == null || businessId <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        Business business = getBusinessById(businessId);
        business.setIsActive(false);
        businessRepository.save(business);
    }

    public boolean isBusinessSubscribedAndActive(Long businessId) {
        if (businessId == null || businessId <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }

        Business business = getBusinessById(businessId);
        return business.getIsSubscribed() &&
                business.getIsActive() &&
                (business.getSubscriptionExpiryDate() == null ||
                        business.getSubscriptionExpiryDate().isAfter(LocalDateTime.now()));
    }
}
