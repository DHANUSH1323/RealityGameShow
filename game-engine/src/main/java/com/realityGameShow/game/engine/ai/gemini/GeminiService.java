package com.realityGameShow.game.engine.ai.gemini;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeminiService {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final String API_KEY = "AIzaSyD2RSjH71xR0HV9iW1QPpleiXktZ7JHBXI";

    private final WebClient webClient;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl(GEMINI_API_URL)
                .build();
    }

    public Mono<String> generateContent(String prompt) {
        GeminiRequest request = new GeminiRequest(prompt);
        
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", API_KEY)
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
