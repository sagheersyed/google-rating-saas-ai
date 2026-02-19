package com.google.rating.saas_ai.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.entity.Review;
import com.google.rating.saas_ai.repository.ReviewRepository;
import com.google.rating.saas_ai.service.BusinessService;
import com.google.rating.saas_ai.service.ReviewService;
import com.google.rating.saas_ai.validation.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleReviewsWebhookHandlerTest {

    @Mock
    private BusinessService businessService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;

    private final ValidationUtil validationUtil = new ValidationUtil();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private GoogleReviewsWebhookHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GoogleReviewsWebhookHandler(businessService, reviewService, reviewRepository, validationUtil);
    }

    @Test
    void processGoogleReviewWebhook_shouldInvokeProcessingForValidPayload() throws Exception {
        JsonNode payload = objectMapper.readTree("""
                {
                  "review": {
                    "name": "places/chij111/reviews/rev_777",
                    "rating": 5,
                    "comment": "Excellent service"
                  }
                }
                """);

        when(reviewRepository.findByGoogleReviewId("places/chij111/reviews/rev_777")).thenReturn(Optional.empty());

        Business business = new Business();
        business.setId(25L);
        business.setBusinessName("Prime Barber");
        when(businessService.getBusinessByGooglePlaceId("chij111")).thenReturn(business);
        when(businessService.isBusinessSubscribedAndActive(25L)).thenReturn(true);

        handler.processGoogleReviewWebhook(payload, "test-signature");

        verify(reviewService).processGoogleReview(any(Review.class), eq(25L));
    }

    @Test
    void processGoogleReviewWebhook_shouldSkipDuplicateReview() throws Exception {
        JsonNode payload = objectMapper.readTree("""
                {
                  "review": {
                    "name": "places/chij111/reviews/rev_777",
                    "rating": 4,
                    "comment": "Good"
                  }
                }
                """);

        when(reviewRepository.findByGoogleReviewId("places/chij111/reviews/rev_777"))
                .thenReturn(Optional.of(new Review()));

        handler.processGoogleReviewWebhook(payload, "test-signature");

        verify(reviewService, never()).processGoogleReview(any(), any());
    }
}
