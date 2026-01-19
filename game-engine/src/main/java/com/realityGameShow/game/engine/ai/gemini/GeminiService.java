package com.realityGameShow.game.engine.ai.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeminiService {

    private final String apiKey;
    private final WebClient webClient;

    public GeminiService(
            @Value("${ai.gemini.api-url}") String apiUrl,
            @Value("${ai.gemini.api-key}") String apiKey
    ) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public Mono<String> generateContent(String prompt) {
        GeminiRequest request = new GeminiRequest(prompt);
        
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", apiKey)
                        .build())
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(GeminiResponse::getText)
                .onErrorReturn("Error generating content");
    }

    public String generateContentSync(String prompt) {
        try {
            return generateContent(prompt).block();
        } catch (Exception e) {
            return "Error generating content: " + e.getMessage();
        }
    }
}
