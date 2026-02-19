package com.google.rating.saas_ai.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.entity.Review;
import com.google.rating.saas_ai.repository.ReviewRepository;
import com.google.rating.saas_ai.service.BusinessService;
import com.google.rating.saas_ai.service.ReviewService;
import com.google.rating.saas_ai.validation.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoogleReviewsWebhookHandler {

    private static final Logger logger = LoggerFactory.getLogger(GoogleReviewsWebhookHandler.class);

    private final BusinessService businessService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final ValidationUtil validationUtil;

    @Value("${saas.webhook.secret:}")
    private String webhookSecret;

    public GoogleReviewsWebhookHandler(BusinessService businessService,
                                       ReviewService reviewService,
                                       ReviewRepository reviewRepository,
                                       ValidationUtil validationUtil) {
        this.businessService = businessService;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.validationUtil = validationUtil;
    }

    public void processGoogleReviewWebhook(JsonNode webhookPayload, String signature) {
        try {
            // Verify webhook signature if applicable
            if (!verifyWebhookSignature(webhookPayload, signature)) {
                logger.warn("Webhook signature verification failed");
                return;
            }

            // Validate webhook payload
            if (webhookPayload == null) {
                logger.warn("Webhook payload is null");
                return;
            }

            // Extract review data from the webhook payload
            JsonNode reviewNode = webhookPayload.path("review");
            if (reviewNode == null || reviewNode.isNull() || reviewNode.isMissingNode()) {
                logger.warn("Webhook payload does not contain review data or review node is invalid");
                return;
            }

            String googleReviewId = reviewNode.path("name").asText();
            if (googleReviewId == null || googleReviewId.trim().isEmpty()) {
                logger.warn("Google review ID is missing or invalid");
                return;
            }

            // Check if this review already exists to prevent duplicates
            Optional<Review> existingReview = reviewRepository.findByGoogleReviewId(googleReviewId);
            if (existingReview.isPresent()) {
                logger.info("Review {} already exists, skipping processing", googleReviewId);
                return;
            }

            if (reviewNode.path("rating").isMissingNode() || !reviewNode.path("rating").isNumber()) {
                logger.warn("Rating is missing or invalid in review: {}", googleReviewId);
                return;
            }

            int rating = reviewNode.path("rating").asInt();
            if (rating < 1 || rating > 5) {
                logger.warn("Rating is out of valid range (1-5): {}", rating);
                return;
            }

            String comment = reviewNode.path("comment").asText("");
            if (comment == null || comment.trim().isEmpty()) {
                logger.warn("Review comment is missing or empty in review: {}", googleReviewId);
                return;
            }

            // Validate the review content
            try {
                validationUtil.validateReview(createTempReview(comment, rating));
            } catch (IllegalArgumentException e) {
                logger.warn("Review validation failed: {}", e.getMessage());
                return;
            }

            // Find the business associated with this review
            String placeId = extractPlaceIdFromReviewName(reviewNode.path("name").asText());
            if (placeId == null || placeId.trim().isEmpty()) {
                logger.warn("Could not extract valid place ID from review name: {}", reviewNode.path("name").asText());
                return;
            }

            Business business;
            try {
                business = businessService.getBusinessByGooglePlaceId(placeId);
            } catch (RuntimeException e) {
                logger.warn("Business not found for place ID: {}", placeId);
                return;
            }

            // Verify business has an active subscription
            try {
                if (!businessService.isBusinessSubscribedAndActive(business.getId())) {
                    logger.warn("Business {} does not have an active subscription", business.getBusinessName());
                    return;
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid business ID when checking subscription: {}", e.getMessage());
                return;
            }

            // Create a new review entity
            Review review = new Review();
            review.setGoogleReviewId(googleReviewId);
            review.setRating(rating);
            review.setComment(comment);

            // Process the review and generate AI response
            try {
                reviewService.processGoogleReview(review, business.getId());
                logger.info("Successfully processed and replied to Google review for business: {}", business.getBusinessName());
            } catch (Exception e) {
                logger.error("Error processing review for business {}: {}", business.getBusinessName(), e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error processing Google review webhook", e);
        }
    }

    private Review createTempReview(String comment, int rating) {
        Review tempReview = new Review();
        tempReview.setComment(comment);
        tempReview.setRating(rating);
        return tempReview;
    }

    private boolean verifyWebhookSignature(JsonNode payload, String signature) {
        // In a real implementation, you would verify the signature against your secret
        // This is a simplified version that just logs for now
        logger.debug("Verifying webhook signature: {}", signature);
        return true; // Placeholder - implement proper signature verification
    }

    private String extractPlaceIdFromReviewName(String reviewName) {
        if (reviewName == null || reviewName.trim().isEmpty()) {
            return null;
        }

        // Google review names typically follow the format:
        // places/<PLACE_ID>/reviews/<REVIEW_ID>
        String[] parts = reviewName.split("/");
        if (parts.length >= 2) {
            String placeId = parts[1];
            return placeId.trim().isEmpty() ? null : placeId;
        }
        return null;
    }
}
