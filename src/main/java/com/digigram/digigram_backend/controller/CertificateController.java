package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.services.CertificateService;
import com.digigram.digigram_backend.dto.CertificateRequestDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "http://localhost:5173")
public class CertificateController {

    @Autowired
    private CertificateService service;

    // CREATE
    @PostMapping
    public Map<String, String> create(
            @RequestBody CertificateRequestDTO dto,
            HttpServletRequest request) throws Exception {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            throw new RuntimeException("Unauthorized");
        }

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);

        FirebaseToken decodedToken =
                FirebaseAuth.getInstance().verifyIdToken(token);

        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("name", decodedToken.getName());   // ✅ Only name
        data.put("phone", dto.getPhone());
        data.put("type", dto.getType());
        data.put("reason", dto.getReason());

        String id = service.createCertificate(data);

        return Map.of("id", id);
    }

    // GET MY CERTIFICATES
    @GetMapping("/my")
    public List<Map<String, Object>> getMyCertificates(
            HttpServletRequest request) throws Exception {

        String uid = (String) request.getAttribute("uid");
        return service.getByUser(uid);
    }

    // ADMIN: GET ALL
    @GetMapping("/all")
    public List<Map<String, Object>> getAllCertificates()
            throws Exception {

        return service.getAll();
    }

    // APPROVE
    @PutMapping("/{id}/approve")
    public String approve(@PathVariable String id) throws Exception {
        service.approve(id);
        return "Approved";
    }

    // REJECT
    @PutMapping("/{id}/reject")
    public String reject(@PathVariable String id) throws Exception {
        service.reject(id);
        return "Rejected";
    }

    // VERIFY
    @GetMapping("/verify/{code}")
    public Object verify(@PathVariable String code) throws Exception {

        Map<String, Object> result = service.verify(code);

        if (result == null)
            return Map.of("status", "Invalid");

        return result;
    }
}