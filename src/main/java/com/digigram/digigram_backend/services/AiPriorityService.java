package com.digigram.digigram_backend.services;

import com.digigram.digigram_backend.model.Complaint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiPriorityService {

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ SAME BASE URL (IMPORTANT)
    @Value("${ai.service.url}")
    private String baseUrl;

    public void enrichWithAiPriority(Complaint complaint) {

        String url = baseUrl + "/predict_priority";

        try {
            Map<String, Object> payload = new HashMap<>();

            payload.put("title", complaint.getTitle() != null ? complaint.getTitle() : "");
            payload.put("description", complaint.getDescription() != null ? complaint.getDescription() : "");
            payload.put("category", complaint.getCategory() != null ? complaint.getCategory() : "");

            // 🔥 NEW (VOICE SUPPORT - SAFE ADD)
            payload.put("audioUrl", complaint.getAudioUrl() != null ? complaint.getAudioUrl() : "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            Map<?, ?> res = response.getBody();

            if (res != null) {

                Object priorityObj = res.get("priority");
                String priority = (priorityObj != null) ? priorityObj.toString() : "Medium";

                Object categoryObj = res.get("category");
                String category = (categoryObj != null) ? categoryObj.toString() : "General";

                Object scoreObj = res.get("aiScore");
                int score = (scoreObj instanceof Number)
                        ? ((Number) scoreObj).intValue()
                        : 50;

                complaint.setPriority(priority);
                complaint.setAiScore(score);
                complaint.setCategory(category);

            } else {
                complaint.setPriority("Medium");
                complaint.setAiScore(50);
                complaint.setCategory("General");
            }

        } catch (Exception e) {
            e.printStackTrace();
            complaint.setPriority("Medium");
            complaint.setAiScore(50);
            complaint.setCategory("General");
        }
    }
}