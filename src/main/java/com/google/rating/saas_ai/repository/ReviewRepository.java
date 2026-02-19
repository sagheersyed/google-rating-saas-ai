package com.google.rating.saas_ai.repository;

import com.google.rating.saas_ai.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByGoogleReviewId(String googleReviewId);
}
