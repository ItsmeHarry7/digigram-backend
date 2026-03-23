package com.digigram.digigram_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class GeminiService {

	@Value("${GEMINI_API_KEY:}")
	private String apiKey;
    

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String message) {

    	String url =
    			"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents",
                List.of(
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of(
                                                "text",
                                                "You are DigiGram AI assistant helping villagers with complaints, schemes and panchayat services. Answer simply and politely.\nUser: "
                                                        + message)))));

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            List<?> candidates =
                    (List<?>) response.getBody().get("candidates");

            Map<?, ?> first = (Map<?, ?>) candidates.get(0);

            Map<?, ?> content = (Map<?, ?>) first.get("content");

            List<?> parts = (List<?>) content.get("parts");

            Map<?, ?> textPart = (Map<?, ?>) parts.get(0);

            return textPart.get("text").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI service unavailable.";
        }
    }
}