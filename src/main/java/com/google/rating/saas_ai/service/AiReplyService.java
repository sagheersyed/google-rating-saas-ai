package com.google.rating.saas_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiReplyService {

    private final ChatClient chatClient;
    private final String aiProvider;

    public AiReplyService(ChatClient.Builder chatClientBuilder, @Value("${ai.provider:ollama}") String aiProvider) {
        this.chatClient = chatClientBuilder.build();
        this.aiProvider = aiProvider;
    }

    public String generateReply(String review, int rating) {
        String prompt = String.format("""
            You are a professional business owner responding to customer feedback. 
            Write a polite, personalized, and professional response to this review. 
            The response should acknowledge the customer's feedback and show appreciation or address concerns appropriately.
            Make sure the response is concise but warm and genuine. It should match the tone of the original review.
            Rating: %d stars
            Review: "%s"
            """, rating, review);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
    
    public String generateReplyWithContext(String review, int rating, String businessName, String businessCategory) {
        String prompt = String.format("""
            You are the owner of '%s', a %s business. Respond professionally to this review from a customer. 
            Your response should be warm, genuine, and appropriate for your business type. Acknowledge their feedback specifically and show appreciation or address concerns appropriately.
            The response should be concise (under 100 words) and reflect the tone of the original review.
            Rating: %d stars
            Review: "%s"
            """, businessName, businessCategory, rating, review);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
