package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.services.CertificateService;
import com.digigram.digigram_backend.services.CitizenService;
import com.digigram.digigram_backend.dto.CertificateRequestDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*") // ✅ FIX FOR HOSTING
public class CertificateController {

    @Autowired
    private CertificateService service;

    @Autowired
    private CitizenService citizenService; // 🔥 ADD THIS

    // ================= CREATE =================
    @PostMapping
    public Map<String, String> create(
            @RequestBody CertificateRequestDTO dto,
            HttpServletRequest request) throws Exception {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 🔥 FETCH PROFILE
        Map<String, Object> profile = citizenService.getProfile(uid);

        String name = "Unknown";
        String phone = "Not Provided";
        String aadhaar = "-";

        if (profile != null) {
            name = (String) profile.getOrDefault("name", "Unknown");
            phone = (String) profile.getOrDefault("phone", "Not Provided");
            aadhaar = (String) profile.getOrDefault("aadhaar", "-");
        }

        Map<String, Object> data = new HashMap<>();

        data.put("uid", uid);
        data.put("name", name);
        data.put("phone", phone);
        data.put("aadhaar", aadhaar);

        data.put("type", dto.getType());
        data.put("reason", dto.getReason());

        data.put("status", "Pending");
        data.put("adminReason", "");

        String id = service.createCertificate(data);

        return Map.of("id", id);
    }

    // ================= GET MY =================
    @GetMapping("/my")
    public List<Map<String, Object>> getMyCertificates(
            HttpServletRequest request) throws Exception {

        String uid = (String) request.getAttribute("uid");
        return service.getByUser(uid);
    }

    // ================= ADMIN =================
    @GetMapping("/all")
    public List<Map<String, Object>> getAllCertificates() throws Exception {
        return service.getAll();
    }

    // ================= APPROVE =================
    @PutMapping("/{id}/approve")
    public String approve(@PathVariable String id) throws Exception {
        service.approve(id);
        return "Approved";
    }

    // ================= REJECT WITH REASON =================
    @PutMapping("/{id}/reject")
    public String reject(
            @PathVariable String id,
            @RequestParam String reason) throws Exception {

        service.rejectWithReason(id, reason);
        return "Rejected";
    }
}
