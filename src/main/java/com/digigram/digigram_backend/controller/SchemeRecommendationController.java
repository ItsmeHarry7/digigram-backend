package com.digigram.digigram_backend.controller;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/schemes")
@CrossOrigin("*")
public class SchemeRecommendationController {

    @PostMapping("/recommend")
    public Map<String, Object> recommendSchemes(@RequestBody Map<String, String> body) throws Exception {

        String uid = body.get("uid");

        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot citizen =
                db.collection("citizenProfiles")
                        .document(uid)
                        .get()
                        .get();

        if (!citizen.exists()) {
            return Map.of("error", "Citizen profile not found");
        }

        Integer age = ((Number) citizen.get("age")).intValue();
        Double income = ((Number) citizen.get("income")).doubleValue();

        QuerySnapshot schemes = db.collection("schemes").get().get();

        List<Map<String, Object>> eligible = new ArrayList<>();

        for (DocumentSnapshot s : schemes.getDocuments()) {

            Integer minAge = s.getLong("minAge").intValue();
            Integer maxAge = s.getLong("maxAge").intValue();
            Double maxIncome = s.getDouble("maxIncome");

            boolean ok = true;

            if (age < minAge || age > maxAge) ok = false;
            if (income > maxIncome) ok = false;

            if (ok) {

                Map<String, Object> scheme = new HashMap<>();

                scheme.put("id", s.getId());
                scheme.put("title", s.getString("title"));
                scheme.put("description", s.getString("description"));

                eligible.add(scheme);
            }
        }

        return Map.of("eligibleSchemes", eligible);
    }
}