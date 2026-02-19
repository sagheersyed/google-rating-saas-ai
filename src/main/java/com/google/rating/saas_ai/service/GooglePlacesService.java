// NEW_FILE_CODE
package com.google.rating.saas_ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GooglePlacesService {

    private static final Logger logger = LoggerFactory.getLogger(GooglePlacesService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String googleApiKey;

    public GooglePlacesService(@Value("${saas.google.api-key:}") String googleApiKey,
                               WebClient.Builder webClientBuilder,
                               ObjectMapper objectMapper) {
        this.googleApiKey = googleApiKey;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder
                .baseUrl("https://places.googleapis.com/v1")
                .build();
    }

    public List<Map<String, Object>> getReviewsForPlace(String placeId) {
        if (googleApiKey.isEmpty()) {
            logger.warn("Google API key is not configured");
            return new ArrayList<>();
        }

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/places/{placeId}")
                            .queryParam("fields", "reviews")
                            .build(placeId))
                    .header("X-Goog-Api-Key", googleApiKey)
                    .header("X-Goog-FieldMask", "reviews")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode reviewsNode = rootNode.path("reviews");

            List<Map<String, Object>> reviews = new ArrayList<>();
            if (reviewsNode.isArray()) {
                for (JsonNode reviewNode : reviewsNode) {
                    Map<String, Object> reviewMap = objectMapper.convertValue(reviewNode, Map.class);
                    reviews.add(reviewMap);
                }
            }

            return reviews;
        } catch (Exception e) {
            logger.error("Error fetching reviews for place: " + placeId, e);
            return new ArrayList<>();
        }
    }

    public void replyToReview(String reviewId, String replyText) {
        if (googleApiKey.isEmpty()) {
            logger.warn("Google API key is not configured for posting replies");
            return;
        }

        try {
            Map<String, Object> requestBody = Map.of("comment", replyText);

            webClient.post()
                    .uri("/places/{reviewId}:reply", reviewId)
                    .header("X-Goog-Api-Key", googleApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Successfully replied to review: " + reviewId);
        } catch (Exception e) {
            logger.error("Error replying to review: " + reviewId, e);
        }
    }
}
