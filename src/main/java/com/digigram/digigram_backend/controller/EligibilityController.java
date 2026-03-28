package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.model.EligibilityRequest;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/eligibility")
@CrossOrigin("*")
public class EligibilityController {

    @PostMapping("/check")
    public Map<String, Object> checkEligibility(@RequestBody EligibilityRequest req) throws Exception {

        Map<String, Object> res = new HashMap<>();

        if (req.getUid() == null || req.getSchemeId() == null) {
            res.put("eligible", false);
            res.put("message", "Invalid request");
            return res;
        }

        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot citizen = db.collection("citizenProfiles")
                .document(req.getUid()).get().get();

        DocumentSnapshot scheme = db.collection("schemes")
                .document(req.getSchemeId()).get().get();

        if (!citizen.exists() || !scheme.exists()) {
            res.put("eligible", false);
            res.put("message", "Data not found");
            return res;
        }

        /* ================= SAFE PARSING ================= */
        String dob = citizen.getString("dateOfBirth");
        Integer age = null;

        try {
            if (dob != null) {
                LocalDate d = LocalDate.parse(dob);
                age = Period.between(d, LocalDate.now()).getYears();
            }
        } catch (Exception e) {
            age = null;
        }

        Double income = parseDouble(citizen.get("income"));

        String gender = safe(citizen.getString("gender"));
        String religion = safe(citizen.getString("religion"));
        String caste = safe(citizen.getString("caste"));
        String occupation = safe(citizen.getString("occupation"));
        Boolean disability = citizen.getBoolean("hasDisability");

        Integer minAge = parseInt(scheme.get("minAge"));
        Integer maxAge = parseInt(scheme.get("maxAge"));
        Double minIncome = parseDouble(scheme.get("minIncome"));
        Double maxIncome = parseDouble(scheme.get("maxIncome"));

        String allowedGender = safe(scheme.getString("allowedGender"));
        String allowedReligion = safe(scheme.getString("allowedReligion"));
        String allowedCaste = safe(scheme.getString("allowedCaste"));
        String allowedOccupation = safe(scheme.getString("allowedOccupation"));
        Boolean disabilityOnly = scheme.getBoolean("disabilityOnly");

        /* ================= VALIDATION ================= */

        if (age == null || income == null) {
            return fail("Profile incomplete (age/income missing)");
        }

        if (minAge != null && age < minAge) return fail("Minimum age not satisfied");
        if (maxAge != null && age > maxAge) return fail("Maximum age exceeded");

        if (minIncome != null && income < minIncome) return fail("Income too low");
        if (maxIncome != null && income > maxIncome) return fail("Income too high");

        if (!allowedGender.isEmpty()
                && !allowedGender.equalsIgnoreCase("All")
                && !allowedGender.equalsIgnoreCase(gender)) {
            return fail("Gender criteria not satisfied");
        }

        if (!allowedReligion.isEmpty()
                && !allowedReligion.equalsIgnoreCase(religion)) {
            return fail("Religion criteria not satisfied");
        }

        if (!allowedCaste.isEmpty()
                && !allowedCaste.equalsIgnoreCase(caste)) {
            return fail("Caste criteria not satisfied");
        }

        if (disabilityOnly != null && disabilityOnly
                && (disability == null || !disability)) {
            return fail("Only for disabled");
        }

        if (!allowedOccupation.isEmpty()
                && !allowedOccupation.equalsIgnoreCase(occupation)) {
            return fail("Occupation not eligible");
        }

        res.put("eligible", true);
        res.put("message", "Eligible");

        return res;
    }

    /* ================= HELPERS ================= */

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private Map<String, Object> fail(String msg) {
        Map<String, Object> r = new HashMap<>();
        r.put("eligible", false);
        r.put("message", msg);
        return r;
    }

    private Integer parseInt(Object o) {
        try { return o == null ? null : Integer.parseInt(o.toString()); }
        catch(Exception e){ return null; }
    }

    private Double parseDouble(Object o) {
        try { return o == null ? null : Double.parseDouble(o.toString()); }
        catch(Exception e){ return null; }
    }
}