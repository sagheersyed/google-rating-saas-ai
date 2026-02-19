//// NEW_FILE_CODE
//package com.google.rating.saas_ai.config;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.ai.openai.api.OpenAiApi;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.web.client.RestClient;
//
//@Configuration
//public class AiConfig {
//
//    @Value("${ai.provider:openai}")
//    private String aiProvider;
//
//    @Value("${spring.ai.openai.api-key:#{null}}")
//    private String openaiApiKey;
//
//    @Value("${spring.ai.azure.openai.api-key:#{null}}")
//    private String azureOpenaiApiKey;
//
//    @Bean
//    @Primary
//    public ChatClient.Builder chatClientBuilder() {
//        if ("openai".equalsIgnoreCase(aiProvider) && openaiApiKey != null && !openaiApiKey.isEmpty()) {
//            return ChatClient.builder().defaultChatModel(openAiChatModel());
//        } else if ("azure".equalsIgnoreCase(aiProvider) && azureOpenaiApiKey != null && !azureOpenaiApiKey.isEmpty()) {
//            // Return Azure OpenAI Chat Model builder when configured
//            return ChatClient.builder(); // Will be configured with Azure when available
//        } else {
//            // For now, using OpenAI as default if no specific provider is configured
//            // Or we could create a mock/fallback implementation
//            return ChatClient.builder().defaultChatModel(openAiChatModel());
//        }
//    }
//
//    @Bean
//    @Primary
//    public ChatModel openAiChatModel() {
//        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
//            OpenAiApi openAiApi = new OpenAiApi(openaiApiKey);
//            return new OpenAiChatModel(openAiApi);
//        } else {
//            // Provide a fallback implementation or throw a more informative error
//            // For now, we'll use a default API key for testing, but this should be configured properly
//            OpenAiApi openAiApi = new OpenAiApi("dummy-key-for-startup");
//            return new OpenAiChatModel(openAiApi);
//        }
//    }
//}
