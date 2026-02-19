package com.google.rating.saas_ai.service;

import com.google.rating.saas_ai.entity.Review;
import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.repository.ReviewRepository;
import com.google.rating.saas_ai.validation.ValidationUtil;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final AiReplyService aiService;
    private final BusinessService businessService;
    private final EmailNotificationService emailNotificationService;
    private final ValidationUtil validationUtil;

    public ReviewService(ReviewRepository reviewRepo, AiReplyService aiService, BusinessService businessService, EmailNotificationService emailNotificationService, ValidationUtil validationUtil) {
        this.reviewRepo = reviewRepo;
        this.aiService = aiService;
        this.businessService = businessService;
        this.emailNotificationService = emailNotificationService;
        this.validationUtil = validationUtil;
    }

    public Review processReview(Review review, Long businessId) {
        // Validate inputs
        if (businessId == null || businessId <= 0) {
            throw new IllegalArgumentException("Business ID must be a positive number");
        }
        validationUtil.validateReview(review);

        // Check if business has an active subscription
        if (!businessService.isBusinessSubscribedAndActive(businessId)) {
            throw new RuntimeException("Business does not have an active subscription");
        }

        Business business = businessService.getBusinessById(businessId);
        review.setBusiness(business);

        String reply = aiService.generateReplyWithContext(
                review.getComment(),
                review.getRating(),
                business.getBusinessName(),
                business.getBusinessCategory()
        );
        review.setAiReply(reply);
        review.setReplied(true);
        Review savedReview = reviewRepo.save(review);

        // Send email notification to business owner
        emailNotificationService.sendReviewResponseNotification(business, savedReview);

        return savedReview;
    }

    public Review processReviewWithoutBusiness(Review review) {
        validationUtil.validateReview(review);

        String reply = aiService.generateReply(
                review.getComment(),
                review.getRating()
        );
        review.setAiReply(reply);
        review.setReplied(true);
        return reviewRepo.save(review);
    }
}
