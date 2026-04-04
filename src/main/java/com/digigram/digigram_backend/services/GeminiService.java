package com.digigram.digigram_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.url}")
    private String baseUrl;

    public String chat(String message) {

        String url = baseUrl + "/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("message", message);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            Map<?, ?> resBody = response.getBody();

            if (resBody != null && resBody.get("reply") != null) {
                return resBody.get("reply").toString();
            }

            return "⚠ No response from AI";

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠ AI service unavailable";
        }
    }
}