package com.google.rating.saas_ai.service;

import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.entity.Review;
import com.google.rating.saas_ai.repository.ReviewRepository;
import com.google.rating.saas_ai.validation.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceGoogleFlowTest {

    @Mock
    private ReviewRepository reviewRepo;
    @Mock
    private AiReplyService aiService;
    @Mock
    private BusinessService businessService;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private GooglePlacesService googlePlacesService;

    private final ValidationUtil validationUtil = new ValidationUtil();

    @InjectMocks
    private ReviewService reviewService;

    private Business business;

    @BeforeEach
    void setUp() {
        business = new Business();
        business.setId(10L);
        business.setBusinessName("Cafe Prime");
        business.setBusinessCategory("Restaurant");
    }

    @Test
    void processGoogleReview_shouldGenerateAndPublishReply() {
        Review inputReview = new Review();
        inputReview.setGoogleReviewId("places/abc123/reviews/r1");
        inputReview.setRating(5);
        inputReview.setComment("Amazing food and fast service");

        when(businessService.isBusinessSubscribedAndActive(10L)).thenReturn(true);
        when(businessService.getBusinessById(10L)).thenReturn(business);
        when(aiService.generateReplyWithContext(any(), any(), any(), any())).thenReturn("Thank you for your kind words!");
        when(reviewRepo.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review saved = reviewService.processGoogleReview(inputReview, 10L);

        assertEquals("Thank you for your kind words!", saved.getAiReply());
        verify(googlePlacesService).replyToReview("places/abc123/reviews/r1", "Thank you for your kind words!");
        verify(emailNotificationService).sendReviewResponseNotification(eq(business), any(Review.class));
    }

    @Test
    void processGoogleReview_shouldFailWhenGoogleReviewIdMissing() {
        Review inputReview = new Review();
        inputReview.setGoogleReviewId("   ");
        inputReview.setRating(4);
        inputReview.setComment("Good place");

        when(businessService.isBusinessSubscribedAndActive(10L)).thenReturn(true);
        when(businessService.getBusinessById(10L)).thenReturn(business);
        when(aiService.generateReplyWithContext(any(), any(), any(), any())).thenReturn("Thanks for visiting");
        when(reviewRepo.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> reviewService.processGoogleReview(inputReview, 10L));
        verify(googlePlacesService, never()).replyToReview(any(), any());
    }
}
