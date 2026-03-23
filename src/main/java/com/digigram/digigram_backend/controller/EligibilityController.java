package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.model.EligibilityRequest;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/eligibility")
@CrossOrigin("*")
public class EligibilityController {

    @PostMapping("/check")
    public Map<String, Object> checkEligibility(@RequestBody EligibilityRequest req) throws Exception {

        Map<String, Object> response = new HashMap<>();

        if (req.getUid() == null || req.getSchemeId() == null) {
            response.put("eligible", false);
            response.put("message", "Invalid request data.");
            return response;
        }

        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot citizenDoc = db.collection("citizenProfiles")
                .document(req.getUid())
                .get()
                .get();

        if (!citizenDoc.exists()) {
            response.put("eligible", false);
            response.put("message", "Citizen profile not found.");
            return response;
        }

        DocumentSnapshot schemeDoc = db.collection("schemes")
                .document(req.getSchemeId())
                .get()
                .get();

        if (!schemeDoc.exists()) {
            response.put("eligible", false);
            response.put("message", "Scheme not found.");
            return response;
        }

        Integer age = parseInteger(citizenDoc.get("age"));
        Double income = parseDouble(citizenDoc.get("income"));
        String religion = citizenDoc.getString("religion");
        String caste = citizenDoc.getString("caste");

        Integer minAge = parseInteger(schemeDoc.get("minAge"));
        Integer maxAge = parseInteger(schemeDoc.get("maxAge"));
        Double minIncome = parseDouble(schemeDoc.get("minIncome"));
        Double maxIncome = parseDouble(schemeDoc.get("maxIncome"));
        String allowedReligion = schemeDoc.getString("allowedReligion");
        String allowedCaste = schemeDoc.getString("allowedCaste");

        boolean eligible = true;
        String reason = "";

        if (age == null || income == null) {
            eligible = false;
            reason = "Profile incomplete (Age or Income missing)";
        }

        if (eligible && minAge != null && age < minAge) {
            eligible = false;
            reason = "Minimum age criteria not satisfied";
        }

        if (eligible && maxAge != null && age > maxAge) {
            eligible = false;
            reason = "Maximum age exceeded";
        }

        if (eligible && minIncome != null && income < minIncome) {
            eligible = false;
            reason = "Income below required limit";
        }

        if (eligible && maxIncome != null && income > maxIncome) {
            eligible = false;
            reason = "Income exceeds allowed limit";
        }

        if (eligible && allowedReligion != null && !allowedReligion.isEmpty()
                && religion != null
                && !allowedReligion.equalsIgnoreCase(religion)) {
            eligible = false;
            reason = "Religion criteria not satisfied";
        }

        if (eligible && allowedCaste != null && !allowedCaste.isEmpty()
                && caste != null
                && !allowedCaste.equalsIgnoreCase(caste)) {
            eligible = false;
            reason = "Caste criteria not satisfied";
        }

        response.put("eligible", eligible);
        response.put("message", eligible ? "You are eligible!" : reason);

        return response;
    }

    private Integer parseInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();

        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();

        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}