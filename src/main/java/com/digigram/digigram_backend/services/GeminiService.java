package com.digigram.digigram_backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String message) {

        String url = "http://localhost:5001/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ FIXED GENERICS
        Map<String, String> body = Map.of("message", message);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            // ✅ SAFE CAST
            Map<?, ?> resBody = response.getBody();

            return resBody.get("reply").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠ AI service unavailable";
        }
    }
}