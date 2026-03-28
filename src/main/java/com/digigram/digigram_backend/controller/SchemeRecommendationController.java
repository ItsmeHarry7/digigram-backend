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

        DocumentSnapshot citizen = db.collection("citizenProfiles")
                .document(uid)
                .get()
                .get();

        if (!citizen.exists()) {
            return Map.of("error", "Citizen profile not found", "eligibleSchemes", Collections.emptyList());
        }

        // Get age with null check
        Object ageObj = citizen.get("age");
        Integer age = null;
        if (ageObj != null) {
            if (ageObj instanceof Number) {
                age = ((Number) ageObj).intValue();
            } else if (ageObj instanceof String) {
                try {
                    age = Integer.parseInt((String) ageObj);
                } catch (NumberFormatException e) {
                    age = null;
                }
            }
        }

        // Get income with null check
        Object incomeObj = citizen.get("income");
        Double income = null;
        if (incomeObj != null) {
            if (incomeObj instanceof Number) {
                income = ((Number) incomeObj).doubleValue();
            } else if (incomeObj instanceof String) {
                try {
                    income = Double.parseDouble((String) incomeObj);
                } catch (NumberFormatException e) {
                    income = null;
                }
            }
        }

        // Get gender for additional filtering
        String gender = citizen.getString("gender");
        String religion = citizen.getString("religion");
        String occupation = citizen.getString("occupation");
        Boolean disability = citizen.getBoolean("disability");

        QuerySnapshot schemes = db.collection("schemes").get().get();

        List<Map<String, Object>> eligible = new ArrayList<>();
        List<Map<String, Object>> partiallyEligible = new ArrayList<>();

        for (DocumentSnapshot s : schemes.getDocuments()) {

            // Get scheme criteria with null checks
            Long minAgeLong = s.getLong("minAge");
            Long maxAgeLong = s.getLong("maxAge");
            Double maxIncomeDouble = s.getDouble("maxIncome");
            String allowedGender = s.getString("allowedGender");
            String allowedReligion = s.getString("allowedReligion");
            String allowedOccupation = s.getString("allowedOccupation");
            Boolean disabilityOnly = s.getBoolean("disabilityOnly");

            boolean eligibleForAge = true;
            boolean eligibleForIncome = true;
            boolean eligibleForGender = true;
            boolean eligibleForReligion = true;
            boolean eligibleForOccupation = true;
            boolean eligibleForDisability = true;

            // Check age
            if (minAgeLong != null && maxAgeLong != null && age != null) {
                int minAge = minAgeLong.intValue();
                int maxAge = maxAgeLong.intValue();
                eligibleForAge = (age >= minAge && age <= maxAge);
            } else if (age == null) {
                eligibleForAge = false;
            }

            // Check income
            if (maxIncomeDouble != null && income != null) {
                eligibleForIncome = (income <= maxIncomeDouble);
            } else if (income == null && maxIncomeDouble != null && maxIncomeDouble > 0) {
                eligibleForIncome = false;
            }

            // Check gender
            if (allowedGender != null && !allowedGender.equals("All") && gender != null) {
                eligibleForGender = allowedGender.equals(gender);
            }

            // Check religion
            if (allowedReligion != null && !allowedReligion.equals("All") && religion != null) {
                eligibleForReligion = allowedReligion.equals(religion);
            }

            // Check occupation
            if (allowedOccupation != null && !allowedOccupation.equals("All") && occupation != null) {
                eligibleForOccupation = allowedOccupation.equals(occupation);
            }

            // Check disability
            if (disabilityOnly != null && disabilityOnly && disability != null) {
                eligibleForDisability = disability;
            }

            boolean isFullyEligible = eligibleForAge && eligibleForIncome && eligibleForGender && 
                                      eligibleForReligion && eligibleForOccupation && eligibleForDisability;

            Map<String, Object> scheme = new HashMap<>();
            scheme.put("id", s.getId());
            scheme.put("title", s.getString("title"));
            scheme.put("description", s.getString("description"));
            scheme.put("officialLink", s.getString("officialLink"));
            scheme.put("minAge", minAgeLong);
            scheme.put("maxAge", maxAgeLong);
            scheme.put("maxIncome", maxIncomeDouble);
            scheme.put("allowedGender", allowedGender != null ? allowedGender : "All");
            scheme.put("allowedReligion", allowedReligion != null ? allowedReligion : "All");
            scheme.put("allowedOccupation", allowedOccupation != null ? allowedOccupation : "All");
            scheme.put("disabilityOnly", disabilityOnly != null && disabilityOnly);

            if (isFullyEligible) {
                scheme.put("eligibilityStatus", "FULLY_ELIGIBLE");
                eligible.add(scheme);
            } else if (eligibleForAge || eligibleForIncome) {
                // Partially eligible - still show but with note
                scheme.put("eligibilityStatus", "PARTIALLY_ELIGIBLE");
                scheme.put("missingCriteria", getMissingCriteria(eligibleForAge, eligibleForIncome, 
                    eligibleForGender, eligibleForReligion, eligibleForOccupation));
                partiallyEligible.add(scheme);
            }
        }

        // Combine results - fully eligible first
        List<Map<String, Object>> allEligible = new ArrayList<>();
        allEligible.addAll(eligible);
        allEligible.addAll(partiallyEligible);

        return Map.of("eligibleSchemes", allEligible);
    }

    private String getMissingCriteria(boolean age, boolean income, boolean gender, boolean religion, boolean occupation) {
        List<String> missing = new ArrayList<>();
        if (!age) missing.add("Age");
        if (!income) missing.add("Income");
        if (!gender) missing.add("Gender");
        if (!religion) missing.add("Religion");
        if (!occupation) missing.add("Occupation");
        return String.join(", ", missing);
    }
}