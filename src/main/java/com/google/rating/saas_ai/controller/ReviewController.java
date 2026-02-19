// NEW_FILE_CODE
package com.google.rating.saas_ai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.rating.saas_ai.entity.Review;
import com.google.rating.saas_ai.handler.GoogleReviewsWebhookHandler;
import com.google.rating.saas_ai.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final GoogleReviewsWebhookHandler googleReviewsWebhookHandler;
    private final ObjectMapper objectMapper;

    public ReviewController(ReviewService reviewService, GoogleReviewsWebhookHandler googleReviewsWebhookHandler, ObjectMapper objectMapper) {
        this.reviewService = reviewService;
        this.googleReviewsWebhookHandler = googleReviewsWebhookHandler;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/generate-reply")
    public ResponseEntity<Review> generateReply(@RequestBody Review review) {
        Review processedReview = reviewService.processReviewWithoutBusiness(review);
        return ResponseEntity.ok(processedReview);
    }

    @PostMapping("/business/{businessId}/generate-reply")
    public ResponseEntity<Review> generateReplyForBusiness(@PathVariable Long businessId, @RequestBody Review review) {
        Review processedReview = reviewService.processReview(review, businessId);
        return ResponseEntity.ok(processedReview);
    }

    @PostMapping("/webhook/google-reviews")
    public ResponseEntity<String> handleGoogleReviewWebhook(@RequestBody String rawPayload,
                                                            @RequestHeader(value = "X-Hub-Signature", required = false) String signature) {
        try {
            // Parse the raw JSON payload
            JsonNode payload = objectMapper.readTree(rawPayload);

            // Process the webhook with the handler
            googleReviewsWebhookHandler.processGoogleReviewWebhook(payload, signature);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing webhook: " + e.getMessage());
        }
    }
}
