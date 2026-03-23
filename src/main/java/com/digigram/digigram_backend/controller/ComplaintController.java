package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.model.Complaint;
import com.digigram.digigram_backend.services.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:5173")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<?> createComplaint(
            @RequestBody Complaint complaint,
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        complaint.setCitizenId(uid);

        Complaint saved = complaintService.createComplaint(complaint);

        return ResponseEntity.ok(saved);
    }

    // ================= GET MY COMPLAINTS =================
    @GetMapping("/my")
    public ResponseEntity<?> getMyComplaints(
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(
                complaintService.getByUserId(uid)
        );
    }

    // ================= ADMIN =================
    @GetMapping("/admin")
    public ResponseEntity<?> getAllComplaints(
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(
                complaintService.getAllSortedByAi()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable String id) {

        Complaint complaint = complaintService.getById(id);

        if (complaint == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(complaint);
    }

    // ================= UPDATE STATUS =================
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable String id,
            @RequestParam String status,
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Complaint updated = complaintService.updateStatus(id, status);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }
}