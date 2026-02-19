// NEW_FILE_CODE
package com.google.rating.saas_ai.validation;

import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.entity.Review;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$"); // E.164 format
    private static final int MAX_REVIEW_LENGTH = 2000;
    private static final int MIN_REVIEW_LENGTH = 1;
    private static final int MAX_REPLY_LENGTH = 1000;

    public void validateBusiness(Business business) {
        if (business == null) {
            throw new IllegalArgumentException("Business cannot be null");
        }

        if (business.getBusinessName() == null || business.getBusinessName().trim().isEmpty()) {
            throw new IllegalArgumentException("Business name cannot be null or empty");
        }

        if (business.getBusinessName().length() > 255) {
            throw new IllegalArgumentException("Business name cannot exceed 255 characters");
        }

        if (business.getGooglePlaceId() == null || business.getGooglePlaceId().trim().isEmpty()) {
            throw new IllegalArgumentException("Google Place ID cannot be null or empty");
        }

        if (business.getBusinessEmail() == null || business.getBusinessEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Business email cannot be null or empty");
        }

        if (!EMAIL_PATTERN.matcher(business.getBusinessEmail()).matches()) {
            throw new IllegalArgumentException("Business email must be a valid email address");
        }

        if (business.getBusinessEmail().length() > 255) {
            throw new IllegalArgumentException("Business email cannot exceed 255 characters");
        }

        if (business.getBusinessPhone() != null && !business.getBusinessPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(business.getBusinessPhone()).matches()) {
                throw new IllegalArgumentException("Business phone number must be in a valid format");
            }
        }

        if (business.getBusinessDescription() != null && business.getBusinessDescription().length() > 1000) {
            throw new IllegalArgumentException("Business description cannot exceed 1000 characters");
        }

        if (business.getBusinessCategory() != null && business.getBusinessCategory().length() > 100) {
            throw new IllegalArgumentException("Business category cannot exceed 100 characters");
        }
    }

    public void validateReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }

        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Review comment cannot be null or empty");
        }

        if (review.getComment().length() < MIN_REVIEW_LENGTH || review.getComment().length() > MAX_REVIEW_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Review comment must be between %d and %d characters", MIN_REVIEW_LENGTH, MAX_REVIEW_LENGTH));
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars");
        }

        if (review.getAiReply() != null && review.getAiReply().length() > MAX_REPLY_LENGTH) {
            throw new IllegalArgumentException("AI reply cannot exceed " + MAX_REPLY_LENGTH + " characters");
        }
    }

    public void validateGooglePlaceId(String googlePlaceId) {
        if (googlePlaceId == null || googlePlaceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Google Place ID cannot be null or empty");
        }

        if (googlePlaceId.length() > 500) {
            throw new IllegalArgumentException("Google Place ID cannot exceed 500 characters");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email must be a valid email address");
        }

        if (email.length() > 255) {
            throw new IllegalArgumentException("Email cannot exceed 255 characters");
        }
    }
}
